package com.example.kesha.blog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MyWebView extends WebView {


        public MyWebView(Context context) {
            super(context);
            initDefaultSetting();
        }

        public MyWebView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initDefaultSetting();
        }

        public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initDefaultSetting();
        }

        @SuppressLint("NewApi")
        public MyWebView(Context context, AttributeSet attrs, int defStyleAttr,
                         int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            initDefaultSetting();
        }


        @SuppressLint("SetJavaScriptEnabled")
        private void initDefaultSetting() {
            WebSettings webSettings = this.getSettings();
            webSettings.setJavaScriptEnabled(true);
            setWebChromeClient(new WebChromeClient());
            setWebViewClient(new MyWebViewClient());
        }

        /**
         * Load Web View with url
         */
        public void load(String url) {
            this.loadUrl(url);
        }


}
