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


    public QuestionTable() {
        super();
        activityClass = AddQuestionsActivity.class;
    }

    @Override
    public String createTable() {
//        Log.i("QuestionTable", TABLE);
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY, " + KEY_TYPE + " VARCHAR, " + KEY_DISPLAY_TEXT + " VARCHAR" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
