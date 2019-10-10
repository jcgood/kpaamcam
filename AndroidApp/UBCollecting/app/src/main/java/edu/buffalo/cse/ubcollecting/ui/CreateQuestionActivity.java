package edu.buffalo.cse.ubcollecting.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.Question;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionProperty;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireType;

/**
 * Activity for creating a Question.
 */

public class CreateQuestionActivity extends AppCompatActivity {

    private ListView questionLanguagesListView;
    private QuestionLanguageAdapter questionLanguageAdapter;
    private HashMap<Language, EditText> questionTexts;
    private Button submit;
    private Question question;
    private TextView selectQuestionProperties;
    private TextView selectQuestionLanguages;
    private Spinner propertySpinner;
    private ArrayAdapter<QuestionPropertyDef> propertyAdapter;


    private boolean checkSelected=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkSelected=false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_questions);

        question = new Question();

        questionLanguagesListView = findViewById(R.id.question_languages_list_view);
        selectQuestionProperties = findViewById(R.id.select_question_properties);
        selectQuestionLanguages = findViewById(R.id.select_question_languages);

        ArrayList<QuestionPropertyDef> quesPropDefs = DatabaseHelper.QUESTION_PROPERTY_DEF_TABLE.getAll();

        propertySpinner =  findViewById(R.id.question_property_spinner);
        propertyAdapter = new ArrayAdapter<QuestionPropertyDef>(this,
                android.R.layout.simple_spinner_item,
                quesPropDefs);
        propertySpinner.setAdapter(propertyAdapter);
        propertySpinner.setSelected(false);
        propertySpinner.setOnItemSelectedListener(new EntryOnItemSelectedListener<QuestionnaireType>());


        ArrayList<Language> quesLangs = DatabaseHelper.LANGUAGE_TABLE.getAll();
        questionLanguageAdapter = new QuestionLanguageAdapter(this, quesLangs);
        questionLanguagesListView.setAdapter(questionLanguageAdapter);
//        UiUtils.setListViewHeightBasedOnItems(questionPropertiesListView);
        UiUtils.setListViewHeightBasedOnItems(questionLanguagesListView);

        questionTexts = new HashMap<>();

        submit = findViewById(R.id.create_question_submit_button);
        submit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if (validateEntry()) {

                    DatabaseHelper.QUESTION_TABLE.insert(question);

                    QuestionPropertyDef propertyDef = (QuestionPropertyDef) propertySpinner.getSelectedItem();
                    QuestionProperty quesProp = new QuestionProperty();
                    quesProp.setQuestionId(question.getId());
                    quesProp.setPropertyId(propertyDef.getId());
                    DatabaseHelper.QUESTION_PROPERTY_TABLE.insert(quesProp);

                    for (Language lang : questionTexts.keySet()) {
                        if (lang.getName().equals("English")) {
                            question.setDisplayText(questionTexts.get(lang).getText().toString());
                            DatabaseHelper.QUESTION_TABLE.update(question);
                        }
                        QuestionLangVersion quesLang = new QuestionLangVersion();
                        quesLang.setQuestionId(question.getId());
                        quesLang.setQuestionLanguageId(lang.getId());
                        quesLang.setQuestionText(questionTexts.get(lang).getText().toString());
                        DatabaseHelper.QUESTION_LANG_VERSION_TABLE.insert(quesLang);
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

            final LinearLayout.LayoutParams listViewParams = (LinearLayout.LayoutParams) questionLanguagesListView.getLayoutParams();

            final EditText questionText = new EditText(getApplicationContext());
            questionText.setTextColor(Color.BLACK);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final LinearLayout linearView = convertView.findViewById(R.id.entry_list_outer_linear_layout);

            final CheckBox languageSelect = (CheckBox) convertView.findViewById(R.id.entry_list_select_box);

            languageSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        linearView.addView(questionText, params);
//                        listViewParams.height+=100;
//                        questionLanguagesListView.setLayoutParams(listViewParams);
                        questionTexts.put(language, questionText);
                    } else {
                        linearView.removeView(questionText);
//                        listViewParams.height-=100;
//                        questionLanguagesListView.setLayoutParams(listViewParams);
                        questionTexts.remove(language);
                    }
                }
            });

            TextView languageName = (TextView) convertView.findViewById(R.id.entry_list_select_text_view);

            languageName.setText(language.getName());

            return convertView;

        }
    }

    /**
     * Helper function that validates user su//bmission
     * @return {@link Boolean}
     */

    private boolean validateEntry() {

        if (questionTexts.isEmpty()) {
            selectQuestionLanguages.setError("You must select at least one language for the question text");
            Toast.makeText(this, "Please Fill in All Required Fields", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            for (EditText text: questionTexts.values()) {
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
