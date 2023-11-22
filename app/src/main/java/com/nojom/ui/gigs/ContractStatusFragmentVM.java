package com.nojom.ui.gigs;

import static com.nojom.util.Constants.API_ACCEPT_CONTRACT;
import static com.nojom.util.Constants.API_CANCEL_CONTRACT;
import static com.nojom.util.Constants.API_VIEW_CLIENT_PROFILE;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nojom.R;
import com.nojom.adapter.ProjectTypeAdapter;
import com.nojom.api.APIRequest;
import com.nojom.databinding.FragmentProjectStatusBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ContractDetails;
import com.nojom.model.ProfileClient;
import com.nojom.model.ProjectType;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.balance.BalanceActivity;
import com.nojom.ui.chat.ChatMessagesActivity;
import com.nojom.ui.clientprofile.EmployerProfileActivity;
import com.nojom.ui.jobs.PlaceBidActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

class ContractStatusFragmentVM extends AndroidViewModel implements View.OnClickListener, APIRequest.APIRequestListener, APIRequest.JWTRequestResponseListener {
    private final FragmentProjectStatusBinding binding;
    private final BaseFragment fragment;
    private ContractDetails projectData;
    private final int WAITING_FOR_DEPOSIT = 2;
    private long tempSec;
    private Timer t;

    ContractStatusFragmentVM(Application application, FragmentProjectStatusBinding projectStatusBinding, BaseFragment projectStatusFragment) {
        super(application);
        binding = projectStatusBinding;
        fragment = projectStatusFragment;
        initData();
    }

    private void initData() {
        binding.tvChat.setOnClickListener(this);
        binding.tvChat1.setOnClickListener(this);
        binding.tvEditBid.setOnClickListener(this);
        binding.tvAcceptJob.setOnClickListener(this);
        binding.rlProfileAcceptJob.setOnClickListener(this);
        binding.tvRejectJob.setOnClickListener(this);
        binding.rlProfile.setOnClickListener(this);
        binding.rlProfile1.setOnClickListener(this);
        binding.tvSubmitJob.setOnClickListener(this);
        binding.rlWithdraw.setOnClickListener(this);
        binding.tvChat2.setOnClickListener(this);

        if (fragment.activity != null) {
            projectData = ((ContractDetailsActivity) fragment.activity).getProjectData();
        }

        if (projectData != null) {
            int IN_PROGRESS = 3;
            int PAID = 4;
            switch (projectData.gigStateID) {
                case Constants.BIDDING:
                    binding.rlProfile.setVisibility(View.GONE);
                    binding.rlEmployee.setVisibility(View.VISIBLE);
                    break;
                case 7:
                case Constants.WAITING_FOR_ACCEPTANCE:
                    binding.rlProfile.setVisibility(View.GONE);
                    binding.rlProfileAcceptJob.setVisibility(View.VISIBLE);
                    binding.llWaitingForAcceptance.setVisibility(View.VISIBLE);
                    binding.rvAcceptJob.setLayoutManager(new LinearLayoutManager(fragment.activity));
                    setProjectData();
                    break;
                case Constants.WAITING_FOR_DEPOSIT:
                    binding.llJobStatus.setVisibility(View.VISIBLE);
                    setJobProgress(WAITING_FOR_DEPOSIT, false);
                    binding.tvJobStatusInfo.setText(fragment.activity.getString(R.string.waiting_for_deposit_info));
                    break;
                case Constants.IN_PROGRESS:
                    binding.llJobStatus.setVisibility(View.VISIBLE);
                    binding.llInprogress.setVisibility(View.VISIBLE);
                    setJobProgress(IN_PROGRESS, false);
                    setTimerTextForIncrement();
                    binding.tvJobStatusInfo.setText(fragment.activity.getString(R.string.in_progress_status_info));
                    binding.tvSubmitJob.setVisibility(View.VISIBLE);
                    break;
                case Constants.SUBMIT_WAITING_FOR_PAYMENT:
                    binding.llJobStatus.setVisibility(View.VISIBLE);
                    binding.llInprogress.setVisibility(View.VISIBLE);
                    setJobProgress(PAID, false);
                    setTimerTextForIncrement();
                    binding.tvJobStatusInfo.setText(fragment.activity.getString(R.string.complete_status_info));
                    binding.tvLiveSupport.setVisibility(View.VISIBLE);
                    break;
                case Constants.COMPLETED:
                    binding.llJobStatus.setVisibility(View.VISIBLE);
                    binding.tvTitle.setVisibility(View.GONE);
                    binding.rlProfile.setVisibility(View.GONE);
                    binding.rlWithdraw.setVisibility(View.VISIBLE);

                    double finalPrice = projectData.totalPrice - projectData.bidCharges;
                    if (projectData.totalPrice != 0) {
                        binding.tvTotal.setText(String.format("$%s", fragment.activity.get2DecimalPlaces(finalPrice)));
                    }

                    setJobProgress(PAID, true);
                    binding.tvJobStatusInfo.setText(fragment.activity.getString(R.string.completed_paid_status));

                    ClickableSpan balanceClick = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            fragment.activity.redirectActivity(BalanceActivity.class);
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(true);
                        }
                    };

                    Utils.makeLinks(binding.tvJobStatusInfo, new String[]{fragment.activity.getString(R.string.balance)
                    }, new ClickableSpan[]{balanceClick});
                    binding.tvLiveSupport.setVisibility(View.VISIBLE);
                    break;
                case Constants.CANCELLED:
                    binding.tvProjectStatus.setText(fragment.getString(R.string.this_project_has_been_closed));
                    // tvTitle.setVisibility(View.GONE);
                    binding.tvTitle.setText(fragment.activity.getString(R.string.job_status));
                    binding.rlProfile.setVisibility(View.GONE);
                    binding.llCloseProject.setVisibility(View.VISIBLE);
                    break;
                case Constants.REFUNDED:
                    binding.tvProjectStatus.setText(fragment.getString(R.string.this_project_has_been_refunded));
                    // tvTitle.setVisibility(View.GONE);
                    binding.tvTitle.setText(fragment.activity.getString(R.string.job_status));
                    binding.rlProfile.setVisibility(View.GONE);
                    binding.llCloseProject.setVisibility(View.VISIBLE);
                    break;
            }

