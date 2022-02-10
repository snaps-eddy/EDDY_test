package com.snaps.mobile.product_native_ui.string_switch;

import com.google.gson.internal.LinkedTreeMap;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsPerform;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ysjeong on 2016. 11. 21..
 */
public abstract class SnapsNativeUIStrSwitch<T> {

    public T targetClass;

    private Map<Object, ISnapsPerform> performMap = new HashMap<>();

    private LinkedTreeMap orgDataMap = null;

    public SnapsNativeUIStrSwitch(T t, LinkedTreeMap treeMap) {
        this.targetClass = t;
        this.orgDataMap = treeMap;
        createCase();
    }

    protected T getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(T t) {
        this.targetClass = t;
    }

    public void setOrgDataMap(LinkedTreeMap map) {
        this.orgDataMap = map;
    }

    protected void addCase(String str, ISnapsPerform perform) {
        if (performMap != null)
            this.performMap.put(str, perform);
    }

    public void perform() {
        if (orgDataMap == null || performMap == null) return;
        Set keySets = orgDataMap.keySet();
        for (Object key : keySets) {
            if (key == null) continue;
            ISnapsPerform performer = performMap.get(key);
            if (performer != null)
                performer.perform(orgDataMap.get(key));
        }
    }

    public abstract void createCase();
}
