package com.androidev.coding.module.tree;

import android.content.Intent;
import android.text.TextUtils;

import com.androidev.coding.model.Tree;
import com.androidev.coding.network.GitHub;

import java.util.Collections;
import java.util.Comparator;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.REPO;
import static com.androidev.coding.misc.Constant.SHA;
import static com.androidev.coding.misc.Constant.TITLE;

class TreePresenter {

    private final static Comparator<Tree.Node> NODE_COMPARATOR = (n1, n2) -> {
        if (!n1.type.equals(n2.type)) {
            return n2.type.compareTo(n1.type);
        } else {
            return n1.path.compareTo(n2.path);
        }
    };
    private TreeActivity mView;
    private String mSha;
    private String mOwner;
    private String mRepo;

    TreePresenter(TreeActivity view) {
        mView = view;
        Intent intent = mView.getIntent();
        mOwner = intent.getStringExtra(OWNER);
        mRepo = intent.getStringExtra(REPO);
        mSha = intent.getStringExtra(SHA);
        String title = intent.getStringExtra(TITLE);
        if (!TextUtils.isEmpty(title)) {
            mView.setTitle(title);
        }
    }

    void refresh() {
        GitHub.getApi().tree(mOwner, mRepo, mSha)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(data -> Collections.sort(data.tree, NODE_COMPARATOR))
                .subscribe(mView::setData, Throwable::printStackTrace);
    }

}
