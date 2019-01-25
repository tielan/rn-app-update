package com.chinacreator.updateapp.module;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.vector.update_app.HttpManager;
import com.vector.update_app.exception.HttpException;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * Created by Vector
 * on 2017/6/19 0019.
 */

public class UpdateAppHttpUtil implements HttpManager {
    private HttpURLConnection urlConn;
    private File original;
    private File bak;
    private long contentLength;

    /**
     * 下载
     *
     * @param url      下载地址
     * @param path     文件保存路径
     * @param fileName 文件名称
     * @param callback 回调
     */
    @Override
    public void download(@NonNull final String url, @NonNull final String apiKey, @NonNull final String path, @NonNull final String fileName, @NonNull final FileCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadNotMainThread(url,apiKey,path,fileName,callback);
            }
        }).start();
    }

    public void downloadNotMainThread(@NonNull String url, @NonNull String apiKey, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
        try {
            File originalPath = new File(path);
            if(!originalPath.exists()){
                originalPath.mkdirs();
            }
            original = new File(path, fileName);
            URL httpUrl = new URL(url);
            urlConn = (HttpURLConnection) httpUrl.openConnection();
            setDefaultProperties(apiKey);
            urlConn.connect();
            int responseCode = urlConn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                urlConn.disconnect();
                throw new HttpException(responseCode, urlConn.getResponseMessage());
            }
            contentLength = urlConn.getContentLength();
            // 使用此bak文件进行下载。辅助进行断点下载。
            if (checkIsDownAll()) {
                urlConn.disconnect();
                urlConn = null;
                // notify download completed
                callback.onResponse(original);
                return;
            }
            callback.onBefore();
            createBakFile();
            FileOutputStream writer = supportBreakpointDownload(httpUrl, apiKey);

            long offset = bak.length();
            InputStream inputStream = urlConn.getInputStream();
            byte[] buffer = new byte[8 * 1024];
            int length;
            long start = System.currentTimeMillis();
            while ((length = inputStream.read(buffer)) != -1) {
                writer.write(buffer, 0, length);
                offset += length;
                long end = System.currentTimeMillis();
                if (end - start > 400) {
                    callback.onProgress((float) (offset*1.0)/contentLength, contentLength);
                    start = System.currentTimeMillis();
                }
            }

            urlConn.disconnect();

            writer.close();
            urlConn = null;
            // notify download completed
            original.delete();
            bak.renameTo(original);
            callback.onResponse(original);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e.getMessage());
        }

    }


    private boolean checkIsDownAll() {
        return original.length() == contentLength
                && contentLength > 0;
    }

    private FileOutputStream supportBreakpointDownload(URL httpUrl, String apiKey) throws IOException {

        String range = urlConn.getHeaderField("Accept-Ranges");
        if (TextUtils.isEmpty(range) || !range.startsWith("bytes")) {
            bak.delete();
            return new FileOutputStream(bak, false);
        }

        long length = bak.length();

        urlConn.disconnect();
        urlConn = (HttpURLConnection) httpUrl.openConnection();

        urlConn.setRequestProperty("RANGE", "bytes=" + length + "-" + contentLength);
        setDefaultProperties(apiKey);
        urlConn.connect();

        int responseCode = urlConn.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new HttpException(responseCode, urlConn.getResponseMessage());
        }

        return new FileOutputStream(bak, true);
    }

    private void setDefaultProperties(String apiKey) throws IOException {
        urlConn.setRequestProperty("x-api-key", apiKey);
        urlConn.setRequestMethod("GET");
        urlConn.setConnectTimeout(10000);
    }

    // 创建bak文件。
    private void createBakFile() {
        bak = new File(String.format("%s_%s", original.getAbsolutePath(), contentLength));
    }
}