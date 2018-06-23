package cn.deepkolos.simplemusic3.Storage.Loader;

import android.net.Uri;

import cn.deepkolos.simplemusic3.Http.Download;
import cn.deepkolos.simplemusic3.Http.MusicApi;
import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.Storage.Store.SongStore;

public class SongLoader {

    public static Uri get (Song song) {
        Uri uri;
        String str;

        // 下载的歌曲
        uri = SongStore.get(song.name, song.singer);
        if (uri != null) return uri;

        // 网络Uri
        uri = Uri.parse(MusicApi.getSongSrc(song.songId));

        return uri;
    }

    public static boolean add (Song song) {
        String mp3 = Download.mp3(song.songId);

        if (mp3 != null) {
            SongStore.add(song.name, song.singer, mp3);
            return true;
        }

        return false;
    }
}
