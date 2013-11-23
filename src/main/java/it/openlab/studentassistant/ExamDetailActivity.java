package it.openlab.studentassistant;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.app.DialogFragment;
import java.util.LinkedList;

/**
 * An activity representing a single Exam detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ExamListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ExamDetailFragment}.
 */

public class ExamDetailActivity extends FragmentActivity
        implements AchievementsDialog.OnCompletedListener, ExamDetailFragment.OnAchievementListener, ExamListFragment.Callbacks{

    private StudentAssistantApplication app;

    @Override
    public void onResume(){
            super.onResume();
            app.myId = getIntent().getStringExtra("ID");
            ExamDbHelper helper = new ExamDbHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();

            // How you want the results sorted in the resulting Cursor
            String sortOrder =
                    ExamContract.Entry.EXAM;

            Cursor c = db.query(
                    ExamContract.Entry.TABLE_NAME,  // The table to query
                    null,                               // The columns to return
                    ExamContract.Entry._ID + " = '"+app.myId+"'",   // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            c.moveToFirst();
            app.currentname = (c.getString(c.getColumnIndex(ExamContract.Entry.EXAM)));
            String name = c.getString(c.getColumnIndex(ExamContract.Entry.EXAM));
            setTitle(name);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        app = ((StudentAssistantApplication)getApplication());

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString("ID",
                    getIntent().getStringExtra("ID"));
            ExamDetailFragment fragment = new ExamDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.exam_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                Intent i = new Intent(this, NewActivity.class);
                i.putExtra("ID",app.myId);
                startActivity(i);
                break;

            case R.id.menu_delete:
                LinkedList<String> list = new LinkedList<String>();
                list.add(app.myId);
                DialogFragment newFragment = new ConfirmationDialog(this,false,list);
                newFragment.show(getFragmentManager(), "delete");
                break;

            case R.id.menu_achievements:
                DialogFragment anotherFragment = new AchievementsDialog();
                Bundle args = new Bundle();
                args.putInt("cat",0);
                anotherFragment.setArguments(args);
                anotherFragment.show(getFragmentManager(),"update");
                break;

            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, ExamListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleted(String id) {
        Bundle arguments = new Bundle();
        arguments.putString("ID", id);
        ExamDetailFragment fragment = new ExamDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.exam_detail_container, fragment)
                .commit();
    }

    @Override
    public void onAchievement(int index, double notes, double notesaday, double slides, double slidesaday, double exercises, double exercisesaday, double pages, double pagesaday) {
        Bundle args = new Bundle();
        args.putInt("cat",index);
        DialogFragment newFragment = new AchievementsDialog();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(),"update");
    }

    @Override
    public void onItemSelected(String id) {
    }

    @Override
    public void onDeleteCurrent() {
        EmptyFragment fragment = new EmptyFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.exam_detail_container, fragment)
                .commit();
    }

    @Override
    public void onDeleteDialog(DialogFragment fragment) {
    }
}
