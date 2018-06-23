package cn.deepkolos.simplemusic3.Widget.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.State.SongListSetState;

public class SelectSongListDialog {
    private AlertDialog dialog;
    private Song song;

    public SelectSongListDialog(final Context context) {
        final SongListSetState setState = new SongListSetState();

        final String[] items = new String[setState.get().size()];
        for (int i = 0; i < items.length; i++ )
            items[i] = setState.get().get(i).name;

        AlertDialog.Builder builder = new AlertDialog.Builder(context,  R.style.Theme_AppCompat_Light_Dialog);
        builder.setTitle("收藏到歌单");

        // 第二个参数是默认选项，此处设置为0
        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setState.get().get(which).localSongListState.add(song);
                        setState.get().get(which).localSongListState.notifyToAll();
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public void show () {
        dialog.show();
    }
}
