package com.nojom.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.model.GetCompanies;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class CompanyAdapter extends ArrayAdapter<GetCompanies.Data> implements Filterable {

    private ArrayList<GetCompanies.Data> fullList;
    private ArrayList<GetCompanies.Data> mOriginalValues;
    private ArrayFilter mFilter;
    private BaseActivity activity;
    private String path;

    public CompanyAdapter(BaseActivity context, int resource, int textViewResourceId, List<GetCompanies.Data> objects) {
        super(context, resource, textViewResourceId, objects);
        this.activity = context;
        fullList = (ArrayList<GetCompanies.Data>) objects;
        mOriginalValues = new ArrayList<>(fullList);
    }

    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public GetCompanies.Data getItem(int position) {
        return fullList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_company, parent,
                false);
        TextView nameView = rowView.findViewById(R.id.tv_name);
        ImageView img = rowView.findViewById(R.id.img_arrow);
        GetCompanies.Data data = fullList.get(position);

        nameView.setText(data.getName(activity.language));
        nameView.setTag(data.id + "");

        Glide.with(activity).load(path + fullList.get(position).filename).placeholder(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
        }).into(img);

        return rowView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    public void setPath(String path) {
        this.path = path;
    }


    private class ArrayFilter extends Filter {
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<>(fullList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<GetCompanies.Data> list = new ArrayList<>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<GetCompanies.Data> values = mOriginalValues;
                int count = values.size();

                ArrayList<GetCompanies.Data> newValues = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    GetCompanies.Data item = values.get(i);
                    if (item.getName(activity.language).toLowerCase().contains(prefixString)) {
                        newValues.add(item);
                    }
                }
                newValues.add(new GetCompanies.Data(activity.getString(R.string.add_new_company), activity.getString(R.string.add_new_company), "", "", -1, -1));
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if (results.values != null) {
                fullList = (ArrayList<GetCompanies.Data>) results.values;
            } else {
                fullList = new ArrayList<>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
