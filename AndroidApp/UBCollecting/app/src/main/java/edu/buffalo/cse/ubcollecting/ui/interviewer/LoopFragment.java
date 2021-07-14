package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.models.Session;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.IS_LAST_LOOP_QUESTION;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.IS_LOOP_QUESTION_SET;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.LOOP_QUESTION_ID;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.QUESTIONNAIRE_CONTENT;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

public class LoopFragment extends QuestionFragment {

    private RecyclerView answerViewList;
    private Button addQuestionsButton;
    private Button mSaveAndQuitButton;
    private Button mediauploadButton;
    private Button viewMediaButton;
    private TextView timer;
    private EditText answerText;
    private EntryAdapter entryAdapter;
    private QuestionnaireContent questionnaireContent;
    private ArrayList<Answer> answerList;
    private LinearLayout listQuestionGroup;
    private LinearLayout mediaQuestionGroup;
    private Session session;
    private boolean mIsLastLoopQuestion = true;
    private boolean isLoopQuestion = false;
    private String mLoopQuestionId;
    private HashMap<String,String> questionSet;
    private HashMap<String,Answer> answerSet;
    private String currentPath;
    private Iterator questionIterator;
    private int answerIndex;
    private MediaRecorder myAudioRecorder;
    private String mCurrentPath;
    private boolean recording;
    private boolean permissionGranted;

    static final int REQUEST_MEDIA_CAPTURE = 1;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder newBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newBuilder.build());

        View view = inflater.inflate(R.layout.fragment_loop, container, false);
        answerViewList = view.findViewById(R.id.answer_list);
        answerText = view.findViewById(R.id.answer_text);
        listQuestionGroup = view.findViewById(R.id.list_group);
        mediaQuestionGroup = view.findViewById(R.id.upload_group);
        mediauploadButton =  view.findViewById(R.id.upload_button);
        viewMediaButton = view.findViewById(R.id.view_media);
        timer = view.findViewById(R.id.timer);

        viewMediaButton.setVisibility(View.INVISIBLE);
        listQuestionGroup.setVisibility(View.VISIBLE);
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

        Button nextQuestion = view.findViewById(R.id.next_question);
        nextQuestion.setOnClickListener(new NextQuestionOnClickListener());
        mIsLastLoopQuestion = (boolean) getArguments().getSerializable(IS_LAST_LOOP_QUESTION);
        isLoopQuestion = (boolean)getArguments().getSerializable(IS_LOOP_QUESTION_SET);
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
//        Check if question is already answered in the database
        ArrayList<EditText> answerTextList = entryAdapter.getAnswerList();
        ArrayList<Answer> previousAnswers = DatabaseHelper.ANSWER_TABLE.getAnswers(mLoopQuestionId, questionnaireContent.getQuestionnaireId());

