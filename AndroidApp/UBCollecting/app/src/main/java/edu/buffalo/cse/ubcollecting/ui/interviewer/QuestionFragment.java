package edu.buffalo.cse.ubcollecting.ui.interviewer;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.ui.QuestionManager;

public abstract class QuestionFragment extends Fragment {

    QuestionManager questionManager;
    QuestionnaireContent questionContent;

//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }

    abstract boolean validateEntry();

    public abstract void submitAnswer();

    public void onAttach(Context context){
        super.onAttach(context);
        questionManager = (QuestionManager) context;
    }


    protected class NextQuestionOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(validateEntry()){
                submitAnswer();
                questionManager.getNextQuestion();
            }
        }
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


}
