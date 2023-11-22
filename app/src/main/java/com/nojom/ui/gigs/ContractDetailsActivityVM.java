package com.nojom.ui.gigs;

import static com.nojom.util.Constants.API_CONTRACT_DETAIL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.ContractDetails;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.RatingBar;
import com.nojom.util.Utils;

import java.util.Objects;

public class ContractDetailsActivityVM extends ViewModel implements APIRequest.JWTRequestResponseListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private ContractDetails projectData;
    private int RC_RATING = 10101;
    private boolean isNeedToRefresh;
    private MutableLiveData<Integer> updateTab = new MutableLiveData<>();
    private MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    private MutableLiveData<Boolean> updatePager = new MutableLiveData<>();
    private MutableLiveData<Boolean> needToRefresh = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<ContractDetails> mutableProjectData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Boolean> getRefresh() {
        return refresh;
    }

    public MutableLiveData<Integer> getUpdateTab() {
        return updateTab;
    }

    public MutableLiveData<Boolean> getUpdatePager() {
        return updatePager;
    }

    public MutableLiveData<Boolean> getNeedToRefresh() {
        return needToRefresh;
    }

    public MutableLiveData<ContractDetails> getMutableProjectData() {
        return mutableProjectData;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }
//
//    void getProjectById(boolean isNeedToRefresh, int projectId) {
//
////        getUpdateUi().postValue(false);
//
//        if (!activity.isNetworkConnected()) {
//            return;
//        }
//
//        getIsShowProgress().postValue(true);
//        this.isNeedToRefresh = isNeedToRefresh;
//        CommonRequest.JobDetail jobDetail = new CommonRequest.JobDetail();
//        jobDetail.setJob_post_id(projectId);
//
//        APIRequest apiRequest = new APIRequest();
//        apiRequest.makeAPIRequest(activity, API_JOB_DETAIL, jobDetail.toString(), true, this);
//
//    }

    void getProjectById(boolean isNeedToRefresh, int contractId) {

        if (!activity.isNetworkConnected()) {
            return;
        }
        getIsShowProgress().postValue(true);
        this.isNeedToRefresh = isNeedToRefresh;

        String url = API_CONTRACT_DETAIL + contractId;
        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, url, false, null);
    }

    ContractDetails getProjectData() {
        return projectData;
    }

    public void setProjectData(ContractDetails projectData) {
        this.projectData = projectData;
    }

    void giveRatingDialog() {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_rate_me);
        dialog.setCancelable(true);

        TextView tvReleasedPayment = dialog.findViewById(R.id.tv_user_release_payment);
        TextView tvHowsUser = dialog.findViewById(R.id.tv_hows_user);
        RatingBar ratingBar = dialog.findViewById(R.id.ratingbar);
        TextView tvNo = dialog.findViewById(R.id.tv_no);

        String username = activity.getProperName(projectData.clientDetails.first_name, projectData.clientDetails.last_name, projectData.clientDetails.username);

        tvReleasedPayment.setText(activity.getString(R.string.user_released_the_payment, username));
        String s = activity.getString(R.string.how_was_user, username);
        String[] words = {username};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvHowsUser.setText(Utils.getBoldString(activity, s, fonts, null, words));

        tvNo.setOnClickListener(v -> dialog.dismiss());

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            dialog.dismiss();
            Intent i = new Intent(activity, ClientGigReviewActivity.class);
            i.putExtra(Constants.USER_DATA, projectData);
            activity.startActivityForResult(i, RC_RATING);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

//    private Dialog dialogCancelBid;
//
//    void showCancelBidDialog(int id) {
//        dialogCancelBid = new Dialog(activity);
//        dialogCancelBid.setTitle(null);
//        dialogCancelBid.setContentView(R.layout.dialog_close_project);
//        dialogCancelBid.setCancelable(true);
//
//        TextView tvTitle = dialogCancelBid.findViewById(R.id.tv_title);
//        TextView tvBudget = dialogCancelBid.findViewById(R.id.tv_budget);
//        TextView tvBidCount = dialogCancelBid.findViewById(R.id.tv_bid_count);
//        TextView txtCancelBid = dialogCancelBid.findViewById(R.id.tv_close_project);
//        TextView tvCancel = dialogCancelBid.findViewById(R.id.tv_cancel);
//        ProgressBar cancelBidProgress = dialogCancelBid.findViewById(R.id.progress_bar);
//
//        tvTitle.setText(projectData.gigPackageName);
////        tvBidCount.setText("$"+projectData.totalPrice);
//        tvBudget.setText(String.format("$%d", projectData.totalPrice));
//
//        tvCancel.setOnClickListener(v -> dialogCancelBid.dismiss());
//
//        txtCancelBid.setOnClickListener(v -> {
//            //cancelBid(id);
//        });
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(Objects.requireNonNull(dialogCancelBid.getWindow()).getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.gravity = Gravity.TOP;
//        dialogCancelBid.show();
//        dialogCancelBid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogCancelBid.getWindow().setAttributes(lp);
//    }

//    private void cancelBid(int contractId) {
//        if (!activity.isNetworkConnected())
//            return;
//
//        showCancelBidVisibility(View.VISIBLE, View.INVISIBLE, true);
//
//        HashMap<String, RequestBody> map = new HashMap<>();
//
//        RequestBody status = RequestBody.create(String.valueOf(contractId), MultipartBody.FORM);
//        RequestBody reason = RequestBody.create("", MultipartBody.FORM);
//        map.put("contractID", status);
//        map.put("cancelReason", reason);
//
//        APIRequest apiRequest = new APIRequest();
//        apiRequest.apiRequestBodyJWT(this, activity, API_GET_GIG_LIST, map);
//
//    }

//    @Override
//    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
//        if (urlEndPoint.equalsIgnoreCase(API_JOB_DETAIL)) {
//            getIsShowProgress().postValue(false);
//            ContractDetails project = ContractDetails.getContractDetails(decryptedData);
//
//        } else {
//            activity.gotoMainActivity(Constants.TAB_HOME);
//            showCancelBidVisibility(View.GONE, View.VISIBLE, false);
//            dialogCancelBid.dismiss();
//        }
////        activity.hideProgress();
//
//    }

//    @Override
//    public void onResponseError(Throwable t, String urlEndPoint) {
////        activity.hideProgress();
//        getIsShowProgress().postValue(false);
//        showCancelBidVisibility(View.GONE, View.VISIBLE, false);
//    }

//    private void showCancelBidVisibility(int visibilityShow, int visibilityHide, boolean isTouch) {
//        if (cancelBidProgress != null) {
//            cancelBidProgress.setVisibility(visibilityShow);
//        }
//        if (txtCancelBid != null) {
//            txtCancelBid.setVisibility(visibilityHide);
//        }
//        activity.disableEnableTouch(isTouch);
//    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        ContractDetails project = ContractDetails.getContractDetails(responseBody);

        activity.isClickableView = false;
        if (project != null) {
            getUpdatePager().postValue(true);

            projectData = project;
            getMutableProjectData().postValue(projectData);

            activity.runOnUiThread(() -> {
                getUpdateTab().postValue(projectData.gigStateID);
                getNeedToRefresh().postValue(isNeedToRefresh);
            });

        } else {
            getRefresh().postValue(true);
            getUpdatePager().postValue(false);
        }
        getIsShowProgress().postValue(false);
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        activity.isClickableView = false;
        getIsShowProgress().postValue(false);
    }
}
