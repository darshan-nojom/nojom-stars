package com.nojom.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nojom.R;
import com.nojom.model.Portfolios;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;


import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_VERTICAL_IMAGE = 0;
    private static final int TYPE_SQUARE_IMAGE = 1;

    private List<Portfolios> mDataset;
    private BaseActivity activity;

    public CustomAdapter(BaseActivity context, List<Portfolios> objects) {
        this.mDataset = objects;
        activity = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return TYPE_VERTICAL_IMAGE;
        } else {
            return TYPE_SQUARE_IMAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_VERTICAL_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical_image, parent, false);
            return new VerticalImageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_square_image, parent, false);
            return new SquareImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == TYPE_VERTICAL_IMAGE && position <= mDataset.size()) {
            Portfolios item = mDataset.get(position);
            // Bind data for vertical image
            VerticalImageViewHolder verticalImageViewHolder = (VerticalImageViewHolder) holder;
            // verticalImageViewHolder.verticalImage.setImageResource(...);

            if (item != null && item.filename != null) {
                if (TextUtils.isEmpty(item.getName(activity.language))) {
                    verticalImageViewHolder.relCompany.setVisibility(View.GONE);
                } else {
                    verticalImageViewHolder.relCompany.setVisibility(View.VISIBLE);
                }
                verticalImageViewHolder.txtCompany.setText(item.getName(activity.language));

                Glide.with(activity).load(activity.getPortfolioUrl() + item.filename)
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(verticalImageViewHolder.verticalImage);

                Glide.with(activity).load(/*activity.getPortfolioUrl() +*/ item.company_filename)
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(verticalImageViewHolder.imgCompany);


                if (item.filename.endsWith(".mp4") || item.filename.endsWith(".MP4")
                        || item.filename.endsWith(".MOV") || item.filename.endsWith(".mov")
                        || item.filename.endsWith(".AVI") || item.filename.endsWith(".avi")
                ) {

                    verticalImageViewHolder.imgPlay.setVisibility(View.VISIBLE);
                    verticalImageViewHolder.view.setVisibility(View.VISIBLE);
                } else {
                    verticalImageViewHolder.imgPlay.setVisibility(View.GONE);
                    verticalImageViewHolder.view.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getPortfolioUrl() + item.filename));
                    activity.startActivity(intent);
                });
            }
        } else {
            // Bind data for square image
            SquareImageViewHolder squareImageViewHolder = (SquareImageViewHolder) holder;
            // squareImageViewHolder.squareImage.setImageResource(...);

            if (mDataset != null && mDataset.get(position).data != null
                    && mDataset.get(position).data.size() > 0) {

                if (TextUtils.isEmpty(mDataset.get(position).data.get(0).getName(activity.language))) {
                    squareImageViewHolder.relCompany.setVisibility(View.GONE);
                } else {
                    squareImageViewHolder.relCompany.setVisibility(View.VISIBLE);
                }
                squareImageViewHolder.txtCompany.setText(mDataset.get(position).data.get(0).getName(activity.language));

                Glide.with(activity).load(/*activity.getPortfolioUrl() +*/ mDataset.get(position).data.get(0).company_filename)
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(squareImageViewHolder.imgCompany);

                Glide.with(activity).load(activity.getPortfolioUrl() + mDataset.get(position).data.get(0).filename)
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(squareImageViewHolder.squareImage);
                if (mDataset.get(position).data.get(0).filename.endsWith(".mp4") || mDataset.get(position).data.get(0).filename.endsWith(".MP4")
                        || mDataset.get(position).data.get(0).filename.endsWith(".MOV") || mDataset.get(position).data.get(0).filename.endsWith(".mov")
                        || mDataset.get(position).data.get(0).filename.endsWith(".AVI") || mDataset.get(position).data.get(0).filename.endsWith(".avi")
                ) {

                    squareImageViewHolder.imgPlay.setVisibility(View.VISIBLE);
                    squareImageViewHolder.view.setVisibility(View.VISIBLE);
                } else {
                    squareImageViewHolder.imgPlay.setVisibility(View.GONE);
                    squareImageViewHolder.view.setVisibility(View.GONE);
                }

                squareImageViewHolder.squareImage.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getPortfolioUrl() + mDataset.get(position).data.get(0).filename));
                    activity.startActivity(intent);
                });

                if (mDataset.get(position).data.size() > 1) {
                    squareImageViewHolder.squareImage2.setVisibility(View.VISIBLE);

                    if (TextUtils.isEmpty(mDataset.get(position).data.get(1).getName(activity.language))) {
                        squareImageViewHolder.relCompany2.setVisibility(View.GONE);
                    } else {
                        squareImageViewHolder.relCompany2.setVisibility(View.VISIBLE);
                    }
                    squareImageViewHolder.txtCompany2.setText(mDataset.get(position).data.get(1).getName(activity.language));

                    Glide.with(activity).load(/*activity.getPortfolioUrl() +*/ mDataset.get(position).data.get(1).company_filename)
                            .placeholder(R.mipmap.ic_launcher)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(squareImageViewHolder.imgCompany2);

                    Glide.with(activity).load(activity.getPortfolioUrl() + mDataset.get(position).data.get(1).filename)
                            .placeholder(R.mipmap.ic_launcher)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(squareImageViewHolder.squareImage2);

                    if (mDataset.get(position).data.get(1).filename.endsWith(".mp4") || mDataset.get(position).data.get(1).filename.endsWith(".MP4")
                            || mDataset.get(position).data.get(1).filename.endsWith(".MOV") || mDataset.get(position).data.get(1).filename.endsWith(".mov")
                            || mDataset.get(position).data.get(1).filename.endsWith(".AVI") || mDataset.get(position).data.get(1).filename.endsWith(".avi")
                    ) {
                        squareImageViewHolder.imgPlay1.setVisibility(View.VISIBLE);
                        squareImageViewHolder.view1.setVisibility(View.VISIBLE);
                    } else {
                        squareImageViewHolder.imgPlay1.setVisibility(View.GONE);
                        squareImageViewHolder.view1.setVisibility(View.GONE);
                    }

                    squareImageViewHolder.squareImage2.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getPortfolioUrl() + mDataset.get(position).data.get(1).filename));
                        activity.startActivity(intent);
                    });
                } else {
                    squareImageViewHolder.squareImage2.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
//        return (mDataset.size() / 3) * 2 + (mDataset.size() % 3 == 0 ? 0 : 1);
//        return (mDataset.size() / 2) * 3 + (mDataset.size() % 2 == 0 ? 0 : 2);
    }


    public class SquareImageViewHolder extends RecyclerView.ViewHolder {
        ImageView squareImage, squareImage2, imgPlay, imgPlay1, view, view1;
        RoundedImageView imgCompany,imgCompany2;
        TextView txtCompany,txtCompany2;
        RelativeLayout relCompany,relCompany2;
        //        RelativeLayout relCompany;
        public SquareImageViewHolder(View itemView) {
            super(itemView);
            squareImage = itemView.findViewById(R.id.square_image);
            squareImage2 = itemView.findViewById(R.id.square_image_2);
            imgPlay = itemView.findViewById(R.id.img_play);
            imgPlay1 = itemView.findViewById(R.id.img_play1);
            view = itemView.findViewById(R.id.view);
            view1 = itemView.findViewById(R.id.view1);
//            relCompany = itemView.findViewById(R.id.rel_company);
            imgCompany = itemView.findViewById(R.id.img_company);
            txtCompany = itemView.findViewById(R.id.tv_chat);
            relCompany = itemView.findViewById(R.id.rel_company);

            imgCompany2 = itemView.findViewById(R.id.img_company_2);
            txtCompany2 = itemView.findViewById(R.id.tv_chat_2);
            relCompany2 = itemView.findViewById(R.id.rel_company_2);

            if (activity.language.equals("ar")) {
                activity.setArFont(txtCompany, Constants.FONT_AR_REGULAR);
                activity.setArFont(txtCompany2, Constants.FONT_AR_REGULAR);
            }
        }
    }

    public class VerticalImageViewHolder extends RecyclerView.ViewHolder {
        ImageView verticalImage, imgPlay, view;
        RoundedImageView imgCompany;
        TextView txtCompany;
        RelativeLayout relCompany;

        public VerticalImageViewHolder(View itemView) {
            super(itemView);
            verticalImage = itemView.findViewById(R.id.vertical_image);
            imgCompany = itemView.findViewById(R.id.img_company);
            imgPlay = itemView.findViewById(R.id.img_play);
            view = itemView.findViewById(R.id.view);
            txtCompany = itemView.findViewById(R.id.tv_chat);
            relCompany = itemView.findViewById(R.id.rel_company);

            if (activity.language.equals("ar")) {
                activity.setArFont(txtCompany, Constants.FONT_AR_REGULAR);
            }

            /*itemView.setOnClickListener(v -> {
                if (mDataset.get(getAdapterPosition()).data.get(0).filename.endsWith(".mp4") || mDataset.get(getAdapterPosition()).data.get(0).filename.endsWith(".MP4")
                        || mDataset.get(getAdapterPosition()).data.get(0).filename.endsWith(".MOV") || mDataset.get(getAdapterPosition()).data.get(0).filename.endsWith(".mov")
                        || mDataset.get(getAdapterPosition()).data.get(0).filename.endsWith(".AVI") || mDataset.get(getAdapterPosition()).data.get(0).filename.endsWith(".avi")
                ) {
                    //play video
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getPortfolioUrl() + mDataset.get(getBindingAdapterPosition()).filename));
                    activity.startActivity(intent);
                } else {
                    //view image
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getPortfolioUrl() + mDataset.get(getBindingAdapterPosition()).filename));
                    activity.startActivity(intent);
                }
            });*/
        }
    }
}
