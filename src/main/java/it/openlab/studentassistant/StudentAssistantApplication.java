package it.openlab.studentassistant;

import android.app.Application;

//Contains shared variables
public class StudentAssistantApplication extends Application {

    public String myId = null;
    public boolean mTwoPane;
    public ExamListAdapter adapter;
    public String currentname;
    public long period;

    private int intentId=0;

    public int getIntentId() {
        return intentId++;
    }

}
