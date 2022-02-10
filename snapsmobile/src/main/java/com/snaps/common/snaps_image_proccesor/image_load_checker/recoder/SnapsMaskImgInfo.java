package com.snaps.common.snaps_image_proccesor.image_load_checker.recoder;

import com.snaps.common.spc.view.CustomImageView;

/**
 * Created by ysjeong on 16. 6. 2..
 */
public class SnapsMaskImgInfo {
    private CustomImageView customImageView;
    private String maskUrl;

    public CustomImageView getCustomImageView() {
        return customImageView;
    }

    public void setCustomImageView(CustomImageView customImageView) {
        this.customImageView = customImageView;
    }

    public String getMaskUrl() {
        return maskUrl;
    }

    public void setMaskUrl(String maskUrl) {
        this.maskUrl = maskUrl;
    }
}
