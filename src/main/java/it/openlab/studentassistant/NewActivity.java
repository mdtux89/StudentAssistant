package it.openlab.studentassistant;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//Activity launched for add new exams or edit existing one
public class NewActivity extends PreferenceActivity {

    private boolean existing;
    private String id;
    private SharedPreferences sharedPref;
    private String general,ddl;
    private int guess_slides,guess_pages,guess_notes,guess_exercises,_slides,_pages,_exercises,_notes;
    private StudentAssistantApplication app;

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        if(app.currentname!=null && !app.currentname.equals("")){
            setTitle(app.currentname);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.app = (StudentAssistantApplication)getApplication();
        Intent i = getIntent();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(i.getExtras()==null){
            existing = false;
            id="";
        }
        else{
            existing = true;
            id = i.getExtras().getString("ID");
        }

        if(existing){

            ExamDbHelper helper = new ExamDbHelper(getApplicationContext());
            SQLiteDatabase db = helper.getReadableDatabase();

            // How you want the results sorted in the resulting Cursor
            String sortOrder =
                    ExamContract.Entry.EXAM;

            Cursor c = db.query(
                    ExamContract.Entry.TABLE_NAME,  // The table to query
                    null,                               // The columns to return
                    ExamContract.Entry._ID + " = '"+id+"'",   // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            c.moveToFirst();
            if(c.getCount()>0){
                String name = c.getString(c.getColumnIndex(ExamContract.Entry.EXAM));
                setTitle(name);
                general = name;
                ddl = c.getString(c.getColumnIndex(ExamContract.Entry.DEADLINE));
                guess_slides = c.getInt(c.getColumnIndex(ExamContract.Entry.SLIDESADAY));
                guess_pages = c.getInt(c.getColumnIndex(ExamContract.Entry.PAGESADAY));
                guess_notes = c.getInt(c.getColumnIndex(ExamContract.Entry.NOTESADAY));
                guess_exercises = c.getInt(c.getColumnIndex(ExamContract.Entry.EXERCISESADAY));
                _slides = c.getInt(c.getColumnIndex(ExamContract.Entry.SLIDES));
                _pages = c.getInt(c.getColumnIndex(ExamContract.Entry.PAGES));
                _notes = c.getInt(c.getColumnIndex(ExamContract.Entry.NOTES));
                _exercises = c.getInt(c.getColumnIndex(ExamContract.Entry.EXERCISES));
                sharedPref.edit().putString("general",general).apply();
                sharedPref.edit().putString("dob", ddl).apply();
                sharedPref.edit().putInt("guess_slides", guess_slides).apply();
                sharedPref.edit().putInt("guess_pages", guess_pages).apply();
                sharedPref.edit().putInt("guess_notes", guess_notes).apply();
                sharedPref.edit().putInt("guess_exercises", guess_exercises).apply();
                sharedPref.edit().putInt("slides", _slides).apply();
                sharedPref.edit().putInt("pages", _pages).apply();
                sharedPref.edit().putInt("notes", _notes).apply();
                sharedPref.edit().putInt("exercises", _exercises).apply();
            }
        }
        else{
            Date now = new Date();
            now.setTime(now.getTime()+((1000)*60*60*24));
            SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");
            String defdate = s.format(now);
            sharedPref.edit().putString("dob", defdate).apply();
            sharedPref.edit().putString("general","").apply();
            sharedPref.edit().putInt("guess_slides", 1).apply();
            sharedPref.edit().putInt("guess_pages", 1).apply();
            sharedPref.edit().putInt("guess_notes", 1).apply();
            sharedPref.edit().putInt("guess_exercises", 1).apply();
            sharedPref.edit().putInt("slides", 0).apply();
            sharedPref.edit().putInt("pages", 0).apply();
            sharedPref.edit().putInt("notes", 0).apply();
            sharedPref.edit().putInt("exercises", 0).apply();
        }
    }

    @Override
    public void onBackPressed(){
        Date now = new Date();
        now.setTime(now.getTime()+((1000)*60*60*24));
        SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");
        String defdate = s.format(now);
        ExamDbHelper helper = new ExamDbHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String name = sharedPref.getString("general", "");
        String dd = sharedPref.getString("dob", defdate);
        int g_slides = sharedPref.getInt("guess_slides", 1);
        int g_pages = sharedPref.getInt("guess_pages", 1);
        int g_notes = sharedPref.getInt("guess_notes", 1);
        int g_exercises = sharedPref.getInt("guess_exercises", 1);
        int slides = sharedPref.getInt("slides", 0);
        int pages = sharedPref.getInt("pages", 0);
        int notes = sharedPref.getInt("notes", 0);
        int exercises = sharedPref.getInt("exercises", 0);
        long time = (new Date()).getTime();

        if(!existing){
            guess_slides= guess_exercises = guess_notes = guess_pages = 1;
            _slides= _exercises = _notes = _pages = 0;
            ddl = defdate;
            general = "";
        }

        if(g_slides!=guess_slides || g_pages!=guess_pages || g_notes!=guess_notes ||
        g_exercises!=guess_exercises || slides!=_slides || notes!=_notes || pages!=_pages ||
        exercises!=_exercises || !dd.equals(ddl) || !name.equals(general)){

            values.put(ExamContract.Entry.DEADLINE, dd);
            if(name.equals("")) name = getString(R.string.newname);
            values.put(ExamContract.Entry.EXAM, name);
            values.put(ExamContract.Entry.SLIDESADAY, g_slides);
            values.put(ExamContract.Entry.PAGESADAY, g_pages);
            values.put(ExamContract.Entry.NOTESADAY, g_notes);
            values.put(ExamContract.Entry.EXERCISESADAY, g_exercises);
            values.put(ExamContract.Entry.SLIDES, slides);
            values.put(ExamContract.Entry.PAGES, pages);
            values.put(ExamContract.Entry.NOTES, notes);
            values.put(ExamContract.Entry.EXERCISES, exercises);
            values.put(ExamContract.Entry.SLIDESUPDATED,time);
            values.put(ExamContract.Entry.NOTESUPDATED,time);
            values.put(ExamContract.Entry.EXERCISESUPDATED,time);
            values.put(ExamContract.Entry.PAGESUPDATED,time);

            if(!existing){
                db.insert(
                        ExamContract.Entry.TABLE_NAME,
                        null,
                        values);
            }
            else{

                if(id!=""){
                    db.update(
                            ExamContract.Entry.TABLE_NAME,
                            values,
                            ExamContract.Entry._ID + " = " + id,
                            null
                    );
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    public static class GeneralFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.general_preference);


        }
    }

    public static class SlidesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.slides_preference);
        }
    }

    public static class NotesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.notes_preference);
        }
    }

    public static class TextbookFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.textbook_preference);
        }
    }

    public static class ExercisesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.exercises_preference);
        }
    }
}
