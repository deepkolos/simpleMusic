package cn.deepkolos.simplemusic3.Page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.App;
import cn.deepkolos.simplemusic3.Page.Main.CloudMusicFragment;
import cn.deepkolos.simplemusic3.Page.Main.ShortVideoFragment;
import cn.deepkolos.simplemusic3.Page.Main.SongListFragment;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;

public class MainActivity extends TopBarActivity {
    DrawerLayout $drawer;
    ViewPager mainVp;
    List<ImageView> tabs = new ArrayList<>();
    int currentTab = 0;
    int tmpTab = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.verifyStoragePermissions(this);

        UnitHelper.init(this);
        setContentView(R.layout.activity_main);

        initDrawer();
        initTabs();
        initViewPager();
        initBottomBar();
    }

    private void initDrawer() {
        $drawer = findViewById(R.id.activity_main_drawer_layout);
    }

    public void initViewPager () {
        mainVp = findViewById(R.id.main_viewPager);
        FirstViewPagerAdapter firstViewPagerAdapter = new FirstViewPagerAdapter(getSupportFragmentManager());
        mainVp.setAdapter(firstViewPagerAdapter);
        mainVp.setOffscreenPageLimit(firstViewPagerAdapter.getCount());

        mainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                tabs.get(tmpTab).setSelected(false);
                tabs.get(position).setSelected(true);
                tmpTab = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0)
                    currentTab = tmpTab;
            }
        });
    }

    public void initTabs() {
        tabs.add((ImageView) findViewById(R.id.tab_songList));
        tabs.add((ImageView) findViewById(R.id.tab_cloudMusic));
        tabs.add((ImageView) findViewById(R.id.tab_shortVideo));

        int i = 0;
        for (View view:tabs) {
            final int _i = i++;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainVp.setCurrentItem(_i, true);
                }
            });
        }

        tabs.get(0).setSelected(true);
    }

    public void initBottomBar() {

    }


    @Override
    public void onBackPressed() {
        if ($drawer.isDrawerOpen(GravityCompat.START)) {
            $drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public class FirstViewPagerAdapter extends FragmentPagerAdapter {
        public FirstViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new SongListFragment();
                break;
                case 1:
                    fragment = new CloudMusicFragment();
                    break;
                case 2:
                    fragment = new ShortVideoFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabs.size();
        }
    }

    @Override
    public void onMenuBtnClick(View view) {
        $drawer.openDrawer(GravityCompat.START);
    }
}
