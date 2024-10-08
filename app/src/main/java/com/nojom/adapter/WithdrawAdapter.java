package com.nojom.adapter;

import static com.nojom.util.Constants.API_CANCEL_WITHDRAWALS;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ItemIncomeBalaneBinding;
import com.nojom.model.Withdrawal;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.balance.BalanceActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.List;
import java.util.Objects;

public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.SimpleViewHolder> implements APIRequest.APIRequestListener {

    private Context context;
    private List<Withdrawal> withdrawList;
    private static final int PENDING = 0;
    private static final int DONE = 1;
    private static final int CANCELLED = 2;
    private static final int REJECTED = 3;
    private BaseActivity activity;
    private OnCancelWithdrawals onCancelWithdrawals;

    public WithdrawAdapter(Context context, OnCancelWithdrawals onCancelWithdrawals) {
        this.context = context;
        activity = (BaseActivity) context;
        this.onCancelWithdrawals = onCancelWithdrawals;
    }

    public void doRefresh(List<Withdrawal> withdrawList) {
        this.withdrawList = withdrawList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemIncomeBalaneBinding incomeBalaneBinding =
                ItemIncomeBalaneBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(incomeBalaneBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        Withdrawal item = withdrawList.get(position);
        holder.binding.tvJobId.setText(item.beneficiary_name);
//        if (activity.language.equals("ar")) {
//            holder.binding.tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy hh:mm:ss a", item.timestamp));
//        } else {
//            holder.binding.tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM dd, yyyy hh:mm:ss a", item.timestamp));
//        }
        holder.binding.tvDate.setText(item.iban);
        switch (item.status) {
            case PENDING:
                holder.binding.tvStatus.setText(context.getString(R.string.pending));
                holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_border_5));
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                holder.binding.tvBalance.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                if (activity.getCurrency().equals("SAR")) {
                    holder.binding.tvBalance.setText(String.format("+%s", Utils.priceWithSAR(activity, Utils.getDecimalValue("" + item.amount))));
                } else {
                    holder.binding.tvBalance.setText(String.format("+%s", Utils.priceWith$(Utils.getDecimalValue("" + item.amount),activity)));
                }
                holder.binding.tvBalance.setPaintFlags(0);
                break;
            case DONE:
                holder.binding.tvStatus.setText(context.getString(R.string.done));
                holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.green_border_5));
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.greendark));
                holder.binding.tvBalance.setTextColor(ContextCompat.getColor(context, R.color.greendark));
                if (activity.getCurrency().equals("SAR")) {
                    holder.binding.tvBalance.setText(String.format("+%s", Utils.priceWithSAR(activity, Utils.getDecimalValue("" + item.amount))));
                } else {
                    holder.binding.tvBalance.setText(String.format("+%s", Utils.priceWith$(Utils.getDecimalValue("" + item.amount),activity)));
                }
                holder.binding.tvBalance.setPaintFlags(0);
                break;
            case CANCELLED:
                holder.binding.tvStatus.setText(context.getString(R.string.cancel));
                holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.black_gray_border_5));
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
                holder.binding.tvBalance.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
                if (activity.getCurrency().equals("SAR")) {
                    holder.binding.tvBalance.setText(Utils.priceWithSAR(activity, Utils.getDecimalValue("" + item.amount)));
                } else {
                    holder.binding.tvBalance.setText(Utils.priceWith$(Utils.getDecimalValue("" + item.amount),activity));
                }
                holder.binding.tvBalance.setPaintFlags(holder.binding.tvBalance.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                break;
            case REJECTED:
                holder.binding.tvStatus.setText(context.getString(R.string.rejected));
                holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.red_border_5));
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red_dark));
                holder.binding.tvBalance.setTextColor(ContextCompat.getColor(context, R.color.red_dark));
                if (activity.getCurrency().equals("SAR")) {
                    holder.binding.tvBalance.setText(String.format("-%s", Utils.priceWithSAR(activity, Utils.getDecimalValue("" + item.amount))));
                } else {
                    holder.binding.tvBalance.setText(String.format("-%s", Utils.priceWith$(Utils.getDecimalValue("" + item.amount),activity)));
                }
                holder.binding.tvBalance.setPaintFlags(0);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return withdrawList != null ? withdrawList.size() : 0;
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (onCancelWithdrawals != null) {
            onCancelWithdrawals.cancelWithdrawals();
        }

        Intent i = new Intent(context, BalanceActivity.class);
        i.putExtra(Constants.TAB_BALANCE, Constants.BALANCE_WITHDRAW);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
//        activity.hideProgress();
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {

//        activity.hideProgress();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemIncomeBalaneBinding binding;

        SimpleViewHolder(ItemIncomeBalaneBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
                if (withdrawList.get(getAdapterPosition()).status == PENDING) {
                    pendingWithdrawDialog(withdrawList.get(getAdapterPosition()));
                }
            });
        }
    }

    private void pendingWithdrawDialog(Withdrawal data) {
        final Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_withdraw);
        dialog.setCancelable(true);

        TextView tvTotal = dialog.findViewById(R.id.tv_total);
        TextView tvEmail = dialog.findViewById(R.id.tv_email);
        TextView tvProvider = dialog.findViewById(R.id.tv_provider);
        TextView tvDate = dialog.findViewById(R.id.tv_date);
        TextView tvCancelWithdraw = dialog.findViewById(R.id.tv_cancel_withdraw);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        TextView tvStatus = dialog.findViewById(R.id.tv_status);

        tvEmail.setText(data.beneficiary_name + "\n" + data.iban);
//        tvProvider.setText(String.format("(%s)", data.provider));
        if (activity.getCurrency().equals("SAR")) {
            tvTotal.setText(String.format("+%s", Utils.priceWithSAR(activity, Utils.getDecimalValue("" + data.amount))));
        } else {
            tvTotal.setText(String.format("+%s", Utils.priceWith$(Utils.getDecimalValue("" + data.amount),activity)));
        }

        if (activity.language.equals("ar")) {
            tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy hh:mm a", data.timestamp));
        } else {
            tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM dd, yyyy hh:mm a", data.timestamp));
        }

        tvStatus.setText(context.getString(R.string.pending));

        tvCancelWithdraw.setOnClickListener(view -> {
            cancelWithdrawal(data.id);
            dialog.dismiss();
        });

        tvOk.setOnClickListener(v -> dialog.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void cancelWithdrawal(int id) {
        if (!activity.isNetworkConnected()) {
            return;
        }

//        activity.showProgress();

        CommonRequest.CancelWithdraw cancelWithdraw = new CommonRequest.CancelWithdraw();
        cancelWithdraw.setWithdrawal_id(id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_CANCEL_WITHDRAWALS, cancelWithdraw.toString(), true, this);
    }

    public interface OnCancelWithdrawals {
        void cancelWithdrawals();
    }
}
