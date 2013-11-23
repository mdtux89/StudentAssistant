package it.openlab.studentassistant;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.LinkedList;
import java.util.List;

/**
 * A list fragment representing a list of Exams. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ExamDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ExamListFragment extends android.support.v4.app.ListFragment {

    private LinkedList<String> codes;
    private int selected;
    private List<String> idArray;
    private LinkedList<ExamRow> items;
    private StudentAssistantApplication app;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selected=0;
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                if(checked){
                    ExamListAdapter adapter = (ExamListAdapter) getListAdapter();
                    adapter.setSelectedIndex(position);
                    codes.add(String.valueOf(idArray.get(position)));
                    selected++;
                }
                else{
                    ExamListAdapter adapter = (ExamListAdapter) getListAdapter();
                    adapter.setDeselectedIndex(position);
                    codes.remove(String.valueOf(idArray.get(position)));
                    selected--;
                }
                if(selected==1) mode.setTitle(selected+" "+getString(R.string.onselected));
                else mode.setTitle(selected+" "+getString(R.string.moreselected));
                mode.invalidate();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.menu_edit:
                        Intent i = new Intent(getActivity(), NewActivity.class);
                        i.putExtra("ID",codes.get(0));
                        ExamDbHelper helper = new ExamDbHelper(getActivity());
                        SQLiteDatabase db = helper.getReadableDatabase();

                        Cursor c = db.query(
                                ExamContract.Entry.TABLE_NAME,  // The table to query
                                null,                               // The columns to return
                                ExamContract.Entry._ID + " = '"+codes.get(0)+"'",   // The columns for the WHERE clause
                                null,                            // The values for the WHERE clause
                                null,                                     // don't group the rows
                                null,                                     // don't filter by row groups
                                null                                 // The sort order
                        );
                        c.moveToFirst();
                        app.currentname = (c.getString(c.getColumnIndex(ExamContract.Entry.EXAM)));
                        startActivity(i);
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.menu_delete:
                        DialogFragment newFragment = new ConfirmationDialog(getActivity(),true,codes);
                        mCallbacks.onDeleteDialog(newFragment);
                        for(int j=0; j<codes.size(); j++){
                            if(app.myId==codes.get(j)){
                                mCallbacks.onDeleteCurrent();
                                break;
                            }
                        }
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_context, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
                selected=0;
                ExamListAdapter adapter = (ExamListAdapter) getListAdapter();
                adapter.noSelected();

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                if (selected == 1){
                    MenuItem item = menu.findItem(R.id.menu_edit);
                    item.setVisible(true);
                    return true;
                } else {
                    MenuItem item = menu.findItem(R.id.menu_edit);
                    item.setVisible(false);
                    return true;
                }
            }
        });

    }

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private Callbacks mCallbacks = sCallbacks;

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
  //  private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
        public void onDeleteCurrent();
        public void onDeleteDialog(DialogFragment fragment);
    }

    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
        public void onDeleteCurrent(){
        }
        public void onDeleteDialog(DialogFragment fragment){
        }
    };

    public ExamListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        final ExamDbHelper helper = new ExamDbHelper(getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ExamContract.Entry.DEADLINE ;

        Cursor c = db.query(
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
        int z = c.getColumnIndex(ExamContract.Entry.DEADLINE);
        int count = c.getCount();
        if(count==0){
            getActivity().findViewById(R.id.emptytext).setVisibility(View.VISIBLE);
        }
        else{
            getActivity().findViewById(R.id.emptytext).setVisibility(View.GONE);
        }
        idArray = new LinkedList<String>();
        codes = new LinkedList<String>();
        items = new LinkedList<ExamRow>();
        for(int i=0; i<count; i++){
            ExamAnalyzer analyzer = new ExamAnalyzer(c.getString(z),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.NOTES)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.NOTESADAY)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.SLIDES)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.SLIDESADAY)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.EXERCISES)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.EXERCISESADAY)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.PAGES)),(double)c.getInt(c.getColumnIndex(ExamContract.Entry.PAGESADAY)));
            ExamRow e = new ExamRow(c.getString(x),c.getString(z),analyzer.analysis());
            items.add(e);
            idArray.add(c.getString(y));
            c.moveToNext();
        }
        final ExamListAdapter adapter;
        if(app.mTwoPane && count==0){
            adapter = null;
            ArrayAdapter<String> notaskAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    new String[]{getString(R.string.menu_new)});
            setListAdapter(notaskAdapter);
        }
        else {
            adapter = new ExamListAdapter(
                    getActivity(),
                    items);
            app.adapter = adapter;
            setListAdapter(adapter);
        }

        ListView listView = getListView();
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            private ExamRow removedItem;
                            private int pos;
                            private double notes, notesaday, slides, slidesaday, pages, pagesaday, exercises, exercisesaday;
                            private long slidesupdated, notesupdated, pagesupdated, exercisesupdated;
                            private String dd, name;
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                UndoBarController.UndoListener listener = new UndoBarController.UndoListener() {
                                    @Override
                                    public void onUndo(Parcelable parcelable) {
                                        if(removedItem==null) return;
                                        items.add(pos,removedItem);
                                        removedItem = null;
                                        if(!app.mTwoPane){
                                            getActivity().findViewById(R.id.emptytext).setVisibility(View.GONE);
                                        }
                                        SQLiteDatabase db = helper.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        values.put(ExamContract.Entry._ID, idArray.get(pos));
                                        values.put(ExamContract.Entry.DEADLINE, dd);
                                        values.put(ExamContract.Entry.EXAM, name);
                                        values.put(ExamContract.Entry.SLIDESADAY, slidesaday);
                                        values.put(ExamContract.Entry.PAGESADAY, pagesaday);
                                        values.put(ExamContract.Entry.NOTESADAY, notesaday);
                                        values.put(ExamContract.Entry.EXERCISESADAY, exercisesaday);
                                        values.put(ExamContract.Entry.SLIDES, slides);
                                        values.put(ExamContract.Entry.PAGES, pages);
                                        values.put(ExamContract.Entry.NOTES, notes);
                                        values.put(ExamContract.Entry.EXERCISES, exercises);
                                        values.put(ExamContract.Entry.SLIDESUPDATED,slidesupdated);
                                        values.put(ExamContract.Entry.NOTESUPDATED,notesupdated);
                                        values.put(ExamContract.Entry.EXERCISESUPDATED,exercisesupdated);
                                        values.put(ExamContract.Entry.PAGESUPDATED,pagesupdated);
                                        db.insert(ExamContract.Entry.TABLE_NAME, null, values);
                                        adapter.notifyDataSetChanged();
                                    }
                                };

                                for (int position : reverseSortedPositions) {
                                    adapter.noSelected();
                                    codes.clear();
                                    selected=0;
                                    UndoBarController.show(getActivity(), getString(R.string.undomess), listener);
                                    removedItem = items.remove(position);
                                    pos = position;
                                    if(items.size()==0 && !app.mTwoPane){
                                        getActivity().findViewById(R.id.emptytext).setVisibility(View.VISIBLE);
                                    }
                                    SQLiteDatabase db = helper.getReadableDatabase();
                                    Cursor c = db.query(
                                            ExamContract.Entry.TABLE_NAME,  // The table to query
                                            null,                               // The columns to return
                                            ExamContract.Entry._ID + " = '"+idArray.get(position)+"'",   // The columns for the WHERE clause
                                            null,                            // The values for the WHERE clause
                                            null,                                     // don't group the rows
                                            null,                                     // don't filter by row groups
                                            null                                 // The sort order
                                    );
                                    c.moveToFirst();
                                    name = c.getString(c.getColumnIndex(ExamContract.Entry.EXAM));
                                    dd = c.getString(c.getColumnIndex(ExamContract.Entry.DEADLINE));
                                    notes = c.getInt(c.getColumnIndex(ExamContract.Entry.NOTES));
                                    notesaday = c.getInt(c.getColumnIndex(ExamContract.Entry.NOTESADAY));
                                    slides = c.getInt(c.getColumnIndex(ExamContract.Entry.SLIDES));
                                    slidesaday = c.getInt(c.getColumnIndex(ExamContract.Entry.SLIDESADAY));
                                    pages = c.getInt(c.getColumnIndex(ExamContract.Entry.PAGES));
                                    pagesaday = c.getInt(c.getColumnIndex(ExamContract.Entry.PAGESADAY));
                                    exercises = c.getInt(c.getColumnIndex(ExamContract.Entry.EXERCISES));
                                    exercisesaday = c.getInt(c.getColumnIndex(ExamContract.Entry.EXERCISESADAY));
                                    notesupdated = c.getLong(c.getColumnIndex(ExamContract.Entry.NOTESUPDATED));
                                    slidesupdated = c.getLong(c.getColumnIndex(ExamContract.Entry.SLIDESUPDATED));
                                    pagesupdated = c.getLong(c.getColumnIndex(ExamContract.Entry.PAGESUPDATED));
                                    exercisesupdated = c.getLong(c.getColumnIndex(ExamContract.Entry.EXERCISESUPDATED));
                                    db = helper.getWritableDatabase();
                                    String selection = ExamContract.Entry._ID + " LIKE ? ";
                                    String[] selectionArgs = { idArray.get(position) };
                                    db.delete(ExamContract.Entry.TABLE_NAME, selection, selectionArgs);
                                    if(app.myId==idArray.get(position)){
                                        mCallbacks.onDeleteCurrent();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        app = ((StudentAssistantApplication)getActivity().getApplication());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        if(idArray.size()>0){
            super.onListItemClick(listView, view, position, id);
            for(int i=0;i<listView.getChildCount();i++){
                listView.getChildAt(i).setBackgroundResource(R.drawable.b1);
            }
            listView.getChildAt(position).setBackgroundResource(R.drawable.b2);
            mCallbacks.onItemSelected(String.valueOf(idArray.get(position)));
        }
        else if(app.mTwoPane){
            Intent newIntent = new Intent(getActivity(), NewActivity.class);
            app.currentname = getResources().getString(R.string.newname);
            startActivity(newIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
