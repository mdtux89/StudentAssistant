package it.openlab.studentassistant;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import java.util.LinkedList;

//Service started each day or week used to trigger the notification (for those exams which are late)
public class ReminderService extends Service {
    private StudentAssistantApplication app;
    private NotificationManager mNM;

    @Override
    public void onCreate() {
        app = (StudentAssistantApplication) getApplication();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        show();
        stopSelf();
        return START_STICKY;
    }

    private void show(){
        ExamDbHelper helper = new ExamDbHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(
                ExamContract.Entry.TABLE_SETTING,  // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        if(c.getString(c.getColumnIndex(ExamContract.Entry.NOTIFICATION)).equals("false")) return;
        db = helper.getReadableDatabase();
        String sortOrder =
                ExamContract.Entry.DEADLINE ;
        c = db.query(
                ExamContract.Entry.TABLE_NAME,  // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.moveToFirst();
        int x = c.getColumnIndex(ExamContract.Entry.EXAM);
        int y = c.getColumnIndex(ExamContract.Entry._ID);
        String id = c.getString(y);
        int z = c.getColumnIndex(ExamContract.Entry.DEADLINE);
        int count = c.getCount();
        LinkedList<String> names = new LinkedList<String>();
        LinkedList<String> ids = new LinkedList<String>();
        for(int i=0; i<count; i++){
            ExamAnalyzer analyzer = new ExamAnalyzer(c.getString(z),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.NOTES)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.NOTESADAY)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.SLIDES)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.SLIDESADAY)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.EXERCISES)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.EXERCISESADAY)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.PAGES)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.PAGESADAY)));
            ExamRow e = new ExamRow(c.getString(x),c.getString(z),analyzer.analysis());
            if(e.getStatus()==-1){
                names.add(c.getString(x));
                ids.add(c.getString(y));
            }
            c.moveToNext();
        }
        //Notification
        NotificationCompat.Builder mBuilder;
        if(names.size()>=1){
            mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.notification)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(names.size()+getString(R.string.bigNotifText))
                    .setAutoCancel(true);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(getString(R.string.bigNotifTitle));
            for (int i=0; i < names.size(); i++) {
                inboxStyle.addLine(names.get(i));
            }
            mBuilder.setStyle(inboxStyle);
            TaskStackBuilder stackBuilder;
            Intent resultIntent = new Intent(getApplicationContext(), ExamListActivity.class);
            stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(ExamListActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
        }
        else{
            return;
        }

        int nId = app.getIntentId();
        mNM.cancel(nId-1);
        mNM.notify(nId, mBuilder.build());
    }
}