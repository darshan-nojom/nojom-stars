package com.nojom.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.EducationItemBinding;
import com.nojom.model.Education;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.SimpleViewHolder> {

    private List<Education> mDataset;
    private BaseActivity context;
    private SingleSelectionItemAdapter itemAdapter;

    public EducationAdapter(BaseActivity context) {
        this.context = context;
    }

    public void doRefresh(List<Education> objects) {
        this.mDataset = objects;
        notifyDataSetChanged();
    }

    public void addEducation(Education education) {
        mDataset.add(education);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        EducationItemBinding itemBinding =
                EducationItemBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        final Education item = mDataset.get(position);
        holder.binding.setEducation(item);

        holder.binding.tvHeading.setText(String.format(context.getString(R.string.education_), position + 1));
        holder.binding.tvRemove.setPaintFlags(holder.binding.tvRemove.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (mDataset.size() == 1) {
            holder.binding.tvRemove.setText(context.getString(R.string.clear));
        } else {
            holder.binding.tvRemove.setText(context.getString(R.string.remove));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public List<Education> getData() {
        return mDataset;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        EducationItemBinding binding;

        SimpleViewHolder(EducationItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.etCollege.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (mDataset != null && mDataset.size() > 0 && getAdapterPosition() >= 0) {
                            mDataset.get(getAdapterPosition()).college = s.toString();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            binding.etDegree.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (mDataset != null && mDataset.size() > 0 && getAdapterPosition() >= 0) {
                            mDataset.get(getAdapterPosition()).degree = s.toString();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            binding.tvLevel.setOnClickListener(view -> {
                try {
                    if (mDataset != null && mDataset.size() > 0 && getAdapterPosition() >= 0) {
                        showItemSelectDialog(binding.tvLevel, mDataset.get(getAdapterPosition()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.tvStartYear.setOnClickListener(view -> {
                try {
                    if (mDataset != null && mDataset.size() > 0 && getAdapterPosition() >= 0) {
                        selectFromDate(binding.tvStartYear, mDataset.get(getAdapterPosition()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.tvEndYear.setOnClickListener(view -> {
                try {
                    if (mDataset != null && mDataset.size() > 0 && getAdapterPosition() >= 0) {
                        selectToDate(binding.tvStartYear.getText().toString(), binding.tvEndYear, mDataset.get(getAdapterPosition()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.tvRemove.setOnClickListener(view -> {
                try {
                    if (mDataset != null && mDataset.size() > 0 && getAdapterPosition() >= 0) {
                        if (mDataset.size() == 1) {
                            mDataset.get(getAdapterPosition()).college = "";
                            mDataset.get(getAdapterPosition()).degree = "";
                            mDataset.get(getAdapterPosition()).startYear = "";
                            mDataset.get(getAdapterPosition()).endYear = "";
                            notifyDataSetChanged();
                        } else {
                            discardDialog(context, getAdapterPosition());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void selectFromDate(final TextView tvYear, final Education item) {
        int year, month;
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DatePickerDialog, (datePicker, year1, month1, i2) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
            String date = dateFormat.format(calendar.getTime());
            tvYear.setText(date);
            item.startYear = date;
        }, year, month, 0);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

        View view = datePickerDialog.getDatePicker().findViewById(context.getResources().getIdentifier("day", "id", "android"));
        if (view != null)
            view.setVisibility(View.GONE);
    }

    private void selectToDate(String fromDate, final TextView tvYear, final Education item) {
        if (TextUtils.isEmpty(fromDate)) {
            ((BaseActivity) context).toastMessage("Select Start Year First");
            return;
        }

        int year = 0, month = 0;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
        Date fDate;
        try {
            fDate = dateFormat.parse(fromDate);
            month = Integer.parseInt((String) DateFormat.format("MM", fDate));
            year = Integer.parseInt((String) DateFormat.format("yyyy", fDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        month = month - 1;
        Calendar c = Calendar.getInstance();
        c.set(year, month, 1);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DatePickerDialog, (datePicker, year1, month1, i2) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM yyyy");
            String date = dateFormat1.format(calendar.getTime());
            tvYear.setText(date);
            item.endYear = date;
        }, year, month, 2);

        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();

        View view = datePickerDialog.getDatePicker().findViewById(context.getResources().getIdentifier("day", "id", "android"));
        if (view != null)
            view.setVisibility(View.GONE);
    }

    private void showItemSelectDialog(final TextView tvLevel, final Education item) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        EditText etSearch = dialog.findViewById(R.id.et_search);
        etSearch.setVisibility(View.GONE);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        rvTypes.setLayoutManager(new LinearLayoutManager(context));
        String[] educationLevel = context.getResources().getStringArray(R.array.education_level);
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(educationLevel));
        int selectedPosition = -1;
        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).equalsIgnoreCase(tvLevel.getText().toString())) {
                    selectedPosition = i;
                }
            }
            itemAdapter = new SingleSelectionItemAdapter(context, arrayList, selectedPosition);
            rvTypes.setAdapter(itemAdapter);
        }

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvApply.setOnClickListener(v -> {
            if (itemAdapter != null && itemAdapter.getSelectedItem() != null) {
                tvLevel.setText(itemAdapter.getSelectedItem());
                item.level = itemAdapter.getSelectedItem();
                dialog.dismiss();
            } else {
                ((BaseActivity) context).toastMessage("Please select one item");
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    void discardDialog(BaseActivity activity, int adapterPos) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        String s = activity.getString(R.string.education_discard_msg);
        String[] words = {"remove"};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvMessage.setText(Utils.getBoldString(activity, s, fonts, null, words));

        tvCancel.setText(activity.getString(R.string.cancel));
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            mDataset.remove(adapterPos);
            notifyDataSetChanged();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }
}
