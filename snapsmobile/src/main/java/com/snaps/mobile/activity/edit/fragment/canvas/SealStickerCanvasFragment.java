package com.snaps.mobile.activity.edit.fragment.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.spc.SealStickerPageCanvas;
import com.snaps.mobile.activity.edit.thumbnail_skin.SnapsThumbNailSkinConstants;

public class SealStickerCanvasFragment extends BaseSimpleCanvasFragment {
    private Bitmap cartSkinBitmap = null;

    @Override
    protected SnapsPageCanvas provideCanvasView(Boolean isCartThumbnail) {
        return new SealStickerPageCanvas(getActivity(), isCartThumbnail);
    }

    @Override
    protected void loadCartSkinBitmap() {
        try {
            String cartSkinUrl = SnapsAPI.DOMAIN() + SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_RESOURCE_URL + "cart-seal-sticker.png";
            cartSkinBitmap = Glide.with(getContext())
                    .asBitmap()
                    .load(cartSkinUrl)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(CART_THUMB_WIDTH_HEIGHT)
                    .submit()
                    .get();

            Thread.sleep(2500);
        }catch (Exception e) {
            Dlog.e(e);
        }
    }

    @Override
    protected Bitmap captureCanvasView() {
        if (canvas == null) {
            return null;    //화면 전환이 될 때, 발생할 수 있다.
        }

        Bitmap bmp = null;
        try {
            Bitmap bgBmp = getInSampledBitmap(CART_THUMB_WIDTH_HEIGHT, CART_THUMB_WIDTH_HEIGHT);
            Bitmap orgBmp = captureCurrentView();
            if (orgBmp == null) {
                return null;
            }
            Bitmap resizeBitmap = Bitmap.createScaledBitmap(orgBmp, 416, 590, true);

            Canvas cartThumbCanvas = new Canvas(bgBmp);
            cartThumbCanvas.drawRGB(250, 250, 250);

            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            cartThumbCanvas.drawBitmap(resizeBitmap, 152, 65, paint);

            if (cartSkinBitmap != null && !cartSkinBitmap.isRecycled()) {
                cartThumbCanvas.drawBitmap(cartSkinBitmap, 0, 0, paint);
            }

            bmp = bgBmp;
        } catch (Exception e) {
            Dlog.e(e);
        }
        return bmp;
    }
}
