package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.adapter.AgentMediaAdapter;
import com.nojom.ccp.CCPCountry;
import com.nojom.databinding.ActivitySocialmediaNewBinding;
import com.nojom.databinding.DialogAddSocialmediaBinding;
import com.nojom.databinding.DialogDeleteBinding;
import com.nojom.databinding.DialogDiscardBinding;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.NumberTextWatcherForThousand;
import com.nojom.util.ReOrderSocialMoveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewSocialMediaActivity extends BaseActivity implements AgentMediaAdapter.UpdateSwipeListener, AgentMediaAdapter.OnClickListener {
    private ActivitySocialmediaNewBinding binding;
    private MyPlatformActivityVM nameActivityVM;
    private MutableLiveData<Boolean> isAnyChanges = new MutableLiveData<>();

    public static NewSocialMediaActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_socialmedia_new);
        nameActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);
        nameActivityVM.getInfluencerPlatform(this);
        if (language.equals("ar")) {
            setArFont(binding.tvTitle, Constants.FONT_AR_SEMI_BOLD);
            setArFont(binding.tvSave, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tv1, Constants.FONT_AR_BOLD);
            setArFont(binding.tv2, Constants.FONT_AR_MEDIUM);
        }
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameActivityVM.getConnectedPlatform(this);
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
//        binding.imgSorting.setOnClickListener(v -> startActivity(new Intent(this, ReOrderProfileActivity.class)));
        binding.relSave.setOnClickListener(v -> {
            //check if no item is present then open add dialog
            redirectActivity(SocialMediaActivity.class);
        });

        nameActivityVM.getSaveCompanyProgress().observe(this, integer -> {
            switch (integer) {
                case 1://save company start
                    if (dialogDiscardBinding != null && dialogDiscard.isShowing()) {
                        dialogDiscardBinding.tvSend.setVisibility(View.INVISIBLE);
                        dialogDiscardBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                    if (dialogDeleteBinding != null && dialogDelete.isShowing()) {
                        dialogDeleteBinding.tvSend.setVisibility(View.INVISIBLE);
                        dialogDeleteBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                    if (dialogAddCompany != null && dialogAddCompany.isShowing()) {
                        dialogAddSocialmediaBinding.tvSend.setVisibility(View.INVISIBLE);
                        dialogAddSocialmediaBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                    break;
                case 11://save company success
                    if (dialogDiscard != null && dialogDiscard.isShowing()) {
                        dialogDiscard.dismiss();
                    }
                    if (dialogDelete != null && dialogDelete.isShowing()) {
                        dialogDelete.dismiss();
                    }
                    if (dialogAddCompany != null && dialogAddCompany.isShowing()) {
                        dialogAddCompany.dismiss();
                    }

                    break;
                case 0://save company over
                    if (dialogDiscardBinding != null && dialogDiscard.isShowing()) {
                        dialogDiscardBinding.tvSend.setVisibility(View.VISIBLE);
                        dialogDiscardBinding.progressBar.setVisibility(View.GONE);
                    }
                    if (dialogDeleteBinding != null && dialogDelete.isShowing()) {
                        dialogDeleteBinding.tvSend.setVisibility(View.VISIBLE);
                        dialogDeleteBinding.progressBar.setVisibility(View.GONE);
                    }
                    if (dialogAddCompany != null && dialogAddCompany.isShowing()) {
                        dialogAddSocialmediaBinding.tvSend.setVisibility(View.VISIBLE);
                        dialogAddSocialmediaBinding.progressBar.setVisibility(View.GONE);
                        dialogAddCompany.dismiss();
                    }
                    break;

                case 2://delete company success
                    if (dialogDelete != null) {
                        dialogDelete.dismiss();
                    }
                    break;
            }
        });

        mAdapter = new AgentMediaAdapter(this, this, this);
        binding.rMenu.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new ReOrderSocialMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rMenu);

        nameActivityVM.getConnectedMediaDataList().observe(this, data -> {
            if (data != null && data.size() > 0) {
                binding.linList.setVisibility(View.VISIBLE);
                binding.linPh.setVisibility(View.GONE);
                setConnectedMediaAdapter(data);
            } else {
                binding.linList.setVisibility(View.GONE);
                binding.linPh.setVisibility(View.VISIBLE);
            }
        });

        /*SwipeController swipeController = new SwipeController(this, new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
//                adapter.players.remove(position);
//                /adapter.notifyItemRemoved(position);
//                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                deleteChangesDialog(mAdapter.getData(position).id, mAdapter.getData(position));

            }

            @Override
            public void onLeftClicked(int position) {
                super.onLeftClicked(position);
                addMediaDialog(mAdapter.getData(position));

            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(binding.rMenu);

        binding.rMenu.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });*/

        isAnyChanges.observe(this, aBoolean -> {
            if (aBoolean) {
                DrawableCompat.setTint(dialogAddSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(NewSocialMediaActivity.this, R.color.black));
                dialogAddSocialmediaBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
            } else {
                DrawableCompat.setTint(dialogAddSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(NewSocialMediaActivity.this, R.color.C_E5E5EA));
                dialogAddSocialmediaBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
            }
        });
    }


    AgentMediaAdapter mAdapter;

    private void setConnectedMediaAdapter(ArrayList<ConnectedSocialMedia.Data> data) {
        mAdapter.doRefresh(data);
    }

    private DialogDiscardBinding dialogDiscardBinding;
    private DialogAddSocialmediaBinding dialogAddSocialmediaBinding;
    private DialogDeleteBinding dialogDeleteBinding;
    private Dialog dialogDiscard, dialogDelete;
    private Dialog dialogAddCompany;


    public void addMediaDialog(ConnectedSocialMedia.Data data) {
        dialogAddCompany = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogAddCompany.setTitle(null);
        dialogAddSocialmediaBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_add_socialmedia, null, false);

        dialogAddSocialmediaBinding.title.setText(getString(R.string.add_social_media));
        dialogAddSocialmediaBinding.swShow.setText(getString(R.string.show_followers_for_all_user));
        dialogAddSocialmediaBinding.tvSend.setText(getString(R.string.save));
        dialogAddSocialmediaBinding.defaultTextInputLayout.setHint(getString(R.string.username));
        dialogAddSocialmediaBinding.defaultTextInputLayoutTime.setHint(getString(R.string.num_of_followers_optional));

        dialogAddSocialmediaBinding.etTime.addTextChangedListener(new NumberTextWatcherForThousand(dialogAddSocialmediaBinding.etTime));

        if (language.equals("ar")) {
            setArFont(dialogAddSocialmediaBinding.title, Constants.FONT_AR_BOLD);
            setArFont(dialogAddSocialmediaBinding.etName, Constants.FONT_AR_REGULAR);
            setArFont(dialogAddSocialmediaBinding.etContact, Constants.FONT_AR_REGULAR);
            setArFont(dialogAddSocialmediaBinding.etContact, Constants.FONT_AR_REGULAR);
            setArFont(dialogAddSocialmediaBinding.txtLink, Constants.FONT_AR_REGULAR);
            setArFont(dialogAddSocialmediaBinding.etTime, Constants.FONT_AR_REGULAR);
            setArFont(dialogAddSocialmediaBinding.swShow, Constants.FONT_AR_MEDIUM);
            setArFont(dialogAddSocialmediaBinding.tvSend, Constants.FONT_AR_BOLD);
        }

        dialogAddCompany.setContentView(dialogAddSocialmediaBinding.getRoot());
        dialogAddCompany.setCancelable(true);
        isAnyChanges.postValue(false);

        if (data != null) {

            if (data.name.contains("Whatsapp") || data.name.contains("Telegram")) {
                dialogAddSocialmediaBinding.relUname.setVisibility(View.GONE);
                dialogAddSocialmediaBinding.linContactNo.setVisibility(View.VISIBLE);

                String[] result = getCountryFromNumber(data.username.replace("+", ""));
                if (result != null) {
                    System.out.println("Country Code: " + result[0]);
                    System.out.println("Phone Number: " + result[1]);
                    dialogAddSocialmediaBinding.etContact.setText(String.format("%s", result[1]));
                    dialogAddSocialmediaBinding.ccp.setCountryForPhoneCode(Integer.parseInt(result[0]));
                    dialogAddSocialmediaBinding.tvCode.setText("(" + dialogAddSocialmediaBinding.ccp.getSelectedCountryCodeWithPlus() + ")");
                }

//            dialogAddSocialmediaBinding.defaultTextInputLayout.setHint(String.format(context.getString(R.string.add_s_number), socialPlatform.getName(context.language)));
//            dialogAddSocialmediaBinding.etName.setInputType(InputType.TYPE_CLASS_PHONE);


                dialogAddSocialmediaBinding.ccp.registerCarrierNumberEditText(dialogAddSocialmediaBinding.etContact);
                dialogAddSocialmediaBinding.ccp.setOnCountryChangeListener(() -> {
                    dialogAddSocialmediaBinding.etContact.setText("");
                    dialogAddSocialmediaBinding.tvCode.setText("(" + dialogAddSocialmediaBinding.ccp.getSelectedCountryCodeWithPlus() + ")");
                    dialogAddSocialmediaBinding.ccp.setTag(dialogAddSocialmediaBinding.ccp.getSelectedCountryCodeWithPlus());
                });

                dialogAddSocialmediaBinding.etContact.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (TextUtils.isEmpty(s)) {
                            dialogAddSocialmediaBinding.txtLink.setVisibility(View.GONE);
                            DrawableCompat.setTint(dialogAddSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(NewSocialMediaActivity.this, R.color.C_E5E5EA));
                            dialogAddSocialmediaBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
                        } else {
                            dialogAddSocialmediaBinding.txtLink.setVisibility(View.VISIBLE);
                            dialogAddSocialmediaBinding.txtLink.setText(data.web_url + "" + s);
                            DrawableCompat.setTint(dialogAddSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(NewSocialMediaActivity.this, R.color.black));
                            dialogAddSocialmediaBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
                        }
                        isAnyChanges.postValue(true);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } else {
                dialogAddSocialmediaBinding.relUname.setVisibility(View.VISIBLE);
                dialogAddSocialmediaBinding.linContactNo.setVisibility(View.GONE);
                dialogAddSocialmediaBinding.etName.setText(String.format("%s", data.username));
                dialogAddSocialmediaBinding.etName.setSelection(dialogAddSocialmediaBinding.etName.getText().length());
            }


            dialogAddSocialmediaBinding.etTime.setText(String.format("%s", data.followers + ""));

            dialogAddSocialmediaBinding.swShow.setChecked(data.is_public == 1);

            dialogAddSocialmediaBinding.etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(s)) {
                        dialogAddSocialmediaBinding.txtLink.setVisibility(View.GONE);
                        DrawableCompat.setTint(dialogAddSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(NewSocialMediaActivity.this, R.color.C_E5E5EA));
                        dialogAddSocialmediaBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
                    } else {
                        dialogAddSocialmediaBinding.txtLink.setVisibility(View.VISIBLE);
                        if (s.toString().startsWith("http")) {
                            dialogAddSocialmediaBinding.txtLink.setText(s);
                        } else {
                            dialogAddSocialmediaBinding.txtLink.setText(data.web_url + "" + s);
                        }
                        DrawableCompat.setTint(dialogAddSocialmediaBinding.relSave.getBackground(), ContextCompat.getColor(NewSocialMediaActivity.this, R.color.black));
                        dialogAddSocialmediaBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
                    }
                    isAnyChanges.postValue(true);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            dialogAddSocialmediaBinding.etName.setText(String.format("%s", data.username));
            dialogAddSocialmediaBinding.etName.setSelection(dialogAddSocialmediaBinding.etName.getText().length());

            dialogAddSocialmediaBinding.etTime.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    isAnyChanges.postValue(true);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            dialogAddSocialmediaBinding.swShow.setOnCheckedChangeListener((compoundButton, b) -> {
                isAnyChanges.postValue(true);
            });

//            txtTitle.setText(String.format(getString(R.string.edit_s), data.getName(language)));

            Glide.with(this).load(data.filename).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(dialogAddSocialmediaBinding.imgPlatform);
            dialogAddSocialmediaBinding.tvCancel.setVisibility(View.VISIBLE);

        }


        dialogAddSocialmediaBinding.tvCancel.setOnClickListener(v -> {
            if (Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            }
            discardChangesDialog(dialogAddCompany, data);
        });

        dialogAddSocialmediaBinding.relSave.setOnClickListener(v -> {
            if (Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            }

            String uname;
            if (data != null) {
                if (data.name.contains("Whatsapp") || data.name.contains("Telegram")) {
                    if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etContact.getText().toString().trim())) {
                        return;
                    }
                    uname = dialogAddSocialmediaBinding.ccp.getSelectedCountryCodeWithPlus() + dialogAddSocialmediaBinding.etContact.getText().toString();
                } else {
                    if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etName.getText().toString().trim())) {
                        return;
                    }
                    uname = dialogAddSocialmediaBinding.etName.getText().toString();
                }

