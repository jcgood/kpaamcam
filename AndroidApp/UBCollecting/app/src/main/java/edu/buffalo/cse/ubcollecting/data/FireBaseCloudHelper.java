package edu.buffalo.cse.ubcollecting.data;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.ubcollecting.EntryActivity;
import edu.buffalo.cse.ubcollecting.app.App;
import edu.buffalo.cse.ubcollecting.data.models.Model;
import edu.buffalo.cse.ubcollecting.data.models.Person;
import edu.buffalo.cse.ubcollecting.data.tables.Table;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.TABLES;

public class FireBaseCloudHelper<E extends Model> extends Application{

    public FirebaseDatabase database;
    public FirebaseStorage storage;
    public DatabaseReference mDatabase;
    public DatabaseReference connRef;
    public DataSnapshot mSnapshot;
    private Context context;
    private String TAG;
    public boolean isConnected;
    static final short GET = 3;
    public SchemaValidator mSchemaValidator;


    public FireBaseCloudHelper(Context context) {
        this.context = context;
        this.TAG = App.getContext().toString();
        this.isConnected = false;
        this.database = FirebaseDatabase.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.mDatabase = database.getReference();
        this.getConnectionStateRef();

        //Initialize the SchemaValidator and create a listener query that triggers the Validator
        this.mSchemaValidator = new SchemaValidator();
        this.attachListenerToDatabaseRef();

    }

