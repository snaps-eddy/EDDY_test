package com.snaps.mobile.activity.edit.fragment.canvas;

import android.view.ViewGroup.LayoutParams;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.spc.SimplePhotoBookCanvas;

public class SimplePhotoBookCanvasFragment extends ThemeBookCanvasFragment {

    private static final String TAG = SimplePhotoBookCanvasFragment.class.getSimpleName();

    @Override
    public void makeSnapsCanvas() {
        int index = getArguments().getInt("index");
        try {
            if (canvas == null) {
                canvas = new SimplePhotoBookCanvas(getActivity());
                canvas.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

                if (isPreview) {
                    canvas.setZoomable(false);
                    canvas.setIsPreview(true);
                }
            }

            pageLoad = getArguments().getBoolean("pageLoad");

            boolean isPageSaving = getArguments().getBoolean("pageSave", false);
            canvas.setIsPageSaving(isPageSaving);

            if (pageLoad) {
                handleIncreaseCanvasLoadCompleteCount();
            }

            SnapsPage spcPage = getPageList().get(index);

            canvas.setCallBack(this);

            imageRange(spcPage, index);

            canvas.setSnapsPage(spcPage, index, true, null);
        } catch (Exception e) {
            Dlog.e(TAG, "이미지 저장 실패" + " : " + index, e);
            setPageThumbnailFail(index);
        }
    }

    @Override
    protected void imageRange(SnapsPage page, int index) {
        try {

            SnapsLayoutControl layout;

            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                MyPhotoSelectImageData imgData = layout.imgData;
                if (imgData != null)
                    layout.angle = imgData.ROTATE_ANGLE + "";
            }

            // 커버처리...텍스트 처리..
            if (page.type.equalsIgnoreCase("cover")) {
                SnapsTextControl textLayout;
                SnapsControl control;
                for (int i = 0; i < page.getControlList().size(); i++) {

                    control = page.getControlList().get(i);
                    if (control instanceof SnapsTextControl) {
                        textLayout = (SnapsTextControl) control;
                        if (page.type.equalsIgnoreCase("cover")) {// 커버-제목
                            if (textLayout.format.verticalView.equalsIgnoreCase("true")) {
                                // 커버인경우 높이와 넓이를 바꿔준다...
                                if (!"".equals(textLayout.width) && !"".equals(textLayout.height)) {
                                    int iWidth = Integer.valueOf(textLayout.width);
                                    int iHeight = Integer.valueOf(textLayout.height);
                                    if (iWidth < iHeight) {
                                        String tmpWidth = textLayout.width;
                                        textLayout.width = textLayout.height;
                                        textLayout.height = tmpWidth;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

    }

}
