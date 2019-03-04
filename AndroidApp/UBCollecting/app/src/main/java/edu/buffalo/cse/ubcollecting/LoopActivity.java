package edu.buffalo.cse.ubcollecting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Arrays;
import java.util.Collections;

import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Loop;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.tables.LoopTable;
import edu.buffalo.cse.ubcollecting.data.tables.Table;
import edu.buffalo.cse.ubcollecting.ui.AddQuestionsActivity;
import edu.buffalo.cse.ubcollecting.ui.UiUtils;
import edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.LOOP_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE;
import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTION_LANG_VERSION_TABLE;
import static edu.buffalo.cse.ubcollecting.data.tables.QuestionnaireTable.KEY_ID;
import static edu.buffalo.cse.ubcollecting.ui.AddQuestionsActivity.EXTRA_QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.QuestionnaireQuestionsFragment.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.QuestionnaireQuestionsFragment.RESULT_ADD_QUESTIONS;

public class LoopActivity extends EntryActivity<Loop> {
    private static final String TAG = PersonActivity.class.getSimpleName().toString();

    private EditText iterationsField;
    private Button addSubQuestionsButton;
    private DragSortListView subQuestionsListView;
    private SubQuestionAdapter subQuestionAdapter;
    private  QuestionnaireContent questionnaireContent;
    private Button submitButton;
    private Button updateButton;
    private ArrayList<QuestionnaireContent> loopContent;

    @SuppressLint("WrongConstant")
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop);

        questionnaireContent = (QuestionnaireContent) getIntent().getSerializableExtra(QUESTIONNAIRE_CONTENT);


        iterationsField = findViewById(R.id.loop_iterations_field);
        String selection;
        String [] selectionArgs;
        if(getIntent().getFlags() == Table.FLAG_EDIT_ENTRY){
            entry = getEntry(getIntent());
            setUI(entry);
             selection = QUESTIONNAIRE_CONTENT_TABLE.KEY_QUESTIONNAIRE_ID + " =? and  " + QUESTIONNAIRE_CONTENT_TABLE.KEY_QUESTION_ORDER + " BETWEEN ? AND ? ";
             selectionArgs = new String[]{questionnaireContent.getQuestionnaireId(), String.valueOf(entry.getStartIndex()), String.valueOf(entry.getEndIndex())};
            Log.i("LOGARITHM", entry.getStartIndex()+" "+entry.getEndIndex());
            loopContent = DatabaseHelper.QUESTIONNAIRE_CONTENT_TABLE.getAll(selection,selectionArgs, null );
        }
        else{
            entry = new Loop();
            loopContent = new ArrayList<QuestionnaireContent>();
        }




        addSubQuestionsButton = findViewById(R.id.add_subquestions_button);


        selection = KEY_ID + " = ? ";
        String [] selectionArguments = {questionnaireContent.getQuestionnaireId()};

        final Questionnaire questionnaire = DatabaseHelper.QUESTIONNAIRE_TABLE.getAll(selection, selectionArguments, null).get(0);
        addSubQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = AddQuestionsActivity.newIntent(view.getContext(), questionnaire, loopContent);
                startActivityForResult(i, RESULT_ADD_QUESTIONS);
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
        submitButton.setOnClickListener(new LoopSubmitOnClickListener());
        updateButton = findViewById(R.id.loop_update_button);
        updateButton.setOnClickListener(new LoopUpdateOnClickListener());

    }
    @Override
    void setUI(Loop entry) {
        Log.i("LOGARITHM", "UI SHOULD BE BEING SET");
        iterationsField.setText(entry.getIterations());

    }

    @Override
    void setEntryByUI() {
        entry.setIterations(iterationsField.getText().toString());
        entry.setStartIndex(String.valueOf(questionnaireContent.getQuestionOrder()));
        Log.i("LOGARITHM", entry.getStartIndex());
        entry.setQuestionnaireId(questionnaireContent.getQuestionnaireId());
        entry.setEndIndex(String.valueOf(entry.getStartIndex())+ loopContent.size());


    }

    @Override
    boolean isValidEntry() {
        return true;
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
            handleQuestionnaireContentUi();
        }
    }

    private void handleQuestionnaireContentUi() {
        subQuestionAdapter.notifyDataSetChanged();

        UiUtils.setDynamicHeight(subQuestionsListView);

        if (loopContent.size() > 0) {
            subQuestionsListView.setVisibility(View.VISIBLE);
        } else {
            subQuestionsListView.setVisibility(View.GONE);
        }
    }
    private class LoopUpdateOnClickListener extends UpdateButtonOnClickListener{

        public LoopUpdateOnClickListener() {
            super(LOOP_TABLE);
        }

    }
    private class LoopSubmitOnClickListener extends SubmitButtonOnClickListener{


        public LoopSubmitOnClickListener() {
            super(LOOP_TABLE);
        }

        @Override
        public void onClick(View view) {
            setEntryByUI();
            if (isValidEntry()) {
                table.insert(entry);
                setEntryResult(entry);
                for (QuestionnaireContent content : loopContent) {
                    QUESTIONNAIRE_CONTENT_TABLE.insert(content);
                }
                finish();
            }

        }
    }




    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, LoopActivity.class);
        return i;
    }
}
