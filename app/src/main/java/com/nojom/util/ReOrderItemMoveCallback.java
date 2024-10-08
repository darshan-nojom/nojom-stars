package com.nojom.util;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.adapter.ProfileMenuAdapter;

public class ReOrderItemMoveCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract mAdapter;

    public ReOrderItemMoveCallback(ItemTouchHelperContract adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ProfileMenuAdapter.SimpleViewHolder) {
                ProfileMenuAdapter.SimpleViewHolder myViewHolder = (ProfileMenuAdapter.SimpleViewHolder) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }
        } else {

        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof ProfileMenuAdapter.SimpleViewHolder) {
            ProfileMenuAdapter.SimpleViewHolder myViewHolder = (ProfileMenuAdapter.SimpleViewHolder) viewHolder;
            mAdapter.onRowClear(myViewHolder);

            Log.e("onSelectedChanged", " -- - ");
            mAdapter.onActionDone();
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);

        void onActionDone();

        void onRowSelected(ProfileMenuAdapter.SimpleViewHolder myViewHolder);

        void onRowClear(ProfileMenuAdapter.SimpleViewHolder myViewHolder);

    }

}
