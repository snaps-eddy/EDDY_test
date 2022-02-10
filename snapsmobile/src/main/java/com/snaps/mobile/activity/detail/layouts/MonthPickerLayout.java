package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaps.common.data.interfaces.DateMonthPickerSelectListener;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionBaseCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCommonValue;

import java.util.Calendar;

import static com.snaps.common.utils.system.DateUtil.CALENDAR_MONTH_DEFAULT_INDEX;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class MonthPickerLayout extends LinkedLayout {
    private static final String TAG = MonthPickerLayout.class.getSimpleName();

    private SnapsProductOptionCell cellData;
    private int selectedIndex = CALENDAR_MONTH_DEFAULT_INDEX;
    private Calendar calendar;

    private MonthPickerLayout(Context context) {
        super(context);
    }

    public static MonthPickerLayout createInstance(Context context, LayoutRequestReciever reciever) {
        MonthPickerLayout instance = new MonthPickerLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductOptionCell) ) return;
        cellData = (SnapsProductOptionCell) data;
        calendar = Calendar.getInstance();
        String[] items;
        try {
            items = DateUtil.createDateRangeItem(getContext());
        } catch (Exception e) {
            Dlog.e(TAG, e);
            //예전에 쓰던 방식인데 혹시나 해서..
            items = new String[13];
            for( int i = 0; i < 13; ++i )
                items[i] = i == 0 ? cellData.getValueList().get(0).getName() : getYearMonthString( i - 1 );
        }

        ViewGroup container = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.detail_layout_selector, null);
        ( (TextView) container.findViewById(R.id.title) ).setText( cellData.getName() );
        ( container.findViewById(R.id.content_layout) ).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerDialog();
            }
        });

        addView(container);
        parent.addView(this);

        selectItem( selectedIndex, getContext().getString(R.string.selectMonthStart));
    }

    private void showPickerDialog() {
        DateUtil.showDateMonthPickerDialog(getContext(), selectedIndex, new DateMonthPickerSelectListener() {
            @Override
            public void onDateMonthSelected(int index, String text) {
                try {
                    selectItem( index, text );
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    /**
     *
     * @param index 1 ~ 13
     * @return
     */
    private int[] getYearMonth( int index ) {
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH ) + 1;

        month = month + index;
        if( month > 12 ) {
            month = month - 12;
            year ++;
        }

        return new int[]{ year, month };
    }

    private String getYearMonthString( int index ) {
        int[] yearMonth = getYearMonth( index );
        return yearMonth[0] + "-" + (yearMonth[1] > 9 ? yearMonth[1] : "0" + yearMonth[1]);
    }

    private void selectItem( int index, String text ) {
        selectedIndex = index;

        ( (TextView) findViewById(R.id.content) ).setText( text );

        if( reciever != null ) {
            String[] yearMonth = selectedIndex == CALENDAR_MONTH_DEFAULT_INDEX ? new String[]{"", ""} : text.split( "-" );
            reciever.itemSelected( "year", yearMonth[0], true );
            reciever.itemSelected( "month", yearMonth[1], true );

            SnapsProductOptionCommonValue value = cellData.getValueList().get(0);
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
