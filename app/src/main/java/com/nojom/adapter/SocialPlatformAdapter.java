package com.nojom.adapter;

import static com.nojom.util.Constants.API_SAVE_PLATFORM;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.databinding.DialogAddSocialmediaBinding;
import com.nojom.databinding.DialogDiscardBinding;
import com.nojom.databinding.ItemSocialMediaIconBinding;
import com.nojom.model.SocialMediaResponse;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.SocialMediaActivity;
import com.nojom.util.Constants;
import com.nojom.util.NumberTextWatcherForThousand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SocialPlatformAdapter extends RecyclerView.Adapter<SocialPlatformAdapter.SimpleViewHolder> implements APIRequest.APIRequestListener {

    private List<SocialMediaResponse.SocialPlatform> mDatasetFiltered;
    public static SocialMediaActivity context;
    private PlatformAddedListener onClickPlatformListener;
    private SocialMediaResponse.Data category;

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equals(API_SAVE_PLATFORM)) {
            if (dialogAddCompany != null) {
                dialogAddCompany.dismiss();
            }
            onClickPlatformListener.platformAdded(decryptedData, true, msg);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equals(API_SAVE_PLATFORM)) {
            if (dialogAddCompany != null && dialogAddSocialmediaBinding != null) {
                dialogAddSocialmediaBinding.tvSend.setVisibility(View.VISIBLE);
                dialogAddSocialmediaBinding.progressBar.setVisibility(View.GONE);
            }
            onClickPlatformListener.platformAdded(null, false, message);
        }
    }

    boolean isFromSignupStep;

    public void setFromSignup(boolean isFromSignupStep) {
        this.isFromSignupStep = isFromSignupStep;
    }

    public interface PlatformAddedListener {
        void platformAdded(String response, boolean isSuccess, String message);

        void platformAddedSignupTime(SocialMediaResponse.Data mainCat, SocialMediaResponse.SocialPlatform platform);
    }

    public SocialPlatformAdapter(SocialMediaActivity context, ArrayList<SocialMediaResponse.SocialPlatform> objects, PlatformAddedListener listener, SocialMediaResponse.Data cat) {
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
            SocialMediaResponse.SocialPlatform item = mDatasetFiltered.get(position);
            holder.binding.tvTitle.setText(item.getName(context.language));

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

    public List<SocialMediaResponse.SocialPlatform> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSocialMediaIconBinding binding;

        public SimpleViewHolder(ItemSocialMediaIconBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.getRoot().setOnClickListener(v -> {
//                enterLinkDialog(mDatasetFiltered.get(getAdapterPosition()));
                if (isFromSignupStep && onClickPlatformListener != null) {
                    onClickPlatformListener.platformAddedSignupTime(category, mDatasetFiltered.get(getAdapterPosition()));
                } else {
                    addMediaDialog(mDatasetFiltered.get(getAdapterPosition()));
                }
            });

        }
    }


    private Dialog dialogAddCompany, dialogDiscard;
    private DialogAddSocialmediaBinding dialogAddSocialmediaBinding;
    private DialogDiscardBinding dialogDiscardBinding;

    public void addMediaDialog(SocialMediaResponse.SocialPlatform socialPlatform) {
        dialogAddCompany = new Dialog(context, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogAddCompany.setTitle(null);
        dialogAddSocialmediaBinding = DataBindingUtil.inflate(LayoutInflater.from(context.getApplicationContext()), R.layout.dialog_add_socialmedia, null, false);
        dialogAddSocialmediaBinding.title.setText(context.getString(R.string.add_social_media));
        dialogAddSocialmediaBinding.swShow.setText(context.getString(R.string.show_followers_for_all_user));
        dialogAddSocialmediaBinding.tvSend.setText(context.getString(R.string.save));
        dialogAddSocialmediaBinding.defaultTextInputLayout.setHint(context.getString(R.string.username));
        dialogAddSocialmediaBinding.defaultTextInputLayoutTime.setHint(context.getString(R.string.num_of_followers_optional));

        dialogAddCompany.setContentView(dialogAddSocialmediaBinding.getRoot());
        dialogAddCompany.setCancelable(true);

        dialogAddSocialmediaBinding.etTime.addTextChangedListener(new NumberTextWatcherForThousand(dialogAddSocialmediaBinding.etTime));

        if (socialPlatform.name.contains("Whatsapp") || socialPlatform.name.contains("Telegram")) {
            dialogAddSocialmediaBinding.relUname.setVisibility(View.GONE);
            dialogAddSocialmediaBinding.linContactNo.setVisibility(View.VISIBLE);
//            dialogAddSocialmediaBinding.defaultTextInputLayout.setHint(String.format(context.getString(R.string.add_s_number), socialPlatform.getName(context.language)));
//            dialogAddSocialmediaBinding.etName.setInputType(InputType.TYPE_CLASS_PHONE);


            dialogAddSocialmediaBinding.ccp.registerCarrierNumberEditText(dialogAddSocialmediaBinding.etContact);
            dialogAddSocialmediaBinding.ccp.setOnCountryChangeListener(() -> {
                dialogAddSocialmediaBinding.etContact.setText("");
                dialogAddSocialmediaBinding.tvCode.setText("(" + dialogAddSocialmediaBinding.ccp.getSelectedCountryCodeWithPlus() + ")");
                dialogAddSocialmediaBinding.ccp.setTag(dialogAddSocialmediaBinding.ccp.getSelectedCountryCodeWithPlus());
            });

            dialogAddSocialmediaBinding.etContact.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(s)) {
                        dialogAddSocialmediaBinding.txtLink.setVisibility(View.GONE);
                        DrawableCompat.setTint(dialogAddSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(context, R.color.C_E5E5EA));
                        dialogAddSocialmediaBinding.tvSend.setTextColor(context.getResources().getColor(R.color.C_020814));
                    } else {
                        dialogAddSocialmediaBinding.txtLink.setVisibility(View.VISIBLE);
                        dialogAddSocialmediaBinding.txtLink.setText(socialPlatform.web_url + "" + s);
                        DrawableCompat.setTint(dialogAddSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(context, R.color.black));
                        dialogAddSocialmediaBinding.tvSend.setTextColor(context.getResources().getColor(R.color.white));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else {
            dialogAddSocialmediaBinding.relUname.setVisibility(View.VISIBLE);
            dialogAddSocialmediaBinding.linContactNo.setVisibility(View.GONE);
        }

        dialogAddSocialmediaBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    dialogAddSocialmediaBinding.txtLink.setVisibility(View.GONE);
                    DrawableCompat.setTint(dialogAddSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(context, R.color.c_AEAEB2));
                    dialogAddSocialmediaBinding.tvSend.setTextColor(context.getResources().getColor(R.color.C_020814));
                } else {
                    dialogAddSocialmediaBinding.txtLink.setVisibility(View.VISIBLE);
                    if (s.toString().startsWith("http")) {
                        dialogAddSocialmediaBinding.txtLink.setText(s);
                    } else {
                        dialogAddSocialmediaBinding.txtLink.setText(socialPlatform.web_url + "" + s);
                    }

                    DrawableCompat.setTint(dialogAddSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(context, R.color.black));
                    dialogAddSocialmediaBinding.tvSend.setTextColor(context.getResources().getColor(R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Glide.with(context).load(socialPlatform.filename).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(dialogAddSocialmediaBinding.imgPlatform);

        dialogAddSocialmediaBinding.tvCancel.setOnClickListener(v -> {
            /*if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etName.getText().toString().trim())) {
                dialogAddCompany.dismiss();
                return;
            }*/
            discardChangesDialog(dialogAddCompany, socialPlatform);
        });

        dialogAddSocialmediaBinding.relSave.setOnClickListener(v -> {
            String uname;
            if (socialPlatform.name.contains("Whatsapp") || socialPlatform.name.contains("Telegram")) {
                if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etContact.getText().toString().trim())) {
                    return;
                }
                uname = dialogAddSocialmediaBinding.ccp.getSelectedCountryCodeWithPlus() + dialogAddSocialmediaBinding.etContact.getText().toString();
            } else {
                if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etName.getText().toString().trim())) {
                    return;
                }
                uname = dialogAddSocialmediaBinding.etName.getText().toString();
            }


            addPlatform(uname, dialogAddSocialmediaBinding.etTime.getText().toString(), socialPlatform.id, category.id, dialogAddSocialmediaBinding.swShow.isChecked() ? 1 : 0);

        });
//
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogAddCompany.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogAddCompany.show();
        dialogAddCompany.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddCompany.getWindow().setAttributes(lp);
    }


    private void addPlatform(String username, String count, int pId, int pTypeId, int fStatus) {
        CommonRequest.AddSocialMedia deleteSurveyImage = new CommonRequest.AddSocialMedia();
        deleteSurveyImage.setSocial_platform_id(pId);
        deleteSurveyImage.setSocial_platform_type_id(pTypeId);
        deleteSurveyImage.setUsername(username);
        deleteSurveyImage.setIs_public(fStatus);
        if (!TextUtils.isEmpty(count)) {
            deleteSurveyImage.setFollowers(Integer.parseInt(count.replaceAll(",", "")));
        }


        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(context, API_SAVE_PLATFORM, deleteSurveyImage.toString(), true, this);
    }

    public void discardChangesDialog(Dialog dialogMain, SocialMediaResponse.SocialPlatform data) {
        dialogDiscard = new Dialog(context, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDiscard.setTitle(null);
        dialogDiscardBinding = DataBindingUtil.inflate(LayoutInflater.from(context.getApplicationContext()), R.layout.dialog_discard, null, false);
        if (context.language.equals("ar")) {
            context.setArFont(dialogDiscardBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            context.setArFont(dialogDiscardBinding.txtDesc, Constants.FONT_AR_REGULAR);
            context.setArFont(dialogDiscardBinding.tvSend, Constants.FONT_AR_BOLD);
            context.setArFont(dialogDiscardBinding.tvCancel, Constants.FONT_AR_BOLD);
        }
        dialogDiscard.setContentView(dialogDiscardBinding.getRoot());
//        dialog.setContentView(R.layout.dialog_discard);
        dialogDiscard.setCancelable(true);
//        TextView tvSend = dialog.findViewById(R.id.tv_send);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        dialogDiscardBinding.txtTitle.setText(context.getString(R.string.save_changes));
        dialogDiscardBinding.txtDesc.setText(context.getString(R.string.would_you_like_to_save_before_exiting));
        dialogDiscardBinding.tvSend.setText(context.getString(R.string.save));
        dialogDiscardBinding.tvCancel.setText(context.getString(R.string.discard_1));


        dialogDiscardBinding.tvCancel.setOnClickListener(v -> {
            dialogDiscard.dismiss();
            dialogMain.dismiss();
        });

        dialogDiscardBinding.relSave.setOnClickListener(v -> {
//            if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etName.getText().toString().trim())) {
//                dialogDiscard.dismiss();
//                return;
//            }
            String uname;
            if (data.name.contains("Whatsapp") || data.name.contains("Telegram")) {
                if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etContact.getText().toString().trim())) {
                    dialogDiscard.dismiss();
                    return;
                }
                uname = dialogAddSocialmediaBinding.ccp.getSelectedCountryCodeWithPlus() + dialogAddSocialmediaBinding.etContact.getText().toString();
            } else {
                if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etName.getText().toString().trim())) {
                    dialogDiscard.dismiss();
                    return;
                }
                uname = dialogAddSocialmediaBinding.etName.getText().toString();
            }

            addPlatform(uname, dialogAddSocialmediaBinding.etTime.getText().toString(), data.id, category.id, dialogAddSocialmediaBinding.swShow.isChecked() ? 1 : 0);


        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDiscard.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDiscard.show();
        dialogDiscard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDiscard.getWindow().setAttributes(lp);
    }

}
