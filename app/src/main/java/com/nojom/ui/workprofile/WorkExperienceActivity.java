package com.nojom.ui.workprofile;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.ActivityWorkExperienceBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.EqualSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class WorkExperienceActivity extends BaseActivity implements ResponseListener, RecyclerviewAdapter.OnViewBindListner {
    private ActivityWorkExperienceBinding binding;
    private WorkExperienceActivityVM workExperienceActivityVM;
    private LayoutBinderHelper layoutBinderHelper;
    private String serviceIds;
    private boolean isEdit = false;
    private RecyclerviewAdapter mAdapter;
    public SparseBooleanArray selectedExpArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_work_experience);
        binding.setWorkActivity(this);
        layoutBinderHelper = new LayoutBinderHelper();
        binding.setLayoutBinder(layoutBinderHelper);

        if (getIntent() != null) {
            serviceIds = getIntent().getStringExtra(Constants.SERVICE_IDS);
            isEdit = getIntent().getBooleanExtra(Constants.IS_EDIT, false);
        }

        if (isEdit) {
            binding.editToolBar.rlEdit.setVisibility(View.VISIBLE);
            binding.editToolBar.imgBack.setVisibility(View.VISIBLE);
            binding.editToolBar.tvEditCancel.setVisibility(View.GONE);
            binding.toolbar.header.setVisibility(View.GONE);
            layoutBinderHelper.setIsEdit(isEdit);
        }
        workExperienceActivityVM = ViewModelProviders.of(this).get(WorkExperienceActivityVM.class);
        initData();
        workExperienceActivityVM.init(this);
        workExperienceActivityVM.setResponseListener(this);
    }

    private void initData() {
        selectedExpArray = new SparseBooleanArray();
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.editToolBar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvCancel.setOnClickListener(v -> gotoMainActivity(Constants.TAB_HOME));
        binding.editToolBar.tvEditCancel.setOnClickListener(v -> onBackPressed());
        binding.editToolBar.tvSave.setOnClickListener(v -> onClickNext());
        binding.toolbar.progress.setProgress(40);

        binding.rvExperience.setLayoutManager(new LinearLayoutManager(this));
        binding.rvExperience.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));

        workExperienceActivityVM.getListMutableLiveData().observe(this, expList -> {
            mAdapter = new RecyclerviewAdapter((ArrayList<?>) expList, R.layout.item_experience, WorkExperienceActivity.this);
            binding.rvExperience.setAdapter(mAdapter);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void onClickNext() {
        if (isEmpty(serviceIds))
            return;
        try {
            if (selectedExpArray.size() == 1) {
                String[] split = serviceIds.split(",");
                String experience;
                if (split.length == 2) {
                    experience = selectedExpArray.keyAt(0) + "," + selectedExpArray.keyAt(0);
                } else {
                    experience = selectedExpArray.keyAt(0) + "";
                }
                if (layoutBinderHelper.getIsEdit()) {
                    binding.editToolBar.imgBack.setVisibility(View.VISIBLE);
                    binding.editToolBar.tvEditCancel.setVisibility(View.GONE);
                    binding.editToolBar.tvSave.setVisibility(View.INVISIBLE);
                    binding.editToolBar.progressBar.setVisibility(View.VISIBLE);
                } else {
                    binding.btnNext.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
                workExperienceActivityVM.updateExperience(experience, serviceIds);
            } else {
                validationError(getString(R.string.please_select_experience));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseSuccess(Response<GeneralModel> response) {
        if (layoutBinderHelper.getIsEdit()) {
            setResult(RESULT_OK);
            finish();
        } else {
            redirectActivity(AvailableForWorkActivity.class);
        }
    }

    @Override
    public void onError() {
        if (layoutBinderHelper.getIsEdit()) {
            binding.editToolBar.imgBack.setVisibility(View.VISIBLE);
            binding.editToolBar.tvEditCancel.setVisibility(View.GONE);
            binding.editToolBar.tvSave.setVisibility(View.VISIBLE);
            binding.editToolBar.progressBar.setVisibility(View.GONE);
        } else {
            binding.btnNext.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void bindView(View view, final int position) {
        final TextView textView = view.findViewById(R.id.tv_skill);
        List<String> expList = workExperienceActivityVM.getListMutableLiveData().getValue();
        if (expList == null || expList.size() == 0) {
            return;
        }
        textView.setText(expList.get(position));

        if (selectedExpArray.get(position)) {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_button_bg));
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(Typeface.createFromAsset(this.getAssets(), Constants.SFTEXT_BOLD));
        } else {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.white_button_bg));
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(Typeface.createFromAsset(this.getAssets(), Constants.SFTEXT_REGULAR));
        }

        textView.setOnClickListener(view1 -> {
            selectedExpArray.clear();
            selectedExpArray.put(position, true);
            if (!binding.getLayoutBinder().getIsEdit())
                binding.btnNext.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        });
    }
}
