package com.nojom.ui.workprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.SkillsAdapter;
import com.nojom.apis.ViewSkillAPI;
import com.nojom.databinding.ActivityEditSkillsBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.UserSkillsModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class EditSkillsActivity extends BaseActivity implements BaseActivity.OnProfileLoadListener, SkillsAdapter.OnItemClickListener {

    private EditSkillsActivityVM viewSkillsActivityViewModel;
    private ActivityEditSkillsBinding binding;
    private static final int REQ_EDIT_LANGUAGE = 101;
    private static final int REQ_EDIT_EXPERTISE = 102;
    private static final int REQ_EDIT_SKILL = 103;
    private int selectedView = -1;//this flag is used for specific view to refresh
    private int selectedProfileSkillId;
    private ArrayList<UserSkillsModel.SkillLists> selectedSkillLists;
    private ViewSkillAPI viewSkillAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_skills);
        binding.setActivity(this);
        viewSkillsActivityViewModel = ViewModelProviders.of(this).get(EditSkillsActivityVM.class);
        viewSkillAPI = new ViewSkillAPI();
        viewSkillAPI.init(this);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvTitle.setText(getString(R.string.skills));

        binding.rvLanguages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvExpertise.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSkills.setLayoutManager(new LinearLayoutManager(this));

        binding.rvLanguages.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));
        binding.rvExpertise.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));
        binding.rvSkills.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));

        ProfileResponse profileData = Preferences.getProfileData(this);
        if (profileData != null) {
            refreshViews(profileData);
        }

        binding.rvLanguages.setNestedScrollingEnabled(false);
        binding.rvExpertise.setNestedScrollingEnabled(false);
        binding.rvSkills.setNestedScrollingEnabled(false);

        setOnProfileLoadListener(this);

        Utils.trackFirebaseEvent(this, "Edit_Skill_Screen");

        viewSkillsActivityViewModel.getSkillDataList().observe(this, skill -> {
            if (skill != null && skill.size() > 0) {
                SkillsAdapter mSkillAdapter = new SkillsAdapter(this, skill, this, false);
                mSkillAdapter.setSELECTED_TAG("SKILL");
                binding.rvSkills.setAdapter(mSkillAdapter);
                binding.llSkillsView.setVisibility(View.VISIBLE);
            } else {
                binding.llSkillsView.setVisibility(View.GONE);
            }
        });

        viewSkillsActivityViewModel.getExpertiseDataList().observe(this, experts -> {
            if (experts != null && experts.size() > 0) {
                binding.llExpertiseView.setVisibility(View.VISIBLE);
                SkillsAdapter mExpertiseAdapter = new SkillsAdapter(EditSkillsActivity.this, experts, EditSkillsActivity.this, false);
                mExpertiseAdapter.setSELECTED_TAG("EXPERT");
                if (experts.size() > 0) {//selected skill id
                    selectedProfileSkillId = experts.get(0).skillId;
                }
                binding.rvExpertise.setAdapter(mExpertiseAdapter);
            } else {
                binding.llExpertiseView.setVisibility(View.GONE);
            }
        });

        viewSkillsActivityViewModel.getLanguageDataList().observe(this, languages -> {
            if (languages != null && languages.size() > 0) {
                binding.llLanguageView.setVisibility(View.VISIBLE);
                SkillsAdapter mLanguageAdapter = new SkillsAdapter(EditSkillsActivity.this, languages, EditSkillsActivity.this, false);
                mLanguageAdapter.setSELECTED_TAG("LANGUAGE");
                binding.rvLanguages.setAdapter(mLanguageAdapter);
            } else {
                binding.llLanguageView.setVisibility(View.GONE);
            }
        });

        viewSkillAPI.getUserModel().observe(this, userSkillsModel -> {
            if (userSkillsModel != null && userSkillsModel.skillLists != null) {
                selectedSkillLists = new ArrayList<>();
                for (UserSkillsModel.SkillLists skills : userSkillsModel.skillLists) {
                    if (skills.psId != null) {

                        if (!selectedSkillLists.contains(skills)) {
                            skills.isSelected = true;
                            selectedSkillLists.add(skills);
                        } else {
                            skills.isSelected = true;
                        }

                    } else {
                        if (selectedSkillLists.size() > 0) {
                            for (UserSkillsModel.SkillLists selectedSkill : selectedSkillLists) {
                                if (selectedSkill.id == skills.id) {
                                    skills.isSelected = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                Intent i = new Intent(EditSkillsActivity.this, SelectSkillsActivity.class);
                i.putExtra(Constants.IS_EDIT, true);
                if (viewSkillsActivityViewModel != null && viewSkillsActivityViewModel.getSkillDataList() != null && viewSkillsActivityViewModel.getSkillDataList().getValue() != null) {
                    i.putExtra("size", Objects.requireNonNull(viewSkillsActivityViewModel.getSkillDataList().getValue()).size());
                }
                i.putExtra(SKILLS, userSkillsModel.skillLists);
                i.putExtra(SKILL_COUNT, userSkillsModel.skillCount);
                i.putExtra(SELECTED_SKILL, selectedSkillLists);
                startActivityForResult(i, REQ_EDIT_SKILL);
                openToLeft();
                selectedView = 3;

            }
            showSkillProgress(false);
        });

        viewSkillAPI.getIsShowProgress().observe(this, isShow -> showSkillProgress(false));
    }

    private void refreshViews(ProfileResponse profileData) {


        switch (selectedView) {
            case 1://language
                viewSkillsActivityViewModel.getLanguageData(profileData.language);
                break;
            case 2://expertise
                viewSkillsActivityViewModel.getExpertiseData(profileData);
                break;
            case 3://skills
                viewSkillsActivityViewModel.getSkillData(profileData);
                break;
            default:

                viewSkillsActivityViewModel.getLanguageData(profileData.language);
                viewSkillsActivityViewModel.getExpertiseData(profileData);
                viewSkillsActivityViewModel.getSkillData(profileData);
                break;
        }
        selectedView = -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_EDIT_LANGUAGE:
                case REQ_EDIT_EXPERTISE:
                case REQ_EDIT_SKILL:
                    getProfile();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void onClickSkills() {
        viewSkillAPI.getSkillsList(1, null);
    }

    private void showSkillProgress(boolean isShow) {
        disableEnableTouch(isShow);
        if (isShow) {
            binding.imgSkillNext.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.imgSkillNext.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    public void onClickExpertise() {
        Intent i = new Intent(this, SelectExpertiseActivity.class);
        i.putExtra(Constants.IS_EDIT, true);
        i.putExtra(SKILL_ID, selectedProfileSkillId);
        startActivityForResult(i, REQ_EDIT_EXPERTISE);
        openToLeft();
        selectedView = 2;
    }

    public void onClickLanguage() {
        Intent i = new Intent(this, LanguagesActivity.class);
        i.putExtra(Constants.IS_EDIT, true);
        startActivityForResult(i, REQ_EDIT_LANGUAGE);
        openToLeft();
        selectedView = 1;
    }

    @Override
    public void onItemSkillClick(boolean isEmlpoyment, String tag) {
        switch (tag) {
            case "LANGUAGE":
                onClickLanguage();
                break;
            case "EXPERT":
                onClickExpertise();
                break;
            case "SKILL":
                onClickSkills();
                break;
        }
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        refreshViews(data);
    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_GET_PROFILE)) {
            ProfileResponse profileObject = ProfileResponse.getProfileObject(decryptedData);
            if (profileObject != null) {
                Preferences.setProfileData(this, profileObject);
                onProfileLoad(profileObject);
            }
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {

    }
}
