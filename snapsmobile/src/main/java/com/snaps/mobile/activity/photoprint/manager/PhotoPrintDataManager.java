package com.snaps.mobile.activity.photoprint.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.PhotoPrintChangeCountDialog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.photoprint.NewPhotoPrintListActivity;
import com.snaps.mobile.activity.photoprint.model.ActivityActionListener;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by songhw on 2017. 2. 22..
 */

public class PhotoPrintDataManager {
    private static final String TAG = PhotoPrintDataManager.class.getSimpleName();
    private static PhotoPrintDataManager instance;

    private ActivityActionListener listener;

    private PhotoPrintData baseData, tempData;
    private ArrayList<PhotoPrintData> datas, listEditDatas, detailEditDatas, editBaseDatas;
    private HashMap<String, Bitmap> bitmapHashMap;

    private String thumbnailPath;

    private boolean isSelectMode = false;
    private boolean isModifyMode = false;
    private boolean isEditMode = false;
    private boolean isLargeView = true;

    private int detailSelectedIndex = 0, indicatorHeight;

    public static PhotoPrintDataManager getInstance() {
        if( instance == null )
            instance = new PhotoPrintDataManager();

        synchronized ( instance ) {
            if( instance.baseData == null || instance.datas == null )
                instance.init();
        }

        return instance;
    }

    public PhotoPrintData getCurrentData() { return isModifyMode && tempData != null ? tempData : baseData; }

    public PhotoPrintData getBaseData() { return baseData; }

    /**
     *
     * @param context
     * @return int[]{ listLargeItemSize, listSmallItemSize, detailItemSize };
     */
    public static int[] getLayoutSize( Context context ) {
        int[] size = new int[3];
        int screenSize = UIUtil.getScreenWidth( context );
        size[0] = screenSize - UIUtil.convertDPtoPX( context, 32 );
        size[1] = screenSize - UIUtil.convertDPtoPX( context, 40 );
        size[1] /= 2;
        size[2] = screenSize - UIUtil.convertDPtoPX( context, 35 );
        return size;
    }

    public void init() {
        clearBitmapPool();

        datas = new ArrayList<PhotoPrintData>();
        baseData = new PhotoPrintData();
        tempData = new PhotoPrintData();
        bitmapHashMap = new HashMap<String, Bitmap>();

        isLargeView = true;
        isSelectMode = false;
        isModifyMode = false;
        isEditMode = false;
        isLargeView = true;
    }

    public void destroy() {
        clearBitmapPool();

        listener = null;
        baseData = null;
        tempData = null;
        datas = null;
        detailEditDatas = null;
        listEditDatas = null;
        bitmapHashMap = null;
        instance = null;
    }

    public void startEditFromCartData() {
        if( datas == null || datas.isEmpty() ) return;

        editBaseDatas = new ArrayList<PhotoPrintData>();
        for( PhotoPrintData data : datas )
            editBaseDatas.add( data.clone() );
    }

    public boolean checkIsChangedFromCartData() {
        boolean changed = false;
        if( editBaseDatas != null && datas != null ) {
            if( editBaseDatas.size() != datas.size() ) changed = true;
            else {
                for( int i = 0; i < editBaseDatas.size(); ++i ) {
                    if( PhotoPrintData.isChanged(editBaseDatas.get(i), datas.get(i)) ) {
                        changed = true;
                        break;
                    }
                }
            }
        }
        return changed;
    }

    public boolean checkIsCountChangedFromCartData() {
        boolean changed = false;
        if( editBaseDatas != null && datas != null ) {
            if( editBaseDatas.size() != datas.size() ) changed = true;
            else {
                for( int i = 0; i < editBaseDatas.size(); ++i ) {
                    if( editBaseDatas.get(i).getCount() != datas.get(i).getCount() ) {
                        changed = true;
                        break;
                    }
                }
            }
        }
        return changed;
    }