//            if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etName.getText().toString().trim())) {
//                return;
//            }

                nameActivityVM.editPlatform(uname, dialogAddSocialmediaBinding.etTime.getText().toString(), data.id, dialogAddSocialmediaBinding.swShow.isChecked() ? 1 : 0, data.public_status);
            }
        });
//
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogAddCompany.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogAddCompany.show();
        dialogAddCompany.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddCompany.getWindow().setAttributes(lp);
    }

    public void discardChangesDialog(Dialog dialogMain, ConnectedSocialMedia.Data data) {
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


        dialogDiscardBinding.tvCancel.setOnClickListener(v -> {
            dialogDiscard.dismiss();
            dialogMain.dismiss();
        });

        dialogDiscardBinding.relSave.setOnClickListener(v -> {
            String uname;
            if (data != null) {
                if (data.name.contains("Whatsapp") || data.name.contains("Telegram")) {
                    if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etContact.getText().toString().trim())) {
                        dialogDiscard.dismiss();
                        return;
                    }
                    uname = dialogAddSocialmediaBinding.ccp.getSelectedCountryCodeWithPlus() + dialogAddSocialmediaBinding.etContact.getText().toString();
                } else {
                    if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etName.getText().toString().trim())) {
                        dialogDiscard.dismiss();
                        return;
                    }
                    uname = dialogAddSocialmediaBinding.etName.getText().toString();
                }

