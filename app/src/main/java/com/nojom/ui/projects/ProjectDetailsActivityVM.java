package com.nojom.ui.projects;

import static com.nojom.util.Constants.API_DELETE_BID;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.ProjectByID;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.clientprofile.LeaveReviewActivity;
import com.nojom.util.Constants;
import com.nojom.util.RatingBar;
import com.nojom.util.Utils;

import java.util.Objects;

public class ProjectDetailsActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private ProjectByID projectData;
    private int RC_RATING = 10101;

    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<ProjectByID> mutableProjectData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<ProjectByID> getMutableProjectData() {
        return mutableProjectData;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    /*void getProjectById(boolean isNeedToRefresh, int projectId) {

//        getUpdateUi().postValue(false);

        if (!activity.isNetworkConnected()) {
            return;
        }

        getIsShowProgress().postValue(true);
        this.isNeedToRefresh = isNeedToRefresh;
        CommonRequest.JobDetail jobDetail = new CommonRequest.JobDetail();
        jobDetail.setJob_post_id(projectId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_JOB_DETAIL, jobDetail.toString(), true, this);

    }*/

    ProjectByID getProjectData() {
        return projectData;
    }

    public void setProjectData(ProjectByID projectData) {
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

        String username = activity.getProperName(projectData.clientFirstName, projectData.clientLastName, projectData.clientUsername);

        tvReleasedPayment.setText(activity.getString(R.string.user_released_the_payment, username));
        String s = activity.getString(R.string.how_was_user, username);
        String[] words = {username};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvHowsUser.setText(Utils.getBoldString(activity, s, fonts, null, words));

        tvNo.setOnClickListener(v -> dialog.dismiss());

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            dialog.dismiss();
            Intent i = new Intent(activity, LeaveReviewActivity.class);
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

    private ProgressBar cancelBidProgress;
    private TextView txtCancelBid;
    private Dialog dialogCancelBid;

    void showCancelBidDialog() {
        dialogCancelBid = new Dialog(activity);
        dialogCancelBid.setTitle(null);
        dialogCancelBid.setContentView(R.layout.dialog_close_project);
        dialogCancelBid.setCancelable(true);

        TextView tvTitle = dialogCancelBid.findViewById(R.id.tv_title);
        TextView tvBudget = dialogCancelBid.findViewById(R.id.tv_budget);
        TextView tvBidCount = dialogCancelBid.findViewById(R.id.tv_bid_count);
        txtCancelBid = dialogCancelBid.findViewById(R.id.tv_close_project);
        TextView tvCancel = dialogCancelBid.findViewById(R.id.tv_cancel);
        cancelBidProgress = dialogCancelBid.findViewById(R.id.progress_bar);

        tvTitle.setText(projectData.title);
        if (projectData.bidsCount >= 2 && projectData.bidsCount <= 10) {
            tvBidCount.setText(projectData.bidsCount + " " + activity.getString(R.string.bids_2_10));
        } else {
            tvBidCount.setText(projectData.bidsCount > 1 ? projectData.bidsCount + " " + activity.getString(R.string.bids_1) : projectData.bidsCount + " " + activity.getString(R.string.bid_));
        }

        if (projectData.clientRateId == 0 && projectData.jobBudget != null) {
            tvBudget.setText("$" + projectData.jobBudget);
        } else {
            if (projectData.clientRate != null) {
                if (projectData.clientRate.rangeTo != null && projectData.clientRate.rangeTo != 0) {
                    tvBudget.setText(String.format("$%s - $%s", projectData.clientRate.rangeFrom, projectData.clientRate.rangeTo));
                } else {
                    tvBudget.setText(String.format("$%s", projectData.clientRate.rangeFrom));
                }
            } else if (projectData.jobBudget != null) {
                tvBudget.setText("$" + projectData.jobBudget);
            } else {
                tvBudget.setText(activity.getString(R.string.free));
            }
        }

        tvCancel.setOnClickListener(v -> dialogCancelBid.dismiss());

        txtCancelBid.setOnClickListener(v -> {

            cancelBid();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogCancelBid.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialogCancelBid.show();
        dialogCancelBid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCancelBid.getWindow().setAttributes(lp);
    }

    private void cancelBid() {
        if (!activity.isNetworkConnected())
            return;

        showCancelBidVisibility(View.VISIBLE, View.INVISIBLE, true);

        CommonRequest.DeleteBid deleteBid = new CommonRequest.DeleteBid();
        deleteBid.setJob_post_bid_id(projectData.jobPostBids.id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_BID, deleteBid.toString(), true, this);

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        activity.gotoMainActivity(Constants.TAB_HOME);
        showCancelBidVisibility(View.GONE, View.VISIBLE, false);
        dialogCancelBid.dismiss();
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {

        getIsShowProgress().postValue(false);
        showCancelBidVisibility(View.GONE, View.VISIBLE, false);
    }

    private void showCancelBidVisibility(int visibilityShow, int visibilityHide, boolean isTouch) {
        if (cancelBidProgress != null) {
            cancelBidProgress.setVisibility(visibilityShow);
        }
        if (txtCancelBid != null) {
            txtCancelBid.setVisibility(visibilityHide);
        }
        activity.disableEnableTouch(isTouch);
    }
}
