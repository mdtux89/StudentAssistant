package it.openlab.studentassistant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

//Dialog showed when the learning procedure try to change a lot the study pace
public class LearningDialog extends DialogFragment {

    private String myId;
    private ContentValues values;
    private AchievementsDialog.OnCompletedListener mListener;
    private StudentAssistantApplication app;

    public LearningDialog(ContentValues values, String id, AchievementsDialog.OnCompletedListener mListener){
        this.values = values;
        this.myId = id;
        this.mListener = mListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        app = (StudentAssistantApplication) getActivity().getApplication();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.learning_dialog)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ExamDbHelper helper = new ExamDbHelper(getActivity());
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.update(
                                ExamContract.Entry.TABLE_NAME,
                                values,
                                ExamContract.Entry._ID + " = " + myId,
                                null
                        );
                        if (!app.mTwoPane) {
                            Intent intent = new Intent(getActivity(), ExamDetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        } else {
                            mListener.onCompleted(myId);
                        }
                        Toast.makeText(getActivity(), getString(R.string.updated), Toast.LENGTH_SHORT).show();
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