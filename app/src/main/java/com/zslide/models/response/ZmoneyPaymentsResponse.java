package com.zslide.models.response;

import com.zslide.models.ZmoneyPayments;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 2017. 3. 8..
 */

public class ZmoneyPaymentsResponse extends SimpleApiResponse {

    @SerializedName("history") ZmoneyPayments payments;

    public ZmoneyPayments getItem() {
        return payments;
    }
}
