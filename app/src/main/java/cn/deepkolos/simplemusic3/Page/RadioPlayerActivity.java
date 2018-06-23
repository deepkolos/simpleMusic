package cn.deepkolos.simplemusic3.Page;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.deepkolos.simplemusic3.R;

public class RadioPlayerActivity extends TopBarActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_player);
    }
}
