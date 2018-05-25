package com.framgia.nguyenson.lesson5;

import java.util.ArrayList;

public class Constants {

    interface ACTION {
         String SEEKBAR_CHANGE = "android.intent.action.SEEKBAR";
         String PLAY_MUSIC = "android.intent.action.PLAY";
         String CONTINUE_MUSIC = "android.intent.action.CONTINUE";
         String PAUSE_MUSIC = "android.intent.action.PAUSE";
         String NEXT_MUSIC = "android.intent.action.NEXT";
         String PREVIOUS_MUSIC = "android.intent.action.PREVIOUS";
    }

    public static ArrayList<Song> data() {
        ArrayList<Song> arrayList = new ArrayList<>();
        Song song;
        song = new Song();
        song.setId(R.raw.chacaidosevesontungmtp);
        song.setName("Chắc ai đó sẽ về");
        arrayList.add(song);

        song = new Song();
        song.setId(R.raw.emtrongmattoiphuonglinh);
        song.setName("Em trong mắt tôi");
        arrayList.add(song);

        song = new Song();
        song.setId(R.raw.giuemdithuychi);
        song.setName("Giữ em đi thuỳ chi");
        arrayList.add(song);

        song = new Song();
        song.setId(R.raw.huongngoclan);
        song.setName("Hương ngọc lan");
        arrayList.add(song);

        song = new Song();
        song.setId(R.raw.khonglienquanphamtruongcanhminh);
        song.setName("Không liên quan");
        arrayList.add(song);

        return arrayList;
    }
}
