package cn.deepkolos.simplemusic3;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.deepkolos.simplemusic3.Model.Raw.HotMusicList;
import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.Service.MediaService;
import cn.deepkolos.simplemusic3.State.BaseState;
import cn.deepkolos.simplemusic3.State.ModeState;
import cn.deepkolos.simplemusic3.State.PlayState;
import cn.deepkolos.simplemusic3.State.PlayingSongState;
import cn.deepkolos.simplemusic3.Storage.BaseStore;
import cn.deepkolos.simplemusic3.Storage.DB;
import cn.deepkolos.simplemusic3.Utils.Callback;

public class App extends Application {
    private static List<MusicEventListener> listeners;
    public static boolean SDCardUsable;
    private static PlayState playState;
    private static ModeState modeState;
    private static PlayingSongState playingSong;
    private static android.os.Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        Intent intent = new Intent();
        intent.setClass(this, MediaService.class);
        startService(intent);

        SDCardUsable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        BaseStore.init(this);
        DB.init(this);
        BaseState.setContext(this);

        listeners = new ArrayList<>();
        playState = new PlayState();
        modeState = new ModeState();
        playingSong = new PlayingSongState();
        handler = new android.os.Handler();
    }

    // 权限检查
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void addMusicListener (MusicEventListener listener) {
        listeners.add(listener);
    }

    public static void removeMusicListener (MusicEventListener listener) {
        listeners.remove(listener);
    }

    public static void emit (Callback<MusicEventListener> callback) {
        for (MusicEventListener listener : listeners)
            callback.call(listener);
    }

    // api
    public static void play () {
        playState.set(true);
        emit(new Callback<MusicEventListener>() {
            @Override
            public void call(MusicEventListener listener) {
                listener.onMusicPlay();
            }
        });
    }

    public static void pause () {
        playState.set(false);
        emit(new Callback<MusicEventListener>() {
            @Override
            public void call(MusicEventListener listener) {
                listener.onMusicPause();
            }
        });
    }

    public static void next () {
        final Song song = modeState.getNext();

        if (song.equals(playingSong.get())) {
            play();
        } else {
            pause();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchSong(song);
                }
            }, MediaService.FADE_OUT_DUR);
        }
    }

    public static void prev () {}

    public static void toggle () {
        if (playState.get()) {
            pause();
        } else {
            play();
        }
    }

    public static void switchSong (final Song song) {
        if (song == null) return;

        if (song.equals(playingSong.get())) {
            play();
            return;
        }

        pause();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                emit(new Callback<MusicEventListener>() {
                    @Override
                    public void call(MusicEventListener listener) {
                        listener.onMusicSwitchOut(playingSong.get());
                    }
                });

                playingSong.set(song);

                emit(new Callback<MusicEventListener>() {
                    @Override
                    public void call(MusicEventListener listener) {
                        listener.onMusicSwitchIn(song);
                    }
                });

                play();
            }
        }, MediaService.FADE_OUT_DUR);
    }

    public static void switchMode (final int mode) {
        modeState.set(mode);

        emit(new Callback<MusicEventListener>() {
            @Override
            public void call(MusicEventListener listener) {
                listener.onMusicModeChange(mode);
            }
        });
    }

    public static void nextMode () {
        int next = modeState.get() + 1;
        if (next >= ModeState.LIST.length)
            next = 0;

        switchMode(next);
    }

    public static void progressUpdate(final float i) {
        emit(new Callback<MusicEventListener>() {
            @Override
            public void call(MusicEventListener listener) {
                listener.onMusicProgressUpdate(i);
            }
        });
    }

    public interface MusicEventListener {
        void onMusicPlay();
        void onMusicPause();
        void onMusicSwitchIn(Song song);
        void onMusicSwitchOut(Song song);
        void onMusicModeChange(int mode);
        void onMusicProgressUpdate(float progress);
    }
}
