package cn.deepkolos.simplemusic3.Storage.Store;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cn.deepkolos.simplemusic3.Storage.BaseStore;

public class SongStore {
    private static final String SONG_PATH = BaseStore.BASE_PATH+"/song";
    private static String songPath;

    public static void init () {
        File root = Environment.getExternalStorageDirectory();
        File song = new File(root.getAbsolutePath() + "/" + SONG_PATH);
        if (!song.exists()) song.mkdirs();

        songPath = song.getAbsolutePath();
    }

    public static Uri get (String name, String singer) {
        File song = new File(songPath + "/" + getSongFileName(name, singer));

        if (song.exists())
            return Uri.fromFile(song);

        return null;
    }

    public static void add (final String name, final String singer, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File song = new File(songPath + "/" + getSongFileName(name, singer));

                if (!song.exists() && song.canWrite()) {
                    try {
                        song.createNewFile();
                        FileWriter writer = new FileWriter(song);
                        writer.write(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static boolean remove (String name, String singer) {
        File song = new File(songPath + "/" + getSongFileName(name,singer));

        return song.exists() && song.delete();
    }

    public static String getSongFileName(String name, String singer) {
        return name + " - " + singer + ".mp3";
    }
}
