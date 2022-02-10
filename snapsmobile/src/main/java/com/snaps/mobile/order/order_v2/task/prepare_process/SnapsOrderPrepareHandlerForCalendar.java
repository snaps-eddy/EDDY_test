package com.snaps.mobile.order.order_v2.task.prepare_process;

import com.snaps.common.data.parser.GetSaveXMLHandler;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.mobile.R;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 3. 31..
 */

public class SnapsOrderPrepareHandlerForCalendar extends SnapsOrderPrepareHandlerDefault {
    protected SnapsOrderPrepareHandlerForCalendar(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerForCalendar createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerForCalendar(attribute, snapsOrderActivityBridge);
    }

    @Override
    protected void setTextControlBaseText() throws Exception {
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
                    LineText lineText = new LineText();
                    lineText.text = textControl.text != null ? textControl.text : "";
                    lineText.width = textControl.width;
                    lineText.height = textControl.height;
                    lineText.x = textControl.getX() + "";
                    lineText.y = textControl.y + "";

                    textControl.textList.add(lineText);
                }
            }
        }
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createSaveConditionSuccessAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttribute()
                .setAdditionSubText(GetSaveXMLHandler.getPeriod(getAttribute().getActivity()));
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertNotPrintAttribute()
                .setAdditionSubText(GetSaveXMLHandler.getPeriod(getAttribute().getActivity()));
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createExistEmptyImageControlAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttributeWithTitleResId(R.string.photo_card_save_cart_blank_photo_alert_msg)
                .setAdditionSubText(GetSaveXMLHandler.getPeriod(getAttribute().getActivity()));
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute() {
        return null;
    }
}
