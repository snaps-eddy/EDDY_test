package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.strategies;

import com.snaps.common.data.img.ImageSelectSNSImageData;
import com.snaps.common.data.img.MyNetworkAlbumData;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.IImageData;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectNetworkPhotoAttribute;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectSNSData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectGetAlbumListListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectLoadPhotosListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.mobile.utils.sns.googlephoto.GoogleAPITokenInfo;
import com.snaps.mobile.utils.sns.googlephoto.GooglePhotoUtil;
import com.snaps.mobile.utils.sns.googlephoto.exception.SnapsGooglePhotoException;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIListener;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIResult;
import com.snaps.mobile.utils.sns.googlephoto.model.GooglePhotoImageListModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import errorhandle.logger.Logg;


/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectSNSPhotoForGooglePhoto extends ImageSelectSNSPhotoBase {
    private static final String TAG = ImageSelectSNSPhotoForGooglePhoto.class.getSimpleName();

    public static final String ALL_PHOTO_DUMMY_COUNT = String.valueOf(Integer.MAX_VALUE - 2);
    private boolean isRetryLoadImage = false;

    public ImageSelectSNSPhotoForGooglePhoto(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public void initialize(ImageSelectSNSData snsData, IImageSelectLoadPhotosListener listener) {
        super.initialize(snsData, listener);

        isRetryLoadImage = false;
    }

    @Override
    public String getMapKey(ImageSelectSNSImageData imageData) {
        if (imageData == null) return "";
        return getSNSTypeCode() + "_" + imageData.getId();
    }

    @Override
    public String getObjectId(String url) {
        if (url == null) return "";
        return Integer.toString(url.hashCode());
    }

    private void retryLoadImageAfterSilentSignIn(final IImageSelectGetAlbumListListener albumListListener) throws Exception {
        GooglePhotoUtil.silentSignIn(new GooglePhotoAPIListener() {
            @Override
            public void onPrepare() {
            }

            @Override
            public void onGooglePhotoAPIResult(boolean isSuccess, GooglePhotoAPIResult resultObj) {
                if (isSuccess && resultObj != null) {
                    GoogleAPITokenInfo.refreshAccessTokenWithAuthCodeAsync(resultObj.getAuthCode(), new GooglePhotoAPIListener() {
                        @Override
                        public void onPrepare() {
                        }

                        @Override
                        public void onGooglePhotoAPIResult(boolean isSuccess, GooglePhotoAPIResult resultObj) {
                            if (isSuccess && resultObj != null) {
                                PrefUtil.setGooglePhotoAuthCode(activity, resultObj.getAuthCode());
                                PrefUtil.setGooglePhotoAcccessToken(activity, resultObj.getAuthAccessToken());
                                isRetryLoadImage = true;
                                loadImageIfExistCreateAlbumList(albumListListener);
                            } else {
                                showTokenErrorAlert();
                            }
                        }
                    });
                } else {
                    showTokenErrorAlert();
                }
            }
        });
    }

    @Override
    public void loadImageIfExistCreateAlbumList(final IImageSelectGetAlbumListListener albumListListener) {
        if (albumListListener != null) {
            albumListListener.onPreprare();
        }

        try {
            GooglePhotoUtil.getAlbumList(activity, true, new SnapsCommonResultListener<ArrayList<IAlbumData>>() {
                @Override
                public void onResult(ArrayList<IAlbumData> iAlbumData) {
                    //전체 사진 처리
                    if (iAlbumData == null) {
                        iAlbumData = new ArrayList<IAlbumData>();
                    }
                    MyNetworkAlbumData myNetworkAlbumData = new MyNetworkAlbumData();
                    myNetworkAlbumData.ALBUM_ID = "";
                    myNetworkAlbumData.USER_ID = "";
                    myNetworkAlbumData.THUMBNAIL_IMAGE_URL = "";
                    myNetworkAlbumData.ALBUM_NAME = activity.getString(R.string.photo_all_photos);
                    myNetworkAlbumData.PHOTO_CNT = ALL_PHOTO_DUMMY_COUNT;
                    iAlbumData.add(0, myNetworkAlbumData);

                    if (albumListListener != null) {
                        albumListListener.onCreatedAlbumList(iAlbumData);
                    }
                }

                @Override
                public void onException(Exception e) {
                    super.onException(e);
                    if (isRetryLoadImage) {
                        showTokenErrorAlert();
                        return;
                    }

                    SnapsGooglePhotoException spe = (SnapsGooglePhotoException) e;
                    int exceptionType = spe.getExceptionType();
                    if (exceptionType == SnapsGooglePhotoException.SNAPS_GOOGLE_PHOTO_EXCEPTION_UNAUTHENTICATED) {
                        try {
                            retryLoadImageAfterSilentSignIn(albumListListener);
                        } catch (Exception ee) {
                            showTokenErrorAlert();
                        }

                    } else if (exceptionType == SnapsGooglePhotoException.SNAPS_GOOGLE_PHOTO_EXCEPTION_RESOURCE_EXHAUSTED) {
                        MessageUtil.toast(activity, "Limited API Call");
                    }
                }
            });

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void showTokenErrorAlert() {
        try {
            activity.runOnUiThread(() -> MessageUtil.alertnoTitleOneBtn(activity, activity.getString(R.string.google_auth_token_has_expired), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    GoogleAPITokenInfo.deleteGoogleAllAuthInfo(activity);
                    FragmentUtil.onBackPressed(activity);
                }
            }));
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private boolean isTokenExpiredException(Exception e) {
        return e != null && e instanceof SnapsGooglePhotoException && ((SnapsGooglePhotoException) e).getExceptionType() == SnapsGooglePhotoException.SNAPS_GOOGLE_PHOTO_EXCEPTION_UNAUTHENTICATED;
    }

    @Override
    public int getSNSTypeCode() {
        return Const_VALUES.SELECT_GOOGLEPHOTO;
    }


    @Override
    public boolean isExistAlbumList() {
        return true;
    }

    @Override
    public int getTitleResId() {
        return R.string.google_photo;
    }


    @Override
    public void loadImage(ImageSelectNetworkPhotoAttribute attri) {
        if (!isAliveNetwork()) {
            if (listener != null) {
                listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.NETWORK_ERROR);
            }
            return;
        }

        this.attribute = attri;
        isFirstLoding = attribute.getPage() == 0;

        boolean isTokenError = false;
        if (isFirstLoding) {
            if (listener != null)
                listener.onLoadPhotoPreprare();
        }
        if (ImageSelectSNSPhotoForGooglePhoto.this.attribute == null) return;
        IAlbumData albumData = ImageSelectSNSPhotoForGooglePhoto.this.attribute.getAlbumCursorInfo();
        if (albumData == null) return;

        try {
            GooglePhotoUtil.getImageList(activity, albumData.getAlbumId(), attribute.getNextKey(), isFirstLoding, new SnapsCommonResultListener<GooglePhotoImageListModel>() {
                @Override
                public void onResult(GooglePhotoImageListModel model) {
                    convertImageList(model, albumData);
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (isTokenExpiredException(e)) {
                isTokenError = true;
            }
        }

        if (isSuspended) return;

        if (isTokenError) {
            showTokenErrorAlert();
            return;
        }
    }

    private void convertImageList(GooglePhotoImageListModel model, IAlbumData albumData) {

        if (isFirstLoding && ((model.getList() == null || model.getList().size() < 1) && adapter != null && adapter.getItemCount() < 2)) { //헤더가 들어있어서....
            if (listener != null) {
                listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.EMPTY);
                return;
            }
        }

        if (model.getList() != null) {
            ArrayList<ImageSelectSNSImageData> snsImageList = new ArrayList<>();
            for (IImageData data : model.getList()) {
                if (isSuspended) return;

                if (data == null) continue;

                ImageSelectSNSImageData imageData = new ImageSelectSNSImageData();
                imageData.setId(data.getImageId());
                imageData.setOrgImageUrl(data.getImageOriginalPath());
                imageData.setThumbnailImageUrl(data.getImageThumbnailPath());
                imageData.setOrgImageWidth(data.getImageOriginalWidth());
                imageData.setOrgImageHeight(data.getImageOriginalHeight());
                imageData.setStrCreateAt(data.getImageCreateAt());
                imageData.setlCreateAt(convertStrDateToLongDate(data.getImageCreateAt()));
                imageData.setMineType(data.getMineType());
                snsImageList.add(imageData);
            }

            String nextKey = model.getNextKey();
            if (adapter != null && nextKey != null) {
                try {
                    if (attribute != null) {
                        if (adapter.getItemCount() < Integer.parseInt(albumData.getPhotoCnt()))
                            attribute.setNextKey(nextKey); //a는 의미 없다..뭔가 있어야 다음거를 로딩하니까 넣은 것 뿐.
                        else
                            attribute.setNextKey(null);
                    }
                } catch (NumberFormatException e) {
                    Dlog.e(TAG, e);
                }
                adapter.addAll(snsImageList);
            } else {
                if (attribute != null) {
                    attribute.setNextKey(null);
                }
            }
        }

        if (isSuspended) return;

        if (listener != null)
            listener.onFinishedLoadPhoto(isFirstLoding ? IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.FIRST_LOAD_COMPLATED : IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.MORE_LOAD_COMPLATE);
    }

    private long convertStrDateToLongDate(String createAt) {
        if (StringUtil.isEmpty(createAt)) return 0;
        try {
            String creatAtConvert = createAt.replace("T", " ").replace("Z", "");
            SimpleDateFormat formatter_one = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter_one.parse(creatAtConvert);
            return date.getTime();

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    @Override
    public void setBaseAlbumIfExistAlbumList(ArrayList<IAlbumData> list) {
        super.setBaseAlbumIfExistAlbumList(list);

        if (list == null || list.size() < 2) return; //1개밖에 없으면 정렬의 의미가 없다.

        //iterater에서 데이터 꺼내면 expseption 날 수 있으니까 거꾸로~
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            IAlbumData data = list.get(ii);
            if (data == null || data.getAlbumName() == null) continue;

            //구글 포토는 auto backup이라는 폴더에 모든 사진이 담기기 때문에 제일 위로 올려준다.
            if (data.getAlbumName().equalsIgnoreCase(ISnapsImageSelectConstants.GOOGLE_PHOTO_AUTO_BACKUP)) {
                IAlbumData findData = list.remove(ii);
                list.add(0, findData);
                break;
            }
        }
    }
}
