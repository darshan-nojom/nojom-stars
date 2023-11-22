package com.nojom.ui.gigs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.SocialChannelAdapter;
import com.nojom.databinding.ActivityPlatformBinding;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;

public class PlatformActivity extends BaseActivity implements SocialChannelAdapter.OnClickPlatformListener {
    private ActivityPlatformBinding binding;
    private SocialPlatformResponse.Data selectedPlatform = null;
    private SocialChannelAdapter mAdapter;
    private ArrayList<SocialPlatformResponse.Data> socialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_platform);
        if (getIntent() != null) {
            socialList = (ArrayList<SocialPlatformResponse.Data>) getIntent().getSerializableExtra("data");
            selectedPlatform = (SocialPlatformResponse.Data) getIntent().getSerializableExtra("platform");
        }

        setUI();
    }

    private void setUI() {
        binding.progress.tvToolbarTitle.setText(getString(R.string.social_channels));
        binding.progress.imgBack.setOnClickListener(v -> finish());
        binding.progress.tvSave.setText(getString(R.string.apply));
        binding.rvCategory.setLayoutManager(new LinearLayoutManager(this));

        if (socialList != null && socialList.size() > 0) {
            setAdapter(socialList);
        }

        binding.progress.tvSave.setOnClickListener(v -> {
            selectedPlatform = mAdapter.getSelectedCategory();

            if (selectedPlatform == null) {
                toastMessage(getString(R.string.select_your_channel));
                return;
            }

            if (TextUtils.isEmpty(selectedPlatform.username)) {
                toastMessage(getString(R.string.enter_username));
                return;
            }

            if (selectedPlatform.followers == -1 || selectedPlatform.followers == 0) {
                toastMessage(getString(R.string.select_followers));
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("channel", selectedPlatform);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void setAdapter(ArrayList<SocialPlatformResponse.Data> data) {
        if (selectedPlatform != null) {
            for (SocialPlatformResponse.Data plt : data) {
                if (plt.id == selectedPlatform.id) {
                    plt.isSelected = true;
                    plt.username = selectedPlatform.username;
                    plt.followers = (selectedPlatform.followers);
                }
            }
        }
        mAdapter = new SocialChannelAdapter(this, data, this);
        binding.rvCategory.setAdapter(mAdapter);
    }

    @Override
    public void onClickPlatform(SocialPlatformResponse.Data platform) {
        selectedPlatform = platform;
    }
}
