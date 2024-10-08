package com.nojom.adapter;

import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nojom.R;
import com.nojom.databinding.ItemSocialMediaBinding;
import com.nojom.model.SocialMediaResponse;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.SocialMediaActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class SocialMediaAdapter extends RecyclerView.Adapter<SocialMediaAdapter.SimpleViewHolder> implements Filterable {

    private List<SocialMediaResponse.Data> mDatasetFiltered;
    private List<SocialMediaResponse.Data> mDataset;
    private List<SocialMediaResponse.Data> mDatasetOrijinal;
    private SocialMediaActivity context;
    private OnClickPlatformListener onClickPlatformListener;
    SocialPlatformAdapter.PlatformAddedListener platformAddedListener;

    public void setListener(SocialPlatformAdapter.PlatformAddedListener platformAddedListener) {
        this.platformAddedListener = platformAddedListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    //clearSelectedDuplicate();
                    mDatasetFiltered = mDataset;
                } else {
                    List<SocialMediaResponse.Data> filteredList = new ArrayList<>();
//                    for (SocialMediaResponse.Data row : mDataset) {
//                        String rowText = row.name.toLowerCase();
//                        if (!TextUtils.isEmpty(rowText)) {
//                            if (rowText.contains(charString.toLowerCase())/* || row.name.equalsIgnoreCase("Other")*/) {
//                                filteredList.add(row);
//                            }
//                        }
//                    }


                    for (int i = 0; i < mDataset.size(); i++) {
                        for (SocialMediaResponse.SocialPlatform sp : mDataset.get(i).social_platforms) {
                            String rowText = sp.getName(context.language).toLowerCase();
                            if (!TextUtils.isEmpty(rowText)) {
                                if (rowText.contains(charString.toLowerCase())/* || row.name.equalsIgnoreCase("Other")*/) {
//                                    mDataset.get(i).social_platforms.clear();
//                                    mDataset.get(i).social_platforms.add(sp);
                                    SocialMediaResponse.Data data = new SocialMediaResponse.Data();
                                    data.social_platforms = new ArrayList<>();
                                    data.social_platforms.add(sp);
                                    data.id = mDataset.get(i).id;
                                    data.name = mDataset.get(i).name;
                                    data.nameAr = mDataset.get(i).nameAr;
                                    data.isSelected = mDataset.get(i).isSelected;
                                    filteredList.add(data);
                                    break;
                                }
                            }
                        }
                    }

                    mDatasetFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDatasetFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDatasetFiltered = (List<SocialMediaResponse.Data>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    boolean isFromSignupStep;

    public void setFromSignup(boolean isFromSignupStep) {
        this.isFromSignupStep = isFromSignupStep;
    }

    public interface OnClickPlatformListener {
        void onClickPlatform(SocialPlatformResponse.Data platform);
    }

    public SocialMediaAdapter(SocialMediaActivity context, ArrayList<SocialMediaResponse.Data> objects, OnClickPlatformListener listener) {
        this.mDatasetFiltered = objects;
        this.mDataset = objects;
        this.mDatasetOrijinal = objects;
        this.context = context;
        this.onClickPlatformListener = listener;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSocialMediaBinding fullBinding = ItemSocialMediaBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            SocialMediaResponse.Data item = mDatasetFiltered.get(position);
            holder.binding.tvTitle.setText(item.getName(context.language));

//            holder.binding.rvPlatform.setLayoutManager(new GridLayoutManager(context, 4));
            int spanCount = 4; // Number of columns
            int spacing = (int) context.getResources().getDimension(R.dimen._8sdp); // Spacing in pixels (you can convert from dp)
            boolean includeEdge = false; // Whether to include edge padding

            holder.binding.rvPlatform.setLayoutManager(new GridLayoutManager(context, spanCount));
            holder.binding.rvPlatform.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));


            if (item.social_platforms != null && item.social_platforms.size() > 2) {
                holder.binding.imgPlt1.setVisibility(View.VISIBLE);
                holder.binding.imgPlt2.setVisibility(View.VISIBLE);
                holder.binding.imgPlt3.setVisibility(View.VISIBLE);
                Glide.with(holder.binding.imgPlt1.getContext()).load(item.social_platforms.get(0).filename).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.imgPlt1);
                Glide.with(holder.binding.imgPlt2.getContext()).load(item.social_platforms.get(1).filename).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.imgPlt2);
                Glide.with(holder.binding.imgPlt3.getContext()).load(item.social_platforms.get(2).filename).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.imgPlt3);
            } else {
                holder.binding.imgPlt1.setVisibility(View.GONE);
                holder.binding.imgPlt2.setVisibility(View.GONE);
                holder.binding.imgPlt3.setVisibility(View.GONE);
            }
            SocialPlatformAdapter mAdapter = new SocialPlatformAdapter(context, item.social_platforms, platformAddedListener, item);
            mAdapter.setFromSignup(isFromSignupStep);
            holder.binding.rvPlatform.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

    public List<SocialMediaResponse.Data> getData() {
        return mDatasetFiltered;
    }


    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSocialMediaBinding binding;

        public SimpleViewHolder(ItemSocialMediaBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (context != null && context.language.equals("ar")) {
                context.setArFont(binding.tvTitle, Constants.FONT_AR_MEDIUM);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (binding.rvPlatform.isShown()) {
                    binding.imgArrow.setRotation(0);
                    binding.rvPlatform.setVisibility(View.GONE);
                } else {
                    binding.imgArrow.setRotation(180);
                    binding.rvPlatform.setVisibility(View.VISIBLE);
                }
            });
        }
    }

//    public SocialPlatformResponse.Data getSelectedCategory() {
//        for (SocialPlatformResponse.Data data : mDatasetFiltered) {
//            if (data.isSelected) {
//                return data;
//            }
//        }
//        return null;
//    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}

