package edu.buffalo.cse.ubcollecting;

import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;

import edu.buffalo.cse.ubcollecting.app.App;
import edu.buffalo.cse.ubcollecting.data.FireBaseCloudHelper;
import edu.buffalo.cse.ubcollecting.data.models.SessionAnswer;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.SESSION_ANSWER_TABLE;

public class SessionAnswerActivity extends AppCompatActivity {

    private static final String TAG = SessionAnswerActivity.class.getSimpleName().toString();

    private TextView questionnaireField;
    private TextView questionField;
    private TextView answerField;
    private Button submitButton;
    private final FireBaseCloudHelper fireBaseCloudHelper = new FireBaseCloudHelper(App.getContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_answer);

        questionnaireField = this.findViewById(R.id.session_answer_questionnaire_field);
        questionField = this.findViewById(R.id.session_answer_question_field);
        answerField = this.findViewById(R.id.session_answer_answer_field);
        submitButton = this.findViewById(R.id.session_answer_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionAnswer sessionAnswer = new SessionAnswer();
                sessionAnswer.setSessionId(""); // TODO
                sessionAnswer.setQuestionnaireId(""); // TODO
                sessionAnswer.setQuestionId(""); // TODO
                sessionAnswer.setAnswerId(""); // TODO

                /* INSERT */
                try {
                    fireBaseCloudHelper.insert(SESSION_ANSWER_TABLE, sessionAnswer);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    Log.i(TAG, "Could not access server database (Firebase)");
                    e.printStackTrace();
                }
                SESSION_ANSWER_TABLE.insert(sessionAnswer);
            }
        });
    }
}