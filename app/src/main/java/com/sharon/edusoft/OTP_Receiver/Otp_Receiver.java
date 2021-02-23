package com.sharon.edusoft.OTP_Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;


public class Otp_Receiver extends BroadcastReceiver {

    private static EditText otpCode;
    public void setEditText(EditText editText){
        Otp_Receiver.otpCode=editText;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle=intent.getExtras();
        if (bundle!=null){
        SmsMessage[] messages= Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage sms:messages) {
            String message = sms.getMessageBody();
            String getOtp = message.substring(0, Math.min(message.length(), 6));
        otpCode.setText(getOtp);
        }
        }

    }
}
