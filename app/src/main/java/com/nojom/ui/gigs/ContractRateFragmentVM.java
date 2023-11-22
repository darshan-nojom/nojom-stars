package com.nojom.ui.gigs;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nojom.R;
import com.nojom.databinding.FragmentProjectRateBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ContractDetails;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.chat.ChatMessagesActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;

import java.util.HashMap;

class ContractRateFragmentVM extends AndroidViewModel {
    private final FragmentProjectRateBinding binding;
    private final BaseFragment fragment;
    private ContractDetails projectData;
    private final int RC_RATING = 1010;

    ContractRateFragmentVM(Application application, FragmentProjectRateBinding projectRateBinding, BaseFragment projectRateFragment) {
        super(application);
        binding = projectRateBinding;
        fragment = projectRateFragment;
        initData();
    }

    void initData() {
        if (fragment.activity != null) {
            projectData = ((ContractDetailsActivity) fragment.activity).getProjectData();
        }

//        binding.ratingClient.setScrollable(false);
        binding.ratingUser.setEnable(false);

        ProfileResponse profileData = Preferences.getProfileData(fragment.activity);
        if (profileData != null) {
            binding.tvUsername.setText(fragment.activity.getProperName(profileData.firstName, profileData.lastName, profileData.username));
            if (profileData.countryName != null)
                binding.tvUserPlace.setText(profileData.countryName);
            if (profileData.profilePic != null) {
                Glide.with(fragment.activity)
                        .load(fragment.activity.getImageUrl() + profileData.profilePic)
                        .apply(new RequestOptions().placeholder(R.drawable.dp))
                        .into(binding.imgUser);
            }

            if (projectData.agentReview != null) {
                if (projectData.agentReview.rate != null) {
                    binding.ratingUser.setRating(projectData.agentReview.rate);
                }
                binding.tvUserRate.setText(projectData.agentReview.comment);
                binding.tvUserRate.setTextColor(ContextCompat.getColor(fragment.activity, R.color.black));
            }
        }

        if (projectData != null) {
            binding.tvClientName.setText(fragment.activity.getProperName(projectData.clientDetails.first_name, projectData.clientDetails.last_name,
                    projectData.clientDetails.username));
            if (projectData.clientDetails.address != null)
                binding.tvClientPlace.setText(projectData.clientDetails.address.country);
            if (projectData.clientDetails.photo != null) {
                Glide.with(fragment.activity)
                        .load(projectData.clientDetails.profilePath + projectData.clientDetails.photo)
                        .apply(new RequestOptions().placeholder(R.drawable.dp))
                        .into(binding.imgClient);
            }

            if (projectData.clientReview != null) {
                if (projectData.clientReview.rate != null) {
                    binding.ratingClient.setRating(projectData.clientReview.rate);
                }
                binding.tvClientRate.setText(projectData.clientReview.comment);
                binding.tvClientRate.setTextColor(ContextCompat.getColor(fragment.activity, R.color.black));
            }

            if (projectData.isClientReview == 1) {
                binding.ratingClient.setIsIndicator(true);
            }
        }

        binding.ratingClient.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if (projectData.isClientReview == 0) {
                Intent i = new Intent(fragment.activity, ClientGigReviewActivity.class);
                i.putExtra(Constants.USER_DATA, projectData);
                fragment.startActivityForResult(i, RC_RATING);
            }
        });

        binding.tvClientChat.setOnClickListener(v -> {
            try {
//            if (projectData != null && projectData.jobPostBids != null) {
                HashMap<String, String> chatMap = new HashMap<>();
                chatMap.put(Constants.RECEIVER_ID, projectData.clientProfileID + "");
                chatMap.put(Constants.RECEIVER_NAME, projectData.clientDetails.username);

                if (projectData.clientDetails != null && !TextUtils.isEmpty(projectData.clientDetails.photo)) {
                    chatMap.put(Constants.RECEIVER_PIC, projectData.clientDetails.profilePath + projectData.clientDetails.photo);
                } else {
                    chatMap.put(Constants.RECEIVER_PIC, "");
                }

                chatMap.put(Constants.SENDER_ID, fragment.activity.getUserID() + "");
                chatMap.put(Constants.SENDER_NAME, fragment.activity.getProfileData().username);
                chatMap.put(Constants.SENDER_PIC, fragment.activity.getImageUrl() + fragment.activity.getProfileData().profilePic);
                chatMap.put(Constants.PROJECT_ID, String.valueOf(projectData.id));
                chatMap.put("isProject", "1");//1 mean updated record
                chatMap.put("projectType", "1");//2=job & 1= gig
                chatMap.put("isDetailScreen", "true");


                Intent i = new Intent(fragment.activity, ChatMessagesActivity.class);

                i.putExtra(Constants.CHAT_ID, projectData.clientProfileID + "-" + fragment.activity.getUserID());  // ClientId - AgentId
                i.putExtra(Constants.CHAT_DATA, chatMap);
                fragment.startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
//            }
        });
    }

    public void refreshPage() {
        initData();
    }

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_RATING) {
                ((ContractDetailsActivity) fragment.activity).getProjectById(true);
            }
        }
    }
}
