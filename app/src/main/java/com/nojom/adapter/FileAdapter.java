package com.nojom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nojom.R;
import com.nojom.databinding.ItemSelectFileBinding;
import com.nojom.model.CampFile;
import com.nojom.ui.BaseActivity;

import java.util.List;


public class FileAdapter extends RecyclerView.Adapter<FileAdapter.SimpleViewHolder> {

    private BaseActivity baseActivity;
    private List<CampFile> arrUserList;
    private String imgPath;
    private LayoutInflater layoutInflater;

    public FileAdapter(Context context, List<CampFile> objects, String imgPath) {
        this.arrUserList = objects;
        this.imgPath = imgPath;
        baseActivity = ((BaseActivity) context);
    }

    public void doRefresh(List<CampFile> arrUserChatList) {
        this.arrUserList = arrUserChatList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemSelectFileBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_select_file, parent, false);
        return new SimpleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        final CampFile item = arrUserList.get(position);
        holder.binding.tvReceiverName.setText(item.fileName);
        if (item.isImage) {
            holder.binding.tvLastMessage.setVisibility(View.VISIBLE);
            holder.binding.tvLastMessage.setText(formatSize(item.fileSize));
        } else {
            holder.binding.tvLastMessage.setVisibility(View.GONE);
        }

        if (item.filepath.startsWith("http")) {
            if (item.isImage) {
                holder.binding.imgCancel.setVisibility(View.GONE);
            } else {
                holder.binding.imgCancel.setVisibility(View.VISIBLE);
            }
        } else {
            holder.binding.imgCancel.setVisibility(View.VISIBLE);
        }

        if (item.isImage) {
            Glide.with(baseActivity)
                    .load(item.filepath)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .centerCrop()
                    .into(holder.binding.imgProfile);
        } else {
            holder.binding.imgProfile.setImageResource(R.drawable.link_icon);
        }
    }

    public String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

    @Override
    public int getItemCount() {
        return arrUserList != null ? arrUserList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSelectFileBinding binding;

        public SimpleViewHolder(ItemSelectFileBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

            binding.imgCancel.setOnClickListener(view -> {
                arrUserList.remove(getAdapterPosition());
                notifyDataSetChanged();
            });

            itemView.getRoot().setOnClickListener(view -> {
                baseActivity.viewFile(arrUserList.get(getAdapterPosition()).filepath);
            });
        }
    }
}
