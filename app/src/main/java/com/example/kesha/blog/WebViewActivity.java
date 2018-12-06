package com.example.kesha.blog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.kesha.blog.utils.Constants;

public class WebViewActivity extends AppCompatActivity {
    private final String TAG = WebViewActivity.class.getSimpleName();
    public static final String ARG_AUTH_URL = "ARG_AUTH_URL";
    public static final String RES_AUTH_VERIFIER = "RES_AUTH_VERIFIER";
    private WebView webView;
    private boolean isVerifierSaved = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        final Intent intent = getIntent();

        if (intent != null) {
            String authUrl = intent.getStringExtra(ARG_AUTH_URL);
            webView = findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportMultipleWindows(true); // This forces ChromeClient enabled.

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    getWindow().setTitle(title); //Set Activity tile to page title.
                }
            });

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (!isVerifierSaved) {
                        view.loadUrl(url);
                    }
                    Log.d(TAG, url);
                    if (url.contains("oauth_verifier=") && !isVerifierSaved) {
                        String[] token = url.split("=");
                        processResult(token[2]);
                        isVerifierSaved = true;
                        return true;
                    }
                    return false;
                }
            });

            webView.loadUrl(authUrl); // specify download page
        }else {
            TextView errText = new TextView(WebViewActivity.this);
            errText.setText(R.string.textErrorOpenWebView);
        }

    }
            //send the verified key back
    private void processResult(final String oauthVerifier) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(Constants.logOutURL);
                Intent intent = new Intent();
                intent.putExtra(RES_AUTH_VERIFIER, oauthVerifier);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_CANCELED);
    }
}
