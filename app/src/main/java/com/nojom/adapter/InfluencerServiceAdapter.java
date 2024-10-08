package com.nojom.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ItemInfServicesBinding;
import com.nojom.model.SocialPlatformList;
import com.nojom.ui.BaseActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;
import java.util.Locale;


public class InfluencerServiceAdapter extends RecyclerView.Adapter<InfluencerServiceAdapter.SimpleViewHolder> {
    private final BaseActivity baseActivity;
    private List<SocialPlatformList.Data> arrUserList;
    private String imgPath = "";
    private LayoutInflater layoutInflater;
    private final PrettyTime prettyTime = new PrettyTime();
    private OnClickService onClickService;

    public InfluencerServiceAdapter(Context context, List<SocialPlatformList.Data> objects, OnClickService listener) {
        this.arrUserList = objects;
        this.onClickService = listener;
        baseActivity = ((BaseActivity) context);
    }

    public void doRefresh(List<SocialPlatformList.Data> arrUserChatList) {
        this.arrUserList = arrUserChatList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemInfServicesBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_inf_services, parent, false);
        return new SimpleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        if (arrUserList != null) {
            final SocialPlatformList.Data item = arrUserList.get(position);
            holder.binding.tvReceiverName.setText(item.getName(baseActivity.language));
            holder.binding.tvAmount.setText(baseActivity.getString(R.string.from) + " " + item.minPrice);

            if (!TextUtils.isEmpty(item.filename)) {
                Glide.with(baseActivity).load(item.filename)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
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
                        .into(holder.binding.imgProfile);
            } else {
                holder.binding.imgProfile.setImageResource(R.mipmap.ic_launcher_round);
            }

            String rate = ((BaseActivity) baseActivity).get1DecimalPlaces(item.starpoints);
            try {
                holder.binding.ratingbar.setRating(Float.parseFloat(rate));
            } catch (Exception e) {
                holder.binding.ratingbar.setRating(0);
            }
            holder.binding.tvReviews.setText(String.format(Locale.getDefault(), "(%d)", item.gig_count_rating));

            holder.binding.loutHeader.setOnClickListener(v -> {
                if (onClickService != null) {
                    onClickService.onClickService(item, position);
                }
            });
        }
    }

    public List<SocialPlatformList.Data> getData() {
        return arrUserList;
    }

    @Override
    public int getItemCount() {
        if (viewMoreService) {
            return arrUserList != null ? Math.min(arrUserList.size(), 2) : 0;
        } else {
            return arrUserList != null ? arrUserList.size() : 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    boolean viewMoreService;

    public void setMore(boolean viewMoreReview) {
        viewMoreService = viewMoreReview;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemInfServicesBinding binding;

        public SimpleViewHolder(ItemInfServicesBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }
    }

    public interface OnClickService {
        void onClickService(SocialPlatformList.Data data, int pos);
    }
}
