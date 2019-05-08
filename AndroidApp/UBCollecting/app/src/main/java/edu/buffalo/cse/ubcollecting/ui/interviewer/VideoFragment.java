package edu.buffalo.cse.ubcollecting.ui.interviewer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.Session;
import edu.buffalo.cse.ubcollecting.data.models.SessionQuestion;
import edu.buffalo.cse.ubcollecting.data.models.SessionQuestionnaire;
import edu.buffalo.cse.ubcollecting.data.tables.AnswerTable;
import edu.buffalo.cse.ubcollecting.data.tables.SessionQuestionnaireTable;
import edu.buffalo.cse.ubcollecting.ui.EntryOnItemSelectedListener;
import edu.buffalo.cse.ubcollecting.ui.QuestionManager;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UpdateAnswerActivity.SELECTED_QUESTION;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectQuestionnaireActivity.SELECTED_QUESTIONNAIRE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.QuestionFragment.SELECTED_ANSWER;

/**
 * A fragment to represent a question to be taken in a questionnaire.
 */

public class VideoFragment extends Fragment{

    public final static String TAG=VideoFragment.class.getName();

    public final static String SELECTED_ANSWER = "selected answer";

    private QuestionnaireContent questionContent;
    private Spinner questionLangSpinner;
    private TextView questionText;
    private Button nextQuestion;
    private Button skipQuestion;
    private Button saveAndExitQuestion;
    private TextView answerListHeading;
    private ListView previousAnswerList;
    private ArrayList<Answer> answerList;
    private ArrayAdapter listAdapter;
    private HashMap<Language,QuestionLangVersion> questionTexts;
    private ArrayList<Language> questionLanguages;
    private ArrayAdapter<Language> questionLanguagesAdapter;
    private QuestionManager questionManager;
    private Answer answer;
    private String type;
    private Button takeVideo;
    private String mCurrentPath;
    private Button viewVideo;
    static final int REQUEST_VIDEO_CAPTURE = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder newBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newBuilder.build());
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        viewVideo=view.findViewById(R.id.view);
        viewVideo.setVisibility(View.INVISIBLE);
        answer = new Answer();
        takeVideo=view.findViewById(R.id.answer_instructions);
        questionText = view.findViewById(R.id.question_text);
        questionContent = (QuestionnaireContent) getArguments().getSerializable(QUESTIONNAIRE_CONTENT);
        questionTexts = DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(questionContent.getQuestionId());
        questionLanguages = new ArrayList<>();
        questionLanguages.addAll(questionTexts.keySet());

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        questionLangSpinner = view.findViewById(R.id.question_language_spinner);
        questionLanguagesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, questionLanguages);
        questionLangSpinner.setAdapter(questionLanguagesAdapter);
        questionLangSpinner.setOnItemSelectedListener(new VideoFragment.LanguageOnItemSelectedListener());
        questionLangSpinner.setSelection(getEnglishQuestionIndex());

        nextQuestion = view.findViewById(R.id.next_question);
        skipQuestion = view.findViewById(R.id.skip_question);
        saveAndExitQuestion = view.findViewById(R.id.saveandexit_question);
        if (questionManager.isLastQuestion()) {
            nextQuestion.setText("Finish");
        }
        nextQuestion.setOnClickListener(new VideoFragment.NextQuestionOnClickListener());
        skipQuestion.setOnClickListener(new VideoFragment.SkipQuestionOnClickListener());
        saveAndExitQuestion.setOnClickListener(new VideoFragment.SaveAndExitQuestionOnClickListener());

        if (getArguments().containsKey(SELECTED_ANSWER)) {
            answerList = (ArrayList<Answer>) getArguments().getSerializable(SELECTED_ANSWER);
            previousAnswerList = view.findViewById(R.id.previous_answers_list);
            answerListHeading = view.findViewById(R.id.answer_list_header);
            answerListHeading.setVisibility(View.VISIBLE);
            listAdapter = new ListAdapter(getContext(), answerList);
//            previousAnswerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    String item = (String) adapterView.getItemAtPosition(i);
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    Log.d(TAG,"Item pressed is "+item);
//                    intent.setDataAndType(Uri.parse(item), "image/*");
//                    startActivity(intent);
//                }
//            });
            previousAnswerList.setAdapter(listAdapter);
        } else {
            answerList = new ArrayList<>();
        }

        takeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG,"enetred take photo");

                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i("VideoFragment", "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, REQUEST_VIDEO_CAPTURE);
                    }
                }

            }
        });

        viewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(!mCurrentPath.isEmpty()){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(mCurrentPath), "video/*");
                        startActivity(intent);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });


        return view;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFile = "MP4_" + timeStamp + "_";

        File image = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), videoFile);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPath = "file:" + image.getAbsolutePath();
        Log.d("VideoFrag","file location: "+mCurrentPath);
        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            try {
                //mImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                viewVideo.setVisibility(View.VISIBLE);
                //mImageView.setImageBitmap(mImageBitmap);
            } catch (Exception e) {
                Log.e(TAG,e.toString());
            }
            return;
        }

        if(data!=null)
        {
            String questionId = (String) data.getSerializableExtra(SELECTED_QUESTION);
            String questionnaireId = (String) data.getSerializableExtra(SELECTED_QUESTIONNAIRE);
            updateAnswerList(questionId, questionnaireId);
        }


    }

    private class ListAdapter extends ArrayAdapter<Answer> {
        ArrayList<Answer> answerList;
        private ListAdapter(Context context, ArrayList<Answer> answerList){
            super(context, 0, answerList);
            this.answerList = answerList;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            final Answer answer = answerList.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_list_item_view, parent, false);
            }
            TextView answerContents = (TextView) convertView.findViewById(R.id.entry_list_text_view);
            answerContents.setText(answer.getText());
            ImageButton updateAnswer = (ImageButton) convertView.findViewById(R.id.entry_list_edit_button);
            ImageButton deleteAnswer = (ImageButton) convertView.findViewById(R.id.entry_list_delete_button);
            updateAnswer.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = UpdateAnswerActivity.newIntent(getActivity());
                    intent.putExtra(SELECTED_ANSWER, answer);
                    startActivityForResult(intent,1);

                }
            });

            deleteAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHelper.ANSWER_TABLE.delete(answer.getId());
                    updateAnswerList(answer.getQuestionId(), answer.getQuestionnaireId());
                }
            });

            return convertView;
        }
    }

    private void updateAnswerList(String questionId, String questionnaireId){
        String selection = AnswerTable.KEY_QUESTION_ID +  " = ?  AND "
                +AnswerTable.KEY_QUESTIONNAIRE_ID + " = ? ";
        String [] selectionArgs = {questionId, questionnaireId};
        final ArrayList<Answer> answerList = DatabaseHelper.ANSWER_TABLE.getAll(selection, selectionArgs, null);
        listAdapter.clear();
        listAdapter.addAll(answerList);
        listAdapter.notifyDataSetChanged();
    }

    public void onAttach(Context context){
        super.onAttach(context);
        questionManager = (QuestionManager) context;
        int x=0;
    }

    private int getEnglishQuestionIndex(){
        for (int i = 0; i<questionLanguages.size(); i++){
            if (questionLanguages.get(i).getName().toLowerCase().equals("english")){
                return i;
            }
        }
        return 0;
    }

    private class LanguageOnItemSelectedListener extends EntryOnItemSelectedListener<Language> {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            super.onItemSelected(parent, view, position, id);
            Language language = (Language) questionLangSpinner.getSelectedItem();
            questionText.setText(questionTexts.get(language).getQuestionText());
        }
    }

    private class NextQuestionOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(validateEntry()){
                submitTextAnswer();
                questionManager.getNextQuestion();
            }
        }
    }

    private class SkipQuestionOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Toast.makeText(getContext(), "Question Skipped", Toast.LENGTH_SHORT).show();
            questionManager.getNextQuestion();
        }
    }

    private class SaveAndExitQuestionOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            questionManager.saveAndQuitQuestionnaire(questionContent);
        }
    }

    private void submitTextAnswer(){
        double version = 0;
        if (!answerList.isEmpty()) {
            Answer recentAnswer = answerList.get(0);
            version = recentAnswer.getVersion();
        }
        answer.setQuestionId(questionContent.getQuestionId());
        answer.setQuestionnaireId(questionContent.getQuestionnaireId());
        answer.setText(mCurrentPath);
        answer.setSessionId(((Session) getArguments().getSerializable(SELECTED_SESSION)).getId());
        answer.setVersion(version+1);
        DatabaseHelper.ANSWER_TABLE.insert(answer);
    }

    protected boolean validateEntry() {

        boolean valid = true;

        if (mCurrentPath.isEmpty()){
            Toast.makeText(this.getActivity(), "Please Take a Photo", Toast.LENGTH_SHORT).show();
            valid=false;
        }

        return valid;

    }

}