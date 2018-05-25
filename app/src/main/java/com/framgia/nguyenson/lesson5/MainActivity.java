package com.framgia.nguyenson.lesson5;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ItemClickListener, SeekBar.OnSeekBarChangeListener{
    private ImageButton mPlay, mPause, mNext, mPrevious;
    private TextView mStart, mEnd;
    private SeekBar mSeekBar;
    private Intent mIntent;
    private MyMusic mMyMusic;
    private RecyclerView mRecyclerView;
    private SongAdapter mSongAdapter;
    private boolean mBound = false;
    private Handler mHandler;
    private int mSaveCurrentPosition;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int duration = intent.getExtras().getInt("duration");
            mEnd.setText(milliSecondsToTimer(duration));
            mSeekBar.setMax(duration);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mMyMusic.getMediaControl().getMediaPlayer().isPlaying()) {
                        int current = mMyMusic.getMediaControl().getMediaPlayer().getCurrentPosition();
                        mSaveCurrentPosition = current;
                        mStart.setText(milliSecondsToTimer(current));
                        mSeekBar.setProgress(current);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapping();
        init();

    }


    @Override
    protected void onStart() {
        super.onStart();
        mIntent = new Intent(this, MyMusic.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter("Broadcast"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private void mapping() {
        mPlay = findViewById(R.id.image_play);
        mPause = findViewById(R.id.image_pause);
        mNext = findViewById(R.id.image_next);
        mPrevious = findViewById(R.id.image_previous);
        mStart = findViewById(R.id.text_start);
        mEnd = findViewById(R.id.text_end);
        mSeekBar = findViewById(R.id.seebar);
        mRecyclerView = findViewById(R.id.recycler_song);
    }

    private void init() {
        mPlay.setOnClickListener(this);
        mPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mSongAdapter = new SongAdapter(Constants.data());
        mSongAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mSongAdapter);
        mSeekBar.setOnSeekBarChangeListener(this);
        mHandler = new Handler();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.image_play:
                mPause.setVisibility(View.VISIBLE);
                mPlay.setVisibility(View.GONE);
                mIntent.setAction(Constants.ACTION.PLAY_MUSIC);
                break;
            case R.id.image_pause:
                mPause.setVisibility(View.GONE);
                mPlay.setVisibility(View.VISIBLE);
                mIntent.setAction(Constants.ACTION.PAUSE_MUSIC);
                break;
            case R.id.image_next:
                mIntent.setAction(Constants.ACTION.NEXT_MUSIC);
                break;
            case R.id.image_previous:
                mIntent.setAction(Constants.ACTION.PREVIOUS_MUSIC);
                break;
        }
        startService(mIntent);
    }


    @Override
    public void onClick(View view, int position) {
        if (mPlay.getVisibility() == View.VISIBLE) {
            mPlay.setVisibility(View.GONE);
            mPause.setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            mIntent.putExtras(bundle);
            mIntent.setAction(Constants.ACTION.PLAY_MUSIC);
            startService(mIntent);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bundle bundle = new Bundle();
        bundle.putInt("mil",mSaveCurrentPosition);
        mIntent.putExtras(bundle);
        mIntent.setAction(Constants.ACTION.CONTINUE_MUSIC);
        startService(mIntent);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyMusic.LocalBinder binder = (MyMusic.LocalBinder) service;
            mMyMusic = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBar.setProgress(progress);
        mIntent.setAction(Constants.ACTION.SEEKBAR_CHANGE);
        Bundle bundle = new Bundle();
        bundle.putInt("progress",progress);
        mIntent.putExtras(bundle);
        startService(mIntent);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString;
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }


}
