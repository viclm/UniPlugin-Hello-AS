package io.dcloud.uniplugin;

import android.content.Context;
import android.widget.Toast;

import io.dcloud.feature.uniapp.common.UniModule;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;

/**
 * PaddleOCRModule：集成 OCR 功能到 UniApp 框架中,使得 JavaScript 可以调用。
 */
public class PaddleOCRModule extends UniModule {
    private Context context;
    private PaddleOCRPlugin paddleOCRPlugin;

    public void attachContext(Context context) {
        this.context = context;
    }

    @UniJSMethod(uiThread = true)
    public void initOCR(PaddleOCRPlugin.OCRCallback callback) {
        if (context == null) {
            context = mUniSDKInstance.getContext();
        }
        paddleOCRPlugin = new PaddleOCRPlugin(context);
        paddleOCRPlugin.initModel(new PaddleOCRPlugin.OCRCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(context, "OCR初始化成功", Toast.LENGTH_SHORT).show();
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onFail(Throwable e) {
                Toast.makeText(context, "OCR初始化失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                if (callback != null) {
                    callback.onFail(e);
                }
            }
        });
    }

    @UniJSMethod(uiThread = true)
    public void recognizeText(String imagePath, PaddleOCRPlugin.OCRCallback ocrCallback) {
        if (paddleOCRPlugin != null) {
            paddleOCRPlugin.recognizeText(imagePath, new PaddleOCRPlugin.OCRCallback() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(mUniSDKInstance.getContext(), "识别结果：" + result, Toast.LENGTH_LONG).show();
                    if (ocrCallback != null) {
                        ocrCallback.onSuccess(result);
                    }
                }

                @Override
                public void onFail(Throwable e) {
                    Toast.makeText(mUniSDKInstance.getContext(), "识别失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                    if (ocrCallback != null) {
                        ocrCallback.onFail(e);
                    }
                }
            });
        } else {
            Toast.makeText(mUniSDKInstance.getContext(), "OCR尚未初始化", Toast.LENGTH_LONG).show();
        }
    }
}
