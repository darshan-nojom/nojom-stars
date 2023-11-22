package com.nojom.interfaces;

import com.nojom.model.GeneralModel;

import retrofit2.Response;

public interface ResponseListener {
    void onResponseSuccess(Response<GeneralModel> response);

    void onError();
}
