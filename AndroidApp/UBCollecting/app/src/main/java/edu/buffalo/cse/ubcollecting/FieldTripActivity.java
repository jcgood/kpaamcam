package edu.buffalo.cse.ubcollecting;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import edu.buffalo.cse.ubcollecting.data.models.FieldTrip;
import edu.buffalo.cse.ubcollecting.data.tables.Table;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.FIELD_TRIP_TABLE;


public class FieldTripActivity extends EntryActivity<FieldTrip> {

    private static final String TAG = FieldTripActivity.class.getSimpleName().toString();

    private EditText nameField;
    private TextView startDateField;
    private TextView endDateField;
    private Button startDateButton;
    private Button endDateButton;
    private Button submitButton;
    private Button updateButton;
    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fieldtrip);

        nameField = this.findViewById(R.id.fieldtrip_name_field);
        startDateField = this.findViewById(R.id.fieldtrip_start_date);
        endDateField = this.findViewById(R.id.fieldtrip_end_date);

        startDateButton = this.findViewById(R.id.fieldtrip_choose_start_date);
        endDateButton = this.findViewById(R.id.fieldtrip_choose_end_date);


        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        FieldTripActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        startDateListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        FieldTripActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        endDateListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        startDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String monthString = month > 9 ? String.valueOf(month) : "0"+String.valueOf(month);
                String dayString = day > 9 ? String.valueOf(day) : "0"+String.valueOf(day);
                Log.d(TAG, "onDateSet: yyyy-mm-dd: " + year + "-" + month + "-" + day);
                String date = year + "-" + monthString + "-" + dayString;
                startDateField.setText(date);
            }
        };

        endDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String monthString = month > 9 ? String.valueOf(month) : "0"+String.valueOf(month);
                String dayString = day > 9 ? String.valueOf(day) : "0"+String.valueOf(day);
                Log.d(TAG, "onDateSet: yyyy-mm-dd: " + year + "-" + month + "-" + day);
                String date = year + "-" + monthString + "-" + dayString;
                endDateField.setText(date);
            }
        };



        submitButton = this.findViewById(R.id.fieldtrip_submit_button);
        submitButton.setOnClickListener(new SubmitButtonOnClickListener(FIELD_TRIP_TABLE));

        updateButton = this.findViewById(R.id.fieldtrip_update_button);
        updateButton.setOnClickListener(new UpdateButtonOnClickListener(FIELD_TRIP_TABLE));

        if (getIntent().getFlags() == Table.FLAG_EDIT_ENTRY) {
            entry = getEntry(getIntent());
            setUI(entry);
            updateButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        } else {
            entry = new FieldTrip();
        }
    }

    @Override
    void setUI(FieldTrip fieldTrip) {
        nameField.setText(fieldTrip.getName());
        startDateField.setText(fieldTrip.getStartDate());
        endDateField.setText(fieldTrip.getEndDate());
    }

    @Override
    void setEntryByUI() {
        entry.setName(nameField.getText().toString());
        entry.setStartDate(startDateField.getText().toString());
        entry.setEndDate(endDateField.getText().toString());
    }


    @Override
    boolean isValidEntry() {

        boolean valid = true;

        if (nameField.getText().toString().trim().isEmpty()) {
            nameField.setError("This field is required");
            valid = false;
        }

        if (startDateField.getText().toString().trim().isEmpty()) {
            startDateField.setError("This field is required");
            valid = false;
        }

        if (endDateField.getText().toString().trim().isEmpty()) {
            endDateField.setError("This field is required");
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, "Please Complete All Required Fields", Toast.LENGTH_SHORT).show();
        }

        return valid;

    }

}
