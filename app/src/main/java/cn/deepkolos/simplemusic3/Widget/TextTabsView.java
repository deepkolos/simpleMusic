package cn.deepkolos.simplemusic3.Widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.Utils.UnitHelper;

public class TextTabsView extends HorizontalScrollView {
    ViewPager viewPager;
    IndicatorLayout container;
    List<String> tabTexts;
    List<TextView> tabTextViews = new ArrayList<>();
    Context context;
    int tabTextSize = 13;
    int tabTextColor = Color.WHITE;
    int currentTab;
    int tmpTab;
    int tabCount;
    int wrapPaddingHorizontal = UnitHelper.dpToPx(22);
    int indicatorType = 0;
    int indicatorColor = Color.WHITE;
    int currPageScrollState = 0;
    boolean useDivide; // 这里会有拓展的问题, 当增加显示模式的时候会需要改很多东西
    int dragPosition = 0;
    float dragOffset = 0;
    int fixedLineWidth = UnitHelper.dpToPx(40);
    int fixedLineMaxWidth = UnitHelper.dpToPx(65);
    Paint textPaint = new Paint();
    Paint linePaint = new Paint();
    int lineOffsetY = 0;
    ValueAnimator groundAnimation;
    int grandAnimState = -1;// -1 init, 0 start, 1 animEnd, 2 actual end then go back to -1

    public final static int INDICATOR_TYPE_FIXED_WIDTH = 0;
    public final static int INDICATOR_TYPE_MATCH_TEXT_WIDTH = 1;

    public TextTabsView(Context context) {
        super(context);
        init(context);
    }

