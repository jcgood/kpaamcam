package edu.buffalo.cse.ubcollecting.app;

/**
 * Created by aamel786 on 2/17/18.
 */

import android.app.Activity;
import android.app.Application;
import android.app.Person;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.DatabaseManager;
import edu.buffalo.cse.ubcollecting.data.FireBaseSynch;
import edu.buffalo.cse.ubcollecting.data.models.Model;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.FIELD_TRIP_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.LANGUAGE_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.PERSON_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.ROLE_TABLE;

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
        FirebaseApp.initializeApp(this);


        SharedPreferences preferences = getSharedPreferences(PREFERENCES_KEY, Activity.MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean(FIRST_RUN_KEY, true);
        if (isFirstRun) {
            DatabaseHelper.populateData();
            preferences.edit().putBoolean(FIRST_RUN_KEY, false).apply();
        }

        FireBaseSynch personSync = new FireBaseSynch(App.getContext(), PERSON_TABLE.findById("123").getClass(), PERSON_TABLE);
//        FireBaseSynch roleSync = new FireBaseSynch(App.getContext(), ROLE_TABLE.findById("123").getClass(), ROLE_TABLE);
//        FireBaseSynch questionnaireSync = new FireBaseSynch(App.getContext(), QUESTIONNAIRE_TABLE.findById("123").getClass(), QUESTIONNAIRE_TABLE);
//        FireBaseSynch languageSync = new FireBaseSynch(App.getContext(), LANGUAGE_TABLE.findById("123").getClass(), LANGUAGE_TABLE);
//        FireBaseSynch questionSync = new FireBaseSynch(App.getContext(), QUESTION_TABLE.findById("123").getClass(), QUESTION_TABLE);
//        FireBaseSynch field_tripSync = new FireBaseSynch(App.getContext(), FIELD_TRIP_TABLE.findById("123").getClass(), FIELD_TRIP_TABLE);
    }

}
