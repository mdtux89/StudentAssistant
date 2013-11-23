package it.openlab.studentassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.Toast;
import java.util.LinkedList;

//Confirmation dialog for delete operation
public class ConfirmationDialog extends DialogFragment {

    private Activity a;
    private boolean menu;
    private LinkedList<String> codes;

    ConfirmationDialog(Activity a, boolean menu, LinkedList<String> ids){
        this.menu = menu;
        this.a = a;
        this.codes = ids;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LinkedList<String> codes = this.codes;
        String title;
        if(codes.size()==1){
            title = getString(R.string.confirmation_dialog);
        }
        else{
            title = getString(R.string.confirmation_dialog_more);
        }
        builder.setMessage(title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ExamDbHelper helper = new ExamDbHelper(getActivity());
                        SQLiteDatabase db = helper.getWritableDatabase();
                        for(int i=0;i<codes.size();i++){
                            String selection = ExamContract.Entry._ID + " LIKE ? ";
                            String[] selectionArgs = { codes.get(i) };
                            db.delete(ExamContract.Entry.TABLE_NAME, selection, selectionArgs);
                        }
                        if(!menu) NavUtils.navigateUpTo(a, new Intent(a, ExamListActivity.class));
                        else{
                            Intent intent = new Intent(getActivity(), ExamListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                        Toast.makeText(a, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}