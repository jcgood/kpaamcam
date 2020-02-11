package edu.buffalo.cse.ubcollecting.ui.interviewer;


import android.content.Context;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

/**
 * A fragment to represent a question to be taken in a questionnaire.
 */

public class PhotoFragment extends QuestionFragment {

    public final static String TAG=PhotoFragment.class.getName();
    public final static String SELECTED_ANSWER = "selected answer";

    private Button saveAndExitQuestion;
    private ArrayList<Answer> answerList;
    private Answer answer;
    private Button takePhoto;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPath;
    private Button viewPhoto;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        StrictMode.VmPolicy.Builder newBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newBuilder.build());

        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        viewPhoto = view.findViewById(R.id.view);

        answer = new Answer();
        takePhoto = view.findViewById(R.id.answer_instructions);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



        saveAndExitQuestion = view.findViewById(R.id.saveandexit_question);

        saveAndExitQuestion.setOnClickListener(new PhotoFragment.SaveAndExitQuestionOnClickListener());
        if(getArguments().containsKey(SELECTED_ANSWER)){
            answerList = (ArrayList<Answer>) getArguments().getSerializable(SELECTED_ANSWER);
            Answer prevAnswer = answerList.get(0);
            viewPhoto.setVisibility(View.VISIBLE);
            mCurrentPath = prevAnswer.getText();
        } else {
            answerList = new ArrayList<>();
            viewPhoto.setVisibility(View.INVISIBLE);
        }

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG,"enetred take photo");

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i("PhotoFragment", "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }

            }
        });

        viewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(mCurrentPath), "image/*");
                    startActivity(intent);
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
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPath = "file:" + image.getAbsolutePath();
        Log.d("PhotoFrag","file location: "+mCurrentPath);
        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            try {
                //mImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                viewPhoto.setVisibility(View.VISIBLE);
                //mImageView.setImageBitmap(mImageBitmap);
            } catch (Exception e) {
                Log.e(TAG,e.toString());
            }
        }

    }





      @Override
      boolean validateEntry() {

        boolean valid = true;

        if (mCurrentPath == null || mCurrentPath.isEmpty()){
            Toast.makeText(this.getActivity(), "Please Take a Photo", Toast.LENGTH_SHORT).show();
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
        DatabaseHelper.ANSWER_TABLE.insert(answer);

    }

}