package com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data;

import android.content.Context;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.ui.UIUtil;

public class SmartRecommendBookMainListItemPinchImageInfo {
    private SnapsPageCanvas view;
    private SmartSnapsConstants.ePinchZoomPivotX pinchZoomPivotX = SmartSnapsConstants.ePinchZoomPivotX.CENTER;
    private int itemPosition = 0;

    private SmartRecommendBookMainListItemPinchImageInfo(Builder builder) {
        this.view = builder.view;
        this.pinchZoomPivotX = builder.pinchZoomPivotX;
        this.itemPosition = builder.itemPosition;
    }

    public SmartSnapsConstants.ePinchZoomPivotX getPinchZoomPivotX() {
        return pinchZoomPivotX;
    }

    public SnapsPageCanvas getView() {
        return view;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    public static class Builder {
        private SnapsPageCanvas view;
        private SmartSnapsConstants.ePinchZoomPivotX pinchZoomPivotX = SmartSnapsConstants.ePinchZoomPivotX.CENTER;
        private int itemPosition = 0;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setView(SnapsPageCanvas view) {
            this.view = view;
            return this;
        }

        public Builder setPinchPosition(int itemPosition, int touchOffsetX) {
            if (context == null) return this;
            this.itemPosition = itemPosition;
            if (itemPosition == 0) { //커버는 중앙을 키운다.
                pinchZoomPivotX = SmartSnapsConstants.ePinchZoomPivotX.CENTER;
            } else {
                int screenWidth = UIUtil.getScreenWidth(context);
                if (screenWidth *.4f > touchOffsetX) {
                    pinchZoomPivotX = SmartSnapsConstants.ePinchZoomPivotX.LEFT;
                } else if (screenWidth *.6f > touchOffsetX) {
                    pinchZoomPivotX = SmartSnapsConstants.ePinchZoomPivotX.CENTER;
                } else {
                    pinchZoomPivotX = SmartSnapsConstants.ePinchZoomPivotX.RIGHT;
                }
            }

            return this;
        }

        public SmartRecommendBookMainListItemPinchImageInfo create() {
            return new SmartRecommendBookMainListItemPinchImageInfo(this);
        }
    }
}
