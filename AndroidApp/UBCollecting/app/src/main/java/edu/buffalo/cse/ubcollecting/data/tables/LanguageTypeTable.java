package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import edu.buffalo.cse.ubcollecting.LanguageTypeActivity;
import edu.buffalo.cse.ubcollecting.data.models.LanguageType;

public class LanguageTypeTable extends Table<LanguageType> {

    public static final String TABLE = "LanguageType";

    // LanguageType Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "Name";

    public LanguageTypeTable() {
        super();
        activityClass = LanguageTypeActivity.class;
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
