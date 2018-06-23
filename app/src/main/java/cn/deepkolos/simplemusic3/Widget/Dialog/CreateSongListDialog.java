package cn.deepkolos.simplemusic3.Widget.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Utils.Callback;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;

public class CreateSongListDialog {
    private Callback<String> callback;
    private AlertDialog dialog;

    public CreateSongListDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_songlist, null);
        builder.setTitle("创建歌单");
        builder.setView(dialogView);

        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edit_text = dialogView.findViewById(R.id.dialog_create_songlist_name);
                        if (callback != null)
                            callback.call(edit_text.getText().toString());
                    }
                });

        dialog = builder.create();
    }

    public void setOnConfirm (Callback<String> callback) {
        this.callback = callback;
    }

    public void show () {
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = UnitHelper.dpToPx(200);
        dialog.getWindow().setAttributes(params);
    }
}
