package cn.deepkolos.simplemusic3.Widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;
import cn.deepkolos.simplemusic3.Widget.Layout.ClipFrameLayout;

public class BottomPopup extends PopupWindow {
    private ClipFrameLayout $popup;
    private ScrollView $scrollView;
    private FrameLayout $content;
    private FrameLayout $topBar;
    protected ViewGroup $maxLayout;
    private View $bg;
    private float startY;
    private float currY = -1;
    private static int threshold = 7;
    private int enterState = 0;
    private int leaveState = 0;
//    0 -> init
//    1 -> started
//    2 -> ended
    private int startWithDirMatched = -1;
    private ValueAnimator animator = new ValueAnimator();
    private Animator.AnimatorListener enterAnimListener;
    private Animator.AnimatorListener leaveAnimListener;
    private static final int animationDuration = 320;
    private ColorDrawable bgColor;
    protected Context context;
    private Runnable todoCallback = null;

    public BottomPopup(Context context, View contentView) {
        super(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        View root = LayoutInflater.from(context).inflate(R.layout.view_bottom_popup, null, false);

        this.context = context;
        initContent(root);
        init(root);
        setContentView(root);
        $popup.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (enterState == 0) {
                    enterState = 1;
                    currY = $popup.getMeasuredHeight();
                    enterAnim();
                }
            }
        });
    }

    protected void initContent(View view) {
        $maxLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_bottom_popup_content, null, false);
        $scrollView = $maxLayout.findViewById(R.id.view_bottom_popup_scrollView);
        $scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if ($scrollView.getScrollY() < 0)
                    $scrollView.setScrollY(0);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(View view) {
        $bg = view.findViewById(R.id.view_bottom_popup_mask_bg);
//        $popup = view.findViewById(R.id.view_bottom_popup);
//        $content = view.findViewById(R.id.view_bottom_popup_content);
//        $scrollView = view.findViewById(R.id.view_bottom_popup_scrollView);
//        $topBar = view.findViewById(R.id.view_bottom_popup_topBar);

        $popup = new SwipeDownClipLayout(context);
        $popup.addView($maxLayout);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        $popup.setLayoutParams(params);
        int dp10 = UnitHelper.dpToPx(10);
        $popup.setRadius(dp10, dp10, 0,0);
        ((FrameLayout) view).addView($popup);

        $content = $maxLayout.findViewById(R.id.view_bottom_popup_content);
        $topBar = $maxLayout.findViewById(R.id.view_bottom_popup_topBar);

        setupAnim();

        bgColor = new ColorDrawable();
        bgColor.setColor(Color.rgb(0, 0, 0));
        bgColor.setAlpha(0);
        $bg.setBackground(bgColor);
        $bg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (leaveState == 0)
                    dismiss();
                return false;
            }
        });

