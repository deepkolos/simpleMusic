package cn.deepkolos.simplemusic3.Widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Widget.ListItem.PlayListSongView;

public class PlaylistBottomPopup {
    private BottomPopupWithListView popup;
    private View rootView;
    private ListView $list;
    private List<PlayListSongView.Model> list;
    private Context context;

    public PlaylistBottomPopup(final Context context, View rootView) {
        this.rootView = rootView;
        this.context = context;
        popup = new BottomPopupWithListView(context, rootView);

        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(Color.rgb(243, 243, 243));
        popup.setBackground(colorDrawable);

        popup.setTopBar(R.layout.view_playlist_song_topbar);

        $list = popup.getListView();

        list = new ArrayList<>();
        list.add(new PlayListSongView.Model("DeepKolos中文", "deepkolos中文", true, ""));
        for(int i = 0; i < 100; i++)
            list.add(new PlayListSongView.Model("DeepKolos中文", "deepkolos中文", false, ""));

        PlayListAdapter adapter = new PlayListAdapter();
        $list.setAdapter(adapter);
    }

    public BottomPopup getPopup () {
        return popup;
    }

    public void show(){
        popup.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    private class PlayListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PlayListSongView view;
            PlayListSongView.Model data = (PlayListSongView.Model) getItem(position);
            if (convertView == null) {
                view = new PlayListSongView(context);

            } else
                view = (PlayListSongView) convertView;

            view.setModel(data);
            return view;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }
}
