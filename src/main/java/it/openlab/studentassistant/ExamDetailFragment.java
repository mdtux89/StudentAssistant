package it.openlab.studentassistant;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * A fragment representing a single Exam detail screen.
 * This fragment is either contained in a {@link ExamListActivity}
 * in two-pane mode (on tablets) or a {@link ExamDetailActivity}
 * on handsets.
 */

public class ExamDetailFragment extends Fragment {
    private View rootView;
    private String dd;
    private double notes, notesaday, slides, slidesaday, exercises, exercisesaday, pages, pagesaday;
    private StudentAssistantApplication app;
    private OnAchievementListener mListener;

    // Container Activity must implement this interface
    public interface OnAchievementListener {
        public void onAchievement(int index, double notes,double notesaday,double slides,double slidesaday,double exercises,double exercisesaday,double pages,double pagesaday);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAchievementListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnAchievementListener");
        }
    }

    private void analysis(int diff, double tot){
        rootView.findViewById(R.id.emptytext).setVisibility(View.GONE);
        rootView.findViewById(R.id.exam_detail_container_inner).setVisibility(View.VISIBLE);
        if(!app.mTwoPane){
            rootView.findViewById(R.id.exam_name).setVisibility(View.GONE);
            rootView.findViewById(R.id.separator).setVisibility(View.GONE);
        }

        ((TextView)rootView.findViewById(R.id.exam_name)).setText(app.currentname);
        TextView t1 = (TextView) rootView.findViewById(R.id.output1);
        t1.setText(getString(R.string.output_deadline)+ " " + diff+ " "+getString(R.string.days)+"\n");
        t1.append(getString(R.string.output_required) + " " + (int)tot + " "+getString(R.string.days));

        ArrayList<TextView> t = new ArrayList<TextView>();
        t.add(0, (TextView) rootView.findViewById(R.id.output2));
        t.add(1,(TextView) rootView.findViewById(R.id.output3));
        t.add(2,(TextView) rootView.findViewById(R.id.output4));
        t.add(3,(TextView) rootView.findViewById(R.id.output5));
        ArrayList<RelativeLayout> l = new ArrayList<RelativeLayout>();
        l.add(0, (RelativeLayout)rootView.findViewById(R.id.reloutput2));
        l.add(1, (RelativeLayout)rootView.findViewById(R.id.reloutput3));
        l.add(2, (RelativeLayout)rootView.findViewById(R.id.reloutput4));
        l.add(3, (RelativeLayout)rootView.findViewById(R.id.reloutput5));
        ArrayList<View> v = new ArrayList<View>();
        v.add(0, rootView.findViewById(R.id.view2));
        v.add(1, rootView.findViewById(R.id.view3));
        v.add(2, rootView.findViewById(R.id.view4));
        if(slides==0 && notes==0 && exercises==0 && pages==0){
            t.get(0).setText(getString(R.string.done));
            l.get(0).setVisibility(View.VISIBLE);
            v.get(0).setVisibility(View.GONE);
            v.get(1).setVisibility(View.GONE);
            v.get(2).setVisibility(View.GONE);
            return;
        }
        int i=0;
        if(slides>0){
            t.get(i).setText((int)slides + " " + getString(R.string.Slides) + getString(R.string.left) + (int)Math.ceil(slides / slidesaday) + " "+getString(R.string.days)+")\n");
            l.get(i).setVisibility(View.VISIBLE);
            l.get(i).setOnClickListener(handler1);
            i++;
        }
        if(notes>0){
            t.get(i).setText((int)notes + " " + getString(R.string.Notes) + getString(R.string.left) + (int)Math.ceil(notes / notesaday) + " "+getString(R.string.days)+")\n");
            l.get(i).setVisibility(View.VISIBLE);
            if(i>0)v.get(i-1).setVisibility(View.VISIBLE);
            l.get(i).setOnClickListener(handler2);
            i++;
        }
        if(pages>0){
            t.get(i).setText((int)pages + " " + getString(R.string.Pages) + getString(R.string.left) + (int)Math.ceil(pages / pagesaday) + " "+getString(R.string.days)+")\n");
            l.get(i).setVisibility(View.VISIBLE);
            if(i>0)v.get(i-1).setVisibility(View.VISIBLE);
            l.get(i).setOnClickListener(handler3);
            i++;
        }
        if(exercises>0){
            t.get(i).setText((int)exercises + " " + getString(R.string.Exercises) + getString(R.string.left) + (int)Math.ceil(exercises / exercisesaday) + " "+getString(R.string.days)+")\n");
            l.get(i).setVisibility(View.VISIBLE);
            if(i>0)v.get(i-1).setVisibility(View.VISIBLE);
            l.get(i).setOnClickListener(handler4);
            i++;
        }
        while(i<4){
            t.get(i).setText("");
            l.get(i).setVisibility(View.GONE);
            if(i>0)v.get(i-1).setVisibility(View.GONE);
            i++;
        }
    }

    private void empty(){
        (rootView.findViewById(R.id.exam_detail_container_inner)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.emptytext)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.view2)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.view3)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.view4)).setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = ((StudentAssistantApplication)getActivity().getApplication());
        setHasOptionsMenu(true);
        if (getArguments().containsKey("ID")) {
            app.myId = getArguments().getString("ID");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (app.myId != null) {
            ExamDbHelper helper = new ExamDbHelper(getActivity());
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
            if(c.getCount()==0){
                empty();
                return;
            }
            c.moveToFirst();

            app.currentname = c.getString(c.getColumnIndex(ExamContract.Entry.EXAM));
            dd = c.getString(c.getColumnIndex(ExamContract.Entry.DEADLINE));
            notes = c.getInt(c.getColumnIndex(ExamContract.Entry.NOTES));
            notesaday = c.getInt(c.getColumnIndex(ExamContract.Entry.NOTESADAY));
            slides = c.getInt(c.getColumnIndex(ExamContract.Entry.SLIDES));
            slidesaday = c.getInt(c.getColumnIndex(ExamContract.Entry.SLIDESADAY));
            pages = c.getInt(c.getColumnIndex(ExamContract.Entry.PAGES));
            pagesaday = c.getInt(c.getColumnIndex(ExamContract.Entry.PAGESADAY));
            exercises = c.getInt(c.getColumnIndex(ExamContract.Entry.EXERCISES));
            exercisesaday = c.getInt(c.getColumnIndex(ExamContract.Entry.EXERCISESADAY));

            ExamAnalyzer analyzer = new ExamAnalyzer(dd,notes,notesaday,slides,slidesaday,exercises,exercisesaday,pages,pagesaday);
            analyzer.analysis();
            int diff = analyzer.getDiff();
            double tot = analyzer.getTot();
            analysis(diff,tot);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_exam_detail, container, false);
        return rootView;
    }

    View.OnClickListener handler1 = new View.OnClickListener() {
        public void onClick(View v) {
            mListener.onAchievement(0,notes,notesaday,slides,slidesaday,exercises,exercisesaday,pages,pagesaday);
        }
    };
    View.OnClickListener handler2 = new View.OnClickListener() {
        public void onClick(View v) {
            mListener.onAchievement(1,notes,notesaday,slides,slidesaday,exercises,exercisesaday,pages,pagesaday);
        }
    };
    View.OnClickListener handler3 = new View.OnClickListener() {
        public void onClick(View v) {
            mListener.onAchievement(2,notes,notesaday,slides,slidesaday,exercises,exercisesaday,pages,pagesaday);
        }
    };
    View.OnClickListener handler4 = new View.OnClickListener() {
        public void onClick(View v) {
            mListener.onAchievement(3,notes,notesaday,slides,slidesaday,exercises,exercisesaday,pages,pagesaday);
        }
    };
}
