package com.snaps.mobile.utils.network.retrofit2.interfacies;

public interface SnapsRetrofitCommonCallFunc<T> {
    <O> T apply(O originData) throws Exception;
}
