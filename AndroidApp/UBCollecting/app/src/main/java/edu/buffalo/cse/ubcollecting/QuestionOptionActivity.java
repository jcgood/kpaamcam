package edu.buffalo.cse.ubcollecting;

import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;

import edu.buffalo.cse.ubcollecting.app.App;
import edu.buffalo.cse.ubcollecting.data.FireBaseCloudHelper;
import edu.buffalo.cse.ubcollecting.data.models.QuestionOption;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_OPTION_TABLE;

/**
 * UNUSED THUS FAR
 */

public class QuestionOptionActivity extends AppCompatActivity {

    private static final String TAG = QuestionOptionActivity.class.getSimpleName().toString();

    private TextView langField;
    private EditText optionTextField;
    private Button submitButton;
    private final FireBaseCloudHelper fireBaseCloudHelper = new FireBaseCloudHelper(App.getContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_option);

        langField = this.findViewById(R.id.question_option_lang_field);
        optionTextField = this.findViewById(R.id.question_option_text_field);
        submitButton = this.findViewById(R.id.question_option_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionOption option = new QuestionOption();
                option.setQuestionId(""); // TODO
                option.setQuestionLanguageId(""); // TODO
                option.setOptionText(optionTextField.getText().toString());

                /* INSERT */
                try {
                    fireBaseCloudHelper.insert(QUESTION_OPTION_TABLE, option);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    Log.i(TAG, "Could not access server database (Firebase)");
                    e.printStackTrace();
                }
                QUESTION_OPTION_TABLE.insert(option);
            }
        });
    }
}
