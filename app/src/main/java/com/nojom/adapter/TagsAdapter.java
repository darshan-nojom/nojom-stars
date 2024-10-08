package com.nojom.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.textview.TextViewSFTextPro;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemContractListBinding;
import com.nojom.databinding.ItemSelectedSkillsBinding;
import com.nojom.model.GigProjectList;
import com.nojom.model.UserSkillsModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.SimpleViewHolder> {

    private BaseActivity context;
    private List<UserSkillsModel.SkillLists> projectsList;

    private TagClickListener jobClickListener;
    private int selectedSkillCount = 0;
    public interface TagClickListener {
        void onClickTag(int jobId, int selectedPos);

        void onClickAddRemove(int jobId, int selectedPos, UserSkillsModel.SkillLists item);
    }

    public TagsAdapter(BaseActivity context, TagClickListener jobClickListener) {
        this.context = context;
        this.jobClickListener = jobClickListener;
    }

    public void doRefresh(List<UserSkillsModel.SkillLists> projectsList) {
        this.projectsList = projectsList;
        notifyDataSetChanged();
    }

    public List<UserSkillsModel.SkillLists> getData() {
        return projectsList;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemSelectedSkillsBinding projectsListBinding =
                ItemSelectedSkillsBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(projectsListBinding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        UserSkillsModel.SkillLists item = projectsList.get(position);

        try {
//            ArrayList<UserSkillsModel.SkillLists> skillLists = skillsActivityViewModel.getListMutableLiveData().getValue();
//            if (skillLists != null && skillLists.size() > 0) {
            if (item != null) {

                holder.binding.tvSkillName.setText(item.getName(context.language));

                if (item.isSelected) {
                    holder.binding.imgAddRemove.setImageResource(R.drawable.close_red);
//                    holder.binding.tvSkillLevel.setVisibility(View.VISIBLE);
//                    if (item.rating != null) {
//                        holder.binding.tvSkillLevel.setText(Utils.getRatingFromId(item.rating));
//                    } else {
//                        holder.binding.tvSkillLevel.setText(Utils.getRatingFromId(0));//default
//                    }
                } else {
//                    holder.binding.tvSkillLevel.setVisibility(View.GONE);
                    holder.binding.imgAddRemove.setImageResource(R.drawable.add);
                }

//                holder.binding.tvSkillLevel.setOnClickListener(view1 -> skillsActivityViewModel.showSingleSelectionDialog(holder.binding.tvSkillLevel, item));

                holder.binding.imgAddRemove.setOnClickListener(view12 -> {
                    try {
//                        item.selectedRating = Utils.getRatingId(holder.binding.tvSkillLevel.getText().toString());
                        if (item.isSelected) {
                            item.isSelected = false;
                            selectedSkillCount--;
                            Objects.requireNonNull(getData()).remove(item);
                        } else {
                            item.isSelected = true;
                            selectedSkillCount++;
                            if (getData() != null) {
                                getData().add(item);
                            } else {
                                ArrayList<UserSkillsModel.SkillLists> selectedSkillLists = new ArrayList<>();
                                selectedSkillLists.add(item);
//                                skillsActivityViewModel.getSelectedListMutableLiveData().postValue(selectedSkillLists);
                            }

                        }
                        notifyItemChanged(position);
//                        binding.tvSkillNo.setText(String.format("%d", selectedSkillCount));

                        if (jobClickListener != null) {
                            jobClickListener.onClickAddRemove(selectedSkillCount, position, projectsList.get(position));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return projectsList != null ? projectsList.size() : 0;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSelectedSkillsBinding binding;

        SimpleViewHolder(ItemSelectedSkillsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.tvSkillLevel.setVisibility(View.GONE);

            itemView.getRoot().setOnClickListener(v -> {
//                if (jobClickListener != null) {
//
//                    jobClickListener.onClickTag(projectsList.get(getAdapterPosition()).id, getAdapterPosition());
//                }

            });
        }
    }
}
