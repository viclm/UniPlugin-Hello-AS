package io.dcloud.uniplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

/**
 * TestModule 是一个自定义的 UniModule 模块,用于在 UniApp 框架中扩展 JavaScript API,
 * 提供 Android 原生功能的调用接口。该模块包含了几个主要功能,如启动原生页面、异步和同步的测试方法,
 * 以及初始化 OCR 识别功能。
 */
public class TestModule extends UniModule {

    String TAG = "TestModule";
    public static int REQUEST_CODE = 1000;

    private Context context;

    // 传递上下文
    public void attachContext(Context context) {
        this.context = context;
    }

    // run ui thread
    @UniJSMethod(uiThread = true)
    public void testAsyncFunc(JSONObject options, UniJSCallback callback) {
        Log.e(TAG, "testAsyncFunc--" + options);
        if (callback != null) {
            JSONObject data = new JSONObject();
            data.put("code", "success");
            callback.invoke(data);
            // callback.invokeAndKeepAlive(data);
        }
    }

    // run JS thread
    @UniJSMethod(uiThread = false)
    public JSONObject testSyncFunc() {
        Log.e(TAG, "testSyncFunc called");
        JSONObject data = new JSONObject();
        data.put("code", "success");
        Log.e(TAG, "testSyncFunc result--" + data);
        return data;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && data.hasExtra("respond")) {
            Log.e("TestModule", "原生页面返回----" + data.getStringExtra("respond"));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @UniJSMethod(uiThread = true)
    public void gotoNativePage() {
        if (mUniSDKInstance != null && mUniSDKInstance.getContext() != null) {
            Intent intent = new Intent(mUniSDKInstance.getContext(), NativePageActivity.class);
            ((Activity) mUniSDKInstance.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @UniJSMethod(uiThread = true)
    public void printAppKey() {
        Log.d(TAG, "printAppKey called");

        if (context == null) {
            Log.e(TAG, "Context is null, cannot get AppKey");
            return;
        }

        String appKey = getAppKey(context);
        Log.d("App", "AppKey from manifest: " + appKey);
        Toast.makeText(context, "AppKey: " + appKey, Toast.LENGTH_SHORT).show();
    }

    private String getAppKey(Context context) {
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            appKey = bundle.getString("dcloud_appkey");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "获取 AppKey 失败: " + e.getMessage(), e);
        }
        return appKey;
    }
}
