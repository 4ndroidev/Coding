package com.androidev.coding.module.commit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.androidev.coding.R;
import com.androidev.coding.model.Commit;
import com.androidev.coding.module.commit.adapter.CommitAdapter;
import com.androidev.coding.widget.SwipeBackLayout;
import com.androidev.refreshlayout.RefreshLayout;

import java.util.List;

public class CommitActivity extends AppCompatActivity {

    private RefreshLayout mRefreshLayout;
    private CommitAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackLayout.attachTo(this);
        setContentView(R.layout.coding_activity_commit);
        CommitPresenter presenter = new CommitPresenter(this);
        mAdapter = new CommitAdapter();
        mAdapter.setOnLoadListener(presenter::load);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(presenter::refresh);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
        presenter.refresh();
    }

    void setData(List<Commit> commits) {
        mRefreshLayout.setRefreshing(false);
        mAdapter.setData(commits);
    }

    void appendData(List<Commit> commits) {
        setLoading(false);
        mAdapter.appendData(commits);
    }

    void setLoading(boolean loading) {
        mAdapter.setLoading(loading);
    }
}
