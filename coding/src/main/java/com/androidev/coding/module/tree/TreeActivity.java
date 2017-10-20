package com.androidev.coding.module.tree;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.androidev.coding.R;
import com.androidev.coding.model.Tree;
import com.androidev.coding.module.code.CodeActivity;
import com.androidev.coding.module.tree.adapter.TreeAdapter;
import com.androidev.coding.widget.SwipeBackLayout;
import com.androidev.refreshlayout.RefreshLayout;

import static com.androidev.coding.misc.Constant.BLOB;
import static com.androidev.coding.misc.Constant.PATH;
import static com.androidev.coding.misc.Constant.SHA;
import static com.androidev.coding.misc.Constant.TREE;

public class TreeActivity extends AppCompatActivity {

    private RefreshLayout mRefreshLayout;
    private TreeAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackLayout.attachTo(this);
        setContentView(R.layout.coding_activity_tree);
        TreePresenter presenter = new TreePresenter(this);
        mAdapter = new TreeAdapter();
        mAdapter.setOnItemClickListener((view, position, data) -> {
            Intent intent = getIntent();
            intent.putExtra(SHA, data.sha);
            intent.putExtra(PATH, data.path);
            if (TREE.equals(data.type)) {
                intent.setClass(this, TreeActivity.class);
                startActivity(intent);
            } else if (BLOB.equals(data.type)) {
                intent.setClass(this, CodeActivity.class);
                startActivity(intent);
            }
        });
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(presenter::refresh);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        presenter.refresh();
    }

    void setData(Tree data) {
        mRefreshLayout.setRefreshing(false);
        mAdapter.setData(data.tree);
    }

    void setError(Throwable throwable) {
        mRefreshLayout.setRefreshing(false);
        throwable.printStackTrace();
    }

}
