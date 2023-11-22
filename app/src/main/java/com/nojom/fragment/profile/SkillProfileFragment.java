package com.nojom.fragment.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.SkillsAdapter;
import com.nojom.databinding.FragmentSkillProfileBinding;
import com.nojom.fragment.BaseFragment;

public class SkillProfileFragment extends BaseFragment {
    private FragmentSkillProfileBinding binding;
    private SkillProfileFragmentVM skillProfileFragmentVM;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_skill_profile, container, false);
        skillProfileFragmentVM = ViewModelProviders.of(this).get(SkillProfileFragmentVM.class);
        initData();
        return binding.getRoot();
    }

    private void initData() {
        binding.rvExpertise.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvSkills.setLayoutManager(new LinearLayoutManager(activity));

//        binding.rvExpertise.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));
//        binding.rvSkills.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));

        skillProfileFragmentVM.refreshViews(this);

        binding.rvExpertise.setNestedScrollingEnabled(false);
        binding.rvSkills.setNestedScrollingEnabled(false);

        skillProfileFragmentVM.getListMutableLiveDataExp().observe(this, expertises -> {
            SkillsAdapter mExpertiseAdapter = new SkillsAdapter(activity, expertises, null, false);
            mExpertiseAdapter.setGrayColor(true);
            binding.rvExpertise.setAdapter(mExpertiseAdapter);
        });

        skillProfileFragmentVM.getListMutableLiveDataSkill().observe(this, skills -> {
            SkillsAdapter mSkillAdapter = new SkillsAdapter(activity, skills, null, false);
            mSkillAdapter.setGrayColor(true);
            binding.rvSkills.setAdapter(mSkillAdapter);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}
