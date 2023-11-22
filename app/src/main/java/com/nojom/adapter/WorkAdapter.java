package com.nojom.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import com.nojom.databinding.WorkItemBinding;
import com.nojom.model.ServicesModel;
import com.nojom.model.Work;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.SimpleViewHolder> {

    private List<Work> mDataset;
    private Context context;
    private ServicesAdapter serviceAdapter;
    private SingleSelectionItemAdapter itemAdapter;

    public WorkAdapter(Context context) {
        this.context = context;
    }

    public void doRefresh(List<Work> objects) {
        this.mDataset = objects;
        notifyDataSetChanged();
    }

    public void addWork(Work work) {
        mDataset.add(work);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        WorkItemBinding itemBinding =
                WorkItemBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        final Work item = mDataset.get(position);
        holder.binding.setWork(item);

        if (item.isCurrentlyWorking == 1) {
            holder.binding.tvEndYear.setText(context.getString(R.string.present));
        } else {
            holder.binding.tvEndYear.setText(item.endYear);
        }


        holder.binding.tvHeading.setText(String.format(context.getString(R.string.employment_), position + 1));
        holder.binding.tvRemove.setPaintFlags(holder.binding.tvRemove.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (mDataset.size() == 1) {
            holder.binding.tvRemove.setText(context.getString(R.string.clear));
        } else {
            holder.binding.tvRemove.setText(context.getString(R.string.remove));
        }

        holder.binding.chkWorking.setOnCheckedChangeListener(null);
        holder.binding.chkWorking.setChecked(item.isCurrentlyWorking == 1);
        holder.binding.chkWorking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (Work work : mDataset) {
                if (work.isCurrentlyWorking == 1) {
//                    Calendar calendar = Calendar.getInstance();
//                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM yyyy");
                    work.endYear = "";
                }
                work.isCurrentlyWorking = 0;

            }
            item.isCurrentlyWorking = isChecked ? 1 : 0;
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM yyyy");
//            Calendar calendar = Calendar.getInstance();
//            item.endYear = dateFormat1.format(calendar.getTime());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public List<Work> getData() {
        return mDataset;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        WorkItemBinding binding;

        SimpleViewHolder(WorkItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.etCompany.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mDataset.get(getAdapterPosition()).company = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            binding.tvStartYear.setOnClickListener(view -> selectFromDate(binding.tvStartYear, mDataset.get(getAdapterPosition())));

            binding.tvEndYear.setOnClickListener(view -> {
                if (mDataset.get(getAdapterPosition()).isCurrentlyWorking == 0) {
                    selectToDate(binding.tvStartYear.getText().toString(), binding.tvEndYear, mDataset.get(getAdapterPosition()));
                }
            });

            binding.tvJobTitle.setOnClickListener(view -> showItemSelectDialog(binding.tvJobTitle, mDataset.get(getAdapterPosition())));

            binding.tvExperience.setOnClickListener(view -> showSingleSelectionDialog(binding.tvExperience, mDataset.get(getAdapterPosition())));

            binding.tvRemove.setOnClickListener(view -> {
                if (mDataset.size() == 1) {
                    mDataset.get(getAdapterPosition()).company = "";
                    mDataset.get(getAdapterPosition()).jobTitle = "";
                    mDataset.get(getAdapterPosition()).experience = "";
                    mDataset.get(getAdapterPosition()).startYear = "";
                    mDataset.get(getAdapterPosition()).endYear = "";
                    mDataset.get(getAdapterPosition()).isCurrentlyWorking = 0;
                } else {
                    mDataset.remove(getAdapterPosition());
                }
                notifyDataSetChanged();
            });
        }
    }

    private void selectFromDate(final TextView tvYear, final Work item) {
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
        }, year, month, 1);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
        View view = datePickerDialog.getDatePicker().findViewById(context.getResources().getIdentifier("day", "id", "android"));
        if (view != null)
            view.setVisibility(View.GONE);
    }

    private void selectToDate(String fromDate, final TextView tvYear, final Work item) {
        if (TextUtils.isEmpty(fromDate)) {
            ((BaseActivity) context).toastMessage(context.getString(R.string.select_start_year_first));
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

        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showItemSelectDialog(final TextView tvJobTitle, final Work item) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialog.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        etSearch.setHint(String.format(context.getString(R.string.search_for), context.getString(R.string.services).toLowerCase()));

        rvTypes.setLayoutManager(new LinearLayoutManager(context));
        List<ServicesModel.Data> mData = Preferences.getTopServices(context);
        if (mData != null && mData.size() > 0) {
//            for (int i = 0; i < mData.size(); i++) {
//                if (mData.get(i).name.equalsIgnoreCase(tvJobTitle.getText().toString())) {
//                    mData.get(i).isSelected = true;
//                }
//            }
            for (ServicesModel.Data data : mData) {
                if (data.name.equalsIgnoreCase(tvJobTitle.getText().toString())) {
                    data.isSelected = true;
                }
            }
            serviceAdapter = new ServicesAdapter(context, mData);
            rvTypes.setAdapter(serviceAdapter);
        }

        tvCancel.setOnClickListener(v -> {
            Utils.hideSoftKeyboard((Activity) context);
            dialog.dismiss();
        });

        tvApply.setOnClickListener(v -> {
            Utils.hideSoftKeyboard((Activity) context);
            if (serviceAdapter != null && serviceAdapter.getSelectedItem() != null) {
                tvJobTitle.setText(serviceAdapter.getSelectedItem().name);
                item.serviceId = serviceAdapter.getSelectedItem().id;
                item.jobTitle = serviceAdapter.getSelectedItem().name;
                dialog.dismiss();
            } else {
                ((BaseActivity) context).toastMessage(context.getString(R.string.please_select_service));
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (serviceAdapter != null)
                    serviceAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

    private void showSingleSelectionDialog(final TextView tvLevel, final Work item) {
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
        String[] experience = context.getResources().getStringArray(R.array.experience);
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(experience));
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
            Utils.hideSoftKeyboard((Activity) context);
            if (itemAdapter != null && itemAdapter.getSelectedItem() != null) {
                tvLevel.setText(itemAdapter.getSelectedItem());
                item.experience = itemAdapter.getSelectedItem();
                dialog.dismiss();
            } else {
                ((BaseActivity) context).toastMessage(context.getString(R.string.please_select_one_time));
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
}
