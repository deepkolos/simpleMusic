package cn.deepkolos.simplemusic3.Page.LocalMusic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.Page.Utils.ViewPagerFragment;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Widget.ListItem.SongView;
import cn.deepkolos.simplemusic3.Widget.Utils.LoadingUtils;

public class SongFragment extends ViewPagerFragment {
    ListView $songList;
    List<SongView.Model> songList;
    SongListAdapter adapter;
    RelativeLayout $head;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        $songList = (ListView)inflater.inflate(R.layout.fragment_local_music_song_album_dir_singer, container, false);
        $head = (RelativeLayout) inflater.inflate(R.layout.fragment_local_music_song_head, null, false);
        $songList.addHeaderView($head);

        songList = new ArrayList<>();
        songList.add(null);
        adapter = new SongListAdapter();
        $songList.setAdapter(adapter);
        $songList.addFooterView(LayoutInflater.from(getContext()).inflate(R.layout.view_player_bar_placeholder, null, false));

        return $songList;
    }

    @Override
    public void onFirstSelected() {
        $songList.postDelayed(new Runnable() {
            @Override
            public void run() {
                songList.remove(0);
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, true, false, false));
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, false, true, true));
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, true, false, true));
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, true, true, true));
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, false, false, false));
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, false, false, false));
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, false, false, false));
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, false, false, false));
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, false, false, false));
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, false, false, false));
                songList.add(new SongView.Model(
                        "deepkolos", "deepkolos", null, false, false, false));

                adapter.notifyDataSetChanged();

            }
        },500);
    }

    private class SongListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return songList.size();
        }

        @Override
        public Object getItem(int position) {
            return songList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // loading
            if (position == 0 && songList.get(0) == null) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.view_loading, null, false);
                LoadingUtils.init(view);
                return view;
            }

            // item
            SongView view;
            if (convertView == null || !(convertView instanceof SongView)) {
                view = new SongView(getContext());
                init(view, position);
            } else {
                view = (SongView) convertView;
            }

            update(view, position);
            return view;
        }

        private void init(SongView view, int position) {
            view.setNoIndexOrPlayingIcon(true);
        }

        private void update(SongView view, int position) {
        }
    }
}
