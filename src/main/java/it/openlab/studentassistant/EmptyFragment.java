package it.openlab.studentassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//Fragment showed in place of ExamDetailFragment in case of empty state
public class EmptyFragment extends Fragment {

    private StudentAssistantApplication app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.app = (StudentAssistantApplication)getActivity().getApplication();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if(app.adapter!=null)app.adapter.noSelected();
        View rootView = inflater.inflate(R.layout.fragment_exam_detail, container, false);
        rootView.findViewById(R.id.emptytext).setVisibility(View.VISIBLE);
        if(app.mTwoPane){
            ((TextView)rootView.findViewById(R.id.emptytext)).setText(R.string.start);
        }
        else{
            ((TextView)rootView.findViewById(R.id.emptytext)).setText(R.string.empty);
        }
        return rootView;
    }

}
