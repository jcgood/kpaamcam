package edu.buffalo.cse.ubcollecting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.buffalo.cse.ubcollecting.data.models.QuestionProperty;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_PROPERTY_TABLE;

/**
 * UNUSED THUS FAR
 */

public class QuestionPropertyActivity extends AppCompatActivity {

    private static final String TAG = QuestionProperty.class.getSimpleName().toString();

    private TextView propertyField;
    private TextView questionField;
    private EditText valueField;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_property);

        propertyField = this.findViewById(R.id.question_property_field);
        questionField = this.findViewById(R.id.question_property_question_field);
        valueField = this.findViewById(R.id.question_property_value_field);
        submitButton = this.findViewById(R.id.question_property_submit_button);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionProperty property = new QuestionProperty();
                property.setPropertyId(""); // TODO
                property.setQuestionId(""); // TODO
                property.setValue(0); //TODO

                QUESTION_PROPERTY_TABLE.insert(property);
            }
        });
    }
}
