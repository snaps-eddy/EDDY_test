package com.snaps.mobile.activity.google_style_image_selector.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoData;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 1. 9..
 */

public class PhonePhotosLoader extends Thread {
    private static final String TAG = PhonePhotosLoader.class.getSimpleName();

    public interface IPhonePhotoLoadListener {
        void onFinishPhonePhotoLoad();
    }

    private Activity activity;

    private boolean isSuspend = false;

    private IPhonePhotoLoadListener loadListener = null;

    public void setLoadListener(IPhonePhotoLoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public void setSuspend(boolean suspend) {
        isSuspend = suspend;
    }

    public PhonePhotosLoader(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        super.run();

        try {
            if (isSuspend || isInterrupted() || activity == null) return;

            //권한이 없는 상태..
            if (Build.VERSION.SDK_INT > 22) {
                if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }

            ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
            if (imageSelectManager == null || imageSelectManager.isCreatedPhonePhotoDataList()) return;

            imageSelectManager.startSyncCreatingPhotoDataList();

            ImageSelectPhonePhotoData phonePhotoUriData = new ImageSelectPhonePhotoData(activity);

            //앨범 리스트 생성
            phonePhotoUriData.createAlbumDatas();

            //생성된 앨범 리스트로 사진 상세 리스트도 미리 쿼리하여 구성함.
            ArrayList<IAlbumData> cursorList = phonePhotoUriData.getArrCursor();
            if (cursorList != null) {
                long t1 = System.currentTimeMillis();
                phonePhotoUriData.createAllPhotoDataOfCellPhone2(cursorList, activity);
                long t2 = System.currentTimeMillis();
                Dlog.d("createAllPhotoDataOfCellPhone2() time:" + (t2 - t1));
            } else {
                //TODO  에러..
                imageSelectManager.finishCreatingPhotoDataList();
                return;
            }

            //모든 사진을 담고 있는 앨범도 생성한다.
            phonePhotoUriData.createAllPhotoCotainedAlbum();

            imageSelectManager.setImageSelectPhonePhotoData(phonePhotoUriData);

            int heifImageCount = phonePhotoUriData.getHeifImageCount();
            imageSelectManager.setHeifImageCount(heifImageCount);

            imageSelectManager.finishCreatingPhotoDataList();

        } catch (Exception e) {
            Dlog.e(TAG, e);

            ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
            if (imageSelectManager != null)
                imageSelectManager.finishCreatingPhotoDataList();
        } finally {
            if (loadListener != null) {
                if (activity != null && !activity.isFinishing()) {
                    activity.runOnUiThread(() -> {
                        Dlog.d("Finish Load Photos ! ");
                        if (loadListener != null) {
                            loadListener.onFinishPhonePhotoLoad();
                        }
                    });
                }
            }
        }
    }
}
