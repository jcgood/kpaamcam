package edu.buffalo.cse.ubcollecting.data;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
//THIS FILE IS AN ATTEMPT TO CENTRALIE CLOUD OPERATIONS
//NOTE:NOT SURE IF THIS IS THE BEST METHOD YET
public class FireBaseCloudHelper<E extends Model> extends Application{

    public FirebaseDatabase database;
    public FirebaseStorage storage;
    public DatabaseReference mDatabase;
    public DatabaseReference connRef;
    private Context context;
    private String TAG;
    public boolean isConnected;
    static final short GET = 3;


    public FireBaseCloudHelper(Context context) {
        this.context = context;
        this.TAG = App.getContext().toString();
        this.isConnected = false;
        this.database = FirebaseDatabase.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.mDatabase = database.getReference();
        this.getConnectionStateRef();
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

    };

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
            String key = method.getName().substring(GET, method.getName().length());
            Object value = method.invoke(entry);
            if(value != null) {
                if (value.getClass().isArray()) {
                    storeMedia(tableName, entryId, key, value);
                } else {
                    mDatabase.child(tableName).child(entryId).child(key).setValue(value);
                }
            }
        }
    };

    /**
     * Deletes an entry from the database
     *
     * @param table the table from which the entry is being removed
     * @param entryID the id of the entry being removed
     */
    public void delete (Table<E> table, String entryID) {
        String name = table.getTableName();

        if (mDatabase.child(name).child(entryID).get() != null) {
            mDatabase.child(name).child(entryID).removeValue();
        }
    };

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

        if (mDatabase.child(tableName).child(entryId).get() != null) {
            Map<String, Object> updates = new HashMap<>();
            for (Method method : entryMethods) {
                String key = method.getName().substring(GET, method.getName().length());
                Object value = method.invoke(entry);
                if (value != null) {
                    updates.put(tableName + "/" + entryId + "/" + key, value);
                }
            }

            mDatabase.updateChildren(updates);
        } else {
            insert(table, entry);
        }
    };

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
    };
}
