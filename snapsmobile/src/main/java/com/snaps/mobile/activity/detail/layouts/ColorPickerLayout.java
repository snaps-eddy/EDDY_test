package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionBaseCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCommonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class ColorPickerLayout extends LinkedLayout {
    public static final String LEATHER_COLOR_INDEX = "leather_color_index";

    private int selectedIndex = -2;
    private ArrayList<ImageView> colorItems;
    private final int[][] images = new int[][]{
            { R.drawable.icon_color_darkbrown, R.drawable.icon_color_darkbrown_select },
            { R.drawable.icon_color_lightbrown, R.drawable.icon_color_lightbrown_select },
            { R.drawable.icon_color_red, R.drawable.icon_color_red_select },
            { R.drawable.icon_color_gray, R.drawable.icon_color_gray_select },
            { R.drawable.icon_color_black, R.drawable.icon_color_black_select },
            { R.drawable.icon_color_emerald, R.drawable.icon_color_emerald_select }
    };
    public static final int COLOR_PICKER_NAME_RED_ID = R.string.cover_color;
    public static final int[] COLOR_STRING_RES_ID = new int[] {
            R.string.leather_cover_dark_brown,
            R.string.leather_cover_light_brown,
            R.string.leather_cover_red,
            R.string.leather_cover_grey,
            R.string.leather_cover_black,
            R.string.leather_cover_emerald
    };
    private SnapsProductOptionCell cellData;


    private ColorPickerLayout(Context context) {
        super(context);
    }

    public static ColorPickerLayout createInstance(Context context, LayoutRequestReciever reciever) {
        ColorPickerLayout instance = new ColorPickerLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductOptionCell) ) return;
        cellData = (SnapsProductOptionCell) data;

        LayoutInflater inflater = LayoutInflater.from( getContext() );
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.detail_layout_color_picker, this);
        ( (TextView) container.findViewById(R.id.title) ).setText( cellData.getName() );

        LinearLayout colorLayout = (LinearLayout) container.findViewById( R.id.colors );
        ViewGroup item;
        colorItems = new ArrayList<ImageView>();
        int resId, imageResId, textResId;
        ImageView image;
        for( int i = 0; i < images.length; ++i ) {
            resId = getResources().getIdentifier("item" + (i + 1), "id", getContext().getPackageName() );
            item = (RelativeLayout) colorLayout.findViewById( resId );
            item.setTag( i );

            imageResId = getResources().getIdentifier("color_image" + (i + 1), "id", getContext().getPackageName() );
            textResId = getResources().getIdentifier("color_text" + (i + 1), "id", getContext().getPackageName() );
            image = (ImageView) item.findViewById( imageResId );
            image.setImageResource(images[i][0]);
            colorItems.add( image );

            ( (TextView) item.findViewById(textResId)).setText( cellData.getValueList().get(i).getName() );

            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem( (Integer) v.getTag() );
                }
            });
        }

        parent.addView(this);

        int defaultIndex = StringUtil.isEmpty( cellData.getDefalutIndex() ) ? 0 : Integer.parseInt( cellData.getDefalutIndex() );
        if( reciever != null ) {
            String indexStr = reciever.getSelectedValue( LEATHER_COLOR_INDEX );
            if( !StringUtil.isEmpty(indexStr) )
                defaultIndex = Integer.parseInt( indexStr );

            if( defaultIndex > cellData.getValueList().size() - 1 )
                defaultIndex = 0;
        }
        selectItem( defaultIndex );
    }

    private void selectItem( int index ) {
        if( index == selectedIndex ) return;
        selectedIndex = index;

        for( int i = 0; i < colorItems.size(); ++i )
            colorItems.get(i).setImageResource( images[i][i == selectedIndex ? 1 : 0] );

        if( reciever != null ) {
            if( tailViewId != 0 )
                reciever.removeLayout( tailViewId );

            reciever.itemSelected( LEATHER_COLOR_INDEX, selectedIndex < 0 ? "" : "" + selectedIndex, true );

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
