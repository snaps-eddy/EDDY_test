package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;


import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.SnapsCustomTargets;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.StringUtil;

/**
 * Created by songhw on 2017. 5.` 16..
 */

public class ResourceSelector {
    private static final boolean FROM_SERVER = false;
    private static final String SERVER_URL = SnapsAPI.DOMAIN(); // TODO

    public static void setResource( View view, String imageName ) {
        setResource( view, imageName, view instanceof ImageView ? false : true );
    }

    public static void setResource( View view, String imageName, boolean asBackground ) {
        if( view == null || StringUtil.isEmpty(imageName) ) return;
        Context context = view.getContext();
        Resources resources = context.getResources();

        if( FROM_SERVER )
            ImageLoader.with( context ).load( SERVER_URL + imageName + ".png" ).into( view );
        else {
            int resId = resources.getIdentifier( imageName, "drawable", context.getPackageName() );
            if( asBackground ) {
                if( Build.VERSION.SDK_INT > 15 )
                    view.setBackground( resources.getDrawable(resId) );
                else
                    view.setBackgroundResource( resId );
            }
            else if( view instanceof ImageView ) {
                ImageView imageView = (ImageView) view;
                imageView.setImageResource( resId );
            }
        }
    }

//    public static void setPatternResource( final View view, String imageName, final int size, final boolean verticalPattern ) {
//        if( view == null || StringUtil.isEmpty(imageName) ) return;
//        Context context = view.getContext();
//        Resources resources = context.getResources();
//
//        if( FROM_SERVER )
//            ImageLoader.with( context ).load( SERVER_URL + imageName + ".png" ).skipMemoryCache(false).into( new SimpleTarget<byte[]>() {
//                @Override
//                public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    Bitmap bitmap = BitmapFactory.decodeByteArray( resource, 0, resource.length, options );
//                    setPatternResource( view, bitmap, size, verticalPattern );
//                }
//            } );
//        else {
//            int resId = resources.getIdentifier( imageName, "drawable", context.getPackageName() );
//            Bitmap bitmap = BitmapFactory.decodeResource( resources, resId );
//            setPatternResource( view, bitmap, size, verticalPattern );
//        }
//    }

