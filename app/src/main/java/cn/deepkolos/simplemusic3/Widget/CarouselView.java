package cn.deepkolos.simplemusic3.Widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;

public class CarouselView extends RelativeLayout {
    ViewPager $viewPager;
    LinearLayout $dotContainer;
    List<View> dotList;
    int count = -1;
    OnGetView getViewCallback;
    onUpdateView updateViewCallback;
    boolean autoPlay = true;
    boolean blockAutoPlay = false;
    int autoPlayDelay = 5000;
    WeakReference<Context> contextRef;
    int currPosition;
    MyThread timer;

    public CarouselView(Context context) {
        super(context);
        init(context);
    }

    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init (Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_carousel, this, true);

        $viewPager = findViewById(R.id.view_carousel_viewPager);
        $dotContainer = findViewById(R.id.view_carousel_dot_container);
        contextRef = new WeakReference<>(context);
        dotList = new ArrayList<>();

        $viewPager.setAdapter(new CarouselPagerAdapter());
        $viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                dotList.get(currPosition % count).setEnabled(false);
                dotList.get(position % count).setEnabled(true);

                currPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING)
                    blockAutoPlay = true;

                if (state == ViewPager.SCROLL_STATE_IDLE)
                    blockAutoPlay = false;
            }
        });
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setAutoPlay(boolean val) {
        autoPlay = val;

        if (timer != null)
            timer.setRunning(false);

        if (autoPlay) {
            timer = new MyThread();
            timer.start();
        }
    }

    public void setAutoPlayDelay(int autoPlayDelay) {
        this.autoPlayDelay = autoPlayDelay;
    }

    public void setOnGetView (OnGetView callback) {
        getViewCallback = callback;
    }

    public void setOnUpdateView(onUpdateView updateViewCallback) {
        this.updateViewCallback = updateViewCallback;
    }

    public PagerAdapter getAdapter () {
        return $viewPager.getAdapter();
    }

    public void applyChange () {
        $dotContainer.removeAllViews();

        for (int i = 0 ; i < count; i++) {
            View $dot = new View(contextRef.get());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UnitHelper.dpToPx(7),UnitHelper.dpToPx(7));
            $dot.setBackgroundResource(R.drawable.dot_color);
            $dot.setEnabled(false);

            if (i != 0)
                params.leftMargin = UnitHelper.dpToPx(7);

            $dot.setLayoutParams(params);
            dotList.add(i, $dot);
            $dotContainer.addView($dot);
        }

        // 设置初始第一页
        currPosition = 1000000+count;
        $viewPager.setCurrentItem(currPosition, false);
    }

    public interface OnGetView {
        View getView (int position);
    }

    public interface onUpdateView {
        void updateView (View view, int position);
    }

    class CarouselPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int currPosition = position % count;

            View view = getViewCallback != null
                    ? getViewCallback.getView(currPosition)
                    : new View(contextRef.get());

            if (updateViewCallback != null)
                updateViewCallback.updateView(view, currPosition);

            container.addView(view);
            return view;
        }
    }

    class MyThread extends Thread {
        boolean running = true;

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            while (running) {

                try {
                    Thread.sleep(autoPlayDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (running && !blockAutoPlay)
                    CarouselView.this.post(new Runnable() {
                        @Override
                        public void run() {
                            if ($viewPager != null && getViewCallback != null)
                                $viewPager.setCurrentItem(currPosition+1, true);
                        }
                    });
            }
        }
    }
}
