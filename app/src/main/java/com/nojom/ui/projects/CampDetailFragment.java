package com.nojom.ui.projects;

import static com.nojom.adapter.CampaignAdapter2.capitalizeWords;
import static com.nojom.util.Constants.API_ACC_REJECT_CAMP;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.nojom.R;
import com.nojom.adapter.PlatformDetailAdapter2;
import com.nojom.api.APIRequest;
import com.nojom.api.CampaignListener;
import com.nojom.databinding.DialogApproveBinding;
import com.nojom.databinding.FragmentCampDetailBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.CampList;
import com.nojom.model.CampListData;
import com.nojom.ui.chat.ChatMessagesActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class CampDetailFragment extends BaseFragment implements CampaignListener {
    private FragmentCampDetailBinding binding;
    private CampList campList;
    private final PrettyTime p = new PrettyTime();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camp_detail, container, false);
        campList = ((CampaignDetailActivity2) activity).campList;
        renderView();
        return binding.getRoot();
    }

    private void renderView() {

        binding.txtCampaignTitle.setText("" + campList.campaignTitle);
        binding.tvReceiverName.setText("" + campList.client_name.en + " " + campList.client_name.ar);
        if (campList.star_details != null) {

            int selTab = ((CampaignDetailActivity2) activity).selectedTab;
            switch (selTab) {
                case 0://in progress
                    binding.linButtonView.setVisibility(View.GONE);
                    binding.relCampComplete.setVisibility(View.VISIBLE);
                    break;
                case 1://pending
                    if (campList.star_details.req_status.equals("approved") || campList.star_details.req_status.equals("rejected")) {
                        binding.linButtonView.setVisibility(View.GONE);
                        binding.relCampComplete.setVisibility(View.GONE);
                    } else {
                        binding.linButtonView.setVisibility(View.VISIBLE);
                        binding.relCampComplete.setVisibility(View.GONE);
                    }

                    break;
                case 2://history
                    binding.linButtonView.setVisibility(View.GONE);
                    binding.relCampComplete.setVisibility(View.GONE);
                    break;
            }

//            binding.txtStars.setText(campList.profiles.size() + " " + activity.getString(R.string.stars));

            //bind adapter
//            TimelineAdapter adapter = new TimelineAdapter(activity, campList.profiles);
//            binding.rvTracks.setAdapter(adapter);
        }
        Date date1 = Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", campList.timestamp);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dfFinal2;
        if (activity.language.equals("ar")) {
            dfFinal2 = new SimpleDateFormat("dd MMM,yyyy");
        } else {
            dfFinal2 = new SimpleDateFormat("MMM dd,yyyy");
        }


        if (date1 != null) {
            if (activity.printDifference(date1, date).equalsIgnoreCase("0")) {
                String result = p.format(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", campList.timestamp));
                binding.txtDate.setText("Due Date: " + result);
            } else {
                String finalDate = dfFinal2.format(date1);
                binding.txtDate.setText("Due Date: " + finalDate);
            }

//            String[] time = campList.timestamp.split("T")[1].split(":");
//            binding.txtTime.setText(time[0] + ":" + time[1]);
        }

        if (!TextUtils.isEmpty(campList.campaignBrief)) {
            binding.txtDetails.setText(campList.campaignBrief);
        }

        if (campList.campaignId != null) {
            binding.txtCampId.setText(String.format(Locale.US, "#%s", campList.campaignId));
        } else if (campList.jp_id != null) {
            binding.txtCampId.setText(String.format(Locale.US, "%s", campList.jp_id));
        }

        if (campList.services != null && campList.services.size() > 0) {
            PlatformDetailAdapter2 adapter = new PlatformDetailAdapter2(activity, campList.services);
            binding.rvPlatform.setAdapter(adapter);
        }

        if (!TextUtils.isEmpty(campList.campaignStatus)) {
//            binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.yellow_bg_20));
//            binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.black));
            binding.tvStatus.setText(capitalizeWords(campList.campaignStatus));
        }

        switch (campList.campaignStatus) {
            case "completed":
                binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.blue_bg_20));
                binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.white));
                break;
            case "in_progress":
                binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.green_button_bg_20));
                binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.white));
                break;
            case "pending":
                binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.orangelight_bg_20));
                binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.white));
                break;
            case "canceled":
                binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.red_bg_20));
                binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.white));
                break;
        }

        if (!TextUtils.isEmpty(campList.campaignAttachmentUrl)) {
            String fName = getFileNameFromUrl(campList.campaignAttachmentUrl);
            binding.txtFileName.setText("" + fName + " " + fName.length() + " " + getString(R.string.kb));
        }

        Glide.with(activity).load(campList.client_profile_picture).error(R.drawable.dp).into(binding.imgProfile);

        if (campList.getActualPrice() != 0 && campList.getActualPrice() > 0) {
            binding.tvBudget.setText(Utils.decimalFormat(String.valueOf(campList.getActualPrice())) + " " + activity.getString(R.string.sar));
        }

        binding.relApproved.setOnClickListener(view -> updateStatus(campList.campaignId, "approve"));
        binding.relReject.setOnClickListener(view -> postDoneDialog(false));

        binding.progressBar.setProgress((int) campList.progress);
        binding.txtPercent.setText(campList.progress + "%");

        binding.txtChat.setOnClickListener(view -> {
            HashMap<String, String> chatMap = new HashMap<>();
            chatMap.put(Constants.RECEIVER_ID, campList.star_details.star_id + "");
            chatMap.put(Constants.RECEIVER_NAME, campList.client_name.en + " " + campList.client_name.ar);

            if (campList.client_profile_picture != null && !TextUtils.isEmpty(campList.client_profile_picture)) {
                chatMap.put(Constants.RECEIVER_PIC, campList.client_profile_picture);
            } else {
                chatMap.put(Constants.RECEIVER_PIC, "");
            }

            chatMap.put(Constants.SENDER_ID, activity.getUserID() + "");
            chatMap.put(Constants.SENDER_NAME, activity.getProfileData() != null ? activity.getProfileData().username : "");
            chatMap.put(Constants.SENDER_PIC, activity.getImageUrl() + (activity.getProfileData() != null ? activity.getProfileData().profilePic : ""));
//            chatMap.put(Constants.PROJECT_ID, String.valueOf(projectData.id));
            chatMap.put("isProject", "1");//1 mean updated record
            chatMap.put("projectType", "2");//2=job & 1= gig
            chatMap.put("isDetailScreen", "true");


            Intent i = new Intent(activity, ChatMessagesActivity.class);

            i.putExtra(Constants.CHAT_ID, activity.getUserID() + "-" + campList.star_details.star_id);  // ClientId - AgentId
            i.putExtra(Constants.CHAT_DATA, chatMap);
            if (activity.getIsVerified() == 1) {
                startActivity(i);
            } else {
                activity.toastMessage(getString(R.string.verification_is_pending_please_complete_the_verification_first_before_chatting_with_them));
            }
        });

        binding.relCampComplete.setOnClickListener(view -> campaignDone(campList.campaignId));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private String getFileNameFromUrl(String url) {
        // Use Uri class to parse the URL
        Uri uri = Uri.parse(url);

        // Get the last segment of the path, which is the file name
        return uri.getLastPathSegment();
    }

    String reqUrl;

    private void updateStatus(Integer campId, String status) {
        if (!activity.isNetworkConnected()) {
            return;
        }
        String reqUrl = API_ACC_REJECT_CAMP + campId + "/request/" + status;

        APIRequest apiRequest = new APIRequest();
        apiRequest.updateCampStatus(this, activity, reqUrl, null);
    }

    private void campaignDone(Integer campId) {
        if (!activity.isNetworkConnected()) {
            return;
        }
        reqUrl = API_ACC_REJECT_CAMP + campId + "/done";

        APIRequest apiRequest = new APIRequest();
        apiRequest.updateCampStatus(this, activity, reqUrl, null);
    }

    @Override
    public void successResponse(CampListData responseBody, String url, String message) {
        if (url.equals(reqUrl)) {//campaign done case
            activity.finish();
        } else {//approve reject case
            activity.finish();
            binding.linButtonView.setVisibility(View.GONE);
            binding.relCampComplete.setVisibility(View.VISIBLE);
        }
        Preferences.writeBoolean(activity, "refresh", true);
        activity.toastMessage(message);
    }

    @Override
    public void failureResponse(Throwable throwable, String url, String message) {
        activity.toastMessage(message);
        if (url.equals(reqUrl)) {//campaign done case

        } else {

        }
    }

    private void postDoneDialog(boolean isApprove) {
        final Dialog dialog = new Dialog(activity);
        dialog.setTitle(null);
        DialogApproveBinding bindingDialog = DialogApproveBinding.inflate(LayoutInflater.from(activity));
        dialog.setContentView(bindingDialog.getRoot());
        dialog.setCancelable(true);

        if (isApprove) {
//            bindingDialog.imgDone.setImageResource(R.drawable.ic_pay_fail);
//            bindingDialog.txtTitle.setText(getString(R.string.payment_failed));
//            bindingDialog.txtDesc.setText(getString(R.string.unfortunately_we_were_unable_to_process_your_payment_please_try_again_or_use_a_different_payment_method) + "\n");
//            bindingDialog.txtDesc1.setText(getString(R.string.if_you_continue_to_experience_issues_our_support_team_is_here_to_help));
//            bindingDialog.btnContinuePrice.setText(getString(R.string.try_again));
        } else {
//            bindingDialog.txtTitle.setText(getString(R.string.successful));
        }

        bindingDialog.btnYes.setOnClickListener(view -> {
            if (isApprove) {
                dialog.dismiss();
            } else {
                updateStatus(campList.campaignId, "reject");
                dialog.dismiss();
            }
        });
        bindingDialog.btnNo.setOnClickListener(view -> {
            if (isApprove) {
                dialog.dismiss();
            } else {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        lp.gravity = Gravity.CENTER;
        lp.width = (int) (displaymetrics.widthPixels * 0.9);
//        lp.height = (int) (displaymetrics.heightPixels * 0.7);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        dialog.setOnDismissListener(dialog1 -> {


        });
    }
}
