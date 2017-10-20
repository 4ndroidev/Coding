package com.androidev.coding.network;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GitHub {

    public final static String BASE_URL = "https://api.github.com";

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


}
