package com.androidev.coding.module.code;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidev.coding.R;
import com.androidev.coding.module.base.BaseActivity;

import static com.androidev.coding.misc.Constant.BASE_URL;

public class CodeActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coding_activity_code);
        mWebView = (WebView) findViewById(R.id.coding_web_view);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(80);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                stopLoading();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
        new CodePresenter(this).load();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void startLoading() {
        findViewById(R.id.coding_loading).setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.coding_loading_rotate_animation);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(3000);
        findViewById(R.id.coding_loading_anim).startAnimation(animation);
    }

    void stopLoading() {
        findViewById(R.id.coding_loading_anim).clearAnimation();
        findViewById(R.id.coding_loading).setVisibility(View.GONE);
    }

    void setError(Throwable throwable) {
        stopLoading();
        throwable.printStackTrace();
    }

    void setData(String data) {
        mWebView.loadDataWithBaseURL(BASE_URL, data, "text/html", "UTF-8", null);
    }

}
