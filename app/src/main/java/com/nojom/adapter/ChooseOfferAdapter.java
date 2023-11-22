package com.nojom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nojom.R;
import com.nojom.databinding.ItemOfferListBinding;
import com.nojom.model.GigList;

import java.util.List;

public class ChooseOfferAdapter extends RecyclerView.Adapter<ChooseOfferAdapter.SimpleViewHolder> {

    private Context context;
    private List<GigList.Data> paymentList;
    private String filePath;
    private GigClickListener gigClickListener;

    public interface GigClickListener {
        void onClickGig(GigList.Data data, int pos);
    }

    public ChooseOfferAdapter(Context context, List<GigList.Data> data, String filePath, GigClickListener gigClickListener) {
        this.context = context;
        paymentList = data;
        this.filePath = filePath;
        this.gigClickListener = gigClickListener;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemOfferListBinding itemAccountBinding =
                ItemOfferListBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        GigList.Data item = paymentList.get(position);
        if (item.isShowProgress) {
            holder.binding.relView.setBackground(context.getResources().getDrawable(R.drawable.transp_rounded_corner_5));
            holder.binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.binding.relView.setBackground(context.getResources().getDrawable(R.drawable.white_button_bg));
            holder.binding.progressBar.setVisibility(View.GONE);
            item.isShowProgress = false;
        }

        holder.binding.tvReceiverName.setText(item.gigTitle);

        String fileUrl;
        if (item.gigImages != null && item.gigImages.size() > 0) {
            fileUrl = filePath + item.gigImages.get(0).imageName;
        } else {
            fileUrl = filePath;
        }

        Glide.with(context).load(fileUrl)
                .placeholder(R.color.black)
                .into(holder.binding.image);
    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemOfferListBinding binding;

        SimpleViewHolder(ItemOfferListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
                if (gigClickListener != null) {
                    binding.relView.setBackground(context.getResources().getDrawable(R.drawable.transp_rounded_corner_5));
                    binding.progressBar.setVisibility(View.VISIBLE);

                    gigClickListener.onClickGig(paymentList.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public List<GigList.Data> getData() {
        return paymentList;
    }

    public void doRefresh(List<GigList.Data> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }
}
