package com.nojom.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemPortfolioListBinding;
import com.nojom.model.Portfolios;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.PortfolioActivity;
import com.nojom.util.Utils;

import java.util.List;

public class PortfolioListAdapter extends RecyclerView.Adapter<PortfolioListAdapter.SimpleViewHolder> {

    private List<Portfolios> mDataset;
    private BaseActivity activity;

    public PortfolioListAdapter(BaseActivity context, List<Portfolios> objects) {
        this.mDataset = objects;
        activity = context;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemPortfolioListBinding listFilesBinding =
                ItemPortfolioListBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(listFilesBinding);
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).id;
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        Portfolios item = mDataset.get(position);

        holder.binding.txtTitle.setText(item.title);
        if (item.portfolioFiles != null && item.portfolioFiles.size() > 0) {
            holder.binding.imgRight.setVisibility(View.VISIBLE);
            holder.binding.txtPage.setText(String.format(activity.getString(R.string.one_item), Utils.nFormate(item.portfolioFiles.size())));

            PortfolioPagerAdapter myCustomPagerAdapter = null;
            if (item.portfolioFiles != null && item.portfolioFiles.size() > 0) {
                myCustomPagerAdapter = new PortfolioPagerAdapter(activity, item.portfolioFiles);
                holder.binding.viewpager.setAdapter(myCustomPagerAdapter);
            }

            holder.binding.imgLeft.setOnClickListener(v -> {
                try {
                    holder.binding.imgRight.setVisibility(View.VISIBLE);

                    holder.binding.viewpager.setCurrentItem(holder.binding.viewpager.getCurrentItem() - 1);
                    holder.binding.txtPage.setText(String.format(holder.binding.viewpager.getCurrentItem() + 1 + " / %s", Utils.nFormate(item.portfolioFiles.size())));

                    if (holder.binding.viewpager.getCurrentItem() == 0) {
                        holder.binding.imgLeft.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            PortfolioPagerAdapter finalMyCustomPagerAdapter = myCustomPagerAdapter;
            holder.binding.imgRight.setOnClickListener(v -> {
                try {
                    holder.binding.imgLeft.setVisibility(View.VISIBLE);

                    holder.binding.viewpager.setCurrentItem(holder.binding.viewpager.getCurrentItem() + 1);
                    holder.binding.txtPage.setText(String.format(holder.binding.viewpager.getCurrentItem() + 1 + " / %s", Utils.nFormate(item.portfolioFiles.size())));

                    if (finalMyCustomPagerAdapter != null && holder.binding.viewpager.getCurrentItem() == finalMyCustomPagerAdapter.getCount() - 1) {
                        holder.binding.imgRight.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            if (item.portfolioFiles.size() == 1) {
                holder.binding.imgLeft.setVisibility(View.GONE);
                holder.binding.imgRight.setVisibility(View.GONE);
            }

        } else {
            holder.binding.imgLeft.setVisibility(View.GONE);
            holder.binding.imgRight.setVisibility(View.GONE);
            holder.binding.txtPage.setText(activity.getString(R.string.zero_zero));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public List<Portfolios> getData() {
        return mDataset;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemPortfolioListBinding binding;

        SimpleViewHolder(ItemPortfolioListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(activity, PortfolioActivity.class);
                intent.putExtra("portfolio", mDataset.get(getAdapterPosition()));
                intent.putExtra("flag", true);
                activity.startActivityForResult(intent, 121);
            });
        }
    }
}
