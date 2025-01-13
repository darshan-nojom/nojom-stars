package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.textview.TextViewSFTextPro;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.nojom.R;
import com.nojom.adapter.SelectCityAdapter;
import com.nojom.adapter.SelectCountryAdapter;
import com.nojom.apis.GetCountriesAPI;
import com.nojom.databinding.ActivityOverviewNewBinding;
import com.nojom.databinding.DialogCategoryNewBinding;
import com.nojom.databinding.DialogDiscardBinding;
import com.nojom.databinding.DialogDobBinding;
import com.nojom.databinding.DialogGenderBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.CategoryResponse;
import com.nojom.model.CityResponse;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.CountryResponse;
import com.nojom.model.GeneralModel;
import com.nojom.model.ProfileResponse;
import com.nojom.model.requestmodel.AuthenticationRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.GenderActivity;
import com.nojom.ui.auth.UpdatePasswordActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Response;

public class OverViewActivityNew extends BaseActivity implements ResponseListener, View.OnClickListener {
    private ActivityOverviewNewBinding binding;
    private ProfileResponse profileData;
    private static final int REQ_ID_VERIFICATION = 103;
    private MyProfileActivityVM myProfileActivityVM;

    private VerifyIDActivityVM verifyIDActivityVM;
    private WorkExperienceActivityVM workExperienceActivityVM;

    private MutableLiveData<Boolean> isAnyChanges = new MutableLiveData<>();
    private UpdateLocationActivityVM updateLocationActivityVM;
    private GetCountriesAPI getCountriesAPI;
    private SelectCountryAdapter selectCountryAdapter;
    private SelectCityAdapter selectCityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_overview_new);
        isAnyChanges.postValue(false);
        myProfileActivityVM = ViewModelProviders.of(this).get(MyProfileActivityVM.class);
        myProfileActivityVM.init(this, null);
        myProfileActivityVM.getCategory(this);
        myProfileActivityVM.getTags(this);
        verifyIDActivityVM = ViewModelProviders.of(this).get(VerifyIDActivityVM.class);
        verifyIDActivityVM.getMawthooqStatus(this);
//        verifyIDActivityVM = ViewModelProviders.of(this).get(VerifyIDActivityVM.class);
//        verifyIDActivityVM.getMawthooqList(this, "maw");
        skillsActivityViewModel = ViewModelProviders.of(this).get(SelectSkillsActivityVM.class);
        skillsActivityViewModel.init(this);
        workExperienceActivityVM = ViewModelProviders.of(this).get(WorkExperienceActivityVM.class);
        workExperienceActivityVM.init(this);
        updateLocationActivityVM = ViewModelProviders.of(this).get(UpdateLocationActivityVM.class);
        updateLocationActivityVM.init(this);
        getCountriesAPI = new GetCountriesAPI();
        getCountriesAPI.init(this);
        getCountriesAPI.getCountries();
        if (language.equals("ar")) {
            setArFont(binding.tvToolbarTitle, Constants.FONT_AR_MEDIUM);
            setArFont(binding.etAbout, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatus, Constants.FONT_AR_BOLD);
            setArFont(binding.tv1, Constants.FONT_AR_BOLD);
            setArFont(binding.txtStatusCat, Constants.FONT_AR_BOLD);
            setArFont(binding.txtCat, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtTags, Constants.FONT_AR_REGULAR);
            setArFont(binding.etMawId, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatusMaw, Constants.FONT_AR_BOLD);
            setArFont(binding.etDob, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatusDob, Constants.FONT_AR_BOLD);
            setArFont(binding.etGender, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatusGender, Constants.FONT_AR_BOLD);
            setArFont(binding.tv2, Constants.FONT_AR_BOLD);
            setArFont(binding.txtStatusPrice, Constants.FONT_AR_BOLD);
            setArFont(binding.etMin, Constants.FONT_AR_REGULAR);
            setArFont(binding.etMax, Constants.FONT_AR_REGULAR);
            setArFont(binding.tvSave, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tvMinSar, Constants.FONT_AR_REGULAR);
            setArFont(binding.tvMaxSar, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv7, Constants.FONT_AR_BOLD);
            setArFont(binding.txtCountry, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtCity, Constants.FONT_AR_REGULAR);
        }
        if (getCurrency().equals("SAR")) {
            binding.tvMinSar.setText(getString(R.string.sar));
            binding.tvMaxSar.setText(getString(R.string.sar));
        } else {
            binding.tvMinSar.setText(getString(R.string.dollar));
            binding.tvMaxSar.setText(getString(R.string.dollar));
        }
        initData();
    }

    private void initData() {
        setOnProfileLoadListener(this::onProfileLoad);

        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
//            discardChangesDialog();
        });
        binding.tvToolbarTitle.setText(getString(R.string.overview));
//        binding.relEdit.setVisibility(View.GONE);

        profileData = Preferences.getProfileData(this);
