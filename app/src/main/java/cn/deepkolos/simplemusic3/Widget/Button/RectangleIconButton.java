package cn.deepkolos.simplemusic3.Widget.Button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.deepkolos.simplemusic3.R;

public class RectangleIconButton extends LinearLayout {
    ImageView $icon;
    TextView $title;
    FrameLayout $topContainer;
    FrameLayout $bottomContainer;
    TextView $badge;
    TextView $topRightText;
    TextView $bottomLeftText;
    FrameLayout $iconContainer;
    boolean initialed = false;

    public RectangleIconButton(Context context) {
        super(context);
        init(context);
    }

    public RectangleIconButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconTitleAttrs);
        Drawable icon = ta.getDrawable(R.styleable.IconTitleAttrs_icon);
        String title = ta.getString(R.styleable.IconTitleAttrs_title);
        String topRightText = ta.getString(R.styleable.IconTitleAttrs_topRightText);
        String bottomLeftText = ta.getString(R.styleable.IconTitleAttrs_bottomLeftText);
        String badge = ta.getString(R.styleable.IconTitleAttrs_badge);

        setIcon(icon);
        setTitle(title);
        setBadgeText(badge);
        setBottomLeftText(bottomLeftText);
        setTopRightText(topRightText);

        ta.recycle();
    }

    private void init (Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_rect_icon_btn,this,true);

        $icon = findViewById(R.id.view_rect_icon_btn_icon);
        $title = findViewById(R.id.view_rect_icon_btn_title);
        $topContainer = findViewById(R.id.view_rect_icon_btn_icon_top_container);
        $bottomContainer = findViewById(R.id.view_rect_icon_btn_icon_bottom_container);
        $bottomLeftText = findViewById(R.id.view_rect_icon_btn_icon_bottom_left_text);
        $topRightText = findViewById(R.id.view_rect_icon_btn_icon_top_right_text);
        $badge = findViewById(R.id.view_rect_icon_btn_icon_badge);
        $iconContainer = findViewById(R.id.view_rect_icon_btn_icon_container);

        $icon.setBackgroundColor(Color.BLACK);
    }

    public void setBadgeText (String text) {
        if (text != null) {
            $badge.setVisibility(VISIBLE);
            $badge.setText(text);
        } else {
            $badge.setVisibility(GONE);
        }
    }

    public void setTopRightText (String text) {
        if (text != null) {
            $topContainer.setVisibility(VISIBLE);
            $topRightText.setText(text);
        } else {
            $topContainer.setVisibility(GONE);
        }
    }

    public void setBottomLeftText (String text) {
        if (text != null) {
            $bottomContainer.setVisibility(VISIBLE);
            $bottomLeftText.setText(text);
        } else {
            $bottomContainer.setVisibility(GONE);
        }
    }

    public void setTitle (String text) {
        $title.setText(text);
    }

    public void setIcon (Drawable drawable) {
        $icon.setImageDrawable(drawable);
    }

    public void setIcon (Bitmap bitmap) {
        $icon.setImageBitmap(bitmap);
    }
}
