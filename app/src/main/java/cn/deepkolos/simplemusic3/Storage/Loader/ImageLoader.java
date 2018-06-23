package cn.deepkolos.simplemusic3.Storage.Loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

import cn.deepkolos.simplemusic3.Http.Download;
import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Storage.Cache.ImageCache;
import cn.deepkolos.simplemusic3.Storage.Store.CoverStore;
import cn.deepkolos.simplemusic3.Utils.Callback;
import cn.deepkolos.simplemusic3.Utils.ImageUtils;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;

public class ImageLoader {
//    public static final int TYPE_RAW = 0;
//    public static final int TYPE_SMALL = 1;
//    public static final int TYPE_BLUR = 2;

    public static String DEFAULT_COVER_CACHE_KEY = "http://simpleMusic/defaultCover.jpg";
    public static String DEFAULT_ARTIST_CACHE_KEY = "http://simpleMusic/defaultArtist.jpg";
    private static WeakReference<Context> contextRef;

    public static void init (Context context) {
        contextRef = new WeakReference<>(context);
    }

    private static Bitmap getFromCache(String url) {
        Bitmap bitmap;

        bitmap = ImageCache.getMemoryCache(url);
        if (bitmap != null) return bitmap;

        bitmap = ImageCache.getDiskCache(url);
        if (bitmap != null) {
            ImageCache.addMemoryCache(url, bitmap);
            return bitmap;
        }

        return null;
    }

    public static Bitmap get (final Song song) {
        Bitmap bitmap;

        // 通用缓存
        bitmap = getFromCache(song.coverSrc);
        if (bitmap != null) return bitmap;

        // 专用缓存
        bitmap = CoverStore.get(song.name, song.singer);
        if (bitmap != null) return bitmap;

        // 网络下载
        bitmap = Download.image(song.coverSrc);
        if (bitmap != null) {
            final Bitmap finalBitmap = bitmap;

            // 先返回bitmap, 在一个线程里面慢慢写入
            new Thread(new java.lang.Runnable() {
                @Override
                public void run() {
                    // 同时添加到通用缓存/专用缓存
                    ImageCache.addMemoryCache(song.coverSrc, finalBitmap);
                    if (!song.isLocal) {
                        ImageCache.addDiskCache(song.coverSrc, finalBitmap);
                    } else {
                        CoverStore.add(song.name, song.singer, finalBitmap);
                    }
                }
            }).start();

            return bitmap;
        };

        return null;
    }

    public static Bitmap get (String url) {
        Bitmap bitmap;

//        switch (type) {
//            case TYPE_SMALL:
//                url += "?type=small";
//                break;
//            case TYPE_BLUR:
//                url += "?type=blur";
//                break;
//            case TYPE_BLUR|TYPE_SMALL:
//                url += "?type=small&blur";
//                break;
//        }

        // 通用缓存
        bitmap = getFromCache(url);
        if (bitmap != null) return bitmap;

        if (url.equals(DEFAULT_COVER_CACHE_KEY)) {
            bitmap = getDefaultCover();
        } else if (url.equals(DEFAULT_ARTIST_CACHE_KEY)) {
            bitmap = getDefaultAvatar();
        } else
            // 网络下载
            bitmap = Download.image(url);

        if (bitmap != null) {
//            int height;
//            int width;
//            switch (type) {
//                case TYPE_SMALL:
//                    height = bitmap.getHeight() / 5;
//                    width = bitmap.getWidth() / 5;
//                    bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
//                    break;
//                case TYPE_BLUR:
//                    bitmap = ImageUtils.blur(bitmap);
//                    break;
//                case TYPE_BLUR|TYPE_SMALL:
//                    height = bitmap.getHeight() / 5;
//                    width = bitmap.getWidth() / 5;
//                    bitmap = ImageUtils.blur(Bitmap.createScaledBitmap(bitmap, width, height, false));
//                    break;
//            }
            bitmap = compress(bitmap);
            final String finalUrl = url;
            final Bitmap finalBitmap = bitmap;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ImageCache.addMemoryCache(finalUrl, finalBitmap);
                    ImageCache.addDiskCache(finalUrl, finalBitmap);
                }
            }).start();
            return bitmap;
        }

        return null;
    }

    public static void get (final String url, @NonNull final Callback<Bitmap> cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = get(url);

                if (bitmap != null)
                    cb.call(bitmap);
            }
        }).start();
    }

    public static Bitmap compress(Bitmap bitmap) {
        if (bitmap == null) return null;

        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        int screenW = UnitHelper.getScreenWidth();
        int screenH = UnitHelper.getScreenHeight();

        // 这里比例可以更好 TODO
        int dstW = Math.round(Math.min(screenW, bitmapW) / 2f);
        int dstH = Math.round(Math.min(screenH, bitmapH) / 2f);

        return ImageUtils.resizeImage(bitmap, dstW, dstH);
    }

    public static Bitmap getDefaultCover () {
        return compress(BitmapFactory.decodeResource(contextRef.get().getResources(), R.drawable.default_cover));
    }

    public static Bitmap getDefaultAvatar() {
        return compress(BitmapFactory.decodeResource(contextRef.get().getResources(), R.drawable.default_artist));
    }
}