//        verifyIDActivityVM.getListMutableLiveData().observe(this, this::setMaqData);
        updateUI();

        binding.relCat.setOnClickListener(this);
        binding.relTags.setOnClickListener(this);
        binding.relSave.setOnClickListener(this);
        binding.etDob.setOnClickListener(this);
        binding.etGender.setOnClickListener(this);
        binding.txtStatus.setOnClickListener(this);
        binding.txtStatusPrice.setOnClickListener(this);
        binding.txtStatusGender.setOnClickListener(this);
        binding.txtStatusMaw.setOnClickListener(this);
        binding.txtStatusDob.setOnClickListener(this);
        binding.txtStatusCat.setOnClickListener(this);
        binding.txtStatusCountry.setOnClickListener(this);
        binding.txtCountry.setOnClickListener(this);
        binding.txtCity.setOnClickListener(this);
        binding.etMawId.setOnClickListener(this);

        skillsActivityViewModel.getIsSkillAdded().observe(this, isSuccess -> {
            getProfile();
            if (dialogCat != null) {
                dialogCat.dismiss();
            }
        });

        myProfileActivityVM.getShowProgress().observe(this, integer -> {
            if (integer == 3) {
                binding.tvSave.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.tvSave.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        isAnyChanges.observe(this, aBoolean -> {
            if (aBoolean && isValid()) {
                DrawableCompat.setTint(binding.relSave.getBackground(), ContextCompat.getColor(OverViewActivityNew.this, R.color.black));
                binding.tvSave.setTextColor(getResources().getColor(R.color.white));
            } else {
                DrawableCompat.setTint(binding.relSave.getBackground(), ContextCompat.getColor(OverViewActivityNew.this, R.color.C_E5E5EA));
                binding.tvSave.setTextColor(getResources().getColor(R.color.C_020814));
            }
        });

        DrawableCompat.setTint(binding.relSave.getBackground(), ContextCompat.getColor(OverViewActivityNew.this, R.color.C_E5E5EA));
        binding.tvSave.setTextColor(getResources().getColor(R.color.C_020814));

        verifyIDActivityVM.getMawthouqStatusMutableLiveData().observe(this, mawthouqStatus -> {
            if (profileData != null && mawthouqStatus != null) {
                if (mawthouqStatus.mawthooq_number != null) {
                    if (mawthouqStatus.status.equalsIgnoreCase("rejected")) {
                        binding.etMawId.setText("");
                    } else {
                        binding.etMawId.setText(String.format("%s", mawthouqStatus.mawthooq_number));
                    }
                } else {
                    binding.etMawId.setText("");
                }
                if (profileData.mawthooq_status != null) {
                    setPublicStatusValue(profileData.mawthooq_status.public_status != null ? profileData.mawthooq_status.public_status : 1, binding.txtStatusMaw);
                } else {
                    setPublicStatusValue(1, binding.txtStatusMaw);
                }
            } else {
                setPublicStatusValue(1, binding.txtStatusMaw);
            }
            binding.etMawId.addTextChangedListener(watcher);
        });
    }


    private void updateUI() {
        profileData = Preferences.getProfileData(this);

        if (profileData != null) {
            if (profileData.about_me != null) {
                binding.etAbout.setText(profileData.about_me);
            }
            if (!TextUtils.isEmpty(profileData.getCountryName(language))) {
                binding.txtCountry.setText(profileData.getCountryName(language));
                binding.txtCountry.setTag(profileData.countryID);
            }
            if (!TextUtils.isEmpty(profileData.getCityName(language))) {
                binding.relCity.setVisibility(View.VISIBLE);
                binding.txtCity.setText(profileData.getCityName(language));
                binding.txtCity.setTag(profileData.cityID);
            } else {
                binding.relCity.setVisibility(View.GONE);
            }

            if (profileData.gender != null) {
                if (profileData.gender == 1) {
                    binding.etGender.setText(getString(R.string.male));
                } else if (profileData.gender == 2) {
                    binding.etGender.setText(getString(R.string.female));
                } else if (profileData.gender == 3) {
                    binding.etGender.setText(getString(R.string.others));
                }
                binding.etGender.setTag(profileData.gender + "");
            }
            if (profileData.minPrice != null && profileData.maxPrice != null) {
//                if (getCurrency().equals("SAR")) {
                binding.etMin.setText(String.format("%s", Utils.getDecimalValue(String.valueOf(profileData.minPrice))));
                binding.etMax.setText(String.format("%s", Utils.getDecimalValue(String.valueOf(profileData.maxPrice))));
//                    binding.txtMaxSar.setText(String.format("%s", getString(R.string.sar)));
//                    binding.txtMinSar.setText(String.format("%s", getString(R.string.sar)));
//                } else {
//                    binding.txtMin.setText(String.format("%s", Utils.getDecimalValue(String.valueOf(profileData.minPrice))));
//                    binding.txtMax.setText(String.format("%s", Utils.getDecimalValue(String.valueOf(profileData.maxPrice))));
//                    binding.txtMaxSar.setText(String.format("%s", getString(R.string.dollar)));
//                    binding.txtMinSar.setText(String.format("%s", getString(R.string.dollar)));
//                }
            }
            if (profileData.birth_date != null && !profileData.birth_date.equals("0000-00-00")) {

                String monthLang;
                SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date dateObj = null;
                try {
                    dateObj = curFormater.parse(profileData.birth_date.split("T")[0]);
                    SimpleDateFormat postFormater = new SimpleDateFormat("MMM", Locale.getDefault());
                    if (dateObj != null) {
                        monthLang = postFormater.format(dateObj);

                        if (language.equals("ar")) {
                            binding.etDob.setText(profileData.birth_date.split("T")[0].split("-")[2] + "-" + monthLang + "-" + profileData.birth_date.split("T")[0].split("-")[0]);
                        } else {
                            String d = profileData.birth_date.split("T")[0];
                            binding.etDob.setText(String.format("%s-%s-%s", d.split("-")[2], d.split("-")[1], d.split("-")[0]));
                        }
                        String d = profileData.birth_date.split("T")[0];
                        binding.etDob.setTag(d.split("-")[0] + "-" + d.split("-")[1] + "-" + d.split("-")[2]);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
            /*if (profileData.getCountryName(language) != null) {
                binding.tvCountry.setText(profileData.getCountryName(language));
                if (!TextUtils.isEmpty(profileData.getCityName(language))) {
                    binding.tvCountry.setText(profileData.getCountryName(language) + ", " + profileData.getCityName(language));
                }
            }*/
            if (profileData.category_lists != null) {
                binding.txtCat.setVisibility(View.GONE);
//                binding.imgCat.setVisibility(View.GONE);
                binding.chipGroup.setVisibility(View.VISIBLE);
                binding.chipGroup.removeAllViews();
                for (ProfileResponse.Skill tag : profileData.category_lists) {
                    Chip chip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, binding.chipGroup, false);
                    chip.setChecked(true);
                    chip.setCheckable(false);
                    chip.setClickable(false);
                    chip.setText(tag.getName(language));
                    binding.chipGroup.addView(chip);
                }
            } else {
                binding.chipGroup.setVisibility(View.GONE);
                binding.txtCat.setVisibility(View.VISIBLE);
//                binding.imgCat.setVisibility(View.VISIBLE);
            }

            if (profileData.tags_lists != null) {
                binding.txtTags.setVisibility(View.GONE);
//                binding.imgTags.setVisibility(View.GONE);
                binding.chipGroupTags.setVisibility(View.VISIBLE);
                binding.chipGroupTags.removeAllViews();
                for (ProfileResponse.Skill tag : profileData.tags_lists) {
                    Chip chip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, binding.chipGroupTags, false);
                    chip.setChecked(true);
                    chip.setCheckable(false);
                    chip.setClickable(false);
                    chip.setText(tag.getName(language));
                    binding.chipGroupTags.addView(chip);
                }
                if (binding.chipGroupTags.getChildCount() > 3) {
                    binding.txtNoTags.setText(binding.chipGroupTags.getChildCount() + "");
                    binding.txtNoTags.setVisibility(View.VISIBLE);
                } else {
                    binding.txtNoTags.setVisibility(View.GONE);
                }
            } else {
                binding.chipGroupTags.setVisibility(View.GONE);
                binding.txtTags.setVisibility(View.VISIBLE);
//                binding.imgTags.setVisibility(View.VISIBLE);
            }

            setPublicStatusValue(profileData.about_me_public_status != null ? profileData.about_me_public_status : 1, binding.txtStatus);
            setPublicStatusValue(profileData.category_public_status != null ? profileData.category_public_status : 1, binding.txtStatusCat);
            setPublicStatusValue(profileData.show_age != null ? profileData.show_age : 1, binding.txtStatusDob);
            setPublicStatusValue(profileData.gender_public_status != null ? profileData.gender_public_status : 1, binding.txtStatusGender);
            setPublicStatusValue(profileData.price_range_public_status != null ? profileData.price_range_public_status : 1, binding.txtStatusPrice);
            setPublicStatusValue(profileData.location_public != null ? profileData.location_public : 1, binding.txtStatusCountry);

        }
        binding.etAbout.addTextChangedListener(watcher);
        binding.etMin.addTextChangedListener(watcher);
        binding.etMax.addTextChangedListener(watcher);
        binding.etGender.addTextChangedListener(watcher);
        binding.etDob.addTextChangedListener(watcher);

        myProfileActivityVM.updateDone.observe(this, integer -> {
            if (integer == 11) {
                finish();
                finishToRight();
            }
        });
    }

    public boolean isValid() {
        /*if (TextUtils.isEmpty(binding.etAbout.getText().toString().trim())) {
            return false;
        }*/
        if (TextUtils.isEmpty(binding.etDob.getText().toString().trim())) {
            return false;
        }
        if (TextUtils.isEmpty(binding.etGender.getText().toString().trim())) {
            return false;
        }
        if (TextUtils.isEmpty(binding.etMin.getText().toString().trim())) {
            return false;
        }
        return !TextUtils.isEmpty(binding.etMax.getText().toString().trim());
    }

    TextWatcher watcher = new TextWatcher() {
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
    };

    @Override
    public void onBackPressed() {
        try {
            if (Boolean.FALSE.equals(isAnyChanges.getValue())) {
                finish();
                finishToRight();
                return;
            }
            discardChangesDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //finishToRight();
    }

    public void onClickChangePass() {
        redirectActivity(UpdatePasswordActivity.class);
    }

    public void onProfileLoad(ProfileResponse data) {
        myProfileActivityVM.getShowProgress().setValue(-1);
        profileData = Preferences.getProfileData(this);

        if (nameDialog != null && nameDialog.isShowing()) {
            nameDialog.dismiss();
        }

        if (adsPriceDialog != null && adsPriceDialog.isShowing()) {
            adsPriceDialog.dismiss();
        }

        updateUI();

//        if (dialogCategory != null && dialogCategory.isShowing()) {
//            showCategoryDialog(true);
//        }
    }


    Dialog nameDialog, dialogCategory, dialogCategorySub, dialogTag, adsPriceDialog;
    TextView txtCountry, txtCity;
    LinearLayout linCity;

    private void showAdsPriceDialog() {
        adsPriceDialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        adsPriceDialog.setTitle(null);
        adsPriceDialog.setContentView(R.layout.dialog_profile_price_range);
        adsPriceDialog.setCancelable(true);

        RelativeLayout rlSave = adsPriceDialog.findViewById(R.id.rel_save);
        TextView txtCancel = adsPriceDialog.findViewById(R.id.txt_cancel);
        TextView txtMaxSign = adsPriceDialog.findViewById(R.id.txt_maxSign);
        TextView txtMinSign = adsPriceDialog.findViewById(R.id.txt_minSign);

        EditText etMaxPrice = adsPriceDialog.findViewById(R.id.et_maxPrice);
        EditText etMinPrice = adsPriceDialog.findViewById(R.id.et_minPrice);

        TextView tvSave = adsPriceDialog.findViewById(R.id.tv_save);
        ProgressBar progressBar = adsPriceDialog.findViewById(R.id.progress);

        if (getCurrency().equals("SAR")) {
            txtMaxSign.setText(getString(R.string.sar));
            txtMinSign.setText(getString(R.string.sar));
        } else {
            txtMaxSign.setText(getString(R.string.dollar));
            txtMinSign.setText(getString(R.string.dollar));
        }

        if (profileData.minPrice != null && profileData.maxPrice != null) {
            etMaxPrice.setText("" + profileData.maxPrice);
            etMinPrice.setText("" + profileData.minPrice);
        }


        myProfileActivityVM.getShowProgressAds().observe(this, aBoolean -> {

            if (aBoolean == 11) {
                if (progressBar != null && tvSave != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    tvSave.setVisibility(View.INVISIBLE);
                }
            } else {
                if (progressBar != null && tvSave != null) {
                    progressBar.setVisibility(View.GONE);
                    tvSave.setVisibility(View.VISIBLE);
                }
            }
        });


        txtCancel.setOnClickListener(v -> adsPriceDialog.dismiss());

        rlSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etMaxPrice.getText().toString().trim()) || Double.parseDouble(etMaxPrice.getText().toString()) == 0.0) {
                toastMessage(getString(R.string.max_price_can_t_be_empty_or_zero));
                return;
            }
            if (TextUtils.isEmpty(etMinPrice.getText().toString().trim()) || Double.parseDouble(etMinPrice.getText().toString()) == 0.0) {
                toastMessage(getString(R.string.min_price_can_not_be_empty));
                return;
            }
            if (Double.parseDouble(etMaxPrice.getText().toString()) < Double.parseDouble(etMinPrice.getText().toString())) {
                toastMessage(getString(R.string.max_price_should_be_greater_than_min_price));
                return;
            }
            myProfileActivityVM.updateProfile(11, null, null, null, null, null, -1, -1, null, Double.parseDouble(etMinPrice.getText().toString()), Double.parseDouble(etMaxPrice.getText().toString()), null, 0);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(adsPriceDialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        adsPriceDialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        adsPriceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        adsPriceDialog.getWindow().setAttributes(lp);
    }

    Dialog dialogCat;
    ArrayList<Integer> selCatIds, selTagIds, selRatings;
//    CategoryAdapter adapter = null;

    private void showCategoryDialog(boolean isCategory) {
        dialogCat = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogCat.setTitle(null);
        DialogCategoryNewBinding dialogCategoryBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_category_new, null, false);
        dialogCategoryBinding.txtTitle.setText(isCategory ? getString(R.string.select_category) : getString(R.string.select_tags));
        dialogCategoryBinding.etSearch.setHint(getString(R.string.search));
        dialogCategoryBinding.tvSend.setText(getString(R.string.save));
        dialogCat.setContentView(dialogCategoryBinding.getRoot());
        dialogCat.setCancelable(true);

        if (language.equals("ar")) {
            setArFont(dialogCategoryBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(dialogCategoryBinding.etSearch, Constants.FONT_AR_REGULAR);
            setArFont(dialogCategoryBinding.tvSend, Constants.FONT_AR_BOLD);
        }

        if (isCategory) {
            setCategoryGroup(dialogCategoryBinding);
        } else {
            setTagsGroup(dialogCategoryBinding);
        }


        dialogCategoryBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {//show all view
                    if (isCategory) {
                        setCategoryGroup(dialogCategoryBinding);
                    } else {
                        setTagsGroup(dialogCategoryBinding);
                    }
                } else {//based on search
                    searchData(dialogCategoryBinding, isCategory ? myProfileActivityVM.getCategoryMutableData().getValue() : myProfileActivityVM.getTagsMutableData().getValue(), isCategory, s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialogCategoryBinding.tvCancel.setOnClickListener(v -> dialogCat.dismiss());

        dialogCategoryBinding.relSave.setOnClickListener(v -> {
            ArrayList<Integer> catIds = new ArrayList<>();
            ArrayList<Integer> tagIds = new ArrayList<>();
            ArrayList<Integer> ratings = new ArrayList<>();

            List<CategoryResponse.CategoryData> selectedCatData;
            if (isCategory) {
                selectedCatData = myProfileActivityVM.getCategoryMutableData().getValue();
            } else {
                selectedCatData = myProfileActivityVM.getTagsMutableData().getValue();
            }

            for (CategoryResponse.CategoryData data : selectedCatData) {
                if (data != null) {
                    if (data.isChecked) {
                        if (isCategory) {
                            catIds.add(data.id);
                        } else {
                            tagIds.add(data.id);
                        }
                        ratings.add(1);
                    }
                }
            }

//            if ((selCatIds == null || selTagIds == null) && selRatings == null) {
//                return;
//            }

            if (isCategory && catIds.size() > 3) {
                toastMessage(getString(R.string.you_can_pick_any_3_category));
                return;
            }

            if (isCategory) {
                binding.chipGroup.removeAllViews();
            } else {
                binding.chipGroupTags.removeAllViews();
            }
            for (CategoryResponse.CategoryData data : selectedCatData) {
                if (data != null) {
                    if (data.isChecked) {
                        Chip chip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, isCategory ? binding.chipGroup : binding.chipGroupTags, false);
                        chip.setText(data.getName(language));
                        chip.setTag(data.id + "");
                        chip.setChecked(true);
                        chip.setClickable(false);
                        if (isCategory) {
                            binding.chipGroup.addView(chip);
                        } else {
                            binding.chipGroupTags.addView(chip);
                        }
                    }
                }
            }

            if (isCategory) {
                selCatIds = catIds;
            } else {
                selTagIds = tagIds;
            }

            selRatings = ratings;
//            dialogCategoryBinding.progressBar.setVisibility(View.VISIBLE);
//            dialogCategoryBinding.tvSend.setVisibility(View.INVISIBLE);
//            skillsActivityViewModel.addSkills(selectedSkillsId, selectedRatingsId, Integer.parseInt(binding.txtStatusCat.getTag().toString()));
            if (isCategory) {
                binding.chipGroup.setVisibility(View.VISIBLE);
                binding.txtCat.setVisibility(View.GONE);
//                binding.imgCat.setVisibility(View.VISIBLE);
            } else {
                binding.chipGroupTags.setVisibility(View.VISIBLE);
                binding.txtTags.setVisibility(View.GONE);
//                binding.imgTags.setVisibility(View.VISIBLE);
                if (binding.chipGroupTags.getChildCount() > 3) {
                    binding.txtNoTags.setText(binding.chipGroupTags.getChildCount() + "");
                    binding.txtNoTags.setVisibility(View.VISIBLE);
                } else {
                    binding.txtNoTags.setVisibility(View.GONE);
                }
            }
            isAnyChanges.postValue(true);
            dialogCat.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogCat.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogCat.show();
        dialogCat.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCat.getWindow().setAttributes(lp);

       /* dialogCat.setOnDismissListener(dialogInterface -> {
            if (adapter != null) {
                adapter.getFilter().filter("");
                myProfileActivityVM.getCategoryMutableData().setValue(adapter.getSelectedChipCopy());
            }
        });*/
    }

    private void searchData(DialogCategoryNewBinding dialogCategoryBinding, List<CategoryResponse.CategoryData> categoryData, boolean isCategory, String search) {

        if (categoryData != null) {//for pre-selected, if any selected by user
            dialogCategoryBinding.chipGroup.removeAllViews();

            for (CategoryResponse.CategoryData item : categoryData) {
                boolean matchesSearch = item.getName(language).toLowerCase().contains(search);

                if (matchesSearch) {
                    Chip chip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, dialogCategoryBinding.chipGroup, false);
                    chip.setText(isCategory ? item.getName(language) : "#" + item.getName(language));
                    chip.setTag(item.id + "");
                    chip.setChecked(item.isChecked);
                    chip.setOnCheckedChangeListener((compoundButton, b) -> item.isChecked = b);
                    dialogCategoryBinding.chipGroup.addView(chip);
                }
            }
        }
    }

    private void setTagsGroup(DialogCategoryNewBinding dialogCategoryBinding) {
        if (myProfileActivityVM.getTagsMutableData().getValue() != null) {//for pre-selected, if any selected by user
            dialogCategoryBinding.chipGroup.removeAllViews();
            for (CategoryResponse.CategoryData data : myProfileActivityVM.getTagsMutableData().getValue()) {
                if (data != null) {
                    /*if (profileData != null && profileData.tags_lists != null) {
                        for (ProfileResponse.Skill tag : profileData.tags_lists) {
                            if (tag.id == data.id) {
                                data.isChecked = true;
                                break;
                            }
                        }
                    }*/
                    if (binding.chipGroupTags.getChildCount() > 0) {
                        for (int i = 0; i < binding.chipGroupTags.getChildCount(); i++) {
                            View child = binding.chipGroupTags.getChildAt(i);

                            if (child instanceof Chip) {
                                Chip chip = (Chip) child;
                                String chipText = chip.getText().toString();
                                if (chipText.equals(data.getName(language))) {
                                    data.isChecked = true;
                                    break;
                                } else {
                                    data.isChecked = false;
                                }
                            }
                        }
                    } else {
                        data.isChecked = false;
                    }

                    Chip chip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, dialogCategoryBinding.chipGroup, false);
                    chip.setText("#" + data.getName(language));
                    chip.setTag(data.id + "");
                    chip.setChecked(data.isChecked);
                    chip.setOnCheckedChangeListener((compoundButton, b) -> {
                        data.isChecked = b;
                    });
                    dialogCategoryBinding.chipGroup.addView(chip);

                }
            }
        }
    }


    private void setCategoryGroup(DialogCategoryNewBinding dialogCategoryBinding) {
        if (myProfileActivityVM.getCategoryMutableData().getValue() != null) {//for pre-selected, if any selected by user
            dialogCategoryBinding.chipGroup.removeAllViews();
            for (CategoryResponse.CategoryData data : myProfileActivityVM.getCategoryMutableData().getValue()) {
                if (data != null) {
                    if (binding.chipGroup.getChildCount() > 0) {
                        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
                            View child = binding.chipGroup.getChildAt(i);

                            if (child instanceof Chip) {
                                Chip chip = (Chip) child;
                                String chipText = chip.getText().toString();
                                if (data.getName(language).equals(chipText)) {
                                    data.isChecked = true;
                                    break;
                                } else {
                                    data.isChecked = false;
                                }
                            }
                        }
                    } else {
                        data.isChecked = false;
                    }

                    Chip chip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, dialogCategoryBinding.chipGroup, false);
                    chip.setText(data.getName(language));
                    chip.setTag(data.id + "");
                    chip.setChecked(data.isChecked);
                    chip.setOnCheckedChangeListener((compoundButton, b) -> {
                        data.isChecked = b;
                    });
                    dialogCategoryBinding.chipGroup.addView(chip);

                }
            }
        }
    }

    private SelectSkillsActivityVM skillsActivityViewModel;

    int selectedExpertiseId = -1;

    @Override
    public void onResponseSuccess(Response<GeneralModel> response) {
        if (dialogCategorySub != null) {
            getProfile();
            dialogCategorySub.dismiss();
            //update Category dialog
        }
    }

    @Override
    public void onError() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_about:
                //showNameDialog(Utils.WindowScreen.ABOUT_ME);
                break;
            case R.id.et_mawId:
                Intent i = new Intent(this, VerifyMawthooqActivity.class);
                i.putExtra("from", true);
                startActivityForResult(i, REQ_ID_VERIFICATION);
                break;
            case R.id.rel_save:
                saveData();
                break;
            case R.id.et_dob:
                dobDialog();
                break;
            case R.id.et_gender:
                genderDialog();
                break;
            case R.id.rel_cat:
                showCategoryDialog(true);
                break;
            case R.id.rel_tags:
                showCategoryDialog(false);
                break;
            case R.id.txt_min:
            case R.id.txt_max:
                showAdsPriceDialog();
                break;
            case R.id.txt_status:
                whoCanSeeDialog(binding.txtStatus);
                break;
            case R.id.txt_status_cat:
                whoCanSeeDialog(binding.txtStatusCat);
                break;
            case R.id.txt_status_maw:
                whoCanSeeDialog(binding.txtStatusMaw);
                break;
            case R.id.txt_status_dob:
                whoCanSeeDialog(binding.txtStatusDob);
                break;
            case R.id.txt_status_gender:
                whoCanSeeDialog(binding.txtStatusGender);
                break;
            case R.id.txt_status_country:
                whoCanSeeDialog(binding.txtStatusCountry);
                break;
            case R.id.txt_status_price:
                whoCanSeeDialog(binding.txtStatusPrice);
                break;
            case R.id.txt_country:
                if (getCountriesAPI.getCountryLiveData() != null) {
                    if (getCountriesAPI.getCountryLiveData() != null && getCountriesAPI.getCountryLiveData().getValue() != null
                            && getCountriesAPI.getCountryLiveData().getValue().size() > 0) {
                        showCountrySelectDialog(getCountriesAPI.getCountryLiveData().getValue());
                    } else {
                        getCountriesAPI.getCountries();
                    }

                }
                break;
            case R.id.txt_city:
                if (updateLocationActivityVM.getCityLiveData() != null && updateLocationActivityVM.getCityLiveData().getValue() != null
                        && updateLocationActivityVM.getCityLiveData().getValue().size() > 0) {
                    showCitySelectDialog(updateLocationActivityVM.getCityLiveData().getValue());
                } else if (profileData.stateID != null) {
                    updateLocationActivityVM.getCityFromCountryState(2849);
                }
                break;
        }
    }

    private void saveData() {

        if (Boolean.FALSE.equals(isAnyChanges.getValue())) {
            finish();
            finishToRight();
            return;
        }
                /*if (TextUtils.isEmpty(binding.etAbout.getText().toString().trim())) {
                    toastMessage(getString(R.string.about_us));
                    return;
                }*/
        if (TextUtils.isEmpty(binding.etDob.getText().toString().trim())) {
            toastMessage(getString(R.string.enter_your_date_of_birth));
            return;
        }
        if (TextUtils.isEmpty(binding.etGender.getText().toString().trim())) {
            toastMessage(getString(R.string.select_your_gender));
            return;
        }
        if (TextUtils.isEmpty(binding.etMin.getText().toString().trim())) {
            toastMessage(getString(R.string.min_price_can_not_be_empty));
            return;
        }
        if (TextUtils.isEmpty(binding.etMax.getText().toString().trim())) {
            toastMessage(getString(R.string.max_price_can_t_be_empty_or_zero));
            return;
        }
        if (profileData.mawthooq_status != null) {
            myProfileActivityVM.updateProfile(0, Integer.parseInt(binding.txtStatusMaw.getTag().toString()), profileData.mawthooq_status.id);
        }
        if (selCatIds != null) {
            skillsActivityViewModel.addCategory(selCatIds, selRatings);
        }
        if (selTagIds != null) {
            skillsActivityViewModel.addTags(selTagIds, selRatings);
        }
        updateLocationActivityVM.updateLocation((Integer) binding.txtCountry.getTag(), (Integer) binding.txtCity.getTag(), -1);

        myProfileActivityVM.updateHeadlines(binding.etAbout.getText().toString(), Integer.parseInt(binding.txtStatus.getTag().toString()));
        workExperienceActivityVM.updateExperience(Integer.parseInt(binding.txtStatusCat.getTag().toString()));

        myProfileActivityVM.updateProfile(3, binding.etDob.getTag().toString(),
                Integer.parseInt(binding.etGender.getTag().toString()), Double.parseDouble(binding.etMin.getText().toString()), Double.parseDouble(binding.etMax.getText().toString()), Integer.parseInt(binding.txtStatusDob.getTag().toString()), Integer.parseInt(binding.txtStatusGender.getTag().toString()), Integer.parseInt(binding.txtStatusPrice.getTag().toString())
                , Integer.parseInt(binding.txtStatusCountry.getTag().toString()));
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

    public void dobDialog() {

        Dialog dialogDiscard = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDiscard.setTitle(null);
        DialogDobBinding dialogDiscardBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_dob, null, false);
        dialogDiscardBinding.txtTitle.setText(getString(R.string.birthdate));
        dialogDiscardBinding.tvSend.setText(getString(R.string.save));
        if (language.equals("ar")) {
            setArFont(dialogDiscardBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(dialogDiscardBinding.tvSend, Constants.FONT_AR_BOLD);
        }

        dialogDiscard.setContentView(dialogDiscardBinding.getRoot());
        dialogDiscard.setCancelable(true);

        if (profileData.birth_date != null) {
            String monthLang;
            String dayLang;
            String yearLang;
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dateObj = null;
            try {
                dateObj = curFormater.parse(profileData.birth_date.split("T")[0]);
                SimpleDateFormat postFormater = new SimpleDateFormat("MMM", Locale.getDefault());
                SimpleDateFormat postFormaterDD = new SimpleDateFormat("dd", Locale.getDefault());
                SimpleDateFormat postFormaterYY = new SimpleDateFormat("yyyy", Locale.getDefault());
                monthLang = postFormater.format(dateObj);
                dayLang = postFormaterDD.format(dateObj);
                yearLang = postFormaterYY.format(dateObj);
//                if (language.equals("ar")) {
//                    etDob.setText(dayLang + "-" + monthLang + "-" + yearLang);
//                } else {
//                    String d = profileData.birth_date.split("T")[0];
//                    etDob.setText(String.format("%s-%s-%s", d.split("-")[2], d.split("-")[1], d.split("-")[0]));
//                }
//                etDob.setTag(profileData.birth_date.split("T")[0]);

                if (profileData.birth_date.split("T")[0] != null && !profileData.birth_date.split("T")[0].equals("0000-00-00")) {
                    int m = Integer.parseInt((profileData.birth_date.split("T")[0].split("-")[1]));
                    if ((profileData.birth_date.split("T")[0].split("-")[1]).startsWith("0")) {
                        m = Integer.parseInt((profileData.birth_date.split("T")[0].split("-")[1]).replace("0", ""));
                    }
                    dialogDiscardBinding.birthDatePicker.updateDate(Integer.parseInt(profileData.birth_date.split("T")[0].split("-")[0]), m - 1, Integer.parseInt(profileData.birth_date.split("T")[0].split("-")[2]));
                }

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        dialogDiscardBinding.birthDatePicker.setMaxDate(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialogDiscardBinding.birthDatePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
                // Do something with the selected date
                String days;
                if (dayOfMonth < 10) {
                    days = "0" + dayOfMonth;
                } else {
                    days = "" + dayOfMonth;
                }

                String month;
                if (((monthOfYear + 1)) < 10) {
                    month = "0" + (monthOfYear + 1);
                } else {
                    month = "" + (monthOfYear + 1);
                }
                String monthLang = month;
                String dayLang = "" + days;
                String yearLang = "" + year;
                String selectedDate1 = year + "-" + month + "-" + days;
                String selectedDate;

                if (language.equals("ar")) {
                    SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    Date dateObj = null;
                    try {
                        dateObj = curFormater.parse(selectedDate1);
                        SimpleDateFormat postFormater = new SimpleDateFormat("MMM", Locale.getDefault());
                        SimpleDateFormat postFormaterDD = new SimpleDateFormat("dd", Locale.getDefault());
                        SimpleDateFormat postFormaterYY = new SimpleDateFormat("yyyy", Locale.getDefault());
                        monthLang = postFormater.format(dateObj);
                        dayLang = postFormaterDD.format(dateObj);
                        yearLang = postFormaterYY.format(dateObj);
                        monthLang = postFormater.format(dateObj);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    selectedDate = dayLang + "-" + monthLang + "-" + yearLang;
                } else {
                    selectedDate = days + "-" + monthLang + "-" + year;
                }

                binding.etDob.setText(selectedDate);
                binding.etDob.setTag(selectedDate1);
            });
        }


        dialogDiscardBinding.tvCancel.setOnClickListener(v -> {
            dialogDiscard.dismiss();
        });

        dialogDiscardBinding.relSave.setOnClickListener(v -> {

            dialogDiscard.dismiss();

        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDiscard.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDiscard.show();
        dialogDiscard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDiscard.getWindow().setAttributes(lp);
    }

    public void genderDialog() {
        AtomicInteger selectedGender = new AtomicInteger();

        Dialog dialogDiscard = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDiscard.setTitle(null);
        DialogGenderBinding dialogDiscardBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_gender, null, false);
        dialogDiscardBinding.txtTitle.setText(getString(R.string.gender));
        dialogDiscardBinding.txtMale.setText(getString(R.string.male));
        dialogDiscardBinding.txtFemale.setText(getString(R.string.female));
//        dialogDiscardBinding.txtLblMe.setText(getString(R.string.others));
        dialogDiscardBinding.tvSend.setText(getString(R.string.save));
        dialogDiscard.setContentView(dialogDiscardBinding.getRoot());
        dialogDiscard.setCancelable(true);
        if (language.equals("ar")) {
            setArFont(dialogDiscardBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(dialogDiscardBinding.txtMale, Constants.FONT_AR_REGULAR);
            setArFont(dialogDiscardBinding.txtFemale, Constants.FONT_AR_REGULAR);
            setArFont(dialogDiscardBinding.tvSend, Constants.FONT_AR_BOLD);
        }
        int genderTag = 1;
        if (binding.etGender.getTag() != null) {
            genderTag = Integer.parseInt(binding.etGender.getTag().toString());
        }

        if (genderTag == 1) {//male
            dialogDiscardBinding.imgMale.setImageResource(R.drawable.radio_button_active);
            dialogDiscardBinding.imgFemale.setImageResource(R.drawable.circle_uncheck);
            dialogDiscardBinding.relMale.setBackgroundResource(R.drawable.white_button_bg_border_7);
            dialogDiscardBinding.relFemale.setBackgroundResource(R.drawable.gray_l_border_6);
        } else if (genderTag == 2) {//female
            dialogDiscardBinding.imgFemale.setImageResource(R.drawable.radio_button_active);
            dialogDiscardBinding.imgMale.setImageResource(R.drawable.circle_uncheck);
            dialogDiscardBinding.relFemale.setBackgroundResource(R.drawable.white_button_bg_border_7);
            dialogDiscardBinding.relMale.setBackgroundResource(R.drawable.gray_l_border_6);
        } /*else if (profileData.gender == 3) {
                dialogDiscardBinding.chkOther.setChecked(true);
            }*/
        selectedGender.set(profileData.gender);

//        dialogDiscardBinding.chkMale.setOnClickListener(view -> {
//            selectedGender.set(1);
//            maleView(dialogDiscardBinding);
//        });
//        dialogDiscardBinding.chkFemale.setOnClickListener(view -> {
//            selectedGender.set(2);
//            femaleView(dialogDiscardBinding);
//        });
//        dialogDiscardBinding.chkOther.setOnClickListener(view -> {
//            selectedGender.set(3);
//            otherView(dialogDiscardBinding);
//        });

        dialogDiscardBinding.relFemale.setOnClickListener(view -> {
            selectedGender.set(2);
            dialogDiscardBinding.imgFemale.setImageResource(R.drawable.radio_button_active);
            dialogDiscardBinding.imgMale.setImageResource(R.drawable.circle_uncheck);
            dialogDiscardBinding.relFemale.setBackgroundResource(R.drawable.white_button_bg_border_7);
            dialogDiscardBinding.relMale.setBackgroundResource(R.drawable.gray_l_border_6);
            DrawableCompat.setTint(binding.relSave.getBackground(), ContextCompat.getColor(OverViewActivityNew.this, R.color.black));
            binding.tvSave.setTextColor(getResources().getColor(R.color.white));
        });
        dialogDiscardBinding.relMale.setOnClickListener(view -> {
            selectedGender.set(1);
            dialogDiscardBinding.imgMale.setImageResource(R.drawable.radio_button_active);
            dialogDiscardBinding.imgFemale.setImageResource(R.drawable.circle_uncheck);
            dialogDiscardBinding.relMale.setBackgroundResource(R.drawable.white_button_bg_border_7);
            dialogDiscardBinding.relFemale.setBackgroundResource(R.drawable.gray_l_border_6);
            DrawableCompat.setTint(binding.relSave.getBackground(), ContextCompat.getColor(OverViewActivityNew.this, R.color.black));
            binding.tvSave.setTextColor(getResources().getColor(R.color.white));
        });

//        dialogDiscardBinding.relMale.setOnClickListener(view -> {
//            selectedGender.set(1);
//            maleView(dialogDiscardBinding);
//        });
//
//        dialogDiscardBinding.relFemale.setOnClickListener(view -> {
//            selectedGender.set(2);
//            femaleView(dialogDiscardBinding);
//        });

//        dialogDiscardBinding.relOther.setOnClickListener(view -> {
//            selectedGender.set(3);
//            otherView(dialogDiscardBinding);
//        });

        dialogDiscardBinding.tvCancel.setOnClickListener(v -> {
            dialogDiscard.dismiss();
        });

        dialogDiscardBinding.relSave.setOnClickListener(v -> {
            if (selectedGender.get() == 1) {
                binding.etGender.setText(getString(R.string.male));
            } else if (selectedGender.get() == 2) {
                binding.etGender.setText(getString(R.string.female));
            } else if (selectedGender.get() == 3) {
                binding.etGender.setText(getString(R.string.others));
            }
            binding.etGender.setTag(selectedGender.get() + "");
            dialogDiscard.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDiscard.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDiscard.show();
        dialogDiscard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDiscard.getWindow().setAttributes(lp);
    }

//    private void otherView(DialogGenderBinding dialogDiscardBinding) {
//        dialogDiscardBinding.chkMale.setChecked(false);
//        dialogDiscardBinding.chkFemale.setChecked(false);
//        dialogDiscardBinding.chkOther.setChecked(true);
//    }

//    private void femaleView(DialogGenderBinding dialogDiscardBinding) {
//        dialogDiscardBinding.chkMale.setChecked(false);
//        dialogDiscardBinding.chkFemale.setChecked(true);
//        dialogDiscardBinding.chkOther.setChecked(false);
//    }

//    private void maleView(DialogGenderBinding dialogDiscardBinding) {
//        dialogDiscardBinding.chkMale.setChecked(true);
//        dialogDiscardBinding.chkFemale.setChecked(false);
//        dialogDiscardBinding.chkOther.setChecked(false);
//    }

    public void whoCanSeeDialog(TextViewSFTextPro txtStatus) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_who);
        dialog.setCancelable(true);
        RadioButton chkPublic = dialog.findViewById(R.id.chk_public);
        RadioButton chkBrand = dialog.findViewById(R.id.chk_brand);
        RadioButton chkMe = dialog.findViewById(R.id.chk_me);
        ImageView tvCancel = dialog.findViewById(R.id.tv_cancel);
        RelativeLayout relSave = dialog.findViewById(R.id.rel_save);
        TextView txtTitle = dialog.findViewById(R.id.txt_title);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        txtTitle.setText(getString(R.string.who_can_see_this_item_1));

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

        int stat = Integer.parseInt(txtStatus.getTag().toString());
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

        tvCancel.setOnClickListener(v -> dialog.dismiss());

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
//            myStoreActivityVM.updateStores(status, companies.id, "", companies.title, companies.link);

            setPublicStatusValue(status, txtStatus);
            isAnyChanges.postValue(true);
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

    void showCountrySelectDialog(List<CountryResponse.CountryData> arrayList) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialog.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        etSearch.setHint(String.format(getString(R.string.search_for), getString(R.string.country).toLowerCase()));

        rvTypes.setLayoutManager(new LinearLayoutManager(this));
        try {
            if (arrayList != null && arrayList.size() > 0) {
                for (CountryResponse.CountryData data : arrayList) {
                    data.isSelected = data.getCountryName(language).equalsIgnoreCase(binding.txtCountry.getText().toString());
                }
                selectCountryAdapter = new SelectCountryAdapter(this, arrayList);
                rvTypes.setAdapter(selectCountryAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvApply.setOnClickListener(v -> {
            if (selectCountryAdapter != null && selectCountryAdapter.getSelectedItem() != null) {
                binding.txtCountry.setText(selectCountryAdapter.getSelectedItem().getCountryName(language));
                binding.txtCountry.setTag(selectCountryAdapter.getSelectedItem().id);
                dialog.dismiss();
//                binding.txtState.setText("");
//                binding.txtState.setHint(getString(R.string.select_state));//clear state & city, when select other country
                binding.txtCity.setText("");
                binding.txtCity.setHint(getString(R.string.select_city));
//                binding.txtState.setTag(null);//clear tag [i.e ID]
                binding.txtCity.setTag(null);

//                if (updateLocationActivityVM.getStateLiveData() != null && updateLocationActivityVM.getStateLiveData().getValue() != null) {
//                    updateLocationActivityVM.getStateLiveData().getValue().clear();
//                }

                if (updateLocationActivityVM.getCityLiveData() != null && updateLocationActivityVM.getCityLiveData().getValue() != null) {
                    updateLocationActivityVM.getCityLiveData().getValue().clear();
                }
                if (selectCountryAdapter.getSelectedItem().id == 194) {
                    binding.relCity.setVisibility(View.VISIBLE);
                    updateLocationActivityVM.getCityFromCountryState(2849);
                } else {
                    binding.relCity.setVisibility(View.GONE);
                }

                isAnyChanges.postValue(true);
            } else {
                toastMessage(getString(R.string.please_select_one_item));
            }
            dialog.dismiss();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectCountryAdapter != null)
                    selectCountryAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(this, etSearch)));
        etSearch.requestFocus();
    }

    void showCitySelectDialog(List<CityResponse.CityData> arrayList) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialog.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        etSearch.setHint(String.format(getString(R.string.search_for), getString(R.string.country).toLowerCase()));

        rvTypes.setLayoutManager(new LinearLayoutManager(this));
        try {
            if (arrayList != null && arrayList.size() > 0) {
                for (CityResponse.CityData data : arrayList) {
                    data.isSelected = data.getCityName(language).equalsIgnoreCase(binding.txtCity.getText().toString());
                }
                selectCityAdapter = new SelectCityAdapter(this, arrayList);
                rvTypes.setAdapter(selectCityAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvApply.setOnClickListener(v -> {
            if (selectCityAdapter != null && selectCityAdapter.getSelectedItem() != null) {
                binding.txtCity.setText(selectCityAdapter.getSelectedItem().getCityName(language));
                binding.txtCity.setTag(selectCityAdapter.getSelectedItem().id);
                isAnyChanges.postValue(true);
                dialog.dismiss();
            } else {
                toastMessage(getString(R.string.please_select_one_item));
            }
            dialog.dismiss();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectCityAdapter != null)
                    selectCityAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(this, etSearch)));
        etSearch.requestFocus();
    }

    Dialog dialogDiscard;
    DialogDiscardBinding dialogDiscardBinding;

    public void discardChangesDialog() {
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
            finish();
            finishToRight();
        });

        dialogDiscardBinding.relSave.setOnClickListener(v -> {
            saveData();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDiscard.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDiscard.show();
        dialogDiscard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDiscard.getWindow().setAttributes(lp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ID_VERIFICATION) {
            binding.etAbout.removeTextChangedListener(null);
            binding.etMin.removeTextChangedListener(null);
            binding.etMax.removeTextChangedListener(null);
            binding.etGender.removeTextChangedListener(null);
            binding.etDob.removeTextChangedListener(null);
            //isAnyChanges.postValue(isAnyChanges.getValue());
            getProfile();
        }
    }
}
