package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;



//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.ui.LoginActivity;

import static edu.buffalo.cse.ubcollecting.SessionActivity.getSession;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

/**
 * Activity that allows interviewer to select which questionnaire to take
 */
public class UserSelectQuestionnaireActivity extends AppCompatActivity {

    private static final String TAG = UserSelectQuestionnaireActivity.class.getSimpleName();

    public final static String SELECTED_QUESTIONNAIRE = "SelectedQuestionnaire";

    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getSession(getIntent()).getId(), "SESSION ID MAKING IT");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select_questionnaire);

        Toolbar toolbar = findViewById(R.id.user_select_questionnaire_toolbar);
        setSupportActionBar(toolbar);

        entryRecyclerView = findViewById(R.id.questionnaire_recycler_view);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        entryAdapter = new UserSelectQuestionnaireActivity.EntryAdapter(DatabaseHelper.QUESTIONNAIRE_TABLE.getAll());
        entryRecyclerView.setAdapter(entryAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Intent intent = new Intent(UserSelectQuestionnaireActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return false;
    }

    private class EntryHolder extends RecyclerView.ViewHolder {

        private Questionnaire questionnaire;
        private Button selectButton;


        public EntryHolder(View view) {
            super(view);

            selectButton = view.findViewById(R.id.entry_list_select_button);
        }

        public void bindEntry(final Questionnaire questionnaire) {
            this.questionnaire = questionnaire;
            selectButton.setText(this.questionnaire.getIdentifier());
            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = ViewQuestionsActivity.newIntent(UserSelectQuestionnaireActivity.this);
                    i.putExtra(SELECTED_SESSION, getSession(getIntent()));
                    i.putExtra(SELECTED_QUESTIONNAIRE, questionnaire);
                    startActivity(i);
                    finish();
                }
            });
        }
    }

    private class EntryAdapter extends RecyclerView.Adapter<UserSelectQuestionnaireActivity.EntryHolder> {

        private List<Questionnaire> entryList;

        public EntryAdapter(List<Questionnaire> entryList) {
            this.entryList = entryList;
        }

        public void setEntryList(List<Questionnaire> entryList) {
            this.entryList = entryList;
        }

        @Override
        public UserSelectQuestionnaireActivity.EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.questionnaire_item_view, parent, false);
            return new UserSelectQuestionnaireActivity.EntryHolder(view);
        }

        @Override
        public void onBindViewHolder(UserSelectQuestionnaireActivity.EntryHolder holder, int position) {
            Questionnaire entry = entryList.get(position);
            holder.bindEntry(entry);
        }

        private static final String TAG = "EntryAdapter";

        @Override
        public int getItemCount() {
            return entryList.size();
        }
    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, UserSelectQuestionnaireActivity.class);
        return i;
    }

    @Override
    public void onBackPressed() {
        Intent intent = UserLandingActivity.newIntent(UserSelectQuestionnaireActivity.this);
        startActivity(intent);
        finish();
    }

}