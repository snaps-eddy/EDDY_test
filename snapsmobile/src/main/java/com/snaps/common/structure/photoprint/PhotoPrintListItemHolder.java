package com.snaps.common.structure.photoprint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.customui.NumericKeyBoardTransformationMethod;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.SnapsCustomTargets;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.IntegerOnlyTextWatcher;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.photoprint.NewPhotoPrintListActivity;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintDataManager;
import com.snaps.mobile.activity.photoprint.model.ActivityActionListener;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;
import com.snaps.mobile.activity.ui.menu.renewal.viewpager.TouchCustomLoopRecyclerViewPager;

import font.FTextView;

/**
 * Created by songhw on 2017. 3. 9..
 */

public class PhotoPrintListItemHolder extends RecyclerView.ViewHolder {
    private static final String TAG = PhotoPrintListItemHolder.class.getSimpleName();
    private RelativeLayout rootView;

    private PhotoPrintData data;

    private Typeface typeface;

    private ImageView plusButton, minusButton;
    private TextView countText;

    private boolean isInitialized = false;
    private boolean isLargeItem = false;
    private boolean isDetailItem = false;
    private boolean isImageLoaded = false;
    private boolean isHeaderOrFooterView = false;

    private String thumbPath;

    private int mPosition, position, imageLayoutW, imageLayoutH, imageFrameSize;

    private float posX, posY;

    public PhotoPrintListItemHolder( RelativeLayout rootView, boolean isLargeItem, boolean isHeaderOrFooterView ) {
        super( rootView );
        this.rootView = rootView;
        this.isLargeItem = isLargeItem;
        this.isHeaderOrFooterView = isHeaderOrFooterView;
    }

    public PhotoPrintListItemHolder( RelativeLayout rootView ) {
        super( rootView );
        this.rootView = rootView;
        isDetailItem = true;
    }

    public boolean isHeaderOrFooterView() { return isHeaderOrFooterView; }

    public void refresh( PhotoPrintData newData, int newPosition ) {
        int dataCount = PhotoPrintDataManager.getInstance().getDataCount();
        if( (!isHeaderOrFooterView && (newPosition < 0 || newPosition > dataCount - 1)) ) {
            if( newPosition == -1 ) {
                rootView.removeAllViews();
                rootView.addView( PhotoPrintListAdapter.getHeaderView(rootView.getContext()) );
                isHeaderOrFooterView = true;
            }
            else if( newPosition >= dataCount ) {
                rootView.removeAllViews();
                rootView.addView( PhotoPrintListAdapter.getFooterView(rootView.getContext(), isLargeItem, newPosition) );
                isHeaderOrFooterView = true;
            }
            return;
        }

        position = newPosition;
        setImageSourceAndRotate( newData, data == null || !isImageLoaded || !data.getMyPhotoSelectImageData().PATH.equalsIgnoreCase(newData.getMyPhotoSelectImageData().PATH)  );
    }

    public void hideImageView() {
        rootView.findViewById( R.id.image ).setVisibility( View.GONE );
    }

    public void showImageView() {
        rootView.findViewById( R.id.image ).setVisibility( View.VISIBLE );
    }

    private void doAfterImageSizeCheck() {
        setBorder( data );
        setDate( data );
        setCount( data );
        setSelect( data );
        setDimArea( data );
    }

    private void doAfterImageSizeCheck( PhotoPrintData newData ) {
        if( data.getAngle() != newData.getAngle() || data.isImageFull() != newData.isImageFull() || data.isMakeBorder() != newData.isMakeBorder() ) {
            setBorder( newData );
            setDate( newData );
        }
        else if( data.isShowPhotoDate() != newData.isShowPhotoDate() )
            setDate( newData );

        if( data.getCount() != newData.getCount() )
            setCount( newData );

        if( data.isSelected() != newData.isSelected() )
            setSelect( newData );

        setDimArea( newData );

        data = newData.clone();
    }


