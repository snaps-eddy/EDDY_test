package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductDetailItem;

import java.util.List;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class InfoLayout extends LinkedLayout {
    private SnapsProductDetailItem cellData;

    private InfoLayout(Context context) {
        super(context);
    }

    public static InfoLayout createInstance(Context context, LayoutRequestReciever reciever) {
        InfoLayout instance = new InfoLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductDetailItem) ) return;
        cellData = (SnapsProductDetailItem) data;

        List<String> contents = cellData.getValues();

        LayoutInflater inflater = LayoutInflater.from( getContext() );
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.detail_layout_info, this);
        ( (TextView) container.findViewById(R.id.title) ).setText( cellData.getName() );
        LinearLayout contentLayout = (LinearLayout) container.findViewById( R.id.container );
        LinearLayout item;
        for( int i = 0; i < contents.size(); ++i ) {
            item = (LinearLayout) inflater.inflate( R.layout.detail_layout_info_item, null );
            ( (TextView)item.findViewById(R.id.content) ).setText( contents.get(i) );
            contentLayout.addView( item );
        }

        parent.addView(this);
    }
}
