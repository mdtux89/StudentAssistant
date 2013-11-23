package it.openlab.studentassistant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//Utility class that computes the remaining time and the lateness for an exam
public class ExamAnalyzer {

    private double notes, notesaday, slides, slidesaday, exercises, exercisesaday, pages, pagesaday;
    private String dd;
    private double tot;
    private int diff;

    public ExamAnalyzer(String dd, double notes, double notesaday, double slides, double slidesaday, double exercises,
                        double exercisesaday, double pages, double pagesaday){
        this.notes = notes;
        this.notesaday = notesaday;
        this.slides = slides;
        this.slidesaday = slidesaday;
        this.exercises = exercises;
        this.exercisesaday = exercisesaday;
        this.pages = pages;
        this.pagesaday = pagesaday;
        this.dd = dd;
    }

    public double getTot(){
        return tot;
    }

    public int getDiff(){
        return diff;
    }

    private int computeDeadline(){
        Date now = new Date();
        Calendar nowCal = new GregorianCalendar();
        nowCal.setTime(now);

        //I will need to know how many day this year..
        int currentYear = nowCal.get(Calendar.YEAR);
        Date nye;
        Calendar nyeCal = new GregorianCalendar();
        try {
            nye = new SimpleDateFormat("MM/dd/yy").parse("12/31/"+currentYear);
            nyeCal.setTime(nye);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int lastdayofyear = nyeCal.get(Calendar.DAY_OF_YEAR);

        //Deadline
        Date deadline;
        Calendar deadlineCal = new GregorianCalendar();
        try {
            deadline = new SimpleDateFormat("yyyy.MM.dd").parse(dd);
            deadlineCal.setTime(deadline);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(nowCal.after(deadlineCal)){
            return -1;
        }

        //Let's count the remaining days..
        int diff = 0;
        int diffy = deadlineCal.get(Calendar.YEAR)-nowCal.get(Calendar.YEAR);
        if(diffy==0) diff = deadlineCal.get(Calendar.DAY_OF_YEAR)- nowCal.get(Calendar.DAY_OF_YEAR);
        else diff = lastdayofyear - nowCal.get(Calendar.DAY_OF_YEAR);
        for(int i=0; i<diffy ; i++){
            diff += deadlineCal.get(Calendar.DAY_OF_YEAR);
        }
        diff--; // last day is for praying =)..
        return diff;
    }

    private double computeRemaining(){
        double tot = 0;
        if(slides!=0)tot += slides / slidesaday;
        if(pages!=0)tot += pages / pagesaday;
        if(notes!=0)tot += notes / notesaday;
        if(exercises!=0)tot += exercises / exercisesaday;
        tot = Math.ceil(tot);
        return tot;
    }

    public int analysis(){
        diff = computeDeadline();
        tot = computeRemaining();

        if(tot>diff){
            return -1;
        }
        else if(tot<diff){
            return 1;
        }
        else {
            return 0;
        }
    }

}
