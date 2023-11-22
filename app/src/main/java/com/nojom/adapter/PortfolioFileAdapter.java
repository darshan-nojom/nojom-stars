package com.nojom.adapter;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nojom.R;
import com.nojom.databinding.ItemPortfolioAddButtonBinding;
import com.nojom.databinding.ItemPortfolioBinding;
import com.nojom.model.Portfolios;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class PortfolioFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Portfolios.PortfolioFiles> mDataset;
    private BaseActivity activity;
    private OnClickListener onClickListener;
    private int VIEW_TYPE_ITEM = 1;
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    private boolean showProgress;

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public interface OnClickListener {
        void onAddImage();

        void onClickImage(Portfolios.PortfolioFiles data, int pos);
    }

    public PortfolioFileAdapter(BaseActivity context, List<Portfolios.PortfolioFiles> attachments, OnClickListener onClickListener) {
        this.activity = context;
        this.mDataset = attachments;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ITEM) {//cell item
            ItemPortfolioBinding uploadedFilesBinding =
                    ItemPortfolioBinding.inflate(layoutInflater, parent, false);
            return new SimpleViewHolder(uploadedFilesBinding);
        } else {// add button layout
            ItemPortfolioAddButtonBinding addButtonBinding =
                    ItemPortfolioAddButtonBinding.inflate(layoutInflater, parent, false);
            return new SimpleViewHolderAddButton(addButtonBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

//        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
        if (position != mDataset.size()) {
            try {
                Portfolios.PortfolioFiles attachment = mDataset.get(position);
                if (TextUtils.isEmpty(attachment.path)) {
                    ((SimpleViewHolder) holder).binding.imgPortfolio.setImageResource(R.drawable.ic_portfolio_blank);
                    ((SimpleViewHolder) holder).binding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    ((SimpleViewHolder) holder).binding.progressBar.setVisibility(View.GONE);
                    String urlEncoded = Uri.encode(attachment.path, ALLOWED_URI_CHARS);
                    Glide.with(activity).load(activity.getPortfolioUrl() + urlEncoded)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(((SimpleViewHolder) holder).binding.imgPortfolio);
                    if (attachment.sort == 1) {
                        ((SimpleViewHolder) holder).binding.txtMainPhoto.setVisibility(View.VISIBLE);
                    } else {
                        ((SimpleViewHolder) holder).binding.txtMainPhoto.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Portfolios.PortfolioFiles> getList() {
        if (mDataset == null) {
            mDataset = new ArrayList<>();
        }
        return mDataset;
    }

    @Override
    public int getItemCount() {
        if (mDataset.size() == 4) {
            return mDataset.size();
        }
        return mDataset.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_TYPE_ADD = 2;
        return (position == mDataset.size()) ? VIEW_TYPE_ADD : VIEW_TYPE_ITEM;
    }

    public void deleteItem(int deletedPos) {
        mDataset.remove(deletedPos);
        notifyItemRemoved(deletedPos);
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemPortfolioBinding binding;

        public SimpleViewHolder(ItemPortfolioBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClickImage(mDataset.get(getAdapterPosition()), getAdapterPosition());
                }
            });

            itemView.imgFull.setOnClickListener(v -> {
                String urlEncoded = Uri.encode(mDataset.get(getAdapterPosition()).path, ALLOWED_URI_CHARS);
                activity.viewFile(activity.getPortfolioUrl() + urlEncoded);
            });
        }
    }

    public class SimpleViewHolderAddButton extends RecyclerView.ViewHolder {

        ItemPortfolioAddButtonBinding binding;

        public SimpleViewHolderAddButton(ItemPortfolioAddButtonBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.rlAddImage.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onAddImage();
                }
            });
        }
    }
}
