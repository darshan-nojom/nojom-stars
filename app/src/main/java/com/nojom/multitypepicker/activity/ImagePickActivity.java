package com.nojom.multitypepicker.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.DividerGridItemDecoration;
import com.nojom.multitypepicker.adapter.ImagePickAdapter;
import com.nojom.multitypepicker.filter.FileFilter;
import com.nojom.multitypepicker.filter.entity.Directory;
import com.nojom.multitypepicker.filter.entity.ImageFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Woo
 * Date: 2016/10/12
 * Time: 16:41
 */

public class ImagePickActivity extends BaseActivity {
    public static final String IS_NEED_CAMERA = "IsNeedCamera";
    public static final String IS_NEED_IMAGE_PAGER = "IsNeedImagePager";
    public static final String IS_TAKEN_AUTO_SELECTED = "IsTakenAutoSelected";

    public static final int DEFAULT_MAX_NUMBER = 9;
    public static final int COLUMN_NUMBER = 3;
    private int mMaxNumber;
    private int mCurrentNumber = 0;
    private ImagePickAdapter mAdapter;
    private boolean isNeedCamera;
    private boolean isNeedImagePager;
    private boolean isTakenAutoSelected;
    public ArrayList<ImageFile> mSelectedList = new ArrayList<>();
    private List<Directory<ImageFile>> mAll;

