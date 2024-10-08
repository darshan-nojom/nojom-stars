package com.nojom.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemAllSectionDragBinding;
import com.nojom.databinding.ItemNewProfileBinding;
import com.nojom.model.ProfileMenu;
import com.nojom.ui.BaseActivity;

import java.util.List;

public class ProfileMenuUpdateAdapter extends RecyclerView.Adapter<ProfileMenuUpdateAdapter.SimpleViewHolder> {

    private BaseActivity context;
    private List<ProfileMenu> paymentList;
    private OnClickMenuListener onClickMenuListener;
    private boolean isDropDone = true;
    private boolean isReOrderScreen;
    private UpdateSwipeListener onClickPlatformListener;

    public void setReOrderScreen(boolean reOrderScreen) {
        isReOrderScreen = reOrderScreen;
    }

    public interface OnClickMenuListener {
        void onClickMenu(ProfileMenu menu);
    }

    public ProfileMenuUpdateAdapter(BaseActivity context, List<ProfileMenu> paymentList, OnClickMenuListener listener,
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
        ItemAllSectionDragBinding itemAccountBinding = ItemAllSectionDragBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        ProfileMenu item = paymentList.get(position);

        holder.binding.txtName.setText(item.menuName);


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

        ItemAllSectionDragBinding binding;

        SimpleViewHolder(ItemAllSectionDragBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

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
