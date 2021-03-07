package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.QuestionOption;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.Session;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectQuestionnaireActivity.SELECTED_QUESTIONNAIRE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

public class ListFragment extends QuestionFragment {

    QuestionnaireContent questionnaireContent;
    //QuestionLangVersion questionLangVersion;
    Session session;
    String questionnaireId;
    ArrayList<Answer> answerList;
    ArrayList<CheckBox> selectedCheckBoxList;

    private Answer initAnswer;
    private static final String TAG = ListFragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout linearView = view.findViewById(R.id.answer_list);

        questionnaireId = (String) getArguments().getSerializable(SELECTED_QUESTIONNAIRE);
        questionnaireContent = (QuestionnaireContent) getArguments().getSerializable(QUESTIONNAIRE_CONTENT);

        //questionLangVersion = DatabaseHelper.QUESTION_LANG_VERSION_TABLE.findById(questionnaireContent.getQuestionId());

        session = (Session) getArguments().getSerializable(SELECTED_SESSION);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        answerList = new ArrayList<>();
        selectedCheckBoxList = new ArrayList<>();
        Button nextQuestion = view.findViewById(R.id.next_question);

        nextQuestion.setOnClickListener(new NextQuestionOnClickListener());
        getPreviousAnswers();
        showCheckBoxList(linearView);

        return view;
    }

    /**
     * Retrieve the most recent answer's(selected checkbox)
     */
    public void getPreviousAnswers() {
        ArrayList<Answer> previousAnswers = DatabaseHelper.ANSWER_TABLE.getMostRecentAnswer(questionnaireContent.getQuestionId(), questionnaireContent.getQuestionnaireId(), initAnswer);
        if (!previousAnswers.isEmpty()) {
            for (Answer answer : previousAnswers) {
                answerList.add(answer);
            }
        }

    }

    /**
     * Convert the List element options to checkbox
     *
     * @param linearLayout
     */
    public void showCheckBoxList(LinearLayout linearLayout) {
        ArrayList<QuestionOption> questionOptions = DatabaseHelper.QUESTION_OPTION_TABLE.getQuestionOptions(questionnaireContent.getQuestionId());

        for (int index = 0; index < questionOptions.size(); index++) {
            String value = questionOptions.get(index).getOptionText().toString();
            final CheckBox cb = new CheckBox(getContext());
            cb.setId(index);
            cb.setText(value);
            cb.setTextSize(20);
            if (!answerList.isEmpty()) {
                for (Answer prevAnswer : answerList) {
                    if (prevAnswer.getText().toString().equals(value))
                        cb.setChecked(true);
                    selectedCheckBoxList.add(cb);
                }
            }
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final LinearLayout linearLayout = (LinearLayout) view.getParent();
                    for (int index = 0; index < linearLayout.getChildCount(); index++) {
                        CheckBox cbView = (CheckBox) linearLayout.getChildAt(index);
                        if (view.getId() == cbView.getId()) {
                            if (cb.isChecked()) {
                                selectedCheckBoxList.add(cb);
                            } else {
                                selectedCheckBoxList.remove(cb);
                            }
                        }
                    }
                }
            });
            linearLayout.addView(cb);
        }
    }

    @Override
    boolean validateEntry() {
        if (selectedCheckBoxList.isEmpty()) {
            Toast.makeText(this.getActivity(), "Please select from the options!", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (CheckBox cb : selectedCheckBoxList) {
            if (cb.isChecked())
                return true;
        }

        return false;
    }

    @Override
    public void submitAnswer() {
        double version = 0;
        //Update the version of the answer in DB
        if (!answerList.isEmpty()) {
            Answer recentAnswer = answerList.get(0);
            version = recentAnswer.getVersion();
        }
        for (CheckBox cb : selectedCheckBoxList) {
            if (cb.isChecked()) {
                String selectedOption = cb.getText().toString();

                Answer answer = new Answer();
                answer.setQuestionnaireId(questionnaireId);
                answer.setSessionId(session.getId());
                answer.setQuestionId(questionnaireContent.getQuestionId());
                answer.setText(selectedOption);
                answer.setVersion(version + 1);
                DatabaseHelper.ANSWER_TABLE.insert(answer);
            }
        }
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