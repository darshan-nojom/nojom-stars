package com.nojom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemPartnerAnswerBinding;
import com.nojom.model.PartnerWithUsResponse;

import java.util.List;

public class PartnerAnswerAdapter extends RecyclerView.Adapter<PartnerAnswerAdapter.SimpleViewHolder> {

    private Context context;
    private List<PartnerWithUsResponse.Answers> paymentList;
    private OnClickAnswerListener onClickAnswerListener;

    public interface OnClickAnswerListener {
        void onClickAnswer(int adapterPosition);
    }

    public PartnerAnswerAdapter(Context context, List<PartnerWithUsResponse.Answers> paymentList, OnClickAnswerListener onClickAnswerListener) {
        this.context = context;
        this.paymentList = paymentList;
        this.onClickAnswerListener = onClickAnswerListener;
    }

    public void doRefresh(List<PartnerWithUsResponse.Answers> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemPartnerAnswerBinding itemAccountBinding =
                ItemPartnerAnswerBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        PartnerWithUsResponse.Answers item = paymentList.get(position);
        holder.binding.tvAnswer.setText(item.answer);

        if (item.isSelected) {
            holder.binding.relView.setBackgroundResource(R.drawable.black_button_bg_5);
            holder.binding.tvAnswer.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.binding.relView.setBackgroundResource(R.drawable.white_rounded_corner_5);
            holder.binding.tvAnswer.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemPartnerAnswerBinding binding;

        SimpleViewHolder(ItemPartnerAnswerBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
                clearSelected();
                paymentList.get(getAdapterPosition()).isSelected = true;
                if (onClickAnswerListener != null) {
                    onClickAnswerListener.onClickAnswer(getAdapterPosition());
                }
                notifyDataSetChanged();
            });
        }
    }

    private void clearSelected() {
        try {
            if (paymentList != null && paymentList.size() > 0) {
                for (PartnerWithUsResponse.Answers data : paymentList) {
                    data.isSelected = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
