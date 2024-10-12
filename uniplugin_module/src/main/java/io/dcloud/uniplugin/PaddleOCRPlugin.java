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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * PaddleOCRPlugin：封装具体的 OCR 功能,负责模型的初始化和文本识别。
 */
public class PaddleOCRPlugin {

    private final OCR ocr;
    private final Context context;
    private static final String TAG = "PaddleOCRPlugin";

    public PaddleOCRPlugin(Context context) {
        this.context = context;
        this.ocr = new OCR(context);
    }

    public void initModel(final OCRCallback callback) {
        OcrConfig config = getOcrConfig();

        ocr.initModel(config, new OcrInitCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "模型加载成功");
                callback.onSuccess("模型加载成功");
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                Log.e(TAG, "模型加载失败: " + e.getMessage(), e);
                callback.onFail(e);
            }
        });
    }

    private static @NonNull OcrConfig getOcrConfig() {
        OcrConfig config = new OcrConfig();
        // .pdmodel：模型结构文件,包含了模型的计算图等信息。
        // .pdiparams：模型参数文件,包含了模型的权重等参数信息。
        // 这两个文件是一起使用的,一个定义了模型的结构,另一个定义了模型的具体参数。
        config.setModelPath("models"); // assets/models 目录
//        config.setClsModelFileName("cls"); // 文本方向分类模型的文件名前缀为 cls
//        config.setDetModelFileName("det"); // 检测模型的文件名前缀为 det
        config.setRecModelFileName("rec"); // 识别模型的文件名前缀为 rec
        config.setRunType(RunType.All);
        config.setCpuPowerMode(LitePowerMode.LITE_POWER_FULL);
//        config.setDrwwTextPositionBox(true);
//        config.setCpuThreadNum(1);
        config.setRecRunPrecision(RunPrecision.LiteInt8);
//        config.setDetRunPrecision(RunPrecision.LiteInt8);
//        config.setClsRunPrecision(RunPrecision.LiteInt8);
        return config;
    }

    public void recognizeText(String imagePath, final OCRCallback callback) throws IOException {
//        Bitmap bitmap = getBitmapFromAssets(imagePath);

        try {
            Log.e(TAG, "加载图片: " + imagePath);
            FileInputStream fis = new FileInputStream(imagePath);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();

            if (bitmap == null) {
                Log.e(TAG, "无法加载图片: " + imagePath);
                callback.onFail(new IOException("无法加载图片: " + imagePath));
                return;
            }
            ocr.run(bitmap, new OcrRunCallback() {
                @Override
                public void onSuccess(@NonNull OcrResult result) {
                    String simpleText = result.getSimpleText();
                    Log.i(TAG, "识别结果：" + simpleText);
                    callback.onSuccess(simpleText);
                }

                @Override
                public void onFail(@NonNull Throwable e) {
                    Log.e(TAG, "识别失败: " + e.getMessage(), e);
                    callback.onFail(e);
                }
            });

        } catch (IOException e) {
            Log.e("PaddleOCRPlugin", "Error reading file: " + e.getMessage());
        }
    }

    private Bitmap getBitmapFromAssets(String fileName) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "无法加载图片: " + fileName, e);
            return null;
        }
    }

    public interface OCRCallback {
        void onSuccess(String result);

        void onFail(Throwable e);
    }
}
