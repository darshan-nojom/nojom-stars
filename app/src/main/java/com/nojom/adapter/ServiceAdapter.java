package com.nojom.adapter;

import static com.nojom.util.NumberTextWatcherForThousand.getDecimalFormat;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ItemPlatformPriceBinding;
import com.nojom.model.SocialMediaResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.SimpleViewHolder> {

    private BaseActivity context;
    private List<SocialMediaResponse.PlatformPrice> paymentList;
    MutableLiveData<Boolean> isAnyChanges;

    public ServiceAdapter(BaseActivity context, PlatformClickListener listener, MutableLiveData<Boolean> isAnyChanges) {
        this.context = context;
        this.platformClickListener = listener;
        this.isAnyChanges = isAnyChanges;
    }

    private PlatformClickListener platformClickListener;

    public interface PlatformClickListener {
        void onPlatformClick(int pos, SocialMediaResponse.PlatformPrice data);
    }

    public void doRefresh(int pos, List<SocialMediaResponse.PlatformPrice> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public List<SocialMediaResponse.PlatformPrice> getSelectedPlatform() {
        List<SocialMediaResponse.PlatformPrice> finalList = new ArrayList<>();
        for (SocialMediaResponse.PlatformPrice plt : paymentList) {
            if (!TextUtils.isEmpty(plt.name)) {
                finalList.add(plt);
            }
        }
        return finalList;
    }

    public void itemChanged(int pos, String name, String nameAr, int id, String file) {
        paymentList.get(pos).name = name;
        paymentList.get(pos).nameAr = nameAr;
        paymentList.get(pos).id = id;
        paymentList.get(pos).file = file;
        notifyItemChanged(pos);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemPlatformPriceBinding itemAccountBinding = ItemPlatformPriceBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        SocialMediaResponse.PlatformPrice item = paymentList.get(position);
        holder.binding.tvIndex.setText("" + (holder.getAdapterPosition() + 1));
        holder.binding.etPlatform.setText(item.getName(context.language));

        if (!TextUtils.isEmpty(item.file)) {
            Glide.with(holder.binding.imgProfile.getContext()).load(item.file).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.binding.imgProfile.setVisibility(View.VISIBLE);
                    return false;
                }
            }).into(holder.binding.imgProfile);

        } else {
            holder.binding.imgProfile.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(item.price)) {
            try {
                String formattedNumber = getDecimalFormat(context.enFormatValue(Double.parseDouble(item.price.replaceAll(",", ""))));
                holder.binding.etPrice.setText(formattedNumber);
            } catch (Exception e) {
                e.printStackTrace();
                holder.binding.etPrice.setText(item.price);
            }

        } else {
            holder.binding.etPrice.setText("");
        }
        holder.binding.etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                item.price = charSequence + "";
                paymentList.get(holder.getAdapterPosition()).price = charSequence + "";
                isAnyChanges.postValue(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    holder.binding.etPrice.removeTextChangedListener(this);
                    String value = holder.binding.etPrice.getText().toString();


                    if (!value.equals("")) {

                        if (value.startsWith(".")) { //adds "0." when only "." is pressed on begining of writting
                            holder.binding.etPrice.setText("0.");
                        }
                        if (value.startsWith("0") && !value.startsWith("0.")) {
                            holder.binding.etPrice.setText(""); //Prevents "0" while starting but not "0."

                        }


                        String str = holder.binding.etPrice.getText().toString().replaceAll(",", "");
                        holder.binding.etPrice.setText(getDecimalFormat(str));
                        holder.binding.etPrice.setSelection(holder.binding.etPrice.getText().toString().length());
                    }
                    holder.binding.etPrice.addTextChangedListener(this);
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    holder.binding.etPrice.addTextChangedListener(this);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    public List<SocialMediaResponse.PlatformPrice> getData() {
        return paymentList;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemPlatformPriceBinding binding;

        SimpleViewHolder(ItemPlatformPriceBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            if (context.language.equals("ar")) {
                context.setArFont(binding.tvIndex, Constants.FONT_AR_BOLD);
                context.setArFont(binding.tvTitle, Constants.FONT_AR_BOLD);
                context.setArFont(binding.etPlatform, Constants.FONT_AR_REGULAR);
                context.setArFont(binding.etPrice, Constants.FONT_AR_REGULAR);
                context.setArFont(binding.tv1, Constants.FONT_AR_REGULAR);
            }

            itemView.getRoot().setOnClickListener(v -> {
//                Intent i = new Intent(context, PaymentActivity.class);
//                i.putExtra(Constants.ACCOUNT_DATA, paymentList.get(getAdapterPosition()));
//                context.startActivity(i);
            });

            binding.imgDelete.setOnClickListener(view -> {
                paymentList.remove(getAdapterPosition());
//                notifyItemRemoved(getAdapterPosition());
                notifyDataSetChanged();
            });

            binding.relPlatform.setOnClickListener(view -> {
                platformClickListener.onPlatformClick(getAdapterPosition(), paymentList.get(getAdapterPosition()));
            });
        }
    }
}
