package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductPremium;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductPremiumItem;

import java.util.List;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class PremiumLayout extends LinkedLayout {
    public static final String LABEL_NOTICE = "";
    public static final String LABEL_PREMIUM = "";
    public static final String LABEL_SALE = "";
    private PremiumLayout(Context context) {
        super(context);
    }

    public static PremiumLayout createInstance(Context context, LayoutRequestReciever reciever) {
        PremiumLayout instance = new PremiumLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductPremium) ) return;
        SnapsProductPremium premium = (SnapsProductPremium) data;

        List<SnapsProductPremiumItem> items = premium.getItems();
        if (items == null || items.isEmpty()) return;

        LayoutInflater inflater = LayoutInflater.from( getContext() );
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.detail_layout_premium, this);

        SnapsProductPremiumItem premiumItem = items.get(0);
        if (premiumItem != null) {
            if("label_notice".equals(premiumItem.getCellType())) {
                container.findViewById(R.id.imageViewNoti).setVisibility(View.VISIBLE);
                container.findViewById(R.id.imagViewPremium).setVisibility(View.GONE);
            } else if("label_kc".equals(premiumItem.getCellType())) {
                container.findViewById(R.id.imageViewNoti).setVisibility(View.GONE);
                container.findViewById(R.id.imagViewPremium).setVisibility(View.GONE);
                container.findViewById(R.id.imageViewKc).setVisibility(View.VISIBLE);
            }
            String text = premiumItem.getValue();
            if (!StringUtil.isEmpty(text))
                ( (TextView) container.findViewById(R.id.title)).setText(text);
        }

        parent.addView(this);
    }
}