//        Update answer at loop position with current answer
        for (int i=0; i<answerList.size();i++) {
            Answer answer = answerList.get(i);
           if(answer.getText()==null){
               answer.setText(answerTextList.get(i).getText().toString());
           }

            DatabaseHelper.ANSWER_TABLE.insert(answer);
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFile = "MP4_" + timeStamp + "_";

        File image = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), videoFile);

        // Save a file: path for use with ACTION_VIEW intents
        currentPath = "file:" + image.getAbsolutePath();
        Log.d("VideoFrag","file location: "+ currentPath);
        return image;
    }


    public void setNextQuestion(String rootAnswer,String questionId,String subQuestionText){
        QuestionPropertyDef questionProperty = DatabaseHelper.QUESTION_PROPERTY_TABLE.getQuestionProperty(questionId);
        String typeofQuestion = questionProperty.getName();

        this.questionText.setText(rootAnswer + "\n" + subQuestionText);
        if(typeofQuestion.equals("Photo")){
            mediaQuestionGroup.setVisibility(View.VISIBLE);
            mediauploadButton.setText("Take Photo");
            viewMediaButton.setText("View Photo");
            mediauploadButton.setOnClickListener(new MediaButtonListener(MediaStore.ACTION_IMAGE_CAPTURE));
            viewMediaButton.setOnClickListener(new ViewMediaListener("image"));
        }
        else if(typeofQuestion.equals("Video")){
            mediaQuestionGroup.setVisibility(View.VISIBLE);
            mediauploadButton.setText("Take Video");
            viewMediaButton.setText("View Video");
            mediauploadButton.setOnClickListener(new MediaButtonListener(MediaStore.ACTION_VIDEO_CAPTURE));
            viewMediaButton.setOnClickListener(new ViewMediaListener("video"));
        }
        else if(typeofQuestion.equals("Audio")){
            mediaQuestionGroup.setVisibility(View.VISIBLE);
            mediauploadButton.setText("Start Recording");
            viewMediaButton.setText("View Audio");
            if (!permissionGranted){
                requestAudioPermissions();
            }
            mediauploadButton.setOnClickListener(new AudioButtonListener());

        }
        else if(typeofQuestion.equals("Audio")){

        }
        else{
            answerText.setVisibility(View.VISIBLE);
            answerText.setText("");
        }

    }
    public void resetUI(){
        mediaQuestionGroup.setVisibility(View.INVISIBLE);
        listQuestionGroup.setVisibility(View.INVISIBLE);
        answerText.setVisibility(View.INVISIBLE);
        viewMediaButton.setVisibility(View.INVISIBLE);

    }
    protected class NextQuestionOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(validateEntry()){
                resetUI();
                if(isLoopQuestion){
                    // Add answer set to question based on how many answers are in answerlist
                    ArrayList<EditText> answerTextList = entryAdapter.getAnswerList();
                    questionSet = questionManager.askRepeatQuestions(answerTextList);
                    questionIterator = questionSet.entrySet().iterator();
                    answerIndex = 0;
                    Map.Entry<String,String> firstQuestion = (Map.Entry)questionIterator.next();
                    String firstAnswer = answerTextList.get(answerIndex).getText().toString();
                    setNextQuestion(firstAnswer,firstQuestion.getKey(),firstQuestion.getValue());
                    isLoopQuestion = false;
                }
                else if(!mIsLastLoopQuestion){

                    Map.Entry<String,String> question = (Map.Entry)questionIterator.next();
                    String rootAnswer = entryAdapter.getAnswerList().get(answerIndex).getText().toString();
                    setNextQuestion(rootAnswer,question.getKey(),question.getValue());
                    if(!questionIterator.hasNext()){
                        answerIndex++;
                        if(answerIndex >= entryAdapter.getAnswerList().size()){
                            mIsLastLoopQuestion = true;
                        }
                        questionIterator = questionSet.entrySet().iterator();
                    }
                }
                else{
                    questionManager.getNextQuestion();
                  //  submitAnswer();

                }
            }
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
    private class MediaButtonListener implements View.OnClickListener{

        String REQUEST_TYPE;
        public MediaButtonListener(String capture_Type){
            REQUEST_TYPE = capture_Type;
        }

        @Override
        public void onClick(View view) {
            Intent cameraIntent = new Intent(this.REQUEST_TYPE);
            if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    viewMediaButton.setVisibility(View.VISIBLE);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.i("PhotoFragment", "IOException");
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, REQUEST_MEDIA_CAPTURE);
                }
            }
        }
    }
    private class ViewMediaListener implements  View.OnClickListener{

        String media_type;
        public ViewMediaListener(String media){
            media_type = media + "*/";
        }

        @Override
        public void onClick(View view) {
            try{
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(currentPath), media_type);
                startActivity(intent);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private class AudioButtonListener implements View.OnClickListener{


        @Override
        public void onClick(View view) {

            if(!permissionGranted){
                return;
            }
            if(!recording){

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
                mediauploadButton.setText("STOP RECORDING");
                viewMediaButton.setVisibility(View.INVISIBLE);

            }
            else{
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
                timer.setVisibility(View.INVISIBLE);

                mediauploadButton.setText("START RECORDING");
                viewMediaButton.setVisibility(View.VISIBLE);
                recording = false;
            }

        }
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