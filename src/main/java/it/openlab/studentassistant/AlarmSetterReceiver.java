package it.openlab.studentassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//Broadcast receiver used to awake notifications after reboot
public class AlarmSetterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent i = new Intent(context, AlarmSetterService.class);
            context.startService(i);
        }
    }
}