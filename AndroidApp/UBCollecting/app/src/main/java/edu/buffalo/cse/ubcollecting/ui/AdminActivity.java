package edu.buffalo.cse.ubcollecting.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.ubcollecting.TableListActivity;
import edu.buffalo.cse.ubcollecting.data.models.Model;
import edu.buffalo.cse.ubcollecting.data.tables.Table;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.FIELD_TRIP_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.LANGUAGE_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.PERSON_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.ROLE_TABLE;

/**
 * Activity for main menu of an admin account
 */
public class AdminActivity extends TableListActivity {

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, AdminActivity.class);
        return i;
    }

    @Override
    protected List<Table<? extends Model>> getTables() {
        List<Table<? extends Model>> tables = new ArrayList<>();
        tables.add(PERSON_TABLE);
        tables.add(ROLE_TABLE);
        tables.add(QUESTIONNAIRE_TABLE);
        tables.add(LANGUAGE_TABLE);
        tables.add(QUESTION_TABLE);
        tables.add(FIELD_TRIP_TABLE);
        return tables;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
