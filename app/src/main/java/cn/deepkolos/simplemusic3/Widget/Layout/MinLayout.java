package cn.deepkolos.simplemusic3.Widget.Layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import cn.deepkolos.simplemusic3.R;

public class MinLayout extends FrameLayout {
    private int mMinHeight = -1;
    private int mMinWidth = -1;

    public MinLayout(@NonNull Context context) {
        super(context);
    }

    public MinLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MaxMinHeightWidth);
        mMinHeight = ta.getDimensionPixelOffset(R.styleable.MaxMinHeightWidth_minHeight, -1);
        mMinWidth = ta.getDimensionPixelOffset(R.styleable.MaxMinHeightWidth_minWidth, -1);
        ta.recycle();
    }

    public void setMinHeight (int minHeight) {
        mMinHeight = minHeight;
    }

    public void setMinWidth (int minWidth) {
        mMinWidth = minWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);

        if (mMinHeight != -1 && modeH == MeasureSpec.AT_MOST && sizeH < mMinHeight)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMinHeight, MeasureSpec.AT_MOST);

        if (mMinWidth != -1 && modeW == MeasureSpec.AT_MOST && sizeW < mMinWidth)
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMinWidth, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
