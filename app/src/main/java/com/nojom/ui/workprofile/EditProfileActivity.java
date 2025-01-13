package com.nojom.ui.workprofile;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.adapter.ProfileMenuAdapter;
import com.nojom.databinding.ActivityEditProfileBinding;
import com.nojom.databinding.DialogDiscardBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.ProfileMenu;
import com.nojom.model.ProfileResponse;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.PermissionRequest;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class EditProfileActivity extends BaseActivity implements ProfileMenuAdapter.OnClickMenuListener, BaseActivity.OnProfileLoadListener, PermissionListener {
    private ActivityEditProfileBinding binding;
    private EditProfileActivityVM editProfileActivityVM;
    private File profileFile = null;
    private final MutableLiveData<Boolean> isAnyChanges = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        isAnyChanges.postValue(false);
        if (language.equals("ar")) {
            setArFont(binding.tvTitle, Constants.FONT_AR_SEMI_BOLD);
            setArFont(binding.etName, Constants.FONT_AR_REGULAR);
            setArFont(binding.etArName, Constants.FONT_AR_REGULAR);
            setArFont(binding.etUsername, Constants.FONT_AR_REGULAR);
            setArFont(binding.etBusinessEmail, Constants.FONT_AR_REGULAR);
            setArFont(binding.etMobile, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv1, Constants.FONT_AR_BOLD);
            setArFont(binding.tv2, Constants.FONT_AR_BOLD);
            setArFont(binding.tv3, Constants.FONT_AR_BOLD);
            setArFont(binding.txtPrev, Constants.FONT_AR_BOLD);
            setArFont(binding.txtcont, Constants.FONT_AR_BOLD);
            setArFont(binding.txtOffer, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv4, Constants.FONT_AR_BOLD);
            setArFont(binding.txtStatusOffer, Constants.FONT_AR_BOLD);
            setArFont(binding.txtStatusBEmail, Constants.FONT_AR_BOLD);
            setArFont(binding.txtStatusBEmail, Constants.FONT_AR_BOLD);
            setArFont(binding.tv5, Constants.FONT_AR_BOLD);
            setArFont(binding.tv9, Constants.FONT_AR_BOLD);
            setArFont(binding.txtStatusWapp, Constants.FONT_AR_BOLD);
            setArFont(binding.tvSave, Constants.FONT_AR_MEDIUM);
        }
        initData();
    }

    String PREFIX;

    private void initData() {
        PREFIX = getString(R.string.nojom_com_e);
        editProfileActivityVM = ViewModelProviders.of(this).get(EditProfileActivityVM.class);
        editProfileActivityVM.init(this, binding);

        setOnProfileLoadListener(this);

        ProfileResponse profileData = Preferences.getProfileData(this);

        binding.ccp.registerCarrierNumberEditText(binding.etMobile);

        binding.ccp.setOnCountryChangeListener(() -> {
//            binding.toolbar.tvSave.setVisibility(View.VISIBLE);
//            binding.tvPhonePrefix.setText(binding.ccp.getSelectedCountryCodeWithPlus());
        });
        /*ViewTooltip.on(this, binding.tv4).
                    color(getResources().getColor(R.color.white))
                    .textColor(getResources().getColor(R.color.C_020814))
                    .autoHide(true, 3000)
                    .corner(30)
                    .position(ViewTooltip.Position.BOTTOM)
                    .text(getString(R.string.control_who_can_send_you_collaboration_offers_choose_public_to_accept_offers_from_anyone_brands_to_limit_offers_to_businesses_or_only_me_to_restrict_any_party_from_sending_you_offers))
                    .show();*/
        /*new SimpleTooltip.Builder(this)
                    .anchorView(view)
                    .textColor(getResources().getColor(R.color.C_020814))
                    .backgroundColor(getResources().getColor(R.color.white))
                    .cornerRadius(10)
                    .text(getString(R.string.control_who_can_send_you_collaboration_offers_choose_public_to_accept_offers_from_anyone_brands_to_limit_offers_to_businesses_or_only_me_to_restrict_any_party_from_sending_you_offers))
                    .gravity(Gravity.END)
                    .arrowColor(getResources().getColor(R.color.white))
                    .maxWidth(R.dimen._180sdp)
                    .build()
                    .show();*/
        //            showPopup(view, getString(R.string.control_who_can_send_you_collaboration_offers_choose_public_to_accept_offers_from_anyone_brands_to_limit_offers_to_businesses_or_only_me_to_restrict_any_party_from_sending_you_offers));
        binding.tv4.setOnClickListener(view -> showCustomTooltip(view, getString(R.string.control_who_can_send_you_collaboration_offers_choose_public_to_accept_offers_from_anyone_brands_to_limit_offers_to_businesses_or_only_me_to_restrict_any_party_from_sending_you_offers)));
        binding.tv9.setOnClickListener(view -> showCustomTooltip(view, getString(R.string.control_who_can_send_you_email_choose_public_to_accept_emails_from_anyone_brands_to_limit_emails_to_businesses_or_only_me_to_restrict_any_party_from_sending_you_emails)));
        binding.tv5.setOnClickListener(view -> showCustomTooltip(view, getString(R.string.control_who_can_contact_you_via_whatsapp_choose_public_to_accept_messages_from_anyone_brands_to_limit_messages_to_businesses_or_only_me_to_restrict_any_party_from_sending_you_messages)));

        if (profileData != null) {

            if (profileData.firstName != null) {
                binding.etName.setText(profileData.firstName);
            }
            if (profileData.lastName != null) {
                binding.etArName.setText(profileData.lastName);
            }
//            if (profileData.username != null) {
            binding.etUsername.setText(String.format(getString(R.string.nojom_com_s), profileData.username != null ? profileData.username : ""));

            binding.etUsername.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    isAnyChanges.postValue(true);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String input = s.toString();
                    if (!input.startsWith(PREFIX)) {
                        // If user tries to change the base URL, reset it
                        binding.etUsername.setText(PREFIX);
                        binding.etUsername.setSelection(binding.etUsername.getText().length());
                    } else {
                        // Remove any text after the base URL
                        String remainingText = input.substring(PREFIX.length());
                        if (remainingText.contains("/")) {
                            String updatedText = PREFIX + remainingText.split("/")[0];
                            binding.etUsername.setText(updatedText);
                            binding.etUsername.setSelection(updatedText.length());
                        }
                    }
                }
            });
