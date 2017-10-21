package com.androidev.coding.module.commit;

import android.content.Intent;

import com.androidev.coding.network.GitHub;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.REPO;
import static com.androidev.coding.misc.Constant.SHA;

class CommitPresenter {

    private String mOwner;
    private String mRepo;
    private String mSha;
    private CommitActivity mView;

    CommitPresenter(CommitActivity view) {
        mView = view;
        Intent intent = mView.getIntent();
        mOwner = intent.getStringExtra(OWNER);
        mRepo = intent.getStringExtra(REPO);
        mSha = intent.getStringExtra(SHA);
        mView.setTitle(mSha.substring(0, 7));
    }

    void refresh() {
        GitHub.getApi().commit(mOwner, mRepo, mSha)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::setData, mView::setError);
    }

}
