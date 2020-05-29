package edu.buffalo.cse.ubcollecting.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.Question;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionOption;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;
import edu.buffalo.cse.ubcollecting.utils.Constants;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_PROPERTY_TABLE;
import static edu.buffalo.cse.ubcollecting.data.tables.Table.EXTRA_MODEL;
import static edu.buffalo.cse.ubcollecting.ui.CreateQuestionActivity.LOOP_QUESTION_EXTRA;
import static edu.buffalo.cse.ubcollecting.utils.Constants.LOOP;


/**
 * Activity for updating existing Questions
 */
public class UpdateQuestionActivity extends AppCompatActivity {

    private ListView questionPropertiesListView;
    //private QuestionPropertyAdapter questionPropertyAdapter;
    private QuestionPropertyDef chosenQuestionProperty;
    private ListView questionLanguagesListView;
    private QuestionLanguageAdapter questionLanguageAdapter;
    private HashMap<Language, QuestionLangVersion> originalQuestionTexts;
    private HashMap<Language, EditText> newQuestionTexts;
    private Button update;
    private Question question;
    private ArrayList<QuestionOption> prevQuestionOptionsList;
    private ArrayList<EditText> allQuestionOptionsList;
    private TextView selectQuestionProperties;
    private TextView selectQuestionLanguages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_questions);

        question = (Question) getIntent().getSerializableExtra(EXTRA_MODEL);

        originalQuestionTexts = DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(question.getId());

        newQuestionTexts = new HashMap<>();

        questionLanguagesListView = findViewById(R.id.question_languages_list_view);

        chosenQuestionProperty = QUESTION_PROPERTY_TABLE.getQuestionProperty(question.getId());


        final CheckBox propertySelect = findViewById(R.id.chosen_question_property_checkbox);
        propertySelect.setText(chosenQuestionProperty.getName());
        propertySelect.setTextSize(22);
        propertySelect.setChecked(true);
        propertySelect.setEnabled(false);

        ArrayList<Language> quesLangs = DatabaseHelper.LANGUAGE_TABLE.getAll();

        questionLanguageAdapter = new QuestionLanguageAdapter(this, quesLangs);
        questionLanguagesListView.setAdapter(questionLanguageAdapter);

        selectQuestionProperties = findViewById(R.id.select_question_properties);

        selectQuestionLanguages = findViewById(R.id.select_question_languages);

        update = findViewById(R.id.update_question_button);

        if (question.getType() != null && question.getType().equals(LOOP)) {
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
                        if (!(question.getType() != null && question.getType().equals(LOOP))) {
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
                        if (chosenQuestionProperty.getName().equals(Constants.LIST)) {
                            double version = prevQuestionOptionsList.get(0).getVersion();
                            for (EditText newOptions : allQuestionOptionsList) {

                            }

                        }
                    }

                    for (Language lang : originalQuestionTexts.keySet()) {
                        if (!newQuestionTexts.containsKey(lang)) {
                            DatabaseHelper.QUESTION_LANG_VERSION_TABLE.delete(originalQuestionTexts.get(lang).getId());
                        }
                    }

                    // Starts the AddLoopQuestionLevelActivity in order to properly edit the contents
                    // of the question itself. The name, however, will changed in this file
                    if (question.getType() != null && question.getType().equals(LOOP)) {
                        Intent intent = new Intent(
                                UpdateQuestionActivity.this, AddLoopQuestionLevelActivity.class);
                        intent.putExtra(LOOP_QUESTION_EXTRA, question);
                        startActivity(intent);
                    }

                    finish();
                }
            }
        });
    }

    private class QuestionLanguageAdapter extends ArrayAdapter<Language> {
        public QuestionLanguageAdapter(Context context, ArrayList<Language> quesLanguages) {
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

            final ImageButton buttonAdd = new ImageButton(getApplicationContext());
            buttonAdd.setMinimumHeight(50);
            buttonAdd.setMinimumWidth(50);
            buttonAdd.setRight(10);
            buttonAdd.setImageResource(R.drawable.ic_add_black_24dp);
            buttonAdd.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText editText = new EditText(getApplicationContext());
                    editText.setTextColor(Color.BLACK);
                    editText.setHint(R.string.type_question_option_hint);
                    editText.setHintTextColor(Color.GRAY);
                    linearView.addView(editText);
                    allQuestionOptionsList.add(editText);
                }
            });

            final CheckBox languageSelect = convertView.findViewById(R.id.entry_list_select_box);

            // Disable any other languages with Loop Questions
            if (question.getType() != null && question.getType().equals(LOOP)) {
                languageSelect.setEnabled(false);
            }

            if (originalQuestionTexts.containsKey(language)) {
                languageSelect.setChecked(true);
                languageSelect.setEnabled(false);
                if (question.getType() != null && question.getType().equals(LOOP)) {
                    questionText.setText(question.getDisplayText());
                }
                else {
                    questionText.setText(originalQuestionTexts.get(language).getQuestionText());
                }

                questionText.setTextColor(Color.BLACK);
                linearView.addView(questionText,params);
                newQuestionTexts.put(language,questionText);
            }

            languageSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        if (chosenQuestionProperty.getName().equals(Constants.LIST)) {
                            prevQuestionOptionsList = DatabaseHelper.QUESTION_OPTION_TABLE.getQuestionOptions(originalQuestionTexts.get(language).getQuestionId());
                            for (QuestionOption options : prevQuestionOptionsList) {
                                EditText editText = new EditText(getApplicationContext());
                                editText.setTextColor(Color.BLACK);
                                editText.setText(options.getOptionText());
                                allQuestionOptionsList.add(editText);
                                linearView.addView(editText);
                            }
                            linearView.addView(buttonAdd);
                        }
                        linearView.addView(questionText, params);
                        newQuestionTexts.put(language, questionText);
                    } else {
                        linearView.removeView(questionText);
                        newQuestionTexts.remove(language);
                    }
                }
            });

            TextView languageName = convertView.findViewById(R.id.entry_list_select_text_view);

            languageName.setText(language.getName());

            return convertView;
        }
    }

    /**
     * Helper function that validates user submission
     *
     * @return {@link Boolean}
     */

    private boolean validateEntry() {

        if (newQuestionTexts.isEmpty()) {
            selectQuestionLanguages.setError("You must select at least one language for the question text");
            Toast.makeText(this, "At least one question property and one language must be selected", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            for (EditText text : newQuestionTexts.values()) {
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