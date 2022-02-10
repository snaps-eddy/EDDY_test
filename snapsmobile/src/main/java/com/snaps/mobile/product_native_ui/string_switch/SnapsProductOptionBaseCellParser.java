package com.snaps.mobile.product_native_ui.string_switch;

import com.google.gson.internal.LinkedTreeMap;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsPerform;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsProductOptionCellConstants;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionBaseCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCommonValue;

/**
 * Created by ysjeong on 2016. 11. 21..
 */

public class SnapsProductOptionBaseCellParser<T extends SnapsProductOptionBaseCell> extends SnapsNativeUIStrSwitch {

    private SnapsProductOptionCommonValue commonValue = null;

    public SnapsProductOptionBaseCellParser(T t, LinkedTreeMap treeMap) {
        super(t, treeMap);
    }

    @Override
    public void createCase() {
        addCase(ISnapsProductOptionCellConstants.KEY_VALUE, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionBaseCell) getTargetClass()).performKeyValue(result, getCommonValue());
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_NAME, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionBaseCell) getTargetClass()).performKeyName(result, getCommonValue());
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_MAX, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionBaseCell) getTargetClass()).performKeyMax(result, getCommonValue());
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_MIN, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionBaseCell) getTargetClass()).performKeyMin(result, getCommonValue());
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_PROD_FORM, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionBaseCell) getTargetClass()).performKeyProdForm(result, getCommonValue());
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_CMD, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionBaseCell) getTargetClass()).performKeyCmd(result, getCommonValue());
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_DETAIL, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionBaseCell) getTargetClass()).performKeyDetail(result, getCommonValue());
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_PRICE, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionBaseCell) getTargetClass()).performKeyPrice(result, getCommonValue());
            }
        });
    }

    public SnapsProductOptionCommonValue getCommonValue() {
        return commonValue;
    }

    public void setCommonValue(SnapsProductOptionCommonValue commonValue) {
        this.commonValue = commonValue;
    }
}
