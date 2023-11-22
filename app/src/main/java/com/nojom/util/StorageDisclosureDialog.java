package com.nojom.util;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.textview.TextViewSFTextPro;
import android.view.Gravity;
import android.view.WindowManager;


import com.nojom.R;
import com.nojom.ui.BaseActivity;

import java.util.Objects;

public class StorageDisclosureDialog {
    private BaseActivity activity;
    private OnClickListener onClickListener;

    public StorageDisclosureDialog(BaseActivity activity, OnClickListener onClickListener) {
        this.activity = activity;
        this.onClickListener = onClickListener;

        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_storage_disclosure);
        dialog.setCancelable(true);

        TextViewSFTextPro tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextViewSFTextPro tvOk = dialog.findViewById(R.id.tv_ok);
        
        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        tvOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (onClickListener != null) {
                onClickListener.onClickOk();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    public interface OnClickListener {
        void onClickOk();
    }
}
