package com.snaps.mobile.product_native_ui.json.detail;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.mobile.activity.detail.layouts.ColorPickerLayout;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsProductOptionCellConstants;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;
import com.snaps.mobile.product_native_ui.string_switch.SnapsProductOptionBaseCellParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public abstract class SnapsProductOptionBaseCell extends SnapsProductNativeUIBaseResultJson implements ISnapsProductOptionCellConstants {

    private static final long serialVersionUID = -4888060173105503127L;
    protected String title;

    protected String name;

    protected String innerPaperKind;

    protected int max;

    protected int min;

    protected String parameter;

//    protected int defalutIndex;
    // null case를 추가하기 위해 String으로 변경
    // 0 ~ ? : 해당 인덱스, null이나 ""일땐 0번 선택, -1일땐 0번 child를 선택하여 하위 데이터를 가져오지만, 비선택 상태로 둔다.
    protected String defalutIndex;

    protected String cellType;

    protected int maxCount, minCount;

    private List<SnapsProductOptionCommonValue> valueList;

    public SnapsProductOptionBaseCell() {}

    public SnapsProductOptionBaseCell(List<LinkedTreeMap> values, LinkedTreeMap myDatas) {

        setBaseDatas(myDatas);

        setValues(values);
    }

    protected void setBaseDatas(LinkedTreeMap myDatas) {
        if (myDatas == null) return;

        if (myDatas.containsKey(KEY_MAX))
            setMax( (int)(double) myDatas.get(KEY_MAX) );

        if (myDatas.containsKey(KEY_MIN))
            setMin( (int)(double) myDatas.get(KEY_MIN) );

        if (myDatas.containsKey(KEY_TITLE))
            setTitle((String) myDatas.get(KEY_TITLE));

        if (myDatas.containsKey(KEY_NAME))
            setName((String) myDatas.get(KEY_NAME));

        if (myDatas.containsKey(KEY_CELL_TYPE))
            setCellType((String) myDatas.get(KEY_CELL_TYPE));

        if (myDatas.containsKey(KEY_DEFAULT_INDEX))
            setDefalutIndex((String) myDatas.get(KEY_DEFAULT_INDEX));

        if( myDatas.containsKey(ISnapsProductOptionCellConstants.KEY_INNER_PAPER_KIND))
            setInnerPaperKind( (String) myDatas.get(ISnapsProductOptionCellConstants.KEY_INNER_PAPER_KIND) );

        if (myDatas.containsKey(KEY_PARAMETER))
            setParameter((String) myDatas.get(KEY_PARAMETER));
    }

    //하위 컨트롤을 가지고 있는 경우
    private void findChildCells(List<LinkedTreeMap> childValueList, SnapsProductOptionCommonValue commonValue) {
        if (childValueList == null || commonValue == null) return;
        for (LinkedTreeMap childValue : childValueList) {
            if (childValue == null) continue;

            if (!childValue.containsKey(KEY_CELL_TYPE) && !childValue.containsKey(KEY_VALUE)) continue;

            String findedCellType = null;
            List<LinkedTreeMap> findedCellList = null;
            Set<?> childKeyset = childValue.keySet();
            for (Object childKey : childKeyset) {
                if (childKey == null) continue;

                if (childKey.equals(ISnapsProductOptionCellConstants.KEY_CELL_TYPE)) {
                    findedCellType = (String) childValue.get(childKey);
                } else if (childKey.equals(ISnapsProductOptionCellConstants.KEY_VALUE)) {
                    findedCellList = (List<LinkedTreeMap>) childValue.get(childKey);
                }
            }

            //cellType을 가지고 있다면 하위 컨트롤을 가지고 있는 것임.
            if (findedCellType != null && findedCellList != null) {
                SnapsProductOptionBaseCell child = SnapsProductOptionCellFactory.createCell(findedCellList, childValue);
                commonValue.setChildControl(child);
                break;
            }
        }
    }

    private void setLeatherCoverIndex( LinkedTreeMap values, int index ) {
        if( !values.containsKey("value") ) return;

        ArrayList<LinkedTreeMap> value = (ArrayList<LinkedTreeMap>) values.get( "value" );
        for( LinkedTreeMap item : value ) {
            if( item.containsKey("value") )
                setLeatherCoverIndex( item, index );
        }

        for( LinkedTreeMap item : value ) {
            if( item.containsKey("detail") ) {
                ( (LinkedTreeMap) item.get("detail") ).put( ISnapsProductOptionCellConstants.KEY_LEATHER_COVER, index + "" );
            }
        }
    }

    public void performKeyName(Object obj, SnapsProductOptionCommonValue commonValue) {
        commonValue.setName((String) obj);
    }

    public void performKeyMax(Object obj, SnapsProductOptionCommonValue commonValue) {
        commonValue.setMax((Integer) obj);
        setMaxCount((Integer)obj);
    }

    public void performKeyMin(Object obj, SnapsProductOptionCommonValue commonValue) {
        commonValue.setMin((Integer) obj);
        setMinCount((Integer)obj);
    }

    public void performKeyProdForm(Object obj, SnapsProductOptionCommonValue commonValue) {
        commonValue.setProdForm((String) obj);
    }

    public void performKeyCmd(Object obj, SnapsProductOptionCommonValue commonValue) {
        commonValue.setCmd((String) obj);
    }

    public void performKeyDetail(Object obj, SnapsProductOptionCommonValue commonValue) {
        LinkedTreeMap detailTreeMap = (LinkedTreeMap) obj;
        SnapsProductOptionDetailValue detailValue = new SnapsProductOptionDetailValue();
        detailValue.performStrParsingFromMap(detailTreeMap);
        commonValue.setDetailValue(detailValue);
    }

    public void performKeyPrice(Object obj, SnapsProductOptionCommonValue commonValue) {
        LinkedTreeMap priceTreeMap = (LinkedTreeMap) obj;
        SnapsProductOptionPrice priceValue = new SnapsProductOptionPrice();
        priceValue.performStrParsingFromMap(priceTreeMap);
        commonValue.setPrice(priceValue);
    }

    public void performKeyValue(Object obj, SnapsProductOptionCommonValue commonValue) {
        List<LinkedTreeMap> childValueList = (List<LinkedTreeMap>) obj;
        findChildCells(childValueList, commonValue);
    }

    protected void setValues(List<LinkedTreeMap> parentValueList) {
        if (parentValueList == null || parentValueList.isEmpty()) return;

        valueList = new ArrayList<>();
        SnapsProductOptionCommonValue commonValue = null;

        int leatherCoverIndex = 0;
        SnapsProductOptionBaseCellParser<SnapsProductOptionBaseCell> snapsSwitch = new SnapsProductOptionBaseCellParser<>(this, null);
        for (LinkedTreeMap mapParentValue : parentValueList) {
            if (mapParentValue == null) continue;

            if( ISnapsProductOptionCellConstants.CELL_TYPE_LEATHER_COVER.equalsIgnoreCase(cellType) ) {
                setLeatherCoverIndex( mapParentValue, leatherCoverIndex );
                leatherCoverIndex ++;
            }

            if( mapParentValue.containsKey(ISnapsProductOptionCellConstants.KEY_CELL_TYPE) // leather cover일때 하드코딩
                    && (mapParentValue.get(ISnapsProductOptionCellConstants.KEY_CELL_TYPE) instanceof String)
                    && ISnapsProductOptionCellConstants.CELL_TYPE_LEATHER_COVER.equalsIgnoreCase((String)mapParentValue.get(ISnapsProductOptionCellConstants.KEY_CELL_TYPE)) ) {
                String cellType = (String) mapParentValue.get( ISnapsProductOptionCellConstants.KEY_CELL_TYPE );
                mapParentValue.remove( ISnapsProductOptionCellConstants.KEY_CELL_TYPE );
                ArrayList<LinkedTreeMap> value = (ArrayList) mapParentValue.get( ISnapsProductOptionCellConstants.KEY_VALUE );

                Context context = ContextUtil.getContext();
                LinkedTreeMap object = new LinkedTreeMap(), tempObj;
                ArrayList<LinkedTreeMap> list = new ArrayList<LinkedTreeMap>();
                object.put( ISnapsProductOptionCellConstants.KEY_NAME, context.getString(ColorPickerLayout.COLOR_PICKER_NAME_RED_ID) );
                object.put( ISnapsProductOptionCellConstants.KEY_CELL_TYPE, cellType );

                ArrayList<LinkedTreeMap> tempValue;
                for( int i = 0; i < ColorPickerLayout.COLOR_STRING_RES_ID.length; ++i ) {
                    tempObj = new LinkedTreeMap();

                    tempValue = (ArrayList<LinkedTreeMap>) value.clone();

                    tempObj.put( ISnapsProductOptionCellConstants.KEY_NAME, context.getString(ColorPickerLayout.COLOR_STRING_RES_ID[i]) );
                    tempObj.put( ISnapsProductOptionCellConstants.KEY_VALUE, tempValue );
                    list.add( tempObj );
                }

                object.put( ISnapsProductOptionCellConstants.KEY_VALUE, list );
                list = new ArrayList<LinkedTreeMap>();
                list.add( object );

                mapParentValue.remove( ISnapsProductOptionCellConstants.KEY_VALUE );
                mapParentValue.put( ISnapsProductOptionCellConstants.KEY_VALUE, list );
            }

            commonValue = new SnapsProductOptionCommonValue();

            //IF, Switch 보다 아래 방식으로 하는 게 좋다고 해서 했는데, 별로 효과는 없다..
            snapsSwitch.setOrgDataMap(mapParentValue);
            snapsSwitch.setCommonValue(commonValue);
            snapsSwitch.perform();

            addValue(commonValue);
        }
    }

    private SnapsProductNativeUIBaseResultJson convertTreeMapToData(LinkedTreeMap treeMap, Class<? extends SnapsProductNativeUIBaseResultJson> t) {
        if (treeMap == null) return null;

        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(treeMap);
        if (jsonElement == null) return null;

        JsonObject priceObj = jsonElement.getAsJsonObject();
        if (priceObj == null) return null;

        return gson.fromJson(priceObj, t);
    }

    public List<SnapsProductOptionCommonValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<SnapsProductOptionCommonValue> valueList) {
        this.valueList = valueList;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getDefalutIndex() {
        return defalutIndex;
    }

    public String getInnerPaperKind() { return innerPaperKind; }

    public void setInnerPaperKind(String innerPaperKind) { this.innerPaperKind = innerPaperKind; }

    public void setDefalutIndex(String defalutIndex) {
        this.defalutIndex = defalutIndex;
    }

    protected void addValue(SnapsProductOptionCommonValue value) {
        if (valueList == null) valueList = new ArrayList<>();
        valueList.add(value);
    }

    public int getMaxCount() { return maxCount; }

    public void setMaxCount(int maxCount) { this.maxCount = maxCount; }

    public int getMinCount() { return minCount; }

    public void setMinCount(int minCount) { this.minCount = minCount; }
}
