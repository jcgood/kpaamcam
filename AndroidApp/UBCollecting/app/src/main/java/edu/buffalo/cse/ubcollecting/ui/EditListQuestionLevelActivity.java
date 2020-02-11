package edu.buffalo.cse.ubcollecting.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.models.Question;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_TABLE;
import static edu.buffalo.cse.ubcollecting.ui.AddListQuestionLevelActivity.QUESTION_LEVEL_DISPLAY_TEXT;
import static edu.buffalo.cse.ubcollecting.ui.AddListQuestionLevelActivity.QUESTION_POSITION;
import static edu.buffalo.cse.ubcollecting.ui.AddListQuestionLevelActivity.QUESTION_STRING;

public class EditListQuestionLevelActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mReorderListButton;
    private Button mAddQuestionButton;
    private Button mSaveLevelButton;
    private EditListQuestionAdapter mEditListQuestionAdapter;

    private ArrayList<String> mQuestionsTextArrayList;
    private ArrayList<String> mQuestionsIdArrayList;
    private int mQuestionPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_list_question_level_activity);

        mQuestionsTextArrayList = new ArrayList<>();
        mQuestionsIdArrayList = new ArrayList<>();
        String originalQuestionString = "";
        mQuestionPosition = 0;

        Intent intent = getIntent();
        if (intent != null) {
            originalQuestionString = intent.getStringExtra(QUESTION_STRING);
            mQuestionPosition = intent.getIntExtra(QUESTION_POSITION, 0);
        }

        mQuestionsTextArrayList.add(originalQuestionString);
        mQuestionsIdArrayList.add("");

        ((TextView) findViewById(R.id.add_question_level_text_view))
                .setText(TextUtils.concat("Level ", String.valueOf(mQuestionPosition + 1)));

        initializeViewVariable();
        setListeners();

        mEditListQuestionAdapter = new EditListQuestionAdapter(this);
        RecyclerView editListQuestionRecyclerView = findViewById(R.id.add_question_level_recycler_view);
        editListQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        editListQuestionRecyclerView.setAdapter(mEditListQuestionAdapter);
    }

    private void initializeViewVariable() {
        mReorderListButton = findViewById(R.id.add_question_level_reorder_list);
        mAddQuestionButton = findViewById(R.id.add_question_level_add_level_button);
        mSaveLevelButton = findViewById(R.id.add_question_level_submit_button);
    }

    private void setListeners() {
        mReorderListButton.setOnClickListener(this);
        mAddQuestionButton.setOnClickListener(this);
        mSaveLevelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_question_level_reorder_list) {
            //TODO
        }
        else if (view.getId() == R.id.add_question_level_add_level_button) {
            mQuestionsTextArrayList.add("");
            mQuestionsIdArrayList.add("");
            mEditListQuestionAdapter.notifyDataSetChanged();
        }
        else if (view.getId() == R.id.add_question_level_submit_button) {
            if (mQuestionsIdArrayList.isEmpty()) {
                Toast.makeText(
                        EditListQuestionLevelActivity.this,
                        "Please create a question level",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Intent returnIntent = new Intent();
            returnIntent.putExtra(QUESTION_STRING,
                    AddListQuestionLevelActivity.concatListQuestions(mQuestionsIdArrayList, '#'));
            returnIntent.putExtra(QUESTION_POSITION, mQuestionPosition);
            returnIntent.putExtra(QUESTION_LEVEL_DISPLAY_TEXT, mQuestionsTextArrayList.get(0));
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    private class EditListQuestionViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView mQuestionOrderTextView;
        private TextView mQuestionTextView;
        private int mPosition;

        EditListQuestionViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.add_list_question_level_list_item, parent, false));

            mQuestionOrderTextView =
                    itemView.findViewById(R.id.add_list_question_level_level_text_view);
            mQuestionTextView =
                    itemView.findViewById(R.id.add_list_question_level_list_item_text_view);
            ImageButton deleteQuestionButton =
                    itemView.findViewById(R.id.add_list_question_level_delete_level_button);
            ImageButton editQuestionButton =
                    itemView.findViewById(R.id.add_list_question_level_view_level_button);

            deleteQuestionButton.setOnClickListener(this);
            editQuestionButton.setOnClickListener(this);
        }

        void bind(int position) {
            mPosition = position;
            mQuestionOrderTextView.setText(String.valueOf(mPosition + 1));
            mQuestionTextView.setText(mQuestionsTextArrayList.get(mPosition));
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.add_list_question_level_delete_level_button) {
                mQuestionsTextArrayList.remove(mPosition);
                mEditListQuestionAdapter.notifyDataSetChanged();
            }
            else if (view.getId() == R.id.add_list_question_level_view_level_button) {
                final View dialogView = getLayoutInflater()
                        .inflate(R.layout.edit_list_question_level_pick_question_dialog, null, false);

                Spinner dialogSpinner =
                        dialogView.findViewById(R.id.edit_list_question_property_spinner);

                dialogSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedItem = adapterView.getItemAtPosition(i).toString();
                        ArrayList<Question> questionArrayList = QUESTION_TABLE.getAll();

                        if (selectedItem.equals(getString(R.string.all_questions))) {
                            createQuestionListView(dialogView, questionArrayList);
                            return;
                        }

                        ArrayList<Question> selectiveQuestionArrayList = new ArrayList<>();
                        for (Question question : questionArrayList) {
                            System.out.println(question.getType());
                            if (selectedItem.equals(question.getType())) {
                                selectiveQuestionArrayList.add(question);
                            }
                        }

                        createQuestionListView(dialogView, selectiveQuestionArrayList);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) { }
                });

                AlertDialog dialog = new AlertDialog.Builder(EditListQuestionLevelActivity.this)
                        .setView(dialogView)
                        .setCancelable(false)
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mEditListQuestionAdapter.notifyDataSetChanged();
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        }

        private void createQuestionListView(View view, final ArrayList<Question> questionArrayList) {
            ListView listView = view.findViewById(R.id.edit_list_question_list_view);
            final ArrayList<String> questionDisplayTextArrayList = new ArrayList<>();

            for (Question question : questionArrayList) {
                System.out.println(question.displayText);
                if (question.getDisplayText() != null) {
                    questionDisplayTextArrayList.add(question.getDisplayText());
                }
                else {
                    questionDisplayTextArrayList.add("Question text N/A");
                }
            }

            ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(
                    EditListQuestionLevelActivity.this,
                    android.R.layout.simple_list_item_1,
                    questionDisplayTextArrayList);
            listView.setAdapter(listViewAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mQuestionsTextArrayList.set(mPosition, questionDisplayTextArrayList.get(i));
                    mQuestionsIdArrayList.set(mPosition, questionArrayList.get(i).getId());
                }
            });
        }
    }

    private class EditListQuestionAdapter extends RecyclerView.Adapter<EditListQuestionViewHolder> {

        private Context mContext;

        EditListQuestionAdapter(Context context) {
            mContext = context;
        }

        @Override
        public EditListQuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EditListQuestionViewHolder(LayoutInflater.from(mContext), parent);
        }

        @Override
        public void onBindViewHolder(EditListQuestionViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mQuestionsTextArrayList.size();
        }
    }
}
