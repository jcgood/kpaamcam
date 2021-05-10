package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.FireBaseCloudHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.Session;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.LOOP_QUESTION_ID;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

public class LoopFragment extends QuestionFragment {

    private RecyclerView answerViewList;
    private Button addQuestionsButton;
    private Button mSaveAndQuitButton;
    private EntryAdapter entryAdapter;
    private QuestionnaireContent questionnaireContent;
    private ArrayList<Answer> answerList;
    private Session session;

    private String mLoopQuestionId;

    private final FireBaseCloudHelper fireBaseCloudHelper = new FireBaseCloudHelper(this.getContext());
    private final String TAG = "LoopFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        answerViewList = view.findViewById(R.id.answer_list);

        super.setIsLoopQuestion(true);
        mLoopQuestionId = (String) getArguments().getSerializable(LOOP_QUESTION_ID);
        questionnaireContent = (QuestionnaireContent) getArguments().getSerializable(QUESTIONNAIRE_CONTENT);
        session = (Session) getArguments().getSerializable(SELECTED_SESSION);
        answerList = new ArrayList<>();

        entryAdapter = new LoopFragment.EntryAdapter();
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
            /* INSERT */
            try {
                fireBaseCloudHelper.insert(DatabaseHelper.ANSWER_TABLE, answer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                Log.i(TAG, "Could not access server database (Firebase)");
                e.printStackTrace();
            }
            DatabaseHelper.ANSWER_TABLE.insert(answer);
        }
    }

    private class EntryHolder extends RecyclerView.ViewHolder {


        private Button deleteButton;
        private RelativeLayout layout;

        EntryHolder(View view) {
            super(view);
            deleteButton = view.findViewById(R.id.entry_list_delete_button);
            layout = view.findViewById(R.id.list_answer_layout);
        }

        void bindEntry(EditText entry) {
            if (entry.getParent() != null) {
                ((ViewGroup) entry.getParent()).removeView(entry);
            }

            entry.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layout.addView(entry);

        }
    }

    private class EntryAdapter extends RecyclerView.Adapter<LoopFragment.EntryHolder> {


        ArrayList<EditText> list;

        EntryAdapter() {
            ArrayList<Answer> previousAnswers = DatabaseHelper.ANSWER_TABLE.getAnswers(mLoopQuestionId, questionnaireContent.getQuestionnaireId());
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

        void addText() {
            list.add(new EditText(getContext()));
            Answer answer = new Answer();
            answer.setQuestionId(mLoopQuestionId);
            answer.setQuestionnaireId(questionnaireContent.getQuestionnaireId());
            answer.setSessionId(session.getId());
            answerList.add(answer);
            this.notifyDataSetChanged();
        }
        void addText(String s){
            EditText editText = new EditText(getContext());
            editText.setText(s);
            list.add(editText);
            this.notifyDataSetChanged();
        }

        ArrayList<EditText> getAnswerList() {
            return list;
        }

        @Override
        public LoopFragment.EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.list_answer_entry_view, parent, false);
            return new LoopFragment.EntryHolder(view);
        }

        @Override
        public void onBindViewHolder(LoopFragment.EntryHolder holder, int position) {
            EditText entry = list.get(position);
            holder.bindEntry(entry);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}