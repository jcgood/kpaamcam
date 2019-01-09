package edu.buffalo.cse.ubcollecting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.ubcollecting.data.models.Model;
import edu.buffalo.cse.ubcollecting.data.tables.Table;

import static edu.buffalo.cse.ubcollecting.EntryActivity.REQUEST_CODE_EDIT_ENTRY;

/**
 * Created by kevinrathbun on 4/10/18.
 */

public abstract class TableSelectActivity<E> extends AppCompatActivity {

    private static final String TAG = TableSelectActivity.class.getSimpleName();

    private static final String EXTRA_TABLE = "edu.buffalo.cse.ubcollecting.select_table";

    private Table<? extends Model> table;
    private ArrayList<E> selections;

    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;
    private EditText searchText;
    private ImageButton clearSearchButton;
    private ImageButton searchButton;
    private Button doneButton;


    public static Intent newIntent(Context packageContext, Table table) {
        Intent i = new Intent(packageContext, TableViewActivity.class);
        i.putExtra(EXTRA_TABLE, table);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);

        Serializable serializableExtra = getIntent().getSerializableExtra(EXTRA_TABLE);

        if (serializableExtra instanceof Table) {
            table = (Table) serializableExtra;
        } else {
            Log.e(TAG, "Extra was not of type Table");
            finish();
        }

        selections = new ArrayList<>();

        searchText = findViewById(R.id.table_select_search_view);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    clearSearchButton.setVisibility(View.VISIBLE);
                } else {
                    clearSearchButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        clearSearchButton = findViewById(R.id.table_select_clear_button);
        clearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
                ArrayList<? extends Model> results = table.getAll();
                entryAdapter.setEntryList(table.getAll());
                entryAdapter.notifyDataSetChanged();
            }
        });

        searchButton = findViewById(R.id.table_select_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchText.getText().toString();
                ArrayList<? extends Model> selections = search(query, table.getAll());
                entryAdapter.setEntryList(selections);
                entryAdapter.notifyDataSetChanged();
            }
        });

        doneButton = findViewById(R.id.table_select_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectionDone(selections);
            }
        });

        entryRecyclerView = findViewById(R.id.table_select_recycler);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        entryAdapter = new TableSelectActivity.EntryAdapter(table.getAll());
        entryRecyclerView.setAdapter(entryAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_EDIT_ENTRY) {
            entryAdapter.setEntryList(table.getAll());
            entryAdapter.notifyDataSetChanged();
        }
    }

    public abstract ArrayList<? extends Model> search(String query, ArrayList<? extends Model> selections);

    public abstract void onSelectionDone(ArrayList<E> selections);

    private class EntryHolder extends RecyclerView.ViewHolder {

        private E entry;
        private CheckBox selectBox;
        private TextView entryNameView;


        public EntryHolder(View view) {
            super(view);

            selectBox = findViewById(R.id.entry_list_select_box);
            entryNameView = findViewById(R.id.entry_list_select_text_view);

            selectBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectBox.isChecked() && !selections.contains(entry)) {
                        selections.add(entry);
                    } else {
                        selections.remove(entry);
                    }
                }
            });
        }

        public void bindEntry(Model entry1) {
            //TODO
        }
    }

    private class EntryAdapter extends RecyclerView.Adapter<TableSelectActivity.EntryHolder> {

        private List<? extends Model> entryList;

        public EntryAdapter(List<? extends Model> entryList) {
            this.entryList = entryList;
        }

        public void setEntryList(List<? extends Model> entryList) {
            this.entryList = entryList;
        }

        @Override
        public TableSelectActivity.EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.entry_list_item_select, parent, false);
            return new TableSelectActivity.EntryHolder(view);
        }

        @Override
        public void onBindViewHolder(TableSelectActivity.EntryHolder holder, int position) {
            Model entry = entryList.get(position);
            holder.bindEntry(entry);
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }
    }
}

