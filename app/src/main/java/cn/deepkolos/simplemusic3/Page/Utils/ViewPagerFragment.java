package cn.deepkolos.simplemusic3.Page.Utils;

import android.support.v4.app.Fragment;

public class ViewPagerFragment extends Fragment {
    private boolean isFirstSelected = false;

    public void onFirstSelected() {}

    public void onSelected () {}

    public void onSelectedListener () {
        if (!isFirstSelected) {
            isFirstSelected = true;
            onFirstSelected();
        }

        onSelected();
    }
}
