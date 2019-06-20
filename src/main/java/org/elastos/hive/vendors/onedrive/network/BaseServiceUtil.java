package org.elastos.hive.vendors.onedrive.network;


import org.elastos.hive.utils.CheckTextUtil;
import org.elastos.hive.utils.LogUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Package: org.elastos.hive.vendors.onedrive.network
 * ClassName: BaseServiceUtil
 * Created by ranwang on 2019/6/17.
 */
public class BaseServiceUtil {
    private static final int DEFAULT_TIMEOUT = 10;

    public static <S> S createService(Class<S> serviceClass, @NotNull String baseUrl , boolean useGsonConverter) throws Exception {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        if (useGsonConverter){
            retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        }else{
            retrofitBuilder.addConverterFactory(new StringConverterFactory());
        }

        if (!CheckTextUtil.isEmpty(baseUrl)) {
            retrofitBuilder.baseUrl(baseUrl);
        } else{
            throw new Exception("base url must not null , and end of /");
        }

        clientBuilder.interceptors().clear();

        if (LogUtil.debug){
            NetworkLogInterceptor networkLogInterceptor = new NetworkLogInterceptor();
            clientBuilder.interceptors().add(networkLogInterceptor);
        }

        OkHttpClient client = clientBuilder.build();
        Retrofit retrofit = retrofitBuilder.client(client).build();
        return retrofit.create(serviceClass);
    }
}