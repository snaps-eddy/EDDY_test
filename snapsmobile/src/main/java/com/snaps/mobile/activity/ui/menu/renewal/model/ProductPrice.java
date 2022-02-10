package com.snaps.mobile.activity.ui.menu.renewal.model;

import com.google.gson.JsonObject;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;

/**
 * Created by songhw on 2016. 7. 27..
 */
public class ProductPrice extends Text {
    private String priceKey;
    private String[] priceColors;
    public ProductPrice( JsonObject jsonObject ) {
        super( jsonObject, TYPE_PROD_PRICE );

        priceKey = jsonObject.get("priceKey").getAsString();
        String colorStr = jsonObject.get( "attribute" ).getAsJsonObject().get("color").getAsString();
        if( !StringUtil.isEmpty(colorStr) ) {
            if ( colorStr.contains(MenuDataManager.SEPARATOR_STRING.replace("\\","")) )
                priceColors = colorStr.split( MenuDataManager.SEPARATOR_STRING );
            else
                priceColors = new String[]{ colorStr };

            for( int i = 0; i < priceColors.length; ++i )
                priceColors[i] = priceColors[i].contains("#") ? priceColors[i] : "#" + priceColors[i];
        }
    }

    public ProductPrice clone() {
        ProductPrice instance = (ProductPrice) super.clone();
        instance.priceKey = priceKey;
        instance.priceColors = priceColors;
        return instance;
    }

    /**
     * getters
     */
    public String getPriceKey() { return priceKey; }
    public String[] getPriceColors() { return priceColors; }
}
