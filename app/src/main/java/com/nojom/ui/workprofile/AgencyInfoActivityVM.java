package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.Skill;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;

public class AgencyInfoActivityVM extends ViewModel implements Constants, APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<ArrayList<Skill>> mDataEmployment;
    private MutableLiveData<ArrayList<Skill>> mDataEducation;
    private MutableLiveData<Boolean> isShowWebView = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isAgencyAddSuccess = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Boolean> getIsAgencyAddSuccess() {
        return isAgencyAddSuccess;
    }

    public MutableLiveData<Boolean> getIsShowWebView() {
        return isShowWebView;
    }

    public AgencyInfoActivityVM() {

    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    void makePublicPrivate(SegmentedButtonGroup segmentedButtonGroup) {
        if (!activity.isNetworkConnected())
            return;

//        getIsShowProgress().postValue(true);

        CommonRequest.ProfilePublicityTypeId profilePublicity = new CommonRequest.ProfilePublicityTypeId();
        profilePublicity.setProfilePublicityTypeID(String.valueOf(segmentedButtonGroup.getTag()));
        profilePublicity.setStatus(String.valueOf(segmentedButtonGroup.getPosition()));

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_PROFILE_PUBLICITY, profilePublicity.toString(), true, this);

    }

    void addAgencyInfoAPI(String agName, String agAbout, String agPhone, String agEmail, String agWebsite, String agAdd,
                          String note, int id, int name, int sgAddressPosition, int sgAboutPosition, int sgPhonePosition,
                          int sgEmailPosition, int sgWebsitePosition, int sgNotePosition) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(true);

        CommonRequest.AddAgencyInfo profilePublicity = new CommonRequest.AddAgencyInfo();
        profilePublicity.setAbout(agAbout);
        profilePublicity.setEmail(agEmail);
        profilePublicity.setAddress(agAdd);
        if (id != 0) {
            profilePublicity.setId(id);
        }
        profilePublicity.setName(agName);
        profilePublicity.setPhone(agPhone);
        profilePublicity.setWebsite(agWebsite);
        profilePublicity.setNote(note);
        profilePublicity.setName_public(name);
        profilePublicity.setAddress_public(sgAddressPosition);
        profilePublicity.setAbout_public(sgAboutPosition);
        profilePublicity.setPhone_public(sgPhonePosition);
        profilePublicity.setEmail_public(sgEmailPosition);
        profilePublicity.setWebsite_public(sgWebsitePosition);
        profilePublicity.setNote_public(sgNotePosition);


        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, ADD_AGENCY, profilePublicity.toString(), true, this);

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_PROFILE_PUBLICITY)) {
            activity.toastMessage(msg);
        } else if (urlEndPoint.equalsIgnoreCase(ADD_AGENCY)) {
            activity.toastMessage(msg);
            getIsAgencyAddSuccess().postValue(true);
        }
        activity.getProfile();
        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
    }
}
