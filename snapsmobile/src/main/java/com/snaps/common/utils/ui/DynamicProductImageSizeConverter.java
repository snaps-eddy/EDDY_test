package com.snaps.common.utils.ui;

import com.snaps.common.utils.log.Dlog;

public class DynamicProductImageSizeConverter {

    private static final String TAG = DynamicProductImageSizeConverter.class.getSimpleName();

    public DynamicProductDimensions getFitImageDimensions(float bWidth, float bHeight, int vWidth, int vHeight) {

        Dlog.d("User Selected Width : " + vWidth);
        Dlog.d("User Selected Height : " + vHeight);

        Dlog.d("Image edge box Width : " + bWidth);
        Dlog.d("Image edge box Height : " + bHeight);

        int resultWidth, resultHeight;
        float postScaleFactor;

        if (bWidth >= bHeight) {
            float factor = vWidth / bWidth;
            resultWidth = vWidth;
            resultHeight = Math.round(bHeight * factor);
            postScaleFactor = (float) resultHeight / bHeight;

            if (resultHeight > vHeight) {
                factor = vHeight / bHeight;
                resultWidth = Math.round(bWidth * factor);
                resultHeight = vHeight;
                postScaleFactor = (float) resultWidth / bWidth;
            }

        } else {
            float factor = vHeight / bHeight;
            resultWidth = Math.round(bWidth * factor);
            resultHeight = vHeight;
            postScaleFactor = (float) resultWidth / bWidth;

            if (resultWidth > vWidth) {
                factor = vWidth / bWidth;
                resultWidth = vWidth;
                resultHeight = Math.round(bHeight * factor);
                postScaleFactor = (float) resultHeight / bHeight;
            }
        }

        return new DynamicProductDimensions(resultWidth, resultHeight, postScaleFactor);
    }
}
