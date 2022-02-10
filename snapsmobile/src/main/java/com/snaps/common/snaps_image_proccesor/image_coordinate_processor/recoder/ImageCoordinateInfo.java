package com.snaps.common.snaps_image_proccesor.image_coordinate_processor.recoder;

import android.graphics.Bitmap;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by ysjeong on 16. 6. 1..
 */
public class ImageCoordinateInfo {
    private WeakReference<Bitmap> loadedImageReference = null;
    private WeakReference<View> viewReference = null;

    private String url = "";
    private int rotate = 0;
    private int loadType = 0;

    public Bitmap getLoadedImage() {
        if (loadedImageReference != null)
            return loadedImageReference.get();
        return null;
    }

    public void setLoadedImage(Bitmap loadedImage) {
        this.loadedImageReference = new WeakReference<>(loadedImage);
    }

    public View getView() {
        if (viewReference != null)
            return viewReference.get();
        return null;
    }

    public void setView(View view) {
        this.viewReference = new WeakReference<>(view);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public int getLoadType() {
        return loadType;
    }

    public void setLoadType(int loadType) {
        this.loadType = loadType;
    }

    public void releaseInstance() {
        if (loadedImageReference != null)
            loadedImageReference.clear();
        loadedImageReference = null;

        if (viewReference != null)
            viewReference.clear();
        viewReference = null;
    }
}
