package com.nojom.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.textview.TextViewSFTextPro;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemContractListBinding;
import com.nojom.model.GigProjectList;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

public class GigProjectsAdapter extends RecyclerView.Adapter<GigProjectsAdapter.SimpleViewHolder> {

    private BaseActivity context;
    private List<GigProjectList.Data> projectsList;
    private PrettyTime p = new PrettyTime();
    private boolean isState;
    private GigClickListener jobClickListener;

    public interface GigClickListener {
        void onClickGig(int jobId, int selectedPos);
    }

    public boolean isState() {
        return isState;
    }

    public void setState(boolean state) {
        isState = state;
    }

    public GigProjectsAdapter(BaseActivity context, GigClickListener jobClickListener) {
        this.context = context;
        this.jobClickListener = jobClickListener;
    }

    public void doRefresh(List<GigProjectList.Data> projectsList) {
        this.projectsList = projectsList;
        notifyDataSetChanged();
    }

    public List<GigProjectList.Data> getData() {
        return projectsList;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemContractListBinding projectsListBinding =
                ItemContractListBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(projectsListBinding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        GigProjectList.Data item = projectsList.get(position);
        holder.binding.tvTitle.setText(String.format(context.getString(R.string.job_id_new) + " : %d", item.contractID));
        String date = p.format(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", item.createdAt));
        double totalPrice = projectsList.get(position).totalPrice;
        holder.binding.tvBudget.setText(String.format(context.getCurrency().equals("SAR") ? context.getString(R.string.s_sar_s) : "$%s - %s", totalPrice, date));

        if (item.isShowProgress) {
            holder.binding.shimmerViewContainer.setBackgroundResource(R.drawable.transp_rounded_corner_10);
            holder.binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.binding.shimmerViewContainer.setBackgroundResource(R.drawable.white_rounded_corner_10);
            holder.binding.progressBar.setVisibility(View.GONE);
            item.isShowProgress = false;
        }

        switch (item.gigStateID) {
            case Constants.BIDDING:
                updateStatus(holder.binding.tvStatus, item.getStateName(context.language), ContextCompat.getDrawable(context, R.drawable.yellow_border_5), ContextCompat.getColor(context, R.color.yellow));
                holder.binding.tvRefunds.setVisibility(View.GONE);
                break;
            case 7:
            case Constants.WAITING_FOR_ACCEPTANCE:
                updateStatus(holder.binding.tvStatus, item.getStateName(context.language), ContextCompat.getDrawable(context, R.drawable.lovender_border_5), ContextCompat.getColor(context, R.color.lovender));
                holder.binding.tvRefunds.setVisibility(View.GONE);
                break;
            case Constants.WAITING_FOR_DEPOSIT:
                updateStatus(holder.binding.tvStatus, item.getStateName(context.language), ContextCompat.getDrawable(context, R.drawable.red_border_5), ContextCompat.getColor(context, R.color.red_dark));
                holder.binding.tvRefunds.setVisibility(View.GONE);
                break;
            case Constants.BANK_TRANSFER_REVIEW:
            case Constants.REFUNDED:
                updateStatus(holder.binding.tvStatus, item.getStateName(context.language), ContextCompat.getDrawable(context, R.drawable.orange_border_5), ContextCompat.getColor(context, R.color.orange_light));
                holder.binding.tvRefunds.setVisibility(View.GONE);
                break;
            case Constants.IN_PROGRESS:
                updateStatus(holder.binding.tvStatus, item.getStateName(context.language), ContextCompat.getDrawable(context, R.drawable.blue_border_5), ContextCompat.getColor(context, R.color.colorPrimary));
                if (item.jobRefunds == 1) {//refund case
                    holder.binding.tvRefunds.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.tvRefunds.setVisibility(View.GONE);
                }
                break;
            case Constants.SUBMIT_WAITING_FOR_PAYMENT:
            case Constants.COMPLETED:
                updateStatus(holder.binding.tvStatus, item.getStateName(context.language), ContextCompat.getDrawable(context, R.drawable.green_border_5), ContextCompat.getColor(context, R.color.greendark));
                if (item.jobRefunds == 1) {//refund case
                    holder.binding.tvRefunds.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.tvRefunds.setVisibility(View.GONE);
                }
                break;
            case Constants.CANCELLED:
                updateStatus(holder.binding.tvStatus, item.getStateName(context.language), ContextCompat.getDrawable(context, R.drawable.black_gray_border_5), ContextCompat.getColor(context, R.color.gray_text));
                holder.binding.tvRefunds.setVisibility(View.GONE);
                break;
        }
        if (!isState()) {
            holder.binding.tvRefunds.setVisibility(View.GONE);
        }
    }

    private void updateStatus(TextViewSFTextPro tvStatus, String name, Drawable drawable, int color) {
        tvStatus.setText(name);
        tvStatus.setBackground(drawable);
        tvStatus.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return projectsList != null ? projectsList.size() : 0;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemContractListBinding binding;

        SimpleViewHolder(ItemContractListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
                if (jobClickListener != null) {
                    binding.shimmerViewContainer.setBackgroundResource(R.drawable.transp_rounded_corner_10);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    jobClickListener.onClickGig(projectsList.get(getAdapterPosition()).contractID, getAdapterPosition());
                }

            });
        }
    }
}
