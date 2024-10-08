package com.nojom.fragment.projects;

import static com.nojom.util.Constants.CLIENT_ATTACHMENT;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahamed.multiviewadapter.SimpleRecyclerAdapter;
import com.nojom.R;
import com.nojom.adapter.binder.FilesBinder;
import com.nojom.databinding.FragmentProjectDetailsBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ProjectByID;
import com.nojom.ui.projects.ProjectDetailsActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class ProjectDetailsFragmentVM extends AndroidViewModel {
    private FragmentProjectDetailsBinding binding;
    private BaseFragment fragment;
    private ProjectByID projectData;
    private List<ProjectByID.Attachments> attachmentsList;
    private SimpleRecyclerAdapter myFilesAdapter;

    ProjectDetailsFragmentVM(Application application, FragmentProjectDetailsBinding projectDetailsBinding, BaseFragment projectDetailsFragment) {
        super(application);
        binding = projectDetailsBinding;
        fragment = projectDetailsFragment;
        initData();
    }

    private void initData() {
        if (fragment.activity != null) {
            projectData = ((ProjectDetailsActivity) fragment.activity).getProjectData();
        }

        if (projectData != null) {
            binding.tvJobTitle.setText(projectData.title);
            binding.tvDetails.setText(projectData.description);
            if (!TextUtils.isEmpty(projectData.deadline)) {
                String deadline = projectData.deadline.replace("T", " ");
                binding.tvDeadline.setText(Utils.setDeadLine1(fragment.activity, deadline));
            } else {
                binding.tvDeadline.setText("-");
            }

            String budget;
            if (projectData.clientRateId == 0) {
                budget = fragment.activity.getCurrency().equals("SAR") ? Utils.getDecimalValue("" + projectData.jobBudget) + " "+fragment.getString(R.string.sar) : fragment.getString(R.string.dollar) + Utils.getDecimalValue("" + projectData.jobBudget);
            } else {
                if (projectData.clientRate != null) {
                    if (projectData.clientRate.rangeTo != null && projectData.clientRate.rangeTo != 0) {
                        budget = fragment.activity.getCurrency().equals("SAR") ? Utils.getDecimalValue("" + projectData.clientRate.rangeFrom) + " "+fragment.getString(R.string.sar)
                                + " - " + Utils.getDecimalValue("" + projectData.clientRate.rangeTo) + " "+fragment.getString(R.string.sar)
                                : fragment.getString(R.string.dollar) + Utils.getDecimalValue("" + projectData.clientRate.rangeFrom)
                                + " - "+fragment.getString(R.string.dollar) + Utils.getDecimalValue("" + projectData.clientRate.rangeTo);
                    } else {
                        budget = fragment.activity.getCurrency().equals("SAR") ? Utils.getDecimalValue("" + projectData.clientRate.rangeFrom) + " "+fragment.getString(R.string.sar)
                                : fragment.getString(R.string.dollar) + Utils.getDecimalValue("" + projectData.clientRate.rangeFrom);
                    }
                } else if (projectData.jobBudget != null) {
                    budget = fragment.activity.getCurrency().equals("SAR") ? Utils.getDecimalValue("" + projectData.jobBudget) + " "+fragment.getString(R.string.sar)
                            : fragment.getString(R.string.dollar) + Utils.getDecimalValue("" + projectData.jobBudget);
                } else {
                    budget = fragment.getString(R.string.free);
                }
            }

//            if (!TextUtils.isEmpty(budget))
//                binding.tvProjectBudget.setText(budget);

            if (projectData.scName != null)
                binding.tvService.setText(projectData.scName);
//            if (projectData.jobPayTypeName != null)
//                binding.tvPaytype.setText(String.format("( %s )", projectData.jobPayTypeName));
            if (projectData.getServiceName(fragment.activity.language) != null)
                binding.tvSkills.setText(projectData.getServiceName(fragment.activity.language));
            if (projectData.jobPostStateId == (Constants.BIDDING)) {
                binding.tvEdit.setVisibility(View.GONE);
            }
            binding.tvJobId.setText(String.format(Locale.US, "%d", projectData.id));

            binding.noData.tvNoTitle.setText(fragment.activity.getString(R.string.no_files));
            binding.noData.tvNoDescription.setText(fragment.activity.getString(R.string.no_attached_file_desc));

            attachmentsList = new ArrayList<>();
            binding.rvMyFiles.setLayoutManager(new LinearLayoutManager(fragment.activity));
            attachmentsList = projectData.attachments;
            setFileAdapter();
        }

    }

    private void setFileAdapter() {
        if (attachmentsList != null && attachmentsList.size() > 0) {
            if (myFilesAdapter == null) {
                myFilesAdapter = new SimpleRecyclerAdapter<>(new FilesBinder(CLIENT_ATTACHMENT));
            }

            if (binding.rvMyFiles.getAdapter() == null) {
                binding.rvMyFiles.setAdapter(myFilesAdapter);
            }

            myFilesAdapter.setData(attachmentsList);
            binding.rvMyFiles.setVisibility(View.VISIBLE);
            binding.noData.llNoData.setVisibility(View.GONE);
        } else {
            binding.rvMyFiles.setVisibility(View.GONE);
            binding.noData.llNoData.setVisibility(View.VISIBLE);
        }
    }
}
