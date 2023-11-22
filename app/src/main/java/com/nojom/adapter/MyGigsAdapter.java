package com.nojom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.nojom.R;
import com.nojom.databinding.ItemMyGigsCopyBinding;
import com.nojom.model.GigList;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.List;

public class MyGigsAdapter extends RecyclerSwipeAdapter<MyGigsAdapter.SimpleViewHolder> {

    private Context context;
    private BaseActivity activity;
    private List<GigList.Data> activeData;
    private String filePath;
    private GigClickListener gigClickListener;

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public interface GigClickListener {
        void onClickGig(GigList.Data data, int pos);

        void onClickDeleteGig(GigList.Data data, int pos);

        void onClickEditGig(GigList.Data data, int pos);

        void onClickDuplicateGig(GigList.Data data, int pos);
    }

    public MyGigsAdapter(Context context, List<GigList.Data> data, String filePath, GigClickListener gigClickListener) {
        this.context = context;
        this.activity = (BaseActivity) context;
        activeData = data;
        this.filePath = filePath;
        this.gigClickListener = gigClickListener;
    }

    public void doRefresh(List<GigList.Data> paymentList) {
        this.activeData = paymentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemMyGigsCopyBinding itemAccountBinding =
                ItemMyGigsCopyBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        if (activeData != null && activeData.size() > 0) {
            GigList.Data item = activeData.get(position);

            holder.binding.swipe.setShowMode(SwipeLayout.ShowMode.LayDown);

            if (item.isShowProgress) {
                holder.binding.relView.setVisibility(View.VISIBLE);
                holder.binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                holder.binding.relView.setVisibility(View.GONE);
                holder.binding.progressBar.setVisibility(View.GONE);
                item.isShowProgress = false;
            }

            if (item.isNew.equalsIgnoreCase("1")) {//new
                holder.binding.tvIsNew.setVisibility(View.VISIBLE);
            } else {//gig
                holder.binding.tvIsNew.setVisibility(View.GONE);
            }

            holder.binding.tvCompletedJobs.setText(String.format(context.getString(R.string._7_completed_jobs), Utils.nFormate(item.totalCompleted)));
            /*if (item.totalCompleted > 0) {
                holder.binding.tvCompletedJobs.setBackgroundResource(R.drawable.green_button_bg_3);
                holder.binding.tvCompletedJobs.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                holder.binding.tvCompletedJobs.setBackgroundResource(R.drawable.gray_button_bg_3);
                holder.binding.tvCompletedJobs.setTextColor(context.getResources().getColor(R.color.tab_gray));
            }*/

            holder.binding.tvInprogressJobs.setText(String.format(context.getString(R.string._4_in_progress_jobs), Utils.nFormate(item.totalInProgress)));
            /*if (item.totalInProgress > 0) {
                holder.binding.tvInprogressJobs.setBackgroundResource(R.drawable.blue_button_bg_3);
                holder.binding.tvInprogressJobs.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                holder.binding.tvInprogressJobs.setBackgroundResource(R.drawable.gray_button_bg_3);
                holder.binding.tvInprogressJobs.setTextColor(context.getResources().getColor(R.color.tab_gray));
            }*/

            holder.binding.tvPendingJobs.setText(String.format(context.getString(R.string._1_pending_jobs), Utils.nFormate(item.totalPending)));
            /*if (item.totalPending > 0) {
                holder.binding.tvPendingJobs.setBackgroundResource(R.drawable.red_rounded_corner_3);
                holder.binding.tvPendingJobs.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                holder.binding.tvPendingJobs.setBackgroundResource(R.drawable.gray_button_bg_3);
                holder.binding.tvPendingJobs.setTextColor(context.getResources().getColor(R.color.tab_gray));
            }*/

            holder.binding.tvTitle.setText(item.gigTitle);

            String fileUrl;
            if (item.gigImages != null && item.gigImages.size() > 0) {
                fileUrl = filePath + item.gigImages.get(0).imageName;
            } else {
                fileUrl = filePath;
            }

            if (activity.language.equals("ar")) {
                holder.binding.swipe.setLeftSwipeEnabled(true);
                holder.binding.swipe.setRightSwipeEnabled(false);
                holder.binding.swipe.setDragEdge(SwipeLayout.DragEdge.Left);
                holder.binding.imageView.setCornerRadius(0,
                        activity.getResources().getDimension(R.dimen._5sdp), 0
                        , activity.getResources().getDimension(R.dimen._5sdp));
            } else {

                holder.binding.imageView.setCornerRadius(activity.getResources().getDimension(R.dimen._5sdp),
                        0, activity.getResources().getDimension(R.dimen._5sdp)
                        , 0);
            }

            Glide.with(context).load(fileUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.binding.imageView);
        }
        mItemManger.bindView(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return activeData != null ? activeData.size() : 0;
    }

    public List<GigList.Data> getData() {
        return activeData;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemMyGigsCopyBinding binding;

        SimpleViewHolder(ItemMyGigsCopyBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.relRoot.setOnClickListener(v -> {
                if (gigClickListener != null) {
                    mItemManger.closeAllItems();
                    binding.relView.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    gigClickListener.onClickGig(activeData.get(getAdapterPosition()), getAdapterPosition());
                }
            });

            itemView.llDelete.setOnClickListener(v -> {
                mItemManger.closeAllItems();
                if (gigClickListener != null) {
                    gigClickListener.onClickDeleteGig(activeData.get(getAdapterPosition()), getAdapterPosition());
                }
            });

            itemView.llEdit.setOnClickListener(view -> {
                mItemManger.closeAllItems();
                if (gigClickListener != null) {
                    binding.relView.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    gigClickListener.onClickEditGig(activeData.get(getAdapterPosition()), getAdapterPosition());
                }
            });

            itemView.llDuplicate.setOnClickListener(view -> {
                mItemManger.closeAllItems();
                if (gigClickListener != null) {
                    binding.relView.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    gigClickListener.onClickDuplicateGig(activeData.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

}
