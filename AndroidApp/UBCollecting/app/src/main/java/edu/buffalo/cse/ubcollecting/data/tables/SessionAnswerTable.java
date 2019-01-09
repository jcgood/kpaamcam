package edu.buffalo.cse.ubcollecting.data.tables;

import edu.buffalo.cse.ubcollecting.SessionAnswerActivity;
import edu.buffalo.cse.ubcollecting.data.models.SessionAnswer;

/**
 * Created by aamel786 on 2/17/18.
 */
public class SessionAnswerTable extends Table<SessionAnswer> {

    public static final String TABLE = "SessionAnswer";

    // SessionAnswer Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_SESSION_ID = "SessionId";
    public static final String KEY_QUESTIONNAIRE_ID = "QuestionnaireId";
    public static final String KEY_QUESTION_ID = "QuestionId";
    public static final String KEY_ANSWER_ID = "AnswerId";

    public SessionAnswerTable() {
        super();
        activityClass = SessionAnswerActivity.class;
    }

    public String createTable() {

        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT, " + KEY_SESSION_ID + " TEXT, "
                + KEY_QUESTIONNAIRE_ID + " TEXT," + KEY_QUESTION_ID + " TEXT,"
                + KEY_ANSWER_ID + " TEXT,"
                + " PRIMARY KEY(" + KEY_SESSION_ID + ", " + KEY_QUESTIONNAIRE_ID + ", "
                + KEY_QUESTION_ID + ", " + KEY_ANSWER_ID + "),"
                + " FOREIGN KEY(" + KEY_QUESTIONNAIRE_ID + ") REFERENCES " + QuestionnaireTable.TABLE
                + " (" + QuestionnaireTable.KEY_ID + "),"
                + " FOREIGN KEY(" + KEY_QUESTION_ID + ") REFERENCES " + QuestionTable.TABLE
                + " (" + QuestionTable.KEY_ID + "),"
                + " FOREIGN KEY(" + KEY_ANSWER_ID + ") REFERENCES " + AnswerTable.TABLE
                + " (" + AnswerTable.KEY_ID + "),"
                + " FOREIGN KEY(" + KEY_SESSION_ID + ") REFERENCES " + SessionTable.TABLE
                + " (" + SessionTable.KEY_ID + ")"
                + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
