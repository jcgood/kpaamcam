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

    public static final String KEY_VERSION ="VersionNumber";
    public static final String KEY_NOTES ="Notes";
    public static final String KEY_DELETED ="Deleted";


    public LanguageTypeTable() {
        super();
        activityClass = LanguageTypeActivity.class;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY NOT NULL," + KEY_NAME + " VARCHAR NOT NULL, "
                + KEY_VERSION + " NUMERIC DEFAULT 1.0 NOT NULL," + KEY_NOTES
                + " VARCHAR DEFAULT ''," + KEY_DELETED + " INTEGER DEFAULT 0 NOT NULL"+
                ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }


}
