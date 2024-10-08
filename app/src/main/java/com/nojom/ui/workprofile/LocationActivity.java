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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.CityAdapter;
import com.nojom.adapter.CountryAdapter;
import com.nojom.adapter.SelectCityAdapter;
import com.nojom.adapter.SelectCountryAdapter;
import com.nojom.adapter.SelectStateAdapter;
import com.nojom.apis.GetCountriesAPI;
import com.nojom.ccp.CCPCountry;
import com.nojom.databinding.ActivityLocationBinding;
import com.nojom.model.CityResponse;
import com.nojom.model.CountryResponse;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.GenderActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.List;
import java.util.Objects;

public class LocationActivity extends BaseActivity implements View.OnClickListener, CountryAdapter.CountryListener, CityAdapter.CityListener {

    private UpdateLocationActivityVM updateLocationActivityVM;
    private ActivityLocationBinding binding;
    private ProfileResponse profileData;
    private int isFromProfessionalInfo = 0;
    private CountryAdapter selectCountryAdapter;
    private SelectStateAdapter selectStateAdapter;
    private CityAdapter selectCityAdapter;
    private boolean isUpdateLocationCompulsory = false;
    private GetCountriesAPI getCountriesAPI;
    private NameActivityVM nameActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location);
        profileData = Preferences.getProfileData(this);
        updateLocationActivityVM = ViewModelProviders.of(this).get(UpdateLocationActivityVM.class);
        updateLocationActivityVM.init(this);

        nameActivityVM = ViewModelProviders.of(this).get(NameActivityVM.class);
