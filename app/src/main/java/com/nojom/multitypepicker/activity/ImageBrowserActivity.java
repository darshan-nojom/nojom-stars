package com.nojom.multitypepicker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.nojom.R;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.filter.FileFilter;
import com.nojom.multitypepicker.filter.entity.Directory;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Objects;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.nojom.multitypepicker.activity.ImagePickActivity.DEFAULT_MAX_NUMBER;

public class ImageBrowserActivity extends BaseActivity {
    public static final String IMAGE_BROWSER_INIT_INDEX = "ImageBrowserInitIndex";
    public static final String IMAGE_BROWSER_SELECTED_LIST = "ImageBrowserSelectedList";
    private int mMaxNumber;
    private int mCurrentNumber = 0;
    private int initIndex = 0;
    private int mCurrentIndex = 0;
    private TextView tv_count;
    private ViewPager mViewPager;
    //    private Toolbar mTbImagePick;
    private ArrayList<ImageFile> mList = new ArrayList<>();
    private ImageView mSelectView;
    private ArrayList<ImageFile> mSelectedFiles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.vw_activity_image_browser);

        mMaxNumber = getIntent().getIntExtra(Constant.MAX_NUMBER, DEFAULT_MAX_NUMBER);
        initIndex = getIntent().getIntExtra(IMAGE_BROWSER_INIT_INDEX, 0);
        mCurrentIndex = initIndex;
        mSelectedFiles = getIntent().getParcelableArrayListExtra(IMAGE_BROWSER_SELECTED_LIST);
        if (mSelectedFiles != null) {
            mCurrentNumber = mSelectedFiles.size();
        }
        loadData();
        super.onCreate(savedInstanceState);
    }

    private void initView() {
//        mTbImagePick = findViewById(R.id.tb_image_pick);
//        mTbImagePick.setTitle(mCurrentNumber + "/" + mMaxNumber);
//        setSupportActionBar(mTbImagePick);
//        mTbImagePick.setNavigationOnClickListener(v -> finishThis());

        RelativeLayout rlDone = findViewById(R.id.rl_done);
        rlDone.setOnClickListener(v -> finishThis());

        tv_count = findViewById(R.id.tv_count);
        tv_count.setText(String.format("%d/%d", mCurrentNumber, mMaxNumber));

        mSelectView = findViewById(R.id.cbx);
        mSelectView.setOnClickListener(v -> {
            if (!v.isSelected() && isUpToMax()) {
                Utils.toastMessage(this, getString(R.string.vw_up_to_max));
                return;
            }

            if (v.isSelected()) {
                mList.get(mCurrentIndex).setSelected(false);
                mCurrentNumber--;
                v.setSelected(false);
                mSelectedFiles.remove(mList.get(mCurrentIndex));
            } else {
                mList.get(mCurrentIndex).setSelected(true);
                mCurrentNumber++;
                v.setSelected(true);
                mSelectedFiles.add(mList.get(mCurrentIndex));
            }

            tv_count.setText(String.format("%d/%d", mCurrentNumber, mMaxNumber));
        });

        mViewPager = findViewById(R.id.vp_image_pick);
        mViewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mViewPager.setAdapter(new ImageBrowserAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
                mSelectView.setSelected(mList.get(mCurrentIndex).isSelected());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(initIndex, false);
        mSelectView.setSelected(mList.get(mCurrentIndex).isSelected());
    }

    private void loadData() {
        FileFilter.getImages(this, directories -> {
            mList.clear();
            for (Directory<ImageFile> directory : directories) {
                mList.addAll(directory.getFiles());
            }

            for (ImageFile file : mList) {
                if (mSelectedFiles.contains(file)) {
                    file.setSelected(true);
                }
            }

            initView();
            Objects.requireNonNull(mViewPager.getAdapter()).notifyDataSetChanged();
        });
    }

    private class ImageBrowserAdapter extends PagerAdapter {
        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView view = new PhotoView(ImageBrowserActivity.this);
            view.enable();
            view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            Glide.with(ImageBrowserActivity.this)
                    .load(mList.get(position).getPath())
                    .transition(withCrossFade())
                    .into(view);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vw_menu_image_pick, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            finishThis();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isUpToMax() {
        return mCurrentNumber >= mMaxNumber;
    }

    private void finishThis() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Constant.RESULT_BROWSER_IMAGE, mSelectedFiles);
//        intent.putExtra(IMAGE_BROWSER_SELECTED_NUMBER, mCurrentNumber);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishThis();
    }
}
