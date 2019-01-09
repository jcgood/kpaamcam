package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import edu.buffalo.cse.ubcollecting.QuestionnaireTypeActivity;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireType;

public class QuestionnaireTypeTable extends Table<QuestionnaireType> {

    public static final String TABLE = "QuestionnaireType";

    // QuestionnaireType Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "Name";

    public QuestionnaireTypeTable() {
        super();
        activityClass = QuestionnaireTypeActivity.class;
    }

    @Override
    public String createTable() {
        //  Added primary key below unlike in original script
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME + " VARCHAR" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