    public void showChangeCountPopup( final TextView view, final boolean changeAll, int index ) {
        if ( listener == null || !(listener instanceof NewPhotoPrintListActivity) || (!changeAll && (datas == null || index > datas.size() - 1)) || (changeAll && tempData == null) ) return;

        NewPhotoPrintListActivity activity = (NewPhotoPrintListActivity) listener;
        if( (Build.VERSION.SDK_INT  > 16 && activity.isDestroyed()) || activity.isFinishing() ) return;

        PhotoPrintChangeCountDialog photoPrintChangeCountDialog = new PhotoPrintChangeCountDialog( (NewPhotoPrintListActivity) listener );

        final PhotoPrintData data  = changeAll ? tempData : datas.get( index );

        if (!photoPrintChangeCountDialog.isShowing() )
            photoPrintChangeCountDialog.showDialog( data.getCount() );

        photoPrintChangeCountDialog.setListener(new PhotoPrintChangeCountDialog.IPhotoPrintChangeCountDialogListener() {
            @Override
            public void onClick(byte clickedOk, int count) {
                if (clickedOk == PhotoPrintChangeCountDialog.IPhotoPrintChangeCountDialogListener.OK) {
                    view.setText( count + "" );
                    data.setCount( count );

                    if( changeAll )
                        applyAllCount( count );

                    listener.refreshListItems();
                }
            }
        });
    }

    private void applyAllCount( int count ) {
        if( isModifyMode && listEditDatas != null && listEditDatas.size() > 0 ) {
            for( PhotoPrintData listData : listEditDatas )
                listData.setCount( count );
        }

        if( isListenerAccessable() )
            listener.refreshListItems();
    }

    public void applyAllCount() {
        if( tempData != null )
            applyAllCount( tempData.getCount() );
    }


    public boolean isFirstItemChanged() {
        if( editBaseDatas == null || datas == null || editBaseDatas.isEmpty() || datas.isEmpty() ) return false;

        String originId = editBaseDatas.get(0).getMyPhotoSelectImageData().F_IMG_SQNC;
        String newId = datas.get(0).getMyPhotoSelectImageData().F_IMG_SQNC;
        return !originId.equalsIgnoreCase( newId );
    }

    public void setListener( ActivityActionListener listener ) { this.listener = listener; }

    public PhotoPrintData getData( int index ) {
        if( (isModifyMode && (listEditDatas == null || listEditDatas.size() < index + 1))
                || (isEditMode && (detailEditDatas == null || detailEditDatas.size() < index + 1))
                || !isEditMode && (datas == null || datas.size() < index + 1) ) return null;

        return isEditMode ? detailEditDatas.get( index ) : isModifyMode ? listEditDatas.get( index ) : datas.get( index );
    }

    public int getDataCount() {
        return datas == null ? 0 : datas.size();
    }

    public int getIndicatorHeight() { return indicatorHeight; }
    public void setIndicatorHeight(int indicatorHeight) { this.indicatorHeight = indicatorHeight; }

    public String getThumbnailPath() { return this.thumbnailPath; }
    public void setThumbnailPath( String thumbnailPath ) { this.thumbnailPath = thumbnailPath; }

    public boolean isChanged() {
        boolean flag = false;

        if( !isEditMode || (datas == null && detailEditDatas == null) );
        else if( datas == null || detailEditDatas == null ) flag = true;
        else if( datas.size() != detailEditDatas.size() ) flag = true;
        else {
            int index = detailSelectedIndex;
            for( int i = 0; i < detailEditDatas.size(); ++i ) {
                if( PhotoPrintData.isChanged(datas.get(index), detailEditDatas.get(i)) ) {
                    flag = true;
                    break;
                }
                index ++;
                if( index > datas.size() - 1 )
                    index = 0;
            }
        }
        return flag;
    }

    public void replaceDatas() {
        if( !isEditMode || detailEditDatas == null ) return;

        datas = new ArrayList<PhotoPrintData>();
        int index = ( detailEditDatas.size() - detailSelectedIndex ) % detailEditDatas.size();
        for( int i = 0; i < detailEditDatas.size(); ++i ) {
            datas.add( detailEditDatas.get(index) );
            index ++;
            if( index > detailEditDatas.size() - 1 )
                index = 0;
        }

        detailEditDatas.clear();
    }

    public void deleteSelectedDatas() {
        if( datas == null ) return;

        ArrayList<PhotoPrintData> deleteList = new ArrayList<PhotoPrintData>();
        for( PhotoPrintData data : datas ) {
            if( data.isSelected() )
                deleteList.add( data );
        }

        datas.removeAll( deleteList );
    }

