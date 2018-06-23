package cn.deepkolos.simplemusic3.Storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.ref.WeakReference;

import cn.deepkolos.simplemusic3.State.HistoryState;
import cn.deepkolos.simplemusic3.State.PlayListState;
import cn.deepkolos.simplemusic3.State.SongListSetState;
import cn.deepkolos.simplemusic3.State.LocalSongListState;
import cn.deepkolos.simplemusic3.State.SongState;

public class DB extends SQLiteOpenHelper {
    private static DB instance;
    private static final String DB_NAME = "music.db";
    private static WeakReference<Context> contextRef;

    private DB(Context context) {
        super(context, DB_NAME, null, 7);
    }

    public static void init (Context context) {
        contextRef = new WeakReference<>(context);
    }

    public static synchronized DB getInstance() {
        if (instance == null)
            instance = new DB(contextRef.get());
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        new SongState.InitSql().onCreate(db);
        new HistoryState.InitSql().onCreate(db);
        new PlayListState.InitSql().onCreate(db);
        new LocalSongListState.InitSql().onCreate(db);
        new SongListSetState.InitSql().onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        new SongState.InitSql().onUpgrade(db, i, i1);
        new HistoryState.InitSql().onUpgrade(db, i, i1);
        new PlayListState.InitSql().onUpgrade(db,i ,i1);
        new LocalSongListState.InitSql().onUpgrade(db, i, i1);
        new SongListSetState.InitSql().onUpgrade(db, i, i1);
    }

    public interface SqlEvent {
        void onCreate(SQLiteDatabase db);
        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }
}
