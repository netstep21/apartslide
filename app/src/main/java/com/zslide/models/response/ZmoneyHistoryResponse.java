package com.zslide.models.response;

import com.zslide.models.ZmoneyHistory;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by chulwoo on 2017. 3. 8..
 */

public class ZmoneyHistoryResponse extends SimpleApiResponse {

    @SerializedName("history") List<ZmoneyHistory> historyList;

    public List<ZmoneyHistory> getItem() {
        return historyList;
    }
}
