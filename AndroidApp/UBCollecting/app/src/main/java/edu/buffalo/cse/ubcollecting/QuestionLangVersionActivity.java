package edu.buffalo.cse.ubcollecting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_LANG_VERSION_TABLE;

/**
 * UNUSEDT THUS FAR
 */

public class QuestionLangVersionActivity extends AppCompatActivity {

    private static final String TAG = QuestionLangVersionActivity.class.getSimpleName().toString();

    private TextView questionField;
    private EditText langField;
    private EditText questionTextField;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_lang_version);

        questionField = this.findViewById(R.id.question_lang_version_question_field);
        langField = this.findViewById(R.id.question_lang_field);
        questionTextField = this.findViewById(R.id.question_lang_version_text_field);
        submitButton = this.findViewById(R.id.question_lang_version_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionLangVersion questionLangVersion = new QuestionLangVersion();
                questionLangVersion.setQuestionLanguageId(""); // TODO
                questionLangVersion.setQuestionId(""); // TODO
                questionLangVersion.setQuestionText(questionTextField.getText().toString());

                QUESTION_LANG_VERSION_TABLE.insert(questionLangVersion);
            }
        });
    }
}