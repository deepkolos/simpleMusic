package cn.deepkolos.simplemusic3.State;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.deepkolos.simplemusic3.Model.ListInfo;
import cn.deepkolos.simplemusic3.Storage.DB;

public class SongListSetState extends BaseState implements State<List<ListInfo>> {
    private static List<ListInfo> latestState;

    @Override
    public synchronized List<ListInfo> get() {
        if (latestState == null) {
            SQLiteDatabase sqLiteDatabase = DB.getInstance().getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select list_id, name, 'desc', type from songlist where type != 2", null);

            latestState = new ArrayList<>();
            ListInfo info;
            while (cursor.moveToNext()) {
                info = new ListInfo();
                info.listId = cursor.getString(0);
                info.name = cursor.getString(1);
                info.desc = cursor.getString(2);
                info.type = cursor.getInt(3);
                info.localSongListState = new LocalSongListState(info.listId);

                info.localSongListState.subscribe();
                info.localSongListState.setOnUpdate(new OnUpdateFunc() {
                    @Override
                    public void call() {
                        notifyToAll();
                    }
                });
                latestState.add(info);
            }

            cursor.close();
        }
        return latestState;
    }

    @Override
    public void set(List<ListInfo> val) {}

    public synchronized void add (String name, String desc) {
        ListInfo info = new ListInfo();
        info.type = 1;
        info.listId = ""+System.currentTimeMillis();
        info.name = name;
        info.desc = desc;
        info.localSongListState = new LocalSongListState(info.listId);

        info.localSongListState.subscribe();
        info.localSongListState.setOnUpdate(new OnUpdateFunc() {
            @Override
            public void call() {
                notifyToAll();
            }
        });

        latestState.add(info);

        storeToDB();
    }

    public void remove (ListInfo info) {
        for (int i = 0; i < latestState.size(); i++) {
            if (latestState.get(i).listId.equals(info.listId)) {
                latestState.remove(i);
                break;
            }
        }
        info.localSongListState.removeAll();
        info.localSongListState.storeToDB();
        info.localSongListState = null;
        storeToDB();
    }

    public void storeToDB () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = DB.getInstance().getWritableDatabase();

                db.beginTransaction();
                try {
                    db.execSQL("DELETE FROM "+ Table.NAME +" where type != 2");
                    Iterator<ListInfo> iterator = latestState.iterator();
                    ListInfo info;
                    ContentValues values;

                    while (iterator.hasNext()) {
                        info = iterator.next();
                        values = new ContentValues();
                        values.put(Table.Cols.LIST_ID, info.listId);
                        values.put(Table.Cols.NAME, info.name);
                        values.put(Table.Cols.DESC, info.desc);
                        values.put(Table.Cols.TYPE, info.type);
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
        public static String NAME = "songlist";

        public static class Cols {
            public static String LIST_ID = "list_id";
            public static String NAME = "name";
            public static String DESC = "desc";
            public static String COVER_SRC = "cover_src";
            public static String TYPE = "type"; // 0 我的音乐固定, 1 自建歌单, 2 收藏歌单
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

            builder.append(Table.Cols.NAME);
            builder.append(" TEXT NOT NULL,");

            builder.append(Table.Cols.LIST_ID);
            builder.append(" INTEGER NOT NULL,");

            builder.append(Table.Cols.COVER_SRC);
            builder.append(" TEXT,");

            builder.append(Table.Cols.DESC);
            builder.append(" TEXT,");

            builder.append(Table.Cols.TYPE);
            builder.append(" INTEGER NOT NULL);");

            db.execSQL(builder.toString());

            ContentValues values = new ContentValues();
            values.put(Table.Cols.TYPE, 0);
            values.put(Table.Cols.NAME, "我喜欢的音乐");
            values.put(Table.Cols.DESC, "");
            values.put(Table.Cols.LIST_ID, 0);

            db.insert(Table.NAME, null, values);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+Table.NAME);
            onCreate(db);
        }
    }
}
