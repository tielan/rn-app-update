package com.chinacreator.zw.update_app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.chinacreator.zw.update_app.http.UpdateAppHttpUtil;
import com.chinacreator.zw.update_app.http.ZWSilenceUpdateCallback;
import com.chinacreator.zw.update_app.http.ZWUpdateCallback;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.vector.update_app.SilenceUpdateCallback;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/18 0018.
 */

public class UpdateManagerModule extends ReactContextBaseJavaModule {

    public static final String REACT_CLASS = "UpdateManagerModule";
    private ReactApplicationContext mReactContext;

    UpdateManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactContext = reactContext;

    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    /**
     * 初始化更新
     *
     * @param paramsMap
     */
    @ReactMethod
    public void init(ReadableMap paramsMap) {
        String mainUrl = paramsMap.getString("mainUrl");
        String mApiKey = paramsMap.getString("apiKey");
        String orgId = paramsMap.getString("orgId");
        if (TextUtils.isEmpty(mainUrl)) {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("orgId", orgId);

        new UpdateAppManager
                .Builder()
                .setActivity(mReactContext.getCurrentActivity())
                .setUpdateUrl(mainUrl + "/appVersion/checkoutNewAppByOrgId")
                .setParams(params)
                .setHttpManager(new UpdateAppHttpUtil(mApiKey))
                .build()
                .checkNewApp(new ZWUpdateCallback(mainUrl + "/minio/apk/download", packageCode(mReactContext)));
    }


    @ReactMethod
    public void check(ReadableMap paramsMap,final Callback callback) {
        String mainUrl = paramsMap.getString("mainUrl");
        String mApiKey = paramsMap.getString("apiKey");
        String orgId = paramsMap.getString("orgId");
        if (TextUtils.isEmpty(mainUrl)) {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("orgId", orgId);
        new UpdateAppManager
                .Builder()
                .setActivity(mReactContext.getCurrentActivity())
                .setUpdateUrl(mainUrl + "/appVersion/checkoutNewAppByOrgId")
                .setParams(params)
                .setHttpManager(new UpdateAppHttpUtil(mApiKey))
                .build()
                .checkNewApp(new ZWUpdateCallback(mainUrl + "/minio/apk/download", packageCode(mReactContext)){

                    @Override
                    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        callback.invoke(true);
                    }

                    @Override
                    protected void noNewApp(String error) {
                        callback.invoke(false);
                    }
                });

    }

    @ReactMethod
    public void silenceUpdateApp(ReadableMap paramsMap) {
        String mainUrl = paramsMap.getString("mainUrl");
        String mApiKey = paramsMap.getString("apiKey");
        String orgId = paramsMap.getString("orgId");
        boolean onlyWifi = paramsMap.hasKey("onlyWifi") ? paramsMap.getBoolean("onlyWifi") : true;

        if (TextUtils.isEmpty(mainUrl)) {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("orgId", orgId);
        if (onlyWifi) {
            new UpdateAppManager
                    .Builder()
                    //当前Activity
                    .setActivity(mReactContext.getCurrentActivity())
                    //更新地址
                    .setUpdateUrl(mainUrl + "/appVersion/checkoutNewAppByOrgId")
                    .setParams(params)
                    //实现httpManager接口的对象
                    .setHttpManager(new UpdateAppHttpUtil(mApiKey))
                    //只有wifi下进行，静默下载(只对静默下载有效)
                    .setOnlyWifi()
                    .build()
                    .checkNewApp(new ZWSilenceUpdateCallback(mainUrl + "/minio/apk/download", packageCode(mReactContext)));

        } else {
            new UpdateAppManager
                    .Builder()
                    //当前Activity
                    .setActivity(mReactContext.getCurrentActivity())
                    //更新地址
                    .setUpdateUrl(mainUrl + "/appVersion/checkoutNewAppByOrgId")
                    .setParams(params)
                    //实现httpManager接口的对象
                    .setHttpManager(new UpdateAppHttpUtil(mApiKey))
                    .build()
                    .checkNewApp(new ZWSilenceUpdateCallback(mainUrl + "/minio/apk/download", packageCode(mReactContext)));
        }
    }

    public static int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int name = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

}
