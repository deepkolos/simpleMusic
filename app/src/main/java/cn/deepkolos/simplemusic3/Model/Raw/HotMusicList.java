package cn.deepkolos.simplemusic3.Model.Raw;

public class HotMusicList {
    public String cat;
    public List[] playlists;

    public static class List {
        public int playCount;
        public String id;
        public String name;
        public Creator creator;
        public String coverImgUrl;

        public static class Creator {
            public String nickname;
            public String avatarUrl;
        }
    }
}
