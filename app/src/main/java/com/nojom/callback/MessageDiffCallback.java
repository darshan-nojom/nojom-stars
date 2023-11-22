package com.nojom.callback;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.nojom.model.Messages;

import java.util.List;

public class MessageDiffCallback extends DiffUtil.Callback {

    private final List<Messages> mOldList;
    private final List<Messages> mNewList;

    public MessageDiffCallback(List<Messages> mOldList, List<Messages> mNewList) {
        this.mOldList = mOldList;
        this.mNewList = mNewList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).id.equals(mNewList.get(newItemPosition).id);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Messages oldSkill = mOldList.get(oldItemPosition);
        Messages newSkill = mNewList.get(newItemPosition);

        return oldSkill.equals(newSkill);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
