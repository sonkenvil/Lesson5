package com.framgia.nguyenson.lesson5;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MyMusic extends Service implements MediaControl.CallBack{
    private IBinder mBinder = new LocalBinder();
    private MediaControl mMediaControl;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaControl = new MediaControl();
        mMediaControl.setSongList(Constants.data());
        mMediaControl.setOnlistener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleEvent(intent);
        return START_NOT_STICKY;
    }

    private void handleEvent(Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int position = bundle.getInt("position");
            mMediaControl.setIndexCurrentSong(position);
            switch (action) {
                case Constants.ACTION.PLAY_MUSIC:
                    play(position);
                    publishResult(mMediaControl.getSong(position));
                    runAsForeground();
                    break;
                case Constants.ACTION.PAUSE_MUSIC:
                    play(position);
                    publishResult(mMediaControl.getSong(position));
                    runAsForeground();
                    break;
                case Constants.ACTION.NEXT_MUSIC:
                    stop();
                    next();
                    publishResult(mMediaControl.getSong(position));
                    break;
                case Constants.ACTION.PREVIOUS_MUSIC:
                    stop();
                    previous();
                    publishResult(mMediaControl.getSong(position));
                    break;
                case Constants.ACTION.CONTINUE_MUSIC:
                     int mil = bundle.getInt("mil");
                     continueMusic(mil);
                case Constants.ACTION.SEEKBAR_CHANGE:
                    int progress = bundle.getInt("progress");
                    seekBarMusic(progress);
                    break;
                default:
                    play(0);
                    break;
            }
        }
    }

    private void seekBarMusic(int progress){
        mMediaControl.getMediaPlayer().seekTo(progress);
    }
    private void continueMusic(int mil){
        mMediaControl.continuePlay(mil);
    }
    private void play(int id) {
        mMediaControl.playSong(getApplicationContext(), id);
    }

    private void next() {
        mMediaControl.playSong(getApplicationContext(), mMediaControl.nextSong());
        mMediaControl.setIndexCurrentSong(mMediaControl.previousSong());
    }

    private void previous() {
        mMediaControl.playSong(getApplicationContext(), mMediaControl.previousSong());
        mMediaControl.setIndexCurrentSong(mMediaControl.previousSong());
    }
    public void stop(){
        mMediaControl.stopSong();
    }

    @Override
    public void onListener(int index) {
        play(index);
        publishResult(mMediaControl.getSong(index));
    }


    class LocalBinder extends Binder {

        MyMusic getService() {
            return MyMusic.this;
        }

    }

    private void runAsForeground() {
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_play_music)
                .setContentTitle("My Music")
                .setContentText("Doing some work...")
                .build();
        startForeground(1337, notification);
    }


    public int getDuration(){
        return mMediaControl.getMediaPlayer().getDuration();
    }

    public void publishResult(Song song){
        Intent intent = new Intent("Broadcast");
        Bundle bundle = new Bundle();
        bundle.putInt("duration",getDuration());

        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public MediaControl getMediaControl(){
        return mMediaControl;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
