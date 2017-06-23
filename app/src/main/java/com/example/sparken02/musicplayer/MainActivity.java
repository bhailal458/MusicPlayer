package com.example.sparken02.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener{


    private ListView musiclist;
    private Cursor musiccursor;
    private int music_column_index;
    private int count;
    private MediaPlayer mMediaPlayer;
    private ImageButton btnnext,btnprevious,btnpause;
    private SeekBar seekbar;
    private int seek_progress;
    private TextView txttime;
    private   android.os.Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnnext =(ImageButton ) findViewById(R.id.btnnext);
        btnprevious =(ImageButton ) findViewById(R.id.btnpre);
        btnpause =(ImageButton ) findViewById(R.id.btnpause);
        txttime = (TextView) findViewById(R.id.txttime);

        seekbar = (SeekBar) findViewById(R.id.seekbar);
        btnprevious.setOnClickListener(this);
        btnnext.setOnClickListener(this);
        btnpause.setOnClickListener(this);
        handler = new android.os.Handler();
        txttime.setTextColor(getResources().getColor(R.color.white));

        init_phone_music_grid();
    }

    private void init_phone_music_grid() {
        System.gc();
        String[] proj = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE};
        musiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,  "UPPER(" +MediaStore.Audio.Media.DISPLAY_NAME + ") ASC");
        count = musiccursor.getCount();
        musiclist = (ListView) findViewById(R.id.songslistView);
        musiclist.setAdapter(new MusicAdapter(getApplicationContext()));
        musiclist.setOnItemClickListener(musicgridlistener);
        mMediaPlayer = new MediaPlayer();
    }
    private AdapterView.OnItemClickListener musicgridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            System.gc();
            music_column_index = musiccursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            musiccursor.moveToPosition(position);
            String filename = musiccursor.getString(music_column_index);

            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.reset();
                    seekbar.setProgress(0);
                }
                mMediaPlayer.setDataSource(filename);
                mMediaPlayer.prepare();
                seekbar.setProgress(0);

               String time = convert(mMediaPlayer.getDuration());
                txttime.setText(time.toString());
                mMediaPlayer.start();

                btnnext.setTag(position);
            } catch (Exception e) {

            }
        }
};

    private String convert(long dur) {
        long mns = (dur / 60000) % 60000;
        long scs = dur % 60000 / 1000;
        String songTime = String.format("%02d:%02d",  mns, scs);
        return songTime;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnnext:
                System.gc();
                musiccursor.getPosition();
                musiccursor.moveToNext();
                music_column_index = musiccursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                String filename = musiccursor.getString(music_column_index);
                try {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.reset();
                    }
                    mMediaPlayer.setDataSource(filename);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
//                    btnnext.setTag(position);
                } catch (Exception e) {

                }

                break;
            case R.id.btnpre:
                System.gc();
                musiccursor.getPosition();
                musiccursor.moveToPrevious();
                music_column_index = musiccursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                filename = musiccursor.getString(music_column_index);

                try {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.reset();
                    }
                    mMediaPlayer.setDataSource(filename);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
//                    btnnext.setTag(position);
                } catch (Exception e) {

                }

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
        }

    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {

        seek_progress = progress;
        seek_progress *= 1000;
        if(fromUser){
            Runnable mRunnable = new Runnable() {

                @Override
                public void run() {
                    int min, sec;

                    if (mMediaPlayer != null /*Checking if the
                       music player is null or not otherwise it
                       may throw an exception*/) {
                        int mCurrentPosition = seekBar.getProgress();

                        min = mCurrentPosition / 60;
                        sec = mCurrentPosition % 60;

                        Log.e("Music Player Activity", "Minutes : "+min +" Seconds : " + sec);

                        /*currentime_mm.setText("" + min);
                        currentime_ss.setText("" + sec);*/
                    }
                    handler.postDelayed(this, 1000);
                }
            };
            mRunnable.run();

        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public class MusicAdapter extends BaseAdapter {
        private Context mContext;

        public MusicAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            System.gc();
            TextView tv = new TextView(mContext.getApplicationContext());

            tv.setTextColor(getResources().getColor(R.color.white));

            String id = null;
            if (convertView == null) {
                music_column_index = musiccursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                musiccursor.moveToPosition(position);
                id = musiccursor.getString(music_column_index);
                music_column_index = musiccursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                musiccursor.moveToPosition(position);
                id += " Size(KB):" + musiccursor.getString(music_column_index);
                tv.setText(id);
            } else
                tv = (TextView) convertView;
            return tv;
        }
    }

//        @Override
//        public void onBackPressed() {
//            // TODO Auto-generated method stub
//            super.onBackPressed();
//            mMediaPlayer.stop();
//        }

}
