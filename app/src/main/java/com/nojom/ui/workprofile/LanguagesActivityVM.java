package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.SelectItemAdapter;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityLanguagesBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.Language;
import com.nojom.model.ProfileResponse;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.nojom.util.Constants.API_ADD_LANGUAGE;
import static com.nojom.util.Constants.API_LANGUAGE;

public class LanguagesActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private ArrayList<Language.Data> languageList;
    private ArrayList<Language.Data> languagesArray;
    private MutableLiveData<ArrayList<Language.Data>> listMutableLiveData;
    private MutableLiveData<ArrayList<Language.Data>> arrayMutableLiveData;

    public MutableLiveData<ArrayList<Language.Data>> getArrayMutableLiveData() {
        if (arrayMutableLiveData == null) {
            arrayMutableLiveData = new MutableLiveData<>();
        }
        return arrayMutableLiveData;
    }

    public MutableLiveData<ArrayList<Language.Data>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    private SelectItemAdapter itemAdapter;
    private ResponseListener responseListener;

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    void init(BaseActivity activity, ActivityLanguagesBinding binding) {
        this.activity = activity;
        languagesArray = new ArrayList<>();
        languageList = new ArrayList<>();

        if (!binding.getLayoutBinder().getIsEdit())
            languageList.add(new Language.Data());

        //get selected languages
        ProfileResponse profileResponse = activity.getProfileData();
        if (profileResponse != null) {
            List<ProfileResponse.Language> selectedLanguages = profileResponse.language;

            if (binding.getLayoutBinder().getIsEdit()) {
                binding.editToolBar.rlEdit.setVisibility(View.VISIBLE);
                binding.toolbar.header.setVisibility(View.GONE);

                if (selectedLanguages != null && selectedLanguages.size() > 0) {
                    for (ProfileResponse.Language languages : selectedLanguages) {
                        if (languages != null) {
                            Language.Data model = new Language.Data();
                            model.id = languages.id;
                            model.name = languages.name;
                            model.level = Utils.getLanguageLevel(languages.level);
                            model.levelId = languages.level;
                            languageList.add(model);
                        }
                    }
                } else {
                    languageList.add(new Language.Data());
                }
                getListMutableLiveData().postValue(languageList);
            }
        }

        getLanguageList();
    }

    void addLanguage(String languagesId, String levelId) {
        if (!activity.isNetworkConnected())
            return;
        activity.disableEnableTouch(true);

        CommonRequest.AddProfileLanguage addProfileLanguage = new CommonRequest.AddProfileLanguage();
        addProfileLanguage.setLanguage_id(languagesId);
        addProfileLanguage.setLevel(levelId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_LANGUAGE, addProfileLanguage.toString(), true, this);
    }


    void showLanguageSelectDialog(ArrayList<Language.Data> arrayList, final TextView textView,
                                  final Language.Data language, final boolean isLanguage) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialog.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        if (isLanguage) {
            etSearch.setHint(String.format(activity.getString(R.string.search_for), activity.getString(R.string.language).toLowerCase()));
        } else {
            etSearch.setVisibility(View.GONE);
            etSearch.setHint(String.format(activity.getString(R.string.search_for), activity.getString(R.string.level).toLowerCase()));
        }

        rvTypes.setLayoutManager(new LinearLayoutManager(activity));
        try {
            if (arrayList != null && arrayList.size() > 0) {
                for (Language.Data data : arrayList) {
                    if (isLanguage) {
                        data.isSelected = data.getName(activity.language).equalsIgnoreCase(textView.getText().toString());
                    } else {
                        data.isSelected = data.level.equalsIgnoreCase(textView.getText().toString());
                    }
                }
                itemAdapter = new SelectItemAdapter(activity, arrayList, isLanguage);
                rvTypes.setAdapter(itemAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvApply.setOnClickListener(v -> {
            if (itemAdapter != null && itemAdapter.getSelectedItem() != null) {
                if (isLanguage) {
                    textView.setText(itemAdapter.getSelectedItem().getName(activity.language));
                    language.name = itemAdapter.getSelectedItem().getName(activity.language);
                    language.id = itemAdapter.getSelectedItem().id;
                } else {
                    textView.setText(itemAdapter.getSelectedItem().level);
                    language.level = itemAdapter.getSelectedItem().level;
                    language.levelId = itemAdapter.getSelectedItem().levelId;
                }
                dialog.dismiss();
            } else {
                activity.toastMessage(activity.getString(R.string.please_select_one_item));
            }
            dialog.dismiss();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (itemAdapter != null)
                    itemAdapter.getFilter().filter(s.toString());
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
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(activity, etSearch)));
        etSearch.requestFocus();
    }

    ArrayList<Language.Data> getLevelList() {
        ArrayList<Language.Data> arrayList = new ArrayList<>();
        Language.Data model = new Language.Data();
        model.level = activity.getString(R.string.basic);
        model.levelId = 0;
        arrayList.add(model);
        model = new Language.Data();
        model.level = activity.getString(R.string.conversational);
        model.levelId = 1;
        arrayList.add(model);
        model = new Language.Data();
        model.level = activity.getString(R.string.fluent);
        model.levelId = 2;
        arrayList.add(model);
        model = new Language.Data();
        model.level = activity.getString(R.string.native_);
        model.levelId = 3;
        arrayList.add(model);
        return arrayList;
    }

    private void getLanguageList() {
        if (!activity.isNetworkConnected())
            return;

//        activity.showProgress();

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_LANGUAGE, null, false, this);
    }

    boolean isValid() {
        try {
            if (languageList != null && languageList.size() > 0) {
                for (Language.Data data : languageList) {
                    if (data == null) {
                        activity.validationError(activity.getString(R.string.please_select_language));
                        return false;
                    }

                    if (activity.isEmpty(data.getName(activity.language))) {
                        activity.validationError(activity.getString(R.string.please_select_language));
                        return false;
                    } else if (activity.isEmpty(data.level)) {
                        activity.validationError(activity.getString(R.string.please_select_level));
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    void addMoreLanguage() {
        languageList.add(new Language.Data());
        getListMutableLiveData().postValue(languageList);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        if (!TextUtils.isEmpty(urlEndPoint)) {
            if (urlEndPoint.equals(API_LANGUAGE)) {
                List<Language.Data> languageList = Language.getLanguageList(decryptedData);
                if (languageList != null && languageList.size() > 0) {
                    languagesArray = (ArrayList<Language.Data>) languageList;
                    getArrayMutableLiveData().setValue(languagesArray);
                    getListMutableLiveData().postValue(this.languageList);
                }
            } else if (urlEndPoint.equals(API_ADD_LANGUAGE)) {
                //add selected language into preferences
                List<ProfileResponse.Language> languageList = ProfileResponse.getSelectedLanguageList(decryptedData);
                if (languageList != null && languageList.size() > 0) {
                    ProfileResponse profileResponse = activity.getProfileData();
                    if (profileResponse != null) {
                        profileResponse.language = languageList;
                        Preferences.setProfileData(activity, profileResponse);
                    }
                }
                if (responseListener != null) {
                    responseListener.onResponseSuccess(null);
                }
            }
        }
//        activity.hideProgress();
        activity.disableEnableTouch(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (!TextUtils.isEmpty(urlEndPoint)) {
            if (urlEndPoint.equals(API_ADD_LANGUAGE)) {
                if (responseListener != null) {
                    responseListener.onError();
                }
            }
        }
        activity.disableEnableTouch(false);
    }
}
