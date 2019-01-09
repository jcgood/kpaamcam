package edu.buffalo.cse.ubcollecting.ui;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.LANGUAGE_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_LANG_VERSION_TABLE;
import static edu.buffalo.cse.ubcollecting.data.tables.LanguageTable.ENGLISH_LANG_NAME;

/**
 * Activity for adding questions to a Questionnaire
 */
public class AddQuestionsActivity extends AppCompatActivity {

    private static final String TAG = AddQuestionsActivity.class.getSimpleName();
    private static final String EXTRA_QUESTIONNAIRE_ID = "edu.buffalo.cse.ubcollecting.ui.questionnaire_id";
    public static final String EXTRA_QUESTIONNAIRE_CONTENT = "edu.buffalo.cse.ubcollecting.ui.questionnaire_content";

    private ArrayList<QuestionnaireContent> selections;
    private HashSet<QuestionnaireContent> selectionsSet;
    private ArrayList<Language> selectedLanguages;
    private String questionnaireId;

    private RecyclerView entryRecyclerView;
    private QuestionLangAdapter entryAdapter;
    private ListView filterList;
    private EditText searchText;
    private ImageButton clearSearchButton;
    private ImageButton searchButton;
    private Button doneButton;


    /**
     * Returns {@link Intent} to start this Activity from a {@link edu.buffalo.cse.ubcollecting.QuestionnaireActivity}
     * @param packageContext Context to start this Intent from
     * @param questionnaire {@link Questionnaire} to add Questions to
     * @return {@link Intent} to add questions to a {@link Questionnaire}
     */
    public static Intent newIntent(Context packageContext,
                                   Questionnaire questionnaire,
                                   ArrayList<QuestionnaireContent> selectedContent) {
        Intent i = new Intent(packageContext, AddQuestionsActivity.class);
        i.putExtra(EXTRA_QUESTIONNAIRE_ID, questionnaire.getId());
        i.putExtra(EXTRA_QUESTIONNAIRE_CONTENT, selectedContent);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);

        questionnaireId = getIntent().getExtras().getString(EXTRA_QUESTIONNAIRE_ID);

        selections = (ArrayList<QuestionnaireContent>) getIntent().getExtras().getSerializable(EXTRA_QUESTIONNAIRE_CONTENT);
        selectionsSet = new HashSet<>();
        selectionsSet.addAll(selections);
        Log.i(TAG, "Set size: " + Integer.toString(selectionsSet.size()));
        selectedLanguages = LANGUAGE_TABLE.getAll();

        searchText = findViewById(R.id.table_select_search_view);
        searchText.addTextChangedListener(new SearchTextWatcher());


