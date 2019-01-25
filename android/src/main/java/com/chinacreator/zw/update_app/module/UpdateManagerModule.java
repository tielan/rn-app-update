package com.chinacreator.zw.update_app.module;

import android.text.TextUtils;

import com.chinacreator.zw.update_app.UpdateAppManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/18 0018.
 */

public class UpdateManagerModule  extends ReactContextBaseJavaModule {

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
     * @param paramsMap
     */
    @ReactMethod
    public void init(ReadableMap paramsMap) {
        String mUpdateUrl = paramsMap.getString("url");
        String apiKey = paramsMap.getString("apiKey");
        String manual = paramsMap.getString("manual");
        ReadableMap paramsMapString = paramsMap.hasKey("params") ? paramsMap.getMap("params") : null;

        if(TextUtils.isEmpty(mUpdateUrl)){
            return;
        }
        Map<String,String> params = new HashMap<String,String>();
        if(paramsMapString != null){
            for (String key:paramsMapString.toHashMap().keySet()) {
               params.put(key,paramsMapString.getString(key));
            }
        }
        new UpdateAppManager
                .Builder()
                .setActivity(mReactContext.getCurrentActivity())
                .setUpdateUrl(mUpdateUrl)
                .setParams(params)
                .setApiKey(apiKey)
                .build()
                .update("true".equals(manual));
    }
}
