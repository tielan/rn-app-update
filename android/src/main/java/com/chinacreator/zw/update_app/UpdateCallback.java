package com.chinacreator.zw.update_app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONObject;


/**
 * 新版本版本检测回调
 */
public class UpdateCallback {
    private boolean isManual;
    private Context mContext;

    public UpdateCallback(Context context, boolean isManual) {
        this.isManual = isManual;
        this.mContext = context;
    }
    public UpdateCallback(){

    }


    /**
     * 解析json,自定义协议
     *
     * @param json 服务器返回的json
     * @return UpdateAppBean
     */
    protected UpdateAppBean parseJson(String json,String host,String versionName) {
        UpdateAppBean updateAppBean = new UpdateAppBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String version = jsonObject.optString("version");
            String update = "";
            if(!TextUtils.isEmpty(version) && versionName.compareTo(version.trim()) < 0){
                update = "Yes";
            }
            String apkFileUrl = host+"/minio/apk/download?url="+ Uri.encode(jsonObject.optString("filePath"),"UTF-8");
            updateAppBean.setUpdate(update)
                    //存放json，方便自定义解析
                    .setOriginRes(json)
                    .setNewVersion(version)
                    .setApkFileUrl(apkFileUrl)
                    .setTargetSize(jsonObject.optString("target_size"))
                    .setUpdateLog(jsonObject.optString("versionDesc"))
                    .setConstraint(jsonObject.optBoolean("isUpload"))
                    .setNewMd5(jsonObject.optString("md5Code"))
                    .setFilename(jsonObject.optString("filename"));
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
     * @param error HttpManager实现类请求出错返回的错误消息，交给使用者自己返回，有可能不同的应用错误内容需要提示给客户
     */
    protected void noNewApp(String error) {
        if(isManual){
            Toast.makeText(mContext,error,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 网络请求之前
     */
    protected void onBefore() {

    }

}
