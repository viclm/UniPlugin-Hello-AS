package io.dcloud.uniplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

/**
 * TestModule 是一个自定义的 UniModule 模块,用于在 UniApp 框架中扩展 JavaScript API,
 * 提供 Android 原生功能的调用接口。该模块包含了几个主要功能,如启动原生页面、异步和同步的测试方法,
 * 以及初始化 OCR 识别功能。
 */
public class TestModule extends UniModule {
    private static final String TAG = "TestModule";
    private Context context;
    private static final String IMAGE_PATH = "pics/3.jpg";
//    private PaddleOCRModule ocrModule;
    private PaddleOCRPlugin paddleOCRPlugin;

    // 传递上下文
    public void attachContext(Context context) {
        this.context = context;
    }

    // run ui thread
    @UniJSMethod(uiThread = false)
    public void initOCR(UniJSCallback callback) {
        Log.i(TAG, "initOCR--");

        context = mUniSDKInstance.getContext();

        paddleOCRPlugin = new PaddleOCRPlugin(context);
        paddleOCRPlugin.initModel(new PaddleOCRPlugin.OCRCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "模型加载成功" + callback);
                if (callback != null) {
                    Log.i(TAG, "模型加载成功");
                    JSONObject data = new JSONObject();
                    data.put("code", 0);
                    data.put("message", "模型加载成功");
                    callback.invoke(data);
                }
            }

            @Override
            public void onFail(Throwable e) {
                if (callback != null) {
                    Log.i(TAG, "模型加载失败：" + e.getMessage());
                    JSONObject data = new JSONObject();
                    data.put("code", 1);
                    data.put("message", "模型加载失败：" + e.getMessage());
                    callback.invoke(data);
                }
            }
        });

//        ocrModule = new PaddleOCRModule();
//        ocrModule.attachContext(context); // 确保上下文被正确传递
//        ocrModule.initOCR(new PaddleOCRPlugin.OCRCallback() {
//            @Override
//            public void onSuccess(String result) {
//                Log.i(TAG, "模型加载成功");
//                JSONObject data = new JSONObject();
//                data.put("code", 0);
//                data.put("message", "模型加载成功");
//                callback.invoke(data);
//            }
//
//            @Override
//            public void onFail(Throwable e) {
//                Log.i(TAG, "模型加载失败：" + e.getMessage());
//                JSONObject data = new JSONObject();
//                data.put("code", 1);
//                data.put("message", "模型加载失败：" + e.getMessage());
//                callback.invoke(data);
//            }
//        });
    }

    @UniJSMethod(uiThread = false)
    public void recognizeText(JSONObject options, UniJSCallback callback) throws IOException {

        File file = new File(options.getString("filepath"));
        boolean exists = file.exists();
        if (exists) {
            Log.i(TAG, "File exists: " + options.getString("filepath"));
        } else {
            Log.i(TAG, "File does not exist: " + options.getString("filepath"));
        }

        paddleOCRPlugin.recognizeText(options.getString("filepath"), new PaddleOCRPlugin.OCRCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "识别结果：" + result);
                JSONObject data = new JSONObject();
                data.put("code", 0);
                data.put("message", "识别结果");
                data.put("data", result);
                callback.invoke(data);
            }

            @Override
            public void onFail(Throwable e) {
                Log.i(TAG, "识别失败：" + e.getMessage());
                JSONObject data = new JSONObject();
                data.put("code", 1);
                data.put("message", "识别失败：" + e.getMessage());
                callback.invoke(data);
            }
        });
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
}

