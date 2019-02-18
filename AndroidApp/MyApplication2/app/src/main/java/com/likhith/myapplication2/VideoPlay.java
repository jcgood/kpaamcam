package com.likhith.myapplication2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;

public class VideoPlay extends AppCompatActivity {

    private VideoView mVideoView;
    private EditText editText;
    private Button save;
    private String uriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        mVideoView = findViewById(R.id.videoView);
        editText=findViewById(R.id.notes);
        save=findViewById(R.id.button);

        uriString=getIntent().getExtras().getString("videoUri");
        editText.setText(MainActivity.db.getOrDefault(uriString,""));
        Uri videoUri = Uri.parse(getIntent().getExtras().getString("videoUri"));
        mVideoView.setVideoURI(videoUri);
        mVideoView.start();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.db.put(uriString,editText.getText().toString());
                startActivity(new Intent(VideoPlay.this,MainActivity.class));
                finish();
            }
        });



    }
}
