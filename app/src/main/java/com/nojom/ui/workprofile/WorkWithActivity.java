package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
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
import com.nojom.adapter.AgentCompaniesAdapter;
import com.nojom.adapter.CompanyAdapter;
import com.nojom.adapter.PortfolioCompanyAdapter;
import com.nojom.adapter.binder.SwipeController;
import com.nojom.adapter.binder.SwipeControllerActions;
import com.nojom.databinding.ActivityWorkWithBinding;
import com.nojom.databinding.DialogAddCompanyBinding;
import com.nojom.databinding.DialogAddWorkwithBinding;
import com.nojom.databinding.DialogDeleteBinding;
import com.nojom.databinding.DialogDiscardBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.GetAgentCompanies;
import com.nojom.model.GetCompanies;
import com.nojom.model.Portfolios;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.MonthYearPickerDialog;
import com.nojom.util.PermissionRequest;
import com.nojom.util.ReOrderCompanyMoveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class WorkWithActivity extends BaseActivity implements AgentCompaniesAdapter.OnClickListener, AgentCompaniesAdapter.UpdateSwipeListener, PortfolioCompanyAdapter.CompanyListener, PermissionListener {
    private ActivityWorkWithBinding binding;
    private WorkWithActivityVM workWithActivityVM;
    private GetCompanies companyList;
    private MutableLiveData<Boolean> isAnyChanges = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_work_with);
        workWithActivityVM = ViewModelProviders.of(this).get(WorkWithActivityVM.class);
        workWithActivityVM.init(this);
        workWithActivityVM.initData();
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
            addCollectionDialog(null);
        });

        workWithActivityVM.getCompanyData().observe(this, getCompanies -> {
            if (getCompanies != null && getCompanies.data != null && getCompanies.data.size() > 0) {
                companyList = getCompanies;
            }
        });

        adapter = new AgentCompaniesAdapter(this, this, this);
        binding.rMenu.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ReOrderCompanyMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rMenu);

        workWithActivityVM.getAgentCompanyData().observe(this, getAgentCompanies -> {
            if (getAgentCompanies.data != null && getAgentCompanies.data.size() > 0) {
                binding.linList.setVisibility(View.VISIBLE);
                binding.linPh.setVisibility(View.GONE);
                setAgentCompanyAdapter(getAgentCompanies);
            } else {
                binding.linList.setVisibility(View.GONE);
                binding.linPh.setVisibility(View.VISIBLE);
            }
        });

        workWithActivityVM.getSaveCompanyProgress().observe(this, integer -> {
            switch (integer) {
                case 1://save company start
                    if (addWorkwithBinding != null && dialogAddCompany.isShowing()) {
                        addWorkwithBinding.tvSend.setVisibility(View.INVISIBLE);
                        addWorkwithBinding.progressBar.setVisibility(View.VISIBLE);
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
                    if (addWorkwithBinding != null && dialogAddCompany.isShowing()) {
                        addWorkwithBinding.tvSend.setVisibility(View.VISIBLE);
                        addWorkwithBinding.progressBar.setVisibility(View.GONE);
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
                addCollectionDialog(adapter.getData(position));

            }
        });*/

//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(binding.rMenu);

/*        binding.rMenu.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });*/


        isAnyChanges.observe(this, aBoolean -> {
            if (addWorkwithBinding != null) {
                if (isValid()) {
                    DrawableCompat.setTint(addWorkwithBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.black));
                } else {
                    DrawableCompat.setTint(addWorkwithBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.c_AEAEB2));
                }
            }
        });
    }

