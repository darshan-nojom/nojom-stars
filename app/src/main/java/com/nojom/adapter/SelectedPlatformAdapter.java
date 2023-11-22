package com.nojom.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
import com.nojom.databinding.ItemSocialMediaSelectBinding;
import com.nojom.model.SocialMedia;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.ItemMoveCallback;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SelectedPlatformAdapter extends RecyclerView.Adapter<SelectedPlatformAdapter.SimpleViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    private List<SocialMedia> mDatasetFiltered;
    private BaseActivity context;
    private OnClickPlatformListener onClickPlatformListener;
    private SocialMedia category;

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDatasetFiltered, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDatasetFiltered, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(SimpleViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(SimpleViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    public interface OnClickPlatformListener {
        void onClickPlatform(SocialPlatformResponse.Data platform);
    }

    public SelectedPlatformAdapter(BaseActivity context, ArrayList<SocialMedia> objects, OnClickPlatformListener listener) {
        this.mDatasetFiltered = objects;
        this.context = context;
        this.onClickPlatformListener = listener;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSocialMediaSelectBinding fullBinding = ItemSocialMediaSelectBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            SocialMedia item = mDatasetFiltered.get(position);

            Glide.with(holder.binding.imgIcon.getContext()).load(item.socialPlatformList.get(position)).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
        return 5/*mDatasetFiltered != null ? mDatasetFiltered.size() : 0*/;
    }

    public List<SocialMedia> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSocialMediaSelectBinding binding;

        public SimpleViewHolder(ItemSocialMediaSelectBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.getRoot().setOnClickListener(v -> {
                enterLinkDialog(mDatasetFiltered.get(getAdapterPosition()));
            });
        }
    }

    private void enterLinkDialog(SocialMedia socialPlatform) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_add_platform_link);
        dialog.setCancelable(true);
        RelativeLayout rlSave = dialog.findViewById(R.id.rel_save);
        RelativeLayout rlDelete = dialog.findViewById(R.id.rel_delete);
        TextView txtLink = dialog.findViewById(R.id.txt_link);
        TextView txtTitle = dialog.findViewById(R.id.txt_title);
        EditText etUsername = dialog.findViewById(R.id.et_username);
        ImageView imgPlatform = dialog.findViewById(R.id.img_platform);

        ImageView imgClose = dialog.findViewById(R.id.img_close);
        imgClose.setOnClickListener(v -> dialog.dismiss());

        rlDelete.setVisibility(View.VISIBLE);
        txtTitle.setText(String.format("Edit %s", socialPlatform.title));
        etUsername.setHint(String.format("Add %s username", socialPlatform.title));
        etUsername.setText(String.format("%s", "test"));

        Glide.with(imgPlatform.getContext()).load(socialPlatform.socialPlatformList.get(0)).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(imgPlatform);

        String link = "https://www." + socialPlatform.title.toLowerCase() + ".com/";
        txtLink.setText(String.format("%s", link));

        String flink = (String.format("%s%s", link, "test"));
        txtLink.setText(Utils.getColorString(context, Html.fromHtml(flink).toString(), "test", R.color.red));
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

        rlDelete.setOnClickListener(v -> {
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
