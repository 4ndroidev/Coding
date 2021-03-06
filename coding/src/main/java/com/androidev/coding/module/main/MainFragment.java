package com.androidev.coding.module.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
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
import com.androidev.coding.model.RateLimit;
import com.androidev.coding.model.Repo;
import com.androidev.coding.module.auth.AuthActivity;
import com.androidev.coding.module.code.CodeActivity;
import com.androidev.coding.module.code.TreeActivity;
import com.androidev.coding.module.commit.CommitsActivity;
import com.androidev.coding.network.GitHub;
import com.bumptech.glide.Glide;

import static com.androidev.coding.misc.Constant.BRANCH;
import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.REPO;
import static com.androidev.coding.misc.Constant.SHA;
import static com.androidev.coding.misc.Constant.TITLE;
import static com.androidev.coding.misc.Constant.TYPE;
import static com.androidev.coding.misc.Constant.TYPE_README;

public class MainFragment extends Fragment implements View.OnClickListener {

    private final static int AUTHORIZE_REQUEST_CODE = 10000;

    private boolean isInflated;
    private View mEmptyView;
    private View mLoadingView;
    private View mLoadingAnim;
    private ViewGroup mContentView;
    private MainPresenter mPresenter;
    private String mOwner, mRepo, mBranch;
    private TextView mRateLimit;
    private GitHub.OnRateLimitChangedListener mOnRateLimitChangedListener;

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
        mOnRateLimitChangedListener = this::updateRateLimit;
        GitHub.getInstance().addOnRateLimitChangedListener(mOnRateLimitChangedListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = (ViewGroup) inflater.inflate(R.layout.coding_fragment_main, container, false);
        mContentView.setFitsSystemWindows(true);
        mContentView.findViewById(R.id.coding_btn_authorize).setOnClickListener(this);
        mContentView.findViewById(R.id.coding_btn_connect).setOnClickListener(this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        GitHub.getInstance().removeOnRateLimitChangedListener(mOnRateLimitChangedListener);
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
            mContentView.findViewById(R.id.coding_authorize).setOnClickListener(this);
            mRateLimit = (TextView) mContentView.findViewById(R.id.coding_limit);
        }
        mEmptyView.setVisibility(View.GONE);
        ImageView icon = (ImageView) mContentView.findViewById(R.id.coding_icon);
        TextView name = (TextView) mContentView.findViewById(R.id.coding_name);
        TextView owner = (TextView) mContentView.findViewById(R.id.coding_owner);
        TextView description = (TextView) mContentView.findViewById(R.id.coding_description);
        TextView star = (TextView) mContentView.findViewById(R.id.coding_star);
        Glide.with(this).load(repo.owner.avatar_url).transform(new RoundTransform(getContext(), 20)).into(icon);
        name.setText(repo.name);
        owner.setText(repo.owner.login);
        description.setText(repo.description);
        star.setText(String.valueOf(repo.stargazers_count));
        icon.setOnClickListener(this);
        updateRateLimit(GitHub.getInstance().getRateLimit());
        stopAnimation();
    }

    void setError(Throwable throwable) {
        mEmptyView.setVisibility(View.VISIBLE);
        stopAnimation();
        throwable.printStackTrace();
    }

    private void updateRateLimit(RateLimit rateLimit) {
        if (mRateLimit == null) return;
        if (rateLimit.limit < 0 || rateLimit.remaining < 0) {
            mRateLimit.setText(R.string.coding_rate_limit_tip);
            return;
        }
        Resources resources = getResources();
        Resources.Theme theme = getActivity().getTheme();
        mRateLimit.setText(resources.getString(R.string.coding_rate_limit_format, rateLimit.remaining, rateLimit.limit));
        int textColor;
        if (rateLimit.remaining <= 20) {
            textColor = ResourcesCompat.getColor(resources, R.color.colorWarning, theme);
        } else {
            textColor = ResourcesCompat.getColor(resources, android.R.color.primary_text_light, theme);
        }
        mRateLimit.setTextColor(textColor);
        View authorize = mContentView.findViewById(R.id.coding_authorize);
        if (rateLimit.limit >= RateLimit.MAX_LIMIT) {
            authorize.setVisibility(View.GONE);
        } else {
            authorize.setVisibility(View.VISIBLE);
        }
    }

    private void download() {
        Activity activity = getActivity();
        new AlertDialog.Builder(activity)
                .setMessage(R.string.coding_download_hint)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    GitHub.getInstance().download(activity, mOwner, mRepo, mBranch);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.coding_btn_connect == id) {
            mPresenter.load();
        } else if (R.id.coding_authorize == id || R.id.coding_btn_authorize == id) {
            Intent intent = new Intent();
            intent.setClass(getContext(), AuthActivity.class);
            startActivityForResult(intent, AUTHORIZE_REQUEST_CODE);
        } else if (R.id.coding_download == id) {
            download();
        } else if (R.id.coding_commit == id) {
            Intent intent = new Intent();
            intent.putExtra(OWNER, mOwner);
            intent.putExtra(REPO, mRepo);
            intent.setClass(getContext(), CommitsActivity.class);
            startActivity(intent);
        } else if (R.id.coding_tree == id) {
            Intent intent = new Intent();
            intent.putExtra(OWNER, mOwner);
            intent.putExtra(REPO, mRepo);
            intent.putExtra(SHA, mBranch);
            intent.putExtra(TITLE, mBranch);
            intent.setClass(getContext(), TreeActivity.class);
            startActivity(intent);
        } else if (R.id.coding_readme == id) {
            Intent intent = new Intent();
            intent.putExtra(OWNER, mOwner);
            intent.putExtra(REPO, mRepo);
            intent.putExtra(SHA, mBranch);
            intent.putExtra(TYPE, TYPE_README);
            intent.setClass(getContext(), CodeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AUTHORIZE_REQUEST_CODE == requestCode && Activity.RESULT_OK == resultCode) {
            mPresenter.load();
        }
    }
}
