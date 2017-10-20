package com.androidev.coding.module.code;

import android.content.Intent;
import android.util.Base64;

import com.androidev.coding.model.Blob;
import com.androidev.coding.network.GitHub;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okio.BufferedSource;
import okio.Okio;

import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.PATH;
import static com.androidev.coding.misc.Constant.REPO;
import static com.androidev.coding.misc.Constant.SHA;

class CodePresenter {

    private CodeActivity mView;
    private String mPath;
    private String mOwner;
    private String mRepo;
    private String mSha;

    @SuppressWarnings("all")
    CodePresenter(CodeActivity view) {
        mView = view;
        Intent intent = mView.getIntent();
        mOwner = intent.getStringExtra(OWNER);
        mRepo = intent.getStringExtra(REPO);
        mPath = intent.getStringExtra(PATH);
        mSha = intent.getStringExtra(SHA);
        mView.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mView.setTitle(mPath);
    }

    void load() {
        mView.startLoading();
        Observable.zip(readTemplate(), GitHub.getApi().blob(mOwner, mRepo, mSha), this::applyTemplate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::setData, mView::setError);

    }

    private Observable<String> readTemplate() {
        return Observable.create(e -> {
            String path = mPath.endsWith(".md") ? "coding/markdown.html" : "coding/code.html";
            BufferedSource source = Okio.buffer(Okio.source(mView.getAssets().open(path)));
            String template = new String(source.readByteArray());
            source.close();
            e.onNext(template);
            e.onComplete();
        });
    }

    private String applyTemplate(String template, Blob blob) {
        String content = new String(Base64.decode(blob.content, Base64.DEFAULT));
        boolean isMarkdown = mPath.endsWith(".md");
        if (isMarkdown) {
            content = content.replace("\n", "\\n").replace("\"", "\\\"").replace("'", "\\'");
        } else {
            content = content.replace("<", "&lt;").replace(">", "&gt;").replace("\u2028", "").replace("\u2029", "");
        }
        return template.replace("${content_placeholder}", content)
                .replace("${lang_placeholder}", mPath.substring(mPath.lastIndexOf(".") + 1));
    }
}
