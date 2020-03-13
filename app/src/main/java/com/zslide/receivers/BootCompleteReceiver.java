package com.zslide.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zslide.ZummaApp;

/**
 * Created by chulwoo on 15. 12. 4..
 * <p>
 * 디바이스가 종료된 후 다시 시작됐을 때, 잠금화면을 자동적으로 실행한다.
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            ZummaApp app = (ZummaApp) context.getApplicationContext();
            app.activateBuzzScreenIfNeeded();
        }
    }
}