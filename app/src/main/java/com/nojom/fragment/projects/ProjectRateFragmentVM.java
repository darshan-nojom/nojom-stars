package com.nojom.fragment.projects;

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
import com.nojom.model.ProfileResponse;
import com.nojom.model.ProjectByID;
import com.nojom.ui.chat.ChatMessagesActivity;
import com.nojom.ui.clientprofile.LeaveReviewActivity;
import com.nojom.ui.projects.ProjectDetailsActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;

import java.util.HashMap;

class ProjectRateFragmentVM extends AndroidViewModel {
    private final FragmentProjectRateBinding binding;
    private final BaseFragment fragment;
    private ProjectByID projectData;
    private final int RC_RATING = 1010;

    ProjectRateFragmentVM(Application application, FragmentProjectRateBinding projectRateBinding, BaseFragment projectRateFragment) {
        super(application);
        binding = projectRateBinding;
        fragment = projectRateFragment;
        initData();
    }

    void initData() {
        if (fragment.activity != null) {
            projectData = ((ProjectDetailsActivity) fragment.activity).getProjectData();
        }

//        binding.ratingClient.setScrollable(false);
        binding.ratingUser.setEnable(false);

        ProfileResponse profileData = Preferences.getProfileData(fragment.activity);
        if (profileData != null) {
            binding.tvUsername.setText(fragment.activity.getProperName(profileData.firstName, profileData.lastName, profileData.username));
            if (profileData.getCountryName(fragment.activity.language) != null)
                binding.tvUserPlace.setText(profileData.getCountryName(fragment.activity.language));
            if (profileData.profilePic != null) {
                Glide.with(fragment.activity)
                        .load(fragment.activity.getImageUrl() + profileData.profilePic)
                        .apply(new RequestOptions().placeholder(R.drawable.dp))
                        .into(binding.imgUser);
            }

            if (projectData != null && projectData.agentReview != null) {
                if (projectData.agentReview.rate != null) {
                    binding.ratingUser.setRating(projectData.agentReview.rate);
                }
                binding.tvUserRate.setText(projectData.agentReview.comment);
                binding.tvUserRate.setTextColor(ContextCompat.getColor(fragment.activity, R.color.black));
            }
        }

        if (projectData != null) {
            binding.tvClientName.setText(fragment.activity.getProperName(projectData.clientFirstName, projectData.clientLastName,
                    projectData.clientUsername));
            if (projectData.getCountryName(fragment.activity.language) != null)
                binding.tvClientPlace.setText(projectData.getCountryName(fragment.activity.language));
            if (projectData.clientPhotos != null) {
                Glide.with(fragment.activity)
                        .load(fragment.activity.getClientImageUrl() + projectData.clientPhotos)
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

            if (projectData.review == 1) {
                binding.ratingClient.setIsIndicator(true);
            }
        }

        binding.ratingClient.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if (projectData.review == 0) {
                Intent i = new Intent(fragment.activity, LeaveReviewActivity.class);
                i.putExtra(Constants.USER_DATA, projectData);
                fragment.startActivityForResult(i, RC_RATING);
            }
        });

        binding.tvClientChat.setOnClickListener(v -> {
            if (projectData != null && projectData.jobPostBids != null) {
                HashMap<String, String> chatMap = new HashMap<>();
                chatMap.put(Constants.RECEIVER_ID, projectData.clientId + "");
                chatMap.put(Constants.RECEIVER_NAME, projectData.clientUsername);

                if (projectData.clientPhotos != null && !TextUtils.isEmpty(projectData.clientPhotos)) {
                    chatMap.put(Constants.RECEIVER_PIC, fragment.activity.getClientImageUrl() + projectData.clientPhotos);
                } else {
                    chatMap.put(Constants.RECEIVER_PIC, "");
                }

                chatMap.put(Constants.SENDER_ID, projectData.jobPostBids.profileId + "");
                chatMap.put(Constants.SENDER_NAME, fragment.activity.getProfileData().username);
                chatMap.put(Constants.SENDER_PIC, fragment.activity.getImageUrl() + fragment.activity.getProfileData().profilePic);
                chatMap.put(Constants.PROJECT_ID, String.valueOf(projectData.id));
                chatMap.put("isProject", "1");//1 mean updated record
                chatMap.put("projectType", "2");//2=job & 1= gig
                chatMap.put("isDetailScreen", "true");


                Intent i = new Intent(fragment.activity, ChatMessagesActivity.class);

                i.putExtra(Constants.CHAT_ID, projectData.clientId + "-" + projectData.jobPostBids.profileId);  // ClientId - AgentId
                i.putExtra(Constants.CHAT_DATA, chatMap);
                if (fragment.activity.getIsVerified() == 1) {
                    fragment.startActivity(i);
                } else {
                    fragment.activity.toastMessage(fragment.getString(R.string.verification_is_pending_please_complete_the_verification_first_before_chatting_with_them));
                }
            }
        });
    }

    public void refreshPage() {
        initData();
    }

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_RATING) {
                ((ProjectDetailsActivity) fragment.activity).getProjectById(true);
            }
        }
    }
}
