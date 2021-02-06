package edu.buffalo.cse.ubcollecting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.Collections;

import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Loop;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.tables.Table;
import edu.buffalo.cse.ubcollecting.ui.AddQuestionsActivity;
import edu.buffalo.cse.ubcollecting.ui.UiUtils;


import static android.view.View.GONE;
import static edu.buffalo.cse.ubcollecting.app.App.getContext;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_LANG_VERSION_TABLE;
import static edu.buffalo.cse.ubcollecting.data.tables.QuestionnaireTable.KEY_ID;
import static edu.buffalo.cse.ubcollecting.data.tables.Table.EXTRA_MODEL;
import static edu.buffalo.cse.ubcollecting.ui.AddQuestionsActivity.EXTRA_QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.QuestionnaireQuestionsFragment.EXTRA_PARENT_QC;
import static edu.buffalo.cse.ubcollecting.ui.QuestionnaireQuestionsFragment.EXTRA_PARENT_QC_ID;
import static edu.buffalo.cse.ubcollecting.ui.QuestionnaireQuestionsFragment.IS_LOOP_QUESTION;
import static edu.buffalo.cse.ubcollecting.ui.QuestionnaireQuestionsFragment.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.QuestionnaireQuestionsFragment.RESULT_ADD_QUESTIONS;

public class LoopActivity  extends AppCompatActivity {
    private static final String TAG = PersonActivity.class.getSimpleName().toString();

    private Button addSubQuestionsButton;
    private DragSortListView subQuestionsListView;
    private SubQuestionAdapter subQuestionAdapter;
    private QuestionnaireContent parentQC;
    private ArrayList<QuestionnaireContent> loopContent;
    private Button submitButton;
    private Button updateButton;

    @SuppressLint("WrongConstant")
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop);

        parentQC = (QuestionnaireContent) getIntent().getSerializableExtra(EXTRA_PARENT_QC);

        loopContent = (ArrayList<QuestionnaireContent>) getIntent().getSerializableExtra(EXTRA_QUESTIONNAIRE_CONTENT);





        addSubQuestionsButton = findViewById(R.id.add_subquestions_button);


        addSubQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selection = QUESTIONNAIRE_CONTENT_TABLE.KEY_PARENT_QUESTIONNAIRE_CONTENT + "= ?";
                String [] selectionArgs = {parentQC.getId()};
                ArrayList<QuestionnaireContent> currentLoopContent = QUESTIONNAIRE_CONTENT_TABLE.getAll(selection, selectionArgs, null);
                Intent intent = AddQuestionsActivity.newIntent(getContext(),parentQC.getQuestionnaireId(), currentLoopContent ) ;
                intent.putExtra(QUESTIONNAIRE_CONTENT,loopContent);

                intent.putExtra(EXTRA_MODEL, parentQC);


                intent.putExtra(IS_LOOP_QUESTION, true);



                startActivityForResult(intent, RESULT_ADD_QUESTIONS);


            }
        });

        subQuestionsListView = findViewById(R.id.sub_question_list_view);
        subQuestionAdapter = new SubQuestionAdapter(this, loopContent );
        subQuestionsListView.setAdapter(subQuestionAdapter);
        subQuestionsListView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from > to) {
                    int temp = from;
                    from = to;
                    to = temp;
                }
                QuestionnaireContent fromContent = loopContent.get(from);
                QuestionnaireContent toContent = loopContent.get(to);

                toContent.setQuestionOrder(from + 1);
                fromContent.setQuestionOrder(to + 1);
                Collections.sort(loopContent);

                subQuestionAdapter.notifyDataSetChanged();
            }
        });
        submitButton = findViewById(R.id.loop_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra(EXTRA_QUESTIONNAIRE_CONTENT, loopContent);
                data.putExtra(EXTRA_PARENT_QC_ID, parentQC.getId());
                setResult(RESULT_OK, data);
                finish();
            }
        });
        updateButton = findViewById(R.id.loop_update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra(EXTRA_QUESTIONNAIRE_CONTENT, loopContent);
                data.putExtra(EXTRA_PARENT_QC, parentQC);
                setResult(RESULT_OK, data);
                finish();
            }
        });

    }






    private class SubQuestionAdapter extends ArrayAdapter <QuestionnaireContent> {

        public SubQuestionAdapter(@NonNull Context context, ArrayList<QuestionnaireContent> questionnaireContent) {
            super(context, 0 , questionnaireContent);
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            QuestionnaireContent content = loopContent.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.numbered_list_item_view, parent, false);
            }
            TextView numberView = convertView.findViewById(R.id.numbered_list_item_number_view);

            numberView.setText(Integer.toString(position+1));
            ImageView loopButton = convertView.findViewById(R.id.numbered_list_item_loop_button);
            loopButton.setVisibility(GONE);
            content.setQuestionOrder(position+1);

            TextView textView = convertView.findViewById(R.id.numbered_list_item_text_view);
            QuestionLangVersion question = QUESTION_LANG_VERSION_TABLE.getQuestionTextInEnglish(content.getQuestionId());
            textView.setText(question.getIdentifier());






            return convertView;
        }



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
            loopContent.clear();
            loopContent.addAll(serializableObject);
            subQuestionAdapter.notifyDataSetChanged();

        }
    }

    private void handleQuestionnaireContentUi() {
        subQuestionAdapter.notifyDataSetChanged();

        UiUtils.setDynamicHeight(subQuestionsListView);

        if (loopContent.size() > 0) {
            subQuestionsListView.setVisibility(View.VISIBLE);
        } else {
            subQuestionsListView.setVisibility(GONE);
        }
    }


    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, LoopActivity.class);
        return i;
    }
}
