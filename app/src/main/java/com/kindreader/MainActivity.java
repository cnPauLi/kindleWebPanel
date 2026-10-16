package com.kindreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

    // ===================== 【默认服务器地址，仅首次安装时使用】 =====================
    private static final String DEFAULT_SERVER_URL = "http://192.168.1.100:8080/page";
    // ==============================================================================

    // 配置持久化
    private static final String PREFS_NAME = "kindreader_config";
    private static final String KEY_SERVER_URL = "server_url";

    // 隐藏入口：连续点击左上角 5 次触发
    private static final int HIDDEN_TAP_COUNT = 5;      // 需要连点次数
    private static final long TAP_INTERVAL_MS = 1500L;  // 两次点击最大间隔（毫秒）
    // 左上角热区（像素）
    private static final int HOTSPOT_WIDTH = 220;
    private static final int HOTSPOT_HEIGHT = 220;

    private WebView webView;

    // 用于统计左上角连点
    private int tapCount = 0;
    private long lastTapTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏：隐藏系统 UI（状态栏 / 导航栏）
        View decorView = getWindow().getDecorView();
        int uiOption = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOption);

        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();

        // WebView 低内存优化配置
        webSettings.setJavaScriptEnabled(true); // 不需要 JS 可改为 false 进一步省内存
        webSettings.setDomStorageEnabled(false);
        webSettings.setDatabaseEnabled(false);
        // setAppCacheEnabled 在 API 33 起已被移除，此处用反射调用：
        // 老设备(API 19~32)可真正关闭 AppCache；新 SDK 下编译不报错。
        setAppCacheEnabledCompat(webSettings);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 完全禁用缓存
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // 禁止拉起外部浏览器，全部在本 WebView 内打开链接
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // 拦截触摸事件：判断是否命中了“左上角隐藏入口”
        webView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    && handleHotspotTap(event.getX(), event.getY())) {
                // 命中隐藏入口，补一个 performClick 以保持无障碍语义，
                // 然后吞掉这次点击，不传给网页
                v.performClick();
                return true;
            }
            return false;
        });

        setContentView(webView);
        webView.loadUrl(getServerUrl());
    }

    /**
     * 统计左上角连点，达到阈值则弹出设置框。
     *
     * @return true 表示本次触摸落在热区内（调用方应吞掉该事件）
     */
    private boolean handleHotspotTap(float x, float y) {
        if (x > HOTSPOT_WIDTH || y > HOTSPOT_HEIGHT) {
            // 不在左上角热区，重置计数
            tapCount = 0;
            return false;
        }

        long now = System.currentTimeMillis();
        if (now - lastTapTime > TAP_INTERVAL_MS) {
            // 距离上次点击太久，重新计数
            tapCount = 1;
        } else {
            tapCount++;
        }
        lastTapTime = now;

        if (tapCount >= HIDDEN_TAP_COUNT) {
            tapCount = 0;
            showServerUrlDialog();
        }
        return true;
    }

    /** 读取配置的地址，未配置则返回默认值。 */
    private String getServerUrl() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String url = sp.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL);
        return TextUtils.isEmpty(url) ? DEFAULT_SERVER_URL : url;
    }

    /** 保存地址到配置。 */
    private void saveServerUrl(String url) {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        sp.edit().putString(KEY_SERVER_URL, url).apply();
    }

    /** 弹出“设置请求地址”对话框：屏幕中央的输入框 + 保存按钮。 */
    private void showServerUrlDialog() {
        // 用代码构建布局，避免新增 XML 资源
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(20);
        container.setPadding(pad, pad, pad, pad);

        EditText input = new EditText(this);
        input.setHint("请输入请求地址，例如 http://192.168.1.100:8080/page");
        input.setText(getServerUrl());
        input.setSelection(input.getText().length());
        input.setSingleLine(true);
        container.addView(input, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("设置请求地址")
                .setView(container)
                .setCancelable(true)
                .setPositiveButton("保存", null) // 先占位，稍后自定义点击避免误关
                .setNegativeButton("取消", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button saveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button cancelBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // 保存
            saveBtn.setOnClickListener(view -> {
                String url = input.getText().toString().trim();
                if (TextUtils.isEmpty(url)) {
                    Toast.makeText(this, "地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    Toast.makeText(this, "地址需以 http:// 或 https:// 开头", Toast.LENGTH_LONG).show();
                    return;
                }
                saveServerUrl(url);
                dialog.dismiss();
                Toast.makeText(this, "已保存，正在加载…", Toast.LENGTH_SHORT).show();
                if (webView != null) {
                    webView.loadUrl(url);
                }
            });

            // 取消
            cancelBtn.setOnClickListener(view -> dialog.dismiss());
        });

        // 让对话框在墨水屏上更清晰，并在弹出时显示软键盘
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        dialog.show();

        // 屏幕中央展示
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            // 输入框背景设为白色、文字黑色，保证墨水屏可读
            input.setTextColor(Color.BLACK);
            input.setBackgroundColor(Color.WHITE);
        }
    }

    private int dpToPx(@SuppressWarnings("SameParameterValue") int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * 反射调用 WebSettings#setAppCacheEnabled(boolean)。
     * 该方法在 API 33 起已被移除，直接调用会在新版 SDK 编译报错；
     * 用反射后，在老设备上仍然生效，新 SDK 下编译通过。
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    private void setAppCacheEnabledCompat(WebSettings settings) {
        try {
            WebSettings.class.getMethod("setAppCacheEnabled", boolean.class)
                    .invoke(settings, false);
        } catch (Throwable ignored) {
            // 方法不存在（新系统已移除）或反射失败，直接忽略
        }
    }

    // 返回键：优先 Web 页面回退，不能回退再退出 App
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 页面销毁：彻底销毁 WebView，【防止内存泄漏，关键】
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.setWebViewClient(null);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
