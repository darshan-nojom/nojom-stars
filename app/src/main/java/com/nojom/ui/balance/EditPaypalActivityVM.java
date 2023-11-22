package com.nojom.ui.balance;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.Payment;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.Objects;

import static com.nojom.util.Constants.API_DELETE_PAYMENT_ACCOUNT;
import static com.nojom.util.Constants.API_EDIT_PAYMENT_ACCOUNT;
import static com.nojom.util.Constants.API_VERIFY_PAYMENT_ACCOUNT;

public class EditPaypalActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgressEmail = new MutableLiveData<>();
    private MutableLiveData<Boolean> deleteAccount = new MutableLiveData<>();

    public MutableLiveData<Boolean> getDeleteAccount() {
        return deleteAccount;
    }

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Boolean> getIsShowProgressEmail() {
        return isShowProgressEmail;
    }

    public EditPaypalActivityVM() {

    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }


    void verifyAccount(Payment paymentData) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgressEmail().postValue(true);

        CommonRequest.SendEmailVerif sendEmailVerif = new CommonRequest.SendEmailVerif();
        sendEmailVerif.setPayment_account_id(paymentData.id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_VERIFY_PAYMENT_ACCOUNT, sendEmailVerif.toString(), true, this);
    }

    void isPrimary(int primary, Payment paymentData) {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.EditPaymentAccount editPaymentAccount = new CommonRequest.EditPaymentAccount();
        editPaymentAccount.setIs_primary(primary);
        editPaymentAccount.setPayment_account_id(paymentData.id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_EDIT_PAYMENT_ACCOUNT, editPaymentAccount.toString(), true, this);
    }

    private void deleteAccount(Payment paymentData) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(true);

        CommonRequest.DeleteAccount deleteAccount = new CommonRequest.DeleteAccount();
        deleteAccount.setPayment_account_id(paymentData.id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_PAYMENT_ACCOUNT, deleteAccount.toString(), true, this);
    }


    void showDeleteDialog(Payment paymentData) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_delete_project);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        tvMessage.setText(Utils.fromHtml(activity.getString(R.string.delete_account_text)));

        tvCancel.setText(activity.getString(R.string.no));
        tvChatnow.setText(activity.getString(R.string.yes));
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            deleteAccount(paymentData);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        if (urlEndPoint.equalsIgnoreCase(API_DELETE_PAYMENT_ACCOUNT)) {
            getIsShowProgress().postValue(false);
            getDeleteAccount().postValue(true);
        } else if (urlEndPoint.equalsIgnoreCase(API_VERIFY_PAYMENT_ACCOUNT)) {
            activity.toastMessage(message);
            getIsShowProgressEmail().postValue(false);
        } else if (urlEndPoint.equalsIgnoreCase(API_EDIT_PAYMENT_ACCOUNT)) {
            activity.toastMessage(message);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equalsIgnoreCase(API_DELETE_PAYMENT_ACCOUNT)) {
            getIsShowProgress().postValue(false);
        } else if (urlEndPoint.equalsIgnoreCase(API_VERIFY_PAYMENT_ACCOUNT)) {
            getIsShowProgressEmail().postValue(false);
        } else if (urlEndPoint.equalsIgnoreCase(API_EDIT_PAYMENT_ACCOUNT)) {
            activity.toastMessage(message);
        }
    }
}
