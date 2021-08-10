package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import edu.buffalo.cse.ubcollecting.data.models.Question;
import edu.buffalo.cse.ubcollecting.ui.AddQuestionsActivity;

public class QuestionTable extends Table<Question> {

    public static final String TABLE = "Question";

    // Question (QuestionPool) Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_TYPE = "Type";
    public static final String KEY_DISPLAY_TEXT = "DisplayText";

    public static final String KEY_VERSION ="Version";
    public static final String KEY_NOTES ="Notes";
    public static final String KEY_DELETED ="Deleted";


    public QuestionTable() {
        super();
        activityClass = AddQuestionsActivity.class;
    }

    @Override
    public String createTable() {
//        Log.i("QuestionTable", TABLE);
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY NOT NULL, "
                + KEY_TYPE + " VARCHAR, "
                + KEY_DISPLAY_TEXT + " VARCHAR,"
                + KEY_VERSION + " NUMERIC DEFAULT 1.0 NOT NULL,"
                + KEY_NOTES + " VARCHAR DEFAULT '',"
                + KEY_DELETED + " INTEGER DEFAULT 0 NOT NULL"+ ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
