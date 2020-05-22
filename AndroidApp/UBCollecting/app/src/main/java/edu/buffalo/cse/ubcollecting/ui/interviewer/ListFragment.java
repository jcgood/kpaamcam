package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.Session;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.LIST_QUESTION_ID;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectQuestionnaireActivity.SELECTED_QUESTIONNAIRE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

public class ListFragment extends QuestionFragment {

    private RecyclerView answerViewList;
    private Button addQuestionsButton;
    private Button mSaveAndQuitButton;
    private EntryAdapter entryAdapter;
    private String questionnaireId;
    private QuestionnaireContent questionnaireContent;
    private ArrayList<Answer> answerList;
    private Session session;

    private String mListQuestionId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        answerViewList = view.findViewById(R.id.answer_list);

        super.setIsListQuestion(true);
        mListQuestionId = (String) getArguments().getSerializable(LIST_QUESTION_ID);
        questionnaireId = (String) getArguments().getSerializable(SELECTED_QUESTIONNAIRE);
        questionnaireContent = (QuestionnaireContent) getArguments().getSerializable(QUESTIONNAIRE_CONTENT);
        session = (Session) getArguments().getSerializable(SELECTED_SESSION);
        answerList = new ArrayList<>();

        entryAdapter = new ListFragment.EntryAdapter();
        answerViewList.setLayoutManager(new LinearLayoutManager(getContext()));
        answerViewList.setAdapter(entryAdapter);




        addQuestionsButton = view.findViewById(R.id.list_add_answer);
        addQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                entryAdapter.addText();

            }
        });

        mSaveAndQuitButton = view.findViewById(R.id.save_and_quit_question);
        mSaveAndQuitButton.setOnClickListener(new SaveAndExitQuestionOnClickListener());

        questionManager.isLastQuestion();
        return view;
    }


    @Override
    boolean validateEntry() {
        boolean valid = true;
        for(EditText answer: entryAdapter.getAnswerList()){
            if (answer.getText().toString().isEmpty()){
                valid = false;
                answer.setError("A Text Answer is Required");
            }
        }


        if (!valid){
            Toast.makeText(this.getActivity(), "Please Fill in All Required Fields", Toast.LENGTH_SHORT).show();
        }

        return valid;

    }

    @Override
    public void submitAnswer() {
        ArrayList<EditText> answerTextList = entryAdapter.getAnswerList();
        for (int i=0; i<answerList.size();i++) {
            Answer answer = answerList.get(i);
           if(answer.getText()==null){
               answer.setText(answerTextList.get(i).getText().toString());

           }
            DatabaseHelper.ANSWER_TABLE.insert(answer);

        }
    }

    private class EntryHolder extends RecyclerView.ViewHolder {


        private Button deleteButton;
        private RelativeLayout layout;


        public EntryHolder(View view) {
            super(view);
            deleteButton = view.findViewById(R.id.entry_list_delete_button);
            layout = (RelativeLayout) view.findViewById(R.id.list_answer_layout);


        }


        public void bindEntry(EditText entry, final int position) {

            if (entry.getParent() != null) {
                ((ViewGroup) entry.getParent()).removeView(entry);
            }
            entry.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


            layout.addView(entry);

        }
    }

    private class EntryAdapter extends RecyclerView.Adapter<ListFragment.EntryHolder> {
        ArrayList<EditText> list;

        public EntryAdapter() {
            ArrayList<Answer> previousAnswers = DatabaseHelper.ANSWER_TABLE.getAnswers(mListQuestionId, questionnaireContent.getQuestionnaireId());
            list = new ArrayList<>();
            if(!previousAnswers.isEmpty()){
                for(Answer answer: previousAnswers){
                    answerList.add(answer);
                    addText(answer.getText());
                }
            }
            else{
                addText();

            }

        }

        public void addText() {
            list.add(new EditText(getContext()));
            Answer answer = new Answer();
            answer.setQuestionId(mListQuestionId);
            answer.setQuestionnaireId(questionnaireContent.getQuestionnaireId());
            answer.setSessionId(session.getId());
            answerList.add(answer);
            this.notifyDataSetChanged();
        }
        public void addText(String s){
            EditText editText = new EditText(getContext());
            editText.setText(s);
            list.add(editText);
            this.notifyDataSetChanged();
        }


        public ArrayList<EditText> getAnswerList() {
            return list;
        }

        @Override
        public ListFragment.EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.list_answer_entry_view, parent, false);
            return new ListFragment.EntryHolder(view);
        }

        @Override
        public void onBindViewHolder(ListFragment.EntryHolder holder, int position) {
            EditText entry = list.get(position);
            holder.bindEntry(entry, position);
        }


        @Override
        public int getItemCount() {
            return list.size();
        }
    }

}
