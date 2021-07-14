package edu.buffalo.cse.ubcollecting.ui.interviewer;


import android.os.Bundle;
import androidx.annotation.Nullable;
//import android.support.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import androidx.core.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.Session;

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
        Button nextQuestion = view.findViewById(R.id.next_question);

        nextQuestion.setOnClickListener(new NextQuestionOnClickListener());
        return view;
    }




    @Override
    protected boolean validateEntry() {

        boolean valid = true;

        String[] nullCheckAndLength = super.nullCheckAndLength;

        if(!checkValidLengthAndNull(nullCheckAndLength,answerText.getText().toString())) {
          valid = false;
        }

        if (answerText.getText().toString().isEmpty()){
            valid = false;
            answerText.setError("A Text Answer is Required");
        }

        if (!valid){
            Toast.makeText(this.getActivity(), "Please Fill in All Required Fields", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    public boolean checkValidLengthAndNull(String[] nullCheckAndLength, String answer){
//      boolean valid = true;

      int min = Integer.parseInt(nullCheckAndLength[0]);
      int max = Integer.parseInt(nullCheckAndLength[1]);
      String type = nullCheckAndLength[2];
      String[] answerArray = answer.split(" ");

      switch (type) {
        case "Number" :
          for(int i = 0; i < answer.length(); i++) {
            if(!Character.isDigit(answer.charAt(i))) {
              Toast.makeText(this.getActivity(), "The answer should be all digits", Toast.LENGTH_SHORT).show();
              return false;
            }
          }
          if(answer.length() <= min || answer.length() >= max) {
            Toast.makeText(this.getActivity(), "this answer length should between " + min +" and " + max + " length" + "(exclusive)", Toast.LENGTH_SHORT).show();
            return false;
          }
          break;
        case "Character":
          for(String i: answerArray) {
            for(int j = 0; j < i.length(); j++) {
              if(!Character.isLetter(i.charAt(j))) {
                Toast.makeText(this.getActivity(), "The answer should be all letters or words", Toast.LENGTH_SHORT).show();
                return false;
              }
            }
          }
          if(answerArray.length < min || answerArray.length > max) {
            Toast.makeText(this.getActivity(), "this answer length should between " + min +" and " + max + " length", Toast.LENGTH_SHORT).show();
            return false;
          }
          break;
        case "mix":
          if(answerArray.length < min || answerArray.length > max) {
            Toast.makeText(this.getActivity(), "this answer length should between " + min +" and " + max + " length", Toast.LENGTH_SHORT).show();
            return false;
          }
          break;
      }
      return true;
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
        /* INSERT */
        DatabaseHelper.ANSWER_TABLE.insert(answer);

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

}