            if (fragment.activity != null) {
                String userName = fragment.activity.getProperName(projectData.clientDetails.first_name, projectData.clientDetails.last_name, projectData.clientDetails.username);
                binding.tvName.setText(userName);
                binding.tvName1.setText(userName);
                binding.tvName2.setText(userName);


                binding.tvPlace.setText(projectData.clientDetails.address.country);
                binding.tvPlace1.setText(projectData.clientDetails.address.country);
                binding.tvPlace2.setText(projectData.clientDetails.address.country);

                if (projectData.clientDetails.photo != null) {
                    Glide.with(fragment.activity)
                            .load(projectData.clientDetails.profilePath + projectData.clientDetails.photo)
                            .apply(new RequestOptions().placeholder(R.drawable.dp))
                            .into(binding.imgUser);
                    Glide.with(fragment.activity)
                            .load(projectData.clientDetails.profilePath + projectData.clientDetails.photo)
                            .apply(new RequestOptions().placeholder(R.drawable.dp))
                            .into(binding.imgUser1);
                    Glide.with(fragment.activity)
                            .load(projectData.clientDetails.profilePath + projectData.clientDetails.photo)
                            .apply(new RequestOptions().placeholder(R.drawable.dp))
                            .into(binding.imgUser2);
                }
            }

