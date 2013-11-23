package it.openlab.studentassistant;

import android.provider.BaseColumns;

public final class ExamContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ExamContract() {}

    /* Inner class that defines the table contents */
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String SLIDESUPDATED = "s_updated";
        public static final String NOTESUPDATED = "n_updated";
        public static final String PAGESUPDATED = "p_updated";
        public static final String EXERCISESUPDATED = "e_updated";
        public static final String EXAM = "exam";
        public static final String DEADLINE = "deadline";
        public static final String SLIDESADAY = "slidesaday";
        public static final String SLIDES = "slides";
        public static final String PAGESADAY = "pageasday";
        public static final String PAGES = "pages";
        public static final String NOTESADAY = "notesaday";
        public static final String NOTES = "notes";
        public static final String EXERCISESADAY = "exercisesaday";
        public static final String EXERCISES = "exercises";

        public static final String TABLE_SETTING = "settings";
        public static final String NOTIFICATION = "notification";
        public static final String FREQUENCY = "freq";
        public static final String LEARNING = "learning";
    }
}