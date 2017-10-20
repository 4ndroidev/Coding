package com.androidev.coding.module.code;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidev.coding.R;
import com.androidev.coding.network.GitHub;

public class CodeActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coding_activity_code);
        mWebView = (WebView) findViewById(R.id.web_view);
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

    public void loadHtml(String html) {
        stopLoading();
        mWebView.loadDataWithBaseURL(GitHub.BASE_URL, html, "text/html", "UTF-8", null);
    }

}
