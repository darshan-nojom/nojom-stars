package com.nojom.ui.clientprofile;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.ActivityLeaveReviewBinding;
import com.nojom.model.ProjectByID;
import com.nojom.model.Questions;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.RatingBar;

import java.util.ArrayList;

public class LeaveReviewActivity extends BaseActivity implements RecyclerviewAdapter.OnViewBindListner {
    private ActivityLeaveReviewBinding binding;
    private LeaveReviewActivityVM leaveReviewActivityVM;
    private ProjectByID jobPostBids;
    private RecyclerviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_leave_review);
        leaveReviewActivityVM = ViewModelProviders.of(this).get(LeaveReviewActivityVM.class);
        leaveReviewActivityVM.init(this);
        initData();
    }

    private void initData() {
        binding.tvSubmit.setOnClickListener(v -> {
            if (leaveReviewActivityVM.isComment(binding)) {
                leaveReviewActivityVM.submitReview(jobPostBids, binding);
            } else {
                toastMessage(getString(R.string.please_enter_your_comment));
            }
        });
        binding.tvCancel.setOnClickListener(v -> onBackPressed());

        if (getIntent() != null) {
            jobPostBids = (ProjectByID) getIntent().getSerializableExtra(Constants.USER_DATA);
        }

        if (jobPostBids == null) {
            finish();
            return;
        }

        binding.rvQuestions.setLayoutManager(new LinearLayoutManager(this));

        leaveReviewActivityVM.getQuestions();

        leaveReviewActivityVM.getListMutableLiveData().observe(this, this::setAdapter);

        leaveReviewActivityVM.getIsShowProgress().observe(this, isShowProgress -> {
            disableEnableTouch(isShowProgress);
            if (isShowProgress) {
                binding.tvSubmit.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.tvSubmit.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        leaveReviewActivityVM.getIsLoadQuestions().observe(this, isLoadQuest -> {
            disableEnableTouch(isLoadQuest);
            if (isLoadQuest) {
                binding.shimmerLayout.setVisibility(View.VISIBLE);
                binding.shimmerLayout.startShimmer();
                binding.rvQuestions.setVisibility(View.INVISIBLE);
            } else {
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.shimmerLayout.stopShimmer();
                binding.rvQuestions.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void bindView(View view, int position) {
        RelativeLayout rlQuestions = view.findViewById(R.id.rl_question);
        TextView tvQuestions = view.findViewById(R.id.tv_question);
        RatingBar ratingBar = view.findViewById(R.id.ratingbar);
        SegmentedButtonGroup segmentedButtonGroup = view.findViewById(R.id.segmentGroup);
        EditText etComment = view.findViewById(R.id.et_comment);
        ArrayList<Questions.Data> questionsList = leaveReviewActivityVM.getListMutableLiveData().getValue();
        if (questionsList == null || questionsList.size() == 0) {
            return;
        }
        Questions.Data item = questionsList.get(position);
        if (item.type == 3) {
            rlQuestions.setVisibility(View.GONE);
            etComment.setVisibility(View.VISIBLE);
            etComment.setOnTouchListener((v, event) -> {
                if (etComment.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            });
        } else {
            rlQuestions.setVisibility(View.VISIBLE);
            etComment.setVisibility(View.GONE);
            if (item.type == 1) {
                ratingBar.setVisibility(View.GONE);
                segmentedButtonGroup.setVisibility(View.VISIBLE);
            } else {
                ratingBar.setVisibility(View.VISIBLE);
                segmentedButtonGroup.setVisibility(View.GONE);
            }
            tvQuestions.setText(item.question);
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });
    }

    private void setAdapter(ArrayList<Questions.Data> questionsList) {
        if (questionsList != null && questionsList.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new RecyclerviewAdapter(questionsList, R.layout.item_agent_review, this);
            }

            mAdapter.doRefresh(questionsList);

            if (binding.rvQuestions.getAdapter() == null) {
                binding.rvQuestions.setAdapter(mAdapter);
            }
        } else {
            if (mAdapter != null)
                mAdapter.doRefresh(questionsList);
        }
    }
}
