package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;

import edu.buffalo.cse.ubcollecting.QuestionPropertyActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.DatabaseManager;
import edu.buffalo.cse.ubcollecting.data.models.QuestionProperty;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;

public class QuestionPropertyTable extends Table<QuestionProperty> {

    public static final String TABLE = "QuestionProperty";

    // QuestionProperty Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_QUESTION_ID = "QuestionId";
    public static final String KEY_PROPERTY_ID = "PropertyId";
    public static final String KEY_VALUE = "Value";

    public QuestionPropertyTable() {
        super();
        activityClass = QuestionPropertyActivity.class;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT, " + KEY_QUESTION_ID + " TEXT,"
                + KEY_PROPERTY_ID + " TEXT," + KEY_VALUE + " INTEGER,"
                + "PRIMARY KEY(" + KEY_QUESTION_ID + ", " + KEY_PROPERTY_ID + "),"
                + " FOREIGN KEY(" + KEY_PROPERTY_ID + ") REFERENCES " + QuestionPropertyDefTable.TABLE
                + " (" + QuestionPropertyDefTable.KEY_ID + ")," + " FOREIGN KEY(" + KEY_QUESTION_ID + ") REFERENCES "
                + QuestionTable.TABLE + " (" + QuestionTable.KEY_ID + ")" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }


    /**
     * Function that returns all properties associated with a given question
     * @param quesId The questionId you want to search the SQlite table for all matching entries
     * @return a {@link HashSet} of {@link QuestionPropertyDef}
     */

    public HashSet<QuestionPropertyDef> getQuestionProperties(String quesId){

        String selection = KEY_QUESTION_ID + " = ?";

        String[] selectionArgs = {quesId};

        ArrayList<QuestionProperty> questionProperties = DatabaseHelper.QUESTION_PROPERTY_TABLE.getAll(selection, selectionArgs,null);

        HashSet<QuestionPropertyDef> quesPropertyDefs = new HashSet<>();

        for (QuestionProperty quesProperty: questionProperties){

            QuestionPropertyDef quesPropDef = DatabaseHelper.QUESTION_PROPERTY_DEF_TABLE.findById(quesProperty.getPropertyId());

            quesPropertyDefs.add(quesPropDef);
        }

        return quesPropertyDefs;

    }


    /**
     * Helper function that deletes a property associated with a question
     * @param id The id of the entry in the table you wish to delete
     */

    public void deleteByPropertyId(String id) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selection = KEY_PROPERTY_ID + " = ?";

        String[] selectionArgs = {id};

        db.delete(this.getTableName(), selection, selectionArgs);

        DatabaseManager.getInstance().closeDatabase();

    }

}