    public static void setPatternResource( final View view, String imageName ) {
        if( view == null || StringUtil.isEmpty(imageName) ) return;
        Context context = view.getContext();
        Resources resources = context.getResources();

        if( FROM_SERVER ) {
            ImageLoader.with( context ).load( SERVER_URL + imageName + ".png" ).skipMemoryCache(false).into( new SnapsCustomTargets<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    if (!BitmapUtil.isUseAbleBitmap(resource)) return;
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    Bitmap bitmap = resource.copy(Bitmap.Config.ARGB_8888, false);//BitmapFactory.decodeByteArray( resource, 0, resource.length, options );
                    setPatternResource( view, resource );
                }
            });
        } else {
            int resId = resources.getIdentifier( imageName, "drawable", context.getPackageName() );
            Bitmap bitmap = BitmapFactory.decodeResource( resources, resId );
            setPatternResource( view, bitmap );
        }
    }

    private static void setPatternResource( View view, Bitmap bitmap, int size, boolean verticalPattern ) {
        Bitmap newBitmap = Bitmap.createScaledBitmap( bitmap, verticalPattern ? size : bitmap.getWidth(), verticalPattern ? bitmap.getHeight() : size, true );
        BitmapDrawable bitmapDrawable = new BitmapDrawable( newBitmap );
        if( verticalPattern )
            bitmapDrawable.setTileModeY( Shader.TileMode.REPEAT );
        else
            bitmapDrawable.setTileModeX( Shader.TileMode.REPEAT );

        if( Build.VERSION.SDK_INT > 15 )
            view.setBackground( bitmapDrawable );
        else
            view.setBackgroundDrawable( bitmapDrawable );
    }

    private static void setPatternResource( View view, Bitmap bitmap ) {
        BitmapDrawable bitmapDrawable = new BitmapDrawable( bitmap );
        bitmapDrawable.setTileModeXY( Shader.TileMode.REPEAT, Shader.TileMode.REPEAT );

        if( Build.VERSION.SDK_INT > 15 )
            view.setBackground( bitmapDrawable );
        else
            view.setBackgroundDrawable( bitmapDrawable );
    }

    /**
     *
     * @param views leftTop, rightTop, leftBottom, rightBottom
     * @param imageName
     */
    public static void setQuadrisetedBitmaps( final View[] views, String imageName ) {
        if( views.length < 4 || views[0] == null || StringUtil.isEmpty(imageName) ) return;

        Context context = views[0].getContext();
        Resources resources = context.getResources();

        if( FROM_SERVER ) {
            ImageLoader.with( context ).load( SERVER_URL + imageName + ".png" ).skipMemoryCache(false).into( new SnapsCustomTargets<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    if (!BitmapUtil.isUseAbleBitmap(resource)) return;
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    Bitmap bitmap = resource.copy(Bitmap.Config.ARGB_8888, false);//BitmapFactory.decodeByteArray( resource, 0, resource.length, options );
                    setQuadrisetedBitmap( views, resource );
                }
            });
        } else {
            int resId = resources.getIdentifier( imageName, "drawable", context.getPackageName() );
            Bitmap bitmap = BitmapFactory.decodeResource( resources, resId );
            setQuadrisetedBitmap( views, bitmap );
        }
    }

    /**
     *
     * @param views
     * @param imageName { leftFrame, topFrame, rightFrame, bottomFrame }
     */
    public static void setWoodFrames( final View[] views, String[] imageName, final int borderWidth, final int frameWidth, final int frameHeight, final int[] frameMargins ) {
        if( views.length < 4 || views[0] == null || imageName.length < 4 || StringUtil.isEmpty(imageName[0]) ) return;

        Context context = views[0].getContext();
        Resources resources = context.getResources();

        for( int i = 0; i < imageName.length; ++i ) {
            final int index = i;
            if( FROM_SERVER ) {
                ImageLoader.with( context ).load( SERVER_URL + imageName[i] + ".png" ).skipMemoryCache(false).into( new SnapsCustomTargets<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        if (!BitmapUtil.isUseAbleBitmap(resource)) return;

//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        Bitmap bitmap = resource.copy(Bitmap.Config.ARGB_8888, false);//BitmapFactory.decodeByteArray( resource, 0, resource.length, options );
                        setBitmapToView( getCuttedFrameBitmap(resource, index, borderWidth, frameWidth, frameHeight, frameMargins), views[index] );
                    }
                } );
            }
            else {
                int resId = resources.getIdentifier( imageName[i], "drawable", context.getPackageName() );
                Bitmap bitmap = BitmapFactory.decodeResource( resources, resId );
                setBitmapToView( getCuttedFrameBitmap(bitmap, index, borderWidth, frameWidth, frameHeight, frameMargins), views[index] );
            }
        }
    }

    /**
     *
     * @param origin
     * @param type 0: left, 1: top, 2: right, 3: bottom
     * @return
     */
    private static Bitmap getCuttedFrameBitmap( Bitmap origin, int type, int borderWidth, int frameWidth, int frameHeight, int[] frameMargins ) {
        Bitmap resultBitmap = Bitmap.createBitmap( origin.getWidth(), origin.getHeight(), origin.getConfig() );

        Canvas canvas = new Canvas( resultBitmap );

        Point[] points = getPoints( origin, type, borderWidth, frameWidth, frameHeight, frameMargins );

        Paint paint = new Paint();
        paint.setAntiAlias( true );
        Path path = new Path();
        path.moveTo( points[0].x, points[0].y );
        path.lineTo( points[1].x, points[1].y );
        path.lineTo( points[2].x, points[2].y );
        path.lineTo( points[3].x, points[3].y );
        path.lineTo( points[0].x, points[0].y );

        canvas.drawPath( path, paint );
        paint.setXfermode( new PorterDuffXfermode(PorterDuff.Mode.SRC_IN) );
        canvas.drawBitmap( origin, 0, 0, paint  );

        return resultBitmap;
    }

    private static Point[] getPoints( Bitmap origin, int type, int borderWidth, int frameWidth, int frameHeight, int[] frameMargins ) {
        float[] bitmapSize = new float[]{ origin.getWidth(), origin.getHeight() };
        float[] targetSize = new float[]{ type == 0 || type == 2 ? borderWidth : frameWidth - frameMargins[0] - frameMargins[2], type == 0 || type == 2 ? frameHeight - frameMargins[1] - frameMargins[3] : borderWidth };
        int adjustWidth = 5;
        int[] size = new int[]{ type == 0 || type == 2 ? (int)((borderWidth - adjustWidth) / targetSize[1] * bitmapSize[1]) : (int)((borderWidth - adjustWidth) / targetSize[0] * bitmapSize[0]), (int)bitmapSize[ type == 0 || type == 2 ? 1 : 0 ] };

        Point[] points = new Point[4];
        switch ( type ) {
            case 0:
                points[0] = new Point( 0, 0 );
                points[1] = new Point( (int)bitmapSize[0], size[0] );
                points[2] = new Point( (int)bitmapSize[0], size[1] - size[0] );
                points[3] = new Point( 0, size[1] );
                break;
            case 1:
                points[0] = new Point( 0, 0 );
                points[1] = new Point( size[1], 0 );
                points[2] = new Point( size[1] - size[0], (int)bitmapSize[1] );
                points[3] = new Point( size[0], (int)bitmapSize[1] );
                break;
            case 2:
                points[0] = new Point( 0, size[0] );
                points[1] = new Point( (int)bitmapSize[0], 0 );
                points[2] = new Point( (int)bitmapSize[0], size[1] );
                points[3] = new Point( 0, size[1] - size[0] );
                break;
            case 3:
                points[0] = new Point( size[0], 0 );
                points[1] = new Point( size[1] - size[0], 0 );
                points[2] = new Point( size[1], (int)bitmapSize[1] );
                points[3] = new Point( 0, (int)bitmapSize[1] );
                break;
        }

        return points;
    }

    private static void setQuadrisetedBitmap( View[] views, Bitmap bitmap ) {
        Bitmap[] bitmaps = new Bitmap[4];

        int x, y, w, h;
        w = bitmap.getWidth();
        h = bitmap.getHeight();
        x = w / 2;
        y = h / 2;

        bitmaps[0] = Bitmap.createBitmap( bitmap, 0, 0, x, y );
        bitmaps[1] = Bitmap.createBitmap( bitmap, x, 0, w - x, y );
        bitmaps[2] = Bitmap.createBitmap( bitmap, 0, y, x, h - y );
        bitmaps[3] = Bitmap.createBitmap( bitmap, x, y, w - x, h - y );

        for( int i = 0; i < bitmaps.length; ++i )
            setBitmapToView( bitmaps[i], views[i] );
    }

    private static void setBitmapToView( Bitmap bitmap, View view ) {
        BitmapDrawable bitmapDrawable = new BitmapDrawable( bitmap );
        if( Build.VERSION.SDK_INT > 15 )
            view.setBackground( bitmapDrawable );
        else
            view.setBackgroundDrawable( bitmapDrawable );
    }
}