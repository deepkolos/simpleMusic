package cn.deepkolos.simplemusic3.Widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import cn.deepkolos.simplemusic3.R;

import static cn.deepkolos.simplemusic3.Utils.UnitHelper.dpToPx;

public class TurnTableHandView extends AppCompatImageView {
    ValueAnimator animator;
    boolean isOnTrack;
    float currRotate;

    public TurnTableHandView(Context context) {
        super(context);
        init();
    }

    public TurnTableHandView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(dpToPx(98), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(dpToPx(135), MeasureSpec.EXACTLY));
    }

    private void init() {
        setImageResource(R.drawable.play_page_needle);
        setPivotX(dpToPx(20));
        setPivotY(dpToPx(16));
        //setRotation(-25);
        isOnTrack = true;
        currRotate = 0f;

        animator = new ValueAnimator();
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(0);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currRotate = (float)animation.getAnimatedValue();
                setRotation(currRotate);
            }
        });


        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isOnTrack = !isOnTrack;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void putOnTrack () {
        if (!isOnTrack) {
            if (animator.isRunning())
                animator.cancel();
            animator.setFloatValues(currRotate, 0f);
            animator.start();
        }

    }

    public void putOffTrack () {
        if (isOnTrack) {
            if (animator.isRunning())
                animator.cancel();
            animator.setFloatValues(currRotate, -25f);
            animator.start();
        }

    }
}
