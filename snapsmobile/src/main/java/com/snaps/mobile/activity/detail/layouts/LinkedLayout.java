package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;

/**
 * Created by songhw on 2016. 10. 19..
 */
public abstract class LinkedLayout extends LinearLayout {
    protected enum Type {
        Thumbnail, Selector, Date, SetAmount, InputString, Display, Color, PageType, Frame, Price, Detail, Info
    }

    protected Object data;

    protected int id = 0, headViewId = 0, tailViewId = 0;
    protected Type type;
    protected LayoutRequestReciever reciever;

    protected LinkedLayout(Context context) {
        super(context);
    }

    public abstract void draw( ViewGroup parent, Object data, int headViewId, int id );

    public void setBottomLineVisibility( boolean flag ) {
        View v = findViewById( R.id.bottom_line );
        if( v != null )
            v.setVisibility( flag ? View.VISIBLE : View.GONE );
    }

    public void destroy() {
        if( reciever != null && tailViewId != 0 )
            reciever.removeLayout( tailViewId );

        data = null;
        id = 0;
        headViewId = 0;
        tailViewId = 0;
        type = null;
        reciever = null;
    }


    /**
     * getters
     */
    @Override
    public int getId() { return id; }
    public Type getType() { return type; }
}
