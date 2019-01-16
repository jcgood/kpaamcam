package edu.buffalo.cse.ubcollecting.data.tables;

import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.SessionQuestionnaire;

public class SessionQuestionnaireTable extends Table<SessionQuestionnaire> {

    public static final String TABLE = "SessionQuestionnaire";

    public static final String KEY_ID = "id";
    public static final String KEY_QUESTIONNAIRE_ID = "questionnaireId";
    public static final String KEY_SESSION_ID = "sessionId";
    public static final String KEY_LAST_QUESTION_ANSWERED = "lastQuestionAnswered";
    public static final String KEY_VERSION = "version";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_DATE  = "date";

    @Override
    public String createTable() {
        return "CREATE TABLE " + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY,"+ KEY_QUESTIONNAIRE_ID
                + " TEXT NOT NULL, " + KEY_SESSION_ID + " TEXT NOT NULL, "+ KEY_LAST_QUESTION_ANSWERED
                + " TEXT, " + KEY_VERSION + " NUMERIC NOT NULL DEFAULT 1.0 , " + KEY_NOTES
                + " TEXT, " + KEY_DATE + " TEXT, " + " FOREIGN KEY ( " + KEY_QUESTIONNAIRE_ID +
                " ) REFERENCES " + QuestionnaireTable.TABLE + "( "
                + QuestionnaireTable.KEY_ID + "), " + " FOREIGN KEY (" + KEY_SESSION_ID + ") REFERENCES "
                + SessionTable.TABLE + "( "+ SessionTable.KEY_ID + "), " + " FOREIGN KEY ("
                + KEY_LAST_QUESTION_ANSWERED + ") REFERENCES "+ QuestionnaireContentTable.TABLE
                + "( " + QuestionnaireContentTable.KEY_ID  + ") " + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
