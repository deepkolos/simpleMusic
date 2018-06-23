package cn.deepkolos.simplemusic3.Storage.Cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import cn.deepkolos.simplemusic3.Storage.BaseStore;

import static cn.deepkolos.simplemusic3.Utils.StringUtils.hashKeyForDisk;

public class ImageCache {
    // 一级缓存 内存
    private static LruCache<String, Bitmap> memCache;

    // 二级缓存 磁盘
    private static final String diskPath = BaseStore.BASE_PATH + "/cache";
    private static final int diskCacheMaxSize = 30 * 1024 * 1024;
    private static DiskLruCache diskCache;

    public static void init(Context context){
        // 初始化一级缓存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 12;
        memCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        // 初始化二级缓存
        File diskFile = getDiskCacheDir(context, diskPath);
        try {
            diskCache = DiskLruCache.open(diskFile, 1, 1, diskCacheMaxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        File path = new File(cachePath + File.separator + uniqueName);

        if (!path.exists()) path.mkdirs();

        return path;
    }

    public static void addMemoryCache (String key, Bitmap bitmap) {
        if (memCache.get(key) == null) {
            memCache.put(key, bitmap);
            Log.i("debug", "加入内存缓存 ======================== ");
        }
    }

    public static Bitmap getMemoryCache (@NonNull String key) {
        Bitmap bitmap = memCache.get(key);
        if (bitmap != null) {
            Log.i("debug", "======================== 命中内存缓存");
        }
        return bitmap;
    }

    public static void addDiskCache(final String key, @NonNull final Bitmap bitmap) {
        // 通过一个key多次写入可能会有问题
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String keyMd5 = hashKeyForDisk(key);
                    DiskLruCache.Editor editor = diskCache.edit(keyMd5);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        outputStream.write(bitmap.getRowBytes());
                        editor.commit();
                    }
                    diskCache.flush();

                    Log.i("debug", "加入磁盘缓存 ======================== ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Bitmap getDiskCache(String key){
        String keyMd5 = hashKeyForDisk(key);
        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(keyMd5);
            if (snapshot != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                if (bitmap != null) {
                    Log.i("debug", "======================== 命中磁盘缓存");
                }
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
