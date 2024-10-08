package com.nojom.ui.workprofile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.adapter.CompanyAdapter;
import com.nojom.adapter.NewPortfolioAdapter;
import com.nojom.adapter.PortfolioCompanyAdapter;
import com.nojom.adapter.binder.SwipeController;
import com.nojom.adapter.binder.SwipeControllerActions;
import com.nojom.databinding.ActivityPortfolioNewBinding;
import com.nojom.databinding.DialogAddCompanyBinding;
import com.nojom.databinding.DialogAddPortfolioBinding;
import com.nojom.databinding.DialogDeleteBinding;
import com.nojom.databinding.DialogDiscardBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.GetCompanies;
import com.nojom.model.Portfolios;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.PermissionRequest;
import com.nojom.util.ReOrderPortfolioMoveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class NewPortfolioActivity extends BaseActivity implements NewPortfolioAdapter.OnClickListener, NewPortfolioAdapter.UpdateSwipeListener, PortfolioCompanyAdapter.CompanyListener, PermissionListener {
    private ActivityPortfolioNewBinding binding;
    private NewPortfolioActivityVM newPortfolioActivityVM;
    private GetCompanies companyList;
    private MutableLiveData<Portfolios> isAnyChanges = new MutableLiveData<>();
    private MutableLiveData<Boolean> isMadeAnyChanges = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_portfolio_new);
        newPortfolioActivityVM = ViewModelProviders.of(this).get(NewPortfolioActivityVM.class);
        newPortfolioActivityVM.init(this);
        newPortfolioActivityVM.initData();
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
//        binding.imgSorting.setOnClickListener(v -> startActivity(new Intent(this, ReOrderProfileActivity.class)));
        binding.relSave.setOnClickListener(v -> {
            //check if no item is present then open add dialog
            addPortfolioDialog(null);
        });

        newPortfolioActivityVM.getCompanyData().observe(this, getCompanies -> {
            if (getCompanies != null && getCompanies.data != null && getCompanies.data.size() > 0) {
                companyList = getCompanies;
//                companyListOrigin = getCompanies;
            }
        });

        adapter = new NewPortfolioAdapter(this, this, this);
        binding.rMenu.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ReOrderPortfolioMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rMenu);

        newPortfolioActivityVM.getListMutableLiveData().observe(this, getAgentCompanies -> {
            if (getAgentCompanies != null) {
                binding.linList.setVisibility(View.VISIBLE);
//                binding.tvSave.setText(getString(R.string.add_video_or_image));
                binding.linPh.setVisibility(View.GONE);
                setAgentPortfolioAdapter(getAgentCompanies);
            } else {
//                binding.tvSave.setText(getString(R.string.add_video_or_image));
                binding.linList.setVisibility(View.GONE);
                binding.linPh.setVisibility(View.VISIBLE);
            }
        });

        newPortfolioActivityVM.getSaveCompanyProgress().observe(this, integer -> {
            switch (integer) {
                case 1://save company start
                    if (addPortfolioBinding != null && dialogAddCompany.isShowing()) {
                        addPortfolioBinding.tvSend.setVisibility(View.INVISIBLE);
                        addPortfolioBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                    if (dialogDiscardBinding != null && dialogDiscard.isShowing()) {
                        dialogDiscardBinding.tvSend.setVisibility(View.INVISIBLE);
                        dialogDiscardBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                    if (dialogDeleteBinding != null && dialogDelete.isShowing()) {
                        dialogDeleteBinding.tvSend.setVisibility(View.INVISIBLE);
                        dialogDeleteBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                    if (addCompanyBinding != null && dialogNewCompany.isShowing()) {
                        addCompanyBinding.tvSend.setVisibility(View.INVISIBLE);
                        addCompanyBinding.progressBar.setVisibility(View.VISIBLE);
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
                    if (dialogNewCompany != null && dialogNewCompany.isShowing()) {
                        dialogNewCompany.dismiss();
                    }

                    break;
                case 0://save company over
                    if (addPortfolioBinding != null && dialogAddCompany.isShowing()) {
                        addPortfolioBinding.tvSend.setVisibility(View.VISIBLE);
                        addPortfolioBinding.progressBar.setVisibility(View.GONE);
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

        /*SwipeController swipeController = new SwipeController(this, new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
//                adapter.players.remove(position);
//                /adapter.notifyItemRemoved(position);
//                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                deleteChangesDialog(adapter.getData(position));

            }

            @Override
            public void onLeftClicked(int position) {
                super.onLeftClicked(position);
                addPortfolioDialog(adapter.getData(position));

            }
        });*/

//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(binding.rMenu);

        /*binding.rMenu.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });*/

        isAnyChanges.observe(this, portfolios -> {
            if (portfolios != null && addPortfolioBinding != null) {
                isMadeAnyChanges.setValue(true);
                DrawableCompat.setTint(addPortfolioBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
                addPortfolioBinding.tvSend.setTextColor(Color.WHITE);
            }
        });

    }


    NewPortfolioAdapter adapter;

    private void setAgentPortfolioAdapter(List<Portfolios> data) {
        adapter.doRefresh(data);
    }

    private void setCompanyAdapter() {

    }

    private DialogAddPortfolioBinding addPortfolioBinding;
    private DialogAddCompanyBinding addCompanyBinding;
    private DialogDiscardBinding dialogDiscardBinding;
    private DialogDeleteBinding dialogDeleteBinding;
    private Dialog dialogAddCompany, dialogNewCompany;
    private Dialog dialogDiscard, dialogDelete;
    PortfolioCompanyAdapter adapter1;

    public void addPortfolioDialog(Portfolios data) {

        dialogAddCompany = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogAddCompany.setTitle(null);
        addPortfolioBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_add_portfolio, null, true);
        addPortfolioBinding.title.setText(getString(R.string.add_new_video_or_image));
        addPortfolioBinding.desc.setText(getString(R.string.use_a_size_that_s_at_least_440_x_280_pixels));
        addPortfolioBinding.tvSend.setText(getString(R.string.save));
        addPortfolioBinding.txtPhTitle.setText(getString(R.string.upload_video_or_image));
        addPortfolioBinding.defaultTextInputLayout.setHint(getString(R.string.company_name_optional));
        dialogAddCompany.setContentView(addPortfolioBinding.getRoot());
        dialogAddCompany.setCancelable(true);

        if (language.equals("ar")) {
            setArFont(addPortfolioBinding.title, Constants.FONT_AR_BOLD);
            setArFont(addPortfolioBinding.desc, Constants.FONT_AR_REGULAR);
            setArFont(addPortfolioBinding.txtPhTitle, Constants.FONT_AR_REGULAR);
            setArFont(addPortfolioBinding.etName, Constants.FONT_AR_REGULAR);
            setArFont(addPortfolioBinding.txtStatus, Constants.FONT_AR_BOLD);
            setArFont(addPortfolioBinding.tvSend, Constants.FONT_AR_BOLD);
        }

//        String text = getString(R.string.upload_video_or_image);
//        SpannableString spannableString = new SpannableString(text);
//
//        // Define the color you want to use
//        int blueColor = Color.BLUE;
//
//        // Find the indices of the words you want to color
//        int videoStart = text.indexOf(getString(R.string.video_));
//        int videoEnd = videoStart + getString(R.string.video_).length();
//
//        int imageStart = text.indexOf(getString(R.string.image_));
//        int imageEnd = imageStart + getString(R.string.image_).length();
//
//        // Set the color for "Video"
//        spannableString.setSpan(new ForegroundColorSpan(blueColor), videoStart, videoEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        // Set the color for "Image"
//        spannableString.setSpan(new ForegroundColorSpan(blueColor), imageStart, imageEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        // Set the SpannableString to the TextView
//        addPortfolioBinding.txtPhTitle.setText(spannableString);

        if (data != null) {
            isMadeAnyChanges.postValue(false);
            addPortfolioBinding.etName.setText(data.getName(language));
            addPortfolioBinding.etName.setTag(data.company_id + "");
            addPortfolioBinding.etName.setSelection(addPortfolioBinding.etName.getText().toString().length());
            addPortfolioBinding.roundedImage.setTag("");

            addPortfolioBinding.etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    isAnyChanges.postValue(data);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            Glide.with(this).load(getPortfolioUrl() + data.filename).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(addPortfolioBinding.roundedImage);
            addPortfolioBinding.imgCancel.setVisibility(View.VISIBLE);
            addPortfolioBinding.imgAdd.setVisibility(View.GONE);

            Glide.with(this).load(companyList.path + data.company_filename)
                    .apply(RequestOptions.circleCropTransform()).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    // Set the drawable as left drawable
                    resource.setBounds(0, 0, 60, 60);
                    addPortfolioBinding.etName.setCompoundDrawables(resource, null, null, null);
                    addPortfolioBinding.etName.setCompoundDrawablePadding(15);
//                            addPortfolioBinding.etName.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    // Handle the cleanup if needed
                }
            });
        }
        DrawableCompat.setTint(addPortfolioBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.C_E5E5EA));
        addPortfolioBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
        addPortfolioBinding.tvSend.setTextColor(Color.BLACK);
        int publicStatus = 1;
        if (data != null) {
            publicStatus = data.public_status;
        }
        addPortfolioBinding.txtStatus.setTag(publicStatus);
        setPublicStatusValue(publicStatus, addPortfolioBinding.txtStatus);

        addPortfolioBinding.imgAdd.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("*/*");
                photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
                startActivityForResult(photoPickerIntent, 1212);
            } else {
                checkPermission();
            }
        });
        addPortfolioBinding.txtStatus.setOnClickListener(v -> whoCanSeeDialog(data, true));

        addPortfolioBinding.imgCancel.setOnClickListener(v -> {
            addPortfolioBinding.imgCancel.setVisibility(View.GONE);
            addPortfolioBinding.imgAdd.setVisibility(View.VISIBLE);
            DrawableCompat.setTint(addPortfolioBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.C_E5E5EA));
            addPortfolioBinding.tvSend.setTextColor(Color.BLACK);
            addPortfolioBinding.roundedImage.setImageResource(R.drawable.ic_portfolio_cloud);
            addPortfolioBinding.roundedImage.setTag("");
            isMadeAnyChanges.setValue(true);
        });

        addPortfolioBinding.etName.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                addPortfolioBinding.rvCompany.setAdapter(adapter1);
                addPortfolioBinding.rvCompany.setVisibility(View.VISIBLE);
            } else {
                addPortfolioBinding.rvCompany.setVisibility(View.GONE);
            }
        });
        addPortfolioBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    addPortfolioBinding.relCompanyName.setBackground(getResources().getDrawable(R.drawable.gray_l_border_6));
                    addPortfolioBinding.etName.setCompoundDrawables(null, null, null, null);
                    addPortfolioBinding.etName.setCompoundDrawablePadding(15);
                    addPortfolioBinding.etName.setTag("0");
                    addPortfolioBinding.rvCompany.setVisibility(View.GONE);
                } else {
                    addPortfolioBinding.relCompanyName.setBackground(getResources().getDrawable(R.drawable.blue_l_border_6));
                    if (adapter1 != null) {
                        adapter1.getFilter().filter("" + s);
                        addPortfolioBinding.rvCompany.setAdapter(adapter1);
                        addPortfolioBinding.rvCompany.setVisibility(View.VISIBLE);
                    }
                }
                if (data != null) {
                    isAnyChanges.postValue(data);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (companyList != null && companyList.data != null) {
//            CompanyAdapter adapter = new CompanyAdapter(this, R.layout.dialog_add_workwith, R.id.tv_name, companyList.data);
//            adapter.setPath(companyList.path);
//            addPortfolioBinding.etName.setAdapter(adapter);
            boolean isIdAddedOrNot = false;
//            companyList = companyListOrigin;
            if (data != null && data.id != 0) {
                for (GetCompanies.Data com : companyList.data) {
                    if (data.company_id == com.id) {
                        isIdAddedOrNot = true;
                        break;
                    }
                }

                if (!isIdAddedOrNot) {//if company is added manually at that time we should have to add it manually in list
                    GetCompanies.Data newCom = new GetCompanies.Data();
                    newCom.id = data.company_id;
                    newCom.name = data.company_name;
                    newCom.nameAr = data.company_name_ar;
                    newCom.filename = data.filename;
                    companyList.data.add(newCom);
                }
            }

            adapter1 = new PortfolioCompanyAdapter(this, this);
            adapter1.doRefresh(companyList.data);
            adapter1.setPath(companyList.path);
//            addPortfolioBinding.rvCompany.setAdapter(adapter1);
        }

        /*addPortfolioBinding.etName.setOnItemClickListener((parent, view, position, id) -> {
//            String com = companyList.get(position).getName(language);
//            etCompany.setText(com);

            LinearLayout rl = (LinearLayout) view;
            TextView tv = (TextView) rl.getChildAt(1);
            ImageView iv = (ImageView) rl.getChildAt(0);

            if (tv.getTag().toString().equals("-1")) {//add new company case
                addPortfolioBinding.etName.setText("");
                addPortfolioBinding.etName.setTag("");
                addCompanyDialog();
                return;
            }


            addPortfolioBinding.etName.setText(tv.getText().toString());
            addPortfolioBinding.etName.setTag(tv.getTag().toString());
            addPortfolioBinding.etName.setSelection(addPortfolioBinding.etName.getText().toString().length());

            Drawable drawable = iv.getDrawable();
            drawable.setBounds(0, 0, 60, 60);
            addPortfolioBinding.etName.setCompoundDrawables(drawable, null, null, null);
            addPortfolioBinding.etName.setCompoundDrawablePadding(15);


        });*/

        addPortfolioBinding.tvCancel.setOnClickListener(v -> {
            if (data == null && (addPortfolioBinding.roundedImage.getTag() == null || TextUtils.isEmpty(addPortfolioBinding.roundedImage.getTag().toString()))) {
                dialogAddCompany.dismiss();
                return;
            } else if (Boolean.FALSE.equals(isMadeAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            }
            discardChangesDialog(dialogAddCompany, data);
        });

        addPortfolioBinding.relSave.setOnClickListener(v -> {
            /*if (TextUtils.isEmpty(addPortfolioBinding.etName.getText().toString().trim()) && (addPortfolioBinding.etName.getTag() == null || TextUtils.isEmpty(addPortfolioBinding.etName.getTag().toString()))) {
                toastMessage(getString(R.string.please_add_your_company));
                return;
            }*/
            if (data == null && (addPortfolioBinding.roundedImage.getTag() == null || TextUtils.isEmpty(addPortfolioBinding.roundedImage.getTag().toString()))) {
                toastMessage(getString(R.string.please_select_media_file));
                return;
            }
            if (data != null) {//edit case
                if (addPortfolioBinding.etName.getTag() == null) {
//                    String company = addPortfolioBinding.etName.getText().toString();
                    newPortfolioActivityVM.updatePortfolio(0, Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), data.id, addPortfolioBinding.roundedImage.getTag().toString());
                } else {
                    if (addPortfolioBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        newPortfolioActivityVM.updatePortfolio(Integer.parseInt(addPortfolioBinding.etName.getTag().toString()), Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), data.id, addPortfolioBinding.roundedImage.getTag().toString());
                    } else {
                        newPortfolioActivityVM.updatePortfolio((addPortfolioBinding.etName.getTag() != null || !TextUtils.isEmpty(addPortfolioBinding.etName.getTag().toString())) ? Integer.parseInt(addPortfolioBinding.etName.getTag().toString()) : 0, Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), data.id, addPortfolioBinding.roundedImage.getTag().toString());
                    }
                }
            } else {//add case
                if (addPortfolioBinding.etName.getTag() == null) {
                    newPortfolioActivityVM.addPortfolio(addPortfolioBinding.roundedImage.getTag().toString(), 0, Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), "", "", "");
                } else {
                    if (addPortfolioBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        newPortfolioActivityVM.addPortfolio(addPortfolioBinding.roundedImage.getTag().toString(), 0, Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), newCompanyName, newWebsiteName, newCompanyFile);
                    } else {
                        newPortfolioActivityVM.addPortfolio(addPortfolioBinding.roundedImage.getTag().toString(), TextUtils.isEmpty(addPortfolioBinding.etName.getTag().toString()) ? 0 : Integer.parseInt(addPortfolioBinding.etName.getTag().toString()), Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), "", "", "");
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

        dialogAddCompany.setOnDismissListener(dialogInterface -> {
            if (data != null && data.id != 0) {
                Iterator<GetCompanies.Data> iterator = companyList.data.iterator();

                while (iterator.hasNext()) {
                    GetCompanies.Data company = iterator.next();
                    if (company.id == data.company_id) {
                        iterator.remove();
                        break; // Remove this if you want to remove all instances with the same ID
                    }
                }
            }
        });
    }

    public void discardChangesDialog(Dialog dialogMain, Portfolios data) {
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
            /*if (TextUtils.isEmpty(addPortfolioBinding.etName.getText().toString().trim()) && (addPortfolioBinding.etName.getTag() == null || TextUtils.isEmpty(addPortfolioBinding.etName.getTag().toString()))) {
                toastMessage(getString(R.string.please_add_your_company));
                return;
            }*/
            if (data == null && (addPortfolioBinding.roundedImage.getTag() == null || TextUtils.isEmpty(addPortfolioBinding.roundedImage.getTag().toString()))) {
                toastMessage(getString(R.string.please_select_media_file));
                return;
            }
            if (data != null) {//edit case
                if (addPortfolioBinding.etName.getTag() == null) {
//                    String company = addPortfolioBinding.etName.getText().toString();
                    newPortfolioActivityVM.updatePortfolio(0, Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), data.id, addPortfolioBinding.roundedImage.getTag().toString());
                } else {
                    newPortfolioActivityVM.updatePortfolio(Integer.parseInt(addPortfolioBinding.etName.getTag().toString()), Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), data.id, addPortfolioBinding.roundedImage.getTag().toString());
                }
            } else {//add case
                if (addPortfolioBinding.etName.getTag() != null) {
                    if (addPortfolioBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        newPortfolioActivityVM.addPortfolio(addPortfolioBinding.roundedImage.getTag().toString(), 0, Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), newCompanyName, newWebsiteName, newCompanyFile);
                    } else {
                        newPortfolioActivityVM.addPortfolio(addPortfolioBinding.roundedImage.getTag().toString(), TextUtils.isEmpty(addPortfolioBinding.etName.getTag().toString()) ? 0 : Integer.parseInt(addPortfolioBinding.etName.getTag().toString()), Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), "", "", "");
                    }
                } else {
                    newPortfolioActivityVM.addPortfolio(addPortfolioBinding.roundedImage.getTag().toString(), 0, Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString()), "", "", "");

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

    public void deleteChangesDialog(Portfolios data) {
        dialogDelete = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDelete.setTitle(null);
        dialogDeleteBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_delete, null, false);
        dialogDelete.setContentView(dialogDeleteBinding.getRoot());
