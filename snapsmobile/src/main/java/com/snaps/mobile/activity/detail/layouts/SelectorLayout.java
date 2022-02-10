package com.snaps.mobile.activity.detail.layouts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionBaseCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCommonValue;

import java.util.List;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class SelectorLayout extends LinkedLayout {
    private int selectedIndex = -1;
    private List<SnapsProductOptionCommonValue> items;

    private SelectorLayout(Context context) {
        super(context);
    }

    public static SelectorLayout createInstance(Context context, LayoutRequestReciever reciever) {
        SelectorLayout instance = new SelectorLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductOptionCell) ) return;
        SnapsProductOptionCell cellData = (SnapsProductOptionCell) data;

        items = cellData.getValueList();
        if(items.size() <= 1) {
            for(SnapsProductOptionCommonValue item : items) {
                if(TextUtils.isEmpty(item.getName())) {
                    setVisibility(View.GONE);
                }
            }
        }
        ViewGroup container = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.detail_layout_selector, null);
        ( (TextView) container.findViewById(R.id.title) ).setText( cellData.getName() );
        ( container.findViewById(R.id.content_layout) ).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

                showSelectItemDialog();
            }
        } );

        addView(container);
        parent.addView(this);

        int index = StringUtil.isEmpty( cellData.getDefalutIndex() ) ? 0 : Integer.parseInt( cellData.getDefalutIndex() );
        if( index < 0 ) index = 0;
        selectItem( index );
    }

    private void showSelectItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        String[] itemStrs = new String[items.size()];
        for( int i = 0; i < items.size(); ++i ) itemStrs[i] = items.get(i).getName();

        builder.setSingleChoiceItems(itemStrs, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog, int which){
                dialog.dismiss();
                selectItem( which );
            }
        } );
        builder.setCancelable( true );
        builder.create().show();
    }

    private void selectItem( int index ) {
        if( index == selectedIndex ) return;
        selectedIndex = index;

        ( (TextView) findViewById(R.id.content) ).setText( items.get(index).getName() );

        if( reciever != null ) {
            if( tailViewId != 0 )
                reciever.removeLayout( tailViewId );

            SnapsProductOptionCommonValue value = items.get( (selectedIndex < 0 ? 0 : selectedIndex) );
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
