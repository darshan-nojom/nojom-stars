package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.ActivitySelectWorkPlaceBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.segment.SegmentedButton;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class SelectWorkPlaceActivity extends BaseActivity implements ResponseListener, RecyclerviewAdapter.OnViewBindListner {
    private ActivitySelectWorkPlaceBinding binding;
    private SelectWorkPlaceActivityVM selectWorkPlaceActivityVM;
    public SparseBooleanArray workArrayIds;
    private List<String> workPlaceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_work_place);
        binding.setWorkPlaceAct(this);
        selectWorkPlaceActivityVM = ViewModelProviders.of(this).get(SelectWorkPlaceActivityVM.class);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvCancel.setOnClickListener(v -> gotoMainActivity(Constants.TAB_HOME));
        selectWorkPlaceActivityVM.setResponseListener(this);

        binding.toolbar.progress.setProgress(60);

        workArrayIds = new SparseBooleanArray();

        workPlaceList = new ArrayList<>();
        workPlaceList.add(getString(R.string.office_base));
        workPlaceList.add(getString(R.string.home_base));

        binding.rvWorkPlace.setLayoutManager(new LinearLayoutManager(this));

        RecyclerviewAdapter mAdapter = new RecyclerviewAdapter((ArrayList<?>) workPlaceList, R.layout.item_select_work_place, this);
        binding.rvWorkPlace.setAdapter(mAdapter);

        ClickableSpan termsOfUseClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

            }
        };

        Utils.makeLinks(binding.tvReadThis, new String[]{getString(R.string.read_this)}, new ClickableSpan[]{termsOfUseClick});
    }

    @Override
    public void bindView(View view, final int position) {
        TextView tvPlace = view.findViewById(R.id.tv_place);
        tvPlace.setText(workPlaceList.get(position));

        final SegmentedButtonGroup segmentedButtonGroup = view.findViewById(R.id.segmentGroup);
        final SegmentedButton tabNo = view.findViewById(R.id.tab_no);
        final SegmentedButton tabYes = view.findViewById(R.id.tab_yes);

        segmentedButtonGroup.setOnPositionChangedListener(btnPosition -> {
            if (btnPosition == 0) {
                workArrayIds.delete(position);
                tabYes.setTypeface(Constants.SFTEXT_REGULAR);
                tabNo.setTypeface(Constants.SFTEXT_BOLD);
                segmentedButtonGroup.setSelectorColor(ContextCompat.getColor(this, R.color.red_dark));
            } else {
                workArrayIds.put(position, true);
                tabNo.setTypeface(Constants.SFTEXT_REGULAR);
                tabYes.setTypeface(Constants.SFTEXT_BOLD);
                segmentedButtonGroup.setSelectorColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void onClickNext() {
        try {
            if (workArrayIds != null && workArrayIds.size() > 0) {
                binding.btnNext.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                if (workArrayIds.size() == 2) {
                    selectWorkPlaceActivityVM.updateWorkbase("2", this);
                } else {
                    if (workArrayIds.size() == 0) {
                        selectWorkPlaceActivityVM.updateWorkbase("3", this);
                    } else {
                        selectWorkPlaceActivityVM.updateWorkbase(String.valueOf(workArrayIds.keyAt(0)), this);
                    }
                }
            } else {
                validationError(getString(R.string.please_select_at_least_one_workplace));
            }
        } catch (Exception e) {
            binding.btnNext.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseSuccess(Response<GeneralModel> response) {
        redirectActivity(LanguagesActivity.class);
        binding.btnNext.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError() {
        binding.btnNext.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }
}
