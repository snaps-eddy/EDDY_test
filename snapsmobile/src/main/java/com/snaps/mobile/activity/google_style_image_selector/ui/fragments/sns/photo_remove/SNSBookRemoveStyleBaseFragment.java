package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.photo_remove;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.SNSBookFragmentActivity;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryDialog;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectGetAlbumListListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectStateChangedListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.PhotoArrayForSNSBookAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.CustomGridLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.ImageSelectFragmentPhotoBaseSpacingItemDecoration;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectBaseFragment;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.component.SnapsNoPrintDialog;

import java.util.ArrayList;
import java.util.HashMap;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import font.FProgressDialog;

/**
 * Created by songhw on 2016. 3. 31..
 */
public abstract class SNSBookRemoveStyleBaseFragment extends ImageSelectBaseFragment implements OnMoreListener, IImageSelectFragmentItemClickListener {
    private static final String TAG = SNSBookRemoveStyleBaseFragment.class.getSimpleName();
    private static final int ITEM_LEFT_TO_LOAD_MORE = 3;

    public static final int CNT = 30; //30개씩 끊어서 보여준다.

    // JSON Node names
    public static final String TAG_ID = "Id";
    public static final String TAG_NAME = "Title";
    public static final String TAG_Image = "Original";
    public static final String TAG_Thumbnail = "Thumnail";
    public static final String TAG_Width = "width";
    public static final String TAG_Height = "height";
    public static final String TAG_OS_TYPE = "OsType";

    public static final String PRODUCT_DATA = "product_data";

    protected int type = SNSBookFragmentActivity.TYPE_KAKAO_STORY;

    protected int pageIdx = 0;
    protected int m_iCursor = 0;
    protected int iStartIdx;
    protected int iEndIdx;

    protected boolean isMoreImg = true;
    protected boolean m_isFirstLoad = true;

    protected String themeKey = "";

    protected PhotoArrayForSNSBookAdapter photoAdapter;
    protected FProgressDialog pd;
    protected SnapsSuperRecyclerView recyclerView = null;

    protected TextView noPhotoMsg;

    @SuppressWarnings("rawtypes")
    protected ArrayList<HashMap<String, String>> m_arrAllDataList;
    private int m_iMoreAskBeforeCount = 0;

    private ImageSelectFragmentPhotoBaseSpacingItemDecoration itemDecoration = null;

    protected abstract boolean addImageData();
    protected abstract boolean checkDataManager();
    protected abstract int getDataCount();
    protected abstract void setLoadIndex( int corsor, int dataCount );
    protected abstract void getData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        if( !checkDataManager() ) return;

        m_isFirstLoad = true;

        imageSelectActivityV2 = (ImageSelectActivityV2) getActivity();

        m_arrAllDataList = new ArrayList<>();

        Setting.set(imageSelectActivityV2, "themekey", "");

        isMoreImg = true;
        pd = new FProgressDialog(imageSelectActivityV2);
        pd.setMessage(getString(R.string.please_wait));
        pd.setCancelable(false);
        pd.show();

        getData();

