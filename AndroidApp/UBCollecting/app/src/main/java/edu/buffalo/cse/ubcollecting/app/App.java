package edu.buffalo.cse.ubcollecting.app;

/**
 * Created by aamel786 on 2/17/18.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.DatabaseManager;

public class App extends Application {

    private static final String PREFERENCES_KEY = "edu.buffalo.cse.ubcollecting.app.preferences_key";
    private static final String FIRST_RUN_KEY = "edu.buffalo.cse.ubcollecting.app.firs_run_key";

    private static DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper();
        DatabaseManager.initializeInstance(dbHelper);
        FirebaseApp.initializeApp(this);


        SharedPreferences preferences = getSharedPreferences(PREFERENCES_KEY, Activity.MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean(FIRST_RUN_KEY, true);
        if (isFirstRun) {
            DatabaseHelper.populateData();
            preferences.edit().putBoolean(FIRST_RUN_KEY, false).apply();
        }
    }

}
