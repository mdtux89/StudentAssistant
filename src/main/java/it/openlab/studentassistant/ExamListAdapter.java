package it.openlab.studentassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

//The custom list adapter for the exams list
public class ExamListAdapter extends ArrayAdapter<ExamRow> {

    private final Context context;
    private final LinkedList<ExamRow> values;
    private boolean[] selectedIndexes;

    public ExamListAdapter(Context context, LinkedList<ExamRow> values) {
        super(context, R.layout.row, values);
        this.context = context;
        this.values = values;
        if(values!=null){
            selectedIndexes = new boolean[values.size()];
            for(int i=0;i<values.size();i++){
                selectedIndexes[i]=false;
            }
        }
    }

    public void setSelectedIndex(int ind)
    {
        selectedIndexes[ind] = true;
        notifyDataSetChanged();
    }

    public void setDeselectedIndex(int ind)
    {
        selectedIndexes[ind] = false;
        notifyDataSetChanged();
    }

    public void noSelected()
    {
        for(int i=0;i<selectedIndexes.length;i++){
            selectedIndexes[i] = false;
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textRow);
        TextView textView2 = (TextView) rowView.findViewById(R.id.textRow2);
        ImageView image = (ImageView) rowView.findViewById(R.id.image);
        textView.setText(values.get(position).getName());
        textView2.setText(values.get(position).getDeadline());
        if(values.get(position).getStatus()==-1){
            image.setImageResource(R.color.red);
        }
        else if(values.get(position).getStatus()==0){
            image.setImageResource(R.color.yellow);
        }
        else{
            image.setImageResource(R.color.green);
        }
        if(selectedIndexes[position])
        {
            rowView.setBackgroundResource(R.drawable.borderspressed);
        }
        else
        {
            rowView.setBackgroundResource(R.drawable.borderslist);
        }
        return rowView;
    }
}