//        dialog.setContentView(R.layout.dialog_discard);
        dialogDelete.setCancelable(true);
        String nam = getString(R.string.item);
        if (data.getName(language) != null) {
            nam = data.getName(language);
        }
        if (language.equals("ar")) {
            setArFont(dialogDeleteBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(dialogDeleteBinding.txtDesc, Constants.FONT_AR_REGULAR);
            setArFont(dialogDeleteBinding.tvSend, Constants.FONT_AR_BOLD);
            setArFont(dialogDeleteBinding.tvCancel, Constants.FONT_AR_BOLD);
        }

        dialogDeleteBinding.txtTitle.setText(getString(R.string.delete_portfolio_item) + " " + nam);
        dialogDeleteBinding.txtDesc.setText(String.format(getString(R.string.you_re_going_to_delete_the_s), nam+"." + " ") + getString(R.string._are_you_sure));
        dialogDeleteBinding.tvSend.setText(getString(R.string.yes_delete));
        dialogDeleteBinding.tvCancel.setText(getString(R.string.no_keep_it));

        dialogDeleteBinding.tvCancel.setOnClickListener(v -> dialogDelete.dismiss());

        dialogDeleteBinding.relSave.setOnClickListener(v -> newPortfolioActivityVM.deletePortfolio(data.id));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDelete.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDelete.show();
        dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.getWindow().setAttributes(lp);
    }


    public void whoCanSeeDialog(Portfolios companies, boolean isAdd) {
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

        int defSt = 1;
        if (addPortfolioBinding != null && addPortfolioBinding.txtStatus.getTag() != null) {
            defSt = Integer.parseInt(addPortfolioBinding.txtStatus.getTag().toString());
        } else if (companies != null) {
            defSt = companies.public_status;
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
        } else {
            switch (defSt) {
                case 2:
                    chkBrand.setChecked(true);
                    break;
                case 3:
                    chkMe.setChecked(true);
                    break;
                default:
                    chkPublic.setChecked(true);
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
//            String val = getString(R.string.public_);
            if (chkPublic.isChecked()) {
//                val = getString(R.string.public_);
                status = 1;
            } else if (chkBrand.isChecked()) {
                status = 2;
//                val = getString(R.string.brand_only);
            } else if (chkMe.isChecked()) {
                status = 3;
//                val = getString(R.string.only_me);
            }

            if (status == 0) {
                toastMessage(getString(R.string.please_select_any));
                return;
            }
            isAnyChanges.postValue(companies);
            if (isAdd) {
                addPortfolioBinding.txtStatus.setTag(status);
                setPublicStatusValue(status, addPortfolioBinding.txtStatus);
            } else {
                newPortfolioActivityVM.updatePortfolio(companies.company_id, status, companies.id, "");
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
    public void onClickShow(Portfolios companies, int pos) {
        whoCanSeeDialog(companies, false);
    }

    @Override
    public void onClickMenu(Portfolios companies, int pos, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];
        showPopupMenu(view, companies, point);
    }

    public static Drawable drawableFromUrl(String url) throws IOException, IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), x);
    }

    @Override
    public void onSwipeSuccess(List<Portfolios> mDatasetFiltered) {
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
            newPortfolioActivityVM.reOrderMedia(this, jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1212) {
            // Handle media selection
            Uri selectedMediaUri = data.getData();

            if (selectedMediaUri != null) {
                try {
//                    InputStream inputStream = getContentResolver().openInputStream(selectedMediaUri);
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    //String realPath = getRealPathFromURI(selectedMediaUri);

                    Glide.with(this).asBitmap().load(selectedMediaUri).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
//                                    imageView.setImageBitmap(resource);
                            if (bitmap.getWidth() >= 440 && bitmap.getHeight() >= 280) {

                                String realPath = getRealPathFromURI(selectedMediaUri);
                                String mediaType = getMediaType(selectedMediaUri);
                                if (mediaType != null && realPath != null) {
                                    Glide.with(NewPortfolioActivity.this).load(selectedMediaUri).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            return false;
                                        }
                                    }).into(addPortfolioBinding.roundedImage);
                                    addPortfolioBinding.roundedImage.setTag(realPath);
                                    addPortfolioBinding.imgCancel.setVisibility(View.VISIBLE);
                                    addPortfolioBinding.imgAdd.setVisibility(View.GONE);
                                    DrawableCompat.setTint(addPortfolioBinding.relSave.getBackground(), ContextCompat.getColor(NewPortfolioActivity.this, R.color.black));
                                    addPortfolioBinding.tvSend.setTextColor(Color.WHITE);
                                } else {
                                    toastMessage(getString(R.string.unsupported_media_type));
                                    addPortfolioBinding.roundedImage.setTag("");
                                }

                            } else {
                                toastMessage(getString(R.string.image_must_be_at_least_440x280_pixels));
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (resultCode == RESULT_OK && requestCode == 1213) {//new company added case
            // Handle media selection
            Uri selectedMediaUri = data.getData();

            if (selectedMediaUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedMediaUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap.getWidth() >= 400 && bitmap.getHeight() >= 440) {

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
//                            DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
                        } else {
                            toastMessage(getString(R.string.unsupported_media_type));
                            if (addPortfolioBinding != null) {
                                addPortfolioBinding.roundedImage.setTag("");
                            }
                            if (addCompanyBinding != null) {
                                addCompanyBinding.roundedImage.setTag("");
                            }
                        }


                    } else {
                        toastMessage(getString(R.string.image_must_be_at_least_440x440_pixels));
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

    String newCompanyFile, newCompanyName, newWebsiteName;

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
        DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(NewPortfolioActivity.this, R.color.C_E5E5EA));
        addCompanyBinding.tvSend.setTextColor(Color.BLACK);
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
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(NewPortfolioActivity.this, R.color.C_E5E5EA));
                    addCompanyBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
                } else {
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(NewPortfolioActivity.this, R.color.black));
                    addCompanyBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
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
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(NewPortfolioActivity.this, R.color.C_E5E5EA));
                    addCompanyBinding.tvSend.setTextColor(Color.BLACK);
                } else {
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(NewPortfolioActivity.this, R.color.black));
                    addCompanyBinding.tvSend.setTextColor(Color.WHITE);
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

            addPortfolioBinding.etName.setText(addCompanyBinding.etName.getText().toString());
            addPortfolioBinding.etName.setTag(-11);
            addPortfolioBinding.etName.setSelection(addPortfolioBinding.etName.getText().toString().length());
            Drawable drawable = addCompanyBinding.roundedImage.getDrawable();
            drawable.setBounds(0, 0, 60, 60);
            addPortfolioBinding.etName.setCompoundDrawables(drawable, null, null, null);
            addPortfolioBinding.etName.setCompoundDrawablePadding(15);
            newCompanyName = addCompanyBinding.etName.getText().toString();
            newWebsiteName = addCompanyBinding.etWebsite.getText().toString();

            dialogNewCompany.dismiss();
            addPortfolioBinding.rvCompany.setVisibility(View.GONE);
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

    private void setPublicStatusValue(int publicStatus, TextView txtStatus) {
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

    private void showPopupMenu(View view, Portfolios portfolios, Point point) throws RuntimeException {

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
        setPublicStatusValue(portfolios.public_status, txtStatus);

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
            addPortfolioDialog(portfolios);
            popupWindow.dismiss();
        });
        txtView.setOnClickListener(v -> {
            whoCanSeeDialog(portfolios, false);
            popupWindow.dismiss();
        });
        txtDelete.setOnClickListener(v -> {
            deleteChangesDialog(portfolios);
            popupWindow.dismiss();
        });

    }

    @Override
    public void onClickCompany(GetCompanies.Data data, ImageView imageView) {

        if (data.id == -11) {//add new company case
            addPortfolioBinding.etName.setText("");
            addPortfolioBinding.etName.setTag("");
            addCompanyDialog();
            return;
        }


        addPortfolioBinding.etName.setText(data.getName(language));
        addPortfolioBinding.etName.setTag(data.id);
        addPortfolioBinding.etName.setSelection(addPortfolioBinding.etName.getText().toString().length());

        Drawable drawable = imageView.getDrawable();
        drawable.setBounds(0, 0, 60, 60);
        addPortfolioBinding.etName.setCompoundDrawables(drawable, null, null, null);
        addPortfolioBinding.etName.setCompoundDrawablePadding(15);

        addPortfolioBinding.rvCompany.setVisibility(View.GONE);
    }

    public void checkPermission() {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionListener(this);
        permissionRequest.checkStorageCameraRequest(this);
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {

    }
}
