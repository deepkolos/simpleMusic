package cn.deepkolos.simplemusic3.Page.Main;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.Model.ListInfo;
import cn.deepkolos.simplemusic3.Model.Raw.HotMusicList;
import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.Page.AlbumActivity;
import cn.deepkolos.simplemusic3.Page.LocalMusicActivity;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.State.BaseState;
import cn.deepkolos.simplemusic3.State.CollectListSetState;
import cn.deepkolos.simplemusic3.State.SongListSetState;
import cn.deepkolos.simplemusic3.Storage.Loader.ImageLoader;
import cn.deepkolos.simplemusic3.Utils.Callback;
import cn.deepkolos.simplemusic3.Utils.UiUtils;
import cn.deepkolos.simplemusic3.Widget.BottomPopup;
import cn.deepkolos.simplemusic3.Widget.Dialog.CreateSongListDialog;
import cn.deepkolos.simplemusic3.Widget.ListItem.AlbumView;

public class SongListFragment extends Fragment {
    ExpandableListView $expandListView;
    ScrollView $scrollView;
    List<String> gData;
    List<AlbumView.Model> tData;
    List<List<AlbumView.Model>> iData;
    List<View> groupViews;
    MyBaseExpandableListAdapter adapter;
    View rootView;

    SongListSetState songListSetState;
    CollectListSetState collectListSetState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_songlist, container, false);
        init(rootView);
        bindEvent(rootView);
        return rootView;
    }

    private void init(View view) {
        $expandListView = view.findViewById(R.id.songList_customList);
        $scrollView = rootView.findViewById(R.id.fragment_songList_scrollView);

        groupViews = new ArrayList<>();
        gData = new ArrayList<>();
        iData = new ArrayList<>();

        loadSongList();
        // 处理收藏列表
        loadCollectList();

        adapter = new MyBaseExpandableListAdapter();
        $expandListView.setAdapter(adapter);

        $expandListView.expandGroup(0);
        if (gData.size() == 2)
            $expandListView.expandGroup(1);
        $expandListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                UiUtils.updateExpandViewHeight($expandListView);
            }
        },0);

        $expandListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                View groupView = groupViews.get(groupPosition);
                ImageView indicator = groupView.findViewById(R.id.songList_album_group_expand_indicator);
                rotateAnimation(indicator, 90f, 0f);
                UiUtils.updateExpandViewHeight($expandListView);
            }
        });

        $expandListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                View groupView = groupViews.get(groupPosition);
                ImageView indicator = groupView.findViewById(R.id.songList_album_group_expand_indicator);
                rotateAnimation(indicator, 0f, 90f);
                UiUtils.updateExpandViewHeight($expandListView);
            }
        });

        $scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                $scrollView.setScrollY(0);
            }
        }, 0);
    }

    private List<AlbumView.Model> toVm (List<ListInfo> playlist) {
        List<AlbumView.Model> vm = new ArrayList<>();

        for (ListInfo info: playlist) {
            vm.add(new AlbumView.Model(info.type, info.name, info.localSongListState.get().size()));
        }
        return vm;
    }

    private List<AlbumView.Model> toCollectVm (List<ListInfo> playlist) {
        List<AlbumView.Model> vm = new ArrayList<>();

        for (ListInfo info: playlist) {
            vm.add(new AlbumView.Model(info.type, info.name, info.desc));
        }
        return vm;
    }

    public void loadSongList () {
        gData.add("创建的歌单");

        songListSetState = new SongListSetState();
        songListSetState.setOnUpdate(new BaseState.OnUpdateFunc() {
            @Override
            public void call() {
                iData.add(0, toVm(songListSetState.get()));
                adapter.notifyDataSetChanged();
                $expandListView.collapseGroup(0);
                $expandListView.expandGroup(0);
            }
        });
        tData = toVm(songListSetState.get());
        iData.add(tData);
    }

    private void loadCollectList () {
        collectListSetState = new CollectListSetState();
        collectListSetState.setOnUpdate(new BaseState.OnUpdateFunc() {
            @Override
            public void call() {
                List<ListInfo> listInfos = collectListSetState.get();
                iData.add(1, toCollectVm(listInfos));
                $expandListView.collapseGroup(1);
                $expandListView.expandGroup(1);

                if (listInfos.size() == 0 && gData.size() == 2)
                    gData.remove(1);

                if (listInfos.size() != 0 && gData.size() == 1) {
                    gData.add("收藏的歌单");
                }

                adapter.notifyDataSetChanged();
            }
        });

        if (collectListSetState.get().size() != 0 && gData.size() == 1) {
            gData.add("收藏的歌单");
        }

        tData = toCollectVm(collectListSetState.get());
        iData.add(tData);
    }

    private void rotateAnimation(View view, float from, float to) {
        RotateAnimation animation = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(200);
        view.startAnimation(animation);
    }

    private class MyBaseExpandableListAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return gData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return iData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return gData.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return iData.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view;
            if (null == convertView) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.view_songlist_album_group, parent, false);
                if (groupPosition == 0) {
                    View indicator = view.findViewById(R.id.songList_album_group_expand_indicator);
                    rotateAnimation(indicator, 0f, 90f);
                }
            } else {
                view = convertView;
            }

            groupViews.add(groupPosition, view);
            String title = (String) getGroup(groupPosition);
            TextView $title = view.findViewById(R.id.songList_album_group_title);
            $title.setText(title);

            ImageView settingBtn = view.findViewById(R.id.songList_album_group_setting);
            settingBtn.setOnClickListener(new ExpandGroupSettingClickListener());
            return view;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final AlbumView view;
            Context context = getContext();
            final AlbumView.Model data = (AlbumView.Model) getChild(groupPosition, childPosition);
            ListInfo info = null;
            if (groupPosition == 0)
                info = songListSetState.get().get(childPosition);
            else if (groupPosition == 1)
                info = collectListSetState.get().get(childPosition);
            
            if (null == convertView) {
                view = new AlbumView(context);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListInfo info = null;
                        if (groupPosition == 0)
                            info = songListSetState.get().get(childPosition);
                        else if (groupPosition == 1) {
                            info = collectListSetState.get().get(childPosition);
                        }
                        Intent intent = new Intent();
                        intent.setClass(getContext(), AlbumActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(AlbumActivity.PLAYLIST_ID, info.listId);
                        bundle.putBoolean(AlbumActivity.IS_LOCAL, groupPosition == 0);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                view.setOnSettingBtnClick(new ExpandItemSettingClickListener(data, groupPosition, childPosition));
            } else {
                view = (AlbumView) convertView;
            }

            // 获取最新添加首歌的封面地址
            String coverSrc = ImageLoader.DEFAULT_COVER_CACHE_KEY;
            if (groupPosition == 0) {
                if (info.localSongListState.get().size() != 0) {
                    Song song = info.localSongListState.get().get(info.localSongListState.get().size() - 1);
                    coverSrc = song.coverSrc;
                }
            } else if (groupPosition == 1) {
                coverSrc = collectListSetState.get().get(childPosition).coverSrc;
            }


            ImageLoader.get(coverSrc, new Callback<Bitmap>() {
                @Override
                public void call(final Bitmap val) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setCover(val);
                        }
                    });
                }
            });

            view.setTitle(data.getTitle());

            if (groupPosition == 0)
                view.setSongNum(data.getSongNum());
            else if (groupPosition == 1)
                view.setSubTitle(data.getSubTitle());
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    private class ExpandItemSettingClickListener implements View.OnClickListener {
        AlbumView.Model data;
        int groupPosition, childPosition;

        public ExpandItemSettingClickListener(AlbumView.Model data, int groupPosition, int childPosition) {
            this.data = data;
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
        }

        @Override
        public void onClick(View v) {
            final BottomPopup popup = new BottomPopup(getContext(), rootView);
            popup.setContent(R.layout.menu_songlist_item_type_setting);

            if (data.getType() == 0) {
                popup.setItemVisibility(R.id.menu_songlist_item_setting_edit_info_btn, View.GONE);
                popup.setItemVisibility(R.id.menu_songlist_item_setting_delete_btn, View.GONE);
            }

            popup.setItemText(R.id.menu_songlist_item_setting_title, "歌单: "+data.getTitle());

            popup.setItemClick(R.id.menu_songlist_item_setting_download_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "download", Toast.LENGTH_SHORT).show();
                }
            });

            popup.setItemClick(R.id.menu_songlist_item_setting_share_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "share", Toast.LENGTH_SHORT).show();
                }
            });

            popup.setItemClick(R.id.menu_songlist_item_setting_edit_info_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "edit", Toast.LENGTH_SHORT).show();
                }
            });

            popup.setItemClick(R.id.menu_songlist_item_setting_delete_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (groupPosition == 0) {
                        ListInfo info = songListSetState.get().get(childPosition);
                        if (info.type != 0) {
                            songListSetState.remove(info);
                            songListSetState.notifyToAll();
                        }
                    } else {
                        collectListSetState.remove(collectListSetState.get().get(childPosition));
                        collectListSetState.notifyToAll();
                    }


//                    Toast.makeText(getContext(), "delete", Toast.LENGTH_SHORT).show();
                }
            });

            $expandListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    popup.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                }
            }, 120);
        }
    }

    private class ExpandGroupSettingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final BottomPopup popup = new BottomPopup(getContext(), rootView);
            popup.setContent(R.layout.menu_songlist_group_setting);

            popup.setItemClick(R.id.songlist_group_setting_create_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "create", Toast.LENGTH_SHORT).show();
                    CreateSongListDialog dialog = new CreateSongListDialog(getContext());
                    dialog.setOnConfirm(new Callback<String>() {
                        @Override
                        public void call(String val) {
                            songListSetState.add(val, "");
                            songListSetState.notifyToAll();
                        }
                    });
                    dialog.show();
                }
            });

            popup.setItemClick(R.id.songlist_group_setting_manage_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "manage", Toast.LENGTH_SHORT).show();
                }
            });

            popup.setItemClick(R.id.songlist_group_setting_recover_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "recover", Toast.LENGTH_SHORT).show();
                }
            });

            popup.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
    }

    private void startActivity (Class activity) {
        Intent intent = new Intent();
        intent.setClass(getContext(), activity);
        startActivity(intent);
    }

    private void bindEvent(View view) {
        view.findViewById(R.id.fragment_songList_local_music_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(LocalMusicActivity.class);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        songListSetState.subscribe();
        collectListSetState.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        songListSetState.unSubscribe();
        collectListSetState.unSubscribe();
    }
}
