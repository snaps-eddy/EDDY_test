package com.snaps.mobile.utils.network.retrofit2.util;

import android.content.Context;

import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.utils.network.provider.SnapsRetrofit;
import com.snaps.mobile.utils.network.retrofit2.interfacies.enums.eSnapsNetworkResponseContentType;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import errorhandle.SnapsAssert;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class SnapsRetrofitProvider {
    private volatile static SnapsRetrofitProvider instance = null;

    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;

    private static final int HTTP_CONNECTION_TIME_OUT = 30000;

    private OkHttpClient sOkHttpClient;

    private Map<String, Retrofit> retrofitMap;

    private Map<Context, CompositeDisposable> compositeDisposableMap;

    public static SnapsRetrofitProvider getInstance() {
        if (instance == null)
            createInstance();

        return instance;
    }

    public void disposeAll() {
        Map<Context, CompositeDisposable> compositeDisposableMap = getCompositeDisposableMap();
        Set<Context> contextSet = compositeDisposableMap.keySet();
        for (Context context : contextSet) {
            if (context == null) continue;
            SnapsRetrofit.disposeOnDestroy(context);
        }
    }

    public void deleteDisposeWithContext(Context context) {
        Map<Context, CompositeDisposable> compositeDisposableMap = getCompositeDisposableMap();
        if (compositeDisposableMap.containsKey(context)) {
            compositeDisposableMap.remove(context);
        }
    }

    private Retrofit createRetrofitWithBaseUrl(Context context, String baseUrl, eSnapsNetworkResponseContentType responseContentType) {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(baseUrl)
                .client(getOkHttpClient(context)).addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        builder = addConvertFactoryWithContentType(builder, responseContentType);

        return builder.build();
    }

    private Retrofit.Builder addConvertFactoryWithContentType(Retrofit.Builder builder, eSnapsNetworkResponseContentType responseContentType) {
        if (responseContentType == null) {
            responseContentType = getDefaultResponseContentType();
        }

        switch (responseContentType) {
            case XML:
                builder.addConverterFactory(SimpleXmlConverterFactory.createNonStrict(new Persister(new AnnotationStrategy())));
                break;
            case JSON:
                builder.addConverterFactory(GsonConverterFactory.create());
                break;
            case STRING:
                builder.addConverterFactory(ScalarsConverterFactory.create());
                break;
        }

        return builder;
    }

    private eSnapsNetworkResponseContentType getDefaultResponseContentType() {
        return eSnapsNetworkResponseContentType.JSON;
    }

    public Retrofit getRetrofitService(Context context, String baseUrl, eSnapsNetworkResponseContentType responseContentType) {
        checkRetrofitWithBaseUrl(context, baseUrl, responseContentType);
        if (retrofitMap == null) {
            SnapsAssert.assertNotNull(null);
            return null;
        }

        return retrofitMap.get(getKeyOfRetrofitSet(baseUrl, responseContentType));
    }

    private void checkRetrofitWithBaseUrl(Context context, String baseUrl, eSnapsNetworkResponseContentType responseContentType) {
        if (retrofitMap == null) retrofitMap = new HashMap<>();

        String key = getKeyOfRetrofitSet(baseUrl, responseContentType);

        if (StringUtil.isEmpty(key)) {
            SnapsAssert.assertTrue(false);
            return;
        }

        if (!retrofitMap.containsKey(key)) {
            retrofitMap.put(key, createRetrofitWithBaseUrl(context, baseUrl, responseContentType));
        }
    }

    private String getKeyOfRetrofitSet(String baseUrl, eSnapsNetworkResponseContentType responseContentType) {
        if (responseContentType == null) responseContentType = getDefaultResponseContentType();
        return responseContentType.toString() + "_" + baseUrl;
    }

    private static void createInstance() {
        if (instance == null) {
            synchronized (SmartSnapsManager.class) {
                if (instance == null) {
                    instance = new SnapsRetrofitProvider();
                }
            }
        }
    }

    public static void addCompositeDisposable(Context context, Disposable disposable) {
        CompositeDisposable compositeDisposable = getCompositeDisposable(context);
        compositeDisposable.add(disposable);
    }

    public static CompositeDisposable getCompositeDisposable(Context context) {
        SnapsRetrofitProvider retrofitManager = getInstance();
        Map<Context, CompositeDisposable> compositeDisposableMap = retrofitManager.getCompositeDisposableMap();
        if (!compositeDisposableMap.containsKey(context)) {
            compositeDisposableMap.put(context, new CompositeDisposable());
        }
        return compositeDisposableMap.get(context);
    }

    private Map<Context, CompositeDisposable> getCompositeDisposableMap() {
        if (compositeDisposableMap == null) compositeDisposableMap = new HashMap<>();
        return compositeDisposableMap;
    }

    private OkHttpClient getOkHttpClient(Context context) {
        if (sOkHttpClient == null) {
            Cache cache = new Cache(new File(context.getCacheDir(), "HttpCache"),
                    1024 * 1024 * 100);
            if (sOkHttpClient == null) {

                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY);

                sOkHttpClient = new OkHttpClient.Builder().cache(cache)
                        .connectTimeout(HTTP_CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
                        .readTimeout(HTTP_CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
                        .writeTimeout(HTTP_CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
                        .addNetworkInterceptor(commonHeaderWriteInterceptor)
                        .addInterceptor(interceptor).build();
            }
        }
        return sOkHttpClient;
    }

    private boolean isNetworkAvailable() {
        boolean isNetworkAvailable = false;
        Context context = ContextUtil.getContext();
        if (context != null) {
            isNetworkAvailable = CNetStatus.getInstance().isAliveNetwork(context);
        }
        return isNetworkAvailable;
    }

    private final Interceptor commonHeaderWriteInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

//            Logg.y("## Retrofit request : "+ bodyToString(request));

            boolean isNetworkAvailable = isNetworkAvailable();
            if (isNetworkAvailable) {
                Request.Builder builder = request.newBuilder();
                builder = appendCommonHeaderInfo(builder);
                request = builder.build();
            } else {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }

            Response originalResponse = chain.proceed(request);
            if (isNetworkAvailable) {
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .code(originalResponse.code())
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
                        .code(originalResponse.code())
                        .removeHeader("Pragma")
                        .build();
            }
        }

//        private String bodyToString(final Request request) {
//            try {
//                final Request copy = request.newBuilder().build();
//                final Buffer buffer = new Buffer();
//                copy.body().writeTo(buffer);
//                return buffer.readUtf8();
//            } catch (final IOException e) {
//                return "did not work";
//            }
//        }

        private Request.Builder appendCommonHeaderInfo(Request.Builder builder) {
            if (builder == null) return null;

            //FIXME...구글포토 때문에 일단 나감..
//            Context context = ContextUtil.getContext();
//            if (context != null) {
//                builder.addHeader("X-SNAPS-CHANNEL", "ANDROID");
//                builder.addHeader("X-SNAPS-VERSION", Config.getAPP_VERSION());
//                builder.addHeader("X-SNAPS-DEVICE",  System.getProperty("http.agent"));
//                builder.addHeader("X-SNAPS-DEVICE-TOKEN", "should insert FCM-token"); //FIXME...FCM 작업 할때 처리 필요..
//                builder.addHeader("X-SNAPS-DEVICE-UUID", SystemUtil.getIMEI(context));
//                builder.addHeader("X-SNAPS-OS-VERSION", String.valueOf(android.os.Build.VERSION.SDK_INT));
//            }
//
//            final String userToken = SnapsLoginManager.getUserToken();
//            if (!StringUtil.isEmptyAfterTrim(userToken)) {
//                builder.addHeader("X-SNAPS-TOKEN", userToken);
//            }

            return builder;
        }
    };
}
