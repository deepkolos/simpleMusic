package cn.deepkolos.simplemusic3.Page.LocalMusic;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.Page.Utils.ViewPagerFragment;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Widget.ListItem.AlbumView;
import cn.deepkolos.simplemusic3.Widget.Utils.LoadingUtils;

public class DirectoryFragment extends ViewPagerFragment {
    ListView $dirList;
    List<AlbumView.Model> dirList;
    DirectoryListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        $dirList = (ListView) inflater.inflate(R.layout.fragment_local_music_song_album_dir_singer, container, false);

        dirList = new ArrayList<>();
        dirList.add(null);
        adapter = new DirectoryListViewAdapter();
        $dirList.setAdapter(adapter);
        $dirList.addFooterView(LayoutInflater.from(getContext()).inflate(R.layout.view_player_bar_placeholder, null, false));

        return $dirList;
    }

    @Override
    public void onFirstSelected() {
        $dirList.postDelayed(new Runnable() {
            @Override
            public void run() {
                Drawable icon = getResources().getDrawable(R.drawable.ic_directory, null);

                dirList.remove(0);
                dirList.add(new AlbumView.Model(icon,"我喜欢的音乐", 0));
                dirList.add(new AlbumView.Model(icon,"deepkolos", 0));
                dirList.add(new AlbumView.Model(icon,"deepkolos", 0));
                dirList.add(new AlbumView.Model(icon,"deepkolos", 0));
                dirList.add(new AlbumView.Model(icon,"deepkolos", 0));
                dirList.add(new AlbumView.Model(icon,"deepkolos", 0));
                dirList.add(new AlbumView.Model(icon,"deepkolos", 0));
                dirList.add(new AlbumView.Model(icon,"deepkolos", 0));
                dirList.add(new AlbumView.Model(icon,"deepkolos", 0));
                dirList.add(new AlbumView.Model(icon,"deepkolos", 0));

                adapter.notifyDataSetChanged();
            }
        }, 500);
    }

    private class DirectoryListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return dirList.size();
        }

        @Override
        public Object getItem(int position) {
            return dirList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // loading
            if (position == 0 && getItem(0) == null) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.view_loading, null, false);
                LoadingUtils.init(view);
                return view;
            }

            AlbumView view;

            if (convertView == null || !(convertView instanceof AlbumView)) {
                view = new AlbumView(getContext());
                init(view, position);
            } else
                view = (AlbumView) convertView;

            update(view, position);
            return view;
        }

        private void init(AlbumView view, int position) {
            view.setOnClickListener(new SettingBtnClickListener());
        }

        private void update (AlbumView view, int position) {
            AlbumView.Model data = (AlbumView.Model) getItem(position);
            view.setModel(data);
        }

        public class SettingBtnClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {

            }
        }
    }

}
