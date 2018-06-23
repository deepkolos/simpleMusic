package cn.deepkolos.simplemusic3.Widget.Button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static cn.deepkolos.simplemusic3.Utils.UnitHelper.dpToPx;

public class PlayPauseButton extends View {
    boolean isPlay = true;
    int progress = 0;
    Paint circlePaint = new Paint();
    Paint progressPaint = new Paint();
    Path circlePath = new Path();
    Path dstPath = new Path();
    PathMeasure measure = new PathMeasure();

    static int colorLightGray = Color.rgb(196, 196, 196);
    static int colorLightBlack = Color.rgb(67, 67, 67);
    static int colorDeepRed = Color.rgb(204, 65, 42);

    public PlayPauseButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(dpToPx(1.5f));
        circlePaint.setColor(colorLightBlack);
        circlePaint.setAntiAlias(true);

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(dpToPx(1.5f));
        progressPaint.setColor(colorDeepRed);
        circlePaint.setAntiAlias(true);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }

    public void toggle() {
        isPlay = !isPlay;

        if (isPlay) {
            circlePaint.setColor(colorLightBlack);
        } else {
            circlePaint.setColor(colorLightGray);
        }

        postInvalidate();
    }

    public void play () {
        isPlay = true;
        circlePaint.setColor(colorLightGray);
        postInvalidate();
    }

    public void pause() {
        isPlay = false;
        circlePaint.setColor(colorLightBlack);
        postInvalidate();
    }

    int cWidth, cHeight;
    int minLen;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        cHeight = canvas.getHeight();
        cWidth = canvas.getWidth();
        minLen = dpToPx(26);

        // 转换为数学坐标系
        canvas.translate(cWidth / 2, cHeight/ 2);
        canvas.scale(1,-1);

        int radius = minLen / 2;

        // 绘制外部的圈
        circlePath.reset();
        RectF oval2 = new RectF(-radius, -radius, radius, radius);
        circlePath.addArc(oval2, 90, -359.9f);
        canvas.drawPath(circlePath, circlePaint);

        // 绘制进度条

        measure.setPath(circlePath, false);

        if (measure.getSegment(0, (progress/100f)*measure.getLength(), dstPath, true)) {
            canvas.drawPath(dstPath, progressPaint);
        }


        if (isPlay) {
            canvas.drawLine(-minLen/7, minLen/5, -minLen/7, -minLen/5, progressPaint);
            canvas.drawLine(minLen/7, minLen/5, minLen/7, -minLen/5, progressPaint);
        } else {
            canvas.drawLine(-minLen/20, minLen/5, -minLen/20, -minLen/5, circlePaint);
            canvas.drawLine(-minLen/20, minLen/5, minLen/6, 0, circlePaint);
            canvas.drawLine(minLen/6,0, -minLen/20, -minLen/5, circlePaint);
        }

        // 恢复原有坐标, 防止ripple位置跑偏
        canvas.scale(1,1);
        canvas.translate(-cWidth / 2, -cHeight/ 2);
    }

}
