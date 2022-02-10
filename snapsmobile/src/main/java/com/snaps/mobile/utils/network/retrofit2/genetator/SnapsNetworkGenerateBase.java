package com.snaps.mobile.utils.network.retrofit2.genetator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.network.provider.SnapsRetrofitRequestBuilder;
import com.snaps.mobile.utils.network.provider.listener.SnapsRetrofitResultListener;
import com.snaps.mobile.utils.network.retrofit2.data.request.body.SnapsRetrofitRequestBaseBody;
import com.snaps.mobile.utils.network.retrofit2.data.response.common.SnapsNetworkAPIBaseResponse;
import com.snaps.mobile.utils.network.retrofit2.data.response.common.SnapsNetworkAPIResponseString;
import com.snaps.mobile.utils.network.retrofit2.exception.SnapsNetworkThrowable;
import com.snaps.mobile.utils.network.retrofit2.interfacies.ISnapsNetworkAPI;
import com.snaps.mobile.utils.network.retrofit2.interfacies.enums.eSnapsNetworkResponseContentType;
import com.snaps.mobile.utils.network.retrofit2.interfacies.enums.eSnapsRetrofitRequestParams;
import com.snaps.mobile.utils.network.retrofit2.util.SnapsRetrofitAPIGenerator;
import com.snaps.mobile.utils.network.retrofit2.util.SnapsRetrofitProvider;

import org.reactivestreams.Publisher;

import errorhandle.SnapsAssert;
import font.FProgressDialog;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static com.snaps.mobile.utils.network.retrofit2.interfacies.SnapsNetworkConstants.TOKEN_EXPIRED_ERR_CODES;

public abstract class SnapsNetworkGenerateBase implements ISnapsNetworkAPI {
    private static final String TAG = SnapsNetworkGenerateBase.class.getSimpleName();

    protected abstract Flowable<?> generateRetrofitAPIService(
            @NonNull SnapsRetrofitRequestBuilder requestBuilder) throws Exception;

    private SnapsRetrofitRequestBuilder requestBuilder;
    private FProgressDialog progressDialog;

    public abstract eSnapsNetworkResponseContentType getResponseContentType();


    @Override
    public void request(@NonNull Context context) {
        request(context, null);
    }

    @Override
    public void request(@NonNull SnapsRetrofitRequestBuilder requestBuilder) {
        request(requestBuilder, null);
    }

