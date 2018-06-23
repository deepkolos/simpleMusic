package cn.deepkolos.simplemusic3.Storage.Loader;

import cn.deepkolos.simplemusic3.Http.Download;
import cn.deepkolos.simplemusic3.Model.Song;
import cn.deepkolos.simplemusic3.Storage.Store.LyricStore;

public class LyricLoader {
    public static String get (Song song) {
        String lyric;

        // 专用缓存
        lyric = LyricStore.get(song.name, song.singer);
        if (lyric != null) return lyric;

        lyric = Download.lyric(song.songId);

        if (lyric != null && song.isLocal) {
            // 如果是已经在自检歌单的歌词就添加到缓存
            LyricStore.add(song.name, song.singer, lyric);
        }

        return lyric;
    }
}
