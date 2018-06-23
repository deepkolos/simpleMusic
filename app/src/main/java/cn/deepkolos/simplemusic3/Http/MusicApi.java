package cn.deepkolos.simplemusic3.Http;


import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.deepkolos.simplemusic3.Model.Raw.BannerRaw;
import cn.deepkolos.simplemusic3.Model.Raw.HotMusicList;
import cn.deepkolos.simplemusic3.Model.Raw.LyricRaw;
import cn.deepkolos.simplemusic3.Model.Raw.PlaylistRaw;
import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.Model.Raw.SongRaw;
import cn.deepkolos.simplemusic3.Utils.Callback;

public class MusicApi {
    private static String MODULUS = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
    private static String NONCE = "0CoJUm6Qyw8W8jud";
    private static String PUBKEY = "010001";
    private static String VI = "0102030405060708";
    private static String USERAGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.157 Safari/537.36";
    private static String COOKIE = "os=pc; osver=Microsoft-Windows-10-Professional-build-10586-64bit; appver=2.0.3.131777; channel=netease; __remember_me=true";
    private static String REFERER = "http://music.163.com/";
    private static String secretKey = "TA3YiYCfY2dDJQgg";
    private static String encSecKey = "84ca47bca10bad09a6b04c5c927ef077d9b9f1e37098aa3eac6ea70eb59df0aa28b691b7e75e4f1f9831754919ea784c8f74fbfadf2898b0be17849fd656060162857830e241aba44991601f137624094c114ea8d17bce815b0cd4e5b8e2fbaba978c6d1d14dc3d1faf852bdd28818031ccdaaa13a6018e1024e2aae98844210";
    private static String API = "http://music.163.com/weapi/";
    private static String CSRF_TOKEN = "csrf_token";
    private static Gson gson = new Gson();
    private static Base64 base64 = new Base64();

