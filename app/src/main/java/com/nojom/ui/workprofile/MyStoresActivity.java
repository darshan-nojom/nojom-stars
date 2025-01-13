package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.adapter.MyProductsAdapter;
import com.nojom.adapter.MyStoresAdapter;
import com.nojom.databinding.ActivityMyStoresBinding;
import com.nojom.databinding.DialogAddProductBinding;
import com.nojom.databinding.DialogAddStoresBinding;
import com.nojom.databinding.DialogDeleteBinding;
import com.nojom.databinding.DialogDiscardBinding;
import com.nojom.databinding.DialogProductCurrencyBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.GetProduct;
import com.nojom.model.GetStores;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.PermissionRequest;
import com.nojom.util.ReOrderPeoductMoveCallback;
import com.nojom.util.ReOrderStoreMoveCallback;
import com.nojom.util.SegmentedButtonGroupNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyStoresActivity extends BaseActivity implements MyStoresAdapter.OnClickListener, MyProductsAdapter.OnClickListener, MyStoresAdapter.UpdateSwipeListener, PermissionListener, MyProductsAdapter.UpdateSwipeListener {
    private ActivityMyStoresBinding binding;
    private MyStoreActivityVM myStoreActivityVM;
    private GetStores storeList;
    private GetProduct productList;
    private MyStoresAdapter adapter = null;
    private MyProductsAdapter productAdapter = null;

    private MutableLiveData<Boolean> isAnyChanges = new MutableLiveData<>();
    private boolean isTabStore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_stores);
        myStoreActivityVM = ViewModelProviders.of(this).get(MyStoreActivityVM.class);
        myStoreActivityVM.init(this);
        if (language.equals("ar")) {
            setArFont(binding.tvTitle, Constants.FONT_AR_SEMI_BOLD);
            setArFont(binding.tvSave, Constants.FONT_AR_MEDIUM);
        }
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
//        binding.imgSorting.setOnClickListener(v -> startActivity(new Intent(this, ReOrderProfileActivity.class)));
        binding.relSave.setOnClickListener(v -> {
            //check if no item is present then open add dialog
            if (isTabStore) {
                addPortfolioDialog(null);
            } else {
                addProductDialog(null);
            }
        });
        storeView();
        binding.linStoreTab.setOnClickListener(view -> storeView());
        binding.linProductTab.setOnClickListener(view -> productView());

        adapter = new MyStoresAdapter(this, this, this);
        binding.rMenuStore.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new ReOrderStoreMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rMenuStore);

        productAdapter = new MyProductsAdapter(this, this, this);
        binding.rMenuProduct.setAdapter(productAdapter);
        ItemTouchHelper.Callback callback1 = new ReOrderPeoductMoveCallback(productAdapter);
        ItemTouchHelper touchHelper1 = new ItemTouchHelper(callback1);
        touchHelper1.attachToRecyclerView(binding.rMenuProduct);

        myStoreActivityVM.getStoreDataList().observe(this, getCompanies -> {
            if (getCompanies != null && getCompanies.data != null && getCompanies.data.size() > 0) {
                storeList = getCompanies;

                binding.linListStore.setVisibility(View.VISIBLE);
                binding.linPh.setVisibility(View.GONE);
                setAgentPortfolioAdapter(getCompanies.data);
            } else {
                binding.linListStore.setVisibility(View.GONE);
                binding.linPh.setVisibility(View.VISIBLE);
            }
        });

        myStoreActivityVM.getProductDataList().observe(this, getCompanies -> {
            if (getCompanies != null && getCompanies.data != null && getCompanies.data.size() > 0) {
                productList = getCompanies;

                binding.linListProduct.setVisibility(View.VISIBLE);
                binding.linPh.setVisibility(View.GONE);
                setAgentProductAdapter(getCompanies.data);
            } else {
                binding.linListProduct.setVisibility(View.GONE);
                binding.linPh.setVisibility(View.VISIBLE);
            }
        });

        myStoreActivityVM.getSaveCompanyProgress().observe(this, integer -> {
            switch (integer) {
                case 1://save company start
                    if (addStoresBinding != null && dialogAddCompany.isShowing()) {
                        addStoresBinding.tvSend.setVisibility(View.INVISIBLE);
                        addStoresBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                    if (addProductBinding != null && dialogAddProduct.isShowing()) {
                        addProductBinding.tvSend.setVisibility(View.INVISIBLE);
                        addProductBinding.progressBar.setVisibility(View.VISIBLE);
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
                    }
                    if (dialogAddProduct != null && dialogAddProduct.isShowing()) {
                        dialogAddProduct.dismiss();
                    }

                    break;
                case 0://save company over
                    if (addStoresBinding != null && dialogAddCompany.isShowing()) {
                        addStoresBinding.tvSend.setVisibility(View.VISIBLE);
                        addStoresBinding.progressBar.setVisibility(View.GONE);
                    }
                    if (addProductBinding != null && dialogAddProduct.isShowing()) {
                        addProductBinding.tvSend.setVisibility(View.VISIBLE);
                        addProductBinding.progressBar.setVisibility(View.GONE);
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


        /*isAnyChanges.observe(this, aBoolean -> {
            if (binding.segmentLoginGroup.getPosition() == 0) {//store
                if (isValid(null)) {
                    DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.black));
                } else {
                    DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.c_AEAEB2));
                }
            } else {//product
                if (aBoolean) {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.black));
                } else {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.c_AEAEB2));
                }
            }
        });*/
    }

    private void storeView() {
        isTabStore = true;
        binding.rMenuStore.setVisibility(View.GONE);
        myStoreActivityVM.getStores();
        binding.tvTitle.setText(getString(R.string.my_stores_));
        binding.tvSave.setText(getString(R.string.add_new_store));
        binding.linListStore.setVisibility(View.VISIBLE);
        binding.linListProduct.setVisibility(View.GONE);
        binding.tvStore.setTextColor(getResources().getColor(R.color.C_020814));
        binding.tvProduct.setTextColor(getResources().getColor(R.color.C_3C3C43));
        binding.v1.setVisibility(View.VISIBLE);
        binding.v2.setVisibility(View.GONE);
    }

    private void productView() {
        isTabStore = false;
        binding.rMenuProduct.setVisibility(View.GONE);
        myStoreActivityVM.getProduct();
        binding.tvSave.setText(getString(R.string.add_new_product));
        binding.tvTitle.setText(getString(R.string.my_products));
        binding.linListStore.setVisibility(View.GONE);
        binding.linListProduct.setVisibility(View.VISIBLE);
        binding.tvProduct.setTextColor(getResources().getColor(R.color.C_020814));
        binding.tvStore.setTextColor(getResources().getColor(R.color.C_3C3C43));
        binding.v2.setVisibility(View.VISIBLE);
        binding.v1.setVisibility(View.GONE);
    }

    public SegmentedButtonGroupNew.OnPositionChangedListener onPositionChangedListener = position -> {
        if (position == 0) {
            binding.rMenuStore.setVisibility(View.GONE);
            myStoreActivityVM.getStores();
            binding.tvSave.setText(getString(R.string.add_new_store));
            binding.linListStore.setVisibility(View.VISIBLE);
            binding.linListProduct.setVisibility(View.GONE);
        } else if (position == 1) {
            binding.rMenuProduct.setVisibility(View.GONE);
            myStoreActivityVM.getProduct();
            binding.tvSave.setText(getString(R.string.add_new_product));
            binding.linListStore.setVisibility(View.GONE);
            binding.linListProduct.setVisibility(View.VISIBLE);
        }
    };


    private void setAgentPortfolioAdapter(List<GetStores.Data> data) {
        adapter.doRefresh(storeList.path);
        adapter.doRefresh(data);
        binding.rMenuStore.setVisibility(View.VISIBLE);
    }

    private void setAgentProductAdapter(List<GetProduct.Data> data) {
        productAdapter.doRefresh(productList.path);
        productAdapter.doRefresh(data);
        binding.rMenuProduct.setVisibility(View.VISIBLE);

        if (isTabStore) {
            binding.linListProduct.setVisibility(View.GONE);
        } else {
            binding.linListProduct.setVisibility(View.VISIBLE);
        }
    }

    private void setCompanyAdapter() {

    }

    private DialogAddStoresBinding addStoresBinding;
    private DialogAddProductBinding addProductBinding;
    private DialogDiscardBinding dialogDiscardBinding;
    private DialogDeleteBinding dialogDeleteBinding;
    private Dialog dialogAddCompany;
    private Dialog dialogAddProduct;
    private Dialog dialogDiscard, dialogDelete;

    public void addPortfolioDialog(GetStores.Data data) {
        dialogAddCompany = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogAddCompany.setTitle(null);
        addStoresBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_add_stores, null, false);

        addStoresBinding.title.setText(getString(R.string.add_new_store));
        addStoresBinding.desc.setText(getString(R.string.upload_image));
        addStoresBinding.desc1.setText(getString(R.string.use_a_size_that_s_at_least_720_x_480_pixels));
        addStoresBinding.tvSend.setText(getString(R.string.save));
        addStoresBinding.defaultTextInputLayout.setHint(getString(R.string.title));
        addStoresBinding.defaultTextInputLayoutWeb.setHint(getString(R.string.website));

        if (language.equals("ar")) {
            setArFont(addStoresBinding.title, Constants.FONT_AR_SEMI_BOLD);
            setArFont(addStoresBinding.desc, Constants.FONT_AR_REGULAR);
            setArFont(addStoresBinding.desc1, Constants.FONT_AR_REGULAR);
            setArFont(addStoresBinding.etName, Constants.FONT_AR_REGULAR);
            setArFont(addStoresBinding.etWebsite, Constants.FONT_AR_REGULAR);
            setArFont(addStoresBinding.tvSend, Constants.FONT_AR_BOLD);
        }

        dialogAddCompany.setContentView(addStoresBinding.getRoot());
        dialogAddCompany.setCancelable(true);
        DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.C_E5E5EA));
        addStoresBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
        isAnyChanges.setValue(false);
        if (data != null) {
            addStoresBinding.etName.setText(data.title);
            addStoresBinding.etWebsite.setText(data.link);
            addStoresBinding.roundedImage.setTag("");

            Glide.with(this).load(storeList.path + data.filename).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
            }).into(addStoresBinding.roundedImage);
            addStoresBinding.imgCancel.setVisibility(View.VISIBLE);
        }

        addStoresBinding.roundedImage.setOnClickListener(v -> {
            if (!addStoresBinding.imgCancel.isShown()) {
                if (checkAndRequestPermissions()) {
                    Intent mediaIntent = new Intent(Intent.ACTION_PICK);
                    mediaIntent.setType("image/*"); // Set the MIME type to include both images and videos
                    startActivityForResult(mediaIntent, 1212);
                } else {
                    checkPermission();
                }
            }
        });
        addStoresBinding.imgCancel.setOnClickListener(v -> {
            addStoresBinding.imgCancel.setVisibility(View.GONE);
            addStoresBinding.roundedImage.setImageResource(R.drawable.ic_add_stores);
            addStoresBinding.roundedImage.setTag("");

            if (data != null) {
                if (isValid(data)) {
                    DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
                    addStoresBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
                } else {
                    DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.C_E5E5EA));
                    addStoresBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
                }
            }
            isAnyChanges.setValue(true);
        });

        addStoresBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isValid(data)) {
                    DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.black));
                    addStoresBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
