package edu.buffalo.cse.ubcollecting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.buffalo.cse.ubcollecting.data.models.File;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.FILE_TABLE;

/**
 * UNUSED THUS FAR
 */

public class FileActivity extends AppCompatActivity {

    private static final String TAG = FileActivity.class.getSimpleName().toString();

    private TextView fileNameField;
    private TextView answerNameField;
    private EditText fileTypeField;
    private EditText filePathField;
    private EditText fileCreatorField;
    private EditText fileStartTimeField;
    private EditText fileEndTimeField;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        fileNameField = this.findViewById(R.id.file_name_field);
        answerNameField = this.findViewById(R.id.file_answer_name_field);
        fileTypeField = this.findViewById(R.id.file_type_field);
        filePathField = this.findViewById(R.id.file_path_field);
        fileCreatorField = this.findViewById(R.id.file_creator_field);
        fileStartTimeField = this.findViewById(R.id.file_start_time_field);
        fileEndTimeField = this.findViewById(R.id.file_end_time_field);
        submitButton = this.findViewById(R.id.file_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File();
                file.setName(fileNameField.getText().toString());
                file.setAnswerId(""); // TODO
                file.setType(fileTypeField.getText().toString());
                file.setPath(filePathField.getText().toString());
                file.setCreatorId(""); // TODO
                file.setStartTime(fileStartTimeField.getText().toString());
                file.setEndTime(fileEndTimeField.getText().toString());

                FILE_TABLE.insert(file);
            }
        });
    }
}
