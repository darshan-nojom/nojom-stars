package com.nojom.ui.workprofile;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.adapter.PaymentAdapter;
import com.nojom.databinding.ActivityAddPaymentBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.BankAccounts;
import com.nojom.model.ProfileResponse;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.PermissionRequest;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaymentActivity extends BaseActivity implements PermissionListener, PaymentAdapter.OnClickPaymentListener {
    private ActivityAddPaymentBinding binding;
    private ProfileResponse profileData;
    private File profileFile = null;
    private PaymentAdapter paymentAdapter;

    private List<ProfileResponse.BankName> bankNameList;
    private PaymentActivityVM paymentActivityVM;
    private int editBankId = 0;
    private BankAccounts.Data bankData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_payment);
        paymentActivityVM = ViewModelProviders.of(this).get(PaymentActivityVM.class);
        initData();
    }

    private void initData() {
        bankNameList = new ArrayList<>();

        if (getIntent() != null) {
            bankData = (BankAccounts.Data) getIntent().getSerializableExtra(Constants.ACCOUNT_DATA);
        }

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvSave.setVisibility(View.GONE);
        binding.relSubmit.setOnClickListener(v -> isValidData());
        binding.relDelete.setOnClickListener(v -> paymentActivityVM.deleteBank(this, editBankId));
        binding.tvAttachFile.setOnClickListener(v -> checkPermission());
        binding.etBankName.setOnClickListener(v -> showBankNameDialog());

        profileData = Preferences.getProfileData(this);

        if (profileData.banks != null && profileData.banks.size() > 0) {
            bankNameList.addAll(profileData.banks);
        }

        if (bankData != null) {
            binding.relDelete.setVisibility(View.VISIBLE);
            editBankId = bankData.id;
            binding.etBankName.setText(bankData.getName(language));
            binding.etBankName.setTag("" + bankData.bank_id);
            binding.etBenifName.setText(bankData.beneficiary_name);

            String prefix = bankData.iban.substring(0, 2);//first two
            String iban = bankData.iban.substring(2);//remaining after first two
            binding.etIban.setText(String.format("%s", iban));
            binding.tvIbanPrefix.setText(String.format("%s", "SA"));

            binding.tvAttachedFile.setText(bankData.bank_certificate);
            binding.tvAttachedFile.setVisibility(View.VISIBLE);

            binding.tvSave.setText(getString(R.string.update));

            binding.segmentGroup.setPosition(bankData.is_primary);
        }

        setOnProfileLoadListener(this::onProfileLoad);

        paymentActivityVM.getShowProgress().observe(this, aBoolean -> {
            if (aBoolean) {
                binding.tvSave.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.tvSave.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        paymentActivityVM.getShowProgressDelete().observe(this, aBoolean -> {
            if (aBoolean) {
                binding.tvDelete.setVisibility(View.INVISIBLE);
                binding.progressBarDelete.setVisibility(View.VISIBLE);
            } else {
                binding.tvDelete.setVisibility(View.VISIBLE);
                binding.progressBarDelete.setVisibility(View.GONE);
            }
        });
    }

    private void isValidData() {
        if (TextUtils.isEmpty(binding.etBankName.getText().toString())) {
            toastMessage(getString(R.string.please_select_bank_name));
            return;
        }
        if (TextUtils.isEmpty(binding.etBenifName.getText().toString())) {
            toastMessage(getString(R.string.pleas_enter_beneficiary_name));
            return;
        }
        if (TextUtils.isEmpty(binding.etIban.getText().toString())) {
            toastMessage(getString(R.string.please_enter_valid_iban));
            return;
        }

        paymentActivityVM.addPayment(this, editBankId, binding.etBankName.getTag().toString(), binding.etBenifName.getText().toString(),
                binding.etIban.getText().toString(), profileFile, binding.segmentGroup.getPosition());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<ImageFile> imgPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (imgPath != null && imgPath.size() > 0) {
                        binding.tvSave.setVisibility(View.VISIBLE);
                        profileFile = new File(imgPath.get(0).getPath());

                        binding.tvAttachedFile.setText(profileFile.getName());
                        binding.tvAttachedFile.setVisibility(View.VISIBLE);

                    } else {
                        toastMessage(getString(R.string.file_not_selected));
                    }
                }
            } else if (requestCode == 1212) {
                if (resultCode == RESULT_OK) {
//                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId(); <-- E.164 format phone number on 10.2.+ devices
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finishToRight();
    }

    public void checkPermission() {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionListener(this);
        permissionRequest.checkStorageCameraRequest(this);
    }

    public void onProfileLoad(ProfileResponse data) {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {
        Intent intent = new Intent(PaymentActivity.this, ImagePickActivity.class);
        intent.putExtra(IS_NEED_CAMERA, true);
        intent.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
    }


    Dialog dialogBankName;

    void showBankNameDialog() {
        dialogBankName = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogBankName.setTitle(null);
        dialogBankName.setContentView(R.layout.dialog_item_select_black);
        dialogBankName.setCancelable(true);

        LinearLayout llButton = dialogBankName.findViewById(R.id.ll_bottom);
        TextView tvCancel = dialogBankName.findViewById(R.id.tv_cancel);
        TextView tvApply = dialogBankName.findViewById(R.id.tv_apply);
        final EditText etSearch = dialogBankName.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialogBankName.findViewById(R.id.rv_items);

        etSearch.setVisibility(View.GONE);
        llButton.setVisibility(View.GONE);

        rvTypes.setLayoutManager(new LinearLayoutManager(this));
        try {
            paymentAdapter = new PaymentAdapter(this, bankNameList, this);
            paymentAdapter.setSelectedLanguageList(binding.etBankName.getText().toString());
            rvTypes.setAdapter(paymentAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> {
            dialogBankName.dismiss();
        });

        tvApply.setOnClickListener(v -> {
            dialogBankName.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogBankName.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogBankName.show();
        dialogBankName.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBankName.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(this, etSearch)));
        etSearch.requestFocus();
    }

    @Override
    public void onClickBank(ProfileResponse.BankName bankName, int adapterPos) {
        binding.etBankName.setText(bankName.getBankName(language));
        binding.etBankName.setTag("" + bankName.id);
        if (dialogBankName != null) {
            dialogBankName.dismiss();
        }
    }
}
