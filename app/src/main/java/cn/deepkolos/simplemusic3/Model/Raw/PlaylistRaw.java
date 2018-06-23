package cn.deepkolos.simplemusic3.Model.Raw;

public class PlaylistRaw {
    public Playlist playlist;


    public static class Playlist {
        public String name;
        public String coverImgUrl;
        public int subscribedCount;
        public SongRaw.Songs[] tracks;
        public HotMusicList.List.Creator creator;
    }
}