    private static String connection(String url, Map<String, Object> data) {
        String response = "";
        OutputStreamWriter out = null;
        BufferedReader reader = null;
        StringBuffer parameterBuffer = new StringBuffer();

        URL curl = null;
        try {
            curl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) curl.openConnection();
            if (data != null && data.size() > 0) {
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Referer", REFERER);
                conn.setRequestProperty("Cookie", COOKIE);
                conn.setRequestProperty("User-Agent", USERAGENT);
                Iterator iterator = data.keySet().iterator();
                String key = null;
                String value = null;
                while (iterator.hasNext()) {
                    key = (String) iterator.next();
                    if (data.get(key) != null) {
                        value = (String) data.get(key);
                    } else {
                        value = "";
                    }
                    parameterBuffer.append(key).append("=")
                            .append(URLEncoder.encode(value, "utf-8"));
                    if (iterator.hasNext()) {
                        parameterBuffer.append("&");
                    }
                }

                try {
                    out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(parameterBuffer.toString());
                    out.flush();
                    reader = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    String lines;
                    while ((lines = reader.readLine()) != null) {
                        lines = new String(lines.getBytes(), "utf-8");
                        response += lines;
                    }
                } catch (Exception e) {

                } finally {
                    reader.close();
                    out.close();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private static Map<String, Object> prepare(Map<String, Object> raw) {
        Map<String, Object> data = new HashMap<>();
        data.put("params", encrypt(jsonEncode(raw), NONCE));
        data.put("params", encrypt((String) data.get("params"), secretKey));
        data.put("encSecKey", encSecKey);
        return data;
    }

    private static String jsonEncode(Object object) {
        return gson.toJson(object);
    }

    private static String encrypt(String content, String password) {
        try {
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
            IvParameterSpec iv = new IvParameterSpec(VI.getBytes());// 创建iv
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);// 初始化
            byte[] result = cipher.doFinal(byteContent);

            return new String (base64.encode(result), "UTF-8"); // 加密
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String lyric (int songId) {
        String url = API+"/lyric?os=osx&id=" + songId + "&lv=-1&kv=-1&tv=-1";
        Map<String, Object> map = new HashMap<>();
        map.put(CSRF_TOKEN, "");

        String result;
        String raw;

        raw = connection(url, prepare(map));
        LyricRaw json = gson.fromJson(raw, LyricRaw.class);
        try {
            result = json.lrc.lyric;
            return result;
        } catch (Exception e) {}

        return null;
    }

    public static String getSongSrc (int songId) {
        return "http://music.163.com/song/media/outer/url?id="+songId+".mp3";
    }

    public static Song song(String id) {
        String url = API+"/v3/song/detail?csrf_token=";
        Map<String, Object> map = new HashMap<>();
        Map<String, String> ids = new HashMap<>();
        ids.put("id", id);
        map.put("c", "[" + jsonEncode(ids) + "]");
        map.put(CSRF_TOKEN, "");

        SongRaw songRaw = gson.fromJson(connection(url, prepare(map)), SongRaw.class);

        return Song.from(songRaw);
    }

    public static HotMusicList hotList () {
        String url = API+"/playlist/list";
        Map<String, Object> map = new HashMap<>();
        map.put(CSRF_TOKEN, "");
        map.put("cat", "全部");
        map.put("order", "hot");
        map.put("offset", 0);
        map.put("total", "true");
        map.put("limit", 6);
        try {
            String res = connection(url, prepare(map));
            return gson.fromJson(res, HotMusicList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void hotList (@NonNull final Callback<HotMusicList> cb) {
        new Thread(new java.lang.Runnable() {
            @Override
            public void run() {
                HotMusicList hotMusicList = hotList();

                if (hotMusicList != null)
                    cb.call(hotMusicList);
            }
        }).start();
    }

    public static void banner (@NonNull final Callback<List<BannerRaw.Banner>> cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL curl;
                BufferedReader reader;
                StringBuffer result = new StringBuffer();
                try {
                    curl = new URL("http://music.163.com/discover");
                    HttpURLConnection conn = (HttpURLConnection) curl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Referer", REFERER);
                    conn.setRequestProperty("User-Agent", USERAGENT);

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = new String(line.getBytes(), "utf-8");
                        result.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    result = null;
                }

                if (result != null) {
                    String regex = "<script[^>]*>\\s*window\\.Gbanners\\s*=\\s*([^;]+?);\\s*<\\/script>";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(result.toString());

                    if (!matcher.find()) return;

                    String json = "{code:200, banners:"+matcher.group(1)+"}";

                    BannerRaw banner = null;
                    try {
                        banner = gson.fromJson(json, BannerRaw.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (banner != null) {
                        ArrayList<BannerRaw.Banner> arrayList = new ArrayList<>(Arrays.asList(banner.banners));
                        cb.call(arrayList);
                    }
                }
            }
        }).start();
    }

    public static void playlist (final String playlistId, @NonNull final Callback<PlaylistRaw> cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = API+"/v3/playlist/detail?csrf_token=";
                Map<String, Object> map = new HashMap<>();
                map.put("id", playlistId);
                map.put("n", 100000);
                map.put("s", 8); // 最近s个收藏者
                map.put(CSRF_TOKEN, "");
                try {
                    String res = connection(url, prepare(map));
                    PlaylistRaw playlist = gson.fromJson(res, PlaylistRaw.class);

                    if (playlist != null)
                        cb.call(playlist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 未重构
    public static String url (String id) {
        String url = API+"/song/enhance/player/url?csrf_token=";
        String[] urls = { id };
        Map<String, Object> map = new HashMap<>();
        map.put("ids", urls);
        map.put("br", 999000);
        map.put(CSRF_TOKEN, "");
        try {
            return connection(url, prepare(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String search(String s) {
        int limit = 1;
        int offset = 0;
        int type = 1;
        String url = API+"/cloudsearch/get/web?csrf_token=";
        Map<String, Object> map = new HashMap<>();
        map.put("s", s);
        map.put("type", type);
        map.put("limit", limit);
        map.put("total", "true");
        map.put("offset", offset);
        map.put("csrf_token", "");
        try {
            return connection(url, prepare(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
