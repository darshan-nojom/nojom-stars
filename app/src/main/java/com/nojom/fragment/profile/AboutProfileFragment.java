package com.nojom.fragment.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.ExperiencesAdapter;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.FragmentAboutProfileBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ProfileResponse;
import com.nojom.model.Skill;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Preferences;

import java.util.Objects;

public class AboutProfileFragment extends BaseFragment implements RecyclerviewAdapter.OnViewBindListner {
    private FragmentAboutProfileBinding binding;
    private AboutProfileFragmentVM aboutProfileFragmentVM;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about_profile, container, false);
        aboutProfileFragmentVM = ViewModelProviders.of(this).get(AboutProfileFragmentVM.class);
        initData();
        return binding.getRoot();
    }

    private void initData() {

        ProfileResponse profileData = Preferences.getProfileData(activity);
        if (profileData != null) {
            if (profileData.headlines != null)
                binding.tvHeadline.setText(profileData.headlines);
            if (profileData.summaries != null)
                binding.tvAboutMe.setText(profileData.summaries);
        }

        binding.rvLanguages.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvLanguages.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));

        aboutProfileFragmentVM.getLanguageData(profileData);
        aboutProfileFragmentVM.getEmploymentData(activity,profileData);

        aboutProfileFragmentVM.getListMutableLiveData().observe(activity, skills -> {
            RecyclerviewAdapter adapter = new RecyclerviewAdapter(skills, R.layout.item_language_agents, AboutProfileFragment.this);
            binding.rvLanguages.setAdapter(adapter);
            binding.rvLanguages.setFocusable(false);
        });

        aboutProfileFragmentVM.getDataEmployment().observe(activity, skills -> {
            ExperiencesAdapter experiencesAdapter = new ExperiencesAdapter(activity, skills, null, true);
            binding.rvEmpHistory.setAdapter(experiencesAdapter);
            binding.rvEmpHistory.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void bindView(View view, int position) {
        TextView tvLanguage = view.findViewById(R.id.tv_language);
        TextView tvLevel = view.findViewById(R.id.tv_level);

        Skill item = Objects.requireNonNull(aboutProfileFragmentVM.getListMutableLiveData().getValue()).get(position);
        tvLanguage.setText(String.format("%s:", item.skillTitle));
        tvLevel.setText(item.skillValue);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
