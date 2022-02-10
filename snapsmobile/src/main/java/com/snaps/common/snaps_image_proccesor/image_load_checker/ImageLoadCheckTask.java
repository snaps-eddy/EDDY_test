package com.snaps.common.snaps_image_proccesor.image_load_checker;

import com.snaps.common.snaps_image_proccesor.image_load_checker.interfaces.IImageLoadCheckListener;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;

/**
 * Created by ysjeong on 16. 5. 31..
 */
public class ImageLoadCheckTask extends Thread {
    private static final String TAG = ImageLoadCheckTask.class.getSimpleName();

    private static final int WAIT_MILLISEC = 200;
    private static final int ONE_MILLISEC = 1000;
    private static final int MAX_WAIT_MILLISEC = 60000 * (ONE_MILLISEC / WAIT_MILLISEC); //1개의 페이지가 1분동안 안 열린다면, 그냥 진행 시킨다.

    private static final long PHOTO_LOAD_ADDITORY_DELAY_PHOTO_CARD  = 3000;
    private static final long PHOTO_LOAD_ADDITORY_DELAY_DEFAULT     = 2000;
    private static final long PHOTO_LOAD_ADDITORY_DELAY_IDENTIFY_PHOTO   = 3000;

    private IImageLoadCheckListener listener = null;
    private long loadDelay = 0;
    private boolean isStop = false;
    private int imgCount = 0;
    private int snapsPageIndex = 0;
    private boolean isMakeThumbnail = false;

    public ImageLoadCheckTask(IImageLoadCheckListener l) {
        this.listener = l;
        setIsStop(false);
        setImgCount(0);

        setDaemon(true);

        setAdditoryLoadDelay();
    }

    private void setAdditoryLoadDelay() {
        if (Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isPhotoCardProduct()) {
            setLoadDelay(PHOTO_LOAD_ADDITORY_DELAY_PHOTO_CARD);
        } else if (Config.isIdentifyPhotoPrint()) {  //증명사진 사진은 한 페이지에 사진이 많다보니, 대표 썸네일 딸 때 사진이 전부 로딩이 안된채, 완료 처리되는 경우가 많아서 좀 더 길게 기다려 준다
            setLoadDelay(PHOTO_LOAD_ADDITORY_DELAY_IDENTIFY_PHOTO);
        } else {
            setLoadDelay(PHOTO_LOAD_ADDITORY_DELAY_DEFAULT);
        }
    }

    public boolean isMakeThumbnail() {
        return isMakeThumbnail;
    }

    public void setMakeThumbnail(boolean makeThumbnail) {
        isMakeThumbnail = makeThumbnail;
    }

    public long getLoadDelay() {
        return loadDelay;
    }

    public void setLoadDelay(long loadDelay) {
        this.loadDelay = loadDelay;
    }

    public boolean isNeedDelay() {
        return isMakeThumbnail() && getLoadDelay() > 0 && getSnapsPageIndex() == 0; //메인 썸네일만 따니까
    }

    public int getSnapsPageIndex() {
        return snapsPageIndex;
    }

    public void setSnapsPageIndex(int snapsPageIndex) {
        this.snapsPageIndex = snapsPageIndex;
    }

    public void releaseInstance() {
        listener = null;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setIsStop(boolean isStop) {
        this.isStop = isStop;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public void addImageLoadCheckCount() {
        this.imgCount++;
    }

    public void subImageLoadCheckCount() {
        this.imgCount--;
    }

    @Override
    public void run() {
        super.run();

        //이미지 로딩 체크
        int waitCnt = 0;
        while (getImgCount() > 0 && !isStop()) {
            try {
                Thread.sleep(WAIT_MILLISEC);
            } catch (InterruptedException e) {
                Dlog.e(TAG, e);
            }
            if (++waitCnt * WAIT_MILLISEC > MAX_WAIT_MILLISEC) break;
        }

        if (isNeedDelay()) {
            try {
                Thread.sleep(getLoadDelay());
            } catch (InterruptedException e) {
                Dlog.e(TAG, e);
            }
        }

        if (isStop()) return;

        if (listener != null)
            listener.onFinishImageLoad();
    }
}
