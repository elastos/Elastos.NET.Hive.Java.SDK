/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.elastos.hive;

import java.nio.file.ProviderNotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.backend.ResolverCache;
import org.elastos.did.exception.DIDException;
import org.elastos.did.exception.DIDResolveException;
import org.elastos.hive.exception.CreateVaultException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.ProviderNotSetException;

public class Client {
	private static boolean resolverDidSetup;

	private AuthenticationHandler authentcationHandler;
	private DIDDocument authenticationDIDDocument;
	private String localDataPath;

	private Client(Options options) {
		this.authenticationDIDDocument = options.authenticationDIDDocument();
		this.authentcationHandler = options.authentcationHandler;
		this.localDataPath = options.localDataPath;
	}

	/**
	 * Constructor without parameters
	 * resolver url and cache path use default value,
	 * resolver url default value: http://api.elastos.io:20606
	 * cache path default value: new java.io.File("didCache")
	 *
	 * @throws HiveException
	 */
	public static void setupResolver() throws HiveException {
		setupResolver(null, null);
	}

	/**
	 * Recommendation for cache dir:
	 * - Laptop/standard Java
	 * System.getProperty("user.home") + "/.cache.did.elastos"
	 * - Android Java
	 * Context.getFilesDir() + "/.cache.did.elastos"
	 *
	 * @param resolver the DIDResolver object
	 * @param cacheDir the cache path name
	 */
	public static void setupResolver(String resolver, String cacheDir) throws HiveException {
		if (cacheDir == null || resolver == null)
			throw new IllegalArgumentException();
		if (resolverDidSetup)
			throw new HiveException("Resolver already setup");

		try {
			DIDBackend.initialize(resolver, cacheDir);
			ResolverCache.reset();
			resolverDidSetup = true;
		} catch (DIDResolveException e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	/**
	 * authentication options, include:
	 * AuthenticationHandler, DIDDocument, data cache path
	 */
	public static class Options {
		private AuthenticationHandler authentcationHandler;
		private DIDDocument authenticationDIDDocument;
		private String localDataPath;

		public Options setAuthenticationDIDDocument(DIDDocument document) {
			this.authenticationDIDDocument = document;
			return this;
		}

		protected DIDDocument authenticationDIDDocument() {
			return authenticationDIDDocument;
		}

		public Options setAuthenticationHandler(AuthenticationHandler authentcationHandler) {
			this.authentcationHandler = authentcationHandler;
			return this;
		}

		protected AuthenticationHandler authenticationHandler() {
			return authentcationHandler;
		}

		public Options setLocalDataPath(String path) {
			this.localDataPath = path;
			return this;
		}

		protected String localDataPath() {
			return localDataPath;
		}

		protected boolean checkValid() {
			return (authenticationDIDDocument != null
					&& authentcationHandler != null
					&& localDataPath != null);
		}
	}

	/**
	 * get Client instance
	 *
	 * @param options authentication options
	 * @return
	 * @throws HiveException
	 * @see Options
	 */
	public static Client createInstance(Options options) throws HiveException {
		if (options == null || !options.checkValid())
			throw new IllegalArgumentException();

		if (!resolverDidSetup)
			throw new HiveException("Setup did resolver first");

		return new Client(options);
	}

	/**
	 * get Vault
	 *
	 * @param ownerDid vault owner did
	 * @return
	 */
	public CompletableFuture<Vault> getVault(String ownerDid, String providerAddress) {
		if (ownerDid == null)
			throw new IllegalArgumentException("Empty ownerDid");

		return getVaultProvider(ownerDid, providerAddress)
				.thenApply(provider -> newVault(ownerDid, provider));
	}

	private Vault newVault(String ownerDid, String provider) {
		if (provider == null)
			throw new ProviderNotSetException(ProviderNotSetException.EXCEPTION);
		AuthHelper authHelper = new AuthHelper(ownerDid, provider,
				localDataPath,
				authenticationDIDDocument,
				authentcationHandler);
		return new Vault(authHelper, provider, ownerDid);
	}

	/**
	 * create Vault
	 *
	 * @param ownerDid
	 * @return
	 */
	public CompletableFuture<Vault> createVault(String ownerDid, String providerAddress) {
		if (ownerDid == null)
			throw new IllegalArgumentException("Empty ownerDid");

		return getVaultProvider(ownerDid, providerAddress)
				.thenApply(provider -> newVault(ownerDid, provider))
				.thenApply(vault -> {
					try {
						boolean exist = vault.checkVaultExist();
						if (!exist) {
							try {
								vault.useTrial();
							} catch (Exception e) {
								throw new CompletionException(e);
							}
						} else {
							throw new CreateVaultException(CreateVaultException.EXCEPTION);
						}
					} catch (Exception e) {
						throw new CompletionException(e);
					}
					return vault;
				});
	}

	/**
	 * Try to acquire provider address for the specific user DID with rules with sequence orders:
	 *  - Use 'preferedProviderAddress' first when it's being with real value; Otherwise
	 *  - Resolve DID document according to the ownerDid from DID sidechain,
	 *    and find if there are more than one "HiveVault" services, then would
	 *    choose the first one service point as target provider address. Otherwise
	 *  - It means no service endpoints declared on this DID Document, then would throw the
	 *    corresponding exception.
	 *
	 * @param ownerDid the owner did that want be set provider address;
	 * @param defaultProviderAddresss the first prority of provider address to
	 *                 be set for ownerDID.
	 * @return the provider address
	 */
	public CompletableFuture<String> getVaultProvider(String ownerDid, String preferedProviderAddress) {
		if (ownerDid == null)
			throw new IllegalArgumentException(
					"Parameters 'ownerDid' and 'defaultProviderAddress' can not be both null");

		return CompletableFuture.supplyAsync(() -> {
			/* Directly choose 'defaultProviderValue' as its provider address.
			 */
			if (preferedProviderAddress != null)
				return preferedProviderAddress;

			try {
				List<DIDDocument.Service> services = null;
				DID did = new DID(ownerDid);
				DIDDocument doc;

				doc = did.resolve();
				if (doc == null)
					throw new ProviderNotFoundException(
							String.format("The DID document %s has not published", ownerDid));

				services = doc.selectServices((String) null, "HiveVault");
				if (services == null || services.size() == 0)
					throw new ProviderNotFoundException(
							String.format("No 'HiveVault' services declared on DID document %s", ownerDid));

				/* TODO: should we throw special exception when it has more than one
				 *       endpoints of service "HiveVault";
				 */
				return services.get(0).getServiceEndpoint();
			} catch (DIDException e) {
				throw new CompletionException(new HiveException(e.getLocalizedMessage()));
			}
		});
	}
}