    public void getConnectionStateRef() {
        // [START rtdb_listen_connected]
        this.connRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        this.connRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    isConnected = true;
                    Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
                } else {
                    isConnected = false;
                    Toast.makeText(context, "not connected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }

        });
    }

    public void attachListenerToDatabaseRef() {
        Query query = FirebaseDatabase.getInstance().getReference();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mSnapshot = snapshot;
                mSchemaValidator.testValidator();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void WriteNewPerson(Person person ) {
         String name= person.getName();
         String otherNames= person.getOtherNames();
         String dob=person.getDob();
         String photoDesc=person.getPhotoDesc();
         String mainRoleId=person.getMainRoleId();
         String introQuestDesc=person.getIntroQuestDesc();
         String email=person.getEmail();
         String password=person.getPassword();

        mDatabase.child("People").child(person.id).child("Name").setValue(name);
        mDatabase.child("People").child(person.id).child("Other Names").setValue(otherNames);
        mDatabase.child("People").child(person.id).child("Date of Birth").setValue(dob);
        mDatabase.child("People").child(person.id).child("Photo Desc").setValue(photoDesc);
        mDatabase.child("People").child(person.id).child("Main Role Id").setValue(mainRoleId);
        mDatabase.child("People").child(person.id).child("Intro Question Description").setValue(introQuestDesc);
        mDatabase.child("People").child(person.id).child("Email").setValue(email);
        mDatabase.child("People").child(person.id).child("password").setValue(password);

    }

    //The methods below were written by Blake to generalize the insert/delete/update process. They're still being tested.

    /**
     * Handles any data model extending Entry, parses the table name and inserts into the server database
     *
     * @param table the table into which the data is being inserted
     * @param entry the data being inserted
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void insert (Table<E> table, E entry) throws InvocationTargetException, IllegalAccessException {
        String tableName = table.getTableName();
        String entryId = entry.getId();
        List<Method> entryMethods = entry.getGetters();

        for (Method method: entryMethods) {
            String key = method.getName().substring(GET);
            Object value = method.invoke(entry);
            if(value != null) {
                if (value.getClass().isArray()) {
                    storeMedia(tableName, entryId, key, value);
                } else {
                    mDatabase.child(tableName).child(entryId).child(key).setValue(value);
                }
            }
        }
    }

    /**
     * Deletes an entry from the database
     *
     * @param table the table from which the entry is being removed
     * @param entryID the id of the entry being removed
     */
    public void delete (Table<E> table, String entryID) {
        String name = table.getTableName();
        mDatabase.child(name).child(entryID).removeValue();
    }

    /**
     * Updates an existing entry in the database; if entry doesn't exist, it inserts it instead
     *
     * @param table table where the value(s) to update is(are)
     * @param entry the entry to update
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void update (Table<E> table, E entry) throws InvocationTargetException, IllegalAccessException {
        String tableName = table.getTableName();
        String entryId = entry.getId();
        List<Method> entryMethods = entry.getGetters();

        Map<String, Object> updates = new HashMap<>();
        for (Method method : entryMethods) {
            String key = method.getName().substring(GET);
            Object value = method.invoke(entry);
            if (value != null) {
                updates.put(tableName + "/" + entryId + "/" + key, value);
            }
        }

        mDatabase.updateChildren(updates);
    }

    /**
     * Stores a media object (typically an image, in the case of this database) in the Firebase
     * storage.
     *
     * @param name Root variable which, along with entryID, defines the path to store the object
     * @param entryId Identifies the object in the storage path
     * @param key Name of file
     * @param value The file itself
     */
    public void storeMedia(String name, String entryId, String key, Object value) {
        //TODO: Generalize storageReferencePath for instances that aren't bmp!
        String storageReferencePath = name + "/" + entryId + "/" + key + ".bmp";
        StorageReference storageLocation = storage.getReference(storageReferencePath);

        UploadTask uploadTask = storageLocation.putBytes((byte[]) value);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    /**
     * Utility to validate the structure and type for values stored in Firebase's Realtime Database,
     * by checking against the client-side SQLite-based database.
     */
    private class SchemaValidator {
        private JSONObject sqlSchema;
        private final String TAG = "Validator";

        public SchemaValidator() {
            initializeSqlSchema();
        }

        /**
         * Generates a JSON file containing the layout of the SQLite-based database on the client
         * device.
         */
        public void initializeSqlSchema() {
            sqlSchema = new JSONObject();

            for (Table<?> table : DatabaseHelper.TABLES) {
                try {
                    sqlSchema.put(table.getTableName(),new JSONArray(table.tableColumns));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

        /**
         * Print JSON file containing client SQLite database structure to log.
         */
        public void readSchema() {
            Log.i(TAG,"Client (SQL) database schema in JSON: " + sqlSchema.toString());
        }

        /**
         * Scans nodes in Firebase database and compares to structure of client SQLite database.
         * Identifies (1) Tables on server that don't exist on client; (2) Columns in "tables" on
         * server that don't exist in corresponding tables on client; and (3) values on the server
         * with data types that don't match the corresponding values on the client.
         * 
         * TODO: verify that values are totally equal (not just same value), check both ways for parity (not just that server against client but client against server) 
         * 
         * @return false if any of the three conditions above are met, true otherwise
         */
        public boolean schemaIsValid () {
            boolean valid = true;

            if (mSnapshot != null) {
                Log.i(TAG, "Checking for tables not present in SQL database...");
                for (DataSnapshot table : mSnapshot.getChildren()) {
                    String tableName = table.getKey();
                    if (!sqlSchema.has(tableName)) {
                        Log.w(TAG, "Invalid table: " + tableName);
                        valid = false;
                    } else {
                        Log.i(TAG, "Checking columns and values in table " + tableName + "...");
                        for (DataSnapshot entry : table.getChildren()) {
                            for (DataSnapshot column : entry.getChildren()) {
                                try {
                                    JSONArray columnsInSqlSchema = sqlSchema.getJSONArray(tableName);
                                    String columnName = column.getKey();
                                    boolean columnExists = false;
                                    for (int i = 0; i < columnsInSqlSchema.length(); i++) {
                                        if (columnsInSqlSchema.get(i).toString().equalsIgnoreCase(columnName)) {
                                            columnExists = true;
                                            try {
                                                Class<?> tableClass = Class.forName("edu.buffalo.cse.ubcollecting.data.models." + tableName);
                                                Field[] tableFields = tableClass.getFields();
                                                for (Field field : tableFields) {
                                                    if (field.getName().equalsIgnoreCase(columnName)) {
                                                        Class type = field.getType();
                                                        if (String.class.isAssignableFrom(type)) {
                                                            if (column.getValue() instanceof String) {
                                                            } else {
                                                                Log.w(TAG, "Invalid type: value of " + columnName + " for entry " + entry.getKey() + " (" + column.getValue().toString() + ") should be a String");
                                                                valid = false;
                                                            }
                                                        } else if (int.class.isAssignableFrom(type)) {
                                                            try {
                                                                Integer testInt = Integer.valueOf(column.getValue().toString());
                                                            } catch (NumberFormatException e) {
                                                                Log.w(TAG, "Invalid type: value of " + columnName + " for entry " + entry.getKey() + " (" + column.getValue().toString() + ") should be an int");
                                                                valid = false;
                                                            }
                                                        } else if (double.class.isAssignableFrom(type)) {
                                                            try {
                                                                Double testDouble = Double.valueOf(column.getValue().toString());
                                                            } catch (NumberFormatException e) {
                                                                Log.w(TAG, "Invalid type: value of " + columnName + " for entry " + entry.getKey() + " (" + column.getValue().toString() + ") should be a Double");
                                                                valid = false;
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (ClassNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    if (!columnExists) {
                                        if (!column.getKey().equals("Version")) {
                                            Log.w(TAG, "Column not found in SQL schema for table '" + tableName + "': " + column.getKey());
                                            valid = false;
                                        }

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            return valid;
        }

        /**
         * A (mostly redundant, at this point) method used to test the validator.
         */
        public void testValidator() {
            readSchema();
            String resultMsg;

            if (schemaIsValid()) {
                Log.i(TAG, "Schema is valid.");
            } else {
                Log.w(TAG, "Schema is invalid.");
            }
        }
    }
}
