package com.androidev.coding.module.commit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.androidev.coding.R;
import com.androidev.coding.model.Commit;
import com.androidev.coding.module.base.BaseActivity;
import com.androidev.coding.module.commit.adapter.CommitsAdapter;
import com.androidev.coding.widget.SwipeBackLayout;
import com.androidev.refreshlayout.RefreshLayout;

import java.util.List;

import static com.androidev.coding.misc.Constant.SHA;

public class CommitsActivity extends BaseActivity {

    private RefreshLayout mRefreshLayout;
    private CommitsAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackLayout.attachTo(this);
        setContentView(R.layout.coding_activity_commits);
        CommitsPresenter presenter = new CommitsPresenter(this);
        mAdapter = new CommitsAdapter();
        mAdapter.setOnLoadListener(presenter::load);
        mAdapter.setOnItemClickListener((v, position, data) -> {
            Intent intent = getIntent();
            intent.putExtra(SHA, data.sha);
            intent.setClass(this, CommitActivity.class);
            startActivity(intent);
        });
        mRefreshLayout = (RefreshLayout) findViewById(R.id.coding_refresh_layout);
        mRefreshLayout.setOnRefreshListener(presenter::refresh);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.coding_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
        presenter.refresh();
    }

    void setData(List<Commit> commits) {
        mRefreshLayout.setRefreshing(false);
        mAdapter.setData(commits);
    }

    void setError(Throwable throwable){
        mRefreshLayout.setRefreshing(false);
        throwable.printStackTrace();
    }

    void appendData(List<Commit> commits) {
        setLoading(false);
        mAdapter.appendData(commits);
    }

    void setLoading(boolean loading) {
        mAdapter.setLoading(loading);
    }
}
