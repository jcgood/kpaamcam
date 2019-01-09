package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import edu.buffalo.cse.ubcollecting.FileActivity;
import edu.buffalo.cse.ubcollecting.data.models.File;

public class FileTable extends Table<File> {

    public static final String TABLE = "File";

    // File Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "Name";
    public static final String KEY_ANSWER_ID = "AnswerId";
    public static final String KEY_TYPE = "Type";
    public static final String KEY_PATH = "Path";
    public static final String KEY_CREATOR_ID = "CreatorId";
    public static final String KEY_START_TIME = "StartTime";
    public static final String KEY_END_TIME = "EndTime";

    public FileTable() {
        super();
        activityClass = FileActivity.class;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME
                + " VARCHAR," + KEY_ANSWER_ID + " TEXT," + KEY_TYPE
                + " VARCHAR," + KEY_PATH + " VARCHAR," + KEY_CREATOR_ID
                + " TEXT," + KEY_START_TIME + " DATETIME," + KEY_END_TIME + " DATETIME,"
                + " FOREIGN KEY(" + KEY_ANSWER_ID + ") REFERENCES " + AnswerTable.TABLE
                + " (" + AnswerTable.KEY_ID + ")," + " FOREIGN KEY(" + KEY_CREATOR_ID + ") REFERENCES "
                + PersonTable.TABLE + " (" + PersonTable.KEY_ID + ")" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
