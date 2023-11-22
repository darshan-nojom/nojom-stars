package com.nojom.ui.partner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.textview.TextViewSFTextPro;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.PartnerAnswerAdapter;
import com.nojom.adapter.SelectCountryAdapter;
import com.nojom.adapter.SelectPartnerAgeAdapter;
import com.nojom.apis.GetCountriesAPI;
import com.nojom.apis.GetPartnerQuestion;
import com.nojom.databinding.ActivityPartnerWithUsBinding;
import com.nojom.model.CountryResponse;
import com.nojom.model.PartnerWithUsResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PartnerActivity extends BaseActivity {
    private ActivityPartnerWithUsBinding binding;
    private PartnerActivityVM notificationActivityVM;
    private SelectCountryAdapter selectCountryAdapter;
    private SelectPartnerAgeAdapter selectPartnerAgeAdapter;
    private ArrayList<PartnerWithUsResponse.Answers> answersOption;
    private ArrayList<PartnerWithUsResponse.Data> responseList;
    private ArrayList<PartnerWithUsResponse.Data> responseListPage2;
    private View investorView = null;
    private boolean isInvestorViewAdded = false, isPartnerAbout, isPartnerApp, isLoadFromOutside;
    private GetCountriesAPI getCountriesAPI;
    private GetPartnerQuestion getPartnerQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_partner_with_us);

        responseList = (ArrayList<PartnerWithUsResponse.Data>) getIntent().getSerializableExtra("data");

        getCountriesAPI = new GetCountriesAPI();
        getCountriesAPI.init(this);
        getPartnerQuestion = new GetPartnerQuestion();
        getPartnerQuestion.init(this);

        notificationActivityVM = ViewModelProviders.of(this).get(PartnerActivityVM.class);
        notificationActivityVM.init(this);
        getCountriesAPI.getCountries();
        initData();

        if (responseList != null && responseList.size() > 0) {
            isLoadFromOutside = true;
            binding.linQuestions.removeAllViews();
            makeView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLoadFromOutside) {
            getPartnerQuestion.getPartnerQuestions();
        }

        isPartnerApp = Preferences.readBoolean(this, PREF_PARTNER_APP, false);
        isPartnerAbout = Preferences.readBoolean(this, PREF_PARTNER_ABOUT, false);

        setLayoutVisibility();

        if ((Math.round(getProfileData().percentage.totalPercentage) == 100)) {
            binding.txtMyprofileStatus.setBackground(getResources().getDrawable(R.drawable.green_rounded_corner_25));
            binding.txtMyprofileStatus.setText(getString(R.string.completed));
        }
        isLoadFromOutside = false;
    }

    private void setLayoutVisibility() {
        if (isPartnerAbout || isPartnerApp) {
            binding.linSurveyForm.setVisibility(View.GONE);
            binding.tvFormMsg.setVisibility(View.GONE);
            binding.linSubmittedSurvey.setVisibility(View.VISIBLE);

            if (isPartnerApp) {
                binding.txtApplicationStatus.setBackground(getResources().getDrawable(R.drawable.green_rounded_corner_25));
                binding.txtApplicationStatus.setText(getString(R.string.completed));
            }

            if (isPartnerAbout) {
                binding.txtAboutmeStatus.setBackground(getResources().getDrawable(R.drawable.green_rounded_corner_25));
                binding.txtAboutmeStatus.setText(getString(R.string.completed));
            }
        } else {
            binding.linSurveyForm.setVisibility(View.VISIBLE);
            binding.tvFormMsg.setVisibility(View.VISIBLE);
            binding.linSubmittedSurvey.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isPartnerAbout || isPartnerApp) {
            if (binding.linSurveyForm.isShown()) {
                binding.linSurveyForm.setVisibility(View.GONE);
                binding.tvFormMsg.setVisibility(View.GONE);
                binding.linSubmittedSurvey.setVisibility(View.VISIBLE);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(view -> onBackPressed());
        responseList = new ArrayList<>();
        responseListPage2 = new ArrayList<>();
        addObserver();

        binding.tvCountry.setOnClickListener(v -> {

            if (getCountriesAPI.getCountryLiveData() != null) {
                if (getCountriesAPI.getCountryLiveData() != null && getCountriesAPI.getCountryLiveData().getValue() != null
                        && getCountriesAPI.getCountryLiveData().getValue().size() > 0) {
                    showCountrySelectDialog(getCountriesAPI.getCountryLiveData().getValue());
                } else {
                    getCountriesAPI.getCountries();
                }

            }
        });

        binding.tvAges.setOnClickListener(v -> {
            if (answersOption != null && answersOption.size() > 0) {
                showAgeDialog(answersOption);
            }
        });

        binding.tvSave.setOnClickListener(v -> {
            if (checkValidation()) {
                Log.e("JSON ARRAY ", "---------------  " + jsonArray);
                notificationActivityVM.addAppSurvey(jsonArray);
            }
        });


        binding.relApplication.setOnClickListener(v -> {
            binding.linSurveyForm.setVisibility(View.VISIBLE);
            binding.tvFormMsg.setVisibility(View.VISIBLE);
            binding.linSubmittedSurvey.setVisibility(View.GONE);

        });

        binding.relAboutMe.setOnClickListener(v -> {
            Intent intent = new Intent(this, PartnerAboutActivity.class);
            intent.putExtra("isEdit", isPartnerAbout);
            intent.putExtra("list", responseListPage2);
            startActivity(intent);
        });

        binding.relMyProfile.setOnClickListener(v -> {
//            if ((Math.round(getProfileData().percentage.totalPercentage) < 100)) {
            redirectActivity(PartnerProfileActivity.class);
//            }
        });

        binding.imgLinkedin.setOnClickListener(v -> {
            String url = "https://linkedin.com/in/ahmed-alsenan";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }

    private JSONArray jsonArray;

    private boolean checkValidation() {
        jsonArray = new JSONArray();
        for (int i = 0; i < responseList.size(); i++) {
            JSONObject object = new JSONObject();
            if (responseList.get(i).page == 1) {
                if (responseList.get(i).type == 1) {

                    if (responseList.get(i).id == 2) {//invest view
                        if (isInvestorViewAdded && responseList.get(i).partnershipAnsId == 0) {
                            toastMessage(getString(R.string.please_select)+" " + responseList.get(i).question);
                            return false;
                        } else if (isInvestorViewAdded) {
                            addValueToObject(object, responseList.get(i).id, responseList.get(i).partnershipAnsId, responseList.get(i).selectedAnswer, 0);
                        }
                    } else {
                        if (responseList.get(i).partnershipAnsId == 0) {
                            toastMessage(getString(R.string.please_select)+" " + responseList.get(i).question);
                            return false;
                        } else {
                            addValueToObject(object, responseList.get(i).id, responseList.get(i).partnershipAnsId, responseList.get(i).selectedAnswer, 0);
                        }
                    }

                } else if (responseList.get(i).type == 0) {
                    if (TextUtils.isEmpty(binding.etWebsite.getText().toString().trim())) {
                        toastMessage(getString(R.string.please_enter)+" " + responseList.get(i).question);
                        return false;
                    }
                    if (!isValidUrl(binding.etWebsite.getText().toString())) {
                        toastMessage(getString(R.string.please_enter_valid_website));
                        return false;
                    }
                    addValueToObject(object, responseList.get(i).id, 0, binding.etWebsite.getText().toString(), 0);
                } else if (responseList.get(i).type == 2) {
                    if (responseList.get(i).id == 6) {//how old are you
                        if (binding.tvAges.getTag() == null) {
                            toastMessage(getString(R.string.please_select)+" " + responseList.get(i).question);
                            return false;
                        }
                        addValueToObject(object, responseList.get(i).id, (Integer) binding.tvAges.getTag(), binding.tvAges.getText().toString(), 0);
                    } else {//where do you live
                        if (binding.tvCountry.getTag() == null) {
                            toastMessage(getString(R.string.please_select)+" " + responseList.get(i).question);
                            return false;
                        }
                        addValueToObject(object, responseList.get(i).id, 0, binding.tvCountry.getText().toString(), (Integer) binding.tvCountry.getTag());
                    }

                }
            }

            if (object.length() > 0) {
                jsonArray.put(object);
            }
        }

//        return jsonArray.length() == responseList.size();
        return true;
    }

    private void addValueToObject(JSONObject object, int partQueId, int answerId, String answer, int countryId) {
        try {
            object.put("partnershipQuestionID", partQueId);
            object.put("partnershipAnswerID", answerId);
            object.put("answer", answer);
            object.put("countryID", countryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addObserver() {

        getPartnerQuestion.getListMutableLiveData().observe(this, data -> {
            responseList = data;
            binding.linQuestions.removeAllViews();
            makeView();
        });

        notificationActivityVM.getIsSubmitAnswer().observe(this, isShow -> {
            disableEnableTouch(isShow);
            if (isShow) {
                binding.tvSave.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.tvSave.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        notificationActivityVM.getIsSubmitAnswerSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Preferences.writeBoolean(this, PREF_PARTNER_APP, true);
                Intent intent = new Intent(this, PartnerAboutActivity.class);
                intent.putExtra("list", responseListPage2);
                startActivity(intent);
            }
        });
    }

    private void makeView() {
        responseListPage2 = new ArrayList<>();
        if (responseList != null && responseList.size() > 0) {
            for (int i = 0; i < responseList.size(); i++) {
                if (responseList.get(i).page == 1 && !TextUtils.isEmpty(responseList.get(i).answer)) {
                    Preferences.writeBoolean(this, PREF_PARTNER_APP, true);
                    isPartnerApp = true;
                }
                if (responseList.get(i).page == 2 && !TextUtils.isEmpty(responseList.get(i).answer)) {
                    Preferences.writeBoolean(this, PREF_PARTNER_ABOUT, true);
                    isPartnerAbout = true;
                }
            }
        }

        setLayoutVisibility();

        for (int i = 0; i < responseList.size(); i++) {
            if (responseList.get(i).page == 1) {//page=1
                if (responseList.get(i).type == 1) {//selection type
                    View view = LayoutInflater.from(this).inflate(R.layout.custom_partner_textview, null);
                    TextViewSFTextPro tvQuestion = view.findViewById(R.id.tv_partner_question);
                    RecyclerView rvQuestions = view.findViewById(R.id.rv_partner_answer);

                    rvQuestions.setLayoutManager(new LinearLayoutManager(this));

                    tvQuestion.setText(responseList.get(i).question);
                    int finalI = i;

                    responseList.get(finalI).partnershipAnsId = responseList.get(finalI).partnershipAnswerID;
                    responseList.get(finalI).selectedAnswer = responseList.get(finalI).answer;

                    if (responseList.get(i).answers_option != null) {
                        for (PartnerWithUsResponse.Answers ans : responseList.get(i).answers_option) {
                            if (!TextUtils.isEmpty(responseList.get(i).answer) && responseList.get(i).answer.equalsIgnoreCase(ans.answer)) {
                                ans.isSelected = true;
                                break;
                            }
                        }
                    }

                    PartnerAnswerAdapter mAdapter = new PartnerAnswerAdapter(this, responseList.get(i).answers_option, (adapterPosition) -> {
                        try {
                            responseList.get(finalI).partnershipAnsId = responseList.get(finalI).answers_option.get(adapterPosition).id;
                            responseList.get(finalI).selectedAnswer = responseList.get(finalI).answers_option.get(adapterPosition).answer;

                            if (responseList.get(finalI).answers_option.get(adapterPosition).id >= 1 && responseList.get(finalI).answers_option.get(adapterPosition).id < 6) {
                                if (responseList.get(finalI).answers_option.get(adapterPosition).id == 2) {
                                    hideShowInvestorView(true);
                                } else {
                                    hideShowInvestorView(false);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                    rvQuestions.setAdapter(mAdapter);

                    if (responseList.get(i).id == 2) {
                        investorView = view;
                        if (!TextUtils.isEmpty(responseList.get(i).answer)) {
                            isInvestorViewAdded = true;
                        }
                    }


                    if (responseList.get(i).id != 2 || !TextUtils.isEmpty(responseList.get(i).answer)) {
                        binding.linQuestions.addView(view);
                    }
                } else if (responseList.get(i).type == 2) {//option type
                    answersOption = responseList.get(i).answers_option;

                    if (answersOption != null) {
                        for (PartnerWithUsResponse.Answers ans : answersOption) {
                            if (!TextUtils.isEmpty(responseList.get(i).answer) && responseList.get(i).answer.equalsIgnoreCase(ans.answer)) {
                                ans.isSelected = true;
                                break;
                            }
                        }
                    }

                    if (responseList.get(i).id == 5) {
                        binding.tvCountry.setText(responseList.get(i).answer);
                        binding.tvCountry.setTag(responseList.get(i).partnershipAnswerID);
                    } else if (responseList.get(i).id == 6) {
                        binding.tvAges.setText(responseList.get(i).answer);
                        binding.tvAges.setTag(responseList.get(i).partnershipAnswerID);
                    }

                } else if (responseList.get(i).type == 0) {
                    binding.etWebsite.setHint(responseList.get(i).placeholder);
                    binding.etWebsite.setText(responseList.get(i).answer);
                }
            } else {//page=2
                responseListPage2.add(responseList.get(i));
            }
        }
    }

    private void hideShowInvestorView(boolean isShow) {
        if (isShow) {
            isInvestorViewAdded = true;
            binding.linQuestions.addView(investorView, 1);
        } else if (isInvestorViewAdded) {
            binding.linQuestions.removeViewAt(1);
            isInvestorViewAdded = false;
        }
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
                    data.isSelected = data.countryName.equalsIgnoreCase(binding.tvCountry.getText().toString());
                }
                selectCountryAdapter = new SelectCountryAdapter(this, arrayList);
                selectCountryAdapter.setBlackColor(true);
                rvTypes.setAdapter(selectCountryAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvApply.setOnClickListener(v -> {
            if (selectCountryAdapter != null && selectCountryAdapter.getSelectedItem() != null) {
                binding.tvCountry.setText(selectCountryAdapter.getSelectedItem().countryName);
                binding.tvCountry.setTag(selectCountryAdapter.getSelectedItem().id);
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

    void showAgeDialog(ArrayList<PartnerWithUsResponse.Answers> ageGroups) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialog.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        etSearch.setHint(String.format(getString(R.string.search_for), ""+getString(R.string.age)));

        rvTypes.setLayoutManager(new LinearLayoutManager(this));
        try {
            if (ageGroups != null && ageGroups.size() > 0) {
                for (PartnerWithUsResponse.Answers data : ageGroups) {
                    data.isSelected = data.answer.equalsIgnoreCase(binding.tvAges.getText().toString());
                }
                selectPartnerAgeAdapter = new SelectPartnerAgeAdapter(this, ageGroups);
                rvTypes.setAdapter(selectPartnerAgeAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvApply.setOnClickListener(v -> {
            if (selectPartnerAgeAdapter != null && selectPartnerAgeAdapter.getSelectedItem() != null) {
                binding.tvAges.setText(selectPartnerAgeAdapter.getSelectedItem().answer);
                binding.tvAges.setTag(selectPartnerAgeAdapter.getSelectedItem().id);
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
                if (selectPartnerAgeAdapter != null)
                    selectPartnerAgeAdapter.getFilter().filter(s.toString());
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

}
