package com.zslide.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 15. 11. 20..
 */
public class Account implements Cloneable {

    @SerializedName("owner") private String owner;
    @SerializedName("account") private String account;
    @SerializedName("bank") private String bank;

    public static Account mock() {
        Account account = new Account();
        account.owner = "테스트";
        account.account = "1234567890";
        account.bank = "테스트은행";

        return account;
    }

    public String getOwner() {
        return owner;
    }

    public String getBlurredOwner() {
        String blurredOwner = "";
        if (owner != null) {
            for (int i = 0; i < owner.length(); i++) {
                if (i == 1) {
                    blurredOwner += "*";
                } else {
                    blurredOwner += owner.charAt(i);
                }
            }
        }

        return blurredOwner;
    }

    public String getAccount() {
        return account;
    }

    public String getBlurredAccount() {
        String blurredAccount = "";
        if (account != null) {
            for (int i = 0; i < account.length(); i++) {
                if (i > 3 && i <= 9) {
                    blurredAccount += "*";
                } else {
                    blurredAccount += account.charAt(i);
                }
            }
        }

        return blurredAccount;
    }

    public String getBank() {
        return bank;
    }

    public boolean isNull() {
        return TextUtils.isEmpty(owner) || TextUtils.isEmpty(account) || TextUtils.isEmpty(bank);
    }

    public Account clone() throws CloneNotSupportedException {
        return Account.class.cast(super.clone());
    }
}
