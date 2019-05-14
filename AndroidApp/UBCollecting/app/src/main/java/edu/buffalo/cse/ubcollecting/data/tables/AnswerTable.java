package edu.buffalo.cse.ubcollecting.data.tables;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.AnswerActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.QuestionProperty;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;

/**
 * Created by aamel786 on 2/17/18.
 */
public class AnswerTable extends Table<Answer> {

    public static final String TABLE = "Answer";

    // Answer Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_QUESTIONNAIRE_ID = "QuestionnaireId";
    public static final String KEY_QUESTION_ID = "QuestionId";
    public static final String KEY_LABEL = "Label";
    public static final String KEY_TEXT = "Text";
    public static final String KEY_SESSION_ID = "SessionId";
    public static final String KEY_PARENT_ANSWER = "parentAnswer";

    public static final String KEY_VERSION ="VersionNumber";
    public static final String KEY_NOTES ="Notes";
    public static final String KEY_DELETED ="Deleted";


    public AnswerTable() {
        super();
        activityClass = AnswerActivity.class;
    }

    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT NOT NULL," + KEY_QUESTIONNAIRE_ID
                + " TEXT," + KEY_QUESTION_ID + " TEXT, " + KEY_PARENT_ANSWER + " TEXT , "+KEY_LABEL + " VARCHAR,"
                + KEY_TEXT + " VARCHAR," + KEY_SESSION_ID + " TEXT, "
                + KEY_VERSION + " NUMERIC DEFAULT 1.0 NOT NULL," + KEY_NOTES
                + " VARCHAR DEFAULT ''," + KEY_DELETED + " INTEGER DEFAULT 0 NOT NULL,"
                + " PRIMARY KEY(" + KEY_QUESTIONNAIRE_ID + ", " + KEY_QUESTION_ID + ", " + KEY_ID + ", "
                + KEY_SESSION_ID + "), "
                + " FOREIGN KEY(" + KEY_QUESTION_ID + ") REFERENCES " + QuestionTable.TABLE
                + " (" + QuestionTable.KEY_ID + ")," + " " +
                "FOREIGN KEY(" + KEY_QUESTIONNAIRE_ID + ") REFERENCES "
                + QuestionnaireTable.TABLE + " (" + QuestionnaireTable.KEY_ID + "),"
                + " FOREIGN KEY(" + KEY_PARENT_ANSWER + ") REFERENCES " + this.TABLE
                + " (" + this.KEY_ID + "), "
                + " FOREIGN KEY(" + KEY_SESSION_ID + ") REFERENCES " + SessionTable.TABLE
                + " (" + SessionTable.KEY_ID + ")"
                + ")";
    }

    public ArrayList<Answer> getAnswers(String questionId, String questionnaireId){
        String selection = AnswerTable.KEY_QUESTION_ID + " = ? AND " + AnswerTable.KEY_QUESTIONNAIRE_ID + " = ? ";
        String [] selectionArgs = {questionId, questionnaireId};
        ArrayList<Answer> answers = DatabaseHelper.ANSWER_TABLE.getAll(selection, selectionArgs, null);
        return answers;

    }
    /**
     * Function that returns the most recent answer for a question (answer(s) with highest version #)
     * @param questionId
     * @param questionnaireId
     * @return a {@link ArrayList} of {@link Answer}
     */

    public ArrayList<Answer> getMostRecentAnswer(String questionId, String questionnaireId, Answer parentAnswer) {
        String selection;
        String [] selectionArgs;
        if(parentAnswer==null){
            selection = AnswerTable.KEY_QUESTION_ID +  " = ?  AND "
                    + AnswerTable.KEY_QUESTIONNAIRE_ID + " = ? ";
            selectionArgs = new String [] {questionId, questionnaireId};
        }
        else{
            selection = AnswerTable.KEY_QUESTION_ID +  " = ?  AND "
                    + AnswerTable.KEY_QUESTIONNAIRE_ID + " = ? AND "+AnswerTable.KEY_PARENT_ANSWER + " = ?";
            selectionArgs = new String[] {questionId, questionnaireId, parentAnswer.getId()};
        }

        ArrayList<Answer> answers = DatabaseHelper.ANSWER_TABLE.getAll(selection, selectionArgs, KEY_VERSION + " DESC");


        if (answers.isEmpty()) {
            return answers;
        }


        double max = 1.0;
        int startIndex = 0;
        int endIndex = 0;

        for (int i=0; i<answers.size(); i++) {
            Answer answer = answers.get(i);
            if (answer.getVersion() > max) {
                max = answer.getVersion();
                startIndex = i;
                endIndex = i;
            }  else if (answer.getVersion() < max) {
                break;
            }
            else {
                endIndex = i;
            }
        }

        return new ArrayList<Answer>(answers.subList(startIndex,endIndex+1));
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
