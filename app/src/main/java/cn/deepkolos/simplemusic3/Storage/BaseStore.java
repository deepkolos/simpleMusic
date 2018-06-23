package cn.deepkolos.simplemusic3.Storage;

import android.content.Context;

import cn.deepkolos.simplemusic3.Storage.Cache.ImageCache;
import cn.deepkolos.simplemusic3.Storage.Loader.ImageLoader;
import cn.deepkolos.simplemusic3.Storage.Store.CoverStore;
import cn.deepkolos.simplemusic3.Storage.Store.LyricStore;
import cn.deepkolos.simplemusic3.Storage.Store.SongStore;

public class BaseStore {
    public static final String BASE_PATH = "simpleMusic";

    public static void init (final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CoverStore.init();
                LyricStore.init();
                SongStore.init();
                ImageCache.init(context);
                ImageLoader.init(context);
            }
        }).start();
    }
}
