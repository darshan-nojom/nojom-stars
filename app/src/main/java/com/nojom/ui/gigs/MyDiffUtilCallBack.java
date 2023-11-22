package com.nojom.ui.gigs;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.nojom.model.RequiremetList;

import java.util.ArrayList;

public class MyDiffUtilCallBack extends DiffUtil.Callback {
    ArrayList<RequiremetList.Data> newList;
    ArrayList<RequiremetList.Data> oldList;

    public MyDiffUtilCallBack(ArrayList<RequiremetList.Data> newList, ArrayList<RequiremetList.Data> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).id == oldList.get(oldItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        int result = newList.get(newItemPosition).compareTo(oldList.get(oldItemPosition));
        return result == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        RequiremetList.Data newModel = newList.get(newItemPosition);
        RequiremetList.Data oldModel = oldList.get(oldItemPosition);

        Bundle diff = new Bundle();

        if (newModel.isSelected != (oldModel.isSelected)) {
            diff.putBoolean("price", newModel.isSelected);
        }
        if (diff.size() == 0) {
            return null;
        }
        return diff;
        //return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}