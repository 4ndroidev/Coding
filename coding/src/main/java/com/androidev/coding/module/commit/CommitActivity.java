package com.androidev.coding.module.commit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.androidev.coding.R;
import com.androidev.coding.model.Commit;
import com.androidev.coding.module.base.BaseActivity;
import com.androidev.coding.module.code.CodeActivity;
import com.androidev.coding.module.commit.adapter.CommitAdapter;
import com.androidev.coding.module.image.ImageActivity;
import com.androidev.coding.widget.SwipeBackLayout;
import com.androidev.refreshlayout.RefreshLayout;

import static com.androidev.coding.misc.Constant.REMOVED;
import static com.androidev.coding.misc.Constant.PATCH;
import static com.androidev.coding.misc.Constant.PATH;
import static com.androidev.coding.misc.Constant.SHA;
import static com.androidev.coding.misc.Constant.TYPE;
import static com.androidev.coding.misc.Constant.TYPE_DIFF;
import static com.androidev.coding.misc.Misc.isImage;

public class CommitActivity extends BaseActivity {

    private RefreshLayout mRefreshLayout;
    private CommitAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackLayout.attachTo(this);
        setContentView(R.layout.coding_activity_commit);
        CommitPresenter presenter = new CommitPresenter(this);
        mAdapter = new CommitAdapter();
        mAdapter.setOnItemClickListener((v, position, data) -> {
            String path = data.filename;
            if (isImage(path)) {
                if (!REMOVED.equals(data.status)) {
                    Intent intent = getIntent();
                    intent.setClass(this, ImageActivity.class);
                    intent.putExtra(SHA, data.sha);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.coding_image_deleted, Toast.LENGTH_SHORT).show();
                }
            } else {
                String patch = presenter.patch2json(data.patch);
                if (patch != null) {
                    Intent intent = getIntent();
                    intent.setClass(this, CodeActivity.class);
                    intent.putExtra(TYPE, TYPE_DIFF);
                    intent.putExtra(PATCH, patch);
                    intent.putExtra(PATH, data.filename);
                    startActivity(intent);
                }
            }
        });
        mRefreshLayout = (RefreshLayout) findViewById(R.id.coding_refresh_layout);
        mRefreshLayout.setOnRefreshListener(presenter::refresh);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.coding_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        presenter.refresh();
    }

    void setData(Commit commit) {
        mRefreshLayout.setRefreshing(false);
        mAdapter.setData(commit);
    }

    void setError(Throwable throwable) {
        mRefreshLayout.setRefreshing(false);
        throwable.printStackTrace();
    }

}
