package com.nojom.ui.jobs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.nojom.util.Constants.API_ADD_JOB_REPORT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.ProjectByID;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.Objects;

public class JobSummaryActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;

    private MutableLiveData<ProjectByID> dataMutableLiveData;

    public MutableLiveData<ProjectByID> getDataMutableLiveData() {
        if (dataMutableLiveData == null) {
            dataMutableLiveData = new MutableLiveData<>();
        }
        return dataMutableLiveData;
    }

    void init(BaseActivity activity) {
        this.activity = activity;
    }

    void refundPaymentReasonDialog(int projectId) {
        final Dialog dialog = new Dialog(activity);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_refund_reason);
        dialog.setCancelable(true);

        TextView tvConfirm = dialog.findViewById(R.id.tv_submit);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView etReason = dialog.findViewById(R.id.edit_reason);
//        TextView txt1 = dialog.findViewById(R.id.txt1);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = group.findViewById(checkedId);
            if (null != rb) {
                if (rb.getText().equals(activity.getString(R.string.other))) {
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
                activity.toastMessage(isOtherSelect ? "" + activity.getString(R.string.please_enter_reason) : activity.getString(R.string.please_select_reason));
                return;
            }
            dialog.dismiss();
            reportPost(selectedReason, isOtherSelect, projectId);
        });

        tvCancel.setOnClickListener(v -> {
            radioGroup.clearCheck();
            dialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void reportPost(String reason, boolean isOtherSelect, int projectId) {
        if (!activity.isNetworkConnected())
            return;

        activity.isClickableView = false;

        CommonRequest.AddJobReport addJobReport = new CommonRequest.AddJobReport();
        addJobReport.setJob_post_id(projectId);
        addJobReport.setOther(isOtherSelect ? 1 : 0);
        addJobReport.setReason(reason);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_JOB_REPORT, addJobReport.toString(), true, this);

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        activity.isClickableView = false;
        activity.failureError(msg);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        activity.isClickableView = false;
//        activity.hideProgress();
    }
}
