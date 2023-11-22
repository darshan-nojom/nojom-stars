package com.nojom.ui.startup;

import android.content.res.TypedArray;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.home.HomePagerModel;

import java.util.ArrayList;

public class SelectAccountActivityVM extends ViewModel {

    MutableLiveData<ArrayList<HomePagerModel>> listMutableLiveData;

    public MutableLiveData<ArrayList<HomePagerModel>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public void getList(BaseActivity activity) {
        ArrayList<HomePagerModel> arrayList = new ArrayList<>();
        TypedArray imgs = activity.getResources().obtainTypedArray(R.array.hire_images);
//        String[] stringArray = activity.getResources().getStringArray(R.array.hire);

        for (int i = 0; i < 12; i++) {
            HomePagerModel model = new HomePagerModel();
            model.icon = imgs.getResourceId(i, -1);
            arrayList.add(model);
        }
        getListMutableLiveData().postValue(arrayList);
    }


}
