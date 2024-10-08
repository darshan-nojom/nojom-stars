package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.adapter.AgentCompaniesAdapter;
import com.nojom.adapter.PortfolioCompanyAdapter;
import com.nojom.databinding.ActivityMyPartnerBinding;
import com.nojom.databinding.DialogAddCompanyBinding;
import com.nojom.databinding.DialogAddPartnerBinding;
import com.nojom.databinding.DialogDeleteBinding;
import com.nojom.databinding.DialogDiscardBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.GetAgentCompanies;
import com.nojom.model.GetCompanies;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.PermissionRequest;
import com.nojom.util.ReOrderCompanyMoveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class MyPartnerActivity extends BaseActivity implements AgentCompaniesAdapter.OnClickListener, AgentCompaniesAdapter.UpdateSwipeListener, PortfolioCompanyAdapter.CompanyListener, PermissionListener {
    private ActivityMyPartnerBinding binding;
    private MyPartnerActivityVM myPartnerActivityVM;
    private GetCompanies companyList;
    private MutableLiveData<Boolean> isAnyChanges = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_partner);
        myPartnerActivityVM = ViewModelProviders.of(this).get(MyPartnerActivityVM.class);
        myPartnerActivityVM.init(this);
        myPartnerActivityVM.initData();
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
            addPartnerDialog(null, null);
        });

        adapter = new AgentCompaniesAdapter(this, this, this);
        binding.rMenu.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ReOrderCompanyMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rMenu);

        myPartnerActivityVM.getCompanyData().observe(this, getCompanies -> {
            if (getCompanies != null && getCompanies.data != null && getCompanies.data.size() > 0) {
                companyList = getCompanies;
            }
        });

        myPartnerActivityVM.getAgentCompanyData().observe(this, getAgentCompanies -> {
            if (getAgentCompanies.data != null && getAgentCompanies.data.size() > 0) {
                binding.linList.setVisibility(View.VISIBLE);
                binding.tvSave.setText(getString(R.string.add_new_partner));
                binding.linPh.setVisibility(View.GONE);
                setAgentCompanyAdapter(getAgentCompanies);
            } else {
                binding.tvSave.setText(getString(R.string.add_new_partner));
                binding.linList.setVisibility(View.GONE);
                binding.linPh.setVisibility(View.VISIBLE);
            }
        });

        myPartnerActivityVM.getSaveCompanyProgress().observe(this, integer -> {
            switch (integer) {
                case 1://save company start
                    if (addPartnerBinding != null && dialogAddCompany.isShowing()) {
                        addPartnerBinding.tvSend.setVisibility(View.INVISIBLE);
                        addPartnerBinding.progressBar.setVisibility(View.VISIBLE);
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
                    if (dialogAddCompany != null && dialogAddCompany.isShowing()) {
                        dialogAddCompany.dismiss();
                        newWebsiteName = "";
                        newCompanyName = "";
                        newCompanyFile = "";
                    }

                    break;
                case 0://save company over
                    if (addPartnerBinding != null && dialogAddCompany.isShowing()) {
                        addPartnerBinding.tvSend.setVisibility(View.VISIBLE);
                        addPartnerBinding.progressBar.setVisibility(View.GONE);
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

        /*swipeController = new SwipeController(this, new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
//                adapter.players.remove(position);
//                /adapter.notifyItemRemoved(position);
//                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                deleteChangesDialog(adapter.getData(position).id, adapter.getData(position));

            }

            @Override
            public void onLeftClicked(int position) {
                super.onLeftClicked(position);
                addPartnerDialog(adapter.getData(position), adapter.path);

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
                DrawableCompat.setTint(addPartnerBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.black));
            } else {
                DrawableCompat.setTint(addPartnerBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.c_AEAEB2));
            }
        });

    }

//    SwipeController swipeController = null;

    AgentCompaniesAdapter adapter;

    private void setAgentCompanyAdapter(GetAgentCompanies data) {
        adapter.doRefresh(data.path);
        adapter.doRefresh(data.data);
        binding.rMenu.setAdapter(adapter);
    }

    private DialogAddPartnerBinding addPartnerBinding;
    private DialogDiscardBinding dialogDiscardBinding;
    private DialogDeleteBinding dialogDeleteBinding;
    private Dialog dialogAddCompany;
    private Dialog dialogDiscard, dialogDelete;
    PortfolioCompanyAdapter adapter1;

    public void addPartnerDialog(GetAgentCompanies.Data data, String path) {
        dialogAddCompany = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogAddCompany.setTitle(null);
        addPartnerBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_add_partner, null, false);
        addPartnerBinding.title.setText(getString(R.string.add_new_partner));
        addPartnerBinding.tvSend.setText(getString(R.string.save));
        addPartnerBinding.defaultTextInputLayout.setHint(getString(R.string.company_name));
        addPartnerBinding.defaultTime.setHint(getString(R.string.link));
        addPartnerBinding.defaultDate.setHint(getString(R.string.coupon_optional));

        if (language.equals("ar")) {
            setArFont(addPartnerBinding.title, Constants.FONT_AR_BOLD);
            setArFont(addPartnerBinding.etName, Constants.FONT_AR_REGULAR);
            setArFont(addPartnerBinding.etLink, Constants.FONT_AR_REGULAR);
            setArFont(addPartnerBinding.etCoupon, Constants.FONT_AR_REGULAR);
            setArFont(addPartnerBinding.tvSend, Constants.FONT_AR_BOLD);
        }

        dialogAddCompany.setContentView(addPartnerBinding.getRoot());
        dialogAddCompany.setCancelable(true);
        isAnyChanges.setValue(false);
        if (data != null) {
            addPartnerBinding.etName.setText(data.getName(language));
            addPartnerBinding.etName.setTag(data.company_id + "");
            addPartnerBinding.etName.setSelection(addPartnerBinding.etName.getText().toString().length());
            addPartnerBinding.etLink.setText(data.link + "");
            if (data.code != null && !data.code.equals("null")) {
                addPartnerBinding.etCoupon.setText(data.code + "");
            }

            Glide.with(this)
                    .load(companyList.path + data.filename)
                    /*.apply(RequestOptions.circleCropTransform())*/
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            // Set the drawable as left drawable
                            resource.setBounds(0, 0, 60, 60);
                            addPartnerBinding.etName.setCompoundDrawables(resource, null, null, null);
                            addPartnerBinding.etName.setCompoundDrawablePadding(15);
//                            addPortfolioBinding.etName.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle the cleanup if needed
                        }
                    });

        }
        DrawableCompat.setTint(addPartnerBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.c_AEAEB2));

        addPartnerBinding.etName.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                addPartnerBinding.rvCompany.setAdapter(adapter1);
                addPartnerBinding.rvCompany.setVisibility(View.VISIBLE);
            } else {
                addPartnerBinding.rvCompany.setVisibility(View.GONE);
            }
        });

        addPartnerBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    addPartnerBinding.etName.setCompoundDrawables(null, null, null, null);
                    addPartnerBinding.etName.setCompoundDrawablePadding(15);
                    addPartnerBinding.etName.setTag("");
                    addPartnerBinding.rvCompany.setVisibility(View.GONE);
                    DrawableCompat.setTint(addPartnerBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.c_AEAEB2));
                    isAnyChanges.setValue(false);
                } else {
                    if (adapter1 != null) {
                        adapter1.getFilter().filter("" + s);
                        addPartnerBinding.rvCompany.setAdapter(adapter1);
                        addPartnerBinding.rvCompany.setVisibility(View.VISIBLE);
                    }
                }
                if (data != null) {
                    isAnyChanges.setValue(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addPartnerBinding.etLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isValid()) {
                    DrawableCompat.setTint(addPartnerBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.black));
                    isAnyChanges.setValue(true);
                } else {
                    DrawableCompat.setTint(addPartnerBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.c_AEAEB2));
                    isAnyChanges.setValue(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addPartnerBinding.etCoupon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (data != null) {
                    isAnyChanges.setValue(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if (companyList != null && companyList.data != null) {
            adapter1 = new PortfolioCompanyAdapter(this, this);
            adapter1.addCompany(true);
            adapter1.doRefresh(companyList.data);
            adapter1.setPath(companyList.path);
        }

        addPartnerBinding.tvCancel.setOnClickListener(v -> {
            if (data == null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            } else if (data != null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            }
            discardChangesDialog(dialogAddCompany, data);
        });

        addPartnerBinding.relSave.setOnClickListener(v -> {
            if (data != null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            }
            if (TextUtils.isEmpty(addPartnerBinding.etName.getText().toString().trim()) && (addPartnerBinding.etName.getTag() == null || TextUtils.isEmpty(addPartnerBinding.etName.getTag().toString()))) {
                toastMessage(getString(R.string.please_add_your_company));
                return;
            }
            if (TextUtils.isEmpty(addPartnerBinding.etLink.getText().toString().trim())) {
                toastMessage(getString(R.string.add_link));
                return;
            }
            if (!isValidHttpOrHttpsUrl(addPartnerBinding.etLink.getText().toString().trim())) {
                toastMessage(getString(R.string.please_enter_valid_link));
                return;
            }
            if (data != null) {//edit case
                if (addPartnerBinding.etName.getTag() == null) {
//                    String company = addPortfolioBinding.etName.getText().toString();
                    myPartnerActivityVM.updatePartners(0,
                            1, data.public_status, data.id, addPartnerBinding.etCoupon.getText().toString(),
                            addPartnerBinding.etLink.getText().toString(), "", "", "");
                } else {
                    if (addPartnerBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        myPartnerActivityVM.updatePartners(Integer.parseInt(addPartnerBinding.etName.getTag().toString()),
                                1, data.public_status, data.id, addPartnerBinding.etCoupon.getText().toString(),
                                addPartnerBinding.etLink.getText().toString(), newCompanyName, newWebsiteName, newCompanyFile);
                    } else {
                        myPartnerActivityVM.updatePartners(Integer.parseInt(addPartnerBinding.etName.getTag().toString()),
                                1, data.public_status, data.id, addPartnerBinding.etCoupon.getText().toString(),
                                addPartnerBinding.etLink.getText().toString(), "", "", "");
                    }
                }

            } else {//add case
//                if (addPartnerBinding.etName.getTag() != null) {
//                    myPartnerActivityVM.addPartners(Integer.parseInt(addPartnerBinding.etName.getTag().toString()),
//                            addPartnerBinding.etCoupon.getText().toString(), addPartnerBinding.etLink.getText().toString());
//                } else {
//                    toastMessage(getString(R.string.please_add_your_company));
//                }

                if (addPartnerBinding.etName.getTag() == null) {
                    myPartnerActivityVM.addPartners(0,
                            addPartnerBinding.etCoupon.getText().toString(), addPartnerBinding.etLink.getText().toString(), "", "", "");
                } else {
                    if (addPartnerBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        myPartnerActivityVM.addPartners(Integer.parseInt(addPartnerBinding.etName.getTag().toString()),
                                addPartnerBinding.etCoupon.getText().toString(), addPartnerBinding.etLink.getText().toString(), newCompanyName, newWebsiteName, newCompanyFile);
                    } else {
                        myPartnerActivityVM.addPartners(Integer.parseInt(addPartnerBinding.etName.getTag().toString()),
                                addPartnerBinding.etCoupon.getText().toString(), addPartnerBinding.etLink.getText().toString(), "", "", "");
                    }
                }
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

    public void discardChangesDialog(Dialog dialogMain, GetAgentCompanies.Data data) {
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
            if (TextUtils.isEmpty(addPartnerBinding.etName.getText().toString().trim()) && (addPartnerBinding.etName.getTag() == null || TextUtils.isEmpty(addPartnerBinding.etName.getTag().toString()))) {
                toastMessage(getString(R.string.please_add_your_company));
                return;
            }
            if (TextUtils.isEmpty(addPartnerBinding.etLink.getText().toString().trim())) {
                toastMessage(getString(R.string.add_link));
                return;
            }
            if (!isValidHttpOrHttpsUrl(addPartnerBinding.etLink.getText().toString().trim())) {
                toastMessage(getString(R.string.please_enter_valid_link));
                return;
            }
            if (data != null) {//edit case

                if (addPartnerBinding.etName.getTag() == null) {
//                    String company = addPortfolioBinding.etName.getText().toString();
                    myPartnerActivityVM.updatePartners(0,
                            1, data.public_status, data.id, addPartnerBinding.etCoupon.getText().toString(),
                            addPartnerBinding.etLink.getText().toString(), "", "", "");
                } else {
                    if (addPartnerBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        myPartnerActivityVM.updatePartners(Integer.parseInt(addPartnerBinding.etName.getTag().toString()),
                                1, data.public_status, data.id, addPartnerBinding.etCoupon.getText().toString(),
                                addPartnerBinding.etLink.getText().toString(), newCompanyName, newWebsiteName, newCompanyFile);
                    } else {
                        myPartnerActivityVM.updatePartners(Integer.parseInt(addPartnerBinding.etName.getTag().toString()),
                                1, data.public_status, data.id, addPartnerBinding.etCoupon.getText().toString(),
                                addPartnerBinding.etLink.getText().toString(), "", "", "");
                    }
                }

            } else {//add case
                if (addPartnerBinding.etName.getTag() == null) {
                    myPartnerActivityVM.addPartners(0,
                            addPartnerBinding.etCoupon.getText().toString(), addPartnerBinding.etLink.getText().toString(), "", "", "");
                } else {
                    if (addPartnerBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        myPartnerActivityVM.addPartners(Integer.parseInt(addPartnerBinding.etName.getTag().toString()),
                                addPartnerBinding.etCoupon.getText().toString(), addPartnerBinding.etLink.getText().toString(), newCompanyName, newWebsiteName, newCompanyFile);
                    } else {
                        myPartnerActivityVM.addPartners(Integer.parseInt(addPartnerBinding.etName.getTag().toString()),
                                addPartnerBinding.etCoupon.getText().toString(), addPartnerBinding.etLink.getText().toString(), "", "", "");
                    }
                }
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

    public void deleteChangesDialog(int itemId, GetAgentCompanies.Data data) {
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
        dialogDeleteBinding.txtTitle.setText(getString(R.string.delete_partner));
        dialogDeleteBinding.txtDesc.setText(Html.fromHtml(getString(R.string.you_re_going_to_delete_the_sm) + "<b> \"" + data.getName(language) + "\"</b><br>" + getString(R.string._are_you_sure)));
        dialogDeleteBinding.tvSend.setText(getString(R.string.yes_delete));
        dialogDeleteBinding.tvCancel.setText(getString(R.string.no_keep_it));


        dialogDeleteBinding.tvCancel.setOnClickListener(v -> {
            dialogDelete.dismiss();
        });

        dialogDeleteBinding.relSave.setOnClickListener(v -> {
            myPartnerActivityVM.deletePartner(itemId);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDelete.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDelete.show();
        dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.getWindow().setAttributes(lp);
    }


    public void whoCanSeeDialog(GetAgentCompanies.Data companies) {
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
            myPartnerActivityVM.updatePartners(companies.company_id, 1, status, companies.id, companies.code, companies.link, "", "", "");
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
    public void onClickShow(GetAgentCompanies.Data companies, int pos) {
        whoCanSeeDialog(companies);
    }

    @Override
    public void onClickMenu(GetAgentCompanies.Data companies, int pos, View view, int location1) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];
        showPopupMenu(view, companies, point, location1);
    }

    private void showPopupMenu(View view, GetAgentCompanies.Data companies, Point point, int location) throws RuntimeException {

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
            addPartnerDialog(companies, adapter.path);
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

    @Override
    public void onSwipeSuccess(List<GetAgentCompanies.Data> mDatasetFiltered) {
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
            myPartnerActivityVM.reOrderPartners(this, jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClickCompany(GetCompanies.Data data, ImageView imageView) {
        if (data.id == -11) {//add new company case
            addPartnerBinding.etName.setText("");
            addPartnerBinding.etName.setTag("");
            addCompanyDialog();
            return;
        }

        addPartnerBinding.etName.setText(data.getName(language));
        addPartnerBinding.etName.setTag(data.id);
        addPartnerBinding.etName.setSelection(addPartnerBinding.etName.getText().toString().length());

        Drawable drawable = imageView.getDrawable();
        drawable.setBounds(0, 0, 60, 60);
        addPartnerBinding.etName.setCompoundDrawables(drawable, null, null, null);
        addPartnerBinding.etName.setCompoundDrawablePadding(15);

        addPartnerBinding.rvCompany.setVisibility(View.GONE);
        if (isValid()) {
            DrawableCompat.setTint(addPartnerBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.black));
            isAnyChanges.setValue(true);
        } else {
            DrawableCompat.setTint(addPartnerBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.c_AEAEB2));
            isAnyChanges.setValue(false);
        }
    }

    private boolean isValid() {
        if (addPartnerBinding.etName.getText() == null ||
                (TextUtils.isEmpty(addPartnerBinding.etName.getText().toString().trim())
                        && (addPartnerBinding.etName.getTag() == null || TextUtils.isEmpty(addPartnerBinding.etName.getTag().toString())))) {
            return false;
        }
        if (!isValidHttpOrHttpsUrl(addPartnerBinding.etLink.getText().toString().trim())) {
            return false;
        }
        return !TextUtils.isEmpty(Objects.requireNonNull(addPartnerBinding.etLink.getText()).toString().trim());
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

    String newCompanyFile, newCompanyName, newWebsiteName;
    private Dialog dialogNewCompany;
    private DialogAddCompanyBinding addCompanyBinding;

    public void addCompanyDialog() {
        dialogNewCompany = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogNewCompany.setTitle(null);
        addCompanyBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_add_company, null, true);
        addCompanyBinding.title.setText(getString(R.string.add_new_company));
        addCompanyBinding.txtPhTitle.setText(getString(R.string.upload_logo_optional));
        addCompanyBinding.desc2.setText(getString(R.string.use_a_size_that_s_at_least_440_x_440_pixels));
        addCompanyBinding.tvSend.setText(getString(R.string.save));
        addCompanyBinding.defaultTextInputLayout.setHint(getString(R.string.company_name));
        addCompanyBinding.defaultTextInputLayoutWeb.setHint(getString(R.string.website));
        dialogNewCompany.setContentView(addCompanyBinding.getRoot());
        dialogNewCompany.setCancelable(true);
        if (language.equals("ar")) {
            setArFont(addCompanyBinding.title, Constants.FONT_AR_BOLD);
            setArFont(addCompanyBinding.txtPhTitle, Constants.FONT_AR_REGULAR);
            setArFont(addCompanyBinding.desc2, Constants.FONT_AR_REGULAR);
            setArFont(addCompanyBinding.etName, Constants.FONT_AR_REGULAR);
            setArFont(addCompanyBinding.etWebsite, Constants.FONT_AR_REGULAR);
            setArFont(addCompanyBinding.tvSend, Constants.FONT_AR_BOLD);
        }
        DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.c_AEAEB2));

        addCompanyBinding.imgAdd.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                Intent mediaIntent = new Intent(Intent.ACTION_PICK);
                mediaIntent.setType("image/*"); // Set the MIME type to include both images and videos
                startActivityForResult(mediaIntent, 1213);
            } else {
                checkPermission();
            }
        });
        addCompanyBinding.imgCancel.setOnClickListener(v -> {
            addCompanyBinding.imgCancel.setVisibility(View.GONE);
            addCompanyBinding.imgAdd.setVisibility(View.VISIBLE);
            addCompanyBinding.roundedImage.setImageResource(R.drawable.ic_portfolio_cloud);
            addCompanyBinding.roundedImage.setTag("");
        });

        addCompanyBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(addCompanyBinding.etName.getText().toString().trim()) || !isValidUrl(addCompanyBinding.etWebsite.getText().toString().trim())) {
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.c_AEAEB2));
                } else {
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.black));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addCompanyBinding.etWebsite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(addCompanyBinding.etName.getText().toString().trim()) || !isValidUrl(addCompanyBinding.etWebsite.getText().toString().trim())) {
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.c_AEAEB2));
                } else {
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(MyPartnerActivity.this, R.color.black));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addCompanyBinding.tvCancel.setOnClickListener(v -> {
//            discardChangesDialog(dialogNewCompany, null);
            dialogNewCompany.dismiss();
        });

        addCompanyBinding.relSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(addCompanyBinding.etName.getText().toString().trim())) {
                toastMessage(getString(R.string.please_add_your_company));
                return;
            }
            if (!isValidUrl(addCompanyBinding.etWebsite.getText().toString())) {
                toastMessage(getString(R.string.enter_valid_website));
                return;
            }
            newCompanyFile = "";
            if (addCompanyBinding.roundedImage.getTag() != null) {
                newCompanyFile = addCompanyBinding.roundedImage.getTag().toString();
            }

            addPartnerBinding.etName.setText(addCompanyBinding.etName.getText().toString());
            addPartnerBinding.etName.setTag(-11);
            addPartnerBinding.etName.setSelection(addPartnerBinding.etName.getText().toString().length());
            Drawable drawable = addCompanyBinding.roundedImage.getDrawable();
            drawable.setBounds(0, 0, 60, 60);
            addPartnerBinding.etName.setCompoundDrawables(drawable, null, null, null);
            addPartnerBinding.etName.setCompoundDrawablePadding(15);
            newCompanyName = addCompanyBinding.etName.getText().toString();
            newWebsiteName = addCompanyBinding.etWebsite.getText().toString();

            dialogNewCompany.dismiss();
            addPartnerBinding.rvCompany.setVisibility(View.GONE);
