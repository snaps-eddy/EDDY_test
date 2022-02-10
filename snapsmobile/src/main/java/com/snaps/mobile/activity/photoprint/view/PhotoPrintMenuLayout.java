package com.snaps.mobile.activity.photoprint.view;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IntegerOnlyTextWatcher;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.photoprint.NewPhotoPrintListActivity;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintDataManager;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;

/**
 * Created by songhw on 2017. 2. 28..
 */

public class PhotoPrintMenuLayout extends FrameLayout {
    private static final String TAG = PhotoPrintMenuLayout.class.getSimpleName();

    public static int MENU_LAYOUT_SIZE;
    public static int MENU_LAYOUT_SELECT_AREA_SIZE;

    public static final int LAYOUT_NORMAL = 0;
    public static final int LAYOUT_SELECT = 1;
    public static final int LAYOUT_MODIFY = 2;
    public static final int LAYOUT_CHANGE_COUNT = 3;

    private TextView count, imageTypeText, paperTypeText, borderText, adjustBrightnessText, dateText;
    private TextView countChangeText;
    private ImageView imageType, paperType, border, adjustBrightness, date, menuDim;

    private View countLayout, controlLayout, selectText, cancelText;

    private NewPhotoPrintListActivity listener;

    public PhotoPrintMenuLayout(Context context) {
        super(context);
        init();
    }

    public PhotoPrintMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoPrintMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        MENU_LAYOUT_SIZE = UIUtil.convertDPtoPX( getContext(), 113 );
        MENU_LAYOUT_SELECT_AREA_SIZE = UIUtil.convertDPtoPX( getContext(), 64 );

        if( getContext() instanceof NewPhotoPrintListActivity )
            listener = (NewPhotoPrintListActivity) getContext();

        LayoutInflater inflater = LayoutInflater.from( getContext() );
        addView( inflater.inflate(R.layout.photo_print_menu_layout, null) );

        count = (TextView) findViewById( R.id.count );

        imageType = (ImageView) findViewById( R.id.image_fit_type );
        imageTypeText = (TextView) findViewById( R.id.image_fit_type_text );

        paperType = (ImageView) findViewById( R.id.page_type );
        paperTypeText = (TextView) findViewById( R.id.page_type_text );

        border = (ImageView) findViewById( R.id.border );
        borderText = (TextView) findViewById( R.id.border_text );

        adjustBrightness = (ImageView) findViewById( R.id.adjust_brightness );
        adjustBrightnessText = (TextView) findViewById( R.id.adjust_brightness_text );

        date = (ImageView) findViewById( R.id.photo_date );
        dateText = (TextView) findViewById( R.id.photo_date_text );

        menuDim = (ImageView) findViewById( R.id.menu_dim_area );

        countLayout = findViewById( R.id.change_count_layout );
        controlLayout = findViewById( R.id.control_layout );
        selectText = findViewById( R.id.select_button );
        cancelText = findViewById( R.id.cancel_button );

