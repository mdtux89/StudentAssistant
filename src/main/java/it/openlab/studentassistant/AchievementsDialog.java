package it.openlab.studentassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Date;

//Dialog showed when user add new achievements. If enabled in settings, try to adjust the study pace for this exam
public class AchievementsDialog extends DialogFragment {
    private OnCompletedListener mListener;
    private String category;
    private double newFreq;
    private StudentAssistantApplication app;

    // Container Activity must implement this interface
    public interface OnCompletedListener {
        public void onCompleted(String id);
    }

    public  AchievementsDialog(){
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCompletedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompletedListener");
        }
    }

    void updateInfo(int x){
        long time = (new Date()).getTime();
        ExamDbHelper helper = new ExamDbHelper(getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = new String[3];
        if(category.equals("Slides")){
            projection[0]=ExamContract.Entry.SLIDES;
            projection[1]=ExamContract.Entry.SLIDESADAY;
            projection[2]=ExamContract.Entry.SLIDESUPDATED;
        }
        else if(category.equals("Notes")){
            projection[0]=ExamContract.Entry.NOTES;
            projection[1]=ExamContract.Entry.NOTESADAY;
            projection[2]=ExamContract.Entry.NOTESUPDATED;
        }
        else if(category.equals("Textbook")){
            projection[0]=ExamContract.Entry.PAGES;
            projection[1]=ExamContract.Entry.PAGESADAY;
            projection[2]=ExamContract.Entry.PAGESUPDATED;
        }
        else {
            projection[0]=ExamContract.Entry.EXERCISES;
            projection[1]=ExamContract.Entry.EXERCISESADAY;
            projection[2]=ExamContract.Entry.EXERCISESUPDATED;
        }

        Cursor c = db.query(
                ExamContract.Entry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                ExamContract.Entry._ID + " = '"+app.myId+"'",   // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        int v1 = c.getInt(c.getColumnIndex(projection[0]));
        v1 = (v1-x);
        if(v1<0){
            v1 = 0;
        }
        int v2 = c.getInt(c.getColumnIndex(projection[1]));
        long v3 = c.getLong((c.getColumnIndex(projection[2])));

        long hours = (time - v3)/3600000;
        long days = (time - v3)/86400000;

        if((days>0 && hours%(days*24)>12)||(days==0 && hours>12)){
            days++;
        }

        Cursor c2 = db.query(
                ExamContract.Entry.TABLE_SETTING,  // The table to query
                null,                               // The columns to return
                null,   // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c2.moveToFirst();
        boolean learningOn;
        if(c2.getString(c2.getColumnIndex(ExamContract.Entry.LEARNING)).equals("true")){
            learningOn=true;
        }
        else learningOn=false;

        ContentValues values = new ContentValues();
        values.put(projection[0], v1);
        values.put(projection[1], v2);
        values.put(projection[2], time);

        if(days>0 && learningOn){
            newFreq = x / days;
            if(newFreq>0){
                values.put(projection[1],newFreq);
                if(newFreq/v2>=2 || newFreq/v2<=0.5){
                    DialogFragment d = new LearningDialog(values,app.myId,mListener);
                    d.show(getFragmentManager(),"learn");
                    return;
                }
                v2=(int)Math.ceil(newFreq);
            }
        }

        db = helper.getWritableDatabase();
        db.update(
                ExamContract.Entry.TABLE_NAME,
                values,
                ExamContract.Entry._ID + " = " + app.myId,
                null
        );
        if (!app.mTwoPane) {
            Intent intent = new Intent(getActivity(), ExamDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            mListener.onCompleted(app.myId);
        }
        Toast.makeText(getActivity(), getString(R.string.updated), Toast.LENGTH_SHORT).show();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        app = ((StudentAssistantApplication)getActivity().getApplication());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);
        Spinner spinner = (Spinner) view.findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getArguments().getInt("cat"));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,int pos, long id) {
                category = (String) parent.getItemAtPosition(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        final NumberPicker np = (NumberPicker) view.findViewById(R.id.number);
        np.setMinValue(0);
        np.setMaxValue(1000);
        builder.setMessage(R.string.update)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int x = np.getValue();
                        updateInfo(x);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
       return builder.create();
    }

}