    public void init( final PhotoPrintData data, int mPosition ) {
        if( data == null ) { // header나 footer의 경우
            isInitialized = true;
            return;
        }


        this.data = data;
        this.position = mPosition;

        int[] sizes = PhotoPrintDataManager.getLayoutSize( rootView.getContext() );
        imageLayoutW = sizes[ isDetailItem ? 2 : isLargeItem ? 0 : 1 ];
        imageLayoutH = imageLayoutW;

        if( !isDetailItem ) {
            plusButton = (ImageView) rootView.findViewById(R.id.plus_button);
            minusButton = (ImageView) rootView.findViewById(R.id.minus_button);
            countText = (TextView) rootView.findViewById(R.id.count_text);

            plusButton.setOnClickListener( clickListener );
            minusButton.setOnClickListener( clickListener );
            countText.setOnClickListener( clickListener );
            countText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
            countText.addTextChangedListener(new IntegerOnlyTextWatcher(countText, 3) {
                @Override
                public void afterTextChanged(Editable s) {
                    super.afterTextChanged(s);
                    applyCount();
                    if (s.length() < 1) {
                        countText.setText("" + 1);
                    }
                }
            });
        }
        else {
            rootView.findViewById( R.id.image ).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if( data.isImageFull() ) return false;

                    TouchCustomLoopRecyclerViewPager.disablePaging = event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE;
                    if( event.getAction() == MotionEvent.ACTION_DOWN ) {
                        posX = event.getRawX();
                        posY = event.getRawY();
                    }
                    else if( event.getAction() == MotionEvent.ACTION_MOVE )
                        onImageMove( event.getRawX() - posX, event.getRawY() - posY );
                    else if( event.getAction() == MotionEvent.ACTION_UP )
                        onImageMoveDone();

                    return true;
                }
            });
        }

        isInitialized = true;

        if(data.getMyPhotoSelectImageData().isNoPrint){
           rootView.findViewById(R.id.img_noprint).setVisibility(View.VISIBLE);
            if(isDetailItem) {
               MessageUtil.noPrintToast(rootView.getContext(), ResolutionConstants.NO_PRINT_TOAST_OFFSETX_PHOTOPRINT_EDIT, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_PHOTOPRINT_EDIT,true);
            }
        }else{
            rootView.findViewById(R.id.img_noprint).setVisibility(View.GONE);
        }

        setImageSourceAndRotate( data, true );
    }

    private void onImageMove( float disX, float disY ) {
        View frame = rootView.findViewById( R.id.image_frame_Layout );
        View image = rootView.findViewById( R.id.image );

        int[] frameSize = new int[]{ frame.getWidth(), frame.getHeight() };
        int[] imageSize = new int[]{ image.getWidth(), image.getHeight() };

        float rate = (float)Math.max( data.getSize()[0], data.getSize()[1] ) / (float)Math.max( frameSize[0], frameSize[1] );
        float newPos;
        float pos, iSize, fSize, iSizeTemp;
        boolean isRotated = ( data.getAngle() ) % 180 != 0;
        boolean useXPos = ( imageSize[1] == frameSize[1] && !isRotated ) || ( imageSize[0] == frameSize[0] && isRotated );

        iSize = imageSize[ imageSize[0] == frameSize[0] ? 1 : 0 ];
        iSizeTemp = imageSize[ imageSize[0] == frameSize[0] ? 0 : 1 ];
        fSize = frameSize[ imageSize[0] == frameSize[0] ? 1 : 0 ];
        float rotateAdjustValue = isRotated ? ( iSizeTemp - iSize )  / 2f : 0;
        pos = useXPos ? data.getX() : data.getY();

        int screenSize = UIUtil.getScreenWidth( rootView.getContext() );

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) image.getLayoutParams();
        newPos = pos / rate + ( (float)screenSize - iSize) / 2f + ( useXPos ? disX : disY ) - rotateAdjustValue;
        if( newPos > ((float)screenSize - fSize) / 2 - rotateAdjustValue )
            newPos = ((float)screenSize - fSize) / 2 - rotateAdjustValue;
        else if( newPos < ( (float)screenSize - fSize) / 2f - (iSize - fSize) - rotateAdjustValue )
            newPos = ((float)screenSize - fSize) / 2f - (iSize - fSize) - rotateAdjustValue;

        if( useXPos )
            params.leftMargin = (int) newPos;
        else
            params.topMargin = (int) newPos;

        image.setLayoutParams( params );
    }

    private void onImageMoveDone() {
        View frame = rootView.findViewById( R.id.image_frame_Layout );
        View image = rootView.findViewById( R.id.image );
        int[] frameSize = new int[]{ frame.getWidth(), frame.getHeight() };
        int[] imageSize = new int[]{ image.getWidth(), image.getHeight() };
        int layoutW = isDetailItem ? UIUtil.getScreenWidth( rootView.getContext() ) : frameSize[0];
        int layoutH = layoutW;

        float rate = (float)Math.max( frameSize[0], frameSize[1] ) / (float)Math.max( data.getSize()[0], data.getSize()[1] );

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) image.getLayoutParams();
        boolean isRotated = ( data.getAngle() ) % 180 != 0;
        if( (imageSize[1] == frameSize[1] && !isRotated) || (imageSize[0] == frameSize[0] && isRotated) )
            data.setX( ((float)params.leftMargin - (float)(layoutW - imageSize[0]) / 2f) / rate );
        else if( (imageSize[0] == frameSize[0] && !isRotated) || (imageSize[1] == frameSize[1] && isRotated) )
            data.setY( ((float)params.topMargin - (float)(layoutH - imageSize[1]) / 2f) / rate );

        PhotoPrintDataManager.getInstance().setPosition( position, data );
    }

    /**
     * 현재 이미지의 real x, y, w, h, realX, realY 값을 전달
     * @return int[]{ x, y, w, h, realX, realY }
     */
    public float[] getImageInfo( PhotoPrintData data ) {
        ImageView image = (ImageView) rootView.findViewById( R.id.image );
        int[] position = new int[2];
        image.getLocationOnScreen( position );
        setPreRotatePosition( position, image.getWidth(), image.getHeight(), data.getAngle() );
        return new float[]{ position[0], position[1], (float)image.getWidth(), (float)image.getHeight() };
    }

    private void setPreRotatePosition( int[] position, int width, int height, int angle ) {
        int x = position[0];
        int y = position[1];

        if( angle % 360 == 90 ) {
            x = x - height + (height - width) / 2;
            y = y - (height - width) / 2;
        }
        else if( angle % 360 == 180 ) {
            x = x - width;
            y = y - height;
        }
        else if( angle % 360 == 270 ) {
            x = x + (height - width) / 2;
            y = y - width - (height - width) / 2;
        }

        position[0] = x;
        position[1] = y;
    }

    public PhotoPrintData getMyPhotoPrintData() {
        return data;
    }

    private void setImageSourceAndRotate( final PhotoPrintData data, final boolean refreshImageResource ) {
        final ImageView image = (ImageView) rootView.findViewById( R.id.image );
        if( image == null ) return;

        if( (data.getWidth() == 0 || data.getHeight() == 0) &&
                !StringUtil.isEmpty(data.getMyPhotoSelectImageData().F_IMG_WIDTH) &&
                !StringUtil.isEmpty(data.getMyPhotoSelectImageData().F_IMG_HEIGHT) ) {
            try {
                data.setWidth( Float.parseFloat(data.getMyPhotoSelectImageData().F_IMG_WIDTH) );
                data.setHeight( Float.parseFloat(data.getMyPhotoSelectImageData().F_IMG_HEIGHT) );
            } catch (NumberFormatException e) {
                Dlog.e(TAG, e);
            }
        }

        View imageFrameLayout = rootView.findViewById( R.id.image_frame_Layout );
        if( isDetailItem ) {
            image.setRotation( data.getAngle() );
            imageFrameLayout.setRotation( data.getAngle() );
            if( data.isImageFull() ) {
                imageFrameLayout.setBackgroundColor( Color.WHITE );
                ( (View) image.getParent() ).bringToFront();
                rootView.findViewById( R.id.border_layout ).bringToFront();
            }
            else {
                imageFrameLayout.setBackgroundResource( R.drawable.border_fff_1dp );
                imageFrameLayout.bringToFront();
                ( rootView.findViewById(R.id.dim_layout) ).bringToFront();
            }

            ( rootView.findViewById(R.id.date_text) ).bringToFront();
        }
        else {
            imageFrameLayout.setRotation( data.getAngle() );
            rootView.findViewById( R.id.image_borderline_layout ).setRotation( data.getAngle() );
        }

        thumbPath = data.getMyPhotoSelectImageData().THUMBNAIL_PATH;
        boolean isUploadedFile = thumbPath.startsWith("/Upload/");
        if (isUploadedFile)
            thumbPath = SnapsAPI.DOMAIN() + thumbPath;

        if( refreshImageResource ) {
            if( data.getWidth() < 1 || data.getHeight() < 1 ) {
                SnapsCustomTargets<Bitmap> customTargets = new SnapsCustomTargets<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        if (!BitmapUtil.isUseAbleBitmap(resource)) return;

                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        Bitmap tempBitmap = resource.copy(Bitmap.Config.ARGB_8888, false);//BitmapFactory.decodeByteArray( resource, 0, resource.length, options );
                        data.setWidth( options.outWidth );
                        data.setHeight( options.outHeight );
                        data.getMyPhotoSelectImageData().F_IMG_WIDTH = options.outWidth + "";
                        data.getMyPhotoSelectImageData().F_IMG_HEIGHT = options.outHeight + "";

                        int[] size = setFrameAndImageSize( data );
                        Bitmap bitmap = Bitmap.createScaledBitmap( resource, size[0], size[1], false );
                        resource.recycle();
                        image.setImageBitmap( bitmap );
                        PhotoPrintDataManager.getInstance().setBitmapResource( position, bitmap );
                        isImageLoaded = true;

                        setImagePosition( data );
                        if( refreshImageResource ) {
                            PhotoPrintListItemHolder.this.data = data.clone();
                            doAfterImageSizeCheck();
                        }
                        else
                            doAfterImageSizeCheck( data );
                    }
                };

                ImageLoader.with( rootView.getContext() ).load( thumbPath ).skipMemoryCache(false).into(customTargets);
            }
            else {
                final int[] realSize = setFrameAndImageSize(data);
                if( data.getMyPhotoSelectImageData().ROTATE_ANGLE % 180 != 0 ) {
                    int temp = realSize[0];
                    realSize[0] = realSize[1];
                    realSize[1] = temp;
                }

                int maxBitmapSize = Config.getDeviceMaxBitmapSize();
                if( maxBitmapSize > 0 && (realSize[0] > maxBitmapSize || realSize[1] > maxBitmapSize) ) {
                    int w = realSize[0];
                    int h = realSize[1];

                    if( w < h ) {
                        w = (int)( (float)maxBitmapSize / (float)h * (float)w );
                        h = maxBitmapSize;
                    }
                    else {
                        h = (int)( (float)maxBitmapSize / (float)w * (float)h );
                        w = maxBitmapSize;
                    }

                    realSize[0] = w;
                    realSize[1] = h;
                }

                if (isDetailItem) {
                    Bitmap detailBitmap = PhotoPrintDataManager.getInstance().getDetailResource(position);
                    if( detailBitmap == null ) {
                        //                        ImageLoader.with(rootView.getContext()).load(thumbPath).skipMemoryCache(false).override(realSize[0] / 2, realSize[1] / 2).asBitmap().setListener(new RequestListener() {
//                            @Override
//                            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                isImageLoaded = true;
//                                Bitmap bitmap = resource instanceof GlideBitmapDrawable ? ((GlideBitmapDrawable) resource).getBitmap() : resource instanceof Bitmap ? (Bitmap) resource : null;
//                                if ( bitmap != null && !bitmap.isRecycled() ) {
//                                    if (data.getUserSelectWidth() == 0 || data.getHeight() == 0) {
//                                        data.setWidth(bitmap.getUserSelectWidth());
//                                        data.setHeight(bitmap.getHeight());
//                                    }
//                                }
//                                else {
//                                    ImageLoader.with(rootView.getContext()).load(thumbPath).skipMemoryCache(true).override(realSize[0] / 2, realSize[1] / 2).asBitmap().into(image);
//                                    return true;
//                                }
//                                return false;
//                            }
//                        } ).into(image);
                        ImageLoader.with(rootView.getContext()).load(thumbPath).skipMemoryCache(false).override(realSize[0] / 2, realSize[1] / 2).asBitmap().setListener(new RequestListener() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                                isImageLoaded = true;
                                Bitmap bitmap = resource instanceof BitmapDrawable ? ((BitmapDrawable) resource).getBitmap() : resource instanceof Bitmap ? (Bitmap) resource : null;
                                if ( bitmap != null && !bitmap.isRecycled() ) {
                                    if (data.getWidth() == 0 || data.getHeight() == 0) {
                                        data.setWidth(bitmap.getWidth());
                                        data.setHeight(bitmap.getHeight());
                                    }
                                }
                                else {
                                    ImageLoader.with(rootView.getContext()).load(thumbPath).skipMemoryCache(true).override(realSize[0] / 2, realSize[1] / 2).asBitmap().into(image);
                                    return true;
                                }
                                return false;
                            }
                        }).into(image);
                    } else {
                        synchronized ( detailBitmap ) {
                            if ( !detailBitmap.isRecycled() )
                                image.setImageBitmap(detailBitmap);
                            else {
                                ImageLoader.with(rootView.getContext()).load(thumbPath).skipMemoryCache(false).override(realSize[0] / 2, realSize[1] / 2).asBitmap().setListener(new RequestListener() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                                        isImageLoaded = true;
                                        Bitmap bitmap = resource instanceof BitmapDrawable ? ((BitmapDrawable) resource).getBitmap() : resource instanceof Bitmap ? (Bitmap) resource : null;
                                        if ( bitmap != null && !bitmap.isRecycled() ) {
                                            if (data.getWidth() == 0 || data.getHeight() == 0) {
                                                data.setWidth(bitmap.getWidth());
                                                data.setHeight(bitmap.getHeight());
                                            }
                                        }
                                        else {
                                            ImageLoader.with(rootView.getContext()).load(thumbPath).skipMemoryCache(true).override(realSize[0] / 2, realSize[1] / 2).asBitmap().into(image);
                                            return true;
                                        }
                                        return false;
                                    }
                                }).into(image);
                            }
                        }
                    }
                } else {
                    ImageLoader.with(rootView.getContext()).load(thumbPath).skipMemoryCache(false).override(realSize[0] / 2, realSize[1] / 2).setListener(new RequestListener() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                            isImageLoaded = true;
                            Bitmap bitmap = resource instanceof BitmapDrawable ? ((BitmapDrawable) resource).getBitmap() : resource instanceof Bitmap ? (Bitmap) resource : null;
                            if ( bitmap != null && !bitmap.isRecycled() ) {
                                if (data.getWidth() == 0 || data.getHeight() == 0) {
                                    data.setWidth(bitmap.getWidth());
                                    data.setHeight(bitmap.getHeight());
                                }

                                PhotoPrintDataManager.getInstance().setBitmapResource(position, bitmap);
                            }
                            else {
                                ImageLoader.with(rootView.getContext()).load(thumbPath).skipMemoryCache(true).override(realSize[0] / 2, realSize[1] / 2).asBitmap().into(image);
                                return true;
                            }
                            return false;
                        }
                    }).asBitmap().into(image);
                }

                setImagePosition(data);
                if (refreshImageResource) {
                    PhotoPrintListItemHolder.this.data = data.clone();
                    doAfterImageSizeCheck();
                } else
                    doAfterImageSizeCheck(data);
            }
        }
        else {
            setFrameAndImageSize( data );
            isImageLoaded = true;

            setImagePosition( data );
            doAfterImageSizeCheck( data );
        }
    }

    public void setImageBitmap( Bitmap bitmap ) {
        ( (ImageView) rootView.findViewById(R.id.image) ).setImageBitmap( bitmap );
    }

    public void clearImageResource(Context context, boolean clearImageView ) {
        ImageView image = (ImageView) rootView.findViewById( R.id.image );
        ImageLoader.clear(context, image);

        if( clearImageView && image != null ) {
//            image.setImageDrawable( null );
            isImageLoaded = false;
        }
    }

    /**
     * set image frame layout and image's Parameters then return imageView's real size
     * @return imageView size
     */
    private int[] setFrameAndImageSize( PhotoPrintData data ) {
        View frameLayout = rootView.findViewById( R.id.image_frame_Layout );
        boolean isRotated = data.getMyPhotoSelectImageData().ROTATE_ANGLE % 180 != 0;

        RelativeLayout.LayoutParams rParams;
        FrameLayout.LayoutParams fParams;

        int[] layoutSize = new int[]{ isRotated ? imageLayoutH : imageLayoutW, isRotated ? imageLayoutW : imageLayoutH };
        layoutSize[0] -= UIUtil.convertDPtoPX( rootView.getContext(), 2 );
        layoutSize[1] -= UIUtil.convertDPtoPX( rootView.getContext(), 1 );
        int[] frameSize = PhotoPrintDataManager.getFrameSize( data, (int)data.getWidth(), (int)data.getHeight() );
        final int[] frameRealSize = PhotoPrintDataManager.getFrameRealSize( frameSize, layoutSize );
        imageFrameSize = Math.max( frameRealSize[0], frameRealSize[1] );

        rParams = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        rParams.width = frameRealSize[ data.getMyPhotoSelectImageData().ROTATE_ANGLE % 180 != 0 ? 1 : 0 ];
        rParams.height = frameRealSize[ data.getMyPhotoSelectImageData().ROTATE_ANGLE % 180 != 0 ? 0 : 1 ];
        frameLayout.setLayoutParams( rParams );

        View image = rootView.findViewById( R.id.image );
        int[] newPos = UIUtil.getPosByImageType( data.isImageFull(), frameRealSize, new int[]{(int)data.getWidth(), (int)data.getHeight()} );

        fParams = (FrameLayout.LayoutParams) image.getLayoutParams();
        fParams.width = newPos[ isRotated ? 3 : 2 ];
        fParams.height = newPos[ isRotated ? 2 : 3 ];
        image.setLayoutParams( fParams );

        return new int[]{ newPos[2], newPos[3] };
    }

    private void setImagePosition( PhotoPrintData data ) {
        int[] size = data.getSize();
        RelativeLayout.LayoutParams frameParams = (RelativeLayout.LayoutParams) rootView.findViewById( R.id.image_frame_Layout ).getLayoutParams();
        View image = rootView.findViewById( R.id.image );
        FrameLayout.LayoutParams imageParams = (FrameLayout.LayoutParams) image.getLayoutParams();

        int[] frameSize = new int[]{ frameParams.width, frameParams.height };
        int[] imageSize = new int[]{ imageParams.width, imageParams.height };
        int layoutW = isDetailItem ? UIUtil.getScreenWidth( rootView.getContext() ) : frameSize[0];
        int layoutH = isDetailItem ? layoutW : frameSize[1];

        boolean rotatePosition = data.getAngle() != this.data.getAngle() && data.getX() == this.data.getX() && data.getY() == this.data.getY();
        if( rotatePosition ) {
            float x = data.getX();
            data.setX(this.data.getY() * -1);
            data.setY( x );
        }

        float rate = (float)Math.max( frameSize[0], frameSize[1] ) / (float)Math.max( size[0], size[1]);
        float x = data.getX();
        float y = data.getY();
        float temp;

        if( !isDetailItem ) { // 리스트에서는 이미지가 아닌 프레임이 회전하므로 전환이 필요함.
            int angle = data.getAngle();// + 360 - data.getMyPhotoSelectImageData().ROTATE_ANGLE;
            if( angle % 360 == 90 ) {
                temp = x;
                x = y;
                y = -temp;
            }
            if( angle % 360 == 180 ) {
                x = -x;
                y = -y;
            }
            if( angle % 360 == 270 ) {
                temp = x;
                x = -y;
                y = temp;
            }
        }

        x = x * rate + (float)(layoutW - imageSize[0]) / 2f;
        y = y * rate + (float)(layoutH - imageSize[1]) / 2f;

        imageParams.leftMargin = (int)x;
        imageParams.topMargin = (int)y;

        image.setLayoutParams( imageParams );
    }

    private void setBorder( PhotoPrintData data ) {
        if( imageLayoutW == 0 || imageLayoutH == 0 || rootView == null || rootView.getContext() == null || !(rootView.getContext() instanceof NewPhotoPrintListActivity) ) return;

        boolean drawBorder = data.isMakeBorder();
        boolean isImageFull = data.isImageFull();
        int borderSize = ( (NewPhotoPrintListActivity) rootView.getContext() ).getBorderThickness( imageFrameSize );

        View left = rootView.findViewById( R.id.border_left );
        View right = rootView.findViewById( R.id.border_right );
        View top = rootView.findViewById( R.id.border_top );
        View bottom = rootView.findViewById( R.id.border_bottom );

        RelativeLayout.LayoutParams params;
        int x, y;
        FrameLayout.LayoutParams fParams = (FrameLayout.LayoutParams) rootView.findViewById(R.id.image).getLayoutParams();
        x = fParams.leftMargin;
        y = fParams.topMargin;

        params = (RelativeLayout.LayoutParams) rootView.findViewById( R.id.image_frame_Layout ).getLayoutParams();
        if( isDetailItem ) {
            int size = UIUtil.getScreenWidth( rootView.getContext() );
            rootView.findViewById( R.id.border_layout ).setRotation( data.getAngle() );
            x = x - (int)Math.ceil( (size - params.width) / 2f );
            y = y - (int)Math.ceil( (size - params.height) / 2f );
        }


        params = (RelativeLayout.LayoutParams) left.getLayoutParams();
        params.width = drawBorder ? borderSize + (isImageFull ? x : 0) : 0;
        params.leftMargin = isImageFull ? 0 : 0;
        params.topMargin = isImageFull ? y: 0;
        params.bottomMargin = isImageFull ? y : 0;
        left.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) right.getLayoutParams();
        params.width = drawBorder ? borderSize + (isImageFull ? x : 0) : 0;
        params.rightMargin = isImageFull ? 0 : 0;
        params.topMargin = isImageFull ? y : 0;
        params.bottomMargin = isImageFull ? y : 0;
        right.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) top.getLayoutParams();
        params.height = drawBorder ? borderSize + (isImageFull ? y : 0) : 0;
        params.leftMargin = isImageFull ? 0 : 0;
        params.rightMargin = isImageFull ? 0 : 0;
        params.topMargin = isImageFull ? 0 : 0;
        top.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) bottom.getLayoutParams();
        params.height = drawBorder ? borderSize + (isImageFull ? y : 0) : 0;
        params.leftMargin = isImageFull ? 0 : 0;
        params.rightMargin = isImageFull ? 0 : 0;
        params.bottomMargin = isImageFull ? 0 : 0;
        bottom.setLayoutParams(params);
    }

    private void setDate( PhotoPrintData data ) {
        FTextView dateText = (FTextView) rootView.findViewById( R.id.date_text );
        if( dateText == null ) return;

        if( data == null || !data.isShowPhotoDate() || data.getMyPhotoSelectImageData().photoTakenDateTime < 1 || !(rootView.getContext() instanceof NewPhotoPrintListActivity) ) {
            dateText.setVisibility( View.GONE );
            return;
        }

        dateText.setVisibility( View.VISIBLE );
        String[] dateData = ( (NewPhotoPrintListActivity)rootView.getContext() ).getDateStringDatas();
        if( dateData != null && dateData.length > 1 ) {
            setTextSettingByHtmlText( data, dateText, dateData[1] );
            float[] rcPos = PhotoPrintDataManager.getPosFromRc( dateData[0] );
            int[] marginsAndHeight = getRightBottomMarginsAndHeight( data, rcPos );
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dateText.getLayoutParams();
            params.height = marginsAndHeight[2];
            params.rightMargin = marginsAndHeight[0];
            params.bottomMargin = marginsAndHeight[1];
            dateText.setLayoutParams( params );
            Long dateLong = data.getMyPhotoSelectImageData().photoTakenDateTime;
            if( dateLong < 1000000000000L )
                dateLong *= 1000;

            dateText.setText( StringUtil.convertTimeLongToStr(dateLong, "yyyy.MM.dd") );
        }
    }

    /**
     * calculate date textView position
     * @param data
     * @param rcPos
     * @return int[]{ rightMargin, bottomMargin, textViewHeight }
     */
    private int[] getRightBottomMarginsAndHeight( PhotoPrintData data, float[] rcPos ) {
        RelativeLayout.LayoutParams frameParams = (RelativeLayout.LayoutParams) rootView.findViewById( R.id.image_frame_Layout ).getLayoutParams();
        FrameLayout.LayoutParams imageParams = (FrameLayout.LayoutParams) rootView.findViewById( R.id.image ).getLayoutParams();

        int[] size = data.getSize();
        float screenW = UIUtil.getScreenWidth( rootView.getContext() );
        if( !isDetailItem ) {
            screenW = screenW - UIUtil.convertDPtoPX( rootView.getContext(), isLargeItem ? 32 : 40 );
            if( !isLargeItem ) screenW /= 2;
        }
        float screenH = screenW;
        boolean isRotated = data.getAngle() % 180 != 0;
        float frameW = data.isImageFull() ? imageParams.width : frameParams.width;
        float frameH = data.isImageFull() ? imageParams.height : frameParams.height;

        float rate = (float)Math.min( frameParams.width, frameParams.height ) / (float)size[0];
        float realRightMargin = (screenW - (isRotated ? frameH : frameW)) / 2 + rcPos[0] * rate;
        float realBottomMargin = (screenH - (isRotated ? frameW : frameH) ) / 2f;
        float height = rcPos[3] * rate;

        return new int[]{ (int)realRightMargin, (int)realBottomMargin, (int)height };
    }

    private void setTextSettingByHtmlText( PhotoPrintData data, final TextView tv, String htmlText ) {
        String htmlText2 = htmlText.substring( htmlText.indexOf("<") + 1, htmlText.indexOf(">") );
        data.setFontStyleHtml( htmlText2 );
        String[] strAry = htmlText2.split( "' " );

        String[] itemStr;
        for( int i = 0; i < strAry.length; ++i ) {
            itemStr = strAry[i].split( "=" );
            if( itemStr.length > 1 ) {
                if( itemStr[0].contains("color") )
                    tv.setTextColor(Color.parseColor(itemStr[1].replace("'", "")) );
                else if( itemStr[0].contains("fontFamily") ) {
                    final String fontName = itemStr[1].replace( "'", "" );
                    data.setFontFamily( fontName );
                    ATask.executeVoid(new ATask.OnTask() {
                        @Override
                        public void onPre() {
                            typeface = null;
                        }

                        @Override
                        public void onBG() {
                            typeface = FontUtil.getFontTypeface( rootView.getContext(), fontName );
                        }

                        @Override
                        public void onPost() {
                            if( typeface != null )
                                tv.setTypeface( typeface );
                        }
                    });
                }
                else if( itemStr[0].contains("fontSize") ) {
                    float fontSize = Float.parseFloat( itemStr[1].replaceAll("'", "") );
                    fontSize /= isDetailItem || isLargeItem ? 1 : 2;
                    fontSize *= 1.2f; // 편집화면 텍스트 크기 살짝 크미세조정
                    tv.setTextSize( fontSize );
                    data.setFontSize( fontSize );
                }
                else if( itemStr[0].contains("textAlign") ) {
                    String alignStr = itemStr[1].replace( "'", "" );
                    data.setAlign( alignStr );
                    if( "right".equalsIgnoreCase(alignStr) )
                        tv.setGravity( Gravity.TOP|Gravity.RIGHT );
                    else if( "left".equalsIgnoreCase(alignStr) )
                        tv.setGravity( Gravity.TOP|Gravity.LEFT );
                    else if( "top".equalsIgnoreCase(alignStr) )
                        tv.setGravity( Gravity.LEFT|Gravity.TOP );
                    else if( "bottom".equalsIgnoreCase(alignStr) )
                        tv.setGravity( Gravity.LEFT|Gravity.BOTTOM );
                }
            }
        }
    }

    private void setCount( PhotoPrintData data ) {
        if( countText != null )
            countText.setText( "" + data.getCount() );
    }

    private void setDimArea( PhotoPrintData data ) {
        if( !isDetailItem ) return;

        boolean isRotated = ( data.getAngle() ) % 180 != 0;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rootView.findViewById( R.id.image_frame_Layout ).getLayoutParams();
        int sizeW = UIUtil.getScreenWidth( rootView.getContext() );
        int sizeH = sizeW;
        int temp;
        if( isRotated ) {
            temp = sizeW;
            sizeW = sizeH;
            sizeH = temp;
        }

        int w = params.width;
        int h = params.height;
        int l = ( sizeW - w ) / 2;
        int t = ( sizeH - h ) / 2;
        int r = params.width + l;
        int b = params.height + t;

        if( isRotated ) {
            temp = l;
            l = t;
            t = temp;
            temp = r;
            r = b;
            b = temp;
        }

        View topDim = rootView.findViewById( R.id.top_dim );
        View leftDim = rootView.findViewById( R.id.left_dim );
        View rightDim = rootView.findViewById( R.id.right_dim );
        View bottomDim = rootView.findViewById( R.id.bottom_dim );

        params = (RelativeLayout.LayoutParams) topDim.getLayoutParams();
        params.width = sizeW;
        params.height = t;
        params.topMargin = 0;
        params.leftMargin = 0;
        topDim.setLayoutParams( params );

        params = (RelativeLayout.LayoutParams) leftDim.getLayoutParams();
        params.width = l;
        params.height = b - t;
        params.topMargin = t;
        params.leftMargin = 0;
        leftDim.setLayoutParams( params );

        params = (RelativeLayout.LayoutParams) rightDim.getLayoutParams();
        params.width = sizeW - r;
        params.height = b - t;
        params.topMargin = t;
        params.leftMargin = r;
        rightDim.setLayoutParams( params );

        params = (RelativeLayout.LayoutParams) bottomDim.getLayoutParams();
        params.width = sizeW;
        params.height = sizeH - b;
        params.topMargin = b;
        params.leftMargin = 0;
        bottomDim.setLayoutParams( params );
    }

    private void setSelect( PhotoPrintData data ) {
        if( isDetailItem ) return;

        int visible = data.isSelected() ? View.VISIBLE : View.GONE;
        rootView.findViewById( R.id.red_border1 ).setVisibility( visible );
        rootView.findViewById( R.id.red_border2 ).setVisibility( visible );
        rootView.findViewById( R.id.red_border3 ).setVisibility( visible );
        rootView.findViewById( R.id.red_border4 ).setVisibility( visible );
        rootView.findViewById( R.id.select_icon ).setVisibility( visible );
    }

    private void changeCount( boolean isPlus ) {
        int newCount = data.getCount() + (isPlus ? 1 : -1);
        if( newCount < 1 )
            newCount = 1;
        data.setCount( newCount );
        countText.setText( "" + newCount );
    }

    private int getCurrentCount() {
        if( countText == null || countText.getText().length() < 1 ) return 0;

        int countValue;
        try {
            countValue = Integer.parseInt( countText.getText().toString() );
        }
        catch ( NumberFormatException e ) {
            Dlog.e(TAG, e);
            return 0;
        }
        return countValue;
    }

    private void applyCount() {
        int countValue = getCurrentCount();
        if( countValue < 1 ) return;

        data.setCount( countValue );
        PhotoPrintDataManager.getInstance().setCount( position, countValue );
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( rootView.getContext() instanceof ActivityActionListener )
                ( (ActivityActionListener) rootView.getContext() ).closeTutorial();

            if( v.getId() == R.id.minus_button )
                changeCount( false );
            else if( v.getId() == R.id.plus_button )
                changeCount( true );
            else if( v.getId() == R.id.count_text )
                PhotoPrintDataManager.getInstance().showChangeCountPopup( countText, false, position );
        }
    };

    public void clearDatas() {
        data = null;
        isInitialized = false;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
