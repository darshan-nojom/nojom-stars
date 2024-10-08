package com.nojom.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemAllSectionBinding;
import com.nojom.databinding.ItemNewProfileBinding;
import com.nojom.model.ProfileMenu;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.ReOrderItemMoveCallback;

import java.util.Collections;
import java.util.List;

public class AllSectionAdapter extends RecyclerView.Adapter<AllSectionAdapter.SimpleViewHolder> {

    private BaseActivity context;
    private List<ProfileMenu> paymentList;
    private OnClickHideShow onClickMenuListener;

    public interface OnClickHideShow {
        void onClickHideShow(ProfileMenu menu, int pos);

        void onClickAdd(ProfileMenu menu);
    }

    public AllSectionAdapter(BaseActivity context, List<ProfileMenu> paymentList, OnClickHideShow listener) {
        this.context = context;
        this.paymentList = paymentList;
        this.onClickMenuListener = listener;
    }

    public void updatePosition(int pos, boolean isSuccess) {
        if (paymentList != null) {
//            paymentList.get(pos).isShow = !paymentList.get(pos).isShow;
//            notifyItemChanged(pos);

            for (ProfileMenu menu : paymentList) {
                if (menu.id == pos) {
                    menu.isShow = !menu.isShow;
//                    notifyItemChanged(pos);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemAllSectionBinding itemAccountBinding = ItemAllSectionBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        ProfileMenu item = paymentList.get(position);

        holder.binding.txtName.setText(item.menuName);

        if (item.isShow) {
            holder.binding.txtShow.setText(context.getString(R.string.hide));
        } else {
            holder.binding.txtShow.setText(context.getString(R.string.show));
        }
        holder.binding.txtAdd.setText(context.getString(R.string.add));
    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemAllSectionBinding binding;

        SimpleViewHolder(ItemAllSectionBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            if (context.language.equals("ar")) {
                context.setArFont(binding.txtName, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtShow, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtAdd, Constants.FONT_AR_MEDIUM);
            }

            itemView.txtShow.setOnClickListener(v -> {
                if (onClickMenuListener != null) {
                    onClickMenuListener.onClickHideShow(paymentList.get(getAdapterPosition()), getAbsoluteAdapterPosition());
                }
            });
            itemView.txtAdd.setOnClickListener(v -> {
                if (onClickMenuListener != null) {
                    onClickMenuListener.onClickAdd(paymentList.get(getAdapterPosition()));
                }
            });
        }
    }
}
