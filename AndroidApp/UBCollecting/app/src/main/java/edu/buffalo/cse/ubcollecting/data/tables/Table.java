package edu.buffalo.cse.ubcollecting.data.tables;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.buffalo.cse.ubcollecting.data.DatabaseManager;
import edu.buffalo.cse.ubcollecting.data.models.MethodComparator;
import edu.buffalo.cse.ubcollecting.data.models.Model;
import edu.buffalo.cse.ubcollecting.data.models.Question;
import edu.buffalo.cse.ubcollecting.ui.UpdateQuestionActivity;

/**
 * Abstraction of an SQLite table of {@link Model}s.
 *
 * @param <E> {@link Model} implementation that the will be stored in Table rows
 */
public abstract class Table<E extends Model> implements Serializable {

    public static final int FLAG_EDIT_ENTRY = 1;
    public static final String EXTRA_MODEL = "edu.buffalo.cse.ubcollecting.data.tables.model_extra";
    protected static final String MODEL_PATH = "edu.buffalo.cse.ubcollecting.data.models.";
    public final String TAG = this.getClass().getSimpleName();

    public ArrayList<String> tableColumns;
    public Class<? extends AppCompatActivity> activityClass;

    public Table() {
        tableColumns = this.getAllColumnNames();
        Collections.sort(tableColumns, new ColumnComparator());
    }

    /**
     * Return an SQLite command to create the table.
     *
     * @return {@link String} of SQlite CREATE command
     */
    public abstract String createTable();

    /**
     * Return the name of the table.
     *
     * @return table name
     */
    public abstract String getTableName();

