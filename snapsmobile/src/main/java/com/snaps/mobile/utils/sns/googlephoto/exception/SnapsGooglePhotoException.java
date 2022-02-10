package com.snaps.mobile.utils.sns.googlephoto.exception;

/**
 * Created by ysjeong on 2017. 5. 22..
 */

public class SnapsGooglePhotoException extends Exception {
    public static int SNAPS_GOOGLE_PHOTO_EXCEPTION_UNAUTHENTICATED = 401;
    public static int SNAPS_GOOGLE_PHOTO_EXCEPTION_RESOURCE_EXHAUSTED = 429;

    private int exceptionType = -1;

    public static SnapsGooglePhotoException newInstanceWithExceptionType(int type) {
        SnapsGooglePhotoException exception = new SnapsGooglePhotoException();
        exception.setExceptionType(type);
        return exception;
    }

    public int getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(int exceptionType) {
        this.exceptionType = exceptionType;
    }
}
