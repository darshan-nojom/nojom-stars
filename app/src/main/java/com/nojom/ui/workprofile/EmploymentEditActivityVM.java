package com.nojom.ui.workprofile;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.nojom.util.Constants.API_ADD_EXPERIENCE;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityEmploymentEditBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.Work;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmploymentEditActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private List<Work> workList;
    private MutableLiveData<List<Work>> mWorkDataList = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<List<Work>> getWorkDataList() {
        return mWorkDataList;
    }

    private String expId, companyName, jobTitle, startDate, endDate, experience, currentWorking;

    public EmploymentEditActivityVM() {
    }

    void init(BaseActivity activity) {
        this.activity = activity;
        initData();
    }

    private void initData() {
        workList = new ArrayList<>();
        try {
            ProfileResponse profileData = Preferences.getProfileData(activity);
            if (profileData != null && profileData.experiences != null && profileData.experiences.size() > 0) {
                for (ProfileResponse.Experiences experiences : profileData.experiences) {
                    if (!activity.isEmpty(experiences.companyName)) {
                        Work model = new Work();
                        model.id = experiences.id;
                        model.company = experiences.companyName;
                        model.jobTitle = experiences.service.nameApp;
                        model.isCurrentlyWorking = experiences.isCurrent;
                        model.experience = Utils.getExperienceLevel(experiences.length);
                        if (!activity.isEmpty(experiences.startDate)) {
                            model.startYear = Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM yyyy", experiences.startDate);
                        } else {
                            model.startYear = "";
                        }

                        if (!activity.isEmpty(experiences.endDate)) {
                            model.endYear = Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM yyyy", experiences.endDate);
//                            model.isCurrentlyWorking = 0;
                        } else {
                            model.endYear = "";
//                            model.isCurrentlyWorking = 1;
                        }
                        workList.add(model);
                    }
                }
            }

            if (workList.size() == 0) {
                workList.add(new Work());
            }
            getWorkDataList().postValue(workList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void addWork() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(true);

        CommonRequest.AddExperience addExperience = new CommonRequest.AddExperience();
        addExperience.setCompany_name(companyName);
        addExperience.setEnd_date(endDate);
        addExperience.setLevel(experience);
        addExperience.setService_category_id(jobTitle);
        addExperience.setStart_date(startDate);
        addExperience.setIs_current(currentWorking);
        addExperience.setExperience_id(expId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_EXPERIENCE, addExperience.toString(), true, this);
    }


    boolean isValid(ActivityEmploymentEditBinding binding) {
        StringBuilder expIdBuilder = null;
        StringBuilder companyBuilder = null;
        StringBuilder serviceIdBuilder = null;
        StringBuilder startDateBuilder = null;
        StringBuilder endDateBuilder = null;
        StringBuilder levelBuilder = null;
        StringBuilder isCurrentBuilder = null;

        for (int i = 0; i < workList.size(); i++) {
            View view = binding.rvWork.getChildAt(i);
            EditText tvCompany = view.findViewById(R.id.et_company);
            TextView tvJobTitle = view.findViewById(R.id.tv_job_title);
            TextView tvExperience = view.findViewById(R.id.tv_experience);
            TextView tvStartYear = view.findViewById(R.id.tv_start_year);
            TextView tvEndYear = view.findViewById(R.id.tv_end_year);
            CheckBox chkWorking = view.findViewById(R.id.chk_working);

            if (activity.isEmpty(tvCompany.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_enter_company_name));
                return false;
            }
            if (activity.isEmpty(tvJobTitle.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_select_job));
                return false;
            }
            if (activity.isEmpty(tvExperience.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_select_experience));
                return false;
            }
            if (activity.isEmpty(tvStartYear.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_select_start_date));
                return false;
            }
            if (!chkWorking.isChecked() && activity.isEmpty(tvEndYear.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_select_end_date));
                return false;
            }

            expIdBuilder = (expIdBuilder == null ? new StringBuilder() : expIdBuilder.append(",")).append(workList.get(i).id);

            companyBuilder = (companyBuilder == null ? new StringBuilder() : companyBuilder.append(",")).append(tvCompany.getText().toString());
            serviceIdBuilder = (serviceIdBuilder == null ? new StringBuilder() : serviceIdBuilder.append(","))
                    .append(Utils.getServiceId(activity, tvJobTitle.getText().toString()));
            String sDate, eDate;
            int syear = 0, smonth = 0, eyear = 0, emonth = 0;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
            Date fDate, tDate;
            try {
                fDate = dateFormat.parse(tvStartYear.getText().toString());
                smonth = Integer.parseInt((String) DateFormat.format("MM", fDate));
                syear = Integer.parseInt((String) DateFormat.format("yyyy", fDate));
                if (!chkWorking.isChecked()) {
                    tDate = dateFormat.parse(tvEndYear.getText().toString());
                    emonth = Integer.parseInt((String) DateFormat.format("MM", tDate));
                    eyear = Integer.parseInt((String) DateFormat.format("yyyy", tDate));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sDate = syear + "/" + (smonth > 9 ? smonth : ("0" + smonth)) + "/12";
            if (eyear == 0 && emonth == 0) {
                eDate = "";
            } else {
                eDate = eyear + "/" + (emonth > 9 ? emonth : ("0" + emonth)) + "/12";
            }
            startDateBuilder = (startDateBuilder == null ? new StringBuilder() : startDateBuilder.append(",")).append(sDate);
            endDateBuilder = (endDateBuilder == null ? new StringBuilder() : endDateBuilder.append(",")).append(eDate);
            int level = Utils.getExperienceLevel(tvExperience.getText().toString());
            levelBuilder = (levelBuilder == null ? new StringBuilder() : levelBuilder.append(",")).append(level);
            isCurrentBuilder = (isCurrentBuilder == null ? new StringBuilder() : isCurrentBuilder.append(",")).append(chkWorking.isChecked() ? "1" : "0");
        }
        expId = expIdBuilder != null ? expIdBuilder.toString() : "";
        companyName = companyBuilder != null ? companyBuilder.toString() : "";
        jobTitle = serviceIdBuilder != null ? serviceIdBuilder.toString() : "";
        startDate = startDateBuilder != null ? startDateBuilder.toString() : "";
        endDate = endDateBuilder != null ? endDateBuilder.toString() : "";
        experience = levelBuilder != null ? levelBuilder.toString() : "";
        currentWorking = isCurrentBuilder != null ? isCurrentBuilder.toString() : "";
        return true;
    }


    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.changes_want_be_save));
        builder.setCancelable(false);

        builder.setPositiveButton(
                activity.getString(R.string.Ok),
                (dialog, id) -> {
                    dialog.cancel();
                    activity.setResult(RESULT_CANCELED);
                    activity.finish();
                });

        builder.setNegativeButton(
                activity.getString(R.string.cancel),
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        activity.setResult(RESULT_OK);
        activity.finish();
        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
    }
}
