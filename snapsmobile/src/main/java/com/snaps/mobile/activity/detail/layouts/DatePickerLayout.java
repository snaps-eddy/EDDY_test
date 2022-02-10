package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaps.common.data.interfaces.DatePickerSelectListener;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductNormalOptionItem;
import com.snaps.mobile.utils.text_animation.animatetext.AnvilText;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class DatePickerLayout extends LinkedLayout {
    private static final String TAG = DatePickerLayout.class.getSimpleName();

    private SnapsProductNormalOptionItem cellData;
    private String attributeName;

    private DatePickerLayout(Context context) {
        super(context);
    }

    public static DatePickerLayout createInstance(Context context, LayoutRequestReciever reciever) {
        DatePickerLayout instance = new DatePickerLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductNormalOptionItem) ) return;
        cellData = (SnapsProductNormalOptionItem) data;

        ViewGroup container = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.detail_layout_selector, null);
        ( (TextView) container.findViewById(R.id.title) ).setText( cellData.getName() );
        ( container.findViewById(R.id.content_layout) ).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        attributeName = cellData.getParameter();

        addView(container);
        parent.addView(this);

        if( reciever != null )
            reciever.itemSelected( attributeName, "", true );
    }

    private void showDatePickerDialog() {
        try {
            DateUtil.showDatePickerDialog(getContext(), new DatePickerSelectListener() {
                @Override
                public void onDateSelected(int year, int monthOfYear, int dayOfMonth) {
                    String yearStr = "" + year;
                    String monthStr = "" + (monthOfYear + 1);
                    String dayStr = "" + dayOfMonth;

                    ( (TextView) findViewById(R.id.content) ).setText( String.format(getContext().getString(com.snaps.common.R.string.year_month_day_string), yearStr, monthStr, dayStr) );
                    if( reciever != null )
                        reciever.itemSelected( attributeName, String.format("%04d%02d%02d",year,monthOfYear+1,dayOfMonth), true );
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
