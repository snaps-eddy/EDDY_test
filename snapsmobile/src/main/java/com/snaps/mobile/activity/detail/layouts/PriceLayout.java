package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionPrice;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class PriceLayout extends LinkedLayout {
    private SnapsProductOptionPrice cellData;
    private static final String ACCESSORY_PRICE = "accessory_price";
    private PriceLayout(Context context) {
        super(context);
    }

    public static PriceLayout createInstance(Context context, LayoutRequestReciever reciever) {
        PriceLayout instance = new PriceLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductOptionPrice) ) return;
        cellData = (SnapsProductOptionPrice) data;

        final ViewGroup container = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.detail_layout_price, this);
        ( (TextView) container.findViewById(R.id.title) ).setText( cellData.getName() );

        TextView oriPriceTv = (TextView) container.findViewById( R.id.ori_price );
        TextView newPriceTv = (TextView) container.findViewById( R.id.new_price );
        StringBuilder newPriceStr = new StringBuilder();
        if( !cellData.getValues().getPrice().equalsIgnoreCase(cellData.getValues().getDiscountPrice()) && !StringUtil.isEmpty(cellData.getValues().getDiscountPrice()) && !StringUtil.isEmpty(cellData.getValues().getSalePerscent()) ) {
            newPriceStr.append( cellData.getValues().getSalePerscent() ).append(" ");
            if( !StringUtil.isEmpty(cellData.getValues().getDiscountPrice()) ) {
                oriPriceTv.setVisibility(View.VISIBLE);
                oriPriceTv.setText( cellData.getValues().getPrice() );
                oriPriceTv.setPaintFlags(oriPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                newPriceStr.append( cellData.getValues().getDiscountPrice() );
            }
        }
        else {
            oriPriceTv.setVisibility( View.GONE );
            newPriceStr.append( cellData.getValues().getPrice() );
        }
        newPriceTv.setText(newPriceStr.toString());

        if(ACCESSORY_PRICE.equals(cellData.getCellType())) {
            accessoryInfo(container);
        } else {
            cardInfo(container);
        }


        parent.addView(this);
    }

    private void cardInfo(ViewGroup container) {
        final RelativeLayout infoLayout = (RelativeLayout) container.findViewById( R.id.info_layout );
        if( !StringUtil.isEmpty(cellData.getLink()) && !StringUtil.isEmpty(cellData.getLinkText()) ) {
            infoLayout.setVisibility(View.VISIBLE);
            ( container.findViewById(R.id.info_button) ).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reciever != null)
                        reciever.openUrl( cellData.getLink(), false );
                }
            });

            final TextView infoContent = (TextView) container.findViewById( R.id.info_content );
            infoContent.setText( cellData.getLinkText() );
            infoContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < 16)
                        infoContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    else
                        infoContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if( infoContent.getLineCount() > 1 ) {
                        ViewGroup.LayoutParams params = infoLayout.getLayoutParams();
                        params.height = UIUtil.convertDPtoPX( getContext(), 52 );
                        infoLayout.setLayoutParams( params );
                    }
                }
            });
        }
        else
            infoLayout.setVisibility(View.GONE);

        ( container.findViewById(R.id.bottom_space) ).setVisibility( infoLayout.getVisibility() );
    }

    private void accessoryInfo(ViewGroup container) {
         container.findViewById( R.id.info_layout ).setVisibility(View.GONE);
         RelativeLayout accessoryLayout = (RelativeLayout) container.findViewById( R.id.accessory_layout );
        if( !StringUtil.isEmpty(cellData.getInfoText())) {
            accessoryLayout.setVisibility(View.VISIBLE);
            final TextView infoContent = (TextView) container.findViewById(R.id.accessory_content);
            String [] tempStr = cellData.getInfoText().split("[|]");
            int color = Color.parseColor("#f4706c");
            SpannableStringBuilder builder = new SpannableStringBuilder(tempStr[0]);
            int startIndex = tempStr[0].indexOf(tempStr[1]);
            int lastIndex = startIndex+tempStr[1].length();
            builder.setSpan(new ForegroundColorSpan(color), startIndex, lastIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            infoContent.append(builder);
           // infoContent.setText(cellData.getInfoText());
        } else {
            accessoryLayout.setVisibility(View.GONE);
        }

        ( container.findViewById(R.id.bottom_space) ).setVisibility( accessoryLayout.getVisibility() );

    }
}
