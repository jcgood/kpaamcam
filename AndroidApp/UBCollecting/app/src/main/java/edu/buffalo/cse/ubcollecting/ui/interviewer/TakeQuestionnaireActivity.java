package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.QuestionProperty;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.Session;
import edu.buffalo.cse.ubcollecting.data.tables.AnswerTable;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionPropertyDefTable;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionPropertyTable;
import edu.buffalo.cse.ubcollecting.ui.QuestionManager;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.QuestionFragment.SELECTED_ANSWER;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectQuestionnaireActivity.SELECTED_QUESTIONNAIRE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.ViewQuestionsActivity.QUESTION_INDEX;


/**
 * Landing activity that sets up the taking of a questionnaire
 */

public class TakeQuestionnaireActivity extends AppCompatActivity implements QuestionManager {

    private QuestionStatePagerAdapter questionStatePagerAdapter;
    private ViewPager questionViewPager;
    private ArrayList<QuestionnaireContent> questionnaire;
    public final static String QUESTIONNAIRE_CONTENT = "Question";
    public final static String QUESTION_TYPE="QuestionType";
    public int questionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_questionnaire);
        questionnaire = DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE.getAllQuestions(getQuestionnaire(getIntent()).getId());
        questionStatePagerAdapter = new QuestionStatePagerAdapter(getSupportFragmentManager());
        questionViewPager = findViewById(R.id.questionnaire_container);
        questionViewPager.setAdapter(questionStatePagerAdapter);
        questionIndex = (Integer) getIntent().getSerializableExtra(QUESTION_INDEX);
        getNextQuestion();
    }

    public void getNextQuestion(){
         if (questionIndex<questionnaire.size()){
            Bundle bundle = new Bundle();
            bundle.putSerializable(QUESTIONNAIRE_CONTENT,questionnaire.get(questionIndex));
            bundle.putSerializable(SELECTED_QUESTIONNAIRE, getQuestionnaire(getIntent()).getId());
            bundle.putSerializable(SELECTED_SESSION, getSession(getIntent()));

            //get any answers this question may have
            String selection = AnswerTable.KEY_QUESTION_ID +  " = ?  AND "
                    +AnswerTable.KEY_QUESTIONNAIRE_ID + " = ? ";
            String questionId = questionnaire.get(questionIndex).getQuestionId();
            String [] selectionArgs = {questionId, getQuestionnaire(getIntent()).getId()};

            String selection1 = QuestionPropertyTable.KEY_QUESTION_ID +  " = ? ";
            String [] selectionArgs1 = {questionId};

            //get type of question
             ArrayList<QuestionProperty> all = DatabaseHelper.QUESTION_PROPERTY_TABLE.getAll(selection1, selectionArgs1, null);
             String prop_id= all.get(0).getPropertyId();

             String selction2=QuestionPropertyDefTable.KEY_ID+" = ?";
             String [] selectionArgs2 = {prop_id};

             ArrayList<QuestionPropertyDef> all1 = DatabaseHelper.QUESTION_PROPERTY_DEF_TABLE.getAll(selction2, selectionArgs2, null);
             String typeOfQuestion=all1.get(0).name;
             Log.d("TakeQuestion","Type of question: "+typeOfQuestion);

             bundle.putSerializable(QUESTION_TYPE,typeOfQuestion);

            final ArrayList<Answer> answerList = DatabaseHelper.ANSWER_TABLE.getAll(selection, selectionArgs, null);

            if(!answerList.isEmpty()){
                bundle.putSerializable(SELECTED_ANSWER, answerList);
            }

            if(typeOfQuestion.equals("Audio")){
                Log.d("TakeQuestion","Audio Frag started");
                AudioFragment audioFragment = new AudioFragment();
                audioFragment.setArguments(bundle);
                questionStatePagerAdapter.addFragement(audioFragment);
                questionStatePagerAdapter.notifyDataSetChanged();
            }
            else if(typeOfQuestion.equals("Video")){
                Log.d("TakeQuestion","Video Frag started");
                VideoFragment videoFragment = new VideoFragment();
                videoFragment.setArguments(bundle);
                questionStatePagerAdapter.addFragement(videoFragment);
                questionStatePagerAdapter.notifyDataSetChanged();
            }
            else if(typeOfQuestion.equals("Photo")){
                Log.d("TakeQuestion","Photo Frag started");
                PhotoFragment photoFragment = new PhotoFragment();
                photoFragment.setArguments(bundle);
                questionStatePagerAdapter.addFragement(photoFragment);
                questionStatePagerAdapter.notifyDataSetChanged();
            }
            else{
                Log.d("TakeQuestion","Text Frag started");
                QuestionFragment questionFragment = new QuestionFragment();
                questionFragment.setArguments(bundle);
                questionStatePagerAdapter.addFragement(questionFragment);
                questionStatePagerAdapter.notifyDataSetChanged();
            }
            questionViewPager.setCurrentItem(questionIndex);
        }
        else{
            Toast.makeText(this, "You have successfully completed the questionnaire!", Toast.LENGTH_SHORT).show();
            Intent i = UserSelectQuestionnaireActivity.newIntent(TakeQuestionnaireActivity.this);
            i.putExtra(SELECTED_SESSION,getSession(getIntent()));
            startActivity(i);
            finish();
        }
    }

    public boolean isLastQuestion(){
        questionIndex++;
        return questionIndex-1 == questionnaire.size()-1;
    }

    public void saveAndQuitQuestionnaire(QuestionnaireContent questionnaireContent){
        Toast.makeText(this, "The questionnaire has been saved, you may resume it at any point", Toast.LENGTH_LONG).show();
        Intent i = UserSelectQuestionnaireActivity.newIntent(TakeQuestionnaireActivity.this);
        i.putExtra(SELECTED_SESSION , getSession(getIntent()));
        startActivity(i);
        finish();
    }

    /**
     * Helper function to extract a {@link edu.buffalo.cse.ubcollecting.data.models.Questionnaire} extra from and {@link Intent}
     * @param data {@link Intent} holding the extra
     * @return {@link edu.buffalo.cse.ubcollecting.data.models.Question} extra from {@link Intent}
     */
    public static Questionnaire getQuestionnaire(Intent data) {
        Serializable serializableObject = data.getSerializableExtra(SELECTED_QUESTIONNAIRE);

        return (Questionnaire) serializableObject;
    }

    public static Session getSession(Intent data) {
        Serializable serializableObject = data.getSerializableExtra(SELECTED_SESSION);

        return (Session) serializableObject;
    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, TakeQuestionnaireActivity.class);
        return i;
    }


}


