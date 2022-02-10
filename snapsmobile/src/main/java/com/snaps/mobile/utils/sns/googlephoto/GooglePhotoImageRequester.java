package com.snaps.mobile.utils.sns.googlephoto;

import android.content.Context;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.snaps.common.data.img.MyNetworkAlbumData;
import com.snaps.common.data.img.MyNetworkImageData;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.IImageData;
import com.snaps.mobile.utils.network.provider.SnapsRetrofit;
import com.snaps.mobile.utils.network.provider.SnapsRetrofitRequestBuilder;
import com.snaps.mobile.utils.network.provider.listener.SnapsRetrofitResultListener;
import com.snaps.mobile.utils.network.retrofit2.data.response.common.SnapsNetworkAPIBaseResponse;
import com.snaps.mobile.utils.network.retrofit2.exception.SnapsNetworkThrowable;
import com.snaps.mobile.utils.network.retrofit2.interfacies.enums.eSnapsRetrofitAPI;
import com.snaps.mobile.utils.sns.googlephoto.exception.SnapsGooglePhotoException;
import com.snaps.mobile.utils.sns.googlephoto.model.GooglePhotoImageListModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import errorhandle.logger.Logg;

import static com.snaps.mobile.utils.sns.googlephoto.GoogleApiConstants.GOOGLE_PHOTO_LIBRARY_DOMAIN;

/**
 * Created by ysjeong on 2017. 5. 22..
 */

public class GooglePhotoImageRequester {
    private static final String TAG = GooglePhotoImageRequester.class.getSimpleName();

    private int currentPos = 1;

    private PhotosLibraryClient photosLibraryClient = null;

    public void finalizeInstance() {
        photosLibraryClient = null;
    }

    public void getImageList(Context context, String albumurl, String nextKey, boolean isFirst, SnapsCommonResultListener<GooglePhotoImageListModel> listener) throws Exception {
        if (isFirst)
            currentPos = 1;
        getImages(context, albumurl, nextKey, listener);
    }

    public void getAlbumList(Context context, boolean isFirst, SnapsCommonResultListener<ArrayList<IAlbumData>> listener) throws Exception {
        getAlbums(context, listener);
    }

    private void getAlbums(Context context, SnapsCommonResultListener<ArrayList<IAlbumData>> listener) throws Exception {
        SnapsRetrofitRequestBuilder requestBuilder = SnapsRetrofitRequestBuilder.createBuilderWithProgress(context).setBaseUrl(GOOGLE_PHOTO_LIBRARY_DOMAIN).create();
        SnapsRetrofit.with(eSnapsRetrofitAPI.GET_GOOGLE_PHOTO_ALBUM).request(requestBuilder, new SnapsRetrofitResultListener<SnapsNetworkAPIBaseResponse>() {
            @Override
            public void onResultSuccess(SnapsNetworkAPIBaseResponse result) {
                //FIXME...Response를 IAlbumData로 변환하던가, JSON 구조로 바꾸던가 해야 함..

                listener.onResult(convertJsonAlbumList(result.getResultString()));
            }

            @Override
            public void onResultFailed(SnapsNetworkThrowable throwable) {
                //Crashlytics 로그를 보면 errorCode가 null인 경우가 발생한다. 일단 강제 종료되는 것만 막는다...
                //TODO::에러 메시지를 보여줘야 하는데..
                int errorCode = 0;
                String errorCodeText = throwable.getErrorCode();
                if (errorCodeText != null) {
                    try {
                        errorCode = Integer.parseInt(errorCodeText);
                    }catch (NumberFormatException e) {
                        Dlog.e(TAG, e);
                    }
                }

                listener.onException(SnapsGooglePhotoException.newInstanceWithExceptionType(errorCode));
            }
        });
    }

