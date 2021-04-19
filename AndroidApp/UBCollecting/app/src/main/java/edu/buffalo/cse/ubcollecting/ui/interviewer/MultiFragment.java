package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.os.Bundle;

import androidx.annotation.ContentView;
import androidx.annotation.Nullable;
//import android.support.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import androidx.core.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.Session;
import edu.buffalo.cse.ubcollecting.ui.CreateQuestionActivity;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

public class MultiFragment extends QuestionFragment {

  public final static String SELECTED_ANSWER = "selected answer";
  private Answer answer;
  private EditText answerText;

  private LinearLayout optionLayout;

  private boolean showOption = false;

  private List<CheckBox> storedOptions;

  private int count = 0;

  private List<Answer> answerList;

  private boolean mcqType;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_mcq, container,false);
    answer = new Answer();

    storedOptions = new ArrayList<>();

    Button button = view.findViewById(R.id.show_option);


    button.setOnClickListener(new OptionListener());

    optionLayout = view.findViewById(R.id.mcq_options_view);

    if(getArguments().containsKey(SELECTED_ANSWER)){
      answerList = (ArrayList<Answer>) getArguments().getSerializable(SELECTED_ANSWER);
      Answer mostRecentAnswer = answerList.get(0);
      answerText.setText(mostRecentAnswer.getText());
    } else {
      answerList = new ArrayList<>();
    }


    return view;

  }

  class OptionListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
      showOptions();
    }
  }

  private void showOptions(){
    if(this.showOption) return;
    optionLayout = this.getView().findViewById(R.id.mcq_options_view);
    questionContent.getId();
    String[] mcq_option =  super.mcq_notes;
    TextView textView = new TextView(this.getContext());
    if(!nullCheckAndLength[2].equals("MCQ")) {
      mcqType = false;
      textView.setText("(Select One Option Please)");
    }
    else {
      mcqType = true;
      textView.setText("(Select all apply)");
    }
    optionLayout.addView(textView);

    for(String i : mcq_option) {
      CheckBox option = new CheckBox(this.getContext());
      option.setText(i);
      option.setTextSize(20);
      storedOptions.add(option);
      optionLayout.addView(option);
    }

    this.showOption = true;
  }

  @Override
  boolean validateEntry() {
    for(CheckBox box : storedOptions) {
      if(box.isChecked()) count++;
    }
    System.out.println("the select count is: "+ count);
    if(!nullCheckAndLength[2].equals("MCQ")) {
      if(count > 1) {
        Toast.makeText(this.getActivity(), "Please looking at require for MCQ", Toast.LENGTH_SHORT).show();
        count = 0;
        return false;
      }
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


    String answerText = "";

    for(CheckBox box: storedOptions) {
      if(box.isChecked()) {
        answerText+=box.getText();
        answerText += ",";
      }
    }

    answer.setQuestionId(questionContent.getQuestionId());
    answer.setQuestionnaireId(questionContent.getQuestionnaireId());
    answer.setSessionId(((Session) getArguments().getSerializable(SELECTED_SESSION)).getId());
    answer.setText(answerText);
    answer.setVersion(version++);

    DatabaseHelper.ANSWER_TABLE.insert(answer);
  }
}
