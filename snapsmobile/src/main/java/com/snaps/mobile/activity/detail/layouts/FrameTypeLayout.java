package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionBaseCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCommonValue;

import java.util.ArrayList;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class FrameTypeLayout extends LinkedLayout {
    public static final String FRAME_TYPE_INDEX = "frame_type_index";
    public static final String FRAME_TYPE = "frame_type";
    public static final String CUTTING_TYPE = "cuttingType";
    private ImageView[][] images;
    private SnapsProductOptionCell cellData;
    private int selectedIndex = -2;
    private String frame_type;
    private FrameTypeLayout(Context context) {
        super(context);
    }

    public static FrameTypeLayout createInstance(Context context, LayoutRequestReciever reciever, String type) {
        FrameTypeLayout instance = new FrameTypeLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        instance.frame_type = type;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductOptionCell) ) return;
        cellData = (SnapsProductOptionCell) data;
        ViewGroup container;
        if(CUTTING_TYPE.equals(frame_type)) {
            container = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.detail_layout_cutting_type, this);
        } else {
            container = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.detail_layout_frame_type, this);
        }

        ( (TextView) container.findViewById(R.id.title) ).setText( cellData.getName() );

        LinearLayout pageLayout = (LinearLayout) container.findViewById( R.id.frames );
        ViewGroup item;
        images = new ImageView[cellData.getValueList().size()][2];
        int resId, imageResId, textResId, checkResId;
        ImageView image, check;
        for( int i = 0; i < 3; ++i ) {
            resId = getResources().getIdentifier("item" + (i + 1), "id", getContext().getPackageName() );
            item = (RelativeLayout) pageLayout.findViewById( resId );
            if( i > cellData.getValueList().size() - 1 ) {
                item.setVisibility( View.GONE );
                continue;
            }
            //item.setVisibility( View.VISIBLE );
            item.setTag( i );
            imageResId = getResources().getIdentifier("frame_border" + (i + 1), "id", getContext().getPackageName() );
            textResId = getResources().getIdentifier("frame_text" + (i + 1), "id", getContext().getPackageName() );
            checkResId = getResources().getIdentifier("frame_check" + (i + 1), "id", getContext().getPackageName() );
            image = (ImageView) item.findViewById( imageResId );
            check = (ImageView) item.findViewById( checkResId );
            images[i] = new ImageView[]{ image, check };

            ( (TextView) item.findViewById(textResId)).setText( cellData.getValueList().get(i).getName() );

            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem( (Integer) v.getTag() );
                }
            });
        }
        parent.addView(this);
        int defaultIndex = -1;
        if(!CUTTING_TYPE.equals(frame_type)) {
            defaultIndex = StringUtil.isEmpty(cellData.getDefalutIndex()) ? 0 : Integer.parseInt(cellData.getDefalutIndex());
            if (reciever != null) {
                String indexStr = reciever.getSelectedValue(FRAME_TYPE_INDEX);
                if (!StringUtil.isEmpty(indexStr))
                    defaultIndex = Integer.parseInt(indexStr);

                if (defaultIndex > cellData.getValueList().size() - 1)
                    defaultIndex = 0;
            }


        }
        selectItem(defaultIndex);
    }

    private void selectItem( int index ) {
        if( index == selectedIndex ) return;
        selectedIndex = index;

        for( int i = 0; i < images.length; ++i ) {
            if(images[i] != null ) {
                if(images[i][0] != null) images[i][0].setVisibility(i == selectedIndex ? View.VISIBLE : View.GONE);
                if(images[i][1] != null) images[i][1].setVisibility(i == selectedIndex ? View.VISIBLE : View.GONE);
            }
        }

        if( reciever != null ) {
            if( tailViewId != 0 )
                reciever.removeLayout( tailViewId );

            reciever.itemSelected( FRAME_TYPE_INDEX, selectedIndex < 0 ? "" : "" + selectedIndex, true );

            SnapsProductOptionCommonValue value = cellData.getValueList().get( (selectedIndex < 0 ? 0 : selectedIndex) );
            SnapsProductOptionBaseCell cell = value.getChildControl();
            if( cell != null )
                tailViewId = reciever.createNextLayout( (ViewGroup)getParent(), cell.getCellType(), cell, id );
            else {
                if( value.getPrice() != null )
                    reciever.removeLayout( tailViewId );
                else
                    setBottomLineVisibility( false );
                tailViewId = reciever.itemSelected( value, (ViewGroup)getParent(), id );
            }
        }
    }
}
