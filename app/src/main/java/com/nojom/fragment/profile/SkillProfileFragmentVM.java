package com.nojom.fragment.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.fragment.BaseFragment;
import com.nojom.model.ProfileResponse;
import com.nojom.model.Skill;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;

public class SkillProfileFragmentVM extends ViewModel {
    private ProfileResponse profileData;
    private MutableLiveData<ArrayList<Skill>> listMutableLiveDataExp;
    private MutableLiveData<ArrayList<Skill>> listMutableLiveDataSkill;

    public MutableLiveData<ArrayList<Skill>> getListMutableLiveDataExp() {
        if (listMutableLiveDataExp == null) {
            listMutableLiveDataExp = new MutableLiveData<>();
        }
        return listMutableLiveDataExp;
    }

    public MutableLiveData<ArrayList<Skill>> getListMutableLiveDataSkill() {
        if (listMutableLiveDataSkill == null) {
            listMutableLiveDataSkill = new MutableLiveData<>();
        }
        return listMutableLiveDataSkill;
    }

    public SkillProfileFragmentVM() {

    }

    void refreshViews(BaseFragment fragment) {
        profileData = Preferences.getProfileData(fragment.activity);

        getExpertiseData();

        getSkillData();

    }

    private void getExpertiseData() {
        ArrayList<Skill> expertiseList = new ArrayList<>();
        if (profileData != null && profileData.expertise != null
                && profileData.expertise.length != null && profileData.expertise.nameApp != null) {
            expertiseList.add(new Skill(profileData.expertise.nameApp,
                    Utils.getExperienceLevel(profileData.expertise.length)));
            getListMutableLiveDataExp().postValue(expertiseList);
        }
    }

    private void getSkillData() {
        ArrayList<Skill> skillList = new ArrayList<>();
        if (profileData != null && profileData.skills != null) {
            for (ProfileResponse.Skill skills : profileData.skills) {
                skillList.add(new Skill(skills.name,
                        Utils.getRatingLevel(skills.rating)));
            }
            getListMutableLiveDataSkill().postValue(skillList);
        }
    }
}
