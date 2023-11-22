package com.nojom.ui.gigs;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.adapter.GigPhotosAdapter;
import com.nojom.api.APIRequest;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.nojom.multitypepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class GigPhotosActivity extends BaseActivity implements GigPhotosAdapter.OnClickPhotoListener, APIRequest.JWTRequestResponseListener {
    private ArrayList<ImageFile> fileList;
    private GigPhotosAdapter adapter;
    private com.nojom.databinding.ActivityGigPhotosBinding binding;
    private int gigId = -1, adapterPos, noOfPhotos = 5;
    private boolean isDuplicate;
    private JSONArray deletedPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gig_photos);
        deletedPhotos = new JSONArray();

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.addItemDecoration(new SpacesItemDecoration(10));

        fileList = getIntent().getParcelableArrayListExtra("data");
        isDuplicate = getIntent().getBooleanExtra("isDuplicate", false);

        if (getIntent().hasExtra("gigid")) {
            gigId = getIntent().getIntExtra("gigid", 0);
        }

        if (fileList != null && fileList.size() > 0) {
            adapter = new GigPhotosAdapter(this, fileList, this);
            binding.recyclerView.setAdapter(adapter);
        }

        binding.imgAddPhotos.setOnClickListener(v -> {
            if (fileList != null && fileList.size() > 0) {
                noOfPhotos = 5 - fileList.size();

                if (fileList.size() < 5) {
                    checkPermission(false);
                } else {
                    toastMessage(getString(R.string.max_five_file_allowed));
                }
            } else {
                checkPermission(false);
            }

        });

        binding.tvDeleteAll.setOnClickListener(v -> {
            if (adapter != null) {
                if (gigId != -1) {//in case of edit

                    if (isDuplicate) {
                        deletedPhotos = adapter.deletePhotosForDuplicate();
                    } else {
                        JSONArray array = adapter.deletedPhotoIds();
                        if (array != null && array.length() > 0) {
                            deleteGigPhotos(array);
                        }
                    }

                } else {
                    adapter.deletePhotos();
                }

                binding.tvDeleteAll.setVisibility(View.INVISIBLE);
            }
        });

        binding.txtSave.setOnClickListener(v -> setResult());
    }

    @Override
    public void onBackPressed() {
        setResult();
        super.onBackPressed();
    }

    private void setResult() {
        if (adapter != null && adapter.getFiles() != null) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("data", adapter.getFiles());
            intent.putExtra("deletedFiles", deletedPhotos.toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void refreshView(int noOfSelection) {
        if (noOfSelection == 0) {
            binding.tvDeleteAll.setVisibility(View.INVISIBLE);
        } else if (noOfSelection == 1) {
            binding.tvDeleteAll.setVisibility(View.VISIBLE);
            binding.tvDeleteAll.setText(getString(R.string.delete));
        } else {
            binding.tvDeleteAll.setVisibility(View.VISIBLE);
            binding.tvDeleteAll.setText(getString(R.string.delete_all));
        }
    }

    @Override
    public void onClickImage(ImageFile imageFile, int adapterPos) {
        if (imageFile != null && imageFile.getId() != 0 && imageFile.getIsServerUrl() == 1) {
            primaryImageDialog(this, imageFile.getId(), adapterPos);

        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int halfSpace;

        public SpacesItemDecoration(int space) {
            this.halfSpace = space / 2;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if (parent.getPaddingLeft() != halfSpace) {
                parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace);
                parent.setClipToPadding(false);
            }

            outRect.top = halfSpace;
            outRect.bottom = halfSpace;
            outRect.left = halfSpace;
            outRect.right = halfSpace;
        }
    }

    public void checkPermission(final boolean isDocument) {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            Intent intent = new Intent(GigPhotosActivity.this, ImagePickActivity.class);
                            intent.putExtra(IS_NEED_CAMERA, true);
                            intent.putExtra(Constant.MAX_NUMBER, Math.abs(noOfPhotos));
                            intent.putExtra("rCode", Constant.REQUEST_CODE_PICK_IMAGE);
                            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);

                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            toastMessage(getString(R.string.please_give_permission));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
            if (data != null) {
                ArrayList<ImageFile> selectedFiles = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                if (selectedFiles != null && selectedFiles.size() > 0) {
                    Log.e("Image Path == > ", selectedFiles.get(0).getPath());
                    if (fileList == null) {
                        fileList = new ArrayList<>();
                    }
                    fileList.addAll(selectedFiles);

                    if (adapter != null) {
                        adapter.doRefresh(fileList);
                    }
                } else {
                    toastMessage(getString(R.string.file_not_selected));
                }
            }
        }
    }

    public void deleteGigPhotos(JSONArray array) {
        if (!isNetworkConnected())
            return;

        binding.progressBar.setVisibility(View.VISIBLE);
        disableEnableTouch(true);

        HashMap<String, RequestBody> map = new HashMap<>();

        RequestBody deletedids = RequestBody.create(array.toString(), MediaType.parse("application/json"));
        RequestBody id = RequestBody.create(gigId + "", MultipartBody.FORM);
        map.put("imageIDs", deletedids);
        map.put("gigID", id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, this, API_DELETE_GIG_PHOTOS, map);
    }

    public void primaryPhotosAPI(long fileId) {
        if (!isNetworkConnected())
            return;

        disableEnableTouch(true);

        HashMap<String, RequestBody> map = new HashMap<>();

        RequestBody fileIDs = RequestBody.create(fileId + "", MediaType.parse("application/json"));
        RequestBody id = RequestBody.create(gigId + "", MultipartBody.FORM);
        map.put("imageID", fileIDs);
        map.put("gigID", id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, this, API_IMAGE_PRIMARY, map);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_IMAGE_PRIMARY)) {
            try {
                Collections.swap(fileList, 0, adapterPos);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                adapterPos = -1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            adapter.setNoOfSelection(0);
            adapter.deletePhotos();
            binding.progressBar.setVisibility(View.GONE);
        }
        disableEnableTouch(false);

    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        if (url.equalsIgnoreCase(API_IMAGE_PRIMARY)) {
            adapterPos = -1;
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
        disableEnableTouch(false);
    }

    void primaryImageDialog(BaseActivity activity, long fileId, int adapterPos) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        String s = activity.getString(R.string.gig_primary_msg);
        String[] words = {getString(R.string.primary)};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvMessage.setText(Utils.getBoldString(activity, s, fonts, null, words));

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            this.adapterPos = adapterPos;
            primaryPhotosAPI(fileId);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

}
