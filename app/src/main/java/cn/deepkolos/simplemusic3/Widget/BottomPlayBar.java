package cn.deepkolos.simplemusic3.Widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;
import cn.deepkolos.simplemusic3.Widget.Layout.ClipFrameLayout;

import static cn.deepkolos.simplemusic3.Utils.UnitHelper.dpToPx;

public class BottomPlayBar extends ViewGroup {
    ImageView ivLeft;
    ImageView ivRight;
    Context context;
    SimpleViewPager songItemContainer;
    int ivLeftW;
    int ivLeftH;
    int songItemW;
    int containerW;
    int containerH;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 对子view进行layout
        containerW = getMeasuredWidth();
        containerH = getMeasuredHeight();

        ivLeftW = ivLeft.getMeasuredWidth();
        ivLeftH = ivLeft.getMeasuredHeight();

        songItemW = containerW - ivLeftW * 2;

        // ivLeft和ivRight的背景
        int marginTop = (containerH - dpToPx(34)) / 2;
        ivLeft.layout(0, marginTop, ivLeftW, ivLeftH+marginTop);
        ivRight.layout(containerW - ivLeftW, marginTop, containerW, ivLeftH+marginTop);

        songItemContainer.setPaddingStartEnd(ivLeftW);
        songItemContainer.layout(0, 0, containerW, containerH);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    public BottomPlayBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        // 拼接view的结构
        ivLeft = new ImageView(context);
        ivRight = new ImageView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.width = dpToPx(8);
        params.height = dpToPx(34);
        ivLeft.setLayoutParams(params);
        ivRight.setLayoutParams(params);
        ivLeft.setVisibility(INVISIBLE);
        ivRight.setVisibility(INVISIBLE);

        Resources rs = getResources();
        ivRight.setImageDrawable(rs.getDrawable(R.drawable.player_bar_swipe_edge_right_bg, null));
        ivLeft.setImageDrawable(rs.getDrawable(R.drawable.player_bar_swipe_edge_left_bg, null));

        LayoutParams paramsSelf = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        paramsSelf.width = LayoutParams.MATCH_PARENT;
        paramsSelf.height = UnitHelper.dpToPx(46);
        setLayoutParams(paramsSelf);

        songItemContainer = new SimpleViewPager(context);
        songItemContainer.setOnScrollStart(new SimpleViewPager.onScrollStart() {
            @Override
            public void call() {
                ivLeft.setVisibility(VISIBLE);
                ivRight.setVisibility(VISIBLE);
            }
        });
        songItemContainer.setOnScrollEnd(new SimpleViewPager.onScrollEnd() {
            @Override
            public void call() {
                ((BottomPlayBarSongItem)songItemContainer.getPageAt(0)).setVisible(false);
                ((BottomPlayBarSongItem)songItemContainer.getPageAt(2)).setVisible(false);
                ((BottomPlayBarSongItem)songItemContainer.getPageAt(1)).setVisible(true);
                ivLeft.setVisibility(INVISIBLE);
                ivRight.setVisibility(INVISIBLE);
            }
        });

        songItemContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomPlayBar.this.performClick();
            }
        });

        for (int i = 0; i < 3; i++) {
            BottomPlayBarSongItem item = new BottomPlayBarSongItem(context);
            songItemContainer.addView(item);
            item.setCover(getResources().getDrawable(R.drawable.default_cover, null));
            if (i == 0) {
                item.setSongSinger("");
                item.setSongName("请选择音乐");
            }
            if (i == 1) {
                item.setSongSinger("");
                item.setSongName("请选择音乐");
            }
            if (i == 2) {
                item.setSongSinger("");
                item.setSongName("请选择音乐");
            }

            item.setVisible(i == 1);
        }

        addView(songItemContainer);
        addView(ivLeft);
        addView(ivRight);
    }

    public class BottomPlayBarSongItem extends LinearLayout {
        ClipFrameLayout coverRadius;
        ImageView ivCover;
        LinearLayout infoContainer;
        OverRollTextView songTitle;
        OverRollTextView songSinger;

        public BottomPlayBarSongItem(Context context) {
            super(context);

            ivCover = new ImageView(context);
            songTitle = new OverRollTextView(context);
            songSinger = new OverRollTextView(context);
            coverRadius = new ClipFrameLayout(context);

            // 设置cover
            coverRadius.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            coverRadius.setRadius(UnitHelper.dpToPx(3));
            ivCover.setLayoutParams(new ViewGroup.LayoutParams(dpToPx(34), dpToPx(34)));
            ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // 设置外框
            infoContainer = new LinearLayout(context);
            MarginLayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(34), 1.0f);
            params.leftMargin = dpToPx(7);
            infoContainer.setLayoutParams(params);
            infoContainer.setOrientation(VERTICAL);

            songTitle.setTextSize(14);
            songTitle.setTextColor(Color.BLACK);
            songTitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(18)));

            songSinger.setTextSize(12);
            songSinger.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(15)));

            setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);

            // 组装
            coverRadius.addView(ivCover);
            infoContainer.addView(songTitle);
            infoContainer.addView(songSinger);
            addView(coverRadius);
            addView(infoContainer);
        }

        public void setSongName(String title) {
            songTitle.setText(title);
        }

        public void setSongSinger (String singer) {
            songSinger.setText(singer);
        }

        public void setCover (Bitmap bitmap) {
            ivCover.setImageBitmap(bitmap);
        }

        public void setCover (Drawable drawable) {
            ivCover.setImageDrawable(drawable);
        }

        public void setVisible(boolean visible) {
            songTitle.setVisible(visible);
            songSinger.setVisible(visible);
        }
    }

    public void setTouchDelegation (OnTouchListener listener) {
        songItemContainer.setTouchDelegation(listener);
    }

    public View getPageAt(int index) {
        return songItemContainer.getPageAt(index);
    }
}
