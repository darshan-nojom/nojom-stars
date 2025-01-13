package com.nojom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemBankAccountNewBinding;
import com.nojom.model.WalletAccount;
import com.nojom.ui.BaseActivity;

import java.util.List;

public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.ViewHolder> {

    private List<WalletAccount> timelineItems;
    private BaseActivity activity;
    private OnClickBankListener onClickStarListener;
    public boolean isHideButton;

    public interface OnClickBankListener {
        void onClickBank(int pos, WalletAccount walletAccount);
    }

    public BankAccountAdapter(BaseActivity activity, List<WalletAccount> timelineItems, OnClickBankListener listener) {
        this.timelineItems = timelineItems;
        this.activity = activity;
        this.onClickStarListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBankAccountNewBinding itemCardListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_bank_account_new, parent, false);
        return new ViewHolder(itemCardListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WalletAccount item = timelineItems.get(position);

//        Glide.with(activity).load(item.profile_picture).error(R.color.orange).into(holder.binding.imgProfile);
        holder.binding.tvBenfName.setText("" + item.bank_name);
        holder.binding.tvAccNo.setText(maskAccountNumber(item.account_number));
//
        if (item.isSelect) {
            holder.binding.imgChk.setImageResource(R.drawable.radio_button_active);
        } else {
            holder.binding.imgChk.setImageResource(R.drawable.rounded_border_black_50);
        }
    }

    @Override
    public int getItemCount() {
        return timelineItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemBankAccountNewBinding binding;

        public ViewHolder(@NonNull ItemBankAccountNewBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            if (isHideButton) {
                binding.imgChk.setVisibility(View.INVISIBLE);
                binding.imgWallet.setVisibility(View.GONE);
            }

            itemView.getRoot().setOnClickListener(view -> {
                for (WalletAccount account : timelineItems) {
                    account.isSelect = false;
                }
                timelineItems.get(getAdapterPosition()).isSelect = true;
                if (onClickStarListener != null) {
                    onClickStarListener.onClickBank(getAdapterPosition(), timelineItems.get(getAdapterPosition()));
                }
                notifyDataSetChanged();
            });
        }
    }

    public String maskAccountNumber(String accountNumber) {
        int length = accountNumber.length();

        // Ensure the account number is long enough
        if (length < 4) {
            return accountNumber; // Return as-is if too short
        }

        // Extract the last 4 digits
        String lastFourDigits = accountNumber.substring(length - 4);

        // Create the masked string (**** **** **** ...)
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < length - 4; i++) {
            if (i > 0 && i % 4 == 0) {
                masked.append(" "); // Add space after every 4 characters
            }
            masked.append("*");
        }

        // Append the last 4 digits
        masked.append(" ").append(lastFourDigits);

        return masked.toString();
    }

}
