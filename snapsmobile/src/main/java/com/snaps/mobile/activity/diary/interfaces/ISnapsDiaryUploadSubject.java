package com.snaps.mobile.activity.diary.interfaces;

/**
 * Created by ysjeong on 16. 3. 22..
 */
public interface ISnapsDiaryUploadSubject {
    void registDiaryUploadObserver(ISnapsDiaryUploadOpserver ob);
    void removeDiaryUploadObserver(ISnapsDiaryUploadOpserver ob);
    void removeAllDiaryUploadObserver();
    void notifyDiaryUploadOpservers(boolean isIssuedInk, boolean isNewWrite);
}
