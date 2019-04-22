package edu.buffalo.cse.ubcollecting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireType;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionnaireContentTable;
import edu.buffalo.cse.ubcollecting.data.tables.Table;
import edu.buffalo.cse.ubcollecting.ui.AddQuestionsActivity;
import edu.buffalo.cse.ubcollecting.ui.CreateQuestionnaireAdapter;
import edu.buffalo.cse.ubcollecting.ui.CreateQuestionnaireFragment;
import edu.buffalo.cse.ubcollecting.ui.EntryOnItemSelectedListener;
import edu.buffalo.cse.ubcollecting.ui.QuestionnaireManager;
import edu.buffalo.cse.ubcollecting.ui.QuestionnaireQuestionsFragment;
import edu.buffalo.cse.ubcollecting.ui.UiUtils;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_TYPE_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_LANG_VERSION_TABLE;
import static edu.buffalo.cse.ubcollecting.ui.AddQuestionsActivity.EXTRA_QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.QuestionnaireQuestionsFragment.RESULT_ADD_QUESTIONS;

/**
 * Activity0 for creating a questionnaire
 */
public class QuestionnaireActivity extends EntryActivity<Questionnaire> implements QuestionnaireManager {

    private static final String TAG = QuestionnaireActivity.class.getSimpleName();

    private Button updateButton;
    private Button submitButton;
    private ViewPager viewPager;
    private QuestionnaireQuestionsFragment questionsFragment;
    private CreateQuestionnaireFragment createQuestionnaireFragment;

    @SuppressLint("WrongConstant")
    private void setupViewPager(ViewPager viewPager){
        CreateQuestionnaireAdapter createQuestionnaireAdapter = new CreateQuestionnaireAdapter(getSupportFragmentManager());
        createQuestionnaireFragment = new CreateQuestionnaireFragment();
        questionsFragment = new QuestionnaireQuestionsFragment();
        createQuestionnaireAdapter.addFragment(createQuestionnaireFragment, "Questionnaire Details ");
        createQuestionnaireAdapter.addFragment(questionsFragment, "Add or edit Questions");
        viewPager.setAdapter(createQuestionnaireAdapter);
        Bundle args = new Bundle();
        if(getIntent().getFlags() == Table.FLAG_EDIT_ENTRY){
            args.putBoolean("Update", true);
        }
        else{
            args.putBoolean("Update", false);
        }
        createQuestionnaireFragment.setArguments(args);
    }


    @Override
    void setUI(Questionnaire entry) {

    }

    @Override
    void setEntryByUI() {

        entry.setName(createQuestionnaireFragment.getName());
        entry.setDescription(createQuestionnaireFragment.getDescription());
        entry.setTypeId(createQuestionnaireFragment.getType());
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        updateButton = this.findViewById(R.id.questionnaire_update_button);
        updateButton.setOnClickListener(new QuestionnaireUpdateOnClickListener());

        submitButton = this.findViewById(R.id.questionnaire_submit_button);
        submitButton.setOnClickListener(new QuestionnaireSubmitOnClickListener());

        if(getIntent().getFlags() == Table.FLAG_EDIT_ENTRY){
            entry = getEntry(getIntent());
            updateButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
        else {
            entry = new Questionnaire();
            updateButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        }
        viewPager = (ViewPager) findViewById(R.id.questionnaire_view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.questionnaire_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }





    protected boolean isValidEntry() {
        boolean valid = true;

        if (createQuestionnaireFragment.getName().trim().isEmpty()) {
//            nameField.setError("This field is required");
            valid = false;
        }
        if (createQuestionnaireFragment.getType() == null) {
//            nameField.setError("This field is required");
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, "Please Fill in All Required Fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public Questionnaire getQuestionnaireEntry() {
        return entry;
    }

    private class QuestionnaireSubmitOnClickListener extends SubmitButtonOnClickListener {
        public QuestionnaireSubmitOnClickListener() {
            super(QUESTIONNAIRE_TABLE);
        }

        @Override
        public void onClick(View view) {
            setEntryByUI();
            if (isValidEntry()) {
                table.insert(entry);
                setEntryResult(entry);
                for (QuestionnaireContent content : questionsFragment.getQuestionnaireContent()) {
                    QUESTIONNAIRE_CONTENT_TABLE.insert(content);
                }
                finish();
            }
        }
    }

    private class QuestionnaireUpdateOnClickListener extends UpdateButtonOnClickListener {
        public QuestionnaireUpdateOnClickListener() {
            super(QUESTIONNAIRE_TABLE);
        }

        @Override
        public void onClick(View view) {
            String selection = QuestionnaireContentTable.KEY_QUESTIONNAIRE_ID+ " = ?";
            String[] selectionArgs = {entry.getId()};
            ArrayList<QuestionnaireContent> prevQuestionnaireContent = DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE.getAll(selection, selectionArgs,null);
            
            for (QuestionnaireContent content : prevQuestionnaireContent) {
                QUESTIONNAIRE_CONTENT_TABLE.permanentlyDelete(content.getId());
            }

            setEntryByUI();
            if (isValidEntry()) {
                table.update(entry);
                setEntryResult(entry);
                for (QuestionnaireContent content : questionsFragment.getQuestionnaireContent()) {
                    QUESTIONNAIRE_CONTENT_TABLE.insert(content);
                }
                finish();
            }
        }
    }


}
