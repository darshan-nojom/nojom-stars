package com.nojom.adapter;

import static com.nojom.util.Constants.API_DELETE_PLATFORM;
import static com.nojom.util.Constants.API_EDIT_PLATFORM;
import static com.nojom.util.Constants.API_SAVE_PLATFORM;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.nojom.api.APIRequest;
import com.nojom.databinding.ItemSocialMediaSelectBinding;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.SocialMedia;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.ItemMoveCallback;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SelectedPlatformAdapter extends RecyclerView.Adapter<SelectedPlatformAdapter.SimpleViewHolder> implements ItemMoveCallback.ItemTouchHelperContract, APIRequest.APIRequestListener {

    private List<ConnectedSocialMedia.Data> mDatasetFiltered;
    private BaseActivity context;
    private UpdateSwipeListener onClickPlatformListener;
    private ConnectedSocialMedia.Data category;
    private boolean isDropDone = true;

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
        Log.e("onRowMoved", " -- From - " + fromPosition + "  --- To - " + toPosition);
        isDropDone = false;
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onActionDone() {
//        for (ConnectedSocialMedia.Data val : mDatasetFiltered) {
//            Log.e("onActionDone", " -- " + val.username);
//        }
        if (onClickPlatformListener != null && !isDropDone) {
            onClickPlatformListener.onSwipeSuccess(mDatasetFiltered);
            isDropDone = true;
        }
    }

    @Override
    public void onRowSelected(SimpleViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(SimpleViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (urlEndPoint.equals(API_EDIT_PLATFORM)) {
            if (onClickPlatformListener != null) {
                onClickPlatformListener.platformEdited(decryptedData, true, msg);
            }
        } else if (urlEndPoint.equals(API_DELETE_PLATFORM)) {
            if (onClickPlatformListener != null) {
                onClickPlatformListener.platformDeleted(decryptedData, true, msg);
            }
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equals(API_EDIT_PLATFORM)) {
            if (tvSave != null && pb != null) {
                tvSave.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
            }
            if (onClickPlatformListener != null) {
                onClickPlatformListener.platformEdited(null, false, message);
            }
        } else if (urlEndPoint.equals(API_DELETE_PLATFORM)) {
            if (tvDelete != null && pbDelete != null) {
                tvDelete.setVisibility(View.VISIBLE);
                pbDelete.setVisibility(View.GONE);
            }
            if (onClickPlatformListener != null) {
                onClickPlatformListener.platformDeleted(null, false, message);
            }
        }
    }

    boolean isPlatformView;

    public void isView(boolean isView) {
        isPlatformView = isView;
    }

    public interface UpdateSwipeListener {
        void onSwipeSuccess(List<ConnectedSocialMedia.Data> mDatasetFiltered);

        void platformEdited(String response, boolean isSuccess, String message);

        void platformDeleted(String response, boolean isSuccess, String message);
    }

    public SelectedPlatformAdapter(BaseActivity context, ArrayList<ConnectedSocialMedia.Data> objects, UpdateSwipeListener listener) {
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
            ConnectedSocialMedia.Data item = mDatasetFiltered.get(position);

            Glide.with(holder.binding.imgIcon.getContext()).load(item.filename).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

    public List<ConnectedSocialMedia.Data> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSocialMediaSelectBinding binding;

        public SimpleViewHolder(ItemSocialMediaSelectBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (isPlatformView) {
                binding.tvTitle.setVisibility(View.GONE);
            }
            binding.getRoot().setOnClickListener(v -> {
                if (isPlatformView) {
                    String link = (mDatasetFiltered.get(getAbsoluteAdapterPosition()).web_url != null)
                            ? mDatasetFiltered.get(getAbsoluteAdapterPosition()).web_url.toLowerCase()
                            : "https://www." + mDatasetFiltered.get(getAbsoluteAdapterPosition()).name.toLowerCase() + ".com/";

                    String flink = (String.format("%s%s", link, mDatasetFiltered.get(getAbsoluteAdapterPosition()).username));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(flink));
                    context.startActivity(intent);
                } else {
                    enterLinkDialog(mDatasetFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    Dialog dialog;
    ProgressBar pb, pbDelete;
    TextView tvSave, tvDelete;

    private void enterLinkDialog(ConnectedSocialMedia.Data socialPlatform) {
        dialog = new Dialog(context, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_add_platform_link);
        dialog.setCancelable(true);
        RelativeLayout rlSave = dialog.findViewById(R.id.rel_save);
        RelativeLayout rlDelete = dialog.findViewById(R.id.rel_delete);
        TextView txtLink = dialog.findViewById(R.id.txt_link);
        TextView txtTitle = dialog.findViewById(R.id.txt_title);
        EditText etUsername = dialog.findViewById(R.id.et_username);
        EditText etCount = dialog.findViewById(R.id.et_count);
        ImageView imgPlatform = dialog.findViewById(R.id.img_platform);

        ImageView imgClose = dialog.findViewById(R.id.img_close);
        tvSave = dialog.findViewById(R.id.tv_save);
        tvDelete = dialog.findViewById(R.id.tv_delete);
        pb = dialog.findViewById(R.id.pb);
        pbDelete = dialog.findViewById(R.id.pb_delete);

        imgClose.setOnClickListener(v -> dialog.dismiss());

        rlDelete.setVisibility(View.VISIBLE);
        if (socialPlatform.name.contains("Whatsapp") || socialPlatform.name.contains("Telegram")) {
            etUsername.setHint(String.format(context.getString(R.string.add_s_number), socialPlatform.getName(context.language)));
            etUsername.setInputType(InputType.TYPE_CLASS_PHONE);

        } else {
            etUsername.setHint(String.format(context.getString(R.string.add_s_username), socialPlatform.getName(context.language)));
        }
        txtTitle.setText(String.format(context.getString(R.string.edit_s), socialPlatform.getName(context.language)));
        etUsername.setText(String.format("%s", socialPlatform.username));

        etUsername.setSelection(etUsername.getText().length());

        Glide.with(imgPlatform.getContext()).load(socialPlatform.filename).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(imgPlatform);

        String link = (socialPlatform.web_url != null)
                ? socialPlatform.web_url.toLowerCase()
                : "https://www." + socialPlatform.name.toLowerCase() + ".com/";
        txtLink.setText(String.format("%s", link));

        String flink = (String.format("%s%s", link, socialPlatform.username));
        txtLink.setText(Utils.getColorString(context, Html.fromHtml(flink).toString(), socialPlatform.username, R.color.red));
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String last = null;
                if (s != null) {
                    if (s.toString().startsWith("https://")) {
                        String[] parts = s.toString().split("/");
                        // Get the last part after the last "/"
                        last = parts[parts.length - 1];
                    } else {
                        last = s.toString();
                    }
                }
//                String flink = (String.format("%s%s", link, last));
//                txtLink.setText(Utils.getColorString(context, Html.fromHtml(flink).toString(), last, R.color.red));

                // Create a SpannableStringBuilder to hold the formatted text
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(link);

                // Create a span for the last part (red color)
                ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
                builder.append(last); // Append the last part
                if (last != null) {
                    builder.setSpan(redSpan, builder.length() - last.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                // Find and set the TextView
                txtLink.setText(builder);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rlSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etUsername.getText().toString().trim())) {
                return;
            }
            int count = 0;
//            if (!TextUtils.isEmpty(etCount.getText().toString())) {
//                count = Integer.parseInt(etCount.getText().toString());
//            }
            tvSave.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
            editPlatform(etUsername.getText().toString(), socialPlatform.id, 0, count);
        });

        rlDelete.setOnClickListener(v -> {
            tvSave.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
            deletePlatform(etUsername.getText().toString(), socialPlatform.id, 0);
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

    private void editPlatform(String username, int pId, int pTypeId, int count) {
        CommonRequest.EditSocialMedia deleteSurveyImage = new CommonRequest.EditSocialMedia();
        deleteSurveyImage.setSocial_platform_id(pId);
        deleteSurveyImage.setUsername(username);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(context, API_EDIT_PLATFORM, deleteSurveyImage.toString(), true, this);
    }

    private void deletePlatform(String username, int pId, int pTypeId) {
        CommonRequest.DeleteSocialMedia deleteSurveyImage = new CommonRequest.DeleteSocialMedia();
        deleteSurveyImage.setSocial_platform_id(pId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(context, API_DELETE_PLATFORM, deleteSurveyImage.toString(), true, this);
    }

}
