package edu.buffalo.cse.ubcollecting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import edu.buffalo.cse.ubcollecting.app.App;
import edu.buffalo.cse.ubcollecting.data.FireBaseCloudHelper;
import edu.buffalo.cse.ubcollecting.data.models.Model;
import edu.buffalo.cse.ubcollecting.data.tables.Table;
import edu.buffalo.cse.ubcollecting.ui.SyncCallback;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static edu.buffalo.cse.ubcollecting.EntryActivity.REQUEST_CODE_EDIT_ENTRY;

public class TableViewActivity extends AppCompatActivity implements SyncCallback {

    private static final String TAG = AppCompatActivity.class.getSimpleName();

    private static final String EXTRA_TABLE = "edu.buffalo.cse.ubcollecting.view_table";

    private Table<? extends Model> table;
    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;
    private FireBaseCloudHelper fireBaseCloudHelper;
    private LinearLayout updateBanner;
    private Button updateButton;

    public static Intent newIntent(Context packageContext, Table table) {
        Intent i = new Intent(packageContext, TableViewActivity.class);
        i.putExtra(EXTRA_TABLE, table);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_view);

        Serializable serializableExtra = getIntent().getSerializableExtra(EXTRA_TABLE);

        if (serializableExtra instanceof Table) {
            table = (Table) serializableExtra;
        } else {
            Log.e(TAG, "Extra was not of type Table");
            finish();
        }
        fireBaseCloudHelper = new FireBaseCloudHelper(App.getContext());
        entryRecyclerView = findViewById(R.id.entry_list_view);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        entryAdapter = new TableViewActivity.EntryAdapter(table.getAll());
        entryRecyclerView.setAdapter(entryAdapter);

        updateBanner = this.findViewById(R.id.updateButtonLayout);
        updateBanner.setVisibility(INVISIBLE);

        updateButton = this.findViewById(R.id.updateListButton);
        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                entryAdapter.setEntryList(table.getAll());
                entryAdapter.notifyDataSetChanged();
                updateBanner.setVisibility(INVISIBLE);
            }
        });

        //Tell the FireBaseSync to use this as a callback
        App.getFireBaseSynch().setCallBack(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_EDIT_ENTRY) {
            entryAdapter.setEntryList(table.getAll());
            entryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayUpdateBanner() {
        this.updateBanner.setVisibility(VISIBLE);
    }

    private class EntryHolder extends RecyclerView.ViewHolder {

        private Model entry;
        private TextView entryNameView;
        private ImageButton editButton;
        private ImageButton deleteButton;


        public EntryHolder(View view) {
            super(view);

            entryNameView = view.findViewById(R.id.entry_list_text_view);
            editButton = view.findViewById(R.id.entry_list_edit_button);
            deleteButton = view.findViewById(R.id.entry_list_delete_button);

            if(table.getTableName() == "Question"){
                deleteButton.setVisibility(INVISIBLE);
            }

            if(table.getTableName() == "Answer"){

            }

        }

        public void bindEntry(final Model entry1) {
            entry = entry1;
            entryNameView.setText(entry.getIdentifier());

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = table.editActivityIntent(TableViewActivity.this, entry);
                    startActivityForResult(i, REQUEST_CODE_EDIT_ENTRY);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder confirmDelete = new AlertDialog.Builder(TableViewActivity.this);
                    confirmDelete.setMessage("Do you want to delete this entry?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //deleting from local
                                    table.delete(entry.id);
                                    entryAdapter.setEntryList(table.getAll());
                                    entryAdapter.notifyDataSetChanged();

                                    //deleting from firebase
                                    fireBaseCloudHelper.delete(table,entry.id);
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

    private class EntryAdapter extends RecyclerView.Adapter<TableViewActivity.EntryHolder> {

        private List<? extends Model> entryList;

        public EntryAdapter(List<? extends Model> entryList) {
            this.entryList = entryList;
        }

        public void setEntryList(List<? extends Model> entryList) {
            this.entryList = entryList;
        }

        @Override
        public TableViewActivity.EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.entry_list_item_view, parent, false);
            return new TableViewActivity.EntryHolder(view);
        }

        @Override
        public void onBindViewHolder(TableViewActivity.EntryHolder holder, int position) {
            Model entry = entryList.get(position);
            holder.bindEntry(entry);
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }
    }


}
