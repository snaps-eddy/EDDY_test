package com.snaps.mobile.utils.network.retrofit2.genetator.test;//package com.snaps.mobile.utils.network.servicies.handler.test;
//
//import android.support.annotation.NonNull;
//
//import com.snaps.mobile.utils.network.retrofit2.api.test.ISnapsAPIPhotoBookTemplateForTEST;
//import com.snaps.mobile.utils.network.retrofit2.data.response.common.SnapsNetworkAPIBaseResponse;
//import com.snaps.mobile.utils.network.retrofit2.util.SnapsRetrofitRequestBuilder;
//import com.snaps.mobile.utils.network.servicies.handler.SnapsNetworkServiceBase;
//
//import io.reactivex.Flowable;
//
//public class SnapsNetworkHandlerPhotoBookTemplate extends SnapsNetworkServiceBase {
//    @Override
//    protected Flowable<? extends SnapsNetworkAPIBaseResponse> generateRetrofitAPIService(@NonNull SnapsRetrofitRequestBuilder requestBuilder) throws Exception {
//        return createAPIWithBaseUrl("http://m.snaps.kr", ISnapsAPIPhotoBookTemplateForTEST.class).
//                .requestGetTemplate(); //파라메터를 순차적으로 잘 넣어준다.
//    }
//}
