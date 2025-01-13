package com.nojom.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.PlatformBinding;
import com.nojom.model.SocialMediaResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class PlatformAdapter extends RecyclerView.Adapter<PlatformAdapter.SimpleViewHolder> implements Filterable {

    private BaseActivity context;
    private List<SocialMediaResponse.SocialPlatform> paymentList;
    private List<SocialMediaResponse.SocialPlatform> paymentListFilter;

    public PlatformAdapter(BaseActivity context, PlatformListener listener) {
        this.context = context;
        this.platformClickListener = listener;
    }

    private PlatformListener platformClickListener;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    //clearSelectedDuplicate();
                    paymentList = paymentListFilter;
                } else {
                    List<SocialMediaResponse.SocialPlatform> filteredList = new ArrayList<>();
                    for (SocialMediaResponse.SocialPlatform row : paymentListFilter) {
                        String rowText = row.name.toLowerCase();
                        if (!TextUtils.isEmpty(rowText)) {
                            if (rowText.contains(charString.toLowerCase())/* || row.name.equalsIgnoreCase("Other")*/) {
                                filteredList.add(row);
                            }
                        }
                    }

                    paymentList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = paymentList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                paymentList = (List<SocialMediaResponse.SocialPlatform>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface PlatformListener {
        void onPlatformSelect(int pos, SocialMediaResponse.SocialPlatform data);
    }

    public void doRefresh(List<SocialMediaResponse.SocialPlatform> paymentList) {
        this.paymentList = paymentList;
        this.paymentListFilter = paymentList;
        notifyDataSetChanged();
    }

    public void itemChanged(int pos, SocialMediaResponse.PlatformPrice data) {
        paymentList.get(pos).name = data.name;
        paymentList.get(pos).nameAr = data.nameAr;
        paymentList.get(pos).id = data.id;
        notifyItemChanged(pos);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        PlatformBinding itemAccountBinding =
                PlatformBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        SocialMediaResponse.SocialPlatform item = paymentList.get(position);
        holder.binding.etPlatform.setText(item.getName(context.language));
        Glide.with(holder.binding.imgProfile.getContext()).load(item.filename).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(holder.binding.imgProfile);

        if (item.isSelect) {
            holder.binding.etPlatform.setTextColor(context.getResources().getColor(R.color.C_3C3C43));
            holder.binding.imgSelect.setImageResource(R.drawable.radio_button_active);
//            holder.binding.relPlatform.setBackground(context.getResources().getDrawable(R.drawable.black_border_5));
        } else {
            holder.binding.etPlatform.setTextColor(context.getResources().getColor(R.color.C_020814));
            holder.binding.imgSelect.setImageResource(R.drawable.circle_uncheck);
//            holder.binding.relPlatform.setBackground(context.getResources().getDrawable(R.drawable.gray_l_border_6));
        }
    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        PlatformBinding binding;

        SimpleViewHolder(PlatformBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (context.language.equals("ar")) {
                context.setArFont(binding.etPlatform, Constants.FONT_AR_REGULAR);
            }
            binding.relPlatform.setOnClickListener(view -> {
                if (!paymentList.get(getAdapterPosition()).isSelect) {
                    platformClickListener.onPlatformSelect(getAdapterPosition(), paymentList.get(getAdapterPosition()));
                }
            });
        }
    }
}