    private TextView tv_count;
    private TextView tv_folder;
    private RelativeLayout tb_pick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_image_pick);

        mMaxNumber = getIntent().getIntExtra(Constant.MAX_NUMBER, DEFAULT_MAX_NUMBER);
        isNeedCamera = getIntent().getBooleanExtra(IS_NEED_CAMERA, false);
        isNeedImagePager = getIntent().getBooleanExtra(IS_NEED_IMAGE_PAGER, true);
        isTakenAutoSelected = getIntent().getBooleanExtra(IS_TAKEN_AUTO_SELECTED, true);
        initView();
        loadData();
    }

    private void initView() {
        tv_count = findViewById(R.id.tv_count);
        tv_count.setText(String.format("%d/%d", mCurrentNumber, mMaxNumber));

        RecyclerView mRecyclerView = findViewById(R.id.rv_image_pick);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_NUMBER);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        mAdapter = new ImagePickAdapter(ImagePickActivity.this, isNeedCamera, isNeedImagePager, mMaxNumber);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnSelectStateListener((state, file) -> {
            if (state) {
                mSelectedList.add(file);
                mCurrentNumber++;
            } else {
                mSelectedList.remove(file);
                mCurrentNumber--;
            }
            tv_count.setText(String.format("%d/%d", mCurrentNumber, mMaxNumber));
        });

        RelativeLayout rl_done = findViewById(R.id.rl_done);
        rl_done.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE, mSelectedList);
            setResult(RESULT_OK, intent);
            finish();
        });

        tb_pick = findViewById(R.id.tb_pick);
        LinearLayout ll_folder = findViewById(R.id.ll_folder);
        if (isNeedFolderList) {
            ll_folder.setVisibility(View.VISIBLE);
            ll_folder.setOnClickListener(v -> mFolderHelper.toggle(tb_pick));
            tv_folder = findViewById(R.id.tv_folder);
            tv_folder.setText(getResources().getString(R.string.vw_all));

            mFolderHelper.setFolderListListener(directory -> {
                mFolderHelper.toggle(tb_pick);
                tv_folder.setText(directory.getName());

                if (TextUtils.isEmpty(directory.getPath())) { //All
                    refreshData(mAll);
                } else {
                    for (Directory<ImageFile> dir : mAll) {
                        if (dir.getPath().equals(directory.getPath())) {
                            List<Directory<ImageFile>> list = new ArrayList<>();
                            list.add(dir);
                            refreshData(list);
                            break;
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_TAKE_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {//Code is set for OS 10 Device [After capture image]
                            File file = new File(String.valueOf(mAdapter.mImageUri));
                            ImageFile imageFile = new ImageFile();
                            imageFile.setPath(mAdapter.mImagePath);
                            imageFile.setName(file.getName());
                            mSelectedList.add(imageFile);
                            Intent intent = new Intent();
                            intent.putParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE, mSelectedList);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            if (mAdapter.mImagePath != null) {
                                File file = new File(mAdapter.mImagePath);
                                Uri contentUri = Uri.fromFile(file);
                                mediaScanIntent.setData(contentUri);
                                sendBroadcast(mediaScanIntent);

                                loadData();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //Delete the record in Media DB, when user select "Cancel" during take picture
                    try {
                        if (mAdapter.mImageUri != null) {
                            getApplicationContext().getContentResolver().delete(mAdapter.mImageUri, null, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Constant.REQUEST_CODE_BROWSER_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_BROWSER_IMAGE);
                        assert list != null;
                        mCurrentNumber = list.size();
                        mAdapter.setCurrentNumber(mCurrentNumber);
                        tv_count.setText(String.format("%d/%d", mCurrentNumber, mMaxNumber));
                        mSelectedList.clear();
                        mSelectedList.addAll(list);

                        for (ImageFile file : mAdapter.getDataSet()) {
                            if (mSelectedList.contains(file)) {
                                file.setSelected(true);
                            } else {
                                file.setSelected(false);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void loadData() {
        FileFilter.getImages(this, directories -> {
            // Refresh folder list
            if (isNeedFolderList) {
                ArrayList<Directory> list = new ArrayList<>();
                Directory all = new Directory();
                all.setName(getResources().getString(R.string.vw_all));
                list.add(all);
                list.addAll(directories);
                mFolderHelper.fillData(list);
            }

            mAll = directories;
            refreshData(directories);
        });
    }

    private void refreshData(List<Directory<ImageFile>> directories) {
        boolean tryToFindTakenImage = isTakenAutoSelected;

        // if auto-select taken image is enabled, make sure requirements are met
        if (tryToFindTakenImage && !TextUtils.isEmpty(mAdapter.mImagePath)) {
            File takenImageFile = new File(mAdapter.mImagePath);
            tryToFindTakenImage = !mAdapter.isUpToMax() && takenImageFile.exists(); // try to select taken image only if max isn't reached and the file exists
        }

        List<ImageFile> list = new ArrayList<>();
        for (Directory<ImageFile> directory : directories) {
            list.addAll(directory.getFiles());

            // auto-select taken images?
            if (tryToFindTakenImage) {
                findAndAddTakenImage(directory.getFiles());   // if taken image was found, we're done
            }
        }

        for (ImageFile file : mSelectedList) {
            int index = list.indexOf(file);
            if (index != -1) {
                list.get(index).setSelected(true);
            }
        }
        mAdapter.refresh(list);
    }

    private boolean findAndAddTakenImage(List<ImageFile> list) {
        for (ImageFile imageFile : list) {
            if (imageFile.getPath().equals(mAdapter.mImagePath)) {
//                for (int i = 0; i < mSelectedList.size(); i++) {
//                    if (mSelectedList.get(i).getPath().equals(mAdapter.mImagePath)) {
//                        return true;  // already added found so do not add
//                    }
//                }
                for (ImageFile file : mSelectedList) {
                    if (file.getPath().equals(mAdapter.mImagePath)) {
                        return true;  // already added found so do not add
                    }
                }
                mSelectedList.add(imageFile);
                mCurrentNumber++;
                mAdapter.setCurrentNumber(mCurrentNumber);
                tv_count.setText(String.format("%d/%d", mCurrentNumber, mMaxNumber));

                return true;   // taken image was found and added
            }
        }
        return false;    // taken image wasn't found
    }

    private void refreshSelectedList(List<ImageFile> list) {
        for (ImageFile file : list) {
            if (file.isSelected() && !mSelectedList.contains(file)) {
                mSelectedList.add(file);
            }
        }
    }
}
