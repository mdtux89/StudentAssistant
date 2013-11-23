package it.openlab.studentassistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;

import java.util.Calendar;


//Activity called to awake notifications after reboot
public class AlarmSetterService extends Service{
    private StudentAssistantApplication app;

    @Override
    public void onCreate() {
        app = (StudentAssistantApplication) getApplication();
    }

    @Override
    public IBinder onBind( Intent arg0 ) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        show();
        stopSelf();
        return START_STICKY;
    }

    public void show(){
        ExamDbHelper helper = new ExamDbHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(
                ExamContract.Entry.TABLE_SETTING,  // The table to query
                null,                               // The columns to return
                null,   // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        c.moveToFirst();
        if(c.getCount()>0){
            boolean n,l;
            if(c.getString(c.getColumnIndex(ExamContract.Entry.NOTIFICATION)).equals("true")){
                n = true;
            }
            else{
                n = false;
            }
            if(c.getString(c.getColumnIndex(ExamContract.Entry.LEARNING)).equals("true")){
                l = true;
            }
            else{
                l = false;
            }
            String r = c.getString(c.getColumnIndex(ExamContract.Entry.FREQUENCY));
            if(n){
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                app.period = Integer.parseInt(r)* AlarmManager.INTERVAL_DAY;
                Intent intent = new Intent(getApplicationContext(), ReminderService.class);
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), app.getIntentId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + app.period, app.period, pendingIntent);
            }
        }
    }
}
