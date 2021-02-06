package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;

import static edu.buffalo.cse.ubcollecting.AnswerActivity.getAnswer;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectQuestionnaireActivity.SELECTED_QUESTIONNAIRE;

public class UpdateAnswerActivity extends AppCompatActivity {
    private EditText newAnswer;
    private Button updateButton;
    private Answer answer;

    public static final String SELECTED_QUESTION = "selected question";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_answer);
        answer = getAnswer(getIntent());
        newAnswer = (EditText) findViewById(R.id.update_answer_field);
        newAnswer.setText(answer.getText());
        updateButton = (Button) findViewById(R.id.update_answer_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answerText = newAnswer.getText().toString();
                answer.setText(answerText);
                DatabaseHelper.ANSWER_TABLE.update(answer);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(SELECTED_QUESTION , answer.getQuestionId());
                resultIntent.putExtra(SELECTED_QUESTIONNAIRE, answer.getQuestionnaireId());
                setResult(1, resultIntent);
                finish();
            }
        });

    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, UpdateAnswerActivity.class);
        return i;
    }


}
