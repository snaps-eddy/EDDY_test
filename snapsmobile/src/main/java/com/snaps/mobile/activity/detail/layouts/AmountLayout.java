package com.snaps.mobile.activity.detail.layouts;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICommonConfirmListener;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.component.SnapsNumberPicker;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionBaseCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCommonValue;

import font.FTextView;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class AmountLayout extends LinkedLayout {
    private static final String TAG = AmountLayout.class.getSimpleName();

    private AmountLayout(Context context) {
        super(context);
    }
    private int amount = -1;
    private FTextView input;
    private SnapsProductOptionCell cellData;
    private Dialog amountPicker;

    public static AmountLayout createInstance(Context context, LayoutRequestReciever reciever) {
        AmountLayout instance = new AmountLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductOptionCell) ) return;
        cellData = (SnapsProductOptionCell) data;

        ViewGroup container = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.detail_layout_amount, null);
        ( (TextView) container.findViewById(R.id.title) ).setText( cellData.getName() );

        ( container.findViewById(R.id.button_plus) ).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAmount( amount + 1 );
            }
        });
        ( container.findViewById(R.id.button_minus) ).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAmount( amount - 1 );
            }
        });

        input = (FTextView) container.findViewById( R.id.content );
        input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showCountPickerDialog();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });

        addView(container);
        parent.addView(this);

        changeAmount( cellData.getMin() );
    }

    @Override
    public void destroy() {
        super.destroy();
        if (amountPicker != null && amountPicker.isShowing()) {
            amountPicker.dismiss();
            amountPicker = null;
        }
    }

    private void showCountPickerDialog() throws Exception {
        if (amountPicker != null && amountPicker.isShowing()) {
            return;
        }

        amountPicker = PhotobookCommonUtils.createCountPickerDialog(getContext(), amount, cellData.getMax(), new ICommonConfirmListener() {
            @Override
            public void onConfirmed() {
                SnapsNumberPicker numberPicker = (SnapsNumberPicker) amountPicker.findViewById(R.id.edit_activity_thumbnail_view_counter_picker_dialog_number_picker);
                int selectedQuantity = numberPicker.getValue();
                changeAmount(selectedQuantity);

                if (amountPicker != null)
                    amountPicker.dismiss();
            }
        });
        amountPicker.show();
    }

    private void changeAmount( int newAmount) {
        amount = newAmount;
        if( amount < cellData.getMin() ) amount = cellData.getMin();
        else if( amount > cellData.getMax() ) amount = cellData.getMax();

        input.setText( ("" + amount) );

        if( reciever != null ) {
            reciever.itemSelected(cellData.getParameter(), "" + amount, true);

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
