package cn.deepkolos.simplemusic3.Page.CloudMusic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.deepkolos.simplemusic3.Page.Utils.ViewPagerFragment;
import cn.deepkolos.simplemusic3.R;

public class RadioStationFragment extends ViewPagerFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_not_get_done, container, false);
        return view;
    }
}
