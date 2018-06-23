package cn.deepkolos.simplemusic3.Widget.Layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;

public class ClipFrameLayout extends FrameLayout {
    private int topLeftRadius = 0;
    private int topRightRadius = 0;
    private int bottomLeftRadius = 0;
    private int bottomRightRadius = 0;
    private Path path = new Path();
    private RectF topLeftRect = new RectF();
    private RectF topRightRect = new RectF();
    private RectF bottomLeftRect = new RectF();
    private RectF bottomRightRect = new RectF();
    private int insetTop = 0;
    private int insetLeft = 0;
    private int insetRight = 0;
    private int insetBottom = 0;

    Paint paint = new Paint();

    public ClipFrameLayout(Context context) {
        super(context);
        init();
    }

    public ClipFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClipLayoutAttrs);
        int radius = ta.getDimensionPixelOffset(R.styleable.ClipLayoutAttrs_radius, -1);

        if (radius != -1) {
            topLeftRadius = radius;
            topRightRadius = radius;
            bottomLeftRadius = radius;
            bottomRightRadius = radius;
        }

        int _topLeftRadius = ta.getDimensionPixelOffset(R.styleable.ClipLayoutAttrs_topLeftRadius, -1);
        if (_topLeftRadius != -1) topLeftRadius = _topLeftRadius;

        int _topRightRadius = ta.getDimensionPixelOffset(R.styleable.ClipLayoutAttrs_topRightRadius, -1);
        if (_topRightRadius != -1) topRightRadius = _topRightRadius;

        int _bottomRightRadius = ta.getDimensionPixelOffset(R.styleable.ClipLayoutAttrs_bottomRightRadius, -1);
        if (_bottomRightRadius != -1) bottomRightRadius = _bottomRightRadius;

        int _bottomLeftRadius = ta.getDimensionPixelOffset(R.styleable.ClipLayoutAttrs_bottomLeftRadius, -1);
        if (_bottomLeftRadius != -1) topRightRadius = _bottomLeftRadius;

        int _inset = ta.getDimensionPixelOffset(R.styleable.ClipLayoutAttrs_inset, -1);
        if (_inset != -1) {
            insetTop = _inset;
            insetLeft = _inset;
            insetRight = _inset;
            insetBottom = _inset;
        }

        int _insetTop = ta.getDimensionPixelOffset(R.styleable.ClipLayoutAttrs_topInset, -1);
        if (_insetTop != -1) insetTop = _insetTop;

        int _insetBottom = ta.getDimensionPixelOffset(R.styleable.ClipLayoutAttrs_bottomInset, -1);
        if (_insetBottom != -1) insetBottom = _insetBottom;

        int _insetLeft = ta.getDimensionPixelOffset(R.styleable.ClipLayoutAttrs_leftInset, -1);
        if (_insetLeft != -1) insetLeft = _insetLeft;

        int _insetRight = ta.getDimensionPixelOffset(R.styleable.ClipLayoutAttrs_rightInset, -1);
        if (_insetRight != -1) insetRight = _insetRight;

        ta.recycle();

        init();
    }

    private void init() {
        setWillNotDraw(false);
    }

    public void setRadius (int all) {
        topLeftRadius = all;
        topRightRadius = all;
        bottomLeftRadius = all;
        bottomRightRadius = all;
        postInvalidate();
    }

    public void setRadius (int topLeft, int topRight, int bottomLeft, int bottomRight) {
        topLeftRadius = topLeft;
        topRightRadius = topRight;
        bottomLeftRadius = bottomLeft;
        bottomRightRadius = bottomRight;
        postInvalidate();
    }

    public void setInset (int all) {
        insetTop = all;
        insetLeft = all;
        insetRight = all;
        insetBottom = all;
        postInvalidate();
    }

    public void setInset (int left, int top, int right, int bottom) {
        insetTop = top;
        insetLeft = left;
        insetRight = right;
        insetBottom = bottom;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // init arc rect
        int height = getHeight();
        int width = getWidth();
        topLeftRect.set(insetLeft, insetTop, topLeftRadius*2 + insetLeft, topLeftRadius*2 + insetTop);
        topRightRect.set(width - (topRightRadius*2 + insetRight), insetTop, width - insetRight, topRightRadius*2+insetTop);
        bottomLeftRect.set(insetLeft, height - (bottomLeftRadius*2 + insetBottom), bottomLeftRadius*2 + insetLeft, height - insetBottom);
        bottomRightRect.set(width - (bottomRightRadius*2 + insetRight), height - (bottomRightRadius*2 + insetBottom), width - insetRight, height - insetBottom);

        // draw clip-path
        path.moveTo(insetLeft, topLeftRadius+insetTop);
        path.arcTo(topLeftRect, 180, 90, false);
        path.lineTo(width - (topRightRadius + insetRight),insetTop);
        path.arcTo(topRightRect, 270, 90, false);
        path.lineTo(width - insetRight, height - (bottomRightRadius + insetBottom));
        path.arcTo(bottomRightRect, 0, 90, false);
        path.lineTo(bottomLeftRadius+insetLeft, height - insetBottom);
        path.arcTo(bottomLeftRect, 90, 90, false);
        path.lineTo(insetLeft, topLeftRadius+insetTop);
        path.close();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(UnitHelper.dpToPx(2));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        canvas.clipPath(path);

    }
}