//            if (TextUtils.isEmpty(dialogAddSocialmediaBinding.etName.getText().toString().trim())) {
//                return;
//            }

                dialogDiscard.dismiss();
                nameActivityVM.editPlatform(uname, dialogAddSocialmediaBinding.etTime.getText().toString(), data.id, dialogAddSocialmediaBinding.swShow.isChecked() ? 1 : 0, data.public_status);
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

    public void deleteChangesDialog(int itemId, ConnectedSocialMedia.Data data) {
        dialogDelete = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDelete.setTitle(null);
        dialogDeleteBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_delete, null, false);
        dialogDelete.setContentView(dialogDeleteBinding.getRoot());
//        dialog.setContentView(R.layout.dialog_discard);
        dialogDelete.setCancelable(true);

        if (language.equals("ar")) {
            setArFont(dialogDeleteBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(dialogDeleteBinding.txtDesc, Constants.FONT_AR_REGULAR);
            setArFont(dialogDeleteBinding.tvSend, Constants.FONT_AR_BOLD);
            setArFont(dialogDeleteBinding.tvCancel, Constants.FONT_AR_BOLD);
        }

//        dialogDeleteBinding.txtTitle.setText(getString(R.string.delete_item));
//        dialogDeleteBinding.txtDesc.setText(getString(R.string.this_video_or_image_will_be_deleted_and_you_can_t_undo_this_action));
//        dialogDeleteBinding.tvSend.setText(getString(R.string.yes_delete));
//        dialogDeleteBinding.tvCancel.setText(getString(R.string.cancel));

        dialogDeleteBinding.txtTitle.setText(getString(R.string.delete) + " " + data.getName(language));
        dialogDeleteBinding.txtDesc.setText(getString(R.string.you_re_going_to_delete_the_sm) + " \"" + data.getName(language) + "\"" + getString(R.string._are_you_sure));
        dialogDeleteBinding.tvSend.setText(getString(R.string.yes_delete));
        dialogDeleteBinding.tvCancel.setText(getString(R.string.no_keep_it));


        dialogDeleteBinding.tvCancel.setOnClickListener(v -> dialogDelete.dismiss());

        dialogDeleteBinding.relSave.setOnClickListener(v -> {
            nameActivityVM.deletePlatform(itemId);
            dialogDelete.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDelete.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDelete.show();
        dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.getWindow().setAttributes(lp);
    }


    public void whoCanSeeDialog(ConnectedSocialMedia.Data companies) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_who);
        dialog.setCancelable(true);
        RadioButton chkPublic = dialog.findViewById(R.id.chk_public);
        RadioButton chkBrand = dialog.findViewById(R.id.chk_brand);
        RadioButton chkMe = dialog.findViewById(R.id.chk_me);
        ImageView tvCancel = dialog.findViewById(R.id.tv_cancel);
        RelativeLayout relSave = dialog.findViewById(R.id.rel_save);
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
//            newPortfolioActivityVM.updatePortfolio(1, status, companies.id, "");
            nameActivityVM.editPlatform(companies.username, companies.followers + "", companies.id, companies.is_public, status);
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

//    @Override
//    public void onClickShow(ConnectedSocialMedia.Data companies, int pos) {
//        whoCanSeeDialog(companies);
//    }

    public static Drawable drawableFromUrl(String url) throws IOException, IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), x);
    }

    @Override
    public void onSwipeSuccess(List<ConnectedSocialMedia.Data> mDatasetFiltered) {
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
            nameActivityVM.reOrderMedia(this, jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClickShow(ConnectedSocialMedia.Data companies, int pos) {
        whoCanSeeDialog(companies);
    }

    @Override
    public void onClickMenu(ConnectedSocialMedia.Data companies, int pos, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];
        showPopupMenu(view, companies, point);
    }

    private void showPopupMenu(View view, ConnectedSocialMedia.Data companies, Point point) throws RuntimeException {

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
        popupWindow.showAtLocation(popupView.getRootView(), Gravity.NO_GRAVITY, point.x - 400, point.y + 60);

        logoutParent.setOnClickListener(v -> popupWindow.dismiss());
        txtEdit.setOnClickListener(v -> {
            addMediaDialog(companies);
            popupWindow.dismiss();
        });
        txtView.setOnClickListener(v -> {
            whoCanSeeDialog(companies);
            popupWindow.dismiss();
        });
        txtDelete.setOnClickListener(v -> {
            deleteChangesDialog(companies.id, companies);
            popupWindow.dismiss();
        });

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

    private String[] getCountryFromNumber(String inputNumber) {
        List<CCPCountry> masterCountries = CCPCountry.getLibraryMasterCountriesEnglish(activity);

        String[] result = separateCountryCodeAndPhoneNumber(masterCountries, inputNumber);
        if (result != null) {
            System.out.println("Country Code: " + result[0]);
            System.out.println("Phone Number: " + result[1]);
        }
        return result;
    }

    private static String[] separateCountryCodeAndPhoneNumber(List<CCPCountry> countries, String inputNumber) {
        for (CCPCountry country : countries) {
            String countryCode = country.getPhoneCode();
            if (inputNumber.startsWith(countryCode)) {
                String phoneNumber = inputNumber.substring(countryCode.length());
                return new String[]{countryCode, phoneNumber};
            }
        }
        return null; // Return null if no match is found.
    }
}
