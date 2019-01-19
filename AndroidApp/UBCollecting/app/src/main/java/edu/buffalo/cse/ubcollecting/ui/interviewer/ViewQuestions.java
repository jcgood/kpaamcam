package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.Session;

import static edu.buffalo.cse.ubcollecting.SessionActivity.getSession;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.getQuestionnaire;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectQuestionnaireActivity.SELECTED_QUESTIONNAIRE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

public class ViewQuestions extends AppCompatActivity {

    private ListView questionsList;
    private TextView questionnaireTitle;

    private ArrayList<QuestionnaireContent> questionnaire;
    public final static String QUESTIONNAIRE_CONTENT = "Question";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_questions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        questionnaireTitle = (TextView) findViewById(R.id.questionareName);
        questionsList=(ListView) findViewById(R.id.qList);

        questionnaire = DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE.getAllQuestions(getQuestionnaire(getIntent()).getId());
        questionnaireTitle.setText("Questionnaire Name: "+getQuestionnaire(getIntent()).name);

        ArrayList<String> listofQ=new ArrayList<>();

        for(int i = 0; i< this.questionnaire.size(); i++) {

            HashMap<Language, QuestionLangVersion> questionTexts = DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(this.questionnaire.get(i).questionId);
            for(Language l:questionTexts.keySet()) {
                listofQ.add("Language: "+l.name+"\nQuestion: "+questionTexts.get(l).getQuestionText());
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listofQ);

        questionsList.setAdapter(arrayAdapter);

        questionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sample="The index is: "+i;
                Toast.makeText(ViewQuestions.this, sample, Toast.LENGTH_SHORT).show();
                TakeQuestionnaireActivity.questionIndex=i;
                Intent k = TakeQuestionnaireActivity.newIntent(ViewQuestions.this);
                k.putExtra(SELECTED_SESSION,getSession(getIntent()));
                k.putExtra(SELECTED_QUESTIONNAIRE, getQuestionnaire(getIntent()));
                startActivity(k);
                finish();
            }
        });


    }
    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, ViewQuestions.class);
        return i;
    }

}
