package com.nojom.ui.gigs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.GigAddRequirementAdapter;
import com.nojom.databinding.ActivityRequirementsBinding;
import com.nojom.databinding.DialogAddGigOtherBinding;
import com.nojom.model.RequiremetList;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.Objects;

public class GigAddRequirementActivity extends BaseActivity implements GigAddRequirementAdapter.OnClickReqListener {
    private ActivityRequirementsBinding binding;
    private GigAddRequirementAdapter mAdapter;
    private RequiremetList.Data selectedReq = null;
    private ArrayList<RequiremetList.Data> requirementByCatList;
    //    private ArrayList<RequiremetList.Data> selectedReqList;
    private RequiremetList.Data otherReq = new RequiremetList.Data();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_requirements);
        binding.shimmerLayout.startShimmer();
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.etSearch.setHint(getString(R.string.search_requirements));
//        binding.etSearch.setVisibility(View.GONE);

        runOnUiThread(this::setUI);

        new LongOperation().execute();

    }

    private final class LongOperation extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            if (getIntent() != null) {
                requirementByCatList = (ArrayList<RequiremetList.Data>) getIntent().getSerializableExtra("list");

                otherReq.isSelected = false;
                otherReq.id = -1;
                otherReq.inputType = -1;
                otherReq.isOther = 1;
                otherReq.name = getString(R.string.other);
                //requirementByCatList.add(data);
            }

           /* if (selectedReqList != null) {
                for (RequiremetList.Data cat : requirementByCatList) {
                    for (RequiremetList.Data selectedReq : selectedReqList) {
                        if (selectedReq.id == cat.id) {
                            cat.isSelected = true;
                            break;
                        }
                    }
                }
            }*/

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setAdapter(requirementByCatList);
        }
    }

    private void setUI() {
        binding.tvTitle.setText(getString(R.string.what_is_your_req));
        binding.progress.imgBack.setOnClickListener(v -> finish());
        binding.progress.tvSave.setText(getString(R.string.apply));

        binding.progress.tvSave.setOnClickListener(v -> {
            if (selectedReq == null && !otherReq.isSelected) {
                toastMessage(getString(R.string.select_your_req));
                return;
            }

            if (otherReq.isSelected) {
                showOtherGigDialog(otherReq);
            } else {
                Intent intent = new Intent();
                intent.putExtra("req", selectedReq);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        binding.rvCategory.setLayoutManager(new LinearLayoutManager(this));

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAdapter != null) {
                    mAdapter.getFilter().filter("" + s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.rlViewCustom.setOnClickListener(view -> {

            if (otherReq.isSelected) {
                otherReq.isSelected = false;
                binding.imgCheckUncheckCustom.setImageResource(R.drawable.circle_uncheck);
                binding.imgCheckUncheckCustom.setColorFilter(ContextCompat.getColor(this,
                        R.color.full_dark_green), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else {
                otherReq.isSelected = true;
                binding.imgCheckUncheckCustom.setImageResource(R.drawable.check_done);
                binding.imgCheckUncheckCustom.clearColorFilter();
                if (mAdapter != null) {
                    mAdapter.clearSelected();
                }
            }

        });
    }

    private void setAdapter(ArrayList<RequiremetList.Data> data) {
        binding.imgCheckUncheckCustom.setImageResource(R.drawable.circle_uncheck);
        binding.imgCheckUncheckCustom.setColorFilter(ContextCompat.getColor(GigAddRequirementActivity.this,
                R.color.full_dark_green), android.graphics.PorterDuff.Mode.MULTIPLY);
        if (data != null && data.size() > 0) {

            mAdapter = new GigAddRequirementAdapter(this, data, this);
            binding.rvCategory.setAdapter(mAdapter);
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickReq(RequiremetList.Data req) {
        selectedReq = req;
        otherReq.isSelected = false;
        binding.imgCheckUncheckCustom.setImageResource(R.drawable.circle_uncheck);
        binding.imgCheckUncheckCustom.setColorFilter(ContextCompat.getColor(this,
                R.color.full_dark_green), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

   /* private void changeInTheList() {

        ArrayList<RequiremetList.Data> models = new ArrayList<>();

        for (RequiremetList.Data model : requirementByCatList) {
            models.add(model.clone());
        }

        for (RequiremetList.Data model : models) {
            if (selectedReqList.contains(model)) {
                model.isSelected = true;
            }
        }
        mAdapter.setData(models);
    }*/

    boolean isNumberSelected = true;

    void showOtherGigDialog(RequiremetList.Data selectedReq) {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        DialogAddGigOtherBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_add_gig_other, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);

        binding.tvTitleStandard.setText(getString(R.string.quantity));
        binding.tvDescStandard.setText(getString(R.string.how_many_do_you_offer_describe_your_gig_requirement_in_a_physical_quantity));
        binding.tvTitleText.setText(getString(R.string.price_rate));
        binding.tvDescText.setText(getString(R.string.how_much_do_you_charge_briefly_describe_the_price_rate_you_set_per_unit_of_output));
        binding.tvTitle.setText(getString(R.string.other));
        binding.tvContinue.setText(getString(R.string.continue_));

        binding.linNumber.setOnClickListener(view -> {
            isNumberSelected = true;
            binding.linNumber.setBackgroundResource(R.drawable.lightblue_border_6);
            binding.linText.setBackgroundResource(R.drawable.gray_border_6);
            binding.rbNumber.setChecked(true);
            binding.rbText.setChecked(false);

        });

        binding.linText.setOnClickListener(view -> {
            isNumberSelected = false;
            binding.linNumber.setBackgroundResource(R.drawable.gray_border_6);
            binding.linText.setBackgroundResource(R.drawable.lightblue_border_6);
            binding.rbNumber.setChecked(false);
            binding.rbText.setChecked(true);

        });


        binding.tvContinue.setOnClickListener(v -> {
            dialog.dismiss();
//            selectedReq.inputType = -1;
            selectedReq.inputType = isNumberSelected ? 1 : 3;
            Intent intent = new Intent();
            intent.putExtra("req", selectedReq);
            intent.putExtra("isNumber", isNumberSelected);
            setResult(RESULT_OK, intent);
            finish();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

}
