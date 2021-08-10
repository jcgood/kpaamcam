package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.QuestionOptionActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.QuestionOption;

public class QuestionOptionTable extends Table<QuestionOption> {

    public static final String TABLE = "QuestionOption";

    // QuestionOption Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_QUESTION_ID = "QuestionId";
    public static final String KEY_QUESTION_LANGUAGE_ID = "QuestionLanguageId";
    public static final String KEY_OPTION_TEXT = "OptionText";

    public static final String KEY_VERSION ="Version";
    public static final String KEY_NOTES ="Notes";
    public static final String KEY_DELETED ="Deleted";


    public QuestionOptionTable() {
        super();
        activityClass = QuestionOptionActivity.class;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT NOT NULL, " + KEY_QUESTION_ID + " TEXT NOT NULL, "
                + KEY_QUESTION_LANGUAGE_ID + " TEXT," + KEY_OPTION_TEXT + " VARCHAR,"
                + KEY_VERSION + " NUMERIC DEFAULT 1.0 NOT NULL," + KEY_NOTES
                + " VARCHAR DEFAULT ''," + KEY_DELETED + " INTEGER DEFAULT 0 NOT NULL,"
                + "PRIMARY KEY(" + KEY_ID + "," + KEY_QUESTION_ID + ", " + KEY_QUESTION_LANGUAGE_ID + "),"
                + " FOREIGN KEY(" + KEY_QUESTION_LANGUAGE_ID + ") REFERENCES " + LanguageTable.TABLE
                + " (" + LanguageTable.KEY_ID + ")," + " FOREIGN KEY(" + KEY_QUESTION_ID + ") REFERENCES "
                + QuestionTable.TABLE + " (" + QuestionTable.KEY_ID + ")" + ")";
    }

    public ArrayList<QuestionOption> getQuestionOptions(String questionId){
        String selection = QuestionOptionTable.KEY_QUESTION_ID + " = ? ";
        String [] selectionArgs = {questionId};
        ArrayList<QuestionOption> questionOptions = DatabaseHelper.QUESTION_OPTION_TABLE.getAll(selection, selectionArgs, null);
        return questionOptions;

    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
