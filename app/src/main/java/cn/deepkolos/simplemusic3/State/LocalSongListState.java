package cn.deepkolos.simplemusic3.State;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.Storage.DB;

public class LocalSongListState extends BaseState implements State<List<Song>> {
//    private List<Song> latestState;
    private static Map<String, List<Song>> latestState = new ArrayMap<>();
    private List<Integer> songIds;
    private String listId;

    private static Map<String, List<LocalSongListState>> listeners = new ArrayMap<>();

    public LocalSongListState(String listId) {
        this.listId = listId;
    }

    @Override
    public void notifyToAll() {
        List<LocalSongListState> listStates = listeners.get(listId);
        if (listStates != null) {
            for (LocalSongListState state : listStates)
                state.notifyDataChanged(false);
        }
    }

    @Override
    public void subscribe() {
        List<LocalSongListState> listStates;
        if (listeners.get(listId) == null) {
            listStates = new ArrayList<>();
            listeners.put(listId, listStates);
        } else {
            listStates = listeners.get(listId);
        }

        listStates.add(this);
    }

    @Override
    public void unSubscribe() {
        List<LocalSongListState> listStates = listeners.get(listId);

        if (listStates != null)
            listStates.remove(this);
    }

    @Override
    public synchronized List<Song> get() {
        if (latestState.get(listId) == null) {
            // 从数据库拿数据
            String sql = "SELECT song.song_id, song.singer_id, song.name, song.singer, song.cover_src FROM song " +
                    "INNER JOIN " +
                    "(select * from song_to_list where song_to_list.list_id = "+listId+") as tmp " +
                    "on tmp.song_id = song.song_id";
            SQLiteDatabase sqLiteDatabase = DB.getInstance().getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(sql,null);

            List<Song> list = new ArrayList<>();
            songIds = new ArrayList<>();
            while (cursor.moveToNext()) {
                list.add(new Song(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        true
                ));
                songIds.add(cursor.getInt(0));
            }

            latestState.put(listId, list);
            cursor.close();
        }
        return latestState.get(listId);
    }

    @Override
    public void set(List<Song> val) {
        latestState.put(listId, val);
        storeToDB();
    }

    public void add (Song song) {
        List<Song> list = get();
        if (song!= null && !songIds.contains(song.songId)) {
            list.add(song);
            songIds.add(song.songId);
            SongState.add(song);
            storeToDB();
        }
    }

    public void remove (Song song) {
        List<Song> list = get();
        if (songIds.contains(song.songId)) {
            songIds.remove((Integer) song.songId);
            SongState.remove(song);
            // 使用map数据来储存更好 TODO
            for (Song song1 : list) {
                if (song1.songId == song.songId) {
                    list.remove(song1);
                    break;
                }
            }
            storeToDB();
        }
    }

    public void remove (int index) {
        List<Song> list = get();
        if (index >=0 && index < list.size()) {
            list.remove(index);
            storeToDB();
        }
    }

    public void removeAll (){
        latestState.clear();
        storeToDB();
    }

    public void storeToDB () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = DB.getInstance().getWritableDatabase();

                db.beginTransaction();
                try {
                    db.execSQL("delete from song_to_list where list_id = '"+listId+"'");
                    Iterator<Song> iterator = latestState.get(listId).iterator();
                    Song song;
                    ContentValues values;

                    while (iterator.hasNext()) {
                        song = iterator.next();
                        values = new ContentValues();
                        values.put(Table.Cols.SONG_ID, song.songId);
                        values.put(Table.Cols.LIST_ID, listId);
                        db.insert(Table.NAME, null, values);
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
            }
        }).start();
    }

    public static class Table {
        public static String NAME = "song_to_list";

        public static class Cols {
            public static String SONG_ID = "song_id";
            public static String LIST_ID = "list_id";
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
            builder.append(" INTEGER NOT NULL,");

            builder.append(Table.Cols.LIST_ID);
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
