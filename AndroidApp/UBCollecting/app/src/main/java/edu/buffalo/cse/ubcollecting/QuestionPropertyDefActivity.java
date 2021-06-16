package edu.buffalo.cse.ubcollecting;

import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;

import edu.buffalo.cse.ubcollecting.app.App;
import edu.buffalo.cse.ubcollecting.data.FireBaseCloudHelper;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_PROPERTY_DEF_TABLE;

public class QuestionPropertyDefActivity extends AppCompatActivity {

    private static final String TAG = QuestionPropertyDefActivity.class.getSimpleName().toString();

    private EditText nameField;
    private Button submitButton;
    private final FireBaseCloudHelper fireBaseCloudHelper = new FireBaseCloudHelper(App.getContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_property_def);

        nameField = this.findViewById(R.id.question_property_def_name_field);
        submitButton = this.findViewById(R.id.question_property_def_submit_button);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionPropertyDef propertyDef = new QuestionPropertyDef();
                propertyDef.setName(nameField.getText().toString());

                /* INSERT */
                try {
                    fireBaseCloudHelper.insert(QUESTION_PROPERTY_DEF_TABLE, propertyDef);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    Log.i(TAG, "Could not access server database (Firebase)");
                    e.printStackTrace();
                }
                QUESTION_PROPERTY_DEF_TABLE.insert(propertyDef);

            }
        });
    }
}