    public TextTabsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    void init (Context context) {
        this.context = context;
        container = new IndicatorLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setWillNotDraw(false);
        addView(container);

        linePaint.setStrokeWidth(UnitHelper.dpToPx(2));
        linePaint.setColor(Color.WHITE);
        setHorizontalScrollBarEnabled(false);

        groundAnimation = ValueAnimator.ofFloat(0f, 1.0f);
        groundAnimation.setDuration(250);
        groundAnimation.setRepeatCount(0);
        groundAnimation.setInterpolator(new LinearInterpolator());
        groundAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dragOffset = (float) animation.getAnimatedValue();
                container.postInvalidate();
            }
        });

        groundAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                grandAnimState = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                grandAnimState = 1;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                grandAnimState = -1;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    public void setTabTexts(List<String> tabTexts) {
        this.tabTexts = tabTexts;
    }

    public void setTabTextSize(int tabTextSize) {
        this.tabTextSize = tabTextSize;
        textPaint.setTextSize(tabTextSize);
    }

    public void setTabTextColor(int tabTextColor) {
        this.tabTextColor = tabTextColor;
    }

    public void setWrapPaddingHorizontal(int wrapPaddingHorizontal) {
        this.wrapPaddingHorizontal = wrapPaddingHorizontal;
    }

    public void setIndicatorType(int indicatorType) {
        this.indicatorType = indicatorType;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public void setFixedLineWidth(int fixedLineWidth) {
        this.fixedLineWidth = fixedLineWidth;
    }

    public void setLineColor (int color) {
        linePaint.setColor(color);
    }

    public void setLineOffsetY(int lineOffsetY) {
        this.lineOffsetY = lineOffsetY;
    }

    public void setFixedLineMaxWidth(int fixedLineMaxWidth) {
        this.fixedLineMaxWidth = fixedLineMaxWidth;
    }

    public void applyChange () {
        if (viewPager == null || viewPager.getAdapter() == null) return;

        PagerAdapter adapter = viewPager.getAdapter();
        container.removeAllViews();

        tabCount = adapter.getCount();
        tmpTab = currentTab = viewPager.getCurrentItem();
        int tabTextsCount = 0;

        if (tabCount > 0) {
            // 初始化dom结构
            useDivide = canUseDivideStyle();
            if (tabTexts != null)
                tabTextsCount = tabTexts.size();

            for (int i = 0; i < tabCount; i++) {
                TextView textView = new TextView(context);

                if (useDivide) {
                    styleTextViewDivide(textView, i);
                } else {
                    styleTextViewWrap(textView, i);
                }

                if (i < tabTextsCount)
                    textView.setText(tabTexts.get(i));

                container.addView(textView);
                tabTextViews.add(textView);
            }

            if (useDivide) {
                setFillViewport(true);
                container.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                setFillViewport(false);
                container.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        }
    }

    private boolean canUseDivideStyle () {
        // padding vertical 9dp
        // padding horizontal 11dp

        // 获取每一个text本身需要占据空间的大小
        int containerWidth = getMeasuredWidth();
        // getMaxTabWidth
        float maxF = 0;
        float currF;
        for (String str : tabTexts) {
            currF = textPaint.measureText(str);
            if (currF > maxF)
                maxF = currF;
        }

        // 对比(max+paddingHorizontal*2)*tabCount > containerWidth
        float tmp = (maxF + wrapPaddingHorizontal * 2f) * tabCount;

        if ((maxF + wrapPaddingHorizontal * 2f) * tabCount > containerWidth)
            return false; // use wrap
        else
            return true;  // use divide

    }

    private void styleTextViewDivide (TextView textView, int i) {
        textView.setTextSize(tabTextSize);
        textView.setTextColor(tabTextColor);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);

        bindTextViewEvent(textView, i);
    }

    private void styleTextViewWrap (TextView textView, int i) {
        textView.setTextSize(tabTextSize);
        textView.setTextColor(tabTextColor);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(wrapPaddingHorizontal, 0, wrapPaddingHorizontal, 0);

        bindTextViewEvent(textView, i);
    }

    private void bindTextViewEvent (TextView textView, final int i) {
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(i,true);
                if (i == currentTab) {
                    // 原地动画
                    groundAnimation.start();
                }
            }
        });
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (groundAnimation.isRunning())
                groundAnimation.cancel();

            grandAnimState = -1;
            dragPosition = position;
            dragOffset = positionOffset;
            container.postInvalidate();
        }

        @Override
        public void onPageSelected(int position) {
            tmpTab = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            currPageScrollState = state;
            if (state == 0)
                currentTab = tmpTab;
        }
    }

    private float getTabCenterX (int position) {
        float centerX = 0;

        for (int i = 0; i < position; i++)
            centerX += tabTextViews.get(i).getMeasuredWidth();

        centerX += tabTextViews.get(position).getMeasuredWidth()/2f;

        return centerX;
    }

    class IndicatorLayout extends LinearLayout {
        public IndicatorLayout(Context context) {
            super(context);
        }

        public IndicatorLayout(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (viewPager == null || viewPager.getAdapter() == null || tabTextViews.size() == 0) return;

            // 初始化line
            int lineWidthStart = 0;
            int lineWidthEnd = 0;
            int lineMaxWidth = fixedLineMaxWidth;
            float lineStartY = lineOffsetY;
            float lineStartX;
            float lineCenterX;
            int lineWidth;
            int containerWidth = TextTabsView.this.getMeasuredWidth();

            if (indicatorType == INDICATOR_TYPE_FIXED_WIDTH)
                lineWidthStart = lineWidthEnd = fixedLineWidth;

            if (indicatorType == INDICATOR_TYPE_MATCH_TEXT_WIDTH) {
                lineWidthStart = Math.round(textPaint.measureText(tabTexts.get(currentTab)));
                lineWidthEnd = Math.round(textPaint.measureText(tabTexts.get(viewPager.getCurrentItem())));
            }

            // 绘制line

            // 根据drag状态做修正
            lineStartX = getTabCenterX(dragPosition);

            // 修正lineStartX
            float tabCenterXDiff = 0;

            if (grandAnimState == -1 && dragPosition != tabCount-1)
                tabCenterXDiff = getTabCenterX(dragPosition+1) - getTabCenterX(dragPosition);

            // 设置lineWidth
            if (dragOffset < 0.5) {
                lineWidth = Math.round(lineWidthStart + (lineMaxWidth - lineWidthStart) * dragOffset * 2);
            } else {
                lineWidth = Math.round(lineWidthEnd + (lineMaxWidth - lineWidthEnd) * (1f - dragOffset) * 2);
            }

            lineStartX += (tabCenterXDiff * dragOffset);
            lineCenterX = lineStartX;
            lineStartX -= lineWidth / 2f;

            canvas.drawLine(lineStartX, lineStartY, (lineStartX + lineWidth), lineStartY, linePaint);

            if (grandAnimState == 2) grandAnimState = -1;

            if (grandAnimState == 1 && dragOffset == 1.0f)
                grandAnimState = 2;

            // 设置scroll尽量居中
            int needScroll = Math.round(lineCenterX - containerWidth / 2f);
            if (needScroll > 0) {
                TextTabsView.this.scrollTo(needScroll,0);
                TextTabsView.this.postInvalidate();
            }
        }
    }
}
