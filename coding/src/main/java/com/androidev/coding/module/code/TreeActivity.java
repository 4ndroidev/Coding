package com.androidev.coding.module.code;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.androidev.coding.R;
import com.androidev.coding.model.Tree;
import com.androidev.coding.module.base.BaseActivity;
import com.androidev.coding.module.code.adapter.TreeAdapter;
import com.androidev.coding.module.image.ImageActivity;
import com.androidev.coding.widget.SwipeBackLayout;
import com.androidev.refreshlayout.RefreshLayout;

import static com.androidev.coding.misc.Constant.BLOB;
import static com.androidev.coding.misc.Constant.PATH;
import static com.androidev.coding.misc.Constant.SHA;
import static com.androidev.coding.misc.Constant.TREE;
import static com.androidev.coding.misc.Constant.TYPE;
import static com.androidev.coding.misc.Constant.TYPE_CODE;
import static com.androidev.coding.misc.Misc.isImage;

public class TreeActivity extends BaseActivity {

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
                if (isImage(data.path)) {
                    intent.setClass(this, ImageActivity.class);
                } else {
                    intent.putExtra(TYPE, TYPE_CODE);
                    intent.setClass(this, CodeActivity.class);
                }
                startActivity(intent);
            }
        });
        mRefreshLayout = (RefreshLayout) findViewById(R.id.coding_refresh_layout);
        mRefreshLayout.setOnRefreshListener(presenter::refresh);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.coding_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        presenter.refresh();
        showLoading();  //just one time
    }

    void setData(Tree data) {
        dismissLoading();
        mRefreshLayout.setRefreshing(false);
        mAdapter.setData(data.tree);
    }

    void setError(Throwable throwable) {
        dismissLoading();
        mRefreshLayout.setRefreshing(false);
        throwable.printStackTrace();
    }

}
