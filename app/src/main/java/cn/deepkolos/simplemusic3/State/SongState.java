package cn.deepkolos.simplemusic3.State;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.Storage.DB;

public class SongState extends BaseState {

    public static void add (final Song song) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = DB.getInstance().getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(Table.Cols.NAME, song.name);
                values.put(Table.Cols.SINGER, song.singer);
                values.put(Table.Cols.COVER_SRC, song.coverSrc);
                values.put(Table.Cols.SINGER_ID, song.singerId);
                values.put(Table.Cols.SONG_ID, song.songId);
                db.insert(Table.NAME, null, values);
            }
        }).start();
    }

    public static void remove (final Song song) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = DB.getInstance().getWritableDatabase();

                db.execSQL("delete from "+Table.NAME+" where song_id = "+song.songId);
            }
        }).start();
    }

    public static class Table {
        public static String NAME = "song";

        public static class Cols {
            public static String SONG_ID = "song_id";
            public static String NAME = "name";
            public static String SINGER = "singer";
            public static String SINGER_ID = "singer_id";
            public static String COVER_SRC = "cover_src";
        }
    }

    public static class InitSql implements DB.SqlEvent {
        @Override
        public void onCreate(SQLiteDatabase db) {
            // 创建歌单表
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS ");
            builder.append(Table.NAME);
            builder.append("(");

            builder.append(Table.Cols.SONG_ID);
            builder.append(" INTEGER NOT NULL PRIMARY KEY UNIQUE,");

            builder.append(Table.Cols.SINGER_ID);
            builder.append(" TEXT NOT NULL,");

            builder.append(Table.Cols.NAME);
            builder.append(" TEXT NOT NULL,");

            builder.append(Table.Cols.SINGER);
            builder.append(" TEXT NOT NULL,");

            builder.append(Table.Cols.COVER_SRC);
            builder.append(" TEXT NOT NULL);");

            db.execSQL(builder.toString());

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+Table.NAME);
            onCreate(db);
        }
    }
}
