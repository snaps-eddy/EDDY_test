package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductOptionCell extends SnapsProductOptionBaseCell {

    private static final long serialVersionUID = -780103353323830151L;

    public SnapsProductOptionCell() {}

    public SnapsProductOptionCell(List<LinkedTreeMap> values, LinkedTreeMap myDatas) {
        super(values, myDatas);
    }

    public SnapsProductOptionCell(List<LinkedTreeMap> values, LinkedTreeMap myDatas, String cellType) {
        super(values, myDatas);
    }

    public SnapsProductOptionCell(List<LinkedTreeMap> values, SnapsProductOption option) {
        super(values, null);
        if (option != null) {
            setTitle(option.getTitle());
            setName(option.getName());
            setCellType(option.getCellType());
            setDefalutIndex(option.getDefalut_index());
            setMax(option.getMax());
            setMin(option.getMin());
            setParameter(option.getParameter());
        }
    }
}
