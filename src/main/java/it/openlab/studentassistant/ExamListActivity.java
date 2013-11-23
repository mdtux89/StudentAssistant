package it.openlab.studentassistant;

import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.LinkedList;

/**
 * An activity representing a list of Exams. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ExamDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ExamListFragment} and the item details
 * (if present) is a {@link ExamDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ExamListFragment.Callbacks} interface
 * to listen for item selections.
 */



public class ExamListActivity extends FragmentActivity
        implements ExamListFragment.Callbacks, AchievementsDialog.OnCompletedListener, ExamDetailFragment.OnAchievementListener{
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    private StudentAssistantApplication app;

    @Override
    protected void onNewIntent(Intent intent){
        this.onResume();
    }

    @Override
    protected void onResume(){
        super.onResume();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);
        app = ((StudentAssistantApplication)getApplication());
        if (findViewById(R.id.exam_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            app.mTwoPane = true;
            // In two-pane mode, list items should be given the
            // 'activated' state when touched.

            ((ExamListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.exam_list))
                    .setActivateOnItemClick(true);

            Bundle arguments = new Bundle();
            arguments.putString("ID", null);
            EmptyFragment fragment = new EmptyFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.exam_detail_container, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new:
                Intent newIntent = new Intent(this, NewActivity.class);
                app.currentname = getResources().getString(R.string.newname);
                startActivity(newIntent);
                break;
            case R.id.menu_edit:
                Intent i = new Intent(this, NewActivity.class);
                i.putExtra("ID",app.myId);
                ExamDbHelper helper = new ExamDbHelper(this);
                SQLiteDatabase db = helper.getReadableDatabase();

                Cursor c = db.query(
                        ExamContract.Entry.TABLE_NAME,  // The table to query
                        null,                               // The columns to return
                        ExamContract.Entry._ID + " = '"+app.myId+"'",   // The columns for the WHERE clause
                        null,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                 // The sort order
                );
                c.moveToFirst();
                app.currentname = (c.getString(c.getColumnIndex(ExamContract.Entry.EXAM)));
                startActivity(i);
                break;

            case R.id.menu_delete:
                LinkedList<String> list = new LinkedList<String>();
                list.add(app.myId);
                DialogFragment confirmFragment = new ConfirmationDialog(this,true,list);
                confirmFragment.show(getFragmentManager(), "delete");
                onDeleteCurrent();
                break;
            case R.id.menu_setting:
                Intent settIntent = new Intent(this, SettingsActivity.class);
                startActivity(settIntent);
                break;
            case R.id.menu_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.menu_problems:
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL, new String[] { getString(R.string.mail) });
                Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                Email.putExtra(Intent.EXTRA_TEXT, "" + "");
                startActivity(Intent.createChooser(Email, "Send Feedback:"));
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
            default:
                break;
        }
        return true;
    }

    /**
     * Callback method from {@link ExamListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */

    @Override
    public void onItemSelected(String id) {
        if (app.mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString("ID", id);
            ExamDetailFragment fragment = new ExamDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.exam_detail_container, fragment)
                    .addToBackStack(null)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ExamDetailActivity.class);
            detailIntent.putExtra("ID", id);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onDeleteCurrent() {
        invalidateOptionsMenu();
        EmptyFragment fragment = new EmptyFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.exam_detail_container, fragment)
                .commit();
    }

    @Override
    public void onDeleteDialog(DialogFragment fragment) {
        fragment.show(getFragmentManager(),"delete");
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
}
