package cn.deepkolos.simplemusic3.Widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cn.deepkolos.simplemusic3.R;
import cn.deepkolos.simplemusic3.Utils.UiUtils;

public class BottomPopupWithListView extends BottomPopup {
    private ListView $listView;

    public BottomPopupWithListView(Context context, View contentView) {
        super(context, contentView);
    }

    @Override
    public void setContent(View view) { }

    public ListView getListView () {
        return $listView;
    }

    @Override
    public int getScrollViewScrollY() {
        return UiUtils.getListViewScrollY($listView);
    }

    @Override
    protected void initContent(View view) {
        $maxLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_bottom_popup_content_listview, null, false);
        $listView = $maxLayout.findViewById(R.id.view_bottom_popup_listView);
        $listView.setOnScrollListener(new UiUtils.ListViewOnScrollY() {
            @Override
            public void onScroll(int scrollY) {
                if (scrollY == 0)
                    $listView.scrollTo(0, 0);
            }
        });
    }

}
