package cn.deepkolos.simplemusic3.Widget.Button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.deepkolos.simplemusic3.R;

public class IconTitleButton extends LinearLayout {
    ImageView imageView;
    TextView textView;
    int layoutId;

    public IconTitleButton(Context context, int layoutId) {
        super(context);

        this.layoutId = layoutId;
        init(context);
    }

    public IconTitleButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconTitleAttrs);
        String title = ta.getString(R.styleable.IconTitleAttrs_title);
        Drawable icon = ta.getDrawable(R.styleable.IconTitleAttrs_icon);
        layoutId = ta.getResourceId(R.styleable.IconTitleAttrs_layout, -1);

        init(context);
        setIcon(icon);
        setTitle(title);
        ta.recycle();
    }

    private void init (Context context) {
        LayoutInflater.from(context).inflate(layoutId, this, true);
        imageView = findViewById(R.id.view_icon_btn_icon);
        textView = findViewById(R.id.view_icon_btn_text);
    }

    public void setIcon (Drawable icon) {
        if (icon != null)
            imageView.setImageDrawable(icon);
    }

    public void setIcon (Bitmap icon) {
        if (icon != null)
            imageView.setImageBitmap(icon);
    }

    public void setTitle (String title) {
        textView.setText(title);
    }
}