    public void setDatas( ArrayList<PhotoPrintData> datas, PhotoPrintData baseData ) {
        this.datas = datas;
        this.baseData = baseData;
    }

    public static String getMapKey( PhotoPrintData data ) {
        if( data == null || data.getMyPhotoSelectImageData() == null ) return null;
        return getMapKey( data.getMyPhotoSelectImageData() );
    }

    public static String getMapKey( MyPhotoSelectImageData data ) {
        if( data == null ) return null;
        return StringUtil.isEmpty(data.FB_OBJECT_ID) ? data.KIND + "_" + data.IMAGE_ID : data.FB_OBJECT_ID;
    }

    public void setDatas( ArrayList<MyPhotoSelectImageData> imageDatas ) {
        if( imageDatas == null || imageDatas.size() < 1 ) return;
        HashMap<String, PhotoPrintData> tempMap = new HashMap<String, PhotoPrintData>();
        if( datas != null && datas.size() > 0 ) {
            for( PhotoPrintData data : datas )
                tempMap.put( getMapKey( data ), data );
        }

        datas = new ArrayList<PhotoPrintData>();
        PhotoPrintData data;
        for( MyPhotoSelectImageData imageData : imageDatas ) {
            String id = getMapKey( imageData );
            if( tempMap.size() > 0 && tempMap.containsKey(id) ) {
                data = tempMap.get( id );
                tempMap.remove( id );
                datas.add( data );
            }
            else {
                data = new PhotoPrintData( imageData );
                data.syncOptions( baseData );
                datas.add( data );
            }
        }
    }

    public void setPageTypeBySaveData( String pageType ) {
        if( StringUtil.isEmpty(pageType) || baseData == null || datas == null ) return;
        String glossyString = "glossy".equalsIgnoreCase( pageType ) ? PhotoPrintData.TYPE_GLOSSY : PhotoPrintData.TYPE_MATT;

        baseData.setGlossyType( glossyString );

        for( PhotoPrintData data : datas )
            data.setGlossyType( glossyString );
    }

    public void initPositionWhenEditMode() {
        MyPhotoSelectImageData imageData;
        float w, h, sizeW, sizeH, startP, endP;
        for( PhotoPrintData data : datas ) {
            imageData = data.getMyPhotoSelectImageData();
            startP = imageData.CROP_INFO.startPercent;
            endP = imageData.CROP_INFO.endPercent;
            sizeW = data.getSize()[0];
            sizeH = data.getSize()[1];
            w = Float.parseFloat( imageData.F_IMG_WIDTH );
            h = Float.parseFloat( imageData.F_IMG_HEIGHT );

            data.setWidth( w );
            data.setHeight( h );

            boolean isRotated = data.getMyPhotoSelectImageData().ROTATE_ANGLE % 180 != 0;
            float temp;

            if( (w > h && !isRotated) || (h > w && isRotated) ) {
                temp = sizeW;
                sizeW = sizeH;
                sizeH = temp;
            }

            if( isRotated ) {
                temp = w;
                w = h;
                h = temp;
            }

            float imageW, imageH;
            if( w / h <= sizeW / sizeH ) {
                imageW = sizeW;
                imageH = sizeW / w * h;
            }
            else {
                imageH = sizeH;
                imageW = sizeH / h * w;
            }

            float x = 0, y = 0;
            float totalMoveArea = 100 - endP + startP;
            if( imageH == sizeH )
                x = ( totalMoveArea / 2 - startP ) / totalMoveArea * 2 * ( imageW - sizeW ) / 2f;
            else
                y = ( totalMoveArea / 2 - startP ) / totalMoveArea * 2 * ( imageH - sizeH ) / 2f;

            data.setX( x );
            data.setY( y );

            if( imageData.THUMBNAIL_PATH.startsWith("http") ) { // original image uploade 안된 경우.
                data.setTinyPath( imageData.THUMBNAIL_PATH );
                imageData.THUMBNAIL_PATH = imageData.PATH;
            }
            else {
                data.setTinyPath( imageData.THUMBNAIL_PATH );
                imageData.PATH = imageData.THUMBNAIL_PATH.replace( "tiny", "oripq" );
                imageData.THUMBNAIL_PATH = imageData.THUMBNAIL_PATH.replace( "tiny", "thum" );
            }

            data.setAngle( imageData.ROTATE_ANGLE );
            data.setCount( imageData.photoPrintCount );
            imageData.ROTATE_ANGLE = 0;
        }
    }



