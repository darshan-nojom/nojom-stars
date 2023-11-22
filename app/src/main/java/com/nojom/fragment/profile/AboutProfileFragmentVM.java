package com.nojom.fragment.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.model.ProfileResponse;
import com.nojom.model.Skill;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.ArrayList;

public class AboutProfileFragmentVM extends ViewModel {

    private MutableLiveData<ArrayList<Skill>> listMutableLiveData;
    private MutableLiveData<ArrayList<Skill>> mDataEmployment;

    public MutableLiveData<ArrayList<Skill>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public MutableLiveData<ArrayList<Skill>> getDataEmployment() {
        if (mDataEmployment == null) {
            mDataEmployment = new MutableLiveData<>();
        }
        return mDataEmployment;
    }

    public AboutProfileFragmentVM() {

    }

    void getLanguageData(ProfileResponse profileData) {
        ArrayList<Skill> languageList = new ArrayList<>();
        if (profileData != null && profileData.language != null) {
            for (ProfileResponse.Language data : profileData.language) {
                if (data.name != null) {
                    languageList.add(new Skill(data.name,
                            Utils.getLanguageLevel(data.level)));
                }
            }
        }
        getListMutableLiveData().postValue(languageList);
    }

    void getEmploymentData(BaseActivity activity, ProfileResponse profileData) {
        try {
            ArrayList<Skill> employmentList = new ArrayList<>();
            if (profileData.experiences != null && profileData.experiences.size() > 0) {
                for (ProfileResponse.Experiences experiences : profileData.experiences) {
                    if (!activity.isEmpty(experiences.companyName)) {
                        if (!activity.isEmpty(experiences.startDate)) {
                            String sDate = Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM yyyy", experiences.startDate);
                            if (experiences.isCurrent == 1) {
                                employmentList.add(new Skill(experiences.companyName, sDate + " - Present"));
                            } else {
                                String eDate = Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM yyyy", experiences.endDate);
                                employmentList.add(new Skill(experiences.companyName, sDate + " - " + eDate));
                            }
                        }
                    }
                }
            }
            getDataEmployment().postValue(employmentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
