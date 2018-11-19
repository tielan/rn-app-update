package org.lzh.framework.updatepluginlib.module;


import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;

import org.json.JSONObject;
import org.lzh.framework.updatepluginlib.UpdateBuilder;
import org.lzh.framework.updatepluginlib.UpdateConfig;
import org.lzh.framework.updatepluginlib.base.UpdateParser;
import org.lzh.framework.updatepluginlib.impl.DefaultCheckWorker;
import org.lzh.framework.updatepluginlib.impl.DefaultDownloadWorker;
import org.lzh.framework.updatepluginlib.model.CheckEntity;
import org.lzh.framework.updatepluginlib.model.Update;


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
    public void config(ReadableArray args) {
        CheckEntity entity = new CheckEntity();
        UpdateConfig.getConfig()
                .setCheckEntity(entity)// 配置检查更新的API接口
                .setUpdateParser(new UpdateParser() {
                    @Override
                    public Update parse(String response) throws Exception {
                        JSONObject object = new JSONObject(response);
                        Update update = new Update();
                        // 此apk包的下载地址
                        update.setUpdateUrl(object.optString("update_url"));
                        // 此apk包的版本号
                        update.setVersionCode(object.optInt("update_ver_code"));
                        // 此apk包的版本名称
                        update.setVersionName(object.optString("update_ver_name"));
                        // 此apk包的更新内容
                        update.setUpdateContent(object.optString("update_content"));
                        // 此apk包是否为强制更新
                        update.setForced(true);
                        // 是否显示忽略此次版本更新按钮
                        update.setIgnore(object.optBoolean("ignore_able",false));
                        return update;
                    }
                }).setCheckWorker(DefaultCheckWorker.class).setDownloadWorker(DefaultDownloadWorker.class);
    }

    @ReactMethod
    public void check(){
        UpdateBuilder.create().check();// 启动更新任务
    }

}
