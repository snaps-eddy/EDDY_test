package com.snaps.mobile.service;

/**
 * Created by ysjeong on 2018. 1. 2..
 */

public interface SnapsPhotoPrintOrgImgUploadListener {
    enum ePhotoPrintOrgImgUploadResult {
        START,
        RESULT_IS_SUCCESS,
        RESULT_IS_EMPTY,
        RESULT_IS_FAIL,
        IMG_KIND_IS_UPLOADED,
        EXCEPTION,
        SUSPENDED,
        COMPLETED
    }

    void onPhotoPrintOrgImgUploadResult(ePhotoPrintOrgImgUploadResult uploadResult, SnapsPhotoPrintOrgImgUploadResultData uploadResultData);
}
