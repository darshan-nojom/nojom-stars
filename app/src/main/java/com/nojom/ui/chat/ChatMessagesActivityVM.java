package com.nojom.ui.chat;

import static com.nojom.multitypepicker.activity.ImagePickActivity.IS_NEED_CAMERA;
import static com.nojom.util.Constants.AGENT_PROFILE;
import static com.nojom.util.Constants.API_CHAT_FILE_UPLOAD;
import static com.nojom.util.Constants.API_CONTRACT_DETAIL;
import static com.nojom.util.Constants.API_CUSTOM_CONTRACT_DETAIL;
import static com.nojom.util.Constants.API_VIEW_CLIENT_PROFILE;
import static com.nojom.util.Constants.API_WITHDRAW_OFFER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.adapter.ChatMessageAdapter;
import com.nojom.api.APIRequest;
import com.nojom.apis.JobDetailAPI;
import com.nojom.databinding.ActivityChatMessagesBinding;
import com.nojom.model.ChatList;
import com.nojom.model.ChatMessageList;
import com.nojom.model.ChatSeenModel;
import com.nojom.model.CommonModel;
import com.nojom.model.ContractDetails;
import com.nojom.model.CreateOfferResponse;
import com.nojom.model.MuteUnmute;
import com.nojom.model.OfferStatusResponse;
import com.nojom.model.ProfileClient;
import com.nojom.model.SenderReceiverSocket;
import com.nojom.model.Typing;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.activity.NormalFilePickActivity;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.clientprofile.EmployerProfileActivity;
import com.nojom.ui.gigs.ContractDetailsActivity;
import com.nojom.ui.projects.ProjectDetailsActivity;
import com.nojom.ui.workprofile.ChooseOfferActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.StorageDisclosureDialog;
import com.nojom.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatMessagesActivityVM extends AndroidViewModel implements View.OnClickListener,
        APIRequest.APIRequestListener, APIRequest.JWTRequestResponseListener,
        ChatMessageAdapter.WithdrawOfferListener {
    boolean isScrolling = false;
    private ActivityChatMessagesBinding binding;
    private BaseActivity activity;
    private ChatMessageAdapter chatMessageAdapter;
    private LinearLayoutManager layoutManager;
    private int count = 0;
    private int firstVisibleItemPosition;
    private JSONObject jsonDataLastKey;
    private long receiverID;
    private ArrayList<ChatMessageList.DataChatList> chatMsgList = null;
    private Date lastMessageDate = null;
    private HashMap<String, String> chatMap = null;
    private ChatMessageList.Project project = null;
    private boolean isFromDetailScreen, isDataLoading;
    private ProgressDialog progress;
    private JobDetailAPI jobDetailAPI;
    private String regex = "([0-9]+)";
    private String pattern = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- . (]?\\d{3}[- . )]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";

    private Matcher m;
    private String cUsername;

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!isDataLoading) {
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem = firstVisibleItemPosition + visibleItemCount;

                if (lastVisibleItem == totalItemCount) {
                    if (!isScrolling) {
                        isScrolling = true;
                        isDataLoading = true;
                        getPreviousMessage(false);
                    }
                }

                if (firstVisibleItemPosition > 2) {
                    if (count == 0)
                        binding.tvNewMessageCount.setVisibility(View.GONE);
                    binding.rlScrollDown.setVisibility(View.VISIBLE);
                } else {
                    count = 0;
                    binding.rlScrollDown.setVisibility(View.GONE);
                }
            }
        }
    };

    private TextView tvViewProfile, tvManage;
    private CircularProgressBar progressBarProfile;

    ChatMessagesActivityVM(Application application, ActivityChatMessagesBinding chatMessagesBinding, BaseActivity baseActivity) {
        super(application);
        binding = chatMessagesBinding;
        activity = baseActivity;
        initData();
    }

    @SuppressLint("CheckResult")
    private void initData() {
        cUsername = activity.getIntent().getStringExtra("user");
        receiverID = activity.getIntent().getIntExtra("reciever_id", 0);
        int status = activity.getIntent().getIntExtra("status", 0);
        String profilePic = activity.getIntent().getStringExtra("profilePic");
        jsonDataLastKey = new JSONObject();
        binding.imgBack.setOnClickListener(this);
        binding.imgSend.setOnClickListener(this);
        binding.imgSetting.setOnClickListener(this);
        binding.imgAttach.setOnClickListener(this);
        binding.ivScrollDown.setOnClickListener(this);
        binding.tvNewMessageCount.setOnClickListener(this);
        binding.txtCreateOffer.setOnClickListener(this);

        jobDetailAPI = new JobDetailAPI();
        jobDetailAPI.init(activity);

        if (activity.getIntent() != null) {
            chatMap = (HashMap<String, String>) activity.getIntent().getSerializableExtra(Constants.CHAT_DATA);

            if (chatMap != null && !TextUtils.isEmpty(chatMap.get("isDetailScreen"))) {
                isFromDetailScreen = true;
            }

            if (chatMap != null && !TextUtils.isEmpty(chatMap.get(Constants.RECEIVER_ID)) && receiverID == 0) {
                receiverID = Long.parseLong(chatMap.get(Constants.RECEIVER_ID));
            }

            if (chatMap != null && !TextUtils.isEmpty(chatMap.get(Constants.RECEIVER_NAME)) && TextUtils.isEmpty(cUsername)) {
                cUsername = chatMap.get(Constants.RECEIVER_NAME);
            }

            if (chatMap != null && !TextUtils.isEmpty(chatMap.get(Constants.RECEIVER_PIC)) && TextUtils.isEmpty(profilePic)) {
                profilePic = chatMap.get(Constants.RECEIVER_PIC);
            }
        }

        binding.tvName.setText(cUsername);

        if (status == 1) {
            binding.tvOnline.setText(activity.getString(R.string.active_now));
        } else {
            binding.tvOnline.setText(activity.getString(R.string.offline));
        }

        if (!TextUtils.isEmpty(profilePic)) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher_round);
            requestOptions.error(R.mipmap.ic_launcher_round);
            Glide.with(activity)
                    .setDefaultRequestOptions(requestOptions)
                    .load(profilePic).into(binding.imgProfile);
        } else {
            binding.imgProfile.setImageResource(R.mipmap.ic_launcher_round);
        }
        layoutManager = new LinearLayoutManager(activity.getApplicationContext());
        binding.rvMessages.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
        binding.rvMessages.addOnScrollListener(onScrollListener);

        binding.etMessage.addTextChangedListener(new TextWatcher() {
            CountDownTimer timer = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
                if (activity.isEmpty(getMessage())) {
                    binding.imgSend.setAlpha(0.5f);
                    binding.imgSend.setClickable(false);
                    sendTyping(false);
                } else {
                    binding.imgSend.setAlpha(1f);
                    binding.imgSend.setClickable(true);
                    sendTyping(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                timer = new CountDownTimer(3000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        Log.e("-------------", "-----------  typing Hold");
                        sendTyping(false);
                    }

                }.start();
            }
        });

        Utils.trackFirebaseEvent(activity, "Conversion_Screen");

        getPreviousMessage(true);
        getHistorySuccess();
        getHistoryError();
        invalidNewMessage();
        getTyping();
        fileSavedSuccess();
        invalidFile();
        getMessageSeenEvent();
        getMuteUnmute();

        jobDetailAPI.getIsShowProgress().observe(activity, isShow -> {
            if (progressBarManage != null && tvManage != null) {
                if (isShow) {
                    progressBarManage.setVisibility(View.VISIBLE);
                    tvManage.setVisibility(View.INVISIBLE);
                } else {
                    progressBarManage.setVisibility(View.GONE);
                    tvManage.setVisibility(View.VISIBLE);
                }
            }
        });

        jobDetailAPI.getProjectById().observe(activity, projectByID -> {

            if (progressBarManage != null && tvManage != null) {
                progressBarManage.setVisibility(View.GONE);
                tvManage.setVisibility(View.VISIBLE);
            }

            if (profileDialog != null) {
                profileDialog.dismiss();
            }
            if (projectByID != null) {
                Intent i = new Intent(activity, ProjectDetailsActivity.class);
                i.putExtra(Constants.PROJECT, projectByID);
                activity.startActivity(i);
            }
        });
    }

    void onResumeMethod() {
        getNewMessage();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                activity.onBackPressed();
                break;
            case R.id.tvNewMessageCount:
            case R.id.ivScrollDown:
                try {
                    count = 0;
                    binding.rlScrollDown.setVisibility(View.GONE);
                    binding.rvMessages.scrollToPosition(0);
                    binding.tvNewMessageCount.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img_send:
                activity.connectSocket(activity);

                if (activity.isEmpty(getMessage())) {
                    return;
                }

                //mobile number validation
                Pattern r = Pattern.compile(pattern);
                m = r.matcher(getMessage());
                if (m.matches()) {
                    showAlert();
                    return;
                }

                Pattern pattern = Pattern.compile("-?\\d+");
                Matcher m = pattern.matcher(getMessage());
                while (m.find()) {
                    if (m.group().length() >= 6 && m.group().length() <= 14) {
                        showAlert();
                        return;
                    }
                }

                //email validation
                if (activity.isValidEmail(getMessage())) {
                    showAlert();
                    return;
                }

                /*//mobile number validation with contain string
                Pattern pattern = Pattern.compile(regex);
                //Creating a Matcher object
                Matcher matcher = pattern.matcher(getMessage());
                while (matcher.find()) {
                    Log.e("----------------- ", matcher.group() + " ");
                    if (activity.isValidMobile(matcher.group())) {
                        activity.toastMessage("Warning");
                    }
                }*/


                senMessageAPI("", "", getMessage());
                break;
            case R.id.img_setting:
                showSettingDialog();
                break;
            case R.id.img_attach:
                selectFileDialog();
                break;
            case R.id.txt_create_offer:
                Intent intent = new Intent(activity, ChooseOfferActivity.class);
                intent.putExtra("cUsername", "" + cUsername);
                intent.putExtra("cUserId", receiverID);
                activity.startActivityForResult(intent, 1211);
                break;
        }
    }

    private void getMuteUnmute() {
        activity.mSocket.on("responseOfMuteUnmute", args -> activity.runOnUiThread(() -> {
            MuteUnmute munm = new Gson().fromJson(args[0].toString(), MuteUnmute.class);
            try {
                Log.e("responseOfMuteUnmute", " ------------- responseOfMuteUnmute" + args[0].toString());
                if (project != null && munm != null) {
                    project.c_mute = munm.data.cMute;
                    activity.toastMessage((project.c_mute ? "Muted" : "Unmuted"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
//        activity.mSocket.on("inValidMessage", args -> {
//            Log.e("AAAAAA", "inValidMessage args...." + args[0].toString());
//            activity.runOnUiThread(() -> activity.toastMessage(args[0].toString()));
//        });
    }

    private void getMessageSeenEvent() {
        try {
            activity.mSocket.on("getMessageSeenEvent", args -> {
                Log.e("AAAAAA", "getMessageSeenEvent args...." + args[0].toString());
                activity.runOnUiThread(() -> {
                    ChatSeenModel chatSeenModel = new Gson().fromJson(args[0].toString(), ChatSeenModel.class);
                    if (chatMsgList != null && chatMsgList.size() > 0) {
                        for (int i = 0; i < chatSeenModel.messageIds.length; i++) {
                            for (int j = 0; j < chatMsgList.size(); j++) {
                                if (chatSeenModel.messageIds[i].equals(chatMsgList.get(j).messageId)) {
                                    try {
                                        chatMsgList.get(j).isSeenMessage = "2";
                                        chatMessageAdapter.notifyItemChanged(i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void manageUserStatus(ChatList.Datum moUserStatus, int status) {
        activity.runOnUiThread(() -> {
            if (moUserStatus != null) {
                if (moUserStatus.id == receiverID) {
                    if (status == 1) {
                        binding.tvOnline.setText(activity.getString(R.string.active_now));
                    } else {
                        binding.tvOnline.setText(activity.getString(R.string.offline));
                    }
                }
            }
        });
    }

    private void selectFileDialog() {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_camera_document_select);
        dialog.setCancelable(true);
        TextView tvCancel = dialog.findViewById(R.id.btn_cancel);
        LinearLayout llCamera = dialog.findViewById(R.id.ll_camera);
        LinearLayout llDocument = dialog.findViewById(R.id.ll_document);

        llCamera.setOnClickListener(v -> {
            if (activity.checkStoragePermission()) {
                checkPermission(false);
            } else {
                new StorageDisclosureDialog(activity, () -> checkPermission(false));
            }
            dialog.dismiss();
        });

        llDocument.setOnClickListener(v -> {
//            if (activity.checkStoragePermission()) {

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (Environment.isExternalStorageManager()) {
//                        activity.openDocuments(activity, 2);
//                    } else { //request for the permission
//                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
//                        intent.setData(uri);
//                        activity.startActivity(intent);
//                    }
//                } else {
            checkPermission(true);
//                }

//            } else {
//                new StorageDisclosureDialog(activity, () -> checkPermission(true));
//            }
            dialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    public void checkPermission(final boolean isDocument) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (isDocument) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    activity.openDocuments(activity, 1);
                                } else {
                                    Intent intent4 = new Intent(activity, NormalFilePickActivity.class);
                                    intent4.putExtra(Constant.MAX_NUMBER, 2);
                                    intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                                    activity.startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                                }
                            } else {
                                Intent intent = new Intent(activity, ImagePickActivity.class);
                                intent.putExtra(IS_NEED_CAMERA, true);
                                intent.putExtra(Constant.MAX_NUMBER, 2);
                                activity.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
                            }
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            activity.toastMessage(activity.getString(R.string.please_give_permission));
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void getPreviousMessage(boolean isPass) {
        if (activity.isNetworkConnected()) {
            try {
                JSONObject jsonData = new JSONObject();
                jsonData.put("senderId", activity.getUserID());
                jsonData.put("receiverId", receiverID);
                jsonData.put("partitionKey", "#message#" + receiverID + "-" + activity.getUserID());
                jsonData.put("limit", 10);

                if (isPass && chatMap != null && !TextUtils.isEmpty(chatMap.get("isProject"))) {
                    jsonData.put("isProject", chatMap.get("isProject"));
                }
                if (isPass && chatMap != null && !TextUtils.isEmpty(chatMap.get("projectType"))) {//gig or job
                    jsonData.put("projectType", chatMap.get("projectType"));
                }
                if (isPass && chatMap != null && !TextUtils.isEmpty(chatMap.get(Constants.PROJECT_ID))) {//gig or job
                    jsonData.put("projectId", chatMap.get(Constants.PROJECT_ID));
                }

                if (jsonDataLastKey != null) {
                    jsonData.put("lastEvaluatedKey", jsonDataLastKey);
                } else {
                    return;
                }

                Log.e("AAAAAA", "getMessageHistory..." + jsonData.toString());
                activity.mSocket.emit("getMessageHistory", jsonData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getMessage() {
        return Objects.requireNonNull(binding.etMessage.getText()).toString().trim();
    }

    private CircularProgressBar progressBarManage;
    private Dialog profileDialog;

    public void showSettingDialog() {
        profileDialog = new Dialog(activity);
        profileDialog.setTitle(null);
        profileDialog.setContentView(R.layout.dialog_chat_setting);
        profileDialog.setCancelable(true);
        TextView tvCancel = profileDialog.findViewById(R.id.btn_cancel);
        tvViewProfile = profileDialog.findViewById(R.id.tv_view_profile);
        TextView tvMute = profileDialog.findViewById(R.id.tv_mute);
        tvManage = profileDialog.findViewById(R.id.tv_manage_project);
        TextView tvReport = profileDialog.findViewById(R.id.tv_report_freelancer);
        progressBarProfile = profileDialog.findViewById(R.id.progress_bar);
        progressBarManage = profileDialog.findViewById(R.id.progress_bar_manage);

        if (project != null) {
            tvMute.setVisibility(View.VISIBLE);
            tvMute.setText(project.c_mute ?
                    activity.getString(R.string.unmute_conversation) : activity.getString(R.string.mute_conversation));
        } else {
            tvMute.setVisibility(View.GONE);
        }

        tvViewProfile.setOnClickListener(v -> {
            if (receiverID != 0) {
                getClientProfile(receiverID);
            }
        });

        tvMute.setOnClickListener(v -> {
            profileDialog.dismiss();
            muteUnmute(!project.c_mute);
        });

        tvManage.setOnClickListener(v -> {

            try {
                if (isFromDetailScreen) {//if come from details or contract screen at that time no need to call API, simply redirect to back screen
                    profileDialog.dismiss();
                    activity.onBackPressed();
                } else {
                    if (project != null && project.projectId != 0) {
                        if (project.projectType.equalsIgnoreCase("1")) {//standard gig
                            getGigDetailAPI(project.projectId, false);
                        } else if (project.projectType.equalsIgnoreCase("2")) {//job
                            jobDetailAPI.getProjectById(project.projectId);
                        } else if (project.projectType.equalsIgnoreCase("3")) {//custom gig
                            getCustomGigDetails(project.projectId, false);
                        }
                    } else {
                        profileDialog.dismiss();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        tvReport.setOnClickListener(v -> {
            profileDialog.dismiss();
            io.intercom.android.sdk.Intercom.client().displayMessageComposer();
        });

        tvCancel.setOnClickListener(v -> profileDialog.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(profileDialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        profileDialog.show();
        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileDialog.getWindow().setAttributes(lp);
    }

    private void sendTyping(boolean isTyping) {
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("senderId", activity.getUserID());
            jsonData.put("receiverId", receiverID);
            jsonData.put("type", isTyping);

            Log.e("AAAAAA", "sending type..." + jsonData.toString());
            activity.mSocket.emit("sendTyping", jsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void muteUnmute(boolean isMute) {
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("c_mute", isMute);
            jsonData.put("profile_type_id", "" + AGENT_PROFILE);
            jsonData.put("partitionKey", "#message#" + receiverID + "-" + activity.getUserID());

            activity.mSocket.emit("userToMuteUnmute", jsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getMessageSeen() {

        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("partitionKey", "#message#" + receiverID + "-" + activity.getUserID());
            jsonData.put("senderId", activity.getUserID());
            jsonData.put("receiverId", receiverID);

            Log.e("AAAAAA", "messageSeen..." + jsonData.toString());
            activity.mSocket.emit("messageSeen", jsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setAdapter() {
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);

        if (chatMsgList != null && chatMsgList.size() > 0) {

            for (int i = 0; i < chatMsgList.size(); i++) {
                ChatMessageList.DataChatList chatList = chatMsgList.get(i);
                String messageCreatedAt = Utils.convertDate(String.valueOf(chatList.messageCreatedAt), "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                Date date = Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", messageCreatedAt);
                chatList.isDayChange = !Utils.isSameDay(lastMessageDate, date);
                lastMessageDate = date;
            }

            Collections.reverse(chatMsgList);

            if (chatMessageAdapter == null) {
                chatMessageAdapter = new ChatMessageAdapter(chatMsgList, activity, this);
                binding.rvMessages.setAdapter(chatMessageAdapter);
            }
        }
    }

    private void getHistorySuccess() {
        activity.mSocket.on("loadMessageHistory", args -> {
            Log.e("AAAAAA", "loadMessageHistory" + args[0].toString());
            ChatMessageList chatMsg = new Gson().fromJson(args[0].toString(), ChatMessageList.class);
            if (chatMsg.data.lastEvaluatedKey != null) {
                try {
                    jsonDataLastKey = new JSONObject(chatMsg.data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                jsonDataLastKey = null;
            }
            if (chatMsg.data.dataChatList.size() > 0) {
                if (chatMsgList != null && chatMsgList.size() > 0) {
                    List<ChatMessageList.DataChatList> chatListTemp = chatMsg.data.dataChatList;
                    Collections.reverse(chatListTemp);
                    chatMsgList.addAll(chatListTemp);
                    Collections.reverse(chatMsgList);
                    Date lastMessageDate = null;
                    for (ChatMessageList.DataChatList chatList : chatMsgList) {
                        String messageCreatedAt = Utils.convertDate(String.valueOf(chatList.messageCreatedAt), "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                        Date date = Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", messageCreatedAt);
                        chatList.isDayChange = !Utils.isSameDay(lastMessageDate, date);
                        Log.e("DATE MSG ", "------ " + chatList.isDayChange + " ===== " + chatList.message);
                        lastMessageDate = date;
                    }
                    Collections.reverse(chatMsgList);
                    activity.runOnUiThread(() -> {
                        chatMessageAdapter.notifyDataSetChanged();
                        isScrolling = false;
                    });
                } else {
                    chatMsgList = new ArrayList<>();
                    chatMsgList.addAll(chatMsg.data.dataChatList);
                    if (count == 0) {
                        getMessageSeen();
                    }
                    activity.runOnUiThread(() -> {
                        setAdapter();
                        isScrolling = false;
                    });
                }
                isDataLoading = false;
            } else {
                activity.runOnUiThread(() -> {
//                    if (binding.shimmerLayout.isShimmerStarted()) {
                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(View.GONE);
//                    }
                });
            }
            if (chatMsg.data != null && chatMsg.data.project != null) {
                project = chatMsg.data.project;
            }
        });
        activity.mSocket.on("loadSenderReceiverData", args -> {
            Log.e("AAAAAA", "loadSenderReceiverData" + args[0].toString());
            SenderReceiverSocket recSendData = new Gson().fromJson(args[0].toString(), SenderReceiverSocket.class);
            if (recSendData != null) {
                if (recSendData.data.receiverData != null) {
                    activity.runOnUiThread(() -> {
                        try {
                            if (recSendData.data.receiverData.isSocketOnline.equalsIgnoreCase("1")) {
                                binding.tvOnline.setText(activity.getString(R.string.active_now));
                            } else {
                                binding.tvOnline.setText(activity.getString(R.string.offline));
                            }
                            if (recSendData.data.receiverData.is_chat.equalsIgnoreCase("1")) {
                                binding.rlBottom.setVisibility(View.VISIBLE);
                                binding.rlBottomOffer.setVisibility(View.VISIBLE);
                                binding.view3.setVisibility(View.VISIBLE);
                            } else {
                                binding.rlBottom.setVisibility(View.INVISIBLE);
                                binding.rlBottomOffer.setVisibility(View.INVISIBLE);
                                binding.view3.setVisibility(View.INVISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
        activity.mSocket.on("inValidOfferData", args -> Log.e("AAAAAA", "inValidOfferData" + args[0].toString()));
        activity.mSocket.on("failMessageWhenSend", args -> Log.e("AAAAAA", "failMessageWhenSend" + args[0].toString()));
        activity.mSocket.on("getLiveOfferStatus", args -> {
            Log.e("AAAAAA", "getLiveOfferStatus" + args[0].toString());
            OfferStatusResponse recSendData = new Gson().fromJson(args[0].toString(), OfferStatusResponse.class);
            if (recSendData != null) {
                activity.runOnUiThread(() -> {
                    if (chatMsgList != null && chatMsgList.size() > 0) {
                        for (int j = 0; j < chatMsgList.size(); j++) {
                            if (recSendData.messageId.equals(chatMsgList.get(j).messageId)) {
                                try {
                                    chatMsgList.get(j).offer.offerStatus = recSendData.offerStatus;
                                    chatMsgList.get(j).offer.contractID = recSendData.contractID;
                                    chatMsgList.get(j).offer.price = recSendData.price;
                                    chatMessageAdapter.notifyItemChanged(j);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                });
            }
        });
    }

    private void senMessageAPI(String base64, String filename, String message) {
        if (activity.isNetworkConnected()) {
            try {
//                JSONObject mainJsonData = new JSONObject();
                JSONObject jsonData = new JSONObject();
                jsonData.put("partitionKey", "#message#" + receiverID + "-" + activity.getUserID());
                jsonData.put("message", message);
                jsonData.put("senderId", activity.getUserID());
                jsonData.put("receiverId", receiverID);
                if (!TextUtils.isEmpty(base64) && !TextUtils.isEmpty(filename)) {
                    JSONObject sendImages = new JSONObject();
                    sendImages.put("filename", filename);
                    sendImages.put("base64", base64);
                    JSONArray array = new JSONArray();
                    array.put(sendImages);
                    jsonData.put("files", array);
                }
//                mainJsonData.put("data",jsonData);
//                mainJsonData.put("action","sendMessage");
                Log.e("AAAAAA", "sendMessage" + jsonData.toString());
                activity.mSocket.emit("sendMessage", jsonData);

                //generateNoteOnSD(activity, filename, base64, filename);
            } catch (JSONException e) {
                Log.d("AAAAAA", "error send message " + e.getMessage());
            }

            //Utils.hideSoftKeyboard(activity);
            binding.etMessage.setText("");
        }
    }

    public void senOfferMessageAPI(String base64, String filename, CreateOfferResponse message) {
        if (activity.isNetworkConnected()) {
            try {
                JSONObject offerMsgObj = new JSONObject();
                offerMsgObj.put("offerTitle", "" + message.offerTitle);
                offerMsgObj.put("offerID", message.offerID);
                offerMsgObj.put("gigType", message.gigType);
                offerMsgObj.put("clientID", message.clientID);
                offerMsgObj.put("agentID", message.agentID);
                offerMsgObj.put("deadlineType", message.deadlineType);
                offerMsgObj.put("deadlineValue", message.deadlineValue);
                offerMsgObj.put("description", message.description);
                offerMsgObj.put("gigID", message.gigID);
                offerMsgObj.put("parentServiceCategoryID", message.parentServiceCategoryID);
                offerMsgObj.put("price", message.price);
                offerMsgObj.put("offerStatus", message.offerStatus);

                JSONObject jsonData = new JSONObject();
                jsonData.put("partitionKey", "#message#" + receiverID + "-" + activity.getUserID());
                jsonData.put("message", " ");
                jsonData.put("offer", offerMsgObj);
                jsonData.put("senderId", activity.getUserID());
                jsonData.put("receiverId", receiverID);
                if (!TextUtils.isEmpty(base64) && !TextUtils.isEmpty(filename)) {
                    JSONObject sendImages = new JSONObject();
                    sendImages.put("filename", filename);
                    sendImages.put("base64", base64);
                    JSONArray array = new JSONArray();
                    array.put(sendImages);
                    jsonData.put("files", array);
                }
                Log.e("AAAAAA", "sendMessage" + jsonData.toString());
                activity.mSocket.emit("sendMessage", jsonData);
                //generateNoteOnSD(activity, filename, base64, filename);

            } catch (JSONException e) {
                Log.d("AAAAAA", "error send message " + e.getMessage());
            }

            //Utils.hideSoftKeyboard(activity);
            binding.etMessage.setText("");
            Preferences.saveCreateOffer(activity, null);
        }
    }

    public void sendLiveStatus(int status, Long messageId) {
        try {

            JSONObject jsonData = new JSONObject();
            jsonData.put("partitionKey", "#message#" + receiverID + "-" + activity.getUserID());
            jsonData.put("offerStatus", status);
            jsonData.put("senderId", activity.getUserID());
            jsonData.put("receiverId", receiverID);
            jsonData.put("messageId", messageId);

            Log.e("AAAAAA", "sendLiveOfferStatus" + jsonData.toString());
            activity.mSocket.emit("sendLiveOfferStatus", jsonData);
        } catch (JSONException e) {
            Log.d("AAAAAA", "error sendLiveOfferStatus " + e.getMessage());
        }
    }

    public void onFileSelect(File imgPaths, String filename) {
        String imgPath = String.valueOf(imgPaths);
        String name = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(imgPaths), filename, name);

//        sendFileToServer(imgPaths,"",name,"");
    }

    public String getStringFile(File f) {
        InputStream inputStream = null;
        ByteArrayOutputStream output = null;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());
            byte[] buffer = new byte[20480];//specify the size to allow
            int bytesRead;
            output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, android.util.Base64.DEFAULT);
            int i = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
                i++;
            }
            output64.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return output != null ? output.toString() : "";
    }

    private void getHistoryError() {
        activity.mSocket.on("inValidMessageHistoryRequest", args -> {
            Log.e("AAAAAA", "inValidMessageHistoryRequest args...." + args[0].toString());
            activity.runOnUiThread(() -> activity.toastMessage(args[0].toString()));
            isDataLoading = false;
        });
    }

    private ChatMessageList.DataChatList tempNewMessage = null;

    private void invalidNewMessage() {
        activity.mSocket.on("inValidMessage", args -> {
            Log.e("AAAAAA", "inValidMessage args...." + args[0].toString());
            activity.runOnUiThread(() -> activity.toastMessage(args[0].toString()));
        });
    }

    private void fileSavedSuccess() {
        activity.mSocket.on("fileSavedSuccess", args -> {
            activity.runOnUiThread(() -> {
            });
        });
    }

    private void invalidFile() {
        activity.mSocket.on("invalidFile", args -> Log.e("AAAAAA", "invalidFile args...." + args[0].toString()));
    }

    public void getNewMessage() {
        activity.mSocket.on("getMessage", args -> {
            Log.e("AAAAAA", "getNewMessage " + args[0].toString());
            activity.runOnUiThread(() -> {
                ChatMessageList.DataChatList newMessage = new Gson().fromJson(args[0].toString(), ChatMessageList.DataChatList.class);
                try {
                    if ((!newMessage.self && this.receiverID != Integer.parseInt(newMessage.senderId))
                            || (newMessage.self && activity.getUserID() != Integer.parseInt(newMessage.senderId))) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (tempNewMessage != null) {//this code is to remove duplication case in case of file send
                    if (newMessage.messageId.equals(tempNewMessage.messageId)) {
                        return;
                    }
                }
                tempNewMessage = newMessage;
                if (chatMsgList == null) {
                    chatMsgList = new ArrayList<>();
                }
                String messageCreatedAt = Utils.convertDate(String.valueOf(newMessage.messageCreatedAt), "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                Date date = Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", messageCreatedAt);
                newMessage.isDayChange = !Utils.isSameDay(lastMessageDate, date);
                lastMessageDate = date;
                chatMsgList.add(0, newMessage);
//                for (ChatMessageList.DataChatList chatList : chatMsgList) {
//                    String messageCreatedAt = Utils.convertDate(String.valueOf(chatList.messageCreatedAt), "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
//                    Date date = Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", messageCreatedAt);
//                    chatList.isDayChange = !Utils.isSameDay(lastMessageDate, date);
//                    lastMessageDate = date;
//                }
                if (activity.getUserID() == Integer.parseInt(newMessage.receiverId)) {
                    getMessageSeen();
                }
                if (chatMessageAdapter != null)
                    chatMessageAdapter.notifyItemInserted(0);
                else {
                    chatMessageAdapter = new ChatMessageAdapter(chatMsgList, activity, this);
                    binding.rvMessages.setAdapter(chatMessageAdapter);
                }
                if (firstVisibleItemPosition >= 0 && firstVisibleItemPosition < 5) {
                    binding.rvMessages.scrollToPosition(0);
                } else {
                    count++;
                    binding.tvNewMessageCount.setVisibility(View.VISIBLE);
                    binding.tvNewMessageCount.setText(count + "");
                }
                if (totalFile == 0) {
                    hideProgress();
                }
            });
        });
    }

    private int offerAdapterPos = -1;
    private Long selectedMessageId = null;

    @Override
    public void onClickWithdrawOffer(ChatMessageList.DataChatList data, int pos) {
        offerAdapterPos = pos;
        showWithdrawAlert(data.offer.offerID, data.sK, data.messageId);
    }

    @Override
    public void onClickOfferView(ChatMessageList.DataChatList data, int pos) {
        offerAdapterPos = pos;
        selectedMessageId = data.messageId;
        if (chatMessageAdapter != null) {
            chatMessageAdapter.refreshOfferView(offerAdapterPos, true, 0);
        }
        if (data.offer.gigType.equalsIgnoreCase("2")) {//standard gig
            getGigDetailAPI(data.offer.contractID, true);
        } else if (data.offer.gigType.equalsIgnoreCase("3") || data.offer.gigType.equalsIgnoreCase("1")) {//custom gig
            getCustomGigDetails(data.offer.contractID, true);
        }
    }

    private void getTyping() {
        activity.mSocket.on("getTyping", args -> {
            activity.runOnUiThread(() -> {
                Typing typing = new Gson().fromJson(args[0].toString(), Typing.class);
                if (typing.type) {
                    if (typing.senderId == receiverID) {
                        binding.tvOnline.setText(activity.getString(R.string.typing_));
                    }
                } else {
                    if (typing.senderId == receiverID) {
                        binding.tvOnline.setText(activity.getString(R.string.active_now));
                    }
                }
            });
        });
    }

    private class AsyncTaskRunner extends AsyncTask<String, Integer, String> {
        private String name;
        private String base64;

        @Override
        protected String doInBackground(String... params) {
            String file = params[0];
            name = params[2];
            base64 = getStringFile(new File(file));
//            int i = 0;
//            while (i <= 100) {
//                try {
//                    Thread.sleep(50);
//                    publishProgress(i);
//                    i++;
//                } catch (Exception e) {
//                    Log.i("TAGGG", e.getMessage());
//                }
//            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            binding.progress.setVisibility(View.GONE);
            Log.e("onPostExecute", " " + base64.length());

            senMessageAPI(base64, name, getMessage());
            //sendFileToServer(null,base64,name,"");
            totalFile = totalFile - 1;
        }

        @Override
        protected void onPreExecute() {

//            showProgress();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progress.setProgress(values[0]);
        }
    }

    private int totalFile;

    public void showProgress(int fileSize) {
        progress = new ProgressDialog(activity);
        progress.setMessage("Uploading, Please wait...");
        progress.setIndeterminate(false);
        progress.show();
        totalFile = fileSize;
    }

    private void hideProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    void getClientProfile(long profileId) {
        if (!activity.isNetworkConnected())
            return;

//        isClickableView = true;
        progressBarProfile.setVisibility(View.VISIBLE);
        tvViewProfile.setVisibility(View.INVISIBLE);

        CommonRequest.ClientProfile clientProfile = new CommonRequest.ClientProfile();
        clientProfile.setClient_id(profileId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_VIEW_CLIENT_PROFILE, clientProfile.toString(), true, this);

    }

    /*void getProjectById(int projectId) {

        if (!activity.isNetworkConnected()) {
            return;
        }

//        isClickableView = true;
        progressBarManage.setVisibility(View.VISIBLE);
        tvManage.setVisibility(View.INVISIBLE);

        CommonRequest.JobDetail jobDetail = new CommonRequest.JobDetail();
        jobDetail.setJob_post_id(projectId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_JOB_DETAIL, jobDetail.toString(), true, this);
    }*/

    public void getGigDetailAPI(int id, boolean isShowProgress) {
        if (!activity.isNetworkConnected())
            return;

        if (isShowProgress) {
            if (chatMessageAdapter != null) {
                chatMessageAdapter.refreshOfferView(offerAdapterPos, true, 0);
            }
        } else {
            if (progressBarManage != null && tvManage != null) {
                progressBarManage.setVisibility(View.VISIBLE);
                tvManage.setVisibility(View.INVISIBLE);
            }
        }


        String url = API_CONTRACT_DETAIL + id;
        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, url, false, null);
    }


    public void getCustomGigDetails(int id, boolean isShowProgress) {
        if (!activity.isNetworkConnected())
            return;

        if (chatMessageAdapter != null && isShowProgress) {
            chatMessageAdapter.refreshOfferView(offerAdapterPos, true, 0);
        }

        String customGigDetailUrl = API_CUSTOM_CONTRACT_DETAIL + id;
        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, customGigDetailUrl, false, null);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {

//        isClickableView = false;

        if (urlEndPoint.equalsIgnoreCase(API_VIEW_CLIENT_PROFILE)) {
            ProfileClient profile = ProfileClient.getClientProfile(decryptedData);

            progressBarProfile.setVisibility(View.GONE);
            tvViewProfile.setVisibility(View.VISIBLE);
            profileDialog.dismiss();

            if (profile != null) {
                Intent intent = new Intent(activity, EmployerProfileActivity.class);
                intent.putExtra(Constants.CLIENT_PROFILE_DATA, profile);
                activity.startActivity(intent);
            }
        }

    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
//        isClickableView = false;
        if (urlEndPoint.equalsIgnoreCase(API_VIEW_CLIENT_PROFILE)) {
            progressBarProfile.setVisibility(View.GONE);
            tvViewProfile.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        activity.isClickableView = false;

        if (url.equals(API_CHAT_FILE_UPLOAD)) {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }

        } else {


            if (offerUrl.equalsIgnoreCase(url)) {


            } else {
                try {
                    ContractDetails project = ContractDetails.getContractDetails(responseBody);
                    if (progressBarManage != null && tvManage != null) {
                        progressBarManage.setVisibility(View.GONE);
                        tvManage.setVisibility(View.VISIBLE);
                    }

                    if (chatMessageAdapter != null && offerAdapterPos != -1) {
                        chatMessageAdapter.refreshOfferView(offerAdapterPos, false, 0);
                        offerAdapterPos = -1;
                    }

                    if (profileDialog != null) {
                        profileDialog.dismiss();
                    }
                    if (project != null) {
                        Intent i = new Intent(activity, ContractDetailsActivity.class);
                        i.putExtra(Constants.PROJECT, project);
                        if (selectedMessageId != null) {
                            i.putExtra("messageid", selectedMessageId);
                        }
                        i.putExtra("gigtype", project.gigType);//custom gig
                        activity.startActivity(i);
                        selectedMessageId = null;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        activity.isClickableView = false;
        if (url.equals(API_CHAT_FILE_UPLOAD)) {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
        }
        if (offerUrl.equalsIgnoreCase(url)) {

        } else {
            try {
                if (progressBarManage != null && tvManage != null) {
                    progressBarManage.setVisibility(View.GONE);
                    tvManage.setVisibility(View.VISIBLE);
                }
                if (chatMessageAdapter != null && offerAdapterPos != -1) {
                    chatMessageAdapter.refreshOfferView(offerAdapterPos, false, 0);
                    offerAdapterPos = -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void showAlert() {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        tvMessage.setText("Your message contains sensitive information like phone number and/or email. It might cause to block your account.\n\nAre you sure want to send the message?");

        tvCancel.setOnClickListener(v -> {
            binding.etMessage.setText("");
            dialog.dismiss();
        });

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            senMessageAPI("", "", getMessage());
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    void showWithdrawAlert(String offerID, long sK, Long messageId) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        tvMessage.setText(activity.getString(R.string.sure_to_withdraw_offer));

        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            withdrawOfferAPI(offerID, sK, messageId);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    String offerUrl = "";

    public void withdrawOfferAPI(String offerId, long sk, Long messageId) {
        if (!activity.isNetworkConnected())
            return;

        if (chatMessageAdapter != null) {
            chatMessageAdapter.refreshOfferView(offerAdapterPos, true, 0);
        }

        Call<CommonModel> call = activity.getGigService().withdrawOffer(API_WITHDRAW_OFFER, offerId, "#message#" + receiverID + "-" + activity.getUserID(), sk + "", activity.getJWT());
        call.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NonNull Call<CommonModel> call, @NonNull Response<CommonModel> response) {

                if (response.isSuccessful()) {
                    CommonModel user = response.body();
                    try {
                        sendLiveStatus(4, messageId);//emit withdraw event
                        if (user != null && user.status) {
                            activity.toastMessage(user.getMessage(activity.language));
                        }
                        if (chatMessageAdapter != null && offerAdapterPos != -1) {
                            chatMessageAdapter.refreshOfferView(offerAdapterPos, false, 4);
                            offerAdapterPos = -1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonModel> call, @NonNull Throwable t) {
                activity.toastMessage(activity.getString(R.string.something_went_wrong));
                if (chatMessageAdapter != null && offerAdapterPos != -1) {
                    chatMessageAdapter.refreshOfferView(offerAdapterPos, false, 0);
                    offerAdapterPos = -1;
                }
            }
        });
    }
}
