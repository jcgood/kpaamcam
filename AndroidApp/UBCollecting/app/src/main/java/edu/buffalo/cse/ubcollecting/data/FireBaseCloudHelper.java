package edu.buffalo.cse.ubcollecting.data;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.ubcollecting.EntryActivity;
import edu.buffalo.cse.ubcollecting.app.App;
import edu.buffalo.cse.ubcollecting.data.models.Person;
import edu.buffalo.cse.ubcollecting.data.tables.Table;
//THIS FILE IS AN ATTEMPT TO CENTRALIE CLOUD OPERATIONS
//NOTE:NOT SURE IF THIS IS THE BEST METHOD YET
public class FireBaseCloudHelper extends Application{

    public FirebaseDatabase database;
    public DatabaseReference mDatabase;
    public DatabaseReference connRef;
    private Context context;
    private String TAG;
    public boolean isConnected;

    public FireBaseCloudHelper(Context context) {
        this.context = context;
        this.TAG = App.getContext().toString();
        this.isConnected = false;
        this.database = FirebaseDatabase.getInstance();
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

    }
}
