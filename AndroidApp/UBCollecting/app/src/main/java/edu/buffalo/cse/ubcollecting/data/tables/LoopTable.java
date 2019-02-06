package edu.buffalo.cse.ubcollecting.data.tables;

import edu.buffalo.cse.ubcollecting.data.models.Loop;

public class LoopTable extends Table<Loop> {

    public static final String TABLE = "loop";

    public static final String KEY_ID = "id";
    public static final String KEY_QUESTIONNAIRE_CONTENT_ID = "questionnaireContentId";
    public static final String KEY_ITERATIONS = "iterations";
    public static final String KEY_VERSION = "version";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_DELETED = "deleted";


    @Override
    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "( " + KEY_ID + " TEXT PRIMARY KEY, " + KEY_QUESTIONNAIRE_CONTENT_ID + " TEXT NOT NULL, "
                + KEY_ITERATIONS + "TEXT NOT NULL, " + KEY_VERSION + " NUMERIC DEFAULT 1.0 NOT NULL"
                + KEY_NOTES + " VARCHAR DEFAULT '', " + KEY_DELETED + " INTEGER DEFAULT 0 NOT NULL, " + "FOREIGN KEY( "
                + KEY_QUESTIONNAIRE_CONTENT_ID + ") " + " REFERENCES " +  QuestionnaireContentTable.TABLE
                + "( " + QuestionnaireContentTable.KEY_ID + ") )";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}