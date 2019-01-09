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
import edu.buffalo.cse.ubcollecting.data.models.FieldTrip;

import static edu.buffalo.cse.ubcollecting.EntryActivity.REQUEST_CODE_EDIT_ENTRY;

/**
 * Landing activity that interviewer sees upon login that allows them to select/create a field trip.
 */
public class UserLandingActivity extends AppCompatActivity {

    private static final String TAG = UserLandingActivity.class.getSimpleName();

    public final static int REQUEST_CODE_ADD_ENTRY = 3;
    public final static String SELECTED_FIELD_TRIP = "SelectedFieldTrip";
    public final static int FLAG_INTERVIEWER_EDIT = 4;

    private Button createFieldTrip;
    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_landing);

        entryRecyclerView = findViewById(R.id.fieldtrip_recycler_view);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        entryAdapter = new UserLandingActivity.EntryAdapter(DatabaseHelper.FIELD_TRIP_TABLE.getActiveFieldTrips());
        entryRecyclerView.setAdapter(entryAdapter);

        createFieldTrip = findViewById(R.id.create_new_fieldtrip);

        createFieldTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = DatabaseHelper.FIELD_TRIP_TABLE.insertActivityIntent(UserLandingActivity.this);
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
            entryAdapter.setEntryList(DatabaseHelper.FIELD_TRIP_TABLE.getActiveFieldTrips());
            Log.i(DatabaseHelper.FIELD_TRIP_TABLE.getActiveFieldTrips().toString(), "HELLOOO");
            entryAdapter.notifyDataSetChanged();
        }

    }

    private class EntryHolder extends RecyclerView.ViewHolder {

        private FieldTrip fieldTrip;
        private Button selectButton;
        private ImageButton editButton;
        private ImageButton deleteButton;


        public EntryHolder(View view) {
            super(view);

            selectButton = view.findViewById(R.id.entry_list_select_button);
            editButton = view.findViewById(R.id.entry_list_edit_button);
            deleteButton = view.findViewById(R.id.entry_list_delete_button);
        }

        public void bindEntry(final FieldTrip fieldTrip) {
            this.fieldTrip = fieldTrip;
            selectButton.setText(this.fieldTrip.getIdentifier());

            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = UserSelectSessionActivity.newIntent(UserLandingActivity.this);
                    i.putExtra(SELECTED_FIELD_TRIP, fieldTrip);
                    startActivity(i);
                    finish();
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = DatabaseHelper.FIELD_TRIP_TABLE.editActivityIntent(UserLandingActivity.this, EntryHolder.this.fieldTrip);
                    startActivityForResult(i, REQUEST_CODE_EDIT_ENTRY);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder confirmDelete = new AlertDialog.Builder(UserLandingActivity.this);
                    confirmDelete.setMessage("Do you want to delete this entry?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseHelper.FIELD_TRIP_TABLE.delete(EntryHolder.this.fieldTrip.id);
                                    entryAdapter.setEntryList(DatabaseHelper.FIELD_TRIP_TABLE.getActiveFieldTrips());
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

    private class EntryAdapter extends RecyclerView.Adapter<UserLandingActivity.EntryHolder> {

        private List<FieldTrip> entryList;

        public EntryAdapter(List<FieldTrip> entryList) {
            this.entryList = entryList;
        }

        public void setEntryList(List<FieldTrip> entryList) {
            this.entryList = entryList;
        }

        @Override
        public UserLandingActivity.EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.field_trip_item_view, parent, false);
            return new UserLandingActivity.EntryHolder(view);
        }

        @Override
        public void onBindViewHolder(UserLandingActivity.EntryHolder holder, int position) {
            FieldTrip entry = entryList.get(position);
            holder.bindEntry(entry);
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }
    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, UserLandingActivity.class);
        return i;
    }

}