package cn.deepkolos.simplemusic3.State;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.Storage.DB;

public class PlayListState extends BaseState implements State<List<Song>> {
    private static List<Song> latestState;

    @Override
    public synchronized List<Song> get() {
        if (latestState == null) {
            // 从DB获取初始化latestState
            SQLiteDatabase sqLiteDatabase = DB.getInstance().getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT song.song_id, song.singer_id, song.name, song.singer, song.cover_src FROM song INNER JOIN playlist ON song.song_id = playlist.song_id",
                    null);

            latestState = new ArrayList<>();
            while (cursor.moveToNext()) {
                latestState.add(new Song(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        true
                ));
            }

            cursor.close();
        }

        return latestState;
    }

    @Override
    public void set(List<Song> val) {
        latestState = val;
    }

    public void insertAfter (Song song, Song refSong) {
        List<Song> list = get();

        if (list == null) return;

        int refIndex = list.indexOf(refSong);

        if (refIndex == list.size() -1)
            list.add(song);
        else {
            for (int i = list.size(); i > refIndex+1; i++)
                list.add(i, list.get(i-1));
            list.add(refIndex+1, song);
        }
    }

    public void add (Song song) {
        List<Song> list = get();
        list.add(song);
    }

    public void remove (Song song) {
        List<Song> list = get();
        list.remove(song);
    }

    public void remove (int index) {
        List<Song> list = get();
        if (index >= 0 && index < list.size())
            list.remove(index);
    }

    public void storeToDB () {
        SQLiteDatabase db = DB.getInstance().getWritableDatabase();

        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM "+ Table.NAME);
            Iterator<Song> iterator = latestState.iterator();
            Song song;
            ContentValues values;

            while (iterator.hasNext()) {
                song = iterator.next();
                values = new ContentValues();
                values.put(Table.Cols.SONG_ID, song.songId);
                db.insert(Table.NAME, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public static class Table {
        public static String NAME = "playlist";

        public static class Cols {
            public static String SONG_ID = "song_id";
            private static final String[] ARR = {NAME};

            public static String[] toArray() {
                return ARR;
            }
        }
    }

    public static class InitSql implements DB.SqlEvent {
        @Override
        public void onCreate(SQLiteDatabase db) {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS ");
            builder.append(Table.NAME);
            builder.append("(");

            builder.append(Table.Cols.SONG_ID);
            builder.append(" INTEGER NOT NULL);");

            db.execSQL(builder.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+Table.NAME);
            onCreate(db);
        }
    }
}
