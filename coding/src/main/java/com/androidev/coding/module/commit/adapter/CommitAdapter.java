package com.androidev.coding.module.commit.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidev.coding.R;
import com.androidev.coding.misc.CircleTransform;
import com.androidev.coding.model.Commit;
import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.androidev.coding.network.GitHubService.time2date;

public class CommitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_ITEM = 1;
    private final static int TYPE_LOADING = 2;

    private boolean hasMore;
    private boolean isLoading;
    private List<Commit> mCommits;
    private OnLoadListener mLoadListener;

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (!hasMore || mLoadListener == null) return;
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int last = layoutManager.findLastVisibleItemPosition();
            int childCount = layoutManager.getChildCount();
            int itemCount = layoutManager.getItemCount();
            if (newState == SCROLL_STATE_IDLE && childCount > 0 && last >= itemCount - 1) {
                mLoadListener.onLoad();
            }
        }
    };

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(mScrollListener);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.removeOnScrollListener(mScrollListener);
    }

    private View createItemView(int layoutId, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutId, parent, false);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new CommitViewHolder(createItemView(R.layout.coding_layout_dynamic_item, parent));
            case TYPE_LOADING:
                return new LoadingViewHolder(createItemView(R.layout.coding_layout_loading_item, parent));
        }
        throw new IllegalStateException("Illegal viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_ITEM:
                ((CommitViewHolder) holder).setData(mCommits.get(position));
                break;
            case TYPE_LOADING:
                ((LoadingViewHolder) holder).setLoading(isLoading);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCommits == null ? 0 : mCommits.size() + (hasMore ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getItemCount() - (hasMore ? 1 : 0))
            return TYPE_ITEM;
        else
            return TYPE_LOADING;
    }

    public void setData(List<Commit> commits) {
        mCommits = commits;
        if (mCommits != null && mCommits.size() > 0) {
            List parents = mCommits.get(mCommits.size() - 1).parents;
            hasMore = parents != null && parents.size() > 0;
        }
        notifyDataSetChanged();
    }

    public void appendData(List<Commit> commits) {
        if (commits == null || commits.size() == 0)
            return;
        mCommits.addAll(commits);
        int size = commits.size();
        mCommits.addAll(commits);
        notifyItemRangeInserted(mCommits.size() - size, size);
        List parents = commits.get(size - 1).parents;
        boolean more = parents != null && parents.size() > 0;
        if (hasMore && !more) {
            hasMore = false;
            notifyItemRemoved(getItemCount());
        } else if (!hasMore && more) {
            hasMore = true;
            notifyItemInserted(mCommits.size());
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (hasMore) notifyItemChanged(getItemCount() - 1);
    }

    public void setOnLoadListener(OnLoadListener listener) {
        this.mLoadListener = listener;
    }

    private class CommitViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView icon;
        private TextView messsage, info;
        private View dividerTop, dividerBottom;

        private CommitViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            icon = (ImageView) itemView.findViewById(R.id.coding_author);
            messsage = (TextView) itemView.findViewById(R.id.coding_message);
            info = (TextView) itemView.findViewById(R.id.coding_info);
            dividerTop = itemView.findViewById(R.id.coding_divider_top);
            dividerBottom = itemView.findViewById(R.id.coding_divider_bottom);
        }

        private void setData(Commit commit) {
            int position = getAdapterPosition();
            boolean isFirst = 0 == position;
            boolean isLast = !hasMore && getItemCount() - 1 == position;
            dividerTop.setVisibility(isFirst ? View.INVISIBLE : View.VISIBLE);
            dividerBottom.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
            Glide.with(context).load(commit.author.avatar_url).transform(new CircleTransform(context)).into(icon);
            messsage.setText(commit.commit.message);
            info.setText(getInfo(commit));
        }

        private String getInfo(Commit commit) {
            Date date = time2date(commit.commit.committer.date);
            return date.toLocaleString();
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        private LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loading_progressbar);
            Context context = itemView.getContext();
            int tint = ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, context.getTheme());
            Drawable indeterminate = progressBar.getIndeterminateDrawable();
            DrawableCompat.setTint(indeterminate, tint);
            DrawableCompat.setTintMode(indeterminate, PorterDuff.Mode.SRC_IN);
        }

        private void setLoading(boolean loading) {
            progressBar.setIndeterminate(loading);
            itemView.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        }

    }

    public interface OnLoadListener {
        void onLoad();
    }

}
