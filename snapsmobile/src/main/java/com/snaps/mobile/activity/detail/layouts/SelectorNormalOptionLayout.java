package com.snaps.mobile.activity.detail.layouts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductNormalOptionItem;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductNormalOptionItemValue;

import java.util.List;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class SelectorNormalOptionLayout extends LinkedLayout {
    private int selectedIndex = -1;
    private List<SnapsProductNormalOptionItemValue> items;
    private String parameter;

    private SelectorNormalOptionLayout(Context context) {
        super(context);
    }

    public static SelectorNormalOptionLayout createInstance(Context context, LayoutRequestReciever reciever) {
        SelectorNormalOptionLayout instance = new SelectorNormalOptionLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductNormalOptionItem) ) return;
        SnapsProductNormalOptionItem cellData = (SnapsProductNormalOptionItem) data;

        items = cellData.getValues();
        parameter = cellData.getParameter();

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

        selectItem( 0 ); // normal option에는 default value값이 없으므로 0번.
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
            reciever.itemSelected( parameter, items.get(index).getValue(), true );
        }
    }
}
