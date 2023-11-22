package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_SUMMARY;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.GeneralModel;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import retrofit2.Response;

public class SummaryActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<Response<GeneralModel>> responseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Response<GeneralModel>> getResponseMutableLiveData() {
        return responseMutableLiveData;
    }

    public SummaryActivityVM() {
    }

    void init(BaseActivity activity) {
        this.activity = activity;
    }

    void updateSummary(String summary) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(true);

        CommonRequest.UpdateSummary updateSummary = new CommonRequest.UpdateSummary();
        updateSummary.setContent(summary);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_SUMMARY, updateSummary.toString(), true, this);
    }

    boolean isValid(String summary) {
        if (activity.isEmpty(summary)) {
            activity.validationError(activity.getString(R.string.enter_your_summary));
            return false;
        }

        if (summary.length() < 100) {
            activity.validationError(activity.getString(R.string.des_min_1000_char));
            return false;
        }

        if (summary.length() > 5000) {
            activity.validationError(activity.getString(R.string.desc_max_1000_char));
            return false;
        }
        return true;
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        getResponseMutableLiveData().postValue(null);
        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
    }
}
