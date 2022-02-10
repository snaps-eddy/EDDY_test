package com.snaps.mobile.activity.edit.fragment.canvas;

import android.app.ActionBar.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.Gravity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.mobile.activity.edit.spc.SNSBookPageCanvas;
import com.snaps.mobile.activity.edit.thumbnail_skin.SnapsThumbNailUtil;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;
import com.snaps.mobile.order.order_v2.exceptions.SnapsIOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.snaps.common.utils.imageloader.ImageLoader.DEFAULT_BITMAP_CONFIG;

public class SNSBookCanvasFragment extends SnapsCanvasFragment {
    private static final String TAG = SNSBookCanvasFragment.class.getSimpleName();
    public String titleBgColor;
    public String coverColor;

    private void reLoadView() {
        int index = getArguments().getInt("index");
        SnapsPage spcPage = getPageList().get(index);
        imageRange(spcPage, index);
        canvas.changeLayoutLayer();
    }

    @Override
    public void makeSnapsCanvas() {
        int index = getArguments().getInt("index");

        if (canvas == null) {
            canvas = new SNSBookPageCanvas(getActivity());
            canvas.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            canvas.setGravity(Gravity.CENTER);
        }

        pageLoad = getArguments().getBoolean("pageLoad");

        if (pageLoad) {
            handleIncreaseCanvasLoadCompleteCount();
        }

        if (getPageList() == null || getPageList().size() < 1) return;
        SnapsPage spcPage = getPageList().get(index);

        canvas.setCallBack(this);
        imageRange(spcPage, index);

        canvas.setSnapsPage(spcPage, index, true, null);
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

            if (page.type.equalsIgnoreCase("cover")) {
                SnapsTextControl textLayout;
                ArrayList<SnapsControl> arrTextList = page.getTextControlList();
                for (int i = 0; i < arrTextList.size(); i++) {
                    textLayout = (SnapsTextControl) arrTextList.get(i);
                    if (textLayout != null) {
                        if (page.type.equalsIgnoreCase("cover")) {// 커버-제목
                            if (textLayout.format.verticalView.equalsIgnoreCase("true")) {

                                if (!"".equals(textLayout.width) && !"".equals(textLayout.height)) {
                                    int iWidth = Integer.valueOf(textLayout.width);
                                    int iHeight = Integer.valueOf(textLayout.height);
                                    if (iWidth < iHeight) {
                                        String tmpWidth = textLayout.width;
                                        textLayout.width = textLayout.height;
                                        textLayout.height = tmpWidth;
                                        if (textLayout.textList != null && textLayout.textList.size() > 0) {
                                            for (LineText lineText : textLayout.textList) {
                                                tmpWidth = lineText.width;
                                                lineText.width = lineText.height;
                                                lineText.height = tmpWidth;
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (page.type.equalsIgnoreCase("title")) {// 속지-이름

                        } else if (page.type.equalsIgnoreCase("page")) {// 페이지-날짜,내용

                        }
                    }
                }
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void saveLoadImageTask(final int page) {
        String[] productData = SnapsThumbNailUtil.getThumbNailData(Config.getPROD_CODE(), canvas);
        if (productData == null) {
            loadThumbNail(true, page, null, 1);
        } else {
            String product = productData[0];
            final float scale = Float.parseFloat(productData[1]);
            SnapsThumbNailUtil.downSkinImage(getContext(), product, new SnapsThumbNailUtil.SnapsSkinLoadListener() {
                @Override
                public void onSkinLoaded(Bitmap bitmap) {
                    loadThumbNail(false, page, bitmap, scale);
                }
            });
        }
    }

    private void loadThumbNail(final boolean preThumbNail, final int page, final Bitmap skinBitmap, final float scale) {
        ATask.executeVoid(new ATask.OnTaskBitmap() {
            @Override
            public void onPre() {
            }

            @Override
            public Bitmap onBG() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
                return null;
            }

            @Override
            public void onPost(Bitmap bitmap) {
                // 앱이 pause되면 작업을 중지하고, resume 시 재작업하도록 상태표시함.
                if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) {
                    SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_IMGSAVE);
                    return;
                }

                Bitmap pageBitmap = null;
                if (preThumbNail) {
                    pageBitmap = getViewBitmap(page, false);
                } else {
                    pageBitmap = getViewBitmapThumbNail(page, scale, 0, 0);
                    if (skinBitmap != null) {
                        Bitmap tempBitmap = CropUtil.getInSampledBitmapCopy(pageBitmap, DEFAULT_BITMAP_CONFIG, 0);
                        Canvas margeCanvas = new Canvas(tempBitmap);
                        margeCanvas.drawBitmap(skinBitmap, new Matrix(), null);
                        pageBitmap = tempBitmap;
                        //BitmapUtil.bitmapRecycle(tempBitmap);
                    }
                }
                if (pageBitmap != null && !pageBitmap.isRecycled()) {

                    FileOutputStream stream = null;

                    String filePath = "";
                    File file = null;
                    try {
                        file = Config.getTHUMB_PATH("thumbnail_" + page + ".png");
                        if (file == null) throw new SnapsIOException("failed make thumbnail dir");
                        if (!file.exists()) file.createNewFile();
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                        return;
                    }

                    try {
                        stream = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        Dlog.e(TAG, e);
                        return;
                    }

                    try {
                        pageBitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);
                        if (page == 0) {
                            // 대표 썸네일을 저장한다. 콜라주는 이미지 반만...
                            saveLocalThumbnail(getActivity(), pageBitmap);
                        }
                        BitmapUtil.bitmapRecycle(pageBitmap);
                    } catch (OutOfMemoryError e) {
                        Dlog.e(TAG, e);
                    } finally {
                        try {
                            if (stream != null)
                                stream.close();
                        } catch (IOException e) {
                            Dlog.e(TAG, e);
                        }
                    }

                }

                setPageFileOutput(page + 1);
            }
        });
    }
}
