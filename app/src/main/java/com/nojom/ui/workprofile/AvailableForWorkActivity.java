package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.apis.UpdateTypeAPI;
import com.nojom.databinding.ActivityAvailableForWorkBinding;
import com.nojom.model.Available;
import com.nojom.segment.SegmentedButton;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class AvailableForWorkActivity extends BaseActivity implements RecyclerviewAdapter.OnViewBindListner {
    private ActivityAvailableForWorkBinding binding;
    private AvailableForWorkActivityVM availableForWorkActivityVM;
    private RecyclerviewAdapter mAdapter;
    private UpdateTypeAPI updateTypeAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_available_for_work);
        binding.setAvailableActivity(this);
        LayoutBinderHelper layoutBinderHelper = new LayoutBinderHelper();
        layoutBinderHelper.setIsEdit(true);
        binding.setLayoutBinder(layoutBinderHelper);
        updateTypeAPI = new UpdateTypeAPI();
        updateTypeAPI.init(this);
        availableForWorkActivityVM = ViewModelProviders.of(this).get(AvailableForWorkActivityVM.class);
        availableForWorkActivityVM.setActivity(this);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvCancel.setOnClickListener(v -> gotoMainActivity(Constants.TAB_HOME));

        binding.toolbar.progress.setProgress(50);

        availableForWorkActivityVM.getPayType();

        binding.rvAvailable.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAvailable.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));

        ClickableSpan termsOfUseClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                toastMessage(getString(R.string.read_this));
            }
        };

        Utils.makeLinks(binding.tvReadThis, new String[]{getString(R.string.read_this)}, new ClickableSpan[]{termsOfUseClick});

        availableForWorkActivityVM.getListMutableLiveData().observe(this, data -> {
            mAdapter = new RecyclerviewAdapter((ArrayList<?>) data,
                    R.layout.item_available_for_work, AvailableForWorkActivity.this);
            binding.rvAvailable.setAdapter(mAdapter);
        });

        updateTypeAPI.getIsShowError().observe(this, aBoolean -> binding.getLayoutBinder().setIsEdit(aBoolean));
    }

    @Override
    public void bindView(View view, final int position) {
        View line = view.findViewById(R.id.view);
        TextView tvSchedule = view.findViewById(R.id.tv_schedule);
        TextView tvAvailable = view.findViewById(R.id.tv_available);
        SegmentedButtonGroup segmentedButtonGroup = view.findViewById(R.id.segmentGroup);
        SegmentedButton tabNo = view.findViewById(R.id.tab_no);
        SegmentedButton tabYes = view.findViewById(R.id.tab_yes);

        List<Available.Data> availableList = availableForWorkActivityVM.getListMutableLiveData().getValue();
        if (availableList == null || availableList.size() == 0) {
            return;
        }
        if (position == 0) {
            line.setVisibility(View.VISIBLE);
        } else {
            line.setVisibility(View.GONE);
        }
        try {
            tvSchedule.setText(availableList.get(position).name);
            tvAvailable.setText(String.format("(%s)", availableList.get(position).detail));
        } catch (Exception e) {
            e.printStackTrace();
        }

        segmentedButtonGroup.setOnPositionChangedListener(btnPosition -> {
            try {
                if (btnPosition == 0) {
                    tabYes.setTypeface(Constants.SFTEXT_REGULAR);
                    tabNo.setTypeface(Constants.SFTEXT_BOLD);
                    segmentedButtonGroup.setSelectorColor(ContextCompat.getColor(this, R.color.red_dark));
                } else {
                    tabNo.setTypeface(Constants.SFTEXT_REGULAR);
                    tabYes.setTypeface(Constants.SFTEXT_BOLD);
                    segmentedButtonGroup.setSelectorColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                }
                updateTypeAPI.updatePayTypes(availableList.get(position).id, btnPosition);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void onClickNext() {
        redirectActivity(SelectWorkPlaceActivity.class);
    }
}
