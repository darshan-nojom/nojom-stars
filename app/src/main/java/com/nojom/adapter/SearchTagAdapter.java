package com.nojom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.nojom.R;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchTagAdapter extends ArrayAdapter<GigSubCategoryModel.Data> {

    Context context;
    int resource, textViewResourceId;
    List<GigSubCategoryModel.Data> items, tempItems, suggestions;

    public SearchTagAdapter(Context context, int resource, int textViewResourceId, List<GigSubCategoryModel.Data> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<>(items); // this makes the difference.
        suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_textview, parent, false);
        }
        GigSubCategoryModel.Data skill = items.get(position);
        if (skill != null) {
            TextView lblName = view.findViewById(R.id.text);
            if (lblName != null)
                lblName.setText(skill.getName(((BaseActivity) context).language));
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((GigSubCategoryModel.Data) resultValue).getName(((BaseActivity) context).language);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (GigSubCategoryModel.Data people : tempItems) {
                    if (people.name.toLowerCase().contains(constraint.toString().toLowerCase())
                            || people.getName(((BaseActivity) context).language).contains(constraint.toString())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<GigSubCategoryModel.Data> filterList = (ArrayList<GigSubCategoryModel.Data>) results.values;
            if (results.count > 0) {
                clear();
                for (GigSubCategoryModel.Data people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}