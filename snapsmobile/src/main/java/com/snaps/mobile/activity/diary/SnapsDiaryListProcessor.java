package com.snaps.mobile.activity.diary;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.IListShapeDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.activities.SnapsDiaryConfirmEditableActivity;
import com.snaps.mobile.activity.diary.activities.SnapsDiaryConfirmViewActivity;
import com.snaps.mobile.activity.diary.activities.SnapsDiaryMainActivity;
import com.snaps.mobile.activity.diary.adapter.SnapsDiaryGridShapeAdapter;
import com.snaps.mobile.activity.diary.adapter.SnapsDiaryListShapeAdapter;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryDialog;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryProfilePopMenu;
import com.snaps.mobile.activity.diary.fragments.SnapsDiaryListFragment;
import com.snaps.mobile.activity.diary.interfaces.IOnSnapsDiaryItemSelectedListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderClickListener;
import com.snaps.mobile.activity.diary.json.SnapsDiaryCountJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListItemJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryProfileThumbnailJson;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryPageInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ysjeong on 16. 3. 7..
 */
public class SnapsDiaryListProcessor extends RecyclerView.OnScrollListener implements SnapsDiaryProfilePopMenu.ISnapsDiaryProfilePopMenuListener, IOnSnapsDiaryItemSelectedListener, ISnapsDiaryHeaderClickListener {
    private static final String TAG = SnapsDiaryListProcessor.class.getSimpleName();
    public static final int DIARY_LIST_PAGING_COUNT = 15; //12보다 작게하면 그리드에 더미가 들어감..

    private static final int LIST_SELECT_MODIFY = 0;
    private static final int LIST_SELECT_DELETE = 1;

    private final SnapsDiaryMainActivity mActivity;

    private SnapsDiaryListFragment listFragment = null;
    private SnapsDiaryProfilePopMenu popMenu = null;

    public SnapsDiaryListProcessor(SnapsDiaryMainActivity activity) {
        super();
        mActivity = activity;
        initControls();
    }

    public void destroyView() {
        if (listFragment != null)
            listFragment.destroyView();
    }

