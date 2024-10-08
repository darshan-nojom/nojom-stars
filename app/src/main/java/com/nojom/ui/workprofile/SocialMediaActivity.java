package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.adapter.SelectedPlatformAdapter;
import com.nojom.adapter.SocialMediaAdapter;
import com.nojom.adapter.SocialPlatformAdapter;
import com.nojom.databinding.ActivitySocialMediaBinding;
import com.nojom.databinding.DialogAddSocialmediaBinding;
import com.nojom.databinding.DialogCustomSocialmediaBinding;
import com.nojom.databinding.DialogDeleteBinding;
import com.nojom.databinding.DialogDiscardBinding;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.SocialMedia;
import com.nojom.model.SocialMediaResponse;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.ItemMoveCallback;
import com.nojom.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SocialMediaActivity extends BaseActivity implements SocialMediaAdapter.OnClickPlatformListener, SocialPlatformAdapter.PlatformAddedListener, SelectedPlatformAdapter.UpdateSwipeListener {
    private ActivitySocialMediaBinding binding;
    private SocialPlatformResponse.Data selectedPlatform = null;
    private SocialMediaAdapter mAdapter;
    private ArrayList<SocialMediaResponse.Data> socialList;
    private MyPlatformActivityVM nameActivityVM;
    List<SocialMedia.SocialPlatform> socialPlatformList;
    private MutableLiveData<Boolean> isAnyChanges = new MutableLiveData<>();
    boolean isFromSignupStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_social_media);
        nameActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);
        nameActivityVM.getInfluencerPlatform(this);
//        nameActivityVM.getConnectedPlatform(this);
//        socialMediaList = new ArrayList<>();
        socialPlatformList = new ArrayList<>();
