package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.buffalo.cse.ubcollecting.FieldTripActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.FieldTrip;

public class FieldTripTable extends Table<FieldTrip> {

    public static final String TABLE = "FieldTrip";

    // FieldTrip Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_FIELD_TRIP_NAME = "Name";
    public static final String KEY_START_DATE = "StartDate";
    public static final String KEY_END_DATE = "EndDate";

    public FieldTripTable() {
        super();
        activityClass = FieldTripActivity.class;
    }

    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_FIELD_TRIP_NAME
                + " VARCHAR," + KEY_START_DATE + " DATE," + KEY_END_DATE
                + " DATE" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }


    /**
     * Returns the active field trips present in the database
     * @return {@link ArrayList} of {@link FieldTrip}
     */

    public ArrayList<FieldTrip> getActiveFieldTrips(){

        String selection = FieldTripTable.KEY_END_DATE + " >= ?";

        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = formatter.format(currentDate);

        String[] selectionArgs = {formatDate};

        return DatabaseHelper.FIELD_TRIP_TABLE.getAll(selection,selectionArgs,null);

    }
}
