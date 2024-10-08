package com.nojom.ui.workprofile;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.ActivityLanguagesBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.model.Language;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Response;

public class LanguagesActivity extends BaseActivity implements ResponseListener, RecyclerviewAdapter.OnViewBindListner {
    private ActivityLanguagesBinding binding;
    private LanguagesActivityVM languagesActivityVM;
    public LayoutBinderHelper layoutBinderHelper;
    private RecyclerviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_languages);
        binding.setLangAct(this);
        layoutBinderHelper = new LayoutBinderHelper();
        binding.setLayoutBinder(layoutBinderHelper);

        if (getIntent() != null) {
            boolean isEdit = getIntent().getBooleanExtra(Constants.IS_EDIT, false);
            layoutBinderHelper.setIsEdit(isEdit);
        }

        languagesActivityVM = ViewModelProviders.of(this).get(LanguagesActivityVM.class);
        initData();
        languagesActivityVM.init(this, binding);
        languagesActivityVM.setResponseListener(this);
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvCancel.setOnClickListener(v -> gotoMainActivity(Constants.TAB_HOME));
        binding.editToolBar.tvEditCancel.setOnClickListener(v -> onBackPressed());
        binding.editToolBar.tvSave.setOnClickListener(v -> onClickNext());

        binding.toolbar.progress.setProgress(80);
        binding.rvLanguages.setLayoutManager(new LinearLayoutManager(this));

        languagesActivityVM.getListMutableLiveData().observe(this, data -> {
            if (mAdapter == null) {
                mAdapter = new RecyclerviewAdapter(data, R.layout.item_language_edit, LanguagesActivity.this);
                binding.rvLanguages.setAdapter(mAdapter);
            } else {
                mAdapter.doRefresh(data);
            }
        });
    }


    @Override
    public void bindView(View view, final int position) {
        final TextView tvLanguage = view.findViewById(R.id.tv_language);
        final TextView tvLevel = view.findViewById(R.id.tv_level);
        ImageView imgDelete = view.findViewById(R.id.img_delete);
        ArrayList<Language.Data> languageList = languagesActivityVM.getListMutableLiveData().getValue();
        if (languageList == null || languageList.size() == 0) {
            return;
        }
        try {
            if (Objects.requireNonNull(languageList).get(position) != null) {
                tvLanguage.setText(languageList.get(position).getName(language));
                tvLevel.setText(languageList.get(position).level);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (position == 0) {
            imgDelete.setVisibility(View.GONE);
        } else {
            imgDelete.setVisibility(View.VISIBLE);
        }

        tvLanguage.setOnClickListener(view1 -> {
            if (languagesActivityVM.getArrayMutableLiveData() != null) {
                languagesActivityVM.showLanguageSelectDialog(languagesActivityVM.getArrayMutableLiveData().getValue(), tvLanguage, languageList.get(position), true);
            }
        });

        tvLevel.setOnClickListener(view12 -> {
            if (!isEmpty(languageList.get(position).getName(language))) {
                languagesActivityVM.showLanguageSelectDialog(languagesActivityVM.getLevelList(), tvLevel, languageList.get(position), false);
            }
        });

        imgDelete.setOnClickListener(view13 -> {
            deleteDialog(this, languageList, position);
        });
    }

    private void deleteLanguage(ArrayList<Language.Data> languageList, int position) {
        languageList.remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, languageList.size());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void onClickNext() {
        if (languagesActivityVM.isValid()) {
            try {
                StringBuilder languageIds = null;
                StringBuilder levelsIds = null;
                for (Language.Data data : Objects.requireNonNull(languagesActivityVM.getListMutableLiveData().getValue())) {
                    languageIds = (languageIds == null ? new StringBuilder() : languageIds.append(",")).append(data.id);
                    levelsIds = (levelsIds == null ? new StringBuilder() : levelsIds.append(",")).append(data.levelId);
                }
                String languageId = languageIds == null ? "" : languageIds.toString();
                String levelId = levelsIds == null ? "" : levelsIds.toString();

                if (layoutBinderHelper.getIsEdit()) {
                    binding.editToolBar.tvSave.setVisibility(View.INVISIBLE);
                    binding.editToolBar.progressBar.setVisibility(View.VISIBLE);
                } else {
                    binding.btnNext.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                }

                languagesActivityVM.addLanguage(languageId, levelId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickAddLanguage() {
        languagesActivityVM.addMoreLanguage();
    }

    @Override
    public void onResponseSuccess(Response<GeneralModel> response) {
        if (layoutBinderHelper.getIsEdit()) {
            toastMessage(getString(R.string.language_added));
            setResult(RESULT_OK);
            finish();
        } else {
            redirectActivity(PayRateActivity.class);
            updateUI();
        }

    }

    @Override
    public void onError() {
        updateUI();
    }

    private void updateUI() {
        if (layoutBinderHelper.getIsEdit()) {
            binding.editToolBar.tvSave.setVisibility(View.VISIBLE);
            binding.editToolBar.progressBar.setVisibility(View.GONE);
        } else {
            binding.btnNext.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    void deleteDialog(BaseActivity activity, ArrayList<Language.Data> languageList, int position) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        String s = activity.getString(R.string.language_discard_msg);
        String[] words = {getString(R.string.delete).toLowerCase(Locale.ROOT)};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvMessage.setText(Utils.getBoldString(activity, s, fonts, null, words));

        tvCancel.setText(getString(R.string.cancel));
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            deleteLanguage(languageList, position);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }
}
