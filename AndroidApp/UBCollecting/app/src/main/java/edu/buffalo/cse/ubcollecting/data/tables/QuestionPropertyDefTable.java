package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import edu.buffalo.cse.ubcollecting.QuestionPropertyDefActivity;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;

public class QuestionPropertyDefTable extends Table<QuestionPropertyDef> {

    public static final String TABLE = "QuestionPropertyDef";

    // QuestionPropertyDef Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "Name";

    public QuestionPropertyDefTable() {
        super();
        activityClass = QuestionPropertyDefActivity.class;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME + " VARCHAR" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }

}
