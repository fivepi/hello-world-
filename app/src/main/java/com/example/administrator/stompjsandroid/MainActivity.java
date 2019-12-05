package com.example.administrator.stompjsandroid;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    WebView mWebView = null;
    LinearLayout linearLayout;
    ScrollView scrollView;
    SimpleDateFormat sDateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.linearlayout);
        scrollView = findViewById(R.id.scrollview);

        if (mWebView == null) {
            initWebView();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = "javascript:connect('http://newh5.ly9900.com/message/access','33333333333:12345655555','24','3')"; //调用js connect方法
                mWebView.loadUrl(url);
            }
        },1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanWebView();
    }

    /**
     * 初始化webview
     */
    protected void initWebView() {
        mWebView = new WebView(this);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsToJava(), "stub");  //JsToJava是内部类，代码在后面。stub是接口名字。
        mWebView.loadUrl("file:///android_asset/test.html"); //加载本地html文件
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });
    }

    /**
     * js方法回调
     */
    private class JsToJava {
        @JavascriptInterface
        public void jsCallbackMethod(String result) {
            addLog(sDateFormat.format(System.currentTimeMillis()) + "     " + result);
        }
    }


    void cleanWebView() {
        // 清WebView
        if (mWebView != null) {
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();

            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.freeMemory();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    private void addLog(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView t = new TextView(MainActivity.this);
                t.setText(message);
                linearLayout.addView(t);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}
