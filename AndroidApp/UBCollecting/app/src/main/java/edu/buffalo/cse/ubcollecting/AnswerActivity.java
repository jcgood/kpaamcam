package edu.buffalo.cse.ubcollecting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.buffalo.cse.ubcollecting.data.models.Answer;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.ANSWER_TABLE;

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

                ANSWER_TABLE.insert(answer);
            }
        });
    }

    protected boolean validateEntry() {

        return true;

    }
}
