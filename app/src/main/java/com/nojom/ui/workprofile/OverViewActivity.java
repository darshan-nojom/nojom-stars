package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.adapter.SelectCityAdapter;
import com.nojom.adapter.SelectCountryAdapter;
import com.nojom.adapter.SelectStateAdapter;
import com.nojom.adapter.SkillsAdapter;
import com.nojom.apis.GetServiceCategoryAPI;
import com.nojom.apis.ViewSkillAPI;
import com.nojom.databinding.ActivityOverviewBinding;
import com.nojom.databinding.ActivitySelectSkillsBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.model.ProfileResponse;
import com.nojom.model.Skill;
import com.nojom.model.UserSkillsModel;
import com.nojom.model.VerifyID;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.UpdatePasswordActivity;
import com.nojom.util.Constants;
import com.nojom.util.EndlessRecyclerViewScrollListener;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Preferences;
import com.nojom.util.SegmentedButtonGroupNew;
import com.nojom.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Response;

public class OverViewActivity extends BaseActivity implements ResponseListener, RecyclerviewAdapter.OnViewBindListner, View.OnClickListener {
    private ActivityOverviewBinding binding;
    private ProfileResponse profileData;

    private MyProfileActivityVM myProfileActivityVM;
    private ViewSkillAPI viewSkillAPI;

