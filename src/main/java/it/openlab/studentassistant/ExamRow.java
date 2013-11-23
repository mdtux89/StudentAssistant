package it.openlab.studentassistant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


//Information needed in the exams list to be displayed for each row
public class ExamRow {

    private String name;
    private String deadline;
    private int status;

    public ExamRow(String name, String deadline, int status){
        this.name = name;
        this.deadline = deadline;
        this.status = status;
    }

    public String getName(){
        return name;
    }

    public int getStatus(){
        return status;
    }

    public String getDeadline(){
        if(deadline.equals(""))return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date d = null;
        try {
            d = sdf.parse(deadline);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern("dd/MM/yyyy");
        return sdf.format(d);
    }

}
