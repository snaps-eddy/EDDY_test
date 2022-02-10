package com.snaps.mobile.utils.network.retrofit2.genetator.test;//package com.snaps.mobile.utils.network.servicies.handler.test;
//
//import android.support.annotation.NonNull;
//
//import com.snaps.mobile.utils.network.retrofit2.util.SnapsRetrofitRequestBuilder;
//import com.snaps.mobile.utils.network.servicies.handler.SnapsNetworkServiceBase;
//
//import io.reactivex.Flowable;
//import okhttp3.MultipartBody;
//
//import static com.snaps.mobile.utils.network.retrofit2.interfacies.SnapsRetrofitRequestConstants.eRequestParams.X_SNAPS_TOKEN;
//
//public class SnapsNetworkHandlerPOSTMultipart extends SnapsNetworkServiceBase {
//    @Override
//    protected Flowable<?> generateRetrofitAPIService(@NonNull SnapsRetrofitRequestBuilder requestBuilder) throws Exception {
//        MultipartBody.Builder builder = createMultipartBodyBuilder(requestBuilder);
//
//        return  createRetrofitServiceWithBaseUrl(SnapsNetworkHandlerPOST.SnapsRetrofitRequestTestModelPOST.class)
//                .requestTestMultipart(requestBuilder.getDynamicParamsStrValue(X_SNAPS_TOKEN),
//                        "20181017010261", //테스트용
//                        builder.build());
//    }
//}
