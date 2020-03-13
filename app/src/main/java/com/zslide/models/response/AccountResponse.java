package com.zslide.models.response;

import com.zslide.models.Account;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 15. 11. 26..
 */
public class AccountResponse extends SimpleApiResponse {

    @SerializedName("account") private Account mAccount;

    public Account getAccount() {
        return mAccount;
    }
}