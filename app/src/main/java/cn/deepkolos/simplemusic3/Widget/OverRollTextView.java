package cn.deepkolos.simplemusic3.Widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import cn.deepkolos.simplemusic3.R;

import static cn.deepkolos.simplemusic3.Utils.UnitHelper.dpToPx;
import static cn.deepkolos.simplemusic3.Utils.UnitHelper.spToPx;

public class OverRollTextView extends View {
    String text;
    float textSize = spToPx(15);
    int textColor = Color.GRAY;
    int speed = dpToPx(15); // px/s
    int startDelay = 1500;
    boolean mIsVisible = true;

    Paint paint = new Paint();
    ValueAnimator animator;
    int scrollWidth = 0;
    int gapWidth = dpToPx(100);
    int mAscent;

    public OverRollTextView(Context context) {
        super(context);
        init();
    }

    public OverRollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextViewAttrs);
        int textSize = ta.getDimensionPixelOffset(R.styleable.TextViewAttrs_textSize, (int)this.textSize);
        String text = ta.getString(R.styleable.TextViewAttrs_text);
        int textColor = ta.getColor(R.styleable.TextViewAttrs_textColor, Color.WHITE);
        int textRollSpeed = ta.getDimensionPixelOffset(R.styleable.TextViewAttrs_textRollSpeed, speed);
        int textRollStartDelay = ta.getInt(R.styleable.TextViewAttrs_textRollStartDelay, startDelay);

        setText(text);
        setTextColor(textColor);
        setTextSizePx(textSize);
        setRollSpeed(textRollSpeed);
        setRollStartDelay(textRollStartDelay);

        ta.recycle();
    }

    private void init() {
        animator = ValueAnimator.ofFloat(0f, 1.0f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setStartDelay(startDelay);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();

                scrollTo(Math.round(progress * scrollWidth), 0);
            }
        });

        animator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                scrollTo(0, 0);
                postInvalidate();

                if (mIsVisible)
                    animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        updatePaint();
        setMeasuredDimension(widthMeasureSpec, measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) paint.measureText(text) + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST)
                result = Math.min(result, specSize);
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        mAscent = (int) paint.ascent();
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) (-mAscent + paint.descent()) + getPaddingTop()
                    + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST)
                result = Math.min(result, specSize);
        }

        return result;
    }

    public void setText(String str) {
        text = str;
        restartRoll();
    }

    public void setTextSize(int sp) {
        this.textSize = spToPx(sp);
        restartRoll();
    }

    public void setTextSizePx(int px) {
        this.textSize = px;
        restartRoll();
    }

    public void setTextColor(int color) {
        this.textColor = color;
        restartRoll();
    }

    public void setVisible(boolean mIsVisible) {
        this.mIsVisible = mIsVisible;
        restartRoll();
    }

    private void restartRoll () {
        stopRoll();
        if (mIsVisible)
            postInvalidate();
    }

    private void stopRoll () {
        animator.cancel();
        scrollTo(0, 0);
    }

    public void setRollSpeed(int speed) {
        this.speed = speed;
    }

    public void setRollStartDelay(int startDelay) {
        this.startDelay = startDelay;
        animator.setStartDelay(startDelay);
    }

    private void updatePaint () {
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        updatePaint();

        canvas.drawText(text, getPaddingLeft(), getPaddingTop() - mAscent, paint);

        if (!mIsVisible) {
            animator.cancel();
            scrollTo(0, 0);
            return;
        };

        int w = (int) paint.measureText(text);
        int canvasW = canvas.getWidth();

        if (w > canvasW) {
            canvas.drawText(text, getPaddingLeft() + w + gapWidth, getPaddingTop() - mAscent, paint);

            if (!animator.isStarted()) {
                scrollWidth = w + gapWidth;
                animator.setDuration((scrollWidth/speed)  * 1000);
                animator.start();
            }
        } else {
            animator.cancel();
            scrollTo(0, 0);
        }
    }
}
