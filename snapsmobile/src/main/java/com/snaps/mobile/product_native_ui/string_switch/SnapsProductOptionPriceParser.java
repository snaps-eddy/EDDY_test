package com.snaps.mobile.product_native_ui.string_switch;

import com.google.gson.internal.LinkedTreeMap;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsPerform;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsProductOptionCellConstants;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionPrice;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionPriceValue;

/**
 * Created by ysjeong on 2016. 11. 21..
 */

public class SnapsProductOptionPriceParser<T extends SnapsProductOptionPrice> extends SnapsNativeUIStrSwitch {

    public SnapsProductOptionPriceParser(T t, LinkedTreeMap treeMap) {
        super(t, treeMap);
    }

    @Override
    public void createCase() {
        addCase(ISnapsProductOptionCellConstants.KEY_NAME, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionPrice) getTargetClass()).setName((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_CELL_TYPE, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionPrice) getTargetClass()).setCellType((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_ATTRIBUTE, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionPrice) getTargetClass()).setAttribute((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_VALUE, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                LinkedTreeMap child = (LinkedTreeMap) result;
                SnapsProductOptionPriceValue value = new SnapsProductOptionPriceValue();
                for (Object cKey : child.keySet()) {
                    if (cKey.equals(ISnapsProductOptionCellConstants.KEY_SALE_PERCENT)) {
                        value.setSalePerscent((String) child.get(cKey));
                    } else if (cKey.equals(ISnapsProductOptionCellConstants.KEY_DISCOUNT_PRICE)) {
                        value.setDiscountPrice((String) child.get(cKey));
                    } else if (cKey.equals(ISnapsProductOptionCellConstants.KEY_PRICE)) {
                        value.setPrice((String) child.get(cKey));
                    }
                }
                ((SnapsProductOptionPrice) getTargetClass()).setValues(value);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_LINK, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionPrice) getTargetClass()).setLink((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_LINK_TEXT, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionPrice) getTargetClass()).setLinkText((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_INFO_TEXT, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionPrice) getTargetClass()).setInfoText((String) result);
            }
        });
    }
}
