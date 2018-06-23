package cn.deepkolos.simplemusic3.Storage.Store;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cn.deepkolos.simplemusic3.Storage.BaseStore;

public class LyricStore {
    private static final String LYRIC_PATH = BaseStore.BASE_PATH+"/lyric";
    private static String lyricPath;

    public static void init () {
        File root = Environment.getExternalStorageDirectory();
        File lyric = new File(root.getAbsolutePath() + "/" + LYRIC_PATH);
        if (!lyric.exists()) lyric.mkdirs();

        lyricPath = lyric.getAbsolutePath();
    }

    public static String get (String name, String singer) {
        String path = lyricPath + "/" + getLyricFileName(name, singer);
        File lyric = new File(path);

        StringBuffer resultBuffer = new StringBuffer();

        try {
            FileReader reader = new FileReader(lyric);
            BufferedReader buffer = new BufferedReader(reader);

            while (buffer.ready()) {
                resultBuffer.append(buffer.readLine());
            }

            reader.close();
            buffer.close();

            return resultBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void add (final String name, final String singer, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File lyric = new File(lyricPath + "/" + getLyricFileName(name, singer));

                if (!lyric.exists() && lyric.canWrite()) {
                    try {
                        lyric.createNewFile();
                        FileWriter writer = new FileWriter(lyric);
                        writer.write(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static boolean remove (String name, String singer) {
        File lyric = new File(lyricPath + "/" + getLyricFileName(name,singer));

        return lyric.exists() && lyric.delete();
    }

    public static String getLyricFileName (String name, String singer) {
        return name + " - " + singer + ".lrc";
    }
}
