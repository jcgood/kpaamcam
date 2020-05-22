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
import java.util.HashMap;
import java.util.Iterator;

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


/**
 * Landing activity that sets up the taking of a questionnaire
 */

public class TakeQuestionnaireActivity extends AppCompatActivity implements QuestionManager {

    static final String LIST_QUESTION_TEXT = "list_question_text";
    static final String LIST_QUESTION_ID = "list_question_id";
    static final String IS_LAST_LIST_QUESTION = "is_last_list_question";

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

            /* Takes care of list question, by adding every question in the list to the viewpager as
               a separate Fragment */
            if (typeOfQuestion.equals("List")) {
                HashMap<Language, QuestionLangVersion> questionTexts =
                        DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(question.getQuestionId());
                String listQuestion = questionTexts.get(questionTexts.keySet().iterator().next()).getQuestionText();
                HashMap<String, String> questionHashMap =
                        ListQuestionHelper.createQuestionHashMap(listQuestion);

                int counter = 0;
                for (String listQuestionId : questionHashMap.keySet()) {
                    String listQuestionText = questionHashMap.get(listQuestionId);
                    Bundle bundle = createBundleArgs(question, typeOfQuestion);
                    bundle.putSerializable(LIST_QUESTION_TEXT, listQuestionText);
                    bundle.putSerializable(LIST_QUESTION_ID, listQuestionId);
                    bundle.putSerializable(IS_LAST_LIST_QUESTION,
                            counter == (questionHashMap.keySet().size() - 1));

                    QuestionFragment listFragment = new ListFragment();
                    listFragment.setArguments(bundle);
                    questionStatePagerAdapter.addFragement(listFragment);
                    System.out.println("Question Text: " + listQuestionText);
                    counter++;
                }
                questionStatePagerAdapter.notifyDataSetChanged();

                if (!mIsFirstQuestion) {
                    continueLoop();
                }

                mIsFirstQuestion = false;
                questionIndex++;
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


