package edu.buffalo.cse.ubcollecting.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.Question;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionProperty;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_PROPERTY_TABLE;
import static edu.buffalo.cse.ubcollecting.data.tables.Table.EXTRA_MODEL;
import static edu.buffalo.cse.ubcollecting.ui.CreateQuestionActivity.LIST_QUESTION_EXTRA;


/**
 * Activity for updating existing Questions
 */
public class UpdateQuestionActivity extends AppCompatActivity {

    private ListView questionPropertiesListView;
    private QuestionPropertyAdapter questionPropertyAdapter;
    private QuestionPropertyDef chosenQuestionProperty;
    private ListView questionLanguagesListView;
    private QuestionLanguageAdapter questionLanguageAdapter;
    private HashMap<Language,QuestionLangVersion> originalQuestionTexts;
    private HashMap<Language,EditText> newQuestionTexts;
    private Button update;
    private Question question;
    private TextView selectQuestionProperties;
    private TextView selectQuestionLanguages;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_questions);

        question = (Question) getIntent().getSerializableExtra(EXTRA_MODEL);

        originalQuestionTexts = DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(question.getId());

        newQuestionTexts = new HashMap<>();

        questionPropertiesListView = findViewById(R.id.question_properties_list_view);

        questionLanguagesListView = findViewById(R.id.question_languages_list_view);

        chosenQuestionProperty = QUESTION_PROPERTY_TABLE.getQuestionProperty(question.getId());

        ArrayList<QuestionPropertyDef> quesPropDefs = DatabaseHelper.QUESTION_PROPERTY_DEF_TABLE.getAll();

        questionPropertyAdapter = new QuestionPropertyAdapter(this, quesPropDefs);

        questionPropertiesListView.setAdapter(questionPropertyAdapter);

        ArrayList<Language> quesLangs = DatabaseHelper.LANGUAGE_TABLE.getResearchLanguages();

        questionLanguageAdapter = new QuestionLanguageAdapter(this, quesLangs);

        questionLanguagesListView.setAdapter(questionLanguageAdapter);

        selectQuestionProperties = findViewById(R.id.select_question_properties);

        selectQuestionLanguages = findViewById(R.id.select_question_languages);

        UiUtils.setListViewHeightBasedOnItems(questionPropertiesListView);

        UiUtils.setListViewHeightBasedOnItems(questionLanguagesListView);

        update = findViewById(R.id.update_question_button);

        if (question.getType() != null && question.getType().equals("List")) {
            update.setText("Edit");
        }

        update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (validateEntry()) {

                    for (Language lang : newQuestionTexts.keySet()) {
                        if (lang.getName().equals("English")) {
                            question.setDisplayText(newQuestionTexts.get(lang).getText().toString());
                            DatabaseHelper.QUESTION_TABLE.update(question);
                        }
                        if (!(question.getType() != null && question.getType().equals("List"))) {
                            if (originalQuestionTexts.containsKey(lang)) {
                                QuestionLangVersion quesLang = originalQuestionTexts.get(lang);
                                quesLang.setQuestionText(newQuestionTexts.get(lang).getText().toString());
                                DatabaseHelper.QUESTION_LANG_VERSION_TABLE.update(quesLang);
                            } else {
                                QuestionLangVersion quesLang = new QuestionLangVersion();
                                quesLang.setQuestionId(question.getId());
                                quesLang.setQuestionLanguageId(lang.getId());
                                quesLang.setQuestionText(newQuestionTexts.get(lang).getText().toString());
                                DatabaseHelper.QUESTION_LANG_VERSION_TABLE.insert(quesLang);
                            }
                        }
                    }

                    for (Language lang : originalQuestionTexts.keySet()) {
                        if(!newQuestionTexts.containsKey(lang)){
                            DatabaseHelper.QUESTION_LANG_VERSION_TABLE.delete(originalQuestionTexts.get(lang).getId());
                        }
                    }

                    // Starts the AddListQuestionLevelActivity in order to properly edit the contents
                    // of the question itself. The name, however, will changed in this file
                    if (question.getType() != null && question.getType().equals("List")) {
                        Intent intent = new Intent(
                                UpdateQuestionActivity.this, AddListQuestionLevelActivity.class);
                        intent.putExtra(LIST_QUESTION_EXTRA, question);
                        startActivity(intent);
                    }

                    finish();
            }
        }
        });

    }

    private class QuestionPropertyAdapter extends ArrayAdapter<QuestionPropertyDef> {
        public QuestionPropertyAdapter(Context context, ArrayList<QuestionPropertyDef> quesPropDefs){
            super(context, 0, quesPropDefs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final QuestionPropertyDef quesPropDef = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_list_item_select, parent, false);
            }
            final CheckBox propertySelect = (CheckBox) convertView.findViewById(R.id.entry_list_select_box);

            if (chosenQuestionProperty.equals(quesPropDef)) {
                propertySelect.setChecked(true);
            }

            propertySelect.setEnabled(false);

            TextView propertyText = (TextView) convertView.findViewById(R.id.entry_list_select_text_view);

            propertyText.setText(quesPropDef.getName());

            return convertView;

        }
    }

    private class QuestionLanguageAdapter extends ArrayAdapter<Language> {
        public QuestionLanguageAdapter(Context context, ArrayList<Language> quesLanguages){
            super(context, 0, quesLanguages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Language language = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_list_item_select, parent, false);
            }

            final EditText questionText = new EditText(getApplicationContext());
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final LinearLayout linearView = convertView.findViewById(R.id.entry_list_outer_linear_layout);

            final CheckBox languageSelect = (CheckBox) convertView.findViewById(R.id.entry_list_select_box);

            // Disable any other languages with List Questions
            if (question.getType() != null && question.getType().equals("List")) {
                languageSelect.setEnabled(false);
            }

            if (originalQuestionTexts.containsKey(language)){
                languageSelect.setChecked(true);
                languageSelect.setEnabled(false);
                if (question.getType() != null && question.getType().equals("List")) {
                    questionText.setText(question.getDisplayText());
                }
                else {
                    questionText.setText(originalQuestionTexts.get(language).getQuestionText());
                }
                linearView.addView(questionText,params);
                newQuestionTexts.put(language,questionText);
            }

            languageSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked){
                        linearView.addView(questionText,params);
                        newQuestionTexts.put(language,questionText);
                    }
                    else{
                        linearView.removeView(questionText);
                        newQuestionTexts.remove(language);
                    }
                }
            });

            TextView languageName = (TextView) convertView.findViewById(R.id.entry_list_select_text_view);

            languageName.setText(language.getName());

            return convertView;
        }
    }


    /**
     * Helper function that validates user submission
     * @return {@link Boolean}
     */

    private boolean validateEntry() {

        if (newQuestionTexts.isEmpty()) {
            selectQuestionLanguages.setError("You must select at least one language for the question text");
            Toast.makeText(this, "At least one question property and one language must be selected", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            for (EditText text: newQuestionTexts.values()) {
                String questionText = text.getText().toString();
                if (questionText.trim().length() < 5) {
                    Toast.makeText(this, "Each Selected Question Text Must Have At Least 5 Characters", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }


}
