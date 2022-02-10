package com.snaps.common.snaps_image_proccesor.image_coordinate_processor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.snaps_image_proccesor.image_coordinate_processor.interfaces.IImageCoordinateCalculateListener;
import com.snaps.common.snaps_image_proccesor.image_coordinate_processor.recoder.ImageCoordinateInfo;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 5. 31..
 *
 * XMl에 작성하기 위한 ImageLayoutControl 들의 좌표를 기록한다.
 */
public class ImageCoordinateCalculator extends ThreadPoolExecutor {
    private static final String TAG = ImageCoordinateCalculator.class.getSimpleName();
    private static final int DEFAULT_CORE_POOL_SIZE = 1; //1개의 쓰레드 사용
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 3; //최대 3개까지
    private static final long DEFAULT_KEEP_ALIVE_TIME = 60; //60초까지 대기
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    private static final  BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(); //Queue pool을 사용함.

    private static final  ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    };

    private IImageCoordinateCalculateListener listener = null;

    private boolean isStop = false;

    public ImageCoordinateCalculator(IImageCoordinateCalculateListener l) {
        super(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXIMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_TIME_UNIT, sPoolWorkQueue, sThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

        this.listener = l;

        if (listener != null)
            listener.onPre();
    }

    public ImageCoordinateCalculator() {
        super(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXIMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_TIME_UNIT, sPoolWorkQueue, sThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * layoutControl 에 등록된 모든 이미지의 좌표를 계산한다.(xml에 들어갈 좌표)
     * @param context
     * @param layoutControl
     */
    public static void setLayoutControlCoordinateInfo(Context context, SnapsLayoutControl layoutControl) {
        if (context == null || layoutControl == null || layoutControl.imgData == null) return;

        String imagePath = ImageUtil.getImagePath(context, layoutControl.imgData);

        int angle = 0;

        //효과 적용 된 사진은 원본사진의 exif로 Width, Height 를 계산해야한다.(어차피 렌더에서는 원본사진의 비율로 판단 함.)
        if (layoutControl.imgData.isApplyEffect) {
            imagePath = layoutControl.imgData.PATH != null ? layoutControl.imgData.PATH : layoutControl.imgData.ORIGINAL_PATH;
        }

        if (imagePath != null && imagePath.startsWith("http"))
            angle = layoutControl.imgData.ROTATE_ANGLE;
        else
            angle = layoutControl.imgData.ROTATE_ANGLE_THUMB;

        int imageWidth = 0;
        int imageHeight= 0;
        try {
            imageWidth = (int) Float.parseFloat(layoutControl.imgData.F_IMG_WIDTH);
            imageHeight = (int) Float.parseFloat(layoutControl.imgData.F_IMG_HEIGHT);
        } catch (NumberFormatException e) { Dlog.e(TAG, e); }

        int loadType = getImgaeLoadType(layoutControl);
        setLayoutControlCoordinateInfo(loadType, layoutControl, imageWidth, imageHeight, imagePath, angle);
    }

    /**
     * 이미지의 가로, 세로 값을 기준으로 XML에 들어갈 좌표를 계산한다.
     */
    public static void setLayoutControlCoordinateInfo(int loadType,
                                                      SnapsLayoutControl layout,
                                                      float imageWidth,
                                                      float imageHeight,
                                                      String uri,
                                                      int rotate) {
        if (layout == null) return;

        if (imageWidth == 0 || imageHeight == 0) {
            Dlog.d("setLayoutControlCoordinateInfo() uri:" + uri);
            Rect rcImageDimens = getImageDimens(uri);
            if (rcImageDimens != null) {
                imageWidth = rcImageDimens.width();
                imageHeight = rcImageDimens.height();

            }
            Dlog.d("setLayoutControlCoordinateInfo() image w:" + imageWidth + ", h:" + imageHeight);
        }

        //이미지의 F_IMAGE_WIDTH, HEIGHT 값은 exif의 orientaion value가 적용되지 않았기 때문에 90도와 270도는 swap해 주어야 한다.
        int totalRotate = rotate == -1 ? 0 : rotate;

        if (uri != null && !uri.startsWith("http")) {
            String imgUrl = uri.replace("file://", "");

            int ro = CropUtil.getExifOrientation(imgUrl);
            totalRotate = (ro + totalRotate) % 360;
        }

        if (totalRotate == 90 || totalRotate == 270) {
            float tempWidth = imageWidth;
            imageWidth = imageHeight;
            imageHeight = tempWidth;
        }

        String[] arImageRect = BitmapUtil.getImagePosition(loadType, layout, imageWidth, imageHeight);

        if (arImageRect != null && arImageRect.length >= 4) {
            layout.img_x = arImageRect[0];
            layout.img_y = arImageRect[1];
            layout.img_width = arImageRect[2];
            layout.img_height = arImageRect[3];

            checkLayoutControlRatio(loadType, layout, uri, rotate);
        }

        if (layout.imgData != null) {
            layout.freeAngle = layout.imgData.FREE_ANGLE;
            layout.angle = String.valueOf(layout.imgData.ROTATE_ANGLE);
        }
        else
            layout.freeAngle = 0;
    }

    /**
     * 이미지 비율 체크
     */
    public static void checkLayoutControlRatio(int loadType,
                                               SnapsLayoutControl layout,
                                               String uri,
                                               int rotate) {
        // 이상한 비율을 보정한다.
        try {
            float rectW = Float.parseFloat(layout.img_width);
            float rectH = Float.parseFloat(layout.img_height);
            boolean isWrongRatio = false;
            MyPhotoSelectImageData imgData = layout.imgData;
            if (imgData != null) {
                // 스냅스 스티커의 경우 F_IMG_WIDTH, F_IMG_HEIGHT값이 ""로 저장되기 때문에 0으로 변경.
                if (layout.imgData.F_IMG_WIDTH == null || layout.imgData.F_IMG_WIDTH.length() < 1)
                    layout.imgData.F_IMG_WIDTH = "0";
                if (layout.imgData.F_IMG_HEIGHT == null || layout.imgData.F_IMG_HEIGHT.length() < 1)
                    layout.imgData.F_IMG_HEIGHT = "0";

                float imgW = Float.parseFloat(imgData.F_IMG_WIDTH);
                float imgH = Float.parseFloat(imgData.F_IMG_HEIGHT);

                isWrongRatio = (imgW > imgH && rectW < rectH) || (imgW < imgH && rectW > rectH);
                if (isWrongRatio) {
                    Dlog.d("checkLayoutControlRatio() isWrongRatio imgData.PATH:" + imgData.PATH);
                }
            }

            if (rectW <= 0 || rectH <= 0 || isWrongRatio) {
                BitmapUtil.setImageDimensionInfo(layout, loadType, uri, rotate);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static int getImgaeLoadType(SnapsLayoutControl layout) {
        if (layout == null) return Const_VALUES.SELECT_EMPTY;

        if (layout.type.equals("browse_file")) {
            if (!layout.mask.isEmpty()) {
                return Const_VALUES.SELECT_KAKAO;
            }

            if (layout.imgData != null) {
                return layout.imgData.KIND;
            } else if (!layout.imagePath.equals("")) {
                return layout.imageLoadType;
            }
        } else if (layout.type.equals("webitem")) {
            if ((layout.regName.equals("like") || layout.regName.equals("more"))) {
                if (!layout.resourceURL.equals("")) {
                    String url = SnapsAPI.DOMAIN(false) + layout.resourceURL;
                    if (!url.equalsIgnoreCase(""))
                        return layout.imageLoadType;
                }
            } else {
                String url = "";
                if (!layout.srcTarget.equalsIgnoreCase("")) {
                    url = SnapsAPI.GET_API_RESOURCE_IMAGE() + "&rname=" + layout.srcTarget + "&rCode=" + layout.srcTarget;
                }
                if (!url.equalsIgnoreCase("") && !"".equals(layout.angle))
                    return layout.imageLoadType;
                else if (!url.equalsIgnoreCase("")) {
                    return layout.imageLoadType;
                }
            }
        }

        if (layout.border != null && !layout.border.equalsIgnoreCase("") && !layout.border.equals("false")) {
            if (layout.angle.isEmpty())
                layout.angle = "0";
            return layout.imageLoadType;
        }

        return layout.imgData != null ? layout.imgData.KIND : layout.imageLoadType;
    }

    public void start(ImageCoordinateInfo coordinateInfo) {
        if (isNeedNetworkConnection(coordinateInfo)) {
            this.execute(new CoordRunnable(coordinateInfo));
        } else {
            calculateCoordinate(coordinateInfo);
        }
    }

    public IImageCoordinateCalculateListener getListener() {
        return listener;
    }

    public void setListener(IImageCoordinateCalculateListener listener) {
        this.listener = listener;
    }

    public void stop() {
        setIsStop(true);

        try {
            this.shutdownNow();
        } catch (SecurityException e) { Dlog.e(TAG, e); }
    }

    public boolean isStop() {
        return isStop;
    }

    public void setIsStop(boolean isStop) {
        this.isStop = isStop;
    }

    /**
     * 이미지의 크기를 가져온다.
     */
    private static Rect getImageDimens(String uri) {
        if (uri == null || uri.length() < 1)  return null;

        if (uri.startsWith("http")) {
            return HttpUtil.getNetworkImageRect(uri);
        } else {
            String filePrefix = "file://";
            if (uri.startsWith(filePrefix))
                uri = uri.substring(filePrefix.length());
            return BitmapUtil.getLocalImageRect(uri);
        }
    }

    /**
     * 이미지의 크기를 가져오기를 실패 했는데, 네트워크 이미지 인지 체크
     */
    private boolean isNeedNetworkConnection(ImageCoordinateInfo coordinateInfo) {
        if (coordinateInfo == null) return false;

        boolean isNetworkUrl = coordinateInfo.getUrl() != null && coordinateInfo.getUrl().startsWith("http");
        boolean isUnknownDimens = false;

        Bitmap loadedImage = coordinateInfo.getLoadedImage();
        if (loadedImage != null && !loadedImage.isRecycled()) {
            View view = coordinateInfo.getView();
            SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControlFromView(view);
            if (snapsControl != null && snapsControl instanceof SnapsLayoutControl) {
                isUnknownDimens = coordinateInfo.getLoadedImage().getWidth() < 1 || coordinateInfo.getLoadedImage().getHeight() < 1;
            }
        } else
            isUnknownDimens = true;

        return isNetworkUrl && isUnknownDimens;
    }

    /**
     * //로딩된 bitmap 크기를 기준으로 좌표를 맞춘다.
     * >> 비트맵의 크기로 할 필요가 없어 보여서 수정 함..
     */
    private void calculateCoordinate(ImageCoordinateInfo coordinateInfo) {
        if (coordinateInfo == null) return;

        try {
            View view = coordinateInfo.getView();
            SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControlFromView(view);
            if (snapsControl != null && snapsControl instanceof SnapsLayoutControl) {
                SnapsLayoutControl layout = (SnapsLayoutControl) snapsControl;

                String uri = coordinateInfo.getUrl();

                float imageWidth = 0.f;
                float imageHeight = 0.f;
                boolean isMeasuaredDimension = false;

                if (layout.imgData != null) {
                    try {
                        if (layout.imgData.F_IMG_WIDTH != null && layout.imgData.F_IMG_WIDTH.length() > 0)
                            imageWidth = Float.parseFloat(layout.imgData.F_IMG_WIDTH);
                        if (layout.imgData.F_IMG_HEIGHT != null && layout.imgData.F_IMG_HEIGHT.length() > 0)
                            imageHeight = Float.parseFloat(layout.imgData.F_IMG_HEIGHT);
                        isMeasuaredDimension = imageWidth > 0 && imageHeight > 0;
                    } catch (Exception e) { Dlog.e(TAG, e); }
                }

                /**
                 * SNS Book 종류는 URL에서 크기 정보를 따오는 로직 때문에 만약,  URL 안에 크기 정보가 없다면
                 * 640, 640으로 셋팅되고 있다. 그래서 로딩된 비트맵 크기를 기준으로 판단한다.
                 */
                if (!isMeasuaredDimension || Const_PRODUCT.isSNSBook()) {
                    Bitmap loadedImage = coordinateInfo.getLoadedImage();
                    if (loadedImage != null && !loadedImage.isRecycled()) {
                        imageWidth = loadedImage.getWidth();
                        imageHeight = loadedImage.getHeight();
                    }
                }

                //효과 적용 된 사진은 원본사진의 exif로 Width, Height 를 계산해야한다.(어차피 렌더에서는 원본사진의 비율로 판단 함.)
                if (layout.imgData != null && layout.imgData.isApplyEffect) {
                    uri = new File( layout.imgData.PATH ).exists() ? layout.imgData.PATH : SnapsAPI.DOMAIN() + layout.imgData.ORIGINAL_PATH;
                    coordinateInfo.setRotate(layout.imgData.ROTATE_ANGLE_THUMB);
                }

                setLayoutControlCoordinateInfo(coordinateInfo.getLoadType(),
                        layout, imageWidth, imageHeight, uri, coordinateInfo.getRotate());
            }
        } catch (Exception e) { Dlog.e(TAG, e); }

        coordinateInfo.releaseInstance();
        coordinateInfo = null;

        if (listener != null)
            listener.onPost();
    }

    /**
     * Network 통신이 필요할 경우, workThread로 돌리기 위해 Runnable 객체 생성
     */
    public class CoordRunnable implements Runnable {
        ImageCoordinateInfo coordinateInfo = null;

        public CoordRunnable(ImageCoordinateInfo coordinateInfo) {
            this.coordinateInfo = coordinateInfo;
        }

        @Override
        public void run() {
            if (isStop() || coordinateInfo == null) return;

            calculateCoordinate(coordinateInfo);
        }
    }
}
