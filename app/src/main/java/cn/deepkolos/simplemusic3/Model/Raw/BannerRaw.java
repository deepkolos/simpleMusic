package cn.deepkolos.simplemusic3.Model.Raw;

public class BannerRaw {
    public Banner[] banners;

    public static class Banner {
        public String picUrl;
        public String targetId;
        public String targetType;
    }
}
