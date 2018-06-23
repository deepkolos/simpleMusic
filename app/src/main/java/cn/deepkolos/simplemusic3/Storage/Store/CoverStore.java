package cn.deepkolos.simplemusic3.Storage.Store;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.deepkolos.simplemusic3.Storage.BaseStore;

public class CoverStore {
    private static final String COVER_PATH = BaseStore.BASE_PATH+"/cover";
    private static String coverPath;

    public static void init () {
        File root = Environment.getExternalStorageDirectory();
        File cover = new File(root.getAbsolutePath() + "/" + COVER_PATH);
        if (!cover.exists()) cover.mkdirs();

        coverPath = cover.getAbsolutePath();
    }

    public static Bitmap get (String name, String singer) {
        String path = coverPath + "/" + getCoverFileName(name, singer);

        return BitmapFactory.decodeFile(path);
    }

    public static void add (final String name, final String singer, final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File cover = new File(coverPath + "/" + getCoverFileName(name, singer));

                if (!cover.exists() && cover.canWrite()) {
                    try {
                        if (cover.createNewFile()) {
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cover));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                            bos.flush();
                            bos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static boolean remove (String name, String singer) {
        File cover = new File(coverPath + "/" + getCoverFileName(name,singer));

        return cover.exists() && cover.delete();
    }

    public static String getCoverFileName(String name, String singer) {
        return name + " - " + singer + ".jpg";
    }
}
