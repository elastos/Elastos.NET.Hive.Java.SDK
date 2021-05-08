package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;

/**
* Vault provides the scripting service to general users to execute a preset
* script by the vault owner.
*/
public interface ScriptingInvocationService {
	/**
	 * Invoke the execution of a specified script registered previously by the vault
	 * owner, where the script is defined with certain preset routines.
	 * It's the general invocation method for external users to call.
	 *
	 * @param <T> String, byte[], JsonNode, Reader
	 * @param name  the name of script to invoke.
	 * @param params the parameters as input to the invocation.
	 * @param appDid The owner's application did.
	 * @param resultType String, byte[], JsonNode, Reader
	 * @return String, byte[], JsonNode, Reader
	 */
	<T> CompletableFuture<T> callScript(String name, JsonNode params, String appDid, Class<T> resultType);

	/**
	 * Invoke the execution of the script to upload a file in the streaming mode.
     * The upload works a bit differently from other executable queries because there
     * are two steps to this executable. First, register a script on the vault,
     * then you call this API actually to upload the file
     *
	 * @param <T> Reader or InputStream class
	 * @param transactionId the streaming identifier to the upload process
	 * @param resultType Reader or InputStream class
	 * @return Reader or InputStream class
	 */
	<T> CompletableFuture<T> uploadFile(String transactionId, Class<T> resultType);

	/**
	 * Invoke the execution of the script to download a file in the streaming mode.
     * The upload works a bit differently from other executable queries because there
     * are two steps to this executable. First, register a script on the vault,
     * then you call this API actually to download the file
     *
	 * @param <T> Reader or InputStream class
     * @param transactionId the streaming identifier to the upload process
	 * @param resultType Reader or InputStream class
	 * @return Reader or InputStream class
	 */
	<T> CompletableFuture<T> downloadFile(String transactionId, Class<T> resultType);
}
