package edu.buffalo.cse.ubcollecting.ui.interviewer;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.ui.EntryOnItemSelectedListener;
import edu.buffalo.cse.ubcollecting.ui.QuestionManager;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.IS_LAST_LOOP_QUESTION;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.LOOP_QUESTION_TEXT;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.QUESTIONNAIRE_CONTENT;

public abstract class QuestionFragment extends Fragment {

    QuestionManager questionManager;
    QuestionnaireContent questionContent;
    private ArrayList<Language> questionLanguages;
    private ArrayAdapter<Language> questionLanguagesAdapter;
    protected TextView questionText;
    private Spinner questionLangSpinner;
    private HashMap<Language, QuestionLangVersion> questionTexts;
    private String mLoopQuestionText;

    private boolean mIsLoopQuestion = false;
    private boolean mIsLastLoopQuestion = true;


    @Override
    public void onStart() {
        Button nextQuestion = getView().findViewById(R.id.next_question);
        Button skipQuestion = getView().findViewById(R.id.skip_question);
        questionLangSpinner = getView().findViewById(R.id.question_language_spinner);
        questionText = getView().findViewById(R.id.question_text);
        skipQuestion.setOnClickListener(new AudioFragment.SkipQuestionOnClickListener());

        if (mIsLoopQuestion) {
            mLoopQuestionText = (String) getArguments().getSerializable(LOOP_QUESTION_TEXT);
            mIsLastLoopQuestion = (boolean) getArguments().getSerializable(IS_LAST_LOOP_QUESTION);
        }

        if (mIsLoopQuestion && mIsLastLoopQuestion) {
            nextQuestion.setText("Next Question");
        }
        if((questionManager.isLastQuestion() && mIsLoopQuestion && mIsLastLoopQuestion)
                || (!mIsLoopQuestion && questionManager.isLastQuestion())){
            nextQuestion.setText("Finish");
        }

        questionContent = (QuestionnaireContent) getArguments().getSerializable(QUESTIONNAIRE_CONTENT);
        questionTexts = DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(questionContent.getQuestionId());

        questionLanguages = new ArrayList<>();
        questionLanguages.addAll(questionTexts.keySet());
        questionLanguagesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, questionLanguages);
        questionLangSpinner.setAdapter(questionLanguagesAdapter);
        questionLangSpinner.setOnItemSelectedListener(new LanguageOnItemSelectedListener());
        questionLangSpinner.setSelection(getEnglishQuestionIndex());
        super.onStart();
    }

    abstract boolean validateEntry();

    public abstract void submitAnswer();

    public void onAttach(Context context){
        super.onAttach(context);
        questionManager = (QuestionManager) context;

    }

    public void setIsLoopQuestion(boolean isLoopQuestion) {
        mIsLoopQuestion = isLoopQuestion;
    }



    protected class SkipQuestionOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Toast.makeText(getContext(), "Question Skipped", Toast.LENGTH_SHORT).show();
            questionManager.getNextQuestion();
        }
    }

    protected class SaveAndExitQuestionOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(validateEntry()){
                submitAnswer();
                questionManager.saveAndQuitQuestionnaire(questionContent);
            }
        }
    }

    private int getEnglishQuestionIndex(){
        for (int i = 0; i<questionLanguages.size(); i++){
            if (questionLanguages.get(i).getName().toLowerCase().equals("english")){
                return i;
            }
        }
        return 0;
    }

    private class LanguageOnItemSelectedListener extends EntryOnItemSelectedListener<Language> {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            super.onItemSelected(parent, view, position, id);
            Language language = (Language) questionLangSpinner.getSelectedItem();
            if (mIsLoopQuestion) {
                questionText.setText(mLoopQuestionText);
            }
            else {
                questionText.setText(questionTexts.get(language).getQuestionText());
            }
        }
    }



}
