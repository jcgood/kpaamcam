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

    public QuestionnaireTable() {
        super();
        activityClass = QuestionnaireActivity.class;
    }

    @Override
    public String createTable() {

        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME + " VARCHAR," + KEY_DESCRIPTION
                + " VARCHAR," + KEY_TYPE_ID + " TEXT," + " FOREIGN KEY(" + KEY_TYPE_ID
                + ") REFERENCES " + QuestionnaireTypeTable.TABLE + " (" + QuestionnaireTypeTable.KEY_ID + ")"
                + ")";

    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
