package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

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

    public QuestionnaireContentTable() {
        super();
        activityClass = QuestionnaireContentActivity.class;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT, " + KEY_QUESTIONNAIRE_ID + " TEXT, "
                + KEY_QUESTION_ID + " TEXT," + KEY_QUESTION_ORDER + " VARCHAR,"
                + "PRIMARY KEY(" + KEY_QUESTIONNAIRE_ID + ", " + KEY_QUESTION_ID + "),"
                + " FOREIGN KEY(" + KEY_QUESTION_ID + ") REFERENCES " + QuestionTable.TABLE
                + " (" + QuestionTable.KEY_ID + ")," + " FOREIGN KEY(" + KEY_QUESTIONNAIRE_ID
                + ") REFERENCES " + QuestionnaireTable.TABLE + " (" + QuestionnaireTable.KEY_ID + ")"
                + ")";

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
        String selection = KEY_QUESTIONNAIRE_ID + " = ?";

        String[] selectionArgs = {questionnaireId};

        return DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE.getAll(selection, selectionArgs,KEY_QUESTION_ORDER);
    }

}
