package com.androidev.coding.network.interceptor;


import android.text.TextUtils;

import com.androidev.coding.model.RateLimit;
import com.androidev.coding.network.GitHub;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Response;

public class RateLimitInterceptor implements Interceptor {

    private final static String KEY_RATE_LIMIT = "X-RateLimit-Limit";
    private final static String KEY_RATE_REMAINING = "X-RateLimit-Remaining";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        Headers headers = response.headers();
        String limit = headers.get(KEY_RATE_LIMIT);
        String remaining = headers.get(KEY_RATE_REMAINING);
        if (!TextUtils.isEmpty(limit) && !TextUtils.isEmpty(remaining)) {
            int limitValue = Integer.parseInt(limit);
            int remainingValue = Integer.parseInt(remaining);
            GitHub.getInstance().setRateLimit(new RateLimit(limitValue, remainingValue));
        } else {
            GitHub.getInstance().setRateLimit(new RateLimit());
        }
        return response;
    }
}
