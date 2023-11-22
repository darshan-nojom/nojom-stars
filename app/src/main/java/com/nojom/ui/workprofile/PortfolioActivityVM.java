package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.Portfolios;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.nojom.util.Constants.API_ADD_PORTFOLIO;
import static com.nojom.util.Constants.API_DELETE_PORTFOLIO;
import static com.nojom.util.Constants.API_DELETE_PORTFOLIO_FILE;
import static com.nojom.util.Constants.API_EDIT_PORTFOLIO;

public class PortfolioActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private int fileItemPos;
    private List<Portfolios.PortfolioFiles> attachmentFiles;
    private MutableLiveData<List<Portfolios.PortfolioFiles>> listMutableLiveData;
    private MutableLiveData<Portfolios> mutablePortfolioData;
    private MutableLiveData<Boolean> resetIds = new MutableLiveData<>();
    private MutableLiveData<Integer> deletePortfolioItem = new MutableLiveData<>();
    private MutableLiveData<Boolean> addPortfolioFailed = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgressFile = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgressDelete = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgressDelete() {
        return isShowProgressDelete;
    }

    public MutableLiveData<Boolean> getIsShowProgressFile() {
        return isShowProgressFile;
    }

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Boolean> getAddPortfolioFailed() {
        return addPortfolioFailed;
    }

    public MutableLiveData<Integer> getDeletePortfolioItem() {
        return deletePortfolioItem;
    }

    public MutableLiveData<Boolean> getResetIds() {
        return resetIds;
    }

    public MutableLiveData<Portfolios> getPortfolioData() {
        if (mutablePortfolioData == null) {
            mutablePortfolioData = new MutableLiveData<>();
        }
        return mutablePortfolioData;
    }

    public MutableLiveData<List<Portfolios.PortfolioFiles>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public PortfolioActivityVM() {
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
        attachmentFiles = new ArrayList<>();
    }

    public void loadData(Portfolios portfolioData) {
        if (portfolioData != null && portfolioData.portfolioFiles != null && portfolioData.portfolioFiles.size() > 0) {
            attachmentFiles.addAll(portfolioData.portfolioFiles);
        }
        getListMutableLiveData().postValue(attachmentFiles);
    }

    void addPortfolio(File file, int fileType, Portfolios portfolioData, String titleTxt, boolean isShow) {
        if (!activity.isNetworkConnected())
            return;

        if (isShow) {
            getIsShowProgress().postValue(true);
        } else {
            getIsShowProgressFile().postValue(true);
        }

        MultipartBody.Part body = null;
        if (file != null) {
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
        }

        String portfolioId = "";
        if (portfolioData != null && portfolioData.id != 0) {
            portfolioId = String.valueOf(portfolioData.id);
        }

        CommonRequest.AddPortfolio addPortfolio = new CommonRequest.AddPortfolio();
        if (!TextUtils.isEmpty(portfolioId)) {
            addPortfolio.setPortfolio_id(portfolioId);
        }
        addPortfolio.setTitle(titleTxt);
        addPortfolio.setType(String.valueOf(fileType));

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_ADD_PORTFOLIO, addPortfolio.toString(), this, body);

    }

    private String convertToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    void deletePortfolioItem(int portfolioFileId, int itemPos, Portfolios portfolioData) {
        if (!activity.isNetworkConnected())
            return;

//        activity.showProgress();
        fileItemPos = itemPos;

        CommonRequest.DeletePortfolioFile deletePortfolioFile = new CommonRequest.DeletePortfolioFile();
        deletePortfolioFile.setFile_id(portfolioFileId);
        deletePortfolioFile.setPortfolio_id(portfolioData.id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_PORTFOLIO_FILE, deletePortfolioFile.toString(), true, this);
    }

    private void deletePortfolio(Portfolios portfolioData) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgressDelete().postValue(true);

        CommonRequest.DeletePortfolio deletePortfolio = new CommonRequest.DeletePortfolio();
        deletePortfolio.setPortfolio_id(portfolioData.id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_PORTFOLIO, deletePortfolio.toString(), true, this);
    }

    void updatePortfolio(File file, int fileType, Portfolios portfolioData, String titleTxt, int selectedFolioId, boolean isShow) {
        if (!activity.isNetworkConnected())
            return;

        if (isShow) {
            getIsShowProgress().postValue(true);
        } else {
            getIsShowProgressFile().postValue(true);
        }

        MultipartBody.Part body = null;
        if (file != null) {
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
        }

        String portfolioId = "";
        if (portfolioData != null && portfolioData.id != 0) {
            portfolioId = String.valueOf(portfolioData.id);
        }

        CommonRequest.UpdatePortfolio updatePortfolio = new CommonRequest.UpdatePortfolio();
        updatePortfolio.setPortfolio_id(portfolioId);
        updatePortfolio.setTitle(titleTxt);
        updatePortfolio.setType(String.valueOf(fileType));
        updatePortfolio.setSort("0");
        updatePortfolio.setFile_id(String.valueOf(selectedFolioId == -1 ? 0 : selectedFolioId));

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_EDIT_PORTFOLIO, updatePortfolio.toString(), this, body);

    }

    void setAsMainPhoto(Portfolios portfolioData, String titleTxt, int selectedFolioId) {
        if (!activity.isNetworkConnected())
            return;

        String portfolioId = "";
        if (portfolioData != null && portfolioData.id != 0) {
            portfolioId = String.valueOf(portfolioData.id);
        }

        CommonRequest.UpdatePortfolio updatePortfolio = new CommonRequest.UpdatePortfolio();
        updatePortfolio.setPortfolio_id(portfolioId);
        updatePortfolio.setTitle(titleTxt);
        updatePortfolio.setType(String.valueOf(1));
        updatePortfolio.setSort("1");//for set main photo, otherwise 0
        updatePortfolio.setFile_id(String.valueOf(selectedFolioId == -1 ? 0 : selectedFolioId));

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_EDIT_PORTFOLIO, updatePortfolio.toString(), true, this);

    }

    void showDeletePortfolioDialog(Portfolios portfolioData) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        tvMessage.setText(activity.getString(R.string.are_you_sure_want_to_delete_portfolio));

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            deletePortfolio(portfolioData);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_ADD_PORTFOLIO)) {
            Portfolios addPortfolios = Portfolios.getPortfolio(decryptedData);

            if (addPortfolios != null) {
                getPortfolioData().postValue(addPortfolios);
                attachmentFiles.clear();
                attachmentFiles.addAll(addPortfolios.portfolioFiles);
                getListMutableLiveData().postValue(attachmentFiles);

                activity.toastMessage(msg);
            }
            getIsShowProgress().postValue(false);
            getIsShowProgressFile().postValue(false);
        } else if (urlEndPoint.equalsIgnoreCase(API_DELETE_PORTFOLIO)) {//delete portfolio
            activity.toastMessage(msg);
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
            getIsShowProgressDelete().postValue(false);
        } else if (urlEndPoint.equalsIgnoreCase(API_DELETE_PORTFOLIO_FILE) && fileItemPos != -1) {//delete file
            getDeletePortfolioItem().postValue(fileItemPos);
            getResetIds().postValue(true);
            fileItemPos = -1;
        } else if (urlEndPoint.equalsIgnoreCase(API_EDIT_PORTFOLIO)) {

            Portfolios addPortfolios = Portfolios.getPortfolio(decryptedData);

            if (addPortfolios != null) {
                getPortfolioData().postValue(addPortfolios);

                attachmentFiles.clear();
                if (addPortfolios.portfolioFiles != null && addPortfolios.portfolioFiles.size() > 0) {
                    attachmentFiles.addAll(addPortfolios.portfolioFiles);
                }
                getListMutableLiveData().postValue(attachmentFiles);

                getResetIds().postValue(true);
                activity.toastMessage(msg);
            }
            getIsShowProgressFile().postValue(false);
            getIsShowProgress().postValue(false);
        }
//        activity.hideProgress();
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
//        activity.hideProgress();
        if (urlEndPoint.equalsIgnoreCase(API_DELETE_PORTFOLIO)) {
            getIsShowProgressDelete().postValue(false);
        }
        getIsShowProgressFile().postValue(false);
        getIsShowProgress().postValue(false);
    }
}
