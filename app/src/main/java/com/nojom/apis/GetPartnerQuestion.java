package com.nojom.apis;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.PartnerWithUsResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.ArrayList;

import static com.nojom.util.Constants.API_GET_PARTNER_QUESTION;
import static com.nojom.util.Constants.PREF_PARTNER_ABOUT;
import static com.nojom.util.Constants.PREF_PARTNER_APP;

public class GetPartnerQuestion implements APIRequest.APIRequestListener {
    private BaseActivity activity;
    private MutableLiveData<Boolean> isShow = new MutableLiveData<>();
    private MutableLiveData<ArrayList<PartnerWithUsResponse.Data>> listMutableLiveData;

    public MutableLiveData<ArrayList<PartnerWithUsResponse.Data>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsShow() {
        return isShow;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void getPartnerQuestions() {
        if (!activity.isNetworkConnected())
            return;

        getIsShow().postValue(true);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_PARTNER_QUESTION, null, false, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_GET_PARTNER_QUESTION)) {
            ArrayList<PartnerWithUsResponse.Data> languageList = PartnerWithUsResponse.getPartnerQuestionList(decryptedData);
            if (languageList != null && languageList.size() > 0) {

                for (int i = 0; i < languageList.size(); i++) {
                    if (languageList.get(i).page == 1 && !TextUtils.isEmpty(languageList.get(i).answer)) {
                        Preferences.writeBoolean(activity, PREF_PARTNER_APP, true);
                    }
                    if (languageList.get(i).page == 2 && !TextUtils.isEmpty(languageList.get(i).answer)) {
                        Preferences.writeBoolean(activity, PREF_PARTNER_ABOUT, true);
                    }
                }

                getListMutableLiveData().postValue(languageList);
            }
            getIsShow().postValue(false);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShow().postValue(false);
    }
}
