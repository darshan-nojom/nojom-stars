package com.nojom.adapter;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.nojom.databinding.ItemUnameBinding;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.ui.BaseActivity;

import java.util.List;

public class AddSocialAdapter extends RecyclerView.Adapter<AddSocialAdapter.SimpleViewHolder> {

    private BaseActivity context;
    private List<ConnectedSocialMedia.Data> paymentList;

    public AddSocialAdapter(BaseActivity context, ItemChangedListener itemChangedListener) {
        this.context = context;
        this.itemChangedListener = itemChangedListener;
    }

    private ItemChangedListener itemChangedListener;

    public interface ItemChangedListener {
        void onTextChanged(CharSequence s);
    }

    public void doRefresh(List<ConnectedSocialMedia.Data> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemUnameBinding itemAccountBinding =
                ItemUnameBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        ConnectedSocialMedia.Data item = paymentList.get(position);

        holder.binding.etPass.setText(item.username);

//        if (!TextUtils.isEmpty(item.username)) {
//            holder.binding.tvLink.setText(item.web_url + item.username);
//        } else {
//            holder.binding.tvLink.setText(item.web_url);
//        }
        holder.binding.etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    holder.binding.tvLink.setText(item.web_url);
                    holder.binding.tvLink.setVisibility(View.GONE);
                } else {
                    holder.binding.tvLink.setVisibility(View.VISIBLE);
                    holder.binding.tvLink.setText(item.web_url + charSequence);
                }
                item.username = charSequence.toString();
                itemChangedListener.onTextChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Glide.with(holder.binding.iv.getContext()).load(item.filename).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(holder.binding.iv);
    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    public ConnectedSocialMedia.Data getData(int pos) {
        return paymentList.get(pos);
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemUnameBinding binding;

        SimpleViewHolder(ItemUnameBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
//                Intent i = new Intent(context, PaymentActivity.class);
//                i.putExtra(Constants.ACCOUNT_DATA, paymentList.get(getAdapterPosition()));
//                context.startActivity(i);
            });

            binding.etPass.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
                // Allowed characters: A-Z, a-z, 0-9, and symbols
                String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_-+=<>?/{}[]|.,~`";

                for (int i = start; i < end; i++) {
                    if (!allowedChars.contains(String.valueOf(source.charAt(i)))) {
                        return ""; // Invalid character, do not accept
                    }
                }
                return null; // Accept input
            }});
        }
    }
}
