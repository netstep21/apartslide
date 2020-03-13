package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.dialogs.ChangeFamilyLeaderDialog;

import butterknife.OnClick;

/**
 * Created by chulwoo on 16. 6. 3..
 */
public class FamilyMoveFragment extends BaseFragment {

    public static FamilyMoveFragment newInstance() {
        return new FamilyMoveFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_family_move;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
    }

    @OnClick(R.id.alone)
    public void alone() {
        ChangeFamilyLeaderDialog dialog = ChangeFamilyLeaderDialog.newInstance();
        dialog.setOnDismissListener(dialog1 -> {
            User user = UserManager.getInstance().getUserValue();
            Family family = UserManager.getInstance().getFamilyValue();
            if (family.isNull() || !family.isFamilyLeader(user)) {
                Navigator.startFamilyRegistrationActivity(getActivity());
                getActivity().finish();
            }
        });
        dialog.show(getFragmentManager(), "change_dialog");
    }

    @OnClick(R.id.together)
    public void together() {
        Navigator.startFamilyRegistrationActivity(getContext());
    }
}
