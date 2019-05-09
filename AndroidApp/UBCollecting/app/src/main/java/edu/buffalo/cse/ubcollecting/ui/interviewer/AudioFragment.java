package edu.buffalo.cse.ubcollecting.ui.interviewer;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.Session;
import edu.buffalo.cse.ubcollecting.ui.EntryOnItemSelectedListener;
import edu.buffalo.cse.ubcollecting.ui.QuestionManager;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

/**
 * A fragment to represent a question to be taken in a questionnaire.
 */

public class AudioFragment extends Fragment{

    public final static String SELECTED_ANSWER = "selected answer";
    public final static String TAG = AudioFragment.class.getName();


    private QuestionnaireContent questionContent;
    private Spinner questionLangSpinner;
    private TextView questionText;
    private Button nextQuestion;
    private Button skipQuestion;
    private Button saveAndExitQuestion;
    private ArrayList<Answer> answerList;
    private HashMap<Language,QuestionLangVersion> questionTexts;
    private ArrayList<Language> questionLanguages;
    private ArrayAdapter<Language> questionLanguagesAdapter;
    private QuestionManager questionManager;
    private Answer answer;
    private Button takeAudio;
    private Button viewAudio;
    private MediaRecorder myAudioRecorder;
    private String mCurrentPath;
    private boolean recording;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private boolean permissionGranted;
    private TextView timer;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder newBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newBuilder.build());

        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        answer = new Answer();
        takeAudio = view.findViewById(R.id.answer_instructions);
        viewAudio = view.findViewById(R.id.view);
        recording = false;
        permissionGranted = false;
        timer=view.findViewById(R.id.timer);

        requestAudioPermissions();

        takeAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!permissionGranted){
                    Log.e(TAG,"Permisiion Issue");
                    return;
                }
                if(!recording){
                    Log.d(TAG,"start recording");

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String audioFile = "MP3_" + timeStamp + "_";

                    mCurrentPath = getActivity().getExternalCacheDir().getAbsolutePath() + "/"+audioFile+".3gp";
                    myAudioRecorder = new MediaRecorder();
                    myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    myAudioRecorder.setOutputFile(mCurrentPath);

                    try {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                    } catch (Exception ise) {
                        Log.e("AudioFragment","Exception: "+ise.toString());
                    }
                    timer.setVisibility(View.VISIBLE);

                    recording =true;
                    takeAudio.setText("STOP RECORDING");
                    viewAudio.setVisibility(View.INVISIBLE);

                }
                else{
                    Log.d(TAG,"Stop recording");
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                    timer.setVisibility(View.INVISIBLE);

                    takeAudio.setText("START RECORDING");
                    viewAudio.setVisibility(View.VISIBLE);
                    recording = false;
                }


            }
        });

        viewAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!permissionGranted)return;

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(mCurrentPath);
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
        if (getArguments().containsKey(SELECTED_ANSWER)) {
            answerList = (ArrayList<Answer>) getArguments().getSerializable(SELECTED_ANSWER);
            Answer prevAnswer = answerList.get(0);
            mCurrentPath = prevAnswer.getText();
            viewAudio.setVisibility(View.VISIBLE);

        } else {
            viewAudio.setVisibility(View.INVISIBLE);
            answerList = new ArrayList<>();
        }
        return view;
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

            if(validateEntry()){
                submitTextAnswer();
                questionManager.saveAndQuitQuestionnaire(questionContent);
            }
        }
    }

    private void submitTextAnswer() {
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
            Toast.makeText(this.getActivity(), "Please Fill in All Required Fields", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;

    }
    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permissionGranted is not granted by user, show them message why this permissionGranted is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(getContext(), "Please grant permissions to recording audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permissionGranted to recording audio
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permissionGranted is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            permissionGranted = true;
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
                    // permissionGranted was granted, yay!
                    permissionGranted = true;
                } else {
                    // permissionGranted denied, boo! Disable the functionality that depends on this permissionGranted.
                    Toast.makeText(getActivity(), "Permissions Denied to recording audio", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}