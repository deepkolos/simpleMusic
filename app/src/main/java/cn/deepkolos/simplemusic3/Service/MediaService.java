package cn.deepkolos.simplemusic3.Service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import cn.deepkolos.simplemusic3.App;
import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.State.PlayingSongState;
import cn.deepkolos.simplemusic3.Storage.Loader.SongLoader;

public class MediaService extends Service implements App.MusicEventListener{
    private MediaPlayer player;
    private VolFade currVolFade;
    private float currVol = 0f;
    public static int FADE_IN_DUR = 1300;
    public static int FADE_OUT_DUR = 150;
    private Timer progressReporter;
    private Song currPlaySong;
    PlayingSongState playingSongState;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        player = new MediaPlayer();
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.i("player","error: i:"+i+" i1:"+i1);
                return false;
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // 下一首
                App.next();
            }
        });


        playingSongState = new PlayingSongState();
        App.addMusicListener(this);
//        progressReporter = new Timer();
//        progressReporter.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (player.isPlaying())
//                    App.progressUpdate(player.getCurrentPosition() / (float) player.getDuration());
//            }
//        }, 0, 200);
    }

    public void onDestroy() {
        player.release();
        player = null;
        progressReporter.cancel();
        App.removeMusicListener(this);
    }

    @Override
    public void onMusicSwitchOut(Song song) {

    }

    @Override
    public void onMusicModeChange(int mode) {

    }

    public void onMusicPlay() {
        if (currPlaySong == null)
            onMusicSwitchIn(playingSongState.get());

        // 如果还是没加载进来就说明没有正在播放的歌曲
        if (currPlaySong == null) return;

        if (currVolFade != null)
            currVolFade.interrupt();

        player.start();
        currVolFade = new VolFade(0f, 1f, FADE_IN_DUR, new Runnable(){
            @Override
            public void run() {
                currVolFade = null;
            }
        });
        currVolFade.run();
    }

    public void onMusicPause() {

        if (currVolFade != null)
            currVolFade.interrupt();

        currVolFade = new VolFade(1f, 0f, FADE_OUT_DUR, new Runnable(){
            @Override
            public void run() {
                player.pause();
                currVolFade = null;
            }
        });
        currVolFade.run();
    }

    public void onMusicSwitchIn(final Song song) {
        if (song == null) return;

        String path = SongLoader.get(song).toString();
        try {
            if (player.isPlaying())
                player.stop();
            player.reset();
            player.setDataSource(path);
            player.prepare();
            player.seekTo(0);
            currPlaySong = song;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class VolFade implements Runnable {
        private float from;
        private float to;
        private int duration;
        private int stepGap = 10;
        private int curr = 0;
        private Runnable cb;
        private Handler handler;
        private Boolean isInterrupted = false;


        public VolFade(float from, float to, int duration, Runnable cb) {
            this.from = from;
            this.to = to;
            this.duration = duration;
            this.cb = cb;
            this.handler = new Handler();;
        }

        @Override
        public void run() {
            if (isInterrupted) return;

            if (currVol != from && curr == 0)
                curr = (int) (to / currVol);

            if (curr >= duration) {
                currVol = to;
                player.setVolume(to, to);
                cb.run();

            } else {
                currVol = ((float) curr/(float) duration) * (to - from) + from;
                player.setVolume(currVol, currVol);
                handler.postDelayed(this, stepGap);
            }

            curr += stepGap;
        }

        public void interrupt () {
            isInterrupted = true;
        }
    }

    @Override
    public void onMusicProgressUpdate(float progress) {

    }
}