    public Bitmap getResource( Integer position ) {
        if( bitmapHashMap == null || !bitmapHashMap.containsKey("idx_" + position) || bitmapHashMap.get("idx_" + position).isRecycled() )
            return null;

        return bitmapHashMap.get( "idx_" + position );
    }

    public Bitmap getDetailResource( Integer detailPosition ) {
        int position = ( detailSelectedIndex + detailPosition ) % getDataCount();
        if( bitmapHashMap == null || !bitmapHashMap.containsKey("idx_" + position) || bitmapHashMap.get("idx_" + position).isRecycled() )
            return null;

        return bitmapHashMap.get( "idx_" + position );
    }

    public void setBitmapResource( int index, Bitmap bitmap ) {
        bitmapHashMap.put( "idx_" + index, bitmap );
    }

    public void setSize( String width, String height ) {
        int[] size = new int[2];
        try {
            size[0] = Integer.parseInt( width );
            size[1] = Integer.parseInt( height );
        } catch ( NumberFormatException e ) {
            Dlog.e(TAG, e);
        }

        baseData.setSize( size );
        if( datas != null && datas.size() > 0 ) {
            for( PhotoPrintData data : datas )
                data.setSize( size );
        }
    }

    private boolean isListenerAccessable() {
        return listener != null && listener instanceof NewPhotoPrintListActivity && !( (NewPhotoPrintListActivity) listener ).isFinishing();
    }

    public boolean isEditMode() { return isEditMode; }

    public boolean isModifyMode() { return isModifyMode; }

    public boolean isSelectMode() { return isSelectMode; }
    public void changeSelectMode( boolean flag ) {
        isSelectMode = flag;
        if( !isSelectMode && datas != null ) {
            for( PhotoPrintData data : datas )
                data.cancelSelect();
        }
    }
    public boolean toggleSelect( int dataIndex ) {
        if( !isSelectMode || datas == null || datas.size() < dataIndex ) return false;
        datas.get( dataIndex ).toggleSelected();
        return true;
    }

    public void changeModifyMode( boolean flag ) {
        if( !isModifyMode ) {// 신규 수정모드 진입시 초기화.
            tempData = new PhotoPrintData( baseData );
            listEditDatas = new ArrayList<PhotoPrintData>();
            for( int i = 0; i < datas.size(); ++i )
                listEditDatas.add( datas.get(i).clone() );

            if( isListenerAccessable() )
                listener.showApplyChangeButtonLayout( true );
        }
        isModifyMode = flag;

        if( isListenerAccessable() ) {
            listener.enableScroll( !flag, false );
            if( !flag )
                listener.refreshListItems();
        }
    }

    /**
     *
     * @param flag
     * @param firstPosition flag가 false일땐 의미없음
     */
    public void changeDetailEditMode( boolean flag, int firstPosition ) {
        detailSelectedIndex = firstPosition;
        if( flag ) {
            detailEditDatas = new ArrayList<PhotoPrintData>();

            if( datas == null || datas.size() < 1 )
                return;

            int index = detailSelectedIndex;
            do {
                detailEditDatas.add( datas.get(index).clone() );
                index ++;
                if( index > datas.size() - 1 )
                    index = 0;
            }
            while( index != detailSelectedIndex );
        }
        else
            detailEditDatas = null;

        isEditMode = flag;
    }

    public int getDetailDisplayIndex( int index ) {
        int totalCount = detailEditDatas.size();
        return ( detailSelectedIndex + index ) % totalCount;
    }

    /**
     * 일괄선택
     */
    public void applyChanges() {
        if( !isModifyMode || tempData == null ) return;

        baseData.syncOptions( tempData );

        datas = new ArrayList<PhotoPrintData>();
        if( listEditDatas != null && listEditDatas.size() > 0 ) {
            for ( int i = 0; i < listEditDatas.size(); ++i )
                datas.add( listEditDatas.get(i).clone() );
        }

        tempData = null;
        listEditDatas = null;
    }

