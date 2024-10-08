package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_PRODUCT;
import static com.nojom.util.Constants.API_ADD_STORES;
import static com.nojom.util.Constants.API_DELETE_PRODUCT;
import static com.nojom.util.Constants.API_DELETE_STORES;
import static com.nojom.util.Constants.API_EDIT_STORES;
import static com.nojom.util.Constants.API_GET_PRODUCT;
import static com.nojom.util.Constants.API_GET_STORES;
import static com.nojom.util.Constants.API_REORDER_PRODUCT;
import static com.nojom.util.Constants.API_REORDER_STORES;
import static com.nojom.util.Constants.API_UPDATE_PRODUCT;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.GetProduct;
import com.nojom.model.GetStores;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;

import org.json.JSONArray;

import java.io.File;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MyStoreActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private final MutableLiveData<Integer> saveCompanyProgress = new MutableLiveData<>();
    private final MutableLiveData<GetStores> getCompanyData = new MutableLiveData<>();
    private final MutableLiveData<GetProduct> getProductData = new MutableLiveData<>();

    public MutableLiveData<Integer> getSaveCompanyProgress() {
        return saveCompanyProgress;
    }

    public MutableLiveData<GetStores> getStoreDataList() {
        return getCompanyData;
    }

    public MutableLiveData<GetProduct> getProductDataList() {
        return getProductData;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
        initData();
    }

    private void initData() {
        //getCompanies();
       // getStores();
//        getProduct();
    }


    public void updateStores(int status, int id, String path, String title, String link) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

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
        CommonRequest.UpdateStores expertiseRequest = new CommonRequest.UpdateStores();
        expertiseRequest.setPublic_status(status);
        expertiseRequest.setStatus(1);
        expertiseRequest.setId(id);
        if (!TextUtils.isEmpty(link)) {
            expertiseRequest.setLink(link);
        }
        if (!TextUtils.isEmpty(title)) {
            expertiseRequest.setTitle(title);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_EDIT_STORES, expertiseRequest.toString(), this, body);
    }

    public void deleteStore(int cId) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

        CommonRequest.DeleteStore expertiseRequest = new CommonRequest.DeleteStore();
        expertiseRequest.setId(cId);//agent company id

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_STORES, expertiseRequest.toString(), true, this);
    }

    public void deleteProduct(int cId) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

        CommonRequest.DeleteStore expertiseRequest = new CommonRequest.DeleteStore();
        expertiseRequest.setId(cId);//agent company id

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_PRODUCT, expertiseRequest.toString(), true, this);
    }

    public void reOrderStore(BaseActivity activity, JSONArray object, int pos) {
        if (!activity.isNetworkConnected()) return;
        this.activity = activity;

        CommonRequest.ReOrderMedia reOrderMedia = new CommonRequest.ReOrderMedia();
        reOrderMedia.setReorder(object.toString());

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, pos == 0 ? API_REORDER_STORES : API_REORDER_PRODUCT, reOrderMedia.toString(), true, this);
    }

    void addStores(String path, String title, String link) {
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

        CommonRequest.AddStores addPortfolio = new CommonRequest.AddStores();
        addPortfolio.setLink(link);
        addPortfolio.setTitle(title);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_ADD_STORES, addPortfolio.toString(), this, body);

    }


    void addProduct(String path, String title, String url, String price, int currency) {
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

        CommonRequest.AddProduct addPortfolio = new CommonRequest.AddProduct();
        addPortfolio.setUrl(url);
        addPortfolio.setPrice(price);
        addPortfolio.setCurrency(currency);
        addPortfolio.setTitle(title);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_ADD_PRODUCT, addPortfolio.toString(), this, body);

    }

    void updateProduct(String path, String title, String url, String price, int currency, int id, int status) {
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

        CommonRequest.UpdateProduct addPortfolio = new CommonRequest.UpdateProduct();
        addPortfolio.setUrl(url);
        addPortfolio.setPrice(price);
        addPortfolio.setCurrency(currency);
        addPortfolio.setTitle(title);
        addPortfolio.setId(id);
        if (status != 0) {
            addPortfolio.setPublic_status(status);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_UPDATE_PRODUCT, addPortfolio.toString(), this, body);

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        switch (urlEndPoint) {
            case API_GET_STORES: {
                GetStores gigCategories = GetStores.getStores(decryptedData);
                if (gigCategories != null) {
                    getStoreDataList().postValue(gigCategories);
                }
                break;
            }
            case API_GET_PRODUCT: {
                GetProduct gigCategories = GetProduct.getProducts(decryptedData);
                if (gigCategories != null) {
                    getProductDataList().postValue(gigCategories);
                }
                break;
            }
            case API_ADD_PRODUCT:
            case API_UPDATE_PRODUCT:
                getProduct();
                getSaveCompanyProgress().postValue(0);
                getSaveCompanyProgress().postValue(11);
                activity.toastMessage(message);
                break;
            case API_ADD_STORES:
            case API_EDIT_STORES:
                getStores();
                getSaveCompanyProgress().postValue(0);
                getSaveCompanyProgress().postValue(11);
                activity.toastMessage(message);
                break;
            case API_DELETE_STORES:
                getStores();
                getSaveCompanyProgress().postValue(2);
                activity.toastMessage(message);
                break;
            case API_DELETE_PRODUCT:
                getProduct();
                getSaveCompanyProgress().postValue(2);
                activity.toastMessage(message);
                break;
            case API_REORDER_STORES:
            case API_REORDER_PRODUCT:
                activity.toastMessage(message);
                break;

        }
        activity.disableEnableTouch(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equals(API_ADD_STORES) || urlEndPoint.equals(API_EDIT_STORES) ||
                urlEndPoint.equals(API_ADD_PRODUCT) || urlEndPoint.equals(API_UPDATE_PRODUCT)) {
            getSaveCompanyProgress().postValue(0);
        } else if (urlEndPoint.equals(API_GET_STORES)) {
            getStoreDataList().postValue(null);
        } else if (urlEndPoint.equals(API_GET_PRODUCT)) {
            getStoreDataList().postValue(null);
        }
        activity.disableEnableTouch(false);
    }

    public void getStores() {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_STORES, null, false, this);
    }

    public void getProduct() {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_PRODUCT, null, false, this);
    }
}