    @Override
    public <T extends SnapsNetworkAPIBaseResponse> void request(
            @NonNull SnapsRetrofitRequestBuilder requestBuilder, SnapsRetrofitResultListener<T> resultListener) {
        try {
            prepareRequest(requestBuilder, resultListener);

            Flowable<?> retrofitService = generateRetrofitAPIService(requestBuilder);
            subscribe(requestBuilder, retrofitService, resultListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            handleOnRequestFailed(resultListener, SnapsNetworkThrowable.withException(e));
        }
    }

    @Override
    public <T extends SnapsNetworkAPIBaseResponse> void request(
            @NonNull Context context, SnapsRetrofitResultListener<T> resultListener) {
        try {
            SnapsRetrofitRequestBuilder requestBuilder = createDefaultRequestBuilder(context);
            prepareRequest(requestBuilder, resultListener);

            Flowable<?> retrofitService = generateRetrofitAPIService(requestBuilder);
            subscribe(requestBuilder, retrofitService, resultListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            handleOnRequestFailed(resultListener, SnapsNetworkThrowable.withException(e));
        }
    }

    @Override
    public <T extends SnapsNetworkAPIBaseResponse> void request(@NonNull Context context, @NonNull String url, SnapsRetrofitResultListener<T> resultListener) {
        SnapsRetrofitRequestBuilder requestBuilder = SnapsRetrofitRequestBuilder.createBuilder(context).appendDynamicParam(eSnapsRetrofitRequestParams.FULL_URL, url).create();
        request(requestBuilder, resultListener);
    }

    private void prepareRequest(@NonNull SnapsRetrofitRequestBuilder requestBuilder, SnapsRetrofitResultListener resultListener) {
        this.requestBuilder = requestBuilder;
        if (resultListener != null) resultListener.onPrepare();

        if (requestBuilder.isShowProgressOnNetworking()) {
            showProgressDialog(requestBuilder.getContext());
        }
    }

    protected String getStringValue(eSnapsRetrofitRequestParams params) {
        return requestBuilder != null ? requestBuilder.getDynamicParamsStrValue(params) : "";
    }

    protected int getIntValue(eSnapsRetrofitRequestParams params) {
        return requestBuilder != null ? requestBuilder.getDynamicParamsIntValue(params) : 0;
    }

    private void showProgressDialog(Context context) {
        if (context == null) return;
        dismissProgressDialog();

        try {
            progressDialog = new FProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected MultipartBody generateMultipartRequestBody() {
        try {
            SnapsRetrofitRequestBaseBody requestBaseCustomBody = requestBuilder.getRequestBody();
            return requestBaseCustomBody.generateMultipartRequestBody();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    protected RequestBody generateRequestBody(SnapsRetrofitRequestBaseBody body) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(body);
            return RequestBody.create(MediaType.parse("application/json"), json);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    protected RequestBody generateRequestBody() {
        return generateRequestBody(requestBuilder.getRequestBody());
    }

/*
    protected MultipartBody.Builder createMultipartBodyBuilder(@NonNull SnapsNetworkRequestBuilder requestBuilder)
            throws Exception {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        Map<String, File> filesMap = requestBuilder.getMultipartFileMap();
        if (filesMap != null) {
            for (Map.Entry<String, File> entry : filesMap.entrySet()) {
                builder.addFormDataPart(entry.getKey(),
                        entry.getValue().getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), entry.getValue()));
            }
        }

        Map<String, String> formMap = requestBuilder.getMultipartFormMap();
        if (formMap != null) {
            for (Map.Entry<String, String> entry : formMap.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        return builder;
    }
*/
    protected <K> K createAPI(@NonNull Class<K> serviceClass) throws Exception {
        return createAPI(requestBuilder, serviceClass);
    }

    protected <K> K createAPI(@NonNull SnapsRetrofitRequestBuilder requestBuilder,
                              @NonNull Class<K> serviceClass)
            throws Exception {
        String baseUrl = requestBuilder.getBaseUrl();
        if (StringUtil.isEmpty(baseUrl)) {
            return SnapsRetrofitAPIGenerator.createGeneratorWithContext(requestBuilder.getContext())
                    .generate(serviceClass, getResponseContentType());
        } else {
            return SnapsRetrofitAPIGenerator.createGeneratorWithContext(requestBuilder.getContext())
                    .generateWithBaseUrl(baseUrl, serviceClass, getResponseContentType());
        }
    }

    protected <K> K createAPIWithBaseUrl(String baseUrl, @NonNull Class<K> serviceClass) throws Exception {
        return SnapsRetrofitAPIGenerator.createGeneratorWithContext(requestBuilder.getContext())
                .generateWithBaseUrl(baseUrl, serviceClass, getResponseContentType());
    }

    protected SnapsRetrofitRequestBuilder createDefaultRequestBuilder(Context context) {
        return SnapsRetrofitRequestBuilder.createBuilder(context).create();
    }

    @SuppressLint("CheckResult")
    private <T extends SnapsNetworkAPIBaseResponse> void subscribe(@NonNull SnapsRetrofitRequestBuilder requester,
                                                                   @NonNull Flowable<?> apiService,
                                                                   SnapsRetrofitResultListener<T> resultListener) throws Exception {

        //set Log
        apiService = appendLog(apiService);

        //error handle
        apiService = appendExceptionHandler(apiService, resultListener);

        //set Thread
        apiService = setScheduler(apiService, requester);

        //set life cycle
//        request = request.compose(RxLifecycleAndroid.b); //생명주기 관련..처리..SnapsRetrofitManager에서 처리 한다.

        Disposable disposable;
        if (resultListener != null) { //현재는 Flowable과 T의 데이터형이 동일한 경우만 가능하다.
            disposable = subscribeWithResultListener(apiService, resultListener);
        } else { //listener가 없을 때,
            disposable = apiService.subscribe();
        }

        SnapsRetrofitProvider.addCompositeDisposable(requester.getContext(), disposable);
    }

    @SuppressLint("CheckResult")
    private <T extends SnapsNetworkAPIBaseResponse> Disposable subscribeWithResultListener(Flowable<?> request,
                                                                                           final SnapsRetrofitResultListener<T> resultListener)
            throws Exception {
        return request.subscribe(result -> {
            dismissProgressDialog();

            if (!(result instanceof Response)) {
                SnapsAssert.assertException(new Exception("result should be Response"));
                return;
            }

            Response response = (Response) result;
            Object responseBody = response.body();
            if (isSuccessfulResponse(response)) {
                if (responseBody instanceof SnapsNetworkAPIBaseResponse) {
                    SnapsNetworkAPIBaseResponse baseResponse = ((SnapsNetworkAPIBaseResponse) responseBody);
                    resultListener.onResultSuccess((T) baseResponse);
                } else if (responseBody instanceof String) {
                    SnapsNetworkAPIResponseString stringResponse = new SnapsNetworkAPIResponseString();
                    stringResponse.setResultString((String) responseBody);
                    resultListener.onResultSuccess((T) stringResponse);
                } else {
                    SnapsAssert.assertException(new Exception("result must extends SnapsNetworkAPIBaseResponse or string."));
                }
            } else {
                if (responseBody instanceof SnapsNetworkAPIBaseResponse) {
                    SnapsNetworkAPIBaseResponse baseResponse = ((SnapsNetworkAPIBaseResponse) responseBody);
                    handleOnRequestFailed(resultListener, SnapsNetworkThrowable.convertThrowable(baseResponse));
                } else {
                    handleOnRequestFailed(resultListener, SnapsNetworkThrowable.convertThrowable(response));
                }
            }
        }, throwable -> {
            handleOnRequestFailed(resultListener, SnapsNetworkThrowable.convertThrowable(throwable));
        });
    }

    private <K, T extends SnapsNetworkAPIBaseResponse> Flowable<K> appendExceptionHandler(Flowable<K> request, SnapsRetrofitResultListener<T> resultListener) throws Exception {
        return request
            .doOnError(e -> {
                Dlog.e(TAG, "#Retrofit Log# state : doOnError", e);
                handleOnRequestFailed(resultListener, SnapsNetworkThrowable.convertThrowable(e));
            })
            .onExceptionResumeNext(e -> {
                Dlog.d("#Retrofit Log# state : onExceptionResumeNext >> " + e);
                handleOnRequestFailed(resultListener, SnapsNetworkThrowable.withErrorMessage(e.toString()));
            })
            .onErrorResumeNext((Publisher<? extends K>) e -> {
                Dlog.d("#Retrofit Log# state : onErrorResumeNext >> " + e);
                handleOnRequestFailed(resultListener, SnapsNetworkThrowable.withErrorMessage(e.toString()));
            });
    }

    private <T extends SnapsNetworkAPIBaseResponse> void handleOnRequestFailed(SnapsRetrofitResultListener<T> resultListener, SnapsNetworkThrowable throwable) {
        dismissProgressDialog();
        if (resultListener != null && !resultListener.isErrorProcessed()) {
            if (requestBuilder != null) {
                showShouldRetryMessageIfTokenHasExpired(requestBuilder.getContext(), throwable);
            }

            SnapsHandler.handleOnMainThread(() -> {
                resultListener.onResultFailed(throwable);
                resultListener.setErrorProcessed(true);
            });
        }
    }

    private void showShouldRetryMessageIfTokenHasExpired(Context context, SnapsNetworkThrowable throwable) {
        if (throwable == null || StringUtil.isEmpty(throwable.getErrorCode())) return;

        for (String errCode : TOKEN_EXPIRED_ERR_CODES) {
            if (errCode.equalsIgnoreCase(throwable.getErrorCode())) {
                refreshLoginToken(context);
                break;
            }
        }
    }

    private void refreshLoginToken(Context context) {
        //FIXME...API 리뉴얼할때 주석 해제
//        String snapsUserId = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_ID);
//        String snapsUserPwd = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_PWD);
//
//        SnapsRetrofitRequestBuilder builder = SnapsRetrofitRequestBuilder.createBuilderWithProgress(context)
//                .appendDynamicParam(eSnapsRetrofitRequestParams.USER_ID, snapsUserId)
//                .appendDynamicParam(eSnapsRetrofitRequestParams.PASSWORD, snapsUserPwd)
//                .create();
//
//        SnapsRetrofit.with(eSnapsRetrofitAPI.LOGIN).request(builder, new SnapsRetrofitResultListener<SnapsNetworkAPIResponseExecuteLogin>() {
//            @Override
//            public void onResultSuccess(SnapsNetworkAPIResponseExecuteLogin result) {
//                if (context != null) showToastOnUIHandler(context, context.getString(R.string.network_error_message_please_wait));
//            }
//
//            @Override
//            public void onResultFailed(SnapsNetworkThrowable throwable) {
//                if (context != null) showToastOnUIHandler(context, context.getString(R.string.network_error_message_please_wait));
//            }
//        });
    }

    private <T> Flowable<T> appendLog(Flowable<T> request) throws Exception {
        return request
        .doOnCancel(() -> {
            Dlog.d("appendLog() #Retrofit Log# state : doOnCancel");
            dismissProgressDialog();
        })
        .doOnComplete(() -> {
            Dlog.d("appendLog() #Retrofit Log# state : doOnComplete");
            dismissProgressDialog();
        })
        .doOnTerminate(() -> {
            Dlog.d("appendLog() #Retrofit Log# state : doOnTerminate");
            dismissProgressDialog();
        })
        .doOnEach(e -> Dlog.d("appendLog() #Retrofit Log# state : doOnEach >> " + e))
        .doOnNext(e -> Dlog.d("appendLog() #Retrofit Log# state : doOnNext >> " + e))
        .doOnRequest(e -> Dlog.d("appendLog() #Retrofit Log# state : doOnRequest >> " + e))
        .doOnSubscribe(e -> Dlog.d("appendLog() #Retrofit Log# state : doOnSubscribe >> " + e));
    }

    private <T> Flowable<T> setScheduler(@NonNull Flowable<T> request,
                                         @NonNull SnapsRetrofitRequestBuilder requester)
            throws Exception {
        Scheduler subscribeOnScheduler = requester.getSubscribeOn();
//        request = request.subscribeOn(subscribeOnScheduler != null ? subscribeOnScheduler : Schedulers.newThread());
        request = request.subscribeOn(subscribeOnScheduler != null ? subscribeOnScheduler : Schedulers.io());
        Scheduler observeOnScheduler = requester.getObserveOn();
        request = request.observeOn(observeOnScheduler != null ? observeOnScheduler : AndroidSchedulers.mainThread());
        return request;
    }

    private void showToastOnUIHandler(Context context, String message) {
        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MessageUtil.toast(context, message);
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private boolean isSuccessfulResponse(Response response) {
        return response != null && response.isSuccessful();
    }
}
