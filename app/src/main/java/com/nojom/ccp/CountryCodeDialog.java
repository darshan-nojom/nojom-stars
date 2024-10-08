package com.nojom.ccp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import org.intellij.lang.annotations.Language;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by hbb20 on 11/1/16.
 */
class CountryCodeDialog {
    public static void openCountryCodeDialog(final CountryCodePicker codePicker) {
        final Context context = codePicker.getContext();
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light_BottomSheetDialog);
        codePicker.refreshCustomMasterList();
        codePicker.refreshPreferredCountries();
        List<CCPCountry> masterCountries = CCPCountry.getCustomMasterCountryList(context, codePicker);
        masterCountries.get(0).setSelect(true);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        RecyclerView rvCountry = dialog.findViewById(R.id.rv_items);
        final EditText etSearch = dialog.findViewById(R.id.et_search);
        etSearch.setHint(String.format(context.getString(R.string.search_for), context.getString(R.string.country)));

        final CountryCodeAdapter cca = new CountryCodeAdapter(context, masterCountries, codePicker, etSearch, tvApply, tvCancel, dialog);
        rvCountry.setLayoutManager(new LinearLayoutManager(context));
        rvCountry.setAdapter(cca);

        dialog.setOnDismissListener(dialogInterface -> {
            try {
                Utils.hideSoftKeyboard((Activity) context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cca.clearSelected();
        });

        dialog.setOnCancelListener(dialogInterface -> Utils.hideSoftKeyboard((Activity) context));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> {
            try {
                Utils.openSoftKeyboard((Activity) context, etSearch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        etSearch.requestFocus();
    }

    public static void openCountryCodeDialog1(final CountryCodePicker codePicker) {
        Context context = codePicker.getContext();
        Dialog dialog = new Dialog(context, R.style.Theme_Design_Light_BottomSheetDialog);
        codePicker.refreshCustomMasterList();
        codePicker.refreshPreferredCountries();
        codePicker.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
        List<CCPCountry> masterCountries = CCPCountry.getCustomMasterCountryList(context, codePicker);
        CCPCountry ccpCountryEgypt = null, ccpCountrySaudi = null;
        for (CCPCountry country : masterCountries) {
            if (country.nameCode.equals("eg")) {
                ccpCountryEgypt = country;
            } else if (country.nameCode.equals("sa")) {
                ccpCountrySaudi = country;
            }
        }
        masterCountries.get(0).setSelect(true);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_country);
        dialog.setCancelable(true);

        RecyclerView rvCountry = dialog.findViewById(R.id.rv_items);
        EditText etSearch = dialog.findViewById(R.id.et_search);
        RelativeLayout relEgypt = dialog.findViewById(R.id.lin_view_egypt);
        RelativeLayout relSaudi = dialog.findViewById(R.id.lin_view_saudi);
        ImageView chkEgypt = dialog.findViewById(R.id.img_chk);
        ImageView chkSaudi = dialog.findViewById(R.id.img_chk_saudi);
        TextView txtPopular = dialog.findViewById(R.id.txt_popular);
//        etSearch.setHint(String.format(context.getString(R.string.search_for), context.getString(R.string.country)));

        if (ccpCountryEgypt != null && codePicker.selectedCCPCountry.nameCode.equals(ccpCountryEgypt.nameCode)) {
            chkEgypt.setVisibility(View.VISIBLE);
            chkSaudi.setVisibility(View.GONE);
        } else if (ccpCountrySaudi != null && codePicker.selectedCCPCountry.nameCode.equals(ccpCountrySaudi.nameCode)) {
            chkEgypt.setVisibility(View.GONE);
            chkSaudi.setVisibility(View.VISIBLE);
        }

        CCPCountry finalCcpCountryEgypt = ccpCountryEgypt;
        relEgypt.setOnClickListener(view -> {
            chkEgypt.setVisibility(View.VISIBLE);
            chkSaudi.setVisibility(View.GONE);
            codePicker.onUserTappedCountry(finalCcpCountryEgypt);
            dialog.dismiss();
        });
        CCPCountry finalCcpCountrySaudi = ccpCountrySaudi;
        relSaudi.setOnClickListener(view -> {
            chkEgypt.setVisibility(View.GONE);
            chkSaudi.setVisibility(View.VISIBLE);
            codePicker.onUserTappedCountry(finalCcpCountrySaudi);
            dialog.dismiss();
        });


        CountryCodeAdapter1 cca = new CountryCodeAdapter1(context, masterCountries, codePicker, null, dialog);
        rvCountry.setLayoutManager(new LinearLayoutManager(context));
        rvCountry.setAdapter(cca);

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    relSaudi.setVisibility(View.VISIBLE);
                    relEgypt.setVisibility(View.VISIBLE);
                    txtPopular.setVisibility(View.VISIBLE);
                } else {
                    relSaudi.setVisibility(View.GONE);
                    relEgypt.setVisibility(View.GONE);
                    txtPopular.setVisibility(View.GONE);
                }
                cca.applyQuery(s.toString());
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                return true;
            }

            return false;
        });

