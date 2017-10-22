package com.androidev.coding.module.commit;

import android.content.Intent;
import android.text.TextUtils;

import com.androidev.coding.model.Patch;
import com.androidev.coding.network.GitHub;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.REPO;
import static com.androidev.coding.misc.Constant.SHA;

class CommitPresenter {

    private final static Pattern PATCH_INFO = Pattern.compile("@@ -(\\d+),\\d+ \\+(\\d+),\\d+ @@");

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

    String patch2json(String patchContent) {
        if (TextUtils.isEmpty(patchContent)) {
            return null;
        }
        Patch patch = new Patch();
        String[] lineContents = patchContent.split("\n");
        int index = 0, left = 0, right = 0;
        for (String lineContent : lineContents) {
            Matcher matcher = PATCH_INFO.matcher(lineContent);
            if (matcher.find()) {
                patch.lines.add(new Patch.Line(index++, 0, 0, " ", lineContent));
                left = Integer.parseInt(matcher.group(1));
                right = Integer.parseInt(matcher.group(2));
            } else {
                String prefix = " ";
                if (lineContent.startsWith("+")) {
                    prefix = "+";
                    left--;
                    lineContent = lineContent.replace('+', ' ');
                } else if (lineContent.startsWith("-")) {
                    prefix = "-";
                    right--;
                    lineContent = lineContent.replace('-', ' ');
                }
                patch.lines.add(new Patch.Line(index++, left++, right++, prefix, lineContent));
            }
        }
        try {
            return GitHub.getObjectMapper().writeValueAsString(patch);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
