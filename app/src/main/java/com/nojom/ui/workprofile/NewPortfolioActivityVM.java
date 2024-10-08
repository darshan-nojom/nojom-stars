package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_COMPANIES;
import static com.nojom.util.Constants.API_ADD_PORTFOLIO;
import static com.nojom.util.Constants.API_DELETE_COMPANIES;
import static com.nojom.util.Constants.API_DELETE_PORTFOLIO;
import static com.nojom.util.Constants.API_EDIT_PORTFOLIO;
import static com.nojom.util.Constants.API_GET_AGENT_COMPANIES;
import static com.nojom.util.Constants.API_GET_COMPANIES;
import static com.nojom.util.Constants.API_GET_PORTFOLIO;
import static com.nojom.util.Constants.API_REORDER_COMPANIES;
import static com.nojom.util.Constants.API_REORDER_PORTFOLIO;
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
import com.nojom.model.Portfolios;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NewPortfolioActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private final MutableLiveData<Integer> saveCompanyProgress = new MutableLiveData<>();
    private final MutableLiveData<GetCompanies> getCompanyData = new MutableLiveData<>();
    private MutableLiveData<List<Portfolios>> listMutableLiveData;

    public MutableLiveData<Integer> getSaveCompanyProgress() {
        return saveCompanyProgress;
    }

    public MutableLiveData<GetCompanies> getCompanyData() {
        return getCompanyData;
    }

    public MutableLiveData<List<Portfolios>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
//        initData();
    }

    public void initData() {
        getCompanies();
        getMyPortfolios();
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

    public void addAgentCompanies(int cId, String time) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);
        getSaveCompanyProgress().postValue(1);

        CommonRequest.AddCompany expertiseRequest = new CommonRequest.AddCompany();
        expertiseRequest.setCompany_id(cId);
        if (TextUtils.isEmpty(time)) {
            time = "0";
        }
        expertiseRequest.setTimes(Integer.parseInt(time));

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_COMPANIES, expertiseRequest.toString(), true, this);
    }

    public void updatePortfolio(int cId, int publicStatus, int id, String path) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);
        getSaveCompanyProgress().postValue(1);
        MultipartBody.Part body = null;
//        if (TextUtils.isEmpty(path)) {
        File file = new File(path);

        file = CompressFile.getCompressedImageFile(file);
        if (file != null) {
            Uri selectedUri = Uri.fromFile(file);
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

            RequestBody requestFile = null;
            if (mimeType != null) {
                requestFile = RequestBody.create(file, MediaType.parse(mimeType));
            }

            Headers.Builder headers = new Headers.Builder();
            headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\"");

            if (requestFile != null) {
                body = MultipartBody.Part.create(headers.build(), requestFile);
            }
        }
//        }
        CommonRequest.UpdateNewPortfolio expertiseRequest = new CommonRequest.UpdateNewPortfolio();
        if (cId != 0) {
            expertiseRequest.setCompany_id(cId);
        }
        expertiseRequest.setPublic_status(publicStatus);
        expertiseRequest.setStatus(1);
        expertiseRequest.setPortfolio_id(id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_EDIT_PORTFOLIO, expertiseRequest.toString(), this, body);
    }

    public void deletePortfolio(int cId) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

        CommonRequest.DeletePortfolio expertiseRequest = new CommonRequest.DeletePortfolio();
        expertiseRequest.setPortfolio_id(cId);//agent company id

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_PORTFOLIO, expertiseRequest.toString(), true, this);
    }

    public void reOrderMedia(BaseActivity activity, JSONArray object) {
        if (!activity.isNetworkConnected()) return;
        this.activity = activity;

        CommonRequest.ReOrderMedia reOrderMedia = new CommonRequest.ReOrderMedia();
        reOrderMedia.setReorder(object.toString());

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_REORDER_PORTFOLIO, reOrderMedia.toString(), true, this);
    }

    void addPortfolio(String path, int cId, int publicStatus, String name, String link, String companyFile) {
        if (!activity.isNetworkConnected()) return;

        getSaveCompanyProgress().postValue(1);

        File file = new File(path);

        MultipartBody.Part body = null;
        file = CompressFile.getCompressedImageFile(file);
        if (file != null) {
            Uri selectedUri = Uri.fromFile(file);
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

            RequestBody requestFile = null;
            if (mimeType != null) {
                requestFile = RequestBody.create(file, MediaType.parse(mimeType));
            }

            Headers.Builder headers = new Headers.Builder();
            headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\"");

            if (requestFile != null) {
                body = MultipartBody.Part.create(headers.build(), requestFile);
            }
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


        CommonRequest.AddNewPortfolio addPortfolio = new CommonRequest.AddNewPortfolio();
        if (cId != 0) {
            addPortfolio.setCompany_id(cId);
        }
        addPortfolio.setPublic_status(publicStatus);
        if (!TextUtils.isEmpty(name)) {
            addPortfolio.setCompany_name(name);
        }
        if (!TextUtils.isEmpty(link)) {
            addPortfolio.setCompany_link(link);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_ADD_PORTFOLIO, addPortfolio.toString(), this, body, body1);

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        switch (urlEndPoint) {
            case API_GET_COMPANIES: {
                GetCompanies gigCategories = GetCompanies.getCompanies(decryptedData);
                if (gigCategories != null) {
                    getCompanyData().postValue(gigCategories);
                }
                break;
            }
            case API_ADD_PORTFOLIO:
            case API_EDIT_PORTFOLIO:
                getCompanies();
                getMyPortfolios();
                getSaveCompanyProgress().postValue(0);
                getSaveCompanyProgress().postValue(11);
                activity.toastMessage(message);
                break;
            case API_DELETE_PORTFOLIO:
                getMyPortfolios();
                getSaveCompanyProgress().postValue(2);
                activity.toastMessage(message);
                break;
            case API_REORDER_PORTFOLIO:
                activity.toastMessage(message);
                break;
            case API_GET_PORTFOLIO:
                List<Portfolios> portfolios = Portfolios.getPortfolios(decryptedData);
                if (portfolios != null && portfolios.size() > 0) {
                    getListMutableLiveData().postValue(portfolios);
                }
                break;
        }
        activity.disableEnableTouch(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equals(API_ADD_PORTFOLIO) || urlEndPoint.equals(API_EDIT_PORTFOLIO)) {
            getSaveCompanyProgress().postValue(0);
        } else if (urlEndPoint.equals(API_GET_PORTFOLIO)) {
            getListMutableLiveData().postValue(null);
        }
        activity.disableEnableTouch(false);
    }

    public void getMyPortfolios() {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_PORTFOLIO, null, false, this);
    }
}
