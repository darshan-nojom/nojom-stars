package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.SelectCityAdapter;
import com.nojom.adapter.SelectCountryAdapter;
import com.nojom.adapter.SelectStateAdapter;
import com.nojom.apis.GetCountriesAPI;
import com.nojom.databinding.ActivityUpdateLocationBinding;
import com.nojom.model.CityResponse;
import com.nojom.model.CountryResponse;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.List;
import java.util.Objects;

public class UpdateLocationActivity extends BaseActivity implements View.OnClickListener {

    private UpdateLocationActivityVM updateLocationActivityVM;
    private ActivityUpdateLocationBinding binding;
    private ProfileResponse profileData;
    private int isFromProfessionalInfo = 0;
    private SelectCountryAdapter selectCountryAdapter;
    private SelectStateAdapter selectStateAdapter;
    private SelectCityAdapter selectCityAdapter;
    private boolean isUpdateLocationCompulsory = false;
    private GetCountriesAPI getCountriesAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_location);
        profileData = Preferences.getProfileData(this);
        updateLocationActivityVM = ViewModelProviders.of(this).get(UpdateLocationActivityVM.class);
        updateLocationActivityVM.init(this);

        getCountriesAPI = new GetCountriesAPI();
        getCountriesAPI.init(this);
        getCountriesAPI.getCountries();

        initData();
    }

    private void initData() {

        if (getIntent() != null) {
            isFromProfessionalInfo = getIntent().getIntExtra("screen", 0);
            isUpdateLocationCompulsory = getIntent().getBooleanExtra("flag", false);
        }

        binding.toolbar.imgBack.setOnClickListener(v -> {
            if (isUpdateLocationCompulsory) {
//                finish();
//                System.exit(0);
            } else {
                onBackPressed();
            }

        });

        binding.btnUpdateLoc.setOnClickListener(v -> {
            if (binding.txtCountry.getTag() == null) {
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
            updateLocationActivityVM.updateLocation((Integer) binding.txtCountry.getTag(), (Integer) binding.txtCity.getTag(), -1);
        });

        binding.txtCountry.setOnClickListener(v -> {

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

        binding.txtCity.setOnClickListener(v -> {
            if (updateLocationActivityVM.getCityLiveData() != null && updateLocationActivityVM.getCityLiveData().getValue() != null
                    && updateLocationActivityVM.getCityLiveData().getValue().size() > 0) {
                showCitySelectDialog(updateLocationActivityVM.getCityLiveData().getValue());
            } else if (profileData.stateID != null) {
                updateLocationActivityVM.getCityFromCountryState(2849);
            }
        });

        if (profileData != null) {
            binding.txtCountry.setText(profileData.getCountryName(language));
            binding.txtCity.setText(profileData.getCityName(language));
//            binding.txtState.setText(profileData.getStateName(language));

            binding.txtCountry.setTag(profileData.countryID);
            binding.txtCity.setTag(profileData.cityID);
//            binding.txtState.setTag(profileData.stateID);

//            if (profileData.countryID != null) {
//                updateLocationActivityVM.getStateFromCountry(profileData.countryID);
//            }
//            if (profileData.cityID != null) {
//
//            }

            if (profileData.countryID == 194) {
                binding.linCity.setVisibility(View.VISIBLE);
                updateLocationActivityVM.getCityFromCountryState(2849);
            } else {
                binding.linCity.setVisibility(View.GONE);
            }

        }

        updateLocationActivityVM.getResponseMutableLiveData().observe(this, generalModelResponse -> {
            if (profileData != null) {
                profileData.countryName = getCountry();
//                profileData.stateName = getState();
                profileData.cityName = getCity();

                profileData.countryID = (Integer) binding.txtCountry.getTag();
//                profileData.stateID = (Integer) binding.txtState.getTag();
                profileData.cityID = (Integer) binding.txtCity.getTag();

                Preferences.setProfileData(UpdateLocationActivity.this, profileData);
            }
            toastMessage(getString(R.string.location_updated_success));
            if (isFromProfessionalInfo == ADDRESS) {
                setResult(RESULT_OK);
            }
            finish();
        });

        /*updateLocationActivityVM.getIsShowStateProgress().observe(this, isShow -> {
            isClickableView = isShow;
            binding.progressBarState.setVisibility(isShow ? View.VISIBLE : View.GONE);
        });*/

        updateLocationActivityVM.getIsShowCityProgress().observe(this, isShow -> {
            isClickableView = isShow;
            binding.progressBarCity.setVisibility(isShow ? View.VISIBLE : View.GONE);
        });

        getCountriesAPI.getIsShowCountryProgress().observe(this, isShow -> {
            isClickableView = isShow;
            binding.progressBarCountry.setVisibility(isShow ? View.VISIBLE : View.GONE);
        });

        updateLocationActivityVM.getIsShowSaveProgress().observe(this, isShow -> {
            isClickableView = isShow;
            binding.progressBarSave.setVisibility(isShow ? View.VISIBLE : View.GONE);
            binding.btnUpdateLoc.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
        });
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
                    binding.linCity.setVisibility(View.VISIBLE);
                    updateLocationActivityVM.getCityFromCountryState(2849);
                } else {
                    binding.linCity.setVisibility(View.GONE);
                }


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
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {

    }

    public String getCountry() {
        return Objects.requireNonNull(binding.txtCountry.getText()).toString().trim();
    }

    /*public String getState() {
        return Objects.requireNonNull(binding.txtState.getText()).toString().trim();
    }*/

    private String getCity() {
        return Objects.requireNonNull(binding.txtCity.getText()).toString().trim();
    }
}
