package com.androidev.coding.network;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.androidev.coding.model.RateLimit;
import com.androidev.coding.network.interceptor.AuthorizeInterceptor;
import com.androidev.coding.network.interceptor.RateLimitInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Locale;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.androidev.coding.misc.Constant.BASE_URL;
import static com.androidev.coding.misc.Constant.DOWNLOAD_DESTINATION;

public class GitHub {

    private final static String TAG = "GitHub";
    private final static String DOWNLOAD_URL_FORMAT = "https://github.com/%s/%s/archive/%s.zip";

    private final static class GitHubHolder {
        private final static GitHub instance = new GitHub();
    }

    private RestApi restApi;
    private ObjectMapper objectMapper;
    private OkHttpClient okHttpClient;
    private AuthorizeInterceptor authorizeInterceptor;
    private RateLimit rateLimit;

    private GitHub() {
        authorizeInterceptor = new AuthorizeInterceptor();
        File cachePath = new File(Environment.getExternalStorageDirectory(), "coding");
        okHttpClient = new OkHttpClient.Builder()
                .cache(new Cache(cachePath, 30 * 1024 * 1024/* 30MB */))
                .addInterceptor(authorizeInterceptor)
                .addNetworkInterceptor(new RateLimitInterceptor())
                .build();
        objectMapper = new ObjectMapper();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callFactory(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
        restApi = retrofit.create(RestApi.class);
        rateLimit = new RateLimit();
    }

    public static GitHub getInstance() {
        return GitHubHolder.instance;
    }

    public static RestApi getApi() {
        return GitHubHolder.instance.restApi;
    }

    public static OkHttpClient getHttpClient() {
        return GitHubHolder.instance.okHttpClient;
    }

    public static ObjectMapper getObjectMapper() {
        return GitHubHolder.instance.objectMapper;
    }

    public void setRateLimit(RateLimit limit) {
        this.rateLimit = limit;
    }

    public RateLimit getRateLimit() {
        return this.rateLimit;
    }

    public void authorize(String token) {
        authorizeInterceptor.setToken(token);
    }

    public void download(Context context, String owner, String repo, String branch) {
        String name = repo + "-" + branch + ".zip";
        File destination = new File(Environment.getExternalStoragePublicDirectory(DOWNLOAD_DESTINATION), name);
        if (destination.exists() && !destination.delete()) {
            Log.e(TAG, "can not delete file: " + destination.getPath());
            return;
        }
        String url = String.format(Locale.US, DOWNLOAD_URL_FORMAT, owner, repo, branch);
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(destination.getName());
        request.setDestinationInExternalPublicDir(DOWNLOAD_DESTINATION, destination.getName());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        downloadManager.enqueue(request);
    }

    public String url4image(String owner, String repo, String sha) {
        String format = BASE_URL + "/repos/%s/%s/git/blobs/%s";
        return String.format(format, owner, repo, sha);
    }
}
