package com.zslide.data.model;

import com.zslide.models.Account;
import com.zslide.models.Apartment;
import com.zslide.models.LivingType;
import com.zslide.models.TempApartment;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by jdekim43 on 2016. 5. 24..
 */
public class Family extends BaseModel {

    public static final Family NULL = new Family();

    @Getter @SerializedName("profile_image") private String profileImageUrl;
    @Getter @SerializedName("name") private String name;
    @Getter @SerializedName("address") private Address address;
    @Getter @SerializedName("apart") private Apartment apartment;
    @Getter @SerializedName("temp_apart") private TempApartment tempApartment;
    @Getter @SerializedName("account") private Account account;
    @Getter @SerializedName("calculation") private Payments payments;
    @SerializedName("users") private List<User> members;
    @Getter @SerializedName("banished") private List<User> blockedMembers;
    @Getter @SerializedName("family_leader_id") private long familyLeaderId;

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullAddress() {
        return getAddress().getFullAddress() + " " + getApartment().getAddress();
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public void setTempApartment(TempApartment tempApartment) {
        this.tempApartment = tempApartment;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean hasAccount() {
        return account != null && !account.isNull();
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public User getLeader() {
        if (members != null) {
            for (User user : members) {
                if (user.getId() == familyLeaderId) {
                    return user;
                }
            }
        }

        return User.NULL;
    }

    public boolean hasTempApartment() {
        return tempApartment != null && apartment == null;
    }

    /**
     * 정보가 변경된 사용자 데이터를 교체
     *
     * @param user 교체할 사용자 정보
     */
    public void notifyUserChanged(User user) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).equals(user)) {
                members.remove(i);
                members.add(user);
            }
        }
    }

    public HouseType getHouseType() {
        if (hasTempApartment()) {
            return HouseType.NONE;
        } else if (apartment != null) {
            LivingType livingType = apartment.getLivingType();
            switch (livingType) {
                case APARTMENT:
                    if (apartment.isJoined()) {
                        return HouseType.ZUMMA_APART;
                    } else {
                        return HouseType.APART;
                    }
                case HOUSE:
                    return HouseType.HOUSE;
                case TEMP_APARTMENT:
                default:
                    return HouseType.NONE;
            }
        } else {
            return HouseType.NONE;
        }
    }

    public List<User> getMembers() {
        // todo: convert든 서버든 따로 처리
        if (members == null) {// null로 오는 경우가 있음
            return new ArrayList<>();
        }

        List<User> fineMember = new ArrayList<>();
        for (User member : members) {
            if (!member.isBanished()) {
                fineMember.add(member);
            }
        }

        return fineMember;
    }

    public boolean isFamilyLeader(User user) {
        return familyLeaderId == user.getId();
    }

    public boolean isMember(User me) {
        for (User member : getMembers()) {
            if (member.equals(me)) {
                return !member.isBanished();
            }
        }

        return false;
    }

    public enum HouseType {
        NONE,
        HOUSE,
        TEMP_APART,
        APART,
        ZUMMA_APART,
    }
}
