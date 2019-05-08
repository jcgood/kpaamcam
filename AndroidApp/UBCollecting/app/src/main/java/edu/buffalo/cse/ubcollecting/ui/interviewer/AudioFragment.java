package edu.buffalo.cse.ubcollecting.ui.interviewer;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UpdateAnswerActivity.SELECTED_QUESTION;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectQuestionnaireActivity.SELECTED_QUESTIONNAIRE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.QuestionFragment.SELECTED_ANSWER;

/**
 * A fragment to represent a question to be taken in a questionnaire.
 */

public class AudioFragment extends Fragment{

    public final static String SELECTED_ANSWER = "selected answer";
    public final static String TAG=AudioFragment.class.getName();


    private QuestionnaireContent questionContent;
    private Spinner questionLangSpinner;
    private TextView questionText;
    private Button nextQuestion;
    private Button skipQuestion;
    private Button saveAndExitQuestion;
    private TextView answerListHeading;
    private ListView previousAnswerList;
    private ArrayAdapter listAdapter;
    private HashMap<Language,QuestionLangVersion> questionTexts;
    private ArrayList<Language> questionLanguages;
    private ArrayAdapter<Language> questionLanguagesAdapter;
    private QuestionManager questionManager;
    private Answer answer;
    private String mCurrentPath;
    private Button takeAudio;
    private Button viewAudio;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private boolean record;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private boolean permission;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        answer = new Answer();
        takeAudio=view.findViewById(R.id.answer_instructions);
        viewAudio=view.findViewById(R.id.view);
        viewAudio.setEnabled(false);
        record=false;
        permission=false;

        requestAudioPermissions();


        takeAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!permission){
                    return;
                }
                if(!record){
                    Log.d(TAG,"start record");

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String audioFile = "MP3_" + timeStamp + "_";

                    outputFile = getActivity().getExternalCacheDir().getAbsolutePath() + "/"+audioFile+".3gp";
                    myAudioRecorder = new MediaRecorder();
                    myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    myAudioRecorder.setOutputFile(outputFile);

                    try {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                    } catch (Exception ise) {
                        Log.e("AudioFragment","Exception: "+ise.toString());
                    }
                    Toast.makeText(getContext(), "Recording started", Toast.LENGTH_LONG).show();
                    record=true;
                    takeAudio.setText("STOP RECORDING");
                    viewAudio.setEnabled(false);
                }
                else{
                    Log.d(TAG,"Stop record");

                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                    Toast.makeText(getContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
                    takeAudio.setText("START RECORDING");
                    viewAudio.setEnabled(true);
                    record=false;
                }


            }
        });

        viewAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!permission)return;

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(getContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("AudioFragment","Play Audio Exception: "+e.toString());
                }
            }
        });

        questionText = view.findViewById(R.id.question_text);
        questionContent = (QuestionnaireContent) getArguments().getSerializable(QUESTIONNAIRE_CONTENT);
        questionTexts = DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(questionContent.getQuestionId());
        questionLanguages = new ArrayList<>();
        questionLanguages.addAll(questionTexts.keySet());

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        questionLangSpinner = view.findViewById(R.id.question_language_spinner);
        questionLanguagesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, questionLanguages);
        questionLangSpinner.setAdapter(questionLanguagesAdapter);
        questionLangSpinner.setOnItemSelectedListener(new AudioFragment.LanguageOnItemSelectedListener());
        questionLangSpinner.setSelection(getEnglishQuestionIndex());

        nextQuestion = view.findViewById(R.id.next_question);
        skipQuestion = view.findViewById(R.id.skip_question);
        saveAndExitQuestion = view.findViewById(R.id.saveandexit_question);
        if(questionManager.isLastQuestion()){
            nextQuestion.setText("Finish");
        }
        nextQuestion.setOnClickListener(new AudioFragment.NextQuestionOnClickListener());
        skipQuestion.setOnClickListener(new AudioFragment.SkipQuestionOnClickListener());
        saveAndExitQuestion.setOnClickListener(new AudioFragment.SaveAndExitQuestionOnClickListener());
        if(getArguments().containsKey(SELECTED_ANSWER)){
            ArrayList<Answer> answerList = (ArrayList<Answer>) getArguments().getSerializable(SELECTED_ANSWER);
            previousAnswerList = view.findViewById(R.id.previous_answers_list);
            answerListHeading = view.findViewById(R.id.answer_list_header);
            answerListHeading.setVisibility(View.VISIBLE);
            listAdapter = new ListAdapter(getContext(), answerList);
            previousAnswerList.setAdapter(listAdapter);
        }
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        String questionId = (String) data.getSerializableExtra(SELECTED_QUESTION);
        String questionnaireId = (String) data.getSerializableExtra(SELECTED_QUESTIONNAIRE);
        updateAnswerList(questionId, questionnaireId);
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
        answer.setQuestionId(questionContent.getQuestionId());
        answer.setQuestionnaireId(questionContent.getQuestionnaireId());
        answer.setText(outputFile);
        answer.setSessionId(((Session) getArguments().getSerializable(SELECTED_SESSION)).getId());
        DatabaseHelper.ANSWER_TABLE.insert(answer);
    }

    protected boolean validateEntry() {
        boolean valid = true;

        if (outputFile.isEmpty()){
            Toast.makeText(this.getActivity(), "Please Fill in All Required Fields", Toast.LENGTH_SHORT).show();
            valid=false;
        }

        return valid;

    }
    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(getContext(), "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            permission=true;
        }
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    permission=true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}