    private VerifyIDActivityVM verifyIDActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_overview);
        myProfileActivityVM = ViewModelProviders.of(this).get(MyProfileActivityVM.class);
        myProfileActivityVM.init(this, null);
        verifyIDActivityVM = ViewModelProviders.of(this).get(VerifyIDActivityVM.class);
        verifyIDActivityVM.getMawthooqList(this, "maw");
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        setOnProfileLoadListener(this::onProfileLoad);

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvTitle.setText(getString(R.string.profile));

        profileData = Preferences.getProfileData(this);
        verifyIDActivityVM.getListMutableLiveData().observe(this, this::setMaqData);
        updateUI();

        binding.txtAbout.setOnClickListener(this);
        binding.txtCategory.setOnClickListener(this);
        binding.txtMawthooq.setOnClickListener(this);
        binding.txtDob.setOnClickListener(this);
        binding.txtGender.setOnClickListener(this);
        binding.txtMin.setOnClickListener(this);
        binding.txtMax.setOnClickListener(this);

    }

    private void setMaqData(List<VerifyID.Data> verifyIdsList) {

        if (verifyIdsList != null && verifyIdsList.size() > 0) {
//            mawData = verifyIdsList;
            if (verifyIdsList.get(0).data != null) {
                binding.txtMawthooq.setText(String.format("%s", verifyIdsList.get(0).data));
//                binding.tvMawId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_password, 0);
            } else {
//                mawData = null;
                binding.txtMawthooq.setText("-");
            }

        } else {
//            mawData = null;
            binding.txtMawthooq.setText("-");
        }
    }

    private void updateUI() {
        profileData = Preferences.getProfileData(this);

        if (profileData != null) {
            if (profileData.about_me != null) {
                binding.txtAbout.setText(profileData.about_me);
            } else {
                binding.txtAbout.setTextColor(getColor(R.color.textgrayAccent));
                binding.txtAbout.setText(getString(R.string.about_me));
            }
            if (profileData.gender != null) {
                binding.txtGender.setText(profileData.gender == 2 ? getString(R.string.male) : getString(R.string.female));
            }
            if (profileData.minPrice != null && profileData.maxPrice != null) {
                if (getCurrency().equals("SAR")) {
                    binding.txtMin.setText(String.format("%s", Utils.getDecimalValue(String.valueOf(profileData.minPrice))));
                    binding.txtMax.setText(String.format("%s", Utils.getDecimalValue(String.valueOf(profileData.maxPrice))));
                    binding.txtMaxSar.setText(String.format("%s", getString(R.string.sar)));
                    binding.txtMinSar.setText(String.format("%s", getString(R.string.sar)));
                } else {
                    binding.txtMin.setText(String.format("%s", Utils.getDecimalValue(String.valueOf(profileData.minPrice))));
                    binding.txtMax.setText(String.format("%s", Utils.getDecimalValue(String.valueOf(profileData.maxPrice))));
                    binding.txtMaxSar.setText(String.format("%s", getString(R.string.dollar)));
                    binding.txtMinSar.setText(String.format("%s", getString(R.string.dollar)));
                }
            }
            if (profileData.birth_date != null) {

                String monthLang;
                SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date dateObj = null;
                try {
                    dateObj = curFormater.parse(profileData.birth_date.split("T")[0]);
                    SimpleDateFormat postFormater = new SimpleDateFormat("MMM", Locale.getDefault());
                    if (dateObj != null) {
                        monthLang = postFormater.format(dateObj);

                        if (language.equals("ar")) {
                            binding.txtDob.setText(profileData.birth_date.split("T")[0].split("-")[2] + "-" + monthLang + "-" + profileData.birth_date.split("T")[0].split("-")[0]);
                        } else {
                            String d = profileData.birth_date.split("T")[0];
                            binding.txtDob.setText(String.format("%s-%s-%s", d.split("-")[2], d.split("-")[1], d.split("-")[0]));
                        }
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
            if (profileData.expertise != null) {
                binding.txtCategory.setText(profileData.expertise.getName(language));
            }


        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finishToRight();
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

        if (dialogCategory != null && dialogCategory.isShowing()) {
            showCategoryDialog(true);
        }
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

    private void showCategoryDialog(boolean isUpadte) {
        RecyclerView rvCat, rvTag;
        if (!isUpadte) {
            dialogCategory = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
            dialogCategory.setTitle(null);
            dialogCategory.setContentView(R.layout.dialog_profile_category);
            dialogCategory.setCancelable(true);
            RelativeLayout relSave = dialogCategory.findViewById(R.id.rel_save);
            TextView txtCancel = dialogCategory.findViewById(R.id.txt_cancel);
            LinearLayout linCat = dialogCategory.findViewById(R.id.lin_category);
            LinearLayout linTag = dialogCategory.findViewById(R.id.lin_tags);
            rvCat = dialogCategory.findViewById(R.id.rv_category);
            rvTag = dialogCategory.findViewById(R.id.rv_tags);

            viewSkillAPI = new ViewSkillAPI();
            viewSkillAPI.init(this);

            linCat.setOnClickListener(v -> selCategoryDialog());

            linTag.setOnClickListener(v -> {
                viewSkillAPI.getSkillsList(1, null);
            });

            txtCancel.setOnClickListener(v -> dialogCategory.dismiss());
            relSave.setOnClickListener(v -> dialogCategory.dismiss());

            viewSkillAPI.getUserModel().observe(this, userSkillsModel -> {
                if (dialogTag == null && userSkillsModel != null && userSkillsModel.skillLists != null) {
                    selectedSkillLists = new ArrayList<>();
                    for (UserSkillsModel.SkillLists skills : userSkillsModel.skillLists) {
                        if (skills.psId != null) {

                            if (!selectedSkillLists.contains(skills)) {
                                skills.isSelected = true;
                                selectedSkillLists.add(skills);
                            } else {
                                skills.isSelected = true;
                            }

                        } else {
                            if (selectedSkillLists.size() > 0) {
                                for (UserSkillsModel.SkillLists selectedSkill : selectedSkillLists) {
                                    if (selectedSkill.id == skills.id) {
                                        skills.isSelected = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

//                    Intent i = new Intent(this, SelectSkillsActivity.class);
//                    i.putExtra(Constants.IS_EDIT, true);
//                    i.putExtra("size", profileData.skills != null ? profileData.skills.size() : 0);
//                    i.putExtra(SKILLS, userSkillsModel.skillLists);
//                    i.putExtra(SKILL_COUNT, userSkillsModel.skillCount);
//                    i.putExtra(SELECTED_SKILL, selectedSkillLists);

                    selectedSkillCount = profileData.skills != null ? profileData.skills.size() : 0;
                    skillLists = userSkillsModel.skillLists;
                    totalSkillCount = userSkillsModel.skillCount;
                    if (dialogTag == null) {
                        selTagDialog();
                    }
                }

            });
        } else {
            rvCat = dialogCategory.findViewById(R.id.rv_category);
            rvTag = dialogCategory.findViewById(R.id.rv_tags);
        }

        ArrayList<Skill> expertiseList = new ArrayList<>();
        try {
            if (profileData != null && profileData.expertise != null && profileData.expertise.id != null) {
                expertiseList.add(new Skill(profileData.expertise.id, profileData.expertise.getName(language), Utils.getExperienceLevel(profileData.expertise.length)));
            }
            SkillsAdapter mExpertiseAdapter = new SkillsAdapter(this, expertiseList, null, false);
            mExpertiseAdapter.setSELECTED_TAG("EXPERT");
//                if (expertiseList.size() > 0) {//selected skill id
//                    selectedProfileSkillId = expertiseList.get(0).skillId;
//                }
            rvCat.setAdapter(mExpertiseAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Skill> skillList = new ArrayList<>();
        try {
            if (profileData != null) {
                if (profileData.skills != null && profileData.skills.size() > 0) {
                    for (ProfileResponse.Skill data : profileData.skills) {
                        skillList.add(new Skill(data.getName(language), Utils.getRatingLevel(data.rating)));
                    }
                }

                SkillsAdapter mSkillAdapter = new SkillsAdapter(this, skillList, null, false);
                mSkillAdapter.setSELECTED_TAG("SKILL");
                rvTag.setAdapter(mSkillAdapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isUpadte) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(dialogCategory.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.BOTTOM;
            dialogCategory.show();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            lp.height = (int) (displayMetrics.heightPixels * 0.95f);
            dialogCategory.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogCategory.getWindow().setAttributes(lp);
        }
    }

    private List<GigSubCategoryModel.Data> servicesList;
    private RecyclerviewAdapter mAdapter;

    private WorkExperienceActivityVM workExperienceActivityVM;

    private void selCategoryDialog() {
        dialogCategorySub = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogCategorySub.setTitle(null);
        dialogCategorySub.setContentView(R.layout.dialog_profile_category_sub);
        dialogCategorySub.setCancelable(true);
        RelativeLayout relSave = dialogCategorySub.findViewById(R.id.rel_save);
        TextView txtCancel = dialogCategorySub.findViewById(R.id.txt_cancel);
        RecyclerView rvSkills = dialogCategorySub.findViewById(R.id.rv_category);

        workExperienceActivityVM = ViewModelProviders.of(this).get(WorkExperienceActivityVM.class);
        workExperienceActivityVM.init(this);
        workExperienceActivityVM.setResponseListener(this);

        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        rvSkills.setLayoutManager(manager);
        rvSkills.addItemDecoration(new EqualSpacingItemDecoration(16));

        servicesList = Preferences.getInfSubCatList(this);

        if (servicesList != null && servicesList.size() > 0) {
            try {
                if (profileData != null && profileData.expertise != null) {
                    for (GigSubCategoryModel.Data data : servicesList) {
                        if (profileData.expertise.id != null && profileData.expertise.id == data.id) {
                            selectedExpertiseId = data.id;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mAdapter = new RecyclerviewAdapter((ArrayList<?>) servicesList, R.layout.item_skills_edit, OverViewActivity.this);
            rvSkills.setAdapter(mAdapter);
            rvSkills.setFocusable(false);

        } else {
            GetServiceCategoryAPI getServiceCategoryAPI = new GetServiceCategoryAPI();
            getServiceCategoryAPI.init(this);
            getServiceCategoryAPI.setServiceCategoryList();
            getServiceCategoryAPI.getServiceCategoriesById(4352);

            getServiceCategoryAPI.getGigSubCatList().observe(this, data -> {
                servicesList = data;

                try {
                    if (profileData != null && profileData.expertise != null) {
                        for (GigSubCategoryModel.Data data1 : servicesList) {
                            if (profileData.expertise.id != null && profileData.expertise.id == data1.id) {
                                selectedExpertiseId = data1.id;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mAdapter = new RecyclerviewAdapter((ArrayList<?>) data, R.layout.item_skills_edit, OverViewActivity.this);
                rvSkills.setAdapter(mAdapter);
                rvSkills.setFocusable(false);
            });
        }

        txtCancel.setOnClickListener(v -> dialogCategorySub.dismiss());
        relSave.setOnClickListener(v -> {
            if (selectedExpertiseId == -1) {
                toastMessage(getString(R.string.select_your_category));
                return;
            }
            //upadte skill (selectdExpertise id) and save close dialog
            workExperienceActivityVM.updateExperience("", "" + selectedExpertiseId);
            //dialogCategorySub.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogCategorySub.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogCategorySub.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        dialogCategorySub.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCategorySub.getWindow().setAttributes(lp);

        dialogCategorySub.setOnDismissListener(dialog -> mAdapter = null);
    }

    private EndlessRecyclerViewScrollListener scrollListener;
    private int pageNo = 1;
    private boolean isSearchCall, isUpdateSkillCompulsory;
    private SelectSkillsActivityVM skillsActivityViewModel;
    private int totalSkillCount;
    private ArrayList<UserSkillsModel.SkillLists> selectedSkillLists, skillLists;
    private RecyclerviewAdapter selectedAdapter;
    private int selectedSkillCount = 0;

    ActivitySelectSkillsBinding bindingTag;

    private void selTagDialog() {
        dialogTag = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogTag.setTitle(null);
        bindingTag = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_select_skills, null, false);
        dialogTag.setContentView(bindingTag.getRoot());
        dialogTag.setCancelable(true);
        bindingTag.etSearch.setHint(getString(R.string.search_for_tags));
        skillsActivityViewModel = ViewModelProviders.of(this).get(SelectSkillsActivityVM.class);
        skillsActivityViewModel.init(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        bindingTag.rvSelectedSkills.setLayoutManager(linearLayoutManager);

        selectedAdapter = null;

        if (skillLists == null || skillLists.size() == 0 || totalSkillCount == 0) {//fresh user or redirect from home screen dialog
            viewSkillAPI.getSkillsList(1, null);
        }

//        binding.toolbar.imgBack.setOnClickListener(v -> {
//            if (isUpdateSkillCompulsory) {
////                finish();
////                System.exit(0);
//            } else {
//                onBackPressed();
//            }
//        });
//        bindingTag.toolbar.tvSave.setOnClickListener(v -> onClickSave());
//        bindingTag.toolbar.imgBack.setVisibility(View.VISIBLE);
//        bindingTag.toolbar.tvEditCancel.setVisibility(View.GONE);
//        bindingTag.toolbar.rlEdit.setVisibility(View.VISIBLE);
//        bindingTag.toolbar.tvToolbarTitle.setText(getString(R.string.select_skills));

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
                    pageNo = page;
                    viewSkillAPI.getSkillsList(pageNo, Objects.requireNonNull(bindingTag.etSearch.getText()).toString().trim());
                }
            }
        };

        bindingTag.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                pageNo = 1;
                selectedAdapter = null;
                if (!isSearchCall) {
                    skillsActivityViewModel.getIsSearch().postValue(true);
                    if (!TextUtils.isEmpty(Objects.requireNonNull(bindingTag.etSearch.getText()).toString().trim())) {
                        viewSkillAPI.getSkillsList(pageNo, bindingTag.etSearch.getText().toString().trim());
                    } else {
                        viewSkillAPI.getSkillsList(pageNo, null);
                    }
                }
                return true;
            }
            return false;
        });

        bindingTag.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    pageNo = 1;
                    selectedAdapter = null;
                    viewSkillAPI.getSkillsList(pageNo, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        skillsActivityViewModel.getMutableCounter().observe(this, counter -> bindingTag.tvTotalSkill.setText(String.format(" / %d", counter)));

        skillsActivityViewModel.getMutablePageNo().observe(this, integer -> {
            if (scrollListener != null) {
                scrollListener.resetState();
            }
            if (pageNo == 1) {
                bindingTag.shimmerLayout.startShimmer();
                bindingTag.shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                bindingTag.shimmerLayout.stopShimmer();
                bindingTag.shimmerLayout.setVisibility(View.GONE);
            }
        });

        skillsActivityViewModel.getIsSearch().observe(this, isSearch -> {
            isSearchCall = isSearch;
            bindingTag.shimmerLayout.stopShimmer();
            bindingTag.shimmerLayout.setVisibility(View.GONE);
            bindingTag.toolbar.tvSave.setVisibility(View.VISIBLE);
            bindingTag.toolbar.progressBar.setVisibility(View.GONE);
            disableEnableTouch(false);
        });

        //get data from previous activity and set here
        if (selectedSkillLists != null && selectedSkillLists.size() > 0) {
            skillsActivityViewModel.getSelectedListMutableLiveData().postValue(null);
            skillsActivityViewModel.getSelectedListMutableLiveData().postValue(selectedSkillLists);
        }

        if (skillLists != null) {
            bindingTag.shimmerLayout.stopShimmer();
            bindingTag.shimmerLayout.setVisibility(View.GONE);

            skillsActivityViewModel.getMutableCounter().postValue(totalSkillCount);
//            skillsActivityViewModel.getListMutableLiveData().postValue(new ArrayList<>());
//            skillsActivityViewModel.getListMutableLiveData().postValue(skillLists);
        }


        skillsActivityViewModel.getListMutableLiveData().observe(this, this::setAdapter);

        skillsActivityViewModel.getIsSkillAdded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                if (dialogTag != null) {
                    dialogTag.dismiss();

                }
                getProfile();

            }
        });

        viewSkillAPI.getIsShowProgress().observe(this, aBoolean -> {
            if (aBoolean) {
                if (pageNo == 1) {
                    skillLists = new ArrayList<>();
                    skillsActivityViewModel.getMutablePageNo().postValue(pageNo);
                }
            } else {
                skillsActivityViewModel.getIsSearch().postValue(false);
            }
        });

        viewSkillAPI.getUserModel().observe(this, userSkillsModel1 -> {
            skillLists = new ArrayList<>();
            if (userSkillsModel1 != null && userSkillsModel1.skillLists != null) {

                for (UserSkillsModel.SkillLists skills : userSkillsModel1.skillLists) {
//                    if (skills.psId != null) {
//
//                        if (selectedSkillLists != null && !selectedSkillLists.contains(skills)) {
//                            skills.isSelected = true;
//                            selectedSkillLists.add(skills);
//                        } else {
//                            skills.isSelected = true;
//                        }
//
//                    } else {
                    if (skillsActivityViewModel.getSelectedListMutableLiveData().getValue() != null && skillsActivityViewModel.getSelectedListMutableLiveData().getValue().size() > 0) {
                        for (UserSkillsModel.SkillLists selectedSkill : skillsActivityViewModel.getSelectedListMutableLiveData().getValue()) {
                            if (selectedSkill.id == skills.id) {
                                skills.isSelected = true;
                                selectedSkill.isSelected = true;
                                break;
                            }
                        }
                    }
//                    }
                }
                skillLists.addAll(userSkillsModel1.skillLists);
                totalSkillCount = selectedSkillCount;
//                skillsActivityViewModel.getSelectedListMutableLiveData().postValue(selectedSkillLists);
                skillsActivityViewModel.getMutableCounter().postValue(userSkillsModel1.skillCount);
                skillsActivityViewModel.getListMutableLiveData().postValue(skillLists);

            }
            skillsActivityViewModel.getIsSearch().postValue(false);
        });


        bindingTag.txtCancel.setOnClickListener(v -> dialogTag.dismiss());
        bindingTag.relSave.setOnClickListener(v -> {

            try {
                ArrayList<UserSkillsModel.SkillLists> selectedSkillLists = skillsActivityViewModel.getSelectedListMutableLiveData().getValue();
                if (selectedSkillLists != null && selectedSkillLists.size() > 0) {
                    for (UserSkillsModel.SkillLists skill : selectedSkillLists) {
                        if (skill.rating != null && isEmpty(String.valueOf(skill.rating))) {
                            validationError(getString(R.string.please_select_experience_level));
                            return;
                        }
                    }

                    StringBuilder skillIds = null;
                    StringBuilder ratingIds = null;
                    for (UserSkillsModel.SkillLists skill : skillsActivityViewModel.getSelectedListMutableLiveData().getValue()) {
                        skillIds = (skillIds == null ? new StringBuilder() : skillIds.append(",")).append(skill.id);
                        ratingIds = (ratingIds == null ? new StringBuilder() : ratingIds.append(",")).append(skill.rating != null ? skill.rating : skill.selectedRating);
                    }

                    String skillsId = skillIds == null ? "" : skillIds.toString();
                    String ratingsId = ratingIds == null ? "" : ratingIds.toString();
//                    binding.toolbar.tvSave.setVisibility(View.INVISIBLE);
//                    binding.toolbar.progressBar.setVisibility(View.VISIBLE);
                    disableEnableTouch(true);
                    skillsActivityViewModel.addSkills(skillsId, ratingsId,1);

                } else {
                    toastMessage(getString(R.string.please_select_a_skill_first));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogTag.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogTag.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        dialogTag.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogTag.getWindow().setAttributes(lp);

        dialogTag.setOnDismissListener(dialog -> {
            selectedAdapter = null;
            dialogTag = null;
        });
    }

    @SuppressLint("DefaultLocale")
    private void setAdapter(ArrayList<UserSkillsModel.SkillLists> skills) {
        selectedAdapter = new RecyclerviewAdapter(skills, R.layout.item_selected_skills, OverViewActivity.this);
        bindingTag.rvSelectedSkills.setAdapter(selectedAdapter);

        if (skillsActivityViewModel.getSelectedListMutableLiveData().getValue() != null) {
            bindingTag.tvSkillNo.setText(String.format("%d", skillsActivityViewModel.getSelectedListMutableLiveData().getValue().size()));
        } else {
            bindingTag.tvSkillNo.setText(String.format("%d", 0));
        }

        bindingTag.shimmerLayout.stopShimmer();
        bindingTag.shimmerLayout.setVisibility(View.GONE);
        disableEnableTouch(false);
        isClickableView = false;
    }

    private SelectCountryAdapter selectCountryAdapter;
    private SelectStateAdapter selectStateAdapter;
    private SelectCityAdapter selectCityAdapter;

    int selectedExpertiseId = -1;

    @Override
    public void bindView(View view, int position) {

        if (mAdapter != null && mAdapter.layoutId == R.layout.item_skills_edit) {

            final TextView textView = view.findViewById(R.id.tv_skill);
            if (servicesList == null || servicesList.size() == 0) {
                return;
            }
            textView.setText(servicesList.get(position).getName(language));

            if (selectedExpertiseId == servicesList.get(position).id) {
                textView.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_button_bg));
                textView.setTextColor(Color.WHITE);
                textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.SFTEXT_BOLD));
            } else {
                textView.setBackground(ContextCompat.getDrawable(this, R.drawable.white_button_bg));
                textView.setTextColor(Color.BLACK);
                textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.SFTEXT_REGULAR));
            }

            textView.setOnClickListener(view1 -> {
                selectedExpertiseId = servicesList.get(position).id;
                mAdapter.notifyDataSetChanged();
            });

        } else if (selectedAdapter != null && selectedAdapter.layoutId == R.layout.item_selected_skills) {

            TextView txtSkill = view.findViewById(R.id.tv_skill_name);
            final TextView txtSkillLevel = view.findViewById(R.id.tv_skill_level);
            txtSkillLevel.setVisibility(View.GONE);
            ImageView imgRemove = view.findViewById(R.id.img_add_remove);
            try {
//            ArrayList<UserSkillsModel.SkillLists> skillLists = skillsActivityViewModel.getListMutableLiveData().getValue();
//            if (skillLists != null && skillLists.size() > 0) {
                if (skillsActivityViewModel.getListMutableLiveData().getValue() != null) {
                    UserSkillsModel.SkillLists skills = skillsActivityViewModel.getListMutableLiveData().getValue().get(position);

                    txtSkill.setText(skills.getName(language));

                    if (skills.isSelected) {
                        imgRemove.setImageResource(R.drawable.close_red);
//                        txtSkillLevel.setVisibility(View.VISIBLE);
//                        if (skills.rating != null) {
//                            txtSkillLevel.setText(Utils.getRatingFromId(skills.rating));
//                        } else {
//                            txtSkillLevel.setText(Utils.getRatingFromId(0));//default
//                        }
                    } else {
//                        txtSkillLevel.setVisibility(View.GONE);
                        imgRemove.setImageResource(R.drawable.add);
                    }

//                    txtSkillLevel.setOnClickListener(view1 -> skillsActivityViewModel.showSingleSelectionDialog(txtSkillLevel, skills));

                    imgRemove.setOnClickListener(view12 -> {
                        try {
//                            skills.selectedRating = Utils.getRatingId(txtSkillLevel.getText().toString());
                            if (skills.isSelected) {
                                skills.isSelected = false;
                                selectedSkillCount--;
                                Objects.requireNonNull(skillsActivityViewModel.getSelectedListMutableLiveData().getValue()).remove(skills);
                            } else {
                                skills.isSelected = true;
                                selectedSkillCount++;
                                if (skillsActivityViewModel.getSelectedListMutableLiveData().getValue() != null) {
                                    skillsActivityViewModel.getSelectedListMutableLiveData().getValue().add(skills);
                                } else {
                                    ArrayList<UserSkillsModel.SkillLists> selectedSkill = new ArrayList<>();
                                    selectedSkill.add(skills);
                                    skillsActivityViewModel.getSelectedListMutableLiveData().postValue(selectedSkill);
                                }

                            }
                            selectedAdapter.notifyItemChanged(position);
                            if (bindingTag != null) {
                                bindingTag.tvSkillNo.setText(String.format("%d", skillsActivityViewModel.getSelectedListMutableLiveData().getValue().size()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
                showNameDialog(Utils.WindowScreen.ABOUT_ME);
                break;
            case R.id.txt_dob:
                showNameDialog(Utils.WindowScreen.DOB);
                break;
            case R.id.txt_gender:
                showNameDialog(Utils.WindowScreen.GENDER);
                break;
            case R.id.txt_category:
                showCategoryDialog(false);
                break;
            case R.id.txt_min:
            case R.id.txt_max:
                showAdsPriceDialog();
                break;
        }
    }

    private void showNameDialog(Utils.WindowScreen screen) {
        nameDialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        nameDialog.setTitle(null);
        nameDialog.setContentView(R.layout.dialog_profile_name);
        nameDialog.setCancelable(true);
        RelativeLayout rlSave = nameDialog.findViewById(R.id.rel_save);
        RelativeLayout rlAllowLocation = nameDialog.findViewById(R.id.rel_allow_loc);
        LinearLayout linName = nameDialog.findViewById(R.id.lin_name);
        LinearLayout linUname = nameDialog.findViewById(R.id.lin_userName);
        LinearLayout linAbout = nameDialog.findViewById(R.id.lin_about);
        LinearLayout linWebsite = nameDialog.findViewById(R.id.lin_website);
        LinearLayout linGender = nameDialog.findViewById(R.id.lin_gender);
        LinearLayout linDob = nameDialog.findViewById(R.id.lin_dob);
        LinearLayout linCountry = nameDialog.findViewById(R.id.lin_country);
        linCity = nameDialog.findViewById(R.id.lin_city);
        LinearLayout linEmailView = nameDialog.findViewById(R.id.lin_emailView);
        EditText etFname = nameDialog.findViewById(R.id.et_firstname);
        EditText etLname = nameDialog.findViewById(R.id.et_lastname);
        EditText etuName = nameDialog.findViewById(R.id.et_userName);
        EditText etAbout = nameDialog.findViewById(R.id.et_aboutme);
        EditText etWebsite = nameDialog.findViewById(R.id.et_website);
        EditText etDob = nameDialog.findViewById(R.id.et_dob);
        EditText etEmail = nameDialog.findViewById(R.id.et_email);
        ProgressBar progressBar = nameDialog.findViewById(R.id.progress);
        SegmentedButtonGroupNew sbActiveWebsite = nameDialog.findViewById(R.id.segmentLoginGroup);
        SegmentedButtonGroupNew sbGender = nameDialog.findViewById(R.id.segmentLoginGroupGender);
        SegmentedButtonGroupNew sgAge = nameDialog.findViewById(R.id.segmentLoginGroupAge);
        SegmentedButtonGroupNew sgEmail = nameDialog.findViewById(R.id.segmentLoginGroupEmail);

        TextView txtCancel = nameDialog.findViewById(R.id.txt_cancel);
        TextView tvSave = nameDialog.findViewById(R.id.tv_save);
        TextView txtTitle = nameDialog.findViewById(R.id.txt_title);
        txtCountry = nameDialog.findViewById(R.id.txt_country);
//        txtState = nameDialog.findViewById(R.id.txt_state);
        txtCity = nameDialog.findViewById(R.id.txt_city);
        DatePicker birthDatePicker = nameDialog.findViewById(R.id.birthDatePicker);

        myProfileActivityVM.getShowProgress().observe(this, aBoolean -> {

            switch (aBoolean) {
                case 0:
                    break;
                case 1:
                case 3:
                case 2:
                    if (progressBar != null && tvSave != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        tvSave.setVisibility(View.INVISIBLE);
                    }

                    break;

                default:
                    if (progressBar != null && tvSave != null) {
                        progressBar.setVisibility(View.GONE);
                        tvSave.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        });


        switch (screen) {
            case ABOUT_ME:
                txtTitle.setText(getString(R.string.about_me));
                linAbout.setVisibility(View.VISIBLE);
                if (profileData.about_me != null) {
                    etAbout.setText("" + profileData.about_me);
                }
                break;
            case GENDER:
                txtTitle.setText(getString(R.string.gender));
                if (profileData.gender != null) {
                    sbGender.setPosition(profileData.gender == 2 ? 0 : 1, false);
                }
                linGender.setVisibility(View.VISIBLE);
                break;
            case DOB:
                txtTitle.setText(getString(R.string.birthdate));
                if (profileData.birth_date != null) {
                    if (profileData.show_age != null) {
                        sgAge.setPosition(profileData.show_age == 2 ? 0 : 1, false);
                    }
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
                        if (language.equals("ar")) {
                            etDob.setText(dayLang + "-" + monthLang + "-" + yearLang);
                        } else {
                            String d = profileData.birth_date.split("T")[0];
                            etDob.setText(String.format("%s-%s-%s", d.split("-")[2], d.split("-")[1], d.split("-")[0]));
                        }
                        etDob.setTag(profileData.birth_date.split("T")[0]);

                        if (profileData.birth_date.split("T")[0] != null) {
                            int m = Integer.parseInt((profileData.birth_date.split("T")[0].split("-")[1]));
                            if ((profileData.birth_date.split("T")[0].split("-")[1]).startsWith("0")) {
                                m = Integer.parseInt((profileData.birth_date.split("T")[0].split("-")[1]).replace("0", ""));
                            }
                            birthDatePicker.updateDate(Integer.parseInt(profileData.birth_date.split("T")[0].split("-")[0]),
                                    m - 1,
                                    Integer.parseInt(profileData.birth_date.split("T")[0].split("-")[2]));
                        }

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                birthDatePicker.setMaxDate(System.currentTimeMillis());


                // Set a listener to capture the selected date
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    birthDatePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
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

                        etDob.setText(selectedDate);
                        etDob.setTag(selectedDate1);
                    });
                }
                linDob.setVisibility(View.VISIBLE);
                break;
        }


        txtCancel.setOnClickListener(v -> nameDialog.dismiss());
        rlSave.setOnClickListener(v -> {

            switch (screen) {
                case ABOUT_ME:

                    if (TextUtils.isEmpty(etAbout.getText().toString().trim())) {
                        toastMessage(getString(R.string.about_me));
                        return;
                    }
                    myProfileActivityVM.updateHeadlines(etAbout.getText().toString(),1);
                    break;

                case GENDER:
                    //save info API and close dialog
                    myProfileActivityVM.updateProfile(3, null, null, null, null, null, -1, sbGender.getPosition() == 0 ? 2 : 1, null, 0, 0, null, 0);
                    break;
                case DOB:
                    if (TextUtils.isEmpty(etDob.getTag().toString().trim())) {
                        toastMessage(getString(R.string.enter_your_date_of_birth));
                        return;
                    }
                    //save info API and close dialog
                    myProfileActivityVM.updateProfile(3, null, null, null, null, null, -1, -1, etDob.getTag().toString(), 0, 0, null, sgAge.getPosition() == 0 ? 2 : 1);
                    break;
            }

            //nameDialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(nameDialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        nameDialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        nameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nameDialog.getWindow().setAttributes(lp);
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
}
