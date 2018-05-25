package com.framgia.nguyenson.lesson5;


import android.content.Context;
import android.media.MediaPlayer;
import java.util.ArrayList;


public class MediaControl implements MediaPlayer.OnCompletionListener{

    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mSongList;
    private int indexCurrentSong;
    private int mState = 3;
    static final int MEDIA_STATE_PLAYING = 1;
    static final int MEDIA_STATE_PAUSE = 0;
    static final int MEDIA_STATE_STOPPED = 2;
    static final int MEDIA_STATE_IDLE = 3;
    private CallBack mCallBack;

    public void playSong(Context context, int index) {
        if (mState == MEDIA_STATE_PLAYING) {
            mMediaPlayer.pause();
            mState = MEDIA_STATE_PAUSE;
        } else if (mState == MEDIA_STATE_IDLE || mState == MEDIA_STATE_STOPPED) {
            indexCurrentSong = index;
            Song song = mSongList.get(indexCurrentSong);
            mMediaPlayer = MediaPlayer.create(context, song.getId());
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.start();
            mState = MEDIA_STATE_PLAYING;
        } else{
            mMediaPlayer.start();
            mState = MEDIA_STATE_PLAYING;
        }
    }
    public void continuePlay(int position){
        mMediaPlayer.seekTo(position);
    }

    public void stopSong() {
        mState = MEDIA_STATE_STOPPED;
        mMediaPlayer.stop();
        mMediaPlayer.reset();

    }

    public int nextSong() {
        if (indexCurrentSong == (mSongList.size() - 1)) indexCurrentSong = 0;
        else indexCurrentSong ++;
        mState = MEDIA_STATE_STOPPED;
        return indexCurrentSong;
    }

    public int previousSong() {
        if ((indexCurrentSong == 0)) indexCurrentSong = mSongList.size() - 1;
        else indexCurrentSong --;
        mState = MEDIA_STATE_STOPPED;
        return indexCurrentSong;
    }

    public void setSongList(ArrayList<Song> list) {
        mSongList = list;
    }


    public void setIndexCurrentSong(int indexCurrentSong) {
        this.indexCurrentSong = indexCurrentSong;
    }


    public Song getSong(int p) {
        return mSongList.get(p);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mCallBack.onListener(nextSong());
    }

    public MediaPlayer getMediaPlayer() {
       return mMediaPlayer;
    }
    public void setOnlistener(CallBack onListener){
       mCallBack = onListener;
    }
    interface CallBack{
        void onListener(int index);
    }

}
