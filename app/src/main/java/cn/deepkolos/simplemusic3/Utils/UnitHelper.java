package cn.deepkolos.simplemusic3.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class UnitHelper {
    static float density;
    static float scaledDensity;
    static int heightPixels;
    static int widthPixels;


    public static void init (Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        density = metrics.density;
        scaledDensity = metrics.scaledDensity;
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;
    }

    public static int dpToPx(float dp) {
        return Math.round(dp * density);
    }

    public static float spToPx(float sp) {
        return sp * scaledDensity;
    }

    public static int getScreenHeight () {
        return heightPixels;
    }

    public static int getScreenWidth () {
        return widthPixels;
    }

    public static float getDensity() {
        return density;
    }
}
