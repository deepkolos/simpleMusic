package cn.deepkolos.simplemusic3.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean r = super.onTouchEvent(ev);
        Log.i("debug", "sv touch return:"+r+" ev:" + ev.toString());
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean r = super.onInterceptTouchEvent(ev);
        Log.i("debug", "sv intercept return:"+r+" ev:" + ev.toString());

        return r;
    }

}
