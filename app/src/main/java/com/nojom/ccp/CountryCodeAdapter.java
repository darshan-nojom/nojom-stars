package com.nojom.ccp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemSelectFullImgBinding;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbb20 on 11/1/16.
 */
class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.CountryCodeViewHolder> {
    private List<CCPCountry> filteredCountries, masterCountries;
    private CountryCodePicker codePicker;
    private EditText editText_search;
    private Dialog dialog;
    private Context context;
    private boolean isSearchMode = false;
    private TextView tvApply;
    private TextView tvCancel;

    CountryCodeAdapter(Context context, List<CCPCountry> countries, CountryCodePicker codePicker, final EditText editText_search,
                       TextView tvApply, TextView tvCancel, Dialog dialog) {
        this.context = context;
        this.masterCountries = countries;
        this.codePicker = codePicker;
        this.dialog = dialog;
        this.tvApply = tvApply;
        this.tvCancel = tvCancel;
        this.editText_search = editText_search;
        this.filteredCountries = getFilteredCountries("");
        setSearchBar();
        setButtonClick();
    }

    private void setButtonClick() {
        tvApply.setOnClickListener(v -> {
            codePicker.onUserTappedCountry(getSelectedItem());
            dialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void setSearchBar() {
        if (codePicker.isSearchAllowed()) {
            setTextWatcher();
        }
    }

    /**
     * add textChangeListener, to apply new query each time editText get text changed.
     */
    private void setTextWatcher() {
        if (this.editText_search != null) {
            this.editText_search.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyQuery(s.toString());
                }
            });

            this.editText_search.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editText_search.getWindowToken(), 0);
                    return true;
                }

                return false;
            });
        }
    }

    /**
     * Filter country list for given keyWord / query.
     * Lists all countries that contains @param query in country's name, name code or phone code.
     *
     * @param query : text to match against country name, name code or phone code
     */
    private void applyQuery(String query) {
        query = query.toLowerCase();
        isSearchMode = !TextUtils.isEmpty(query);

        if (query.length() > 0 && query.charAt(0) == '+') {
            query = query.substring(1);
        }

        filteredCountries = getFilteredCountries(query);
        notifyDataSetChanged();
    }

    private List<CCPCountry> getFilteredCountries(String query) {
        List<CCPCountry> tempCCPCountryList = new ArrayList<>();
        if (codePicker.preferredCountries != null && codePicker.preferredCountries.size() > 0) {
            for (CCPCountry ccpCountry : codePicker.preferredCountries) {
                if (ccpCountry.isEligibleForQuery(query)) {
                    tempCCPCountryList.add(ccpCountry);
                }
            }

            if (tempCCPCountryList.size() > 0) { //means at least one preferred country is added.
//                CCPCountry divider = null;
                tempCCPCountryList.add(null);
            }
        }

        for (CCPCountry ccpCountry : masterCountries) {
            if (ccpCountry.isEligibleForQuery(query)) {
                ccpCountry.isSelect = ccpCountry.name.toLowerCase().equals(codePicker.getSelectedCountryName().toLowerCase());
                tempCCPCountryList.add(ccpCountry);
            }
        }
        return tempCCPCountryList;
    }

    @NonNull
    @Override
    public CountryCodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        ItemSelectFullImgBinding fullBinding =
                ItemSelectFullImgBinding.inflate(layoutInflater, viewGroup, false);
        return new CountryCodeViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryCodeViewHolder holder, final int position) {
        CCPCountry ccpCountry = filteredCountries.get(position);
        if (ccpCountry != null) {
            holder.binding.tvTitle.setVisibility(View.VISIBLE);
            holder.binding.imgFlag.setImageResource(ccpCountry.getFlagID());
            holder.binding.tvTitle.setText(ccpCountry.getName());
//            if (isSearchMode) {
//                if (position == 0) {
//                    holder.tvHeading.setVisibility(View.VISIBLE);
//                    holder.tvHeading.setText(R.string.all_types);
//                } else {
//                    holder.tvHeading.setVisibility(View.GONE);
//                }
//            } else {
//                if (position == 0) {
//                    holder.tvHeading.setVisibility(View.VISIBLE);
//                    holder.tvHeading.setText(R.string.most_popular);
//                } else if (position == 6) {
//                    holder.tvHeading.setVisibility(View.VISIBLE);
//                    holder.tvHeading.setText(R.string.all_types);
//                } else {
//                    holder.tvHeading.setVisibility(View.GONE);
//                }
//            }
            if (ccpCountry.isSelect()) {
                holder.binding.linView.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_button_bg));
                holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
                Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.SFTEXT_BOLD);
                holder.binding.tvTitle.setTypeface(tf);
            } else {
                holder.binding.linView.setBackgroundColor(Color.TRANSPARENT);
                holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.black));
                Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.SFTEXT_REGULAR);
                holder.binding.tvTitle.setTypeface(tf);
            }

            holder.binding.linView.setOnClickListener(view -> {
                clearSelected();
                filteredCountries.get(position).setSelect(true);
                notifyDataSetChanged();
            });
        }
    }

    public void clearSelected() {
        if (filteredCountries != null && filteredCountries.size() > 0) {
            for (CCPCountry ccpCountry : filteredCountries) {
                ccpCountry.setSelect(false);
            }
        }
    }

    private CCPCountry getSelectedItem() {
        if (filteredCountries != null && filteredCountries.size() > 0) {
            for (CCPCountry ccpCountry : filteredCountries) {
                if (ccpCountry.isSelect()) {
                    return ccpCountry;
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return filteredCountries.size();
    }

    class CountryCodeViewHolder extends RecyclerView.ViewHolder {

        ItemSelectFullImgBinding binding;

        public CountryCodeViewHolder(ItemSelectFullImgBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}

