package cn.deepkolos.simplemusic3.Widget.ListItem;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.deepkolos.simplemusic3.R;

public class PlayListSongView extends LinearLayout {
    TextView $name;
    TextView $singer;
    ImageView $link;
    ImageView $delete;
    ImageView $playIcon;
    boolean isPlaying = false;
    static int red = Color.rgb(211, 58, 49);
    static int black = Color.BLACK;
    static int grey = Color.rgb(122, 122, 122);

    public PlayListSongView(Context context) {
        super(context);
        init(context);
    }

    public PlayListSongView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init (Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_playlist_song_item, this, true);
        $name = findViewById(R.id.view_playlist_song_item_name);
        $singer = findViewById(R.id.view_playlist_song_item_singer);
        $link = findViewById(R.id.view_playlist_song_item_link);
        $delete = findViewById(R.id.view_playlist_song_item_delete);
        $playIcon = findViewById(R.id.view_playlist_song_item_playIcon);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
        $playIcon.setVisibility(playing ? VISIBLE : GONE);
        if (isPlaying) {
            $name.setTextColor(red);
            $singer.setTextColor(red);
        } else {
            $name.setTextColor(black);
            $singer.setTextColor(grey);
        }
    }

    public void setSongName (String val) {
        $name.setText(val);
    }

    public void setSinger (String val) {
        if (val != null) {
            val = " - " + val;
            $singer.setText(val);
        }
    }

    public void setLink (String val) {

    }

    public void setModel (Model data) {
        if (data == null) return;

        setSongName(data.getName());
        setLink(data.getLink());
        setSinger(data.getSinger());
        setPlaying(data.isPLaying());
    }

    public static class Model {
        private String name;
        private String singer;
        private boolean isPLaying;
        private String link;

        public String getName() {
            return name;
        }

        public String getSinger() {
            return singer;
        }

        public boolean isPLaying() {
            return isPLaying;
        }

        public String getLink() {
            return link;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public void setPLaying(boolean PLaying) {
            isPLaying = PLaying;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public Model(String name, String singer, boolean isPLaying, String link) {
            this.name = name;
            this.singer = singer;
            this.isPLaying = isPLaying;
            this.link = link;
        }
    }
}