            updateBidPrice(projectData);
        }
    }

    public void updateBidPrice(ContractDetails projectData) {
        if (projectData.totalPrice != 0) {
            binding.tvBidPrice.setText(String.format("$%s", projectData.totalPrice));
//            if (projectData.jobPayTypeId != null && projectData.jobPayTypeId == 1) {
            binding.tvPriceType.setText(fragment.getString(R.string._project));
//            } else {
//                binding.tvPriceType.setText(fragment.getString(R.string._hr));
//            }
        }
    }


    void onPauseMathod() {
        if (t != null)
            t.cancel();
    }

    private void setTimerTextForIncrement() {
        if (projectData.timer == null) {
            return;
        }
        try {
            long day = projectData.timer.days;
            long hour = projectData.timer.hours;
            long minute = projectData.timer.minutes;
            long second = projectData.timer.seconds;
            tempSec = (day * (24 * 60 * 60)) + (hour * 60 * 60) + (minute * 60) + second;

            tempSec = tempSec * 1000;

            if (projectData.timer.isDue) {
                setTimerUi(projectData.timer.days, projectData.timer.hours, projectData.timer.minutes, projectData.timer.seconds);
            } else {
                t = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {

                        if (projectData.timer.isDue) {
                            tempSec = tempSec + 1000;
                        } else {
                            tempSec = tempSec - 1000;
                        }

                        long days = TimeUnit.MILLISECONDS.toDays(tempSec);
                        long hours = TimeUnit.MILLISECONDS.toHours(tempSec) - TimeUnit.DAYS.toHours(days);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(tempSec) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(tempSec));
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(tempSec) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tempSec));

                        Log.e("TTT", "days.." + days + "D hour : " + hours + "\t minute.." + minutes + "\t second..." + seconds);

                        if (fragment.activity != null)
                            fragment.activity.runOnUiThread(() -> setTimerUi((int) days, (int) hours, (int) minutes, (int) seconds));
                    }
                };
                t.scheduleAtFixedRate(tt, new Date(), 1000);
            }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTimerUi(int day, int hour, int minute, int second) {
//         tvHours.setText((projectData.timer.isdue ? "-" : "") + Utils.doubleDigit(hour));
        if (projectData.timer.isDue) {
            binding.tvDays.setText(fragment.getString(R.string.zero));
            //tvDays.setText(Utils.doubleDigit(day));
            binding.tvMinutes.setText(fragment.getString(R.string.zero));
            binding.tvSecond.setText(fragment.getString(R.string.zero));
            // tvHours.setText((projectData.timer.isdue ? "-" : "") + Utils.doubleDigit(hour));
            binding.tvHours.setText(fragment.getString(R.string.zero));

            binding.tvDays.setTextColor(Color.RED);
            binding.tvMinutes.setTextColor(Color.RED);
            binding.tvSecond.setTextColor(Color.RED);
            binding.tvHours.setTextColor(Color.RED);
        } else {
            binding.tvDays.setText(Utils.doubleDigit(day));
            binding.tvHours.setText(Utils.doubleDigit(hour));
            binding.tvDays.setText(Utils.doubleDigit(day));
            binding.tvMinutes.setText(Utils.doubleDigit(minute));
            binding.tvSecond.setText(Utils.doubleDigit(second));
        }
    }

    private void setProjectData() {
        try {

            ArrayList<ProjectType> projectTypesList = new ArrayList<>();
            projectTypesList.add(new ProjectType(fragment.activity.getString(R.string.contract_type), fragment.activity.getString(R.string.fixed)));
            projectTypesList.add(new ProjectType(fragment.activity.getString(R.string.gig_amount), "$" + Utils.getDecimalValue("" + projectData.totalPrice)));
            projectTypesList.add(new ProjectType(fragment.activity.getString(R.string.you_will_get_total_amount), Utils.priceWith$(Utils.getDecimalValue("" + (projectData.totalPrice - projectData.bidCharges)))));
//        if (projectData.jobPostCharges != null) {
            projectTypesList.add(new ProjectType(fragment.activity.getString(R.string.influencebird_fees) /*+ projectData.jobPostCharges.bidPercentCharges + "%)"*/, Utils.priceWith$(Utils.getDecimalValue("" + projectData.bidCharges))));
//        }

            ProjectTypeAdapter projectTypeAdapter = new ProjectTypeAdapter(projectTypesList);
            binding.rvAcceptJob.setAdapter(projectTypeAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void acceptRejectJob(boolean isAccept, String reason) {
        if (!fragment.activity.isNetworkConnected())
            return;

        showAcceptRejectVisibility(View.VISIBLE, View.GONE, true);


        HashMap<String, RequestBody> map = new HashMap<>();
        APIRequest apiRequest = new APIRequest();
        RequestBody contarctId = RequestBody.create(String.valueOf(projectData.id), MultipartBody.FORM);
        map.put("contractID", contarctId);

        if (isAccept) {
            apiRequest.apiRequestBodyJWT(this, fragment.activity, API_ACCEPT_CONTRACT, map);
        } else {
            RequestBody cancelReason = RequestBody.create(reason, MultipartBody.FORM);
            map.put("cancelReason", cancelReason);
            apiRequest.apiRequestBodyJWT(this, fragment.activity, API_CANCEL_CONTRACT, map);
        }
    }

    private void setJobProgress(int progress, boolean isComplete) {
        for (int i = 1; i <= progress; i++) {
            int resTv = fragment.activity.getResources().getIdentifier("tv_" + i, "id", fragment.activity.getPackageName());
            int resTvTitle = fragment.activity.getResources().getIdentifier("txt_" + i, "id", fragment.activity.getPackageName());
            TextView tv = binding.getRoot().findViewById(resTv);
            TextView tvLbl = binding.getRoot().findViewById(resTvTitle);
            try {
                View viewLeft = null;
                View viewRight = null;

                if (i != 1) {
                    int resLeft = fragment.activity.getResources().getIdentifier("view" + i + "_left", "id", fragment.activity.getPackageName());
                    viewLeft = binding.getRoot().findViewById(resLeft);
                }

                if (i != progress) {
                    int resRight = fragment.activity.getResources().getIdentifier("view" + i + "_right", "id", fragment.activity.getPackageName());
                    viewRight = binding.getRoot().findViewById(resRight);
                }

                if (i < progress) {
                    setTextColor(tv, R.color.white);
                    setTextColor(tvLbl, R.color.black);
                    setTextBackground(tv, R.drawable.job_status_complete);
                    if (viewLeft != null) {
                        setTextBackground(viewLeft, R.color.black);
                    }
                    if (viewRight != null) {
                        setTextBackground(viewRight, R.color.black);
                    }
                } else {
                    setTextColor(tv, isComplete ? R.color.white : R.color.black);
                    setTextColor(tvLbl, R.color.black);
                    setTextBackground(tv, isComplete ? R.drawable.job_status_complete : R.drawable.job_status_current);
                    if (viewLeft != null) {
                        setTextBackground(viewLeft, R.color.black);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setTextColor(TextView tv, int color) {
        tv.setTextColor(ContextCompat.getColor(fragment.activity, color));
    }

    private void setTextBackground(View view, int drawable) {
        view.setBackground(ContextCompat.getDrawable(fragment.activity, drawable));
    }

    @Override
    public void onClick(View view) {
        if (fragment.activity != null) {
            projectData = ((ContractDetailsActivity) fragment.activity).getProjectData();
        }
        switch (view.getId()) {
            case R.id.rl_profile:
                break;
            case R.id.rl_employee:
            case R.id.rl_profile1:
                if (projectData != null && projectData.clientProfileID != 0) {
                    getClientProfile(projectData.clientProfileID, 1);
                }
                break;
            case R.id.rl_profile_accept_job:
                if (projectData != null && projectData.clientProfileID != 0) {
                    getClientProfile(projectData.clientProfileID, 2);
                }
                break;
            case R.id.tv_chat:
            case R.id.tv_chat1:
            case R.id.tv_chat2:
//                if (projectData != null && projectData.jobPostBids != null) {
                HashMap<String, String> chatMap = new HashMap<>();
                chatMap.put(Constants.RECEIVER_ID, projectData.clientProfileID + "");
                chatMap.put(Constants.RECEIVER_NAME, projectData.clientDetails.username);

                if (!TextUtils.isEmpty(projectData.clientDetails.photo)) {
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
//                }
                break;
            case R.id.tv_edit_bid:
                fragment.activity.isClickableView = false;
//                if (projectData != null && projectData.jobPostBids != null) {
                Preferences.writeInteger(fragment.activity, Constants.EDIT_BID_ID, projectData.id);
                Intent inte = new Intent(fragment.activity, PlaceBidActivity.class);
                inte.putExtra(Constants.IS_EDIT, true);
                inte.putExtra(Constants.PROJECT_DATA, projectData);
                fragment.startActivity(inte);
//                }
                break;
            case R.id.tv_accept_job:
                showAcceptRejectJob(true);
                break;
            case R.id.tv_reject_job:
                showAcceptRejectJob(false);
                break;
            case R.id.tv_submit_job:
//                if (projectData != null && projectData.jobPostBids != null) {

                if (projectData.refundStatus != null && (projectData.refundStatus.equals("0")
                        || projectData.refundStatus.equals("1"))) {
                    Toast.makeText(fragment.activity, fragment.getString(R.string.refund_status_message), Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(fragment.activity, SubmitContractJobActivity.class);
                intent.putExtra(Constants.PROJECT_BID_ID, projectData.id);
                fragment.startActivity(intent);
//                }
                break;
            case R.id.rl_withdraw:
                fragment.activity.redirectActivity(BalanceActivity.class);
                break;
        }
    }

    private TextView txtAcceptReject;
    private ProgressBar progressBarAcceptReject;
    private Dialog dialogAcceptReject;

    private void showAcceptRejectJob(final boolean isAcceptJob) {
        dialogAcceptReject = new Dialog(fragment.activity);
        dialogAcceptReject.setTitle(null);
        dialogAcceptReject.setContentView(R.layout.dialog_accept_reject_job);
        dialogAcceptReject.setCancelable(true);

        TextView tvHeader = dialogAcceptReject.findViewById(R.id.tv_header);
        TextView tvTitle = dialogAcceptReject.findViewById(R.id.tv_title);
        TextView tvBudget = dialogAcceptReject.findViewById(R.id.tv_budget);
        txtAcceptReject = dialogAcceptReject.findViewById(R.id.tv_accept_reject);
        TextView tvCancel = dialogAcceptReject.findViewById(R.id.tv_cancel);
        progressBarAcceptReject = dialogAcceptReject.findViewById(R.id.progress_bar);
        RelativeLayout relButton = dialogAcceptReject.findViewById(R.id.rel_button);
        EditText etReason = dialogAcceptReject.findViewById(R.id.et_reason);

        if (!isAcceptJob) {
            etReason.setVisibility(View.VISIBLE);
        }

        tvHeader.setText(fragment.activity.getString(isAcceptJob ? R.string.accept_this_job : R.string.reject_this_offer));
        if (isAcceptJob) {
            tvTitle.setText(projectData.gigPackageName);
            tvHeader.setText(fragment.activity.getString(R.string.accept_this_job));
        } else {
            tvTitle.setVisibility(View.GONE);
            String s = fragment.activity.getString(R.string.reject_this_offer);
            int[] colorList = {R.color.red_dark};
            String[] words = {"Reject"};
            tvHeader.setText(Utils.getBoldString(fragment.activity, s, null, colorList, words));
        }

//        if (projectData.jobPostBids != null) {
        String budget = "$" + Utils.getDecimalValue("" + projectData.totalPrice);
        String projectType = budget + " / " + fragment.activity.getString(R.string.fixed);
        String[] fontList = {Constants.SFTEXT_BOLD};
        String[] words = {budget};
        tvBudget.setText(Utils.getBoldString(fragment.activity, projectType, fontList, null, words));
//        }

        txtAcceptReject.setText(fragment.activity.getString(isAcceptJob ? R.string.yes_accept_this_job : R.string.yes_reject_this_offer));
        relButton.setBackground(ContextCompat.getDrawable(fragment.activity,
                isAcceptJob ? R.drawable.blue_rounded_corner : R.drawable.red_rounded_corner_20));
//        txtAcceptReject.setBackground(ContextCompat.getDrawable(fragment.activity,
//                isAcceptJob ? R.drawable.blue_rounded_corner : R.drawable.red_rounded_corner_20));

        txtAcceptReject.setOnClickListener(v -> {
            if (!isAcceptJob && TextUtils.isEmpty(etReason.getText().toString().trim())) {
                fragment.activity.toastMessage(fragment.activity.getString(R.string.enter_reason));
                return;
            }
            acceptRejectJob(isAcceptJob, etReason.getText().toString());
            //dialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> dialogAcceptReject.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogAcceptReject.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialogAcceptReject.show();
        dialogAcceptReject.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAcceptReject.getWindow().setAttributes(lp);
    }

    private void showRemindEmployerDialog() {
        final Dialog dialog = new Dialog(fragment.activity);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_remind_employer_for_deposit);
        dialog.setCancelable(true);

        TextView tvOk = dialog.findViewById(R.id.tv_ok);

        tvOk.setOnClickListener(v -> dialog.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);

        dialog.setOnDismissListener(dialogInterface -> {
            if (fragment.activity instanceof ContractDetailsActivity) {
                ((ContractDetailsActivity) fragment.activity).onBackPressed();
            } else {
                fragment.activity.gotoMainActivity(Constants.TAB_GIG);
            }
        });
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        showAcceptRejectVisibility(View.GONE, View.VISIBLE, false);
        fragment.activity.isClickableView = false;
        if (urlEndPoint.equalsIgnoreCase(API_VIEW_CLIENT_PROFILE)) {
            ProfileClient profile = ProfileClient.getClientProfile(decryptedData);

            if (selectedView == 0) {
                binding.progressBarProfile.setVisibility(View.GONE);
                binding.rlProfile.setBackgroundResource(R.drawable.white_rounded_corner_10);
            } else if (selectedView == 1) {
                binding.progressBar.setVisibility(View.GONE);
                binding.rlEmployee.setBackground(null);
            } else if (selectedView == 2) {
                binding.progressBarAccept.setVisibility(View.GONE);
                binding.rlProfileAcceptJob.setBackground(null);
            }

            if (profile != null) {
                Intent intent = new Intent(fragment.activity, EmployerProfileActivity.class);
                intent.putExtra(Constants.CLIENT_PROFILE_DATA, profile);
                fragment.startActivity(intent);
            }
        }

    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        showAcceptRejectVisibility(View.GONE, View.VISIBLE, false);
        fragment.activity.isClickableView = false;
        if (urlEndPoint.equalsIgnoreCase(API_VIEW_CLIENT_PROFILE)) {

            if (selectedView == 0) {
                binding.progressBarProfile.setVisibility(View.GONE);
                binding.rlProfile.setBackgroundResource(R.drawable.white_rounded_corner_10);
            } else if (selectedView == 1) {
                binding.progressBar.setVisibility(View.GONE);
                binding.rlEmployee.setBackground(null);
            } else if (selectedView == 2) {
                binding.progressBarAccept.setVisibility(View.GONE);
                binding.rlProfileAcceptJob.setBackground(null);
            }

        }
    }

    private void showAcceptRejectVisibility(int visibilityShow, int visibilityHide, boolean isTouch) {
        if (progressBarAcceptReject != null) {
            progressBarAcceptReject.setVisibility(visibilityShow);
        }
        if (txtAcceptReject != null) {
            txtAcceptReject.setVisibility(visibilityHide);
        }
        fragment.activity.disableEnableTouch(isTouch);
    }

    private int selectedView;

    void getClientProfile(int profileId, int clickView) {
        if (!fragment.activity.isNetworkConnected())
            return;

        fragment.activity.isClickableView = true;

        selectedView = clickView;
        if (clickView == 0) {
            binding.progressBarProfile.setVisibility(View.VISIBLE);
            binding.rlProfile.setBackgroundResource(R.drawable.transp_rounded_corner_10);
        } else if (clickView == 1) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.rlEmployee.setBackgroundResource(R.drawable.transp_rounded_corner_10);
        } else if (clickView == 2) {
            binding.progressBarAccept.setVisibility(View.VISIBLE);
            binding.rlProfileAcceptJob.setBackgroundResource(R.drawable.transp_rounded_corner_10);
        }

        CommonRequest.ClientProfile clientProfile = new CommonRequest.ClientProfile();
        clientProfile.setClient_id(profileId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(fragment.activity, API_VIEW_CLIENT_PROFILE, clientProfile.toString(), true, this);

    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        showAcceptRejectVisibility(View.GONE, View.VISIBLE, false);
        fragment.activity.isClickableView = false;
        if (url.equalsIgnoreCase(API_ACCEPT_CONTRACT)) {
            binding.llWaitingForAcceptance.setVisibility(View.GONE);
            binding.llJobStatus.setVisibility(View.VISIBLE);
            binding.tvJobStatusInfo.setText(fragment.activity.getString(R.string.waiting_for_deposit_info));
            setJobProgress(WAITING_FOR_DEPOSIT, false);
            showRemindEmployerDialog();
            dialogAcceptReject.dismiss();
        } else if (url.equalsIgnoreCase(API_CANCEL_CONTRACT)) {
            dialogAcceptReject.dismiss();
            if (((ContractDetailsActivity) fragment.activity).selectedMessageId != null && ((ContractDetailsActivity) fragment.activity).selectedMessageId != 0) {
                cancelContractSocket();// call when offer case
                fragment.activity.gotoMainActivity(Constants.TAB_CHAT);//in case of offer redirect to chat list directly
            } else {
                ((ContractDetailsActivity) fragment.activity).onBackPressed();
            }
        }
    }

    private void cancelContractSocket() {
        try {

            JSONObject jsonData = new JSONObject();
            jsonData.put("partitionKey", "#message#" + projectData.clientDetails.clientID + "-" + fragment.activity.getUserID());
            jsonData.put("offerStatus", 3);
            jsonData.put("senderId", fragment.activity.getUserID());
            jsonData.put("receiverId", projectData.clientDetails.clientID);
            jsonData.put("messageId", ((ContractDetailsActivity) fragment.activity).selectedMessageId);

            fragment.activity.mSocket.emit("sendLiveOfferStatus", jsonData);

        } catch (JSONException e) {
            Log.d("AAAAAA", "error sendLiveOfferStatus " + e.getMessage());
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        showAcceptRejectVisibility(View.GONE, View.VISIBLE, false);
        fragment.activity.isClickableView = false;
    }
}
