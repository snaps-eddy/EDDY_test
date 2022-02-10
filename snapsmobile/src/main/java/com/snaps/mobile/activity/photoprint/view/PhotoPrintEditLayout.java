package com.snaps.mobile.activity.photoprint.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.snaps.common.structure.photoprint.PhotoPrintDetailEditAdapter;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintDataManager;
import com.snaps.mobile.activity.photoprint.model.ActivityActionListener;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;
import com.snaps.mobile.activity.ui.menu.renewal.viewpager.TouchCustomLoopRecyclerViewPager;

/**
 * Created by songhw on 2017. 2. 28..
 */

public class PhotoPrintEditLayout extends RelativeLayout {
    private TouchCustomLoopRecyclerViewPager recyclerViewPager;
    private PhotoPrintDetailEditAdapter adapter;

    private RelativeLayout rootView;

    private boolean resourceCleared = false;

    private int currentIndex = 0;

    public PhotoPrintEditLayout(Context context) {
        super(context);
    }

    public PhotoPrintEditLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoPrintEditLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init( RelativeLayout parent, int position ) {
        resourceCleared = false;
        PhotoPrintDataManager.getInstance().changeDetailEditMode( true, position );
        rootView = (RelativeLayout) ( (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) ).inflate( R.layout.photo_print_edit_layout, null, false );
        parent.addView( rootView );

        ( rootView.findViewById(R.id.button1) ).setOnClickListener( paperFullButtonClick );
        ( rootView.findViewById(R.id.button2) ).setOnClickListener( imageFullButtonClick );
        ( rootView.findViewById(R.id.button3) ).setOnClickListener( rotateButtonClick );
        ( rootView.findViewById(R.id.button4) ).setOnClickListener( borderButtonClick );
        ( rootView.findViewById(R.id.button5) ).setOnClickListener( brightbessButtonClick );
        ( rootView.findViewById(R.id.button6) ).setOnClickListener( dateButtonClick );
        ( rootView.findViewById(R.id.prev_button) ).setOnClickListener( prevButtonClick );
        ( rootView.findViewById(R.id.next_button) ).setOnClickListener( nextButtonClick );

        ( rootView.findViewById(R.id.cancel_button) ).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChanges();
            }
        });
        ( rootView.findViewById(R.id.confirm_button) ).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });

        setPageSelected( position );
        initButtonStatus( position );

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerViewPager = (TouchCustomLoopRecyclerViewPager) rootView.findViewById( R.id.view_pager );
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerViewPager.getLayoutParams();
        params.height = UIUtil.getScreenWidth( rootView.getContext() );
        recyclerViewPager.setLayoutParams( params );

        recyclerViewPager.setPadding( 0, 0, 0, 0 );
        recyclerViewPager.setTriggerOffset(0.01f);
        recyclerViewPager.setSinglePageFling(true);
        recyclerViewPager.setClipToPadding(true);
        recyclerViewPager.setLayoutManager(manager);
        recyclerViewPager.setBackgroundColor(Color.TRANSPARENT);
        adapter = new PhotoPrintDetailEditAdapter(getContext());
        recyclerViewPager.setAdapter(adapter);
        recyclerViewPager.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int i, int i1) {
                setPageSelected( recyclerViewPager.getActualCurrentPosition() );
            }
        });
    }

    private void initButtonStatus( int index ) {
        PhotoPrintData data = PhotoPrintDataManager.getInstance().getData( index );
        if( data == null ) return;

        setPaperFullButtonActivate( !data.isImageFull() );
        setBorderButtonActivate( data.isMakeBorder() );
        setAdjustBrightnessButtonActivate( data.isAdjustBrightness() );
        setShowDateButtonActivate( data.isShowPhotoDate() );
    }

    private void refreshItem() {
        recyclerViewPager.getAdapter().notifyDataSetChanged();
    }

    private void setPageSelected( int index ) {
        currentIndex = index;
        int displayIndex = PhotoPrintDataManager.getInstance().getDetailDisplayIndex( index );
        initButtonStatus( index );
        ( (TextView) rootView.findViewById(R.id.page_count_text) ).setText( (displayIndex + 1) + " / " + PhotoPrintDataManager.getInstance().getDataCount() );
    }

    private void finish() {
        if( !resourceCleared )
            clearResource();

        if( getContext() instanceof ActivityActionListener )
            ( (ActivityActionListener) getContext() ).editLayoutFinished();

        ( (ViewGroup) this.getParent() ).removeAllViews();
    }

    private void clearResource() {
        // TODO clear resource
        // 비트맵 따로 저장 안하므로 안해도 될듯. 나중에 메모리 문제 생기면 그때 추가합니다.
    }

    private int getCurrentIndex() {
        return currentIndex;
    }

    private int getTextColor( boolean flag ) {
        int resId = flag ? R.color.photo_print_detail_menu_text_color_on : R.color.photo_print_detail_menu_text_color_off;
        int color = 0;
        if( Build.VERSION.SDK_INT < 23 )
            color = getResources().getColor( resId );
        else
            color = getContext().getColor( resId );

        return color;
    }

    private void setPaperFullButtonActivate( boolean flag ) {
        ( (ImageView) rootView.findViewById(R.id.image1) ).setImageResource( flag ? R.drawable.icon_print_01_w_on : R.drawable.icon_print_01_w_off );
        ( (TextView) rootView.findViewById(R.id.text1) ).setTextColor( getTextColor(flag) );

        ( (ImageView) rootView.findViewById(R.id.image2) ).setImageResource( flag ? R.drawable.icon_print_02_w_off : R.drawable.icon_print_02_w_on );
        ( (TextView) rootView.findViewById(R.id.text2) ).setTextColor( getTextColor(!flag) );
    }
    private void setBorderButtonActivate( boolean flag ) {
        ( (ImageView) rootView.findViewById(R.id.image4) ).setImageResource( flag ? R.drawable.icon_border_w_on : R.drawable.icon_border_w_off );
        ( (TextView) rootView.findViewById(R.id.text4) ).setTextColor( getTextColor(flag) );
    }
    private void setAdjustBrightnessButtonActivate( boolean flag ) {
        ( (ImageView) rootView.findViewById(R.id.image5) ).setImageResource( flag ? R.drawable.icon_bright_w_on : R.drawable.icon_bright_w_off );
        ( (TextView) rootView.findViewById(R.id.text5) ).setTextColor( getTextColor(flag) );

    }
    private void setShowDateButtonActivate( boolean flag ) {
        ( (ImageView) rootView.findViewById(R.id.image6) ).setImageResource( flag ? R.drawable.icon_date_w_on : R.drawable.icon_date_w_off );
        ( (TextView) rootView.findViewById(R.id.text6) ).setTextColor( getTextColor(flag) );
    }

    private OnClickListener paperFullButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = getCurrentIndex();
            PhotoPrintDataManager.getInstance().setPaperFull( index );
            initButtonStatus( index );
            refreshItem();
        }
    };
    private OnClickListener imageFullButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = getCurrentIndex();
            PhotoPrintDataManager.getInstance().setImageFull( index );
            initButtonStatus( index );
            refreshItem();
        }
    };
    private OnClickListener rotateButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = getCurrentIndex();
            PhotoPrintDataManager.getInstance().setRotate( index );
            initButtonStatus( index );
            refreshItem();
        }
    };
    private OnClickListener borderButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = getCurrentIndex();
            PhotoPrintDataManager.getInstance().toggleBorder( index );
            initButtonStatus( index );
            refreshItem();
        }
    };
    private OnClickListener brightbessButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = getCurrentIndex();
            PhotoPrintDataManager.getInstance().toggleAdjustBrightness( index );
            initButtonStatus( index );
            refreshItem();
        }
    };
    private OnClickListener dateButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = getCurrentIndex();
            PhotoPrintDataManager.getInstance().toggleShowDate( index );
            initButtonStatus( index );
            refreshItem();
        }
    };
    private OnClickListener nextButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            recyclerViewPager.scrollToPosition( recyclerViewPager.getCurrentPosition() + 1 );
        }
    };
    private OnClickListener prevButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            recyclerViewPager.scrollToPosition( recyclerViewPager.getCurrentPosition() - 1 );
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        if( !resourceCleared )
            clearResource();

        super.onDetachedFromWindow();
    }

    private void applyChanges() {
        PhotoPrintDataManager.getInstance().replaceDatas();
        finish();
        if( getContext() instanceof ActivityActionListener )
            ( (ActivityActionListener) getContext() ).refreshListItems();
    }

    public void cancelChanges() {
        if( PhotoPrintDataManager.getInstance().isChanged() ) {
            CustomizeDialog confirmDialog = new CustomizeDialog( getContext(), getContext().getString(R.string.do_not_save_then_move_to_list_page), R.string.confirm, R.string.cancel, new ICustomDialogListener() {

                @Override
                public void onClick(byte clickedOk) {
                    if(clickedOk == ICustomDialogListener.OK) {
                        finish();
                    }
                }
            }, null );
            confirmDialog.show();
        }
        else finish();
    }

}
