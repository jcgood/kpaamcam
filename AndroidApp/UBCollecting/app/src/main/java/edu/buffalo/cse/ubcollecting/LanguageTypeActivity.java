package edu.buffalo.cse.ubcollecting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.buffalo.cse.ubcollecting.data.models.LanguageType;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.LANGUAGE_TYPE_TABLE;

public class LanguageTypeActivity extends AppCompatActivity {

    private static final String TAG = LanguageTypeActivity.class.getSimpleName().toString();

    private EditText nameField;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_type);

        nameField = this.findViewById(R.id.language_type_name_field);
        submitButton = this.findViewById(R.id.language_type_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LanguageType langType = new LanguageType();
                langType.setName(nameField.getText().toString());

                LANGUAGE_TYPE_TABLE.insert(langType);
            }
        });
    }
}
