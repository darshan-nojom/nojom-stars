package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_COMPANIES;
import static com.nojom.util.Constants.API_ADD_PORTFOLIO;
import static com.nojom.util.Constants.API_DELETE_COMPANIES;
import static com.nojom.util.Constants.API_GET_AGENT_COMPANIES;
import static com.nojom.util.Constants.API_GET_COMPANIES;
import static com.nojom.util.Constants.API_REORDER_COMPANIES;
import static com.nojom.util.Constants.API_RE_ORDER_MEDIA;
import static com.nojom.util.Constants.API_UPDATE_COMPANIES;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.GetAgentCompanies;
import com.nojom.model.GetCompanies;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;

import org.json.JSONArray;

import java.io.File;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class WorkWithActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private final MutableLiveData<Integer> saveCompanyProgress = new MutableLiveData<>();
    private final MutableLiveData<GetCompanies> getCompanyData = new MutableLiveData<>();
    private final MutableLiveData<GetAgentCompanies> getAgentCompanyData = new MutableLiveData<>();

    public MutableLiveData<Integer> getSaveCompanyProgress() {
        return saveCompanyProgress;
    }

    public MutableLiveData<GetCompanies> getCompanyData() {
        return getCompanyData;
    }

    public MutableLiveData<GetAgentCompanies> getAgentCompanyData() {
        return getAgentCompanyData;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
//        initData();
    }

    public void initData() {
        getCompanies();
        getAgentCompanies();
    }

    void getCompanies() {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

//        CommonRequest.ExpertiseRequest expertiseRequest = new CommonRequest.ExpertiseRequest();
//        expertiseRequest.setExperience(experience);
//        expertiseRequest.setService_category_id(serviceIds);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_COMPANIES, null, false, this);
    }

    public void getAgentCompanies() {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

//        CommonRequest.ExpertiseRequest expertiseRequest = new CommonRequest.ExpertiseRequest();
//        expertiseRequest.setExperience(experience);
//        expertiseRequest.setService_category_id(serviceIds);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_AGENT_COMPANIES, null, false, this);
    }

    public void addAgentCompanies(int cId, String time, int psTimes, String campDate, int psCamDate
            , String fromDate, String toDate, int psContractStatus, boolean isContract, String name, String link, String companyFile) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);
        getSaveCompanyProgress().postValue(1);

        CommonRequest.AddCompany expertiseRequest = new CommonRequest.AddCompany();
        expertiseRequest.setCompany_id(cId);
        if (TextUtils.isEmpty(time)) {
            time = "0";
        }
        expertiseRequest.setTimes(Integer.parseInt(time));
        expertiseRequest.setTimes_public_status(psTimes);
        expertiseRequest.setPublic_status(1);
        if (isContract) {
            expertiseRequest.setContract_start_date(fromDate);
            expertiseRequest.setContract_end_date(toDate);
            expertiseRequest.setContract_public_status(psContractStatus);
        } else {
            if (!TextUtils.isEmpty(campDate)) {
                expertiseRequest.setCampaign_date(campDate);
            }
            expertiseRequest.setCampaign_date_public_status(psCamDate);

        }

        if (!TextUtils.isEmpty(name)) {
            expertiseRequest.setCompany_name(name);
        }
        if (!TextUtils.isEmpty(link)) {
            expertiseRequest.setCompany_link(link);
        }

        File file1 = new File(companyFile);

        MultipartBody.Part body1 = null;
        file1 = CompressFile.getCompressedImageFile(file1);
        if (file1 != null) {
            Uri selectedUri = Uri.fromFile(file1);
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

            RequestBody requestFile = null;
            if (mimeType != null) {
                requestFile = RequestBody.create(file1, MediaType.parse(mimeType));
            }

            Headers.Builder headers = new Headers.Builder();
            headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"company_file\"; filename=\"" + file1.getName() + "\"");

            if (requestFile != null) {
                body1 = MultipartBody.Part.create(headers.build(), requestFile);
            }
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_ADD_COMPANIES, expertiseRequest.toString(), this, body1);
    }

    public void updateAgentCompanies(int cId, int status, int publicStatus, int id, String time
            , int psTimes, String campDate, int psCamDate
            , String fromDate, String toDate, int psContractStatus, boolean isContract, String name, String link, String companyFile) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

        CommonRequest.UpdateCompany expertiseRequest = new CommonRequest.UpdateCompany();
        expertiseRequest.setCompany_id(cId);
        if (TextUtils.isEmpty(time)) {
            time = "0";
        }
        expertiseRequest.setTimes(Integer.parseInt(time));
        expertiseRequest.setStatus(status);
        expertiseRequest.setPublic_status(publicStatus);
        expertiseRequest.setId(id);
        expertiseRequest.setTimes_public_status(psTimes);

        if (isContract) {
            expertiseRequest.setContract_start_date(fromDate);
            expertiseRequest.setContract_end_date(toDate);
            expertiseRequest.setContract_public_status(psContractStatus);
        } else {
            if (!TextUtils.isEmpty(campDate)) {
                expertiseRequest.setCampaign_date(campDate);
            }
            expertiseRequest.setCampaign_date_public_status(psCamDate);
        }


        if (!TextUtils.isEmpty(name)) {
            expertiseRequest.setCompany_name(name);
        }
        if (!TextUtils.isEmpty(link)) {
            expertiseRequest.setCompany_link(link);
        }

        File file1 = new File(companyFile);

        MultipartBody.Part body1 = null;
        file1 = CompressFile.getCompressedImageFile(file1);
        if (file1 != null) {
            Uri selectedUri = Uri.fromFile(file1);
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

            RequestBody requestFile = null;
            if (mimeType != null) {
                requestFile = RequestBody.create(file1, MediaType.parse(mimeType));
            }

            Headers.Builder headers = new Headers.Builder();
            headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"company_file\"; filename=\"" + file1.getName() + "\"");

            if (requestFile != null) {
                body1 = MultipartBody.Part.create(headers.build(), requestFile);
            }
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_UPDATE_COMPANIES, expertiseRequest.toString(), this, body1);


