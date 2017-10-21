package com.androidev.coding.network;


import com.androidev.coding.model.Blob;
import com.androidev.coding.model.Branch;
import com.androidev.coding.model.Commit;
import com.androidev.coding.model.Repo;
import com.androidev.coding.model.Tree;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

import static com.androidev.coding.misc.Constant.HEADER_ACCEPT;
import static com.androidev.coding.misc.Constant.MEDIA_TYPE_RAW;

public interface RestApi {

    String REPO_FORMAT = "repos/{owner}/{repo}";

    @GET(REPO_FORMAT)
    Observable<Repo> repo(@Path("owner") String owner, @Path("repo") String repo);

    @GET(REPO_FORMAT + "/commits")
    Observable<List<Commit>> commits(@Path("owner") String owner, @Path("repo") String repo, @QueryMap Map<String, Object> data);

    @GET(REPO_FORMAT + "/commits/{sha}")
    Observable<Commit> commit(@Path("owner") String owner, @Path("repo") String repo, @Path("sha") String sha);

    // 暂时用不到
    @GET(REPO_FORMAT + "/branches/{branch}")
    Observable<Branch> branch(@Path("owner") String owner, @Path("repo") String repo, @Path("branch") String branch);

    @GET(REPO_FORMAT + "/git/trees/{sha}")
    Observable<Tree> tree(@Path("owner") String owner, @Path("repo") String repo, @Path("sha") String sha);

    // 暂时用不到
    @GET(REPO_FORMAT + "/git/blobs/{sha}")
    Observable<Blob> blob(@Path("owner") String owner, @Path("repo") String repo, @Path("sha") String sha);

    @GET(REPO_FORMAT + "/git/blobs/{sha}")
    @Headers({HEADER_ACCEPT + ": " + MEDIA_TYPE_RAW})
    Observable<ResponseBody> raw(@Path("owner") String owner, @Path("repo") String repo, @Path("sha") String sha);
}
