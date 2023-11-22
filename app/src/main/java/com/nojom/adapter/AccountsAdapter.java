package com.nojom.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemAccountBinding;
import com.nojom.model.Payment;
import com.nojom.ui.balance.EditPaypalActivity;
import com.nojom.util.Constants;

import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.SimpleViewHolder> {

    private Context context;
    private List<Payment> paymentList;

    public AccountsAdapter(Context context) {
        this.context = context;
    }

    public void doRefresh(List<Payment> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemAccountBinding itemAccountBinding =
                ItemAccountBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        Payment item = paymentList.get(position);
        holder.binding.setAccount(item);

        if (!TextUtils.isEmpty(item.token) || item.verified.equalsIgnoreCase("1")) {
            holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.green_border_5));
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.greendark));
            holder.binding.tvStatus.setText(context.getString(R.string.verified));
        } else {
            holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.red_border_5));
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red_dark));
            holder.binding.tvStatus.setText(context.getString(R.string.not_verified));
        }
    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemAccountBinding binding;

        SimpleViewHolder(ItemAccountBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
                Intent i = new Intent(context, EditPaypalActivity.class);
                i.putExtra(Constants.ACCOUNT_DATA, paymentList.get(getAdapterPosition()));
                context.startActivity(i);
            });
        }
    }
}
