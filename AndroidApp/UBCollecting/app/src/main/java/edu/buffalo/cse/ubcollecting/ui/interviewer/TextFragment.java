package edu.buffalo.cse.ubcollecting.ui.interviewer;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.Session;
import edu.buffalo.cse.ubcollecting.ui.EntryOnItemSelectedListener;
import edu.buffalo.cse.ubcollecting.ui.QuestionManager;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.IN_LOOP;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.PARENT_ANSWER;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

/**
 * A fragment to represent a question to be taken in a questionnaire.
 */

public class TextFragment extends QuestionFragment{

    public final static String SELECTED_ANSWER = "selected answer";




    private EditText answerText;

    private Button saveAndExitQuestion;
    private ArrayList<Answer> answerList;
    private Answer answer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        answer = new Answer();

        answerText = view.findViewById(R.id.answer_text);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);




        saveAndExitQuestion = view.findViewById(R.id.saveandexit_question);



        saveAndExitQuestion.setOnClickListener(new SaveAndExitQuestionOnClickListener());
        if(getArguments().containsKey(SELECTED_ANSWER)){
            answerList = (ArrayList<Answer>) getArguments().getSerializable(SELECTED_ANSWER);
            Answer mostRecentAnswer = answerList.get(0);
            answerText.setText(mostRecentAnswer.getText());
        } else {
            answerList = new ArrayList<>();
        }
        return view;
    }




    @Override
    protected boolean validateEntry() {

        boolean valid = true;

        if (answerText.getText().toString().isEmpty()){
            valid = false;
            answerText.setError("A Text Answer is Required");
        }

        if (!valid){
            Toast.makeText(this.getActivity(), "Please Fill in All Required Fields", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    @Override
    public void submitAnswer() {
        double version = 0;
        if (!answerList.isEmpty()) {
            Answer recentAnswer = answerList.get(0);
            version = recentAnswer.getVersion();
        }
//        Boolean inLoop = (boolean) getArguments().getSerializable(IN_LOOP);
//        if(inLoop){
//            Answer parentAnswer = (Answer) getArguments().getSerializable(PARENT_ANSWER);
//            String parentAnswerId = parentAnswer.getId();
//            answer.setParentAnswer(parentAnswerId);
//        }
        answer.setQuestionId(questionContent.getQuestionId());
        answer.setQuestionnaireId(questionContent.getQuestionnaireId());
        answer.setText(answerText.getText().toString());
        answer.setSessionId(((Session) getArguments().getSerializable(SELECTED_SESSION)).getId());
        answer.setVersion(version+1);
        DatabaseHelper.ANSWER_TABLE.insert(answer);

    }


}
