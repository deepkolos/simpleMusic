package cn.deepkolos.simplemusic3.Utils;

import android.content.Context;
import android.view.View;

import cn.deepkolos.simplemusic3.R;

public class RippleHelper {
    public static void applyBtn (View view, Context context) {
        view.setFocusable(true);
        view.setClickable(true);
        view.setBackground(context.getDrawable(R.drawable.ripple_27_btn));
    }

    public static void applyBar (View view, Context context) {
        view.setFocusable(true);
        view.setClickable(true);
        view.setBackground(context.getDrawable(R.drawable.ripple_bar));
    }
}
