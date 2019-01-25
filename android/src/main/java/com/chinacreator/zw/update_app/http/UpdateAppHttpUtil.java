package com.chinacreator.zw.update_app.http;

import android.support.annotation.NonNull;

import com.chinacreator.zw.update_app.HttpManager;
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
    /**
     * 异步get
     *
     * @param url      get请求地址
     * @param params   get参数
     * @param callBack 回调
     */
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params,Map<String, String> headers, @NonNull final Callback callBack) {
        if(headers == null){
            headers = new HashMap<>();
        }
        headers.put("accept","application/json");
        headers.put("content-type","application/json");
        headers.put("credentials","include");
        OkHttpUtils.get()
                .url(url)
                .params(params)
                .headers(headers)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        callBack.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(String response, int id) {
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
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params,Map<String, String> headers, @NonNull final Callback callBack) {
        if(headers == null){
            headers = new HashMap<>();
        }
        headers.put("accept","application/json");
        headers.put("content-type","application/json");
        headers.put("credentials","include");
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .headers(headers)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        callBack.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(String response, int id) {
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
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName,Map<String, String> headers, @NonNull final FileCallback callback) {
        if(headers == null){
            headers = new HashMap<>();
        }
        headers.put("accept","application/octet-stream");
        headers.put("credentials","include");
        OkHttpUtils.getInstance().timeout(120*1000);
        OkHttpUtils.getInstance().debug(true,"down");
        OkHttpUtils.get()
                .url(url)
                .headers(headers)
                .build()
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