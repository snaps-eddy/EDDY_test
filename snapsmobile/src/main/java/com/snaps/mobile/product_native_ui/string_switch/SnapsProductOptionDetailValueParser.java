package com.snaps.mobile.product_native_ui.string_switch;

import com.google.gson.internal.LinkedTreeMap;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsPerform;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsProductOptionCellConstants;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionDetailValue;

/**
 * Created by ysjeong on 2016. 11. 21..
 */

public class SnapsProductOptionDetailValueParser<T extends SnapsProductOptionDetailValue> extends SnapsNativeUIStrSwitch {

    public SnapsProductOptionDetailValueParser(T t, LinkedTreeMap treeMap) {
        super(t, treeMap);
    }

    @Override
    public void createCase() {
        addCase(ISnapsProductOptionCellConstants.KEY_PRODUCT_SIZE, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setProductSize((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_USE_IMAGE_CNT, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setUseImageCnt((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_FRAME_SIZE, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setFrameSize((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_ENABLEPAGE, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setEnablePage((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_PAGE_PRICE, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setPage_price((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_PHOTO_SIZE, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setPhotoSize((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_PRODUCT_MATERIAL, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setProductMaterial((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_PRODUCT_VOLUME, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setProductVolumn((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_THUMBNAIL, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setThumbnail((Integer) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_PROD_FORM, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setProdForm((String) result);
            }
        });
        addCase(ISnapsProductOptionCellConstants.KEY_LEATHER_COVER, new ISnapsPerform() {
            @Override
            public void perform(Object result) {
                ((SnapsProductOptionDetailValue) getTargetClass()).setLeatherCover((String) result);
            }
        });
    }
}
