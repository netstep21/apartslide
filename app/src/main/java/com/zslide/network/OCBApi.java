package com.zslide.network;

import android.support.annotation.NonNull;

import com.zslide.Config;
import com.zslide.data.UserManager;
import com.zslide.data.model.User;
import com.zslide.models.OCB;
import com.zslide.models.OCBPoint;
import com.zslide.network.service.OCBService;
import com.zslide.ocb.Engine.Seed;
import com.zslide.ocb.SeedCrypt;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by chulwoo on 16. 9. 28..
 */
public class OCBApi {

    private static final String OCB_KEY = Config.DEBUG ? "1234567890123456" : "OCBPG!13@OCBPASS";
    private OCBService ocbService;

    protected OCBApi(Retrofit retrofit) {
        ocbService = retrofit.create(OCBService.class);
    }

    public Observable<OCB> auth(@NonNull String cardNumber, @NonNull String password, String type) {
        return auth(cardNumber, password, "30", type);
    }

    public Observable<OCB> auth(@NonNull String phoneNumber, String type) {
        return auth(phoneNumber, "", "31", type);
    }

    private Observable<OCB> auth(String authId, String authPassword, String authType, String ocbType) {
        SeedCrypt crypt = new SeedCrypt();
        crypt.init(true, Seed.MODE_ECB, Seed.PAD_X923, OCB_KEY.getBytes());
        User user = UserManager.getInstance().getUserValue();
        String encs = "SubMallUserId=" + user.getId() +
                ";Amount=30000" +
                ";UserName=" + user.getName() +
                ";AuthId=" + authId +
                ";AuthPwd=" + authPassword;
        return ocbService.lookupOCB(crypt.encrypt(encs), authType, ocbType);
    }

    public Observable<OCB> use(String authId, String authPassword, OCB ocb, int usePoint) {
        SeedCrypt crypt = new SeedCrypt();
        crypt.init(true, Seed.MODE_ECB, Seed.PAD_X923, OCB_KEY.getBytes());
        User user = UserManager.getInstance().getUserValue();
        String encs = "SubMallUserId=" + user.getId() +
                ";Amount=" + usePoint +
                ";Point=" + usePoint +
                ";UserName=" + user.getName() +
                ";AuthId=" + authId +
                ";AuthPwd=" + authPassword;
        String cancelEncs = ";Point=" + usePoint +
                ";AuthId=" + authId;
        return ocbService.useOCB(crypt.encrypt(encs), ocb.getMctTrNo(), usePoint, crypt.encrypt(cancelEncs));
    }

    public Observable<OCBPoint> usedPoint() {
        return ocbService.getUsedOCBPoint();
    }

    public Observable<OCB> inquiry(OCB ocb) {
        return ocbService.inquiryOCB(ocb.getMctTrNo());
    }

    public Observable<OCB> cancel() {
        return ocbService.cancelOCB("");
    }
}
