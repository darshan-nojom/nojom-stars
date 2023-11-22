package com.nojom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nojom.R;
import com.nojom.databinding.ItemUploadedFilesBinding;
import com.nojom.model.Attachment;
import com.nojom.ui.BaseActivity;

import java.io.File;
import java.util.ArrayList;

public class UploadFileAdapter extends RecyclerView.Adapter<UploadFileAdapter.SimpleViewHolder> {

    private ArrayList<Attachment> mDataset;
    private Context context;
    private BaseActivity activity;
    private OnFileDeleteListener onFileDeletelistener;

    public UploadFileAdapter(Context context) {
        this.context = context;
        activity = (BaseActivity) context;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemUploadedFilesBinding uploadedFilesBinding =
                ItemUploadedFilesBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(uploadedFilesBinding);
    }

    public void doRefresh(ArrayList<Attachment> mDataset) {
        this.mDataset = mDataset;
        notifyDataSetChanged();
    }

    public interface OnFileDeleteListener {
        void onFileDelete(ArrayList<Attachment> mDataset);
    }

    public void setOnFileDeleteListener(OnFileDeleteListener onFileDeletelistener) {
        this.onFileDeletelistener = onFileDeletelistener;
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        Attachment attachment = mDataset.get(position);
        File file = new File(attachment.filepath);
        viewHolder.binding.tvFileName.setText(file.getName());
        if (attachment.isImage)
            Glide.with(context).load(file).into(viewHolder.binding.imgFile);
        else
            viewHolder.binding.imgFile.setImageResource(R.drawable.file);

    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemUploadedFilesBinding binding;

        public SimpleViewHolder(ItemUploadedFilesBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
                File file = new File(mDataset.get(getAdapterPosition()).filepath);
                activity.viewFile(file);
            });

            binding.imgDelete.setOnClickListener(v -> {
                mDataset.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(), mDataset.size());
                if (onFileDeletelistener != null) {
                    onFileDeletelistener.onFileDelete(mDataset);
                }
            });
        }
    }
}
