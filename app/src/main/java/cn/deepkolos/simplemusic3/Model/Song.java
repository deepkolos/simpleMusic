package cn.deepkolos.simplemusic3.Model;

import cn.deepkolos.simplemusic3.Model.Raw.SongRaw;

public class Song {
    public int songId;
    public String singerId;
    public String name;
    public String singer;
    public String coverSrc;

    public boolean isLocal;
    public boolean canPlay;
    public boolean downloaded = false; // 下载的状态是否需要缓存?
    public boolean SQ;
    public boolean hasMv;

    public Song() {}

    public Song(int songId, String singerId, String name, String singer, String coverSrc, boolean isLocal) {
        this.songId = songId;
        this.singerId = singerId;
        this.name = name;
        this.singer = singer;
        this.coverSrc = coverSrc;
        this.isLocal = isLocal;
    }

    public static Song from(SongRaw songRaw) {
        SongRaw.Songs songs = songRaw.songs[0];
        return from(songs);
    }

    public static Song from (SongRaw.Songs songs) {
        Song song = new Song();
        try {
            song.songId = songs.id;
            song.coverSrc = songs.al.picUrl;
            song.name = songs.name;

            StringBuilder singerBuilder = new StringBuilder();
            StringBuilder singerIdBuilder = new StringBuilder();

            for (int i = 0; i < songs.ar.length; i++ ){
                singerBuilder.append(songs.ar[i].name);
                singerIdBuilder.append(songs.ar[i].id);

                if (i != songs.ar.length-1) {
                    singerBuilder.append("/");
                    singerIdBuilder.append("/");
                }
            }

            song.singer = singerBuilder.toString();
            song.singerId = singerIdBuilder.toString();
            song.canPlay = songs.fee == 0;
            song.SQ = songs.h != null;
            song.hasMv = songs.mv != 0;
            // todo
            // song.IS_LOCAL 打算从磁盘获取

            return song;
        } catch (Exception ignored) {}

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  Song) {
            Song target = (Song) obj;
            return target.songId == songId;
        }
        return false;
    }
}
