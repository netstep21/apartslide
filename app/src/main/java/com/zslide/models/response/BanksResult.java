package com.zslide.models.response;

import com.zslide.models.Bank;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by chulwoo on 15. 11. 26..
 * <p>
 * v2용 데이터로 변경해야함
 */
@Deprecated
public class BanksResult {

    @SerializedName("results") private List<Bank> mBanks;

    public List<Bank> getBanks() {
        return mBanks;
    }
}
