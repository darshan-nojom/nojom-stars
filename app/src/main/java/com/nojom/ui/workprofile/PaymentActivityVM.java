package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_PAYMENT;
import static com.nojom.util.Constants.API_ADD_PROFILE_VERIF;
import static com.nojom.util.Constants.API_DELETE_BANK;
import static com.nojom.util.Constants.API_UPDATE_PAYMENT;
import static com.nojom.util.Constants.API_UPDATE_PROFILE;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;

import java.io.File;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PaymentActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    private ResponseListener responseListener;

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    private BaseActivity activity;

    private MutableLiveData<Boolean> showProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> showProgressDelete = new MutableLiveData<>();

    public MutableLiveData<Boolean> getShowProgress() {
        return showProgress;
    }

    public MutableLiveData<Boolean> getShowProgressDelete() {
        return showProgressDelete;
    }

    public void addPayment(BaseActivity activity, int editId, String bankid, String benifName, String iban, File file, int primary) {
        if (!activity.isNetworkConnected())
            return;

        getShowProgress().postValue(true);
        this.activity = activity;

        MultipartBody.Part body = null;
        if (file != null) {
            if (file.getAbsolutePath().contains(".jpg") || file.getAbsolutePath().contains(".png")
                    || file.getAbsolutePath().contains(".jpeg")) {
                file = CompressFile.getCompressedImageFile(file);
            }

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
        }

        CommonRequest.AddPayment addPayment = new CommonRequest.AddPayment();
        addPayment.setBank_id(Integer.parseInt(bankid));
        addPayment.setBeneficiary_name(benifName);
        addPayment.setIban("SA" + iban);
        addPayment.setIs_primary(primary);
        if (editId != 0) {
            addPayment.setId(editId);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, editId == 0 ? API_ADD_PAYMENT : API_UPDATE_PAYMENT, addPayment.toString(), this, body);

    }


    public void deleteBank(BaseActivity activity, int editId) {
        if (!activity.isNetworkConnected())
            return;

        getShowProgressDelete().postValue(true);
        this.activity = activity;

        CommonRequest.DeleteBank addPayment = new CommonRequest.DeleteBank();
        if (editId != 0) {
            addPayment.setId(editId);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_BANK, addPayment.toString(), true,this);

    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (responseListener != null) {
            responseListener.onResponseSuccess(null);
        }
        activity.getProfile();
        getShowProgress().postValue(false);
        getShowProgressDelete().postValue(false);
        activity.toastMessage(msg);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (responseListener != null) {
            responseListener.onError();
        }
        getShowProgress().postValue(false);
        getShowProgressDelete().postValue(false);
        activity.toastMessage(message);
    }
}
