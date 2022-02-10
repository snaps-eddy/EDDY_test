package com.snaps.common.utils.imageloader.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.security.MessageDigest;

/**
 * Created by ysjeong on 2018. 3. 20..
 */

public class MaskImageTransformation implements Transformation<Bitmap> {

    private static final String ID = "com.snaps.common.utils.imageloader.transformations.MaskImageTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);
    private Paint maskingPaint = new Paint();

    private BitmapPool bitmapPool;
    private BitmapDrawable maskDrawable;

    public MaskImageTransformation(Context context, BitmapDrawable maskDrawable) {
        this(Glide.get(context).getBitmapPool(), maskDrawable);
    }

    public MaskImageTransformation(BitmapPool pool, BitmapDrawable maskDrawable) {
        this.bitmapPool = pool;
        this.maskDrawable = maskDrawable;
        this.maskingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        this.maskingPaint.setAntiAlias(true);
    }

    @Override
    public Resource<Bitmap> transform(Context context, Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();

        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap result = bitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        if (maskDrawable != null) {
            Canvas canvas = new Canvas(result);
            maskDrawable.setBounds(0, 0, width, height);
            maskDrawable.draw(canvas);
            canvas.drawBitmap(source, 0, 0, maskingPaint);
        }

        return BitmapResource.obtain(result, bitmapPool);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MaskImageTransformation;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
