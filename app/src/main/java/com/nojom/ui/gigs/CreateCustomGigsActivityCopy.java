package com.nojom.ui.gigs;

import static com.nojom.multitypepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.model.ConnectedSocialMedia;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.nojom.R;
import com.nojom.adapter.GigLanguageAdapter;
import com.nojom.databinding.CreateCustomGigsCopyBinding;
import com.nojom.model.GigCatCharges;
import com.nojom.model.GigCategoryModel;
import com.nojom.model.GigDetails;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.model.Language;
import com.nojom.model.ProfileResponse;
import com.nojom.model.RequiremetList;
import com.nojom.model.SocialPlatformResponse;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CreateCustomGigsActivityCopy extends BaseActivity implements View.OnClickListener, GigLanguageAdapter.OnClickLanguageListener {
    private CreateCustomGigsCopyBinding binding;
    private CreateCustomGigsActivityVM createGigsActivityVM;
    private GigCategoryModel.Data selectedCategory;
    private GigCatCharges.Data selectedCatCharges;
    private GigSubCategoryModel.Data selectedSubCat;
    private ArrayList<Language.Data> selectedLanguageList;

    private ArrayList<ImageFile> fileList;
    private ArrayList<String> searchTags;
    private GigDetails gigDetails;
    private boolean isEdit, isDuplicate;
    private int selectedScreenTab;
    private ArrayList<RequiremetList.Data> requirementByCatList;
    private ArrayList<RequiremetList.Data> requirementByCatListBinding;
    private GigLanguageAdapter gigLanguageAdapter;
    private ArrayList<Language.Data> gigLang;
    private JSONArray duplicateDeletedFile;
    private PowerMenu powerMenu;
    private ArrayList<GigCategoryModel.Data> catList;
    private ArrayList<ConnectedSocialMedia.Data> socialDataList;
    private boolean isOpenReqScreen = true;
    private ArrayList<GigCategoryModel.Deadline> deadlineList;
    private PowerMenuItem days, hours;
    private Typeface tf;
    private final DecimalFormat format = new DecimalFormat("0.##");
    private final int CUSTOM_VIEW_LIMIT = 19;//no of count for custom requirement view
    private String deadlineDescription = "";
    private ConnectedSocialMedia.Data selectedPlatform = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.create_custom_gigs_copy);
        catList = new ArrayList<>();
        socialDataList = new ArrayList<>();
        deadlineList = new ArrayList<>();

        if (getIntent() != null && getIntent().hasExtra(GIG_DETAILS)) {
            gigDetails = (GigDetails) getIntent().getSerializableExtra(GIG_DETAILS);
            selectedScreenTab = getIntent().getIntExtra(Constants.SCREEN_TAB, 1);
            isDuplicate = getIntent().getBooleanExtra("isDuplicate", false);
            if (!isDuplicate) {
                isEdit = true;
            }
        }
        requirementByCatListBinding = new ArrayList<>();
        createGigsActivityVM = new CreateCustomGigsActivityVM();
        createGigsActivityVM.init(this);
        initData();
        Utils.trackFirebaseEvent(this, "Create_Gig_Screen");
    }

    private void initData() {
        searchTags = new ArrayList<>();
        selectedLanguageList = new ArrayList<>();
        binding.linCategory.setOnClickListener(this);
        binding.linDescription.setOnClickListener(this);
        binding.linSearchTag.setOnClickListener(this);
        binding.linGigPhotos.setOnClickListener(this);
        binding.linGigTitle.setOnClickListener(this);
        binding.linLanguage.setOnClickListener(this);
        binding.linGigPhotos.setOnClickListener(this);
        binding.linSkills.setOnClickListener(this);
        binding.tvDelete.setOnClickListener(this);
        binding.tvAddRequirement.setOnClickListener(this);
        binding.relLive.setOnClickListener(this);
        binding.relSave.setOnClickListener(this);
        binding.relPause.setOnClickListener(this);
        binding.relActive.setOnClickListener(this);
        binding.relViewPublic.setOnClickListener(this);
        binding.linPlatform.setOnClickListener(this);
        binding.delTime.imgDelete.setOnClickListener(this);

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        if (isEdit) {
            binding.tvTitle.setText(getString(R.string.update_service));
            binding.tvDelete.setVisibility(View.VISIBLE);

            if (selectedScreenTab == 1) {//active
                binding.relPause.setVisibility(View.VISIBLE);
                binding.relSave.setVisibility(View.GONE);
            } else if (selectedScreenTab == 3) {//pause
                binding.relActive.setVisibility(View.VISIBLE);
                binding.relLive.setVisibility(View.GONE);
            }

        } else if (isDuplicate) {
            binding.tvTitle.setText(getString(R.string.add_service));
            binding.tvDelete.setVisibility(View.GONE);
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

        //show progress bar
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
                case 7:
                    binding.progressBarDelete.setVisibility(View.VISIBLE);
                    binding.tvDelete.setVisibility(View.INVISIBLE);
                    break;
                case 9:
                    binding.progressBarReq.setVisibility(View.VISIBLE);
                    binding.tvAddRequirement.setVisibility(View.INVISIBLE);
                    break;
                case 10:
                    binding.progressBarLive.setVisibility(View.VISIBLE);
                    binding.tvLive.setVisibility(View.INVISIBLE);
                    break;
                case 11:
                    binding.progressBarSave.setVisibility(View.VISIBLE);
                    binding.tvSave.setVisibility(View.INVISIBLE);
                    break;
                case 12:
                    binding.progressBarPause.setVisibility(View.VISIBLE);
                    binding.tvPause.setVisibility(View.INVISIBLE);
                    break;
                case 13:
                    binding.progressBarActive.setVisibility(View.VISIBLE);
                    binding.tvActive.setVisibility(View.INVISIBLE);
                    break;
                case 14:
                    binding.progressBarPlatform.setVisibility(View.VISIBLE);
                    binding.txtPlatform.setVisibility(View.INVISIBLE);
                    break;
            }
        });

        //hide progress bar
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
                case 7:
                    binding.progressBarDelete.setVisibility(View.GONE);
                    binding.tvDelete.setVisibility(View.VISIBLE);
                    break;
                case 9:
                    binding.progressBarReq.setVisibility(View.GONE);
                    binding.tvAddRequirement.setVisibility(View.VISIBLE);
                    break;
                case 10:
                    binding.progressBarLive.setVisibility(View.GONE);
                    binding.tvLive.setVisibility(View.VISIBLE);
                    break;
                case 11:
                    binding.progressBarSave.setVisibility(View.GONE);
                    binding.tvSave.setVisibility(View.VISIBLE);
                    break;
                case 12:
                    binding.progressBarPause.setVisibility(View.GONE);
                    binding.tvPause.setVisibility(View.VISIBLE);
                    break;
                case 13:
                    binding.progressBarActive.setVisibility(View.GONE);
                    binding.tvActive.setVisibility(View.VISIBLE);
                    break;
                case 14:
                    binding.progressBarPlatform.setVisibility(View.GONE);
                    binding.txtPlatform.setVisibility(View.VISIBLE);
                    break;
            }
        });

        //success response of create gig
        createGigsActivityVM.getIsCreateSuccess().observe(this, isSuccess -> {
            disableEnableTouch(isSuccess);
            if (isSuccess) {
                Utils.trackFirebaseEvent(this, "Gig_Created");
                gotoMainActivity(Constants.TAB_GIG);
            }
        });

        //delete response of gig
        createGigsActivityVM.getIsDeleteGigSuccess().observe(this, isSuccess -> {
            disableEnableTouch(isSuccess);
            if (isSuccess) {
                Utils.trackFirebaseEvent(this, "Gig_Delete_Success");
                gotoMainActivity(Constants.TAB_GIG);
            }
        });

        //active/inactive gig response
        createGigsActivityVM.getIsActiveInactiveSuccess().observe(this, isSuccess -> {
            disableEnableTouch(isSuccess);
            if (isSuccess) {
                Utils.trackFirebaseEvent(this, "Gig_Active_Inactive_Success");
                gotoMainActivity(Constants.TAB_GIG);
            }
        });

        //gig category list response
        createGigsActivityVM.getGigCategoryList().observe(this, data -> {
            catList = data;
            if (!isEdit && !isDuplicate) {
                callGigCategoryActivity(data, false);
            }
        });

        //social platform response
        createGigsActivityVM.getSocialDataList().observe(this, data -> {
            socialDataList = data;
            Intent intent = new Intent(CreateCustomGigsActivityCopy.this, PlatformActivity.class);
            intent.putExtra("data", socialDataList);
            intent.putExtra("platform", selectedPlatform);
            startActivityForResult(intent, 14);
        });

        //gig cub category or skill list response
        createGigsActivityVM.getGigSubCatList().observe(this, this::callSubCategoryActivity);

        //list of custom requirement
        createGigsActivityVM.getRequirementList().observe(this, data -> {
            requirementByCatList = data;
            if (isOpenReqScreen) {
                //callRequirementByCatActivity(101);
            }
            isOpenReqScreen = true;
        });


        //perform action based on edit & duplicate case
        if ((isEdit || isDuplicate) && gigDetails != null) {
            //category
            GigCategoryModel.Data catData = new GigCategoryModel.Data();
            catData.id = gigDetails.parentCategoryID;
            catData.nameApp = gigDetails.parentCategoryNamme;
            catData.name = gigDetails.parentCategoryNamme;
            selectedCategory = catData;
//            getChargesByCat(selectedCategory.id);
            binding.txtCategory.setText(gigDetails.parentCategoryNamme);

            //selected platform
            if (gigDetails.socialPlatform != null && gigDetails.socialPlatform.size() > 0) {
                selectedPlatform = new ConnectedSocialMedia.Data();
                selectedPlatform.followers = gigDetails.socialPlatform.get(0).followers;
                selectedPlatform.id = gigDetails.socialPlatform.get(0).socialPlatformID;
                selectedPlatform.username = gigDetails.socialPlatform.get(0).username;
                selectedPlatform.name = gigDetails.socialPlatform.get(0).name;
                selectedPlatform.filename = gigDetails.socialPlatform.get(0).platformIcon;

                binding.txtPlatform.setText(selectedPlatform.getName(language));
            }

            if (gigDetails.customPackages != null && gigDetails.customPackages.size() > 0) {
                for (GigDetails.CustomRequirements data : gigDetails.customPackages) {

                    //display custom packages
                    //if there is only 1 requirement

                    RequiremetList.Data gigPack = new RequiremetList.Data();
                    gigPack.name = data.name;
                    gigPack.inputType = data.inputType;
                    gigPack.id = data.reqOrOtherReqID;
                    gigPack.dataReq = data.name;
                    gigPack.gigReqType = data.gigRequirementType;
                    gigPack.isSelected = false;
                    gigPack.isOther = 1;
                    gigPack.isOtherReq = data.isOther;
                    gigPack.gigOtherInputType = data.inputType;
                    gigPack.reqDescription = data.description;

                    if (data.requirmentDetails.get(0).price != null) {
                        gigPack.dataValue = "" + data.requirmentDetails.get(0).price;
                    } else {
                        gigPack.dataValue = "";
                    }
                    if (data.requirmentDetails.get(0).featureName != null) {
                        gigPack.featureTitle = "" + data.requirmentDetails.get(0).featureName;
                        gigPack.dataReq = "" + data.requirmentDetails.get(0).featureName;
                    }

                    boolean isNumber = false;
                    if (data.isOther && data.inputType == 1) {
                        isNumber = true;
                    }

                    if (data.requirmentDetails.size() > 1 && data.gigRequirementType != 1) {//this is for custom requirement

                        ArrayList<RequiremetList.CustomData> customDataList = new ArrayList<>();

                        for (int i = 0; i < data.requirmentDetails.size(); i++) {
                            if (i != 0) {//skip zero index
                                GigDetails.ReqDetail custViewData = data.requirmentDetails.get(i);
                                RequiremetList.CustomData customData = new RequiremetList.CustomData();
                                customData.dataReq = custViewData.featureName;
                                customData.dataValue = "" + custViewData.price;
                                customDataList.add(customData);
                            }
                        }

                        if (gigPack.customData == null) {
                            gigPack.customData = new ArrayList<>();
                        }
                        gigPack.customData.addAll(customDataList);

                    }

                    //dynamically add view
                    addDynamicView(gigPack, isNumber, true);

                    requirementByCatListBinding.add(gigPack);

                }
            }

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

            //gig photos
            if (gigDetails.gigImages != null && gigDetails.gigImages.size() > 0) {
                String photoText = gigDetails.gigImages.size() == 1 ? "" + getString(R.string.photo) : "" + getString(R.string.photos);
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

            deadlineDescription = gigDetails.deadlineDescription;
//            if (selectedCategory.id == 4352) {//social influencer
//                binding.delTime.txtAddDesc.setText("Edit Link");
//            } else {
            binding.delTime.txtAddDesc.setText(getString(R.string.edit_description));
//            }

            if (gigDetails.deadlines != null && gigDetails.deadlines.size() > 0) {
                binding.delTime.linDelivery.setVisibility(View.VISIBLE);
                if (selectedCategory.id == 4352) {//social influencer
                    binding.delTime.imgDelete.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < gigDetails.deadlines.size(); i++) {
                    if (i == 0) {
                        binding.delTime.etPrice.setText(getCurrency().equals("SAR") ? Utils.priceWithSAR(this, format.format(gigDetails.deadlines.get(i).price)) : Utils.priceWith$(format.format(gigDetails.deadlines.get(i).price), this));
                        binding.delTime.etNumber.setText("" + gigDetails.deadlines.get(i).value);
                        binding.delTime.tvDelMethod.setText(gigDetails.deadlines.get(i).type == 2 ? "" + getString(R.string.days) : "" + getString(R.string.hours));
                    } else {
                        editDeadlineView(gigDetails.deadlines.get(i));
                    }
                }
            } else {//hide deadline in case of edit also if not found from server
                if (selectedCategory.id == 4352) {//social influencer
                    binding.delTime.linDelivery.setVisibility(View.GONE);
                    binding.delTime.imgDelete.setVisibility(View.GONE);
                }
            }
        }

        //Added by DPP on 03-08-2023 for only social influencers
        //category
        GigCategoryModel.Data cat = new GigCategoryModel.Data();
        cat.id = 4352;
        cat.name = "Social star";
        cat.nameApp = "star";

        selectedCategory = cat;

        if (selectedCategory.id == 4352) {//social influencer
            binding.txtSkillLbl.setText(getString(R.string.industries));//just change lbl name
//                                binding.delTime.txtAddDesc.setText("Add Link");
            binding.linPlatform.setVisibility(View.VISIBLE);
            binding.delTime.linDelivery.setVisibility(View.VISIBLE);
        }
        getChargesByCat(selectedCategory.id);
        //End by DPP on 03-08-2023

        addMoreDeliveryOption();

        if (selectedCategory != null) {
            createGigsActivityVM.getServiceCategoriesById(selectedCategory.id);//call API for subcategory or skill list
            isOpenReqScreen = false;
//            createGigsActivityVM.getRequirementById(selectedCategory.id, false);//call API for custom requirement list
        }

        runOnUiThread(() -> {
            gigLang = Preferences.getGigLanguage(CreateCustomGigsActivityCopy.this);
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

        tf = Typeface.createFromAsset(getAssets(), Constants.SFTEXT_REGULAR);
        String deadlineType = gigDetails != null ? gigDetails.deadlineType.equals("2") ? "" + getString(R.string.days) : "" + getString(R.string.hours) : "";

        days = new PowerMenuItem(getString(R.string.days), deadlineType.equals(getString(R.string.days)));
        hours = new PowerMenuItem(getString(R.string.hours), !deadlineType.equals(getString(R.string.days)));

        binding.delTime.tvDelMethod.setOnClickListener(view1 -> {
            powerMenu = new PowerMenu.Builder(CreateCustomGigsActivityCopy.this).addItem(days).addItem(hours).setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT).setMenuRadius(10f).setMenuShadow(10f).setTextColor(ContextCompat.getColor(CreateCustomGigsActivityCopy.this, R.color.black)).setTextGravity(Gravity.CENTER).setTextTypeface(tf).setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE).setSelectedMenuColor(ContextCompat.getColor(CreateCustomGigsActivityCopy.this, R.color.colorPrimary)).setOnMenuItemClickListener((position, item) -> {
                powerMenu.setSelectedPosition(position);
                powerMenu.dismiss();
                binding.delTime.tvDelMethod.setText(item.getTitle());
            }).build();

            powerMenu.showAsDropDown(binding.delTime.tvDelMethod);
        });


        binding.delTime.etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (getCurrency().equals("SAR")) {
                    if (!binding.delTime.etPrice.getText().toString().endsWith(getString(R.string.sar))) {
                        binding.delTime.etPrice.removeTextChangedListener(this);
                        String val = binding.delTime.etPrice.getText().toString().replaceAll(getString(R.string.sar), "").replace(" ", "");
                        int len = val.length();
                        binding.delTime.etPrice.setText(val + " " + getString(R.string.sar));
                        binding.delTime.etPrice.setSelection(len);
                        binding.delTime.etPrice.addTextChangedListener(this);
                    } else if (binding.delTime.etPrice.getText().toString().equalsIgnoreCase(getString(R.string.sar))) {
                        binding.delTime.etPrice.removeTextChangedListener(this);
                        binding.delTime.etPrice.setText("");
                        binding.delTime.etPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                        binding.delTime.etPrice.addTextChangedListener(this);
                    }
                } else {
                    if (!binding.delTime.etPrice.getText().toString().startsWith(getString(R.string.dollar))) {
                        binding.delTime.etPrice.removeTextChangedListener(this);
                        binding.delTime.etPrice.getText().toString().replaceAll(getString(R.string.dollar), "");
                        binding.delTime.etPrice.setText(getString(R.string.dollar) + binding.delTime.etPrice.getText().toString());
                        binding.delTime.etPrice.setSelection(binding.delTime.etPrice.getText().toString().length());
                        binding.delTime.etPrice.addTextChangedListener(this);
                    } else if (binding.delTime.etPrice.getText().toString().equalsIgnoreCase(getString(R.string.dollar))) {
                        binding.delTime.etPrice.removeTextChangedListener(this);
                        binding.delTime.etPrice.setText("");
                        binding.delTime.etPrice.addTextChangedListener(this);
                    }
                }
            }
        });

        hideShowInfluencerView();
        if (!isEdit && !isDuplicate) {
            addDefaultRequirement();
        }
    }

    public void addDefaultRequirement() {
        RequiremetList.Data gigPack = new RequiremetList.Data();
        gigPack.name = "";
        gigPack.inputType = 1;
        gigPack.id = 0;
        gigPack.gigReqType = 1;
        gigPack.isSelected = false;
        gigPack.isOther = 1;
        gigPack.isOtherReq = true;
        gigPack.gigOtherInputType = 3;
        gigPack.reqDescription = "";
        gigPack.dataValue = "";
        gigPack.featureTitle = "";
        gigPack.dataReq = "";
        if (gigPack.customData == null) {
            gigPack.customData = new ArrayList<>();
        }
        ArrayList<RequiremetList.CustomData> customDataList = new ArrayList<>();
        RequiremetList.CustomData customData = new RequiremetList.CustomData();
        customData.dataReq = "";
        customData.dataValue = "";
        customDataList.add(customData);
        gigPack.customData.addAll(customDataList);

        addDynamicView(gigPack, false, false);

        requirementByCatListBinding.add(gigPack);
    }

    private void addMoreDeliveryOption() {

        binding.delTime.relAddMore.setOnClickListener(view1 -> {

            GigCategoryModel.Deadline deadline = new GigCategoryModel.Deadline();

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View customView = inflater.inflate(R.layout.deliver_time_price, null);
            ImageView imgDeleteCustom = customView.findViewById(R.id.img_delete);
            EditText etCustomPrice = customView.findViewById(R.id.et_price);
            EditText etTime = customView.findViewById(R.id.et_number);
            TextView tvDelMethod = customView.findViewById(R.id.tv_del_method);
            etTime.setBackground(getResources().getDrawable(R.drawable.gray_button_bg_left_radius));
            tvDelMethod.setBackground(getResources().getDrawable(R.drawable.black_button_bg_6_right));
            etTime.setHint(getString(R.string.enter_time));
            etCustomPrice.setHint(getString(R.string.price));
            tvDelMethod.setText(getString(R.string.days));

            deadline.type = 2;
            customView.setTag(deadline);
            etTime.requestFocus();
            showKeyboard(etTime);

            etTime.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!TextUtils.isEmpty(charSequence)) {
                        deadline.value = Integer.parseInt(charSequence.toString());
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            int viewCount = binding.delTime.linDynamicView.getChildCount();
            if (viewCount == CUSTOM_VIEW_LIMIT) {
                toastMessage(getString(R.string.you_cant_add_more_than_20_options));
                return;
            }

            imgDeleteCustom.setOnClickListener(view2 -> removeDeadlineDialog(customView, deadline, binding.delTime.linDynamicView, binding.delTime.relAddMore));

            etCustomPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (getCurrency().equals("SAR")) {
                        if (!TextUtils.isEmpty(charSequence) && !etCustomPrice.getText().toString().equalsIgnoreCase(getString(R.string.sar))) {
                            if (!TextUtils.isEmpty(etCustomPrice.getText().toString().trim().replace(getString(R.string.sar), ""))) {
                                deadline.price = Double.parseDouble(etCustomPrice.getText().toString().trim().replace(getString(R.string.sar), ""));
                            } else {
                                deadline.price = 0;
                            }
                        } else {
                            deadline.price = 0;
                        }
                    } else {
                        if (!TextUtils.isEmpty(charSequence) && !etCustomPrice.getText().toString().equalsIgnoreCase(getString(R.string.dollar))) {
                            if (!TextUtils.isEmpty(etCustomPrice.getText().toString().trim().replace(getString(R.string.dollar), ""))) {
                                deadline.price = Double.parseDouble(etCustomPrice.getText().toString().replace(getString(R.string.dollar), ""));
                            } else {
                                deadline.price = 0;
                            }
                        } else {
                            deadline.price = 0;
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (getCurrency().equals("SAR")) {
                        if (!etCustomPrice.getText().toString().endsWith(getString(R.string.sar))) {
                            etCustomPrice.removeTextChangedListener(this);
                            etCustomPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                            int len = etCustomPrice.getText().toString().length();
                            etCustomPrice.setText(etCustomPrice.getText().toString() + " " + getString(R.string.sar));
                            etCustomPrice.setSelection(len);
                            etCustomPrice.addTextChangedListener(this);
                        } else if (etCustomPrice.getText().toString().equalsIgnoreCase(getString(R.string.sar))) {
                            etCustomPrice.removeTextChangedListener(this);
                            etCustomPrice.setText("");
                            etCustomPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                            etCustomPrice.addTextChangedListener(this);
                        }
                    } else {
                        if (!etCustomPrice.getText().toString().startsWith(getString(R.string.dollar))) {
                            etCustomPrice.removeTextChangedListener(this);
                            etCustomPrice.getText().toString().replaceAll(getString(R.string.dollar), "");
                            etCustomPrice.setText(getString(R.string.dollar) + etCustomPrice.getText().toString());
                            etCustomPrice.setSelection(etCustomPrice.getText().toString().length());
                            etCustomPrice.addTextChangedListener(this);
                        } else if (etCustomPrice.getText().toString().equalsIgnoreCase(getString(R.string.dollar))) {
                            etCustomPrice.removeTextChangedListener(this);
                            etCustomPrice.setText("");
                            etCustomPrice.addTextChangedListener(this);
                        }
                    }
                }
            });

            tvDelMethod.setOnClickListener(view2 -> {
                powerMenu = new PowerMenu.Builder(CreateCustomGigsActivityCopy.this).addItem(days).addItem(hours).setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT).setMenuRadius(10f).setMenuShadow(10f).setTextColor(ContextCompat.getColor(CreateCustomGigsActivityCopy.this, R.color.black)).setTextGravity(Gravity.CENTER).setTextTypeface(tf).setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE).setSelectedMenuColor(ContextCompat.getColor(CreateCustomGigsActivityCopy.this, R.color.colorPrimary)).setOnMenuItemClickListener((position, item) -> {
                    powerMenu.setSelectedPosition(position);
                    powerMenu.dismiss();
                    tvDelMethod.setText(item.getTitle());
                    deadline.type = item.getTitle().equals(getString(R.string.days)) ? 2 : 1;
                }).build();

                powerMenu.showAsDropDown(tvDelMethod);
            });

            deadlineList.add(deadline);

            binding.delTime.linDynamicView.addView(customView);
            imgDeleteCustom.setTag(binding.delTime.linDynamicView.getChildCount() - 1);

            if (binding.delTime.linDynamicView.getChildCount() == CUSTOM_VIEW_LIMIT) {//hide 'Add More' option button if its 20
                binding.delTime.relAddMore.setVisibility(View.INVISIBLE);
            }
        });

        binding.delTime.relAddDescription.setOnClickListener(view1 -> {
            Intent packgDesInt = new Intent(this, GigDescriptionActivity.class);
            packgDesInt.putExtra("screen", GIG_DEAD_REQUIREMENT_DESC);
            packgDesInt.putExtra("data", "" + deadlineDescription);
            if (selectedCategory.id == 4352) {//social influencer
                packgDesInt.putExtra("is_influe", true);
            }
            startActivityForResult(packgDesInt, GIG_DEAD_REQUIREMENT_DESC);
        });
    }

    private void callRequirementByCatActivity(int code) {
        Intent intentReq = new Intent(CreateCustomGigsActivityCopy.this, GigAddRequirementActivity.class);
        if (requirementByCatListBinding != null && requirementByCatListBinding.size() > 0) {
            ArrayList<RequiremetList.Data> tempList = new ArrayList<>();
            for (RequiremetList.Data apiData : requirementByCatList) {
                for (RequiremetList.Data selData : requirementByCatListBinding) {
                    if (selData.id != null && selData.id.equals(apiData.id)) {
                        tempList.add(apiData);
                        break;
                    }
                }
            }
            requirementByCatList.removeAll(tempList);
        }

        intentReq.putExtra("list", requirementByCatList);
        startActivityForResult(intentReq, code);
    }

    private ArrayList<GigSubCategoryModel.Data> subCategoryList;
    private boolean isClickOnSkill;

    private void callSubCategoryActivity(ArrayList<GigSubCategoryModel.Data> data) {
        createGigsActivityVM.getIsHideProgress().postValue(2);
        if (selectedCategory == null || selectedCategory.id == 0) {
            toastMessage(getString(R.string.please_select_category_first));
            return;
        }
        subCategoryList = data;
        if (isClickOnSkill) {
            Intent intent = new Intent(CreateCustomGigsActivityCopy.this, GigSubCategoryActivity.class);
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_category:
                if (catList != null && catList.size() > 0) {
                    callGigCategoryActivity(catList, true);
                } else {
                    createGigsActivityVM.getServiceCategories();
                }
                break;
            case R.id.lin_description:
                Intent descInt = new Intent(this, GigDescriptionActivity.class);
                descInt.putExtra("screen", GIG_DESC);
                descInt.putExtra("data", binding.txtDescription.getText().toString().equalsIgnoreCase("") ? "" : binding.txtDescription.getText().toString());
                startActivityForResult(descInt, GIG_DESC);
                break;
            case R.id.lin_search_tag:
                Intent intentTag = new Intent(CreateCustomGigsActivityCopy.this, GigSearchTagActivity.class);
                intentTag.putExtra("data", searchTags);
                intentTag.putExtra("list", subCategoryList);
                startActivityForResult(intentTag, 7);
                break;
            case R.id.lin_gig_photos:
                if (TextUtils.isEmpty(binding.txtPhotos.getText().toString())) {
                    checkPermission();
                } else {
                    Intent intentPhoto = new Intent(CreateCustomGigsActivityCopy.this, GigPhotosActivity.class);
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
                packgInt.putExtra("tab", "");
                packgInt.putExtra("data", "");
                startActivityForResult(packgInt, GIG_PACKAGE_NAME);
                break;
            case R.id.lin_package_desc:
                Intent packgDesInt = new Intent(this, GigDescriptionActivity.class);
                packgDesInt.putExtra("screen", GIG_PACKAGE_DESC);
                packgDesInt.putExtra("data", "");
                startActivityForResult(packgDesInt, GIG_PACKAGE_DESC);
                break;
            case R.id.lin_skills:
                isClickOnSkill = true;
                if (selectedCategory != null && (subCategoryList == null || subCategoryList.size() == 0)) {
                    createGigsActivityVM.getServiceCategoriesById(selectedCategory.id);
                } else {
                    callSubCategoryActivity(subCategoryList);
                }
                break;
            case R.id.rel_save:
//                if (checkValidation()) {
                makeRequest(false);
//                }
                break;
            case R.id.rel_live:
                if (checkValidation()) {
                    makeRequest(true);
                }
                break;
            case R.id.tv_delete:
                deleteGigDialog(this);
                break;
            case R.id.img_delete:
                deadlineList.clear();
                binding.delTime.etNumber.setText("");
                binding.delTime.etPrice.setText("");
                binding.delTime.linDelivery.setVisibility(View.GONE);
                binding.delTime.imgDelete.setVisibility(View.GONE);
                break;
            case R.id.rel_pause:
                if (checkValidation()) {
                    createGigsActivityVM.activeInactiveGig(gigDetails.gigID, 1);
                }
                break;
            case R.id.rel_active:
                if (checkValidation()) {
                    createGigsActivityVM.activeInactiveGig(gigDetails.gigID, 3);
                }
                break;
            case R.id.tv_add_requirement:
                //TODO: add requirement view directly
                addDefaultRequirement();
//                if (binding.linDynamicView.getChildCount() == 10) {
//                    toastMessage(getString(R.string.you_cantt_add_more_than_10_requirements));
//                    return;
//                }
//
//                if (requirementByCatList != null && requirementByCatList.size() > 0) {
//                    callRequirementByCatActivity(101);
//                } else {
//                    createGigsActivityVM.getRequirementById(selectedCategory.id, true);
//                }
                break;
            case R.id.rel_view_public:
                if (checkValidation()) {
                    ArrayList<GigCategoryModel.Deadline> deadlines = new ArrayList<>();

                    GigCategoryModel.Deadline deadline = new GigCategoryModel.Deadline();
                    deadline.value = Integer.parseInt(binding.delTime.etNumber.getText().toString());
                    deadline.type = binding.delTime.tvDelMethod.getText().toString().equals(getString(R.string.days)) ? 2 : 1;
                    if (!TextUtils.isEmpty(binding.delTime.etPrice.getText().toString())) {
                        deadline.price = Double.parseDouble(getCurrency().equals("SAR") ? Utils.priceWithoutSAR(this, binding.delTime.etPrice.getText().toString())
                                : Utils.priceWithout$(binding.delTime.etPrice.getText().toString()));
                    }
                    deadline.id = -1;
                    deadlines.add(deadline);

                    deadlines.addAll(deadlineList);

                    Intent intent1 = new Intent(this, CustomGigViewlActivity.class);
                    intent1.putExtra("title", binding.txtGigTitle.getText().toString());
                    intent1.putExtra("desc", binding.txtDescription.getText().toString());
                    intent1.putExtra("deadDesc", deadlineDescription);
                    intent1.putExtra("delivery", deadlines);
                    intent1.putExtra("files", fileList);
                    intent1.putExtra("platform", selectedPlatform);
                    intent1.putExtra("catId", selectedCategory.id);
                    intent1.putExtra("packages", requirementByCatListBinding);
                    startActivity(intent1);
                }
                break;
            case R.id.lin_platform:
                createGigsActivityVM.getInfluencerPlatform();
                break;
            /*case R.id.lin_gig_price:
                if (selectedCategory == null) {
                    toastMessage("Please select Category first");
                    return;
                }
                Intent gigPrice = new Intent(this, EnterPriceActivity.class);
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
                break;*/
        }
    }

    private int selectedReqDescPos = -1;

    private void addDynamicView(RequiremetList.Data req, boolean isNumber, boolean isEdit) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View finalView;
        ImageView imgDelete = null;
        EditText etPrice;

        View view = inflater.inflate(R.layout.item_custom_gig, null);
        imgDelete = view.findViewById(R.id.img_delete);
        RadioGroup radioGroup = view.findViewById(R.id.rg);
        RadioButton radioFixedPrice = view.findViewById(R.id.rb_fixed_price);
        RadioButton radioCustPrice = view.findViewById(R.id.rb_custom_price);
        RelativeLayout relAddMore = view.findViewById(R.id.rel_add_more);
        RelativeLayout relAddDesc = view.findViewById(R.id.rel_add_description);
        LinearLayout dynamicCustomView = view.findViewById(R.id.lin_dynamicView);
        LinearLayout linReqTitle = view.findViewById(R.id.lin_requirement_title);
        EditText etReqTitle = view.findViewById(R.id.txt_reqTitle);
        EditText etReqName = view.findViewById(R.id.et_req);
        etPrice = view.findViewById(R.id.et_price);
        TextView txtDesc = view.findViewById(R.id.txt_add_desc);

        etReqTitle.setFocusable(true);
        etReqTitle.setFocusableInTouchMode(true);
        etReqTitle.setClickable(true);
        etReqTitle.setText("");
        etReqName.setText("");

        radioFixedPrice.setText(getString(R.string.fix_price));
        radioCustPrice.setText(getString(R.string.custom_price));
        etPrice.setHint(getString(R.string.enter_price));
        etReqName.setHint(getString(R.string.enter_requirement));
        etReqTitle.setHint(getString(R.string.requirements));
//        if (selectedCategory.id == 4352) {//social influencer
//            if (!TextUtils.isEmpty(req.reqDescription)) {
//                relAddDesc.setTag(req.reqDescription);
//            }
//            txtDesc.setText(req.name + " Link");
//        } else {
        if (!TextUtils.isEmpty(req.reqDescription)) {
            relAddDesc.setTag(req.reqDescription);
            txtDesc.setText(getString(R.string.edit_description));
        } else {
            txtDesc.setText(getString(R.string.add_description));
        }
//        }

        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                req.dataValue = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (getCurrency().equals("SAR")) {
                    if (!etPrice.getText().toString().endsWith(getString(R.string.sar))) {
                        etPrice.removeTextChangedListener(this);
                        etPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                        int len = etPrice.getText().toString().length();
                        etPrice.setText(etPrice.getText().toString() + " " + getString(R.string.sar));
                        etPrice.setSelection(len);
                        etPrice.addTextChangedListener(this);
                    } else if (etPrice.getText().toString().equalsIgnoreCase(getString(R.string.sar))) {
                        etPrice.removeTextChangedListener(this);
                        etPrice.setText("");
                        etPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                        etPrice.addTextChangedListener(this);
                    }
                } else {
                    if (!etPrice.getText().toString().startsWith(getString(R.string.dollar))) {
                        etPrice.removeTextChangedListener(this);
                        etPrice.getText().toString().replaceAll(getString(R.string.dollar), "");
                        etPrice.setText(getString(R.string.dollar) + etPrice.getText().toString());
                        etPrice.setSelection(etPrice.getText().toString().length());
                        etPrice.addTextChangedListener(this);
                    } else if (etPrice.getText().toString().equalsIgnoreCase(getString(R.string.dollar))) {
                        etPrice.removeTextChangedListener(this);
                        etPrice.setText("");
                        etPrice.addTextChangedListener(this);
                    }
                }
            }
        });

        etReqName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (!TextUtils.isEmpty(req.name) && !req.name.equalsIgnoreCase("Other")) {
                req.dataReq = charSequence.toString();
//                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (!TextUtils.isEmpty(req.name)) {
            etReqTitle.setText(req.name);

            if (!TextUtils.isEmpty(req.dataReq)) {//edit case
                if (req.gigReqType == 1) {
                    etReqName.setText(" " + req.dataReq);
                } else {
                    etReqName.setText("" + req.dataReq);
                }
            } else {
//                if (selectedCategory.id == 4352) {//social influencer
//                    etReqName.setText("1 Post");
//                } else {
                etReqName.setText("" + req.name);
//                }

            }
        }

        etReqTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((req.isOther == 1 || req.isOther == -1)) {
                    if (req.gigReqType == 1) {
                        etReqName.setText("" + charSequence.toString());
                    }
                    req.name = charSequence.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        /*if (!TextUtils.isEmpty(req.name) && req.name.equalsIgnoreCase(getString(R.string.other))) {
            //if (!isNumber) {//if type = 'text' at that time need to allow user to enter other title & there is no option in 'number' case
            etReqTitle.setFocusable(true);
            etReqTitle.setFocusableInTouchMode(true);
            etReqTitle.setClickable(true);
            etReqTitle.setText("");
            etReqName.setText("");

        } else {
            etReqTitle.setFocusable(false);
            etReqTitle.setFocusableInTouchMode(false);
            etReqTitle.setClickable(false);
        }*/

        if (!TextUtils.isEmpty(req.dataValue)) {
            etPrice.setText(format.format(Double.valueOf(req.dataValue)));
        }

//        if (req.gigReqType == 1) {//fixed
//            etReqName.setFocusable(false);
//            etReqName.setFocusableInTouchMode(false);
//            etReqName.setClickable(false);
//        }

        if (isEdit || isDuplicate) {
            if (req.gigReqType == 1) {//fixed
                radioFixedPrice.setChecked(true);
//                relAddMore.setVisibility(View.GONE);
                relAddMore.setVisibility(View.INVISIBLE);

//                etReqName.setFocusable(false);
//                etReqName.setFocusableInTouchMode(false);
//                etReqName.setClickable(false);
//                if (selectedCategory.id == 4352) {//social influencer
//                    etReqName.setText("1 Post");
//                } else {
                etReqName.setText("" + etReqTitle.getText().toString());
//                }

            } else if (req.gigReqType == 3) {//custom
                radioCustPrice.setChecked(true);
//                relAddMore.setVisibility(View.VISIBLE);
                relAddMore.setVisibility(View.VISIBLE);

//                etReqName.setFocusable(true);
//                etReqName.setFocusableInTouchMode(true);
//                etReqName.setClickable(true);
            }
        }

        radioGroup.setOnCheckedChangeListener((arg0, id) -> {
            switch (id) {
                case R.id.rb_custom_price://custom
//                    relAddMore.setVisibility(View.VISIBLE);
                    relAddMore.setVisibility(View.VISIBLE);
                    etReqName.setText("");
//                    etReqName.setFocusable(true);
//                    etReqName.setFocusableInTouchMode(true);
//                    etReqName.setClickable(true);
                    req.gigReqType = 3;
                    break;
                case R.id.rb_fixed_price://fixed
                    req.gigReqType = 1;
                    req.customData = null;
                    req.dataReq = "";
//                    etReqName.setFocusable(false);
//                    etReqName.setFocusableInTouchMode(false);
//                    etReqName.setClickable(false);

                    if (!TextUtils.isEmpty(req.name) && req.name.equalsIgnoreCase(getString(R.string.other))) {

                        if (!TextUtils.isEmpty(etReqTitle.getText().toString())) {
                            etReqName.setText("" + etReqTitle.getText().toString());
                        }

                    } else {

                        if (!TextUtils.isEmpty(req.dataReq)) {
                            etReqName.setText(req.dataReq);
                        } else if (!TextUtils.isEmpty(req.name)) {
//                            if (selectedCategory.id == 4352) {//social influencer
//                                etReqName.setText("1 Post");
//                            } else {
                            etReqName.setText("" + req.name);
//                            }
                        }
                    }

//                    relAddMore.setVisibility(View.GONE);
                    relAddMore.setVisibility(View.INVISIBLE);
                    dynamicCustomView.removeAllViews();
                    break;
            }
        });

        finalView = view;
        finalView.setTag(req);
        ImageView finalImgDelete = imgDelete;
        imgDelete.setOnClickListener(view1 -> removeRequirementDialog(true, finalView, req, dynamicCustomView, relAddMore, (Integer) finalImgDelete.getTag()));

        if (isEdit) {
            addCustomView(req, dynamicCustomView, relAddMore, finalImgDelete);
        }

        relAddMore.setOnClickListener(view1 -> {

            View customView = inflater.inflate(R.layout.item_custom_gig_customprice, null);
            ImageView imgDeleteCustom = customView.findViewById(R.id.img_delete);
            EditText etCustomPrice = customView.findViewById(R.id.et_price);
            EditText etCustomReq = customView.findViewById(R.id.et_req);

            etCustomReq.setHint(getString(R.string.enter_requirement));
            etCustomPrice.setHint(getString(R.string.enter_price));

            RequiremetList.CustomData customData = new RequiremetList.CustomData();
            if (req.customData == null) {
                req.customData = new ArrayList<>();
            }

            customView.setTag(customData);

            etCustomReq.requestFocus();
            showKeyboard(etCustomReq);

            etCustomReq.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                            req.featureTitle = charSequence.toString();

                    int subViewPos = (int) imgDeleteCustom.getTag();

                    req.customData.get(subViewPos).dataReq = charSequence.toString();

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            int viewCount = dynamicCustomView.getChildCount();
            if (viewCount == CUSTOM_VIEW_LIMIT) {
                toastMessage(getString(R.string.you_cant_add_more_than_20_options));
                return;
            }

            imgDeleteCustom.setOnClickListener(view2 -> removeRequirementDialog(false, customView, req, dynamicCustomView, relAddMore, (Integer) finalImgDelete.getTag()));

            etCustomPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                            req.dataValue = charSequence.toString();
                    int subViewPos = (int) imgDeleteCustom.getTag();
                    req.customData.get(subViewPos).dataValue = charSequence.toString();

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (getCurrency().equals("SAR")) {
                        if (!etCustomPrice.getText().toString().endsWith(getString(R.string.sar))) {
                            etCustomPrice.removeTextChangedListener(this);
                            etCustomPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                            int len = etCustomPrice.getText().toString().length();
                            etCustomPrice.setText(etCustomPrice.getText().toString() + " " + getString(R.string.sar));
                            etCustomPrice.setSelection(len);
                            etCustomPrice.addTextChangedListener(this);
                        } else if (etCustomPrice.getText().toString().equalsIgnoreCase(getString(R.string.sar))) {
                            etCustomPrice.removeTextChangedListener(this);
                            etCustomPrice.setText("");
                            etCustomPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                            etCustomPrice.addTextChangedListener(this);
                        }
                    } else {
                        if (!etCustomPrice.getText().toString().startsWith(getString(R.string.dollar))) {
                            etCustomPrice.removeTextChangedListener(this);
                            etCustomPrice.getText().toString().replaceAll(getString(R.string.dollar), "");
                            etCustomPrice.setText(getString(R.string.dollar) + etCustomPrice.getText().toString());
                            etCustomPrice.setSelection(etCustomPrice.getText().toString().length());
                            etCustomPrice.addTextChangedListener(this);
                        } else if (etCustomPrice.getText().toString().equalsIgnoreCase(getString(R.string.dollar))) {
                            etCustomPrice.removeTextChangedListener(this);
                            etCustomPrice.setText("");
                            etCustomPrice.addTextChangedListener(this);
                        }
                    }

                }
            });

            req.customData.add(customData);

            dynamicCustomView.addView(customView);
            imgDeleteCustom.setTag(dynamicCustomView.getChildCount() - 1);

            if (dynamicCustomView.getChildCount() == CUSTOM_VIEW_LIMIT) {//hide 'Add More' option button if its 20
                relAddMore.setVisibility(View.INVISIBLE);
            }
        });

        ImageView finalImgDelete1 = imgDelete;
        relAddDesc.setOnClickListener(view1 -> {
            selectedReqDescPos = (int) finalImgDelete1.getTag();
            Intent packgDesInt = new Intent(this, GigDescriptionActivity.class);
            packgDesInt.putExtra("screen", GIG_REQUIREMENT_DESC);
//            if (!TextUtils.isEmpty((CharSequence) relAddDesc.getTag())) {
//                packgDesInt.putExtra("data", "" + relAddDesc.getTag());
//            } else if (!TextUtils.isEmpty(req.reqDescription)) {
            if (selectedCategory.id == 4352) {//social influencer
                packgDesInt.putExtra("is_influe", true);
            }
            packgDesInt.putExtra("data", "" + req.reqDescription);
//            }
            startActivityForResult(packgDesInt, GIG_REQUIREMENT_DESC);
        });

        binding.linDynamicView.addView(view);
        imgDelete.setTag(binding.linDynamicView.getChildCount() - 1);
    }

    private void addCustomView(RequiremetList.Data req, LinearLayout dynamicCustomView, RelativeLayout linCustomOptions, ImageView finalImgDelete) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (req.customData != null && req.customData.size() > 0) {

            for (RequiremetList.CustomData cData : req.customData) {

                View customView = inflater.inflate(R.layout.item_custom_gig_customprice, null);
                ImageView imgDeleteCustom = customView.findViewById(R.id.img_delete);
                EditText etCustomPrice = customView.findViewById(R.id.et_price);
                EditText etCustomReq = customView.findViewById(R.id.et_req);

                etCustomReq.setHint(getString(R.string.enter_requirement));
                etCustomPrice.setHint(getString(R.string.enter_price));

                customView.setTag(cData);
                if (!TextUtils.isEmpty(cData.dataValue)) {
                    etCustomPrice.setText(getCurrency().equals("SAR") ? Utils.priceWithSAR(this, format.format(Double.valueOf(cData.dataValue)))
                            : Utils.priceWith$(format.format(Double.valueOf(cData.dataValue)), this));
                }
                etCustomReq.setText(cData.dataReq);

                etCustomReq.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                            req.featureTitle = charSequence.toString();

                        int subViewPos = (int) imgDeleteCustom.getTag();

                        req.customData.get(subViewPos).dataReq = charSequence.toString();

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                int viewCount = dynamicCustomView.getChildCount();
                if (viewCount == CUSTOM_VIEW_LIMIT) {
                    toastMessage(getString(R.string.you_cant_add_more_than_20_options));
                    return;
                }

                imgDeleteCustom.setOnClickListener(view2 -> removeRequirementDialog(false, customView, req, dynamicCustomView, linCustomOptions, (Integer) finalImgDelete.getTag()));

                etCustomPrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                            req.dataValue = charSequence.toString();
                        int subViewPos = (int) imgDeleteCustom.getTag();
                        req.customData.get(subViewPos).dataValue = charSequence.toString();

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (getCurrency().equals("SAR")) {
                            if (!etCustomPrice.getText().toString().endsWith(getString(R.string.sar))) {
                                etCustomPrice.removeTextChangedListener(this);
                                etCustomPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                                int len = etCustomPrice.getText().toString().length();
                                etCustomPrice.setText(etCustomPrice.getText().toString() + " " + getString(R.string.sar));
                                etCustomPrice.setSelection(len);
                                etCustomPrice.addTextChangedListener(this);
                            } else if (etCustomPrice.getText().toString().equalsIgnoreCase(getString(R.string.sar))) {
                                etCustomPrice.removeTextChangedListener(this);
                                etCustomPrice.setText("");
                                etCustomPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                                etCustomPrice.addTextChangedListener(this);
                            }
                        } else {
                            if (!etCustomPrice.getText().toString().startsWith(getString(R.string.dollar))) {
                                etCustomPrice.removeTextChangedListener(this);
                                etCustomPrice.getText().toString().replaceAll(getString(R.string.dollar), "");
                                etCustomPrice.setText(getString(R.string.dollar) + etCustomPrice.getText().toString());
                                etCustomPrice.setSelection(etCustomPrice.getText().toString().length());
                                etCustomPrice.addTextChangedListener(this);
                            } else if (etCustomPrice.getText().toString().equalsIgnoreCase(getString(R.string.dollar))) {
                                etCustomPrice.removeTextChangedListener(this);
                                etCustomPrice.setText("");
                                etCustomPrice.addTextChangedListener(this);
                            }
                        }
                    }
                });

                dynamicCustomView.addView(customView);
                imgDeleteCustom.setTag(dynamicCustomView.getChildCount() - 1);

                if (dynamicCustomView.getChildCount() == CUSTOM_VIEW_LIMIT) {//hide 'Add More' option button if its 20
                    linCustomOptions.setVisibility(View.INVISIBLE);
                }

            }

        }

    }

    private void callGigCategoryActivity(ArrayList<GigCategoryModel.Data> catList, boolean isCallAct) {
        if (isCallAct) {
            Intent intent = new Intent(CreateCustomGigsActivityCopy.this, GigCategoryActivity.class);
            intent.putExtra("data", selectedCategory);
            intent.putExtra("list", catList);
            startActivityForResult(intent, 2);
        } else {
            try {
                GigCategoryModel.Data catData = new GigCategoryModel.Data();
                ProfileResponse profileData = Preferences.getProfileData(this);
                //set default category of expertise. If not found then set first from API response
                if (profileData != null && profileData.expertise != null && profileData.expertise.id != null) {
                    catData.id = profileData.expertise.id;
                    catData.nameApp = profileData.expertise.nameApp;
                    catData.name = profileData.expertise.name;
                } else {//api response
                    catData.id = catList.get(0).id;
                    catData.nameApp = catList.get(0).name;
                    catData.name = catList.get(0).name;
                }
                selectedCategory = catData;
                binding.txtCategory.setText(selectedCategory.nameApp);

                hideShowInfluencerView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void hideShowInfluencerView() {
        try {
            if (selectedCategory.id == 4352) {//social influencer
                binding.txtSkillLbl.setText(getString(R.string.industries));//just change lbl name
                binding.linPlatform.setVisibility(View.VISIBLE);
                if (!isEdit && !isDuplicate) {
                    binding.delTime.linDelivery.setVisibility(View.VISIBLE);
                }
            } else {
//                binding.txtSkillLbl.setText(getString(R.string.skills));
                binding.linPlatform.setVisibility(View.GONE);
                binding.delTime.linDelivery.setVisibility(View.VISIBLE);
                selectedPlatform = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        if (requirementByCatListBinding == null) {
            return;
        }

        //custom deadline
        JSONArray jsonArrayDeadline = new JSONArray();
        try {
            if (!TextUtils.isEmpty(binding.delTime.etNumber.getText().toString()) && Integer.parseInt(binding.delTime.etNumber.getText().toString()) > 0) {
                JSONObject objectDeadline = new JSONObject();
                objectDeadline.put("value", Integer.valueOf(binding.delTime.etNumber.getText().toString()));
                objectDeadline.put("price", Double.valueOf(getCurrency().equals("SAR") ? Utils.priceWithoutSAR(this, binding.delTime.etPrice.getText().toString())
                        : Utils.priceWithout$(binding.delTime.etPrice.getText().toString())));
                objectDeadline.put("type", binding.delTime.tvDelMethod.getText().toString().equals(getString(R.string.days)) ? 2 : 1);
                jsonArrayDeadline.put(objectDeadline);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (deadlineList != null && deadlineList.size() > 0) {
            for (GigCategoryModel.Deadline dead : deadlineList) {
                try {
                    if (dead.value > 0) {
                        JSONObject object = new JSONObject();
                        object.put("value", dead.value);
                        object.put("price", Double.parseDouble(getCurrency().equals("SAR") ? Utils.priceWithoutSAR(this, dead.price)
                                : Utils.priceWithout$(dead.price)));
                        object.put("type", dead.type);
                        jsonArrayDeadline.put(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //custom requirement
        JSONArray jsonArray = new JSONArray();
        for (RequiremetList.Data pack : requirementByCatListBinding) {
            if (pack.inputType != -1 && pack.isOther != 1) {//custom requirement (except other)
                try {
                    if (pack.gigReqType == 1) {//fixed type
                        if (!TextUtils.isEmpty(pack.dataValue)) {
                            JSONObject object = new JSONObject();
                            object.put("requirment", pack.featureTitle);
                            if (getCurrency().equals("SAR")) {
                                object.put("price", !pack.dataValue.equalsIgnoreCase(getString(R.string.sar)) ? Double.valueOf(Utils.priceWithoutSAR(this, pack.dataValue)) : null);
                            } else {
                                object.put("price", !pack.dataValue.equalsIgnoreCase(getString(R.string.dollar)) ? Double.valueOf(Utils.priceWithout$(pack.dataValue)) : null);
                            }

                            object.put("inputType", pack.inputType);
                            object.put("gigRequirementType", pack.gigReqType);
                            if (!TextUtils.isEmpty(pack.reqDescription)) {
                                object.put("description", pack.reqDescription);
                            }
                            jsonArray.put(object);
                        }
                    } else {//custom type
                        if (!TextUtils.isEmpty(pack.dataValue) && !TextUtils.isEmpty(pack.dataReq)) {
                            JSONObject object1 = new JSONObject();
                            object1.put("requirment", pack.id);
                            object1.put("inputType", pack.inputType);
                            object1.put("gigRequirementType", pack.gigReqType);
                            object1.put("featureName", pack.dataReq);

                            if (getCurrency().equals("SAR")) {
                                object1.put("price", !pack.dataValue.equalsIgnoreCase(getString(R.string.sar)) ? Double.valueOf(Utils.priceWithoutSAR(this, pack.dataValue)) : null);
                            } else {
                                object1.put("price", !pack.dataValue.equalsIgnoreCase(getString(R.string.dollar)) ? Double.valueOf(Utils.priceWithout$(pack.dataValue)) : null);
                            }
                            if (!TextUtils.isEmpty(pack.reqDescription)) {
                                object1.put("description", pack.reqDescription);
                            }
                            jsonArray.put(object1);
                        }

                        if (pack.customData != null && pack.customData.size() > 0) {//custom type with multiple options
                            for (RequiremetList.CustomData cutData : pack.customData) {
                                if (!TextUtils.isEmpty(cutData.dataReq) && !TextUtils.isEmpty(cutData.dataValue)) {
                                    JSONObject object = new JSONObject();
                                    object.put("requirment", pack.id);
                                    object.put("inputType", pack.inputType);
                                    object.put("gigRequirementType", pack.gigReqType);
                                    object.put("featureName", cutData.dataReq);
                                    if (getCurrency().equals("SAR")) {
                                        object.put("price", !cutData.dataValue.equalsIgnoreCase(getString(R.string.sar)) ? Double.valueOf(Utils.priceWithoutSAR(this, cutData.dataValue)) : null);
                                    } else {
                                        object.put("price", !cutData.dataValue.equalsIgnoreCase(getString(R.string.dollar)) ? Double.valueOf(Utils.priceWithout$(cutData.dataValue)) : null);
                                    }
                                    jsonArray.put(object);
                                }

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //other requirements
        JSONArray jsonArrayOther = new JSONArray();
        for (RequiremetList.Data pack : requirementByCatListBinding) {

            if (pack.isOther == 1) {//other requirement

                try {
                    if (pack.gigReqType == 1) {//fixed type
                        if (!TextUtils.isEmpty(pack.dataValue)) {
                            JSONObject object = new JSONObject();
                            object.put("requirment", pack.name);
                            object.put("price", getCurrency().equals("SAR") ? Double.valueOf(Utils.priceWithoutSAR(this, pack.dataValue))
                                    : Double.valueOf(Utils.priceWithout$(pack.dataValue)));
                            object.put("inputType", pack.inputType);
                            object.put("gigRequirementType", pack.gigReqType);
                            if (!TextUtils.isEmpty(pack.reqDescription)) {
                                object.put("description", pack.reqDescription);
                            }
                            jsonArrayOther.put(object);
                        }
                    } else {//custom type
                        if (!TextUtils.isEmpty(pack.dataValue) && !TextUtils.isEmpty(pack.dataReq)) {
                            JSONObject object1 = new JSONObject();
                            object1.put("requirment", pack.name);
                            if (getCurrency().equals("SAR")) {
                                object1.put("price", pack.dataValue != null ? Double.valueOf(Utils.priceWithoutSAR(this, pack.dataValue)) : null);
                            } else {
                                object1.put("price", pack.dataValue != null ? Double.valueOf(Utils.priceWithout$(pack.dataValue)) : null);
                            }

                            object1.put("inputType", pack.inputType);
                            object1.put("gigRequirementType", pack.gigReqType);
                            object1.put("featureName", pack.dataReq);
                            if (!TextUtils.isEmpty(pack.reqDescription)) {
                                object1.put("description", pack.reqDescription);
                            }
                            jsonArrayOther.put(object1);
                        }

                        if (pack.customData != null && pack.customData.size() > 0) {//custom type with multiple options
                            for (RequiremetList.CustomData cutData : pack.customData) {
                                if (!TextUtils.isEmpty(cutData.dataReq) && !TextUtils.isEmpty(cutData.dataValue)) {
                                    JSONObject object = new JSONObject();
                                    object.put("requirment", pack.name);
                                    object.put("inputType", pack.inputType);
                                    object.put("gigRequirementType", pack.gigReqType);
                                    object.put("featureName", cutData.dataReq);
                                    if (getCurrency().equals("SAR")) {
                                        object.put("price", cutData.dataValue != null ? Double.valueOf(Utils.priceWithoutSAR(this, cutData.dataValue)) : null);
                                    } else {
                                        object.put("price", cutData.dataValue != null ? Double.valueOf(Utils.priceWithout$(cutData.dataValue)) : null);
                                    }

                                    jsonArrayOther.put(object);
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //custom deadline
        JSONArray jsonArrayPlatform = new JSONArray();
        if (selectedPlatform != null) {
            jsonArrayPlatform.put(selectedPlatform.id);
        }


        JSONArray arraySubCat = new JSONArray();
        arraySubCat.put(selectedSubCat != null ? "" + selectedSubCat.id : "0");

        RequestBody profileIdBody = RequestBody.create(getUserID() + "", MultipartBody.FORM);
        RequestBody gigTitleBody = RequestBody.create(binding.txtGigTitle.getText().toString(), MultipartBody.FORM);
        RequestBody gigCatIdBody = RequestBody.create(selectedCategory != null ? "" + selectedCategory.id : "4352", MultipartBody.FORM);
        RequestBody gigSubCatIdBody = RequestBody.create(arraySubCat.toString(), MediaType.parse("application/json"));
        RequestBody gigDescBody = RequestBody.create(binding.txtDescription.getText().toString(), MultipartBody.FORM);
        RequestBody gigMainPrice = RequestBody.create("", MultipartBody.FORM);
        RequestBody gigPackagesBody = RequestBody.create(jsonArray.toString(), MediaType.parse("application/json"));
        RequestBody gigOtherReq = RequestBody.create(jsonArrayOther.toString(), MediaType.parse("application/json"));
        RequestBody deadlineArray = RequestBody.create(jsonArrayDeadline.toString(), MediaType.parse("application/json"));
        RequestBody platformArray = RequestBody.create(jsonArrayPlatform.toString(), MediaType.parse("application/json"));
        RequestBody deadlineDesc = null;
        if (!TextUtils.isEmpty(deadlineDescription)) {
            deadlineDesc = RequestBody.create(deadlineDescription, MultipartBody.FORM);
        }
//        RequestBody gigDeadlineValue = RequestBody.create(binding.delTime.etNumber.getText().toString(), MultipartBody.FORM);

        RequestBody gigId = null;
        RequestBody duplicateBody = null;
        RequestBody fileToDeleteBody = null;

        if (isEdit && gigDetails != null) {
            gigId = RequestBody.create("" + gigDetails.gigID, MultipartBody.FORM);
        } else if (isDuplicate && gigDetails != null) {
            duplicateBody = RequestBody.create("1", MultipartBody.FORM);
            gigId = RequestBody.create("" + gigDetails.gigID, MultipartBody.FORM);
            if (duplicateDeletedFile != null && duplicateDeletedFile.length() > 0) {
                fileToDeleteBody = RequestBody.create(duplicateDeletedFile.toString(), MediaType.parse("application/json"));
            }
        }

        RequestBody gigStatus = null;
        if (!isLive) {//for draft
            if (selectedScreenTab == 3 && !isDuplicate) {//pause tab
                gigStatus = RequestBody.create("3", MultipartBody.FORM);
            } else {
                gigStatus = RequestBody.create("2", MultipartBody.FORM);
            }

        }

        JSONArray jsonArrayTag = new JSONArray();
        for (String pack : searchTags) {
            jsonArrayTag.put(pack);
        }
//
        JSONArray jsonArrayLang = new JSONArray();
        for (Language.Data lang : selectedLanguageList) {
            jsonArrayLang.put(lang.id);
        }
        RequestBody gigLanguageBody = RequestBody.create(jsonArrayLang.toString(), MediaType.parse("application/json"));
        RequestBody gigTagsBody = RequestBody.create(jsonArrayTag.toString(), MediaType.parse("application/json"));

        if (isDuplicate) {
            createGigsActivityVM.duplicateGig(gigTitleBody, gigDescBody, gigCatIdBody, gigSubCatIdBody, gigPackagesBody, gigOtherReq, gigTagsBody, gigStatus, gigId, body, gigLanguageBody, fileToDeleteBody, duplicateBody, isLive, profileIdBody, deadlineArray, gigMainPrice, deadlineDesc, platformArray);
        } else {
            createGigsActivityVM.createUpdateGig(gigTitleBody, gigDescBody, gigCatIdBody, gigSubCatIdBody, gigPackagesBody, gigOtherReq, gigTagsBody, gigStatus, gigId, body, gigLanguageBody, isLive, profileIdBody, deadlineArray, gigMainPrice, deadlineDesc, platformArray);
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
                            GigCategoryModel.Data cat = new GigCategoryModel.Data();
                            cat.id = 4352;
                            cat.name = "Social star";
                            cat.nameApp = "star";

                            selectedCategory = cat;
                            selectedSubCat = null;
                            subCategoryList = null;
                            requirementByCatList = null;
                            requirementByCatListBinding = null;
                            binding.linDynamicView.removeAllViews();

//                            binding.txtSkills.setText("");

                            if (selectedCategory.id == 4352) {//social influencer
                                binding.txtSkillLbl.setText(getString(R.string.industries));//just change lbl name
//                                binding.delTime.txtAddDesc.setText("Add Link");
                                binding.linPlatform.setVisibility(View.VISIBLE);
                                binding.delTime.linDelivery.setVisibility(View.VISIBLE);
                            } else {
                                binding.delTime.linDelivery.setVisibility(View.VISIBLE);
//                                binding.txtSkillLbl.setText(getString(R.string.skills));
//                                binding.delTime.txtAddDesc.setText("Add Description");
                                binding.linPlatform.setVisibility(View.GONE);
                                selectedPlatform = null;
                            }

                            createGigsActivityVM.getServiceCategoriesById(selectedCategory.id);//call API for subcategory list
                            getChargesByCat(selectedCategory.id);
                        }
                    }
                    break;
                case 3:
                    if (data != null) {
                        selectedSubCat = (GigSubCategoryModel.Data) data.getSerializableExtra("data");
                        if (selectedSubCat != null) {
                            binding.txtSkills.setText(selectedSubCat.getName(language));
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
//                case 14:
//                    if (data != null) {
//                        String gigPrice = data.getStringExtra("data");
//                        binding.txtPrice.setText(String.format("$%s", gigPrice));
//                        binding.txtPrice.setTag(gigPrice);
//                    }
//                    break;
                case 14:
                    if (data != null) {
                        ConnectedSocialMedia.Data platforms = (ConnectedSocialMedia.Data) data.getSerializableExtra("channel");
                        binding.txtPlatform.setText(platforms.getName(language));
                        selectedPlatform = platforms;
                    }
                    break;
                case 101:
                case 102:
                    if (data != null) {
                        RequiremetList.Data req = (RequiremetList.Data) data.getSerializableExtra("req");
                        boolean isNumber = data.getBooleanExtra("isNumber", false);

                        if (req != null) {
                            if (selectedCategory.id == 4352 && req.id == 3141) {//social influencer
                                binding.delTime.linDelivery.setVisibility(View.VISIBLE);
                                binding.delTime.imgDelete.setVisibility(View.VISIBLE);
                            } else {
                                if (requirementByCatListBinding == null) {
                                    requirementByCatListBinding = new ArrayList<>();
                                }

                                requirementByCatListBinding.add(req);
                                req.gigReqType = 1;
                                addDynamicView(req, isNumber, false);
                            }
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
                            String photoText = fileList.size() == 1 ? "" + getString(R.string.photo) : "" + getString(R.string.photos);
                            binding.txtPhotos.setText("(" + fileList.size() + ") " + photoText);
                        } else {
                            binding.txtPhotos.setText("");
                        }
                    }
                    break;
                case 17://requirement description
                    if (data != null) {
                        String reqDescription = data.getStringExtra("data");
                        if (selectedReqDescPos != -1) {//selected description view position
                            requirementByCatListBinding.get(selectedReqDescPos).reqDescription = reqDescription;
                            selectedReqDescPos = -1;
                        }
                    }
                    break;
                case 18://deadline description
                    if (data != null) {
                        deadlineDescription = data.getStringExtra("data");
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
                            String photoText = fileList.size() == 1 ? "" + getString(R.string.photo) : "" + getString(R.string.photos);
                            binding.txtPhotos.setText("(" + fileList.size() + ") " + photoText);
                        } else {
                            toastMessage(getString(R.string.file_not_selected));
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

    public void checkPermission() {
        Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {

                    Intent intent = new Intent(CreateCustomGigsActivityCopy.this, ImagePickActivity.class);
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
        }).onSameThread().check();
    }

    private boolean checkValidation() {

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

//        if (fileList == null || fileList.size() == 0) {
//            toastMessage(getString(R.string.please_select_photos));
//            return false;
//        }
//
//        if (fileList.size() > 5) {
//            toastMessage(getString(R.string.only_five_photos_allowed));
//            return false;
//        }

        if (selectedCategory.id != 4352) {//ignore to check delivery time in case of select social influencer
            if (TextUtils.isEmpty(binding.delTime.etNumber.getText().toString().trim())) {
                toastMessage(getString(R.string.please_enter_delivery_time));
                return false;
            }
        }

        if (selectedCategory.id != 4352) {//ignore to check delivery time in case of select social influencer
            if (Double.parseDouble(binding.delTime.etNumber.getText().toString()) < 1) {
                toastMessage(getString(R.string.time_should_not_be_zero));
                return false;
            }
        }

        if (selectedCategory.id != 4352) {//ignore to check delivery time in case of select social influencer
            if (TextUtils.isEmpty(binding.delTime.etPrice.getText().toString().trim())) {
                toastMessage(getString(R.string.please_enter_delivery_price));
                return false;
            }
        }

//        if (Double.parseDouble(Utils.priceWithout$(binding.delTime.etPrice.getText().toString())) < 1) {
//            toastMessage("Delivery price should not be zero");
//            return false;
//        }

        if (requirementByCatListBinding == null || requirementByCatListBinding.size() == 0) {
            toastMessage(getString(R.string.please_add_at_least_one_requirement));
            return false;
        }

        for (int i = 0; i < requirementByCatListBinding.size(); i++) {
            RequiremetList.Data pack = requirementByCatListBinding.get(i);

            if (TextUtils.isEmpty(pack.dataValue)) {
                toastMessage(getString(R.string.please_enter_price_for) + pack.name);
                return false;
            }

            if (TextUtils.isEmpty(pack.dataReq)) {
                toastMessage(getString(R.string.please_enter_req_title));
                return false;
            }
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
                if (gigLanguageAdapter != null) gigLanguageAdapter.getFilter().filter(s.toString());
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

    void removeRequirementDialog(boolean isMainReq, View finalView, RequiremetList.Data req, LinearLayout customDynamicView, RelativeLayout linCustomOptions, int selPos) {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        String s;
        if (isMainReq) {
            s = getString(R.string.remove_req_msg);
        } else {
            s = getString(R.string.remove_option_req_msg);
        }

        String[] words = {getString(R.string.remove)};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvMessage.setText(Utils.getBoldString(this, s, fonts, null, words));

        tvCancel.setText(getString(R.string.cancel));
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();

            if (isMainReq) {
                requirementByCatListBinding.remove(finalView.getTag());
                binding.linDynamicView.removeView(finalView);
                if (requirementByCatList == null) {
                    requirementByCatList = new ArrayList<>();
                }
                requirementByCatList.add(req);
            } else {
                if (requirementByCatListBinding != null && requirementByCatListBinding.get(selPos).customData != null) {
                    RequiremetList.CustomData selData = (RequiremetList.CustomData) finalView.getTag();
                    for (RequiremetList.CustomData cutData : requirementByCatListBinding.get(selPos).customData) {
                        if (cutData.dataValue.equalsIgnoreCase(selData.dataValue) && cutData.dataReq.equalsIgnoreCase(selData.dataReq)) {
                            requirementByCatListBinding.get(selPos).customData.remove(cutData);
                            break;
                        }
                    }
                }

                customDynamicView.removeView(finalView);
                if (customDynamicView.getChildCount() < 20) {//show add more option when less than 20 option display
                    linCustomOptions.setVisibility(View.VISIBLE);
                }
            }

        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    void removeDeadlineDialog(View finalView, GigCategoryModel.Deadline deadline, LinearLayout customDynamicView, RelativeLayout relAddMoreCustomView) {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        String s = getString(R.string.remove_option_req_msg);

        String[] words = {getString(R.string.remove)};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvMessage.setText(Utils.getBoldString(this, s, fonts, null, words));

        tvCancel.setText(getString(R.string.cancel));
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();

            customDynamicView.removeView(finalView);
            deadlineList.remove(deadline);
            Log.e("deadline list ", "==================== " + deadlineList.size());
            if (customDynamicView.getChildCount() < 20) {//show add more option when less than 20 option display
                relAddMoreCustomView.setVisibility(View.VISIBLE);
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void editDeadlineView(GigCategoryModel.Deadline deadline) {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View customView = inflater.inflate(R.layout.deliver_time_price, null);
        ImageView imgDeleteCustom = customView.findViewById(R.id.img_delete);
        EditText etCustomPrice = customView.findViewById(R.id.et_price);
        EditText etTime = customView.findViewById(R.id.et_number);
        TextView tvDelMethod = customView.findViewById(R.id.tv_del_method);

        etTime.setHint(getString(R.string.enter_time));
        etCustomPrice.setHint(getString(R.string.price));
        tvDelMethod.setBackgroundResource(R.drawable.black_button_bg_6_right);

        etCustomPrice.setText(getCurrency().equals("SAR") ? Utils.priceWithSAR(this, format.format(deadline.price)) : Utils.priceWith$(format.format(deadline.price), this));
        etTime.setText("" + deadline.value);
        tvDelMethod.setText(deadline.type == 2 ? "" + getString(R.string.days) : "" + getString(R.string.hours));

        customView.setTag(deadline);

        etTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    deadline.value = Integer.parseInt(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        int viewCount = binding.delTime.linDynamicView.getChildCount();
        if (viewCount == CUSTOM_VIEW_LIMIT) {
            toastMessage(getString(R.string.you_cant_add_more_than_20_options));
            return;
        }

        imgDeleteCustom.setOnClickListener(view2 -> removeDeadlineDialog(customView, deadline, binding.delTime.linDynamicView, binding.delTime.relAddMore));

        etCustomPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && !charSequence.toString().equals(getString(R.string.dollar))) {
                    deadline.price = Double.parseDouble(charSequence.toString().replace(getString(R.string.dollar), ""));
                } else {
                    deadline.price = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (getCurrency().equals("SAR")) {
                    if (!etCustomPrice.getText().toString().endsWith(getString(R.string.sar))) {
                        etCustomPrice.removeTextChangedListener(this);
                        etCustomPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                        int len = etCustomPrice.getText().toString().length();
                        etCustomPrice.setText(etCustomPrice.getText().toString() + " " + getString(R.string.sar));
                        etCustomPrice.setSelection(len);
                        etCustomPrice.addTextChangedListener(this);
                    } else if (etCustomPrice.getText().toString().equalsIgnoreCase(getString(R.string.sar))) {
                        etCustomPrice.removeTextChangedListener(this);
                        etCustomPrice.setText("");
                        etCustomPrice.getText().toString().replaceAll(getString(R.string.sar), "");
                        etCustomPrice.addTextChangedListener(this);
                    }
                } else {
                    if (!etCustomPrice.getText().toString().startsWith(getString(R.string.dollar))) {
                        etCustomPrice.removeTextChangedListener(this);
                        etCustomPrice.getText().toString().replaceAll(getString(R.string.dollar), "");
                        etCustomPrice.setText(getString(R.string.dollar) + etCustomPrice.getText().toString());
                        etCustomPrice.setSelection(etCustomPrice.getText().toString().length());
                        etCustomPrice.addTextChangedListener(this);
                    } else if (etCustomPrice.getText().toString().equalsIgnoreCase(getString(R.string.dollar))) {
                        etCustomPrice.removeTextChangedListener(this);
                        etCustomPrice.setText("");
                        etCustomPrice.addTextChangedListener(this);
                    }
                }
            }
        });

        tvDelMethod.setOnClickListener(view2 -> {
            powerMenu = new PowerMenu.Builder(CreateCustomGigsActivityCopy.this).addItem(days).addItem(hours).setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT).setMenuRadius(10f).setMenuShadow(10f).setTextColor(ContextCompat.getColor(CreateCustomGigsActivityCopy.this, R.color.black)).setTextGravity(Gravity.CENTER).setTextTypeface(tf).setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE).setSelectedMenuColor(ContextCompat.getColor(CreateCustomGigsActivityCopy.this, R.color.colorPrimary)).setOnMenuItemClickListener((position, item) -> {
                powerMenu.setSelectedPosition(position);
                powerMenu.dismiss();
                tvDelMethod.setText(item.getTitle());
                deadline.type = item.getTitle().equals(getString(R.string.days)) ? 2 : 1;
            }).build();

            powerMenu.showAsDropDown(tvDelMethod);
        });

        deadlineList.add(deadline);

        binding.delTime.linDynamicView.addView(customView);
        imgDeleteCustom.setTag(binding.delTime.linDynamicView.getChildCount() - 1);

        if (binding.delTime.linDynamicView.getChildCount() == CUSTOM_VIEW_LIMIT) {//hide 'Add More' option button if its 20
            binding.delTime.relAddMore.setVisibility(View.INVISIBLE);
//            linAddCustomOption.setVisibility(View.GONE);
        }
    }
}
