package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.adapter.AvailabilityAdapter;
import com.nojom.adapter.WorkbaseAdapter;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityEditAvailabilityBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Preferences;

public class EditAvailabilityActivity extends BaseActivity implements APIRequest.APIRequestListener, WorkbaseAdapter.WorkbaseClickListener {

    private EditAvailabilityActivityVM editAvailabilityActivityVM;
    private ActivityEditAvailabilityBinding binding;
    boolean isNeedToUpdate = false;
    private int selectedWorkbasePosition = 0;
    private WorkbaseAdapter mWorkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_availability);

        editAvailabilityActivityVM = ViewModelProviders.of(this).get(EditAvailabilityActivityVM.class);
        editAvailabilityActivityVM.init(this);
        initData();
    }

    private void initData() {

        binding.rvWorkType.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));
        binding.rvAvailability.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));

        binding.shimmerLayoutAvail.setVisibility(View.VISIBLE);
        binding.shimmerLayoutAvail.startShimmer();
        binding.shimmerLayoutWork.setVisibility(View.VISIBLE);
        binding.shimmerLayoutWork.startShimmer();

        editAvailabilityActivityVM.getWorkTypeList();

        binding.rvWorkType.setNestedScrollingEnabled(false);
        binding.rvAvailability.setNestedScrollingEnabled(false);

        binding.toolbar.imgBack.setOnClickListener(view -> {
            if (isNeedToUpdate) {
                getProfile();
            }
            onBackPressed();
        });

        editAvailabilityActivityVM.getMutableLiveDataWork().observe(this, workTypeList -> {
            binding.shimmerLayoutWork.setVisibility(View.GONE);
            binding.shimmerLayoutWork.stopShimmer();
            binding.rvWorkType.setVisibility(View.VISIBLE);
            mWorkAdapter = new WorkbaseAdapter(EditAvailabilityActivity.this, workTypeList, this);
            binding.rvWorkType.setAdapter(mWorkAdapter);
        });

        editAvailabilityActivityVM.getMutableLiveDataAvailable().observe(this, availabilityList -> {
            binding.shimmerLayoutAvail.setVisibility(View.GONE);
            binding.shimmerLayoutAvail.stopShimmer();
            binding.rvAvailability.setVisibility(View.VISIBLE);
            AvailabilityAdapter mAvailableAdapter = new AvailabilityAdapter(EditAvailabilityActivity.this, availabilityList);
            binding.rvAvailability.setAdapter(mAvailableAdapter);
        });

        ProfileResponse profileResponse = getProfileData();
        if (profileResponse != null && profileResponse.profilePayTypes != null) {
            editAvailabilityActivityVM.getMutableLiveDataAvailable().postValue(profileResponse.profilePayTypes);
        }
    }

    @Override
    public void onBackPressed() {
        if (isNeedToUpdate) {
            getProfile();
        }
        super.onBackPressed();
        finishToRight();
    }

    public void isRefresh(boolean isNeedToUpdate) {
        this.isNeedToUpdate = isNeedToUpdate;
    }


    private void updateWorkbase(String workbaseIds, int selectedPos) {
        if (!isNetworkConnected()) {
            notifyWorkbaseAdapter(selectedPos);
            return;
        }
        selectedWorkbasePosition = selectedPos;
        CommonRequest.UpdateWorkbase updateWorkbase = new CommonRequest.UpdateWorkbase();
        updateWorkbase.setWorkbase(workbaseIds);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(this, API_UPDATE_PROFILE, updateWorkbase.toString(), true, this);

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        if (urlEndPoint.equalsIgnoreCase(API_GET_PROFILE)) {
            toastMessage(message);
            ProfileResponse profileObject = ProfileResponse.getProfileObject(decryptedData);
            if (profileObject != null) {
                Preferences.setProfileData(this, profileObject);
            }
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {

    }

    @Override
    public void onClickWorkbase(String value, int pos) {
        updateWorkbase(value, pos);
    }

    private void notifyWorkbaseAdapter(int selectedPos) {
//        if (mWorkAdapter != null) {
//            mWorkAdapter.updateData(selectedPos);
//            mWorkAdapter.notifyDataSetChanged();
//        }
    }
}