//            } else {
//                binding.etUsername.addTextChangedListener(watcher);
//            }
            if (profileData.bussiness_email != null) {
                binding.etBusinessEmail.setText(profileData.bussiness_email);
            }
            setPublicStatusValue(profileData.show_send_offer_button, binding.txtStatusOffer);
            setPublicStatusValue(profileData.show_whatsapp, binding.txtStatusWapp);
            setPublicStatusValue(profileData.show_email, binding.txtStatusBEmail);

            binding.swChat.setChecked(profileData.chat_allowed == 1);
            setPreview();

            if (profileData.whatsapp_number != null) {
                try {
                    String[] split = profileData.whatsapp_number.split("\\.");
                    if (split.length == 2) {
                        binding.etMobile.setText(split[1]);
//                        binding.tvPhonePrefix.setText(split[0]);

                        String nameCode = Preferences.readString(this, COUNTRY_CODE, "");
                        if (!TextUtils.isEmpty(nameCode)) {
                            binding.ccp.setDetectCountryWithAreaCode(true);
                            binding.ccp.setCountryForNameCode(nameCode);
                        } else {
                            String code = split[0].replace("+", "").replace(" ", "");//added by DPP on 12th Feb[replace " " with ""]
                            binding.ccp.setCountryForPhoneCode(Integer.parseInt(code));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Glide.with(this).load(getImageUrl() + profileData.profilePic).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
            }).into(binding.imgProfile);

        }

        isAnyChanges.observe(this, aBoolean -> {
            if (aBoolean && isValid()) {
                DrawableCompat.setTint(binding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
                binding.tvSave.setTextColor(getResources().getColor(R.color.white));
            } else {
                DrawableCompat.setTint(binding.relSave.getBackground(), ContextCompat.getColor(this, R.color.C_E5E5EA));
                binding.tvSave.setTextColor(getResources().getColor(R.color.C_020814));
            }
        });

        binding.swChat.setOnCheckedChangeListener((compoundButton, b) -> {
            isAnyChanges.postValue(true);
        });

        binding.txtStatusBEmail.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatusBEmail, 2));
        binding.txtStatusWapp.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatusWapp, 3));
        binding.txtStatusOffer.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatusOffer, 1));

        binding.imgBack.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(isAnyChanges.getValue())) {
                discardChangesDialog();
            } else {
                onBackPressed();
            }
        });
        binding.relSave.setOnClickListener(v -> saveData(null));
        binding.imgPick.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                Intent intent = new Intent(this, ImagePickActivity.class);
                intent.putExtra(IS_NEED_CAMERA, true);
                intent.putExtra(Constant.MAX_NUMBER, 1);
                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
            } else {
                checkPermission();
            }
        });

        editProfileActivityVM.getShowProgress().observe(this, showProgress -> {
            disableEnableTouch(showProgress);
            if (showProgress) {
                binding.tvSave.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.tvSave.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        binding.etName.addTextChangedListener(watcher);
        binding.etArName.addTextChangedListener(watcher);
//        binding.etUsername.addTextChangedListener(watcher);
        binding.etBusinessEmail.addTextChangedListener(watcher);
        binding.etMobile.addTextChangedListener(watcher);
        binding.etArName.setFilters(new InputFilter[]{new ArabicInputFilter()});
        binding.etName.setFilters(new InputFilter[]{new EnglishInputFilter()});
    }

    private boolean isValid() {

        if (TextUtils.isEmpty(getFirstName()) && TextUtils.isEmpty(getUsername()) /*&& TextUtils.isEmpty(getEmail()) && TextUtils.isEmpty(getMobile())*/) {
            return false;
        }

        if (TextUtils.isEmpty(getFirstName())) {
            return false;
        }
//        if (TextUtils.isEmpty(getArName())) {
//            return false;
//        }
        return !TextUtils.isEmpty(getUsername());
    }

    private void saveData(Dialog dialogDiscard) {
        if (Boolean.FALSE.equals(isAnyChanges.getValue())) {
            onBackPressed();
            return;
        }

        if (TextUtils.isEmpty(getFirstName()) && TextUtils.isEmpty(getUsername()) /*&& TextUtils.isEmpty(getEmail()) && TextUtils.isEmpty(getMobile())*/) {
            toastMessage(getString(R.string.all_fields_are_required));
            return;
        }

        if (TextUtils.isEmpty(getFirstName())) {
            toastMessage(getString(R.string.please_enter_first_name));
            return;
        }
//        if (TextUtils.isEmpty(getArName())) {
//            toastMessage(getString(R.string.arabic_name));
//            return;
//        }
        if (TextUtils.isEmpty(getUsername())) {
            toastMessage(getString(R.string.enter_username));
            return;
        }

        int bEmail = Integer.parseInt(binding.txtStatusBEmail.getTag().toString());
        int wApp = Integer.parseInt(binding.txtStatusWapp.getTag().toString());

        if (bEmail == 1 || bEmail == 2) {
            if (TextUtils.isEmpty(getEmail())) {
                toastMessage(getString(R.string.enter_valid_email));
                return;
            }
            if (!isValidEmailData(getEmail())) {
                toastMessage(getString(R.string.enter_valid_email));
                return;
            }
        }
        if (wApp == 1 || wApp == 2) {
            if (TextUtils.isEmpty(getMobile())) {
                toastMessage(getString(R.string.please_enter_mobile));
                return;
            }
            if (!binding.ccp.isValidFullNumber()) {
                toastMessage(getString(R.string.enter_valid_number));
                return;
            }
        }


        if (!TextUtils.isEmpty(getEmail())) {
            if (!isValidEmailData(getEmail())) {
                toastMessage(getString(R.string.enter_valid_email));
                return;
            }
        }
//            if (!isValidEmail(getEmail())) {
//                toastMessage(getString(R.string.enter_valid_email));
//                return;
//            }
//            if (TextUtils.isEmpty(getMobile())) {
//                toastMessage(getString(R.string.please_enter_mobile));
//                return;
//            }
//            if (!binding.ccp.isValidFullNumber()) {
//                toastMessage(getString(R.string.enter_valid_number));
//                return;
//            }
        if (dialogDiscard != null) {
            dialogDiscard.dismiss();
        }
        editProfileActivityVM.updateProfile(getFirstName(), getEmail(), getMobile(), getMobilePrefix(),
                getUsername(), profileFile, -1, getArName(), 1, Integer.parseInt(binding.txtStatusOffer.getTag().toString()),
                Integer.parseInt(binding.txtStatusBEmail.getTag().toString()), Integer.parseInt(binding.txtStatusWapp.getTag().toString()), binding.swChat.isChecked() ? 1 : 0);
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!TextUtils.isEmpty(charSequence.toString().trim())) {
                isAnyChanges.postValue(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public void onClickMenu(ProfileMenu menu) {
        switch (menu.id) {
            case 1://social media
                break;
            case 2://overview
                break;
            case 3://portfolio
                break;
            case 4://work with
                break;
            case 5://my store
                break;
            case 6://youtube
                break;
            case 7://partners
                break;
            case 8://agency
                break;
        }
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        toastMessage(getString(R.string.privateinfo_updated_succeefully));
        onBackPressed();
    }

    public String getFirstName() {
        return Objects.requireNonNull(binding.etName.getText()).toString().trim();
    }

    public String getArName() {
        return Objects.requireNonNull(binding.etArName.getText()).toString().trim();
    }

    public String getEmail() {
        return Objects.requireNonNull(binding.etBusinessEmail.getText()).toString().trim();
    }

    public String getUsername() {
        return Objects.requireNonNull(binding.etUsername.getText()).toString().trim().replaceAll(PREFIX, "");
    }

    private String getMobile() {
        return Objects.requireNonNull(binding.etMobile.getText()).toString().trim();
    }

    private String getMobilePrefix() {
        return binding.ccp.getSelectedCountryCodeWithPlus();
    }

    public void checkPermission() {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionListener(this);
        permissionRequest.checkStorageCameraRequest(this);
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {
        Intent intent = new Intent(this, ImagePickActivity.class);
        intent.putExtra(IS_NEED_CAMERA, true);
        intent.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<ImageFile> imgPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (imgPath != null && imgPath.size() > 0) {
//                        binding.toolbar.tvSave.setVisibility(View.VISIBLE);
                        profileFile = new File(imgPath.get(0).getPath());

                        Glide.with(this).load(profileFile).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                        binding.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                        binding.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(binding.imgProfile);
                        isAnyChanges.postValue(true);
                    } else {
                        toastMessage(getString(R.string.image_not_selected));
                    }
                }
            } else if (requestCode == 1212) {
                if (resultCode == RESULT_OK) {
//                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId()-; <-- E.164 format phone number on 10.2.+ devices
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPublicStatusValue(int publicStatus, TextView txtStatus) {
        txtStatus.setTag(publicStatus);
        switch (publicStatus) {
            case 2://brands
                txtStatus.setText(getString(R.string.brand_only));
                txtStatus.setTextColor(getResources().getColor(R.color.c_075E45));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_C7EBD1));
                break;
            case 3://only me
                txtStatus.setText(getString(R.string.only_me));
                txtStatus.setTextColor(getResources().getColor(R.color.red_dark));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_FADCD9));
                break;
            default:
                txtStatus.setText(getString(R.string.public_));
                txtStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_D4E4FA));
                break;
        }
    }

    public void whoCanSeeDialog(TextView textView, int module) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_who);
        dialog.setCancelable(true);
        RadioButton chkPublic = dialog.findViewById(R.id.chk_public);
        RadioButton chkBrand = dialog.findViewById(R.id.chk_brand);
        RadioButton chkMe = dialog.findViewById(R.id.chk_me);
        ImageView tvCancel = dialog.findViewById(R.id.tv_cancel);
        RelativeLayout relSave = dialog.findViewById(R.id.rel_save);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        TextView txtTitle = dialog.findViewById(R.id.txt_title);
        TextView txtLblPub = dialog.findViewById(R.id.txt_lbl_public);
        TextView txtLblBrn = dialog.findViewById(R.id.txt_lbl_brand);
        TextView txtLblMe = dialog.findViewById(R.id.txt_lbl_me);
        TextView tv1 = dialog.findViewById(R.id.tv1);
        TextView tv2 = dialog.findViewById(R.id.tv2);
        TextView tv3 = dialog.findViewById(R.id.tv3);
        TextView tvSend = dialog.findViewById(R.id.tv_send);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        if (language.equals("ar")) {
//            setArFont(tvCancel, Constants.FONT_AR_REGULAR);
            setArFont(txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(txtLblPub, Constants.FONT_AR_REGULAR);
            setArFont(txtLblBrn, Constants.FONT_AR_REGULAR);
            setArFont(txtLblMe, Constants.FONT_AR_REGULAR);
            setArFont(tv1, Constants.FONT_AR_REGULAR);
            setArFont(tv2, Constants.FONT_AR_REGULAR);
            setArFont(tv3, Constants.FONT_AR_REGULAR);
            setArFont(tvSend, Constants.FONT_AR_BOLD);
        }

        int stat = Integer.parseInt(textView.getTag().toString());
        switch (stat) {
            case 1:
                chkPublic.setChecked(true);
                break;
            case 2:
                chkBrand.setChecked(true);
                break;
            case 3:
                chkMe.setChecked(true);
                break;

        }

        chkMe.setOnClickListener(v -> {
            chkMe.setChecked(true);
            chkBrand.setChecked(false);
            chkPublic.setChecked(false);
        });
        chkBrand.setOnClickListener(v -> {
            chkMe.setChecked(false);
            chkBrand.setChecked(true);
            chkPublic.setChecked(false);
        });
        chkPublic.setOnClickListener(v -> {
            chkMe.setChecked(false);
            chkBrand.setChecked(false);
            chkPublic.setChecked(true);
        });

        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        relSave.setOnClickListener(v -> {
            int status = 0;
            if (chkPublic.isChecked()) {
                status = 1;
            } else if (chkBrand.isChecked()) {
                status = 2;
            } else if (chkMe.isChecked()) {
                status = 3;
            }

            if (status == 0) {
                toastMessage(getString(R.string.please_select_any));
                return;
            }
            isAnyChanges.postValue(true);
            setPublicStatusValue(status, textView);
            setPreview();

            dialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    private void setPreview() {
        if (getEmailStatus() == 3 && getWhatsappStatus() == 3 && getAcceptOfferStatus() == 3) {
            msg();
        } else if (getWhatsappStatus() != 3 && getAcceptOfferStatus() != 3 && getEmailStatus() != 3) {
            businessEmailWhatsapp(false);
        } else if (getEmailStatus() != 3 && getAcceptOfferStatus() != 3) {
            msgEmailOffer();
        } else if (getEmailStatus() == 3 && getAcceptOfferStatus() != 3 && getWhatsappStatus() != 3) {
            msgWhatsappOffer(true);
        } else if (getEmailStatus() != 3 && getAcceptOfferStatus() == 3 && getWhatsappStatus() != 3) {
            businessEmailWhatsapp(true);
        } else if (getEmailStatus() == 3 && getAcceptOfferStatus() == 3 && getWhatsappStatus() != 3) {
            msgWhatsapp(true);
        } else if (getWhatsappStatus() != 3 && getAcceptOfferStatus() != 3) {
            msgWhatsappOffer(false);
        } else if (getAcceptOfferStatus() != 3 && getEmailStatus() == 3 && getWhatsappStatus() == 3) {
            msgOffer();
        }
    }

    private int getAcceptOfferStatus() {
        return Integer.parseInt(binding.txtStatusOffer.getTag().toString());
    }

    private int getEmailStatus() {
        return Integer.parseInt(binding.txtStatusBEmail.getTag().toString());
    }

    private int getWhatsappStatus() {
        return Integer.parseInt(binding.txtStatusWapp.getTag().toString());
    }

    private void setPreviewDynamic(int publicStatus, int module) {
        if (publicStatus == 3) {//only me
            msg();
        } else {

        }
//        businessEmailWhatsapp();
//        msgWhatsappOffer();
//        msgEmailOffer();
//        msgOffer();

    }

    private void businessEmailWhatsapp(boolean offerGone) {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(offerGone ? View.GONE : View.VISIBLE);
        for (int i = 0; i < 3; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
            TextView txtView = view.findViewById(R.id.txt_view1);
            if (language.equals("ar")) {
                setArFont(txtView, Constants.FONT_AR_REGULAR);
            }
            switch (i) {
                case 0:
                    txtView.setText(getString(R.string.whatsapp));
                    break;
                case 1:
                    txtView.setText(getString(R.string.email));
                    setMargins(txtView, (int) getResources().getDimension(R.dimen._5sdp), 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
                    break;
                case 2:
                    txtView.setText(getString(R.string.message));
                    break;
            }
//            txtView.setBackground(getResources().getDrawable(R.drawable.gray_button_bg));
//            txtView.setTextColor(getResources().getColor(R.color.C_020814));
            binding.linPreview.addView(view);
        }
    }

    private void msgOffer() {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(View.GONE);
        for (int i = 0; i < 2; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
            TextView txtView = view.findViewById(R.id.txt_view1);
            if (language.equals("ar")) {
                setArFont(txtView, Constants.FONT_AR_REGULAR);
            }
            switch (i) {
                case 0:
                    txtView.setText(getString(R.string.message));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.gray_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
                    if (language.equals("ar")) {
                        setMargins(txtView, (int) getResources().getDimension(R.dimen._5sdp), 0, 0, 0);
                    } else {
                        setMargins(txtView, 0, 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
                    }
                    break;
                case 1:
                    txtView.setText(getString(R.string.send_offer));
                    txtView.setBackground(getResources().getDrawable(R.drawable.light_black_bg_7));
                    txtView.setTextColor(getResources().getColor(R.color.black));
                    break;
            }

            binding.linPreview.addView(view);
        }
    }

    private void msg() {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(View.GONE);

        View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
        TextView txtView = view.findViewById(R.id.txt_view1);
        if (language.equals("ar")) {
            setArFont(txtView, Constants.FONT_AR_REGULAR);
        }
        txtView.setText(getString(R.string.message));
//        txtView.setBackground(getResources().getDrawable(R.drawable.gray_button_bg));
//        txtView.setTextColor(getResources().getColor(R.color.C_020814));
        binding.linPreview.addView(view);
    }


    private void msgWhatsappOffer(boolean offerGone) {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(offerGone ? View.GONE : View.VISIBLE);
        for (int i = 0; i < 3; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
            TextView txtView = view.findViewById(R.id.txt_view1);
            if (language.equals("ar")) {
                setArFont(txtView, Constants.FONT_AR_REGULAR);
            }
            switch (i) {
                case 0:
                    txtView.setText(getString(R.string.message));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.gray_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
//                    setMargins(txtView,0,0,(int) getResources().getDimension(R.dimen._5sdp),0);
                    break;
                case 1:
                    txtView.setText(getString(R.string.whatsapp));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.gray_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
                    setMargins(txtView, (int) getResources().getDimension(R.dimen._5sdp), 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
                    break;
                case 2:
                    txtView.setText(getString(R.string.send_offer));
                    txtView.setBackground(getResources().getDrawable(R.drawable.light_black_bg_7));
                    txtView.setTextColor(getResources().getColor(R.color.black));
                    break;
            }

            binding.linPreview.addView(view);
        }
    }

    private void msgEmailOffer() {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(View.GONE);
        for (int i = 0; i < 3; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
            TextView txtView = view.findViewById(R.id.txt_view1);
            if (language.equals("ar")) {
                setArFont(txtView, Constants.FONT_AR_REGULAR);
            }
            switch (i) {
                case 0:
                    txtView.setText(getString(R.string.message));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.gray_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
//                    setMargins(txtView,0,0,(int) getResources().getDimension(R.dimen._5sdp),0);
                    break;
                case 1:
                    txtView.setText(getString(R.string.email));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.gray_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
                    setMargins(txtView, (int) getResources().getDimension(R.dimen._5sdp), 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
                    break;
                case 2:
                    txtView.setText(getString(R.string.send_offer));
                    txtView.setBackground(getResources().getDrawable(R.drawable.light_black_bg_7));
                    txtView.setTextColor(getResources().getColor(R.color.black));
                    break;
            }

            binding.linPreview.addView(view);
        }
    }

    private void msgWhatsapp(boolean offerGone) {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(offerGone ? View.GONE : View.VISIBLE);
        for (int i = 0; i < 2; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
            TextView txtView = view.findViewById(R.id.txt_view1);
            if (language.equals("ar")) {
                setArFont(txtView, Constants.FONT_AR_REGULAR);
            }
            switch (i) {
                case 0:
                    txtView.setText(getString(R.string.message));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.gray_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
                    if (language.equals("ar")) {
                        setMargins(txtView, (int) getResources().getDimension(R.dimen._5sdp), 0, 0, 0);
                    } else {
                        setMargins(txtView, 0, 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
                    }
                    break;
                case 1:
                    txtView.setText(getString(R.string.whatsapp));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.gray_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
                    break;
            }

            binding.linPreview.addView(view);
        }
    }


    private Dialog dialogDiscard;
    DialogDiscardBinding dialogDiscardBinding;

    public void discardChangesDialog() {
        dialogDiscard = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDiscard.setTitle(null);
        dialogDiscardBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_discard, null, false);
        dialogDiscard.setContentView(dialogDiscardBinding.getRoot());
//        dialog.setContentView(R.layout.dialog_discard);
        if (language.equals("ar")) {
            setArFont(dialogDiscardBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(dialogDiscardBinding.txtDesc, Constants.FONT_AR_REGULAR);
            setArFont(dialogDiscardBinding.tvSend, Constants.FONT_AR_BOLD);
            setArFont(dialogDiscardBinding.tvCancel, Constants.FONT_AR_BOLD);
        }
        dialogDiscard.setCancelable(true);
//        TextView tvSend = dialog.findViewById(R.id.tv_send);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        dialogDiscardBinding.txtTitle.setText(getString(R.string.save_changes));
        dialogDiscardBinding.txtDesc.setText(getString(R.string.would_you_like_to_save_before_exiting));
        dialogDiscardBinding.tvSend.setText(getString(R.string.save));
        dialogDiscardBinding.tvCancel.setText(getString(R.string.discard_1));


        dialogDiscardBinding.tvCancel.setOnClickListener(v -> {
            dialogDiscard.dismiss();
            onBackPressed();
        });

        dialogDiscardBinding.relSave.setOnClickListener(v -> saveData(dialogDiscard));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDiscard.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDiscard.show();
        dialogDiscard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDiscard.getWindow().setAttributes(lp);
    }

    private void showCustomTooltip(View anchor, String msg) {

        Balloon balloon = new Balloon.Builder(this)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.START)
//                .setRtlSupports(true)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setWidth(190)
                .setHeight(BalloonSizeSpec.WRAP)
                .setTextSize(11f)
                .setPadding(5)
                .setAutoDismissDuration(3000)
                .setCornerRadius(6f)
                .setAlpha(0.9f)
                .setText(msg)
                .setTextColor(ContextCompat.getColor(this, R.color.C_020814))
                .setTextIsHtml(true)
//                .setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_lock_id))
                .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
//                .setOnBalloonClickListener(onBalloonClickListener)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .setLifecycleOwner(binding.getLifecycleOwner())
                .build();
        if (language.equals("ar")) {
            balloon.showAlignLeft(anchor);
        } else {
            balloon.showAlignRight(anchor);
        }
    }

}
