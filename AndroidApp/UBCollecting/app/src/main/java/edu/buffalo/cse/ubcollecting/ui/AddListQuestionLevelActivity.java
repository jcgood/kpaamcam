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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.Question;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;

import static edu.buffalo.cse.ubcollecting.ui.CreateQuestionActivity.LIST_QUESTION_EXTRA;

public class AddListQuestionLevelActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String QUESTION_STRING = "question_string";
    public static final String QUESTION_POSITION = "question_position";
    public static final String QUESTION_LEVEL_DISPLAY_TEXT = "question_level_display_text";
    private static final int REQUEST_CODE = 1;

    private TextView mQuestionTextTextView;
    private Button mAddQuestionLevelButton;
    private Button mSubmitButton;

    private AddQuestionLevelAdapter mAddQuestionLevelAdapter;
    private ArrayList<String> mQuestionLevelIdArrayList;
    private ArrayList<String> mQuestionLevelDisplayArrayList;
    private HashMap<Language, QuestionLangVersion> mOriginalQuestionTexts;
    private Question mQuestion;
    private Language mLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_list_question_level_activity);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mQuestion = (Question) intent.getSerializableExtra(LIST_QUESTION_EXTRA);
        }

        initializeViewVariables();
        setListeners();

        mOriginalQuestionTexts =
                DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(mQuestion.getId());

        mQuestionLevelIdArrayList = new ArrayList<>();
        mQuestionLevelDisplayArrayList = new ArrayList<>();

        if (mOriginalQuestionTexts.size() != 0 && mOriginalQuestionTexts.keySet().size() == 1) {
            mLanguage = mOriginalQuestionTexts.keySet().iterator().next();
            mQuestionTextTextView.setText(mQuestion.getDisplayText());
        }

        mAddQuestionLevelAdapter = new AddQuestionLevelAdapter(this);
        RecyclerView addQuestionLevelRecyclerView = findViewById(R.id.add_question_level_recycler_view);
        addQuestionLevelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addQuestionLevelRecyclerView.setAdapter(mAddQuestionLevelAdapter);
    }

    private void initializeViewVariables() {
        mQuestionTextTextView = findViewById(R.id.add_question_level_question_text_text_view);
        mAddQuestionLevelButton = findViewById(R.id.add_question_level_add_level_button);
        mSubmitButton = findViewById(R.id.add_question_level_submit_button);
    }

    private void setListeners() {
        mAddQuestionLevelButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_question_level_add_level_button) {
            mQuestionLevelIdArrayList.add(mQuestionLevelIdArrayList.size(), "");
            mQuestionLevelDisplayArrayList.add(mQuestionLevelDisplayArrayList.size(), "");
            Intent intent = new Intent(
                    AddListQuestionLevelActivity.this, EditListQuestionLevelActivity.class);
            intent.putExtra(QUESTION_STRING, "");
            intent.putExtra(QUESTION_POSITION, mQuestionLevelIdArrayList.size() - 1);
            startActivityForResult(intent, REQUEST_CODE);
        }
        else if (view.getId() == R.id.add_question_level_submit_button) {
            if (!mQuestionLevelIdArrayList.isEmpty()) {
                QuestionLangVersion quesLang = mOriginalQuestionTexts.get(mLanguage);
                quesLang.setQuestionText(concatListQuestions(mQuestionLevelIdArrayList, '|'));
                DatabaseHelper.QUESTION_LANG_VERSION_TABLE.update(quesLang);
                Toast.makeText(this, "Your Question has been submitted", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(this, "Please Enter Question a Level", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // The '|' is used to separate question levels
    // The '#' is used to separate questions within the same question level
    public static String concatListQuestions(ArrayList<String> questionArrayList, char delim) {
        String listQuestion = "";
        for (String question : questionArrayList) {
            if (!question.isEmpty()) {
                listQuestion = listQuestion.concat(delim + question);
            }
        }

        return listQuestion.substring(1);
    }

    private class AddQuestionLevelViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mLevelTextView;
        private TextView mQuestionTextView;
        private int mPosition;

        AddQuestionLevelViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.add_list_question_level_list_item, parent, false));

            mLevelTextView = itemView.findViewById(R.id.add_list_question_level_level_text_view);
            mQuestionTextView = itemView.findViewById(R.id.add_list_question_level_list_item_text_view);
            ImageButton editLevelButton = itemView.findViewById(R.id.add_list_question_level_view_level_button);
            ImageButton deleteLevelButton = itemView.findViewById(R.id.add_list_question_level_delete_level_button);
            editLevelButton.setOnClickListener(this);
            deleteLevelButton.setOnClickListener(this);
        }

        void bind(int position) {
            mLevelTextView.setText(String.valueOf(position + 1));
            String questionText = mQuestionLevelDisplayArrayList.get(position);
            if (questionText.contains("#")) {
                questionText = questionText.substring(0, questionText.indexOf('#'));
            }
            if (questionText.length() > 20) {
                questionText = questionText.substring(0, 20);
            }
            mQuestionTextView.setText(questionText);
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.add_list_question_level_view_level_button) {
                Intent intent = new Intent(AddListQuestionLevelActivity.this, EditListQuestionLevelActivity.class);
                intent.putExtra(QUESTION_STRING, mQuestionLevelIdArrayList.get(mPosition));
                intent.putExtra(QUESTION_POSITION, mPosition);
                startActivityForResult(intent, REQUEST_CODE);
            }
            else if (view.getId() == R.id.add_list_question_level_delete_level_button) {
                AlertDialog dialog = new AlertDialog.Builder(getApplicationContext())
                        .setMessage("Are you sure you want to delete question level " + mPosition + "?")
                        .setCancelable(false)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mQuestionLevelIdArrayList.remove(mPosition);
                                mAddQuestionLevelAdapter.notifyDataSetChanged();
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int position = data.getIntExtra(QUESTION_POSITION, -1);
                if (position !=  -1) {
                    mQuestionLevelIdArrayList.set(position, data.getStringExtra(QUESTION_STRING));
                    mQuestionLevelDisplayArrayList.set(
                            position, data.getStringExtra(QUESTION_LEVEL_DISPLAY_TEXT));
                    mAddQuestionLevelAdapter.notifyDataSetChanged();
                }
            }
            else {
                mQuestionLevelIdArrayList.remove(mQuestionLevelIdArrayList.size() - 1);
                mQuestionLevelDisplayArrayList.remove(mQuestionLevelDisplayArrayList.size() - 1);
            }
        }
    }

    private class AddQuestionLevelAdapter extends RecyclerView.Adapter<AddQuestionLevelViewHolder> {

        private Context mContext;

        AddQuestionLevelAdapter(Context context) {
            mContext = context;
        }

        @Override
        public AddQuestionLevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AddQuestionLevelViewHolder(LayoutInflater.from(mContext), parent);
        }

        @Override
        public void onBindViewHolder(AddQuestionLevelViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mQuestionLevelIdArrayList.size();
        }
    }
}
