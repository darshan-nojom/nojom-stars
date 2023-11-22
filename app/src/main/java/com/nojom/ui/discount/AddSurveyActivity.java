package com.nojom.ui.discount;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.ActivityAddSurveyBinding;
import com.nojom.ui.BaseActivity;


public class AddSurveyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddSurveyBinding addSurveyBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_survey);
        new AddSurveyActivityVM(Task24Application.getActivity(), addSurveyBinding, this);
    }
}
