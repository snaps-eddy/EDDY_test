package com.snaps.mobile.order.order_v2.interfacies;

/**
 * Created by ysjeong on 2018. 4. 17..
 */

public interface SnapsOrderGetPROJCodeTaskImp {
    //프로젝트 발급
    void getProjectCode(final SnapsOrderResultListener getProjectCodeListener);

    //프로젝트 번호 검증
    void performVerifyProjectCode(final SnapsOrderResultListener verifyProjectCodeListener) throws Exception;
}
