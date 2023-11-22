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
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.Objects;

import static com.nojom.util.Constants.API_ADD_WITHDRAWALS;

public class WithdrawMoneyActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    private MutableLiveData<Boolean> onClickDialog = new MutableLiveData<>();

    public MutableLiveData<Boolean> getOnClickDialog() {
        return onClickDialog;
    }

    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;

    public WithdrawMoneyActivityVM() {

    }

    void addWithdraw(BaseActivity activity, int accountId, double withdrawBalance) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;
        getIsShowProgress().postValue(true);

        CommonRequest.AddWithdraw addWithdraw = new CommonRequest.AddWithdraw();
        addWithdraw.setAmount(withdrawBalance);
        addWithdraw.setPayment_account_id(accountId);
        addWithdraw.setPayment_platform_id(3);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_WITHDRAWALS, addWithdraw.toString(), true, this);
    }

    private void thanksForWithdrawDialog(BaseActivity activity) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_withdraw_done);
        dialog.setCancelable(true);

        TextView tvDone = dialog.findViewById(R.id.tv_done);

        tvDone.setOnClickListener(v -> dialog.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);

        dialog.setOnDismissListener(dialog1 -> getOnClickDialog().postValue(true));
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        thanksForWithdrawDialog(activity);
        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
        Utils.trackFirebaseEvent(activity, "Withdraw_Balance_Failed");
    }
}
