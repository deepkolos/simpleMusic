package cn.deepkolos.simplemusic3.Utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;

import java.util.Arrays;

public class RippleDrawableUtils {

    /**
     * for general button with ripple above 5.0 and selector lower than.
     *
     * @param normalColorID id for normal color
     * @return backgroundDrawable
     */
    public static Drawable getButtonRippleBackground(
            int normalColorID) {

        float radius = 2;
        int normalColor = Color.TRANSPARENT;
        getRippleColor(normalColor);

        return getCompatRippleDrawable(normalColor, getRippleColor(normalColor), radius);
    }

    public static Drawable getCompatRippleDrawable(
            int normalColor, int pressedColor, float radius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(getPressedColorSelector(normalColor, pressedColor),
                    getShapeDrawable(normalColor, radius), getRippleMask(normalColor));
        } else {
            return getStateListDrawable(normalColor, pressedColor, radius);
        }
    }

    private static int getRippleColor(int normalColor) {
        int r = (int) (((normalColor >> 16) & 0xFF) * 0.8);
        int g = (int) (((normalColor >> 8) & 0xFF) * 0.8);
        int b = (int) (((normalColor) & 0xFF) * 0.8);
        return Color.rgb(r, g, b);
    }


    private static ColorStateList getPressedColorSelector(int normalColor, int pressedColor) {
        return new ColorStateList(
                new int[][]
                        {
                                new int[]{android.R.attr.state_pressed},
                                new int[]{android.R.attr.state_focused},
                                new int[]{android.R.attr.state_activated},
                                new int[]{}
                        },
                new int[]
                        {
                                pressedColor,
                                pressedColor,
                                pressedColor,
                                normalColor
                        }
        );
    }

    private static Drawable getRippleMask(int color) {
        float[] outerRadii = new float[8];
        // 3 is radius of final ripple,
        // instead of 3 you can give required final radius
        Arrays.fill(outerRadii, 3);

        RoundRectShape r = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(r);
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    private static StateListDrawable getStateListDrawable(
            int normalColor, int pressedColor, float radius) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                getShapeDrawable(pressedColor, radius));
        states.addState(new int[]{android.R.attr.state_focused},
                getShapeDrawable(pressedColor, radius));
        states.addState(new int[]{android.R.attr.state_activated},
                getShapeDrawable(pressedColor, radius));
        states.addState(new int[]{},
                getShapeDrawable(normalColor, radius));
        return states;
    }

    private static GradientDrawable getShapeDrawable(int color, float radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(radius);
        shape.setColor(color);
        return shape;
    }
}