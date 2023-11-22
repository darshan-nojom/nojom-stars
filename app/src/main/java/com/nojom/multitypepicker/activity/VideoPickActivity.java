package com.nojom.multitypepicker.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.DividerGridItemDecoration;
import com.nojom.multitypepicker.adapter.VideoPickAdapter;
import com.nojom.multitypepicker.filter.FileFilter;
import com.nojom.multitypepicker.filter.entity.Directory;
import com.nojom.multitypepicker.filter.entity.VideoFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Vincent Woo
 * Date: 2016/10/21
 * Time: 14:02
 */

public class VideoPickActivity extends BaseActivity {
    public static final String THUMBNAIL_PATH = "FilePick";
    public static final String IS_NEED_CAMERA = "IsNeedCamera";
    public static final String IS_TAKEN_AUTO_SELECTED = "IsTakenAutoSelected";

    public static final int DEFAULT_MAX_NUMBER = 9;
    public static final int COLUMN_NUMBER = 3;
    private int mMaxNumber;
    private int mCurrentNumber = 0;
    private VideoPickAdapter mAdapter;
    private boolean isNeedCamera;
    private boolean isTakenAutoSelected;
    private ArrayList<VideoFile> mSelectedList = new ArrayList<>();
    private List<Directory<VideoFile>> mAll;
    private ProgressBar mProgressBar;

    private TextView tv_count;
    private TextView tv_folder;
    private RelativeLayout tb_pick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_video_pick);

        mMaxNumber = getIntent().getIntExtra(Constant.MAX_NUMBER, DEFAULT_MAX_NUMBER);
        isNeedCamera = getIntent().getBooleanExtra(IS_NEED_CAMERA, false);
        isTakenAutoSelected = getIntent().getBooleanExtra(IS_TAKEN_AUTO_SELECTED, true);
        initView();
        loadData();
    }

    private void initView() {
        tv_count = findViewById(R.id.tv_count);
        tv_count.setText(String.format("%d/%d", mCurrentNumber, mMaxNumber));

        RecyclerView mRecyclerView = findViewById(R.id.rv_video_pick);
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_NUMBER);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        mAdapter = new VideoPickAdapter(this, isNeedCamera, mMaxNumber);
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

        mProgressBar = findViewById(R.id.pb_video_pick);
        File folder = new File(Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + File.separator + THUMBNAIL_PATH);
        if (!folder.exists()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }

        RelativeLayout rl_done = findViewById(R.id.rl_done);
        rl_done.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO, mSelectedList);
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
                    for (Directory<VideoFile> dir : mAll) {
                        if (dir.getPath().equals(directory.getPath())) {
                            List<Directory<VideoFile>> list = new ArrayList<>();
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
        if (requestCode == Constant.REQUEST_CODE_TAKE_VIDEO) {
            if (resultCode == RESULT_OK) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File file = new File(mAdapter.mVideoPath);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);

                loadData();
            }
        }
    }

    private void loadData() {
        FileFilter.getVideos(this, directories -> {
            mProgressBar.setVisibility(View.GONE);
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

    private void refreshData(List<Directory<VideoFile>> directories) {
        boolean tryToFindTaken = isTakenAutoSelected;

        // if auto-select taken file is enabled, make sure requirements are met
        if (tryToFindTaken && !TextUtils.isEmpty(mAdapter.mVideoPath)) {
            File takenFile = new File(mAdapter.mVideoPath);
            tryToFindTaken = !mAdapter.isUpToMax() && takenFile.exists(); // try to select taken file only if max isn't reached and the file exists
        }

        List<VideoFile> list = new ArrayList<>();
        for (Directory<VideoFile> directory : directories) {
            list.addAll(directory.getFiles());

            // auto-select taken file?
            if (tryToFindTaken) {
                tryToFindTaken = findAndAddTaken(directory.getFiles());   // if taken file was found, we're done
            }
        }

        for (VideoFile file : mSelectedList) {
            int index = list.indexOf(file);
            if (index != -1) {
                list.get(index).setSelected(true);
            }
        }
        mAdapter.refresh(list);
    }

    private boolean findAndAddTaken(List<VideoFile> list) {
        for (VideoFile videoFile : list) {
            if (videoFile.getPath().equals(mAdapter.mVideoPath)) {
                mSelectedList.add(videoFile);
                mCurrentNumber++;
                mAdapter.setCurrentNumber(mCurrentNumber);
                tv_count.setText(String.format("%d/%d", mCurrentNumber, mMaxNumber));

                return true;   // taken file was found and added
            }
        }
        return false;    // taken file wasn't found
    }
}
