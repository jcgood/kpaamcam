package edu.buffalo.cse.ubcollecting;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;

import edu.buffalo.cse.ubcollecting.app.App;
import edu.buffalo.cse.ubcollecting.data.FireBaseCloudHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.ANSWER_TABLE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TextFragment.SELECTED_ANSWER;

/**
 * UNUSED THUS FAR
 */
public class AnswerActivity extends AppCompatActivity {

    private static final String TAG = AnswerActivity.class.getSimpleName().toString();

    private TextView questionnaireNameField;
    private TextView questionNameField;
    private EditText answerLabelField;
    private EditText answerTextField;
    private Button submitButton;
    private final FireBaseCloudHelper fireBaseCloudHelper = new FireBaseCloudHelper(App.getContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        questionnaireNameField = this.findViewById(R.id.answer_questionnaire_name_field);
        questionNameField = this.findViewById(R.id.answer_question_name_field);
        answerLabelField = this.findViewById(R.id.answer_label_field);
        answerTextField = this.findViewById(R.id.answer_text_field);
        submitButton = this.findViewById(R.id.answer_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Answer answer = new Answer();
                answer.setQuestionnaireId(""); // TODO
                answer.setQuestionId(""); // TODO
                answer.setLabel(answerLabelField.getText().toString());
                answer.setText(answerTextField.getText().toString());

                /* INSERT */
                try {
                    fireBaseCloudHelper.insert(ANSWER_TABLE, answer);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    Log.i(TAG, "Could not access server database (Firebase)");
                    e.printStackTrace();
                }
                ANSWER_TABLE.insert(answer);
            }
        });
    }

    public static Answer getAnswer(Intent intent){
        Answer answer = (Answer) intent.getSerializableExtra(SELECTED_ANSWER);
        return answer;
    }

    protected boolean validateEntry() {

        return true;

    }
}
