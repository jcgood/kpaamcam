package edu.buffalo.cse.ubcollecting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;

import edu.buffalo.cse.ubcollecting.app.App;
import edu.buffalo.cse.ubcollecting.data.FireBaseCloudHelper;
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
    private final FireBaseCloudHelper fireBaseCloudHelper = new FireBaseCloudHelper(App.getContext());

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

                /* INSERT */
                try {
                    fireBaseCloudHelper.insert(QUESTION_LANG_VERSION_TABLE, questionLangVersion);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    Log.i(TAG, "Could not access server database (Firebase)");
                    e.printStackTrace();
                }
                QUESTION_LANG_VERSION_TABLE.insert(questionLangVersion);
            }
        });
    }
}