//        APIRequest apiRequest = new APIRequest();
//        apiRequest.makeAPIRequest(activity, API_UPDATE_COMPANIES, expertiseRequest.toString(), true, this);
    }

    public void updateAgentCompanies(int cId, int status, int publicStatus, int id, String time
            , int psTimes, String campDate, int psCamDate
            , String fromDate, String toDate, int psContractStatus, boolean isContract) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

        CommonRequest.UpdateCompany expertiseRequest = new CommonRequest.UpdateCompany();
        expertiseRequest.setCompany_id(cId);
        if (TextUtils.isEmpty(time)) {
            time = "0";
        }
        expertiseRequest.setTimes(Integer.parseInt(time));
        expertiseRequest.setStatus(status);
        expertiseRequest.setPublic_status(publicStatus);
        expertiseRequest.setId(id);
        expertiseRequest.setTimes_public_status(psTimes);

        if (isContract) {
            expertiseRequest.setContract_start_date(fromDate);
            expertiseRequest.setContract_end_date(toDate);
            expertiseRequest.setContract_public_status(psContractStatus);
        } else {
            if (!TextUtils.isEmpty(campDate)) {
                expertiseRequest.setCampaign_date(campDate);
            }
            expertiseRequest.setCampaign_date_public_status(psCamDate);

        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_COMPANIES, expertiseRequest.toString(), true, this);
    }

    public void deleteAgentCompanies(int cId) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

        CommonRequest.DeleteCompany expertiseRequest = new CommonRequest.DeleteCompany();
        expertiseRequest.setId(cId);//agent company id

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_COMPANIES, expertiseRequest.toString(), true, this);
    }

    public void reOrderMedia(BaseActivity activity, JSONArray object) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;

        CommonRequest.ReOrderMedia reOrderMedia = new CommonRequest.ReOrderMedia();
        reOrderMedia.setReorder(object.toString());

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_REORDER_COMPANIES, reOrderMedia.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        if (urlEndPoint.equals(API_GET_COMPANIES)) {
            GetCompanies gigCategories = GetCompanies.getCompanies(decryptedData);
            if (gigCategories != null) {
                getCompanyData().postValue(gigCategories);
            }
        } else if (urlEndPoint.equals(API_GET_AGENT_COMPANIES)) {
            GetAgentCompanies gigCategories = GetAgentCompanies.getAgentCompanies(decryptedData);
            if (gigCategories != null) {
                getAgentCompanyData().postValue(gigCategories);
            }
        } else if (urlEndPoint.equals(API_ADD_COMPANIES) || urlEndPoint.equals(API_UPDATE_COMPANIES)) {
            getAgentCompanies();
            getSaveCompanyProgress().postValue(0);
            getSaveCompanyProgress().postValue(11);
            activity.toastMessage(message);
        } else if (urlEndPoint.equals(API_DELETE_COMPANIES)) {
            getAgentCompanies();
            getSaveCompanyProgress().postValue(2);
            activity.toastMessage(message);
        } else if (urlEndPoint.equals(API_REORDER_COMPANIES)) {
            activity.toastMessage(message);
        }
        activity.disableEnableTouch(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equals(API_ADD_COMPANIES) || urlEndPoint.equals(API_UPDATE_COMPANIES)) {
            getSaveCompanyProgress().postValue(0);
        }
        activity.disableEnableTouch(false);
    }
}
