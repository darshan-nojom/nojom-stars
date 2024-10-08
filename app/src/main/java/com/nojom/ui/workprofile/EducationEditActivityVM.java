package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityEducationEditBinding;
import com.nojom.model.Education;
import com.nojom.model.ProfileResponse;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.nojom.util.Constants.API_ADD_EDUCATIONS;

public class EducationEditActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private List<Education> educationList;
    private MutableLiveData<List<Education>> mDataList = new MutableLiveData<>();
    private String degree, schoolName, startDate, endDate, levelIds;

    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public EducationEditActivityVM() {

    }

    public MutableLiveData<List<Education>> getDataList() {
        return mDataList;
    }

    void init(BaseActivity activity) {
        this.activity = activity;
        initData();
    }

    private void initData() {
        educationList = new ArrayList<>();

        try {
            ProfileResponse profileData = Preferences.getProfileData(activity);
            if (profileData != null && profileData.educations != null && profileData.educations.size() > 0) {
                for (ProfileResponse.Education data : profileData.educations) {
                    Education model = new Education();
                    model.college = data.schoolName;
                    model.degree = data.degree;
                    model.level = Utils.getEducationLevel(data.level);
                    if (!activity.isEmpty(data.startDate)) {
                        model.startYear = Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM yyyy", data.startDate);
                    } else {
                        model.startYear = "";
                    }

                    if (!activity.isEmpty(data.endDate)) {
                        model.endYear = Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM yyyy", data.endDate);
                    } else {
                        model.endYear = "";
                    }
                    educationList.add(model);
                }
            } else {
                educationList.add(new Education());
            }
            getDataList().postValue(educationList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.setOnProfileLoadListener(this::onProfileLoad);
    }

    void addEducation() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(true);

        CommonRequest.AddEducations addEducations = new CommonRequest.AddEducations();
        addEducations.setDegree(degree);
        addEducations.setEnd_date(endDate);
        addEducations.setLevel(levelIds);
        addEducations.setSchool_name(schoolName);
        addEducations.setStart_date(startDate);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_EDUCATIONS, addEducations.toString(), true, this);
    }

    public void onProfileLoad(ProfileResponse data) {
        activity.finish();
    }

    boolean isValid(ActivityEducationEditBinding binding) {
        StringBuilder degreeBuilder = null;
        StringBuilder schoolBuilder = null;
        StringBuilder startDateBuilder = null;
        StringBuilder endDateBuilder = null;
        StringBuilder levelBuilder = null;

        for (int i = 0; i < educationList.size(); i++) {
            View view = binding.rvEducation.getChildAt(i);
            EditText school = view.findViewById(R.id.et_college);
            EditText degree = view.findViewById(R.id.et_degree);
            TextView tvLevel = view.findViewById(R.id.tv_level);
            TextView tvStartYear = view.findViewById(R.id.tv_start_year);
            TextView tvEndYear = view.findViewById(R.id.tv_end_year);

            if (activity.isEmpty(degree.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_enter_degree));
                return false;
            }
            if (activity.isEmpty(school.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_enter_college));
                return false;
            }
            if (activity.isEmpty(tvLevel.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_select_level));
                return false;
            }
            if (activity.isEmpty(tvStartYear.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_select_start_date));
                return false;
            }
            if (activity.isEmpty(tvEndYear.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_select_end_date));
                return false;
            }

            degreeBuilder = (degreeBuilder == null ? new StringBuilder() : degreeBuilder.append(activity.getString(R.string.dollar))).append(degree.getText().toString());
            schoolBuilder = (schoolBuilder == null ? new StringBuilder() : schoolBuilder.append(activity.getString(R.string.dollar))).append(school.getText().toString());
            String sDate, eDate;
            int sYear = 0, sMonth = 0, eYear = 0, eMonth = 0;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
            Date fDate, tDate;
            try {
                fDate = dateFormat.parse(tvStartYear.getText().toString());
                tDate = dateFormat.parse(tvEndYear.getText().toString());
                sMonth = Integer.parseInt((String) DateFormat.format("MM", fDate));
                sYear = Integer.parseInt((String) DateFormat.format("yyyy", fDate));
                eMonth = Integer.parseInt((String) DateFormat.format("MM", tDate));
                eYear = Integer.parseInt((String) DateFormat.format("yyyy", tDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sDate = sYear + "/" + sMonth + "/12";
            eDate = eYear + "/" + eMonth + "/12";
            startDateBuilder = (startDateBuilder == null ? new StringBuilder() : startDateBuilder.append(",")).append(sDate);
            endDateBuilder = (endDateBuilder == null ? new StringBuilder() : endDateBuilder.append(",")).append(eDate);
            int level = Utils.getEducationLevel(tvLevel.getText().toString());
            levelBuilder = (levelBuilder == null ? new StringBuilder() : levelBuilder.append(",")).append(level);
        }

        degree = degreeBuilder != null ? degreeBuilder.toString() : "";
        schoolName = schoolBuilder != null ? schoolBuilder.toString() : "";
        startDate = startDateBuilder != null ? startDateBuilder.toString() : "";
        endDate = endDateBuilder != null ? endDateBuilder.toString() : "";
        levelIds = levelBuilder != null ? levelBuilder.toString() : "";
        return true;
    }

    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.changes_want_be_save));
        builder.setCancelable(false);

        builder.setPositiveButton(
                activity.getString(R.string.ok),
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
