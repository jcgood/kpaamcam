package edu.buffalo.cse.ubcollecting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireType;
import edu.buffalo.cse.ubcollecting.data.tables.Table;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_TYPE_TABLE;

public class QuestionnaireTypeActivity extends EntryActivity<QuestionnaireType> {

    private static final String TAG = QuestionnaireTypeActivity.class.getSimpleName().toString();

    private EditText nameField;
    private Button updateButton;
    private Button submitButton;

    @Override
    void setUI(QuestionnaireType entry) {
        nameField.setText(entry.getName());
    }

    @Override
    void setEntryByUI() {
        entry.setName(nameField.getText().toString());
    }

    @Override
    boolean isValidEntry() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_type);

        nameField = this.findViewById(R.id.questionnaire_type_name_field);
        updateButton = this.findViewById(R.id.questionnaire_type_update_button);
        updateButton.setOnClickListener(new UpdateButtonOnClickListener(QUESTIONNAIRE_TYPE_TABLE));

        submitButton = this.findViewById(R.id.questionnaire_type_submit_button);
        submitButton.setOnClickListener(new SubmitButtonOnClickListener(QUESTIONNAIRE_TYPE_TABLE));

        if (getIntent().getFlags() == Table.FLAG_EDIT_ENTRY) {
            entry = getEntry(getIntent());
            setUI(entry);
            updateButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        } else {
            entry = new QuestionnaireType();
            updateButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        }
    }
}
