package com.chinacreator.zw.update_app.http;

import android.net.Uri;

import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;

import org.json.JSONObject;

public class ZWUpdateCallback extends UpdateCallback {

    private String downloadUrl;
    private int versionCode;

    public ZWUpdateCallback(String downloadUrl, int versionCode) {
        this.downloadUrl = downloadUrl;
        this.versionCode = versionCode;
    }


    /**
     * 解析json,自定义协议
     *
     * @param json 服务器返回的json
     * @return UpdateAppBean
     */
    protected UpdateAppBean parseJson(String json) {
        UpdateAppBean updateAppBean = new UpdateAppBean();
        try {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int _versionCode = jsonObject.optInt("versionCode");
                String version = jsonObject.optString("version")+"";
                String update = "";
                if (versionCode < _versionCode) {
                    update = "Yes";
                }
                String apkFileUrl = downloadUrl + "?url=" + Uri.encode(jsonObject.optString("filePath"), "UTF-8");
                updateAppBean.setUpdate(update)
                        //存放json，方便自定义解析
                        .setOriginRes(json)
                        .setNewVersion(version)
                        .setApkFileUrl(apkFileUrl)
                        .setTargetSize(jsonObject.optString("target_size"))
                        .setUpdateLog(jsonObject.optString("versionDesc"))
                        .setConstraint(jsonObject.optBoolean("isUpload"))
                        .setNewMd5(jsonObject.optString("md5Code"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateAppBean;
    }

    /**
     * 有新版本
     *
     * @param updateApp        新版本信息
     * @param updateAppManager app更新管理器
     */
    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
        updateAppManager.showDialogFragment();
    }

    /**
     * 网路请求之后
     */
    protected void onAfter() {
    }


    /**
     * 没有新版本
     *
     * @param error HttpManager实现类请求出错返回的错误消息，交给使用者自己返回，有可能不同的应用错误内容需要提示给客户
     */
    protected void noNewApp(String error) {
    }

    /**
     * 网络请求之前
     */
    protected void onBefore() {
    }

}