//        nameActivityVM.getSocialPlatforms(this);
        isFromSignupStep = getIntent().getBooleanExtra("isFrom", false);
        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_REGULAR);
            setArFont(binding.etSearch, Constants.FONT_AR_REGULAR);
            setArFont(binding.tvAddLink, Constants.FONT_AR_MEDIUM);
        }
        setUI();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setUI() {
//        binding.progress.tvTitle.setText(getString(R.string.social_media));
        binding.progress.imgBack.setImageResource(R.drawable.back);
        binding.progress.imgBack.setOnClickListener(v -> onBackPressed());
        binding.tvAddLink.setOnClickListener(v -> {
//            showAddWebsiteDialog();
            addMediaDialog();
        });
        binding.rvCategory.setLayoutManager(new LinearLayoutManager(this));

        nameActivityVM.getSocialMediaDataList().observe(this, data -> {
            for (SocialMediaResponse.Data data1 : data) {//remove whatsapp from list
                if (data1.id == 2) {//business
                    for (SocialMediaResponse.SocialPlatform plt : data1.social_platforms) {
                        if (plt.id == 12) {//whatsapp
                            data1.social_platforms.remove(plt);
                            break;
                        }
                    }
                }
            }

            socialList = data;

            if (socialList.size() > 0) {
                setAdapter(socialList);
            }
        });

        nameActivityVM.getConnectedMediaDataList().observe(this, data -> {

            if (data != null) {
                setConnectedMediaAdapter(data);
            }
        });

        nameActivityVM.getIsHideProgress().observe(this, integer -> {
            binding.progressBar.setVisibility(View.GONE);
        });

        nameActivityVM.getIsShowProgress().observe(this, integer -> {
            binding.progressBar.setVisibility(View.VISIBLE);
        });

        nameActivityVM.getSaveCompanyProgress().observe(this, integer -> {
            if (dialogAddLink != null && dialogAddLink.isShowing()) {
                dialogAddLink.dismiss();
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAdapter != null) {
                    mAdapter.getFilter().filter("" + s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        isAnyChanges.observe(this, aBoolean -> {
            if (customSocialmediaBinding != null && dialogAddLink != null) {
                if (isValid()) {
                    DrawableCompat.setTint(customSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(SocialMediaActivity.this, R.color.black));
                    customSocialmediaBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
                } else {
                    DrawableCompat.setTint(customSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(SocialMediaActivity.this, R.color.C_E5E5EA));
                    customSocialmediaBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
                }
            }
        });

    }

    private void setAdapter(ArrayList<SocialMediaResponse.Data> data) {

        mAdapter = new SocialMediaAdapter(this, socialList, this);
        mAdapter.setListener(this);
        mAdapter.setFromSignup(isFromSignupStep);
        binding.rvCategory.setAdapter(mAdapter);

    }

    ArrayList<ConnectedSocialMedia.Data> connectedList;

    private void setConnectedMediaAdapter(ArrayList<ConnectedSocialMedia.Data> data) {
        connectedList = data;
        SelectedPlatformAdapter mAdapter = new SelectedPlatformAdapter(this, data, this);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rvSelection);
        binding.rvSelection.setAdapter(mAdapter);
    }

    @Override
    public void onClickPlatform(SocialPlatformResponse.Data platform) {
        selectedPlatform = platform;
    }

    @Override
    public void platformAdded(String responseData, boolean isSuccess, String message) {
        toastMessage(message);
        /*if (isSuccess) {
            nameActivityVM.getConnectedPlatform(this);
        }*/
    }

    @Override
    public void platformAddedSignupTime(SocialMediaResponse.Data mainCat, SocialMediaResponse.SocialPlatform platform) {
        Intent intent = new Intent();
        intent.putExtra("data", mainCat);
        intent.putExtra("plat", platform);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSwipeSuccess(List<ConnectedSocialMedia.Data> mDatasetFiltered) {
        JSONArray jsonArray = new JSONArray();
        JSONObject mainObj = new JSONObject();
        for (int i = 0; i < mDatasetFiltered.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", mDatasetFiltered.get(i).id);
                jsonObject.put("display_order", i + 1);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            mainObj.put("reorder", jsonArray);
            nameActivityVM.reOrderMedia(this, jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void platformEdited(String response, boolean isSuccess, String message) {
        toastMessage(message);
        if (isFromSignupStep) {
            finish();
        } else if (isSuccess) {
            nameActivityVM.getConnectedPlatform(this);
        }
    }

    @Override
    public void platformDeleted(String response, boolean isSuccess, String message) {
        toastMessage(message);
        if (isSuccess) {
            nameActivityVM.getConnectedPlatform(this);
        }
    }

    private DialogCustomSocialmediaBinding customSocialmediaBinding;
    private Dialog dialogAddLink;


    public void addMediaDialog() {
        dialogAddLink = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogAddLink.setTitle(null);
        customSocialmediaBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_custom_socialmedia, null, false);

        if (language.equals("ar")) {
            setArFont(customSocialmediaBinding.title, Constants.FONT_AR_BOLD);
            setArFont(customSocialmediaBinding.txtPhTitle, Constants.FONT_AR_REGULAR);
            setArFont(customSocialmediaBinding.desc2, Constants.FONT_AR_REGULAR);
            setArFont(customSocialmediaBinding.etName, Constants.FONT_AR_REGULAR);
            setArFont(customSocialmediaBinding.etLink, Constants.FONT_AR_REGULAR);
            setArFont(customSocialmediaBinding.txtLink, Constants.FONT_AR_REGULAR);
            setArFont(customSocialmediaBinding.tvSend, Constants.FONT_AR_REGULAR);
        }

        customSocialmediaBinding.title.setText(getString(R.string.custom_link));

        customSocialmediaBinding.txtPhTitle.setText(getString(R.string.upload_image_option));
        customSocialmediaBinding.desc2.setText(getString(R.string.use_a_size_that_s_at_least_480_x_480_pixels));
        customSocialmediaBinding.tvSend.setText(getString(R.string.save));
        customSocialmediaBinding.defaultTextInputLayout.setHint(getString(R.string.name_of_platform));
        customSocialmediaBinding.defaultTextInputLayoutTime.setHint(getString(R.string.enter_link));

        dialogAddLink.setContentView(customSocialmediaBinding.getRoot());
        dialogAddLink.setCancelable(true);

        DrawableCompat.setTint(customSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(SocialMediaActivity.this, R.color.C_E5E5EA));
        customSocialmediaBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));

        customSocialmediaBinding.imgAdd.setOnClickListener(v -> {
            Intent mediaIntent = new Intent(Intent.ACTION_PICK);
            mediaIntent.setType("image/*");
            startActivityForResult(mediaIntent, 1213);
        });
        customSocialmediaBinding.imgCancel.setOnClickListener(v -> {
            customSocialmediaBinding.imgCancel.setVisibility(View.GONE);
            customSocialmediaBinding.imgAdd.setVisibility(View.VISIBLE);
            customSocialmediaBinding.roundedImage.setImageResource(R.drawable.ic_portfolio_cloud);
            customSocialmediaBinding.roundedImage.setTag("");
        });


        customSocialmediaBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isAnyChanges.postValue(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        customSocialmediaBinding.etLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    customSocialmediaBinding.txtLink.setVisibility(View.GONE);
                } else {
                    if (!isValidUrl(customSocialmediaBinding.etLink.getText().toString().trim())) {
                        customSocialmediaBinding.txtLink.setVisibility(View.VISIBLE);
                        customSocialmediaBinding.txtLink.setText(getString(R.string.oops_the_link_you_entered_seems_to_be_incorrect_please_check_the_url_and_try_again_if_you_need_assistance_feel_free_to_contact_our_support_team_oops_the_link_you_entered_seems_to_be_incorrect_please_check_the_url_and_try_again_if_you_need_assistance_feel_free_to_contact_our_support_team));
                    } else {
                        customSocialmediaBinding.txtLink.setVisibility(View.GONE);
                    }
                }
                isAnyChanges.postValue(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        customSocialmediaBinding.tvCancel.setOnClickListener(v -> {
            //dialogAddLink.dismiss();
            discardChangesDialog(dialogAddLink, null);
        });

        customSocialmediaBinding.relSave.setOnClickListener(v -> {

            if (TextUtils.isEmpty(customSocialmediaBinding.etName.getText().toString().trim())) {
                toastMessage(getString(R.string.add_platform));
                return;
            }
            if (TextUtils.isEmpty(customSocialmediaBinding.etLink.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_your_link));
                return;
            }
            if (!isValidUrl(customSocialmediaBinding.etLink.getText().toString().trim())) {
                toastMessage(getString(R.string.please_enter_valid_link));
                return;
            }

            nameActivityVM.addSocialMediaRequest(this, customSocialmediaBinding.etLink.getText().toString(),
                    customSocialmediaBinding.roundedImage.getTag() != null ? customSocialmediaBinding.roundedImage.getTag().toString() : "", customSocialmediaBinding.etName.getText().toString());
        });
//
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogAddLink.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogAddLink.show();
        dialogAddLink.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddLink.getWindow().setAttributes(lp);
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(customSocialmediaBinding.etName.getText().toString().trim())) {
            return false;
        }
        if (TextUtils.isEmpty(customSocialmediaBinding.etLink.getText().toString().trim())) {
            return false;
        }
        return isValidUrl(customSocialmediaBinding.etLink.getText().toString().trim());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1213) {//new company added case
            // Handle media selection
            Uri selectedMediaUri = data.getData();

            if (selectedMediaUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedMediaUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap.getWidth() >= 480 && bitmap.getHeight() >= 480) {

                        String realPath = getRealPathFromURI(selectedMediaUri);
                        String mediaType = getMediaType(selectedMediaUri);
                        if (mediaType != null && realPath != null) {
                            Glide.with(this).load(selectedMediaUri).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(customSocialmediaBinding.roundedImage);
                            customSocialmediaBinding.roundedImage.setTag(realPath);
                            customSocialmediaBinding.imgCancel.setVisibility(View.VISIBLE);
                            customSocialmediaBinding.imgAdd.setVisibility(View.GONE);
                            isAnyChanges.postValue(true);
//                            DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
                        } else {
                            toastMessage(getString(R.string.unsupported_media_type));
                            if (customSocialmediaBinding != null) {
                                customSocialmediaBinding.roundedImage.setTag("");
                            }
                        }


                    } else {
                        toastMessage(getString(R.string.use_a_size_that_s_at_least_480_x_480_pixels));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Handle failure or cancellation
            Toast.makeText(this, "Selection Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMediaType(Uri uri) {
        String contentType = getContentResolver().getType(uri);
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return "image";
            } else if (contentType.startsWith("video/")) {
                return "video";
            }
        }
        return null;
    }

    @SuppressLint("NewApi")
    public String getRealPathFromURI(Uri uri) {
        String filePath = "";
        String[] projection = {MediaStore.MediaColumns.DATA};
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                filePath = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e("FilePathUtils", "Error retrieving file path: " + e.getMessage());
        }
        return filePath;
    }

    Dialog dialogDiscard;
    DialogDiscardBinding dialogDiscardBinding;

    public void discardChangesDialog(Dialog dialogMain, ConnectedSocialMedia.Data data) {
        dialogDiscard = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDiscard.setTitle(null);
        dialogDiscardBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_discard, null, false);
        if (language.equals("ar")) {
            setArFont(dialogDiscardBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(dialogDiscardBinding.txtDesc, Constants.FONT_AR_REGULAR);
            setArFont(dialogDiscardBinding.tvSend, Constants.FONT_AR_BOLD);
            setArFont(dialogDiscardBinding.tvCancel, Constants.FONT_AR_BOLD);
        }
        dialogDiscardBinding.txtTitle.setText(getString(R.string.save_changes));
        dialogDiscardBinding.txtDesc.setText(getString(R.string.would_you_like_to_save_before_exiting));
        dialogDiscardBinding.tvSend.setText(getString(R.string.save));
        dialogDiscardBinding.tvCancel.setText(getString(R.string.discard_1));
        dialogDiscard.setContentView(dialogDiscardBinding.getRoot());
//        dialog.setContentView(R.layout.dialog_discard);
        dialogDiscard.setCancelable(true);
//        TextView tvSend = dialog.findViewById(R.id.tv_send);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);


        dialogDiscardBinding.tvCancel.setOnClickListener(v -> {
            dialogDiscard.dismiss();
            dialogMain.dismiss();
        });

        dialogDiscardBinding.relSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(customSocialmediaBinding.etName.getText().toString().trim())) {
                toastMessage(getString(R.string.add_platform));
                return;
            }
            if (TextUtils.isEmpty(customSocialmediaBinding.etLink.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_your_link));
                return;
            }
            if (!isValidUrl(customSocialmediaBinding.etLink.getText().toString().trim())) {
                toastMessage(getString(R.string.please_enter_valid_link));
                return;
            }
            nameActivityVM.addSocialMediaRequest(this, customSocialmediaBinding.etLink.getText().toString(),
                    customSocialmediaBinding.roundedImage.getTag() != null ? customSocialmediaBinding.roundedImage.getTag().toString() : "", customSocialmediaBinding.etName.getText().toString());
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
