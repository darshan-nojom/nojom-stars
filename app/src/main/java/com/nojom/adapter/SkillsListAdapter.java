package com.nojom.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemChipViewBinding;
import com.nojom.model.Skill;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.List;

public class SkillsListAdapter extends RecyclerView.Adapter<SkillsListAdapter.SimpleViewHolder> {
    private final List<Skill> arrSkillsList;
    private BaseActivity activity;
    private boolean isCategoryTag;

    public SkillsListAdapter(BaseActivity activity, List<Skill> arrSkillsList, boolean isCategory) {
        this.arrSkillsList = arrSkillsList;
        this.activity = activity;
        this.isCategoryTag = isCategory;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChipViewBinding popularBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chip_view, parent, false);
        return new SimpleViewHolder(popularBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        Skill item = arrSkillsList.get(position);

        if (isCategoryTag) {
            holder.binding.txtTagList.setText(item.skillTitle);
            holder.binding.txtTagList.setBackground(activity.getResources().getDrawable(R.drawable.rounded_border_gray_40));
        } else {
            holder.binding.txtTagList.setText("#" + item.skillTitle);
        }
    }

    @Override
    public int getItemCount() {
        return arrSkillsList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        ItemChipViewBinding binding;

        public SimpleViewHolder(ItemChipViewBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            if (activity.language.equals("ar")) {
                activity.setArFont(binding.txtTagList, Constants.FONT_AR_MEDIUM);
            }
        }
    }
}
