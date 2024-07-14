package io.dcloud.uniplugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.paddle.fastdeploy.LitePowerMode;
import com.equationl.fastdeployocr.OCR;
import com.equationl.fastdeployocr.OcrConfig;
import com.equationl.fastdeployocr.RunPrecision;
import com.equationl.fastdeployocr.RunType;
import com.equationl.fastdeployocr.bean.OcrResult;
import com.equationl.fastdeployocr.callback.OcrInitCallback;
import com.equationl.fastdeployocr.callback.OcrRunCallback;

/**
 * PaddleOCRPlugin：封装具体的 OCR 功能,负责模型的初始化和文本识别。
 */
public class PaddleOCRPlugin {

    private OCR ocr;
    private Context context;

    public PaddleOCRPlugin(Context context) {
        this.context = context;
        this.ocr = new OCR(context);
    }

    public void initModel(final OCRCallback callback) {
        OcrConfig config = new OcrConfig();
        config.setModelPath("models"); // assets/models 目录
        config.setClsModelFileName("cls");
        config.setDetModelFileName("det");
        config.setRecModelFileName("rec");
        config.setRunType(RunType.All);
        config.setCpuPowerMode(LitePowerMode.LITE_POWER_FULL);
        config.setDrwwTextPositionBox(true);
        config.setRecRunPrecision(RunPrecision.LiteFp16);
        config.setDetRunPrecision(RunPrecision.LiteFp16);
        config.setClsRunPrecision(RunPrecision.LiteFp16);

        ocr.initModel(config, new OcrInitCallback() {
            @Override
            public void onSuccess() {
                Log.i("PaddleOCRPlugin", "模型加载成功");
                callback.onSuccess("模型加载成功");
            }

            @Override
            public void onFail(Throwable e) {
                Log.e("PaddleOCRPlugin", "模型加载失败: " + e.getMessage(), e);
                callback.onFail(e);
            }
        });
    }

    public void recognizeText(String imagePath, final OCRCallback callback) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ocr.run(bitmap, new OcrRunCallback() {
            @Override
            public void onSuccess(@NonNull OcrResult result) {
                String simpleText = result.getSimpleText();
                callback.onSuccess(simpleText);
            }

            @Override
            public void onFail(Throwable e) {
                callback.onFail(e);
            }
        });
    }

    public interface OCRCallback {
        void onSuccess(String result);

        void onFail(Throwable e);
    }
}
