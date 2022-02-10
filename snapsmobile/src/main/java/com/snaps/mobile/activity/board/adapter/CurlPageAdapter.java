package com.snaps.mobile.activity.board.adapter;

import android.graphics.Bitmap;

import com.snaps.common.customui.curlview.CurlPage;
import com.snaps.common.customui.curlview.CurlView.PageProvider;
import com.snaps.common.data.bitmap.PageBitmap;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.board.BaseMyArtworkDetail;

import errorhandle.logger.Logg;

public class CurlPageAdapter implements PageProvider {
    private static final String TAG = CurlPageAdapter.class.getSimpleName();
    BaseMyArtworkDetail baseAct;
    int pageCount;

    public CurlPageAdapter(BaseMyArtworkDetail baseAct, int pageCount) {
        this.baseAct = baseAct;
        this.pageCount = pageCount;
    }

    @Override
    public int getPageCount() {
        return pageCount;
    }

    @Override
    public void updatePage(CurlPage page, int width, int height, int index) {
        Dlog.d("updatePage() index:" + index);

        PageBitmap pageBitmap = baseAct.getCurlPageBitmap(index);
        if (pageBitmap == null)
            return;

        // 앞장
        Bitmap front = pageBitmap.getBitmap(PageBitmap.ORIENT_RIGHT);
        if (front != null)
            page.setTexture(front, CurlPage.SIDE_FRONT);

        // 뒷장
        Bitmap back = pageBitmap.getBitmap(PageBitmap.ORIENT_LEFT);
        if (back != null)
            page.setTexture(back, CurlPage.SIDE_BACK);
    }
}