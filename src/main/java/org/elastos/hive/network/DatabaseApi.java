package org.elastos.hive.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DatabaseApi {
	@POST(BaseApi.API_VERSION + "/db/create_collection")
	Call<ResponseBody> createCollection(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/db/delete_collection")
	Call<ResponseBody> deleteCollection(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/db/insert_one")
	Call<ResponseBody> insertOne(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/db/insert_many")
	Call<ResponseBody> insertMany(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/db/update_one")
	Call<ResponseBody> updateOne(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/db/update_many")
	Call<ResponseBody> updateMany(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/db/delete_one")
	Call<ResponseBody> deleteOne(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/db/delete_many")
	Call<ResponseBody> deleteMany(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/db/count_documents")
	Call<ResponseBody> countDocs(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/db/find_one")
	Call<ResponseBody> findOne(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/db/find_many")
	Call<ResponseBody> findMany(@Body RequestBody body);
}
