package com.erhuoapp.erhuo.activity;

import java.util.List;

import org.apache.http.cookie.Cookie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.IConstants;

/**
 * 浏览器界面
 *
 * @author hujiawei
 * @datetime 2015/1/17
 */
public class ActivityBrowser extends FragmentActivity{

    private final String TAG = "ActivityBrowser";

    private View mView = null;
    private WebView webView;
    private WebChromeClient chromeClient;
    private WebChromeClient.CustomViewCallback mCallback;
    private TextView textViewTitle;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        textViewTitle = (TextView) findViewById(R.id.tv_browser_title);
        frameLayout = (FrameLayout) findViewById(R.id.fl_browser_result);
        webView = (WebView) findViewById(R.id.wv_browser_webview);
        chromeClient = new MyChromeClient();
        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        //webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);

        String result = getIntent().getStringExtra("url");
        if (result != null) {
        	CookieSyncManager.createInstance(this);
        	CookieManager cookieManager = CookieManager.getInstance();
        	cookieManager.setAcceptCookie(true);  
            cookieManager.removeSessionCookie();// 移除  
            String CookieStr = cookieManager.getCookie(result);
            Log.i(TAG,"- TempWebViewonPageFinished " + CookieStr);
        	String oldCookie = 
        			"id"+"="+AppUtil.getInstance().getUserId()+
        			"; domain=" + "www.erhuoapp.com";  
        	cookieManager.setCookie(result, oldCookie);// 设置cookie的id，原本用循环比较好，这里信息较少且已知，所以直接加了
        	CookieSyncManager.getInstance().sync();
        	
        	String oldCookie2 = 
        			"token"+"="+AppUtil.getInstance().getToken()+
        			"; domain=" + "www.erhuoapp.com";  
        	cookieManager.setCookie(result, oldCookie2);// 设置cookie的token
        	CookieSyncManager.getInstance().sync();
        	
        	Log.i(TAG,"- url " + result);
        	Log.i(TAG,"- oldCookie " + oldCookie);
            /*
            List<Cookie> cookies = AppUtil.cookies;
            if (cookies.isEmpty()) {
                Log.i(TAG, "NONE");
             } else {
                 for (int i = 0; i < cookies.size(); i++) {             
                   Log.i(TAG,"- domain " + cookies.get(i).getDomain());
                   Log.i(TAG,"- path " + cookies.get(i).getPath());
                   Log.i(TAG,"- value " + cookies.get(i).getValue());
                   Log.i(TAG,"- name " + cookies.get(i).getName());
                   Log.i(TAG,"- port " + cookies.get(i).getPorts());
                   Log.i(TAG,"- comment " + cookies.get(i).getComment());
                   Log.i(TAG,"- commenturl" + cookies.get(i).getCommentURL());
                   Log.i(TAG,"- all " + cookies.get(i).toString());
                 }
             }
            */
            //Log.i(TAG,"- all " + AppUtil.cookies.toString());
        	cookieManager.setCookie(result, oldCookie);// 指定要修改的cookies
        	CookieSyncManager.getInstance().sync();
        	webView.loadUrl(result);
        }

        String title = getIntent().getStringExtra("title");
        if (title != null) {
            textViewTitle.setText(title);
        }

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }

        // 事件监听
        findViewById(R.id.ib_browser_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // WebChromeClient
    public class MyChromeClient extends WebChromeClient {

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (mView != null) {
                callback.onCustomViewHidden();
                return;
            }
            frameLayout.removeView(webView);
            frameLayout.addView(view);
            mView = view;
            mCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            if (mView == null) {
                return;
            }
            frameLayout.removeView(mView);
            frameLayout.addView(webView);
            mCallback.onCustomViewHidden();
            mView = null;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return super.onConsoleMessage(consoleMessage);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mView == null) {
            super.onBackPressed();
        } else {
            chromeClient.onHideCustomView();
        }
    }

}
