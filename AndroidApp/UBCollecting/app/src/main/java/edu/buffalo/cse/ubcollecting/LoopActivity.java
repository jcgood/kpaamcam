package edu.buffalo.cse.ubcollecting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import edu.buffalo.cse.ubcollecting.data.models.Loop;
import edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity;

public class LoopActivity extends EntryActivity<Loop> {
    private static final String TAG = PersonActivity.class.getSimpleName().toString();

    EditText iterationsField;

    @SuppressLint("WrongConstant")
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop);

        iterationsField = findViewById(R.id.loop_iterations_field);
    }
    @Override
    void setUI(Loop entry) {

    }

    @Override
    void setEntryByUI() {

    }

    @Override
    boolean isValidEntry() {
        return false;
    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, LoopActivity.class);
        return i;
    }
}
