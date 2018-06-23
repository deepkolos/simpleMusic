package cn.deepkolos.simplemusic3.Page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.deepkolos.simplemusic3.App;
import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.State.BaseState;
import cn.deepkolos.simplemusic3.State.ModeState;
import cn.deepkolos.simplemusic3.State.PlayState;
import cn.deepkolos.simplemusic3.State.PlayingSongState;
import cn.deepkolos.simplemusic3.Storage.Loader.ImageLoader;
import cn.deepkolos.simplemusic3.Utils.Callback;
import cn.deepkolos.simplemusic3.Widget.BottomPlayBar;
import cn.deepkolos.simplemusic3.Widget.Button.PlayListButton;
import cn.deepkolos.simplemusic3.Widget.Button.PlayPauseButton;
import cn.deepkolos.simplemusic3.Widget.PlaylistBottomPopup;

public class PlayBarFragment extends Fragment implements App.MusicEventListener{
    BottomPlayBar $bottomBar;
    LinearLayout $playBarContainer;
    PlayPauseButton $playPauseButton;
    PlayListButton $playListButton;

    PlayState playState;
    PlayingSongState playingSongState;
    ModeState modeState;
    Song currSong;
    boolean isPlaying;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_bar, container, false);

        $bottomBar = view.findViewById(R.id.fragment_player_bar_bottomPlayer);
        $bottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
                startActivity(intent);
            }
        });

        $playBarContainer = view.findViewById(R.id.fragment_player_bar_container);

        $bottomBar.setTouchDelegation(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                $playBarContainer.onTouchEvent(event);
                return false;
            }
        });

        $playPauseButton = view.findViewById(R.id.fragment_player_bar_playPauseBtn);
        $playListButton = view.findViewById(R.id.fragment_player_bar_playListBtn);

        $playListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1 = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
                final PlaylistBottomPopup popup = new PlaylistBottomPopup(getContext(), view1);
                $playListButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popup.show();
                    }
                }, 100);
            }
        });

        $playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.toggle();
            }
        });

        currSong = playingSongState.get();
        notifyDataChanged();
        playState.notifyDataChanged(false);
        return view;
    }

    private void notifyDataChanged () {
        final BottomPlayBar.BottomPlayBarSongItem item;
        item = (BottomPlayBar.BottomPlayBarSongItem) $bottomBar.getPageAt(1);

        if (currSong == null) return;

        ImageLoader.get(currSong.coverSrc, new Callback<Bitmap>() {
            @Override
            public void call(final Bitmap val) {
                item.post(new Runnable() {
                    @Override
                    public void run() {
                        item.setCover(val);
                    }
                });
            }
        });
        item.setSongSinger(currSong.singer);
        item.setSongName(currSong.name);
    }

    @Override
    public void onMusicPlay() {}

    @Override
    public void onMusicPause() {}

    @Override
    public void onMusicSwitchIn(Song song) {

    }

    @Override
    public void onMusicSwitchOut(Song song) {

    }

    @Override
    public void onMusicModeChange(int mode) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modeState = new ModeState();
        playingSongState = new PlayingSongState();
        playingSongState.setOnUpdate(new BaseState.OnUpdateFunc() {
            @Override
            public void call() {
                currSong = playingSongState.get();
                notifyDataChanged();
            }
        });
        playState = new PlayState();
        playState.setOnUpdate(new BaseState.OnUpdateFunc() {
            @Override
            public void call() {
                isPlaying = playState.get();
                if (isPlaying) {
                    $playPauseButton.play();
                } else {
                    $playPauseButton.pause();
                }
            }
        });
        playState.subscribe();
        modeState.subscribe();
        playingSongState.subscribe();
        App.addMusicListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playState.unSubscribe();
        modeState.unSubscribe();
        playingSongState.unSubscribe();
        App.removeMusicListener(this);
    }

    @Override
    public void onMusicProgressUpdate(float progress) {
        int deg = (int) (progress * 360);
        $playPauseButton.setProgress(deg);
        Log.i("debug","update progress");
    }
}
