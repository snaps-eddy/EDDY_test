package com.snaps.mobile.activity.common.products.base;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.imageloader.filters.ImageFilters;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;

import static com.snaps.common.data.img.MyPhotoSelectImageData.INVALID_ROTATE_ANGLE;

public class PhoneCaseEditorHandler extends SnapsProductBaseEditorHandler {

    private static final String TAG = PhoneCaseEditorHandler.class.getSimpleName();

    public static SnapsProductBaseEditorHandler createHandlerWithBridge(SnapsProductBaseEditor uiHandleBridge) {
        PhoneCaseEditorHandler baseHandler = new PhoneCaseEditorHandler();
        baseHandler.productEditorBase = uiHandleBridge;
        baseHandler.snapsProductEditorSmartSnapsHandler = SnapsProductEditorSmartSnapsHandler.createInstanceWithBaseHandler(baseHandler);
        return baseHandler;
    }

    // @Marko SnapsProductBaseEditorHandler 에 있는 코드 거의 그냥 긁어옴.
    @Override
    public void changePage(int index, SnapsPage newPage) throws Exception {
        if (newPage == null) {
            return;
        }

        int productWidth = getSnapsTemplate().getPixelWidth();
        int productHeight = getSnapsTemplate().getPixelHeight();
        int newPageWidth = newPage.getWidth();
        int newPageHeight = newPage.getHeight();

        SnapsPage oldPage = getPageList().get(index);
        newPage.info = oldPage.info;
        newPage.setPageID(oldPage.getPageID());
        newPage.setQuantity(oldPage.getQuantity());
        newPage.setWidth(String.valueOf(productWidth));
        newPage.setHeight(String.valueOf(productHeight));

        Fryingpan fryingpan = new Fryingpan();
        fryingpan.turnOnFire(productWidth, productHeight, newPageWidth, newPageHeight);

        for (SnapsControl control : newPage.getLayoutList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
            fryingpan.flip((SnapsLayoutControl) control);
        }

        for (SnapsControl control : newPage.getBgList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
            fryingpan.flip((SnapsBgControl) control);
        }

        for (SnapsControl control : newPage.getControlList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);

            if (control instanceof SnapsTextControl) {
                fryingpan.flip((SnapsTextControl) control);

            } else if (control instanceof SnapsClipartControl) {
                fryingpan.flip((SnapsClipartControl) control);
            }
        }

        for (SnapsControl control : newPage.getFormList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
        }

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        final List<MyPhotoSelectImageData> smartSnapsImageList = smartSnapsManager.createSmartSnapsImageListWithPageIdx(index);

        applyUserAppliedImageData(oldPage, newPage, index, smartSnapsImageList);

        // 페이지 교체...
        getPageList().remove(index);
        getPageList().add(index, newPage);

        if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
            SmartSnapsManager.setSmartAreaSearching(true);
        }

        updateEditorView(index, smartSnapsImageList);
    }

    private void applyUserAppliedImageData(SnapsPage oldPage, SnapsPage newPage, int index, List<MyPhotoSelectImageData> smartSnapsImageList) throws Exception {

        int imgCnt = newPage.getLayoutList().size();
        int idx = 0;

        for (SnapsControl control : oldPage.getLayoutList()) {
            if (control instanceof SnapsLayoutControl) {
                SnapsLayoutControl oldControl = (SnapsLayoutControl) control;
                if (oldControl.imgData != null && oldControl.type.equalsIgnoreCase("browse_file")) {
                    if (idx < imgCnt) {

                        SnapsLayoutControl newControl = ((SnapsLayoutControl) newPage.getLayoutList().get(idx));
                        newControl.setControlId(-1);

                        if (oldControl.imgData.ORIGINAL_ROTATE_ANGLE != INVALID_ROTATE_ANGLE) {
                            oldControl.imgData.ROTATE_ANGLE = oldControl.imgData.ORIGINAL_ROTATE_ANGLE;
                        }

                        //효과 필터가 적용 된 사진은 회전 정보가 반영 되어 있기 때문에 원래 각도로 복구해서 로딩한다.
                        if (oldControl.imgData.isApplyEffect
                                && (oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE != oldControl.imgData.ROTATE_ANGLE_THUMB || oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE == INVALID_ROTATE_ANGLE)) {
                            try {
                                if (!ImageFilters.updateEffectImageToOrgAngle(getActivity(), oldControl.imgData)) {
                                    oldControl.imgData.isApplyEffect = false;
                                }
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                                oldControl.imgData.isApplyEffect = false;
                            }
                        }

                        if (oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE != INVALID_ROTATE_ANGLE) //만약 ratio 오류같은 게 발생한다면 이부분을 의심해보자
                            oldControl.imgData.ROTATE_ANGLE_THUMB = oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE;

                        newControl.imgData = oldControl.imgData;

                        newControl.imgData.FREE_ANGLE = 0;
                        newControl.imgData.RESTORE_ANGLE = SnapsImageDownloader.INVALID_ANGLE;
                        newControl.imgData.isAdjustableCropMode = false;
                        newControl.imgData.ADJ_CROP_INFO = new AdjustableCropInfo();

                        newControl.imgData.IMG_IDX = Integer.parseInt(0 + "" + newPage.getLayoutList().get(idx).regValue);
                        newControl.freeAngle = 0;// oldControl.imgData.FREE_ANGLE;
                        newControl.angle = String.valueOf(oldControl.imgData.ROTATE_ANGLE);
                        newControl.imagePath = oldControl.imgData.PATH;
                        newControl.imageLoadType = oldControl.imgData.KIND;
                        newControl.imgData.cropRatio = newControl.getRatio();
                        newControl.imgData.increaseUploadPriority();

                        // 인쇄가능 여부..
                        try {
                            getEditorBase().setPhotoResolutionEnableWithLayoutControl(newControl);
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }

                        if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                            MyPhotoSelectImageData imageData = newControl.imgData;
                            if (imageData != null && imageData.isSmartSnapsSupport()) {
                                SmartSnapsUtil.setSmartImgDataStateReadyOnChangeLayout(imageData, index);
                                if (smartSnapsImageList != null)
                                    smartSnapsImageList.add(imageData);
                            }
                        }
                    }

                    SnapsOrderManager.removeBackgroundUploadOrgImageData(oldControl.imgData);
                    // 커버 이미지 삭제..
                    oldControl.imgData = null;
                    idx++;
                }
            }
        }
    }

    private void updateEditorView(int index, List<MyPhotoSelectImageData> smartSnapsImageList) {
        Single.just(true)
                .delay(50, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map(dummy -> {
                    getEditorBase().refreshList(index, index);
                    dismissPageProgress();
                    return dummy;
                })
                .delay(800, TimeUnit.MILLISECONDS)
                .map(dummy -> {
                    if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                        SmartSnapsUtil.refreshSmartSnapsImgInfoOnNewLayoutWithImgList(getActivity(), getSnapsTemplate(), smartSnapsImageList, index);
                        SmartSnapsManager.startSmartSnapsAutoFitImage(getEditorBase().getDefaultSmartSnapsAnimationListener(), SmartSnapsConstants.eSmartSnapsProgressType.CHANGE_DESIGN, index);
                    }
                    return dummy;
                })
                .subscribe(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        uploadThumbImgOnBackgroundAfterSuspendCurrentExecutor(null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Dlog.e(TAG, e);
                    }
                });
    }
}
