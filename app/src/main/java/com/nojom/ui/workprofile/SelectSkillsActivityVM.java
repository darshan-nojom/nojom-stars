package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.SingleSelectionItemAdapter;
import com.nojom.api.APIRequest;
import com.nojom.model.UserSkillsModel;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.nojom.util.Constants.API_ADD_SKILL;

public class SelectSkillsActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;


    private MutableLiveData<ArrayList<UserSkillsModel.SkillLists>> listMutableLiveData;
    private MutableLiveData<ArrayList<UserSkillsModel.SkillLists>> selectedListMutableLiveData;
    private MutableLiveData<Integer> mutableCounter = new MutableLiveData<>();
    private MutableLiveData<Integer> mutablePageNo = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSearch = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSkillAdded = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    private SingleSelectionItemAdapter itemAdapter;
    private ArrayList<String> experienceLevel;

    public MutableLiveData<Boolean> getIsSkillAdded() {
        return isSkillAdded;
    }

    public MutableLiveData<Boolean> getIsSearch() {
        return isSearch;
    }

    public MutableLiveData<Integer> getMutablePageNo() {
        return mutablePageNo;
    }

    public MutableLiveData<Integer> getMutableCounter() {
        return mutableCounter;
    }

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<ArrayList<UserSkillsModel.SkillLists>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public MutableLiveData<ArrayList<UserSkillsModel.SkillLists>> getSelectedListMutableLiveData() {
        if (selectedListMutableLiveData == null) {
            selectedListMutableLiveData = new MutableLiveData<>();
        }
        return selectedListMutableLiveData;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
        String[] experience = activity.getResources().getStringArray(R.array.experience_rate);
        experienceLevel = new ArrayList<>(Arrays.asList(experience));
    }

    void addSkills(String skillsIds, String ratingIds) {
        if (!activity.isNetworkConnected())
            return;

//        activity.showProgress();

        CommonRequest.AddSkills addSkills = new CommonRequest.AddSkills();
        addSkills.setRating(ratingIds);
        addSkills.setSkill_id(skillsIds);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_SKILL, addSkills.toString(), true, this);

    }

    void showSingleSelectionDialog(final TextView tvLevel, final UserSkillsModel.SkillLists item) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        EditText etSearch = dialog.findViewById(R.id.et_search);
        etSearch.setVisibility(View.GONE);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        rvTypes.setLayoutManager(new LinearLayoutManager(activity));
        int selectedPosition = -1;
        if (experienceLevel.size() > 0) {
            for (int i = 0; i < experienceLevel.size(); i++) {
                if (experienceLevel.get(i).equals(tvLevel.getText().toString())) {
                    selectedPosition = i;
                }
            }
            itemAdapter = new SingleSelectionItemAdapter(activity, experienceLevel, selectedPosition);
            rvTypes.setAdapter(itemAdapter);
        }

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvApply.setOnClickListener(v -> {
            Utils.hideSoftKeyboard(activity);
            if (itemAdapter != null && itemAdapter.getSelectedItem() != null) {
                tvLevel.setText(itemAdapter.getSelectedItem());
                int selectedId = Utils.getRatingId(itemAdapter.getSelectedItem());
                if (item.rating != null) {
                    item.rating = selectedId;
                }
                item.selectedRating = selectedId;

                //update skill value in selected list
                if (getSelectedListMutableLiveData() != null && getSelectedListMutableLiveData().getValue() != null
                        && getSelectedListMutableLiveData().getValue().size() > 0) {
                    for (UserSkillsModel.SkillLists skill : getSelectedListMutableLiveData().getValue()) {
                        if (skill.id == item.id) {
                            skill.rating = selectedId;
                            skill.selectedRating = selectedId;
                            break;
                        }
                    }
                }

                dialog.dismiss();
            } else {
                activity.toastMessage(activity.getString(R.string.please_select_one_item));
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_ADD_SKILL)) {
            getIsSkillAdded().postValue(true);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsSearch().postValue(false);
//        activity.hideProgress();
    }
}
