package cn.deepkolos.simplemusic3.Utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.Dictionary;
import java.util.Hashtable;


public class UiUtils {

    public static void updateExpandViewHeight(ExpandableListView expandableListView) {
        BaseExpandableListAdapter expandableListAdapter = (BaseExpandableListAdapter) expandableListView.getExpandableListAdapter();
        if (expandableListAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int groupCount = expandableListAdapter.getGroupCount(), count;
        int num = groupCount;
        for (int i = 0; i < groupCount; i++) {
            count = expandableListAdapter.getChildrenCount(i);
            View groupListItem = expandableListAdapter.getGroupView(i, false, null, expandableListView);
            groupListItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += groupListItem.getMeasuredHeight(); // 统计所有子项的总高度
            num = num + count;

            for (int j = 0; j < count; j++) {
                if (expandableListView.isGroupExpanded(i)) {
                    View listItem = expandableListAdapter.getChildView(i, j, false, null, expandableListView);

                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }
        ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
        params.height = totalHeight
                + (expandableListView.getDividerHeight() * (num - 1));
        expandableListView.setLayoutParams(params);
    }

    public static void updateListViewHeight(ListView listView) {
        BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
        if (adapter == null) return;

        float totalHeight = 0;
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View view = adapter.getView(i, null, null);
            view.measure(0, 0);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = Math.round(totalHeight);
        listView.setLayoutParams(params);
    }

    public static class DisplaySwitcher {
        View view;
        ValueAnimator animator;
        float currAlpha;
        boolean isShowing;

        public DisplaySwitcher(final View view, boolean display) {
            this.view = view;

            if (display) {
                show(false);
            } else {
                hidden(false);
            }

            animator = new ValueAnimator();
            animator.setDuration(300);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatCount(0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currAlpha = (float) animation.getAnimatedValue();
                    view.setAlpha(currAlpha);
                }
            });
        }

        public void setDuration(int duration) {
            animator.setDuration(duration);
        }

        public void show (boolean withAnimation) {
            if (withAnimation) {
                if (animator.isRunning())
                    animator.cancel();

                animator.setFloatValues(currAlpha, 1f);
                animator.removeAllListeners();
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isShowing = true;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                animator.start();
            } else {
                currAlpha = 1f;
                view.setVisibility(View.VISIBLE);
                view.setAlpha(currAlpha);
                isShowing = true;
            }
        }

        public void hidden (boolean withAnimation) {
            if (withAnimation) {
                if (animator.isRunning())
                    animator.cancel();

                animator.setFloatValues(currAlpha, 0f);
                animator.removeAllListeners();
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                        isShowing = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                animator.start();
            } else {
                currAlpha = 0f;
                view.setVisibility(View.GONE);
                view.setAlpha(currAlpha);
                isShowing = false;
            }
        }

        public void toggle (boolean withAnimation) {
            if (isShowing)
                hidden(withAnimation);
            else
                show(withAnimation);
        }
    }

    public static int getListViewScrollY (ListView $listView) {
        View c = $listView.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = $listView.getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();
    }

    public static abstract class ListViewOnScrollY implements AbsListView.OnScrollListener {
        Dictionary<Integer, Integer> listViewItemHeights = new Hashtable<>();

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            View c = view.getChildAt(0); //this is the first visible row
            if (c != null) {
                int scrollY = -c.getTop();
                listViewItemHeights.put(view.getFirstVisiblePosition(), c.getHeight());
                for (int i = 0; i < view.getFirstVisiblePosition(); ++i) {
                    if (listViewItemHeights.get(i) != null) // (this is a sanity check)
                        scrollY += listViewItemHeights.get(i); //add all heights of the views that are gone

                }

                onScroll(scrollY);
            }
        }

        public abstract void onScroll (int scrollY);
    }
}
