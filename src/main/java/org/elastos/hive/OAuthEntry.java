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

/**
 * Elastos Hive SDK configuration for oauth authentication.<br>
 * All app information should match the information in your app portal
 */
public class OAuthEntry {
	private final String clientId;
	private final String scope;
	private final String redirectURL;

	/**
	 * OAuthEntry constructor
	 * @param clientId User client id
	 * @param scope User scope
	 * @param redirectURL User redirect url
	 */
	public OAuthEntry(String clientId, String scope, String redirectURL) {
		this.clientId = clientId;
		this.scope = scope;
		this.redirectURL = redirectURL;
	}

	/**
	 * Get current client id
	 * @return Return current client id
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Get current scope
	 * @return current scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Get current redirect url
	 * @return current redirect url
	 */
	public String getRedirectURL() {
		return redirectURL;
	}
}