    public void requestGetUserProfileThumbnail() {
        SnapsDiaryInterfaceUtil.requestUserProfileThumbnail(mActivity, new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
            @Override
            public void onPreperation() {
                mActivity.showProgress();
                SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
                if (userInfo != null) userInfo.releaseCache();
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                mActivity.hideProgress();
                if (result && resultObj != null) {
                    SnapsDiaryProfileThumbnailJson profileThumbnailJson = (SnapsDiaryProfileThumbnailJson) resultObj;
                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
                    if (userInfo != null) {
                        String path = profileThumbnailJson.getThumbnailPath();
                        userInfo.setThumbnailPath(path);
                    }

                    requestThumbnailRefresh();
                } else {
                    MessageUtil.alert(mActivity, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK)
                                requestGetUserProfileThumbnail();
                        }
                    });
                }
            }
        });
    }

    public void requestDiaryListRefresh() {
        if(listFragment == null) return;
        SnapsDiaryListShapeAdapter listAdapter = listFragment.getListShapeAdapter();
        if (listAdapter != null)
            listAdapter.refreshData();
        SnapsDiaryGridShapeAdapter gridAdapter = listFragment.getGridShapeAdapter();
        if (gridAdapter != null)
            gridAdapter.refreshData();
    }

    public void requestDiaryHeaderRefresh() {
        if(listFragment == null) return;
        SnapsDiaryListShapeAdapter listAdapter = listFragment.getListShapeAdapter();
        if (listAdapter != null)
            listAdapter.refreshHeader();
        SnapsDiaryGridShapeAdapter gridAdapter = listFragment.getGridShapeAdapter();
        if (gridAdapter != null)
            gridAdapter.refreshHeader();
    }

    public void requestThumbnailRefresh() {
        if(listFragment == null) return;
        SnapsDiaryListShapeAdapter listAdapter = listFragment.getListShapeAdapter();
        if (listAdapter != null)
            listAdapter.refreshThumbnail();

        SnapsDiaryGridShapeAdapter gridAdapter = listFragment.getGridShapeAdapter();
        if (gridAdapter != null)
            gridAdapter.refreshThumbnail();
    }

    public void requestAdapterRefresh() {
        if(listFragment == null) return;
        listFragment.refreshAdapter();
    }

    @Override
    public void onDiaryItemSelected(final SnapsDiaryListItem item, final int position, final boolean isDetailView) {
        if(isDetailView) { //상세보기로 진입
            showDetailPage(item);
        } else { //리스트에서 바로 수정, 삭제 처리
            SnapsDiaryDialog.showListSelectPopup(mActivity, new IListShapeDialogListener() {
                @Override
                public void onClick(int select) {
                    switch (select) {
                        case LIST_SELECT_MODIFY:
                            startModifyPage(item);
                            break;
                        case LIST_SELECT_DELETE:
                            checkDiaryCount(item, position);
                            break;
                    }
                }
            });
        }
    }

    /**
     * 당일 작성한 일기를 모두 삭제하면, 잉크도 삭제해야 하기 때문에, 당일 작성한 마지막 일기인지 체크한다.
     */
    private void checkDiaryCount(final SnapsDiaryListItem item, final int position) {
        SnapsDiaryInterfaceUtil.requestGetDiaryCountSameDate(mActivity, item.getDiaryNo(), new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
            @Override
            public void onPreperation() {
                mActivity.showProgress();
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                mActivity.hideProgress();
                if (result && resultObj != null) {
                    SnapsDiaryCountJson countJson = (SnapsDiaryCountJson) resultObj;
                    String count = countJson.getCount();
                    int iCnt = 0;
                    boolean isWarningMissionFailWhenDeleted = false;
                    if (count != null) {
                        try {
                            iCnt = Integer.parseInt(count);
                            if (iCnt == 1) {
                                isWarningMissionFailWhenDeleted = SnapsDiaryDataManager.isWarningMissionFailedWhenDelete(item);
                            }
                        } catch (NumberFormatException e) {
                            Dlog.e(TAG, e);
                        }
                    }

                    int msg = 0;
                    int impectMsg = 0;
                    if (isWarningMissionFailWhenDeleted) {
                        msg = R.string.diary_alert_delete_diary_and_mission_fail;
                        impectMsg = R.string.diary_alert_delete_diary_and_mission_fail_impect;
                    } else if (iCnt == 1) {
                        msg = R.string.diary_alert_delete_diary_and_ink;
                        impectMsg = R.string.diary_alert_delete_diary_and_ink_impect;
                    } else {
                        msg = R.string.diary_alert_delete_diary;
                    }

                    SnapsDiaryDialog.showDialogWithImpect(mActivity,
                            msg,
                            impectMsg,
                            mActivity.getString(R.string.confirm),
                            mActivity.getString(R.string.cancel), new ICustomDialogListener() {
                                @Override
                                public void onClick(byte clickedOk) {
                                    if (clickedOk == ICustomDialogListener.OK) {
                                        deleteItem(item, position);
                                    }
                                }
                            });
                } else {
                    MessageUtil.alert(mActivity, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK)
                                checkDiaryCount(item, position);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }

    @Override
    public void onStripClick(int shape) {
        if(listFragment != null)
            listFragment.changeShape(shape);
    }

    @Override
    public void onProfilePopMenuClick(int position) {
        switch (position) {
            case 0 : //사진 앨범에서 선택하기
                requestUserProfileSelectPhoto();
                break;
            case 1 : //기본 앨범으로 선택
                requestDeleteUserThumbnail();
                break;
        }
    }

    @Override
    public void onThumbnailClick(ImageView targetView) {
        if (popMenu == null || targetView == null || mActivity == null) return;
        RelativeLayout rootView = (RelativeLayout) mActivity.findViewById(R.id.snaps_diary_main_act_parent_ly);
        if(rootView == null) return;
        popMenu.showPopMenu(rootView, targetView);
        popMenu.setPopMenuListener(this);
    }

    public void initDiaryList() {

        SnapsDiaryPageInfo pageInfo = new SnapsDiaryPageInfo();
        pageInfo.setIsUsePaging(true);
        pageInfo.setPagingNo(1);
        pageInfo.setPagingSize(SnapsDiaryListProcessor.DIARY_LIST_PAGING_COUNT);

        SnapsDiaryInterfaceUtil.getDiaryList(mActivity, pageInfo, new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {

            @Override
            public void onPreperation() {
                mActivity.showProgress();
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                mActivity.hideProgress();

                if (result && resultObj != null) {
                    SnapsDiaryListJson listResult = (SnapsDiaryListJson) resultObj;
                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryListInfo listInfo = dataManager.getListInfo();

                    listInfo.setCurrentPageNo(listResult.getPageNo());
                    listInfo.setTotalCount(listResult.getTotalCount());
                    listInfo.setPageSize(listResult.getPageSize());
                    listInfo.setIosCount(listResult.getIosCount());
                    listInfo.setAndroidCount(listResult.getAndroidCount());

                    List<SnapsDiaryListItemJson> list = listResult.getDiaryList();
                    if (list != null && !list.isEmpty()) {
                        listInfo.addDiaryList(list);
                    }
                    checkMoreListListener();
                    requestDiaryListRefresh();

                    mActivity.onFinishDiaryListLoad();

                } else {
                    MessageUtil.alert(mActivity, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK)
                                initDiaryList();
                        }
                    });
                }
            }
        });
    }

    public void checkMoreListListener() {
        if(listFragment != null)
            listFragment.setRecyclerViewMoreListener();
    }

    protected void requestUserProfileSelectPhoto() {
        Intent intent = new Intent(getApplicationContext(), ImageSelectActivityV2.class);
        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_SINGLE_CHOOSE_TYPE)
                .setDiaryProfilePhoto(true).create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.putExtras(bundle);

        mActivity.startActivityForResult(intent, SnapsDiaryConstants.REQUEST_CODE_SELECT_ONE_PHOTO);
    }

    protected void requestDeleteUserThumbnail() {
        SnapsDiaryInterfaceUtil.requestUpdateUserProfileThumbnail(mActivity, "", true, new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
            @Override
            public void onPreperation() {
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                if (result) {
                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
                    if (userInfo != null) {
                        userInfo.setThumbnailPath(null);
                        userInfo.releaseCache();
                        requestThumbnailRefresh();
                    }
                } else {
                    MessageUtil.alert(mActivity, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK)
                                requestDeleteUserThumbnail();
                        }
                    });
                }
            }
        });
    }

    private void initControls() {
        if (mActivity == null) return;

        popMenu = new SnapsDiaryProfilePopMenu(mActivity);

        listFragment =  SnapsDiaryListFragment.newInstance(this);
        FragmentUtil.replce(R.id.snaps_diary_recycler_fragment_ly, mActivity, listFragment);
    }

    private void deleteItem(final SnapsDiaryListItem item, final int position) {
        if(item == null) return;

        final String DIARY_NO = item.getDiaryNo();

        SnapsDiaryInterfaceUtil.requestDiaryDelete(mActivity, DIARY_NO, new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {

            @Override
            public void onPreperation() {
                mActivity.showProgress();
            }


            @Override
            public void onResult(boolean result, Object resultObj) {
                mActivity.hideProgress();

                if (result) {
                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryListInfo listInfo = dataManager.getListInfo();
                    listInfo.removeDiaryItem(DIARY_NO);

                    listInfo.setTotalCount(listInfo.getTotalCount() - 1);

                    if (SnapsDiaryConstants.IS_SUPPORT_IOS_VERSION) {
                        //FIXME

                    } else {
                        if (SnapsDiaryConstants.isOSTypeEqualsAndroid(item.getOsType())) {
                            listInfo.setAndroidCount(listInfo.getAndroidCount() - 1);
                        } else {
                            listInfo.setIosCount(listInfo.getIosCount() - 1);
                        }
                    }
                    mActivity.getUserMissionInfo(false);
                } else {
                    MessageUtil.alert(mActivity, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK)
                                deleteItem(item, position);
                        }
                    });
                }
            }
        });
    }

    private void showDetailPage(SnapsDiaryListItem item) {
        if (item == null)
            return;

        if (SnapsDiaryConstants.IS_SUPPORT_IOS_VERSION) {
        //FIXME
        } else {
            if (!SnapsDiaryConstants.isOSTypeEqualsAndroid(item.getOsType())) {
                showWriteFromOtherPlatformAlert(false);
                return;
            }
        }

        Intent ittConfirm= new Intent(mActivity, SnapsDiaryConfirmViewActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.DIARY_DATA, item);

        ittConfirm.putExtras(bundle);

        mActivity.startActivityForResult(ittConfirm, SnapsDiaryConstants.REQUEST_CODE_DIARY_UPDATE);
    }

    private void showWriteFromOtherPlatformAlert(boolean isTryModify) {
        try {

            String title = String.format(mActivity.getResources().getString(
                    isTryModify ? R.string.is_not_allow_modify_detail_cause_other_os_title : R.string.is_not_allow_into_detail_cause_other_os_title), mActivity.getString(R.string.ios_eng));
            String msg = String.format(mActivity.getResources().getString(
                    isTryModify ? R.string.is_not_allow_modify_detail_cause_other_os_msg : R.string.is_not_allow_into_detail_cause_other_os_msg), mActivity.getString(R.string.ios_eng), mActivity.getString(R.string.android_os_eng) );

            SnapsDiaryDialog.showDialogOneBtn(mActivity,
                    title,
                    msg, null);
        } catch (WindowManager.BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    private void startModifyPage(SnapsDiaryListItem item) {
        if (item == null) return;

        if (SnapsDiaryConstants.IS_SUPPORT_IOS_VERSION) {

        } else {
            if (!SnapsDiaryConstants.isOSTypeEqualsAndroid(item.getOsType())) {
                showWriteFromOtherPlatformAlert(true);
                return;
            }
        }

        Intent intent = new Intent(mActivity, SnapsDiaryConfirmEditableActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.DIARY_DATA, item);
        bundle.putBoolean(Const_EKEY.DIARY_IS_MODIFY_MODE, true);

        intent.putExtras(bundle);

        mActivity.startActivityForResult(intent, SnapsDiaryConstants.REQUEST_CODE_DIARY_UPDATE);
    }

    public void clearAndReloadDiaryList() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryListInfo listInfo = dataManager.getListInfo();
        listInfo.clearDiaryList();

        SnapsDiaryPageInfo pageInfo = new SnapsDiaryPageInfo();
        pageInfo.setIsUsePaging(true);
        pageInfo.setPagingNo(1);
        pageInfo.setPagingSize(DIARY_LIST_PAGING_COUNT);

        SnapsDiaryInterfaceUtil.getDiaryList(mActivity, pageInfo, new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {

            @Override
            public void onPreperation() {
                mActivity.showProgress();
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                mActivity.hideProgress();

                if (result && resultObj != null) {
                    SnapsDiaryListJson listResult = (SnapsDiaryListJson) resultObj;

                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryListInfo listInfo = dataManager.getListInfo();

                    listInfo.setCurrentPageNo(listResult.getPageNo());
                    listInfo.setTotalCount(listResult.getTotalCount());
                    listInfo.setPageSize(listResult.getPageSize());
                    listInfo.setIosCount(listResult.getIosCount());
                    listInfo.setAndroidCount(listResult.getAndroidCount());

                    List<SnapsDiaryListItemJson> list = listResult.getDiaryList();
                    if (list != null && !list.isEmpty()) {
                        listInfo.addDiaryList(list);
                    }

                    listFragment.scrollToPosition(0);

                    requestDiaryListRefresh();

                    mActivity.setUIByUserMissionState();

                } else {
                    //업로드를 했을때만 본 메서드가 호출 되기 때문에, 리스트가 하나도 없다는 것은 서버 통신 실패로 간주하여 재 시도 처리를 한다..
                    MessageUtil.alert(mActivity, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK)
                                clearAndReloadDiaryList();
                        }
                    });
                }

                listFragment.setRecyclerViewMoreListener();

                mActivity.onFinishDiaryListLoad();
            }
        });
    }

    public boolean isShownPopMenu() {
        return popMenu != null && popMenu.isShowing();
    }

    public void closePopMenu() {
        if (popMenu != null && popMenu.isShowing())
            popMenu.dissmiss();
    }
}