//        nameActivityVM.setNameActivityListener(this);

        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tv2, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtDesc, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatus, Constants.FONT_AR_BOLD);
            setArFont(binding.etCountry, Constants.FONT_AR_REGULAR);
            setArFont(binding.etCity, Constants.FONT_AR_REGULAR);
            setArFont(binding.btnLogin, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtSkip, Constants.FONT_AR_MEDIUM);
        }

        getCountriesAPI = new GetCountriesAPI();
        getCountriesAPI.init(this);
        getCountriesAPI.getCountries();

        initData();
    }

    private CityResponse.CityData cityData;

    private void initData() {

        if (getIntent() != null) {
            isFromProfessionalInfo = getIntent().getIntExtra("screen", 0);
            isUpdateLocationCompulsory = getIntent().getBooleanExtra("flag", false);
        }

        binding.imgBack.setOnClickListener(v -> {
            if (isUpdateLocationCompulsory) {
//                finish();
//                System.exit(0);
            } else {
                onBackPressed();
            }

        });

        binding.relLogin.setOnClickListener(v -> {
            if (binding.etCountry.getTag() == null) {
                toastMessage(getString(R.string.select_country));
                return;
            }
            /*if (binding.txtState.getTag() == null) {
                toastMessage(getString(R.string.select_state));
                return;
            }
            if (binding.txtCity.getTag() == null) {
                toastMessage(getString(R.string.select_city));
                return;
            }*/
            updateLocationActivityVM.updateLocation((Integer) binding.etCountry.getTag(), (Integer) binding.etCity.getTag(), RS_3_GENDER);
            nameActivityVM.updateProfile(this, "", RS_3_GENDER,
                    (Integer) binding.txtStatus.getTag(), 0);
        });

        binding.etCountry.setOnClickListener(v -> {

            if (getCountriesAPI.getCountryLiveData() != null) {
                if (getCountriesAPI.getCountryLiveData() != null && getCountriesAPI.getCountryLiveData().getValue() != null
                        && getCountriesAPI.getCountryLiveData().getValue().size() > 0) {
                    showCountrySelectDialog(getCountriesAPI.getCountryLiveData().getValue());
                } else {
                    getCountriesAPI.getCountries();
                }

            }
        });
        /*binding.txtState.setOnClickListener(v -> {
            if (updateLocationActivityVM.getStateLiveData() != null && updateLocationActivityVM.getStateLiveData().getValue() != null
                    && updateLocationActivityVM.getStateLiveData().getValue().size() > 0) {
                showStateSelectDialog(updateLocationActivityVM.getStateLiveData().getValue());
            } *//*else if (profileData.countryID != null) {
                updateLocationActivityVM.getStateFromCountry(profileData.countryID);
            }*//*
        });*/

        binding.etCity.setOnClickListener(v -> {
            if (updateLocationActivityVM.getCityLiveData() != null && updateLocationActivityVM.getCityLiveData().getValue() != null
                    && updateLocationActivityVM.getCityLiveData().getValue().size() > 0) {
                showCitySelectDialog(updateLocationActivityVM.getCityLiveData().getValue());
            } else if (profileData.stateID != null) {
                updateLocationActivityVM.getCityFromCountryState(2849);
            }
        });

        if (profileData != null) {
            binding.etCountry.setText(profileData.getCountryName(language));
            binding.etCity.setText(profileData.getCityName(language));
//            binding.txtState.setText(profileData.getStateName(language));

            binding.etCountry.setTag(profileData.countryID);
            binding.etCity.setTag(profileData.cityID);
//            binding.txtState.setTag(profileData.stateID);

//            if (profileData.countryID != null) {
//                updateLocationActivityVM.getStateFromCountry(profileData.countryID);
//            }
//            if (profileData.cityID != null) {
//
//            }

            if (profileData.countryID == 194) {
                binding.relCity.setVisibility(View.VISIBLE);
                updateLocationActivityVM.getCityFromCountryState(2849);
            } else {
                binding.relCity.setVisibility(View.GONE);
            }

        }

        updateLocationActivityVM.getResponseMutableLiveData().observe(this, generalModelResponse -> {
            if (profileData != null) {
                profileData.countryName = getCountry();
//                profileData.stateName = getState();
                profileData.cityName = getCity();

                profileData.countryID = (Integer) binding.etCountry.getTag();
//                profileData.stateID = (Integer) binding.txtState.getTag();
                if (binding.etCity.getTag() != null) {
                    profileData.cityID = Integer.parseInt(binding.etCity.getTag().toString());
                }

                Preferences.setProfileData(LocationActivity.this, profileData);
            }
            toastMessage(getString(R.string.location_updated_success));
            if (isFromProfessionalInfo == ADDRESS) {
                setResult(RESULT_OK);
                finish();
            } else {
                redirectActivity(GenderActivity.class);
            }

        });
        updateLocationActivityVM.getCityLiveData().observe(this, cityData -> {
            if (cityData != null && cityData.size() > 0) {
                for (CityResponse.CityData cityData1 : cityData) {
                    if (cityData1.cityName.equals("Riyadh")) {
                        binding.etCity.setText(cityData1.getCityName(language));
                        binding.etCity.setTag("" + cityData1.id);
                        this.cityData = cityData1;
                        break;
                    }
                }
            } else {
                binding.etCity.setText("");
                binding.etCity.setTag("");
            }
        });

        /*updateLocationActivityVM.getIsShowStateProgress().observe(this, isShow -> {
            isClickableView = isShow;
            binding.progressBarState.setVisibility(isShow ? View.VISIBLE : View.GONE);
        });*/

        updateLocationActivityVM.getIsShowCityProgress().observe(this, isShow -> {
            isClickableView = isShow;
            // binding.progressBarCity.setVisibility(isShow ? View.VISIBLE : View.GONE);
        });

        getCountriesAPI.getIsShowCountryProgress().observe(this, isShow -> {
            isClickableView = isShow;
            //binding.progressBarCountry.setVisibility(isShow ? View.VISIBLE : View.GONE);
        });

        updateLocationActivityVM.getIsShowSaveProgress().observe(this, isShow -> {
            isClickableView = isShow;
            binding.progressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
            binding.btnLogin.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
        });
        binding.txtSkip.setOnClickListener(view -> {
            updateLocationActivityVM.updateLocation(null, null, RS_3_GENDER);
//            redirectActivity(GenderActivity.class);
        });

        binding.txtDesc.setText(getString(R.string.visible_to) + " " + getString(R.string.brand_only).toLowerCase());
        binding.txtStatus.setTag(2);
        setPublicStatusValue(2, binding.txtStatus);
        binding.txtStatus.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatus));
    }

    Dialog dialogCountry;
    CountryResponse.CountryData ccpCountryEgypt = null, ccpCountrySaudi = null;

    void showCountrySelectDialog(List<CountryResponse.CountryData> arrayList) {
        dialogCountry = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogCountry.setTitle(null);
        dialogCountry.setContentView(R.layout.dialog_country);
        dialogCountry.setCancelable(true);

        //TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        //TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialogCountry.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialogCountry.findViewById(R.id.rv_items);
        RelativeLayout relEgypt = dialogCountry.findViewById(R.id.lin_view_egypt);
        RelativeLayout relSaudi = dialogCountry.findViewById(R.id.lin_view_saudi);
        ImageView chkEgypt = dialogCountry.findViewById(R.id.img_chk);
        ImageView chkSaudi = dialogCountry.findViewById(R.id.img_chk_saudi);
        TextView txtPopular = dialogCountry.findViewById(R.id.txt_popular);

        etSearch.setHint(String.format(getString(R.string.search_for), getString(R.string.country).toLowerCase()));

        rvTypes.setLayoutManager(new LinearLayoutManager(this));

        try {
            if (arrayList != null && arrayList.size() > 0) {
                for (CountryResponse.CountryData data : arrayList) {
                    data.isSelected = data.getCountryName(language).equalsIgnoreCase(binding.etCountry.getText().toString());

                    if (data.countryCode.toLowerCase().equals("eg")) {
                        ccpCountryEgypt = data;
                    } else if (data.countryCode.toLowerCase().equals("sa")) {
                        ccpCountrySaudi = data;
                    }
                }

                selectCountryAdapter = new CountryAdapter(this, arrayList);
                selectCountryAdapter.setCountryListener(this);
                rvTypes.setAdapter(selectCountryAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //tvCancel.setOnClickListener(v -> dialog.dismiss());

        if (ccpCountryEgypt != null && binding.etCountry.getText().toString().equals(ccpCountryEgypt.getCountryName(language))) {
            chkEgypt.setVisibility(View.VISIBLE);
            chkSaudi.setVisibility(View.GONE);
        } else if (ccpCountrySaudi != null && binding.etCountry.getText().toString().equals(ccpCountrySaudi.getCountryName(language))) {
            chkEgypt.setVisibility(View.GONE);
            chkSaudi.setVisibility(View.VISIBLE);
        }

        CountryResponse.CountryData finalCcpCountryEgypt = ccpCountryEgypt;
        relEgypt.setOnClickListener(view -> {
            chkEgypt.setVisibility(View.VISIBLE);
            chkSaudi.setVisibility(View.GONE);
            onClickCountry(0, finalCcpCountryEgypt);
            // dialogCountry.dismiss();
        });

        CountryResponse.CountryData finalCcpCountrySaudi = ccpCountrySaudi;
        relSaudi.setOnClickListener(view -> {
            chkEgypt.setVisibility(View.GONE);
            chkSaudi.setVisibility(View.VISIBLE);
            onClickCountry(0, finalCcpCountrySaudi);
            //dialogCountry.dismiss();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    relSaudi.setVisibility(View.VISIBLE);
                    relEgypt.setVisibility(View.VISIBLE);
                    txtPopular.setVisibility(View.VISIBLE);
                } else {
                    relSaudi.setVisibility(View.GONE);
                    relEgypt.setVisibility(View.GONE);
                    txtPopular.setVisibility(View.GONE);
                }

                if (selectCountryAdapter != null)
                    selectCountryAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogCountry.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogCountry.show();
        dialogCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCountry.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(this, etSearch)));
        etSearch.requestFocus();
    }

//    void showStateSelectDialog(List<StateResponse.StateData> arrayList) {
//        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
//        dialog.setTitle(null);
//        dialog.setContentView(R.layout.dialog_item_select_black);
//        dialog.setCancelable(true);
//
//        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
//        TextView tvApply = dialog.findViewById(R.id.tv_apply);
//        final EditText etSearch = dialog.findViewById(R.id.et_search);
//        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);
//
//        etSearch.setHint(String.format(getString(R.string.search_for), getString(R.string.country).toLowerCase()));
//
//        rvTypes.setLayoutManager(new LinearLayoutManager(this));
//        try {
//            if (arrayList != null && arrayList.size() > 0) {
//                for (StateResponse.StateData data : arrayList) {
//                    data.isSelected = data.getStateName(language).equalsIgnoreCase(binding.txtState.getText().toString());
//                }
//                selectStateAdapter = new SelectStateAdapter(this, arrayList);
//                rvTypes.setAdapter(selectStateAdapter);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        tvCancel.setOnClickListener(v -> dialog.dismiss());
//
//        tvApply.setOnClickListener(v -> {
//            if (selectStateAdapter != null && selectStateAdapter.getSelectedItem() != null) {
//                binding.txtState.setText(selectStateAdapter.getSelectedItem().getStateName(language));
//                binding.txtState.setTag(selectStateAdapter.getSelectedItem().id);
//                dialog.dismiss();
//                binding.txtCity.setText("");
//                binding.txtCity.setHint(getString(R.string.select_city));
//                binding.txtCity.setTag(null);
//
//                if (updateLocationActivityVM.getCityLiveData() != null && updateLocationActivityVM.getCityLiveData().getValue() != null) {
//                    updateLocationActivityVM.getCityLiveData().getValue().clear();
//                }
//
//                updateLocationActivityVM.getCityFromCountryState(2849);
//            } else {
//                toastMessage(getString(R.string.please_select_one_item));
//            }
//            dialog.dismiss();
//        });
//
//        etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (selectStateAdapter != null)
//                    selectStateAdapter.getFilter().filter(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.gravity = Gravity.BOTTOM;
//        dialog.show();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setAttributes(lp);
//        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(this, etSearch)));
//        etSearch.requestFocus();
//    }

    Dialog dialogCity;

    void showCitySelectDialog(List<CityResponse.CityData> arrayList) {
        dialogCity = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogCity.setTitle(null);
        dialogCity.setContentView(R.layout.dialog_country);
        dialogCity.setCancelable(true);

        TextView title = dialogCity.findViewById(R.id.title);
        title.setText(getString(R.string.select_city));
        // TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialogCity.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialogCity.findViewById(R.id.rv_items);
        TextView txtPop = dialogCity.findViewById(R.id.txt_popular);
        RelativeLayout relEgy = dialogCity.findViewById(R.id.lin_view_egypt);
        RelativeLayout relSaud = dialogCity.findViewById(R.id.lin_view_saudi);
        View v1 = dialogCity.findViewById(R.id.vi1);
        TextView tvCityName = dialogCity.findViewById(R.id.tv_title);
        TextView tvCityCode = dialogCity.findViewById(R.id.tv_code);
        ImageView imgChk = dialogCity.findViewById(R.id.img_chk);
//        txtPop.setVisibility(View.GONE);
        relSaud.setVisibility(View.GONE);
        tvCityCode.setVisibility(View.GONE);
//        v1.setVisibility(View.GONE);

        if (cityData != null) {
            tvCityName.setText(cityData.getCityName(language));
            tvCityName.setTag("" + cityData.id);

            if (binding.etCity.getText().toString().equalsIgnoreCase(getString(R.string.riyadh))) {
                imgChk.setVisibility(View.VISIBLE);
            }
        }

        relEgy.setOnClickListener(view -> {
            if (cityData != null) {
                binding.etCity.setText(cityData.getCityName(language));
                binding.etCity.setTag(cityData.id);
                dialogCity.dismiss();
            }
        });

        //etSearch.setHint(String.format(getString(R.string.search_for), getString(R.string.country).toLowerCase()));

        rvTypes.setLayoutManager(new LinearLayoutManager(this));
        try {
            if (arrayList != null && arrayList.size() > 0) {
                for (CityResponse.CityData data : arrayList) {
                    data.isSelected = data.getCityName(language).equalsIgnoreCase(binding.etCity.getText().toString());
                }
                selectCityAdapter = new CityAdapter(this, arrayList);
                selectCityAdapter.setCityListener(this);
                rvTypes.setAdapter(selectCityAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    relEgy.setVisibility(View.VISIBLE);
                    txtPop.setVisibility(View.VISIBLE);
                    v1.setVisibility(View.VISIBLE);
                } else {
                    relEgy.setVisibility(View.GONE);
                    txtPop.setVisibility(View.GONE);
                    v1.setVisibility(View.GONE);
                }

                if (selectCityAdapter != null)
                    selectCityAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogCity.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogCity.show();
        dialogCity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCity.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(this, etSearch)));
        etSearch.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isUpdateLocationCompulsory) {
//            System.exit(0);
        }
//        updateLocationActivityVM.updateLocation(null, null, RS_3_GENDER);
//        redirectActivity(GenderActivity.class);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {

    }

    public String getCountry() {
        return Objects.requireNonNull(binding.etCountry.getText()).toString().trim();
    }

    /*public String getState() {
        return Objects.requireNonNull(binding.txtState.getText()).toString().trim();
    }*/

    private String getCity() {
        return Objects.requireNonNull(binding.etCity.getText()).toString().trim();
    }

    @Override
    public void onClickCountry(int pos, CountryResponse.CountryData data) {
        if (selectCountryAdapter != null) {
            binding.etCountry.setText(data.getCountryName(language));
            binding.etCountry.setTag(data.id);
//                binding.txtState.setText("");
//                binding.txtState.setHint(getString(R.string.select_state));//clear state & city, when select other country
            binding.etCity.setText("");
            binding.etCity.setHint(getString(R.string.select_city));
//                binding.txtState.setTag(null);//clear tag [i.e ID]
            binding.etCity.setTag(null);

//                if (updateLocationActivityVM.getStateLiveData() != null && updateLocationActivityVM.getStateLiveData().getValue() != null) {
//                    updateLocationActivityVM.getStateLiveData().getValue().clear();
//                }

            if (updateLocationActivityVM.getCityLiveData() != null && updateLocationActivityVM.getCityLiveData().getValue() != null) {
                updateLocationActivityVM.getCityLiveData().getValue().clear();
            }
            if (data.id == 194) {
                binding.relCity.setVisibility(View.VISIBLE);
                updateLocationActivityVM.getCityFromCountryState(2849);
            } else {
                binding.relCity.setVisibility(View.GONE);
            }

            DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(LocationActivity.this, R.color.black));
        } else {
            toastMessage(getString(R.string.select_country));
        }
        dialogCountry.dismiss();
    }

    @Override
    public void onClickCity(int pos, CityResponse.CityData data) {
        if (selectCityAdapter != null && selectCityAdapter.getSelectedItem() != null) {
            binding.etCity.setText(selectCityAdapter.getSelectedItem().getCityName(language));
            binding.etCity.setTag(selectCityAdapter.getSelectedItem().id);
        } else {
            toastMessage(getString(R.string.select_city));
        }
        dialogCity.dismiss();
    }

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
            switch (status) {
                case 1:
                    binding.txtDesc.setText(getString(R.string.visible_to) + " " + getString(R.string.everyone));
                    break;
                case 2:
                    binding.txtDesc.setText(getString(R.string.visible_to) + " " + getString(R.string.brand_only).toLowerCase());
                    break;
                case 3:
                    binding.txtDesc.setText(getString(R.string.visible_to) + " " + getString(R.string.only_me).toLowerCase());
                    break;
            }

//            binding.relSave.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.black_button_bg_10, null));
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

    private void setPublicStatusValue(int publicStatus, TextView txtStatus) {
        txtStatus.setTag(publicStatus);
        switch (publicStatus) {
            case 2://brands
                txtStatus.setText(getString(R.string.brand_only));
                txtStatus.setTextColor(getResources().getColor(R.color.c_34A853));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_34A853));
                break;
            case 3://only me
                txtStatus.setText(getString(R.string.only_me));
                txtStatus.setTextColor(getResources().getColor(R.color.red_dark));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.red_dark));
                break;
            default:
                txtStatus.setText(getString(R.string.public_));
                txtStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.colorPrimary));
                break;
        }
    }

    public CityResponse.CityData getCityByName(List<CityResponse.CityData> cities, String cityName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return cities.stream()
                    .filter(city -> city.getCityName(language).equalsIgnoreCase(cityName))
                    .findFirst()
                    .orElse(null); // Return null if no city is found
        }
        return null;
    }
}
