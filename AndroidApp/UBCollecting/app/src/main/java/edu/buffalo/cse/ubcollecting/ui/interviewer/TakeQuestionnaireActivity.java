package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.Session;
import edu.buffalo.cse.ubcollecting.ui.QuestionManager;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.TextFragment.SELECTED_ANSWER;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectQuestionnaireActivity.SELECTED_QUESTIONNAIRE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.ViewQuestionsActivity.QUESTION_INDEX;
import static edu.buffalo.cse.ubcollecting.utils.Constants.LOOP;


/**
 * Landing activity that sets up the taking of a questionnaire
 */

public class TakeQuestionnaireActivity extends AppCompatActivity implements QuestionManager {

    static final String LOOP_QUESTION_TEXT = "loop_question_text";
    static final String LOOP_QUESTION_ID = "loop_question_id";
    static final String IS_LAST_LOOP_QUESTION = "is_last_loop_question";
    static final String IS_LOOP_QUESTION_SET = "is_loop_set";

    private QuestionStatePagerAdapter questionStatePagerAdapter;
    private ViewPager questionViewPager;
    private ArrayList<QuestionnaireContent> questionnaire;
    public final static String QUESTIONNAIRE_CONTENT = "Question";
    public final static String IN_LOOP = "inLoop";
    public final static String QUESTION_TYPE="QuestionType";
    public final static String PARENT_ANSWER = "parentAnswer";
    public int questionIndex;
    private int iterationsCounter = 0;
    private int currentQuestionPosition = 0;
    private boolean mIsFirstQuestion = true;
    public static final String QUESTION_ID = "Question ID";

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
        QuestionnaireContent question;
        if(questionIndex>=questionnaire.size()){
            Toast.makeText(this, "You have successfully completed the questionnaire!", Toast.LENGTH_SHORT).show();
            Intent i = UserSelectQuestionnaireActivity.newIntent(TakeQuestionnaireActivity.this);
            i.putExtra(SELECTED_SESSION,getSession(getIntent()));
            startActivity(i);
            finish();

        }
        else{
            question = questionnaire.get(questionIndex);
            Answer answerParent=null;

            // get most recent answer(s)
            String questionId = question.getQuestionId();
            ArrayList<Answer> answerList = DatabaseHelper.ANSWER_TABLE.getMostRecentAnswer(questionId, getQuestionnaire(getIntent()).getId(), answerParent);

            // get type of question
            QuestionPropertyDef questionProperty = DatabaseHelper.QUESTION_PROPERTY_TABLE.getQuestionProperty(questionId);
            String typeOfQuestion = questionProperty.getName();

            // Goes to the end of the View Pager, to ensure latest question is showing
            if (questionStatePagerAdapter.getCount() > 0) {
                currentQuestionPosition = questionStatePagerAdapter.getCount() - 1;
                questionViewPager.setCurrentItem(currentQuestionPosition);
                System.out.println("Current: " + questionViewPager.getCurrentItem() + ", Max: " + questionStatePagerAdapter.getCount());
            }

            /* Takes care of loop question, by adding every question in the loop to the viewpager as
               a separate Fragment */
            if (typeOfQuestion.equals(LOOP)) {
                // Adding the root question to the loop
                String rootID = (String) getIntent().getSerializableExtra(QUESTION_ID);
                String rootText =  DatabaseHelper.QUESTION_TABLE.findById(questionId).getDisplayText();
                Bundle root = createBundleArgs(question,typeOfQuestion);
                root.putSerializable(LOOP_QUESTION_TEXT,rootText);
                root.putSerializable(LOOP_QUESTION_ID,rootID);
                root.putSerializable(IS_LAST_LOOP_QUESTION,false);
                root.putSerializable(IS_LOOP_QUESTION_SET,true);
                QuestionFragment rootFragment = new LoopFragment();
                rootFragment.setArguments(root);
                questionStatePagerAdapter.addFragement(rootFragment);

                questionStatePagerAdapter.notifyDataSetChanged();

                if (!mIsFirstQuestion) {
                    continueLoop();
                }

                mIsFirstQuestion = false;
                return;
            }

            QuestionFragment questionFragment;
            if(typeOfQuestion.equals("Audio")){
                questionFragment = new AudioFragment();

            }
            else if(typeOfQuestion.equals("Video")){
                questionFragment = new VideoFragment();

            }
            else if(typeOfQuestion.equals("Photo")){
                questionFragment = new PhotoFragment();

            }

            else {
                questionFragment = new TextFragment();

            }

            Bundle bundle = createBundleArgs(question, typeOfQuestion);

            if(!answerList.isEmpty()){
                bundle.putSerializable(SELECTED_ANSWER, answerList);
            }

            questionFragment.setArguments(bundle);
            questionStatePagerAdapter.addFragement(questionFragment);
            questionStatePagerAdapter.notifyDataSetChanged();
            currentQuestionPosition += 1;
            questionViewPager.setCurrentItem(currentQuestionPosition);
            Log.i("ITERATIONS COUNTER ", String.valueOf(iterationsCounter));

            questionIndex++;
            mIsFirstQuestion = false;
        }
    }

    private Bundle createBundleArgs(QuestionnaireContent questionnaireContent, String questionType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUESTIONNAIRE_CONTENT, questionnaireContent);
        bundle.putSerializable(SELECTED_QUESTIONNAIRE, getQuestionnaire(getIntent()).getId());
        bundle.putSerializable(SELECTED_SESSION, getSession(getIntent()));
        bundle.putSerializable(QUESTION_TYPE, questionType);

        return bundle;
    }

    public boolean isLastQuestion(){
        return questionIndex-1 == questionnaire.size()-1;

    }

    public void continueLoop(){
        currentQuestionPosition += 1;
        questionViewPager.setCurrentItem(currentQuestionPosition);
        System.out.println("Question position: " + currentQuestionPosition);
    }

    public void saveAndQuitQuestionnaire(QuestionnaireContent questionnaireContent){
        Toast.makeText(this, "The questionnaire has been saved, you may resume it at any point", Toast.LENGTH_LONG).show();
        Intent i = UserSelectQuestionnaireActivity.newIntent(TakeQuestionnaireActivity.this);
        i.putExtra(SELECTED_SESSION , getSession(getIntent()));
        startActivity(i);
        finish();
    }
    //Function to add the repeat questions related to the loop
    public HashMap<String,String> askRepeatQuestions(ArrayList<EditText> answers){
        QuestionnaireContent question = questionnaire.get(questionIndex);
        String questionId = question.getQuestionId();

        QuestionPropertyDef questionProperty = DatabaseHelper.QUESTION_PROPERTY_TABLE.getQuestionProperty(questionId);
        String typeOfQuestion = questionProperty.getName();

        HashMap<Language, QuestionLangVersion> questionTexts =
                DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(question.getQuestionId());
        String loopQuestion = questionTexts.get(questionTexts.keySet().iterator().next()).getQuestionText();
        HashMap<String, String> questionHashMap =
                LoopQuestionHelper.createQuestionHashMap(loopQuestion);
        questionIndex++;
        return questionHashMap;

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

    @Override
    public void onBackPressed() {
        Intent intent = UserLandingActivity.newIntent(TakeQuestionnaireActivity.this);
        startActivity(intent);
        finish();
    }


}


