package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;

import com.zslide.R;
import com.zslide.widget.AuthButton;

import java.util.List;

import butterknife.BindViews;

/**
 * Created by chulwoo on 15. 12. 29..
 */
public class LoginFragment extends BaseFragment {

    @BindViews({R.id.emailLogin, R.id.kakaoLogin, R.id.naverLogin}) List<AuthButton> authButtons;
    private AuthButton.Authable authable;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        if (authable != null) {
            for (AuthButton button : authButtons) {
                button.setAuthable(authable);
            }
        }
    }

    public LoginFragment setAuthable(AuthButton.Authable authable) {
        this.authable = authable;
        if (authButtons != null) {
            for (AuthButton button : authButtons) {
                button.setAuthable(authable);
            }
        }
        return this;
    }
}