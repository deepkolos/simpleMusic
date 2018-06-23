package cn.deepkolos.simplemusic3.Widget.ListItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.service.quicksettings.Tile;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.deepkolos.simplemusic3.R;

public class AlbumView extends LinearLayout {
    ImageView $firstSongCover;
    ImageView $downloadedIcon;
    ImageView $playingIcon;
    TextView $title;
    TextView $subTitle;
    ImageView $settingBtn;
    RelativeLayout $container;
    int defaultResId = R.layout.view_album_list_item;

    public AlbumView(Context context) {
        super(context);
        init(context, defaultResId);
    }

    public AlbumView(Context context, int resId) {
        super(context);
        init(context, resId);
    }

    public AlbumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, defaultResId);
    }

    private void init (Context context, int resId) {
        LayoutInflater.from(context).inflate(resId, this, true);
        $firstSongCover = findViewById(R.id.songList_album_cover);
        $downloadedIcon = findViewById(R.id.songList_album_downloaded_icon);
        $title = findViewById(R.id.songList_album_title);
        $subTitle = findViewById(R.id.songList_album_subTitle);
        $settingBtn = findViewById(R.id.songList_album_setting_btn);
        $container = findViewById(R.id.songList_album_container);
    }

    public void setPlay (boolean val){
        $playingIcon.setVisibility(val ? VISIBLE : GONE);
    }

    public void setTitle (String val) {
        if (val != null)
            $title.setText(val);
    }

    public void setSongNum(int val) {
        String str = val+"首";
        $subTitle.setText(str);
    }

    public void setSubTitle (String title) {
        if (title != null)
            $subTitle.setText(title);
    }

    public void setCover (Bitmap bitmap) {
        if (bitmap != null)
            $firstSongCover.setImageBitmap(bitmap);
    }

    public void setCover (Drawable drawable) {
        if (drawable != null)
            $firstSongCover.setImageDrawable(drawable);
    }

    public void setCover (int resId) {
        $firstSongCover.setImageResource(resId);
    }

    public void setDownloadIcon (boolean val) {
        $downloadedIcon.setVisibility(val ? VISIBLE : GONE);
    }

    public void setOnSettingBtnClick (OnClickListener listener) {
        $settingBtn.setOnClickListener(listener);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        $container.setOnClickListener(l);
    }

    public void setModel (Model data) {
        setCover(data.getDrawableCover());
        setCover(data.getBitmapCover());
        setTitle(data.getTitle());
        setSongNum(data.getSongNum());
        setSubTitle(data.getTitle());
    }

    public static class Model {
        int type = 1;
        Bitmap bitMapCover;
        Drawable drawableCover;
        String title;
        String subTitle;
        int songNum;

        public Model(int type, Bitmap bitMapCover, String title, int songNum) {
            this.type = type;
            this.bitMapCover = bitMapCover;
            this.title = title;
            this.songNum = songNum;
        }

        public Model(Bitmap bitMapCover, String title, int songNum) {
            this.bitMapCover = bitMapCover;
            this.title = title;
            this.songNum = songNum;
        }

        public Model(Drawable drawableCover, String title, int songNum) {
            this.drawableCover = drawableCover;
            this.title = title;
            this.songNum = songNum;
        }

        public Model(String title, int songNum) {
            this.title = title;
            this.songNum = songNum;
        }

        public Model(int type, String title, int songNum) {
            this.type = type;
            this.title = title;
            this.songNum = songNum;
        }

        public Model(int type, String title, String subTitle) {
            this.type = type;
            this.title = title;
            this.subTitle = subTitle;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Bitmap getBitmapCover() {
            return bitMapCover;
        }

        public Drawable getDrawableCover() {
            return drawableCover;
        }

        public void setBitMapCover(Bitmap bitMapCover) {
            this.bitMapCover = bitMapCover;
        }

        public void setDrawableCover(Drawable drawableCover) {
            this.drawableCover = drawableCover;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSubTitle (String title) {
            this.subTitle = title;
        }

        public int getSongNum() {
            return songNum;
        }

        public void setSongNum(int songNum) {
            this.songNum = songNum;
        }

        public String getSubTitle() {
            return subTitle;
        }
    }
}
