package com.nojom.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.nojom.R;
import com.nojom.databinding.ItemSwitchAccountBinding;
import com.nojom.model.UserModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SwitchAccountAdapter extends RecyclerSwipeAdapter<SwitchAccountAdapter.SimpleViewHolder> {

    private final BaseActivity context;
    private final List<UserModel> accounts;
    private final int loggedinId;
    private Dialog dialog;

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public SwitchAccountAdapter(BaseActivity context, List<UserModel> accounts, int userid) {
        this.context = context;
        this.loggedinId = userid;
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemSwitchAccountBinding itemAccountBinding =
                ItemSwitchAccountBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        UserModel item = accounts.get(position);

        holder.binding.swipe.setShowMode(SwipeLayout.ShowMode.LayDown);

        holder.binding.tvEmail.setText(item.email);
        holder.binding.tvUsername.setText(item.username);
        if (loggedinId != 0 && loggedinId == item.id) {
            holder.binding.imgCheck.setVisibility(View.VISIBLE);
            holder.binding.swipe.setSwipeEnabled(false);
        } else {
            holder.binding.swipe.setSwipeEnabled(true);
        }

        Glide.with(context).load(context.getImageUrl() + item.profilePic)
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        binding.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        binding.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.binding.imgProfile);
        mItemManger.bindView(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return accounts != null ? accounts.size() : 0;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSwitchAccountBinding binding;

        SimpleViewHolder(ItemSwitchAccountBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.loutHeader.setOnClickListener(v -> {
                if (loggedinId != 0 && loggedinId != accounts.get(getAdapterPosition()).id) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    mItemManger.closeAllItems();
                    Preferences.saveUserData(context, accounts.get(getAdapterPosition()));
                    if (!TextUtils.isEmpty(accounts.get(getAdapterPosition()).jwt)) {
                        Preferences.writeString(context, Constants.JWT, accounts.get(getAdapterPosition()).jwt);
                    }
                    context.getProfile();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    context.gotoMainActivity(Constants.TAB_HOME);
                }
            });

            itemView.llDelete.setOnClickListener(v -> {
                mItemManger.closeAllItems();
                showConfDialog(context, accounts.get(getAdapterPosition()), getAdapterPosition());
            });
        }
    }

    void showConfDialog(BaseActivity activity, UserModel userModel, int adapterPosition) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        tvMessage.setText("Are you sure want to remove account?");

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            deleteAccount(context, userModel, adapterPosition);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    public void deleteAccount(Context context, UserModel userModel, int adapterPosition) {
        HashMap<String, String> accounts = Preferences.getMultipleAccounts(context);

        for (Map.Entry<String, String> entry : accounts.entrySet()) {
            Log.e("username", "----- " + entry.getKey());
            if (userModel.username.equalsIgnoreCase(entry.getKey())) {
                accounts.remove(entry.getKey());
                break;
            }
        }
        Log.e("jwt", "---- " + accounts.size());
        this.accounts.remove(adapterPosition);
        Preferences.refreshAccount(context, accounts);
        notifyDataSetChanged();

    }
}
