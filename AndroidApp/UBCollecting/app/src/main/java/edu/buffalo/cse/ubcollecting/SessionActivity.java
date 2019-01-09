package edu.buffalo.cse.ubcollecting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.buffalo.cse.ubcollecting.data.models.FieldTrip;
import edu.buffalo.cse.ubcollecting.data.models.Session;
import edu.buffalo.cse.ubcollecting.data.tables.Table;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.SESSION_TABLE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserLandingActivity.SELECTED_FIELD_TRIP;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;


public class SessionActivity extends EntryActivity<Session> {

    private static final String TAG = SessionActivity.class.getSimpleName().toString();

    private EditText labelField;
    private EditText nameField;
    private EditText locationField;
    private EditText descriptionField;
    private Button submitButton;
    private Button updateButton;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        labelField = this.findViewById(R.id.session_label_field);
        nameField = this.findViewById(R.id.session_name_field);
        locationField = this.findViewById(R.id.session_location_field);
        descriptionField = this.findViewById(R.id.session_description_field);

        updateButton = this.findViewById(R.id.session_update_button);
        updateButton.setOnClickListener(new UpdateButtonOnClickListener(SESSION_TABLE));

        submitButton = this.findViewById(R.id.session_submit_button);
        submitButton.setOnClickListener(new SubmitButtonOnClickListener(SESSION_TABLE));

        if (getIntent().getFlags() == Table.FLAG_EDIT_ENTRY) {
            entry = getEntry(getIntent());
            setUI(entry);
            updateButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        } else {
            entry = new Session();
        }

    }


    @Override
    void setUI(Session session) {
        nameField.setText(session.getName());
        labelField.setText(session.getLabel());
        locationField.setText(session.getLocation());
        descriptionField.setText(session.getDescription());
    }

    @SuppressLint("WrongConstant")
    @Override
    void setEntryByUI() {
        entry.setName(nameField.getText().toString());
        entry.setLabel(labelField.getText().toString());

        if (getIntent().getFlags() != Table.FLAG_EDIT_ENTRY) {
            Date currentDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatDate = formatter.format(currentDate);
            Log.i("SET START TIME", formatDate);
            entry.setStartTime(formatDate);
        }

        entry.setLocation(locationField.getText().toString());
        entry.setDescription(descriptionField.getText().toString());
        entry.setFieldTripId(getFieldTrip(getIntent()).getId());
    }

    @Override
    boolean isValidEntry() {
        boolean valid = true;

        if (nameField.getText().toString().trim().isEmpty()) {
            nameField.setError("This field is required");
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, "Please Fill in All Required Fields", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }


    /**
     * Helper function to extract a {@link edu.buffalo.cse.ubcollecting.data.models.FieldTrip} extra from and {@link Intent}
     * @param data {@link Intent} holding the extra
     * @return {@link edu.buffalo.cse.ubcollecting.data.models.FieldTrip} extra from {@link Intent}
     */
    public static FieldTrip getFieldTrip(Intent data) {
        Serializable serializableObject = data.getSerializableExtra(SELECTED_FIELD_TRIP);

        return (FieldTrip) serializableObject;
    }

    /**
     * Helper function to extract a {@link edu.buffalo.cse.ubcollecting.data.models.Session} extra from and {@link Intent}
     * @param data {@link Intent} holding the extra
     * @return {@link edu.buffalo.cse.ubcollecting.data.models.Session} extra from {@link Intent}
     */
    public static Session getSession(Intent data) {
        Serializable serializableObject = data.getSerializableExtra(SELECTED_SESSION);

        return (Session) serializableObject;
    }

}
