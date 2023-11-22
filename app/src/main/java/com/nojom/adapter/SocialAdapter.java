package com.nojom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.nojom.R;
import com.nojom.databinding.ItemSocialBinding;
import com.nojom.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.ViewHolder> {

    private Context context;
    private List<String> socialList;
    private int selectedService = -1;

    public int getSelectedService() {
        return selectedService;
    }

    public SocialAdapter(Context context, List<String> socialList) {
        this.context = context;
        this.socialList = socialList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemSocialBinding socialBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_social, parent, false);
        return new ViewHolder(socialBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.tvSkill.setText(socialList.get(position));

        if (selectedService == position) {
            holder.binding.tvSkill.setBackground(ContextCompat.getDrawable(context, R.drawable.black_button_bg));
            holder.binding.tvSkill.setTextColor(Color.WHITE);
            holder.binding.tvSkill.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.SFTEXT_BOLD));
        } else {
            holder.binding.tvSkill.setBackground(ContextCompat.getDrawable(context, R.drawable.white_button_bg));
            holder.binding.tvSkill.setTextColor(Color.BLACK);
            holder.binding.tvSkill.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.SFTEXT_REGULAR));
        }
    }

    public String getSelectedItem() {
        if (selectedService != -1) {
            return socialList.get(selectedService);
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return socialList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemSocialBinding binding;

        public ViewHolder(ItemSocialBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.tvSkill.setOnClickListener(view1 -> {
                selectedService = getAdapterPosition();
                notifyDataSetChanged();
            });
        }
    }
}  