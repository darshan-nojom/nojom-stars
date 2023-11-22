package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.model.Available;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.ArrayList;
import java.util.List;

public class EditAvailabilityActivityVM extends ViewModel /*implements APIRequest.APIRequestListener*/ {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private ArrayList<Available.Data> availabilityList;
    private MutableLiveData<ArrayList<Available.Data>> mutableLiveDataWork;
    private MutableLiveData<List<ProfileResponse.ProfilePayType>> mutableLiveDataAvailable;
    private ProfileResponse profileData;

    public MutableLiveData<ArrayList<Available.Data>> getMutableLiveDataWork() {
        if (mutableLiveDataWork == null) {
            mutableLiveDataWork = new MutableLiveData<>();
        }
        return mutableLiveDataWork;
    }

    public MutableLiveData<List<ProfileResponse.ProfilePayType>> getMutableLiveDataAvailable() {
        if (mutableLiveDataAvailable == null) {
            mutableLiveDataAvailable = new MutableLiveData<>();
        }
        return mutableLiveDataAvailable;
    }

    public EditAvailabilityActivityVM() {

    }

    public void init(EditAvailabilityActivity editAvailabilityActivity) {
        this.activity = editAvailabilityActivity;
        this.profileData = Preferences.getProfileData(activity);
    }

    void getWorkTypeList() {
        if (profileData == null) {
            return;
        }

        ArrayList<Available.Data> workTypeList = new ArrayList<>();
        Available.Data model = new Available.Data();
        model.name = activity.getString(R.string.office_base);
        if (!activity.isEmpty(profileData.workbase) && (profileData.workbase.equals("0") || profileData.workbase.equals("2"))) {
            model.isChecked = true;
        }
        workTypeList.add(model);
        model = new Available.Data();
        model.name = activity.getString(R.string.home_base);
        if (!activity.isEmpty(profileData.workbase) && (profileData.workbase.equals("1") || profileData.workbase.equals("2"))) {
            model.isChecked = true;
        }
        workTypeList.add(model);
        getMutableLiveDataWork().postValue(workTypeList);
    }
}
