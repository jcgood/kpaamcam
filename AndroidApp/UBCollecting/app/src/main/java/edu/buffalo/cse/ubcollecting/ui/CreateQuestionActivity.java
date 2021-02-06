package edu.buffalo.cse.ubcollecting.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
//import androidx.constraintlayout.ConstraintLayout;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.Model;
import edu.buffalo.cse.ubcollecting.data.models.Question;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionOption;
import edu.buffalo.cse.ubcollecting.data.models.QuestionProperty;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireType;
import edu.buffalo.cse.ubcollecting.utils.Constants;

import static edu.buffalo.cse.ubcollecting.utils.Constants.LOOP;

/**
 * Activity for creating a Question.
 */

public class CreateQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOOP_QUESTION_EXTRA = "loop_question_extra";

    private ListView questionLanguagesListView;
    private QuestionLanguageAdapter questionLanguageAdapter;
    private HashMap<Language, EditText> questionTexts;
    private Button submit;
    private Button mAddLoopQuestionLevel;
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
        propertySpinner.setOnItemSelectedListener(new OnItemSelectedListener<QuestionnaireType>());

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
                    questionPropertyDef = (QuestionPropertyDef) propertySpinner.getSelectedItem();

                    if (checkIsLoopQuestionAndOneLanguageSelected()) {
                        insertInformationIntoDataBases(questionPropertyDef, question, questionTexts, allListOptions);
                        finish();
                    }
                }
            }
        });

        mAddLoopQuestionLevel = findViewById(R.id.create_question_add_list_level_button);
        mAddLoopQuestionLevel.setOnClickListener(this);
    }

    // If a Question is selected to be a Loop Question then this will allow you to
    // create Loop Question Levels
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.create_question_add_list_level_button) {
            if (validateEntry()) {
                questionPropertyDef = (QuestionPropertyDef) propertySpinner.getSelectedItem();

                if (checkIsLoopQuestionAndOneLanguageSelected()) {
                    insertInformationIntoDataBases(questionPropertyDef, question, questionTexts, allListOptions);

                    Intent intent = new Intent(CreateQuestionActivity.this, AddLoopQuestionLevelActivity.class);
                    intent.putExtra(LOOP_QUESTION_EXTRA, question);
                    startActivity(intent);

                    finish();
                }
            }
        }
    }

    // Inserts the information related to question into the appropriate places
    // Used for storing both Loop Questions and non Loop Questions
    private static void insertInformationIntoDataBases(
            QuestionPropertyDef propertyDef,
            Question question,
            HashMap<Language, EditText> questionTexts,
            List<EditText> listOptions) {

        question.setType(propertyDef.getName());
        DatabaseHelper.QUESTION_TABLE.insert(question);

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

            if (propertyDef.getName().equals(Constants.LIST)) {
                for (EditText options : listOptions) {
                    String preDefinedAnswer = options.getText().toString();

                    QuestionOption questionOption = new QuestionOption();
                    questionOption.setQuestionId(question.getId());
                    questionOption.setQuestionLanguageId(lang.getId());
                    questionOption.setOptionText(preDefinedAnswer);
                    DatabaseHelper.QUESTION_OPTION_TABLE.insert(questionOption);
                }
            }
        }
    }

    // Checks to see if the Question is a Loop Question, and if so
    // it checks to see if only one language is selected. Then you may edit
    // the Question levels
    private boolean checkIsLoopQuestionAndOneLanguageSelected() {
        if (questionPropertyDef.getName().equals(LOOP)) {
            boolean languageChecked = false;
            for (int i = 0; i < questionLanguagesListView.getChildCount(); i++) {
                View childView = questionLanguagesListView.getChildAt(i);
                if (((CheckBox) childView.findViewById(R.id.entry_list_select_box)).isChecked()) {
                    if (!languageChecked) {
                        languageChecked = true;
                        continue;
                    }
                    Toast.makeText(
                            this,
                            "For Loop Question please select only one language",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
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

            final ConstraintLayout.LayoutParams listViewParams = (ConstraintLayout.LayoutParams) questionLanguagesListView.getLayoutParams();

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

    // Custom listener for enabling and disabling the add question level button
    private class OnItemSelectedListener<E extends Model> implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            E questionnaireType = (E) adapterView.getItemAtPosition(position);
            TextView listView = view.findViewById(android.R.id.text1);
            listView.setText(questionnaireType.getIdentifier());
            if (questionnaireType.getIdentifier().equals(LOOP)) {
                mAddLoopQuestionLevel.setEnabled(true);
            } else {
                mAddLoopQuestionLevel.setEnabled(false);
            }

            propertySpinner.setSelection(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { }
    }
}
