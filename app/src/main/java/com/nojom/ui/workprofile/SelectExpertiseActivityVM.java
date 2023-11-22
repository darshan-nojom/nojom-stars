package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.apis.GetServiceCategoryAPI;
import com.nojom.model.ProfileResponse;
import com.nojom.model.ServicesModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.List;

public class SelectExpertiseActivityVM extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private List<ServicesModel.Data> servicesList;
    private MutableLiveData<List<ServicesModel.Data>> listMutableLiveData;
    private ProfileResponse profileData;
    private BaseActivity.LayoutBinderHelper layoutBinderHelper;
    public int selectedExpertiseId;

    public MutableLiveData<List<ServicesModel.Data>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public void init(BaseActivity activity, BaseActivity.LayoutBinderHelper layoutBinderHelper) {
        this.activity = activity;
        this.layoutBinderHelper = layoutBinderHelper;
        initData();
    }

    private void initData() {
        profileData = Preferences.getProfileData(activity);
        servicesList = Preferences.getTopServices(activity);

        if (servicesList != null && servicesList.size() > 0) {
            setData();
        } else {
            GetServiceCategoryAPI getServiceCategoryAPI = new GetServiceCategoryAPI();
            getServiceCategoryAPI.init(activity);
            getServiceCategoryAPI.setServiceCategoryList();
            getServiceCategoryAPI.getServiceCategories();

            getServiceCategoryAPI.getServiceCategoryList().observe(activity, data -> {
                servicesList = data;
                setData();
            });
        }
    }

    private void setData() {
        if (layoutBinderHelper.getIsEdit()) {
            try {
                if (profileData != null && profileData.expertise != null) {
                    for (ServicesModel.Data data : servicesList) {
                        if (profileData.expertise.id != null && profileData.expertise.id == data.id) {
                            selectedExpertiseId = data.id;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getListMutableLiveData().postValue(servicesList);
    }
}
