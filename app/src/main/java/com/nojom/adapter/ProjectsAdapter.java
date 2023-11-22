package com.nojom.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.textview.TextViewSFTextPro;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemProjectsListCopyBinding;
import com.nojom.model.Projects;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.SimpleViewHolder> {

    private Context context;
    private List<Projects.Data> projectsList;
    private PrettyTime p = new PrettyTime();
    private boolean isState;
    private JobClickListener jobClickListener;

    public interface JobClickListener {
        void onClickJob(int jobId, int selectedPos, String jobType, String gigType);
    }

    public boolean isState() {
        return isState;
    }

    public void setState(boolean state) {
        isState = state;
    }

    public ProjectsAdapter(Context context, JobClickListener jobClickListener) {
        this.context = context;
        this.jobClickListener = jobClickListener;
    }

    public void doRefresh(List<Projects.Data> projectsList) {
        this.projectsList = projectsList;
        notifyDataSetChanged();
    }

    public List<Projects.Data> getData() {
        return projectsList;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemProjectsListCopyBinding projectsListBinding =
                ItemProjectsListCopyBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(projectsListBinding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        Projects.Data item = projectsList.get(position);
        holder.binding.tvTitle.setText(item.title);
        holder.binding.tvJobId.setText(String.format(context.getString(R.string.job_id_colon_), item.id));
//        holder.binding.tvJobId.setText(String.format(context.getString(R.string.job_id_colon_), item.id));
        String result = p.format(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", item.timestamp));
        holder.binding.tvDaysleft.setText(result);

        if (item.isShowProgress) {
            holder.binding.shimmerViewContainer.setBackgroundResource(R.drawable.transp_rounded_corner_10);
            holder.binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.binding.shimmerViewContainer.setBackgroundResource(R.drawable.white_rounded_corner_10);
            holder.binding.progressBar.setVisibility(View.GONE);
            item.isShowProgress = false;
        }

        if (!TextUtils.isEmpty(item.job)) {
            holder.binding.tvJobStatus.setVisibility(View.VISIBLE);
            if (item.job.equalsIgnoreCase("job")) {//job
                holder.binding.tvJobStatus.setBackgroundResource(R.drawable.blue_button_bg);
                holder.binding.tvJobStatus.setText(context.getString(R.string.job));
                holder.binding.tvBidDot.setVisibility(View.VISIBLE);
                holder.binding.tvBids.setVisibility(View.VISIBLE);
                holder.binding.tvBids.setText(item.bidsCount > 1 ? String.format(context.getString(R.string.bids), Utils.nFormate(item.bidsCount)) : String.format(context.getString(R.string.bid), Utils.nFormate(item.bidsCount)));
            } else {//gig
                holder.binding.tvBidDot.setVisibility(View.GONE);
                holder.binding.tvBids.setVisibility(View.GONE);
                holder.binding.tvJobStatus.setBackgroundResource(R.drawable.green_button_bg);
                holder.binding.tvJobStatus.setText(context.getString(R.string.gig));
            }
        } else {
            holder.binding.tvBidDot.setVisibility(View.VISIBLE);
            holder.binding.tvBids.setVisibility(View.VISIBLE);
            holder.binding.tvJobStatus.setVisibility(View.GONE);
            holder.binding.tvBids.setText(item.bidsCount > 1 ? String.format(context.getString(R.string.bids), Utils.nFormate(item.bidsCount)) : String.format(context.getString(R.string.bid), Utils.nFormate(item.bidsCount)));
        }

        if (item.clientRateId == 0 && item.budget != null) {
            holder.binding.tvBudget.setText("$" + Utils.getDecimalValue("" + item.budget) + "");
        } else {
            if (item.rangeFrom != null && item.rangeTo != null) {
                if (!TextUtils.isEmpty(item.rangeTo) && !item.rangeTo.equals("null")) {
                    holder.binding.tvBudget.setText(String.format("$%s - $%s", item.rangeFrom, item.rangeTo));
                } else {
                    holder.binding.tvBudget.setText(String.format("$%s", item.rangeFrom));
                }
            } else if (item.budget != null) {
                holder.binding.tvBudget.setText("$" + Utils.getDecimalValue("" + item.budget) + "");
            } else {
                holder.binding.tvBudget.setText(context.getString(R.string.free));
            }
        }

        switch (item.jobPostStateId) {
            case Constants.BIDDING:
                updateStatus(holder.binding.tvStatus, item.jobPostStateName, ContextCompat.getDrawable(context, R.drawable.yellow_border_5), ContextCompat.getColor(context, R.color.yellow));
                holder.binding.tvRefunds.setVisibility(View.GONE);
                break;
            case 7:
            case Constants.WAITING_FOR_ACCEPTANCE:
                updateStatus(holder.binding.tvStatus, item.jobPostStateName, ContextCompat.getDrawable(context, R.drawable.lovender_border_5), ContextCompat.getColor(context, R.color.lovender));
                holder.binding.tvRefunds.setVisibility(View.GONE);
                break;
            case Constants.WAITING_FOR_DEPOSIT:
                updateStatus(holder.binding.tvStatus, item.jobPostStateName, ContextCompat.getDrawable(context, R.drawable.red_border_5), ContextCompat.getColor(context, R.color.red_dark));
                holder.binding.tvRefunds.setVisibility(View.GONE);
                break;
            case Constants.IN_PROGRESS:
                updateStatus(holder.binding.tvStatus, item.jobPostStateName, ContextCompat.getDrawable(context, R.drawable.blue_border_5), ContextCompat.getColor(context, R.color.colorPrimary));
                if (item.jobRefunds != null) {//refund case
                    holder.binding.tvRefunds.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.tvRefunds.setVisibility(View.GONE);
                }
                break;
            case Constants.SUBMIT_WAITING_FOR_PAYMENT:
            case Constants.COMPLETED:
                updateStatus(holder.binding.tvStatus, item.jobPostStateName, ContextCompat.getDrawable(context, R.drawable.green_border_5), ContextCompat.getColor(context, R.color.greendark));
                if (item.jobRefunds != null) {//refund case
                    holder.binding.tvRefunds.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.tvRefunds.setVisibility(View.GONE);
                }
                break;
            case Constants.CANCELLED:
                updateStatus(holder.binding.tvStatus, item.jobPostStateName, ContextCompat.getDrawable(context, R.drawable.black_gray_border_5), ContextCompat.getColor(context, R.color.gray_text));
                holder.binding.tvRefunds.setVisibility(View.GONE);
                break;
            case Constants.REFUNDED:
                updateStatus(holder.binding.tvStatus, item.jobPostStateName, ContextCompat.getDrawable(context, R.drawable.orange_border_5), ContextCompat.getColor(context, R.color.orange_light));
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

        ItemProjectsListCopyBinding binding;

        SimpleViewHolder(ItemProjectsListCopyBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
                if (jobClickListener != null) {
                    try {
                        if (getAdapterPosition() >= 0) {
                            binding.shimmerViewContainer.setBackgroundResource(R.drawable.transp_rounded_corner_10);
                            binding.progressBar.setVisibility(View.VISIBLE);

                            jobClickListener.onClickJob(projectsList.get(getAdapterPosition()).id, getAdapterPosition(), projectsList.get(getAdapterPosition()).job
                                    , projectsList.get(getAdapterPosition()).gigType);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
