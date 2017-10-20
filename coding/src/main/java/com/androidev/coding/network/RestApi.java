package com.androidev.coding.network;


import com.androidev.coding.model.Branch;
import com.androidev.coding.model.Commit;
import com.androidev.coding.model.Repo;
import com.androidev.coding.model.Tree;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface RestApi {

    String REPO_FORMAT = "repos/{owner}/{repo}";

    @GET(REPO_FORMAT)
    Observable<Repo> repo(@Path("owner") String owner, @Path("repo") String repo);

    @GET(REPO_FORMAT + "/commits")
    Observable<List<Commit>> commits(@Path("owner") String owner, @Path("repo") String repo, @QueryMap Map<String, Object> data);

    @GET(REPO_FORMAT + "/branches/{branch}")
    Observable<Branch> branch(@Path("owner") String owner, @Path("repo") String repo, @Path("branch") String branch);

    @GET(REPO_FORMAT + "/git/trees/{sha}")
    Observable<Tree> tree(@Path("owner") String owner, @Path("repo") String repo, @Path("sha") String sha);
}
