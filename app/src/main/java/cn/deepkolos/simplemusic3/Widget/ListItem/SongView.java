package cn.deepkolos.simplemusic3.Widget.ListItem;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Utils.UnitHelper;

public class SongView extends RelativeLayout {
    TextView $index;
    TextView $name;
    TextView $singer;
    ImageView $mvBtn;
    ImageView $settingBtn;
    ImageView $badgeSQ;
    ImageView $badgeDownloaded;
    ImageView $playingIcon;
    LinearLayout $container;
    RelativeLayout $self;

    boolean hasSQ = false;
    boolean hasDownloaded = false;
    boolean hasMVToPlay = false;
    boolean isPlaying = false;
    boolean noIndexOrPlayingIcon = false;
    String name;
    String index;
    String singer;

    public SongView(Context context) {
        super(context);
        init(context);
    }

    public SongView(Context context, @NonNull Model model) {
        this(context);
        init(context);
        setModel(model);
    }

    public SongView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SongView);
        hasSQ = ta.getBoolean(R.styleable.SongView_hasSQ, false);
        hasDownloaded = ta.getBoolean(R.styleable.SongView_hasDownloaded, false);
        hasMVToPlay = ta.getBoolean(R.styleable.SongView_hasMVToPlay, false);
        name = ta.getString(R.styleable.SongView_name);
        index = ta.getString(R.styleable.SongView_index);
        singer = ta.getString(R.styleable.SongView_singer);
        isPlaying = ta.getBoolean(R.styleable.SongView_isPlaying, false);
        noIndexOrPlayingIcon = ta.getBoolean(R.styleable.SongView_noIndexOrPlayingIcon, false);
        ta.recycle();

        setHasSQ(hasSQ);
        setHasDownloaded(hasDownloaded);
        setHasMVToPlay(hasMVToPlay);
        setName(name);
        setIndex(index);
        setSinger(singer);
        setPlaying(isPlaying);
        setNoIndexOrPlayingIcon(noIndexOrPlayingIcon);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_song_item, this, true);
        $index = findViewById(R.id.view_album_songlist_item_index);
        $name = findViewById(R.id.view_album_songlist_item_title);
        $singer = findViewById(R.id.view_album_songlist_item_singer);
        $mvBtn = findViewById(R.id.view_album_songlist_item_play_mv_btn);
        $settingBtn = findViewById(R.id.view_album_songlist_item_setting_btn);
        $badgeSQ = findViewById(R.id.view_album_songlist_item_sq_badge);
        $badgeDownloaded = findViewById(R.id.view_album_songlist_item_downloaded_badge);
        $playingIcon = findViewById(R.id.view_album_songlist_item_playing_icon);
        $container = findViewById(R.id.view_album_songlist_item_container);
        $self = findViewById(R.id.view_album_songlist_item);
    }

    public void setModel (Model model) {
        if (model != null) {
            setHasSQ(model.getHasSQ());
            setHasDownloaded(model.getHasDownloaded());
            setHasMVToPlay(model.getHasMVToPlay());
            setName(model.getName());
            setIndex(model.getIndex());
            setSinger(model.getSinger());
        }
    }

    public void setHasSQ(boolean hasSQ) {
        this.hasSQ = hasSQ;

        $badgeSQ.setVisibility(hasSQ ? VISIBLE : GONE);
    }

    public void setHasDownloaded(boolean hasDownloaded) {
        this.hasDownloaded = hasDownloaded;

        $badgeDownloaded.setVisibility(hasDownloaded ? VISIBLE : GONE);
    }

    public void setHasMVToPlay(boolean hasMVToPlay) {
        this.hasMVToPlay = hasMVToPlay;

        $mvBtn.setVisibility(hasMVToPlay ? VISIBLE : GONE);
    }

    public void setName(String name) {
        this.name = name;

        if (name != null)
            $name.setText(name);
    }

    public void setIndex(String index) {
        this.index = index;
        if (index != null)
            $index.setText(index);
    }

    public void setSinger(String singer) {
        this.singer = singer;
        if (singer != null)
            $singer.setText(singer);
    }

    public void setPlaying (boolean val) {
        isPlaying = val;
        $playingIcon.setVisibility(val ? VISIBLE : INVISIBLE);
    }

    public void setNoIndexOrPlayingIcon(boolean noIndexOrPlayingIcon) {
        this.noIndexOrPlayingIcon = noIndexOrPlayingIcon;

        if (noIndexOrPlayingIcon) {
            $playingIcon.setVisibility(GONE);
            $index.setVisibility(GONE);
            $container.setPadding(UnitHelper.dpToPx(10),0,0,0);
        } else {
            $playingIcon.setVisibility(VISIBLE);
            $index.setVisibility(VISIBLE);
            $container.setPadding(0,0,0,0);
        }
    }

    public static class Model {
        String name;
        String index;
        String singer;
        boolean hasSQ;
        boolean hasDownloaded;
        boolean hasMVToPlay;

        public Model(String name, String singer, String index, boolean hasSQ, boolean hasDownloaded, boolean hasMVToPlay) {
            this.name = name;
            this.index = index;
            this.singer = singer;
            this.hasSQ = hasSQ;
            this.hasDownloaded = hasDownloaded;
            this.hasMVToPlay = hasMVToPlay;
        }

        public boolean getHasSQ() {
            return hasSQ;
        }

        public boolean getHasDownloaded() {
            return hasDownloaded;
        }

        public boolean getHasMVToPlay() {
            return hasMVToPlay;
        }

        public String getName() {
            return name;
        }

        public String getIndex() {
            return index;
        }

        public String getSinger() {
            return singer;
        }

        public void setHasSQ(boolean hasSQ) {
            this.hasSQ = hasSQ;
        }

        public void setHasDownloaded(boolean hasDownloaded) {
            this.hasDownloaded = hasDownloaded;
        }

        public void setHasMVToPlay(boolean hasMVToPlay) {
            this.hasMVToPlay = hasMVToPlay;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }
    }

    public void setOnSettingBtnClick (OnClickListener listener) {
        $settingBtn.setOnClickListener(listener);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        $self.setOnClickListener(l);
    }
}
