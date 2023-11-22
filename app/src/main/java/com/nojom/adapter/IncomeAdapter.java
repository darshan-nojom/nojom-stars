package com.nojom.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemIncomeBalaneCopyBinding;
import com.nojom.model.Balance;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.SimpleViewHolder> {

    private BaseActivity context;
    private List<Balance.Income> incomeList;
    private static final int PENDING = 0;
    private static final int COMPLETED = 1;
    private static final int CANCELLED = 2;
    private static final int REFUNDED = 3;
    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClickItem(int jobPostId, int pos, String postType, String gigType);
    }

    public IncomeAdapter(BaseActivity context, OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
    }

    public void doRefresh(List<Balance.Income> incomeList) {
        this.incomeList = incomeList;
        notifyDataSetChanged();
    }

    public List<Balance.Income> getData() {
        return incomeList;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemIncomeBalaneCopyBinding incomeBalaneBinding =
                ItemIncomeBalaneCopyBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(incomeBalaneBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        Balance.Income item = incomeList.get(position);
        holder.binding.tvJobId.setText(context.getString(R.string.paid_for_job_id, item.jobPostId + ""));
        if(context.language.equals("ar")){
            holder.binding.tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy hh:mm:ss a", item.timestamp));
        }else{
            holder.binding.tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM dd, yyyy hh:mm:ss a", item.timestamp));
        }


        if (item.isShowProgress) {
            holder.binding.shimmerViewContainer.setBackgroundResource(R.drawable.transp_rounded_corner_10);
            holder.binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.binding.shimmerViewContainer.setBackgroundResource(R.drawable.white_rounded_corner_10);
            holder.binding.progressBar.setVisibility(View.GONE);
            item.isShowProgress = false;
        }

        if (item.type.equalsIgnoreCase("2")) {//job
            holder.binding.tvJobStatus.setBackgroundResource(R.drawable.blue_button_bg_topright_corner);
            holder.binding.tvJobStatus.setText(context.getString(R.string.job));
        } else {//gig
            holder.binding.tvJobStatus.setBackgroundResource(R.drawable.green_button_bg_topright_corner);
            holder.binding.tvJobStatus.setText(context.getString(R.string.gig));
        }

        if (item.refundType != null) {
            holder.binding.tvRefundStatus.setVisibility(View.VISIBLE);
            if (item.refundType.equalsIgnoreCase("1")) {
                holder.binding.tvRefundStatus.setText(context.getString(R.string.partial_refund));
            } else {//gig
                holder.binding.tvRefundStatus.setText(context.getString(R.string.full_refund));
            }

            holder.binding.tvRefundStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.orange_border_5));
            holder.binding.tvRefundStatus.setTextColor(ContextCompat.getColor(context, R.color.orange));
        } else {
            holder.binding.tvRefundStatus.setVisibility(View.GONE);
        }

        double finalAmnt;
        switch (item.status) {
            case PENDING:
                holder.binding.tvStatus.setText(context.getString(R.string.pending));
                holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_border_5));
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                holder.binding.tvBalance.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                finalAmnt = item.amount - item.refundedAmount;
                holder.binding.tvBalance.setText(String.format(context.getString(R.string.plus_), Utils.priceWith$(Utils.getDecimalValue("" + finalAmnt))));
                holder.binding.tvBalance.setPaintFlags(0);
                break;
            case COMPLETED:
                holder.binding.tvStatus.setText(context.getString(R.string.completed));
                holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.green_border_5));
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.greendark));
                holder.binding.tvBalance.setTextColor(ContextCompat.getColor(context, R.color.greendark));
                finalAmnt = item.amount - item.refundedAmount;
                holder.binding.tvBalance.setText(String.format(context.getString(R.string.plus_), Utils.priceWith$(Utils.getDecimalValue("" + finalAmnt))));
                holder.binding.tvBalance.setPaintFlags(0);
                break;
            case CANCELLED:
                holder.binding.tvStatus.setText(context.getString(R.string.cancel));
                holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.black_gray_border_5));
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
                holder.binding.tvBalance.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
                finalAmnt = item.amount - item.refundedAmount;
                holder.binding.tvBalance.setText(Utils.priceWith$(Utils.getDecimalValue("" + finalAmnt)));
                holder.binding.tvBalance.setPaintFlags(holder.binding.tvBalance.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                break;
            case REFUNDED:
                holder.binding.tvStatus.setText(context.getString(R.string.refund));
                holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.red_border_5));
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red_dark));
                holder.binding.tvBalance.setTextColor(ContextCompat.getColor(context, R.color.red_dark));
                finalAmnt = item.amount - item.refundedAmount;
                holder.binding.tvBalance.setText(String.format(context.getString(R.string.minus_), Utils.priceWith$(Utils.getDecimalValue("" + finalAmnt))));
                holder.binding.tvBalance.setPaintFlags(0);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return incomeList != null ? incomeList.size() : 0;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemIncomeBalaneCopyBinding binding;

        SimpleViewHolder(ItemIncomeBalaneCopyBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            itemView.getRoot().setOnClickListener(v -> {
                if (onClickListener != null) {
                    binding.shimmerViewContainer.setBackgroundResource(R.drawable.transp_rounded_corner_10);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    onClickListener.onClickItem(incomeList.get(getAdapterPosition()).jobPostId, getAdapterPosition(), incomeList.get(getAdapterPosition()).type
                            , incomeList.get(getAdapterPosition()).gigType);

                }

            });
        }
    }
}
