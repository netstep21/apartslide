package com.zslide.models.response;

import com.zslide.data.model.Zmoney;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by chulwoo on 2017. 3. 8..
 */

public class PaymentResponse extends SimpleApiResponse {

    @SerializedName("reward") ArrayList<Zmoney> zmoneyList;

    public ArrayList<Zmoney> getItem() {
        return zmoneyList;
    }
}