    private ArrayList<IAlbumData> convertJsonAlbumList(String json) {
        ArrayList<IAlbumData> list = new ArrayList<>();

        try {
            JSONObject jsonObjectAlbum = new JSONObject(json);
            JSONArray jsonArray = jsonObjectAlbum.getJSONArray("albums");

            for (int i = 0; i < jsonArray.length(); i++) {
                MyNetworkAlbumData albumData = new MyNetworkAlbumData();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int photoCount = 0;

                if (jsonObject.has("mediaItemsCount")) {
                    photoCount = jsonObject.getInt("mediaItemsCount");
                }

                albumData.ALBUM_ID = jsonObject.getString("id");
                albumData.ALBUM_NAME = jsonObject.getString("title");
                albumData.THUMBNAIL_IMAGE_URL = jsonObject.getString("coverPhotoBaseUrl");
                albumData.PHOTO_CNT = Integer.toString(photoCount);
                list.add(albumData);

            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return list;
    }

    private void getImages(Context context, final String albumurl, String nextKey, final SnapsCommonResultListener<GooglePhotoImageListModel> listener) throws Exception {
        //FIXME...작업 해야 함..
        SnapsRetrofitRequestBuilder requestBuilder;
        if (nextKey == null) {
            requestBuilder = SnapsRetrofitRequestBuilder.createBuilderWithProgress(context).setBaseUrl(GOOGLE_PHOTO_LIBRARY_DOMAIN).appendSimplePostParam("albumId", albumurl).create();
        } else {
            requestBuilder = SnapsRetrofitRequestBuilder.createBuilderWithProgress(context).setBaseUrl(GOOGLE_PHOTO_LIBRARY_DOMAIN).appendSimplePostParam("albumId", albumurl).appendSimplePostParam("pageToken", nextKey).create();
        }
        SnapsRetrofit.with(eSnapsRetrofitAPI.GET_GOOGLE_PHOTO_LIST).request(requestBuilder, new SnapsRetrofitResultListener<SnapsNetworkAPIBaseResponse>() {
            @Override
            public void onResultSuccess(SnapsNetworkAPIBaseResponse result) {
                listener.onResult(convertJsonImageList(result.getResultString()));
            }

            @Override
            public void onResultFailed(SnapsNetworkThrowable throwable) {
            }
        });
    }

    private GooglePhotoImageListModel convertJsonImageList(String json) {
        GooglePhotoImageListModel model = new GooglePhotoImageListModel();
        ArrayList<IImageData> list = new ArrayList<>();
        String nextKey = null;
        try {
            JSONObject jsonObjectAlbum = new JSONObject(json);
            nextKey = jsonObjectAlbum.optString("nextPageToken");

            JSONArray jsonArray = jsonObjectAlbum.optJSONArray("mediaItems");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    MyNetworkImageData imageData = new MyNetworkImageData();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String baseUrl = jsonObject.getString("baseUrl");
                    imageData.setImageId(jsonObject.getString("id"));
                    imageData.setImageThumbnailPath(baseUrl);
                    imageData.setMineType(jsonObject.getString("mimeType"));
                    JSONObject jsonObject1ImageData = jsonObject.getJSONObject("mediaMetadata");
                    String width = jsonObject1ImageData.getString("width");
                    String height = jsonObject1ImageData.getString("height");
                    String originalPath = baseUrl + "=w" + width + "-h" + height + "-c";
                    imageData.setImageOriginalPath(originalPath);
                    imageData.setImageOriginalWidth(jsonObject1ImageData.getString("width"));
                    imageData.setImageOriginalHeight(jsonObject1ImageData.getString("height"));
                    imageData.setImageCreateAt(jsonObject1ImageData.getString("creationTime"));
                    list.add(imageData);
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        model.setList(list);
        model.setNextKey(nextKey);
        return model;
    }

    private void setMediaGroup(MyNetworkImageData imgData, Element element) {
        NodeList mediaGroupChildList = element.getChildNodes();

        int thumbWidth = 0;

        for (int i = 0; i < mediaGroupChildList.getLength(); i++) {
            Element mediaGroup = (Element) mediaGroupChildList.item(i);
            String tag = mediaGroup.getNodeName();
            if (tag.equals("media:content")) {
                imgData.ORIGIN_IMAGE_URL = mediaGroup.getAttribute("url");
                imgData.ORIGIN_IMAGE_HEIGHT = mediaGroup.getAttribute("height");
                imgData.ORIGIN_IMAGE_WIDTH = mediaGroup.getAttribute("width");
                String type = mediaGroup.getAttribute("type");
                if (!type.contains("image")) {
                    imgData.isInclude = false;
                }

            } else if (tag.equals("media:thumbnail")) {
                // String h = mediaGroup.getAttribute("height");
                String w = mediaGroup.getAttribute("width");
                if (thumbWidth < Integer.parseInt(w)) {
                    imgData.THUMBNAIL_IMAGE_URL = mediaGroup.getAttribute("url");
                    thumbWidth = Integer.parseInt(w);
                }
            }

        }
    }
}
