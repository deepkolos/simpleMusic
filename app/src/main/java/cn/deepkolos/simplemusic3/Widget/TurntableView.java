package cn.deepkolos.simplemusic3.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.deepkolos.simplemusic3.R;

public class TurntableView extends FrameLayout {
    ImageView $cover;

    public TurntableView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TurntableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageAttrs);
        Drawable src = ta.getDrawable(R.styleable.ImageAttrs_src);
        setSrc(src);
        ta.recycle();
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_turntable,this,true);
        $cover = findViewById(R.id.view_turntable_cover);
        $cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void setSrc (Drawable icon) {
        if (icon != null)
            $cover.setImageDrawable(icon);
    }

    public void setSrc (Bitmap icon) {
        if (icon != null)
            $cover.setImageBitmap(icon);
    }


}