//            newPortfolioActivityVM.addCompany(file, addCompanyBinding.etName.getText().toString(), addCompanyBinding.etWebsite.getText().toString());

        });
//
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogNewCompany.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogNewCompany.show();
        dialogNewCompany.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogNewCompany.getWindow().setAttributes(lp);

    }

    public void checkPermission() {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionListener(this);
        permissionRequest.checkStorageCameraRequest(this);
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1213) {//new company added case
            // Handle media selection
            Uri selectedMediaUri = data.getData();

            if (selectedMediaUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedMediaUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap.getWidth() >= 440 && bitmap.getHeight() >= 440) {

                        String realPath = getRealPathFromURI(selectedMediaUri);
                        String mediaType = getMediaType(selectedMediaUri);
                        if (mediaType != null && realPath != null) {
                            Glide.with(this).load(selectedMediaUri).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(addCompanyBinding.roundedImage);
                            addCompanyBinding.roundedImage.setTag(realPath);
                            addCompanyBinding.imgCancel.setVisibility(View.VISIBLE);
                            addCompanyBinding.imgAdd.setVisibility(View.GONE);
                            isAnyChanges.postValue(true);
//                            DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
                        } else {
                            toastMessage(getString(R.string.unsupported_media_type));
                            if (addCompanyBinding != null) {
                                addCompanyBinding.roundedImage.setTag("");
                            }
                        }


                    } else {
                        toastMessage(getString(R.string.use_a_size_that_s_at_least_440_x_440_pixels));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else {
            // Handle failure or cancellation
            Toast.makeText(this, "Selection Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMediaType(Uri uri) {
        String contentType = getContentResolver().getType(uri);
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return "image";
            } else if (contentType.startsWith("video/")) {
                return "video";
            }
        }
        return null;
    }

    @SuppressLint("NewApi")
    public String getRealPathFromURI(Uri uri) {
        String filePath = "";
        String[] projection = {MediaStore.MediaColumns.DATA};
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                filePath = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e("FilePathUtils", "Error retrieving file path: " + e.getMessage());
        }
        return filePath;
    }
}
