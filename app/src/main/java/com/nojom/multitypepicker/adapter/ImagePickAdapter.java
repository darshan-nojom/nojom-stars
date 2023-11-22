package com.nojom.multitypepicker.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.Util;
import com.nojom.multitypepicker.activity.ImageBrowserActivity;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DCIM;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.nojom.multitypepicker.activity.ImageBrowserActivity.IMAGE_BROWSER_INIT_INDEX;
import static com.nojom.multitypepicker.activity.ImageBrowserActivity.IMAGE_BROWSER_SELECTED_LIST;

public class ImagePickAdapter extends BaseAdapter<ImageFile, ImagePickAdapter.ImagePickViewHolder> {
    private boolean isNeedImagePager;
    private boolean isNeedCamera;
    private int mMaxNumber;
    private int mCurrentNumber = 0;
    public String mImagePath;
    public Uri mImageUri;
    private Activity activity;

    public ImagePickAdapter(Activity ctx, boolean needCamera, boolean isNeedImagePager, int max) {
        this(ctx, new ArrayList<>(), needCamera, isNeedImagePager, max);
    }

    private ImagePickAdapter(Activity ctx, ArrayList<ImageFile> list, boolean needCamera, boolean needImagePager, int max) {
        super(ctx, list);
        this.activity = ctx;
        isNeedCamera = needCamera;
        mMaxNumber = max;
        isNeedImagePager = needImagePager;
    }

    @Override
    public ImagePickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_image_pick, parent, false);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params != null) {
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            assert wm != null;
            int width = wm.getDefaultDisplay().getWidth();
            params.height = width / ImagePickActivity.COLUMN_NUMBER;
        }
        return new ImagePickViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImagePickViewHolder holder, int position) {
        if (isNeedCamera && position == 0) {
            holder.mIvCamera.setVisibility(View.VISIBLE);
            holder.mIvThumbnail.setVisibility(View.INVISIBLE);
            holder.mCbx.setVisibility(View.INVISIBLE);
            holder.mShadow.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(v -> {

                if (checkPermission()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
                    File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).getAbsolutePath()
                            + "/IMG_" + timeStamp + ".jpg");
                    mImagePath = file.getAbsolutePath();

                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, mImagePath);
                    mImageUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    if (Util.detectIntent(mContext, intent)) {
                        ((Activity) mContext).startActivityForResult(intent, Constant.REQUEST_CODE_TAKE_IMAGE);
                    } else {
                        Utils.toastMessage((Activity) mContext, mContext.getString(R.string.vw_no_photo_app));
                    }
                } else {
                    Utils.toastMessage((Activity) mContext, "Please give permission");
                }
            });
        } else {
            holder.mIvCamera.setVisibility(View.INVISIBLE);
            holder.mIvThumbnail.setVisibility(View.VISIBLE);
            holder.mCbx.setVisibility(View.VISIBLE);

            ImageFile file;
            if (isNeedCamera) {
                file = mList.get(position - 1);
            } else {
                file = mList.get(position);
            }

            RequestOptions options = new RequestOptions();
            Glide.with(mContext)
                    .load(file.getPath())
                    .apply(options.centerCrop())
                    .transition(withCrossFade())
//                    .transition(new DrawableTransitionOptions().crossFade(500))
                    .into(holder.mIvThumbnail);

            if (file.isSelected()) {
                holder.mCbx.setSelected(true);
                holder.mShadow.setVisibility(View.VISIBLE);
            } else {
                holder.mCbx.setSelected(false);
                holder.mShadow.setVisibility(View.INVISIBLE);
            }

            holder.mCbx.setOnClickListener(v -> {
                if (!v.isSelected() && isUpToMax()) {
                    Utils.toastMessage(activity, activity.getString(R.string.vw_up_to_max));
                    return;
                }

                int index = isNeedCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition();
                if (v.isSelected()) {
                    holder.mShadow.setVisibility(View.INVISIBLE);
                    holder.mCbx.setSelected(false);
                    mCurrentNumber--;
                    mList.get(index).setSelected(false);
                } else {
                    holder.mShadow.setVisibility(View.VISIBLE);
                    holder.mCbx.setSelected(true);
                    mCurrentNumber++;
                    mList.get(index).setSelected(true);
                }

                if (mListener != null) {
                    mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(index));
                }
            });

            if (isNeedImagePager) {
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                    intent.putExtra(Constant.MAX_NUMBER, mMaxNumber);
                    intent.putExtra(IMAGE_BROWSER_INIT_INDEX,
                            isNeedCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition());
                    intent.putParcelableArrayListExtra(IMAGE_BROWSER_SELECTED_LIST, ((ImagePickActivity) mContext).mSelectedList);
                    ((Activity) mContext).startActivityForResult(intent, Constant.REQUEST_CODE_BROWSER_IMAGE);
                });
            } else {
                holder.mIvThumbnail.setOnClickListener(view -> {
                    if (!holder.mCbx.isSelected() && isUpToMax()) {
                        Utils.toastMessage(activity, activity.getString(R.string.vw_up_to_max));
                        return;
                    }

                    int index = isNeedCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition();
                    if (holder.mCbx.isSelected()) {
                        holder.mShadow.setVisibility(View.INVISIBLE);
                        holder.mCbx.setSelected(false);
                        mCurrentNumber--;
                        mList.get(index).setSelected(false);
                    } else {
                        holder.mShadow.setVisibility(View.VISIBLE);
                        holder.mCbx.setSelected(true);
                        mCurrentNumber++;
                        mList.get(index).setSelected(true);
                    }

                    if (mListener != null) {
                        mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(index));
                    }
                });
            }
        }
    }

    private boolean checkPermission() {
        final boolean[] returnValu = {false};
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            returnValu[0] = true;
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            returnValu[0] = false;
                            //  activity.toastMessage("Please give permission");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
        return returnValu[0];
    }


    @Override
    public int getItemCount() {
        return isNeedCamera ? mList.size() + 1 : mList.size();
    }

    class ImagePickViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvCamera;
        private ImageView mIvThumbnail;
        private View mShadow;
        private ImageView mCbx;

        ImagePickViewHolder(View itemView) {
            super(itemView);
            mIvCamera = itemView.findViewById(R.id.iv_camera);
            mIvThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            mShadow = itemView.findViewById(R.id.shadow);
            mCbx = itemView.findViewById(R.id.cbx);
        }
    }

    public boolean isUpToMax() {
        return mCurrentNumber >= mMaxNumber;
    }

    public void setCurrentNumber(int number) {
        mCurrentNumber = number;
    }
}