    public void setCount( int index, int count ) {
        if( (isModifyMode && (listEditDatas == null || listEditDatas.size() < index + 1)) || (!isModifyMode && (datas == null || datas.size() < index + 1)) ) return;
        PhotoPrintData data  = isModifyMode ? listEditDatas.get( index ) : datas.get( index );
        data.setCount( count );
    }

    public void setCounts( int count ) {
        if( !isModifyMode || tempData == null ) return;
        tempData.setCount( count );

        if( listEditDatas != null && listEditDatas.size() > 0 ) {
            for ( PhotoPrintData data : listEditDatas )
                data.setCount( count );
        }

        if( isListenerAccessable() )
            listener.refreshListItems();
    }

    public void toggleImageType( Context context ) {
        if( !isModifyMode || tempData == null ) return;

        boolean flag = !tempData.isImageFull();
        tempData.setImageFull( flag );
        tempData.resetPosition( baseData );

        for( int i = 0; i < datas.size(); ++i ) {
            listEditDatas.get(i).setImageFull( flag );
            listEditDatas.get(i).resetPosition( datas.get(i) );
        }

        if( listener != null )
            listener.refreshListItems();

            MessageUtil.toast( context, tempData.isImageFull() ? R.string.photo_print_toast_message_imagefull : R.string.photo_print_toast_message_paperfull );
    }

    public void toggleGlossyType( Context context ) {
        if( !isModifyMode || tempData == null ) return;
        String glossyType = PhotoPrintData.TYPE_GLOSSY.equalsIgnoreCase(tempData.getGlossyType()) ? PhotoPrintData.TYPE_MATT : PhotoPrintData.TYPE_GLOSSY;
        tempData.setGlossyType( glossyType );

        for( int i = 0; i < datas.size(); ++i )
            listEditDatas.get(i).setGlossyType( glossyType );

        if( listener != null )
            listener.refreshListItems();

            MessageUtil.toast( context, glossyType.equalsIgnoreCase(PhotoPrintData.TYPE_MATT) ? R.string.photo_print_toast_message_matt : R.string.photo_print_toast_message_glossy );
    }

    public void toggleBorder( Context context ) {
        if( !isModifyMode || tempData == null ) return;

        boolean flag = !tempData.isMakeBorder();
        tempData.setMakeBorder( flag );

        for( int i = 0; i < datas.size(); ++i )
            listEditDatas.get(i).setMakeBorder( flag );

        if( listener != null )
            listener.refreshListItems();

        MessageUtil.toast( context, tempData.isMakeBorder() ? R.string.photo_print_toast_message_border_on : R.string.photo_print_toast_message_border_off );
    }

    public void toggleAdjustBrightness( Context context ) {
        if( !isModifyMode || tempData == null ) return;

        boolean flag = !tempData.isAdjustBrightness();
        tempData.setAdjustBrightness( flag );

        for( int i = 0; i < datas.size(); ++i )
            listEditDatas.get(i).setAdjustBrightness( flag );

        if( listener != null )
            listener.refreshListItems();

        MessageUtil.toast( context, tempData.isAdjustBrightness() ? R.string.photo_print_toast_message_adjust_brightness_on : R.string.photo_print_toast_message_adjust_brightness_off );
    }

    public void toggleTakePictureDate( Context context ) {
        if( !isModifyMode || tempData == null ) return;

        boolean flag = !tempData.isShowPhotoDate();
        tempData.setShowPhotoDate( flag );

        for( int i = 0; i < datas.size(); ++i )
            listEditDatas.get(i).setShowPhotoDate( flag );

        if( listener != null )
            listener.refreshListItems();

        MessageUtil.toast( context, tempData.isShowPhotoDate() ? R.string.photo_print_toast_message_show_date : R.string.photo_print_toast_message_hide_date );
    }

    /**
     * 상세 수정
     */
    public void setPaperFull( int index ) {
        if( !isEditMode || detailEditDatas == null || detailEditDatas.size() < index + 1 ) return;
        detailEditDatas.get( index ).setImageFull( false );
    }

