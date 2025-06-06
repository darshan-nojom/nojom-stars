package com.nojom.multitypepicker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.DividerListItemDecoration;
import com.nojom.multitypepicker.adapter.NormalFilePickAdapter;
import com.nojom.multitypepicker.filter.FileFilter;
import com.nojom.multitypepicker.filter.entity.Directory;
import com.nojom.multitypepicker.filter.entity.NormalFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Woo
 * Date: 2016/10/26
 * Time: 10:14
 */

public class NormalFilePickActivity extends BaseActivity {
    public static final int DEFAULT_MAX_NUMBER = 9;
    public static final String SUFFIX = "Suffix";
    private int mMaxNumber;
    private int mCurrentNumber = 0;
    private NormalFilePickAdapter mAdapter;
    private ArrayList<NormalFile> mSelectedList = new ArrayList<>();
    private List<Directory<NormalFile>> mAll;
    private ProgressBar mProgressBar;
    private String[] mSuffix;

    private TextView tv_count;
    private TextView tv_folder;
    private RelativeLayout tb_pick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_file_pick);

        mMaxNumber = getIntent().getIntExtra(Constant.MAX_NUMBER, DEFAULT_MAX_NUMBER);
        mSuffix = getIntent().getStringArrayExtra(SUFFIX);
        initView();
        loadData();
    }

    private void initView() {
        tv_count = findViewById(R.id.tv_count);
        tv_count.setText(String.format("%d/%d", mCurrentNumber, mMaxNumber));

        RecyclerView mRecyclerView = findViewById(R.id.rv_file_pick);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(this,
                LinearLayoutManager.VERTICAL, R.drawable.vw_divider_rv_file));
        mAdapter = new NormalFilePickAdapter(this, mMaxNumber);
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

        mProgressBar = findViewById(R.id.pb_file_pick);

        RelativeLayout rl_done = findViewById(R.id.rl_done);
        rl_done.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Constant.RESULT_PICK_FILE, mSelectedList);
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
                    for (Directory<NormalFile> dir : mAll) {
                        if (dir.getPath().equals(directory.getPath())) {
                            List<Directory<NormalFile>> list = new ArrayList<>();
                            list.add(dir);
                            refreshData(list);
                            break;
                        }
                    }
                }
            });
        }
    }

    private void loadData() {
        FileFilter.getFiles(this, directories -> {
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
        }, mSuffix);
    }

    private void refreshData(List<Directory<NormalFile>> directories) {
        mProgressBar.setVisibility(View.GONE);
        List<NormalFile> list = new ArrayList<>();
        for (Directory<NormalFile> directory : directories) {
            list.addAll(directory.getFiles());
        }

        for (NormalFile file : mSelectedList) {
            int index = list.indexOf(file);
            if (index != -1) {
                list.get(index).setSelected(true);
            }
        }

        mAdapter.refresh(list);
    }
}
