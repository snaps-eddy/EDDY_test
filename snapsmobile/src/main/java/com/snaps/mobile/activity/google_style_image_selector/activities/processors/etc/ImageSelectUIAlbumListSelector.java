package com.snaps.mobile.activity.google_style_image_selector.activities.processors.etc;

import android.app.Activity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectStateChangedListener;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAlbumListSelectorAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.CustomLinearLayoutManager;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2016. 12. 15..
 */

public class ImageSelectUIAlbumListSelector {

    private boolean isCreated = false;
    private Activity activity = null;

    private ImageSelectAlbumListSelectorAdapter adapter = null;

    private IImageSelectStateChangedListener stateChangedListener = null;

    public ImageSelectUIAlbumListSelector(Activity activity, IImageSelectStateChangedListener stateChangedListener) {
        this.activity = activity;

        setStateChangedListener(stateChangedListener);

        initialize();
    }

    private void initialize() {
        if (activity == null) return;

        SnapsRecyclerView recyclerView = (SnapsRecyclerView) activity.findViewById(R.id.include_google_photo_style_image_select_album_list_recycler_view);

        adapter = new ImageSelectAlbumListSelectorAdapter(activity, getStateChangedListener());

        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public int getAlbumListCount() {
        return adapter != null ? adapter.getItemCount() : 0;
    }

    public void clearAdapterData() {
        if (adapter == null) return;
        adapter.clear();
    }

    public void makeSelector(ArrayList<IAlbumData> cusors) {
        if (adapter == null || isCreated) return;
        adapter.setData(cusors);
        isCreated = true;
    }

    public IImageSelectStateChangedListener getStateChangedListener() {
        return stateChangedListener;
    }

    public void setStateChangedListener(IImageSelectStateChangedListener stateChangedListener) {
        this.stateChangedListener = stateChangedListener;
    }

    public ArrayList<IAlbumData> getCursors() {
        if (adapter == null) return null;
        return adapter.getAlbumCursors();
    }
}
