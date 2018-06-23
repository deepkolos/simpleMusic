package cn.deepkolos.simplemusic3.Page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.Page.LocalMusic.AlbumFragment;
import cn.deepkolos.simplemusic3.Page.LocalMusic.DirectoryFragment;
import cn.deepkolos.simplemusic3.Page.LocalMusic.SingerFragment;
import cn.deepkolos.simplemusic3.Page.LocalMusic.SongFragment;
import cn.deepkolos.simplemusic3.Page.Utils.ViewPagerFragment;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;
import cn.deepkolos.simplemusic3.Widget.TextTabsView;

public class LocalMusicActivity extends TopBarActivity {
    TextTabsView textTabsView;
    ViewPager viewPager;
    boolean initialed;
    LocalMusicViewPagerAdapter pagerAdapter;
    int tmpTab;
    int currTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        initViewPager(rootView);
        initTab(rootView);
    }

    private void initViewPager (View view) {
        viewPager = view.findViewById(R.id.activity_local_music_viewPager);
        pagerAdapter = new LocalMusicViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        currTab = 0;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tmpTab = position;
                Log.i("viewPager","pager selected:"+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    currTab = tmpTab;
                    pagerAdapter.fragments.get(currTab).onSelectedListener();
                }
            }
        });
    }

    private void initTab (View view) {
        textTabsView = view.findViewById(R.id.activity_local_music_tab);
        textTabsView.setViewPager(viewPager);
        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("单曲");
        tabs.add("歌手");
        tabs.add("专辑");
        tabs.add("文件夹");
        textTabsView.setTabTexts(tabs);
        textTabsView.setLineOffsetY(UnitHelper.dpToPx(33));

        textTabsView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (!initialed) {
                    initialed = true;
                    textTabsView.applyChange();
                }

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        pagerAdapter.fragments.get(0).onSelectedListener();
    }

    private class LocalMusicViewPagerAdapter extends FragmentPagerAdapter {
        List<ViewPagerFragment> fragments;

        public LocalMusicViewPagerAdapter(FragmentManager fm) {
            super(fm);

            fragments = new ArrayList<>();
            fragments.add(new SongFragment());
            fragments.add(new SingerFragment());
            fragments.add(new AlbumFragment());
            fragments.add(new DirectoryFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }


}

