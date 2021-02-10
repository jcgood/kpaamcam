package edu.buffalo.cse.ubcollecting;

import android.annotation.SuppressLint;
import android.os.Bundle;
//import android.support.design.widget.TabLayout;
//import androidx.core.app.Fragment;
//import androidx.core.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionnaireContentTable;
import edu.buffalo.cse.ubcollecting.data.tables.Table;
import edu.buffalo.cse.ubcollecting.ui.CreateQuestionnaireAdapter;
import edu.buffalo.cse.ubcollecting.ui.CreateQuestionnaireFragment;
import edu.buffalo.cse.ubcollecting.ui.QuestionnaireManager;
import edu.buffalo.cse.ubcollecting.ui.QuestionnaireQuestionsFragment;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_TABLE;

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
            Log.d("QA","Flg set of updateButton to VISIBLE");
            entry = getEntry(getIntent());
            updateButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
        else {
            Log.d("QA","Flg set of submitButton to VISIBLE");
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
                /* INSERT */
                table.insert(entry);
                setEntryResult(entry);
                for (QuestionnaireContent content : questionsFragment.getQuestionnaireContent()) {
                    /* INSERT */
                    QUESTIONNAIRE_CONTENT_TABLE.insert(content);
                }
                Hashtable<String, ArrayList<QuestionnaireContent>> loopContentTable =questionsFragment.getLoopContent();
                for(String qcId: loopContentTable.keySet()){
                    ArrayList <QuestionnaireContent> loopContent = loopContentTable.get(qcId);
                    for(QuestionnaireContent qcEntry: loopContent){
                        /* INSERT */
                        QUESTIONNAIRE_CONTENT_TABLE.insert(qcEntry);
                    }
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
                /* UPDATE */ /*not sure about this one, might just be a UI element */
                table.update(entry);
                setEntryResult(entry);
                for (QuestionnaireContent content : questionsFragment.getQuestionnaireContent()) {
                    content.setIsParent(0);
                    /* INSERT */
                    QUESTIONNAIRE_CONTENT_TABLE.insert(content);
                }
                Hashtable<String, ArrayList<QuestionnaireContent>> loopContentTable =questionsFragment.getLoopContent();
                for(String qcId: loopContentTable.keySet()){
                    ArrayList <QuestionnaireContent> loopContent = loopContentTable.get(qcId);
                    for(QuestionnaireContent qcEntry: loopContent){
                        /* INSERT */
                        QUESTIONNAIRE_CONTENT_TABLE.insert(qcEntry);
                    }
                }
                finish();
            }
        }
    }


}