//                    isAnyChanges.setValue(true);
                } else {
                    DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.C_E5E5EA));
                    addStoresBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
//                    isAnyChanges.setValue(false);
                }
                isAnyChanges.setValue(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addStoresBinding.etWebsite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isValid(data)) {
                    DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.black));
                    addStoresBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
//                    isAnyChanges.setValue(true);
                } else {
                    DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.C_E5E5EA));
                    addStoresBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
//                    isAnyChanges.setValue(false);
                }
                isAnyChanges.setValue(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addStoresBinding.tvCancel.setOnClickListener(v -> {
            if (data == null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            } else if (data != null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            }
            discardChangesDialog(dialogAddCompany, data, null);
        });

        addStoresBinding.relSave.setOnClickListener(v -> {
            if (data != null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddCompany.dismiss();
                return;
            }
            if (TextUtils.isEmpty(addStoresBinding.etName.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_title));
                return;
            }
            if (TextUtils.isEmpty(addStoresBinding.etWebsite.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_valid_website));
                return;
            }
            if (!isValidHttpOrHttpsUrl(addStoresBinding.etWebsite.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_valid_website));
                return;
            }
            if (data == null && (addStoresBinding.roundedImage.getTag() == null || TextUtils.isEmpty(addStoresBinding.roundedImage.getTag().toString()))) {
                toastMessage(getString(R.string.please_select_media_file));
                return;
            }
            if (data != null) {//edit case
                myStoreActivityVM.updateStores(data.public_status, data.id, addStoresBinding.roundedImage.getTag().toString(), addStoresBinding.etName.getText().toString(), addStoresBinding.etWebsite.getText().toString());
            } else {//add case
                myStoreActivityVM.addStores(addStoresBinding.roundedImage.getTag().toString(), addStoresBinding.etName.getText().toString(), addStoresBinding.etWebsite.getText().toString());
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

    public void addProductDialog(GetProduct.Data data) {
        dialogAddProduct = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogAddProduct.setTitle(null);
        addProductBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_add_product, null, false);

        addProductBinding.title.setText(getString(R.string.add_new_product));
        addProductBinding.desc.setText(getString(R.string.upload_image));
        addProductBinding.desc1.setText(getString(R.string.use_a_size_that_s_at_least_480_x_480_pixels));
        addProductBinding.tvSend.setText(getString(R.string.save));
        addProductBinding.defaultTextInputLayout.setHint(getString(R.string.title));
        addProductBinding.defaultTextInputLayoutWeb.setHint(getString(R.string.product_url));
        addProductBinding.defaultTextInputLayoutCurr.setHint(getString(R.string.currency));
        addProductBinding.defaultTextInputLayoutPri.setHint(getString(R.string.price));

        if (language.equals("ar")) {
            setArFont(addProductBinding.title, Constants.FONT_AR_BOLD);
            setArFont(addProductBinding.desc, Constants.FONT_AR_REGULAR);
            setArFont(addProductBinding.desc1, Constants.FONT_AR_REGULAR);
            setArFont(addProductBinding.etName, Constants.FONT_AR_REGULAR);
            setArFont(addProductBinding.etWebsite, Constants.FONT_AR_REGULAR);
            setArFont(addProductBinding.etCurrency, Constants.FONT_AR_REGULAR);
            setArFont(addProductBinding.etPrice, Constants.FONT_AR_REGULAR);
            setArFont(addProductBinding.tvSend, Constants.FONT_AR_BOLD);
        }

        dialogAddProduct.setContentView(addProductBinding.getRoot());
        dialogAddProduct.setCancelable(true);
        isAnyChanges.setValue(false);
        DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.C_E5E5EA));
        addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
        if (data != null) {
            addProductBinding.etName.setText(data.title);
            addProductBinding.etWebsite.setText(data.url);
            addProductBinding.roundedImage.setTag("");
            addProductBinding.etPrice.setText(data.price + "");
            if (data.currency == 2) {
                addProductBinding.etCurrency.setText(getString(R.string.sar));
            } else {
                addProductBinding.etCurrency.setText(getString(R.string.dollar));
            }

            Glide.with(this).load(productList.path + data.filename).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
            }).into(addProductBinding.roundedImage);
            addProductBinding.imgCancel.setVisibility(View.VISIBLE);
        } else {
            addProductBinding.etCurrency.setText(getString(R.string.sar));
        }

        addProductBinding.roundedImage.setOnClickListener(v -> {
            if (!addProductBinding.imgCancel.isShown()) {
                checkPermission();
            }
            if (!addProductBinding.imgCancel.isShown()) {
                if (checkAndRequestPermissions()) {
                    Intent mediaIntent = new Intent(Intent.ACTION_PICK);
                    mediaIntent.setType("image/*"); // Set the MIME type to include both images and videos
                    startActivityForResult(mediaIntent, 1213);
                } else {
                    checkPermission();
                }
            }
        });
        addProductBinding.etCurrency.setOnClickListener(view -> {
            currencyDialog(addProductBinding.etCurrency, data);
        });


        addProductBinding.imgCancel.setOnClickListener(v -> {
            addProductBinding.imgCancel.setVisibility(View.GONE);
            addProductBinding.roundedImage.setImageResource(R.drawable.ic_add_product);
            addProductBinding.roundedImage.setTag("");
            if (data != null) {
                if (isValidProduct(data)) {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
                } else {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.C_E5E5EA));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
                }
            }
            isAnyChanges.setValue(true);
        });

        addProductBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isValidProduct(data)) {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.black));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
