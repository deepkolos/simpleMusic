package cn.deepkolos.simplemusic3.Page;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.State.BaseState;
import cn.deepkolos.simplemusic3.State.PlayState;
import cn.deepkolos.simplemusic3.State.PlayingSongState;
import cn.deepkolos.simplemusic3.Storage.Loader.ImageLoader;
import cn.deepkolos.simplemusic3.Utils.Callback;
import cn.deepkolos.simplemusic3.Utils.ImageUtils;
import cn.deepkolos.simplemusic3.Widget.Menu.LyricMenu;
import cn.deepkolos.simplemusic3.Widget.OverRollTextView;
import cn.deepkolos.simplemusic3.Widget.SimpleViewPager;
import cn.deepkolos.simplemusic3.Widget.TurnTableHandView;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;
import cn.deepkolos.simplemusic3.Utils.UiUtils.DisplaySwitcher;
import cn.deepkolos.simplemusic3.Widget.TurntableView;

public class MusicPlayerActivity extends TopBarActivity {
    SimpleViewPager $albumViewPager;
    TurnTableHandView $turnTableHand;
    FrameLayout $turnTable;
    FrameLayout $lyrics;
    FrameLayout $root;
    DisplaySwitcher turnTableSwitcher;
    DisplaySwitcher lyricsSwitcher;
    DisplaySwitcher bgSwitcher;
    ImageView $lyricMenuBtn;
    ImageView $bg;
    OverRollTextView $name;
    TextView $singer;

    PlayingSongState playingSongState;
    PlayState playState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_player);
        $albumViewPager = findViewById(R.id.activity_music_player_turntable_viewpager);
        $turnTableHand = findViewById(R.id.activity_music_player_turntable_hand);
        $root = findViewById(R.id.activity_music_player);

        $albumViewPager.setOnScrollStart(new SimpleViewPager.onScrollStart() {
            @Override
            public void call() {
                Log.i("debug", "putOffTrack");
                $turnTableHand.putOffTrack();
            }
        });
        $albumViewPager.setOnScrollEnd(new SimpleViewPager.onScrollEnd() {
            @Override
            public void call() {
                Log.i("debug", "putOnTrack");
                $turnTableHand.putOnTrack();
            }
        });
        $albumViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnTableSwitcher.hidden(true);
                lyricsSwitcher.show(true);
            }
        });
        $albumViewPager.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MusicPlayerActivity.this,"long clicked~", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        $turnTable = findViewById(R.id.activity_music_player_turntable);
        $lyrics = findViewById(R.id.activity_music_player_lyrics);
        $lyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnTableSwitcher.show(true);
                lyricsSwitcher.hidden(true);
            }
        });

        turnTableSwitcher = new DisplaySwitcher($turnTable, true);
        lyricsSwitcher = new DisplaySwitcher($lyrics, false);

        $lyricMenuBtn = findViewById(R.id.activity_music_player_lyrics_menu);
        $lyricMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LyricMenu menu = new LyricMenu(MusicPlayerActivity.this, R.layout.view_lyric_menu);
                int[] location = {0, 0};
                $lyricMenuBtn.getLocationInWindow(location);
                DisplayMetrics dm = getResources().getDisplayMetrics();
                int height = dm.heightPixels;

                menu.showAtLocation($lyricMenuBtn, Gravity.BOTTOM|Gravity.END, UnitHelper.dpToPx(13),height - location[1] + UnitHelper.dpToPx(5));
            }
        });

        $bg = findViewById(R.id.activity_music_player_bg_blur);
        bgSwitcher = new DisplaySwitcher($bg, false);

        $name = findViewById(R.id.activity_music_player_topbar_song);
        $singer = findViewById(R.id.activity_music_player_topbar_singer);

        // 加载歌曲信息
        loadSongInfo();

        playState = new PlayState();
        playState.setOnUpdate(new BaseState.OnUpdateFunc() {
            @Override
            public void call() {
                if (playState.get()) {

                } else {

                }
            }
        });
        playState.notifyDataChanged(false);
    }

    private void loadSongInfo () {
        playingSongState = new PlayingSongState();
        // 这里还是需要从state里面去获取的
        Song song = playingSongState.get();
        if (song == null) return;

        $name.setText(song.name);
        $singer.setText(song.singer);

        ImageLoader.get(song.coverSrc, new Callback<Bitmap>() {
            @Override
            public void call(final Bitmap val) {
                int dstHeight = UnitHelper.getScreenHeight() / 5;
                int dstWidth = UnitHelper.getScreenWidth() / 5;
                final Bitmap blur = ImageUtils.blur(Bitmap.createScaledBitmap(val, dstWidth, dstHeight, false));

                $bg.post(new Runnable() {
                    @Override
                    public void run() {
                        $bg.setImageBitmap(blur);
                        bgSwitcher.show(true);
                        ViewGroup viewGroup = (ViewGroup) $albumViewPager.getPageAt(1);
                        TurntableView turntableView = (TurntableView) viewGroup.getChildAt(0);
                        turntableView.setSrc(val);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        playingSongState.subscribe();
        playState.subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playingSongState.unSubscribe();
        playState.unSubscribe();
    }
}
