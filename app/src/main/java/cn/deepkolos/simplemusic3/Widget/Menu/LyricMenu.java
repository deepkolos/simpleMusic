package cn.deepkolos.simplemusic3.Widget.Menu;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;

public class LyricMenu extends PopupWindow {
    private int enterState = 0;
    private int leaveState = 0;
//    0 -> init
//    1 -> started
//    2 -> ended
    private ValueAnimator animator = new ValueAnimator();
    private Animator.AnimatorListener enterAnimListener;
    private Animator.AnimatorListener leaveAnimListener;
    private float currScale;

    public LyricMenu(Context context, int menuResId) {
        super(null, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        final View rootView = LayoutInflater.from(context).inflate(menuResId, null, false);
        setContentView(rootView);

        animator.setFloatValues(0f, 1f);
        animator.setRepeatCount(0);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currScale = (float) animation.getAnimatedValue();
                rootView.setScaleX(currScale);
                rootView.setScaleY(currScale);
            }
        });

        enterAnimListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        };

        leaveAnimListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                leaveState = 1;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                LyricMenu.super.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        };

        rootView.setScaleX(0f);
        rootView.setScaleY(0f);
        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (enterState == 0) {
                    enterState = 1;
                    currScale = 0f;
                    rootView.setPivotX(rootView.getWidth());
                    rootView.setPivotY(rootView.getHeight());
                    enterAnim();
                }
            }
        });

        ColorDrawable bgColor = new ColorDrawable();
        bgColor.setColor(Color.rgb(45, 45, 45));
        bgColor.setAlpha(0);
        setBackgroundDrawable(bgColor);
    }

    private void enterAnim () {
        animator.setFloatValues(currScale, 1f);
        animator.removeAllListeners();
        animator.addListener(enterAnimListener);
        animator.start();
    }

    private void leaveAnim () {
        animator.setFloatValues(currScale, 0f);
        animator.removeAllListeners();
        animator.addListener(leaveAnimListener);
        animator.start();
    }

    @Override
    public void dismiss() {
        if (leaveState == 0)
            leaveAnim();
    }
}
