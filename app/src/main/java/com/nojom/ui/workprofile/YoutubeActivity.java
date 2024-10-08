package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.nojom.R;
import com.nojom.adapter.YoutubeAdapter;
import com.nojom.databinding.ActivityYoutubeBinding;
import com.nojom.databinding.DialogAddYoutubeBinding;
import com.nojom.databinding.DialogDeleteBinding;
import com.nojom.databinding.DialogDiscardBinding;
import com.nojom.model.GetYoutube;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.ReOrderYoutubeMoveCallback;
import com.nojom.util.YouTubeUrlExtractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeActivity extends BaseActivity implements YoutubeAdapter.OnClickListener,
        YoutubeAdapter.UpdateSwipeListener {
    private ActivityYoutubeBinding binding;
    private YoutubeActivityVM youtubeActivityVM;
    private static final String YOUTUBE_URL_PATTERN =
            "^https?://(?:www\\.)?(?:youtube\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)|\\S*[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})$";

    private MutableLiveData<Boolean> isAnyChanges = new MutableLiveData<>();
    private int selectedAdapterPos = -1;

//    public boolean isValidYouTubeUrl(String url) {
//        Pattern pattern = Pattern.compile(YOUTUBE_URL_PATTERN, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(url);
//        return matcher.matches();
//    }

    private static final String YOUTUBE_URL_PATTERN_M =
            "^https?:\\/\\/(www\\.)?(m\\.)?(youtube\\.com|youtu\\.be)\\/watch\\?v=[a-zA-Z0-9_-]{11}$";

    public static boolean isValidYouTubeUrlM(String url) {
        return url.matches(YOUTUBE_URL_PATTERN_M);
    }

    public static boolean isValidYouTubeUrl(String youTubeURl) {
        boolean success;
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";

        // Not Valid youtube URL
        success = !youTubeURl.isEmpty() && youTubeURl.matches(pattern);
        if (!success) {
            try {
                String tempUrl = youTubeURl;
                youTubeURl = YouTubeUrlExtractor.extractYouTubeUrl(youTubeURl);
                if (youTubeURl != null) {
                    success = isValidYouTubeUrl(youTubeURl);
                } else {
                    success = isValidYouTubeUrlM(tempUrl);
                    /*if (tempUrl.contains("https://m.youtube.com")) {
                        return true;
                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_youtube);
        youtubeActivityVM = ViewModelProviders.of(this).get(YoutubeActivityVM.class);
        youtubeActivityVM.init(this);
        if (language.equals("ar")) {
            setArFont(binding.tvTitle, Constants.FONT_AR_SEMI_BOLD);
            setArFont(binding.tvSave, Constants.FONT_AR_MEDIUM);
        }
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.imgSorting.setOnClickListener(v -> startActivity(new Intent(this, ReOrderProfileActivity.class)));
        binding.relSave.setOnClickListener(v -> {
            //check if no item is present then open add dialog
            addYoutube(null, -1);
        });

        adapter = new YoutubeAdapter(this, this, this);
        binding.rMenu.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ReOrderYoutubeMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rMenu);

        youtubeActivityVM.getYoutubeListData().observe(this, youtubeData -> {
            if (youtubeData.data != null && youtubeData.data.size() > 0) {
                binding.linList.setVisibility(View.VISIBLE);
                binding.linPh.setVisibility(View.GONE);
                setYoutubeAdapter(youtubeData);
            } else {
                binding.linList.setVisibility(View.GONE);
                binding.linPh.setVisibility(View.VISIBLE);
            }
        });

        youtubeActivityVM.getSaveCompanyProgress().observe(this, integer -> {
            switch (integer) {
                case 1://save company start
                    if (addYoutubeBinding != null && dialogAddYoutube.isShowing()) {
                        addYoutubeBinding.tvSend.setVisibility(View.INVISIBLE);
                        addYoutubeBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                    if (dialogDiscardBinding != null && dialogDiscard.isShowing()) {
                        dialogDiscardBinding.tvSend.setVisibility(View.INVISIBLE);
                        dialogDiscardBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                    if (dialogDeleteBinding != null && dialogDelete.isShowing()) {
                        dialogDeleteBinding.tvSend.setVisibility(View.INVISIBLE);
                        dialogDeleteBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                    break;
                case 11://save company success
                    if (dialogDiscard != null && dialogDiscard.isShowing()) {
                        dialogDiscard.dismiss();
                    }
                    if (dialogDelete != null && dialogDelete.isShowing()) {
                        dialogDelete.dismiss();
                    }
                    if (dialogAddYoutube != null && dialogAddYoutube.isShowing()) {
                        dialogAddYoutube.dismiss();
                    }

                    break;
                case 0://save company over
                    if (addYoutubeBinding != null && dialogAddYoutube.isShowing()) {
                        addYoutubeBinding.tvSend.setVisibility(View.VISIBLE);
                        addYoutubeBinding.progressBar.setVisibility(View.GONE);
                    }
                    if (dialogDiscardBinding != null && dialogDiscard.isShowing()) {
                        dialogDiscardBinding.tvSend.setVisibility(View.VISIBLE);
                        dialogDiscardBinding.progressBar.setVisibility(View.GONE);
                    }
                    if (dialogDeleteBinding != null && dialogDelete.isShowing()) {
                        dialogDeleteBinding.tvSend.setVisibility(View.VISIBLE);
                        dialogDeleteBinding.progressBar.setVisibility(View.GONE);
                    }
                    break;

                case 2://delete company success
                    if (dialogDelete != null) {
                        dialogDelete.dismiss();
                    }
                    break;
            }
        });

        isAnyChanges.observe(this, aBoolean -> {
            if (aBoolean) {
                DrawableCompat.setTint(addYoutubeBinding.relSave.getBackground(), ContextCompat.getColor(YoutubeActivity.this, R.color.black));
            } else {
                DrawableCompat.setTint(addYoutubeBinding.relSave.getBackground(), ContextCompat.getColor(YoutubeActivity.this, R.color.c_AEAEB2));
            }
        });

        youtubeActivityVM.updateCompany.observe(this, integer -> {

            if (integer == 11) {//update data success

            }
        });

    }


    YoutubeAdapter adapter;

    private void setYoutubeAdapter(GetYoutube data) {
        adapter.setSelectedAdapter(selectedAdapterPos);
        if (selectedAdapterPos != -1) {
            adapter.doRefresh(data.data, selectedAdapterPos);
            selectedAdapterPos = -1;
        } else {
            adapter.doRefresh(data.data);
        }
    }

    private void setCompanyAdapter() {

    }

    private DialogAddYoutubeBinding addYoutubeBinding;
    private DialogDiscardBinding dialogDiscardBinding;
    private DialogDeleteBinding dialogDeleteBinding;
    private Dialog dialogAddYoutube;
    private Dialog dialogDiscard, dialogDelete;

    public void addYoutube(GetYoutube.Data data, int adpPos) {
        dialogAddYoutube = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogAddYoutube.setTitle(null);
        addYoutubeBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_add_youtube, null, false);

        addYoutubeBinding.title.setText(getString(R.string.add_youtube_video));
        addYoutubeBinding.tvSend.setText(getString(R.string.save));
        addYoutubeBinding.error.setText(getString(R.string.it_looks_like_the_link_you_entered_is_not_correct_please_enter_a_valid_youtube_link_if_you_need_assistance_feel_free_to_contact_our_support_team));
        addYoutubeBinding.defaultTextInputLayout.setHint(getString(R.string.video_link));
        if (language.equals("ar")) {
            setArFont(addYoutubeBinding.title, Constants.FONT_AR_BOLD);
            setArFont(addYoutubeBinding.etName, Constants.FONT_AR_REGULAR);
            setArFont(addYoutubeBinding.tvSend, Constants.FONT_AR_BOLD);
        }
        dialogAddYoutube.setContentView(addYoutubeBinding.getRoot());
        dialogAddYoutube.setCancelable(true);
        isAnyChanges.setValue(false);
        DrawableCompat.setTint(addYoutubeBinding.relSave.getBackground(), ContextCompat.getColor(YoutubeActivity.this, R.color.c_AEAEB2));
        if (data != null) {
            addYoutubeBinding.etName.setText(data.link);
        }

        addYoutubeBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    addYoutubeBinding.etName.setCompoundDrawables(null, null, null, null);
                    addYoutubeBinding.etName.setCompoundDrawablePadding(15);
                    addYoutubeBinding.etName.setTag("");
//                    DrawableCompat.setTint(addYoutubeBinding.relSave.getBackground(), ContextCompat.getColor(YoutubeActivity.this, R.color.c_AEAEB2));
                    isAnyChanges.setValue(false);
                } else {
                    if (isValid()) {
                        DrawableCompat.setTint(addYoutubeBinding.relSave.getBackground(), ContextCompat.getColor(YoutubeActivity.this, R.color.black));
                        isAnyChanges.setValue(true);
                        addYoutubeBinding.error.setVisibility(View.GONE);
                        addYoutubeBinding.relView.setBackground(getResources().getDrawable(R.drawable.gray_l_border_6));
                        addYoutubeBinding.defaultTextInputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.C_3C3C43)));
                    } else {
                        DrawableCompat.setTint(addYoutubeBinding.relSave.getBackground(), ContextCompat.getColor(YoutubeActivity.this, R.color.c_AEAEB2));
                        isAnyChanges.setValue(false);
                        addYoutubeBinding.error.setVisibility(View.VISIBLE);
                        addYoutubeBinding.relView.setBackground(getResources().getDrawable(R.drawable.red_l_border_6));
                        addYoutubeBinding.defaultTextInputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.C_FF3B30)));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addYoutubeBinding.tvCancel.setOnClickListener(v -> {
            if (data == null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddYoutube.dismiss();
                return;
            } else if (data != null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddYoutube.dismiss();
                return;
            }
            discardChangesDialog(dialogAddYoutube, data);
        });

        addYoutubeBinding.relSave.setOnClickListener(v -> {
            if (data != null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddYoutube.dismiss();
                return;
            }
            if (TextUtils.isEmpty(addYoutubeBinding.etName.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_your_link));
                return;
            }
            if (!isValidYouTubeUrl(addYoutubeBinding.etName.getText().toString().trim())) {
                //toastMessage(getString(R.string.please_enter_valid_link));
                return;
            }

            if (data != null) {//edit case
                selectedAdapterPos = adpPos;
                youtubeActivityVM.updateYoutube(
                        data.public_status,
                        data.id, addYoutubeBinding.etName.getText().toString());
            } else {//add case
                youtubeActivityVM.addYoutube(addYoutubeBinding.etName.getText().toString());
            }
        });
//
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogAddYoutube.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogAddYoutube.show();
        dialogAddYoutube.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddYoutube.getWindow().setAttributes(lp);
    }

    public void discardChangesDialog(Dialog dialogMain, GetYoutube.Data data) {
        dialogDiscard = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDiscard.setTitle(null);
        dialogDiscardBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_discard, null, false);
        if (language.equals("ar")) {
            setArFont(dialogDiscardBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(dialogDiscardBinding.txtDesc, Constants.FONT_AR_REGULAR);
            setArFont(dialogDiscardBinding.tvSend, Constants.FONT_AR_BOLD);
            setArFont(dialogDiscardBinding.tvCancel, Constants.FONT_AR_BOLD);
        }
        dialogDiscard.setContentView(dialogDiscardBinding.getRoot());
//        dialog.setContentView(R.layout.dialog_discard);
        dialogDiscard.setCancelable(true);
//        TextView tvSend = dialog.findViewById(R.id.tv_send);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        dialogDiscardBinding.txtTitle.setText(getString(R.string.save_changes));
        dialogDiscardBinding.txtDesc.setText(getString(R.string.would_you_like_to_save_before_exiting));
        dialogDiscardBinding.tvSend.setText(getString(R.string.save));
        dialogDiscardBinding.tvCancel.setText(getString(R.string.discard_1));


        dialogDiscardBinding.tvCancel.setOnClickListener(v -> {
            dialogDiscard.dismiss();
            dialogMain.dismiss();
        });

        dialogDiscardBinding.relSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(addYoutubeBinding.etName.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_your_link));
                return;
            }
            if (!isValidYouTubeUrl(addYoutubeBinding.etName.getText().toString().trim())) {
                toastMessage(getString(R.string.please_enter_valid_link));
                return;
            }

            if (data != null) {//edit case
                youtubeActivityVM.updateYoutube(
                        data.public_status,
                        data.id, addYoutubeBinding.etName.getText().toString());
            } else {//add case
                youtubeActivityVM.addYoutube(addYoutubeBinding.etName.getText().toString());
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDiscard.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDiscard.show();
        dialogDiscard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDiscard.getWindow().setAttributes(lp);
    }

    public void deleteChangesDialog(int itemId, GetYoutube.Data data, String title, int adpPos) {
        dialogDelete = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDelete.setTitle(null);
        dialogDeleteBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_delete, null, false);
        dialogDelete.setContentView(dialogDeleteBinding.getRoot());
//        dialog.setContentView(R.layout.dialog_discard);
        dialogDelete.setCancelable(true);
        if (TextUtils.isEmpty(title.trim())) {
            title = getString(R.string.video_);
        }
        if (language.equals("ar")) {
            setArFont(dialogDeleteBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(dialogDeleteBinding.txtDesc, Constants.FONT_AR_REGULAR);
            setArFont(dialogDeleteBinding.tvSend, Constants.FONT_AR_BOLD);
            setArFont(dialogDeleteBinding.tvCancel, Constants.FONT_AR_BOLD);
        }
        dialogDeleteBinding.txtTitle.setText(getString(R.string.delete_youtube_video));
        dialogDeleteBinding.txtDesc.setText(String.format(getString(R.string.you_re_going_to_delete_the_s), title) + getString(R.string._are_you_sure));
        dialogDeleteBinding.tvSend.setText(getString(R.string.yes_delete));
        dialogDeleteBinding.tvCancel.setText(getString(R.string.no_keep_it));


        dialogDeleteBinding.tvCancel.setOnClickListener(v -> {
            dialogDelete.dismiss();
        });

        dialogDeleteBinding.relSave.setOnClickListener(v -> {
            youtubeActivityVM.deleteYoutube(itemId);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDelete.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDelete.show();
        dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.getWindow().setAttributes(lp);
    }


    public void whoCanSeeDialog(GetYoutube.Data companies, boolean isOnlySet, TextView textView, int adpPos) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_who);
        dialog.setCancelable(true);
        RadioButton chkPublic = dialog.findViewById(R.id.chk_public);
        RadioButton chkBrand = dialog.findViewById(R.id.chk_brand);
        RadioButton chkMe = dialog.findViewById(R.id.chk_me);
        ImageView tvCancel = dialog.findViewById(R.id.tv_cancel);
        RelativeLayout relSave = dialog.findViewById(R.id.rel_save);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);

        TextView txtTitle = dialog.findViewById(R.id.txt_title);
        TextView txtLblPub = dialog.findViewById(R.id.txt_lbl_public);
        TextView txtLblBrn = dialog.findViewById(R.id.txt_lbl_brand);
        TextView txtLblMe = dialog.findViewById(R.id.txt_lbl_me);
        TextView tv1 = dialog.findViewById(R.id.tv1);
        TextView tv2 = dialog.findViewById(R.id.tv2);
        TextView tv3 = dialog.findViewById(R.id.tv3);
        TextView tvSend = dialog.findViewById(R.id.tv_send);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        if (language.equals("ar")) {
//            setArFont(tvCancel, Constants.FONT_AR_REGULAR);
            setArFont(txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(txtLblPub, Constants.FONT_AR_REGULAR);
            setArFont(txtLblBrn, Constants.FONT_AR_REGULAR);
            setArFont(txtLblMe, Constants.FONT_AR_REGULAR);
            setArFont(tv1, Constants.FONT_AR_REGULAR);
            setArFont(tv2, Constants.FONT_AR_REGULAR);
            setArFont(tv3, Constants.FONT_AR_REGULAR);
            setArFont(tvSend, Constants.FONT_AR_BOLD);
        }

        if (companies != null) {
            switch (companies.public_status) {
                case 1:
                    chkPublic.setChecked(true);
                    break;
                case 2:
                    chkBrand.setChecked(true);
                    break;
                case 3:
                    chkMe.setChecked(true);
                    break;
            }
        }

        chkMe.setOnClickListener(v -> {
            chkMe.setChecked(true);
            chkBrand.setChecked(false);
            chkPublic.setChecked(false);
        });
        chkBrand.setOnClickListener(v -> {
            chkMe.setChecked(false);
            chkBrand.setChecked(true);
            chkPublic.setChecked(false);
        });
        chkPublic.setOnClickListener(v -> {
            chkMe.setChecked(false);
            chkBrand.setChecked(false);
            chkPublic.setChecked(true);
        });

        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        relSave.setOnClickListener(v -> {
            int status = 0;
            if (chkPublic.isChecked()) {
                status = 1;
            } else if (chkBrand.isChecked()) {
                status = 2;
            } else if (chkMe.isChecked()) {
                status = 3;
            }

            if (status == 0) {
                toastMessage(getString(R.string.please_select_any));
                return;
            }


            if (isOnlySet) {
                textView.setTag(status);
                setPublicStatusValue(status, textView);
            } else if (companies != null) {
                selectedAdapterPos = adpPos;
                youtubeActivityVM.updateYoutube(status, companies.id, companies.link + "");
            }

            dialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onClickShow(GetYoutube.Data companies, int pos) {
        whoCanSeeDialog(companies, false, null, pos);
    }

    @Override
    public void onClickMenu(GetYoutube.Data companies, int pos, View view, int location1, String title) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];
        showPopupMenu(view, companies, point, location1, title, pos);
    }

    @Override
    public void onSwipeSuccess(List<GetYoutube.Data> mDatasetFiltered) {
        JSONArray jsonArray = new JSONArray();
        JSONObject mainObj = new JSONObject();
        for (int i = 0; i < mDatasetFiltered.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", mDatasetFiltered.get(i).id);
                jsonObject.put("display_order", i + 1);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            mainObj.put("reorder", jsonArray);
            youtubeActivityVM.reOrderMedia(this, jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private boolean isValid() {
        if (TextUtils.isEmpty(addYoutubeBinding.etName.getText().toString().trim())) {
            return false;
        }
        return isValidYouTubeUrl(addYoutubeBinding.etName.getText().toString().trim());
    }

    private void setPublicStatusValue(int publicStatus, TextView txtStatus) {
        txtStatus.setTag(publicStatus);
        switch (publicStatus) {
            case 2://brands
                txtStatus.setText(getString(R.string.brand_only));
                txtStatus.setTextColor(getResources().getColor(R.color.c_075E45));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_C7EBD1));
                break;
            case 3://only me
                txtStatus.setText(getString(R.string.only_me));
                txtStatus.setTextColor(getResources().getColor(R.color.red_dark));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_FADCD9));
                break;
            default:
                txtStatus.setText(getString(R.string.public_));
                txtStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_D4E4FA));
                break;
        }
    }

    private void showPopupMenu(View view, GetYoutube.Data companies, Point point, int location, String title, int adpPos) throws RuntimeException {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.popup_menu, null);
        LinearLayout logoutParent = popupView.findViewById(R.id.parent);
        TextView txtEdit = popupView.findViewById(R.id.txt_edit);
        TextView txtView = popupView.findViewById(R.id.txt_view);
        TextView txtStatus = popupView.findViewById(R.id.txt_status);
        TextView txtDelete = popupView.findViewById(R.id.txt_delete);
        if (language.equals("ar")) {
            setArFont(txtEdit, Constants.FONT_AR_REGULAR);
            setArFont(txtView, Constants.FONT_AR_REGULAR);
            setArFont(txtStatus, Constants.FONT_AR_BOLD);
            setArFont(txtDelete, Constants.FONT_AR_REGULAR);
        }
        setPublicStatusValue(companies.public_status, txtStatus);

        PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setContentView(popupView.getRootView());
        popupWindow.setWidth((int) getResources().getDimension(R.dimen._170sdp));
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(3);
        int val;
        if (location >= 1000) {
            val = (location / 2) - (int) getResources().getDimension(R.dimen._25sdp);
        } else {
            val = (location / 2) - (int) getResources().getDimension(R.dimen._20sdp);
        }
        popupWindow.showAtLocation(popupView.getRootView(), Gravity.NO_GRAVITY, point.x - val, point.y + 60);

        logoutParent.setOnClickListener(v -> popupWindow.dismiss());
        txtEdit.setOnClickListener(v -> {
            addYoutube(companies, adpPos);
            popupWindow.dismiss();
        });
        txtView.setOnClickListener(v -> {
            whoCanSeeDialog(companies, false, null, adpPos);
            popupWindow.dismiss();
        });
        txtDelete.setOnClickListener(v -> {
            deleteChangesDialog(companies.id, companies, title, adpPos);
            popupWindow.dismiss();
        });

    }
}
