package com.nojom.fragment.projects;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.nojom.R;
import com.nojom.adapter.PlatformDetailAdapter;
import com.nojom.databinding.ActivityCampaignDetailsBinding;
import com.nojom.model.Projects;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

public class CampaignDetailActivity extends BaseActivity {

    private Projects.Data campList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCampaignDetailsBinding detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_campaign_details);
//        projectDetailsActivityVM = new ProjectDetailsActivityVM(Task24Application.getInstance(), detailsBinding, this);

        if (getIntent() != null) {
            campList = (Projects.Data) getIntent().getSerializableExtra(Constants.PROJECT);
        }

        if (campList != null) {
            detailsBinding.tvJobTitle.setText(campList.title);
            detailsBinding.tvProjectBudget.setText(campList.amount + "");
            detailsBinding.tvPaytype.setText(getString(R.string.sar));
            if (!TextUtils.isEmpty(campList.created_at)) {
                String deadline = campList.created_at.replace("T", " ");
                detailsBinding.tvDeadline.setText(Utils.setDeadLine(this, deadline));
            }

            detailsBinding.tvDetails.setText(campList.brief + "");
            detailsBinding.tvJobId.setText(campList.id + "");

            PlatformDetailAdapter adapter = new PlatformDetailAdapter(this, campList.services);
            detailsBinding.rvPlatform.setAdapter(adapter);

            Glide.with(this).load(campList.attachment)
                    .centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(detailsBinding.imgCampaign);
        }

        detailsBinding.imgBack.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
