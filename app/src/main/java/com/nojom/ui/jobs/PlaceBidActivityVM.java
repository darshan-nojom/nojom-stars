package com.nojom.ui.jobs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.ProjectByID;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nojom.util.Constants.API_ADD_BID;
import static com.nojom.util.Constants.API_EDIT_BID;

public class PlaceBidActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<Boolean> validateError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> getValidateError() {
        return validateError;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    void init(BaseActivity activity) {
        this.activity = activity;
    }

    void placeBid(ProjectByID projectData, String amount, boolean isEdit, String bidText, String currency, String deadlineType, String deadlineValue) {
        if (!activity.isNetworkConnected())
            return;

        getIsLoading().postValue(true);

        double bidDollarCharges = projectData.jobPostCharges != null ? projectData.jobPostCharges.bidDollarCharges : 0;
        double bidPercFee = projectData.jobPostCharges != null ? projectData.jobPostCharges.bidPercentCharges : 0;
        amount = amount.replaceAll(",", "");
        double percentage = (Double.parseDouble(amount) * bidPercFee) / 100;
        double bidCharges = Math.max(percentage, bidDollarCharges);

        Log.d("TTT", "percentage...." + percentage);
        Log.d("TTT", "bidCharges...." + bidCharges);

        if (isEdit) {

            CommonRequest.EditBid editBid = new CommonRequest.EditBid();
            editBid.setAmount(amount);
            editBid.setBid_charges(bidCharges + "");
            editBid.setJob_post_bid_id(projectData.jobPostBids.id + "");
            editBid.setDeadline_type(deadlineType);
            editBid.setDeadline_value(deadlineValue);
            editBid.setMessage(bidText);

            APIRequest apiRequest = new APIRequest();
            apiRequest.makeAPIRequest(activity, API_EDIT_BID, editBid.toString(), true, this);

        } else {

            CommonRequest.AddBid addBid = new CommonRequest.AddBid();
            addBid.setAmount(amount);
            addBid.setBid_charges(bidCharges + "");
            addBid.setCurrency(currency);
            addBid.setDeadline_type(deadlineType);
            addBid.setDeadline_value(deadlineValue);
            addBid.setJob_post_id(projectData.id + "");
            addBid.setMessage(bidText);

            APIRequest apiRequest = new APIRequest();
            apiRequest.makeAPIRequest(activity, API_ADD_BID, addBid.toString(), true, this);
        }
    }


    boolean isValid(String amount, String deadlineValue, String bidText, ProjectByID projectData) {
        if (activity.isEmpty(amount)) {
            activity.validationError(activity.getString(R.string.please_enter_bid_amount));
            return false;
        }

        if (amount.startsWith(".")) {
            activity.toastMessage(activity.getString(R.string.an_entered_amount_is_invalid));
            return false;
        }

        if (projectData.jobPayTypeId != null && projectData.jobPayTypeId != 5/*Free*/) {
            if (amount.equals("0")) {
                activity.validationError(activity.getString(R.string.amount_should_not_be_zero));
                return false;
            }
            if (amount.startsWith("00")) {
                activity.validationError(activity.getString(R.string.amount_should_not_start_with_zero));
                return false;
            }
        }

        if (projectData.jobPayTypeId != null && projectData.jobPayTypeId != 5/*Free*/) {
            try {
                if (Double.parseDouble(amount.replaceAll(",", "")) < 9) {
                    activity.validationError(activity.getCurrency().equals("SAR") ? activity.getString(R.string.minimum_bid_amount_should_be_nine_sar)
                            : activity.getString(R.string.minimum_bid_amount_should_be_nine));
                    return false;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (projectData.jobPayTypeId != null && projectData.jobPayTypeId != 5/*Free*/) {
            if (projectData.jobPostCharges != null && !activity.isEmpty(amount) && Double.parseDouble(amount.replaceAll(",", "")) < projectData.jobPostCharges.bidDollarCharges) {
                getValidateError().postValue(true);
                return false;
            }
        }

        if (activity.isEmpty(deadlineValue)) {
            activity.validationError(activity.getString(R.string.please_enter_delivery_time));
            return false;
        }

        if (projectData.jobPayTypeId != null && projectData.jobPayTypeId != 5/*Free*/) {
            if (deadlineValue.equals("0") || deadlineValue.equals("00") || deadlineValue.equals("000")) {
                activity.validationError(activity.getString(R.string.please_enter_valid_delivery_time));
                return false;
            }
        }

        if (activity.isEmpty(bidText)) {
            activity.validationError(activity.getString(R.string.please_describe_your_bid));
            return false;
        }

        Pattern pattern = Pattern.compile("-?\\d+");
        Matcher m = pattern.matcher(bidText);
        while (m.find()) {
            if (m.group().length() >= 6 && m.group().length() <= 14) {
                activity.validationError(activity.getString(R.string.you_cannot_enter_number));
                return false;
            }
        }
        pattern = Pattern.compile("([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+)");
        m = pattern.matcher(bidText);
        while (m.find()) {
            activity.validationError(activity.getString(R.string.you_cannot_enter_email));
            return false;
        }
        return true;
    }

    private void biddingDoneDialog() {
        try {
            final Dialog dialog = new Dialog(activity, R.style.Theme_Design_BottomSheetDialog);
            dialog.setTitle(null);
            dialog.setContentView(R.layout.dialog_bidding_done);
            dialog.setCancelable(true);

            TextView tvViewBidding = dialog.findViewById(R.id.tv_view_bidding);

            tvViewBidding.setOnClickListener(v -> dialog.dismiss());

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.TOP;
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setAttributes(lp);
            dialog.setOnDismissListener(dialog1 -> activity.gotoMainActivity(Constants.TAB_HOME, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        getIsLoading().postValue(false);
        if (urlEndPoint.equalsIgnoreCase(API_ADD_BID)) {
            biddingDoneDialog();
            Utils.trackFirebaseEvent(activity, "Place_Bid_Success");
        } else if (urlEndPoint.equalsIgnoreCase(API_EDIT_BID)) {
            activity.finish();
            Utils.trackFirebaseEvent(activity, "Edit_Bid_Success");
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsLoading().postValue(false);
    }
}
