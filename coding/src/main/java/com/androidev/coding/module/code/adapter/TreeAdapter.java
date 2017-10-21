package com.androidev.coding.module.code.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidev.coding.R;
import com.androidev.coding.model.Tree;

import java.util.List;

import static com.androidev.coding.misc.Constant.TREE;

public class TreeAdapter extends RecyclerView.Adapter<TreeAdapter.NodeViewHolder> {

    private List<Tree.Node> mNodes;
    private OnItemClickListener<Tree.Node> mOnItemClickListener;

    private View createItemView(int layoutId, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutId, parent, false);
    }

    @Override
    public NodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NodeViewHolder(createItemView(R.layout.coding_layout_tree_item, parent));
    }

    @Override
    public void onBindViewHolder(NodeViewHolder holder, int position) {
        holder.setData(mNodes.get(position));
    }

    @Override
    public int getItemCount() {
        return mNodes == null ? 0 : mNodes.size();
    }

    public void setData(List<Tree.Node> nodes) {
        mNodes = nodes;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<Tree.Node> listener) {
        mOnItemClickListener = listener;
    }

    class NodeViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView icon;
        private TextView name;

        private NodeViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            icon = (ImageView) itemView.findViewById(R.id.coding_icon);
            name = (TextView) itemView.findViewById(R.id.coding_name);
            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    int position = getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, position, mNodes.get(position));
                }
            });
        }

        private void setData(Tree.Node node) {
            icon.setImageResource(TREE.equals(node.type) ? R.drawable.coding_icon_folder : R.drawable.coding_icon_file);
            name.setText(node.path);
        }

    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, int position, T data);
    }

}
