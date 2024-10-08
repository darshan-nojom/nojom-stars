package com.nojom.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nojom.R;
import com.nojom.databinding.ItemSocialChannelBinding;
import com.nojom.model.GigCategoryModel;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyPlatformAdapter extends RecyclerView.Adapter<MyPlatformAdapter.SimpleViewHolder> {

    private List<SocialPlatformResponse.Data> mDatasetFiltered;
    private BaseActivity context;
    private OnClickPlatformListener onClickPlatformListener;
    private GigCategoryModel.Data selectedPosData;

    // Initializing a String Array
    String[] followers;

    final List<String> followersList;
    private ArrayAdapter<String> spinnerArrayAdapter;

    public interface OnClickPlatformListener {
        void onClickPlatform(SocialPlatformResponse.Data platform);
    }

    public MyPlatformAdapter(BaseActivity context, ArrayList<SocialPlatformResponse.Data> objects, OnClickPlatformListener listener) {
        this.mDatasetFiltered = objects;
        this.context = context;
        this.onClickPlatformListener = listener;

        followers = new String[]{context.getString(R.string.select_followers), "1k - 10k", "10k - 50k", "50k - 100k", "100k - 500k", "500k - 1M", "1M - 10M", "10M+"};
        followersList = new ArrayList<>(Arrays.asList(followers));
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSocialChannelBinding fullBinding = ItemSocialChannelBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            SocialPlatformResponse.Data item = mDatasetFiltered.get(position);
            holder.binding.tvCategory.setText(item.getName(context.language));

            Glide.with(context).load(Uri.parse(item.platformIcon)).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.imgCheckUncheck);

            if (item.isSelected) {
                holder.binding.linSocial.setVisibility(View.VISIBLE);
                holder.binding.tvCategory.setVisibility(View.GONE);
            } else {
                holder.binding.linSocial.setVisibility(View.GONE);
                holder.binding.tvCategory.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(item.username)) {
                holder.binding.etUsername.setText(item.username);
            }

            holder.binding.etUsername.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mDatasetFiltered.get(holder.getAdapterPosition()).username = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            spinnerArrayAdapter = new ArrayAdapter<String>(context, R.layout.layout_textview, followersList) {
                @Override
                public boolean isEnabled(int position) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return position != 0;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(R.id.text);
                    tv.setText(item.getName(context.language));

                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };

            context.runOnUiThread(() -> {


//                spinnerArrayAdapter = new ArrayAdapter<String>(context, R.layout.layout_textview, R.id.text, followersList);
//                holder.binding.spinnerFollowers.setAdapter(spinnerArrayAdapter);

//                /*nnerArrayAdapter = new ArrayAdapter(context, R.layout.layout_textview, R.id.text, followersList) {
//                    @Override
//                    public View getView(int position, View convertView, ViewGroup parent) {
//                        View view = super.getView(position, convertView, parent);
////                        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
//
////                        text1.setText(item.name);
////                        text1.setTextColor(Color.BLACK);
//                        return view;
//                    }
//                };*/

//                spinnerArrayAdapter.setDropDownViewResource(R.layout.layout_textview);

                SpinnerDropDownAdapter sddadapter = new SpinnerDropDownAdapter(context, followersList);
                holder.binding.spinnerFollowers.setAdapter(sddadapter);

                holder.binding.spinnerFollowers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                        String selectedItemText = (String) parent.getSelectedItem();
                        int selectedItemId = (int) parent.getSelectedItemId();
                        // If user change the default selection
                        // First item is disable and it is used for hint
//                        if (position1 > 0) {
                        mDatasetFiltered.get(holder.getAdapterPosition()).followers = Utils.getPlatformId(selectedItemText,context);
                        mDatasetFiltered.get(holder.getAdapterPosition()).platformFollower = selectedItemText;
//                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if (item.followers > 0) {
                    holder.binding.spinnerFollowers.setSelection(item.followers);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

    public List<SocialPlatformResponse.Data> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSocialChannelBinding binding;

        public SimpleViewHolder(ItemSocialChannelBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.tvCategory.setOnClickListener(v -> {
//                for (int i = 0; i < mDatasetFiltered.size(); i++) {
//                    mDatasetFiltered.get(i).isSelected = mDatasetFiltered.get(i).username != null && !mDatasetFiltered.get(i).username.isEmpty();
//                }
                mDatasetFiltered.get(getAdapterPosition()).isSelected = !(mDatasetFiltered.get(getAdapterPosition()).username != null && !mDatasetFiltered.get(getAdapterPosition()).username.isEmpty());

                notifyDataSetChanged();
            });
        }
    }

    public SocialPlatformResponse.Data getSelectedCategory() {
        for (SocialPlatformResponse.Data data : mDatasetFiltered) {
            if (data.isSelected) {
                return data;
            }
        }
        return null;
    }

    public static class SpinnerDropDownAdapter extends BaseAdapter implements SpinnerAdapter {
        BaseActivity context;
        List<String> values;

        SpinnerDropDownAdapter(BaseActivity ctx, List<String> values) {
            context = ctx;
            this.values = values;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public String getItem(int pos) {
            // TODO Auto-generated method stub
            return values.get(pos);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text = new TextView(context);
            text.setTextColor(Color.BLACK);
            text.setText(values.get(position));
            return text;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            int padding = (int) context.getResources().getDimension(R.dimen._5sdp);
            TextView text = new TextView(context);
            text.setTextColor(Color.BLACK);
            text.setPadding(padding, padding, padding, padding);
            text.setTextSize(context.getResources().getDimension(R.dimen._4sdp));
            text.setText(values.get(position));
            return text;
        }
    }
}
