package it.openlab.studentassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//The dbHelper with two table: one for the exams, the other one for the app settings
public class ExamDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 13;
        public static final String DATABASE_NAME = "Exams.db";

        public ExamDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + ExamContract.Entry.TABLE_NAME + " (" +
                            ExamContract.Entry._ID + " INTEGER PRIMARY KEY," +
                            ExamContract.Entry.PAGESUPDATED + " LONG" + "," +
                            ExamContract.Entry.NOTESUPDATED + " LONG" + "," +
                            ExamContract.Entry.SLIDESUPDATED + " LONG" + "," +
                            ExamContract.Entry.EXERCISESUPDATED + " LONG" + "," +
                            ExamContract.Entry.EXAM + " TEXT" + "," +
                            ExamContract.Entry.DEADLINE + " TEXT" + "," +
                            ExamContract.Entry.EXERCISES + " INTEGER" + "," +
                            ExamContract.Entry.EXERCISESADAY + " INTEGER" + "," +
                            ExamContract.Entry.NOTES + " INTEGER" + "," +
                            ExamContract.Entry.NOTESADAY + " INTEGER" + "," +
                            ExamContract.Entry.PAGES + " INTEGER" + "," +
                            ExamContract.Entry.PAGESADAY + " INTEGER" + "," +
                            ExamContract.Entry.SLIDES + " INTEGER" + "," +
                            ExamContract.Entry.SLIDESADAY + " INTEGER" +
                            " )"
            );
            db.execSQL(
                    "CREATE TABLE " + ExamContract.Entry.TABLE_SETTING + " (" +
                            ExamContract.Entry._ID + " INTEGER PRIMARY KEY," +
                            ExamContract.Entry.NOTIFICATION + " TEXT" + "," +
                            ExamContract.Entry.FREQUENCY + " INTEGER" + "," +
                            ExamContract.Entry.LEARNING + " TEXT" +
                            " )"
            );
            ContentValues values = new ContentValues();
            values.put(ExamContract.Entry.NOTIFICATION, "false");
            values.put(ExamContract.Entry.LEARNING, "false");
            values.put(ExamContract.Entry.FREQUENCY, "7");
            db.insert(ExamContract.Entry.TABLE_SETTING,null,values);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ExamContract.Entry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ExamContract.Entry.TABLE_SETTING);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
}