//                    isAnyChanges.setValue(true);
                } else {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.C_E5E5EA));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
//                    isAnyChanges.setValue(false);
                }
                isAnyChanges.setValue(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addProductBinding.etWebsite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isValidProduct(data)) {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.black));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
//                    isAnyChanges.setValue(true);
                } else {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.C_E5E5EA));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
//                    isAnyChanges.setValue(false);
                }
                isAnyChanges.setValue(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addProductBinding.etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isValidProduct(data)) {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.black));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
//                    isAnyChanges.setValue(true);
                } else {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.C_E5E5EA));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
//                    isAnyChanges.setValue(false);
                }
                isAnyChanges.setValue(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addProductBinding.etCurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isValidProduct(data)) {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.black));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
//                    isAnyChanges.setValue(true);
                } else {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.C_E5E5EA));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
//                    isAnyChanges.setValue(false);
                }
                isAnyChanges.setValue(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addProductBinding.tvCancel.setOnClickListener(v -> {
            if (data == null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddProduct.dismiss();
                return;
            } else if (data != null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddProduct.dismiss();
                return;
            }
            discardChangesDialog(dialogAddProduct, null, data);
        });

        addProductBinding.relSave.setOnClickListener(v -> {
            if (data != null && Boolean.FALSE.equals(isAnyChanges.getValue())) {
                dialogAddProduct.dismiss();
                return;
            }
            if (TextUtils.isEmpty(addProductBinding.etName.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_title));
                return;
            }
            if (TextUtils.isEmpty(addProductBinding.etWebsite.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_valid_website));
                return;
            }
            if (!isValidHttpOrHttpsUrl(addProductBinding.etWebsite.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_valid_website));
                return;
            }
            /*if (TextUtils.isEmpty(addProductBinding.etPrice.getText().toString().trim())) {
                toastMessage(getString(R.string.enter_your_price));
                return;
            }
            if (TextUtils.isEmpty(addProductBinding.etCurrency.getText().toString().trim())) {
                toastMessage(getString(R.string.select_currency));
                return;
            }*/
            if (data == null && (addProductBinding.roundedImage.getTag() == null || TextUtils.isEmpty(addProductBinding.roundedImage.getTag().toString()))) {
                toastMessage(getString(R.string.please_select_media_file));
                return;
            }
            int currency = 2;
            if (Objects.requireNonNull(addProductBinding.etCurrency.getText()).toString().equals(getString(R.string.sar))) {
                currency = 2;
            } else if (Objects.requireNonNull(addProductBinding.etCurrency.getText()).toString().equals(getString(R.string.dollar))) {
                currency = 1;
            }
            /*if (currency == 0) {
                toastMessage(getString(R.string.select_currency));
                return;
            }*/
            if (data != null) {//edit case
                myStoreActivityVM.updateProduct(addProductBinding.roundedImage.getTag().toString(), addProductBinding.etName.getText().toString(), addProductBinding.etWebsite.getText().toString(), addProductBinding.etPrice.getText().toString(), currency, data.id, data.public_status);
            } else {//add case
                myStoreActivityVM.addProduct(addProductBinding.roundedImage.getTag().toString(), addProductBinding.etName.getText().toString(), addProductBinding.etWebsite.getText().toString(), addProductBinding.etPrice.getText().toString(), currency);
            }
        });
