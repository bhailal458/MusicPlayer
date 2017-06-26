package com.example.sparken02.musicplayer;


import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener{

    private Utilities utils= new Utilities();
    private static final String FORMAT = "%02d:%02d";
//    private ListView musiclist;
    private Cursor musiccursor;
    private int music_column_index;
    private int count;
    private MediaPlayer mMediaPlayer;
    private ImageButton btnnext,btnprevious,btnpause;
    private SeekBar seekbar;
    private TextView txttimestart,txttimeend;
    private Handler mHandler = new Handler();
    private ArrayList<Model> songArrayList = new ArrayList<>();
    private LinearLayout mLinearLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnnext =(ImageButton ) findViewById(R.id.btnnext);
        btnprevious =(ImageButton ) findViewById(R.id.btnpre);
        btnpause =(ImageButton ) findViewById(R.id.btnpause);
        txttimestart = (TextView) findViewById(R.id.txttimestart);
        txttimeend = (TextView) findViewById(R.id.txttimeend);
//        musiclist = (ListView) findViewById(R.id.songslistView);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        btnprevious.setOnClickListener(this);
        btnnext.setOnClickListener(this);
        btnpause.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        txttimestart.setTextColor(getResources().getColor(R.color.white));
        txttimeend.setTextColor(getResources().getColor(R.color.white));

        init_phone_music_grid();
    }
    private void init_phone_music_grid() {
        getSongList();
        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(MainActivity.this,songArrayList,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.gc();
                int position = (int) view.getTag();
//                Toast.makeText(MainActivity.this, "pos"+position, Toast.LENGTH_SHORT).show();
//                musiccursor.moveToPosition(position);
                playSong();
            }
        });

        mMediaPlayer = new MediaPlayer();
    }
    private void getSongList() {
        System.gc();
        musiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,  "UPPER(" +MediaStore.Audio.Media.DISPLAY_NAME + ") ASC");
        count = musiccursor.getCount();
        while (musiccursor.moveToNext()) {
            Model modelobj = new Model();
            String title =  musiccursor.getString(musiccursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String path = musiccursor.getString(musiccursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String size = musiccursor.getString(musiccursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            modelobj.setTitle(title);
            modelobj.setPath(path);
            modelobj.setSize(size);
            songArrayList.add(modelobj);
        }
    }
//    private AdapterView.OnItemClickListener musicgridlistener = new AdapterView.OnItemClickListener() {
//        public void onItemClick(AdapterView parent, View v, int position, long id) {
//            System.gc();
//            musiccursor.moveToPosition(position);
//            playSong();
//        }
//};
    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 1000);
    }
    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){
            case R.id.btnnext:
                nextsong();
                break;
            case R.id.btnpre:
                System.gc();
                if(musiccursor.getPosition() == 0){
                    musiccursor.moveToLast();
                }
                musiccursor.getPosition();
                musiccursor.moveToPrevious();
                playSong();
                break;
            case R.id.btnpause:
                if(mMediaPlayer.isPlaying()){
                    btnpause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    mMediaPlayer.pause();
                }else{
                    btnpause.setImageResource(R.drawable.ic_pause_white_24dp);
                    mMediaPlayer.start();
                }
                break;
            case R.id.txtRecycview:
                int pos = (int) v.getTag();
                System.gc();
                musiccursor.moveToPosition(pos);
                playSong();
        }
    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            float totalDuration = mMediaPlayer.getDuration();
            float currentDuration = mMediaPlayer.getCurrentPosition();
            txttimestart.setText(""+utils.milliSecondsToTimer((long) totalDuration));
            txttimeend.setText(""+utils.milliSecondsToTimer((long) currentDuration));
            float progress = currentDuration/totalDuration;
            progress=progress*100;
            seekbar.setProgress((int) progress);

            if(seekbar.getProgress() == 99){
                mHandler.removeCallbacks(mUpdateTimeTask);
                nextsong();
            }
            // Running this thread after 1000 milliseconds
            mHandler.postDelayed(this, 1000);
        }
    };
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);

        float totalDuration = mMediaPlayer.getDuration();
        float progress = seekBar.getProgress();
        totalDuration = totalDuration/1000;
        float currentDuration = ((progress/100) * totalDuration);
        float currentPosition = currentDuration * 1000;
        // forward or backward to certain seconds
        Log.i("TAG",""+currentPosition+"->"+""+currentDuration+"->"+""+totalDuration);
        mMediaPlayer.seekTo((int) currentPosition);
        // update timer progress again
        updateProgressBar();
    }
    public void playSong(){
        music_column_index = musiccursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        String filename = musiccursor.getString(music_column_index);
        try {
            if (mMediaPlayer.isPlaying()) {
                  mMediaPlayer.reset();
            }
            mMediaPlayer.setDataSource(filename);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            seekbar.setProgress(0);
            seekbar.setMax(100);
            updateProgressBar();
        } catch (Exception e) {}
    }

    public void nextsong(){
        System.gc();
        if(musiccursor.getPosition() == count){
            musiccursor.moveToFirst();
        }
        musiccursor.getPosition();
        musiccursor.moveToNext();
        playSong();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
    }
}
