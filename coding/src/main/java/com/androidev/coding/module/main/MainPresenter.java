package com.androidev.coding.module.main;


import android.os.Bundle;

import com.androidev.coding.network.GitHub;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.REPO;

class MainPresenter {

    private MainFragment mView;
    private String mOwner;
    private String mRepo;

    MainPresenter(MainFragment view) {
        mView = view;
        Bundle arguments = mView.getArguments();
        mOwner = arguments.getString(OWNER);
        mRepo = arguments.getString(REPO);
    }

    void load() {
        mView.startAnimation();
        GitHub.getApi().repo(mOwner, mRepo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::setData, mView::setEmpty);
    }

}
