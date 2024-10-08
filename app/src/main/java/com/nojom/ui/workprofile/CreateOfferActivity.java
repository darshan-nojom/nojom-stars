package com.nojom.ui.workprofile;

import static com.nojom.ui.workprofile.ChooseOfferActivity.chooseOfferActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.nojom.R;
import com.nojom.databinding.ActivityCreateOfferBinding;
import com.nojom.model.GigCatCharges;
import com.nojom.model.GigCategoryModel;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.gigs.CreateCustomGigsActivityVM;
import com.nojom.ui.gigs.GigCategoryActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CreateOfferActivity extends BaseActivity {

    private ActivityCreateOfferBinding binding;
    private PowerMenu powerMenu;
    private PowerMenuItem days, hours;
    private Typeface tf;
    private CreateCustomGigsActivityVM createCustomGigsActivityVM;
    private ArrayList<GigCategoryModel.Data> catList;
    private GigCategoryModel.Data selectedCategory;
    private String cUsername;
    private long cUserid;
    private CreateCustomGigsActivityVM createGigsActivityVM;
    private ArrayList<GigCatCharges.Data> chargeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_offer);
        createGigsActivityVM = new CreateCustomGigsActivityVM();
        createGigsActivityVM.init(this);
        cUsername = getIntent().getStringExtra("cUsername");
        cUserid = getIntent().getLongExtra("cUserId", 0);

        binding.llToolbar.tvTitle.setText(getString(R.string.create_offer));
        createCustomGigsActivityVM = new CreateCustomGigsActivityVM();
        createCustomGigsActivityVM.init(this);
        catList = new ArrayList<>();

        tf = Typeface.createFromAsset(getAssets(), Constants.SFTEXT_REGULAR);
        days = new PowerMenuItem(getString(R.string.days), true);
        hours = new PowerMenuItem(getString(R.string.hours), false);

        binding.txtPrice.setText(getCurrency().equals("SAR") ? getString(R.string.price) + " (0 "+getString(R.string.sar)+")" : getString(R.string.price) + " ($0)");

        ProfileResponse profileData = Preferences.getProfileData(this);
        if (profileData != null && profileData.expertise != null && profileData.expertise.id != null) {
            GigCategoryModel.Data catData = new GigCategoryModel.Data();
            catData.id = profileData.expertise.id;
            catData.nameApp = profileData.expertise.nameApp;
            catData.name = profileData.expertise.name;
            selectedCategory = catData;
            binding.txtCategory.setText(profileData.expertise.nameApp);
        }

        createCustomGigsActivityVM.getServiceCategories();

