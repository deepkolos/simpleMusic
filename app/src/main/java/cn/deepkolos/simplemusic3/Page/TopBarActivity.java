package cn.deepkolos.simplemusic3.Page;

import android.support.v4.app.FragmentActivity;
import android.view.View;

public class TopBarActivity extends FragmentActivity {
    public void backBtnClick(View view) {
        onBackPressed();
    }

    public void onSearchBtnClick (View view) {

    }

    public void onMenuBtnClick (View view) {

    }

    public void onShareBtnClick (View view) {

    }

    public View getRootView () {
        return getWindow().getDecorView().findViewById(android.R.id.content);
    }
}
