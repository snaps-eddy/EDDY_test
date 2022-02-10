package com.snaps.mobile.product_native_ui.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsProductListParams;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductRoot;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductDesignList;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductSizeList;

import java.io.IOException;
import java.io.Reader;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import static com.snaps.common.utils.constant.SnapsAPI.GET_API_PRODUCT_DETAIL;
import static com.snaps.common.utils.constant.SnapsAPI.GET_API_PRODUCT_SUB_LIST;

/**
 * Created by ysjeong on 16. 10. 17..
 */
public class SnapsProductNativeUIUtil {
    private static final String TAG = SnapsProductNativeUIUtil.class.getSimpleName();
    public interface ISnapsProductNativeUIInterfaceCallback {
        void onNativeProductInfoInterfaceResult(boolean result, SnapsProductNativeUIBaseResultJson resultObj);
    }

//	public static String getTestString(Context context) {
//		AssetManager assetManager = context.getAssets();
//		OutputStream outStream = null;
//		BufferedReader in = null;
//		try {
//			InputStream ims = assetManager.open("photobook.json");
//
//			in = new BufferedReader(new InputStreamReader(ims, "UTF-8"));
//			String str;
//			StringBuffer buf = new StringBuffer();
//
//			while ((str = in.readLine()) != null) {
//				buf.append(str);
//			}
//
//			return buf.toString();
//		} catch (IOException e) {
//			Dlog.e(TAG, e);
//		} finally {
//			try {
//				if (in != null)
//					in.close();
//
//				if (outStream != null)
//					outStream.close();
//			} catch (IOException e) {
//				Dlog.e(TAG, e);
//			}
//		}
//        return null;
//	}

