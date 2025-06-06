package com.nojom.multitypepicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.multitypepicker.filter.entity.Directory;

import java.util.ArrayList;

/**
 * Created by Vincent Woo
 * Date: 2018/2/27
 * Time: 10:25
 */

public class FolderListAdapter extends BaseAdapter<Directory, FolderListAdapter.FolderListViewHolder> {
    private FolderListListener mListener;

    public FolderListAdapter(Context ctx, ArrayList<Directory> list) {
        super(ctx, list);
    }

    @Override
    public FolderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_folder_list,
                parent, false);
        return new FolderListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FolderListViewHolder holder, int position) {
        holder.mTvTitle.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class FolderListViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;

        FolderListViewHolder(View itemView) {
            super(itemView);

            mTvTitle = itemView.findViewById(R.id.tv_folder_title);

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onFolderListClick(mList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface FolderListListener {
        void onFolderListClick(Directory directory);
    }

    public void setListener(FolderListListener listener) {
        this.mListener = listener;
    }
}
