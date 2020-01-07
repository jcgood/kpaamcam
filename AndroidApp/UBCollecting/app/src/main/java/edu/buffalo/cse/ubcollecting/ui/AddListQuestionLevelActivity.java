package edu.buffalo.cse.ubcollecting.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Question;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionProperty;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;

import static edu.buffalo.cse.ubcollecting.ui.CreateQuestionActivity.LIST_QUESTION_ANSWER;
import static edu.buffalo.cse.ubcollecting.ui.CreateQuestionActivity.LIST_QUESTION_LANGUAGE;
import static edu.buffalo.cse.ubcollecting.ui.CreateQuestionActivity.LIST_QUESTION_LANGUAGE_ID;

public class AddListQuestionLevelActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mLanguageTextView;
    private Button mAddQuestionLevelButton;
    private Button mSubmitButton;
    private AddQuestionLevelAdapter mAddQuestionLevelAdapter;
    private RecyclerView mAddQuestionLevelRecyclerView;
    private ArrayList<String> mQuestionLevelArrayList;
    private Question mQuestion;
    private String mLanguageId;
    private String mLanguageName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_list_question_level_activity);

        mQuestion = new Question();
        DatabaseHelper.QUESTION_TABLE.insert(mQuestion);

        for (QuestionPropertyDef propertyDef : DatabaseHelper.QUESTION_PROPERTY_DEF_TABLE.getAll()) {
            if (propertyDef.getName().equals("List")) {
                QuestionProperty quesProp = new QuestionProperty();
                quesProp.setQuestionId(mQuestion.getId());
                quesProp.setPropertyId(propertyDef.getId());
                DatabaseHelper.QUESTION_PROPERTY_TABLE.insert(quesProp);
            }
        }

        mQuestionLevelArrayList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mLanguageName = intent.getStringExtra(LIST_QUESTION_LANGUAGE);
            mLanguageId = intent.getStringExtra(LIST_QUESTION_LANGUAGE_ID);
            mQuestionLevelArrayList.add(0, intent.getStringExtra(LIST_QUESTION_ANSWER));
        }

        if (mQuestionLevelArrayList.size() == 0) {
            mQuestionLevelArrayList.add(0, "");
        }

        initializeViewVariables();
        setListeners();
        mLanguageTextView.setText(mLanguageName);

        mAddQuestionLevelAdapter = new AddQuestionLevelAdapter(this);
        mAddQuestionLevelRecyclerView = findViewById(R.id.add_question_level_recycler_view);
        mAddQuestionLevelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAddQuestionLevelRecyclerView.setAdapter(mAddQuestionLevelAdapter);
    }

    private void initializeViewVariables() {
        mLanguageTextView = findViewById(R.id.add_question_level_language_text_view);
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
            mQuestionLevelArrayList.add(mQuestionLevelArrayList.size(), "");
            mAddQuestionLevelAdapter.notifyItemChanged(mQuestionLevelArrayList.size());
        }
        else if (view.getId() == R.id.add_question_level_submit_button) {
            boolean canSubmit = true;

            for (int i = 0; i < mAddQuestionLevelRecyclerView.getChildCount(); i++) {
                View childView = mAddQuestionLevelRecyclerView.getChildAt(i);
                EditText questionEditText =
                        childView.findViewById(R.id.add_list_question_level_list_item_edit_text);
                if (questionEditText.getText().length() < 5) {
                    questionEditText.setError("Question length must be greater than 5 characters");
                    canSubmit = false;
                }

                Button saveButton =
                        childView.findViewById(R.id.add_list_question_level_save_question);
                if (saveButton.getText().equals(getString(R.string.save)) && canSubmit) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please save all Question before proceeding",
                            Toast.LENGTH_SHORT).show();
                    canSubmit = false;
                }
            }

            if (!canSubmit) {
                return;
            }

            if (mLanguageName.equals("English")) {
                mQuestion.setDisplayText(mQuestionLevelArrayList.get(0));
                DatabaseHelper.QUESTION_TABLE.update(mQuestion);
            }
            QuestionLangVersion quesLang = new QuestionLangVersion();
            quesLang.setQuestionId(mQuestion.getId());
            quesLang.setQuestionLanguageId(mLanguageId);
            quesLang.setQuestionText(concatListQuestions());
            DatabaseHelper.QUESTION_LANG_VERSION_TABLE.insert(quesLang);
            Toast.makeText(this, "Your Question has been submitted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String concatListQuestions() {
        String listQuestion = "";
        for (String question : mQuestionLevelArrayList) {
            listQuestion = listQuestion.concat("|" + question);
        }

        return listQuestion.substring(1);
    }

    private class AddQuestionLevelViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, TextWatcher {

        private TextView mLevelTextView;
        private EditText mQuestionEditText;
        private Button mSaveQuestionButton;
        private int mPosition;

        AddQuestionLevelViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.add_list_question_level_list_item, parent, false));

            mLevelTextView = itemView.findViewById(R.id.add_list_question_level_list_item_text_view);
            mQuestionEditText = itemView.findViewById(R.id.add_list_question_level_list_item_edit_text);
            mQuestionEditText.addTextChangedListener(this);
            mSaveQuestionButton = itemView.findViewById(R.id.add_list_question_level_save_question);
            mSaveQuestionButton.setOnClickListener(this);
        }

        void bind(int position) {
            mLevelTextView.setText(TextUtils.concat("Question Level ", String.valueOf(position)));
            mQuestionEditText.setText(mQuestionLevelArrayList.get(position));
            mPosition = position;

            if (position == 0) {
                mSaveQuestionButton.setText(getString(R.string.saved));
                mSaveQuestionButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            }
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.add_list_question_level_save_question) {
                if (mQuestionEditText.getText().toString().length() < 5) {
                    mQuestionEditText.setError("Question length must be greater than 5 characters");
                    return;
                }
                mQuestionLevelArrayList.set(mPosition, mQuestionEditText.getText().toString());
                mSaveQuestionButton.setText(getString(R.string.saved));
                mSaveQuestionButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                Toast.makeText(getApplicationContext(), "Question saved to level " + mPosition, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!mSaveQuestionButton.getText().equals("Save")) {
                mSaveQuestionButton.setText(getString(R.string.save));
                mSaveQuestionButton.setBackground(getResources().getDrawable(android.R.drawable.btn_default));
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
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
            return mQuestionLevelArrayList.size();
        }
    }
}
