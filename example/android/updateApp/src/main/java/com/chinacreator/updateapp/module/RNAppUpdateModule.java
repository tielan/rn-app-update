package com.chinacreator.updateapp.module;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.chinacreator.updateapp.module.update.DUpdateAppManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.listener.ExceptionHandler;

import org.json.JSONObject;

import java.util.HashMap;


public class RNAppUpdateModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNAppUpdateModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNAppUpdate";
    }

    @ReactMethod
    public void update(ReadableMap args) {

        String new_version = args.hasKey("version") ? args.getString("version") : null;
        String update = getVersionName().compareTo(new_version) < 0 ? "Yes" : "No";

        String target_size = args.hasKey("target_size") ? args.getString("target_size") : null;
        String update_log = args.hasKey("versionDesc") ? args.getString("versionDesc") : null;
        boolean constraint = args.hasKey("constraint") ? args.getBoolean("constraint") : false;

        String downloadUrl = args.hasKey("downloadUrl") ? args.getString("downloadUrl") : null;
        String xApiKey = args.hasKey("xApiKey") ? args.getString("xApiKey") : null;

        UpdateAppBean updateAppBean = new UpdateAppBean();
        try {
            updateAppBean.setUpdate(update)
                    .setOriginRes("")
                    .setNewVersion(new_version)
                    .setApkFileUrl(downloadUrl)
                    .setTargetSize(target_size)
                    .setUpdateLog(update_log)
                    .setConstraint(constraint)
                    .setApiKey(xApiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new DUpdateAppManager
                .Builder()
                .setActivity(getCurrentActivity())
                .setHttpManager(new UpdateAppHttpUtil())
                .build()
                .checkNewApp(updateAppBean);
    }

    //获取当前App版本名称
    public String getVersionName() {
        PackageManager manager = getCurrentActivity().getPackageManager();
        String versionName = "";
        try {
            PackageInfo info = manager.getPackageInfo(getCurrentActivity().getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
