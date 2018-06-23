package cn.deepkolos.simplemusic3.Page;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.App;
import cn.deepkolos.simplemusic3.Http.MusicApi;
import cn.deepkolos.simplemusic3.Model.ListInfo;
import cn.deepkolos.simplemusic3.Model.Raw.PlaylistRaw;
import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.State.BaseState;
import cn.deepkolos.simplemusic3.State.CollectListSetState;
import cn.deepkolos.simplemusic3.State.SongListSetState;
import cn.deepkolos.simplemusic3.State.LocalSongListState;
import cn.deepkolos.simplemusic3.Storage.Loader.ImageLoader;
import cn.deepkolos.simplemusic3.Utils.Callback;
import cn.deepkolos.simplemusic3.Utils.ImageUtils;
import cn.deepkolos.simplemusic3.Utils.UiUtils;
import cn.deepkolos.simplemusic3.Widget.Dialog.SelectSongListDialog;
import cn.deepkolos.simplemusic3.Widget.ListItem.SongView;
import cn.deepkolos.simplemusic3.Widget.BottomPopup;
import cn.deepkolos.simplemusic3.Widget.Layout.ClipFrameLayout;
import cn.deepkolos.simplemusic3.Widget.OverRollTextView;
import cn.deepkolos.simplemusic3.Widget.Utils.LoadingUtils;

public class AlbumActivity extends TopBarActivity {
    View $tail;
    ImageView $bg;
    ImageView $cover;
    ImageView $topBarBg;
    ImageView $creatorAvatar;
    ListView $listView;
    ViewGroup $head;
    TextView $playAllCount;
    TextView $collectBtn;
    TextView $albumName;
    TextView $creator;
    LinearLayout $albumInfo;
    LinearLayout $btnContainer;
    LinearLayout $topBar;
    ClipFrameLayout $content;
    OverRollTextView $subTitle;
    OverRollTextView $title;
    UiUtils.DisplaySwitcher coverSwitcher;
    UiUtils.DisplaySwitcher bgSwitcher;
    AlbumSongListAdapter adapter;

    List<SongView.Model> songListVm;
    List<Song> songList;
    LocalSongListState localSongListState;
    String listId;
    ListInfo onlineListInfo;