    public void setImageFull( int index ) {
        if( !isEditMode || detailEditDatas == null || detailEditDatas.size() < index + 1 ) return;
        detailEditDatas.get( index ).setImageFull( true );
        detailEditDatas.get( index ).initPosition();
    }

    /**
     * 회전을 시키고 0도가 아닐 경우 true를 반환
     * @param index
     * @return angle == 0
     */
    public void setRotate( int index ) {
        if( !isEditMode || detailEditDatas == null || detailEditDatas.size() < index + 1 ) return;

        int angle = detailEditDatas.get( index ).getAngle() + 90;
        if( angle > 270 )
            angle = 0;
        detailEditDatas.get( index ).setAngle( angle );
    }

    public void setPosition( int position, PhotoPrintData data ) {
        if( !isEditMode || data == null || detailEditDatas == null || detailEditDatas.size() < position + 1 ) return;
        float x = data.getX();
        float y = data.getY();
        detailEditDatas.get( position ).setPosition( x, y );
    }


    public void toggleBorder( int index ) {
        if( !isEditMode || detailEditDatas == null || detailEditDatas.size() < index + 1 ) return;

        boolean border = !detailEditDatas.get( index ).isMakeBorder();
        detailEditDatas.get( index ).setMakeBorder( border );
    }

    public void toggleAdjustBrightness( int index ) {
        if( !isEditMode || detailEditDatas == null || detailEditDatas.size() < index + 1 ) return;

        boolean adjustBrightness = !detailEditDatas.get( index ).isAdjustBrightness();
        detailEditDatas.get( index ).setAdjustBrightness( adjustBrightness );
    }

    public void toggleShowDate( int index ) {
        if( !isEditMode || detailEditDatas == null || detailEditDatas.size() < index + 1 ) return;

        boolean showDate = !detailEditDatas.get( index ).isShowPhotoDate();
        detailEditDatas.get( index ).setShowPhotoDate( showDate );
    }

    public boolean checkNotUploadedFileExist() {
        if( datas != null && datas.size() > 0 )
            for( PhotoPrintData data : datas ) {
                if( StringUtil.isEmpty(data.getMyPhotoSelectImageData().F_UPLOAD_PATH) )
                    return true;
            }
        return false;
    }

    public ArrayList<PhotoPrintData> getDatas() { return datas; }

    public boolean isLargeView() { return isLargeView; }
    public void setLargeView(boolean largeView) { isLargeView = largeView; }

    public static int[] getFrameSize( PhotoPrintData data, int imageW, int imageH ) {
        int[] size = data.getSize();

        if( (size[0] - size[1]) * (imageW - imageH) < 0 )
            size = swapPos( size );

        return size;
    }

    public static int[] swapPos( int[] pos ) {
        if( pos == null || pos.length < 2 ) return pos;
        int[] result = new int[2];
        result[0] = pos[1];
        result[1] = pos[0];
        return result;
    }

    public static float[] getPosFromRc( String rc ) {
        String[] posStr = rc.split( " " );
        float[] pos = new float[posStr.length];
        for( int i = 0; i < posStr.length; ++i )
            pos[i] = Float.parseFloat( posStr[i] );

        return pos;
    }

    public static int[] getFrameRealSize( int[] frameSize, int[] layoutSize ) {
        int w, h;
        if( (float)frameSize[0] / (float)frameSize[1] > (float)layoutSize[0] / (float)layoutSize[1] ) {
            w = layoutSize[0];
            h = (int)( (float)layoutSize[0] / (float)frameSize[0] * (float)frameSize[1] );
        }
        else {
            w = (int)( (float)layoutSize[1] / (float)frameSize[1] * (float)frameSize[0] );
            h = layoutSize[1];
        }
        return new int[]{ w, h };
    }

    private void clearBitmapPool() {
        Iterator<String> it;
        if( bitmapHashMap != null && bitmapHashMap.size() > 0 ) {
            it = bitmapHashMap.keySet().iterator();
            while (it.hasNext())
                clearBitmap( it.next() );
        }
    }

    private void clearBitmap( String key ) {
        if( bitmapHashMap != null && bitmapHashMap.containsKey(key) ) {
            Bitmap bitmap = bitmapHashMap.get( key );
            if( bitmap != null && !bitmap.isRecycled() ) {
            }
        }
    }
}
