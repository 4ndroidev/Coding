package com.androidev.coding.module.commit;

import android.content.Intent;

import com.androidev.coding.model.Commit;
import com.androidev.coding.network.GitHubService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.REPO;

class CommitPresenter {

    private String mSha;
    private String mOwner;
    private String mRepo;
    private CommitActivity mView;

    CommitPresenter(CommitActivity view) {
        mView = view;
        Intent intent = mView.getIntent();
        mOwner = intent.getStringExtra(OWNER);
        mRepo = intent.getStringExtra(REPO);
    }

    void refresh() {
        Map<String, Object> data = new HashMap<>();
        data.put("page", 1);
        data.put("per_page", 20);
        GitHubService.get().commits(mOwner, mRepo, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext(commits -> {
                    if (commits == null || commits.size() == 0) return;
                    List<Commit.Parents> parents = commits.get(commits.size() - 1).parents;
                    if (parents == null || parents.size() == 0) return;
                    mSha = parents.get(0).sha;
                })
                .subscribe(mView::setData, Throwable::printStackTrace);
    }

    void load() {
        mView.setLoading(true);
        Map<String, Object> data = new HashMap<>();
        data.put("page", 1);
        data.put("per_page", 20);
        data.put("sha", mSha);
        GitHubService.get().commits(mOwner, mRepo, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext(commits -> {
                    if (commits == null || commits.size() == 0) return;
                    List<Commit.Parents> parents = commits.get(commits.size() - 1).parents;
                    if (parents == null || parents.size() == 0) return;
                    mSha = parents.get(0).sha;
                })
                .subscribe(mView::appendData, Throwable::printStackTrace);
    }

}
