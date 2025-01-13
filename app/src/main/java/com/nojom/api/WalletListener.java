package com.nojom.api;


import com.nojom.model.WalletData;

import java.util.List;

public interface WalletListener {

    void successResponse(WalletData responseBody, String url, String message);
    void successTxnResponse(List<WalletData> responseBody, String url, String message);

    void failureResponse(Throwable throwable, String url, String message);

}