package cn.deepkolos.simplemusic3.Widget.ListItem;

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

public class WithCounterView extends LinearLayout {
    ImageView iconIv;
    TextView titleTv;
    TextView countTv;

    public WithCounterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(HORIZONTAL);

        LayoutInflater.from(context).inflate(R.layout.view_list_of_songlist_item, this, true);
        iconIv = findViewById(R.id.songList_item_icon);
        titleTv = findViewById(R.id.songList_item_title);
        countTv = findViewById(R.id.songList_item_count);

        // 处理attrs
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconTitleAttrs);
        String title = ta.getString(R.styleable.IconTitleAttrs_title);
        int count = ta.getInt(R.styleable.IconTitleAttrs_count, 0);
        Drawable icon = ta.getDrawable(R.styleable.IconTitleAttrs_icon);
        ta.recycle();

        setCount(count);
        setIcon(icon);
        setTitle(title);
    }

    public void setCount (int val) {
        String str = "("+val+")";
        countTv.setText(str);
    }

    public void setTitle (String val) {
        titleTv.setText(val);
    }

    public void setIcon (Bitmap bitmap) {
        iconIv.setImageBitmap(bitmap);
    }

    public void setIcon (Drawable drawable) {
        iconIv.setImageDrawable(drawable);
    }
}
