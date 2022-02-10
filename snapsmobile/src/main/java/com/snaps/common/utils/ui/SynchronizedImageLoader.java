package com.snaps.common.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.SnapsCustomTargets;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.mobile.R;

/**
 * Created by songhw on 2017. 1. 12..
 */

public class SynchronizedImageLoader {
    private static final String TAG = SynchronizedImageLoader.class.getSimpleName();

    private ImageLoadInfo[] imageLoadInfos;
    private boolean started = false, isPhotobookType = false;
    private int index = 0;
    private boolean isSuspened = false;

    public SynchronizedImageLoader() {
        imageLoadInfos = new ImageLoadInfo[3];
    }

    public void addShadow( ImageView iv, boolean isPhotobookType ) {
        if( started ) return;

        this.isPhotobookType = isPhotobookType;
        imageLoadInfos[2] = new ImageLoadInfo( iv );
    }

    public void add( String path, ImageView iv, boolean isSkin ) {
        if( started ) return;

        imageLoadInfos[ isSkin ? 1 : 0 ] = new ImageLoadInfo( path, iv );
    }

    public void start( Context context ) {
        if (isSuspened ) return;
        started = true;
        index = 0;

        loadBitmap( context, imageLoadInfos[index] );
    }

    public void suspendTask() {
        if (imageLoadInfos == null) return;
        for (ImageLoadInfo imageLoadInfo : imageLoadInfos) {
            if (imageLoadInfo == null) continue;
            imageLoadInfo.setSuspended(true);
        }
        isSuspened = true;
    }

    public void restart() {
        isSuspened = false;
    }

    public void unbindImageViewReferences() {
        if (imageLoadInfos == null || imageLoadInfos.length < 1) return;
        for (ImageLoadInfo imageLoadInfo : imageLoadInfos) {
            if (imageLoadInfo == null) continue;
            ImageView imageView = imageLoadInfo.getIv();
            if (imageView != null) {
                try {
                    ViewUnbindHelper.unbindReferences(imageView, null, false);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }
    }

    private void loadBitmap( final Context context, final ImageLoadInfo info ) {
        if (isSuspened ) return;

        if( info == null || (info.bitmap != null && info.bitmap.getByteCount() > 0) ) {
            checkComplete( context );
            return;
        }

        //이렇게 크게 부르면 OOM 난다...-_-;;
//        int width = Math.min((int)(UIUtil.getScreenWidth(context) * .8f), 2048);
//        int height = Math.min((int)(UIUtil.getScreenHeight(context) * .8f), 2048);
        int width = Math.min((int)(UIUtil.getScreenWidth(context) * .4f), 480);
        int height = Math.min((int)(UIUtil.getScreenHeight(context) * .4f), 480);

        ImageLoader.with( context ).load( info.path ).skipMemoryCache(true).override(width, height).into(new SnapsCustomTargets<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition transition) {
                if (!BitmapUtil.isUseAbleBitmap(resource) || isSuspened) return;
                info.loadComplete( resource );
                checkComplete( context );
            }
        });
    }

    private void checkComplete( Context context ) {
        if (isSuspened ) return;

        if( index < imageLoadInfos.length - 1 ) {
            index ++;
            loadBitmap( context, imageLoadInfos[index] );
            return;
        }

        setShadowLayoutSize();

        if( isPhotobookType )
            setCombinedBitmap();
        else {
            for (int i = 0; i < imageLoadInfos.length; ++i) {
                if( imageLoadInfos[i] != null )
                    imageLoadInfos[i].setImage();
            }
        }
    }

    private void setCombinedBitmap() {
        if (isSuspened ) return;

        Bitmap bitmap;
        int width = isPhotobookType ? imageLoadInfos[2].iv.getLayoutParams().width : imageLoadInfos[0].iv.getRight();
        int height = isPhotobookType ? imageLoadInfos[2].iv.getLayoutParams().height : imageLoadInfos[0].iv.getBottom();
        bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );

        Canvas canvas = new Canvas( bitmap );

        if( isPhotobookType && imageLoadInfos[2].bitmap != null ) {
            byte[] chunk = imageLoadInfos[2].bitmap.getNinePatchChunk();
            imageLoadInfos[2].ninePatchDrawable = new NinePatchDrawable( imageLoadInfos[2].bitmap, chunk, new Rect(), null );
            imageLoadInfos[2].ninePatchDrawable.setBounds(0, 0, width, height);
            imageLoadInfos[2].ninePatchDrawable.draw( canvas );
        }
        canvas.drawBitmap( imageLoadInfos[0].bitmap,
                new Rect(0,0,imageLoadInfos[0].bitmap.getWidth(),imageLoadInfos[0].bitmap.getHeight()),
                new Rect( imageLoadInfos[0].iv.getPaddingLeft(), imageLoadInfos[0].iv.getPaddingTop(), width - imageLoadInfos[0].iv.getPaddingRight(), height - imageLoadInfos[0].iv.getPaddingBottom()), null);

