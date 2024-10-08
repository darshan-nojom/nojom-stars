package com.nojom.adapter;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ItemCompanyBinding;
import com.nojom.model.GetCompanies;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class PortfolioCompanyAdapter extends RecyclerView.Adapter<PortfolioCompanyAdapter.SimpleViewHolder>
        implements Filterable {

    private BaseActivity context;
    private List<GetCompanies.Data> companyList, mOriginalValues;
    private ArrayFilter mFilter;
    private CompanyListener companyListener;

    public PortfolioCompanyAdapter(BaseActivity context, CompanyListener listener) {
        this.context = context;
        this.companyListener = listener;
    }

    public void doRefresh(List<GetCompanies.Data> paymentList) {
        this.companyList = paymentList;
        this.mOriginalValues = paymentList;
        notifyDataSetChanged();
    }

    private boolean isAddCompany = true;

    public void addCompany(boolean b) {
        isAddCompany = b;
    }

    public interface CompanyListener {
        void onClickCompany(GetCompanies.Data data, ImageView imageView);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemCompanyBinding itemAccountBinding =
                ItemCompanyBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {

        GetCompanies.Data data = companyList.get(position);

        if (position == getItemCount()-1) {
            holder.binding.tvName.setText(context.getString(R.string.add_new_company));
            holder.binding.tvName.setTag(11 + "");

            holder.binding.tvName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
//            holder.binding.tvName.setTypeface(holder.binding.tvName.getTypeface(), Typeface.BOLD);
            holder.binding.imgArrow.setImageResource(R.drawable.add_circle);

        } else {
            holder.binding.tvName.setText(data.getName(context.language));
            holder.binding.tvName.setTag(data.id + "");
//            holder.binding.tvName.setTypeface(holder.binding.tvName.getTypeface(), Typeface.NORMAL);
            holder.binding.tvName.setTextColor(context.getResources().getColor(R.color.c_080921));
            Glide.with(context).load(path + companyList.get(position).filename).placeholder(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
            }).into(holder.binding.imgArrow);
        }
    }

    @Override
    public int getItemCount() {
        if (companyList != null) {
            return Math.min(companyList.size(), 5);
//            companyList.add(new GetCompanies.Data(context.getString(R.string.add_new_company),
//                    context.getString(R.string.add_new_company), "", "", -11, -1));
        }
        return 0;
    }

    String path;

    public void setPath(String path) {
        this.path = path;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemCompanyBinding binding;

        SimpleViewHolder(ItemCompanyBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (context.language.equals("ar")) {
                context.setArFont(binding.tvName, Constants.FONT_AR_REGULAR);
            }
            itemView.getRoot().setOnClickListener(v -> {
                if (companyListener != null) {
                    if (getAdapterPosition() == getItemCount()-1) {
                        companyListener.onClickCompany(new GetCompanies.Data(context.getString(R.string.add_new_company),
                                context.getString(R.string.add_new_company), "", "", -11, -1), binding.imgArrow);
                    } else {
                        companyListener.onClickCompany(companyList.get(getAdapterPosition()), binding.imgArrow);
                    }
                }
            });
        }
    }

    private class ArrayFilter extends Filter {
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<>(companyList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<GetCompanies.Data> list = new ArrayList<>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
                companyList = mOriginalValues;
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<GetCompanies.Data> values = (ArrayList<GetCompanies.Data>) mOriginalValues;
                int count = values.size();

                ArrayList<GetCompanies.Data> newValues = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    GetCompanies.Data item = values.get(i);
                    if (item.getName(context.language).toLowerCase().contains(prefixString)) {
                        newValues.add(item);
                    }
                }
                if (isAddCompany) {
                    newValues.add(new GetCompanies.Data(context.getString(R.string.add_new_company),
                            context.getString(R.string.add_new_company), "", "", -11, -1));
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if (results.values != null) {
                companyList = (ArrayList<GetCompanies.Data>) results.values;
            } else {
                companyList = new ArrayList<>();
            }
//            if (results.count > 0) {
            notifyDataSetChanged();
//            } else {
//                notifyDataSetInvalidated();
//            }
        }
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }
}
