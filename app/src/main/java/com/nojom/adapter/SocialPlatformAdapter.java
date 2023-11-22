package com.nojom.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.nojom.databinding.ItemSocialMediaIconBinding;
import com.nojom.model.SocialMedia;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.List;
import java.util.Objects;

public class SocialPlatformAdapter extends RecyclerView.Adapter<SocialPlatformAdapter.SimpleViewHolder> {

    private List<SocialMedia.SocialPlatform> mDatasetFiltered;
    private BaseActivity context;
    private OnClickPlatformListener onClickPlatformListener;
    private SocialMedia category;

    public interface OnClickPlatformListener {
        void onClickPlatform(SocialPlatformResponse.Data platform);
    }

    public SocialPlatformAdapter(BaseActivity context, List<SocialMedia.SocialPlatform> objects, OnClickPlatformListener listener, SocialMedia cat) {
        this.mDatasetFiltered = objects;
        this.context = context;
        this.category = cat;
        this.onClickPlatformListener = listener;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSocialMediaIconBinding fullBinding = ItemSocialMediaIconBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            SocialMedia.SocialPlatform item = mDatasetFiltered.get(position);
            holder.binding.tvTitle.setText(item.socialPlatTitle);

            Glide.with(holder.binding.imgIcon.getContext()).load(item.socialPlatformUrl).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.binding.imgIcon);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return 8/*mDatasetFiltered != null ? mDatasetFiltered.size() : 0*/;
    }

    public List<SocialMedia.SocialPlatform> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSocialMediaIconBinding binding;

        public SimpleViewHolder(ItemSocialMediaIconBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.getRoot().setOnClickListener(v -> enterLinkDialog(mDatasetFiltered.get(getAdapterPosition())));
        }
    }

//    public SocialPlatformResponse.Data getSelectedCategory() {
//        for (SocialPlatformResponse.Data data : mDatasetFiltered) {
//            if (data.isSelected) {
//                return data;
//            }
//        }
//        return null;
//    }


    private void enterLinkDialog(SocialMedia.SocialPlatform socialPlatform) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_add_platform_link);
        dialog.setCancelable(true);
        RelativeLayout rlSave = dialog.findViewById(R.id.rel_save);
        TextView txtLink = dialog.findViewById(R.id.txt_link);
        TextView txtTitle = dialog.findViewById(R.id.txt_title);
        EditText etUsername = dialog.findViewById(R.id.et_username);
        ImageView imgPlatform = dialog.findViewById(R.id.img_platform);

        ImageView imgClose = dialog.findViewById(R.id.img_close);
        imgClose.setOnClickListener(v -> dialog.dismiss());

        txtTitle.setText(String.format("Add %s", socialPlatform.socialPlatTitle));
        etUsername.setHint(String.format("Add %s username", socialPlatform.socialPlatTitle));

        Glide.with(imgPlatform.getContext()).load(socialPlatform.socialPlatformUrl).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(imgPlatform);

        String link = "https://www." + socialPlatform.socialPlatTitle.toLowerCase() + ".com/";
        txtLink.setText(String.format("%s", link));
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String flink = (String.format("%s%s", link, s));
                txtLink.setText(Utils.getColorString(context, Html.fromHtml(flink).toString(), s.toString(), R.color.red));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rlSave.setOnClickListener(v -> {
            dialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

}
