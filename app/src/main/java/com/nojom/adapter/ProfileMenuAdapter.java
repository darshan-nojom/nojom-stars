package com.nojom.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemBankAccountBinding;
import com.nojom.databinding.ItemNewProfileBinding;
import com.nojom.model.BankAccounts;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.ProfileMenu;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.PaymentActivity;
import com.nojom.util.Constants;
import com.nojom.util.ItemMoveCallback;
import com.nojom.util.ReOrderItemMoveCallback;

import java.util.Collections;
import java.util.List;

public class ProfileMenuAdapter extends RecyclerView.Adapter<ProfileMenuAdapter.SimpleViewHolder> implements ReOrderItemMoveCallback.ItemTouchHelperContract {

    private BaseActivity context;
    private List<ProfileMenu> paymentList;
    private OnClickMenuListener onClickMenuListener;
    private boolean isDropDone = true;
    private boolean isReOrderScreen;
    private UpdateSwipeListener onClickPlatformListener;

    public void setReOrderScreen(boolean reOrderScreen) {
        isReOrderScreen = reOrderScreen;
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
        myViewHolder.itemView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(SimpleViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    public interface OnClickMenuListener {
        void onClickMenu(ProfileMenu menu);
    }

    public ProfileMenuAdapter(BaseActivity context, List<ProfileMenu> paymentList, OnClickMenuListener listener,
                              UpdateSwipeListener updatelistener) {
        this.context = context;
        this.paymentList = paymentList;
        this.onClickMenuListener = listener;
        this.onClickPlatformListener = updatelistener;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemNewProfileBinding itemAccountBinding = ItemNewProfileBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        ProfileMenu item = paymentList.get(position);

        holder.binding.txtName.setText(item.menuName);

        if (isReOrderScreen) {
            holder.binding.imgName.setImageResource(R.drawable.ic_re_order);
        } else {
            holder.binding.imgName.setImageResource(R.drawable.arrow_next);
        }

       /* if (paymentList.size() - 1 == position) {
            holder.binding.view.setVisibility(View.GONE);
        } else {
            holder.binding.view.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemNewProfileBinding binding;

        SimpleViewHolder(ItemNewProfileBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (context.language.equals("ar")) {
                context.setArFont(binding.txtName, Constants.FONT_AR_REGULAR);
            }


            itemView.getRoot().setOnClickListener(v -> {
                if (onClickMenuListener != null) {
                    onClickMenuListener.onClickMenu(paymentList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface UpdateSwipeListener {
        void onSwipeSuccess(List<ProfileMenu> mDatasetFiltered);

    }
}
