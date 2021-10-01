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
import edu.buffalo.cse.ubcollecting.data.models.SessionPerson;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.SESSION_PERSON_TABLE;

public class SessionPersonActivity extends AppCompatActivity {

    private static final String TAG = SessionPersonActivity.class.getSimpleName().toString();

    private TextView personField;
    private TextView sessionField;
    private TextView roleField;
    private Button submitButton;
    private final FireBaseCloudHelper fireBaseCloudHelper = new FireBaseCloudHelper(App.getContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_person);

        personField = this.findViewById(R.id.session_person_name_field);
        sessionField = this.findViewById(R.id.session_person_session_field);
        roleField = this.findViewById(R.id.session_person_role_field);
        submitButton = this.findViewById(R.id.session_person_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionPerson person = new SessionPerson();
                person.setPersonId(""); // TODO
                person.setSessionId(""); // TODO
                person.setRoleId(""); // TODO

                /* INSERT */
                try {
                    fireBaseCloudHelper.insert(SESSION_PERSON_TABLE, person);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    Log.i(TAG, "Could not access server database (Firebase)");
                    e.printStackTrace();
                }
                SESSION_PERSON_TABLE.insert(person);
            }
        });
    }
}