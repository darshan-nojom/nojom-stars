package com.nojom.ui.workprofile;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.ActivitySelectExpertiseBinding;
import com.nojom.model.ServicesModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Preferences;

import java.util.ArrayList;
import java.util.List;

public class SelectExpertiseActivity extends BaseActivity implements RecyclerviewAdapter.OnViewBindListner {

    private SelectExpertiseActivityVM selectExpertiseActivityVM;
    private ActivitySelectExpertiseBinding binding;
    private static final int REQ_EDIT_EXPERIENCE = 102;
    private boolean isEdit = false;
    private RecyclerviewAdapter mAdapter;
    public int selectedExpertiseId;
    private LayoutBinderHelper layoutBinderHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_expertise);

        layoutBinderHelper = new LayoutBinderHelper();
        binding.setLayoutBinder(layoutBinderHelper);
        binding.setExpertises(this);

        if (getIntent() != null) {
            isEdit = getIntent().getBooleanExtra(Constants.IS_EDIT, false);
            selectedExpertiseId = getIntent().getIntExtra(Constants.SKILL_ID, -1);
            layoutBinderHelper.setIsEdit(isEdit);
        }

        binding.rvSkills.setVisibility(View.GONE);
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.startShimmer();

        selectExpertiseActivityVM = ViewModelProviders.of(this).get(SelectExpertiseActivityVM.class);
        initData();
        selectExpertiseActivityVM.init(this, layoutBinderHelper);

    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.editToolBar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvCancel.setOnClickListener(v -> gotoMainActivity(Constants.TAB_HOME));
        binding.editToolBar.tvEditCancel.setOnClickListener(v -> onBackPressed());
        binding.editToolBar.tvSave.setOnClickListener(v -> onClickNext());

        if (layoutBinderHelper.getIsEdit()) {
            binding.editToolBar.rlEdit.setVisibility(View.VISIBLE);
            binding.editToolBar.imgBack.setVisibility(View.VISIBLE);
            binding.editToolBar.tvEditCancel.setVisibility(View.GONE);
            binding.editToolBar.relSave.setVisibility(View.GONE);
            binding.toolbar.header.setVisibility(View.GONE);
        }

        binding.toolbar.progress.setProgress(20);

        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        binding.rvSkills.setLayoutManager(manager);
        binding.rvSkills.addItemDecoration(new EqualSpacingItemDecoration(16));

        selectExpertiseActivityVM.getListMutableLiveData().observe(this, data -> {
            binding.rvSkills.setVisibility(View.VISIBLE);
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);

            mAdapter = new RecyclerviewAdapter((ArrayList<?>) data, R.layout.item_skills_edit, SelectExpertiseActivity.this);
            binding.rvSkills.setAdapter(mAdapter);
            binding.rvSkills.setFocusable(false);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_EDIT_EXPERIENCE) {
                setResult(RESULT_OK);
                finish();
                Preferences.writeBoolean(this, IS_REFRESH_JOB, true);//flag fot refresh Home screen jobs
            }
        }
    }

    @Override
    public void bindView(View view, final int position) {
        final TextView textView = view.findViewById(R.id.tv_skill);
        List<ServicesModel.Data> servicesList = selectExpertiseActivityVM.getListMutableLiveData().getValue();
        if (servicesList == null || servicesList.size() == 0) {
            return;
        }
        textView.setText(servicesList.get(position).name);

        if (selectedExpertiseId == servicesList.get(position).id) {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_button_bg));
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.SFTEXT_BOLD));
        } else {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.white_button_bg));
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.SFTEXT_REGULAR));
        }

        textView.setOnClickListener(view1 -> {
            selectedExpertiseId = servicesList.get(position).id;
            mAdapter.notifyDataSetChanged();
            layoutBinderHelper.setIsEdit(false);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void onClickNext() {
        if (selectedExpertiseId != 0) {
            try {
                StringBuilder serviceIds;
                serviceIds = new StringBuilder().append(selectedExpertiseId);
                Intent i = new Intent(this, WorkExperienceActivity.class);
                i.putExtra(Constants.SERVICE_IDS, serviceIds.toString());
                i.putExtra(Constants.IS_EDIT, isEdit);
                if (isEdit)
                    startActivityForResult(i, REQ_EDIT_EXPERIENCE);
                else
                    startActivity(i);
                openToLeft();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            validationError(getString(R.string.please_select_your_expertise));
        }
    }
}
