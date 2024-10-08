package com.nojom.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.nojom.databinding.ItemProfileProductBinding;
import com.nojom.model.GetProduct.Data;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.List;

public class ProfileProductsAdapter extends RecyclerView.Adapter<ProfileProductsAdapter.SimpleViewHolder> {

    private BaseActivity context;
    public String path;
    private List<Data> paymentList;
//    private OnClickListener onClickListener;

    public Data getData(int pos) {
        return paymentList.get(pos);
    }

    public ProfileProductsAdapter(BaseActivity context) {
        this.context = context;
//        onClickListener = listener;
//        this.onClickPlatformListener = updatelistener;
    }

    public void doRefresh(List<Data> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }

    public void doRefresh(String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemProfileProductBinding itemAccountBinding =
                ItemProfileProductBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        Data item = paymentList.get(position);

        holder.binding.txtName.setText(item.title);
        holder.binding.txtPrice.setText(String.format("%s %s", context.formatValue(item.price), context.getCurrency().equals("SAR") ? context.getString(R.string.sar) : context.getString(R.string.dollar)));

        Glide.with(context).load(path + item.filename).placeholder(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.binding.imgProfile);

    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemProfileProductBinding binding;

        SimpleViewHolder(ItemProfileProductBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (context.language.equals("ar")) {
                context.setArFont(binding.txtName, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtPrice, Constants.FONT_AR_REGULAR);
            }
            itemView.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path + paymentList.get(getAdapterPosition()).filename));
                context.startActivity(intent);
            });

        }
    }

    public interface OnClickListener {
        void onClickShowProduct(Data companies, int pos);

        void onClickMenuProduct(Data companies, int pos, View view, int loc);
    }

}
