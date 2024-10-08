package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.apis.ViewSkillAPI;
import com.nojom.databinding.ActivitySelectSkillsBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.UserSkillsModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.EndlessRecyclerViewScrollListener;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class SelectSkillsActivity extends BaseActivity implements RecyclerviewAdapter.OnViewBindListner, BaseActivity.OnProfileLoadListener {
    private SelectSkillsActivityVM skillsActivityViewModel;
    private ActivitySelectSkillsBinding binding;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int pageNo = 1;
    private boolean isSearchCall, isUpdateSkillCompulsory;
    private int selectedSkillCount = 0;
    private RecyclerviewAdapter selectedAdapter;
    private ArrayList<UserSkillsModel.SkillLists> selectedSkillLists, skillLists;
    private int totalSkillCount;
    private ViewSkillAPI viewSkillAPI;
    private ArrayList<UserSkillsModel.SkillLists> skillItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_skills);
        skillsActivityViewModel = ViewModelProviders.of(this).get(SelectSkillsActivityVM.class);
        skillsActivityViewModel.init(this);
        skillItems = new ArrayList<>();
        viewSkillAPI = new ViewSkillAPI();
        viewSkillAPI.init(this);
        initData();
    }

    @SuppressLint("DefaultLocale")
    private void initData() {

        if (getIntent() != null) {
            selectedSkillCount = getIntent().getIntExtra("size", 0);
            skillLists = (ArrayList<UserSkillsModel.SkillLists>) getIntent().getSerializableExtra(SKILLS);
            selectedSkillLists = (ArrayList<UserSkillsModel.SkillLists>) getIntent().getSerializableExtra(SELECTED_SKILL);
            totalSkillCount = getIntent().getIntExtra(SKILL_COUNT, 0);
            isUpdateSkillCompulsory = getIntent().getBooleanExtra("flag", false);

            if (isUpdateSkillCompulsory) {
                setOnProfileLoadListener(this);
            }
        }

        if (skillLists == null || skillLists.size() == 0 || totalSkillCount == 0) {//fresh user or redirect from home screen dialog
            viewSkillAPI.getSkillsList(1, null);
        }

        binding.toolbar.imgBack.setOnClickListener(v -> {
            if (isUpdateSkillCompulsory) {
//                finish();
//                System.exit(0);
            } else {
                onBackPressed();
            }
        });
        binding.toolbar.tvSave.setOnClickListener(v -> onClickSave());
        binding.toolbar.imgBack.setVisibility(View.VISIBLE);
        binding.toolbar.tvEditCancel.setVisibility(View.GONE);
        binding.toolbar.rlEdit.setVisibility(View.VISIBLE);
        binding.toolbar.tvToolbarTitle.setText(getString(R.string.select_skills));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvSelectedSkills.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
                    pageNo = page;
                    viewSkillAPI.getSkillsList(pageNo, Objects.requireNonNull(binding.etSearch.getText()).toString().trim());
                }
            }
        };

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                pageNo = 1;
                selectedAdapter = null;
                if (!isSearchCall) {
                    skillsActivityViewModel.getIsSearch().postValue(true);
                    if (!TextUtils.isEmpty(Objects.requireNonNull(binding.etSearch.getText()).toString().trim())) {
                        viewSkillAPI.getSkillsList(pageNo, binding.etSearch.getText().toString().trim());
                    } else {
                        viewSkillAPI.getSkillsList(pageNo, null);
                    }
                }
                return true;
            }
            return false;
        });

        skillsActivityViewModel.getMutableCounter().observe(this, counter -> binding.tvTotalSkill.setText(String.format(" / %d", counter)));

        skillsActivityViewModel.getMutablePageNo().observe(this, integer -> {
            if (scrollListener != null) {
                scrollListener.resetState();
            }
            if (pageNo == 1) {
                binding.shimmerLayout.startShimmer();
                binding.shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
            }
        });

        skillsActivityViewModel.getIsSearch().observe(this, isSearch -> {
            isSearchCall = isSearch;
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.toolbar.tvSave.setVisibility(View.VISIBLE);
            binding.toolbar.progressBar.setVisibility(View.GONE);
            disableEnableTouch(false);
        });

        //get data from previous activity and set here
        if (selectedSkillLists != null && selectedSkillLists.size() > 0) {
            skillsActivityViewModel.getSelectedListMutableLiveData().postValue(selectedSkillLists);
        }

        if (skillLists != null) {
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);

            skillsActivityViewModel.getMutableCounter().postValue(totalSkillCount);
            skillsActivityViewModel.getListMutableLiveData().postValue(skillLists);
        }


        skillsActivityViewModel.getListMutableLiveData().observe(this, this::setAdapter);

        skillsActivityViewModel.getIsSkillAdded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                if (isUpdateSkillCompulsory) {
                    getProfile();
                } else {
                    setResult(RESULT_OK);
                    finish();
                    finishToRight();
                }

            }
        });

        viewSkillAPI.getIsShowProgress().observe(this, aBoolean -> {
            if (aBoolean) {
                if (pageNo == 1) {
                    skillLists = new ArrayList<>();
                    skillsActivityViewModel.getMutablePageNo().postValue(pageNo);
                }
            } else {
                skillsActivityViewModel.getIsSearch().postValue(false);
            }
        });

        viewSkillAPI.getUserModel().observe(this, userSkillsModel -> {
            if (userSkillsModel != null && userSkillsModel.skillLists != null) {

                for (UserSkillsModel.SkillLists skills : userSkillsModel.skillLists) {
                    if (skills.psId != null) {

                        if (selectedSkillLists != null && !selectedSkillLists.contains(skills)) {
                            skills.isSelected = true;
                            selectedSkillLists.add(skills);
                        } else {
                            skills.isSelected = true;
                        }

                    } else {
                        if (selectedSkillLists != null && selectedSkillLists.size() > 0) {
                            for (UserSkillsModel.SkillLists selectedSkill : selectedSkillLists) {
                                if (selectedSkill.id == skills.id) {
                                    skills.isSelected = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                skillLists.addAll(userSkillsModel.skillLists);
                skillsActivityViewModel.getSelectedListMutableLiveData().postValue(selectedSkillLists);
                skillsActivityViewModel.getMutableCounter().postValue(userSkillsModel.skillCount);
                skillsActivityViewModel.getListMutableLiveData().postValue(skillLists);

            }
            skillsActivityViewModel.getIsSearch().postValue(false);
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void bindView(View view, final int position) {
        TextView txtSkill = view.findViewById(R.id.tv_skill_name);
        final TextView txtSkillLevel = view.findViewById(R.id.tv_skill_level);
        ImageView imgRemove = view.findViewById(R.id.img_add_remove);
        try {
//            ArrayList<UserSkillsModel.SkillLists> skillLists = skillsActivityViewModel.getListMutableLiveData().getValue();
//            if (skillLists != null && skillLists.size() > 0) {
            if (skillsActivityViewModel.getListMutableLiveData().getValue() != null) {
                UserSkillsModel.SkillLists skills = skillsActivityViewModel.getListMutableLiveData().getValue().get(position);

                txtSkill.setText(skills.getName(language));

                if (skills.isSelected) {
                    imgRemove.setImageResource(R.drawable.close_red);
                    txtSkillLevel.setVisibility(View.VISIBLE);
                    if (skills.rating != null) {
                        txtSkillLevel.setText(Utils.getRatingFromId(skills.rating));
                    } else {
                        txtSkillLevel.setText(Utils.getRatingFromId(0));//default
                    }
                } else {
                    txtSkillLevel.setVisibility(View.GONE);
                    imgRemove.setImageResource(R.drawable.add);
                }

                txtSkillLevel.setOnClickListener(view1 -> skillsActivityViewModel.showSingleSelectionDialog(txtSkillLevel, skills));

                imgRemove.setOnClickListener(view12 -> {
                    try {
                        skills.selectedRating = Utils.getRatingId(txtSkillLevel.getText().toString());
                        if (skills.isSelected) {
                            skills.isSelected = false;
                            selectedSkillCount--;
                            Objects.requireNonNull(skillsActivityViewModel.getSelectedListMutableLiveData().getValue()).remove(skills);
                        } else {
                            skills.isSelected = true;
                            selectedSkillCount++;
                            if (skillsActivityViewModel.getSelectedListMutableLiveData().getValue() != null) {
                                skillsActivityViewModel.getSelectedListMutableLiveData().getValue().add(skills);
                            } else {
                                ArrayList<UserSkillsModel.SkillLists> selectedSkillLists = new ArrayList<>();
                                selectedSkillLists.add(skills);
                                skillsActivityViewModel.getSelectedListMutableLiveData().postValue(selectedSkillLists);
                            }

                        }
                        selectedAdapter.notifyItemChanged(position);
                        binding.tvSkillNo.setText(String.format("%d", selectedSkillCount));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    private void setAdapter(ArrayList<UserSkillsModel.SkillLists> skills) {
        if (selectedAdapter == null) {
            selectedAdapter = new RecyclerviewAdapter(skills, R.layout.item_selected_skills,
                    SelectSkillsActivity.this);
            binding.rvSelectedSkills.setAdapter(selectedAdapter);
        } else {
            selectedAdapter.doRefresh(skills);
        }

        if (skillsActivityViewModel.getSelectedListMutableLiveData().getValue() != null) {
            binding.tvSkillNo.setText(String.format("%d", skillsActivityViewModel.getSelectedListMutableLiveData().getValue().size()));
        } else {
            binding.tvSkillNo.setText(String.format("%d", 0));
        }
    }

    public void onClickSave() {
        try {
            ArrayList<UserSkillsModel.SkillLists> selectedSkillLists = skillsActivityViewModel.getSelectedListMutableLiveData().getValue();
            if (selectedSkillLists != null && selectedSkillLists.size() > 0) {
                for (UserSkillsModel.SkillLists skill : selectedSkillLists) {
                    if (skill.rating != null && isEmpty(String.valueOf(skill.rating))) {
                        validationError(getString(R.string.please_select_experience_level));
                        return;
                    }
                }

                StringBuilder skillIds = null;
                StringBuilder ratingIds = null;
                for (UserSkillsModel.SkillLists skill : skillsActivityViewModel.getSelectedListMutableLiveData().getValue()) {
                    skillIds = (skillIds == null ? new StringBuilder() : skillIds.append(",")).append(skill.id);
                    ratingIds = (ratingIds == null ? new StringBuilder() : ratingIds.append(",")).append(skill.rating != null ? skill.rating : skill.selectedRating);
                }

                String skillsId = skillIds == null ? "" : skillIds.toString();
                String ratingsId = ratingIds == null ? "" : ratingIds.toString();
                binding.toolbar.tvSave.setVisibility(View.INVISIBLE);
                binding.toolbar.progressBar.setVisibility(View.VISIBLE);
                disableEnableTouch(true);
                skillsActivityViewModel.addSkills(skillsId, ratingsId,1);

            } else {
                toastMessage(getString(R.string.please_select_a_skill_first));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isUpdateSkillCompulsory) {
//            System.exit(0);
        }
        finishToRight();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (scrollListener != null)
            binding.rvSelectedSkills.removeOnScrollListener(scrollListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scrollListener != null)
            binding.rvSelectedSkills.addOnScrollListener(scrollListener);
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        setResult(RESULT_OK);
        finish();
        finishToRight();
    }
}