        dialog.setOnDismissListener(dialogInterface -> {
            try {
                Utils.hideSoftKeyboard((Activity) context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cca.clearSelected();
        });

        dialog.setOnCancelListener(dialogInterface -> {
            try {
                Utils.hideSoftKeyboard((Activity) context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> {
            try {
                Utils.openSoftKeyboard((Activity) context, etSearch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        etSearch.requestFocus();
    }


    public static void openCountryCodeDialog1(final CountryCodePicker codePicker, BaseActivity activity) {
        Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
        codePicker.refreshCustomMasterList();
        codePicker.refreshPreferredCountries();
        codePicker.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
        List<CCPCountry> masterCountries = CCPCountry.getCustomMasterCountryList(activity, codePicker);
        CCPCountry ccpCountryEgypt = null, ccpCountrySaudi = null;
        for (CCPCountry country : masterCountries) {
            if (country.nameCode.equals("eg")) {
                ccpCountryEgypt = country;
            } else if (country.nameCode.equals("sa")) {
                ccpCountrySaudi = country;
            }
        }
        masterCountries.get(0).setSelect(true);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_country);
        dialog.setCancelable(true);

        RecyclerView rvCountry = dialog.findViewById(R.id.rv_items);
        EditText etSearch = dialog.findViewById(R.id.et_search);
//        etSearch.setHint(String.format(context.getString(R.string.search_for), context.getString(R.string.country)));
        RelativeLayout relEgypt = dialog.findViewById(R.id.lin_view_egypt);
        RelativeLayout relSaudi = dialog.findViewById(R.id.lin_view_saudi);
        ImageView chkEgypt = dialog.findViewById(R.id.img_chk);
        ImageView chkSaudi = dialog.findViewById(R.id.img_chk_saudi);
        TextView txtPopular = dialog.findViewById(R.id.txt_popular);
//        etSearch.setHint(String.format(context.getString(R.string.search_for), context.getString(R.string.country)));

        if (ccpCountryEgypt != null && codePicker.selectedCCPCountry.nameCode.equals(ccpCountryEgypt.nameCode)) {
            chkEgypt.setVisibility(View.VISIBLE);
            chkSaudi.setVisibility(View.GONE);
        } else if (ccpCountrySaudi != null && codePicker.selectedCCPCountry.nameCode.equals(ccpCountrySaudi.nameCode)) {
            chkEgypt.setVisibility(View.GONE);
            chkSaudi.setVisibility(View.VISIBLE);
        }

        CCPCountry finalCcpCountryEgypt = ccpCountryEgypt;
        relEgypt.setOnClickListener(view -> {
            chkEgypt.setVisibility(View.VISIBLE);
            chkSaudi.setVisibility(View.GONE);
            codePicker.onUserTappedCountry(finalCcpCountryEgypt);
            dialog.dismiss();
        });
        CCPCountry finalCcpCountrySaudi = ccpCountrySaudi;
        relSaudi.setOnClickListener(view -> {
            chkEgypt.setVisibility(View.GONE);
            chkSaudi.setVisibility(View.VISIBLE);
            codePicker.onUserTappedCountry(finalCcpCountrySaudi);
            dialog.dismiss();
        });

        CountryCodeAdapter1 cca = new CountryCodeAdapter1(activity, masterCountries, codePicker, null, dialog);
        rvCountry.setLayoutManager(new LinearLayoutManager(activity));
        rvCountry.setAdapter(cca);

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    relSaudi.setVisibility(View.VISIBLE);
                    relEgypt.setVisibility(View.VISIBLE);
                    txtPopular.setVisibility(View.VISIBLE);
                } else {
                    relSaudi.setVisibility(View.GONE);
                    relEgypt.setVisibility(View.GONE);
                    txtPopular.setVisibility(View.GONE);
                }
                cca.applyQuery(s.toString());
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                return true;
            }

            return false;
        });

        dialog.setOnDismissListener(dialogInterface -> {
            try {
                Utils.hideSoftKeyboard((Activity) activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cca.clearSelected();
        });

        dialog.setOnCancelListener(dialogInterface -> {
            try {
                Utils.hideSoftKeyboard((Activity) activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> {
            try {
                Utils.openSoftKeyboard((Activity) activity, etSearch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        etSearch.requestFocus();
    }

    static {
        Field editorField = null;
        Field cursorDrawableField = null;
        Field cursorDrawableResourceField = null;
        try {
            cursorDrawableResourceField = TextView.class.getDeclaredField("mCursorDrawableRes");
            cursorDrawableResourceField.setAccessible(true);
            final Class<?> drawableFieldClass;
            editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            drawableFieldClass = editorField.getType();
            cursorDrawableField = drawableFieldClass.getDeclaredField("mCursorDrawable");
            cursorDrawableField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
