package com.androidev.coding.network;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.androidev.coding.misc.Constant.DOWNLOAD_DESTINATION;

public class GitHub {

    public final static String BASE_URL = "https://api.github.com";

    private final static String TAG = "GitHub";
    private final static String DOWNLOAD_URL_FORMAT = "https://github.com/%s/%s/archive/%s.zip";
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private final static class GitHubHolder {
        private final static GitHub instance = new GitHub();
    }

    private RestApi restApi;

    private GitHub() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        restApi = retrofit.create(RestApi.class);
    }

    public static GitHub getInstance() {
        return GitHubHolder.instance;
    }

    public static RestApi getApi() {
        return GitHubHolder.instance.restApi;
    }

    public static Date time2date(String timestamp) {
        return DATE_FORMAT.parse(timestamp, new ParsePosition(0));
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
