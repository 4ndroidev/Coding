package com.androidev.coding.module.code;

import android.content.Intent;

import com.androidev.coding.R;
import com.androidev.coding.model.Tree;
import com.androidev.coding.network.GitHub;
import com.androidev.coding.network.RestApi;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.PATCH;
import static com.androidev.coding.misc.Constant.PATH;
import static com.androidev.coding.misc.Constant.README_MD_LOWERCASE;
import static com.androidev.coding.misc.Constant.REPO;
import static com.androidev.coding.misc.Constant.SHA;
import static com.androidev.coding.misc.Constant.TYPE;
import static com.androidev.coding.misc.Constant.TYPE_CODE;
import static com.androidev.coding.misc.Constant.TYPE_DIFF;
import static com.androidev.coding.misc.Constant.TYPE_README;

class CodePresenter {

    private CodeActivity mView;
    private String mPath;
    private String mOwner;
    private String mRepo;
    private String mSha;
    private int mType;

    @SuppressWarnings("all")
    CodePresenter(CodeActivity view) {
        mView = view;
        Intent intent = mView.getIntent();
        mOwner = intent.getStringExtra(OWNER);
        mRepo = intent.getStringExtra(REPO);
        mPath = intent.getStringExtra(PATH);
        mSha = intent.getStringExtra(SHA);
        mType = intent.getIntExtra(TYPE, TYPE_CODE);
        mView.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (TYPE_README == mType) {
            mView.setTitle(R.string.coding_readme);
            mPath = README_MD_LOWERCASE;
        } else {
            mView.setTitle(mPath.substring(mPath.lastIndexOf("/") + 1));
        }
    }

    void load() {
        mView.startLoading();
        RestApi api = GitHub.getApi();
        Observable<ResponseBody> requestRaw;
        if (TYPE_README == mType) {
            requestRaw = api.tree(mOwner, mRepo, mSha).switchMap(tree -> api.raw(mOwner, mRepo, tree4readme(tree)));
        } else if (TYPE_DIFF == mType) {
            requestRaw = Observable.create(e -> {
                e.onNext(ResponseBody.create(null, mView.getIntent().getStringExtra(PATCH)));
                e.onComplete();
            });
        } else {
            requestRaw = api.raw(mOwner, mRepo, mSha);
        }
        Observable.zip(readTemplate(), requestRaw, this::applyTemplate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::setData, mView::setError);
    }

    private String tree4readme(Tree data) {
        for (Tree.Node node : data.tree) {
            if (node.path.toLowerCase().equals(README_MD_LOWERCASE)) {
                return node.sha;
            }
        }
        throw new IllegalStateException("can not find readme in the repo.");
    }

    private Observable<String> readTemplate() {
        return Observable.create(e -> {
            boolean isMarkdown = mType == TYPE_README || mPath.endsWith(".md");
            String path = isMarkdown ? "coding/markdown.html" :
                    TYPE_DIFF == mType ? "coding/diff.html" : "coding/code.html";
            BufferedSource source = Okio.buffer(Okio.source(mView.getAssets().open(path)));
            String template = new String(source.readByteArray());
            source.close();
            e.onNext(template);
            e.onComplete();
        });
    }

    private String applyTemplate(String template, ResponseBody body) throws IOException {
        String content = body.string();
        boolean isMarkdown = mType == TYPE_README || mPath.endsWith(".md");
        if (isMarkdown) {
            content = content.replace("\n", "\\n").replace("\"", "\\\"").replace("'", "\\'");
        } else {
            content = content.replace("\u2028", "").replace("\u2029", "");
            if (TYPE_CODE == mType) {
                content = content.replace("<", "&lt;").replace(">", "&gt;");
            }
        }
        return template.replace("${content_placeholder}", content)
                .replace("${lang_placeholder}", mPath.substring(mPath.lastIndexOf(".") + 1));
    }
}