    // 定义activity调用接口
    public static String PLAYLIST_ID = "PLAYLIST_ID"; // string 网易云api的数值太大还是用字符串
    public static String IS_LOCAL = "IS_LOCAL";       // boolean

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album);

        $listView = findViewById(R.id.activity_album_listView);
        $topBar = findViewById(R.id.activity_album_topBar);
        $topBarBg = findViewById(R.id.activity_album_topBar_bg_blur);
        $title = findViewById(R.id.activity_album_topBar_title);
        $subTitle = findViewById(R.id.activity_album_topBar_subtitle);

        initHead();

        bgSwitcher = new UiUtils.DisplaySwitcher($bg, false);
        coverSwitcher = new UiUtils.DisplaySwitcher($cover, false);
        $head.setAlpha(0.5f); // 数据未初始化时
        $listView.addHeaderView($head);

        // tail
        $tail = LayoutInflater.from(this).inflate(R.layout.view_player_bar_placeholder, null, false);
        $listView.addFooterView($tail);

        // songListVm
        songListVm = new ArrayList<>();
        songListVm.add(null);
        adapter = new AlbumSongListAdapter();
        $listView.setAdapter(adapter);

        $listView.setOnScrollListener(new UiUtils.ListViewOnScrollY() {
            @Override
            public void onScroll(int scrollY) {
                int contentTop = $content.getTop() - $topBar.getHeight();
                float progress = 1;

                if (scrollY <= contentTop)
                    progress = scrollY / (float) contentTop;

                float alpha = 1 - progress;

                $albumInfo.setAlpha(alpha);
                $btnContainer.setAlpha(alpha);
                $topBarBg.setAlpha(progress);

                if (scrollY < 1)
                    $listView.setScrollY(0);
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            listId = bundle.getString(AlbumActivity.PLAYLIST_ID);
            boolean isLocal = bundle.getBoolean("IS_LOCAL");
            if (!bundle.getBoolean("IS_LOCAL"))
                doOnlineListLoad(listId);
            else
                doLocalListLoad(listId);
        }
    }

    private void initHead() {
        // head
        $head = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_album_content_head, null, false);
        $btnContainer = $head.findViewById(R.id.activity_album_btn_container);
        $albumInfo = $head.findViewById(R.id.activity_album_info);
        $content = $head.findViewById(R.id.activity_album_content);
        $cover = $head.findViewById(R.id.activity_album_cover);
        $bg = $head.findViewById(R.id.activity_album_bg_blur);
        $playAllCount = $head.findViewById(R.id.activity_album_songlist_topBar_playAll_count);
        $collectBtn = $head.findViewById(R.id.activity_album_collection_btn);
        $albumName = $head.findViewById(R.id.activity_album_content_name);
        $creator = $head.findViewById(R.id.activity_album_content_creator);
        $creatorAvatar = $head.findViewById(R.id.activity_album_content_creator_avatar);

        $collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectListSetState collectListSetState = new CollectListSetState();
                collectListSetState.add(onlineListInfo);
                collectListSetState.notifyToAll();
            }
        });
    }


    private void doOnlineListLoad (final String playlistId) {
        MusicApi.playlist(playlistId, new Callback<PlaylistRaw>() {
            @Override
            public void call(final PlaylistRaw list) {
                songListVm.remove(0);
                songList = new ArrayList<>();
                for (int i = 0; i < list.playlist.tracks.length; i++ ) {
                    Song song = Song.from(list.playlist.tracks[i]);

                    if (song != null) {
                        songListVm.add(new SongView.Model(
                                song.name, song.singer, (i + 1) + "", song.SQ, song.downloaded, song.hasMv));

                        songList.add(song);
                    }
                }

                onlineListInfo = new ListInfo();
                onlineListInfo.listId = playlistId;
                onlineListInfo.name = list.playlist.name;
                onlineListInfo.desc = list.playlist.creator.nickname;
                onlineListInfo.type = 2;
                onlineListInfo.coverSrc = list.playlist.coverImgUrl;

                setHeadData(
                    list.playlist.coverImgUrl,
                    list.playlist.creator.avatarUrl,
                    list.playlist.tracks.length,
                    list.playlist.subscribedCount,
                    list.playlist.creator.nickname,
                    list.playlist.name);
            }
        });
    }

    private void doLocalListLoad (final String listId) {
        final SongListSetState songListSetState = new SongListSetState();
        localSongListState = new LocalSongListState(listId);
        final Runnable updateView = new Runnable() {
            @Override
            public void run() {
                ListInfo info = null;
                for (ListInfo i : songListSetState.get()) {
                    if (i.listId.equals(listId)) {
                        info = i;
                        break;
                    }
                }

                songListVm.clear();
                List<Song> list = localSongListState.get();
                songList = list;
                Song song;
                for (int i = list.size() - 1; i > -1 ; i--) {
                    song = list.get(i);

                    if (song != null)
                        songListVm.add(new SongView.Model(
                                song.name, song.singer, (list.size() - i)+"", song.SQ, song.downloaded, song.hasMv));
                }

                // 最新一首歌的封面
                song = null;
                if (list.size() != 0)
                    song = list.get(list.size() - 1);

                setHeadData(
                        song != null ? song.coverSrc : ImageLoader.DEFAULT_COVER_CACHE_KEY,
                        ImageLoader.DEFAULT_ARTIST_CACHE_KEY,
                        list.size(), null, "未登陆",
                        info != null ? info.name : "");

                $listView.post(new Runnable() {
                    @Override
                    public void run() {
                        $subTitle.setVisibility(View.GONE);
                    }
                });
            }
        };

        localSongListState.subscribe();
        localSongListState.setOnUpdate(new BaseState.OnUpdateFunc() {
            @Override
            public void call() {
                updateView.run();
            }
        });



        new Thread(updateView).start();
    }

    private void setHeadData (
            String coverSrc,
            String avatarSrc,
            final int songCount,
            final Integer collectCount,
            final String creatorName,
            final String albumName
    ) {
        ImageLoader.get(coverSrc, new Callback<Bitmap>() {
            @Override
            public void call(final Bitmap val) {
                // 处理背景blur
                final Bitmap blur = ImageUtils.blur(Bitmap.createScaledBitmap(val, 200, 200, false));
                $listView.post(new Runnable() {
                    @Override
                    public void run() {
                        $cover.setImageBitmap(val);
                        $bg.setImageBitmap(blur);
                        $topBarBg.setImageBitmap(blur);
                        coverSwitcher.show(false);
                        coverSwitcher.show(true);
                        bgSwitcher.hidden(false);
                        bgSwitcher.show(true);
                    }
                });
            }
        });

        ImageLoader.get(avatarSrc, new Callback<Bitmap>() {
            @Override
            public void call(final Bitmap val) {
                $listView.post(new Runnable() {
                    @Override
                    public void run() {
                        $creatorAvatar.setImageBitmap(val);
                    }
                });
            }
        });

        $listView.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

                String count = "(共"+songCount+"首)";

                if (collectCount == null) {
                    $collectBtn.setVisibility(View.GONE);
                } else {
                    String collectCountStr = "+ 收藏("+collectCount+")";
                    $collectBtn.setText(collectCountStr);
                    $collectBtn.setVisibility(View.VISIBLE);
                }
                $creator.setText(creatorName);
                $playAllCount.setText(count);
                $albumName.setText(albumName);
                $head.setAlpha(1f);
            }
        });
    }

    class AlbumSongListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return songListVm.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // loading
            if (position == 0 && songListVm.get(0) == null) {
                View view = LayoutInflater.from(AlbumActivity.this).inflate(R.layout.view_loading, null, false);
                LoadingUtils.init(view);
                return view;
            }

            // item
            SongView view;
            if (convertView == null || !(convertView instanceof SongView)) {
                view = new SongView(AlbumActivity.this);

            } else {
                view = (SongView) convertView;
            }

            initSongItemView(view, position);
            updateSongItemView(view, position);
            return view;
        }

        private void initSongItemView(SongView view, int position) {
            SongView.Model data = songListVm.get(position);
            view.setOnSettingBtnClick(new AlbumListItemSettingClickListener(data, position));
            view.setOnClickListener(new AlbumListItemClickListener(data, position));
        }

        private void updateSongItemView(SongView view, int position) {
            view.setModel(songListVm.get(position));
        }

    }

    private class AlbumListItemClickListener implements View.OnClickListener {
        SongView.Model data;
        int position;

        public AlbumListItemClickListener(SongView.Model data, int position) {
            this.data = data;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            int songIndex = localSongListState != null ? songList.size() - position - 1 : position;

            App.switchSong(songList.get(songIndex));
        }
    }

    private class AlbumListItemSettingClickListener implements View.OnClickListener {
        SongView.Model data;
        int position;

        public AlbumListItemSettingClickListener(SongView.Model data, int position) {
            this.data = data;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            View rootView = getRootView();
            final BottomPopup popup = new BottomPopup(getApplicationContext(), rootView);
            popup.setContent(R.layout.menu_album_item_setting);

            popup.setItemText(R.id.menu_album_item_setting_title, "歌曲: "+data.getName());
            popup.setItemClick(R.id.menu_album_item_next_btn, new NextClickListener());
            popup.setItemClick(R.id.menu_album_item_collect_btn, new CollectClickListener());
            popup.setItemClick(R.id.menu_album_item_download_btn, new DownloadClickListener());
            popup.setItemClick(R.id.menu_album_item_comment_btn, new CommentClickListener());
            popup.setItemClick(R.id.menu_album_item_share_btn, new ShareClickListener());
            popup.setItemClick(R.id.menu_album_item_singer_btn, new SingerClickListener());
            popup.setItemClick(R.id.menu_album_item_album_btn, new AlbumClickListener());
            popup.setItemClick(R.id.menu_album_item_video_btn, new VideoClickListener());
            popup.setItemClick(R.id.menu_album_item_ring_btn, new RingClickListener());
            popup.setItemClick(R.id.menu_album_item_delete_btn, new DeleteClickListener());

            popup.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }

        class NextClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {

            }
        }

        class CollectClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                SelectSongListDialog dialog = new SelectSongListDialog(AlbumActivity.this);
                dialog.setSong(songList.get(position));
                dialog.show();
            }
        }

        class DownloadClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {

            }
        }

        class CommentClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {

            }
        }

        class ShareClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {

            }
        }

        class SingerClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {

            }
        }

        class AlbumClickListener implements View.OnClickListener{
            @Override
            public void onClick(View v) {

            }
        }

        class VideoClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {

            }
        }

        class RingClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {

            }
        }

        class DeleteClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                // 从第一个列表删除, 删除是倒序的
                SongListSetState setState = new SongListSetState();
                for (ListInfo i : setState.get()) {
                    if (i.listId.equals(listId)) {
                        i.localSongListState.remove(songList.get(songList.size() - position - 1));
                        i.localSongListState.notifyToAll();
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localSongListState != null)
            localSongListState.unSubscribe();
    }
}
