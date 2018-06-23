package cn.deepkolos.simplemusic3.Widget.Button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import static cn.deepkolos.simplemusic3.Utils.UnitHelper.dpToPx;

public class PlayListButton extends View {
    int color;
    static Paint paint = new Paint();

    public PlayListButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        color = Color.rgb(76, 76, 76);
    }

    public void setColor(int color) {
        this.color = color;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 转换为数学坐标系
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.scale(1,-1);

        // 绘制小三角
        Path path = new Path();

        path.moveTo(dpToPx(-10), dpToPx(9f));
        path.rLineTo(0, dpToPx(-6));
        path.rLineTo(dpToPx(4), dpToPx(3));
        path.close();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setStrokeWidth(dpToPx(1));
        paint.setAntiAlias(true);

        canvas.drawPath(path, paint);

        // 绘制线
        paint.setStyle(Paint.Style.STROKE);
        Path line = new Path();
        line.moveTo(dpToPx(-2), dpToPx(6));
        line.rLineTo(dpToPx(12),0);

        line.moveTo(dpToPx(-10), dpToPx(-0.8f));
        line.rLineTo(dpToPx(20),0);

        line.moveTo(dpToPx(-10), dpToPx(-7));
        line.rLineTo(dpToPx(20),0);

        canvas.drawPath(line, paint);

        // 恢复原有坐标, 防止ripple位置跑偏
        canvas.scale(1,1);
        canvas.translate(-canvas.getWidth() / 2, -canvas.getHeight()/ 2);
    }
}
