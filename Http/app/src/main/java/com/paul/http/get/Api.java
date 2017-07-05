package com.paul.http.get;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by yang on 2017/7/1.
 * paulyung@outlook.com
 */

public interface Api {
    String base_url = "http://39.108.177.76:8080/";

    //使用 ResponseBody 获取字符串，并不是最好的方法
    @GET("getString")
    Call<ResponseBody> getString(@Query("name") String name);

    //使用转换器获取字符串 compile 'com.squareup.retrofit2:converter-scalars:2.3.0'
    @GET("getString")
    Call<String> getString();

    @GET("getJson")
    Call<String> getJson();

    //文件上传，这种方式不知为何 flask 那边说服务器不能理解请求的内容
    @Multipart
    @POST("/postFile")
    Call<String> postFile(@Part("name") String name, @Part("file") RequestBody fileBody);

    //文件上传，能够正常上传
    @Multipart
    @POST("/postFile")
    Call<String> postFile(@Part("name") String name, @Part MultipartBody.Part filePart);

    //多文件上传
    @Multipart
    @POST("/postFiles")
    Call<String> postFiles(@Part("name") String name, @Part List<MultipartBody.Part> fileParts);
}
