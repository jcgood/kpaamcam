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
    private final FireBaseCloudHelper fireBaseCloudHelper = new FireBaseCloudHelper(App.getContext());

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

                /* INSERT */
                try {
                    fireBaseCloudHelper.insert(QUESTIONNAIRE_CONTENT_TABLE, content);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    Log.i(TAG, "Could not access server database (Firebase)");
                    e.printStackTrace();
                }
                QUESTIONNAIRE_CONTENT_TABLE.insert(content);
            }
        });
    }
}
