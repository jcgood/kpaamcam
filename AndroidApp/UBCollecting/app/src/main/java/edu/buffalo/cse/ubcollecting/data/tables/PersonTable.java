package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import edu.buffalo.cse.ubcollecting.PersonActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.DatabaseManager;
import edu.buffalo.cse.ubcollecting.data.models.Person;
import edu.buffalo.cse.ubcollecting.data.models.Role;


public class PersonTable extends Table<Person> {

    public static final String TABLE = "Person";

    // Person Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "Name";
    public static final String KEY_OTHER_NAMES = "OtherNames";
    public static final String KEY_DOB = "DOB";
    public static final String KEY_PHOTO = "Photo";
    public static final String KEY_PHOTO_DESC = "PhotoDesc";
    public static final String KEY_MAIN_ROLE_ID = "MainRoleId";
    public static final String KEY_INTRO_QUEST_DESC = "IntroQuestDesc";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_PASSWORD = "Password";

    public PersonTable() {
        super();
        activityClass = PersonActivity.class;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "( " + KEY_ID + " TEXT PRIMARY KEY, " + KEY_NAME
                + " VARCHAR, " + KEY_OTHER_NAMES + " VARCHAR, " + KEY_DOB
                + " DATETIME, " + KEY_MAIN_ROLE_ID + " TEXT," + KEY_PHOTO + " BLOB, "
                + KEY_PHOTO_DESC + " VARCHAR, " + KEY_INTRO_QUEST_DESC + " VARCHAR, "
                + KEY_EMAIL + " TEXT, " + KEY_PASSWORD + " TEXT,"
                + " FOREIGN KEY (" + KEY_MAIN_ROLE_ID + ") REFERENCES " + RoleTable.TABLE
                + " (" + RoleTable.KEY_ID + ")" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }



    /**
     * Function that validates email and password of user and returns user's person id and role name
       upon validation
     * @param email User's inputted email
     * @param password User's inputted password
     * @return  a {@link Array} of size 2
     */

    public String[] validateUser(String email, String password) {

        String[] output = new String[2];

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String[] columns = {
                KEY_ID,
                KEY_MAIN_ROLE_ID
        };

        String selection = KEY_EMAIL + " = ?" + " AND " + KEY_PASSWORD + " = ?";

        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            output[0] = cursor.getString(cursor.getColumnIndex(KEY_ID));
            output[1] = cursor.getString(cursor.getColumnIndex(KEY_MAIN_ROLE_ID));
        } else {
            return null;
        }

        cursor.close();

        DatabaseManager.getInstance().closeDatabase();

        if (output.length > 0 && output[1] != null && output[1].length() > 5) {
            Role role = DatabaseHelper.ROLE_TABLE.findById(output[1]);
            output[1] = role.getName();
        }

        return output;
    }

}