    /**
     * Insert a {@link Model} into the {@link Table}
     *
     * @param model {@link} Model instance to insert into the table
     * @return SQLite ID number
     */
    public long insert(E model) {

        long rowId;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        List<Method> getters = model.getGetters();

        Collections.sort(getters, new MethodComparator());

        for (int i = 0; i < tableColumns.size(); i++) {

            try {
                String key = tableColumns.get(i);
                Object value = getters.get(i).invoke(model, null);
                insertContent(values, key, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        rowId = db.insert(this.getTableName(), null, values);

        DatabaseManager.getInstance().closeDatabase();

        return rowId;
    }

    /**
     * Return an intent for starting the corresponding {@link edu.buffalo.cse.ubcollecting.EntryActivity EntryActivity}
     * for inserting.
     *
     * @param packageContext Context in which the activity is being started
     * @return Intent to the corresponding {@link edu.buffalo.cse.ubcollecting.EntryActivity EntryActivity}
     */
    public Intent insertActivityIntent(Context packageContext) {
        Intent i = new Intent(packageContext, activityClass);
        return i;
    }

    /**
     * Return an intent for starting the corresponding {@link edu.buffalo.cse.ubcollecting.EntryActivity EntryActivity}
     * for editing.
     *
     * @param packageContext Context in which the activity is being started
     * @return Intent to the corresponding {@link edu.buffalo.cse.ubcollecting.EntryActivity EntryActivity}
     */
    @SuppressLint("WrongConstant")
    public Intent editActivityIntent(Context packageContext, Model entry) {
        if (entry instanceof Question){
            activityClass = UpdateQuestionActivity.class;
        }
        Intent i = new Intent(packageContext, activityClass);
        i.putExtra(EXTRA_MODEL, entry);
        i.setFlags(FLAG_EDIT_ENTRY);
        return i;
    }

    /**
     * Insert key and value pair into {@link ContentValues}
     *
     * @param values ContentValues where key-value pair is to be added
     * @param key    key value
     * @param value  pair value
     */
    private void insertContent(ContentValues values, String key, Object value) {

        if (value instanceof Integer) {
            values.put(key, (Integer) value);
        } else if (value instanceof String) {
            values.put(key, (String) value);
        } else if (value instanceof byte[]) {
            values.put(key, (byte[]) value);
        }
    }

    /**
     * Return a {@link List} of all {@link Model} entries in the SQlite table.
     *
     * @return {@link List} of {@link Model} entries
     */
    public ArrayList<E> getAll() {

        ArrayList<E> tuples = new ArrayList<>();

        try {

            Class theClass = Class.forName(MODEL_PATH + this.getTableName());

            String selectQuery = "SELECT  * FROM " + this.getTableName();
            SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

            Cursor cursor = db.rawQuery(selectQuery, null);
            ArrayList<Method> setters = ((E) theClass.newInstance()).getSetters();

            Collections.sort(setters, new MethodComparator());

            if (cursor.moveToFirst()) {
                do {
                    E model = (E) theClass.newInstance();
                    for (int i = 0; i < tableColumns.size(); i++) {
                        String key = tableColumns.get(i);
                        Method method = setters.get(i);
                        insertIntoObject(cursor, model, key, method);
                    }
                    tuples.add(model);
                } while (cursor.moveToNext());
            }

            cursor.close();

            DatabaseManager.getInstance().closeDatabase();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return tuples;

    }

    /**
     * Return a {@link List} of all {@link Model} entries in the SQlite table that satisfy
     * the passed in selection predicate(s)
     * @param selection The fields of the respective table by which you want to filter the result
     * @param selectionArgs The corresponding values/condition those fields should satisfy
     * @param sortByColumn The column to sort the entries by
     * @return {@link List} of {@link Model} entries
     */

    public ArrayList<E> getAll(String selection, String[] selectionArgs, String sortByColumn) {

        ArrayList<E> tuples = new ArrayList<>();

        try {

            Class theClass = Class.forName(MODEL_PATH + this.getTableName());

            SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

            Cursor cursor = db.query(this.getTableName(),
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortByColumn);

            ArrayList<Method> setters = ((E) theClass.newInstance()).getSetters();

            Collections.sort(setters, new MethodComparator());

            if (cursor.moveToFirst()) {
                do {
                    E model = (E) theClass.newInstance();
                    for (int i = 0; i < tableColumns.size(); i++) {
                        String key = tableColumns.get(i);
                        Method method = setters.get(i);
                        insertIntoObject(cursor, model, key, method);
                    }
                    tuples.add(model);
                } while (cursor.moveToNext());
            }

            cursor.close();

            DatabaseManager.getInstance().closeDatabase();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return tuples;

    }


    /**
     * Return a {@link Model} of the entry in the SQlite table that has the passed in id
     * @param id The id of the row you want to find
     * @return a {@link Model} entry
     */

    public E findById(String id) {

        E model = null;

        try {

            Class theClass = Class.forName(MODEL_PATH + this.getTableName());

            model = (E) theClass.newInstance();

            String selectQuery = "SELECT  * FROM " + this.getTableName() + " WHERE id = " + id;

            SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

            Cursor cursor = db.rawQuery(selectQuery, null);

            ArrayList<Method> setters = model.getSetters();

            Collections.sort(setters, new MethodComparator());

            if (cursor.moveToFirst()) {

                for (int i = 0; i < tableColumns.size(); i++) {
                    String key = tableColumns.get(i);
                    Method method = setters.get(i);
                    insertIntoObject(cursor, model, key, method);
                }

            }

            cursor.close();

            DatabaseManager.getInstance().closeDatabase();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return model;

    }

    /**
     * Updates a row in the SQlite table based on passed in {@link Model}
     * @param model {@link Model} representing how the row it represents in table should be updated
     */

    public void update(Model model) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues updatedValues = new ContentValues();

        List<Method> getters = model.getGetters();

        Collections.sort(getters, new MethodComparator());

        String id = null;

        for (int i = 0; i < tableColumns.size(); i++) {

            try {
                String key = tableColumns.get(i);
                Object value = getters.get(i).invoke(model, null);
                insertContent(updatedValues, key, value);
                if (getters.get(i).getName().equals("getId")) {
                    id = (String) value;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        String selection = "id = ?";

        String[] selectionArgs = {id};

        db.update(this.getTableName(), updatedValues, selection, selectionArgs);

        DatabaseManager.getInstance().closeDatabase();

    }

    /**
     * Deletes a row in the SQlite table based on passed in id
     * @param id The id of the row you want to delete
     */

    public void delete(String id) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selection = "id = ?";

        String[] selectionArgs = {id};

        db.delete(this.getTableName(), selection, selectionArgs);

        DatabaseManager.getInstance().closeDatabase();

    }

    /**
     * Helper method that appropriately updates a {@link Model} representing a row from the SQlite table
     * @param cursor The {@link Cursor} returned by SQlite query that contains the result of the query
     * @param model The {@link Model} that is to be updated
     * @param key The {@link String} key representing the field of the {@link Model} that is to be updated
     * @param method The corresponding setter method of the {@link Model} that can be invoked
     */

    protected void insertIntoObject(Cursor cursor, E model, String key, Method method) {

        try {
            Class<?> ptype = method.getParameterTypes()[0];

            if (Integer.TYPE.equals(ptype)) {
                int value = cursor.getInt(cursor.getColumnIndex(key));
                method.invoke(model, value);
            } else if ("".getClass().equals(ptype)) {
                String value = cursor.getString(cursor.getColumnIndex(key));
                method.invoke(model, value);
            } else {
                byte[] value = cursor.getBlob(cursor.getColumnIndex(key));
                method.invoke(model, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * Return a {@link List} of {@link String} containing all the columns/fields of a SQlite table
     * @return a {@link List} of {@link String}
     */

    private ArrayList<String> getAllColumnNames() {

        Field[] allFields = this.getClass().getDeclaredFields();
        ArrayList<String> allColumnNames = new ArrayList<>();

        for (int i = 0; i < allFields.length; i++) {
            if (allFields[i].getName().startsWith("KEY")) {
                try {
                    Field field = allFields[i];
                    allColumnNames.add((String) field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }

        return allColumnNames;
    }
}
