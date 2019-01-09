package edu.buffalo.cse.ubcollecting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import edu.buffalo.cse.ubcollecting.data.models.Model;
import edu.buffalo.cse.ubcollecting.data.tables.Table;
import edu.buffalo.cse.ubcollecting.ui.CreateQuestionActivity;

public abstract class TableListActivity extends AppCompatActivity {

    public static final String TAG = TableListActivity.class.getSimpleName();

    private RecyclerView tableRecyclerView;
    private RecyclerView.Adapter tableAdapter;

    protected abstract List<Table<? extends Model>> getTables();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);

        tableRecyclerView = findViewById(R.id.table_recycler_view);
        tableRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tableAdapter = new TableAdapter(getTables());
        tableRecyclerView.setAdapter(tableAdapter);
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

