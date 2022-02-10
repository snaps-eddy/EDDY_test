package com.snaps.common.utils.ui;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.log.Dlog;

import java.util.List;

public class ImageEdgeValidation {

    private Bitmap bitmap;
    private List<ImageEdge> edges;
    private String imageType;

    private static final String TAG = ImageEdgeValidation.class.getSimpleName();

    public ImageEdgeValidation(Bitmap bitmap, List<ImageEdge> edges, String imageType) {
        this.bitmap = bitmap;
        this.edges = edges;
        this.imageType = imageType;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isOnlyOneEdge() {
        return edges != null && edges.size() == 1;
    }

    public boolean isJpeg() {
        return "image/jpg".equalsIgnoreCase(imageType) || "image/jpeg".equalsIgnoreCase(imageType);
    }

    public boolean isOverMinimumSize() {

        ImageValidationInfoProvider validator = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();

        Dlog.d("Validator : width " + validator.getUserSelectWidth());
        Dlog.d("Validator : height " + validator.getUserSelectHeight());
        Dlog.d("Validator : getMinimumPx " + validator.getMinimumPx());

        if (!isJpeg() && edges.size() <= 0) {
            return false;
        }

        Rect imageRect = isJpeg() ? new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()) : edges.get(0).getEdgeRect();

        int bWidth = Math.abs(imageRect.width()) + 1;
        int bHeight = Math.abs(imageRect.height()) + 1;

        int vWidth = validator.getUserSelectWidth();
        int vHeight = validator.getUserSelectHeight();

        DynamicProductImageSizeConverter converter = new DynamicProductImageSizeConverter();
        DynamicProductDimensions dimensions = converter.getFitImageDimensions(bWidth, bHeight, vWidth, vHeight);

        if (dimensions.getWidth() == 0 || dimensions.getHeight() == 0) {
            return false;
        }

        int shortSide = Math.min(dimensions.getWidth(), dimensions.getHeight());

        return shortSide >= validator.getMinimumPx();
    }

    public interface ImageValidationInfoProvider {

        int getUserSelectWidth();

        int getUserSelectHeight();

        int getMinimumPx();

        int getThicknessKnifelinePX();
    }
}


