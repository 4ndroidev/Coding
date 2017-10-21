package com.androidev.coding.module.commit.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidev.coding.R;
import com.androidev.coding.misc.CircleTransform;
import com.androidev.coding.model.Commit;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.androidev.coding.misc.Constant.ADDED;
import static com.androidev.coding.misc.Constant.DAY;
import static com.androidev.coding.misc.Constant.HOUR;
import static com.androidev.coding.misc.Constant.MINUTE;
import static com.androidev.coding.misc.Constant.MODIFIED;
import static com.androidev.coding.misc.Constant.MONTH;
import static com.androidev.coding.misc.Constant.RENAMED;
import static com.androidev.coding.network.GitHub.time2date;

public class CommitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_ITEM = 1;
    private final static int TYPE_HEADER = 2;

    private Commit mCommit;
    private OnItemClickListener<Commit.File> mOnItemClickListener;

    private View createItemView(int layoutId, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutId, parent, false);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new FileViewHolder(createItemView(R.layout.coding_layout_commit_item, parent));
            case TYPE_HEADER:
                return new HeaderViewHolder(createItemView(R.layout.coding_layout_commit_header, parent));
        }
        throw new IllegalStateException("Illegal viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_ITEM:
                ((FileViewHolder) holder).setData(mCommit.files.get(position - 1));
                break;
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).setData(mCommit);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCommit == null ? 0 : mCommit.files.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0)
            return TYPE_ITEM;
        else
            return TYPE_HEADER;
    }

    public void setData(Commit commit) {
        mCommit = commit;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<Commit.File> listener) {
        this.mOnItemClickListener = listener;
    }

    private class FileViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView title, addition, deletion;

        private FileViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.coding_icon);
            title = (TextView) itemView.findViewById(R.id.coding_title);
            addition = (TextView) itemView.findViewById(R.id.coding_addition);
            deletion = (TextView) itemView.findViewById(R.id.coding_deletion);
            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    int position = getAdapterPosition() - 1;
                    mOnItemClickListener.onItemClick(v, position, mCommit.files.get(position));
                }
            });
        }

        private void setData(Commit.File data) {
            icon.setImageResource(status4image(data.status));
            title.setText(data.filename.substring(data.filename.lastIndexOf("/") + 1));
            String additions = "+" + data.additions;
            String deletions = "-" + data.deletions;
            addition.setText(additions);
            deletion.setText(deletions);
        }

        private int status4image(String status) {
            if (MODIFIED.equals(status) || RENAMED.equals(status)) {
                return R.drawable.coding_icon_modification;
            } else if (ADDED.equals(status)) {
                return R.drawable.coding_icon_addition;
            } else {
                return R.drawable.coding_icon_deletion;
            }
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView icon;
        private TextView name, time, message, change;
        private int additionColor, deletionColor;

        private HeaderViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            icon = (ImageView) itemView.findViewById(R.id.coding_icon);
            name = (TextView) itemView.findViewById(R.id.coding_name);
            time = (TextView) itemView.findViewById(R.id.coding_time);
            message = (TextView) itemView.findViewById(R.id.coding_message);
            change = (TextView) itemView.findViewById(R.id.coding_change);
            Resources resources = context.getResources();
            Resources.Theme theme = context.getTheme();
            additionColor = ResourcesCompat.getColor(resources, R.color.colorAddition, theme);
            deletionColor = ResourcesCompat.getColor(resources, R.color.colorDeletion, theme);
        }

        private void setData(Commit data) {
            Object avatar = data.author != null ? data.author.avatar_url : R.drawable.coding_icon_default_author;
            Glide.with(context).load(avatar).placeholder(R.drawable.coding_icon_default_author)
                    .transform(new CircleTransform(context)).into(icon);
            name.setText(data.commit.author.name);
            time.setText(getTimeStamp(data.commit.committer.date));
            message.setText(data.commit.message);
            int fileSize = data.files.size();
            int additions = data.stats.additions;
            int deletions = data.stats.deletions;
            int start, end;
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(context.getString(R.string.coding_change_format, fileSize)).append(" ");
            String additionText = context.getString(R.string.coding_addition_format, additions);
            start = ssb.length();
            end = start + additionText.length();
            ssb.append(additionText).append(" ");
            ssb.setSpan(new ForegroundColorSpan(additionColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            String deletionText = context.getString(R.string.coding_deletion_format, deletions);
            start = ssb.length();
            end = start + deletionText.length();
            ssb.append(deletionText);
            ssb.setSpan(new ForegroundColorSpan(deletionColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            change.setText(ssb);
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

    public interface OnItemClickListener<T> {
        void onItemClick(View v, int position, T data);
    }
}
