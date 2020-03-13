package com.zslide.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.InviteInfo;
import com.zslide.data.model.User;
import com.zslide.dialogs.FamilyInviteDialog;
import com.zslide.network.ApiConstants;
import com.zslide.utils.EventLogger;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import butterknife.BindView;
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by chulwoo on 16. 6. 2..
 */
public class FamilyInviteFragment extends InviteFragment {

    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.inviteCode) TextView inviteCodeView;

    private String userName;
    private String familyName;
    private OnCompleteListener listener;

    private InviteInfo inviteInfo;

    public static FamilyInviteFragment newInstance() {

        Bundle args = new Bundle();

        FamilyInviteFragment fragment = new FamilyInviteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = UserManager.getInstance().getUserValue();
        Family family = UserManager.getInstance().getFamilyValue();

        userName = user.getName();
        familyName = family.getName();

        callback = new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                //Toast.makeText(getApplicationContext(), errorResult.getErrorMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                EventLogger.invite(getActivity(), "family", "카카오톡");
                //Toast.makeText(getApplicationContext(), "Successfully sent KakaoLink v2 message.", Toast.LENGTH_LONG).show();
            }
        };

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_family_invite;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setInviteInfo(InviteInfo inviteInfo) {
        this.inviteInfo = inviteInfo;
        inviteCodeView.setText(inviteInfo.getInviteCode());
        messageView.setText(getString(R.string.message_invite_family1, String.valueOf(inviteInfo.getReward())));
    }

    @OnClick(R.id.complete)
    public void complete() {
        if (listener != null) {
            listener.complete();
        } else {
            getActivity().finish();
        }
    }

    @OnClick(R.id.inviteKakao)
    public void inviteViaKakaotalk() {
        if (inviteInfo == null) {
            return;
        }

        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("줌머니 친구초대",
                        ApiConstants.BASE_URL + "/media/invite_logo.png",
                        LinkObject.newBuilder().setWebUrl("https://zummaslide.com")
                                .setMobileWebUrl("https://zummaslide.com").build())
                        .setDescrption(getString(R.string.invite_family_message, userName, familyName, inviteInfo.getMessage(), inviteInfo.getInviteCode()))
                        .build())
                .addButton(new ButtonObject("가입하러가기", LinkObject.newBuilder()
                        .setWebUrl("http://zummaslide.com/app/main?utm_campaign=%EC%A4%8C%EB%A7%88%EC%8A%AC%EB%9D%BC%EC%9D%B4%EB%93%9C+%EA%B0%80%EC%A1%B1%EC%B4%88%EB%8C%80&utm_medium=app&utm_source=zummaslidetps://play.google.com/store/apps/details?id=com.mobitle.zummoney")
                        .setMobileWebUrl("https://play.google.com/store/apps/details?id=com.mobitle.zummoney")
                        .setAndroidExecutionParams("key1=value1")
                        .setIosExecutionParams("key1=value1")
                        .build()))
                .build();
        KakaoLinkService.getInstance().sendDefault(getApplicationContext(), params, callback);
    }

    @OnClick(R.id.inviteSms)
    public void inviteViaSms() {
        if (inviteInfo == null) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", getString(R.string.invite_family_message_with_link,
                userName, familyName, inviteInfo.getMessage(), inviteInfo.getInviteCode()));
        startActivity(intent);
        EventLogger.invite(getActivity(), "family", "SMS");
    }

    @OnClick(R.id.inviteCopy)
    public void inviteViaClipboard() {
        if (inviteInfo == null) {
            return;
        }

        FamilyInviteDialog.newInstance(userName, familyName, inviteInfo.getMessage(), inviteInfo.getInviteCode()).show(getFragmentManager(), "invite_dialog");
        EventLogger.invite(getActivity(), "family", "문구 복사");
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }

    public interface OnCompleteListener {
        void complete();
    }

    private ResponseCallback<KakaoLinkResponse> callback;
}
