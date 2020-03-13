package com.zslide.data.model;

import android.content.Context;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.models.Dong;
import com.zslide.models.LevelInfo;
import com.zslide.models.Sex;
import com.zslide.models.SocialAccount;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by chulwoo on 15. 8. 27..
 */
public class User extends BaseModel implements Cloneable {

    public static final User NULL = new User();

    @SerializedName("phone_number") private String phoneNumber;
    @Getter @Setter @SerializedName("profile_image") private String profileImageUrl;
    @Getter @SerializedName("email") private String email;
    @Getter @Setter @SerializedName("user_name") private String name;
    @Getter @Setter @SerializedName("nickname") private String nickname;
    @Setter @Getter @SerializedName("birthday") private String strBirth;
    @Getter @SerializedName("sex") private Sex sex;
    @Getter @SerializedName("dong") private Dong dong;
    @Getter @SerializedName("family") private long familyId;
    @SerializedName("new_notification") private boolean hasNewNotification;
    @SerializedName("is_banished") private boolean isBanished;
    @SerializedName("zummalevel") private LevelInfo levelInfo;
    @SerializedName("social_account") private SocialAccount socialAccount;
    /**
     * 계정 연동 여부(이메일 or 소셜 계정)
     */
    @Getter @SerializedName("authenticated") private boolean accountLinked;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBlurredPhoneNumber(boolean formatted) {
        if (this.phoneNumber == null) {
            return "";
        }

        String phoneNumber;
        if (formatted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                phoneNumber = PhoneNumberUtils.formatNumber(this.phoneNumber,
                        Locale.KOREA.getCountry());
            } else {
                phoneNumber = PhoneNumberUtils.formatNumber(this.phoneNumber);
            }
        } else {
            phoneNumber = this.phoneNumber;
        }

        char[] blurredPhoneNumber = new char[phoneNumber.length()];
        for (int i = blurredPhoneNumber.length - 1, j = 0; i >= 0; i--) {
            char number = phoneNumber.charAt(i);
            if (j == 1 || j == 2 ||
                    j == 5 || j == 6) {
                blurredPhoneNumber[i] = '*';
                j++;
            } else {
                blurredPhoneNumber[i] = number;
                if (number != '-') {
                    j++;
                }
            }
        }

        return String.valueOf(blurredPhoneNumber);
    }

    public String getBlurredName(Context context) {
        StringBuilder blurredName = new StringBuilder();

        User me = UserManager.getInstance().getUserValue();
        if (this.equals(me)) {
            return name;
        }

        if (TextUtils.isEmpty(name)) {
            return context.getString(R.string.empty_name);
        }

        for (int i = 0; i < name.length(); i++) {
            if (i == 1) {
                blurredName.append("*");
            } else {
                blurredName.append(name.charAt(i));
            }
        }

        return blurredName.toString();
    }

    public boolean hasProfileImage() {
        return !TextUtils.isEmpty(profileImageUrl);
    }

    public boolean hasNickname() {
        return !TextUtils.isEmpty(nickname);
    }

    public String getDisplayNickname(Context context) {
        return TextUtils.isEmpty(nickname) ? context.getString(R.string.empty_nickname) : nickname;
    }

    public boolean hasName() {
        return !TextUtils.isEmpty(name);
    }

    public String getDisplayName(Context context) {
        if (TextUtils.isEmpty(name)) {
            if (TextUtils.isEmpty(nickname)) {
                return context.getString(R.string.empty_name);
            } else {
                return nickname;
            }
        } else {
            return name;
        }
    }

    public String getBirthYear() {
        if (!TextUtils.isEmpty(strBirth)) {
            return strBirth.substring(0, 4);
        }

        return "";
    }

    /**
     * 저장된 생년월일을 통해 해당 사용자의 나이를 구한다.
     *
     * @return 사용자의 나이
     */
    public int getAge() {
        int age = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

            Calendar current = Calendar.getInstance();
            Calendar birth = Calendar.getInstance();
            birth.setTime(dateFormat.parse(getBirthYear()));
            current.setTime(new Date());

            age = current.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + 1;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return age;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setFamilyId(long familyId) {
        this.familyId = familyId;
    }

    public boolean hasNewNotification() {
        return hasNewNotification;
    }

    public boolean hasFamily() {
        return familyId != 0 && !isBanished;
    }

    public boolean isBanished() {
        return isBanished;
    }

    public SocialAccount getSocialAccount() {
        return socialAccount;
    }

    public LevelInfo getLevelInfo() {
        return levelInfo;
    }

    public User clone() throws CloneNotSupportedException {
        return User.class.cast(super.clone());
    }
}