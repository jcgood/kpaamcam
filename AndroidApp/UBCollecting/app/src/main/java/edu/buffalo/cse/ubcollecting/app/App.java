package edu.buffalo.cse.ubcollecting.app;

/**
 * Created by aamel786 on 2/17/18.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.DatabaseManager;

public class App extends Application {

    private static final String PREFERENCES_KEY = "edu.buffalo.cse.ubcollecting.app.preferences_key";
    private static final String FIRST_RUN_KEY = "edu.buffalo.cse.ubcollecting.app.firs_run_key";

    private static Context context;
    private static DatabaseHelper dbHelper;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        dbHelper = new DatabaseHelper();
        DatabaseManager.initializeInstance(dbHelper);


        SharedPreferences preferences = getSharedPreferences(PREFERENCES_KEY, Activity.MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean(FIRST_RUN_KEY, true);
        if (isFirstRun) {
            DatabaseHelper.populateData();
            preferences.edit().putBoolean(FIRST_RUN_KEY, false).apply();
        }
    }

}
