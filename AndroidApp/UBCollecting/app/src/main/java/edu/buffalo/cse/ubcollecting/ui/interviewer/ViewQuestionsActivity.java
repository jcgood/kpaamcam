package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.tables.AnswerTable;

import static edu.buffalo.cse.ubcollecting.SessionActivity.getSession;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.getQuestionnaire;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectQuestionnaireActivity.SELECTED_QUESTIONNAIRE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

public class ViewQuestionsActivity extends AppCompatActivity {

    private TextView questionnaireTitle;
    private RecyclerView questionView;
    private EntryAdapter entryAdapter;

    private ArrayList<QuestionnaireContent> questionnaire;

    public static final String QUESTION_INDEX = "Question Index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_questions);


        questionnaireTitle = (TextView) findViewById(R.id.questionnaireName);
        questionView =(RecyclerView) findViewById(R.id.questionList);
        questionView.setLayoutManager(new LinearLayoutManager(this));


        questionnaire = DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE.getAllQuestions(getQuestionnaire(getIntent()).getId());
        questionnaireTitle.setText(getQuestionnaire(getIntent()).name);


        ArrayList<String> listofQuestions = new ArrayList<>();

        entryAdapter = new ViewQuestionsActivity.EntryAdapter(questionnaire);
        questionView.setAdapter(entryAdapter);


    }


    private class EntryHolder extends RecyclerView.ViewHolder {


        private Button selectButton;


        public EntryHolder(View view) {
            super(view);
            selectButton = view.findViewById(R.id.entry_list_select_button);

        }

        public void bindEntry(final QuestionnaireContent questionnaireContent, final int position) {
            String questionId = questionnaireContent.questionId;
            String questionText = DatabaseHelper.QUESTION_TABLE.findById(questionId).getDisplayText();
            selectButton.setText(questionText);

            // GET ANSWER IF IT EXISTS
            String selection = AnswerTable.KEY_QUESTION_ID +  " = ?  AND "
                               +AnswerTable.KEY_QUESTIONNAIRE_ID + " = ? ";
            String [] selectionArgs = {questionId, getQuestionnaire(getIntent()).getId()};
            final ArrayList<Answer> answerList = DatabaseHelper.ANSWER_TABLE.getAll(selection, selectionArgs, null);
            if(answerList.size()!=0){

                selectButton.setTextColor(Color.rgb(22, 135, 14));
            }

            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent k = TakeQuestionnaireActivity.newIntent(ViewQuestionsActivity.this);
                    k.putExtra(SELECTED_SESSION,getSession(getIntent()));
                    k.putExtra(SELECTED_QUESTIONNAIRE, getQuestionnaire(getIntent()));
                    k.putExtra(QUESTION_INDEX, position);
                    startActivity(k);
                    finish();
                }
            });
        }
    }


    private class EntryAdapter extends RecyclerView.Adapter<ViewQuestionsActivity.EntryHolder> {

        private List<QuestionnaireContent> entryList;

        public EntryAdapter(List<QuestionnaireContent> entryList) {
            this.entryList = entryList;
        }


        @Override
        public ViewQuestionsActivity.EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater                    .inflate(R.layout.questionnaire_item_view, parent, false);
            return new ViewQuestionsActivity.EntryHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewQuestionsActivity.EntryHolder holder, int position) {
            QuestionnaireContent entry = entryList.get(position);
            holder.bindEntry(entry, position);
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }
    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, ViewQuestionsActivity.class);
        return i;
    }

}
