package com.zslide.network;

import android.support.annotation.NonNull;

import com.zslide.data.UserManager;
import com.zslide.data.model.AuthType;
import com.zslide.data.model.Family;
import com.zslide.data.model.Payments;
import com.zslide.data.model.User;
import com.zslide.firebase.MyFirebaseInstanceID;
import com.zslide.models.ApartmentFamilyParams;
import com.zslide.models.FamilyParams;
import com.zslide.models.HouseFamilyParams;
import com.zslide.models.LevelCouponLog;
import com.zslide.models.LivingType;
import com.zslide.models.PaginationData;
import com.zslide.models.Sex;
import com.zslide.models.TempApartmentFamilyParams;
import com.zslide.models.response.AccountResponse;
import com.zslide.models.response.BanksResult;
import com.zslide.models.response.RecommendCountResponse;
import com.zslide.models.response.SimpleApiResponse;
import com.zslide.network.service.UserService;
import com.zslide.utils.ZLog;

import java.io.File;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by chulwoo on 16. 9. 28..
 */
public class UserApi {

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private UserService userService;

    protected UserApi(Retrofit retrofit) {
        this.userService = retrofit.create(UserService.class);
    }

    public Observable<User> login(@NonNull String phoneNumber, @NonNull String password, @NonNull MyFirebaseInstanceID myFirebaseInstanceID) {
        return userService.login(phoneNumber, password, myFirebaseInstanceID.deviceId, myFirebaseInstanceID.token);
    }

    public Observable<User> socialLogin(@NonNull String phoneNumber, @NonNull String uid,
                                        String accessToken, @NonNull String refreshToken,
                                        @NonNull MyFirebaseInstanceID myFirebaseInstanceID, AuthType authType) {
        ZLog.i(this, "[auth with " + authType.getValue().toUpperCase() + "] Access Token: " + accessToken + "\n" +
                "[auth with " + authType.getValue().toUpperCase() + "] Refresh Token:" + refreshToken);
        return userService.socialLogin(phoneNumber, uid, accessToken, refreshToken, myFirebaseInstanceID.token, myFirebaseInstanceID.deviceId, authType.getValue());
    }

    public Observable<SimpleApiResponse> kakaoLink(@NonNull String refreshToken, @NonNull String accessToken) {
        return userService.kakaoLink(refreshToken, accessToken);
    }

    public Observable<SimpleApiResponse> naverLink(@NonNull String refreshToken, @NonNull String accessToken,
                                                   @NonNull String uid) {
        return userService.naverLink(refreshToken, accessToken, uid);
    }

    public Observable<User> sync() {
        return userService.getUser();
    }

    public Observable<SimpleApiResponse> requestApartAgree(int id) {
        return userService.requestApartAgree(id);
    }

    public Observable<AccountResponse> registrationAccount(int bankId, String accountNumber,
                                                           String accountOwner) {
        return userService.createAccount(bankId, accountNumber, accountOwner);
    }

    public Observable<BanksResult> banks() {
        return userService.getBanks();
    }

    public Observable<RecommendCountResponse> recommendCount() {
        return userService.getRecommendCount();
    }

    public Observable<Family> searchFamily(String familyName) {
        return userService.searchFamily(familyName);
    }

    public Observable<Family> createFamily(String name, FamilyParams params) {
        if (params instanceof ApartmentFamilyParams) {
            ApartmentFamilyParams apartParams = (ApartmentFamilyParams) params;
            return userService.createFamily(name, -1, apartParams.getApartId(), apartParams.getDong(),
                    apartParams.getHo(), "", -1, LivingType.APARTMENT.getRawType());
        } else if (params instanceof HouseFamilyParams) {
            HouseFamilyParams houseParams = (HouseFamilyParams) params;
            return userService.createFamily(name, houseParams.getDongId(), -1, "", -1,
                    houseParams.getDetailAddress(), -1, LivingType.HOUSE.getRawType());
        } else if (params instanceof TempApartmentFamilyParams) {
            TempApartmentFamilyParams tempApartmentParams = (TempApartmentFamilyParams) params;
            return userService.createFamily(name, -1, -1, "", -1, "",
                    tempApartmentParams.getTempApartmentId(), LivingType.TEMP_APARTMENT.getRawType());
        } else {
            throw new IllegalArgumentException(params.getClass().getName() + " does not supported");
        }
    }

    public Observable<Family> setFamily(long id) {
        return userService.setFamily(id, id);
    }

    public Observable<Family> joinFamily(long familyId, long joinFamilyId) {
        return userService.joinFamily(familyId, joinFamilyId, "join");
    }

