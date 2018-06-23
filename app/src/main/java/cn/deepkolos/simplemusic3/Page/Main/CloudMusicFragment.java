package cn.deepkolos.simplemusic3.Page.Main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.Page.CloudMusic.FriendsFragment;
import cn.deepkolos.simplemusic3.Page.CloudMusic.RadioStationFragment;
import cn.deepkolos.simplemusic3.Page.CloudMusic.RecommendationFragment;
import cn.deepkolos.simplemusic3.Page.Utils.ViewPagerFragment;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Widget.TextTabsView;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;

public class CloudMusicFragment extends Fragment {
    ViewPager viewPager;
    TextTabsView textTabsView;
    boolean initialed;
    int tmpTab, currTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloudmusic, container, false);

        initialed = false;
        initViewPager(view);
        initTab(view);
        Log.i("debug", "view create");
        return view;
    }

    private void initViewPager (View view) {
        viewPager = view.findViewById(R.id.cloudMusic_viewPager);
        final CloudMusicViewPagerAdapter pagerAdapter = new CloudMusicViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(new CloudMusicViewPagerAdapter(getFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tmpTab = position;
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
        textTabsView = view.findViewById(R.id.cloudMusic_tab);
        textTabsView.setViewPager(viewPager);
        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("推荐");
        tabs.add("朋友");
        tabs.add("电台");
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

    private class CloudMusicViewPagerAdapter extends FragmentPagerAdapter {
        public List<ViewPagerFragment> fragments;

        public CloudMusicViewPagerAdapter(FragmentManager fm) {
            super(fm);

            fragments = new ArrayList<>();
            fragments.add(new RecommendationFragment());
            fragments.add(new FriendsFragment());
            fragments.add(new RadioStationFragment());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.i("debug", "view create");
    }
}
