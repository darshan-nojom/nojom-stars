package com.nojom.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ItemGigPhotosBinding;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;

public class GigPhotosAdapter extends RecyclerView.Adapter<GigPhotosAdapter.SimpleViewHolder> {

    private ArrayList<ImageFile> mDataSet;
    private BaseActivity activity;
    private int noOfSelection;
    private OnClickPhotoListener onClickPhotoListener;

    public void setNoOfSelection(int noOfSelection) {
        this.noOfSelection = noOfSelection;
    }

    public interface OnClickPhotoListener {
        void refreshView(int noOfSelection);

        void onClickImage(ImageFile imageFile, int adapterPos);
    }

    public GigPhotosAdapter(BaseActivity gigPhotosActivity, ArrayList<ImageFile> gigImages, OnClickPhotoListener onClickPhotoListener) {
        activity = gigPhotosActivity;
        mDataSet = gigImages;
        this.onClickPhotoListener = onClickPhotoListener;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemGigPhotosBinding hireGridItemBinding =
                ItemGigPhotosBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(hireGridItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        ImageFile item = mDataSet.get(position);

        if (position == 0) {
            holder.binding.tvPrimary.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvPrimary.setVisibility(View.GONE);
        }

        if (item.isSelected) {
            holder.binding.imgCheckUncheck.setImageResource(R.drawable.check_done);
            holder.binding.imgCheckUncheck.clearColorFilter();

        } else {
            holder.binding.imgCheckUncheck.setImageResource(R.drawable.circle_uncheck);
            holder.binding.imgCheckUncheck.setColorFilter(ContextCompat.getColor(activity,
                    R.color.full_dark_green), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        try {
            String imgUrl = item.getPath().startsWith("http") ? item.getPath() : "file://" + item.getPath();
            Glide.with(activity).load(imgUrl)
                    .placeholder(R.color.black)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    })
                    .into(holder.binding.img);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        private ItemGigPhotosBinding binding;

        SimpleViewHolder(ItemGigPhotosBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.imgCheckUncheck.setOnClickListener(v -> {
                try {

                    mDataSet.get(getAdapterPosition()).isSelected = !mDataSet.get(getAdapterPosition()).isSelected;

//                    notifyDataSetChanged();
                    notifyItemChanged(getAdapterPosition());

                    noOfSelection = 0;
                    for (ImageFile file : mDataSet) {
                        if (file.isSelected) {
                            noOfSelection++;
                        }
                    }

                    if (onClickPhotoListener != null) {
                        onClickPhotoListener.refreshView(noOfSelection);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            itemView.getRoot().setOnClickListener(v -> {
                if (onClickPhotoListener != null && getAdapterPosition() != 0) {
                    onClickPhotoListener.onClickImage(mDataSet.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public void doRefresh(ArrayList<ImageFile> gigImages) {
        mDataSet = gigImages;
        notifyDataSetChanged();
    }


    public ArrayList<ImageFile> getFiles() {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }
        return mDataSet;
    }

    public JSONArray deletedPhotoIds() {
        JSONArray jsonArray = new JSONArray();
        Iterator<ImageFile> iterator = mDataSet.iterator();
        while (iterator.hasNext()) {
            ImageFile file = iterator.next();
            if (file.isSelected) {
                jsonArray.put(file.getId());
            }
        }
        return jsonArray;
    }

    public void deletePhotos() {
        Iterator<ImageFile> iterator = mDataSet.iterator();
        while (iterator.hasNext()) {
            ImageFile file = iterator.next();
            if (file.isSelected) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    public JSONArray deletePhotosForDuplicate() {
        JSONArray jsonArray = new JSONArray();
        Iterator<ImageFile> iterator = mDataSet.iterator();
        while (iterator.hasNext()) {
            ImageFile file = iterator.next();
            if (file.isSelected) {
                jsonArray.put(file.getId());
                iterator.remove();
            }
        }
        notifyDataSetChanged();
        return jsonArray;
    }
}
