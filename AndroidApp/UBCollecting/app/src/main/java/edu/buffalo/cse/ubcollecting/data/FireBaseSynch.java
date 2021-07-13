package edu.buffalo.cse.ubcollecting.data;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.buffalo.cse.ubcollecting.app.App;
import edu.buffalo.cse.ubcollecting.data.models.Model;
import edu.buffalo.cse.ubcollecting.data.models.Person;
import edu.buffalo.cse.ubcollecting.data.tables.Table;

import static edu.buffalo.cse.ubcollecting.data.FireBaseCloudHelper.GET;

public class FireBaseSynch<E extends Model> {

    private final Class<E> clazz;
    public Context context;
    public Table<E> table;
    DatabaseReference mTableRef;


    public FireBaseSynch(Context context, Class<E> clazz, Table<E> table) {
        this.clazz = clazz;
        this.context = context;
        this.table = table;
        this.mTableRef = FirebaseDatabase.getInstance().getReference(table.getTableName());
        this.getData();

    }

    public void getData() {
        mTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot modelSnapshot : snapshot.getChildren()) {
                    E fbItem = modelSnapshot.getValue(clazz); // <- now is type safe.

                    List<Method> entryMethods = fbItem.getGetters();
                    List<Method> setMethods = fbItem.getSetters();
                    List<Object> objects = new ArrayList<Object>();

                    //TODO:maybe can use setters to set values for generic type?
                    int i = 0;
                    for (Method method : entryMethods) {
                        String key = method.getName().substring(GET);
                        Object value = modelSnapshot.child(key).getValue(method.getReturnType());
//                                Toast.makeText(context, key+" : "+value.toString(), Toast.LENGTH_SHORT).show();
                        //objects.add(value);
                        try {
                            setMethods.get(i).invoke(fbItem, value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        i++;
                    }

                    //if the fbItem IS NOT in the sql lite table insert the fbItem
                    E sqlItem = table.findById(fbItem.id);
                    if (!sqlItem.id.equals(fbItem.id)) {
                        table.insert(fbItem);
                        Toast.makeText(context, "New Data has been added from cloud", Toast.LENGTH_SHORT).show();
                    }
                    //if fb IS already in the table
                    else {

                        try {
                            //get sqlversion
                            int sqlVersion = getVersionFromGeneric(sqlItem);
                            //get firebase version
                            int fbVersion = getVersionFromGeneric(sqlItem);
                            if(fbVersion > sqlVersion){
                                table.update(fbItem);
                            }
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        //table.update(fbItem);
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w(context.toString(), "loadModel:onCancelled", error.toException());

            }
        });

    }

    int getVersionFromGeneric(E model) throws InvocationTargetException, IllegalAccessException {


        List<Method> entryMethods = model.getGetters();
        List<Object> objects = new ArrayList<Object>();
        int retval = 0;

        for (int i = 0; i < entryMethods.size(); i++) {
            Method method = entryMethods.get(i);
            String key = method.getName().substring(GET);
            if (key.equals("version")) {
                try {
                    retval =  (int)method.invoke(model);
                }catch(Exception e){
                    Log.w(context.toString(),"Error while getting version number");
                    Log.w(context.toString(),e.toString());
                    Toast.makeText(context,  "exception"+ String.valueOf(retval), Toast.LENGTH_SHORT).show();
                    return 0;
                }
            }

        }
        Toast.makeText(context,  "no exception"+ String.valueOf(retval), Toast.LENGTH_SHORT).show();
        return retval;
    }
}