//        View.OnTouchListener listener = new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // 如果已经触发了就不会再处理任何其他时间了
//                Log.i("debug", event.toString());
//                if (leaveState == 1) return true;
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        startY = event.getRawY();
//                        startWithDirMatched = -1;
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        if ($scrollView.getScrollViewScrollY() != 0) {
//                            startY = event.getRawY();
//                            startWithDirMatched = -1;
//                            break;
//                        }
//
//                        if (startWithDirMatched == -1 && event.getRawY() - startY > threshold)
//                            startWithDirMatched = event.getRawY() > startY ? 1 : 0;
//
//                        if (startWithDirMatched == 1) {
//
//                            currY = event.getRawY() - startY;
//                            currY = currY >= 0.0f ? currY : 0;
//                            $popup.setTranslationY(currY);
//
//                            updateMaskBg();
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        // startWithDirMatched
//                        if (startWithDirMatched == 1) {
//                            int popupHeight = $popup.getHeight();
//
//                            if (currY > popupHeight * 0.15f) {
//                                leaveAnim();
//                            } else {
//                                if (currY != 0)
//                                    enterAnim();
//                            }
//                        }
//                        break;
//                }
//
//                return true;
//            }
//        };
//
//        $popup.setOnTouchListener(listener);
//        $scrollView.setOnTouchListener(listener);
    }

    private void setupAnim () {
        animator.setFloatValues($popup.getHeight(), 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setRepeatCount(0);
        animator.setDuration(animationDuration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean skip = true;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currY = (float)animation.getAnimatedValue();
                $popup.setTranslationY(currY);

                skip = !skip;
                if (!skip)
                    updateMaskBg();
            }
        });

        enterAnimListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currY = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

        leaveAnimListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                leaveState = 1;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (todoCallback != null)
                    todoCallback.run();

                BottomPopup.super.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    private void updateMaskBg() {
        float alpha = currY / (float) $popup.getHeight();
        alpha = alpha < 0 ? 0 : alpha;
        alpha = alpha > 1 ? 1 : alpha;
        alpha = alpha * 0.6f;
        alpha = 0.6f - alpha;

        bgColor.setAlpha(Math.round(alpha*(float) 255));
    }

    public void setContent(View view) {
        $content.removeAllViews();
        $content.addView(view);
    }

    public void setContent (int layoutId) {
        View view = LayoutInflater.from(context).inflate(layoutId, null, false);
        setContent(view);
    }

    public void setTopBar (View view) {
        $topBar.removeAllViews();
        $topBar.addView(view);
    }

    public void setTopBar (int layoutId) {
        View view = LayoutInflater.from(context).inflate(layoutId, null, false);
        setTopBar(view);
    }

    public View getPopup () {
        return $popup;
    }

    public void setBackground(Drawable drawable) {
        $maxLayout.setBackground(drawable);
    }

    public void setItemClick (int resId, final View.OnClickListener listener) {
        View view = $popup.findViewById(resId);
        if (view != null)
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    dismiss(new Runnable() {
                        @Override
                        public void run() {
                            listener.onClick(v);
                        }
                    });
                }
            });
    }

    private void enterAnim () {
        if (currY != 0) {
            animator.setFloatValues(currY, 0);
            animator.removeAllListeners();
            animator.addListener(enterAnimListener);
            animator.start();
        }
    }

    private void leaveAnim () {
        if (currY != $popup.getHeight()) {
            animator.setFloatValues(currY, $popup.getHeight());
            animator.removeAllListeners();
            animator.addListener(leaveAnimListener);
            animator.start();
        }
    }

    @Override
    public void dismiss() {
        leaveAnim();
    }

    public void dismiss(Runnable callback) {
        todoCallback = callback;
        leaveAnim();
    }

    public int getScrollViewScrollY() {
        return $scrollView.getScrollY();
    }

    class SwipeDownClipLayout extends ClipFrameLayout {
        public SwipeDownClipLayout(Context context) {
            super(context);
        }

        public SwipeDownClipLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY = event.getRawY();
                    startWithDirMatched = -1;
                    Log.i("debug", "onInterceptTouchEvent touch start");
                    break;
                case MotionEvent.ACTION_MOVE:

                    Log.i("debug", "swipedownclip onInterceptTouchEvent");
                    if (getScrollViewScrollY() != 0) {
                        startY = event.getRawY();
                        startWithDirMatched = -1;
                        break;
                    }

                    if (startWithDirMatched == -1 && event.getRawY() - startY > threshold) {
                        startWithDirMatched = event.getRawY() > startY ? 1 : 0;
                        if (startWithDirMatched == 1) {
                            Log.i("debug", "onInterceptTouchEvent matched");
                        }
                    }

                    if (startWithDirMatched == 1) {
                        Log.i("debug", "onInterceptTouchEvent update");
                        currY = event.getRawY() - startY;
                        currY = currY >= 0.0f ? currY : 0;
                        $popup.setTranslationY(currY);

                        updateMaskBg();
                    }
                    break;
            }

            return startWithDirMatched == 1;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (leaveState == 1) return true;

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    Log.i("debug", "swipedownclip touchevent");
                    if (startWithDirMatched == 1) {
                        currY = event.getRawY() - startY;
                        currY = currY >= 0.0f ? currY : 0;
                        $popup.setTranslationY(currY);

//                        updateMaskBg();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (startWithDirMatched == 1) {
                        int popupHeight = $popup.getHeight();

                        if (currY > popupHeight * 0.15f) {
                            leaveAnim();
                        } else {
                            if (currY != 0)
                                enterAnim();
                        }
                        Log.i("debug", "touch end");
                    }
                    break;
            }
            return true;
        }
    }

    public void setItemText (int resId, String text) {
        TextView view;
        try {
            view = $popup.findViewById(resId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (view != null)
            view.setText(text);
    }

    public void setItemVisibility (int resId, int visibility) {
        View view;
        try {
            view = $popup.findViewById(resId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (view != null)
            view.setVisibility(visibility);
    }
}
