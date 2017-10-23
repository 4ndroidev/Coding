package com.androidev.coding.module.commit.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.androidev.coding.misc.Constant.DAY;
import static com.androidev.coding.misc.Constant.HOUR;
import static com.androidev.coding.misc.Constant.MINUTE;
import static com.androidev.coding.misc.Constant.MONTH;
import static com.androidev.coding.misc.Misc.time2date;

public class CommitsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_ITEM = 1;
    private final static int TYPE_LOADING = 2;

    private boolean hasMore;
    private boolean isLoading;
    private List<Commit> mCommits;
    private OnLoadListener mLoadListener;
    private OnItemClickListener<Commit> mOnItemClickListener;

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
                return new CommitViewHolder(createItemView(R.layout.coding_layout_commits_item, parent));
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
        int additionSize = commits.size();
        mCommits.addAll(commits);
        notifyItemRangeInserted(mCommits.size() - additionSize, additionSize);
        List parents = commits.get(additionSize - 1).parents;
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

    public void setOnItemClickListener(OnItemClickListener<Commit> listener) {
        this.mOnItemClickListener = listener;
    }

    private class CommitViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView icon;
        private TextView message, info;
        private View dividerTop, dividerBottom;

        private CommitViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            icon = (ImageView) itemView.findViewById(R.id.coding_icon);
            message = (TextView) itemView.findViewById(R.id.coding_message);
            info = (TextView) itemView.findViewById(R.id.coding_info);
            dividerTop = itemView.findViewById(R.id.coding_divider_top);
            dividerBottom = itemView.findViewById(R.id.coding_divider_bottom);
            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    int position = getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, position, mCommits.get(position));
                }
            });
        }

        private void setData(Commit data) {
            int position = getAdapterPosition();
            boolean isFirst = 0 == position;
            boolean isLast = !hasMore && getItemCount() - 1 == position;
            dividerTop.setVisibility(isFirst ? View.INVISIBLE : View.VISIBLE);
            dividerBottom.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
            message.setText(Html.fromHtml(data.commit.message));
            Object avatar = data.author != null ? data.author.avatar_url : R.drawable.coding_icon_default_author;
            Glide.with(context).load(avatar).placeholder(R.drawable.coding_icon_default_author)
                    .transform(new CircleTransform(context)).into(icon);
            String author = data.commit.author.name;
            String timestamp = getTimeStamp(data.commit.committer.date);
            info.setText(context.getString(R.string.coding_commit_info_format, author, timestamp));
        }

        private String getTimeStamp(String time) {
            Date date = time2date(time);
            Date now = new Date();
            long pass = (now.getTime() - date.getTime()) / 1000;
            if (pass < MINUTE) {
                return context.getString(R.string.coding_second_format, pass);
            } else if (pass < HOUR) {
                return context.getString(R.string.coding_minute_format, pass / MINUTE);
            } else if (pass < DAY) {
                return context.getString(R.string.coding_hour_format, pass / HOUR);
            } else if (pass < MONTH) {
                return context.getString(R.string.coding_day_format, pass / DAY);
            } else
                return new SimpleDateFormat(context.getString(R.string.coding_date_format), Locale.US).format(date);
        }

    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        private LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.coding_loading_progressbar);
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

    public interface OnItemClickListener<T> {
        void onItemClick(View v, int position, T data);
    }

}
