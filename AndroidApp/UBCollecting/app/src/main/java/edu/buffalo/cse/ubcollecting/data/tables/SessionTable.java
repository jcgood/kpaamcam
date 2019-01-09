package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.SessionActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.FieldTrip;
import edu.buffalo.cse.ubcollecting.data.models.Session;

public class SessionTable extends Table<Session> {

    // Table Name
    public static final String TABLE = "Session";

    // Session Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_LABEL = "Label";
    public static final String KEY_NAME = "Name";
    public static final String KEY_START_TIME = "StartTime";
    public static final String KEY_LOCATION = "Location";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_FIELD_TRIP_ID = "FieldTripId";

    public SessionTable() {
        super();
        activityClass = SessionActivity.class;
    }

    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_LABEL
                + " VARCHAR," + KEY_NAME + " VARCHAR," + KEY_START_TIME
                + " DATETIME," + KEY_LOCATION + " VARCHAR," + KEY_DESCRIPTION
                + " VARCHAR," + KEY_FIELD_TRIP_ID + " TEXT," + " FOREIGN KEY(" + KEY_FIELD_TRIP_ID
                + ") REFERENCES " + FieldTripTable.TABLE + " (" + FieldTripTable.KEY_ID + ")" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }

    /**
     * Returns the sessions corresponding to a Field Trip.
     * @param fieldTrip The {@link FieldTrip} for which you want to obtain existing sessions
     * @return {@link ArrayList} of {@link Session}
     */

    public ArrayList<Session> getFieldTripSessions(FieldTrip fieldTrip){

        String selection = KEY_FIELD_TRIP_ID + " = ?";

        String[] selectionArgs = {fieldTrip.getId()};

        return DatabaseHelper.SESSION_TABLE.getAll(selection,selectionArgs,null);
    }
}