//
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogAddProduct.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogAddProduct.show();
        dialogAddProduct.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddProduct.getWindow().setAttributes(lp);
    }

    public void discardChangesDialog(Dialog dialogMain, GetStores.Data data, GetProduct.Data productData) {
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

            if (isTabStore) {
                if (TextUtils.isEmpty(addStoresBinding.etName.getText().toString().trim())) {
                    toastMessage(getString(R.string.enter_title));
                    return;
                }
                if (TextUtils.isEmpty(addStoresBinding.etWebsite.getText().toString().trim())) {
                    toastMessage(getString(R.string.enter_valid_website));
                    return;
                }
                if (!isValidHttpOrHttpsUrl(addStoresBinding.etWebsite.getText().toString().trim())) {
                    toastMessage(getString(R.string.enter_valid_website));
                    return;
                }
                if (data == null && (addStoresBinding.roundedImage.getTag() == null || TextUtils.isEmpty(addStoresBinding.roundedImage.getTag().toString()))) {
                    toastMessage(getString(R.string.please_select_media_file));
                    return;
                }
                if (data != null) {//edit case
                    myStoreActivityVM.updateStores(data.public_status, data.id, addStoresBinding.roundedImage.getTag().toString(), addStoresBinding.etName.getText().toString(), addStoresBinding.etWebsite.getText().toString());
                } else {//add case
                    myStoreActivityVM.addStores(addStoresBinding.roundedImage.getTag().toString(), addStoresBinding.etName.getText().toString(), addStoresBinding.etWebsite.getText().toString());
                }
            } else {
                if (TextUtils.isEmpty(addProductBinding.etName.getText().toString().trim())) {
                    toastMessage(getString(R.string.enter_title));
                    return;
                }
                if (TextUtils.isEmpty(addProductBinding.etWebsite.getText().toString().trim())) {
                    toastMessage(getString(R.string.enter_valid_website));
                    return;
                }
                if (!isValidHttpOrHttpsUrl(addProductBinding.etWebsite.getText().toString().trim())) {
                    toastMessage(getString(R.string.enter_valid_website));
                    return;
                }
                /*if (TextUtils.isEmpty(addProductBinding.etPrice.getText().toString().trim())) {
                    toastMessage(getString(R.string.enter_your_price));
                    return;
                }*/
                if (productData == null && (addProductBinding.roundedImage.getTag() == null || TextUtils.isEmpty(addProductBinding.roundedImage.getTag().toString()))) {
                    toastMessage(getString(R.string.please_select_media_file));
                    return;
                }
                int currency = 2;
                if (Objects.requireNonNull(addProductBinding.etCurrency.getText()).toString().equals(getString(R.string.sar))) {
                    currency = 2;
                } else if (Objects.requireNonNull(addProductBinding.etCurrency.getText()).toString().equals(getString(R.string.dollar))) {
                    currency = 1;
                }
                /*if (currency == 0) {
                    toastMessage(getString(R.string.select_currency));
                    return;
                }*/
                if (productData != null) {//edit case
                    myStoreActivityVM.updateProduct(addProductBinding.roundedImage.getTag().toString(), addProductBinding.etName.getText().toString(), addProductBinding.etWebsite.getText().toString(), addProductBinding.etPrice.getText().toString(), currency, productData.id, productData.public_status);
                } else {//add case
                    myStoreActivityVM.addProduct(addProductBinding.roundedImage.getTag().toString(), addProductBinding.etName.getText().toString(), addProductBinding.etWebsite.getText().toString(), addProductBinding.etPrice.getText().toString(), currency);
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

    public void deleteChangesDialog(int itemId, String data) {
        dialogDelete = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDelete.setTitle(null);
        dialogDeleteBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_delete, null, false);
        dialogDelete.setContentView(dialogDeleteBinding.getRoot());
        if (language.equals("ar")) {
            setArFont(dialogDeleteBinding.txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(dialogDeleteBinding.txtDesc, Constants.FONT_AR_REGULAR);
            setArFont(dialogDeleteBinding.tvSend, Constants.FONT_AR_BOLD);
            setArFont(dialogDeleteBinding.tvCancel, Constants.FONT_AR_BOLD);
        }
//        dialog.setContentView(R.layout.dialog_discard);
        dialogDelete.setCancelable(true);
        String txt;
        if (isTabStore) {
            dialogDeleteBinding.txtTitle.setText(getString(R.string.delete_store) + " " + data);
            txt = getString(R.string.store).toLowerCase();
        } else {
            dialogDeleteBinding.txtTitle.setText(getString(R.string.delete_product) + " " + data);
            txt = getString(R.string.product).toLowerCase();
        }

        dialogDeleteBinding.txtDesc.setText(getString(R.string.you_re_going_to_delete_the_sm) + " \"" + data + "\" " + txt + "\n" + getString(R.string._are_you_sure));
        dialogDeleteBinding.tvSend.setText(getString(R.string.yes_delete));
        dialogDeleteBinding.tvCancel.setText(getString(R.string.no_keep_it));

        dialogDeleteBinding.tvCancel.setOnClickListener(v -> dialogDelete.dismiss());

        dialogDeleteBinding.relSave.setOnClickListener(v -> {
            if (isTabStore) {
                myStoreActivityVM.deleteStore(itemId);
            } else {
                myStoreActivityVM.deleteProduct(itemId);
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDelete.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDelete.show();
        dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.getWindow().setAttributes(lp);
    }


    public void whoCanSeeDialog(GetStores.Data companies) {
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
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        txtTitle.setText(getString(R.string.who_can_see_this_item_1));

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
            myStoreActivityVM.updateStores(status, companies.id, "", companies.title, companies.link);
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

    public void whoCanSeeDialogProduct(GetProduct.Data companies) {
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
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        txtTitle.setText(getString(R.string.who_can_see_this_item_1));

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
            myStoreActivityVM.updateProduct("", companies.title, companies.url, companies.price + "", companies.currency, companies.id, status);
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
    public void onClickShow(GetStores.Data companies, int pos) {
        whoCanSeeDialog(companies);
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
    public void onSwipeSuccess(List<GetStores.Data> mDatasetFiltered) {
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
            myStoreActivityVM.reOrderStore(this, jsonArray, isTabStore ? 0 : 1);
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
                    InputStream inputStream = getContentResolver().openInputStream(selectedMediaUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap.getWidth() >= 480 && bitmap.getHeight() >= 480) {

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
                            }).into(addStoresBinding.roundedImage);
                            addStoresBinding.roundedImage.setTag(realPath);
                            addStoresBinding.imgCancel.setVisibility(View.VISIBLE);

                            if (isValid(null)) {
                                DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.black));
                                addStoresBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
                            } else {
                                DrawableCompat.setTint(addStoresBinding.relSave.getBackground(), ContextCompat.getColor(MyStoresActivity.this, R.color.C_E5E5EA));
                                addStoresBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
                            }
                        } else {
                            Toast.makeText(this, "Unsupported media type", Toast.LENGTH_SHORT).show();
                            addStoresBinding.roundedImage.setTag("");
                        }


                    } else {
                        if (isTabStore) {
                            toastMessage(getString(R.string.image_must_be_at_least_720x480_pixels));
                        } else {
                            toastMessage(getString(R.string.use_a_size_that_s_at_least_480_x_480_pixels));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else if (resultCode == RESULT_OK && requestCode == 1213) {
            // Handle media selection
            Uri selectedMediaUri = data.getData();

            if (selectedMediaUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedMediaUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap.getWidth() >= 480 && bitmap.getHeight() >= 480) {

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
                            }).into(addProductBinding.roundedImage);
                            addProductBinding.roundedImage.setTag(realPath);
                            addProductBinding.imgCancel.setVisibility(View.VISIBLE);
                            if (isValidProduct(null)) {
                                DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
                                addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
                            } else {
                                DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.C_E5E5EA));
                                addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
                            }
                        } else {
                            Toast.makeText(this, "Unsupported media type", Toast.LENGTH_SHORT).show();
                            addProductBinding.roundedImage.setTag("");
                        }


                    } else {
                        toastMessage(getString(R.string.image_must_be_at_least_480x480_pixels));
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

    private boolean isValid(GetStores.Data data) {

        if (TextUtils.isEmpty(Objects.requireNonNull(addStoresBinding.etName.getText()).toString().trim())) {
            return false;
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(addStoresBinding.etWebsite.getText()).toString().trim())) {
            return false;
        }
        if (!isValidHttpOrHttpsUrl(addStoresBinding.etWebsite.getText().toString().trim())) {
            return false;
        }
        return data != null || (addStoresBinding.roundedImage.getTag() != null && !TextUtils.isEmpty(addStoresBinding.roundedImage.getTag().toString()));

    }

    private boolean isValidProduct(GetProduct.Data data) {
        if (TextUtils.isEmpty(Objects.requireNonNull(addProductBinding.etName.getText()).toString().trim())) {
            return false;
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(addProductBinding.etWebsite.getText()).toString().trim())) {
            return false;
        }
        if (!isValidHttpOrHttpsUrl(addProductBinding.etWebsite.getText().toString().trim())) {
            return false;
        }
//        if (TextUtils.isEmpty(Objects.requireNonNull(addProductBinding.etPrice.getText()).toString().trim())) {
//            return false;
//        }
//        if (TextUtils.isEmpty(Objects.requireNonNull(addProductBinding.etCurrency.getText()).toString().trim())) {
//            return false;
//        }
        return data != null || (addProductBinding.roundedImage.getTag() != null && !TextUtils.isEmpty(addProductBinding.roundedImage.getTag().toString()));

    }

    public void checkPermission() {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionListener(this);
        permissionRequest.checkStorageCameraRequest(this);
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {
        if (isTabStore) {
            Intent mediaIntent = new Intent(Intent.ACTION_PICK);
            mediaIntent.setType("image/*"); // Set the MIME type to include both images and videos
            startActivityForResult(mediaIntent, 1212);
        } else {
            Intent mediaIntent = new Intent(Intent.ACTION_PICK);
            mediaIntent.setType("image/*"); // Set the MIME type to include both images and videos
            startActivityForResult(mediaIntent, 1213);
        }
    }

    @Override
    public void onClickMenu(GetStores.Data companies, int pos, View view, int location1) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];
        showPopupMenu(view, companies, point, location1);
    }

    private void showPopupMenu(View view, GetStores.Data companies, Point point, int location) throws RuntimeException {

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
            addPortfolioDialog(companies);
            popupWindow.dismiss();
        });
        txtView.setOnClickListener(v -> {
            whoCanSeeDialog(companies);
            popupWindow.dismiss();
        });
        txtDelete.setOnClickListener(v -> {
            deleteChangesDialog(companies.id, companies.title);
            popupWindow.dismiss();
        });
    }


    private void showPopupMenuProduct(View view, GetProduct.Data companies, Point point, int location) throws RuntimeException {

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
            addProductDialog(companies);
            popupWindow.dismiss();
        });
        txtView.setOnClickListener(v -> {
            whoCanSeeDialogProduct(companies);
            popupWindow.dismiss();
        });
        txtDelete.setOnClickListener(v -> {
            deleteChangesDialog(companies.id, companies.title);
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

    public void currencyDialog(TextInputEditText etCurrency, GetProduct.Data data) {
        AtomicBoolean isSelectSaudi = new AtomicBoolean(true);

        Dialog dialogDiscard = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogDiscard.setTitle(null);
        DialogProductCurrencyBinding dialogDiscardBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_product_currency, null, false);
        dialogDiscard.setContentView(dialogDiscardBinding.getRoot());
        dialogDiscard.setCancelable(true);
        dialogDiscardBinding.title.setText(getString(R.string.select_currency));
        dialogDiscardBinding.dollar.setText(getString(R.string.dollar_d));
        dialogDiscardBinding.saudi.setText(getString(R.string.saudi_riyal_sar));
        dialogDiscardBinding.tvSend.setText(getString(R.string.save));

        if (etCurrency.getText() != null) {
            if (etCurrency.getText().toString().equals(getString(R.string.dollar))) {
                isSelectSaudi.set(false);
                dollarView(dialogDiscardBinding);
            } else {
                isSelectSaudi.set(true);
                saudiView(dialogDiscardBinding);
            }
        }

        dialogDiscardBinding.relDollar.setOnClickListener(view -> {
            isSelectSaudi.set(false);
            isAnyChanges.setValue(true);
            dollarView(dialogDiscardBinding);
        });

        dialogDiscardBinding.relSaudi.setOnClickListener(view -> {
            isSelectSaudi.set(true);
            saudiView(dialogDiscardBinding);
        });

        dialogDiscardBinding.tvCancel.setOnClickListener(v -> {
            dialogDiscard.dismiss();
        });

        dialogDiscardBinding.relSave.setOnClickListener(v -> {
            etCurrency.setText(isSelectSaudi.get() ? getString(R.string.sar) : getString(R.string.dollar));
            if (data != null) {
                if (isValidProduct(data)) {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.white));
                } else {
                    DrawableCompat.setTint(addProductBinding.relSave.getBackground(), ContextCompat.getColor(this, R.color.C_E5E5EA));
                    addProductBinding.tvSend.setTextColor(getResources().getColor(R.color.C_020814));
                }
                isAnyChanges.setValue(true);
            }
            dialogDiscard.dismiss();

        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogDiscard.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogDiscard.show();
        dialogDiscard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDiscard.getWindow().setAttributes(lp);
    }

    private void saudiView(DialogProductCurrencyBinding dialogDiscardBinding) {
        dialogDiscardBinding.relSaudi.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_black_5, null));
        dialogDiscardBinding.relDollar.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_e5e5_5, null));
        dialogDiscardBinding.imgDollar.setImageResource(R.drawable.circle_uncheck);
        dialogDiscardBinding.imgSaudi.setImageResource(R.drawable.radio_button_active);
    }

    private void dollarView(DialogProductCurrencyBinding dialogDiscardBinding) {
        dialogDiscardBinding.relDollar.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_black_5, null));
        dialogDiscardBinding.relSaudi.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_e5e5_5, null));
        dialogDiscardBinding.imgDollar.setImageResource(R.drawable.radio_button_active);
        dialogDiscardBinding.imgSaudi.setImageResource(R.drawable.circle_uncheck);
    }

    @Override
    public void onClickShowProduct(GetProduct.Data companies, int pos) {
        whoCanSeeDialogProduct(companies);
    }

    @Override
    public void onClickMenuProduct(GetProduct.Data companies, int pos, View view, int loc) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];
        showPopupMenuProduct(view, companies, point, loc);
    }

    @Override
    public void onSwipeSuccessProduct(List<GetProduct.Data> mDatasetFiltered) {
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
            myStoreActivityVM.reOrderStore(this, jsonArray, isTabStore ? 0 : 1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
