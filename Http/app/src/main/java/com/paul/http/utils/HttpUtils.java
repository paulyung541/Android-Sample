package com.paul.http.utils;

import com.paul.http.get.Api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by yang on 2017/7/2.
 * paulyung@outlook.com
 */

public class HttpUtils {
    private static HttpUtils _INSTANCE;
    private static OkHttpClient mClient;
    private static Api api;

    static {
        api = getApi();
    }

    public static OkHttpClient createClient() {
        if (mClient != null)
            return mClient;
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtil.d("GETActivity#log() : " + message);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logInterceptor)
                .build();
        return mClient = client;
    }

    public static Api getApi() {
        if (api != null)
            return api;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.base_url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(createClient())
                .build();

        return api = retrofit.create(Api.class);
    }
}
