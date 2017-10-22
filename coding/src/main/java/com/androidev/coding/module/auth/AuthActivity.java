package com.androidev.coding.module.auth;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.androidev.coding.R;
import com.androidev.coding.model.Auth;
import com.androidev.coding.module.base.BaseActivity;
import com.androidev.coding.network.GitHub;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.androidev.coding.misc.Constant.ACCESS_TOKEN_URL;
import static com.androidev.coding.misc.Constant.APP;
import static com.androidev.coding.misc.Constant.AUTHORIZE_URL;
import static com.androidev.coding.misc.Constant.CLIENT_ID;
import static com.androidev.coding.misc.Constant.CLIENT_SECRET;
import static com.androidev.coding.misc.Constant.KEY_AUTHORIZE_CODE;
import static com.androidev.coding.misc.Constant.KEY_CLIENT_ID;
import static com.androidev.coding.misc.Constant.KEY_CLIENT_SECRET;
import static com.androidev.coding.misc.Constant.KEY_TOKEN;
import static com.androidev.coding.misc.Constant.REDIRECT_URI;

public class AuthActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.coding_activity_auth);
        mWebView = (WebView) findViewById(R.id.coding_web_view);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                stopLoading();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(REDIRECT_URI)) {
                    finishAuthorize(Uri.parse(url).getQueryParameter(KEY_AUTHORIZE_CODE));
                } else {
                    mWebView.loadUrl(url);
                }
                return true;
            }
        });
        startAuthorize();
    }

    private void startLoading() {
        findViewById(R.id.coding_loading).setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.coding_loading_rotate_animation);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(3000);
        findViewById(R.id.coding_loading_anim).startAnimation(animation);
    }

    private void stopLoading() {
        findViewById(R.id.coding_loading_anim).clearAnimation();
        findViewById(R.id.coding_loading).setVisibility(View.GONE);
    }

    private void startAuthorize() {
        startLoading();
        String format = "%s?client_id=%s&redirect_uri=%s";
        String url = String.format(format, AUTHORIZE_URL, CLIENT_ID, REDIRECT_URI);
        mWebView.loadUrl(url);
    }

    private void finishAuthorize(String code) {
        OkHttpClient okHttpClient = GitHub.getHttpClient();
        RequestBody body = new FormBody.Builder()
                .add(KEY_CLIENT_ID, CLIENT_ID)
                .add(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .add(KEY_AUTHORIZE_CODE, code)
                .build();
        Request request = new Request.Builder()
                .url(ACCESS_TOKEN_URL)
                .post(body)
                .header("Accept", "application/json")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onResult(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    onResult(null);
                    return;
                }
                Auth auth = GitHub.getObjectMapper().readValue(responseBody.bytes(), Auth.class);
                onResult(auth.access_token);
            }
        });
    }

    private void onResult(String token) {
        getSharedPreferences(APP, Context.MODE_PRIVATE).edit().putString(KEY_TOKEN, token).apply();
        GitHub.getInstance().authorize(token);
        mWebView.post(() -> {
            int message = TextUtils.isEmpty(token) ? R.string.coding_authorize_failure : R.string.coding_authorize_success;
            Toast.makeText(AuthActivity.this, message, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

}
