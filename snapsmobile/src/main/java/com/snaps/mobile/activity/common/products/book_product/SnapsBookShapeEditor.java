package com.snaps.mobile.activity.common.products.book_product;

import android.graphics.Rect;
import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditorCommonImplement;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.GifTutorialView;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;

/**
 * Created by ysjeong on 2017. 10. 19..
 */

public abstract class SnapsBookShapeEditor extends SnapsProductBaseEditorCommonImplement {
    private static final String TAG = SnapsBookShapeEditor.class.getSimpleName();

    protected SnapsBookShapeEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    protected boolean isCoverPage() {
        return getCurrentPageIndex() == 0;
    }

    @Override
    public void preHandleLoadedTemplateInfo(SnapsTemplate template) {
        template.clonePage();
    }

    @Override
    protected void showEditActivityTutorial() {
        handleShowPinchZoomTutorial();
        super.showEditActivityTutorial();
    }

    private void handleShowPinchZoomTutorial() {
        SnapsTutorialAttribute.GIF_TYPE gifType = SnapsTutorialAttribute.GIF_TYPE.PINCH_ZOOM;
        //KT 북
        if (Config.isKTBook()) {
            gifType = SnapsTutorialAttribute.GIF_TYPE.KT_BOOK_EDITOR;
        }
        SnapsTutorialUtil.showGifView(getActivity(), new SnapsTutorialAttribute.Builder().setGifType(gifType).create(), new GifTutorialView.CloseListener() {
            @Override
            public void close() {
                handlePinchZoomTutorialOnClose();

                SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();
            }
        });
    }

    @Override
    public Rect getQRCodeRect() {
        try {
            return PhotobookCommonUtils.getPhotoBookQRCodeRect(getTemplate());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return null;
    }
}