    public Observable<Family> updateFamily(FamilyParams params) {
        if (params instanceof ApartmentFamilyParams) {
            ApartmentFamilyParams apartParams = (ApartmentFamilyParams) params;
            return userService.updateFamily(apartParams.getFamilyId(), "", -1, apartParams.getApartId(),
                    apartParams.getDong(), apartParams.getHo(), "", -1,
                    LivingType.APARTMENT.getRawType(), "update");
        } else if (params instanceof HouseFamilyParams) {
            HouseFamilyParams houseParams = (HouseFamilyParams) params;
            return userService.updateFamily(houseParams.getFamilyId(), "", houseParams.getDongId(),
                    -1, "", -1, houseParams.getDetailAddress(), -1, LivingType.HOUSE.getRawType(), "update");
        } else if (params instanceof TempApartmentFamilyParams) {
            TempApartmentFamilyParams tempApartmentParams = (TempApartmentFamilyParams) params;
            return userService.updateFamily(tempApartmentParams.getFamilyId(), "", -1, -1, "", -1, "",
                    tempApartmentParams.getTempApartmentId(), LivingType.TEMP_APARTMENT.getRawType(), "update");
        } else {
            throw new IllegalArgumentException(params.getClass().getName() + " not supported");
        }
    }

    public Observable<User> editName(String name) {
        User user = UserManager.getInstance().getUserValue();
        return userService.editProfile(user.getId(), name, user.getBirthYear() + "-01-01", user.getSex().getSimpleEnglish());
    }

    public Observable<User> editBirthYear(String birthYear) {
        User user = UserManager.getInstance().getUserValue();
        return userService.editProfile(user.getId(), user.getName(), birthYear + "-01-01", user.getSex().getSimpleEnglish());
    }

    public Observable<User> editSex(Sex sex) {
        User user = UserManager.getInstance().getUserValue();
        return userService.editProfile(user.getId(), user.getName(), user.getBirthYear() + "-01-01", sex.getSimpleEnglish());
    }

    public Observable<User> editNickname(String nickname) {
        User user = UserManager.getInstance().getUserValue();
        return userService.editNickname(user.getId(), nickname);
    }

    public Observable<User> editProfileImage(long id, File file) {
        if (file != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("profile_image", file.getName(), requestFile);
            return userService.editProfileImage(id, part);
        } else {
            return userService.deleteProfileImage(id, null);
        }
    }

    public Observable<Family> editFamilyName(long id, String name) {
        return userService.editFamilyName(id, name);
    }

    public Observable<User> changeFamilyLeader(long userId) {
        return userService.changeFamilyLeader(userId, null);
    }

    public Observable<Family> editFamilyImage(long id, File file) {
        if (file != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("profile_image", file.getName(), requestFile);
            return userService.editFamilyImage(id, part);
        } else {
            return userService.deleteFamilyImage(id, null);
        }
    }

    public Observable<Family> blockMembers(long id, Set<User> users, String reason) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        boolean first = true;
        for (User user : users) {
            if (first) {
                first = false;
            } else {
                builder.append(",");
            }
            builder.append(user.getId());
        }
        builder.append("]");
        String a = builder.toString();
        ZLog.e(this, a);

        // ValueError('list.remove(x): x not in list',)
        return userService.blockMembers(id, a, reason);
    }

    public Observable<Family> unblock(long id) {
        return userService.unblock(id, "");
    }

    /**
     * @param zmoney 탈퇴 사유 줌머니
     * @param zstore 탈퇴 사유 줌마가게
     * @param zshop  탈퇴 사유 줌마쇼핑
     * @param extra  탈퇴 사유 기타
     * @return 성공여부
     */
    public Observable<SimpleApiResponse> leave(boolean zmoney, boolean zstore, boolean zshop, String extra) {
        User user = UserManager.getInstance().getUserValue();
        return userService.leave(user.getId(), zmoney, zstore, zshop, extra);
    }

    public Observable<List<LevelCouponLog>> recentCouponLogs() {
        return userService.getCouponLogs(1)
                .map(coupons -> {
                    List<LevelCouponLog> logs = coupons.getResults();
                    if (logs.size() >= 3) {
                        return logs.subList(0, 3);
                    } else {
                        return logs;
                    }
                });
    }

    public Observable<PaginationData<LevelCouponLog>> couponLogs(int page) {
        return userService.getCouponLogs(page);
    }

    public Observable<PaginationData<Payments>> calculations(int page) {
        return userService.getCalculations(page);
    }
}
