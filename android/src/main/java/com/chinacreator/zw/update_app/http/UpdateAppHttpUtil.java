package com.chinacreator.zw.update_app.http;


import android.util.Log;

import androidx.annotation.NonNull;

import com.vector.update_app.HttpManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Vector
 * on 2017/6/19 0019.
 */

public class UpdateAppHttpUtil implements HttpManager {

    private String mApiKey;

    public UpdateAppHttpUtil(String mApiKey) {
        this.mApiKey = mApiKey;
    }

    /**
     * 异步get
     *
     * @param url      get请求地址
     * @param params   get参数
     * @param callBack 回调
     */
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final HttpManager.Callback callBack) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept","application/json");
        headers.put("content-type","application/json");
        headers.put("credentials","include");
        headers.put("x-api-key",mApiKey);
        OkHttpUtils.get()
                .url(url)
                .params(params)
                .headers(headers)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        Log.d("UpdateApp",e.getMessage());
                        callBack.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("UpdateApp",response);
                        callBack.onResponse(response);
                    }
                });
    }

    /**
     * 异步post
     *
     * @param url      post请求地址
     * @param params   post请求参数
     * @param callBack 回调
     */
    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params,@NonNull final Callback callBack) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept","application/json");
        headers.put("content-type","application/json");
        headers.put("credentials","include");
        headers.put("x-api-key",mApiKey);
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .headers(headers)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        Log.d("UpdateApp",e.getMessage());

                        callBack.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("UpdateApp",response);
                        callBack.onResponse(response);
                    }
                });

    }

    /**
     * 下载
     *
     * @param url      下载地址
     * @param path     文件保存路径
     * @param fileName 文件名称
     * @param callback 回调
     */
    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept","application/octet-stream");
        headers.put("credentials","include");
        headers.put("x-api-key",mApiKey);
        OkHttpUtils.get()
                .url(url)
                .headers(headers)
                .build()
                .connTimeOut(30*1000)
                .readTimeOut(30*1000)
                .execute(new FileCallBack(path, fileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        callback.onProgress(progress, total);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        callback.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        callback.onResponse(response);

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        callback.onBefore();
                    }
                });

    }
}