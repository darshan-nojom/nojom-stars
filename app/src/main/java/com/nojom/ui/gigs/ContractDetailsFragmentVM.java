package com.nojom.ui.gigs;

import static com.nojom.util.Constants.GIG_ATTACHMENT;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahamed.multiviewadapter.SimpleRecyclerAdapter;
import com.nojom.R;
import com.nojom.adapter.GigAdapter;
import com.nojom.adapter.binder.FilesBinder;
import com.nojom.databinding.FragmentContractDetailsBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ContractDetails;
import com.nojom.model.ProjectByID;
import com.nojom.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class ContractDetailsFragmentVM extends AndroidViewModel {
    private FragmentContractDetailsBinding binding;
    private BaseFragment fragment;
    private ContractDetails projectData;
    private List<ProjectByID.Attachments> attachmentsList;
    private SimpleRecyclerAdapter myFilesAdapter;

    ContractDetailsFragmentVM(Application application, FragmentContractDetailsBinding projectDetailsBinding, BaseFragment projectDetailsFragment) {
        super(application);
        binding = projectDetailsBinding;
        fragment = projectDetailsFragment;
        initData();
    }

    private void initData() {
        if (fragment.activity != null) {
            projectData = ((ContractDetailsActivity) fragment.activity).getProjectData();
        }

        if (projectData != null) {

            if (TextUtils.isEmpty(projectData.clientJobDescribe)) {
                binding.tvDescription.setVisibility(View.VISIBLE);
            } else {
                binding.tvJobTitle.setText(projectData.clientJobDescribe);
            }
            binding.tvJobId.setText(String.format(Locale.US,"%d", projectData.id));

            binding.noData.tvNoTitle.setText(fragment.activity.getString(R.string.no_files));
            binding.noData.tvNoDescription.setText(fragment.activity.getString(R.string.no_attached_file_desc));

            attachmentsList = new ArrayList<>();
            binding.rvMyFiles.setLayoutManager(new LinearLayoutManager(fragment.activity));
            attachmentsList = projectData.attachments;
            setFileAdapter();

            binding.tvGigTitle.setText(projectData.gigTitle);
            binding.tvGigDetails.setText(projectData.gigDescription);
            binding.tvGigPrice.setText("$" + Utils.getDecimalValue("" + projectData.totalPrice));

            binding.tvRevisionsDays.setText("" + projectData.revisions);
            binding.tvQuantity.setText("" + projectData.quantity);

            if (fragment.activity != null && ((ContractDetailsActivity) fragment.activity).getGigType() != null
                    && (((ContractDetailsActivity) fragment.activity).getGigType().equalsIgnoreCase("1")
                    || ((ContractDetailsActivity) fragment.activity).getGigType().equalsIgnoreCase("3"))) {//custom
                binding.linPackageDetails.setVisibility(View.GONE);
                binding.linCustomPackageDetails.setVisibility(View.VISIBLE);
                binding.linPrice.setVisibility(View.GONE);
                binding.tvDeliveryDaysCustom.setText(Utils.priceWith$(Math.round(projectData.deadlinePrice)));
                binding.txtDelDays.setText(fragment.activity.getString(R.string.delivery_time)
                        + " (" + Math.round(projectData.deadlineValue) + " " + (projectData.deadlineType.equals("1") ? "" + fragment.activity.getString(R.string.hours)
                        : "" + fragment.activity.getString(R.string.days)
                ) + ")");

                if (projectData.socialPlatform != null && projectData.socialPlatform.size() > 0) {
                    binding.linPlatform.setVisibility(View.VISIBLE);
                    binding.txtPlatformLbl.setText(projectData.socialPlatform.get(0).name + " (" + Utils.getPlatformTxt(projectData.socialPlatform.get(0).followers) + ")");
                    binding.tvPlatPrice.setText(projectData.socialPlatform.get(0).username);
                }

                if (projectData.customPackages != null && projectData.customPackages.size() > 0) {
                    setRequirement();
                }

            } else {
                binding.linPackageDetails.setVisibility(View.VISIBLE);
                binding.linCustomPackageDetails.setVisibility(View.GONE);
                binding.tvDeliveryDays.setText(projectData.duration + " " + fragment.activity.getString(R.string.days)
                );

                binding.rvRequirements.setLayoutManager(new LinearLayoutManager(fragment.activity));

                try {

                    binding.tvPackageTitle.setText(projectData.gigPackageName);
                    binding.tvPackageDescription.setText(projectData.gigPackageDescription);

                    if (projectData.requirements != null && projectData.requirements.size() > 0) {
                        binding.rvRequirements.setVisibility(View.VISIBLE);
                        GigAdapter adapter = new GigAdapter(fragment.activity, projectData.requirements);
                        binding.rvRequirements.setAdapter(adapter);
                    } else {
                        binding.rvRequirements.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    DecimalFormat format = new DecimalFormat("0.##");

    private void setRequirement() {
        LayoutInflater inflater = (LayoutInflater) fragment.activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        for (ContractDetails.CustomPackages reqData : projectData.customPackages) {
            View view = inflater.inflate(R.layout.item_custom_gig_package, null);
            TextView tvReqTitle = view.findViewById(R.id.tv_req_name);
            TextView tvAmnt = view.findViewById(R.id.tv_req_amount);
            if (!TextUtils.isEmpty(reqData.featureName)) {
                tvReqTitle.setText(String.format("%s (%s)", reqData.name, reqData.featureName));
            } else {
                tvReqTitle.setText(reqData.name);
            }


            if (reqData.inputType.equals("1") && reqData.gigRequirementType.equals("1")) {//fixed with qty
                tvAmnt.setText("$" + format.format(Float.parseFloat(reqData.price)) + " x " + reqData.quantity);
            } else {
                tvAmnt.setText("$" + format.format(Float.parseFloat(reqData.price)));
            }


            binding.linRequirementsCustom.addView(view);
        }
    }

    private void setFileAdapter() {
        if (attachmentsList != null && attachmentsList.size() > 0) {
            if (myFilesAdapter == null) {
                myFilesAdapter = new SimpleRecyclerAdapter<>(new FilesBinder(GIG_ATTACHMENT, projectData.clientAttachmentsPath));
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
