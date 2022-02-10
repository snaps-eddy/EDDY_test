package com.snaps.mobile.order.order_v2.task.prepare_process;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import errorhandle.SnapsAssert;

import static com.snaps.common.utils.constant.Const_PRODUCT.BIG_RECTANGLE_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.EXAM_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.LONG_PHOTO_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.NAME_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.RECTANGLE_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.ROUND_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.SQUARE_STICKER;

/**
 * Created by ysjeong on 2017. 3. 31..
 */

public class SnapsOrderPrepareHandlerForTempStickerKit extends SnapsOrderPrepareBaseHandler {
    private static final String TAG = SnapsOrderPrepareHandlerForTempStickerKit.class.getSimpleName();

    private SnapsOrderPrepareHandlerForTempStickerKit(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerForTempStickerKit createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerForTempStickerKit(attribute, snapsOrderActivityBridge);
    }

    @Override
    public int performInspectOrderOptionAndGetResultCode() throws Exception {
        int resultCode = checkBaseOrderOptionAndGetResultCode();
        if (resultCode != ORDER_PREPARE_INSPECT_RESULT_OK) return resultCode;

        if (!Config.isValidProjCode()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_PROJECT_CODE;
        return resultCode;
    }

    @Override
    public void setOrderBaseInfo(final SnapsOrderResultListener baseInfoListener) throws Exception {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            boolean isSuccess = true;
            @Override
            public void onPre() {
                //본격적으로 장바구니에 저장하기에 앞서 필요한 내용을 셋팅한다.
                try {
                    //추가한 페이지 정보
                    setAddPageInfo();

                    /**
                     * 테마북의 텍스트는 기본적으로 그 화면에서 작성을 하기 때문에 안 들어갈 수가 없지만,
                     * 달력의 경우는 페이저를 안 넘기고 장바구니에 담을 경우, 텍스트가 안 들어갈 수 있기 때문에 강제로 넣어준다.
                     */
                    setImageInfo();

                    //화면이 켜져 있도록
                    setKeepScreenState(true);

                    //재저장이면 m으로 넣어주어야 한다.
                    initProjUType();

                    //업로드 실패한 원본 이미지 리스트 초기화
                    SnapsUploadFailedImageDataCollector.clearHistory(Config.getPROJ_CODE());
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    isSuccess = false;
                    SnapsAssert.assertException(getAttribute().getActivity(), e);
                }
            }
            @Override
            public void onBG() {
                //네트워크 통신이 일어나는 부분도 있기 때문에 UI 쓰레드를 사용하면 안된다.
                try {
                    setLayoutControlCoordinateInfo();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    isSuccess = false;
                    SnapsAssert.assertException(getAttribute().getActivity(), e);
                }
            }

            @Override
            public void onPost() {
                if (isSuccess) {
                    baseInfoListener.onSnapsOrderResultSucceed(null);
                } else {
                    baseInfoListener.onSnapsOrderResultFailed(null, eSnapsOrderType.ORDER_TYPE_PREPARATION);
                }
            }
        });
    }

    /**
     * 스티커킷에 이미지 정보를 넣어주는 작업인데, 곧 스티커킷 상품이 리뉴얼 될 예정이라 굳이 별도로 빼지 않았다
     * 리뉴얼 되고 나면 아래 코드 제거 하자
     */
    private void setImageInfo() throws Exception {
        ArrayList<SnapsPage> pageList = getAttribute().getPageList();
        for (int ii = 0; ii < pageList.size(); ii++) {
            SnapsPage page = pageList.get(ii);
            imageRange(page, ii);
        }
    }

    private final Comparator<SnapsControl> myComparator = new Comparator<SnapsControl>() {
        private final Collator _collator = Collator.getInstance();

        @Override
        public int compare(SnapsControl p, SnapsControl n) {
            return _collator.compare(p.regValue, n.regValue);
        }
    };

    private void imageRange(SnapsPage page, int index) {
        try {
            SnapsLayoutControl layout;
            Collections.sort(page.getLayoutList(), myComparator);

            int sticketCnt = 6;
            if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_2))
                sticketCnt = 2;
            else if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_1))
                sticketCnt = 1;

            int idx = (sticketCnt * (index - 1));
            idx = Math.max(idx, 0);

            ArrayList<MyPhotoSelectImageData> _imageList = getSnapsOrderActivityBridge().getUploadImageList();
            if(_imageList != null) {
                if (idx >= _imageList.size()) {
                    idx = (idx % _imageList.size());
                }

                for (int i = 0; i < page.getLayoutList().size(); i++) {
                    layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                    MyPhotoSelectImageData imgData;

                    if (layout.type.equalsIgnoreCase("browse_file")) {
                        if (idx >= _imageList.size())
                            idx = 0;

                        imgData = _imageList.get(idx);
                        if(imgData != null) {
                            imgData.pageIDX = index;

                            if (!imgData.PATH.equalsIgnoreCase("")) {
                                imgData.cropRatio = layout.getRatio();
                                imgData.IMG_IDX = getImageIDX(index, layout.regValue);

                                layout.imgData = imgData;
                                layout.angle = String.valueOf(imgData.ROTATE_ANGLE);
                                layout.imagePath = imgData.PATH;
                                layout.imageLoadType = imgData.KIND;
                                layout.imgData.mmPageWidth = Integer.parseInt(getSnapsOrderActivityBridge().getTemplate().info.F_PAGE_MM_WIDTH);
                                layout.imgData.pxPageWidth = Integer.parseInt(getSnapsOrderActivityBridge().getTemplate().info.F_PAGE_PIXEL_WIDTH);
                                layout.imgData.controlWidth = layout.width;
                            }
                        }

                        idx++;
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private int getImageIDX(int page, String regValue) {
        return page * 100 + Integer.parseInt(regValue);
    }

    @Override
    public void showSaveToBasketAlert(SnapsOrderSaveToBasketAlertAttribute alertAttribute, DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) throws Exception {
        if (!isShowingDiagInput()) {
            if (getAttribute().isEditMode())
                setDiagInput(DialogInputNameFragment.newInstanceSave(Config.ORDR_STAT_ORDER_CODE, dialogInputNameClickListener));
            else
                setDiagInput(DialogInputNameFragment.newInstance(Config.ORDR_STAT_ORDER_CODE, dialogInputNameClickListener));
            getDiagInput().show(((FragmentActivity) getAttribute().getActivity()).getSupportFragmentManager(), "dialog");
        }
    }

    @Override
    public void showCompleteUploadPopup(DialogConfirmFragment.IDialogConfirmClickListener dialogConfirmClickListener) {
        setDiagConfirm(DialogConfirmFragment.newInstance(DialogConfirmFragment.DIALOG_TYPE_ORDER_COMPLETE, dialogConfirmClickListener));

        try {
            getDiagConfirm().show(((FragmentActivity) getAttribute().getActivity()).getSupportFragmentManager(), "dialog");
        } catch (IllegalStateException e) { Dlog.e(TAG, e); }
    }

    @Override
    protected void setTextControlBaseText() throws Exception {}

    @Override
    protected boolean isLackQuantity() throws Exception {
        return false;
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertNotPrintAttribute();
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttributeWithTitleResId(getStickerLackQuantitySaveAlertMsg());
    }

    private int getStickerLackQuantitySaveAlertMsg() {
        switch (Config.getPROD_CODE()) {
            case ROUND_STICKER:
                return R.string.round_sticker_quantity_shortage;
            case SQUARE_STICKER:
                return  R.string.square_sticker_quantity_shortage;
            case RECTANGLE_STICKER:
                return R.string.rectangle_sticker_quantity_shortage;
            case EXAM_STICKER:
                return R.string.round_sticker_quantity_shortage;
            case NAME_STICKER:
                return R.string.name_sticker_quantity_shortage;
            case BIG_RECTANGLE_STICKER:
                return R.string.round_sticker_quantity_shortage;
            case LONG_PHOTO_STICKER:
                return R.string.long_photo_sticker_quantity_shortage;
            default:
                return 0;
        }
    }
}
