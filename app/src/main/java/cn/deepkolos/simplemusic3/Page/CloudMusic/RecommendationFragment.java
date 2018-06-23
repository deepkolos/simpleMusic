package cn.deepkolos.simplemusic3.Page.CloudMusic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.deepkolos.simplemusic3.Http.MusicApi;
import cn.deepkolos.simplemusic3.Model.Raw.BannerRaw;
import cn.deepkolos.simplemusic3.Model.Raw.HotMusicList;
import cn.deepkolos.simplemusic3.Page.AlbumActivity;
import cn.deepkolos.simplemusic3.Page.Utils.ViewPagerFragment;
import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Storage.Loader.ImageLoader;
import cn.deepkolos.simplemusic3.Utils.Callback;
import cn.deepkolos.simplemusic3.Utils.NumberUtils;
import cn.deepkolos.simplemusic3.Widget.Button.RectangleIconButton;
import cn.deepkolos.simplemusic3.Widget.CarouselItem;
import cn.deepkolos.simplemusic3.Widget.CarouselView;

public class RecommendationFragment extends ViewPagerFragment {
    private CarouselView $carousel;
    private List<BannerRaw.Banner> banners;
    private ArrayList<View> tiles; // 推荐的6个歌单磁贴

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View $root = inflater.inflate(R.layout.fragment_recommendation, container, false);
        initCarousel($root);
        initData();

        tiles = new ArrayList<>();
        $root.findViewsWithText(tiles, "hotList", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        return $root;
    }

    private void initCarousel (View view) {
        $carousel = view.findViewById(R.id.fragment_recommendation_carousel);

        $carousel.setCount(8);
        $carousel.setAutoPlay(true);

//        final int[] colors = {
//                Color.rgb(10,80,10),
//                Color.rgb(20,70,20),
//                Color.rgb(30,60,30),
//                Color.rgb(40,50,40),
//                Color.rgb(50,40,50),
//                Color.rgb(60,30,60),
//                Color.rgb(70,20,70),
//                Color.rgb(80,10,80),
//        };

        $carousel.setOnGetView(new CarouselView.OnGetView() {
            @Override
            public View getView(int position) {
                final CarouselItem item = new CarouselItem(getContext());
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getPlaylistId() == -1) return;

                        Intent intent = new Intent();
                        intent.setClass(getContext(), AlbumActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("PLAYLIST_ID", item.getPlaylistId());
                        startActivity(intent, bundle);
                    }
                });
                return item;
            }
        });

        $carousel.setOnUpdateView(new CarouselView.onUpdateView() {
            @Override
            public void updateView(View view, int position) {
                final CarouselItem item;
                if (view instanceof CarouselItem) {
                    if (banners == null) return;

                    item = (CarouselItem) view;
                    final BannerRaw.Banner banner = banners.get(position);

                    ImageLoader.get(banner.picUrl, new Callback<Bitmap>() {
                        @Override
                        public void call(final Bitmap val) {
                            $carousel.post(new Runnable() {
                                @Override
                                public void run() {
                                    item.setImage(val);
                                }
                            });
                        }
                    });
                }
            }
        });

        $carousel.applyChange();
    }

    public void initData () {
        // banner
        MusicApi.banner(new Callback<List<BannerRaw.Banner>>() {
            @Override
            public void call(List<BannerRaw.Banner> val) {
                banners = val;
                $carousel.getAdapter().notifyDataSetChanged();
            }
        });

        // 推荐歌单
        MusicApi.hotList(new Callback<HotMusicList>() {
            @Override
            public void call(final HotMusicList hotMusicList) {
                $carousel.post(new java.lang.Runnable() {
                    @Override
                    public void run() {
                        RectangleIconButton button;
                        for (int i = 0; i < tiles.size(); i++) {
                            button = (RectangleIconButton) tiles.get(i);

                            button.setTitle(hotMusicList.playlists[i].name);
                            button.setBottomLeftText(hotMusicList.playlists[i].creator.nickname);
                            button.setTopRightText(NumberUtils.shorten(hotMusicList.playlists[i].playCount));

                            final RectangleIconButton finalButton = button;

                            ImageLoader.get(hotMusicList.playlists[i].coverImgUrl, new Callback<Bitmap>() {
                                @Override
                                public void call(final Bitmap val) {
                                    $carousel.post(new java.lang.Runnable() {
                                        @Override
                                        public void run() {
                                            finalButton.setIcon(val);
                                        }
                                    });
                                }
                            });

                            final int finalI = i;
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setClass(getContext(), AlbumActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(AlbumActivity.PLAYLIST_ID, hotMusicList.playlists[finalI].id);
                                    bundle.putBoolean(AlbumActivity.IS_LOCAL, false);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
