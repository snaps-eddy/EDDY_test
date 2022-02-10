package com.snaps.mobile.activity.google_style_image_selector.datas;

import android.view.View;
import android.widget.TextView;

import com.snaps.mobile.activity.diary.customview.SnapsRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;

/**
 * Created by ysjeong on 2016. 11. 25..
 */

public class ImageSelectUITrayControl {
    private TextView leftCountView;
    private TextView rightCountView;
    private SnapsRecyclerView trayThumbRecyclerView = null;
    private ImageSelectTrayBaseAdapter trayAdapter = null;
    private View trayAllViewSelectLayout = null;
    private ImageSelectAdapterHolders.TrayThumbnailItemHolder traySingleThumbnailItemHolder = null;

    public void releaseInstance() {
        if (trayThumbRecyclerView != null) {
            trayThumbRecyclerView = null;
        }

        if (trayAllViewSelectLayout != null) {
            trayAllViewSelectLayout = null;
        }

        if (traySingleThumbnailItemHolder != null) {
            traySingleThumbnailItemHolder = null;
        }
    }

    public View getTrayAllViewSelectLayout() {
        return trayAllViewSelectLayout;
    }

    public void setTrayAllViewSelectLayout(View trayAllViewSelectLayout) {
        this.trayAllViewSelectLayout = trayAllViewSelectLayout;
    }

    public ImageSelectTrayBaseAdapter getTrayAdapter() {
        return trayAdapter;
    }

    public void setTrayAdapter(ImageSelectTrayBaseAdapter trayAdapter) {
        this.trayAdapter = trayAdapter;
    }

    public SnapsRecyclerView getTrayThumbRecyclerView() {
        return trayThumbRecyclerView;
    }

    public TextView getLeftCountView() {
        return leftCountView;
    }

    public void setLeftCountView(TextView leftCountView) {
        this.leftCountView = leftCountView;
    }

    public TextView getRightCountView() {
        return rightCountView;
    }

    public void setRightCountView(TextView rightCountView) {
        this.rightCountView = rightCountView;
    }

    public void setTrayThumbRecyclerView(SnapsRecyclerView trayThumbRecyclerView) {
        this.trayThumbRecyclerView = trayThumbRecyclerView;
    }

    public ImageSelectAdapterHolders.TrayThumbnailItemHolder getTraySingleThumbnailItemHolder() {
        return traySingleThumbnailItemHolder;
    }

    public void setTraySingleThumbnailItemHolder(ImageSelectAdapterHolders.TrayThumbnailItemHolder traySingleThumbnailItemHolder) {
        this.traySingleThumbnailItemHolder = traySingleThumbnailItemHolder;
    }
}
