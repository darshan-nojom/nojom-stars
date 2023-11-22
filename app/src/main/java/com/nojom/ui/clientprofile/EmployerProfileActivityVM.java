package com.nojom.ui.clientprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.BlockUserResponse;
import com.nojom.model.ClientReviews;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.nojom.util.Constants.API_BLOCK_USER;
import static com.nojom.util.Constants.API_CLIENT_REVIEW;
import static com.nojom.util.Constants.API_UNBLOCK_USER;

public class EmployerProfileActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;

    private List<ClientReviews.Data> reviews;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> isBlockStatus = new MutableLiveData<>();

    public MutableLiveData<Integer> getIsBlockStatus() {
        return isBlockStatus;
    }

    private MutableLiveData<List<ClientReviews.Data>> reviewsList = new MutableLiveData<>();

    public MutableLiveData<List<ClientReviews.Data>> getReviewsList() {
        return reviewsList;
    }

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    void init(BaseActivity activity) {
        this.activity = activity;

    }


    void getClientReview(int page, int clientId) {
        if (!activity.isNetworkConnected())
            return;

        if (page == 1) {
            reviews = new ArrayList<>();
            getIsShowProgress().postValue(true);
        }

        CommonRequest.ClientReview clientProfile = new CommonRequest.ClientReview();
        clientProfile.setPage_no(page);
        clientProfile.setClient_id(clientId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_CLIENT_REVIEW, clientProfile.toString(), true, this);
    }

    private Dialog dialogBlock;
    private TextView tvConfirm;
    private CircularProgressBar progressBarBlock;

    void refundPaymentReasonDialog(int profileId) {
        dialogBlock = new Dialog(activity);
        dialogBlock.setTitle(null);
        dialogBlock.setContentView(R.layout.dialog_refund_reason);
        dialogBlock.setCancelable(true);

        tvConfirm = dialogBlock.findViewById(R.id.tv_submit);
        progressBarBlock = dialogBlock.findViewById(R.id.progress_bar);
        TextView tvCancel = dialogBlock.findViewById(R.id.tv_cancel);
        TextView etReason = dialogBlock.findViewById(R.id.edit_reason);
        TextView tvTitle = dialogBlock.findViewById(R.id.tv_title);
        TextView txt1 = dialogBlock.findViewById(R.id.txt1);
        RadioGroup radioGroup = dialogBlock.findViewById(R.id.radioGroup);
        RadioButton rb1 = dialogBlock.findViewById(R.id.rb_inappropriate);
        RadioButton rb2 = dialogBlock.findViewById(R.id.rb_irrelevant);
        RadioButton rb4 = dialogBlock.findViewById(R.id.rb_other);
        RadioButton rb3 = dialogBlock.findViewById(R.id.rb_scam);
        radioGroup.clearCheck();

        tvTitle.setText(activity.getString(R.string.whats_wrong_with_this_profile));
        txt1.setText(activity.getString(R.string.help_us_know_why_you_are_reporting_this_profile_dont_worry_your_identity_is_safe_with_us));
        rb1.setText(activity.getString(R.string.scammer));
        rb2.setText(activity.getString(R.string.share_personal_contact_and_payment_details));
        rb3.setText(activity.getString(R.string.blackmailing_and_harassment));
        rb4.setText(activity.getString(R.string.others));

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = group.findViewById(checkedId);
            if (null != rb) {
                if (rb.getText().equals("Others")) {
                    etReason.setVisibility(VISIBLE);
                } else {
                    etReason.setVisibility(GONE);
                }
            }
        });

        tvConfirm.setOnClickListener(v -> {
            RadioButton rb = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            String selectedReason = "";
            boolean isOtherSelect = false;
            if (rb != null && !TextUtils.isEmpty(rb.getText())) {
                if (rb.getText().equals(activity.getString(R.string.other))) {
                    selectedReason = etReason.getText().toString();
                    isOtherSelect = true;
                } else {
                    selectedReason = rb.getText().toString();
                    isOtherSelect = false;
                }
            }
            if (TextUtils.isEmpty(selectedReason)) {
                activity.toastMessage(activity.getString(R.string.please_select_reason));
                return;
            }

            reportUser(selectedReason, isOtherSelect, profileId);
        });

        tvCancel.setOnClickListener(v -> {
            radioGroup.clearCheck();
            dialogBlock.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogBlock.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialogBlock.show();
        dialogBlock.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBlock.getWindow().setAttributes(lp);
    }

    void reportUser(String reason, boolean isOtherSelect, int profileId) {
        if (!activity.isNetworkConnected())
            return;

        activity.isClickableView = true;
        progressBarBlock.setVisibility(VISIBLE);
        tvConfirm.setVisibility(View.INVISIBLE);

        CommonRequest.BlockUser blockUser = new CommonRequest.BlockUser();
        blockUser.setOther(isOtherSelect ? 1 : 0);
        blockUser.setReason(reason);
        blockUser.setReported_user(profileId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_BLOCK_USER, blockUser.toString(), true, this);

    }

    void unBlockUser(int profileId) {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.UnBlockUser unBlockUser = new CommonRequest.UnBlockUser();
        unBlockUser.setReported_user(profileId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UNBLOCK_USER, unBlockUser.toString(), true, this);
    }

    void showUnblockDialog(int profileId) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_delete_project);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        tvMessage.setText(activity.getString(R.string.are_you_sure_want_to_unblock_the_user));

        tvCancel.setText(activity.getString(R.string.no));
        tvChatnow.setText(activity.getString(R.string.yes));
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            unBlockUser(profileId);
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
        activity.isClickableView = false;
        if (urlEndPoint.equalsIgnoreCase(API_BLOCK_USER)) {
            activity.failureError(msg);
            BlockUserResponse blockUserResponse = BlockUserResponse.getBlockUnblockUser(decryptedData);
            if (blockUserResponse != null && !TextUtils.isEmpty(blockUserResponse.profile_id)) {
                getIsBlockStatus().postValue(1);
            }
            progressBarBlock.setVisibility(GONE);
            tvConfirm.setVisibility(VISIBLE);
            dialogBlock.dismiss();
        } else if (urlEndPoint.equalsIgnoreCase(API_UNBLOCK_USER)) {
            activity.failureError(msg);
            BlockUserResponse blockUserResponse = BlockUserResponse.getBlockUnblockUser(decryptedData);
            if (blockUserResponse != null && !TextUtils.isEmpty(blockUserResponse.profile_id)) {
                getIsBlockStatus().postValue(0);
            }
        } else {
            if (urlEndPoint.equalsIgnoreCase(API_CLIENT_REVIEW)) {
                List<ClientReviews.Data> review = ClientReviews.getClientReview(decryptedData);
                if (review != null) {
                    reviews.addAll(review);
                    getReviewsList().postValue(reviews);
                }

                getIsShowProgress().postValue(false);
            }
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        activity.isClickableView = false;
        getIsShowProgress().postValue(false);
        if (urlEndPoint.equalsIgnoreCase(API_BLOCK_USER)) {
            progressBarBlock.setVisibility(GONE);
            tvConfirm.setVisibility(VISIBLE);
        }
    }
}
