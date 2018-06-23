package cn.deepkolos.simplemusic3.Widget.Utils;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import cn.deepkolos.simplemusic3.R;

public class LoadingUtils {
    public static void init (final View $loading) {
        $loading.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView textView = $loading.findViewById(R.id.view_loading_textView);
                Drawable drawables[] = textView.getCompoundDrawables();
                AnimationDrawable anim = (AnimationDrawable) drawables[0];
                anim.start();
            }
        },0);
    }
}
