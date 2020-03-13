package com.zslide.view.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.ZummaApp;
import com.zslide.data.AuthenticationManager;
import com.zslide.data.UserManager;
import com.zslide.data.model.User;
import com.zslide.dialogs.LevelUpDialog;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.Event;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.BackPressFinishHandler;
import com.zslide.utils.EasySharedPreferences;
import com.zslide.utils.EventLogger;
import com.zslide.view.base.BaseActivity;
import com.zslide.widget.EventView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private BackPressFinishHandler backPressFinishHandler;

    private List<AlertDialog> showingDialogs;
    private CompositeDisposable disposables;
    private User prevUser;

    public boolean isValidUser() {
        if (!AuthenticationManager.getInstance().isLoggedIn()) {
            showSessionExpiredDialog();
            return false;
        }

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable();
        if (!isValidUser()) {
            return;
        }

        EventLogger.action(this, "enter_main");

        backPressFinishHandler = new BackPressFinishHandler(this);
        backPressFinishHandler.setBackPressAction(super::onBackPressed);
        showingDialogs = new ArrayList<>();

        // todo : main fragment에서.
        setupEventDialogs();
        User user = UserManager.getInstance().getUserValue();
        if (user.isBanished()) {
            UserManager.getInstance().checkNotifiedBan()
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(notified -> {
                        UserManager.getInstance().setNotifiedBan(true);
                        if (!notified) {
                            SimpleAlertDialog.newInstance("",
                                    getString(R.string.message_family_registration_blocked),
                                    getString(R.string.label_registration_address), "", true)
                                    .setOnConfirmListener(() -> Navigator.startFamilyRegistrationActivity(this))
                                    .show(getSupportFragmentManager(), "alert");
                        }
                    });
        }

        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, MainFragment.newInstance())
                .commit();

    }

    private void updateUser(User user) {
        this.prevUser = user;
        checkUserBanished(user);
        checkLevelUp(user);
        ZummaApp.get(this).activateBuzzScreenIfNeeded();
    }

    private void checkUserBanished(User user) {
        if (user.isBanished()) {
            disposables.add(UserManager.getInstance().checkNotifiedBan()
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(notified -> !notified)
                    .subscribe(__ -> {
                        UserManager.getInstance().setNotifiedBan(true)
                                .subscribeOn(Schedulers.io())
                                .subscribe();

                        SimpleAlertDialog.newInstance("",
                                //getString(R.string.message_family_registration_blocked, user.getFamily().getName()),
                                getString(R.string.message_family_registration_blocked, ""),
                                getString(R.string.label_registration_address), "", true)
                                .setOnConfirmListener(() -> Navigator.startFamilyRegistrationActivity(this))
                                .show(getSupportFragmentManager(), "alert");
                    }));
        }
    }

    private void checkLevelUp(User user) {
        if (prevUser != null) {
            if (prevUser.getLevelInfo() == null || user.getLevelInfo() == null) {
                return;
            }

            if (prevUser.getLevelInfo().getAdvantage() == null ||
                    user.getLevelInfo().getAdvantage() == null) {
                return;
            }

            if (prevUser.getLevelInfo().getAdvantage().getPriority() <
                    user.getLevelInfo().getAdvantage().getPriority()) {
                showLevelUpDialog();
            }
        }
    }

    public void showLevelUpDialog() {
        Fragment levelUpDialog = getSupportFragmentManager().findFragmentByTag("level_up");
        if (levelUpDialog == null || !levelUpDialog.isAdded()) {
            LevelUpDialog.newInstance().show(getSupportFragmentManager(), "level_up");
        }
    }

    private void setupEventDialogs() {
        ZummaApi.event().popups()
                .subscribeOn(rx.schedulers.Schedulers.newThread())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(this::showEventDialogs, ZummaApiErrorHandler::handleError);
    }

    private void showEventDialogs(List<Event> events) {
        Date today = new Date();
        for (Event event : events) {
            if (today.before(EasySharedPreferences.with(this)
                    .getObject(EventView.PREF_DISABLE_SHOWING_DATE + "-" + event.getId(),
                            today, Date.class))) {
                continue;
            }
            EventView eventView = new EventView(this);
            eventView.setEvent(event);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.EventDialogTheme);
            dialogBuilder.setView(eventView);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            dialog.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.dialog_event_width), WindowManager.LayoutParams.WRAP_CONTENT);
            showingDialogs.add(dialog);

            eventView.setButtonClickListener(v -> {
                if (v.getId() == R.id.close) {
                    dialog.dismiss();
                    showingDialogs.remove(dialog);
                }
            });
            eventView.setDisableSettingChangeListener(isDisable -> setDisableEvent(event, isDisable));
        }
    }

    private void setDisableEvent(Event event, boolean isDisable) {
        Calendar disableDate = Calendar.getInstance();
        if (isDisable) {
            switch (event.getType()) {
                case Event.TYPE_NEVER_SHOWING:
                    disableDate.add(Calendar.DATE, 365);
                    break;
                case Event.TYPE_TODAY_NOT_SHOWING:
                    disableDate.add(Calendar.DATE, 1);
                    break;
                case Event.TYPE_WEEK_NOT_SHOWING:
                    disableDate.add(Calendar.DATE, 7);
                    break;
            }
        }

        EasySharedPreferences.with(MainActivity.this)
                .putObject(EventView.PREF_DISABLE_SHOWING_DATE + "-" + event.getId(),
                        disableDate.getTime());
    }

    @Override
    protected void onResume() {
        super.onResume();
        disposables.add(UserManager.getInstance().fetchUser().onErrorComplete().subscribe());
        disposables.add(UserManager.getInstance().getUserObservable().subscribe(this::updateUser, this::handleError));
    }

    @Override
    protected void onPause() {
        if (disposables != null) {
            disposables.clear();
        }
        super.onPause();
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        backPressFinishHandler.onBackPressed();
    }
}
