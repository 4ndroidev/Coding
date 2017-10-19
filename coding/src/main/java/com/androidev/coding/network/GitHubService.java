package com.androidev.coding.network;

import com.androidev.coding.model.Commit;
import com.androidev.coding.model.Repo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public class GitHubService {

    private final static String BASE_URL = "https://api.github.com";
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CHINA);

    private final static class GitHubServiceHolder {
        private final static GitHubService instance = new GitHubService();
    }

    private Service service;

    private GitHubService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        service = retrofit.create(Service.class);
    }

    public static Service get() {
        return GitHubServiceHolder.instance.service;
    }

    public static Date time2date(String timestamp) {
        try {
            return DATE_FORMAT.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public interface Service {

        @GET("repos/{owner}/{repo}")
        Observable<Repo> repo(@Path("owner") String owner, @Path("repo") String repo);

        @GET("repos/{owner}/{repo}/commits")
        Observable<List<Commit>> commits(@Path("owner") String owner, @Path("repo") String repo, @QueryMap Map<String, Object> data);
    }

}
