package com.nojom.ui.workprofile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.model.ProfileResponse;
import com.nojom.model.Skill;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class EditSkillsActivityVM extends ViewModel {
    private MutableLiveData<ArrayList<Skill>> skillDataList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Skill>> expertiseDataList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Skill>> languageDataList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Skill>> getSkillDataList() {
        if (skillDataList == null) {
            skillDataList = new MutableLiveData<>();
        }
        return skillDataList;
    }

    public MutableLiveData<ArrayList<Skill>> getExpertiseDataList() {
        if (expertiseDataList == null) {
            expertiseDataList = new MutableLiveData<>();
        }
        return expertiseDataList;
    }

    public MutableLiveData<ArrayList<Skill>> getLanguageDataList() {
        if (languageDataList == null) {
            languageDataList = new MutableLiveData<>();
        }
        return languageDataList;
    }

    public EditSkillsActivityVM() {

    }

    void getLanguageData(List<ProfileResponse.Language> languages) {
        ArrayList<Skill> languageList = new ArrayList<>();
        try {
            if (languages != null && languages.size() > 0) {
                for (ProfileResponse.Language data : languages) {
                    languageList.add(new Skill(data.name,
                            Utils.getLanguageLevel(data.level)));
                }

            }
            getLanguageDataList().postValue(languageList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getExpertiseData(ProfileResponse profileData) {
        ArrayList<Skill> expertiseList = new ArrayList<>();
        try {
            if (profileData != null && profileData.expertise != null && profileData.expertise.id != null) {
                expertiseList.add(new Skill(profileData.expertise.id, profileData.expertise.nameApp,
                        Utils.getExperienceLevel(profileData.expertise.length)));
            }
            getExpertiseDataList().postValue(expertiseList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getSkillData(ProfileResponse profileData) {
        ArrayList<Skill> skillList = new ArrayList<>();
        try {
            if (profileData != null) {
                if (profileData.skills != null && profileData.skills.size() > 0) {
                    for (ProfileResponse.Skill data : profileData.skills) {
                        skillList.add(new Skill(data.name,
                                Utils.getRatingLevel(data.rating)));
                    }
                }
            }
            getSkillDataList().postValue(skillList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
