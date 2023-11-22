package com.nojom.fragment.chat;

import android.app.Application;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;

import com.nojom.R;
import com.nojom.databinding.FragmentChatManagerBinding;
import com.nojom.fragment.BaseFragment;

class ManagerFragmentVM extends AndroidViewModel implements View.OnClickListener {
    private FragmentChatManagerBinding binding;
    private BaseFragment fragment;

    ManagerFragmentVM(Application application, FragmentChatManagerBinding chatManagerBinding, BaseFragment managerFragment) {
        super(application);
        binding = chatManagerBinding;
        fragment = managerFragment;
        initData();
    }

    private void initData() {
        binding.rlReportProblem.setOnClickListener(this);
        binding.rlIssueApp.setOnClickListener(this);
        binding.rlSuggesting.setOnClickListener(this);
        binding.rlOther.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_report_problem:
                fragment.activity.showFeedbackDialog(1, fragment.getString(R.string.report_a_problem));
                break;
            case R.id.rl_issue_app:
                fragment.activity.showFeedbackDialog(2, fragment.getString(R.string.issue_with_app));
                break;
            case R.id.rl_suggesting:
                fragment.activity.showFeedbackDialog(3, fragment.getString(R.string.suggestions));
                break;
            case R.id.rl_other:
                fragment.activity.showFeedbackDialog(4, fragment.getString(R.string.other));
                break;
        }
    }
}
