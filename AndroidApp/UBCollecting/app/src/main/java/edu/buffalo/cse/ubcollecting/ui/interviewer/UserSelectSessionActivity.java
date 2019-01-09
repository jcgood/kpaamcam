package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Session;

import static edu.buffalo.cse.ubcollecting.EntryActivity.REQUEST_CODE_EDIT_ENTRY;
import static edu.buffalo.cse.ubcollecting.SessionActivity.getFieldTrip;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserLandingActivity.REQUEST_CODE_ADD_ENTRY;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserLandingActivity.SELECTED_FIELD_TRIP;


/**
 * Activity that allows interviewer to select/create a session
 */
public class UserSelectSessionActivity extends AppCompatActivity {

    private static final String TAG = UserSelectSessionActivity.class.getSimpleName();


    public final static String SELECTED_SESSION = "SelectedSession";

    private Button createSession;
    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select_session);

        entryRecyclerView = findViewById(R.id.session_recycler_view);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        entryAdapter = new UserSelectSessionActivity.EntryAdapter(DatabaseHelper.SESSION_TABLE.getFieldTripSessions(getFieldTrip(getIntent())));
        entryRecyclerView.setAdapter(entryAdapter);

        createSession = findViewById(R.id.create_new_session);

        createSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = DatabaseHelper.SESSION_TABLE.insertActivityIntent(UserSelectSessionActivity.this);
                i.putExtra(SELECTED_FIELD_TRIP,getFieldTrip(getIntent()));
                startActivityForResult(i,REQUEST_CODE_ADD_ENTRY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_EDIT_ENTRY || requestCode == REQUEST_CODE_ADD_ENTRY) {
            entryAdapter.setEntryList(DatabaseHelper.SESSION_TABLE.getFieldTripSessions(getFieldTrip(getIntent())));
            entryAdapter.notifyDataSetChanged();
        }

    }

    private class EntryHolder extends RecyclerView.ViewHolder {

        private Session session;
        private Button selectButton;
        private ImageButton editButton;
        private ImageButton deleteButton;


        public EntryHolder(View view) {
            super(view);

            selectButton = view.findViewById(R.id.entry_list_select_button);
            editButton = view.findViewById(R.id.entry_list_edit_button);
            deleteButton = view.findViewById(R.id.entry_list_delete_button);
        }

        public void bindEntry(final Session session) {
            this.session = session;
            selectButton.setText(this.session.getIdentifier());

            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = AddSessionRolesActivity.newIntent(UserSelectSessionActivity.this);
                    i.putExtra(SELECTED_FIELD_TRIP,getFieldTrip(getIntent()));
                    i.putExtra(SELECTED_SESSION, session);
                    Log.i(session.getId(), "SESSION ID SELECTED");
                    startActivity(i);
                    finish();
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = DatabaseHelper.SESSION_TABLE.editActivityIntent(UserSelectSessionActivity.this, EntryHolder.this.session);
                    i.putExtra(SELECTED_FIELD_TRIP,getFieldTrip(getIntent()));
                    i.putExtra(SELECTED_SESSION, session);
                    startActivityForResult(i, REQUEST_CODE_EDIT_ENTRY);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder confirmDelete = new AlertDialog.Builder(UserSelectSessionActivity.this);
                    confirmDelete.setMessage("Do you want to delete this entry?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseHelper.SESSION_TABLE.delete(EntryHolder.this.session.id);
                                    entryAdapter.setEntryList(DatabaseHelper.SESSION_TABLE.getFieldTripSessions(getFieldTrip(getIntent())));
                                    entryAdapter.notifyDataSetChanged();
                                    Toast.makeText(getApplicationContext(), "Entry Deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel",null);
                    AlertDialog alert = confirmDelete.create();
                    alert.setTitle("Confirm Selection");
                    alert.show();
                }
            });
        }
    }

    private class EntryAdapter extends RecyclerView.Adapter<UserSelectSessionActivity.EntryHolder> {

        private List<Session> entryList;

        public EntryAdapter(List<Session> entryList) {
            this.entryList = entryList;
        }

        public void setEntryList(List<Session> entryList) {
            this.entryList = entryList;
        }

        @Override
        public UserSelectSessionActivity.EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.field_trip_item_view, parent, false);
            return new UserSelectSessionActivity.EntryHolder(view);
        }

        @Override
        public void onBindViewHolder(UserSelectSessionActivity.EntryHolder holder, int position) {
            Session entry = entryList.get(position);
            holder.bindEntry(entry);
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }

    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, UserSelectSessionActivity.class);
        return i;
    }


}