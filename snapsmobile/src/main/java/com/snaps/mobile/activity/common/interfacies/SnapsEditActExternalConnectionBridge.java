package com.snaps.mobile.activity.common.interfacies;

import android.app.Activity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public interface SnapsEditActExternalConnectionBridge {
    ArrayList<MyPhotoSelectImageData> getGalleryList();
    ArrayList<SnapsPage> getPageList();
    SnapsTemplate getTemplate();
    Activity getActivity();

    int getCanvasLoadCompleteCount();
    void increaseCanvasLoadCompleteCount();
    void decreaseCanvasLoadCompleteCount();

    void pageProgressUnload();
    void showPageProgress();

    void setPageThumbnailFail(int index);
    void setPageThumbnail(final int pageIdx, String filePath);

    void setPageFileOutput(final int index);

    SnapsProductEditorAPI getProductEditorAPI();
}
