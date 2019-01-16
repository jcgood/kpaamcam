package edu.buffalo.cse.ubcollecting.data.tables;

import edu.buffalo.cse.ubcollecting.data.models.SessionQuestion;
import edu.buffalo.cse.ubcollecting.data.models.SessionQuestionnaire;

public class SessionQuestionTable extends Table<SessionQuestion>{

    public static final String Table = "SessionQuestion";

    public static final  String KEY_ID = "id";
    public static final  String KEY_SESSION_QUESTIONNAIRE_ID = "SessionQuestionnaireid";
    public static final  String KEY_QUESTION_COMPLETED = "QuestionCompleted";
    public static final  String KEY_VERSION = "Version";
    public static final  String KEY_DATE = "Date";
    public static final  String KEY_NOTES = "Notes";


    @Override
    public String createTable() {
        return "CREATE TABLE (" + KEY_ID + "TEXT PRIMARY KEY, " + KEY_SESSION_QUESTIONNAIRE_ID
                + " TEXT NOT NULL, " + KEY_QUESTION_COMPLETED
                + " NUMERIC NOT NULL DEFAULT 0  CHECK(QuestionCompleted = 1 OR QuestionCompleted = 0), "
                + KEY_VERSION + " NUMERIC NOT NULL DEFAULT 1.0 , " + KEY_NOTES + "TEXT, " + KEY_DATE
                + " TEXT, " + "FOREIGN KEY ( "+ KEY_SESSION_QUESTIONNAIRE_ID + ") " + "REFERENCES " + SessionQuestionnaireTable.TABLE
                + " (" + SessionQuestionnaireTable.KEY_ID + ") " + ") ";
    }

    @Override
    public String getTableName() {
        return Table;
    }
}
