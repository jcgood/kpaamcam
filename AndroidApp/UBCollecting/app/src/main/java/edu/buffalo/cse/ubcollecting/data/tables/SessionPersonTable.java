package edu.buffalo.cse.ubcollecting.data.tables;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.buffalo.cse.ubcollecting.SessionPersonActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Role;
import edu.buffalo.cse.ubcollecting.data.models.SessionPerson;

/**
 * Created by aamel786 on 2/17/18.
 */

public class SessionPersonTable extends Table<SessionPerson> {

    public static final String TABLE = "SessionPerson";

    // SessionPerson Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_SESSION_ID = "SessionId";
    public static final String KEY_PERSON_ID = "PersonId";
    public static final String KEY_ROLE_ID = "RoleId";

    public SessionPersonTable() {
        super();
        activityClass = SessionPersonActivity.class;
    }

    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT, " + KEY_SESSION_ID + " TEXT, "
                + KEY_PERSON_ID + " TEXT," + KEY_ROLE_ID + " TEXT,"
                + "PRIMARY KEY(" + KEY_SESSION_ID + ", " + KEY_ROLE_ID + ", " + KEY_PERSON_ID + "),"
                + " FOREIGN KEY(" + KEY_SESSION_ID + ") REFERENCES " + SessionTable.TABLE
                + " (" + SessionTable.KEY_ID + "),"
                + " FOREIGN KEY(" + KEY_PERSON_ID + ") REFERENCES " + PersonTable.TABLE
                + " (" + PersonTable.KEY_ID + "),"
                + " FOREIGN KEY(" + KEY_ROLE_ID + ") REFERENCES " + RoleTable.TABLE
                + " (" + RoleTable.KEY_ID + ")" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }


    /**
     * Returns the roles that are already assigned for a session
     * @param sessionId The id for the session for which you want to obtain the assigned roles for
     * @return a {@link HashMap} where {@link SessionPerson} is the key and {@link ArrayList of {@link Role}} is the value

     */

    public HashMap<SessionPerson,ArrayList<Role>> getSessionPersonRoles(String sessionId){
        String selection = KEY_SESSION_ID + " = ?";

        String[] selectionArgs = {sessionId};

        ArrayList<SessionPerson> peopleAssigned = DatabaseHelper.SESSION_PERSON_TABLE.getAll(selection, selectionArgs,null);

        Log.i(peopleAssigned.toString(),"PEOPLE ASSIGED");

        HashMap<SessionPerson,ArrayList<Role>> allRolesAssigned = new HashMap<>();

        for (SessionPerson sp: peopleAssigned){
            String roleId = sp.getRoleId();
            Role role = DatabaseHelper.ROLE_TABLE.findById(roleId);
            if(allRolesAssigned.containsKey(sp)){
                allRolesAssigned.get(sp).add(role);
            }
            else{
                ArrayList<Role> roles = new ArrayList<>();
                roles.add(role);
                allRolesAssigned.put(sp,roles);
            }
        }
        Log.i(allRolesAssigned.toString(), "ROLES THAT ARE ALREADY ASSIGNED");

        return allRolesAssigned;
    }
}
