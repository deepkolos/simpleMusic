package cn.deepkolos.simplemusic3.State;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.deepkolos.simplemusic3.Model.ListInfo;
import cn.deepkolos.simplemusic3.Storage.DB;

public class CollectListSetState extends BaseState implements State<List<ListInfo>> {
    private static List<ListInfo> latestState;

    @Override
    public synchronized List<ListInfo> get() {
        if (latestState == null) {
            SQLiteDatabase sqLiteDatabase = DB.getInstance().getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select list_id, name, 'desc', type, cover_src from songlist where type = 2", null);

            latestState = new ArrayList<>();
            ListInfo info;
            while (cursor.moveToNext()) {
                info = new ListInfo();
                info.listId = cursor.getString(0);
                info.name = cursor.getString(1);
                info.desc = cursor.getString(2);
                info.type = cursor.getInt(3);
                info.coverSrc = cursor.getString(4);
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

    public synchronized void add (ListInfo i) {
        ListInfo info = new ListInfo();
        info.type = 2;
        info.listId = i.listId;
        info.name = i.name;
        info.desc = i.desc;
        info.coverSrc = i.coverSrc;

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
                    db.execSQL("DELETE FROM "+ SongListSetState.Table.NAME + " where type = 2");
                    Iterator<ListInfo> iterator = latestState.iterator();
                    ListInfo info;
                    ContentValues values;

                    while (iterator.hasNext()) {
                        info = iterator.next();
                        values = new ContentValues();
                        values.put(SongListSetState.Table.Cols.LIST_ID, info.listId);
                        values.put(SongListSetState.Table.Cols.NAME, info.name);
                        values.put(SongListSetState.Table.Cols.DESC, info.desc);
                        values.put(SongListSetState.Table.Cols.TYPE, info.type);
                        values.put(SongListSetState.Table.Cols.COVER_SRC, info.coverSrc);
                        db.insert(SongListSetState.Table.NAME, null, values);
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
}
