package cn.deepkolos.simplemusic3.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;

import cn.deepkolos.simplemusic3.R;

import static cn.deepkolos.simplemusic3.Utils.UnitHelper.dpToPx;

public class SimpleViewPager extends ViewGroup {
    ArrayList<View> pages = new ArrayList<>();
    Scroller scroller;
    onNext onNextCb;
    onPrev onPrevCb;
    onScrollStart onScrollStart;
    onScrollEnd onScrollEnd;
    boolean initial = true;
    int scrollDuration = 350;
    static final int threshold = 6;
    static final int longPressThreshold = 500;
    static final int touchDelegationDelay = 35;
    boolean thresholdMatched;
    LongPressTimer longPressTimer;
    TouchDelegationDelayTimer touchDelegationDelayTimer;
    boolean longPressTriggered;
    int paddingStartEnd = 0;


    public SimpleViewPager(Context context) {
        super(context);
        init(context);
    }

    public SimpleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerAttrs);
        scrollDuration = ta.getInt(R.styleable.ViewPagerAttrs_duration, scrollDuration);

        ta.recycle();
    }

    private void init(Context context) {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setClickable(true);

        scroller = new Scroller(context);
    }

    public void setPaddingStartEnd(int paddingStartEnd) {
        this.paddingStartEnd = paddingStartEnd;
    }

    @Override
    public void addView(View child) {
        pages.add(child);
        super.addView(child);
    }

    @Override
    public void removeAllViews() {
        pages.clear();
        super.removeAllViews();
    }

    public View getPageAt(int index) {
        return pages.get(index);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cl, ct, cr, cb;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (initial && getChildCount() != 0) {
            for (int i = 0; i < getChildCount(); i++) {
                pages.add(getChildAt(i));
            }
            initial = true;
        }

        for (int i = 0, j; i < 3; i++) {
            if (i < pages.size()) {
                View item = pages.get(i);
                j = i - 1;
                cl = j * width + paddingStartEnd;
                ct = 0;
                cr = cl + width - paddingStartEnd;
                cb = ct + height;
                item.layout(cl, ct, cr, cb);
            }
        }
        scrollTo(0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    float scrollX = 0;
    float startX = 0;
    int currIndex = 0;
    boolean scrolling = false;
    boolean firstMove;
    OnTouchListener touchDelegation;

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        float currX = event.getRawX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = currX;
                firstMove = true;
                thresholdMatched = false;
                longPressTriggered = false;
                drawableHotspotChanged(event.getX(), event.getY());
                setPressed(true);

                if (scroller.isFinished()) {
                    // start long press timer
                    longPressTimer = new LongPressTimer();
                    touchDelegationDelayTimer = new TouchDelegationDelayTimer(event);
                    SimpleViewPager.this.postDelayed(longPressTimer, longPressThreshold);
                    SimpleViewPager.this.postDelayed(touchDelegationDelayTimer, touchDelegationDelay);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!scroller.isFinished()) {
                    startX = currX;
                    break;
                }

                if (!thresholdMatched && Math.abs(currX - startX) > threshold) {
                    thresholdMatched = true;
                    setPressed(false);
                    longPressTimer.disable();

                    if (!touchDelegationDelayTimer.isDisable()) {
                        touchDelegationDelayTimer.disable();

                        if (touchDelegationDelayTimer.isTriggered()) {
                            event.setAction(MotionEvent.ACTION_UP);
                            if (touchDelegation != null)
                                touchDelegation.onTouch(null, event);
                        }
                    }
                }

                if (!thresholdMatched || longPressTriggered) break;

                if (firstMove && onScrollStart != null) {
                    firstMove = false;
                    SimpleViewPager.this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onScrollStart.call();
                        }
                    }, 10);
                }

                scrollTo(Math.round(scrollX - (currX - startX)), 0);
                break;
            case MotionEvent.ACTION_UP:
                if (!scroller.isFinished()) break;

                if (touchDelegation != null)
                    touchDelegation.onTouch(null, event);

                if (!thresholdMatched || longPressTriggered) {
                    if (event.getEventTime() - event.getDownTime() < longPressThreshold) {
                        setPressed(false);
                        longPressTimer.disable();
                        SimpleViewPager.this.performClick();
                    }
                    break;
                }

                float dx;

                if (Math.abs(startX - currX) < dpToPx(50)) {
                    // 不切换
                    dx = currX - startX;
                } else {
                    // 切换
                    if (currX - startX > 0) {
                        currIndex--; // left
                    } else {
                        currIndex++; // right
                    }
                    dx = currIndex * getMeasuredWidth() - Math.round(scrollX - (currX - startX));
                }

                scroller.startScroll(Math.round(scrollX - (currX - startX)), 0, Math.round(dx), 0, scrollDuration);
                scrollX = Math.round(scrollX - (currX - startX)) + Math.round(dx);
                scrolling = true;
                break;
        }
        postInvalidate();
        return true;
    }

    class LongPressTimer implements Runnable {
        private boolean disable = false;

        public void disable() {
            disable = true;
        }

        @Override
        public void run() {
            if (!disable) {
                setPressed(false);
                SimpleViewPager.this.performLongClick();
                longPressTriggered = true;
            }
        }
    }

    class TouchDelegationDelayTimer implements Runnable {
        private boolean disable = false;
        private boolean triggered = false;
        private MotionEvent event;

        public void disable() {
            disable = true;
        }

        public boolean isDisable() {
            return disable;
        }

        public boolean isTriggered () {
            return triggered;
        }

        public TouchDelegationDelayTimer(MotionEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            if (!disable && touchDelegation != null) {
                event.setAction(MotionEvent.ACTION_DOWN);
                touchDelegation.onTouch(null, event);
                triggered = true;
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            postInvalidate();
        } else {
            if (scroller.isFinished() && scrolling) {
                scrolling = false;
                onScrollFinish();
            }
        }
    }

    public void setTouchDelegation(OnTouchListener touchDelegation) {
        this.touchDelegation = touchDelegation;
    }

    public void onScrollFinish() {
        if (currIndex == -1) {
            View item = pages.get(2);

            pages.set(2, pages.get(1));
            pages.set(1, pages.get(0));
            pages.set(0, item);

            if (onPrevCb != null)
                onPrevCb.call(item);
        } else if (currIndex == 1) {
            View item = pages.get(0);

            pages.set(0, pages.get(1));
            pages.set(1, pages.get(2));
            pages.set(2, item);

            if (onNextCb != null)
                onNextCb.call(item);
        }

        if (onScrollEnd != null)
            onScrollEnd.call();

        if (currIndex != 0) {
            currIndex = 0;
            scrollX = 0;
            requestLayout();
        }

        Log.i("debug", "scroll done");
    }

    public View getNextPage() {
        if (2 < pages.size())
            return pages.get(2);
        return null;
    }

    public View getPrevPage() {
        if (0 < pages.size())
            return pages.get(0);
        return null;
    }

    public View getCurrPage() {
        if (1 < pages.size())
            return pages.get(1);
        return null;
    }

    public void setOnNextCb(onNext onNextCb) {
        this.onNextCb = onNextCb;
    }

    public void setOnPrevCb(onPrev onPrevCb) {
        this.onPrevCb = onPrevCb;
    }

    public void setOnScrollStart(onScrollStart listener) {
        this.onScrollStart = listener;
    }

    public void setOnScrollEnd(onScrollEnd listener) {
        this.onScrollEnd = listener;
    }

    public interface onPrev {
        void call(View view);
    }

    public interface onNext {
        void call(View view);
    }

    public interface onScrollStart {
        void call();
    }

    public interface onScrollEnd {
        void call();
    }
}
