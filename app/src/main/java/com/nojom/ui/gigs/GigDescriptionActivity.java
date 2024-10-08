package com.nojom.ui.gigs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityGigTitleBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.Objects;


public class GigDescriptionActivity extends BaseActivity implements Constants {
    private ActivityGigTitleBinding binding;
    private int screen;
    private String data;
    private String selectedTab;
    String regex = "^.*[A-Z].*$";
    private boolean isInfluence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gig_title);
        runOnUiThread(this::initData);

    }

    private void initData() {

        if (getIntent() != null) {
            screen = getIntent().getIntExtra("screen", 0);
            data = getIntent().getStringExtra("data");
            selectedTab = getIntent().getStringExtra("tab");
            isInfluence = getIntent().getBooleanExtra("is_influe", false);
        }


        if (isInfluence) {//influencer view
            binding.toolbar.tvTitle.setText(getString(R.string.description));
            binding.relStatusView.setVisibility(View.INVISIBLE);
            binding.title1.setVisibility(View.INVISIBLE);
            binding.title2.setVisibility(View.INVISIBLE);
            binding.title3.setVisibility(View.INVISIBLE);
            binding.etHeadline.setHint(getString(R.string.please_enter_description));
        } else {
            if (screen == GIG_DESC) {
//            binding.etHeadline.setKeyListener(DigitsKeyListener.getInstance("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz. "));
                binding.toolbar.tvTitle.setText(getString(R.string.description));
                binding.etHeadline.setMinLines(6);
                binding.title1.setText(getString(R.string.create_desc_min_120_char));
                binding.title2.setText(getString(R.string.desc_more_then_100_word));
                binding.title3.setText("•  " + getString(R.string.u25cf_description_should_not_be_all_uppercase_letters));
                if (language.equals("ar")) {
                    binding.title3.setVisibility(View.GONE);
                } else {
                    binding.title3.setVisibility(View.VISIBLE);
                }

                binding.etHeadline.setHint(getString(R.string.write_your_gig_description));
                binding.tvCharacter.setText("0/1200 " + getString(R.string.max));
            } else if (screen == GIG_TITLE) {
                binding.toolbar.tvTitle.setText(getString(R.string.gig_title));
                binding.etHeadline.setText(getString(R.string.i_will) + " ");
                Selection.setSelection(binding.etHeadline.getText(), binding.etHeadline.getText().length());
                binding.tvCharacter.setText("0/80 " + getString(R.string.max));
                if (language.equals("ar")) {
                    binding.title2.setVisibility(View.GONE);
                } else {
                    binding.title2.setVisibility(View.VISIBLE);
                }
            } else if (screen == GIG_PACKAGE_NAME) {
                binding.toolbar.tvTitle.setText(selectedTab + " " + getString(R.string.package_name));
                binding.etHeadline.setHint("Gig package name");
                binding.title3.setVisibility(View.INVISIBLE);
                binding.title2.setVisibility(View.INVISIBLE);
                binding.title1.setVisibility(View.INVISIBLE);
                binding.tvCharacter.setText("0/35 " + getString(R.string.max));
            } else if (screen == GIG_PACKAGE_DESC) {
                binding.toolbar.tvTitle.setText(getString(R.string.description));
                binding.etHeadline.setHint("Gig package description");
                binding.title3.setVisibility(View.INVISIBLE);
                binding.title2.setVisibility(View.INVISIBLE);
                binding.title1.setVisibility(View.INVISIBLE);
                binding.tvCharacter.setText("0/100 " + getString(R.string.max));
            } else if (screen == GIG_REQUIREMENT_DESC || screen == GIG_DEAD_REQUIREMENT_DESC) {
//            binding.etHeadline.setKeyListener(DigitsKeyListener.getInstance("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz. "));
                binding.toolbar.tvTitle.setText(getString(R.string.package_description));
                binding.etHeadline.setMinLines(6);
                binding.title1.setText(getString(R.string.dec_shouldnt_uppercase));
                binding.title2.setText(getString(R.string.des_should_more_then_3_chat));
                binding.title3.setVisibility(View.INVISIBLE);
                binding.etHeadline.setHint(getString(R.string.package_description));
                binding.tvCharacter.setText("0/300 " + getString(R.string.max));
            }
        }

        if (!TextUtils.isEmpty(data) && !data.equalsIgnoreCase("null")) {
            binding.etHeadline.setText(data);
            setLengthStatus(data);
        }

        binding.etHeadline.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.etHeadline.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        binding.tvSave.setOnClickListener(v -> {

            if (isInfluence) {//only for Influencer
                if (TextUtils.isEmpty(binding.etHeadline.getText().toString().trim())) {
                    toastMessage(getString(R.string.link_should_not_empty));
                    return;
                }
                if (!Patterns.WEB_URL.matcher(binding.etHeadline.getText().toString()).matches()) {
                    toastMessage(getString(R.string.please_enter_valid_link));
                    return;
                }
                setData();
            } else {
                if (screen == GIG_DESC) {
                    if (checkDescValidation()) {
                        setData();
                    }
                } else if (screen == GIG_TITLE) {
                    if (checkTitleValidation()) {
                        setData();
                    }
                } else if (screen == GIG_PACKAGE_NAME) {
                    if (checkPackageNameValidation()) {
                        setData();
                    }
                } else if (screen == GIG_PACKAGE_DESC) {
                    if (checkPackageDescValidation()) {
                        setData();
                    }
                } else if (screen == GIG_REQUIREMENT_DESC || screen == GIG_DEAD_REQUIREMENT_DESC) {
                    if (checkReqDescValidation()) {
                        setData();
                    }
                }
            }

//                if (screen == GIG_DESC || screen == GIG_TITLE || screen == GIG_PACKAGE_NAME || screen == GIG_PACKAGE_DESC) {
//                    Intent intent = new Intent();
//                    intent.putExtra("data", binding.etHeadline.getText().toString().trim());
//                    setResult(RESULT_OK, intent);
//                    finish();
//                } else if (screen == OFFICE_ADD) {
//                    if (getHeadline().length() < 10) {
//                        toastMessage("Enter minimum 10 character");
//                    }
//
//                }
        });

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());

        binding.etHeadline.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isInfluence) {//only for Influencer
                    if (screen == GIG_DESC) {
                        setLengthStatus(s.toString());
                        checkDescValidation();
                    } else if (screen == GIG_TITLE) {
                        setLengthStatus(s.toString());
                        checkTitleValidation();
                    } else if (screen == GIG_PACKAGE_NAME) {
                        setLengthStatus(s.toString());
                        checkPackageNameValidation();
                    } else if (screen == GIG_PACKAGE_DESC) {
                        setLengthStatus(s.toString());
                        checkPackageDescValidation();
                    } else if (screen == GIG_REQUIREMENT_DESC || screen == GIG_DEAD_REQUIREMENT_DESC) {
                        setLengthStatus(s.toString());
                        checkReqDescValidation();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setData() {
        Intent intent = new Intent();
        intent.putExtra("data", binding.etHeadline.getText().toString().trim());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setLengthStatus(String length) {
        if (!isInfluence) {//only for Influencer
            if (TextUtils.isEmpty(length)) {
                if (screen == GIG_DESC) {
                    binding.tvCharacter.setText("0/1200 " + getString(R.string.max));
                } else if (screen == GIG_TITLE) {
                    binding.tvCharacter.setText("0/80 " + getString(R.string.max));
                } else if (screen == GIG_PACKAGE_NAME) {
                    binding.tvCharacter.setText("0/35 " + getString(R.string.max));
                } else if (screen == GIG_PACKAGE_DESC) {
                    binding.tvCharacter.setText("0/100 " + getString(R.string.max));
                } else if (screen == GIG_REQUIREMENT_DESC || screen == GIG_DEAD_REQUIREMENT_DESC) {
                    binding.tvCharacter.setText("0/300 " + getString(R.string.max));
                }

            } else {
                if (screen == GIG_DESC) {
                    binding.tvCharacter.setText(length.length() + "/1200 " + getString(R.string.max));
                } else if (screen == GIG_TITLE) {
                    binding.tvCharacter.setText(length.length() + "/80 " + getString(R.string.max));
                } else if (screen == GIG_PACKAGE_NAME) {
                    binding.tvCharacter.setText(length.length() + "/35 " + getString(R.string.max));
                } else if (screen == GIG_PACKAGE_DESC) {
                    binding.tvCharacter.setText(length.length() + "/100 " + getString(R.string.max));
                } else if (screen == GIG_REQUIREMENT_DESC || screen == GIG_DEAD_REQUIREMENT_DESC) {
                    binding.tvCharacter.setText(length.length() + "/300 " + getString(R.string.max));
                }

            }
        }
    }

    private String getHeadline() {
        return Objects.requireNonNull(binding.etHeadline.getText()).toString().trim();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public boolean isContainLowerCase(String s) {
        boolean isContainLowerCase = false;
        for (int i = 0; i < s.length(); i++) {

            if (language.equals("ar")) {
                if (isLowerCaseArabic(s.charAt(i))) {
                    isContainLowerCase = true;
                }
            } else {
                if (Character.isLowerCase(s.charAt(i))) {
                    isContainLowerCase = true;
                }
            }
        }
        return isContainLowerCase;
    }

    private static boolean isLowerCaseArabic(char ch) {
        // Check if the character is in the Unicode range for lowercase Arabic letters
        return (ch >= '\u0600' && ch <= '\u06FF') || (ch >= '\u0750' && ch <= '\u077F');
    }

    private boolean checkTitleValidation() {
        if (TextUtils.isEmpty(getHeadline())) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.title_should_not_be_empty));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (!isContainLowerCase(getHeadline())) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.title_should_not_all_upper));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        String[] split = getHeadline().split(" ");
        if (split.length < 2) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.title_atleast_two_words));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (getHeadline().length() < 15) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.title_should_min_15_ch));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (getHeadline().length() > 80) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.title_max_80_char));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        binding.tvStatus.setText(getString(R.string.just_perfect));
        binding.tvStatus.setTextColor(getResources().getColor(R.color.greendark));
        binding.etHeadline.setBackgroundResource(R.drawable.gray_border_5);
        return true;
    }

    private boolean checkDescValidation() {
        if (TextUtils.isEmpty(getHeadline())) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.desc_not_empty));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (!isContainLowerCase(getHeadline())) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.desc_not_in_uppercase));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        String[] split = getHeadline().split(" ");
        if (split.length < 10) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.desc_have_10_words));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (getHeadline().length() < 120) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.desc_min_120_char));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (getHeadline().length() > 1200) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.desc_max_1200_char));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        binding.tvStatus.setText(getString(R.string.just_perfect));
        binding.tvStatus.setTextColor(getResources().getColor(R.color.greendark));
        binding.etHeadline.setBackgroundResource(R.drawable.gray_border_5);
        return true;
    }

    private boolean checkReqDescValidation() {
        if (TextUtils.isEmpty(getHeadline())) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.desc_not_empty));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (!isContainLowerCase(getHeadline())) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.desc_not_in_uppercase));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        String[] split = getHeadline().split(" ");
        if (split.length < 3) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.desc_have_3_words));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (getHeadline().length() < 20) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.desc_min_20_char));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (getHeadline().length() > 300) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.desc_max_300_char));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        binding.tvStatus.setText(getString(R.string.just_perfect));
        binding.tvStatus.setTextColor(getResources().getColor(R.color.greendark));
        binding.etHeadline.setBackgroundResource(R.drawable.gray_border_5);
        return true;
    }

    private boolean checkPackageNameValidation() {
        if (TextUtils.isEmpty(getHeadline())) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.package_not_empty));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (getHeadline().length() > 35) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.package_max_35_char));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        binding.tvStatus.setText(getString(R.string.just_perfect));
        binding.tvStatus.setTextColor(getResources().getColor(R.color.greendark));
        binding.etHeadline.setBackgroundResource(R.drawable.gray_border_5);
        return true;
    }

    private boolean checkPackageDescValidation() {
        if (TextUtils.isEmpty(getHeadline())) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.package_desc_not_empty));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        if (getHeadline().length() > 100) {
            binding.etHeadline.setBackgroundResource(R.drawable.red_border_5);
            binding.tvStatus.setText(getString(R.string.pack_desc_max_100_char));
            binding.tvStatus.setTextColor(getResources().getColor(R.color.red_dark));
            return false;
        }

        binding.tvStatus.setText(getString(R.string.just_perfect));
        binding.tvStatus.setTextColor(getResources().getColor(R.color.greendark));
        binding.etHeadline.setBackgroundResource(R.drawable.gray_border_5);
        return true;
    }
}
