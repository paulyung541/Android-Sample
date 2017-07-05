package com.paul.http.get;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.paul.http.BaseActivity;
import com.paul.http.R;
import com.paul.http.utils.HttpUtils;
import com.paul.http.utils.LogUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GETActivity extends BaseActivity {
    TextView mPrint;
    EditText mInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get);

        mPrint = (TextView) findViewById(R.id.textView);
        mInput = (EditText) findViewById(R.id.editText);
    }

    private void p(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("GETActivity#p() : " + s);
                mPrint.setText(s);
            }
        });
    }

    //请求字符串
    public void onGetString(View view) {
        p("请求中...");
        OkHttpClient client = HttpUtils.createClient();
        final Request request = new Request.Builder()
                .url(Api.base_url + "getString")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                p(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                p(response.body().string());
            }
        });
    }

    //请求Json字符串
    public void onGetJson(View view) {
        p("请求中...");
        OkHttpClient client = HttpUtils.createClient();
        final Request request = new Request.Builder()
                .url(Api.base_url + "getJson")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                p(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                p(response.body().string());
            }
        });
    }

    //利用Retrofit2封装请求指定字符串
    public void onGetStringByRetrofit2(View view) {
        HttpUtils.getApi().getString("xiaoming").enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    if (response.code() > 300) {
                        p(response.errorBody().string());
                        return;
                    }
                    p(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //利用Retrofit2封装请求指定Json
    public void onGetJsonByRetrofit2(View view) {
        HttpUtils.getApi().getJson().enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {
                p(response.body());
            }

            @Override
            public void onFailure(retrofit2.Call<String> call, Throwable t) {

            }
        });
    }
}
