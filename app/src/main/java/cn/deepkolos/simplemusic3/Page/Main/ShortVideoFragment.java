package cn.deepkolos.simplemusic3.Page.Main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.deepkolos.simplemusic3.Page.CloudMusic.FriendsFragment;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Widget.TextTabsView;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;

public class ShortVideoFragment extends Fragment {
    ViewPager viewPager;
    TextTabsView textTabsView;
    boolean initialed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shortvideo,container,false);

        initialed = false;
        initViewPager(view);
        initTab(view);
        return view;
    }

    private void initViewPager(View view) {
        viewPager = view.findViewById(R.id.shortVideo_viewPager);
        viewPager.setAdapter(new ShortVideoViewPagerAdapter(getFragmentManager()));
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
    }

    private void initTab (View view) {
        textTabsView = view.findViewById(R.id.shortVideo_tab);
        textTabsView.setViewPager(viewPager);
        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("推荐");
        tabs.add("音乐");
        tabs.add("Showtime");
        tabs.add("MV");
        tabs.add("二次元");
        tabs.add("舞蹈");
        tabs.add("游戏");
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

    private class ShortVideoViewPagerAdapter extends FragmentPagerAdapter {
        public ShortVideoViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new FriendsFragment();
                    break;
                case 1:
                    fragment = new FriendsFragment();
                    break;
                case 2:
                    fragment = new FriendsFragment();
                    break;
                case 3:
                    fragment = new FriendsFragment();
                    break;
                case 4:
                    fragment = new FriendsFragment();
                    break;
                case 5:
                    fragment = new FriendsFragment();
                    break;
                case 6:
                    fragment = new FriendsFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 7;
        }
    }
}
