package com.snaps.mobile.utils.network.retrofit2.genetator.test;

//public class SnapsNetworkHandlerGET extends SnapsNetworkServiceBase {
//    @Override
//    protected Flowable<? extends SnapsNetworkAPIBaseResponse> generateRetrofitAPIService(@NonNull SnapsRetrofitRequestBuilder requestBuilder) throws Exception {
//        return createRetrofitServiceWithBaseUrl(SnapsRetrofitRequestTestModelGET.class)
//                .requestGETTest(requestBuilder.getDynamicParamsStrValue(SnapsRetrofitRequestConstants.eRequestParams.X_SNAPS_TOKEN)); //파라메터를 순차적으로 잘 넣어준다.
//    }
//
//    public interface SnapsRetrofitRequestTestModelGET {
//        @Headers({
//                "X-SNAPS-CHANNEL: WEB"
//        })
//        @GET("/v1/account/user")
//        Flowable<SnapsRetrofitResponseGETTest> requestGETTest(@Header("X-SNAPS-TOKEN") String headerToken);
//    }
//}

//    //TESTCODE
//    private void testPost() {
//        SnapsNetworkService.with(TEST_POST).request(
//                SnapsRetrofitRequestBuilder.createBuilder(this)
//                        .setBaseUrl("https://stg-www.snaps.com") //baseUrl이 없으면 기본 스냅스 도메인으로 들어간다.
//                        .setConverter(new SnapsRetrofitRequestTestModelPOST.SnapsRetrofitPostTestToStringConverter<String>()) //만약 결과값에 대한 컨버팅이 필요하다면 이와 같은 방식으로...
//                        .create(),
//                new SnapsRetrofitResultListener<String>() {
//                    @Override
//                    public void onResult(String result) {
//                        Logg.y("onResult : " + result);
//
//                        testGET(result);
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Logg.y("Throwable : " + throwable);
//                    }
//                });
//    }
//
//    private void testGET(String token) {
//        //TODO  실제로는 빌더가 필요 없는 경우가 많을 것이다.
//        SnapsNetworkRequestBuilder retrofitRequest = SnapsNetworkRequestBuilder.createBuilder(this)
//                .setBaseUrl("https://stg-www.snaps.com") //baseUrl이 없으면 기본 스냅스 도메인으로 들어간다.
//                .appendDynamicParam(X_SNAPS_TOKEN, token)
//                .create();
//
//        SnapsNetworkService.with(TEST_GET).request(retrofitRequest,
//                new SnapsRetrofitResultListener<SnapsRetrofitResponseGETTest>() {
//                    @Override
//                    public void onResult(SnapsRetrofitResponseGETTest result) {
//                        Logg.y("");
//
//                        testMultipart(token);
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Logg.y("");
//                    }
//                });
//    }
//
//    private void testMultipart(String token) {
//        SnapsNetworkRequestBuilder retrofitRequest = SnapsNetworkRequestBuilder.createBuilder(this)
//                .setBaseUrl("https://stg-www.snaps.com") //baseUrl이 없으면 기본 스냅스 도메인으로 들어간다.
//                .appendDynamicParam(X_SNAPS_TOKEN, token) //실제로 이건, 로그인 Manager쪽에서 관리하자..
//                .appendMultipartFile("largeFile", new File("/storage/emulated/0/cs/cs_i/2018101418395560107.jpg"))
//                .appendMultipartFile("middleFile", new File("/storage/emulated/0/cs/cs_i/2018101418395560107.jpg"))
//                .appendMultipartFormData("fileSize", String.valueOf(new File("/storage/emulated/0/cs/cs_i/2018101418395560107.jpg").length()))
//                .create();
//
//        SnapsNetworkService.with(TEST_POST_MULTIPART).request(retrofitRequest,
//                new SnapsRetrofitResultListener<SnapsRetrofitResponseMultipartTest>() {
//                    @Override
//                    public void onResult(SnapsRetrofitResponseMultipartTest result) {
//                        Logg.y("");
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Logg.y("");
//                    }
//                });
//    }