//    SwipeController swipeController = null;

    AgentCompaniesAdapter adapter;

    private void setAgentCompanyAdapter(GetAgentCompanies data) {
        adapter.doRefresh(data.path);
        adapter.doRefresh(data.data);
    }

    private void setCompanyAdapter() {

    }

    private DialogAddWorkwithBinding addWorkwithBinding;
    private DialogDiscardBinding dialogDiscardBinding;
    private DialogDeleteBinding dialogDeleteBinding;
    private Dialog dialogAddCompany;
    private Dialog dialogDiscard, dialogDelete;
    PortfolioCompanyAdapter adapter1;

    public void addCollectionDialog(GetAgentCompanies.Data data) {
        dialogAddCompany = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogAddCompany.setTitle(null);
        addWorkwithBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_add_workwith, null, false);
        addWorkwithBinding.title.setText(getString(R.string.add_company));
        addWorkwithBinding.txtcont.setText(getString(R.string.have_contract));
        addWorkwithBinding.tvSend.setText(getString(R.string.save));
        addWorkwithBinding.cDate.setText(getString(R.string.contract_date));
        addWorkwithBinding.defaultTextInputLayout.setHint(getString(R.string.company_name));
        addWorkwithBinding.defaultTime.setHint(getString(R.string.number_of_times_optional));
        addWorkwithBinding.defaultDate.setHint(getString(R.string.campaign_date_optional));
        addWorkwithBinding.defaultFrom.setHint(getString(R.string.from));
        addWorkwithBinding.defaultTo.setHint(getString(R.string.to));
        dialogAddCompany.setContentView(addWorkwithBinding.getRoot());
        dialogAddCompany.setCancelable(true);
        isAnyChanges.setValue(false);
        if (data != null) {
            addWorkwithBinding.etName.setText(data.getName(language));
            addWorkwithBinding.etName.setTag(data.company_id + "");
            addWorkwithBinding.etName.setSelection(addWorkwithBinding.etName.getText().toString().length());
            addWorkwithBinding.etTime.setText(data.times + "");

            Glide.with(this).load(companyList.path + data.filename)
                    /*.apply(RequestOptions.circleCropTransform())*/.into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    // Set the drawable as left drawable
                    resource.setBounds(0, 0, 60, 60);
                    addWorkwithBinding.etName.setCompoundDrawables(resource, null, null, null);
                    addWorkwithBinding.etName.setCompoundDrawablePadding(15);
//                            addPortfolioBinding.etName.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    // Handle the cleanup if needed
                }
            });

            if (data.campaign_date != null && data.campaign_date.contains("T")) {
                String[] campDate = data.campaign_date.split("T");
                addWorkwithBinding.etDate.setText(campDate[0]);
            }
            if (data.contract_start_date != null) {
                addWorkwithBinding.etFrom.setText(data.contract_start_date);
                addWorkwithBinding.swContract.setChecked(true);
                addWorkwithBinding.relContractDate.setVisibility(View.VISIBLE);
                addWorkwithBinding.linDate.setVisibility(View.VISIBLE);
                addWorkwithBinding.relCampDate.setVisibility(View.GONE);
            } else {
                addWorkwithBinding.swContract.setChecked(false);
                addWorkwithBinding.relContractDate.setVisibility(View.GONE);
                addWorkwithBinding.linDate.setVisibility(View.GONE);
                addWorkwithBinding.relCampDate.setVisibility(View.VISIBLE);
            }
            if (data.contract_end_date != null) {
                addWorkwithBinding.etTo.setText(data.contract_end_date);
            }
            setPublicStatusValue(data.times_public_status != 0 ? data.times_public_status : 1, addWorkwithBinding.txtStatus);
            setPublicStatusValue((data.campaign_date_public_status != null && data.campaign_date_public_status != 0) ? data.campaign_date_public_status : 1, addWorkwithBinding.txtStatusDate);
            setPublicStatusValue(data.contract_public_status != null ? data.contract_public_status : 1, addWorkwithBinding.txtStatusContractDate);

            /*if (isValid()) {
                DrawableCompat.setTint(addWorkwithBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.black));
            } else {
                DrawableCompat.setTint(addWorkwithBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.c_AEAEB2));
            }*/

        } else {
            setPublicStatusValue(1, addWorkwithBinding.txtStatus);
            setPublicStatusValue(2, addWorkwithBinding.txtStatusDate);
            setPublicStatusValue(2, addWorkwithBinding.txtStatusContractDate);
        }

        DrawableCompat.setTint(addWorkwithBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.c_AEAEB2));

        addWorkwithBinding.swContract.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                addWorkwithBinding.etDate.setText("");
                addWorkwithBinding.relContractDate.setVisibility(View.VISIBLE);
                addWorkwithBinding.linDate.setVisibility(View.VISIBLE);
                addWorkwithBinding.relCampDate.setVisibility(View.GONE);
            } else {
                addWorkwithBinding.relContractDate.setVisibility(View.GONE);
                addWorkwithBinding.linDate.setVisibility(View.GONE);
                addWorkwithBinding.relCampDate.setVisibility(View.VISIBLE);
            }
            isAnyChanges.postValue(true);
        });
        Calendar calendar = Calendar.getInstance();

        addWorkwithBinding.etFrom.setOnClickListener(v -> {

            MonthYearPickerDialog pd = MonthYearPickerDialog.newInstance(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));

            pd.setListener((view, selectedYear, selectedMonth, selectedDay) -> {

                String currentDateFormat = selectedMonth + "/" + selectedYear;

                addWorkwithBinding.etFrom.setText(currentDateFormat);

            });
            pd.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        });

        addWorkwithBinding.etTo.setOnClickListener(v -> {

            MonthYearPickerDialog pd = MonthYearPickerDialog.newInstance(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));

            pd.setListener((view, selectedYear, selectedMonth, selectedDay) -> {

                String currentDateFormat = selectedMonth + "/" + selectedYear;

                addWorkwithBinding.etTo.setText(currentDateFormat);

            });
            pd.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        });
        addWorkwithBinding.etDate.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(WorkWithActivity.this, (view, year1, monthOfYear, dayOfMonth) -> {
                // Set selected date to EditText
                addWorkwithBinding.etDate.setText(year1 + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
            }, year, month, day);

            datePickerDialog.show();
        });

        addWorkwithBinding.txtStatus.setOnClickListener(v -> whoCanSeeDialog(data, true, addWorkwithBinding.txtStatus));
        addWorkwithBinding.txtStatusDate.setOnClickListener(v -> whoCanSeeDialog(data, true, addWorkwithBinding.txtStatusDate));
        addWorkwithBinding.txtStatusContractDate.setOnClickListener(v -> whoCanSeeDialog(data, true, addWorkwithBinding.txtStatusContractDate));

        addWorkwithBinding.etName.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                addWorkwithBinding.rvCompany.setAdapter(adapter1);
                addWorkwithBinding.rvCompany.setVisibility(View.VISIBLE);
            } else {
                addWorkwithBinding.rvCompany.setVisibility(View.GONE);
            }
        });
        addWorkwithBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    addWorkwithBinding.etName.setCompoundDrawables(null, null, null, null);
                    addWorkwithBinding.etName.setCompoundDrawablePadding(15);
                    addWorkwithBinding.etName.setTag("");
                    addWorkwithBinding.rvCompany.setVisibility(View.GONE);
                    DrawableCompat.setTint(addWorkwithBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.c_AEAEB2));
                } else {
                    if (adapter1 != null) {
                        adapter1.getFilter().filter("" + s);
                        addWorkwithBinding.rvCompany.setAdapter(adapter1);
                        addWorkwithBinding.rvCompany.setVisibility(View.VISIBLE);
                    }
                }

                isAnyChanges.postValue(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addWorkwithBinding.etFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isAnyChanges.postValue(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addWorkwithBinding.etDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isAnyChanges.postValue(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addWorkwithBinding.etTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isAnyChanges.postValue(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addWorkwithBinding.etTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isAnyChanges.postValue(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (companyList != null && companyList.data != null) {
            adapter1 = new PortfolioCompanyAdapter(this, this);
            adapter1.addCompany(true);
            adapter1.doRefresh(companyList.data);
            adapter1.setPath(companyList.path);
        }

        addWorkwithBinding.tvCancel.setOnClickListener(v -> {
            if (data == null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            } else if (data != null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            }
            discardChangesDialog(dialogAddCompany, data);
        });

        addWorkwithBinding.relSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(addWorkwithBinding.etName.getText().toString().trim()) && (addWorkwithBinding.etName.getTag() == null || TextUtils.isEmpty(addWorkwithBinding.etName.getTag().toString()))) {
                toastMessage(getString(R.string.please_add_your_company));
                return;
            }
            if (addWorkwithBinding.swContract.isChecked()) {
                if (TextUtils.isEmpty(addWorkwithBinding.etFrom.getText().toString())) {
                    toastMessage(getString(R.string.please_select_from_date));
                    return;
                }
                if (TextUtils.isEmpty(addWorkwithBinding.etTo.getText().toString())) {
                    toastMessage(getString(R.string.please_select_to_date));
                    return;
                }
            }
            if (data != null) {//edit case

                if (addWorkwithBinding.etName.getTag() == null) {
//                    String company = addPortfolioBinding.etName.getText().toString();
                    workWithActivityVM.updateAgentCompanies(0, 1, data.public_status, data.id, addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked());
                } else {
                    if (addWorkwithBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        workWithActivityVM.updateAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), 1, data.public_status, data.id, addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked(),
                                newCompanyName, newWebsiteName, newCompanyFile);
                    } else {
                        workWithActivityVM.updateAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), 1, data.public_status, data.id, addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked());
                    }
                }

            } else {//add case
//                if (addWorkwithBinding.etName.getTag() != null) {
//                    workWithActivityVM.addAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked());
//                } else {
//                    toastMessage(getString(R.string.please_add_your_company));
//                }

                if (addWorkwithBinding.etName.getTag() == null) {
                    workWithActivityVM.addAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked(), "", "", "");
                } else {
                    if (addWorkwithBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        workWithActivityVM.addAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked(), newCompanyName, newWebsiteName, newCompanyFile);
                    } else {
                        workWithActivityVM.addAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked(), "", "", "");
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
            if (TextUtils.isEmpty(addWorkwithBinding.etName.getText().toString().trim()) && (addWorkwithBinding.etName.getTag() == null || TextUtils.isEmpty(addWorkwithBinding.etName.getTag().toString()))) {
                toastMessage(getString(R.string.please_add_your_company));
                return;
            }
            if (addWorkwithBinding.swContract.isChecked()) {
                if (TextUtils.isEmpty(addWorkwithBinding.etFrom.getText().toString())) {
                    toastMessage(getString(R.string.please_select_from_date));
                    return;
                }
                if (TextUtils.isEmpty(addWorkwithBinding.etTo.getText().toString())) {
                    toastMessage(getString(R.string.please_select_to_date));
                    return;
                }
            }
            if (data != null) {//edit case
//                workWithActivityVM.updateAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), 1, data.public_status, data.id, addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked());
                if (addWorkwithBinding.etName.getTag() == null) {
//                    String company = addPortfolioBinding.etName.getText().toString();
                    workWithActivityVM.updateAgentCompanies(0, 1, data.public_status, data.id, addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked());
                } else {
                    if (addWorkwithBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        workWithActivityVM.updateAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), 1, data.public_status, data.id, addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked(),
                                newCompanyName, newWebsiteName, newCompanyFile);
                    } else {
                        workWithActivityVM.updateAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), 1, data.public_status, data.id, addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked());
                    }
                }
            } else {//add case
                if (addWorkwithBinding.etName.getTag() == null) {
                    workWithActivityVM.addAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked(), "", "", "");
                } else {
                    if (addWorkwithBinding.etName.getTag().toString().equals("-11")) {//new company added case
                        workWithActivityVM.addAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked(), newCompanyName, newWebsiteName, newCompanyFile);
                    } else {
                        workWithActivityVM.addAgentCompanies(Integer.parseInt(addWorkwithBinding.etName.getTag().toString()), addWorkwithBinding.etTime.getText() != null ? addWorkwithBinding.etTime.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatus.getTag().toString()), addWorkwithBinding.etDate.getText() != null ? addWorkwithBinding.etDate.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusDate.getTag().toString()), addWorkwithBinding.etFrom.getText() != null ? addWorkwithBinding.etFrom.getText().toString() : "", addWorkwithBinding.etTo.getText() != null ? addWorkwithBinding.etTo.getText().toString() : "", Integer.parseInt(addWorkwithBinding.txtStatusContractDate.getTag().toString()), addWorkwithBinding.swContract.isChecked(), "", "", "");
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

        dialogDeleteBinding.txtTitle.setText(getString(R.string.delete_company));
        dialogDeleteBinding.txtDesc.setText(getString(R.string.you_re_going_to_delete_the_sm) + " \"" + data.getName(language) + "\"" + getString(R.string._are_you_sure));
        dialogDeleteBinding.tvSend.setText(getString(R.string.yes_delete));
        dialogDeleteBinding.tvCancel.setText(getString(R.string.no_keep_it));


        dialogDeleteBinding.tvCancel.setOnClickListener(v -> {
            dialogDelete.dismiss();
        });

        dialogDeleteBinding.relSave.setOnClickListener(v -> {
            workWithActivityVM.deleteAgentCompanies(itemId);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDelete.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDelete.show();
        dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.getWindow().setAttributes(lp);
    }


    public void whoCanSeeDialog(GetAgentCompanies.Data companies, boolean isOnlySet, TextView textView) {
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
        if (textView != null && textView.getTag() != null) {
            defSt = Integer.parseInt(textView.getTag().toString());
        } else if (companies != null) {
            defSt = companies.public_status;
        }

        if (companies != null) {
            switch (defSt) {
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
                isAnyChanges.setValue(true);
            } else if (companies != null) {
                workWithActivityVM.updateAgentCompanies(companies.company_id, 1, status,
                        companies.id, companies.times + "",
                        companies.times_public_status != 0 ? companies.times_public_status : 1,
                        companies.campaign_date, companies.campaign_date_public_status != null ? companies.campaign_date_public_status : 1,
                        companies.contract_start_date, companies.contract_end_date,
                        companies.contract_public_status != null ? companies.contract_public_status : 2,
                        !TextUtils.isEmpty(companies.contract_start_date));
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
    public void onClickShow(GetAgentCompanies.Data companies, int pos) {
        whoCanSeeDialog(companies, false, null);
    }

    @Override
    public void onClickMenu(GetAgentCompanies.Data companies, int pos, View view, int lo) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];
        showPopupMenu(view, companies, point);
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
            workWithActivityVM.reOrderMedia(this, jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClickCompany(GetCompanies.Data data, ImageView imageView) {
        if (data.id == -11) {//add new company case
            addWorkwithBinding.etName.setText("");
            addWorkwithBinding.etName.setTag("");
            addCompanyDialog();
            return;
        }


        addWorkwithBinding.etName.setText(data.getName(language));
        addWorkwithBinding.etName.setTag(data.id);
        addWorkwithBinding.etName.setSelection(addWorkwithBinding.etName.getText().toString().length());

        Drawable drawable = imageView.getDrawable();
        drawable.setBounds(0, 0, 60, 60);
        addWorkwithBinding.etName.setCompoundDrawables(drawable, null, null, null);
        addWorkwithBinding.etName.setCompoundDrawablePadding(15);

        addWorkwithBinding.rvCompany.setVisibility(View.GONE);
        if (isValid()) {
            DrawableCompat.setTint(addWorkwithBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.black));
        } else {
            DrawableCompat.setTint(addWorkwithBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.c_AEAEB2));
        }

    }

    private boolean isValid() {
        if (TextUtils.isEmpty(addWorkwithBinding.etName.getText().toString().trim()) && (addWorkwithBinding.etName.getTag() == null || TextUtils.isEmpty(addWorkwithBinding.etName.getTag().toString()))) {
            return false;
        }
        if (addWorkwithBinding.swContract.isChecked()) {
            if (TextUtils.isEmpty(addWorkwithBinding.etFrom.getText().toString())) {
                return false;
            }
            if (TextUtils.isEmpty(addWorkwithBinding.etTo.getText().toString())) {
                return false;
            }
        }
        return true;
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

    private void showPopupMenu(View view, GetAgentCompanies.Data companies, Point point) throws RuntimeException {

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
        popupWindow.showAtLocation(popupView.getRootView(), Gravity.NO_GRAVITY, point.x - 400, point.y + 60);

        logoutParent.setOnClickListener(v -> popupWindow.dismiss());
        txtEdit.setOnClickListener(v -> {
            addCollectionDialog(companies);
            popupWindow.dismiss();
        });
        txtView.setOnClickListener(v -> {
            whoCanSeeDialog(companies, false, null);
            popupWindow.dismiss();
        });
        txtDelete.setOnClickListener(v -> {
            deleteChangesDialog(companies.id, companies);
            popupWindow.dismiss();
        });
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

        DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.C_E5E5EA));

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
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.C_E5E5EA));
                    addCompanyBinding.tvSend.setTextColor(getResources().getColor(R.color.c_AEAEB2));
                } else {
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.black));
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
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.C_E5E5EA));
                    addCompanyBinding.tvSend.setTextColor(getResources().getColor(R.color.c_AEAEB2));
                } else {
                    DrawableCompat.setTint(addCompanyBinding.relSave.getBackground(), ContextCompat.getColor(WorkWithActivity.this, R.color.black));
                    addCompanyBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
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

            addWorkwithBinding.etName.setText(addCompanyBinding.etName.getText().toString());
            addWorkwithBinding.etName.setTag(-11);
            addWorkwithBinding.etName.setSelection(addWorkwithBinding.etName.getText().toString().length());
            Drawable drawable = addCompanyBinding.roundedImage.getDrawable();
            drawable.setBounds(0, 0, 60, 60);
            addWorkwithBinding.etName.setCompoundDrawables(drawable, null, null, null);
            addWorkwithBinding.etName.setCompoundDrawablePadding(15);
            newCompanyName = addCompanyBinding.etName.getText().toString();
            newWebsiteName = addCompanyBinding.etWebsite.getText().toString();

            dialogNewCompany.dismiss();
            addWorkwithBinding.rvCompany.setVisibility(View.GONE);
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
