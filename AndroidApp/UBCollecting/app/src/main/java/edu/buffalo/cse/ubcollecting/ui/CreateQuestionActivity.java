package edu.buffalo.cse.ubcollecting.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.Question;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionOption;
import edu.buffalo.cse.ubcollecting.data.models.QuestionProperty;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireType;
import edu.buffalo.cse.ubcollecting.ui.interviewer.UserLandingActivity;
import edu.buffalo.cse.ubcollecting.utils.Constants;

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

    private QuestionPropertyDef questionPropertyDef;
    private List<EditText> allListOptions;
    private boolean checkSelected = false;
    private int listOptionsCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkSelected = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_questions);

        Toolbar toolbar = findViewById(R.id.create_question_toolbar);
        setSupportActionBar(toolbar);

        question = new Question();
        allListOptions = new ArrayList<EditText>();

        questionLanguagesListView = findViewById(R.id.question_languages_list_view);
        selectQuestionProperties = findViewById(R.id.select_question_properties);
        selectQuestionLanguages = findViewById(R.id.select_question_languages);

        final ArrayList<QuestionPropertyDef> quesPropDefs = DatabaseHelper.QUESTION_PROPERTY_DEF_TABLE.getAll();

        propertySpinner = findViewById(R.id.question_property_spinner);
        propertyAdapter = new ArrayAdapter<QuestionPropertyDef>(this,
                android.R.layout.simple_spinner_item,
                quesPropDefs);
        propertySpinner.setAdapter(propertyAdapter);
        propertySpinner.setSelected(false);
        propertySpinner.setOnItemSelectedListener(new EntryOnItemSelectedListener<QuestionnaireType>() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos).toString();
                propertySpinner.setSelection(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        ArrayList<Language> quesLangs = DatabaseHelper.LANGUAGE_TABLE.getAll();
        questionLanguageAdapter = new QuestionLanguageAdapter(this, quesLangs);
        questionLanguagesListView.setAdapter(questionLanguageAdapter);

        //HashMap of the language and the question text
        questionTexts = new HashMap<>();

        submit = findViewById(R.id.create_question_submit_button);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (validateEntry()) {

                    DatabaseHelper.QUESTION_TABLE.insert(question);

                    questionPropertyDef = (QuestionPropertyDef) propertySpinner.getSelectedItem();
                    QuestionProperty quesProp = new QuestionProperty();
                    quesProp.setQuestionId(question.getId());
                    quesProp.setPropertyId(questionPropertyDef.getId());
                    DatabaseHelper.QUESTION_PROPERTY_TABLE.insert(quesProp);


                    for (Language lang : questionTexts.keySet()) {
                        if (lang.getName().equals("English")) {
                            question.setDisplayText(questionTexts.get(lang).getText().toString());
                            DatabaseHelper.QUESTION_TABLE.update(question);
                        }
                        QuestionLangVersion quesLang = new QuestionLangVersion();
                        quesLang.setQuestionId(question.getId());
                        quesLang.setQuestionLanguageId(lang.getId());
                        quesLang.setQuestionText(question.getDisplayText());
                        DatabaseHelper.QUESTION_LANG_VERSION_TABLE.insert(quesLang);

                        if (questionPropertyDef.getName().equals(Constants.LIST)) {
                            for (EditText options : allListOptions) {
                                String preDefinedAnswer = options.getText().toString();

                                QuestionOption questionOption = new QuestionOption();
                                questionOption.setQuestionId(question.getId());
                                questionOption.setQuestionLanguageId(lang.getId());
                                questionOption.setOptionText(preDefinedAnswer);
                                DatabaseHelper.QUESTION_OPTION_TABLE.insert(questionOption);
                            }
                        }
                    }

                    finish();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_logout){
            Intent intent = new Intent(CreateQuestionActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return false;
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

            //Edit Text for entering the question for the selected lang
            final EditText questionText = new EditText(getApplicationContext());
            questionText.setTextColor(Color.BLACK);
            questionText.setHint(R.string.type_question_hint);
            questionText.setHintTextColor(Color.GRAY);

            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final LinearLayout linearView = convertView.findViewById(R.id.entry_list_outer_linear_layout);

            //Add button for adding predefined answer options
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
                    listOptionsCounter += 1;
                    editText.setTextColor(Color.BLACK);
                    editText.setHint("Type Option " + listOptionsCounter);
                    editText.setHintTextColor(Color.GRAY);
                    linearView.addView(editText);
                    allListOptions.add(editText);
                }
            });

            final CheckBox languageSelect = convertView.findViewById(R.id.entry_list_select_box);
            languageSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    questionPropertyDef = (QuestionPropertyDef) propertySpinner.getSelectedItem();
                    //final String questionType = propertyDef.getName();
                    question.setType(questionPropertyDef.getName());
                    if (isChecked) {
                        if (question.getType().equals(Constants.LIST)) {
                            buttonAdd.setVisibility(View.VISIBLE);
                            linearView.addView(questionText, params);
                            linearView.addView(buttonAdd);
                            questionTexts.put(language, questionText);
                        } else {
                            linearView.addView(questionText, params);
                            questionTexts.put(language, questionText);
                        }
                    } else {
                        linearView.removeView(questionText);
                        listViewParams.height -= 100;
                        questionTexts.remove(language);
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

        if (questionTexts.isEmpty()) {
            selectQuestionLanguages.setError("You must select at least one language for the question text");
            Toast.makeText(this, "Please Fill in All Required Fields", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            for (EditText text : questionTexts.values()) {
                String questionText = text.getText().toString();
                if (questionText.trim().length() < 5) {
                    Toast.makeText(this, "Each Selected Question Text Must Have At Least 5 Characters", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (question.getType().equals(Constants.LIST)) {
                    for (EditText options : allListOptions) {
                        if (null == options.getText() || options.getText().toString().isEmpty()) {
                            Toast.makeText(this, "Please Fill in options Fields", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