    public static void requestProductDetail(final Context context, final SnapsProductListParams productListParams, final ISnapsProductNativeUIInterfaceCallback callback) {

        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            SnapsProductRoot jsonData = null;

            @Override
            public void onPre() {}

            @Override
            public boolean onBG() {
                String url = productListParams.getDetailInterfaceUrl();
//                String url = "https://www.dropbox.com/s/6mndyt79594dzjl/diysticker_test.json?dl=1";
                Reader resultJsonReader = null;

                try {
                    resultJsonReader = HttpUtil.connectGetReader(url, null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    jsonData = (SnapsProductRoot) getResultObject(resultJsonReader, SnapsProductRoot.class);
                    if (jsonData != null) {
                        jsonData.createProductOptionControls();
                        return true;
                    }
                } catch (JsonSyntaxException e) {
                    Dlog.e(TAG, e);
                } finally {
                    if (resultJsonReader != null) {
                        try {
                            resultJsonReader.close();
                        } catch (IOException e) {
                            Dlog.e(TAG, e);
                        }
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (callback != null)
                    callback.onNativeProductInfoInterfaceResult(result, jsonData);
            }
        });
    }

    private static SnapsProductNativeUIBaseResultJson getResultObject(Reader resultJsonStr, final Class<?> classOfT) {
        if (resultJsonStr == null) return null;

        JsonParser jsonParser = new JsonParser();
        JsonElement rootElement = jsonParser.parse(resultJsonStr);
        if (rootElement == null) return null;

        JsonObject rootObejct = rootElement.getAsJsonObject();
        if (rootObejct == null) return null;

		JsonElement responseElement = rootObejct.get("rsp");
        if (responseElement == null) return null;

        Gson gson = new Gson();
        return (SnapsProductNativeUIBaseResultJson) gson.fromJson(responseElement.toString(), classOfT);
    }

    public static void requestProductList(final Context context,
                                          final boolean isSizeType,
                                          final SnapsProductListParams productListParams,
                                          final ISnapsProductNativeUIInterfaceCallback callback) {

        ATask.executeVoidWithThreadPoolBooleanDefProgress(context, new ATask.OnTaskResult() {
            SnapsProductNativeUIBaseResultJson jsonData = null;

            @Override
            public void onPre() { }

            @Override
            public boolean onBG() {
                if (productListParams == null) return false;

                String url = getProductListPageUrl(isSizeType, productListParams);
                Reader resultJsonReader = null;

                try {
                    resultJsonReader = HttpUtil.connectGetReader(url, null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//                String resultJson = getTestString(context);
//                    Logg.y("###### SnapsProductNativeUI debug : " + resultJsonReader);

                    Class<? extends SnapsProductNativeUIBaseResultJson> classType = isSizeType ? SnapsProductSizeList.class : SnapsProductDesignList.class;
                    jsonData = getResultObject(resultJsonReader, classType);
                    if (jsonData != null) return true;
                } catch (JsonSyntaxException e) {
                    Dlog.e(TAG, e);
                } finally {
                    if (resultJsonReader != null) {
                        try {
                            resultJsonReader.close();
                        } catch (IOException e) {
                            Dlog.e(TAG, e);
                        }
                    }

                    try {
                        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.v1_product_click)
                                .appendPayload(WebLogConstants.eWebLogPayloadType.PRODUCT_CLICK, url));
                    }catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (callback != null)
                    callback.onNativeProductInfoInterfaceResult(result, jsonData);
            }
        });
    }

    public static void requestProductListDesignList(final Context context,
                                          final boolean isSizeType,
                                          final SnapsProductListParams productListParams,
                                          final ISnapsProductNativeUIInterfaceCallback callback) {
            boolean loadComplete = false;
            SnapsProductNativeUIBaseResultJson jsonData = null;

                if (productListParams == null) return ;

                String url = getProductListPageUrl(isSizeType, productListParams);
                Reader resultJsonReader = null;

                try {
                    resultJsonReader = HttpUtil.connectGetReader(url, null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//                String resultJson = getTestString(context);
//                    Logg.y("###### SnapsProductNativeUI debug : " + resultJsonReader);

                    Class<? extends SnapsProductNativeUIBaseResultJson> classType = isSizeType ? SnapsProductSizeList.class : SnapsProductDesignList.class;
                    jsonData = getResultObject(resultJsonReader, classType);
                    if (jsonData != null) loadComplete = true;
                } catch (JsonSyntaxException e) {
                    Dlog.e(TAG, e);
                } finally {
                    if (resultJsonReader != null) {
                        try {
                            resultJsonReader.close();
                        } catch (IOException e) {
                            Dlog.e(TAG, e);
                        }
                    }
                }
                if (callback != null)
                    callback.onNativeProductInfoInterfaceResult(loadComplete, jsonData);
    }

    public static void requestCardProductListDesignList(final Context context,
                                                    final SnapsProductListParams productListParams,
                                                    final ISnapsProductNativeUIInterfaceCallback callback) {
        boolean loadComplete = false;
        SnapsProductNativeUIBaseResultJson jsonData = null;

        if (productListParams == null) return ;

        String url = getCardProductListPageUrl(productListParams);
        Reader resultJsonReader = null;

        try {
            resultJsonReader = HttpUtil.connectGetReader(url, null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//                String resultJson = getTestString(context);
//                    Logg.y("###### SnapsProductNativeUI debug : " + resultJsonReader);

            Class<? extends SnapsProductNativeUIBaseResultJson> classType = SnapsProductDesignList.class;
            jsonData = getResultObject(resultJsonReader, classType);
            if (jsonData != null) loadComplete = true;
        } catch (JsonSyntaxException e) {
            Dlog.e(TAG, e);
        } finally {
            if (resultJsonReader != null) {
                try {
                    resultJsonReader.close();
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                }
            }
        }
        if (callback != null)
            callback.onNativeProductInfoInterfaceResult(loadComplete, jsonData);
    }

    private static String getProductListPageUrl(boolean isSizeType, SnapsProductListParams productListParams) {
        if (productListParams == null) return "";
        StringBuilder urlBuild = new StringBuilder();

//        //일반 사진 인화, 탁상 달력, 스퀘어 프린트팩
//        boolean isProductSubListUrl = isSizeType || productListParams.isProductSubList();

        urlBuild.append((isSizeType? GET_API_PRODUCT_SUB_LIST() : SnapsAPI.GET_API_PRODUCT_LIST())).append("/").append(productListParams.getClssCode())
                .append("?chnlCode=").append(Config.getCHANNEL_CODE())
                .append("&ostype=190002").append( "&debug=" ).append( Config.isRealServer() ? 0 : 1 );
        return urlBuild.toString();
    }

    private static String getCardProductListPageUrl(SnapsProductListParams productListParams) {
        if (productListParams == null) return "";
        StringBuilder urlBuild = new StringBuilder();
        if(Config.isRealServer()) {
            urlBuild.append(SnapsAPI.GET_API_PRODUCT_LIST()).append("/").append(productListParams.getClssCode())
                    .append("?hppntype=190002")
                    .append("&chnlCode=").append(Config.getCHANNEL_CODE())
                    .append("&listtype=ALL")
                    .append("&prodcode=").append(productListParams.getProdCode());
        } else {
            urlBuild.append(SnapsAPI.GET_API_PRODUCT_LIST()).append("/").append(productListParams.getClssCode())
                    .append("?hppntype=190002")
                    .append("&debug=").append(Config.isRealServer() ? 0 : 1)
                    .append("&chnlCode=").append(Config.getCHANNEL_CODE())
                    .append("&listtype=ALL")
                    .append("&prodcode=").append(productListParams.getProdCode());
        }
        return urlBuild.toString();
    }

    public static String getProductDetailPageUrl(SnapsProductListParams productListParams) {
        if (productListParams == null) return "";
        StringBuilder urlBuild = new StringBuilder();
        urlBuild.append(GET_API_PRODUCT_DETAIL()).append("/").append(productListParams.getClssCode());
        if( !StringUtil.isEmpty(productListParams.getTemplateCode()) ) urlBuild.append("/").append(productListParams.getTemplateCode());
        urlBuild.append("?chnlCode=").append(Config.getCHANNEL_CODE()).append("&ostype=190002");
        if( !StringUtil.isEmpty(productListParams.getProdCode()) ) urlBuild.append( "&prodCode=" ).append( productListParams.getProdCode() );
        return urlBuild.append( "&debug=" ).append( Config.isRealServer() ? 0 : 1 ).toString();
    }
}
