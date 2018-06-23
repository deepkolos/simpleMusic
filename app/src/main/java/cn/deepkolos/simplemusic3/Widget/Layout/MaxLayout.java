package cn.deepkolos.simplemusic3.Widget.Layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cn.deepkolos.simplemusic3.R;

public class MaxLayout extends LinearLayout {
    private int mMaxHeight = -1;
    private int mMaxWidth = -1;
//    private int mMinHeight = -1;
//    private int mMinWidth = -1;

    public MaxLayout(Context context) {
        super(context);
    }

    public MaxLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MaxMinHeightWidth);
        mMaxHeight = ta.getDimensionPixelOffset(R.styleable.MaxMinHeightWidth_maxHeight, -1);
        mMaxWidth = ta.getDimensionPixelOffset(R.styleable.MaxMinHeightWidth_maxWidth, -1);
//        mMinHeight = ta.getDimensionPixelOffset(R.styleable.MaxMinHeightWidth_minHeight, -1);
//        mMinWidth = ta.getDimensionPixelOffset(R.styleable.MaxMinHeightWidth_minWidth, -1);
        ta.recycle();
    }

    public void setMaxHeight(int maxHeight) {
        mMaxHeight = maxHeight;
    }

    public void setMaxWidth (int maxWidth) {
        mMaxWidth = maxWidth;
    }

//    public void setMinHeight (int minHeight) {
//        mMinHeight = minHeight;
//    }
//
//    public void setMinWidth (int minWidth) {
//        mMinWidth = minWidth;
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight != -1)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        if (mMaxWidth != -1)
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
