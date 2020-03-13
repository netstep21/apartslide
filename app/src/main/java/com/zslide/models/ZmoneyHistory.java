package com.zslide.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chulwoo on 2017. 3. 8..
 */

public class ZmoneyHistory extends ApiData {

    @SerializedName("year") int year;
    @SerializedName("month") int month;
    @SerializedName("rewards") List<Reward> rewards;

    public ZmoneyHistory(int year, int month) {
        this(year, month, new ArrayList<>());
    }

    public ZmoneyHistory(int year, int month, List<Reward> rewards) {
        this.year = year;
        this.month = month;
        this.rewards = rewards;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public int getTotalReward() {
        int totalReward = 0;
        for (Reward reward : rewards) {
            totalReward += reward.getValue();
        }

        return totalReward;
    }

    public static class Reward {
        @SerializedName("user_id") long userId;
        @SerializedName("user_name") String userName;
        @SerializedName("reward") int value;

        public Reward(long userId, String userName, int value) {
            this.userId = userId;
            this.userName = userName;
            this.value = value;
        }

        public long getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public int getValue() {
            return value;
        }
    }
}