        countChangeText = (TextView) findViewById( R.id.count_change_text );
        countChangeText.addTextChangedListener( new IntegerOnlyTextWatcher( countChangeText, 3 ) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                applyCount();
            }
        } );

        ( findViewById(R.id.count_layout) ).setOnClickListener( clickListener );
        ( findViewById(R.id.image_fit_type_layout) ).setOnClickListener( clickListener );
        ( findViewById(R.id.page_type_layout) ).setOnClickListener( clickListener );
        ( findViewById(R.id.border_layout) ).setOnClickListener( clickListener );
        ( findViewById(R.id.adjust_brightness_layout) ).setOnClickListener( clickListener );
        ( findViewById(R.id.photo_date_layout) ).setOnClickListener( clickListener );
        ( findViewById(R.id.minus_button) ).setOnClickListener( clickListener );
        ( findViewById(R.id.count_change_text) ).setOnClickListener( clickListener );
        ( findViewById(R.id.plus_button) ).setOnClickListener( clickListener );
        ( findViewById(R.id.select_button) ).setOnClickListener( clickListener );
        ( findViewById(R.id.cancel_button) ).setOnClickListener( clickListener );
        ( findViewById(R.id.small_view_button) ).setOnClickListener( clickListener );
        ( findViewById(R.id.large_view_button) ).setOnClickListener( clickListener );

        refreshStatus( true );
    }

    public void refreshStatus( boolean refreshCount ) {
        PhotoPrintDataManager dataManager = PhotoPrintDataManager.getInstance();

        ( (ImageView)findViewById(R.id.large_view_button) ).setImageResource(dataManager.isLargeView() ? R.drawable.icon_module_large_on : R.drawable.icon_module_large_off);
        ( (ImageView)findViewById(R.id.small_view_button) ).setImageResource(dataManager.isLargeView() ? R.drawable.icon_module_small_off : R.drawable.icon_module_small_on );

        if( refreshCount ) {
            count.setText( dataManager.getCurrentData().getCount() + "" );
            countChangeText.setText( dataManager.getCurrentData().getCount() + "" );
        }

        imageType.setImageResource( dataManager.getCurrentData().isImageFull() ? R.drawable.icon_print_02_on : R.drawable.icon_print_01_on );
        imageTypeText.setText( dataManager.getCurrentData().isImageFull() ? R.string.image_full : R.string.paper_full );

        paperType.setImageResource( PhotoPrintData.TYPE_GLOSSY.equalsIgnoreCase(dataManager.getCurrentData().getGlossyType()) ? R.drawable.icon_glossy_on : R.drawable.icon_matt_on );
        paperTypeText.setText( PhotoPrintData.TYPE_GLOSSY.equalsIgnoreCase(dataManager.getCurrentData().getGlossyType()) ? R.string.glossy : R.string.matt );

        border.setImageResource( dataManager.getCurrentData().isMakeBorder() ? R.drawable.icon_border_on : R.drawable.icon_border_off );
        borderText.setTextColor( getTextColor(dataManager.getCurrentData().isMakeBorder()) );

        adjustBrightness.setImageResource( dataManager.getCurrentData().isAdjustBrightness() ? R.drawable.icon_bright_on : R.drawable.icon_bright_off );
        adjustBrightnessText.setTextColor( getTextColor(dataManager.getCurrentData().isAdjustBrightness()) );

        date.setImageResource( dataManager.getCurrentData().isShowPhotoDate() ? R.drawable.icon_date_on : R.drawable.icon_date_off );
        dateText.setTextColor( getTextColor(dataManager.getCurrentData().isShowPhotoDate()) );
    }

    public void refreshStatus() {
        refreshStatus( false );
    }

    private int getTextColor( boolean flag ) {
        int resId = R.color.photo_print_meun_text_color_on;
        int color = 0;
        if( Build.VERSION.SDK_INT < 23 )
            color = getResources().getColor( resId );
        else
            color = getContext().getColor( resId );

        return color;
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final PhotoPrintDataManager manager = PhotoPrintDataManager.getInstance();
            if( manager.isEditMode() ) return;

            if( v.getId() == R.id.count_layout ) {
                if( manager.isSelectMode() ) return;

                manager.changeModifyMode( true );
                changeLayout( LAYOUT_CHANGE_COUNT );

                PhotoPrintDataManager.getInstance().applyAllCount();
            }
            else if( v.getId() == R.id.image_fit_type_layout ) {
                if( manager.isSelectMode() ) return;

                manager.changeModifyMode( true );
                manager.toggleImageType( getContext() );
                changeLayout( LAYOUT_MODIFY );

                refreshStatus();
            }
            else if( v.getId() == R.id.page_type_layout ) {
                if( manager.isSelectMode() ) return;

                if( listener != null && !listener.isMattTypeAvailable() ) {
                    MessageUtil.toast( getContext(), getContext().getString(R.string.only_glossy_type_available_for_current_size) );
                    return;
                }

                manager.changeModifyMode( true );
                manager.toggleGlossyType( getContext() );
                changeLayout( LAYOUT_MODIFY );

                refreshStatus();
            }
            else if( v.getId() == R.id.border_layout ) {
                if( manager.isSelectMode() ) return;

                manager.changeModifyMode( true );
                manager.toggleBorder( getContext() );
                changeLayout( LAYOUT_MODIFY );

                refreshStatus();
            }
            else if( v.getId() == R.id.adjust_brightness_layout ) {
                if( manager.isSelectMode() ) return;

                manager.changeModifyMode( true );
                manager.toggleAdjustBrightness( getContext() );
                changeLayout( LAYOUT_MODIFY );

                refreshStatus();
            }
            else if( v.getId() == R.id.photo_date_layout ) {
                if( manager.isSelectMode() ) return;

                manager.changeModifyMode( true );
                manager.toggleTakePictureDate( getContext() );
                changeLayout( LAYOUT_MODIFY );

                refreshStatus();
            }
            else if( v.getId() == R.id.minus_button ) {
                changeCount( false );
            }
            else if( v.getId() == R.id.plus_button ) {
                changeCount( true );
            }
            else if( v.getId() == R.id.count_change_text ) {
                if( countChangeText == null ) return;
                manager.showChangeCountPopup( countChangeText, true, -1 );
            }
            else if( v.getId() == R.id.select_button ) {
                if( listener == null || listener.isMenuHided() )
                    return;

                listener.showDeleteButton( true );
                listener.hideMenu();

                changeLayout( LAYOUT_SELECT );
                manager.changeSelectMode( true );
            }
            else if( v.getId() == R.id.cancel_button ) {
                finishEditMode();
            }
            else if( v.getId() == R.id.small_view_button ) {
                if( listener != null && !listener.isMenuHided() )
                    listener.changeListMode( false );
            }
            else if( v.getId() == R.id.large_view_button ) {
                if( listener != null && !listener.isMenuHided() )
                    listener.changeListMode( true );
            }
        }
    };

    public void changeLayout( int mode ) {
        countLayout.setVisibility( mode == LAYOUT_CHANGE_COUNT ? View.VISIBLE : View.GONE );
        controlLayout.setVisibility( mode == LAYOUT_NORMAL || mode == LAYOUT_SELECT ? View.VISIBLE : View.GONE );

        selectText.setVisibility( mode == LAYOUT_SELECT ? View.GONE : View.VISIBLE );
        cancelText.setVisibility( mode == LAYOUT_SELECT ? View.VISIBLE : View.GONE );

        if( mode != LAYOUT_CHANGE_COUNT )
            UIUtil.hideKeyboard( getContext(), countChangeText );

        if( mode != LAYOUT_NORMAL && listener != null )
            listener.closeTutorial();
    }

    public void finishSelectMode() {
        changeLayout( LAYOUT_NORMAL );
        PhotoPrintDataManager.getInstance().changeSelectMode( false );
        menuDim.setVisibility( View.GONE );

        if( listener != null ) {
            listener.showDeleteButton( false );
            listener.showMenu();
        }
    }

    public void finishEditMode() {
        if( listener == null || listener.isMenuHided() )
            return;

        changeLayout( LAYOUT_NORMAL );
        PhotoPrintDataManager.getInstance().changeSelectMode( false );

        listener.showDeleteButton( false );
        listener.showMenu();
        listener.refreshListItems();
    }

    private int getCurrentCount() {
        if( countChangeText == null || countChangeText.getText().length() < 1 ) return 0;

        int countValue;
        try {
            countValue = Integer.parseInt( countChangeText.getText().toString() );
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

        PhotoPrintDataManager.getInstance().setCounts( countValue );
        count.setText( "" + countValue );
    }
    private void changeCount( boolean isPlus ) {
        int countValue = getCurrentCount();
        if( countValue < 1 ) return;

        countValue += isPlus ? 1 : -1;
        if( countValue < 1 )
            countValue = 1;

        countChangeText.setText( "" + countValue );
        PhotoPrintDataManager.getInstance().setCounts( countValue );
    }
}
