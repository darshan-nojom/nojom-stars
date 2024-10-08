package com.nojom.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemCategoryListBinding;
import com.nojom.model.Language;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.SimpleViewHolder> {

    private List<ProfileResponse.BankName> mDatasetFiltered;
    private BaseActivity context;
    private OnClickPaymentListener onClickLanguageListener;
    private String selectedLanguageList;

    public void setSelectedLanguageList(String selectedLanguageList) {
        this.selectedLanguageList = selectedLanguageList;
    }

    public interface OnClickPaymentListener {
        void onClickBank(ProfileResponse.BankName name, int adapterPos);
    }

    public PaymentAdapter(BaseActivity context, List<ProfileResponse.BankName> objects, OnClickPaymentListener listener) {
        this.mDatasetFiltered = objects;
        this.context = context;
        this.onClickLanguageListener = listener;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemCategoryListBinding fullBinding =
                ItemCategoryListBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            ProfileResponse.BankName item = mDatasetFiltered.get(position);

            holder.binding.tvCategory.setText(item.getBankName(context.language));
            if (selectedLanguageList.contains(item.getBankName(context.language))) {
//            if (item.isSelected) {
                holder.binding.imgCheckUncheck.setImageResource(R.drawable.check_done);
                holder.binding.imgCheckUncheck.clearColorFilter();
            } else {
                holder.binding.imgCheckUncheck.setImageResource(R.drawable.circle_uncheck);
                holder.binding.imgCheckUncheck.setColorFilter(ContextCompat.getColor(context,
                        R.color.full_dark_green), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSelectedItem() {
        return selectedLanguageList;
    }

    @Override
    public int getItemCount() {
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

    public List<ProfileResponse.BankName> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemCategoryListBinding binding;

        public SimpleViewHolder(ItemCategoryListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.rlView.setOnClickListener(v -> {
                try {

                    selectedLanguageList = mDatasetFiltered.get(getAdapterPosition()).getBankName(context.language);

//                    if (mDatasetFiltered.get(getAdapterPosition()).isSelected) {
//                        if (onClickLanguageListener != null) {
//                            onClickLanguageListener.onClickLanguage(true, mDatasetFiltered.get(getAdapterPosition()));
//                        }
//                    } else {
//                        if (onClickLanguageListener != null) {
//                            onClickLanguageListener.onClickLanguage(false, mDatasetFiltered.get(getAdapterPosition()));
//                        }
//                    }
                    // notifyItemChanged(getAdapterPosition());

                    if (onClickLanguageListener != null) {
                        onClickLanguageListener.onClickBank(mDatasetFiltered.get(getAdapterPosition()), getAdapterPosition());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
