package com.nojom.ui.workprofile;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.adapter.PortfolioFileAdapter;
import com.nojom.databinding.ActivityPortfolioBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.Portfolios;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.multitypepicker.filter.entity.VideoFile;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.clientprofile.FreelancerProfileActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PortfolioActivity extends BaseActivity implements PortfolioFileAdapter.OnClickListener, PermissionListener {
    private PortfolioActivityVM portfolioActivityVM;
    private ActivityPortfolioBinding binding;
    private Portfolios portfolioData;
    private PortfolioFileAdapter portfolioFileAdapter;
    private int selectedFolioId = -1;
    private int selectedPos = -1;
    private boolean isSimpleBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_portfolio);
        portfolioActivityVM = ViewModelProviders.of(this).get(PortfolioActivityVM.class);
        portfolioActivityVM.init(this);
        initData();
    }

    private void initData() {
        if (getIntent() != null) {
            portfolioData = (Portfolios) getIntent().getSerializableExtra("portfolio");
            isSimpleBack = getIntent().getBooleanExtra("flag", false);
        }

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvSave.setVisibility(View.GONE);
        binding.imgDelete.setOnClickListener(v -> portfolioActivityVM.showDeletePortfolioDialog(portfolioData));
        binding.viewPortfolio.setOnClickListener(v -> {
//            if (isSimpleBack) {
//                onBackPressed();
//            } else {
            Intent intent = new Intent(this, FreelancerProfileActivity.class);
            intent.putExtra("isFromPortfolio", true);
            startActivity(intent);
//            }
        });

        binding.toolbar.tvSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(Objects.requireNonNull(binding.editTitle.getText()).toString().trim())) {
                binding.editTitle.setError(getString(R.string.enter_title));
                return;
            }
            binding.editTitle.setError(null);
            if (portfolioData != null) {
                portfolioActivityVM.updatePortfolio(null, 1, portfolioData, binding.editTitle.getText().toString().trim(), selectedFolioId, true);
            } else {
                portfolioActivityVM.addPortfolio(null, 1, null, binding.editTitle.getText().toString().trim(), true);
            }
        });

        int numberOfColumns = 2;
        GridLayoutManager manager = new GridLayoutManager(this, numberOfColumns);
        binding.recyclerView.setLayoutManager(manager);

        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.startShimmer();

        if (portfolioData != null) {
            binding.editTitle.setText(portfolioData.title);

            portfolioActivityVM.loadData(portfolioData);

            binding.txtEditProfile.setText(getString(R.string.edit_portfolio));
            binding.imgDelete.setVisibility(View.VISIBLE);
        } else {
            portfolioActivityVM.loadData(null);
        }

        binding.editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.toolbar.tvSave.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        portfolioActivityVM.getListMutableLiveData().observe(this, this::setAdapter);

        portfolioActivityVM.getPortfolioData().observe(this, data -> portfolioData = data);

        portfolioActivityVM.getResetIds().observe(this, isReset -> {
            if (isReset) {
                selectedFolioId = -1;
                selectedPos = -1;
            }
        });

        portfolioActivityVM.getDeletePortfolioItem().observe(this, itemPos -> {
            if (portfolioFileAdapter != null) {
                portfolioFileAdapter.deleteItem(itemPos);
            }
        });

        portfolioActivityVM.getAddPortfolioFailed().observe(this, aBoolean -> {
            String msg = getString(R.string.add_portfolio_failed);
            if (portfolioFileAdapter != null) {
                if (portfolioFileAdapter.getItemCount() == 1) {
                    msg = getString(R.string.please_select_portfolio_file_first);
                }
            }
            failureError(msg);
        });

        portfolioActivityVM.getIsShowProgress().observe(this, isShowProgress -> {
            disableEnableTouch(isShowProgress);
            if (isShowProgress) {
                binding.toolbar.tvSave.setVisibility(View.INVISIBLE);
                binding.toolbar.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.toolbar.tvSave.setVisibility(View.VISIBLE);
                binding.toolbar.progressBar.setVisibility(View.GONE);
            }
        });

        portfolioActivityVM.getIsShowProgressFile().observe(this, isSHowProgress -> {
            disableEnableTouch(isSHowProgress);
            if (portfolioFileAdapter != null) {
                portfolioFileAdapter.setShowProgress(isSHowProgress);
                portfolioFileAdapter.notifyItemChanged(portfolioFileAdapter.getItemCount());
            }
        });

        portfolioActivityVM.getIsShowProgressDelete().observe(this, isShowDelete -> {
            disableEnableTouch(isShowDelete);
            if (isShowDelete) {
                binding.imgDelete.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.imgDelete.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setAdapter(List<Portfolios.PortfolioFiles> portfolioFiles) {
        if (portfolioFileAdapter == null) {
            portfolioFileAdapter = new PortfolioFileAdapter(this, portfolioFiles, this);
            binding.recyclerView.setAdapter(portfolioFileAdapter);
        } else {
            portfolioFileAdapter.notifyDataSetChanged();
        }
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.shimmerLayout.setVisibility(View.GONE);
        binding.shimmerLayout.stopShimmer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null) {

                ArrayList<ImageFile> imgPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                if (imgPath != null && imgPath.size() > 0) {
                    String profileFile = imgPath.get(0).getPath();
                    try {
                        if (selectedFolioId == -1 && selectedPos == -1) {
                            addDummyImage();
                            portfolioActivityVM.addPortfolio(new File(profileFile), 1, portfolioData, Objects.requireNonNull(binding.editTitle.getText()).toString().trim(), false);
                        } else {
                            portfolioActivityVM.updatePortfolio(new File(profileFile), 1, portfolioData, Objects.requireNonNull(binding.editTitle.getText()).toString().trim(), selectedFolioId, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    toastMessage(getString(R.string.image_not_selected));
                }

            }
        } else if (requestCode == Constant.REQUEST_CODE_PICK_VIDEO) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<VideoFile> videoPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                if (videoPath != null && videoPath.size() > 0) {
                    Log.e("Video path", videoPath.get(0).getPath());
                    try {
                        if (selectedFolioId == -1 && selectedPos == -1) {
                            addDummyImage();
                            portfolioActivityVM.addPortfolio(new File(videoPath.get(0).getPath()), 2, portfolioData, Objects.requireNonNull(binding.editTitle.getText()).toString().trim(), false);
                        } else {
                            portfolioActivityVM.updatePortfolio(new File(videoPath.get(0).getPath()), 2, portfolioData, Objects.requireNonNull(binding.editTitle.getText()).toString().trim(), selectedFolioId, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            portfolioActivityVM.getResetIds().setValue(true);
        }
    }

    private void addDummyImage() {
        List<Portfolios.PortfolioFiles> dummyImages;
        if (portfolioFileAdapter != null) {
            dummyImages = portfolioFileAdapter.getList();
            dummyImages.add(new Portfolios.PortfolioFiles(-1, null, 1, 0, 0));
            portfolioFileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAddImage() {
        if (portfolioFileAdapter != null) {
            if (portfolioFileAdapter.getItemCount() <= 4) {
                if (TextUtils.isEmpty(Objects.requireNonNull(binding.editTitle.getText()).toString().trim())) {
                    binding.editTitle.setError(getString(R.string.enter_title));
                    return;
                }
                binding.editTitle.setError(null);
                portfolioActivityVM.getResetIds().setValue(true);
                checkPermissionImage();
            } else {
                toastMessage(getString(R.string.max_4_image_uploaded));
            }
        }
    }

    @Override
    public void onClickImage(Portfolios.PortfolioFiles data, int pos) {
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.editTitle.getText()).toString().trim())) {
            binding.editTitle.setError(getString(R.string.enter_title));
            return;
        }
        binding.editTitle.setError(null);
        selectEditPortfolioDialog(data.id, pos);
    }

    public void checkPermissionImage() {
        com.nojom.util.PermissionRequest permissionRequest = new com.nojom.util.PermissionRequest();
        permissionRequest.setPermissionListener(this);
        permissionRequest.checkStorageCameraRequest(this);
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {
        Intent intent = new Intent(this, ImagePickActivity.class);
        intent.putExtra(IS_NEED_CAMERA, true);
        intent.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
    }

    public void selectEditPortfolioDialog(int portfolioId, int itemPosition) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_portfolio);
        dialog.setCancelable(true);
        TextView tvCancel = dialog.findViewById(R.id.btn_cancel);
        TextView txtChangePhoto = dialog.findViewById(R.id.tv_change_photo);
        TextView txtDelete = dialog.findViewById(R.id.tv_delete);
        TextView txtMainPhoto = dialog.findViewById(R.id.tv_as_main);

        if (portfolioFileAdapter != null && portfolioFileAdapter.getItemCount() == 2) {//if there is singal image at that time do not allow to delete portfolio file
            txtDelete.setVisibility(View.GONE);
        }

        txtChangePhoto.setOnClickListener(v -> {
            dialog.dismiss();
            selectedFolioId = portfolioId;
            selectedPos = itemPosition;
            checkPermissionImage();
        });

        txtDelete.setOnClickListener(v -> {
            if(portfolioData!=null) {
                dialog.dismiss();
                selectedFolioId = portfolioId;
                selectedPos = itemPosition;
                portfolioActivityVM.deletePortfolioItem(portfolioId, itemPosition, portfolioData);
            }
        });

        txtMainPhoto.setOnClickListener(v -> {
            dialog.dismiss();
            selectedFolioId = portfolioId;
            selectedPos = itemPosition;
            portfolioActivityVM.setAsMainPhoto(portfolioData, Objects.requireNonNull(binding.editTitle.getText()).toString().trim(), selectedFolioId);
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
}
