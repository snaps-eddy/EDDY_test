package com.snaps.mobile.order.order_v2.task.prepare_process;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimduckwon on 2017. 11. 28..
 */

public class SnapsOrderPrepareHandlerForCard extends SnapsOrderPrepareBaseHandler {
    private static final String TAG = SnapsOrderPrepareHandlerForCard.class.getSimpleName();

    private final int LINE_TEXT_HEIGHT_10PX = 11;
    private final int LINE_TEXT_HEIGHT_20PX = 23;
    private float lineSpacing = 9;

    protected SnapsOrderPrepareHandlerForCard(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerForCard createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerForCard(attribute, snapsOrderActivityBridge);
    }

//    @Override
//    protected SnapsOrderSaveToBasketAlertAttribute createSaveToBasketAlertAttribute() throws Exception {
//        int completeCard = isCheckEditState();
//        if(completeCard == -1) {
//            return super.createSaveToBasketAlertAttribute();
//        } else {
//            int alertTitleMsgResId = R.string.photo_card_save_cart_blank_photo_alert_msg;
//            return SnapsOrderSaveToBasketAlertAttribute.createPhotoCardSaveToBasketAlertAttributeWithTitleResId(alertTitleMsgResId);
//        }
//    }

    @Override
    public int performInspectOrderOptionAndGetResultCode() throws Exception {
        int resultCode = checkBaseOrderOptionAndGetResultCode();
        if (resultCode != ORDER_PREPARE_INSPECT_RESULT_OK) return resultCode;

        if (!Config.isValidProjCode()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_PROJECT_CODE;
        //else if (!isPassPhotoReplenishmentCheck()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_PHOTO_REPLENISHMENT;
//        else if (!isExistLowResolutionPhoto()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_PRINTABLE_PHOTO_EXIST;

        return resultCode;
    }

    @Override
    public void showSaveToBasketAlert(SnapsOrderSaveToBasketAlertAttribute alertAttribute, DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) throws Exception {
        // 액자인 경우 프로젝트 명을 받지않고 저장을 한다.
        if (Const_PRODUCT.isFrameProduct() || Const_PRODUCT.isPolaroidProduct() || Const_PRODUCT.isWalletProduct() || Const_PRODUCT.isDesignNoteProduct()
                || Const_PRODUCT.isLegacyPhoneCaseProduct() || Const_PRODUCT.isUvPhoneCaseProduct()
                || Const_PRODUCT.isPrintPhoneCaseProduct() || Const_PRODUCT.isMousePadProduct() || Const_PRODUCT.isPhotoMugCupProduct()
                || Const_PRODUCT.isTumblerProduct() || Const_PRODUCT.isPackageProduct() || Const_PRODUCT.isCardProduct()) {
//            if (!Config.isNotCoverPhotoBook())
//                dialogInputNameClickListener.onClick(true);//바로 업로드 하도록
//            else {
            if (!isShowingDiagInput()) {
                if (getAttribute().isEditMode())
                    setDiagInput(DialogInputNameFragment.newInstanceSave(Config.ORDR_STAT_ORDER_CODE, dialogInputNameClickListener));
                else
                    setDiagInput(DialogInputNameFragment.newInstance(Config.ORDR_STAT_ORDER_CODE, dialogInputNameClickListener));

                setSaveToBasketAlertMsg(alertAttribute);

                getDiagInput().show(((FragmentActivity) getAttribute().getActivity()).getSupportFragmentManager(), "dialog");
            }
//            }
        } else {
            if (!isShowingDiagInput()) {
                if (getAttribute().isEditMode())
                    setDiagInput(DialogInputNameFragment.newInstanceSave(Config.ORDR_STAT_ORDER_CODE, dialogInputNameClickListener));
                else
                    setDiagInput(DialogInputNameFragment.newInstance(Config.ORDR_STAT_ORDER_CODE, dialogInputNameClickListener));

                setSaveToBasketAlertMsg(alertAttribute);

                getDiagInput().show(((FragmentActivity) getAttribute().getActivity()).getSupportFragmentManager(), "dialog");
            }
        }
    }

    private void setSaveToBasketAlertMsg(SnapsOrderSaveToBasketAlertAttribute alertAttribute) throws Exception {
        getDiagInput().setAlertAttribute(alertAttribute);
    }

    @Override
    public void showCompleteUploadPopup(DialogConfirmFragment.IDialogConfirmClickListener dialogConfirmClickListener) {
        setDiagConfirm(DialogConfirmFragment.newInstance(DialogConfirmFragment.DIALOG_TYPE_ORDER_COMPLETE, dialogConfirmClickListener));

        try {
            getDiagConfirm().show(((FragmentActivity) getAttribute().getActivity()).getSupportFragmentManager(), "dialog");
        } catch (IllegalStateException e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void setTextControlBaseText() {
        if (getAttribute() == null || getAttribute().getPageList() == null) return;
        ArrayList<SnapsPage> arrPageList = getAttribute().getPageList();
        for (SnapsPage snapsPage : arrPageList) {
            if (snapsPage == null) continue;
            //Texts
            ArrayList<SnapsControl> arrControls = snapsPage.getControlList();
            for (SnapsControl control : arrControls) {
                if (control == null || !(control instanceof SnapsTextControl)) continue;
                SnapsTextControl textControl = (SnapsTextControl) control;
                if (textControl.textList != null && textControl.textList.isEmpty()) {
                    setTextList(textControl);
                }
            }
        }
    }

    private void setTextList(SnapsTextControl textControl) {
        int lineHeight = 0;
        if ("10".equals(textControl.format.fontSize)) {
            lineHeight = LINE_TEXT_HEIGHT_10PX;
        } else {
            lineHeight = LINE_TEXT_HEIGHT_20PX;
        }
        ;
        List<String> textDataList = getTextList(textControl.text, Integer.parseInt(textControl.width), Integer.parseInt(textControl.height));
        float height = (lineHeight / 2) + getCurViewCenterOffsetY(textControl.getIntHeight(), textDataList, lineHeight);
        final int TEXT_CONTROL_Y = Const_PRODUCT.isCardShapeFolder() ? (lineHeight / 2) : (int) (textControl.getIntY() / 2);
        int limit = measuredMaxTextSize(textControl.getIntHeight(), height, textDataList.size(), lineHeight);

        for (int ii = 0; ii < limit; ii++) {

            if (textDataList.size() <= ii) break;
            String text = textDataList.get(ii);
            LineText lineText = new LineText();

            lineText.x = textControl.getX() + "";
//					lineText.y = (int) (height + TEXT_CONTROL_Y) + ""; //FIXME Control의 Y축 만큼 이동시킴..
            lineText.y = textControl.getIntY() + (int) (height + TEXT_CONTROL_Y) + ""; //FIXME Control의 Y축 만큼 이동시킴..
            lineText.height = (lineHeight + lineSpacing) + "";
            lineText.width = textControl.getIntWidth() + "";
            lineText.text = text;
            textControl.textList.add(lineText);
            height += lineHeight + lineSpacing;
        }
    }

    private float getCurViewCenterOffsetY(int height, List<String> list, int lineHeight) {
        float offsetHeight = (height - ((list.size() - 1) * (lineHeight + lineSpacing))) / 2.f;
        if (offsetHeight < 0.f)
            offsetHeight = 0.f;
        return offsetHeight;
    }

    private int measuredMaxTextSize(int heightTotal, float offsetY, int size, int lineHeight) {
        float height = offsetY;
        float lineSpacing = 9;
        for (int ii = 0; ii < size; ii++) {
            // 자신의 영역을 넘어가면 텍스트를 뿌리지 않는다.
            if (heightTotal - (lineHeight) < height) {
                return ii;
            }

            height += lineHeight;

            if (ii > 0)
                height += lineSpacing;
        }

        return size;
    }

    private List<String> getTextList(String msg, int tw, int th) {
        List<String> textList = new ArrayList<String>();
        String[] textArr = msg.split("\n");
        int end = 0;
        for (int i = 0; i < textArr.length; i++) {
            if (textArr[i].length() == 0) {
                textList.add(textArr[i]);
                continue;
            }
            do {
                // 글자가 width 보다 넘어가는지 체크
//								end = mPaint.breakText(textArr[i], true, mAvailableWidth, null); //만약, 문제가 생긴다면 이걸로 적용!
                end = FontUtil.customBreakText(textArr[i], tw > th ? FontUtil.TEXT_TYPE_CARD_TEXT_VERTICAL : FontUtil.TEXT_TYPE_CARD_TEXT);

                if (end > 0) {
                    String s = textArr[i].substring(0, end);
                    textList.add(s);
                    textArr[i] = textArr[i].substring(end);

                }

            } while (end > 0);
        }
        return textList;
    }

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
        return null;
    }
}

