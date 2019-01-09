package edu.buffalo.cse.ubcollecting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.tables.Table;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.LANGUAGE_TABLE;

public class LanguageActivity extends EntryActivity<Language> {

    private static final String TAG = LanguageActivity.class.getSimpleName().toString();

    private EditText nameField;
    private EditText otherNamesField;
    private EditText descriptionField;
    //    private TextView typeField;
    private Button submitButton;
    private Button updateButton;

    @Override
    public void setUI(Language language) {
        nameField.setText(language.getName());
        otherNamesField.setText(language.getOtherNames());
        descriptionField.setText(language.getDescription());

    }

    @Override
    public void setEntryByUI() {

        entry.setName(nameField.getText().toString());
        entry.setOtherNames(otherNamesField.getText().toString());
        entry.setDescription(descriptionField.getText().toString());

    }


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        nameField = this.findViewById(R.id.language_name_field);
        otherNamesField = this.findViewById(R.id.language_other_names_field);
        descriptionField = this.findViewById(R.id.language_description_field);
//        typeField = this.findViewById(R.id.language_langtype_field);

        submitButton = this.findViewById(R.id.language_submit_button);
        submitButton.setOnClickListener(new SubmitButtonOnClickListener(LANGUAGE_TABLE));

        updateButton = this.findViewById(R.id.language_update_button);
        updateButton.setOnClickListener(new UpdateButtonOnClickListener(LANGUAGE_TABLE));


        if (getIntent().getFlags() == Table.FLAG_EDIT_ENTRY) {
            entry = getEntry(getIntent());
            setUI(entry);
            updateButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        } else {
            entry = new Language();
        }

    }

    @Override
    protected boolean isValidEntry() {

        boolean valid = true;

        if (nameField.getText().toString().trim().isEmpty()) {
            nameField.setError("This field is required");
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, "Please Fill in All Required Fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

}
