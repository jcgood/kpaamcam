package edu.buffalo.cse.ubcollecting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE;

/**
 * UNUSED THUS FAR
 */

public class QuestionnaireContentActivity extends AppCompatActivity {

    private static final String TAG = QuestionnaireContentActivity.class.getSimpleName().toString();

    private TextView questionnaireField;
    private TextView questionField;
    private EditText orderField;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_content);

        questionnaireField = this.findViewById(R.id.questionnaire_content_questionnaire_field);
        questionField = this.findViewById(R.id.questionnaire_content_question_field);
        orderField = this.findViewById(R.id.questionnaire_content_order_field);
        submitButton = this.findViewById(R.id.questionnaire_content_submit_button);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionnaireContent content = new QuestionnaireContent();
                content.setQuestionnaireId(""); // TODO
                content.setQuestionId(""); // TODO
                content.setQuestionOrder(Integer.valueOf(orderField.getText().toString()));

                QUESTIONNAIRE_CONTENT_TABLE.insert(content);
            }
        });
    }
}
