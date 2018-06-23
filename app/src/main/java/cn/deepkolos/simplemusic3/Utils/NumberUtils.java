package cn.deepkolos.simplemusic3.Utils;

public class NumberUtils {
    public static String shorten (int num) {
        if (num < 1000)
            return num+"";

        if (num < 10000)
            return (num/1000) + "K";

        return (num/10000)+"W";
    }
}
