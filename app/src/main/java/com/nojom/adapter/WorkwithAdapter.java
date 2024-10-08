package com.nojom.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ItemWorkwithBinding;
import com.nojom.model.GetAgentCompanies;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WorkwithAdapter extends RecyclerView.Adapter<WorkwithAdapter.SimpleViewHolder> {

    private BaseActivity context;
    public String path;
    private List<GetAgentCompanies.Data> paymentList;

    public GetAgentCompanies.Data getData(int pos) {
        return paymentList.get(pos);
    }

    public WorkwithAdapter(BaseActivity context) {
        this.context = context;
    }

    public void doRefresh(List<GetAgentCompanies.Data> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }

    public void doRefresh(String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemWorkwithBinding itemAccountBinding = ItemWorkwithBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        GetAgentCompanies.Data item = paymentList.get(position);

        holder.binding.txtName.setText(item.getName(context.language));

        String text = item.times + " " + context.getString(R.string.cooperations);

        holder.binding.txtUname.setText(applyStyle(text));
        holder.binding.txtContract.setText(context.getString(R.string.contract));

        if (item.contract_start_date != null) {
            holder.binding.txtContract.setVisibility(View.VISIBLE);
            holder.binding.linCDate.setVisibility(View.VISIBLE);
            holder.binding.txtDate.setVisibility(View.GONE);
            if (item.contract_start_date != null) {
                String campDate = item.contract_start_date;
                String campDateEnd = item.contract_end_date;

                SimpleDateFormat inputFormat = new SimpleDateFormat("M/yyyy"); // Single 'M' to handle months without leading zero
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM-yyyy");

                try {
                    // Parse the input date string to a Date object
                    Date dateS = inputFormat.parse(campDate);
                    Date dateE = inputFormat.parse(campDateEnd);

                    // Format the Date object to the desired output format
                    String outputDateS = null;
                    if (dateS != null) {
                        outputDateS = outputFormat.format(dateS);
                    }
                    String outputDateE = null;
                    if (dateE != null) {
                        outputDateE = outputFormat.format(dateE);
                    }

                    // Print the result
                    holder.binding.txtDateC.setText(outputDateS);
                    holder.binding.txtDateEndC.setText(outputDateE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (item.campaign_date != null && item.campaign_date.contains("T")) {
                String[] campDate = item.campaign_date.split("T");
                holder.binding.txtDate.setVisibility(View.VISIBLE);

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd"); // Single 'M' to handle months without leading zero
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM-yyyy");

                try {
                    // Parse the input date string to a Date object
                    Date dateS = inputFormat.parse(campDate[0]);

                    // Format the Date object to the desired output format
                    String outputDateS = null;
                    if (dateS != null) {
                        outputDateS = outputFormat.format(dateS);
                    }

                    holder.binding.txtDate.setText(outputDateS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            holder.binding.txtContract.setVisibility(View.GONE);
            holder.binding.linCDate.setVisibility(View.GONE);
        }


        Glide.with(context).load(path + item.filename).placeholder(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.binding.imgProfile);

    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemWorkwithBinding binding;

        SimpleViewHolder(ItemWorkwithBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (context.language.equals("ar")) {
                context.setArFont(binding.txtContract, Constants.FONT_AR_REGULAR);
                context.setArFont(binding.txtName, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtUname, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtDate, Constants.FONT_AR_BOLD);
                context.setArFont(binding.txtDateC, Constants.FONT_AR_BOLD);
                context.setArFont(binding.txtDateEndC, Constants.FONT_AR_BOLD);
            }
            binding.txtContract.setBackground(context.getResources().getDrawable(R.drawable.gray_button_bg_top_bottom));
            binding.txtContract.setTextColor(context.getResources().getColor(R.color.c_080921));
            itemView.getRoot().setOnClickListener(v -> {
//                Intent i = new Intent(context, PaymentActivity.class);
//                i.putExtra(Constants.ACCOUNT_DATA, paymentList.get(getAdapterPosition()));
//                context.startActivity(i);
            });
        }
    }

    private SpannableString applyStyle(String text) {
        // Find the position of the first non-digit character
        int firstNonDigitIndex = 0;
        while (firstNonDigitIndex < text.length() && Character.isDigit(text.charAt(firstNonDigitIndex))) {
            firstNonDigitIndex++;
        }

        // Create a SpannableString
        SpannableString spannableString = new SpannableString(text);

        // Apply color to the digit part
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, firstNonDigitIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }
}
