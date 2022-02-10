package com.snaps.mobile.activity.ui.menu.renewal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by songhw on 2016. 7. 26..
 */
public class Value implements Serializable, Parcelable {
    private static final long serialVersionUID = 3154037026672611616L;
    private HashMap<String, String> values; // String, String
    private HashMap<String, Value> subValues; // String, Value
    private ArrayList<CustomPair> valuePairs;
    private ArrayList<CustomPair> subValuePairs;

    public Value(JsonObject jsonObject) {
        Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        values = new HashMap<String, String>();
        subValues = new HashMap<String, Value>();
        for( Map.Entry<String, JsonElement> entry : entrySet ) {
            if( entry.getValue().isJsonObject() ) subValues.put( entry.getKey(), new Value(entry.getValue().getAsJsonObject()) );
            else values.put( entry.getKey(), entry.getValue().getAsString() );
        }
    }

    public Value( HashMap<String, String> values ) {
        this.values = values;
    }

    private void mapToArray() {
        if( values != null ) {
            valuePairs = new ArrayList<CustomPair>();
            Set<Map.Entry<String, String>> entrySet = values.entrySet();
            for( Map.Entry<String, String> entry : entrySet )
                valuePairs.add( new CustomPair(entry.getKey(), entry.getValue()) );
        }

        if( subValues != null ) {
            subValuePairs = new ArrayList<CustomPair>();
            Set<Map.Entry<String, Value>> entrySet = subValues.entrySet();
            for( Map.Entry<String, Value> entry : entrySet )
                subValuePairs.add(new CustomPair(entry.getKey(), entry.getValue()));
        }
    }

    private void arrayToMap() {
        Object obj;

        if( valuePairs != null ) {
            values = new HashMap<String, String>();
            for( CustomPair pair: valuePairs ) {
                if( !StringUtil.isEmpty(pair.key) && !StringUtil.isEmpty(pair.valueStr) )
                    values.put( pair.key, pair.valueStr );
            }
        }

        if( subValuePairs != null ) {
            subValues = new HashMap<String, Value>();
            for( CustomPair pair: subValuePairs ) {
                if( !StringUtil.isEmpty(pair.key) && pair.value != null )
                    subValues.put( pair.key, pair.value );
            }
        }
    }


    protected Value(Parcel in) {
        valuePairs = in.createTypedArrayList(CustomPair.CREATOR);
        subValuePairs = in.createTypedArrayList(CustomPair.CREATOR);

        arrayToMap();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mapToArray();

        dest.writeTypedList(valuePairs);
        dest.writeTypedList(subValuePairs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Value> CREATOR = new Creator<Value>() {
        @Override
        public Value createFromParcel(Parcel in) {
            return new Value(in);
        }

        @Override
        public Value[] newArray(int size) {
            return new Value[size];
        }
    };

    public boolean has( String key ) { return values.containsKey(key); }

    public void addData( String key, String value ) {
        values.put( key, value );
    }

    /**
     * getters
     */
    public String[] getStringValues( String key ) {
        String valueStr = values.get(key);
        return StringUtil.isEmpty(valueStr) ? new String[0] : valueStr.split( MenuDataManager.SEPARATOR_STRING );
    }

    public String getStringValue( String key ) { return getStringValue(key, 0); }
    public String getStringValue( String key, int index ) {
        String valueStr = values.get( key );
        if( !StringUtil.isEmpty(valueStr) && valueStr.contains(MenuDataManager.SEPARATOR_STRING) ) {
            String[] strAry = valueStr.split( MenuDataManager.SEPARATOR_STRING );
            if( strAry.length > index ) valueStr = strAry[index];
        }
        return valueStr;
    }
    public Value getSubValue( String key ) { return subValues.get( key ); }

    /**
     * setters
     */
    public void addSubData( String key, Value value ) { this.subValues.put( key, value ); }
    public void clearSubData() { this.subValues.clear(); }

}
