package cn.deepkolos.simplemusic3.Http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class Download {
    public static Bitmap image (String url) {
        try {
            URL curl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) curl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            return BitmapFactory.decodeStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String lyric (int songId) {
        return MusicApi.lyric(songId);
    }

    public static String mp3 (int songId) {
        StringBuffer result = new StringBuffer();
        BufferedReader reader = null;
        try {
            URL curl = new URL(MusicApi.getSongSrc(songId));
            HttpURLConnection conn = (HttpURLConnection) curl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);

            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));

            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                result.append(lines);
            }

            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