//        chargeList = Preferences.getCategoryCharges(this);
//        if (chargeList != null && chargeList.size() > 0) {
//            createGigsActivityVM.getGigCatChargesList().setValue(chargeList);
//            getChargesByCat(selectedCategory.id);
//        } else {
//            createGigsActivityVM.getGigCatCharges();
//        }
        createGigsActivityVM.getGigCatCharges();

        createGigsActivityVM.getGigCatChargesList().observe(this, data -> {
            chargeList = data;
            getChargesByCat(selectedCategory.id);
        });

        binding.llToolbar.imgBack.setOnClickListener(v -> finish());

        binding.rlCategory.setOnClickListener(v -> {
            if (catList != null && catList.size() > 0) {
                callGigCategoryActivity(catList, true);
            } else {
                createCustomGigsActivityVM.getServiceCategories();
            }
        });

        binding.tvDelMethod.setOnClickListener(v -> deliveryMethod());

        //gig category list response
        createCustomGigsActivityVM.getGigCategoryList().observe(this, data -> {
            catList = data;
            callGigCategoryActivity(data, false);
        });

        binding.txtSendOffer.setOnClickListener(v -> {
            if (checkValidation()) {
                showConfDialog();
            }
        });

        createCustomGigsActivityVM.getIsShowProgressCreateOffer().observe(this, isShow -> {
            disableEnableTouch(isShow);
            if (isShow) {
                binding.txtSendOffer.setVisibility(View.INVISIBLE);
                binding.progressBarView.setVisibility(View.VISIBLE);
            } else {
                binding.txtSendOffer.setVisibility(View.VISIBLE);
                binding.progressBarView.setVisibility(View.GONE);
            }
        });

        createCustomGigsActivityVM.getCreateOfferSuccess().observe(this, createOfferResponse -> {
            chooseOfferActivity.finish();
            Preferences.saveCreateOffer(CreateOfferActivity.this, createOfferResponse);
            finish();
        });

        if (getCurrency().equals("SAR")) {
            binding.etPrice.setHint("9.99 "+getString(R.string.sar));
        }

        binding.etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    binding.txtPrice.setText(getCurrency().equals("SAR") ? getString(R.string.price) + " (0 "+getString(R.string.sar)+")" : getString(R.string.price) + " ($0)");
                    calculatePercentage(0);
                } else {
                    binding.txtPrice.setText(getCurrency().equals("SAR") ? getString(R.string.price) + " (" + s + " "+getString(R.string.sar)+")"
                            : getString(R.string.price) + " ("+getString(R.string.dollar) + s + ")");
                    calculatePercentage(Double.parseDouble(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etDescription.setOnTouchListener((v, event) -> {
            if (binding.etDescription.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
            }
            return false;
        });

        binding.etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(s.toString()) && s.length() > 15) {
                    if (s.length() > 80) {
                        binding.tvCharacter.setText(getString(R.string.max_80_char));
                        binding.tvCharacter.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        binding.tvCharacter.setText(getString(R.string.just_perfect));
                        binding.tvCharacter.setTextColor(getResources().getColor(R.color.greendark));
                    }
                } else {
                    binding.tvCharacter.setText(getString(R.string.min_15_char));
                    binding.tvCharacter.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(s.toString()) && s.length() > 50) {
                    if (s.length() > 1200) {
                        binding.tvCharacterDesc.setText(getString(R.string.max_1200_char));
                        binding.tvCharacterDesc.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        binding.tvCharacterDesc.setText(getString(R.string.just_perfect));
                        binding.tvCharacterDesc.setTextColor(getResources().getColor(R.color.greendark));
                    }

                } else {
                    binding.tvCharacterDesc.setText(getString(R.string.min_50_char));
                    binding.tvCharacterDesc.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void deliveryMethod() {

        powerMenu = new PowerMenu.Builder(this)
                .addItem(days)
                .addItem(hours)
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setTextColor(ContextCompat.getColor(this, R.color.black))
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(tf)
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setOnMenuItemClickListener((position, item) -> {
                    powerMenu.setSelectedPosition(position);
                    powerMenu.dismiss();
                    binding.tvDelMethod.setText(item.getTitle());
                })
                .build();

        powerMenu.showAsDropDown(binding.tvDelMethod);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    if (data != null) {
                        GigCategoryModel.Data language = (GigCategoryModel.Data) data.getSerializableExtra("category");
                        if (language != null) {
                            binding.txtCategory.setText(language.nameApp);
                            selectedCategory = language;
                            binding.etPrice.setText("");
                            binding.txtPriceLbl.setText("");
                            getChargesByCat(selectedCategory.id);
                        }
                    }
                    break;
            }
        }
    }

    private void callGigCategoryActivity(ArrayList<GigCategoryModel.Data> catList, boolean isCallAct) {
        if (isCallAct) {
            Intent intent = new Intent(this, GigCategoryActivity.class);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void showConfDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_conf_offer);
        dialog.setCancelable(true);

        TextView txtOffer = dialog.findViewById(R.id.txt_offer);
        TextView tvConfirm = dialog.findViewById(R.id.tv_confirm);
        TextView tvNo = dialog.findViewById(R.id.tv_no);

        txtOffer.setText(getString(R.string.do_you_want_to_send_an_offer_anant05, cUsername));

        tvNo.setOnClickListener(v -> dialog.dismiss());

        tvConfirm.setOnClickListener(v -> {
            dialog.dismiss();

            RequestBody clientProfileIdBody = RequestBody.create("" + cUserid, MultipartBody.FORM);
            RequestBody gigTitleBody = RequestBody.create(binding.etTitle.getText().toString(), MultipartBody.FORM);
            RequestBody gigCatIdBody = RequestBody.create(selectedCategory != null ? "" + selectedCategory.id : "0", MultipartBody.FORM);
            RequestBody gigDescBody = RequestBody.create(binding.etDescription.getText().toString(), MultipartBody.FORM);
            RequestBody gigMainPrice = RequestBody.create(binding.etPrice.getText().toString(), MultipartBody.FORM);
            RequestBody gigDeadType = RequestBody.create(binding.tvDelMethod.getText().toString().equalsIgnoreCase("Days") ? "2" : "1", MultipartBody.FORM);
            RequestBody gigDeadValue = RequestBody.create(binding.etNumber.getText().toString(), MultipartBody.FORM);
//            RequestBody gigID = RequestBody.create("", MultipartBody.FORM);

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("offerTitle", gigTitleBody);
            map.put("description", gigDescBody);
            map.put("parentServiceCategoryID", gigCatIdBody);
            map.put("clientID", clientProfileIdBody);
            map.put("deadlineType", gigDeadType);
            map.put("deadlineValue", gigDeadValue);
            map.put("price", gigMainPrice);
//        map.put("gigID", gigID);

            createCustomGigsActivityVM.createOfferGig(map);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private boolean checkValidation() {

        if (selectedCategory == null) {
            toastMessage(getString(R.string.please_select_category));
            return false;
        }

        if (TextUtils.isEmpty(binding.etTitle.getText().toString())) {
            toastMessage(getString(R.string.please_enter_gig_title));
            return false;
        }

        if (binding.etTitle.getText().length() < 15) {
            toastMessage(getString(R.string.title_must_15_char));
            return false;
        }

        if (TextUtils.isEmpty(binding.etDescription.getText().toString())) {
            toastMessage(getString(R.string.please_enter_gig_description));
            return false;
        }

        if (binding.etDescription.getText().length() < 50) {
            toastMessage(getString(R.string.desc_must_more_50_char));
            return false;
        }

        if (TextUtils.isEmpty(binding.etNumber.getText().toString().trim())) {
            toastMessage(getString(R.string.please_enter_delivery_time));
            return false;
        }

        if (Double.parseDouble(binding.etNumber.getText().toString()) < 1) {
            toastMessage(getString(R.string.time_should_not_be_zero));
            return false;
        }

        return true;
    }

    private void calculatePercentage(double amount) {
        try {
            double percentage;
            if (selectedCatCharges == null) {
                percentage = getChargesByCat(selectedCategory.id);
            } else {
                percentage = selectedCatCharges.percentCharge;
            }

            double percentAmount = Double.parseDouble(Utils.get2DecimalPlaces((amount / 100.0f) * percentage));
            double finalAmountReceived = amount - percentAmount;

            binding.txtPriceLbl.setVisibility(View.VISIBLE);
            if (getCurrency().equals("SAR")) {
                binding.txtPriceLbl.setText(getString(R.string.you_will_get_total_amount) + " : " + Utils.getDecimalValue(String.valueOf(finalAmountReceived)) + " "+getString(R.string.sar)+" (" + Utils.getDecimalValue(String.valueOf(percentage)) + "%)");
            } else {
                binding.txtPriceLbl.setText(getString(R.string.you_will_get_total_amount) + " : "+getString(R.string.dollar) + Utils.getDecimalValue(String.valueOf(finalAmountReceived)) + " (" + Utils.getDecimalValue(String.valueOf(percentage)) + "%)");
            }
        } catch (Exception e) {
            binding.txtPriceLbl.setText("");
            e.printStackTrace();
        }
    }

    private GigCatCharges.Data selectedCatCharges;

    private double getChargesByCat(int catId) {
        if (chargeList != null && chargeList.size() > 0) {
            for (GigCatCharges.Data charge : chargeList) {
                if (catId == charge.serviceCategoryId) {
                    selectedCatCharges = charge;
                    return charge.percentCharge;
                }
            }
        }
        return 5;//by default its 5
    }
}