        clearSearchButton = findViewById(R.id.table_select_clear_button);
        clearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
                resetQueriedEntries();
                clearSearchButton.setVisibility(View.GONE);
            }
        });

        searchButton = findViewById(R.id.table_select_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchText.getText().toString();
                ArrayList<QuestionLangVersion> queriedQuestions = queryQuestionsForLanguages(selectedLanguages);
                queriedQuestions = search(query,queriedQuestions);
                entryAdapter.setEntryList(queriedQuestions);
                entryAdapter.notifyDataSetChanged();
            }
        });

        doneButton = findViewById(R.id.table_select_done_button);
        doneButton.setOnClickListener(new DoneOnClickListener());

        entryRecyclerView = findViewById(R.id.table_select_recycler);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        entryAdapter = new QuestionLangAdapter();
        entryRecyclerView.setAdapter(entryAdapter);

        filterList = findViewById(R.id.table_select_filter_list);
        filterList.setAdapter(new LanguageListAdapter(AddQuestionsActivity.this, LANGUAGE_TABLE.getAll()));
    }

    /**
     * Returns an {@link ArrayList} of {@link QuestionLangVersion} whose content matches the query
     *
     * @param query Term(s) to be queried for
     * @param selections Currently selected questions
     * @return {@link ArrayList} of queried questions
     */
    private ArrayList<QuestionLangVersion> search(String query, List<QuestionLangVersion> selections) {
        ArrayList<QuestionLangVersion> queryResult = new ArrayList<>();

        for (QuestionLangVersion questionLangVersion : selections) {
            if (questionLangVersion.getQuestionText().matches(".*" + query + ".*")) {
                queryResult.add(questionLangVersion);
            }
        }
        return queryResult;
    }

    private ArrayList<QuestionLangVersion> queryQuestionsForLanguages(ArrayList<Language> languages) {
        ArrayList<QuestionLangVersion> queryResult = new ArrayList<>();
        HashSet<String> addedQuestionId = new HashSet<>();

        for (QuestionLangVersion questionLangVersion : QUESTION_LANG_VERSION_TABLE.getAll()) {
            if (addedQuestionId.contains(questionLangVersion.getQuestionId())) {
                continue;
            }
            for (Language lang : languages) {
                if (questionLangVersion.getQuestionLanguageId().equals(lang.getId())) {
                    queryResult.add(questionLangVersion);
                    addedQuestionId.add(questionLangVersion.getQuestionId());
                    break;
                }
            }
        }
        return queryResult;
    }

    private void resetQueriedEntries() {
        ArrayList<QuestionLangVersion> langQuestions = queryQuestionsForLanguages(selectedLanguages);
        entryAdapter.getEntryList().clear();
        entryAdapter.getEntryList().addAll(langQuestions);
        entryAdapter.notifyDataSetChanged();
    }

    /**
     * {@link android.view.View.OnClickListener} to pass an {@link ArrayList} of
     * the selected questions back to the parent Activity.
     */
    private class DoneOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ArrayList<QuestionnaireContent> contentList = onSelectionDone(selections);
            Intent data = new Intent();
            data.putExtra(EXTRA_QUESTIONNAIRE_CONTENT, contentList);
            setResult(RESULT_OK, data);
            finish();
        }

        private ArrayList<QuestionnaireContent> onSelectionDone(ArrayList<QuestionnaireContent> selections) {
            for (int i = 0; i < selections.size(); i++) {
                selections.get(i).setQuestionOrder(i + 1);
            }
            Log.i(TAG, "SELECTIONS: " + Integer.toString(selections.size()));
            return selections;
        }
    }

    /**
     * Holds objects relating to individual question views
     */
    private class QuestionLangHolder extends RecyclerView.ViewHolder {

        private QuestionnaireContent question;
        private CheckBox selectBox;
        private TextView entryNameView;
        private CompoundButton.OnCheckedChangeListener selectBoxListener;


        public QuestionLangHolder(View view) {
            super(view);

            selectBox = view.findViewById(R.id.entry_list_select_box);
            entryNameView = view.findViewById(R.id.entry_list_select_text_view);

            selectBoxListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b && !selectionsSet.contains(question)) {
                        selections.add(question);
                        selectionsSet.add(question);
                    } else {
                        selections.remove(question);
                        selectionsSet.remove(question);
                    }
                }
            };
        }

        public void bindEntry(QuestionLangVersion question1) {
            selectBox.setOnCheckedChangeListener(null);

            QuestionnaireContent content = new QuestionnaireContent();
            content.setQuestionId(question1.getQuestionId());
            content.setQuestionnaireId(questionnaireId);
            question = content;

            entryNameView.setText(question1.getTextSummary());

            selectBox.setChecked(selections.contains(question));
            selectBox.setOnCheckedChangeListener(selectBoxListener);
        }
    }

    /**
     * {@link RecyclerView.Adapter} to hold list of all questions and corresponding
     * {@link QuestionLangHolder}
     */
    private class QuestionLangAdapter extends RecyclerView.Adapter<QuestionLangHolder> {

        private List<QuestionLangVersion> entryList;

        public QuestionLangAdapter() {
            List<QuestionLangVersion> entryList = QUESTION_LANG_VERSION_TABLE.getAll();
            this.entryList = new ArrayList<>();
            for (QuestionLangVersion questionLangVersion : entryList) {
                Language lang = LANGUAGE_TABLE.findById(questionLangVersion.getQuestionLanguageId());
                if (lang.getName().equals(ENGLISH_LANG_NAME)) {
                    this.entryList.add(questionLangVersion);
                }
            }
        }

        public List<QuestionLangVersion> getEntryList() {
            return entryList;
        }

        public void setEntryList(List<QuestionLangVersion> entryList) {
            this.entryList = entryList;
        }

        @Override
        public QuestionLangHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.entry_list_item_select, parent, false);
            return new QuestionLangHolder(view);
        }

        @Override
        public void onBindViewHolder(QuestionLangHolder holder, int position) {
            QuestionLangVersion entry = entryList.get(position);
            holder.bindEntry(entry);
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }
    }

    /**
     * {@link ArrayAdapter} for list of available question languages
     */
    private class LanguageListAdapter extends ArrayAdapter<Language> {

        private LanguageListAdapter(Context context, ArrayList<Language> languages) {
            super(context, 0, languages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Language lang = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_list_item_select, parent, false);
            }
            TextView nameView = convertView.findViewById(R.id.entry_list_select_text_view);
            CheckBox checkBox = convertView.findViewById(R.id.entry_list_select_box);

            nameView.setText(lang.getIdentifier());
            checkBox.setChecked(selectedLanguages.contains(lang));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean selected) {
                    searchText.setText("");
                    if (selected) {
                        selectedLanguages.add(lang);
                        resetQueriedEntries();
                    } else {
                        selectedLanguages.remove(lang);
                        ArrayList<QuestionLangVersion> langQuestions = queryQuestionsForLanguages(selectedLanguages);
                        resetQueriedEntries();
                    }
                }
            });

            return convertView;
        }
    }

    /**
     * {@link TextWatcher} to make clear button visible only when the search box has text in it
     */
    private class SearchTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() != 0) {
                clearSearchButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
}
