package com.snaps.mobile.activity.ui.menu.renewal.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsMaxPageInfo;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.photoprint.PhotoPrintProductInfo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by songhw on 2016. 9. 22..
 */
public class MenuData implements Parcelable, Serializable {
    private static final String SAVE_FILE_NAME = "MenuData";
    private static final long serialVersionUID = -4516205531958357837L;

    public ArrayList<Category> categories;
    public ArrayList<PhotoPrintProductInfo> photoPrintProductInfoArray;
    private ArrayList<CustomPair> subCategoryArray; // SubCategory
    private ArrayList<CustomPair> layoutArray; // Layout
    private ArrayList<CustomPair> homeValueArray; // Value
    private ArrayList<CustomPair> menuValueArray; // Value

    public HashMap<String, SubCategory> subCategoryMap;
    public HashMap<String, Layout> layoutMap;
    public HashMap<String, Value> homeValueMap;
    public HashMap<String, Value> menuValueMap;


    public SnapsMaxPageInfo maxPageInfo;

    public String categoryVersion, subCategoryVersion, layoutVersion, homeValueVersion, menuValueVersion, spineInfoVersion, photoPrintVersion;

    public Menu menuCrmLogin, menuCrmLogout;
    public int crmIdx = -1, deliveryIdx = -1;
    public boolean isExistHomeMenu = false;

    public MenuData() {}

    protected MenuData(Parcel in) {
        categories = in.createTypedArrayList(Category.CREATOR);
        photoPrintProductInfoArray = in.createTypedArrayList(PhotoPrintProductInfo.CREATOR);
        subCategoryArray = in.createTypedArrayList(CustomPair.CREATOR);
        layoutArray = in.createTypedArrayList(CustomPair.CREATOR);
        homeValueArray = in.createTypedArrayList(CustomPair.CREATOR);
        menuValueArray = in.createTypedArrayList(CustomPair.CREATOR);
        maxPageInfo = in.readParcelable(SnapsMaxPageInfo.class.getClassLoader());
        categoryVersion = in.readString();
        subCategoryVersion = in.readString();
        layoutVersion = in.readString();
        homeValueVersion = in.readString();
        menuValueVersion = in.readString();
        spineInfoVersion = in.readString();
        photoPrintVersion = in.readString();
        menuCrmLogin = in.readParcelable(Menu.class.getClassLoader());
        menuCrmLogout = in.readParcelable(Menu.class.getClassLoader());
        crmIdx = in.readInt();
        deliveryIdx = in.readInt();
        isExistHomeMenu = in.readByte() != 0;

        arrayToMap();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mapToArray();

        dest.writeTypedList(categories);
        dest.writeTypedList(photoPrintProductInfoArray);
        dest.writeTypedList(subCategoryArray);
        dest.writeTypedList(layoutArray);
        dest.writeTypedList(homeValueArray);
        dest.writeTypedList(menuValueArray);
        dest.writeParcelable(maxPageInfo, flags);
        dest.writeString(categoryVersion);
        dest.writeString(subCategoryVersion);
        dest.writeString(layoutVersion);
        dest.writeString(homeValueVersion);
        dest.writeString(menuValueVersion);
        dest.writeString(spineInfoVersion);
        dest.writeString(photoPrintVersion);
        dest.writeParcelable(menuCrmLogin, flags);
        dest.writeParcelable(menuCrmLogout, flags);
        dest.writeInt(crmIdx);
        dest.writeInt(deliveryIdx);
        dest.writeByte((byte) (isExistHomeMenu ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MenuData> CREATOR = new Creator<MenuData>() {
        @Override
        public MenuData createFromParcel(Parcel in) {
            return new MenuData(in);
        }

        @Override
        public MenuData[] newArray(int size) {
            return new MenuData[size];
        }
    };

    private void mapToArray() {
        if( subCategoryMap != null ) {
            subCategoryArray = new ArrayList<CustomPair>();
            Set<Map.Entry<String, SubCategory>> entrySet = subCategoryMap.entrySet();
            for( Map.Entry<String, SubCategory> entry : entrySet )
                subCategoryArray.add( new CustomPair(entry.getKey(), entry.getValue()) );
        }

        if( layoutMap != null ) {
            layoutArray = new ArrayList<CustomPair>();
            Set<Map.Entry<String, Layout>> entrySet = layoutMap.entrySet();
            for( Map.Entry<String, Layout> entry : entrySet )
                layoutArray.add(new CustomPair(entry.getKey(), entry.getValue()));
        }

        if( homeValueMap != null ) {
            homeValueArray = new ArrayList<CustomPair>();
            Set<Map.Entry<String, Value>> entrySet = homeValueMap.entrySet();
            for( Map.Entry<String, Value> entry : entrySet )
                homeValueArray.add(new CustomPair(entry.getKey(), entry.getValue()));
        }

        if( menuValueMap != null ) {
            menuValueArray = new ArrayList<CustomPair>();
            Set<Map.Entry<String, Value>> entrySet = menuValueMap.entrySet();
            for( Map.Entry<String, Value> entry : entrySet )
                menuValueArray.add(new CustomPair(entry.getKey(), entry.getValue()));
        }
    }

    private void arrayToMap() {
        Object obj;

        if( subCategoryArray != null ) {
            subCategoryMap = new HashMap<String, SubCategory>();
            for( CustomPair pair: subCategoryArray ) {
                if( !StringUtil.isEmpty(pair.key) && pair.subCategory != null )
                    subCategoryMap.put( pair.key, pair.subCategory );
            }
        }

        if( layoutArray != null ) {
            layoutMap = new HashMap<String, Layout>();
            for( CustomPair pair: layoutArray ) {
                if( !StringUtil.isEmpty(pair.key) && pair.layout != null )
                    layoutMap.put( pair.key, pair.layout );
            }
        }

        if( homeValueArray != null ) {
            homeValueMap = new HashMap<String, Value>();
            for( CustomPair pair: homeValueArray ) {
                if( !StringUtil.isEmpty(pair.key) && pair.value != null )
                    homeValueMap.put( pair.key, pair.value );
            }
        }

        if( menuValueArray != null ) {
            menuValueMap = new HashMap<String, Value>();
            for( CustomPair pair: menuValueArray ) {
                if( !StringUtil.isEmpty(pair.key) && pair.value != null )
                    menuValueMap.put( pair.key, pair.value );
            }
        }
    }

    public static File getSaveTargetFile( Context context ) {
        return new File( Const_VALUE.PATH_PACKAGE(context, false) + File.separator + SAVE_FILE_NAME );
    }
}
