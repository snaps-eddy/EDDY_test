package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductNormalOptionItem extends SnapsProductNativeUIBaseResultJson {
    private static final String TAG = SnapsProductNormalOptionItem.class.getSimpleName();
    private static final long serialVersionUID = 983829653922731328L;
    @SerializedName("name")
    private String name;
    @SerializedName("cellType")
    private String cellType;
    @SerializedName("parameter")
    private String parameter;
    @SerializedName("placeHolder")
    private String placeHolder;
    @SerializedName("font")
    private String font;
    @SerializedName("maxWidth")
    private String maxWidth;

    @SerializedName("items")
    private List<SnapsProductNormalOptionItemValue> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public List<SnapsProductNormalOptionItemValue> getValues() {
        return items;
    }

    public void setValues(List<SnapsProductNormalOptionItemValue> values) {
        this.items = values;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(String maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getCellMaxWidthInteger() {
        try {
            if (!StringUtil.isEmpty(getMaxWidth()))
                return Integer.parseInt(getMaxWidth());
        } catch (NumberFormatException e) { Dlog.e(TAG, e); }
        return 0;
    }

    public boolean isEmpty() {
        if( items == null ) return true;

        ArrayList<SnapsProductNormalOptionItemValue> deleteList = new ArrayList<SnapsProductNormalOptionItemValue>();
        for( SnapsProductNormalOptionItemValue value : items ) {
            if( value.isEmpty() )
                deleteList.add( value );
        }

        if( deleteList.size() > 0 )
            items.removeAll( deleteList );

        if( items.isEmpty() )
            items = null;

        return StringUtil.isEmpty(name) && StringUtil.isEmpty(cellType) && StringUtil.isEmpty(parameter) && StringUtil.isEmpty(placeHolder) && items == null;
    }
}
