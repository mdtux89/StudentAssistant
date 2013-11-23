package it.openlab.studentassistant;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;

//Activity launched when "Settings" menu item is selected
public class SettingsActivity extends PreferenceActivity {

    private SharedPreferences sharedPref;
    private boolean stored_n,stored_l;
    private String stored_r;
    private StudentAssistantApplication app;

    @Override
    public void onResume(){
        super.onResume();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (StudentAssistantApplication) getApplication();
        ExamDbHelper helper = new ExamDbHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

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
            sharedPref.edit().putBoolean("notification", n).apply();
            sharedPref.edit().putString("rate",c.getString(c.getColumnIndex(ExamContract.Entry.FREQUENCY))).apply();
            sharedPref.edit().putBoolean("learning",l).apply();
            stored_n = n;
            stored_l = l;
            stored_r = c.getString(c.getColumnIndex(ExamContract.Entry.FREQUENCY));
        }

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingFragment())
                .commit();
    }

    public static class SettingFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_preference);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ExamDbHelper helper = new ExamDbHelper(this);
                SQLiteDatabase db = helper.getReadableDatabase();
                ContentValues values = new ContentValues();
                boolean n = sharedPref.getBoolean("notification", false);
                String r = sharedPref.getString("rate", "7");
                boolean l = sharedPref.getBoolean("learning", false);

                if(stored_n==n && stored_r.equals(r) && stored_l==l){
                    onBackPressed();
                    break;
                }
                if(n){
                    values.put(ExamContract.Entry.NOTIFICATION, "true");
                }
                else{
                    values.put(ExamContract.Entry.NOTIFICATION, "false");
                }
                values.put(ExamContract.Entry.FREQUENCY, r);
                if(l){
                    values.put(ExamContract.Entry.LEARNING, "true");
                }
                else{
                    values.put(ExamContract.Entry.LEARNING, "false");
                }
                db.update(
                        ExamContract.Entry.TABLE_SETTING,
                        values,
                        null,
                        null
                );
                if(stored_n==n && stored_r.equals(r)){
                    onBackPressed();
                    Toast.makeText(getApplicationContext(), getString(R.string.setting_update), Toast.LENGTH_LONG).show();
                    break;
                }
                if(n){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    app.period = Integer.parseInt(r)*AlarmManager.INTERVAL_DAY;
                    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), ReminderService.class);
                    PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), app.getIntentId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + app.period, app.period, pendingIntent);
                }
                Toast.makeText(getApplicationContext(), getString(R.string.setting_update), Toast.LENGTH_LONG).show();
                NavUtils.navigateUpTo(this, new Intent(this, ExamListActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}
