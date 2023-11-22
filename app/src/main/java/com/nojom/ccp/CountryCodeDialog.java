package com.nojom.ccp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.util.Utils;

import java.lang.reflect.Field;
import java.util.List;
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
            Utils.hideSoftKeyboard((Activity) context);
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
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard((Activity) context, etSearch)));
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
