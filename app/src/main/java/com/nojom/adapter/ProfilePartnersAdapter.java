package com.nojom.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ItemAgentCompanyBinding;
import com.nojom.databinding.ItemProfilePartnersBinding;
import com.nojom.model.GetAgentCompanies;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.ReOrderCompanyMoveCallback;

import java.util.Collections;
import java.util.List;

public class ProfilePartnersAdapter extends RecyclerView.Adapter<ProfilePartnersAdapter.SimpleViewHolder> {

    private BaseActivity context;
    public String path;
    private List<GetAgentCompanies.Data> paymentList;
//    private OnClickListener onClickListener;

    public GetAgentCompanies.Data getData(int pos) {
        return paymentList.get(pos);
    }

    public ProfilePartnersAdapter(BaseActivity context) {
        this.context = context;
//        onClickListener = listener;
//        this.onClickPlatformListener = updatelistener;
    }

    public void doRefresh(List<GetAgentCompanies.Data> paymentList) {
        this.paymentList = paymentList;
//        notifyDataSetChanged();
    }

    public void doRefresh(String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProfilePartnersBinding itemAccountBinding = ItemProfilePartnersBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        GetAgentCompanies.Data item = paymentList.get(position);

        holder.binding.txtTitle.setText(item.getName(context.language));
        if (!TextUtils.isEmpty(item.code)) {
            holder.binding.relCode.setVisibility(View.VISIBLE);
            holder.binding.txtCode.setText(item.code);
        } else {
            holder.binding.relCode.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(item.filename)) {
            Glide.with(holder.binding.imgProfile.getContext()).load(path + item.filename)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.binding.imgProfile);
        } else {
            holder.binding.imgProfile.setImageResource(R.mipmap.ic_launcher_round);
        }

    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemProfilePartnersBinding binding;

        SimpleViewHolder(ItemProfilePartnersBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (context.language.equals("ar")) {
                context.setArFont(binding.txtTitle, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtFollowerCount, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtCode, Constants.FONT_AR_MEDIUM);
            }
            binding.txtFollowerCount.setText(context.getString(R.string.download_app));
            binding.txtFollowerCount.setOnClickListener(v -> {
                context.viewFile(paymentList.get(getAdapterPosition()).link);
            });

            binding.imgArrow.setOnClickListener(view -> {
                copyMsg(paymentList.get(getAdapterPosition()).code + "");
            });
        }
    }

    private void copyMsg(String msg) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied", msg);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            context.toastMessage(context.getString(R.string.copy));
        }
    }

    public interface OnClickListener {
        void onClickShow(GetAgentCompanies.Data companies, int pos);

        void onClickMenu(GetAgentCompanies.Data companies, int pos, View view, int loc);
    }
}
