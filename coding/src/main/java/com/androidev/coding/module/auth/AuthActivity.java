package com.androidev.coding.module.auth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.androidev.coding.R;
import com.androidev.coding.module.base.BaseActivity;

import static com.androidev.coding.misc.Constant.APP;
import static com.androidev.coding.misc.Constant.KEY_AUTHORIZE_CODE;
import static com.androidev.coding.misc.Constant.REDIRECT_URI;

public class AuthActivity extends BaseActivity {

    private WebView mWebView;
    private AuthPresenter mPresenter;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coding_activity_auth);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mWebView = (WebView) findViewById(R.id.coding_web_view);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUserAgentString(APP);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(REDIRECT_URI)) {
                    mPresenter.code4token(Uri.parse(url).getQueryParameter(KEY_AUTHORIZE_CODE));
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoading();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!url.startsWith(REDIRECT_URI)) {
                    dismissLoading();
                }
            }


        });
        mPresenter = new AuthPresenter(this);
        mPresenter.startAuthorize();
    }

    void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    void onResult(boolean success) {
        mWebView.post(() -> {
            dismissLoading();
            int message = success ? R.string.coding_authorize_success : R.string.coding_authorize_failure;
            Toast.makeText(AuthActivity.this, message, Toast.LENGTH_SHORT).show();
            setResult(success ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
            finish();
        });
    }

}
