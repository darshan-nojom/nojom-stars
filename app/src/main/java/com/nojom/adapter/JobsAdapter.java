package com.nojom.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemJobsListCopyBinding;
import com.nojom.model.Projects.Data;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.SimpleViewHolder> {

    private Context context;
    private List<Data> projectsList;
    private PrettyTime p = new PrettyTime();
    private boolean isBidding;
    private boolean isOffer;
    private OnNewJobClick onNewJobClick;
    private BaseActivity activity;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClickJob(int jobId, int selectedPos);
    }

    public JobsAdapter(Context context, boolean isBidding, boolean isOffer, OnItemClickListener clickListener) {
        this.context = context;
        this.isBidding = isBidding;
        this.isOffer = isOffer;
        this.onItemClickListener = clickListener;
        activity = (BaseActivity) context;
    }

    public void doRefresh(List<Data> projectsList) {
        this.projectsList = projectsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemJobsListCopyBinding itemJobsListBinding =
                ItemJobsListCopyBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemJobsListBinding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        if (projectsList != null) {
            Data item = projectsList.get(position);
            holder.binding.tvTitle.setText(item.title);
            holder.binding.tvJobId.setText(String.format(context.getString(R.string.job_id_colon_), item.id));

            if (item.isShowProgress) {
                holder.binding.shimmerViewContainer.setBackgroundResource(R.drawable.transp_rounded_corner_10);
                holder.binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                holder.binding.shimmerViewContainer.setBackgroundResource(R.drawable.white_rounded_corner_10);
                holder.binding.progressBar.setVisibility(View.GONE);
                item.isShowProgress = false;
            }

            Date date1 = Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", item.timestamp);
            Date date = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dfFinal2 = new SimpleDateFormat("MMM dd, yyyy");

            if (date1 != null) {
                if (activity.printDifference(date1, date).equalsIgnoreCase("0")) {
                    String result = p.format(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", item.timestamp));
                    holder.binding.tvDaysleft.setText(result);
                } else {
                    String finalDate = dfFinal2.format(date1);
                    holder.binding.tvDaysleft.setText(finalDate);
                }
            }

            holder.binding.tvBids.setText(item.bidsCount > 1 ? item.bidsCount + " "+context.getString(R.string.bids_) : item.bidsCount + " "+context.getString(R.string.bid_));
            if (item.clientRateId == 0 && item.budget != null) {
                holder.binding.tvBudget.setText("$" + Utils.getDecimalValue("" + item.budget) + "");
            } else {
                if (item.rangeTo != null && item.rangeFrom != null) {
                    if (!TextUtils.isEmpty(item.rangeTo) && !item.rangeTo.equals("null")) {
                        holder.binding.tvBudget.setText(String.format("$%s - $%s", item.rangeFrom, item.rangeTo));
                    } else {
                        holder.binding.tvBudget.setText(String.format("$%s", item.rangeFrom));
                    }
                } else if (item.budget != null) {
                    holder.binding.tvBudget.setText("$" + Utils.getDecimalValue("" + item.budget) + "");
                } else {
                    holder.binding.tvBudget.setText(activity.getString(R.string.free));
                }
            }

            if (isBidding || isOffer) {
                holder.binding.tvSeen.setVisibility(View.GONE);
            } else {
                holder.binding.tvSeen.setVisibility(item.seen == 0 ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return projectsList != null ? projectsList.size() : 0;
    }

    public interface OnNewJobClick {
        void newJobClick(int position);
    }

    public List<Data> getData() {
        return projectsList;
    }

    public void setOnNewJobClick(OnNewJobClick onNewJobClick) {
        this.onNewJobClick = onNewJobClick;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemJobsListCopyBinding binding;

        SimpleViewHolder(ItemJobsListCopyBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
                try {
                    if (projectsList != null) {
                        if (getAdapterPosition() >= 0 && projectsList.size() > 0)
                            if (!isBidding && projectsList.get(getAdapterPosition()).seen == 1) {
                                if (onNewJobClick != null) {
                                    onNewJobClick.newJobClick(getAdapterPosition());
                                }
                            }
                        if (onItemClickListener != null) {
                            binding.shimmerViewContainer.setBackgroundResource(R.drawable.transp_rounded_corner_10);
                            binding.progressBar.setVisibility(View.VISIBLE);
                            activity.disableEnableTouch(true);
                            onItemClickListener.onClickJob(projectsList.get(getAdapterPosition()).id, getAdapterPosition());
                        }

                    }
                } catch (Exception e) {
                    Log.e("Error", "------------------" + e);
                    e.printStackTrace();
                }
            });
        }
    }
}
