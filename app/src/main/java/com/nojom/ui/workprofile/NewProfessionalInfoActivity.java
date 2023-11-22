package com.nojom.ui.workprofile;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.ExperiencesAdapter;
import com.nojom.adapter.SkillsAdapter;
import com.nojom.databinding.ActivityProfessionalInfoNewBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.filter.entity.NormalFile;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class NewProfessionalInfoActivity extends BaseActivity implements BaseActivity.OnProfileLoadListener, SkillsAdapter.OnItemClickListener, ExperiencesAdapter.OnItemClickListener {

    private ActivityProfessionalInfoNewBinding binding;
    private NewProfessionalInfoActivityVM professionalInfoActivityVM;
    private static final int REQ_REFRESH = 101;
    private ProfileResponse profileData;
    private String resumeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_professional_info_new);
        binding.setActivity(this);
        professionalInfoActivityVM = ViewModelProviders.of(this).get(NewProfessionalInfoActivityVM.class);
        professionalInfoActivityVM.init(this);
        initData();
    }

    private void initData() {
        setOnProfileLoadListener(this);

        binding.rvEmployment.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEducation.setLayoutManager(new LinearLayoutManager(this));

        binding.rvEmployment.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));
        binding.rvEducation.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));

        binding.llToolbar.imgBack.setOnClickListener(v -> onBackPressed());

        profileData = Preferences.getProfileData(this);
        refreshViews();

        if (profileData != null) {

            if (profileData.profilePublicity != null && profileData.profilePublicity.size() > 0) {

                try {
                    for (ProfileResponse.ProfilePublicity publicity : profileData.profilePublicity) {
                        if (publicity.publicityType.equalsIgnoreCase("headline")) {
                            binding.sgHeadline.setPosition(Integer.parseInt(publicity.status));
                            binding.sgHeadline.setTag(publicity.id);
                        } else if (publicity.publicityType.equalsIgnoreCase("summary")) {
                            binding.sgProfSummary.setPosition(Integer.parseInt(publicity.status));
                            binding.sgProfSummary.setTag(publicity.id);
                        } else if (publicity.publicityType.equalsIgnoreCase("resume")) {
//                            if (isFree()) {
                            binding.sgResume.setPosition(Integer.parseInt(publicity.status));
//                            }
                            binding.sgResume.setTag(publicity.id);
                        } else if (publicity.publicityType.equalsIgnoreCase("hour_rate")) {
                            binding.sgHourlyRate.setPosition(Integer.parseInt(publicity.status));
                            binding.sgHourlyRate.setTag(publicity.id);
                        } else if (publicity.publicityType.equalsIgnoreCase("employment")) {
                            binding.sgEmpHistory.setPosition(Integer.parseInt(publicity.status));
                            binding.sgEmpHistory.setTag(publicity.id);
                        } else if (publicity.publicityType.equalsIgnoreCase("education")) {
                            binding.sgEducation.setPosition(Integer.parseInt(publicity.status));
                            binding.sgEducation.setTag(publicity.id);
                        } else if (publicity.publicityType.equalsIgnoreCase("address")) {
//                            if (isFree()) {
                            binding.sgAddress.setPosition(Integer.parseInt(publicity.status));
//                            }
                            binding.sgAddress.setTag(publicity.id);
                        } else if (publicity.publicityType.equalsIgnoreCase("phone")) {
//                            if (isFree()) {
                            binding.sgPhone.setPosition(Integer.parseInt(publicity.status));
//                            }
                            binding.sgPhone.setTag(publicity.id);
                        } else if (publicity.publicityType.equalsIgnoreCase("email")) {
//                            if (isFree()) {
                            binding.sgEmail.setPosition(Integer.parseInt(publicity.status));
//                            }
                            binding.sgEmail.setTag(publicity.id);
                        } else if (publicity.publicityType.equalsIgnoreCase("website")) {
//                            if (isFree()) {
                            binding.sgWebsite.setPosition(Integer.parseInt(publicity.status));
//                            }
                            binding.sgWebsite.setTag(publicity.id);
                        } else if (publicity.publicityType.equalsIgnoreCase("pro_address")) {
//                            if (isFree()) {
                            binding.sgOffAdd.setPosition(Integer.parseInt(publicity.status));
//                            }
                            binding.sgOffAdd.setTag(publicity.id);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        binding.rvEmployment.setNestedScrollingEnabled(false);
        binding.rvEducation.setNestedScrollingEnabled(false);

        Utils.trackFirebaseEvent(this, "Professional_Info_Screen");

        setupGroups();

        professionalInfoActivityVM.getDataEducation().observe(this, education -> {
//            SkillsAdapter mExpertiseAdapter = new SkillsAdapter(NewProfessionalInfoActivity.this, education, NewProfessionalInfoActivity.this, false);
//            binding.rvEducation.setAdapter(mExpertiseAdapter);

            if (education != null && education.size() > 0) {
                ExperiencesAdapter educationAdapter = new ExperiencesAdapter(NewProfessionalInfoActivity.this, education, NewProfessionalInfoActivity.this, false);
                binding.rvEducation.setAdapter(educationAdapter);
                binding.rvEducation.setVisibility(View.VISIBLE);
                binding.txtEducationPh.setVisibility(View.GONE);
            } else {
                binding.rvEducation.setVisibility(View.GONE);
                binding.txtEducationPh.setVisibility(View.VISIBLE);
            }
        });

        professionalInfoActivityVM.getDataEmployment().observe(this, experiences -> {
            if (experiences != null && experiences.size() > 0) {
                ExperiencesAdapter experiencesAdapter = new ExperiencesAdapter(NewProfessionalInfoActivity.this, experiences, NewProfessionalInfoActivity.this, true);
                binding.rvEmployment.setAdapter(experiencesAdapter);
                binding.rvEmployment.setVisibility(View.VISIBLE);
                binding.txtEmpPh.setVisibility(View.GONE);
            } else {
                binding.rvEmployment.setVisibility(View.GONE);
                binding.txtEmpPh.setVisibility(View.VISIBLE);
            }

        });

        professionalInfoActivityVM.getIsShowWebView().observe(this, isShowWebview -> {
            if (isShowWebview) {
//                startWebView(resumeUrl);
                viewFile(resumeUrl);
            }
        });

        professionalInfoActivityVM.getIsShowProgress().observe(this, isShow -> {
            disableEnableTouch(isShow);
            if (isShow) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.tvResume.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                binding.tvResume.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.attachment, 0);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    public boolean isFree() {
        return profileData != null && profileData.isJobPostFree != null && profileData.isJobPostFree.equalsIgnoreCase("1");
    }

    @Override
    public void onBackPressed() {
        if (binding.webview.getVisibility() == View.VISIBLE) {
            binding.webview.setVisibility(View.GONE);
            binding.llTopSkill.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
            finishToRight();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<NormalFile> docPaths = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    if (docPaths != null && docPaths.size() > 0) {
                        Log.e("Doc Path == > ", docPaths.get(0).getPath());
                        professionalInfoActivityVM.updateResume(new File(docPaths.get(0).getPath()), "");
                    } else {
                        toastMessage(getString(R.string.file_not_selected));
                    }
                }
                break;
            case REQ_REFRESH:
                if (resultCode == Activity.RESULT_OK) {
                    getProfile();
                }
                break;
            case 4545://doc picker for android 10+
//                String fileName = Utils.getFileName(this, data.getData());
//                Uri fileUrl = Utils.getFilePath(this,data.getData());
                String path = null;
                try {
                    if (data != null && data.getData() != null) {
                        path = Utils.getFilePath(this, data.getData());

                        if (path != null) {
                            Log.e("Doc Path == > ", path);
                            professionalInfoActivityVM.updateResume(new File(path), "");
                        } else {
                            toastMessage(getString(R.string.file_not_selected));
                        }
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

//    private static String copyFileAndGetPath(Context context, Uri realUri, String id) {
//        final String selection = "_id=?";
//        final String[] selectionArgs = new String[]{id};
//        String path = null;
//        Cursor cursor = null;
//        try {
//            final String[] projection = {"_display_name"};
//            cursor = context.getContentResolver().query(realUri, projection, selection, selectionArgs,
//                    null);
//            cursor.moveToFirst();
//            final String fileName = cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
//            File file = new File(context.getCacheDir(), fileName);
//
//            FileUtils.saveAnswerFileFromUri(realUri, file, context);
//            path = file.getAbsolutePath();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return path;
//    }

    public void onClickHeadline() {
        openDescriptionScreen(HEADLINE);
    }

    public void onClickEmployment() {
        Intent i = new Intent(this, EmploymentEditActivity.class);
        startActivityForResult(i, REQ_REFRESH);
    }

    public void onClickEducation() {
        Intent i = new Intent(this, EducationEditActivity.class);
        startActivityForResult(i, REQ_REFRESH);
    }

    public void onClickSummary() {
        Intent i = new Intent(this, SummaryActivity.class);
        startActivityForResult(i, REQ_REFRESH);
    }

    public void onClickResume() {
        professionalInfoActivityVM.showOptionDialog(resumeUrl);
    }

    public void onClickAddress() {
        Intent iAdd = new Intent(this, UpdateLocationActivity.class);
        iAdd.putExtra("screen", ADDRESS);
        startActivityForResult(iAdd, REQ_REFRESH);
    }

    public void onClickWebsite() {
        openDescriptionScreen(WEBSITE);
    }

    public void onClickHourlyRate() {
        professionalInfoActivityVM.dialogHourlyRate(binding.tvHourlyRate).show();
    }

    public void onClickOffAddress() {
        openDescriptionScreen(OFFICE_ADD);
    }

    private void openDescriptionScreen(int screen) {
        Intent i = new Intent(this, HeadlinesActivity.class);
        i.putExtra("screen", screen);
        startActivityForResult(i, REQ_REFRESH);
    }

    private void setupGroups() {
//        binding.sgHeadline.setEnabled(false);
//        binding.sgProfSummary.setEnabled(false);
        binding.sgHeadline.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgHeadline));
        binding.sgProfSummary.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgProfSummary));
        binding.sgHourlyRate.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgHourlyRate));
        binding.sgEmpHistory.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgEmpHistory));
        binding.sgEducation.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgEducation));

        try {
            if (isFree()) {
                binding.sgAddress.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgAddress));
                binding.sgOffAdd.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgOffAdd));
                binding.sgPhone.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgPhone));
                binding.sgEmail.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgEmail));
                binding.sgWebsite.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgWebsite));
                binding.sgResume.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgResume));

            } else {
                binding.sgAddress.setEnabled(false);
                binding.sgOffAdd.setEnabled(false);
                binding.sgPhone.setEnabled(false);
                binding.sgEmail.setEnabled(false);
                binding.sgWebsite.setEnabled(false);
                binding.sgResume.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPositionChanges(int position, SegmentedButtonGroup segmentedButtonGroup) {
        segmentedButtonGroup.setPosition(position);
        professionalInfoActivityVM.makePublicPrivate(segmentedButtonGroup);
    }

    private void refreshViews() {

        if (profileData != null) {
            if (TextUtils.isEmpty(profileData.headlines)) {
                binding.tvHeadline.setHint(getString(R.string.headline));
            } else {
                binding.tvHeadline.setText(profileData.headlines);
            }
            if (TextUtils.isEmpty(profileData.summaries)) {
                binding.tvSummary.setHint(getString(R.string.profile_summary));
            } else {
                binding.tvSummary.setText(profileData.summaries);
            }

            if (TextUtils.isEmpty(profileData.contactNo)) {
                binding.tvPhone.setHint(getString(R.string.phone));
            } else {
                binding.tvPhone.setText(profileData.contactNo.replace(".", " "));
            }

            if (TextUtils.isEmpty(profileData.email)) {
                binding.tvEmail.setHint(getString(R.string.email));
            } else {
                binding.tvEmail.setText(profileData.email);
            }

            StringBuilder address = new StringBuilder();
            if (!TextUtils.isEmpty(profileData.cityName)) {
                address.append(profileData.cityName);
            }

            if (!TextUtils.isEmpty(profileData.stateName)) {
                if (address.length() > 0) {
                    address.append(", ");
                }
                address.append(profileData.stateName);
            }

            if (!TextUtils.isEmpty(profileData.countryName)) {
                if (address.length() > 0) {
                    address.append(", ");
                }
                address.append(profileData.countryName);
            }

            if (!TextUtils.isEmpty(address)) {
                binding.tvAddress.setText(String.format("%s", address));
            } else {
                binding.tvAddress.setHint(getString(R.string.address));
            }

            if (TextUtils.isEmpty(profileData.addProAddress)) {
                binding.tvOffAdd.setHint(getString(R.string.professional_address));
            } else {
                binding.tvOffAdd.setText(profileData.addProAddress);
            }

            if (TextUtils.isEmpty(profileData.websites)) {
                binding.tvWebsite.setHint(getString(R.string.website));
            } else {
                binding.tvWebsite.setText(profileData.websites);
            }

            professionalInfoActivityVM.getEmploymentData(profileData);

            professionalInfoActivityVM.getEducationData(profileData);

            if (profileData.resumes != null) {
                resumeUrl = getResumeUrl() + profileData.resumes;
                binding.tvResume.setText("" + profileData.resumes);
            }
        }
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        if (data != null) {
            profileData = data;
            refreshViews();
            professionalInfoActivityVM.getIsShowProgress().postValue(false);
        }
    }

    @Override
    public void onItemSkillClick(boolean isEmployment, String tag) {
        Intent i;
        if (isEmployment) {
            i = new Intent(this, EmploymentEditActivity.class);
        } else {
            i = new Intent(this, EducationEditActivity.class);
        }
        startActivityForResult(i, REQ_REFRESH);
    }

    @Override
    public void onItemExperienceClick(boolean isEmployment, String tag) {
        Intent i;
        if (isEmployment) {
            i = new Intent(this, EmploymentEditActivity.class);
        } else {
            i = new Intent(this, EducationEditActivity.class);
        }
        startActivityForResult(i, REQ_REFRESH);
    }

    public void checkAndAskStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {

                    // Do something if permission is not granted and the user has also checked the **"Don't ask again"**

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // Do something if permission not granted
                }
            }
        }
    }
}