        if( imageLoadInfos[1] != null && imageLoadInfos[1].bitmap != null )
            canvas.drawBitmap( imageLoadInfos[1].bitmap,
                    new Rect(0,0,imageLoadInfos[1].bitmap.getWidth(),imageLoadInfos[1].bitmap.getHeight()),
                    new Rect( imageLoadInfos[1].iv.getPaddingLeft(), imageLoadInfos[1].iv.getPaddingTop(), width - imageLoadInfos[1].iv.getPaddingRight(), height - imageLoadInfos[1].iv.getPaddingBottom()), null);

        imageLoadInfos[ isPhotobookType ? 2 : 0 ].iv.setImageBitmap( bitmap );
    }

    private void setShadowLayoutSize() {
        if (isSuspened ) return;

        if( !isPhotobookType ) {
            imageLoadInfos[0].iv.setPadding( 0, 0, 0, 0 );
            if( imageLoadInfos[1] != null ) imageLoadInfos[1].iv.setPadding( 0, 0, 0, 0 );
            return;
        }

        Bitmap imageBitmap = imageLoadInfos[0].bitmap;

        ViewGroup.LayoutParams params = imageLoadInfos[2].iv.getLayoutParams();
        int imageViewSize[] = { imageLoadInfos[0].iv.getWidth(), imageLoadInfos[0].iv.getHeight() };
        int bitmapSize[] = { imageBitmap.getWidth(), imageBitmap.getHeight() };
        int imageSize[] = new int[2];

        int basePadding = UIUtil.convertDPtoPX( imageLoadInfos[0].iv.getContext(), 16 );

        if( (float)imageViewSize[0] / (float)imageViewSize[1] > (float)bitmapSize[0] / (float)bitmapSize[1] ) {
            imageSize[1] = imageViewSize[1] - basePadding * 2;
            imageSize[0] = (int)( (float)bitmapSize[0] / (float)bitmapSize[1] * (float)imageSize[1] );
        }
        else {
            imageSize[0] = imageViewSize[0] - basePadding * 2;
            imageSize[1] = (int)( (float)bitmapSize[1] / (float)bitmapSize[0] * (float)imageSize[0] );
        }

        params.width = imageSize[0] + basePadding * 2;
        params.height = imageSize[1] + basePadding * 2;
        imageLoadInfos[2].iv.setLayoutParams( params );

        imageLoadInfos[0].iv.setPadding( basePadding, basePadding, basePadding, basePadding );
        if( imageLoadInfos[1] != null )
            imageLoadInfos[1].iv.setPadding( basePadding, basePadding, basePadding, basePadding );
    }

    private class ImageLoadInfo {
        private ImageView iv;
        private String path;
        private Bitmap bitmap;
        private NinePatchDrawable ninePatchDrawable;
//        private BitmapDrawable bitmapDrawable;
        private boolean isSuspended = false;

        public ImageLoadInfo( String path, ImageView iv ) {
            this.iv = iv;
            this.path = path;
        }

        // shadow 전용 생성자
        public ImageLoadInfo( ImageView iv ) {
            if (isSuspended) return;
            this.iv = iv;
            try {
                bitmap = BitmapFactory.decodeResource( iv.getResources(), R.drawable.bookshadow );
            } catch (OutOfMemoryError e) { Dlog.e(TAG, e); }
        }

        public void loadComplete( Bitmap resource ) {
            if (isSuspended) return;
            bitmap = resource;//CropUtil.getInSampledBitmapCopy(resource, Bitmap.Config.ARGB_8888);//BitmapFactory.decodeByteArray(resource, 0, resource.length);
//            bitmapDrawable = new BitmapDrawable( iv.getResources(), bitmap );
        }

        public ImageView getIv() {
            return iv;
        }

        public void setImage() {
//            if( iv != null && bitmapDrawable != null )
//                iv.setImageDrawable( bitmapDrawable );
            if (!isSuspended && iv != null && iv.isShown() && BitmapUtil.isUseAbleBitmap(bitmap)) {
                iv.setImageBitmap(bitmap);
            }
        }

        public void setSuspended(boolean suspended) {
            isSuspended = suspended;
        }
    }
}
