package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.HiveFile;
import org.elastos.hive.exceptions.HiveException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

final class OneDriveFile extends HiveFile {
	private final AuthHelper authHelper;

	private String pathName;
	private OneDrive oneDrive;
	private String createdDateTime;
	private String lastModifiedDateTime;
	private String id;
	private boolean isFile;
	private boolean isDirectory;

	public OneDriveFile(OneDrive oneDrive, String pathName) {
		super(oneDrive);
		authHelper = oneDrive.getAuthHelper();
		this.pathName = pathName;
		this.oneDrive = oneDrive;
	}
	
	public void initialize(String id, boolean isDir, String createdDateTime, String lastModifiedDateTime) {
		this.id = id;
		this.isDirectory = isDir;
		this.isFile = !isDir;
		this.createdDateTime = createdDateTime;
		this.lastModifiedDateTime = lastModifiedDateTime;
	}

	@Override
	public @NotNull String getPath() {
		return pathName;
	}

	@Override
	@NotNull
	public String getParentPath() {
		if (pathName.equals("/")) {
			return pathName;
		}

		return pathName.substring(0, pathName.lastIndexOf("/") + 1);
	}

	@Override
	@NotNull
	public HiveFile getParent() throws HiveException {
		return super.getDrive().getFile(getParentPath());
	}

	@Override
	@NotNull
	public String getCreatedTimeDate() {
		return createdDateTime;
	}

