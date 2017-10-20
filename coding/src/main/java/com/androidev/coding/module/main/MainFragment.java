package com.androidev.coding.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidev.coding.R;
import com.androidev.coding.misc.RoundTransform;
import com.androidev.coding.model.Repo;
import com.androidev.coding.module.commit.CommitActivity;
import com.androidev.coding.module.tree.TreeActivity;
import com.bumptech.glide.Glide;

import static com.androidev.coding.misc.Constant.BRANCH;
import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.REPO;
import static com.androidev.coding.misc.Constant.SHA;
import static com.androidev.coding.misc.Constant.TITLE;

public class MainFragment extends Fragment implements View.OnClickListener {

    private boolean isInflated;
    private View mEmptyView;
    private View mLoadingView;
    private View mLoadingAnim;
    private ViewGroup mContentView;
    private MainPresenter mPresenter;
    private String mOwner, mRepo, mBranch;

    public static MainFragment newInstance(Bundle arguments) {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter(this);
        Bundle arguments = getArguments();
        mOwner = arguments.getString(OWNER);
        mRepo = arguments.getString(REPO);
        mBranch = arguments.getString(BRANCH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = (ViewGroup) inflater.inflate(R.layout.coding_fragment_main, container, false);
        mContentView.setFitsSystemWindows(true);
        mContentView.findViewById(R.id.coding_connect).setOnClickListener(this);
        mEmptyView = mContentView.findViewById(R.id.coding_empty);
        mLoadingView = mContentView.findViewById(R.id.coding_loading);
        mLoadingAnim = mContentView.findViewById(R.id.coding_loading_anim);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.load();
    }

    void startAnimation() {
        mLoadingView.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.coding_loading_rotate_animation);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(3000);
        mLoadingAnim.startAnimation(animation);
    }

    void stopAnimation() {
        mLoadingView.postDelayed(() -> {
            mLoadingAnim.clearAnimation();
            mLoadingView.setVisibility(View.GONE);
        }, 500);
    }

    void setData(Repo repo) {
        if (!isInflated) {
            isInflated = true;
            ((ViewStub) mContentView.findViewById(R.id.coding_main)).inflate();
            mContentView.findViewById(R.id.coding_commit).setOnClickListener(this);
            mContentView.findViewById(R.id.coding_tree).setOnClickListener(this);
            mContentView.findViewById(R.id.coding_readme).setOnClickListener(this);
            mContentView.findViewById(R.id.coding_document).setOnClickListener(this);
            mContentView.findViewById(R.id.coding_download).setOnClickListener(this);
        }
        mEmptyView.setVisibility(View.GONE);
        ImageView icon = (ImageView) mContentView.findViewById(R.id.project_icon);
        TextView name = (TextView) mContentView.findViewById(R.id.project_name);
        TextView owner = (TextView) mContentView.findViewById(R.id.project_owner);
        TextView description = (TextView) mContentView.findViewById(R.id.project_description);
        Glide.with(this).load(repo.owner.avatar_url).transform(new RoundTransform(getContext(), 20)).into(icon);
        name.setText(repo.name);
        owner.setText(repo.owner.login);
        description.setText(repo.description);
        icon.setOnClickListener(this);
        stopAnimation();
    }

    void setError(Throwable throwable) {
        mEmptyView.setVisibility(View.VISIBLE);
        stopAnimation();
        throwable.printStackTrace();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.coding_connect == id || R.id.project_icon == id) {
            mPresenter.load();
        } else if (R.id.coding_commit == id) {
            Intent intent = new Intent();
            intent.putExtra(OWNER, mOwner);
            intent.putExtra(REPO, mRepo);
            intent.setClass(getContext(), CommitActivity.class);
            startActivity(intent);
        } else if (R.id.coding_tree == id) {
            Intent intent = new Intent();
            intent.putExtra(OWNER, mOwner);
            intent.putExtra(REPO, mRepo);
            intent.putExtra(SHA, mBranch);
            intent.putExtra(TITLE, mBranch);
            intent.setClass(getContext(), TreeActivity.class);
            startActivity(intent);
        }
    }
}
