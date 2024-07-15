package io.dcloud.uniplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 在原生 Android 环境中使用 PaddleOCRModule 进行 OCR 功能的演示,展示如何初始化和识别文本。
 */
public class NativePageActivity extends Activity {
    private static final String IMAGE_PATH = "pics/3.jpg";
    private PaddleOCRModule ocrModule;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout rootView = new FrameLayout(this);
        TextView textView = new TextView(this);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(30);
        textView.setText("点击我将返回 并携带参数返回");
        rootView.addView(textView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
        textView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("respond", "我是原生页面");
            setResult(TestModule.REQUEST_CODE, intent);
            finish();
        });
        setContentView(rootView);

        TestModule testModule = new TestModule();
        context = getApplicationContext();
        testModule.attachContext(context);
        testModule.printAppKey();

        ocrModule = new PaddleOCRModule();
        ocrModule.attachContext(context); // 确保上下文被正确传递
        ocrModule.initOCR(new PaddleOCRPlugin.OCRCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(context, "模型加载成功", Toast.LENGTH_SHORT).show();
                // 模型初始化成功后直接执行识别图片的功能
                recognizeImage();
            }

            @Override
            public void onFail(Throwable e) {
                Toast.makeText(context, "模型加载失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void recognizeImage() {
        ocrModule.recognizeText(IMAGE_PATH, new PaddleOCRPlugin.OCRCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(context, "识别结果：" + result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(Throwable e) {
                Toast.makeText(context, "识别失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