	@Override
	@NotNull
	public String getLastModifiedDateTime() {
		HttpResponse<JsonNode> response;

		try {
			String requestUrl = oneDrive.getRootPath() + ":/" + pathName;
			response = Unirest.get(requestUrl)
					.header("Authorization", "bearer " + authHelper.getAuthInfo().getAccessToken())
					.asJson();
			if (response.getStatus() == 200) {
				try {
					JSONObject baseJson = response.getBody().getObject();
					JSONObject fileSystemInfo = (JSONObject)baseJson.get("fileSystemInfo");
					lastModifiedDateTime = fileSystemInfo.getString("lastModifiedDateTime");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
			else {
				System.out.println("Invoking [getLastModifiedDateTime] has error: status=" + response.getStatus());
			}
		} 
		catch (UnirestException e) {
			e.printStackTrace();
		}
		
		return lastModifiedDateTime;
	}

	@Override
	public void updateDatetime(@NotNull String newDateTime) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public boolean isFile() {
		return isFile;
	}

	@Override
	public boolean isDirectory() {
		return isDirectory;
	}

	@Override
	public void copyTo(@NotNull String newPath) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void copyTo(@NotNull HiveFile newFile) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void renameTo(@NotNull String newPath) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void renameTo(@NotNull HiveFile newFile) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void delete() throws HiveException {
		authHelper.checkExpired();
		doHttpDelete();
	}

	@Override
	public @NotNull HiveFile[] list() throws HiveException {
		authHelper.checkExpired();
		if (!isDirectory()) {
			return null;
		}

		HiveFile[] files = null;
		try {
			String requestUrl = String.format("%s/items/%s/children", OneDrive.API_URL, this.id);

			System.out.println("Invoking [list] requestUrl=" + requestUrl);
			HttpResponse<JsonNode> response = Unirest.get(requestUrl)
					.header("Authorization", "bearer " + authHelper.getAuthInfo().getAccessToken())
					.asJson();

			System.out.println("Invoking [list] body=" + response.getBody());
			System.out.println("Invoking [list] getStatus=" + response.getStatus());
			if (response.getStatus() == 200) {
				JSONObject baseJson = response.getBody().getObject();
				JSONArray values = baseJson.getJSONArray("value"); 
				int len = values.length();
				System.out.println("valuse len=="+len);
				if (len > 0) {
					files = new HiveFile[len];
					for (int i = 0; i < len; i++) {
						JSONObject itemJson = values.getJSONObject(i);
						String name = itemJson.getString("name");
						OneDriveFile file = new OneDriveFile(oneDrive, name);

						String id = itemJson.getString("id");
						JSONObject fileSystemInfo = (JSONObject)itemJson.get("fileSystemInfo");
						String createdDateTime = fileSystemInfo.getString("createdDateTime");
						String lastModifiedDateTime = fileSystemInfo.getString("lastModifiedDateTime");
						boolean isDir = itemJson.has("folder");
						System.out.println("name: " + name + ", isDir=="+isDir);
						file.initialize(id, isDir, createdDateTime, lastModifiedDateTime);
						files[i] = file;
					}
				}
			}
			else {
				throw new HiveException("Invoking the list has error.");	
			}
		} 
		catch (UnirestException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return files;
	}

	@Override
	public HiveFile mkdir(@NotNull String pathName) throws HiveException {
		authHelper.checkExpired();

		if (!isDirectory()) {
			throw new HiveException("This is a file, can't create a child folder.");
		}

		OneDriveFile file = null;
		try {
			String requestUrl = String.format("%s/items/%s/children", OneDrive.API_URL, this.id);

			System.out.println("Invoking [mkdir] requestUrl=" + requestUrl);
			//conflictBehavior' value : fail, replace, or rename
			String body = "{\"name\": \"" + pathName + "\", \"folder\": { }, \"@microsoft.graph.conflictBehavior\": \"fail\"}";

			System.out.println("Invoking [mkdir] before body=" + body);
			HttpResponse<JsonNode> response = Unirest.post(requestUrl)
					.header("Authorization", "bearer " + authHelper.getAuthInfo().getAccessToken())
					.header("Content-Type", "application/json")
					.body(body)
					.asJson();

			System.out.println("Invoking [mkdir] body=" + response.getBody());
			System.out.println("Invoking [mkdir] StatusText=" + response.getStatusText());
			JSONObject baseJson = response.getBody().getObject();
			if (response.getStatus() != 200 && response.getStatus() != 201) {
				System.out.println("Invoking [mkdir] has error: status=" + response.getStatus());
				JSONObject errorJson = (JSONObject)baseJson.get("error");
				String errorMsg = errorJson.getString("message");
				System.out.println("Invoking [mkdir] error message: " + errorMsg);
				throw new HiveException(errorMsg);
			}

			file = new OneDriveFile(oneDrive, pathName);
			String id = baseJson.getString("id"); 
			JSONObject fileSystemInfo = (JSONObject)baseJson.get("fileSystemInfo");
			String createdDateTime = fileSystemInfo.getString("createdDateTime");
			String lastModifiedDateTime = fileSystemInfo.getString("lastModifiedDateTime");
			boolean isDir = baseJson.has("folder");
			System.out.println("id: " + id + ", isDir=="+isDir);
			file.initialize(id, isDir, createdDateTime, lastModifiedDateTime);
		} 
		catch (UnirestException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		return file;
	}

	@Override
	public void mkdirs(@NotNull String pathName) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void close() throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public String toString() {
		// TODO
		return null;
	}

	void doHttpGet() throws HiveException {
		HttpResponse<JsonNode> response;

		try {
			response = Unirest.get(oneDrive.getRootPath() + pathName)
					.header("accept", "application/json")
					.header("Authorization", "bearer ") // TODO:
					.asJson();
			if (response.getStatus() == 200) {
				// TODO;
			} else {
				// TODO;
			}
		} catch (UnirestException e) {
			// TODO
			e.printStackTrace();
		}

		// TOOD: response;
	}

	void doHttpDelete() throws HiveException {
		HttpResponse<JsonNode> response;

		try {
			response = Unirest.delete(oneDrive.getRootPath() + ":/" + pathName)
					.header("Authorization", "bearer") //TODO
					.asJson();
			if (response.getStatus() == 200) {
				// TODO;
			} else {
				// TODO;
			}
		} catch (UnirestException e) {
			// TODO
			e.printStackTrace();
		}
	}
}
