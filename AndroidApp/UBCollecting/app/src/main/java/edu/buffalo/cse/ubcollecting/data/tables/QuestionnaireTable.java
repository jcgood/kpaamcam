package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import edu.buffalo.cse.ubcollecting.QuestionnaireActivity;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;


public class QuestionnaireTable extends Table<Questionnaire> {

    public static final String TABLE = "Questionnaire";

    // Questionnaire Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "Name";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_TYPE_ID = "TypeId";

    public static final String KEY_VERSION ="VersionNumber";
    public static final String KEY_NOTES ="Notes";
    public static final String KEY_DELETED ="Deleted";

    public QuestionnaireTable() {
        super();
        activityClass = QuestionnaireActivity.class;
    }

    @Override
    public String createTable() {

        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY NOT NULL," + KEY_NAME + " VARCHAR NOT NULL," + KEY_DESCRIPTION
                + " VARCHAR," + KEY_TYPE_ID + " TEXT,"
                + KEY_VERSION + " NUMERIC DEFAULT 1.0 NOT NULL," + KEY_NOTES
                + " VARCHAR DEFAULT ''," + KEY_DELETED + " INTEGER DEFAULT 0 NOT NULL,"
                + " FOREIGN KEY(" + KEY_TYPE_ID
                + ") REFERENCES " + QuestionnaireTypeTable.TABLE + " (" + QuestionnaireTypeTable.KEY_ID + ")"
                + ")";

    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
