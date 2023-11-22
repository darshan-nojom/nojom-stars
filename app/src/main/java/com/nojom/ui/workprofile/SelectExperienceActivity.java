package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivitySelectExperienceBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

public class SelectExperienceActivity extends BaseActivity {
    private ActivitySelectExperienceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_experience);
        binding.setExpAct(this);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvCancel.setOnClickListener(v -> gotoMainActivity(Constants.TAB_HOME));
        binding.toolbar.progress.setProgress(30);
    }

    public void onClickBeginner() {
        binding.btnNext.setVisibility(View.VISIBLE);
        whiteText(binding.tvBeginner, binding.tvBeginnerText);
        blackText(binding.tvIntermediate, binding.tvIntermediateText, binding.tvAdvanced, binding.tvAdvancedText);
        setBlueBg(binding.llBeginner);
        setWhiteBg(binding.llAdvanced, binding.llIntermediate);
    }

    public void onClickIntermediate() {
        binding.btnNext.setVisibility(View.VISIBLE);
        whiteText(binding.tvIntermediate, binding.tvIntermediateText);
        blackText(binding.tvBeginner, binding.tvBeginnerText, binding.tvAdvanced, binding.tvAdvancedText);
        setBlueBg(binding.llIntermediate);
        setWhiteBg(binding.llAdvanced, binding.llBeginner);
    }

    public void onClickAdvance() {
        binding.btnNext.setVisibility(View.VISIBLE);
        whiteText(binding.tvAdvanced, binding.tvAdvancedText);
        blackText(binding.tvIntermediate, binding.tvIntermediateText, binding.tvBeginner, binding.tvBeginnerText);
        setBlueBg(binding.llAdvanced);
        setWhiteBg(binding.llBeginner, binding.llIntermediate);
    }

    public void onClickNext() {
        redirectActivity(UpdateLocationActivity.class);
    }

    void blackText(TextView... textViews) {
        for (TextView textView : textViews) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    void whiteText(TextView... textViews) {
        for (TextView textView : textViews) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    void setBlueBg(View... views) {
        for (View v : views) {
            v.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_rounded_corner_10));
        }
    }

    void setWhiteBg(View... views) {
        for (View v : views) {
            v.setBackground(ContextCompat.getDrawable(this, R.drawable.white_rounded_corner_10));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }
}
