package cn.deepkolos.simplemusic3.State;

import android.content.Context;
import android.content.SharedPreferences;

import cn.deepkolos.simplemusic3.Model.Song;

import static cn.deepkolos.simplemusic3.State.SongState.Table;

public class PlayingSongState extends BaseState implements State<Song>{
    private static String SP_NAME = "playingSongState";
    private static Song latestState;

    @Override
    public synchronized Song get() {
        if (latestState == null) {
            SharedPreferences sp = BaseState.contextRef.get().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            Song song = new Song();
            song.name = sp.getString(Table.Cols.NAME, "");
            song.singer = sp.getString(Table.Cols.SINGER, "");
            song.songId = sp.getInt(Table.Cols.SONG_ID, -1);
            song.singerId = sp.getString(Table.Cols.SINGER_ID, "");
            song.coverSrc = sp.getString(Table.Cols.COVER_SRC, "");

            if (song.songId == -1)
                return null;

            latestState = song;
        }
        return latestState;
    }

    @Override
    public void set(final Song val) {
        latestState = val;
        notifyToAll();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = BaseState.contextRef.get().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString(Table.Cols.NAME, val.name);
                editor.putString(Table.Cols.SINGER, val.singer);
                editor.putInt(Table.Cols.SONG_ID, val.songId);
                editor.putString(Table.Cols.SINGER_ID, val.singerId);
                editor.putString(Table.Cols.COVER_SRC, val.coverSrc);
                editor.apply();
            }
        }).start();
    }
}
