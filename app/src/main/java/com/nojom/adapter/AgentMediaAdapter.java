package com.nojom.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.nojom.databinding.ItemAgentCompanyBinding;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.GetAgentCompanies;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.ReOrderCompanyMoveCallback;
import com.nojom.util.ReOrderSocialMoveCallback;

import java.util.Collections;
import java.util.List;

public class AgentMediaAdapter extends RecyclerView.Adapter<AgentMediaAdapter.SimpleViewHolder> implements ReOrderSocialMoveCallback.ItemTouchHelperContract {

    private BaseActivity context;
    public String path;
    private List<ConnectedSocialMedia.Data> paymentList;
    private OnClickListener onClickListener;
    private boolean isDropDone = true;

    public ConnectedSocialMedia.Data getData(int pos) {
        return paymentList.get(pos);
    }

    public AgentMediaAdapter(BaseActivity context, OnClickListener listener, UpdateSwipeListener updatelistener) {
        this.context = context;
        onClickListener = listener;
        this.onClickPlatformListener = updatelistener;
    }

    public void doRefresh(List<ConnectedSocialMedia.Data> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }

    public void doRefresh(String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemAgentCompanyBinding itemAccountBinding =
                ItemAgentCompanyBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        ConnectedSocialMedia.Data item = paymentList.get(position);

        holder.binding.txtName.setText(item.getName(context.language));

        if (item.followers != 0) {
            String formattedNumber = context.formatNumber(item.followers);
            holder.binding.txtUname.setText(formattedNumber);
            holder.binding.txtUname.setVisibility(View.VISIBLE);
        } else {
            holder.binding.txtUname.setVisibility(View.GONE);
        }

        switch (item.public_status) {
            case 2://brands
                holder.binding.txtStatus.setText(context.getString(R.string.brand_only));
                holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.c_075E45));
                DrawableCompat.setTint(holder.binding.txtStatus.getBackground(), ContextCompat.getColor(context, R.color.c_C7EBD1));
                break;
            case 3://only me
                holder.binding.txtStatus.setText(context.getString(R.string.only_me));
                holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.red_dark));
                DrawableCompat.setTint(holder.binding.txtStatus.getBackground(), ContextCompat.getColor(context, R.color.c_FADCD9));
                break;
            default:
                holder.binding.txtStatus.setText(context.getString(R.string.public_));
                holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                DrawableCompat.setTint(holder.binding.txtStatus.getBackground(), ContextCompat.getColor(context, R.color.c_D4E4FA));
                break;
        }

        Glide.with(context).load(item.filename).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(paymentList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(paymentList, i, i - 1);
            }
        }
        Log.e("onRowMoved", " -- From - " + fromPosition + "  --- To - " + toPosition);
        isDropDone = false;
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onActionDone() {
        if (onClickPlatformListener != null && !isDropDone) {
            onClickPlatformListener.onSwipeSuccess(paymentList);
            isDropDone = true;
        }
    }

    @Override
    public void onRowSelected(SimpleViewHolder myViewHolder) {
//        myViewHolder.itemView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(SimpleViewHolder myViewHolder) {
//        myViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemAgentCompanyBinding binding;

        SimpleViewHolder(ItemAgentCompanyBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (context.language.equals("ar")) {
                context.setArFont(binding.txtName, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtUname, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtStatus, Constants.FONT_AR_BOLD);
                context.setArFont(binding.txtContract, Constants.FONT_AR_MEDIUM);
            }
            itemView.getRoot().setOnClickListener(v -> {
//                Intent i = new Intent(context, PaymentActivity.class);
//                i.putExtra(Constants.ACCOUNT_DATA, paymentList.get(getAdapterPosition()));
//                context.startActivity(i);
            });

            binding.txtStatus.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClickShow(paymentList.get(getAdapterPosition()), getAbsoluteAdapterPosition());
                }
            });
            binding.imgMenu.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClickMenu(paymentList.get(getAdapterPosition()), getAbsoluteAdapterPosition(), binding.imgMenu);
                }
            });

        }
    }

    public interface OnClickListener {
        void onClickShow(ConnectedSocialMedia.Data companies, int pos);

        void onClickMenu(ConnectedSocialMedia.Data companies, int pos, View view);
    }

    private UpdateSwipeListener onClickPlatformListener;

    public interface UpdateSwipeListener {
        void onSwipeSuccess(List<ConnectedSocialMedia.Data> mDatasetFiltered);

    }
}
