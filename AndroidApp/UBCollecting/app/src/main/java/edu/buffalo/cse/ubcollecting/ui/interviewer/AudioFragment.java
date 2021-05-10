package edu.buffalo.cse.ubcollecting.ui.interviewer;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.StrictMode;
//import android.support.annotation.Nullable;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
//import androidx.core.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.FireBaseCloudHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.Session;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

/**
 * A fragment to represent a question to be taken in a questionnaire.
 */

public class AudioFragment extends QuestionFragment{

    public final static String SELECTED_ANSWER = "selected answer";
    public final static String TAG = AudioFragment.class.getName();


    private Button saveAndExitQuestion;
    private ArrayList<Answer> answerList;
    private Answer answer;
    private Button takeAudio;
    private Button viewAudio;
    private MediaRecorder myAudioRecorder;
    private String mCurrentPath;
    private boolean recording;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private boolean permissionGranted;
    private TextView timer;

    private final FireBaseCloudHelper fireBaseCloudHelper = new FireBaseCloudHelper(this.getContext());



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


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);





        saveAndExitQuestion = view.findViewById(R.id.saveandexit_question);





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






    @Override
    protected boolean validateEntry() {
        boolean valid = true;

        if (mCurrentPath == null || mCurrentPath.isEmpty()){
            Toast.makeText(this.getActivity(), "Please Record an Audio Snippet", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;

    }

    @Override
    public void submitAnswer() {
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