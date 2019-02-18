package com.likhith.myapplication2;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    private static int REQUEST_CODE=101;

    private Uri videoUri;
    private Button record;
    private Button play;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private static HashMap<Integer,String> map=new HashMap<>();
    public static HashMap<String,String> db=new HashMap<>();

    int counter=1;
    public static final ArrayList<String> recordings=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        record=(Button) findViewById(R.id.recordVideo);
        play=(Button) findViewById(R.id.playVideo);
        listView=(ListView) findViewById(R.id.listofrecording);

        //videoUri=Uri.fromFile(getFile());

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playIntent=new Intent(MainActivity.this,VideoPlay.class);
                playIntent.putExtra("videoUri",videoUri.toString());
                startActivity(playIntent);
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recordings);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent playIntent=new Intent(MainActivity.this,VideoPlay.class);
                String sample=map.get(i);
                playIntent.putExtra("videoUri",sample);
                startActivity(playIntent);

            }
        });


        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent i=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                if(i.resolveActivity(getPackageManager())!=null)
                {
                    startActivityForResult(i,REQUEST_CODE);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                Toast.makeText(getApplicationContext(), "Video has been saved", Toast.LENGTH_SHORT).show();
                videoUri=data.getData();
                String path=videoUri.toString();
                int size=recordings.size()+1;
                recordings.add("Recording "+size);
                map.put(recordings.size()-1,videoUri.toString());
                adapter.notifyDataSetChanged();
                Intent playIntent=new Intent(MainActivity.this,VideoPlay.class);
                playIntent.putExtra("videoUri",videoUri.toString());
                startActivity(playIntent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Error, video not saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getFile(){
        File video_file=new File("sdcard/myfolder/myvideo2.mp4");
        return video_file;
    }
}