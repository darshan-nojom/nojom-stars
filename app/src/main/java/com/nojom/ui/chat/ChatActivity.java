package com.nojom.ui.chat;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nojom.R;
import com.nojom.databinding.ActivityChatBinding;
import com.nojom.fragment.chat.ChatListFragment;
import com.nojom.fragment.chat.LiveChatFragment;
import com.nojom.fragment.chat.ManagerFragment;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity implements BaseActivity.OnProfileLoadListener {
    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        binding.setChatAct(this);
        initData();
    }

    private void initData() {
        setupViewPager();
        Utils.trackFirebaseEvent(this, "Open_Chat_Screen");
        binding.segmentedGroupTab.setOnCheckedChangeListener(onCheckedChangeListener);
        setTab(0);
        binding.viewpager.setCurrentItem(0);
        setOnProfileLoadListener(this);
    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton button = group.findViewById(checkedId);
            if (button.getTag().equals("employer")) {
                binding.viewpager.setCurrentItem(0);
            } else if (button.getTag().equals("support")) {
                binding.viewpager.setCurrentItem(1);
            } else if (button.getTag().equals("manager")) {
                binding.viewpager.setCurrentItem(2);
            }
        }
    };

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            binding.viewpager.setCurrentItem(position);
            setTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setTab(int pos) {
        if (pos == 0) {
            binding.tabEmployer.setChecked(true);
        } else if (pos == 1) {
            binding.tabSupport.setChecked(true);
        } else if (pos == 2) {
//            binding.tabManager.setChecked(true);
        }
    }

    public void onClickSetting() {
        showContactUsDialog();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ChatListFragment(), getString(R.string.employer));
        adapter.addFrag(new LiveChatFragment(), getString(R.string.live_chat));
//        adapter.addFrag(new ManagerFragment(), getString(R.string.manager));
        // binding.viewpager.setPageMargin(20);
        binding.viewpager.setAdapter(adapter);
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {

    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        public final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pageChangeListener != null)
            binding.viewpager.removeOnPageChangeListener(pageChangeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfile();
        if (pageChangeListener != null)
            binding.viewpager.addOnPageChangeListener(pageChangeListener);
    }

    @Override
    public void onBackPressed() {
        if (getParent() != null)
            redirectTab(Constants.TAB_HOME);
        else
            super.onBackPressed();
    }
}
