package com.zslide.view.auth;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.utils.DLog;
import com.zslide.utils.ZLog;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chulwoo on 15. 8. 4..
 */
public class KakaoLinkActivity extends com.zslide.view.base.BaseActivity {

    private ISessionCallback kakaoSessionCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kakaoSessionCallback = new AuthCallback();
        Session session = Session.getCurrentSession();
        if (!session.isClosed()) {
            session.close();
        }
        session.addCallback(kakaoSessionCallback);
        final ArrayList<AuthType> authTypes = getKakaoAuthTypes();
        if (authTypes.size() == 1) {
            Session.getCurrentSession().open(authTypes.get(0), this);
        } else {
            LoginDialog.newInstance(authTypes).show(getSupportFragmentManager(), "login_dialog");
        }
    }

    @Override
    protected void onDestroy() {
        Session.getCurrentSession().removeCallback(kakaoSessionCallback);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ArrayList<AuthType> getKakaoAuthTypes() {
        final ArrayList<AuthType> availableAuthTypes = new ArrayList<>();
        availableAuthTypes.add(AuthType.KAKAO_LOGIN_ALL);
        return availableAuthTypes;
    }

    private static class Item {
        public final int textId;
        public final int icon;
        public final com.kakao.auth.AuthType authType;

        public Item(final int textId, final Integer icon, final com.kakao.auth.AuthType authType) {
            this.textId = textId;
            this.icon = icon;
            this.authType = authType;
        }
    }

    public static class LoginDialog extends DialogFragment {

        public static LoginDialog newInstance(ArrayList<AuthType> authTypes) {
            Bundle args = new Bundle();
            args.putSerializable(IntentConstants.EXTRA_AUTH_TYPE, authTypes);

            LoginDialog fragment = new LoginDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ArrayList<AuthType> authTypes =
                    (ArrayList<AuthType>) getArguments().getSerializable(IntentConstants.EXTRA_AUTH_TYPE);
            final List<Item> itemList = new ArrayList<>();
            if (authTypes.contains(com.kakao.auth.AuthType.KAKAO_TALK)) {
                itemList.add(new Item(R.string.com_kakao_kakaotalk_account, R.drawable.kakaotalk_icon, com.kakao.auth.AuthType.KAKAO_TALK));
            }
            if (authTypes.contains(com.kakao.auth.AuthType.KAKAO_STORY)) {
                itemList.add(new Item(R.string.com_kakao_kakaostory_account, R.drawable.kakaostory_icon, com.kakao.auth.AuthType.KAKAO_STORY));
            }
            if (authTypes.contains(com.kakao.auth.AuthType.KAKAO_ACCOUNT)) {
                itemList.add(new Item(R.string.com_kakao_other_kakaoaccount, R.drawable.kakaoaccount_icon, com.kakao.auth.AuthType.KAKAO_ACCOUNT));
            }
            itemList.add(new Item(R.string.com_kakao_account_cancel, 0, null)); //no icon for this one

            final Item[] items = itemList.toArray(new Item[itemList.size()]);

            final ListAdapter adapter = new ArrayAdapter<Item>(
                    getActivity(),
                    android.R.layout.select_dialog_item,
                    android.R.id.text1, items) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    TextView tv = (TextView) v.findViewById(android.R.id.text1);

                    tv.setText(items[position].textId);
                    tv.setTextColor(Color.BLACK);
                    tv.setTextSize(15);
                    tv.setGravity(Gravity.CENTER);
                    if (position == itemList.size() - 1) {
                        tv.setBackgroundResource(R.drawable.btn_transparent);
                    } else {
                        tv.setBackgroundResource(R.drawable.kakao_account_button_background);
                    }
                    tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                    int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                    tv.setCompoundDrawablePadding(dp5);

                    return v;
                }
            };

            return new AlertDialog.Builder(getActivity())
                    .setAdapter(adapter, (dialog, position) -> {
                        final com.kakao.auth.AuthType authType = items[position].authType;
                        if (authType != null) {
                            Session.getCurrentSession().open(authType, getActivity());
                        } else {
                            getActivity().setResult(RESULT_CANCELED);
                            getActivity().finish();
                        }

                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .create();
        }
    }

    class AuthCallback implements ISessionCallback {

        AuthCallback() {
        }

        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().requestMe(new MeResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    setResult(RESULT_CANCELED);
                    finish();
                    ZLog.e(this, errorResult.getErrorMessage());
                }

                @Override
                public void onNotSignedUp() {
                    Session session = Session.getCurrentSession();
                    Intent intent = new Intent();
                    intent.putExtra(IntentConstants.EXTRA_REFRESH_TOKEN, session.getRefreshToken());
                    intent.putExtra(IntentConstants.EXTRA_ACCESS_TOKEN, session.getAccessToken());
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onSuccess(UserProfile profile) {
                    Session session = Session.getCurrentSession();
                    Intent intent = new Intent();
                    intent.putExtra(IntentConstants.EXTRA_REFRESH_TOKEN, session.getRefreshToken());
                    intent.putExtra(IntentConstants.EXTRA_ACCESS_TOKEN, session.getAccessToken());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            DLog.e(this, "session open failed");
            if (exception != null) {
                DLog.e(this, exception.getErrorType() + ", " + exception.getMessage());
            }
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}