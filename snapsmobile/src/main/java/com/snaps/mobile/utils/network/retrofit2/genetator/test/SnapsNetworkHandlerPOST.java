package com.snaps.mobile.utils.network.retrofit2.genetator.test;//package com.snaps.mobile.utils.network.servicies.handler.test;
//
//import android.support.annotation.NonNull;
//
//import com.snaps.mobile.utils.network.retrofit2.util.SnapsRetrofitRequestBuilder;
//import com.snaps.mobile.utils.network.servicies.handler.SnapsNetworkServiceBase;
//
//import io.reactivex.Flowable;
//
//public class SnapsNetworkHandlerPOST extends SnapsNetworkServiceBase {
//    @Override
//    protected Flowable<?> generateRetrofitAPIService(@NonNull SnapsRetrofitRequestBuilder requestBuilder) throws Exception {
////        //Body는 가능하면 여기서 작성하고 부득이 동적으로 생성해야 하는 경우 requester에 포함할 수도 있다.
////        SnapsNetworkAPIBodyExecuteLogin bodyTest = new SnapsNetworkAPIBodyExecuteLogin();
////        bodyTest.setHashedPassword("1234");
////        bodyTest.setLoginId("mysql1@snaps.com");
////        bodyTest.setSnsId("string");
////        bodyTest.setSnsType("EMAIL");
////        bodyTest.setType("SNAPS");
////        bodyTest.setUserNo(0);
////
////        return createRetrofitServiceWithBaseUrl(SnapsRetrofitRequestTestModelPOST.class)
////                .requestTestPost(bodyTest);
//        return null;
//    }
//
//    public interface SnapsRetrofitRequestTestModelPOST {
////        /**
////         * 단순 POST 테스트
////         */
////        @Headers({
////                "X-SNAPS-CHANNEL: WEB"
////        })
////        @POST("/v1/account/user/login")
////        Flowable<SnapsRetrofitResponsePostTest> requestTestPost(@Body SnapsNetworkAPIBodyExecuteLogin bodyTest);
////
////
////        /**
////         * Multipart POST 테스트
////         */
////        @Headers({
////                "X-SNAPS-CHANNEL: WEB"
////        })
////        @POST("/v1/project/{projectCode}/file")
////        Flowable<SnapsRetrofitResponseMultipartTest> requestTestMultipart(
////                @Header("X-SNAPS-TOKEN") String headerToken,
////                @Path("projectCode") String projectCode,
////                @Body RequestBody body);
//    }
//
//}
