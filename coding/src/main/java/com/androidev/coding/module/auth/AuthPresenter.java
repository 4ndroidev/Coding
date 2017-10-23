package com.androidev.coding.module.auth;

import android.content.Context;
import android.text.TextUtils;

import com.androidev.coding.model.Auth;
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

class AuthPresenter {

    private AuthActivity mView;

    AuthPresenter(AuthActivity view) {
        mView = view;
    }

    void startAuthorize() {
        String format = "%s?client_id=%s&redirect_uri=%s";
        String url = String.format(format, AUTHORIZE_URL, CLIENT_ID, REDIRECT_URI);
        mView.loadUrl(url);
    }

    void code4token(String code) {
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
        GitHub.getInstance().authorize(token);
        mView.getSharedPreferences(APP, Context.MODE_PRIVATE).edit().putString(KEY_TOKEN, token).apply();
        mView.onResult(!TextUtils.isEmpty(token));
    }

}
