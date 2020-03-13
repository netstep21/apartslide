package com.zslide.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.ZLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by chulwoo on 16. 4. 11..
 */
public class CertificationCodeSMSReceiver extends BroadcastReceiver {

    private PublishSubject<String> certificationCodePublisher = PublishSubject.create();

    @Override
    public void onReceive(Context context, Intent intent) {
        final StringBuilder builder = new StringBuilder();
        Observable.just(intent)
                .map(Intent::getExtras)
                .filter(bundle -> bundle != null)
                .map(bundle -> (Object[]) bundle.get("pdus"))
                .subscribe(pdus -> {
                    Observable.from(pdus)
                            .map(pdu -> (byte[]) pdu)
                            .map(SmsMessage::createFromPdu)
                            .map(SmsMessage::getMessageBody)
                            .subscribe(builder::append, ZummaApiErrorHandler::handleError, () -> {
                                String message = builder.toString();
                                Pattern certificationCodePattern = Pattern.compile("\\d{5}");
                                Matcher certificationCodeMatcher = certificationCodePattern.matcher(message);
                                if (certificationCodeMatcher.find()) {
                                    certificationCodePublisher.onNext(certificationCodeMatcher.group(0));
                                }
                            });
                }, ZLog::e);
    }

    public PublishSubject<String> getCertificationCodePublisher() {
        return certificationCodePublisher;
    }
}
