package cn.deepkolos.simplemusic3.Model.Raw;

public class SongRaw {
    public Songs[] songs;

    public static class Songs {
        public int id;
        public String name;
        public AL al;
        public AR[] ar;
        public int fee;
        public H h;
        public int mv;

        public static class AL {
            public String picUrl;
        }

        public static class AR {
            public int id;
            public String name;
        }

        public static class H {
            public int br;
        }
    }
}
