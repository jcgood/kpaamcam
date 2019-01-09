package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import android.util.Log;

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.RoleActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.LanguageType;
import edu.buffalo.cse.ubcollecting.data.models.Role;

public class RoleTable extends Table<Role> {

    public static final String TABLE = "Role";

    // Role Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "Name";
    public static final String KEY_INTRO_REQUIRED = "IntroRequired";
    public static final String KEY_PHOTO_REQUIRED = "PhotoRequired";
    public static final String KEY_ON_CLIENT = "OnClient";

    public RoleTable() {
        super();
        activityClass = RoleActivity.class;
    }

    @Override
    public String createTable() {
        Log.i("Role TabLe NAME: ", TABLE);
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME
                + " VARCHAR," + KEY_INTRO_REQUIRED + " INTEGER," + KEY_PHOTO_REQUIRED
                + " INTEGER," + KEY_ON_CLIENT + " INTEGER" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }


    /**
     * Returns the on client roles that can be assigned for purposes of session roles
     * @return {@link ArrayList} of {@link Role}
     */

    public static ArrayList<Role> getOnClientRoles() {

        String selection = KEY_ON_CLIENT + " = ?";

        String[] selectionArgs = {"1"};

        return DatabaseHelper.ROLE_TABLE.getAll(selection, selectionArgs,null);

    }
}
