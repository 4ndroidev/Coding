package com.androidev.coding;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.androidev.coding.module.main.MainFragment;

import static com.androidev.coding.misc.Constant.BRANCH;
import static com.androidev.coding.misc.Constant.DEFAULT_BRANCH;
import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.REPO;

public class Coding {

    private final static String TAG = "Coding";

    private String owner;
    private String repo;
    private String branch;
    private FragmentActivity activity;

    public Coding(FragmentActivity activity) {
        this.activity = activity;
    }

    public Coding owner(String owner) {
        this.owner = owner;
        return this;
    }

    public Coding repo(String repo) {
        this.repo = repo;
        return this;
    }

    public Coding branch(String branch) {
        this.branch = branch;
        return this;
    }

    public void attach(int layoutId) {
        if (TextUtils.isEmpty(owner) || TextUtils.isEmpty(repo)) {
            Log.e(TAG, "Either owner or repo can not be empty!");
            return;
        }
        Bundle arguments = new Bundle();
        arguments.putString(OWNER, owner);
        arguments.putString(REPO, repo);
        arguments.putString(BRANCH, TextUtils.isEmpty(branch) ? DEFAULT_BRANCH : branch);
        activity.getSupportFragmentManager().beginTransaction().replace(layoutId, MainFragment.newInstance(arguments)).commit();
    }
}
