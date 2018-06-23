package cn.deepkolos.simplemusic3.Widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.deepkolos.simplemusic3.Utils.UnitHelper;
import cn.deepkolos.simplemusic3.Widget.Layout.ClipFrameLayout;

public class CarouselItem extends ClipFrameLayout {
    private ImageView imageView;
    private int playlistId = -1;

    public CarouselItem(Context context) {
        super(context);

        LinearLayout linearLayout = new LinearLayout(getContext());
        imageView = new ImageView(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.leftMargin = UnitHelper.dpToPx(6);
        params.rightMargin = UnitHelper.dpToPx(6);
        imageView.setLayoutParams(params);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.addView(imageView);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setRadius(UnitHelper.dpToPx(5));
        setInset(UnitHelper.dpToPx(6), 0, UnitHelper.dpToPx(6), 0);
        addView(linearLayout);
    }

    public void setImage (Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }
}