        imageSelectActivityV2.registerListUpdateListener(this);
    }

    private void setRecyclerViewMoreListener(boolean isExistNext) {
        if(recyclerView == null) return;
        if(isExistNext) {
            recyclerView.setupMoreListener(this, ITEM_LEFT_TO_LOAD_MORE);
        } else {
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
        }
    }

    private void showUnSelecteLockMsg() {
        if (type == SNSBookFragmentActivity.TYPE_DIARY) {
            try {
                String title = String.format( getActivity().getResources().getString(R.string.is_not_allow_publish_detail_cause_other_os_title), getActivity().getString(R.string.ios_eng) );
                String msg = String.format( getActivity().getResources().getString(R.string.is_not_allow_publish_detail_cause_other_os_msg), getActivity().getString(R.string.ios_eng), getActivity().getString(R.string.android_os_eng) );

                SnapsDiaryDialog.showDialogOneBtn(getActivity(),
                        title,
                        msg, null);
            } catch (WindowManager.BadTokenException e) {
                Dlog.e(TAG, e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imageSelectActivityV2 != null) {
            imageSelectActivityV2.updateTitle(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_imagedetail_for_kakao, container, false);

        recyclerView = (SnapsSuperRecyclerView) v.findViewById(R.id.custom_snaps_native_super_recycler_view);
        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(imageSelectActivityV2, Const_VALUE.IMAGE_GRID_COLS);
        recyclerView.setLayoutManager(layoutManager);

        return v;
    }

    //아이템 갱신 요청
    @Override
    public void onUpdatedPhotoList(String imageKey) {
        if (photoAdapter == null) return;
        if (imageKey != null) {
            photoAdapter.notifyDataSetChangedByImageKey(imageKey);
        } else {
            photoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setBaseAlbumIfExistAlbumList(ArrayList<IAlbumData> list) {}

    @Override
    public void loadImageIfExistCreateAlbumList(IImageSelectGetAlbumListListener albumListListener) {}

    @Override
    public IAlbumData getCurrentAlbumCursor() {
        return null;
    }

    @Override
    public boolean isExistAlbumList() {
        return false;
    }

    @Override
    public void onChangedAlbumCursor(IAlbumData cursor) {}

    @Override
    public void onClickFragmentItem(ImageSelectAdapterHolders.PhotoFragmentItemHolder vh) {
        if (vh == null) return;

        if (vh.isDisableClick()) {
            if (Config.TEST_TUTORIAL || !Setting.getBoolean(imageSelectActivityV2, "noprint")) {
                SnapsNoPrintDialog dialog = new SnapsNoPrintDialog(imageSelectActivityV2);
                dialog.show();
            }
            return;
        }

        if (ImageSelectUtils.isContainsInImageHolder(vh.getMapKey())) {
            if (isUnSelecteLock(vh)) {
                showUnSelecteLockMsg();
                return;
            }

            imageSelectActivityV2.onItemUnSelectedListener(IImageSelectStateChangedListener.eCONTROL_TYPE.FRAGMENT, vh.getMapKey());
        }
        else {
            if (imageSelectActivityV2.isAddableImage()) {
                imageSelectActivityV2.putSelectedImageData(vh.getMapKey(), vh.getImgData());

                if (photoAdapter != null)
                    photoAdapter.notifyDataSetChangedByImageKey(vh.getMapKey());
            } else {
                ImageSelectUtils.showDisableAddPhotoMsg();
            }
        }

        imageSelectActivityV2.updateTitle(0);
    }

    private boolean isUnSelecteLock(ImageSelectAdapterHolders.PhotoFragmentItemHolder viewHolder) {
        if (SnapsDiaryConstants.IS_SUPPORT_IOS_VERSION) {
            return false;
        } else {
            if (type == SNSBookFragmentActivity.TYPE_DIARY) {
                if (viewHolder != null) {
                    return !SnapsDiaryConstants.isOSTypeEqualsAndroid(viewHolder.getOsType());
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        imageSelectActivityV2.unRegisterListUpdateListener(this);

        pageIdx = 0;
        isMoreImg = true;
    }

    protected boolean isDuplicateData(ArrayList<HashMap<String, String>> list, HashMap<String, String> contact) {

        if (list == null || contact == null)
            return true;

        for (HashMap<String, String> map : list) {
            if (map == null)
                continue;

            String storagePath = map.get(TAG_ID);
            String newPath = contact.get(TAG_ID);

            if (storagePath.equals(newPath))
                return true;
        }

        return false;
    }

    protected void imageLoading(int cursor) {
        int dataCount = getDataCount();
        if ( dataCount < 1 ) return;

        setLoadIndex(cursor, dataCount);

        if (m_arrAllDataList != null)
            m_iMoreAskBeforeCount = m_arrAllDataList.size();

        if(addImageData())
            setUI();
        else {
            if(m_isFirstLoad)
                showEmptyErrorMsg();
        }

        m_isFirstLoad = false;
    }

    private void showEmptyErrorMsg() {
        int toastStrResId = R.string.none_story_text;
        switch( type ) {
            case SNSBookFragmentActivity.TYPE_FACEBOOK_PHOTOBOOK:
                toastStrResId = R.string.none_post_text;
                break;
            case SNSBookFragmentActivity.TYPE_DIARY:
                toastStrResId = R.string.snaps_diary_empty_post_error_message_1;
                break;
        }

        Toast.makeText(getActivity(), getString(toastStrResId), Toast.LENGTH_LONG).show();

        setUI();
    }

    private void setUI() {
        setRecyclerViewMoreListener(isMoreImg);

        process();

        if (pd != null)
            pd.dismiss();
        imageSelectActivityV2.showTutorial(ISnapsImageSelectConstants.eTUTORIAL_TYPE.PHONE_FRAGMENT_PINCH_MOTION);
    }

    //스크롤에 의한 추가 로딩 (superRecclerView)
    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
        imageLoading(++m_iCursor);
    }

    @SuppressWarnings("unchecked")
    public void process() {
        if (recyclerView == null)
            return;

        if (photoAdapter == null) {
            photoAdapter = PhotoArrayForSNSBookAdapter.getInstance(getActivity(), m_arrAllDataList, type );
            photoAdapter.setItemClickListener(this);
            recyclerView.setAdapter(photoAdapter);

            if (itemDecoration != null) {
                recyclerView.removeItemDecoration(itemDecoration);
            }

            itemDecoration = new ImageSelectFragmentPhotoBaseSpacingItemDecoration(imageSelectActivityV2, UIUtil.convertDPtoPX(imageSelectActivityV2, 3));
            recyclerView.addItemDecoration(itemDecoration);
        } else {

            int startIdx = Math.min(m_iMoreAskBeforeCount, photoAdapter.getItemCount() - 1);
            photoAdapter.notifyItemRangeInserted(startIdx, photoAdapter.getItemCount() - 1);
        }

        if (imageSelectActivityV2 != null) {
            imageSelectActivityV2.updateTitle(0);

            if (type == SNSBookFragmentActivity.TYPE_DIARY) {
                if (SnapsDiaryConstants.IS_SUPPORT_IOS_VERSION) {
                    //FIXME
                } else {
                    //IOS 호환 관련..
                    if (m_arrAllDataList != null) {
                        for (int ii = 0; ii < m_arrAllDataList.size(); ii++) {
                            HashMap<String, String> mapData = (HashMap<String, String>) m_arrAllDataList.get(ii);
                            if (mapData == null) continue;
                            if (mapData.containsKey(KakaobookBookPhotoRemoveFragment.TAG_OS_TYPE)) {
                                String osType = mapData.get(KakaobookBookPhotoRemoveFragment.TAG_OS_TYPE);
                                if (!SnapsDiaryConstants.isOSTypeEqualsAndroid(osType)) {
                                    String mapKey = mapData.get(KakaobookBookPhotoRemoveFragment.TAG_ID);
                                    imageSelectActivityV2.putSelectedImageData(mapKey, new MyPhotoSelectImageData());
                                    imageSelectActivityV2.updateTitle(0);
                                }
                            }
                        }

                        //FIXME 자동으로 선택되게..
                        if (photoAdapter != null)
                            photoAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    protected void setType( int type ) { this.type = type; }

}
