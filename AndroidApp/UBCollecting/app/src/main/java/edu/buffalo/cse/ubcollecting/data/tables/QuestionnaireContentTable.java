package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import android.util.Log;

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.QuestionnaireContentActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;


public class QuestionnaireContentTable extends Table<QuestionnaireContent> {

    public static final String TABLE = "QuestionnaireContent";

    // QuestionnaireContent Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_QUESTIONNAIRE_ID = "QuestionnaireId";
    public static final String KEY_QUESTION_ID = "QuestionId";
    public static final String KEY_QUESTION_ORDER = "QuestionOrder";
    public static final String KEY_IS_PARENT = "isParent";
    public static final String KEY_PARENT_QUESTIONNAIRE_CONTENT = "parentQuestionnaireContent";
    public static final String KEY_VERSION ="Version";
    public static final String KEY_NOTES ="Notes";
    public static final String KEY_DELETED ="Deleted";


    public QuestionnaireContentTable() {
        super();
        activityClass = QuestionnaireContentActivity.class;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT NOT NULL, " + KEY_QUESTIONNAIRE_ID + " TEXT NOT NULL, "
                + KEY_QUESTION_ID + " TEXT NOT NULL," + KEY_QUESTION_ORDER + " VARCHAR NOT NULL,"
                + KEY_VERSION + " NUMERIC DEFAULT 1.0 NOT NULL," + KEY_NOTES
                + " VARCHAR DEFAULT ''," + KEY_DELETED + " INTEGER DEFAULT 0 NOT NULL,"
                + KEY_IS_PARENT + " INTEGER DEFAULT 0 NOT NULL, " + KEY_PARENT_QUESTIONNAIRE_CONTENT
                + " TEXT, "+ "PRIMARY KEY(" + KEY_ID +"),"
                + " FOREIGN KEY(" + KEY_QUESTION_ID + ") REFERENCES " + QuestionTable.TABLE
                + " (" + QuestionTable.KEY_ID + ")," + " FOREIGN KEY(" + KEY_QUESTIONNAIRE_ID
                + ") REFERENCES " + QuestionnaireTable.TABLE + " (" + QuestionnaireTable.KEY_ID + "), "
                + "FOREIGN KEY (" + KEY_PARENT_QUESTIONNAIRE_CONTENT+ ") REFERENCES "+ this.TABLE +
                "("+ KEY_ID +")" +
                ")";

    }

    @Override
    public String getTableName() {
        return TABLE;
    }


    /**
     * Returns all the questions of a questionnaire in the correct order
     * @return {@link ArrayList} of {@link QuestionnaireContent}
     */
    public ArrayList<QuestionnaireContent> getAllQuestions(String questionnaireId){
        String selection = KEY_QUESTIONNAIRE_ID + " = ? and "+ KEY_PARENT_QUESTIONNAIRE_CONTENT + " IS NULL";

        String[] selectionArgs = {questionnaireId};

        return DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE.getAll(selection, selectionArgs,KEY_QUESTION_ORDER);
    }
    public ArrayList<QuestionnaireContent> getLoopingQuestions(String questionnaireContentId){
        String selection =KEY_PARENT_QUESTIONNAIRE_CONTENT + "= ?";

        String[] selectionArgs = {questionnaireContentId};
        Log.i("DATA", questionnaireContentId);
        return DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE.getAll(selection, selectionArgs,KEY_QUESTION_ORDER);
    }


}
