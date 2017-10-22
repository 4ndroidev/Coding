package com.androidev.coding.network.interceptor;


import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthorizeInterceptor implements Interceptor {

    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (TextUtils.isEmpty(token)) {
            return chain.proceed(chain.request());
        }
        Headers.Builder headersBuilder = chain.request().headers().newBuilder();
        headersBuilder.add("Authorization", "token " + token);
        return chain.proceed(chain.request().newBuilder().headers(headersBuilder.build()).build());
    }
}