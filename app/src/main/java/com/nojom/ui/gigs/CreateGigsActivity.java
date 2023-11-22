package com.nojom.ui.gigs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.adapter.GigLanguageAdapter;
import com.nojom.apis.GetGigPackages;
import com.nojom.databinding.CreateMyGigsBinding;
import com.nojom.model.GigCatCharges;
import com.nojom.model.GigCategoryModel;
import com.nojom.model.GigDeliveryTimeModel;
import com.nojom.model.GigDetails;
import com.nojom.model.GigPackages;
import com.nojom.model.GigRequirementsModel;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.model.Language;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.nojom.multitypepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class CreateGigsActivity extends BaseActivity implements View.OnClickListener, GigLanguageAdapter.OnClickLanguageListener {
    private CreateMyGigsBinding binding;
    private CreateGigsActivityVM createGigsActivityVM;
    private GigCategoryModel.Data selectedCategory;
    private GigCatCharges.Data selectedCatCharges;
    private ArrayList<TextView> tvList = new ArrayList<>();
    private GigSubCategoryModel.Data selectedSubCat;
    private ArrayList<Language.Data> selectedLanguageList;
    private int selectedPackagePosition;
    private ArrayList<GigPackages.Data> packages;
    private ArrayList<ImageFile> fileList;
    private ArrayList<String> searchTags;
    private GigDetails gigDetails;
    private boolean isEdit, isDuplicate;
    private int selectedScreenTab;
    private ArrayList<GigDeliveryTimeModel.Data> deliveryList;
    private ArrayList<GigRequirementsModel.Data> requirementList;
    private GigLanguageAdapter gigLanguageAdapter;
    private ArrayList<Language.Data> gigLang;
    private JSONArray duplicateDeletedFile;
    private GetGigPackages gigPackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.create_my_gigs);

        if (getIntent() != null && getIntent().hasExtra(GIG_DETAILS)) {
            gigDetails = (GigDetails) getIntent().getSerializableExtra(GIG_DETAILS);
            selectedScreenTab = getIntent().getIntExtra(Constants.SCREEN_TAB, 1);
            isDuplicate = getIntent().getBooleanExtra("isDuplicate", false);
            if (!isDuplicate) {
                isEdit = true;
            }
        }

        gigPackages = new GetGigPackages();
        gigPackages.init(this);

        createGigsActivityVM = new CreateGigsActivityVM();
        createGigsActivityVM.init(this);
        initData();
        Utils.trackFirebaseEvent(this, "Create_Gig_Screen");
    }

    private void initData() {
        packages = new ArrayList<>();
        searchTags = new ArrayList<>();
        deliveryList = new ArrayList<>();
        selectedLanguageList = new ArrayList<>();
        binding.linCategory.setOnClickListener(this);
        binding.linDeliveryTime.setOnClickListener(this);
        binding.linDescription.setOnClickListener(this);
        binding.linSearchTag.setOnClickListener(this);
        binding.linGigPhotos.setOnClickListener(this);
        binding.linGigTitle.setOnClickListener(this);
        binding.linLanguage.setOnClickListener(this);
        binding.linPackageDesc.setOnClickListener(this);
        binding.linPackageName.setOnClickListener(this);
        binding.linPrice.setOnClickListener(this);
        binding.linRequirement.setOnClickListener(this);
        binding.linRevision.setOnClickListener(this);
        binding.linSkills.setOnClickListener(this);
        binding.relViewPublic.setOnClickListener(this);
        binding.txtSaveLive.setOnClickListener(this);
        binding.txtSaveLater.setOnClickListener(this);
        binding.tvDelete.setOnClickListener(this);
        binding.relSetLive.setOnClickListener(this);
        binding.relPause.setOnClickListener(this);
        binding.relSetActive.setOnClickListener(this);

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        if (isEdit) {
            binding.tvTitle.setText(getString(R.string.update_gig));
            binding.tvDelete.setVisibility(View.VISIBLE);

            if (selectedScreenTab == 1) {//active
                binding.relPause.setVisibility(View.VISIBLE);
                binding.relDraft.setVisibility(View.GONE);
            } else if (selectedScreenTab == 3) {//pause
                binding.txtSaveLater.setText(getString(R.string.update_gig_));
                binding.relSetActive.setVisibility(View.VISIBLE);
                binding.relSetLive.setVisibility(View.GONE);
            }

        } else if (isDuplicate) {
            binding.tvTitle.setText(getString(R.string.add_gig));
            binding.tvDelete.setVisibility(View.GONE);

//            if (selectedScreenTab == 1) {//active
//                binding.relPause.setVisibility(View.VISIBLE);
//                binding.relDraft.setVisibility(View.GONE);
//            } else if (selectedScreenTab == 3) {//pause
//                binding.txtSaveLater.setText("Update gig");
//                binding.relSetActive.setVisibility(View.VISIBLE);
//                binding.relSetLive.setVisibility(View.GONE);
//            }

        } else {

            packages = Preferences.getGigPackages(this);
            if (packages == null || packages.size() == 0) {
                gigPackages.getGigPackages();
            } else {
                addTabs(packages);
            }
        }

        ArrayList<GigCatCharges.Data> chargeList = Preferences.getCategoryCharges(this);
        if (chargeList != null && chargeList.size() > 0) {
            createGigsActivityVM.getGigCatChargesList().setValue(chargeList);
            if (isEdit || isDuplicate) {
                getChargesByCat(gigDetails.parentCategoryID);
            }
        } else {
            createGigsActivityVM.getGigCatCharges();
        }


        createGigsActivityVM.getIsShowProgress().observe(this, state -> {
            disableEnableTouch(true);
            switch (state) {
                case 1:
                    binding.progressBarCategory.setVisibility(View.VISIBLE);
                    binding.imgCatNext.setVisibility(View.GONE);
                    break;
                case 2:
                    binding.progressBarSkills.setVisibility(View.VISIBLE);
                    binding.imgSubcatNext.setVisibility(View.GONE);
                    break;
                case 3:
                    binding.progressBarLanguage.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    binding.progressBarDelivery.setVisibility(View.VISIBLE);
                    binding.imgDeliveryNext.setVisibility(View.GONE);
                    break;
                case 5:
                    binding.progressBarLive.setVisibility(View.VISIBLE);
                    binding.txtSaveLive.setVisibility(View.INVISIBLE);
                    break;
                case 6:
                    binding.progressBarDraft.setVisibility(View.VISIBLE);
                    binding.txtSaveLater.setVisibility(View.INVISIBLE);
                    break;
                case 7:
                    binding.progressBarDelete.setVisibility(View.VISIBLE);
                    binding.tvDelete.setVisibility(View.INVISIBLE);
                    break;
                case 8:
                    binding.progressBarActive.setVisibility(View.VISIBLE);
                    binding.txtSetActive.setVisibility(View.INVISIBLE);
                    break;
                case 9:
                    binding.progressBarPause.setVisibility(View.VISIBLE);
                    binding.txtPause.setVisibility(View.INVISIBLE);
                    break;
                case 10:
                    binding.progressBarRequirements.setVisibility(View.VISIBLE);
                    binding.imgReqNext.setVisibility(View.GONE);
                    break;
            }
        });

        createGigsActivityVM.getIsHideProgress().observe(this, state -> {
            disableEnableTouch(false);
            switch (state) {
                case 1:
                    binding.progressBarCategory.setVisibility(View.GONE);
                    binding.imgCatNext.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    binding.progressBarSkills.setVisibility(View.GONE);
                    binding.imgSubcatNext.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    binding.progressBarLanguage.setVisibility(View.GONE);
                    break;
                case 4:
                    binding.progressBarDelivery.setVisibility(View.GONE);
                    binding.imgDeliveryNext.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    binding.progressBarLive.setVisibility(View.GONE);
                    binding.txtSaveLive.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    binding.txtSaveLater.setVisibility(View.VISIBLE);
                    binding.progressBarDraft.setVisibility(View.GONE);
                    break;
                case 7:
                    binding.progressBarDelete.setVisibility(View.GONE);
                    binding.tvDelete.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    binding.progressBarActive.setVisibility(View.GONE);
                    binding.txtSetActive.setVisibility(View.VISIBLE);
                    break;
                case 9:
                    binding.progressBarPause.setVisibility(View.GONE);
                    binding.txtPause.setVisibility(View.VISIBLE);
                    break;
                case 10:
                    binding.progressBarRequirements.setVisibility(View.GONE);
                    binding.imgReqNext.setVisibility(View.VISIBLE);
                    break;

            }
        });

        gigPackages.getIsShowProgress().observe(this, state -> {
            disableEnableTouch(true);
            if (state == 1) {
                binding.progressBarCategory.setVisibility(View.VISIBLE);
                binding.imgCatNext.setVisibility(View.GONE);
            }
        });

        gigPackages.getIsHideProgress().observe(this, state -> {
            disableEnableTouch(false);
            if (state == 1) {
                binding.progressBarCategory.setVisibility(View.GONE);
                binding.imgCatNext.setVisibility(View.VISIBLE);
            }
        });

        if (!isEdit && !isDuplicate) {
            gigPackages.getGigPackageList().observe(this, data -> {
                packages = data;
                addTabs(data);
            });
        }

        createGigsActivityVM.getIsCreateSuccess().observe(this, isSuccess -> {
            disableEnableTouch(isSuccess);
            if (isSuccess) {
//                setResult(RESULT_OK);
//                finish();
                Utils.trackFirebaseEvent(this, "Gig_Created");
                gotoMainActivity(Constants.TAB_GIG);
            }
        });

        createGigsActivityVM.getIsDeleteGigSuccess().observe(this, isSuccess -> {
            disableEnableTouch(isSuccess);
            if (isSuccess) {
//                setResult(RESULT_OK);
//                finish();
                Utils.trackFirebaseEvent(this, "Gig_Delete_Success");
                gotoMainActivity(Constants.TAB_GIG);
            }
        });

        createGigsActivityVM.getIsActiveInactiveSuccess().observe(this, isSuccess -> {
            disableEnableTouch(isSuccess);
            if (isSuccess) {
                Utils.trackFirebaseEvent(this, "Gig_Active_Inactive_Success");
                gotoMainActivity(Constants.TAB_GIG);
            }
        });

        gigPackages.getGigCategoryList().observe(this, this::callGigCategoryActivity);

        createGigsActivityVM.getGigSubCatList().observe(this, this::callSubCategoryActivity);

        createGigsActivityVM.getGigDeliveryList().observe(this, data -> {
            deliveryList = data;
            callDeliveryActivity();
        });

        createGigsActivityVM.getGigRequirementList().observe(this, data -> {
            requirementList = data;
            callRequirementActivity();
        });

        if ((isEdit || isDuplicate) && gigDetails != null) {

            for (GigDetails.Packages data : gigDetails.packages) {
                GigPackages.Data gigPack = new GigPackages.Data();
                gigPack.deliveryTimeID = data.deliveryTimeID;
                gigPack.deliveryTime = data.deliveryTitle;
                gigPack.packageName = data.packageName;
                gigPack.id = data.packageID;
                gigPack.price = data.price;
                gigPack.name = data.name;
                gigPack.packageDescription = data.description;
                gigPack.revisions = data.revisions;
                gigPack.requirements = data.requirements;

                GigDeliveryTimeModel.Data delData = new GigDeliveryTimeModel.Data();
                delData.deliveryTitle = data.deliveryTitle;
                delData.id = data.deliveryTimeID;
                delData.isSelected = true;
                gigPack.delivery = delData;
                packages.add(gigPack);
            }

            addTabs(packages);

            //category
            GigCategoryModel.Data catData = new GigCategoryModel.Data();
            catData.id = gigDetails.parentCategoryID;
            catData.nameApp = gigDetails.parentCategoryNamme;
            catData.name = gigDetails.parentCategoryNamme;
            selectedCategory = catData;
            getChargesByCat(selectedCategory.id);
            binding.txtCategory.setText(gigDetails.parentCategoryNamme);
            //sub-category or skills
            GigSubCategoryModel.Data subCat = new GigSubCategoryModel.Data();
            subCat.id = gigDetails.subCategoryID;
            subCat.name = gigDetails.subCategoryNamme;
            selectedSubCat = subCat;

            binding.txtSkills.setText(gigDetails.subCategoryNamme);
            //language
            if (gigDetails.languages != null && gigDetails.languages.size() > 0) {
                selectedLanguageList = gigDetails.languages;
                setLanguage();
            }
            //gig title
            binding.txtGigTitle.setText(gigDetails.gigTitle);
            //gig desc
            binding.txtDescription.setText(gigDetails.gigDescription);
            //requirements
            if (gigDetails.packages != null && gigDetails.packages.get(0).requirements != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (GigRequirementsModel.Data model : gigDetails.packages.get(0).requirements) {
                    stringBuilder.append(model.name);
                    stringBuilder.append(",");
                }
                if (stringBuilder.length() > 0) {
                    binding.txtRequirement.setText(stringBuilder.deleteCharAt(stringBuilder.length() - 1));
                }
                packages.get(selectedPackagePosition).requirements = gigDetails.packages.get(0).requirements;
            }

            if (gigDetails.packages != null) {
                binding.txtPrice.setText("$" + gigDetails.packages.get(0).price);
                binding.txtPackageName.setText(gigDetails.packages.get(0).name);
                binding.txtPackageDesc.setText(gigDetails.packages.get(0).description);
                binding.txtDelivery.setText(gigDetails.packages.get(0).deliveryTitle);
                binding.etRevision.setText("" + gigDetails.packages.get(0).revisions);
            }

            //gig photos
            if (gigDetails.gigImages != null && gigDetails.gigImages.size() > 0) {
                String photoText = gigDetails.gigImages.size() == 1 ? ""+getString(R.string.photo) : ""+getString(R.string.photos);
                binding.txtPhotos.setText("(" + gigDetails.gigImages.size() + ") " + photoText);

                for (GigDetails.GigImage img : gigDetails.gigImages) {
                    ImageFile imageFile = new ImageFile();
                    imageFile.setPath(gigDetails.gigImagesPath + img.imageName);
                    imageFile.setId(img.id);
                    imageFile.setIsServerUrl(1);
                    if (fileList == null) {
                        fileList = new ArrayList<>();
                    }
                    fileList.add(imageFile);
                }
            }
            //search tag
            if (gigDetails.searchTags != null && gigDetails.searchTags.size() > 0) {
                for (GigDetails.TagName tag : gigDetails.searchTags) {
                    searchTags.add(tag.tagName);
                }
                binding.txtTags.setText(searchTags.toString().replace("[", "").replace("]", ""));
            }
        } else {
            //category
            try {
                GigCategoryModel.Data catData = new GigCategoryModel.Data();
                catData.id = getProfileData().expertise.id;
                catData.nameApp = getProfileData().expertise.nameApp;
                catData.name = getProfileData().expertise.name;
                selectedCategory = catData;
                binding.txtCategory.setText(getProfileData().expertise.nameApp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (selectedCategory != null) {
            createGigsActivityVM.getServiceCategoriesById(selectedCategory.id);//call API for subcategory list
        }

        binding.imgPlus.setOnClickListener(v -> onClickAdd());
        binding.imgMinus.setOnClickListener(v -> onClickMinus());

        binding.etRevision.setOnTouchListener((v, event) -> {
            binding.etRevision.setFocusable(true);
            binding.etRevision.setCursorVisible(true);
            binding.etRevision.setSelection(getRate().length());
            return false;
        });

        binding.etRevision.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateUi();
                return true;
            }
            return false;
        });

        binding.etRevision.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    int rev = Integer.parseInt(s.toString());
                    binding.etRevision.setTag(rev);
                    if (rev > 0) {
                        packages.get(selectedPackagePosition).revisions = rev;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        runOnUiThread(() -> {
            gigLang = Preferences.getGigLanguage(CreateGigsActivity.this);
            if (gigLang != null && gigLang.size() > 0) {
                if (selectedLanguageList != null && selectedLanguageList.size() > 0) {
                    for (Language.Data mainLang : gigLang) {
                        for (Language.Data selLang : selectedLanguageList) {
                            if (mainLang.id == selLang.id) {
                                mainLang.isSelected = true;
                                break;
                            }
                        }
                    }
                }
            } else {//call gig language API
                createGigsActivityVM.getGigLanguage();
            }
        });

        createGigsActivityVM.getLanguageList().observe(this, data -> gigLang = data);
    }

    public void onClickAdd() {
        updateUi();
        if (!isEmpty(getRate())) {
            try {

                int revision = Integer.parseInt(getRate()) + 1;
                binding.etRevision.setText("" + revision);
                binding.etRevision.setTag(revision);
                if (revision >= 0) {
                    packages.get(selectedPackagePosition).revisions = revision;
                }
            } catch (NumberFormatException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void onClickMinus() {
        updateUi();
        if (!isEmpty(getRate())) {
            try {
//                int revision = Integer.parseInt((getRate()));
                int revision = Integer.parseInt(getRate()) - 1;
                if (revision >= 0) {
                    binding.etRevision.setText("" + revision);
                    binding.etRevision.setTag(revision);
                    packages.get(selectedPackagePosition).revisions = revision;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUi() {
        Utils.hideSoftKeyboard(this);
        binding.etRevision.setCursorVisible(false);
    }

    public String getRate() {
        return binding.etRevision.getText().toString().trim().replaceAll(",", "");
    }

    private void callRequirementActivity() {
        Intent intentReq = new Intent(CreateGigsActivity.this, GigRequirementsActivity.class);
        intentReq.putExtra("data", packages.get(selectedPackagePosition).requirements);
        intentReq.putExtra("list", requirementList);
        startActivityForResult(intentReq, 5);
    }

    private void callDeliveryActivity() {
        Intent intentDel = new Intent(CreateGigsActivity.this, GigDeliveryActivity.class);
        intentDel.putExtra("data", packages.get(selectedPackagePosition).delivery);
        intentDel.putExtra("list", deliveryList);
        startActivityForResult(intentDel, 6);
    }

    private void addTabs(List<GigPackages.Data> packages) {
        for (int listPosition = 0; listPosition < packages.size(); listPosition++) {
            if (listPosition == 0) {
                selectedPackagePosition = listPosition;
            }
            TextView textViewName = new TextView(this);
            Typeface face = Typeface.createFromAsset(getAssets(),
                    "font/SF-Pro-Text-Medium.otf");
            textViewName.setTypeface(face);
            textViewName.setTextSize(14);
            if (listPosition == 0) { //left border black
                textViewName.setBackground(getResources().getDrawable(R.drawable.left_bottom_black));
                textViewName.setTextColor(getResources().getColor(R.color.white));
            } else if (packages.size() - 1 == listPosition) {
                textViewName.setBackground(getResources().getDrawable(R.drawable.right_bottom_white));
                textViewName.setTextColor(getResources().getColor(R.color.tab_gray));
            } else {
                textViewName.setBackgroundColor(getResources().getColor(R.color.white));
                textViewName.setTextColor(getResources().getColor(R.color.tab_gray));
            }
            textViewName.setText(packages.get(listPosition).packageName.toUpperCase());
            textViewName.setTag(packages.get(listPosition).id);

            if (packages.size() == 3 || packages.size() == 2) {
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1);
                textViewName.setLayoutParams(param);
            }
            textViewName.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    1,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            View view = new View(this);
            view.setPadding(0,
                    (int) getResources().getDimension(R.dimen._5sdp),
                    0,
                    (int) getResources().getDimension(R.dimen._5sdp));
            view.setBackgroundColor(getResources().getColor(R.color.white));
            view.setLayoutParams(param);

            tvList.add(textViewName);
            binding.llSubMain.addView(textViewName);
            if (packages.size() - 1 != listPosition) {
                binding.llSubMain.addView(view);
            }

            final int finalListPosition = listPosition;
            textViewName.setOnClickListener(v -> {
                selectedPackagePosition = finalListPosition;
                changeSelection(finalListPosition, packages);
                updatePackageWiseData(packages.get(finalListPosition));
            });
        }
    }

    public void clearRequirements() {
        for (GigPackages.Data pack : packages) {
            pack.requirements = null;
        }
        binding.txtRequirement.setText("");
    }

    private void updatePackageWiseData(GigPackages.Data data) {
        try {
            binding.etRevision.setText(data.revisions != 0 ? "" + data.revisions : "0");
            binding.txtPrice.setText(data.price >= 0 ? "$" + data.price : "");
            binding.txtPackageDesc.setText(TextUtils.isEmpty(data.packageDescription) ? "" : data.packageDescription);
            binding.txtPackageName.setText(TextUtils.isEmpty(data.name) ? "" : data.name);
            binding.txtDelivery.setText(TextUtils.isEmpty(data.deliveryTime) ? "" : data.deliveryTime);
            if (data.requirements != null && data.requirements.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (GigRequirementsModel.Data model : data.requirements) {
                    stringBuilder.append(model.name);
                    stringBuilder.append(",");
                }
                binding.txtRequirement.setText(stringBuilder.deleteCharAt(stringBuilder.length() - 1));
            } else {
                binding.txtRequirement.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<GigSubCategoryModel.Data> subCategoryList;
    private boolean isClickOnSkill;

    private void callSubCategoryActivity(ArrayList<GigSubCategoryModel.Data> data) {
        if (selectedCategory == null || selectedCategory.id == 0) {
            toastMessage(getString(R.string.please_select_category_first));
            return;
        }
        subCategoryList = data;
        if (isClickOnSkill) {
            Intent intent = new Intent(CreateGigsActivity.this, GigSubCategoryActivity.class);
            intent.putExtra("list", subCategoryList);
            intent.putExtra("data", selectedSubCat);
            startActivityForResult(intent, 3);
            isClickOnSkill = false;
        }
    }

    @Override
    public void onBackPressed() {
        discardDialog(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_category:
                ArrayList<GigCategoryModel.Data> catList = Preferences.getStndrdGigCategory(this);
                if (catList != null && catList.size() > 0) {
                    callGigCategoryActivity(catList);
                } else {
                    gigPackages.getStndrdServiceCategories();
                }

                break;
            case R.id.lin_delivery_time:
                if (deliveryList != null && deliveryList.size() > 0) {
                    callDeliveryActivity();
                } else {
                    createGigsActivityVM.getDeliveryTime();
                }
                break;
            case R.id.lin_description:
                Intent descInt = new Intent(this, GigDescriptionActivity.class);
                descInt.putExtra("screen", GIG_DESC);
                descInt.putExtra("data", binding.txtDescription.getText().toString().equalsIgnoreCase("") ? "" : binding.txtDescription.getText().toString());
                startActivityForResult(descInt, GIG_DESC);
                break;
            case R.id.lin_search_tag:
                Intent intentTag = new Intent(CreateGigsActivity.this, GigSearchTagActivity.class);
                intentTag.putExtra("data", searchTags);
                intentTag.putExtra("list", subCategoryList);
                startActivityForResult(intentTag, 7);
                break;
            case R.id.lin_gig_photos:
                if (TextUtils.isEmpty(binding.txtPhotos.getText().toString())) {
                    checkPermission(false);
                } else {
                    Intent intentPhoto = new Intent(CreateGigsActivity.this, GigPhotosActivity.class);
                    intentPhoto.putExtra("data", fileList);
                    intentPhoto.putExtra("isDuplicate", isDuplicate);
                    if (gigDetails != null) {
                        intentPhoto.putExtra("gigid", gigDetails.gigID);
                    }
                    startActivityForResult(intentPhoto, 16);
                }

                break;
            case R.id.lin_gig_title:
                Intent gigTitle = new Intent(this, GigDescriptionActivity.class);
                gigTitle.putExtra("screen", GIG_TITLE);
                gigTitle.putExtra("data", binding.txtGigTitle.getText().toString().equalsIgnoreCase("") ? "" : binding.txtGigTitle.getText().toString());
                startActivityForResult(gigTitle, GIG_TITLE);
                break;
            case R.id.lin_language:
                showLanguageSelectDialog();
                break;
            case R.id.lin_package_name:
                Intent packgInt = new Intent(this, GigDescriptionActivity.class);
                packgInt.putExtra("screen", GIG_PACKAGE_NAME);
                packgInt.putExtra("tab", TextUtils.isEmpty(packages.get(selectedPackagePosition).packageName) ? "" : packages.get(selectedPackagePosition).packageName);
                packgInt.putExtra("data", TextUtils.isEmpty(packages.get(selectedPackagePosition).name) ? "" : packages.get(selectedPackagePosition).name);
                startActivityForResult(packgInt, GIG_PACKAGE_NAME);
                break;
            case R.id.lin_package_desc:
                Intent packgDesInt = new Intent(this, GigDescriptionActivity.class);
                packgDesInt.putExtra("screen", GIG_PACKAGE_DESC);
                packgDesInt.putExtra("data", TextUtils.isEmpty(packages.get(selectedPackagePosition).packageDescription) ? "" : packages.get(selectedPackagePosition).packageDescription);
                startActivityForResult(packgDesInt, GIG_PACKAGE_DESC);
                break;
            case R.id.lin_price:
                if (selectedCategory == null) {
                    toastMessage(getString(R.string.please_select_category_first));
                    return;
                }
                Intent gigPrice = new Intent(this, EnterPriceActivity.class);
                gigPrice.putExtra("data", packages.get(selectedPackagePosition).price > 0 ? "" + packages.get(selectedPackagePosition).price : "");
                double percentage;
                if (selectedCatCharges == null) {
                    percentage = getChargesByCat(selectedCategory.id);
                } else {
                    percentage = selectedCatCharges.percentCharge;
                }
                if (percentage > 0) {
                    gigPrice.putExtra("percent", percentage);
                }
                startActivityForResult(gigPrice, GIG_PRICE);
                break;
            case R.id.lin_requirement:
                if (selectedCategory == null) {
                    toastMessage(getString(R.string.please_select_category_first));
                    return;
                }
                if (requirementList != null && requirementList.size() > 0) {
                    callRequirementActivity();
                } else {
                    createGigsActivityVM.getRequirements(selectedCategory.id);
                }

                break;
            case R.id.lin_revision:
//                Intent gigRev = new Intent(this, EnterRevisionActivity.class);
//                gigRev.putExtra("data", packages.get(selectedPackagePosition).revisions == 0 ? "" : "" + packages.get(selectedPackagePosition).revisions);
//                startActivityForResult(gigRev, GIG_REVISION);
                break;
            case R.id.lin_skills:
                isClickOnSkill = true;
                if (subCategoryList == null || subCategoryList.size() == 0) {
                    createGigsActivityVM.getServiceCategoriesById(selectedCategory.id);
                } else {
                    callSubCategoryActivity(subCategoryList);
                }
                break;
            case R.id.rel_view_public:
                if (checkValidation(false, null)) {
                    Intent intent1 = new Intent(this, GigDetailActivity.class);
                    intent1.putExtra("title", binding.txtGigTitle.getText().toString());
                    intent1.putExtra("desc", binding.txtDescription.getText().toString());
                    intent1.putExtra("files", fileList);
                    intent1.putExtra("packages", packages);
                    startActivity(intent1);
                }
                break;
            case R.id.txt_save_later:
                if (checkValidation(true, false)) {
                    makeRequest(false);
                }
                break;
            case R.id.txt_save_live:
                if (checkValidation(true, true)) {
                    makeRequest(true);
                }
                break;
            case R.id.tv_delete:
                deleteGigDialog(this);
                break;
            case R.id.rel_pause:
                if (checkValidation(false, null)) {
                    createGigsActivityVM.activeInactiveGig(gigDetails.gigID, 1);
                }
                break;
            case R.id.rel_set_active:
                if (checkValidation(false, null)) {
                    createGigsActivityVM.activeInactiveGig(gigDetails.gigID, 3);
                }
                break;
        }
    }

    private void callGigCategoryActivity(ArrayList<GigCategoryModel.Data> catList) {
        Intent intent = new Intent(CreateGigsActivity.this, GigCategoryActivity.class);
        intent.putExtra("data", selectedCategory);
        intent.putExtra("list", catList);
        startActivityForResult(intent, 2);
    }

    private void makeRequest(boolean isLive) {
        if (fileList == null) {
            fileList = new ArrayList<>();
        }
        int fileListSize = fileList.size();
        if (isEdit || isDuplicate) {//this logic is set for get the actual image selected
            int actualSize = 0;
            for (int i = 0; i < fileList.size(); i++) {
                if (!isEmpty(fileList.get(i).getPath()) && !fileList.get(i).getPath().startsWith("http")) {
                    actualSize = actualSize + 1;
                }
            }
            fileListSize = actualSize;
        }

        MultipartBody.Part[] body = null;
        if (fileList.size() > 0) {
            body = new MultipartBody.Part[fileListSize];
            int bodySize = 0;
            for (int i = 0; i < fileList.size(); i++) {
                File file;
                if (!isEmpty(fileList.get(i).getPath()) && !fileList.get(i).getPath().startsWith("http")) {
                    if (fileList.get(i).getPath().contains(".png") || fileList.get(i).getPath().contains(".jpg") || fileList.get(i).getPath().contains(".jpeg")) {
                        file = CompressFile.getCompressedImageFile(new File(fileList.get(i).getPath()));
                    } else {
                        file = new File(fileList.get(i).getPath());
                    }
                    Uri selectedUri = Uri.fromFile(file);
                    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

                    RequestBody requestFile = null;
                    if (mimeType != null) {
                        requestFile = RequestBody.create(file, MediaType.parse(mimeType));
                    }

                    Headers.Builder headers = new Headers.Builder();
                    headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\"");

                    if (requestFile != null) {
                        body[bodySize] = MultipartBody.Part.create(headers.build(), requestFile);
                    }
                    bodySize++;
                }
            }
        }

        if (packages == null) {
            return;
        }

        JSONArray jsonArray = new JSONArray();
        for (GigPackages.Data pack : packages) {

            JSONObject object = new JSONObject();
            try {
                object.put("deliveryTimeID", pack.deliveryTimeID);
                object.put("name", pack.name);
                object.put("packageDescription", pack.packageDescription);
                object.put("packageID", pack.id);
                object.put("price", pack.price);
                object.put("revisions", pack.revisions);
                JSONArray reqIdArray = new JSONArray();
                if (pack.requirements != null) {
                    for (GigRequirementsModel.Data reqId : pack.requirements) {
                        reqIdArray.put(reqId.id);
                    }
                }
                object.put("requirements", reqIdArray);
                jsonArray.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestBody gigTitleBody = RequestBody.create(MultipartBody.FORM, binding.txtGigTitle.getText().toString());
        RequestBody gigCatIdBody = RequestBody.create(MultipartBody.FORM, selectedCategory != null ? "" + selectedCategory.id : "0");
        RequestBody gigSubCatIdBody = RequestBody.create(MultipartBody.FORM, selectedSubCat != null ? "" + selectedSubCat.id : "0");
        RequestBody gigDescBody = RequestBody.create(MultipartBody.FORM, binding.txtDescription.getText().toString());
        RequestBody gigPackagesBody = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());
        RequestBody gigId = null;
        RequestBody duplicateBody = null;
        RequestBody fileToDeleteBody = null;

        if (isEdit && gigDetails != null) {
            gigId = RequestBody.create(MultipartBody.FORM, "" + gigDetails.gigID);
        } else if (isDuplicate && gigDetails != null) {
            duplicateBody = RequestBody.create(MultipartBody.FORM, "1");
            gigId = RequestBody.create(MultipartBody.FORM, "" + gigDetails.gigID);
            if (duplicateDeletedFile != null && duplicateDeletedFile.length() > 0) {
                fileToDeleteBody = RequestBody.create(MediaType.parse("application/json"), duplicateDeletedFile.toString());
            }
        }

        RequestBody gigStatus = null;
        if (!isLive) {//for draft
            if (selectedScreenTab == 3 && !isDuplicate) {//pause tab
                gigStatus = RequestBody.create(MultipartBody.FORM, "3");
            } else {
                gigStatus = RequestBody.create(MultipartBody.FORM, "2");
            }

        }

        JSONArray jsonArrayTag = new JSONArray();
        for (String pack : searchTags) {
            jsonArrayTag.put(pack);
        }

        JSONArray jsonArrayLang = new JSONArray();
        for (Language.Data lang : selectedLanguageList) {
            jsonArrayLang.put(lang.id);
        }
        RequestBody gigLanguageBody = RequestBody.create(MediaType.parse("application/json"), jsonArrayLang.toString());
        RequestBody gigTagsBody = RequestBody.create(MediaType.parse("application/json"), jsonArrayTag.toString());

        if (isDuplicate) {
            createGigsActivityVM.duplicateGig(gigTitleBody, gigDescBody, gigCatIdBody, gigSubCatIdBody, gigPackagesBody, gigTagsBody, gigStatus, gigId, body, gigLanguageBody, fileToDeleteBody, duplicateBody, isLive);
        } else {
            createGigsActivityVM.createUpdateGig(gigTitleBody, gigDescBody, gigCatIdBody, gigSubCatIdBody, gigPackagesBody, gigTagsBody, gigStatus, gigId, body, gigLanguageBody, isLive);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    if (data != null) {
                        GigCategoryModel.Data language = (GigCategoryModel.Data) data.getSerializableExtra("category");
                        if (language != null) {
                            binding.txtCategory.setText(language.nameApp);
                            selectedCategory = language;
                            selectedSubCat = null;
                            requirementList = null;
                            subCategoryList = null;
                            binding.txtSkills.setText("");
                            clearRequirements();

                            createGigsActivityVM.getServiceCategoriesById(selectedCategory.id);//call API for subcategory list
                            getChargesByCat(selectedCategory.id);
                        }
                    }
                    break;
                case 3:
                    if (data != null) {
                        selectedSubCat = (GigSubCategoryModel.Data) data.getSerializableExtra("data");
                        if (selectedSubCat != null) {
                            binding.txtSkills.setText(selectedSubCat.name);
                        } else {
                            binding.txtSkills.setText("");
                        }
                    }
                    break;
                case 4:
                    if (data != null) {
                        selectedLanguageList = (ArrayList<Language.Data>) data.getSerializableExtra("data");
                        setLanguage();
                    }
                    break;
                case 5:
                    if (data != null) {
                        ArrayList<GigRequirementsModel.Data> selectedReqList = (ArrayList<GigRequirementsModel.Data>) data.getSerializableExtra("data");
                        if (selectedReqList != null && selectedReqList.size() > 0) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (GigRequirementsModel.Data model : selectedReqList) {
                                stringBuilder.append(model.name);
                                stringBuilder.append(",");
                            }
                            binding.txtRequirement.setText(stringBuilder.deleteCharAt(stringBuilder.length() - 1));
                            if (packages != null) {
                                packages.get(selectedPackagePosition).requirements = selectedReqList;
                            }
                        } else {
                            binding.txtRequirement.setText("");
                            if (packages != null) {
                                packages.get(selectedPackagePosition).requirements = null;
                            }
                        }
                    }
                    break;
                case 6:
                    if (data != null) {
                        GigDeliveryTimeModel.Data delivery = (GigDeliveryTimeModel.Data) data.getSerializableExtra("delivery");
                        if (delivery != null) {
                            binding.txtDelivery.setText(delivery.deliveryTitle);
                            if (packages != null) {
                                packages.get(selectedPackagePosition).deliveryTime = delivery.deliveryTitle;
                                packages.get(selectedPackagePosition).deliveryTimeID = delivery.id;
                                packages.get(selectedPackagePosition).delivery = delivery;
                            }
                        }
                    }
                    break;
                case 7:
                    if (data != null) {
                        ArrayList<String> selectedTags = data.getStringArrayListExtra("tags");
                        if (selectedTags != null && selectedTags.size() > 0) {
                            searchTags = selectedTags;
                            binding.txtTags.setText(selectedTags.toString().replace("[", "").replace("]", ""));
                        } else {
                            binding.txtTags.setText("");
                            searchTags = null;
                        }
                    }
                    break;
                case 10:
                    if (data != null) {
                        String gigTitle = data.getStringExtra("data");
                        binding.txtGigTitle.setText(gigTitle);
                    }
                    break;
                case 11:
                    if (data != null) {
                        String description = data.getStringExtra("data");
                        binding.txtDescription.setText(description);
                    }
                    break;
                case 12:
                    if (data != null) {
                        String packgName = data.getStringExtra("data");
                        binding.txtPackageName.setText(packgName);
                        packages.get(selectedPackagePosition).name = packgName;
                    }
                    break;
                case 13:
                    if (data != null) {
                        String packgDesc = data.getStringExtra("data");
                        binding.txtPackageDesc.setText(packgDesc);
                        packages.get(selectedPackagePosition).packageDescription = packgDesc;
                    }
                    break;
                case 14:
                    if (data != null) {
                        String gigPrice = data.getStringExtra("data");
                        binding.txtPrice.setText(String.format("$%s", gigPrice));
                        binding.txtPrice.setTag(gigPrice);
                        if (gigPrice != null) {
                            packages.get(selectedPackagePosition).price = Double.parseDouble(gigPrice);
                        }
                    }
                    break;
                case 15:
                    if (data != null) {
                        String revision = data.getStringExtra("revision");
                        binding.etRevision.setText("" + revision);
                        binding.etRevision.setTag(revision);
                        if (revision != null) {
                            packages.get(selectedPackagePosition).revisions = Integer.parseInt(revision);
                        }
                    }
                    break;
                case 16:
                    if (data != null) {
                        ArrayList<ImageFile> selectedFiles = data.getParcelableArrayListExtra("data");
                        String deletedFiles = data.getStringExtra("deletedFiles");
                        if (!TextUtils.isEmpty(deletedFiles)) {
                            try {
                                duplicateDeletedFile = new JSONArray(deletedFiles);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        fileList = new ArrayList<>();
                        if (selectedFiles != null && selectedFiles.size() > 0) {
                            fileList.addAll(selectedFiles);
                            String photoText = fileList.size() == 1 ? ""+getString(R.string.photo) : ""+getString(R.string.photos);
                            binding.txtPhotos.setText("(" + fileList.size() + ") " + photoText);
                        } else {
                            binding.txtPhotos.setText("");
                        }
                    }
                    break;
                case Constant.REQUEST_CODE_PICK_IMAGE:
                    if (data != null) {
                        ArrayList<ImageFile> selectedFiles = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                        if (selectedFiles != null && selectedFiles.size() > 0) {
                            Log.e("Image Path == > ", selectedFiles.get(0).getPath());
                            if (fileList == null) {
                                fileList = new ArrayList<>();
                            }
                            fileList.addAll(selectedFiles);
                            String photoText = fileList.size() == 1 ? ""+getString(R.string.photo) : ""+getString(R.string.photos);
                            binding.txtPhotos.setText("(" + fileList.size() + ") " + photoText);
                        } else {
                            toastMessage("File not selected");
                        }
                    }
                    break;
            }
        }
    }

    private void setLanguage() {
        if (selectedLanguageList != null && selectedLanguageList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Language.Data model : selectedLanguageList) {
                stringBuilder.append(model.getName(language));
                stringBuilder.append(",");
            }
            binding.txtLanguage.setText(stringBuilder.deleteCharAt(stringBuilder.length() - 1));
        } else {
            binding.txtLanguage.setText("");
        }
    }

    private double getChargesByCat(int catId) {
        ArrayList<GigCatCharges.Data> catCharges = createGigsActivityVM.getGigCatChargesList().getValue();
        if (catCharges != null && catCharges.size() > 0) {
            for (GigCatCharges.Data charge : catCharges) {
                if (catId == charge.serviceCategoryId) {
                    selectedCatCharges = charge;
                    return charge.percentCharge;
                }
            }
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void checkPermission(final boolean isDocument) {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            Intent intent = new Intent(CreateGigsActivity.this, ImagePickActivity.class);
                            intent.putExtra(IS_NEED_CAMERA, true);
                            intent.putExtra(Constant.MAX_NUMBER, 5);
                            intent.putExtra("rCode", Constant.REQUEST_CODE_PICK_IMAGE);
                            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);

                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            toastMessage(getString(R.string.please_give_permission));
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }


    private void changeSelection(int position, List<GigPackages.Data> packages) {
        for (int listPosition = 0; listPosition < packages.size(); listPosition++) {
            if (listPosition == 0) { //left border black
                if (position == listPosition) {
                    tvList.get(listPosition).setBackground(getResources().getDrawable(R.drawable.left_bottom_black));
                    tvList.get(listPosition).setTextColor(getResources().getColor(R.color.white));
                } else {
                    tvList.get(listPosition).setBackground(getResources().getDrawable(R.drawable.left_bottom_white));
                    tvList.get(listPosition).setTextColor(getResources().getColor(R.color.tab_gray));
                }
            } else if (packages.size() - 1 == listPosition) {
                if (position == listPosition) {
                    tvList.get(listPosition).setBackground(getResources().getDrawable(R.drawable.right_bottom_black));
                    tvList.get(listPosition).setTextColor(getResources().getColor(R.color.white));
                } else {
                    tvList.get(listPosition).setBackground(getResources().getDrawable(R.drawable.right_bottom_white));
                    tvList.get(listPosition).setTextColor(getResources().getColor(R.color.tab_gray));
                }
            } else if (position == listPosition) {
                tvList.get(listPosition).setBackgroundColor(getResources().getColor(R.color.black));
                tvList.get(listPosition).setTextColor(getResources().getColor(R.color.white));
            } else {
                tvList.get(listPosition).setBackgroundColor(getResources().getColor(R.color.white));
                tvList.get(listPosition).setTextColor(getResources().getColor(R.color.tab_gray));
            }
        }
    }

    private boolean checkValidation(boolean checkFreeValidation, Boolean isLive) {

        if (selectedCategory == null) {
            toastMessage(getString(R.string.please_select_category));
            return false;
        }

        if (selectedSubCat == null) {
            toastMessage(getString(R.string.select_your_skills));
            return false;
        }

        if (selectedLanguageList == null || selectedLanguageList.size() == 0) {
            toastMessage(getString(R.string.please_select_language));
            return false;
        }

        if (TextUtils.isEmpty(binding.txtGigTitle.getText().toString())) {
            toastMessage(getString(R.string.please_enter_gig_title));
            return false;
        }

        if (TextUtils.isEmpty(binding.txtDescription.getText().toString())) {
            toastMessage(getString(R.string.please_enter_gig_description));
            return false;
        }

        if (searchTags == null || searchTags.size() == 0) {
            toastMessage(getString(R.string.please_set_search_tag_for_your_gig));
            return false;
        }

        if (fileList == null || fileList.size() == 0) {
            toastMessage(getString(R.string.please_select_photos));
            return false;
        }

        if (fileList.size() > 5) {
            toastMessage(getString(R.string.only_five_photos_allowed));
            return false;
        }

        GigPackages.Data previousPackage = null;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < packages.size(); i++) {
            GigPackages.Data pack = packages.get(i);

            if (pack.price < 0) {
                toastMessage(getString(R.string.please_enter_price_for)+" " + pack.packageName);
                return false;
            }

//            if (i == 0 && pack.price < 0) {//for basic case it should be min 2
//                toastMessage("The minimum amount should be $2 for " + pack.packageName);
//                return false;
//            }

            if (TextUtils.isEmpty(pack.name)) {
                toastMessage(getString(R.string.please_fill_proper_package_data) + pack.packageName);
                return false;
            }

            if (TextUtils.isEmpty(pack.packageDescription)) {
                toastMessage(getString(R.string.please_fill_proper_package_data) + pack.packageName);
                return false;
            }

            if (TextUtils.isEmpty(pack.deliveryTime)) {
                toastMessage(getString(R.string.please_set_del_time_for) + pack.packageName);
                return false;
            }

            if (pack.price == 0 || pack.price == 0.0) {
                stringBuilder.append(pack.packageName);
                stringBuilder.append(",");
            }

            if (previousPackage != null && previousPackage.price > 0 /*&& pack.price > 0*/) {
                if (previousPackage.price >= pack.price) {
                    toastMessage(getString(R.string.price_for) + pack.packageName + " "+getString(R.string.should_be_greater_than)+" " + previousPackage.packageName);
                    return false;
                }
            }

            previousPackage = pack;

        }

        if (checkFreeValidation && stringBuilder.length() > 0) {
            String msg = stringBuilder.deleteCharAt(stringBuilder.length() - 1) + " "+getString(R.string.packages_are_free);
            freePackageDialog(this, msg, isLive);
            return false;
        }

        return true;
    }

    void deleteGigDialog(BaseActivity activity) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        String s = activity.getString(R.string.gig_delete_msg);
        String[] words = {getString(R.string.delete)};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvMessage.setText(Utils.getBoldString(activity, s, fonts, null, words));

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            createGigsActivityVM.deleteGig(gigDetails.gigID);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    void discardDialog(BaseActivity activity) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        String s = activity.getString(R.string.gig_discard_msg);
        String[] words = {getString(R.string.discard)};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvMessage.setText(Utils.getBoldString(activity, s, fonts, null, words));

        tvCancel.setText(getString(R.string.cancel));
        tvCancel.setOnClickListener(v -> {
            if (isEdit || isDuplicate) {
                setResult(RESULT_OK);
            }
            dialog.dismiss();
        });

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            if (isEdit || isDuplicate) {
                setResult(RESULT_OK);
            }
            finish();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    void showLanguageSelectDialog() {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialog.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        etSearch.setHint(String.format(getString(R.string.search_for), getString(R.string.language).toLowerCase()));

        rvTypes.setLayoutManager(new LinearLayoutManager(this));
        try {
            gigLanguageAdapter = new GigLanguageAdapter(this, gigLang, this);
            gigLanguageAdapter.setSelectedLanguageList(selectedLanguageList);
            rvTypes.setAdapter(gigLanguageAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> {
            if (selectedLanguageList == null || selectedLanguageList.size() == 0) {
                setLanguage();
            }
            dialog.dismiss();
        });

        tvApply.setOnClickListener(v -> {
            if (selectedLanguageList != null && selectedLanguageList.size() > 0) {
                setLanguage();
                dialog.dismiss();
            } else {
                toastMessage(getString(R.string.please_select_language));
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (gigLanguageAdapter != null)
                    gigLanguageAdapter.getFilter().filter(s.toString());
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

    @Override
    public void onClickLanguage(boolean isAdded, Language.Data language, int adapterPos) {
        addRemoveItem(isAdded, language, adapterPos);
    }

    public void addRemoveItem(boolean isAdded, Language.Data model, int adapterPos) {
        if (selectedLanguageList != null && selectedLanguageList.size() > 0) {
            if (isAdded) {
                selectedLanguageList.add(model);
            } else {
                selectedLanguageList.remove(model);
            }
        } else {
            selectedLanguageList.add(model);
        }
        if (gigLanguageAdapter != null) {
            gigLanguageAdapter.setSelectedLanguageList(selectedLanguageList);
            gigLanguageAdapter.notifyItemChanged(adapterPos);
        }
    }

    void freePackageDialog(BaseActivity activity, String message, Boolean isLive) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvMessage.setText(message);

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            if (isLive != null) {
                makeRequest(isLive);
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }
}
