package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class TitleLayout extends LinkedLayout {
    private TitleLayout(Context context) {
        super(context);
    }

    public static TitleLayout createInstance(Context context, LayoutRequestReciever reciever) {
        TitleLayout instance = new TitleLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof String) ) return;

        LayoutInflater inflater = LayoutInflater.from( getContext() );
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.detail_layout_title, this);
        ( (TextView) container.findViewById(R.id.title) ).setText( (String) data );
        LinearLayout contentLayout = (LinearLayout) container.findViewById( R.id.container );

        parent.addView(this);
    }
}
