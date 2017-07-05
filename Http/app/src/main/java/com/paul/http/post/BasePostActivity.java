package com.paul.http.post;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import com.paul.http.BaseActivity;
import com.paul.http.R;
import com.paul.http.get.Api;
import com.paul.http.utils.HttpUtils;
import com.paul.http.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class BasePostActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_post);
    }

    //普通字符串提交（也可以提交Json字符串，也可以设置 MediaType JSON = MediaType.parse("application/json; charset=utf-8"); 去提交Json）
    public void onPostString(View view) {
        OkHttpClient client = HttpUtils.createClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("name unencoded", "Xiao Ming")
                .addEncoded("name encoded", "Xiao Ming")//注意观察这两种的区别
                .build();

        final Request request = new Request.Builder()
                .url(Api.base_url + "postString")
                .post(requestBody)
                .build();


        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("BasePostActivity#onResponse() : ");
            }
        });
    }

    //文件上传，带其它参数
    public void onUpload(View view) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "OGQ/Q.jpg");
        if (!file.exists()) {
            LogUtil.d("BasePostActivity#onUpload() : 文件不存在");
            return;
        }


        //如果要监听进度，可以不用 create 方式来创建 RequestBody
        RequestBody fileBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/octet-stream");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
            }
        };

        OkHttpClient client = HttpUtils.createClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "xiaoming")
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        final Request request = new Request.Builder()
                .url(Api.base_url + "postFile")
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d("BasePostActivity#onFailure() : " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("BasePostActivity#onResponse() : ");
            }
        });
    }

    //利用Retrofit2上传文件
    public void onUploadByRetrofit2(View view) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "OGQ/Q.jpg");
        if (!file.exists()) {
            LogUtil.d("BasePostActivity#onUpload() : 文件不存在");
            return;
        }
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", "Q.jpg", fileBody);
        HttpUtils.getApi().postFile("xiaoming", part).enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {

            }

            @Override
            public void onFailure(retrofit2.Call<String> call, Throwable t) {

            }
        });
    }

    //利用Retrofit2上传多个文件
    public void onUploadFiles(View view) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "OGQ/Q.jpg");
        if (!file.exists()) {
            LogUtil.d("BasePostActivity#onUpload() : 文件不存在");
            return;
        }

        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "OGQ/R.jpg");
        if (!file2.exists()) {
            LogUtil.d("BasePostActivity#onUpload() : 文件不存在");
            return;
        }

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file1", "Q.jpg", fileBody);//注意名字不要重复了

        RequestBody fileBody2 = RequestBody.create(MediaType.parse("application/octet-stream"), file2);
        MultipartBody.Part part2 = MultipartBody.Part.createFormData("file2", "Q.jpg", fileBody2);

        List<MultipartBody.Part> parts = new ArrayList<>(2);
        parts.add(part);
        parts.add(part2);
        HttpUtils.getApi().postFiles("xiaoming", parts).enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {

            }

            @Override
            public void onFailure(retrofit2.Call<String> call, Throwable t) {

            }
        });
    }
}
