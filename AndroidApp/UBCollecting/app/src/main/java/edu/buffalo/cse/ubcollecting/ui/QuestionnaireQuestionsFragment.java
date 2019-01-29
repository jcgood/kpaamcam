package edu.buffalo.cse.ubcollecting.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.buffalo.cse.ubcollecting.QuestionnaireActivity;
import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_LANG_VERSION_TABLE;
import static edu.buffalo.cse.ubcollecting.ui.AddQuestionsActivity.EXTRA_QUESTIONNAIRE_CONTENT;

public class QuestionnaireQuestionsFragment extends Fragment {
    private QuestionnaireManager questionnaireManager;
    private DragSortListView questionnaireDragView;
    private QuestionnaireQuestionsFragment.QuestionnaireContentAdapter questionnaireContentAdapter;
    private Button addQuestionsButton;
    private ArrayList<QuestionnaireContent> questionnaireContent;
    public static final int RESULT_ADD_QUESTIONS = 1;
    public static final String TAG = QuestionnaireQuestionsFragment.class.getSimpleName();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        questionnaireManager = (QuestionnaireManager) context;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questionnaire_questions, container, false);
        addQuestionsButton = view.findViewById(R.id.add_questions_button);
        addQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = AddQuestionsActivity.newIntent(getContext(), questionnaireManager.getQuestionnaireEntry(), questionnaireContent);
                startActivityForResult(i, RESULT_ADD_QUESTIONS);
            }
        });

        questionnaireDragView = view.findViewById(R.id.questionnaire_question_list_view);

        questionnaireContent = QUESTIONNAIRE_CONTENT_TABLE.getAllQuestions(questionnaireManager.getQuestionnaireEntry().getId());

        for (QuestionnaireContent qc: questionnaireContent){
            Log.i(qc.getQuestionId(),"QUESTION ID");
            Log.i(qc.getQuestionnaireId(),"QUESTIONNAIRE ID");
            Log.i(Integer.toString(qc.getQuestionOrder()),"QUESTIONNAIRE ID");
            Log.i("--","--");
        }

        questionnaireContentAdapter =
                new QuestionnaireContentAdapter(getContext(), questionnaireContent);
        questionnaireDragView.setAdapter(questionnaireContentAdapter);
        questionnaireDragView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from > to) {
                    int temp = from;
                    from = to;
                    to = temp;
                }
                QuestionnaireContent fromContent = questionnaireContent.get(from);
                QuestionnaireContent toContent = questionnaireContent.get(to);

                toContent.setQuestionOrder(from + 1);
                fromContent.setQuestionOrder(to + 1);
                Collections.sort(questionnaireContent);
                Log.i(TAG, Arrays.toString(questionnaireContent.toArray()));

                questionnaireContentAdapter.notifyDataSetChanged();
            }
        });
        handleQuestionnaireContentUi();


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == RESULT_ADD_QUESTIONS) {
            ArrayList<QuestionnaireContent> serializableObject =
                    (ArrayList<QuestionnaireContent>) data.getSerializableExtra(EXTRA_QUESTIONNAIRE_CONTENT);

            Log.i(TAG, "REC: " + Integer.toString(serializableObject.size()));
            questionnaireContent.clear();
            questionnaireContent.addAll(serializableObject);
            handleQuestionnaireContentUi();
        }
    }

    private void handleQuestionnaireContentUi() {
        questionnaireContentAdapter.notifyDataSetChanged();

        UiUtils.setDynamicHeight(questionnaireDragView);

        if (questionnaireContent.size() > 0) {
            questionnaireDragView.setVisibility(View.VISIBLE);
            addQuestionsButton.setText("Edit Questions");
        } else {
            questionnaireDragView.setVisibility(View.GONE);
        }
    }

    public ArrayList<QuestionnaireContent> getQuestionnaireContent(){
        Log.i("QUESTIONNAIRESIZE", String.valueOf(questionnaireContent.size()));
        return questionnaireContent;
    }


    private class QuestionnaireContentAdapter extends ArrayAdapter<QuestionnaireContent> {
        public QuestionnaireContentAdapter(Context context, ArrayList<QuestionnaireContent> questionnaireContent) {
            super(context, 0, questionnaireContent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            QuestionnaireContent content = questionnaireContent.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.numbered_list_item_view, parent, false);
            }
            TextView numberView = convertView.findViewById(R.id.numbered_list_item_number_view);

            numberView.setText(Integer.toString(position+1));
            content.setQuestionOrder(position+1);

            TextView textView = convertView.findViewById(R.id.numbered_list_item_text_view);
            QuestionLangVersion question = QUESTION_LANG_VERSION_TABLE.getQuestionTextInEnglish(content.getQuestionId());
            textView.setText(question.getIdentifier());




            return convertView;
        }
    }


}
