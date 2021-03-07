package edu.buffalo.cse.ubcollecting;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.buffalo.cse.ubcollecting.data.models.Model;
import edu.buffalo.cse.ubcollecting.data.tables.Table;
import edu.buffalo.cse.ubcollecting.ui.CreateQuestionActivity;
import edu.buffalo.cse.ubcollecting.ui.LoginActivity;

public abstract class TableListActivity extends AppCompatActivity {

    public static final String TAG = TableListActivity.class.getSimpleName();

    private RecyclerView tableRecyclerView;
    private RecyclerView.Adapter tableAdapter;

    protected abstract List<Table<? extends Model>> getTables();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);

        Toolbar toolbar = findViewById(R.id.table_list_toolbar);
        setSupportActionBar(toolbar);

        tableRecyclerView = findViewById(R.id.table_recycler_view);
        tableRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tableAdapter = new TableAdapter(getTables());
        tableRecyclerView.setAdapter(tableAdapter);
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
            Intent intent = new Intent(TableListActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return false;
    }

    private class TableHolder extends RecyclerView.ViewHolder {

        private Table table;
        private TextView tableNameView;
        private ImageButton insertButton;
        private ImageButton viewButton;


        public TableHolder(View view) {
            super(view);

            tableNameView = view.findViewById(R.id.table_item_name_view);
            insertButton = view.findViewById(R.id.table_item_insert_button);
            viewButton = view.findViewById(R.id.table_item_view_button);
        }

        public void bindTable(Table<?> table1) {
            this.table = table1;
            tableNameView.setText(table.getTableName());

            insertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = null;
                    if (table.getTableName().equals("Question")) {
                        i = new Intent(TableListActivity.this, CreateQuestionActivity.class);
                    } else {
                        i = table.insertActivityIntent(TableListActivity.this);

                    }
                    startActivity(i);
                }
            });

            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = TableViewActivity.newIntent(TableListActivity.this, table);
                    startActivity(i);
                }
            });
        }
    }

    private class TableAdapter extends RecyclerView.Adapter<TableHolder> {

        private List<Table<?>> tableList;

        public TableAdapter(List<Table<?>> tableList) {
            this.tableList = tableList;
        }

        @Override
        public TableHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.table_list_item_view, parent, false);
            return new TableHolder(view);
        }

        @Override
        public void onBindViewHolder(TableHolder holder, int position) {
            Table table = tableList.get(position);
            holder.bindTable(table);

        }

        @Override
        public int getItemCount() {
            return tableList.size();
        }
    }
}

