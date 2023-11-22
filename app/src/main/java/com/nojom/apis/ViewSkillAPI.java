package com.nojom.apis;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.UserSkillsModel;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import static com.nojom.util.Constants.API_VIEW_SKILL;

public class ViewSkillAPI implements APIRequest.APIRequestListener {
    private BaseActivity activity;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<UserSkillsModel> userModel = new MutableLiveData<>();

    public MutableLiveData<UserSkillsModel> getUserModel() {
        return userModel;
    }

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void getSkillsList(int pageNo, String search) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(true);

        CommonRequest.Skills skills = new CommonRequest.Skills();
        skills.setPage_no(pageNo);
        skills.setSearch(search);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_VIEW_SKILL, skills.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        UserSkillsModel userSkillsModel = UserSkillsModel.getSkills(decryptedData);
        if (userSkillsModel != null) {
            getUserModel().postValue(userSkillsModel);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
    }
}
