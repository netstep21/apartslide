package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 5. 26..
 */
public class NaverUserProfileWrapper {

    private @SerializedName("resultcode") String resultCode;
    private @SerializedName("message") String message;
    private @SerializedName("response") NaverUserProfile profile;

    public String getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public NaverUserProfile getProfile() {
        return profile;
    }

    public class NaverUserProfile {
        private @SerializedName("id") String id;
        private @SerializedName("name") String name;
        private @SerializedName("nickname") String nickname;
        private @SerializedName("email") String email;
        private @SerializedName("gender") String gender;
        private @SerializedName("age") String age;
        private @SerializedName("birthday") String birthday;
        private @SerializedName("profile_image") String profileImage;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getNickname() {
            return nickname;
        }

        public String getEmail() {
            return email;
        }

        public Sex getGender() {
            if ("F".equals(gender)) {
                return Sex.WOMAN;
            } else {
                return Sex.MAN;
            }
        }

        public String getAge() {
            return age;
        }

        public String getBirthday() {
            return birthday;
        }

        public String getProfileImage() {
            return profileImage;
        }
    }
}