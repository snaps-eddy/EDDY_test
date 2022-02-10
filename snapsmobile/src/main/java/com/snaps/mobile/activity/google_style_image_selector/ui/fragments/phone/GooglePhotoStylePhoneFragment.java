package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.phone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SystemIntentUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoFragmentData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectDragItemListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectGetAlbumListListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.DateDisplayScrollBar;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.GooglePhotoStyleFrontView;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectBaseFragment;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.google_style_image_selector.utils.PhonePhotosLoader;

import java.util.ArrayList;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import font.FTextView;

public class GooglePhotoStylePhoneFragment extends ImageSelectBaseFragment implements IImageSelectFragmentItemClickListener, IImageSelectDragItemListener,
        PhonePhotosLoader.IPhonePhotoLoadListener {
    private static final String TAG = GooglePhotoStylePhoneFragment.class.getSimpleName();

    private GooglePhotoStylePhoneFragmentProcessor googleStyleUIProcessor = null;    //RecyclerView, Adapter관린, 핀치 줌..등 모든 UI 처리는 여기서

    private boolean permissionGranted = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        imageSelectActivityV2 = (ImageSelectActivityV2) getActivity();
    }

    @Override
    public void onChangedAlbumCursor(IAlbumData cursor) {
        try {
            if (googleStyleUIProcessor != null)
                googleStyleUIProcessor.changeAlbum(cursor);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        saveLastSelectedAlbumIdWithAlbumData(cursor);
    }

    private void saveLastSelectedAlbumIdWithAlbumData(IAlbumData cursor) {
        if (cursor == null) return;
        ImageSelectUtils.saveLastSelectedPhoneAlbumId(cursor.getAlbumId());
    }

    @Override
    public void setBaseAlbumIfExistAlbumList(ArrayList<IAlbumData> list) {}

    @Override
    public IAlbumData getCurrentAlbumCursor() {
        return null;
    }


    private ImageSelectPhonePhotoData getPhonePhotoData() {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager == null) return null;

//        imageSelectManager.waitIfCreatingPhotoDataList(); //synchronized

        ImageSelectPhonePhotoFragmentData photoFragmentData = imageSelectManager.getPhonePhotoFragmentDatas();
        if (photoFragmentData != null) {
            return photoFragmentData.getPhonePhotoData();
        }
        return null;
    }

    @Override
    public void loadImageIfExistCreateAlbumList(IImageSelectGetAlbumListListener albumListListener) {
        //백그라운드에서 이미 생성이 되고 있다. 아직 생성이 안 되었다면 기다린다.
        ImageSelectPhonePhotoData phonePhotoData = getPhonePhotoData();

        if (phonePhotoData == null || !phonePhotoData.isExistPhotoPhotoData()) {   //권한 없이 진입하면 데이터가 없다.
            final ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
            if (imageSelectManager == null) return;

            showProgress(false);

            imageSelectManager.createPhonePhotoDatas(imageSelectActivityV2, this);
        } else {
            //이미 데이터가 만들어져 있는 상태이니, 로딩 완료 메서드를 호출한다.
            onFinishPhonePhotoLoad();
        }
    }

    @Override
    public void onClickFragmentItem(ImageSelectAdapterHolders.PhotoFragmentItemHolder vh) {
        if (vh == null || googleStyleUIProcessor == null || googleStyleUIProcessor.isActivePinchAnimation()) return;

        if (googleStyleUIProcessor.getCurrentDepth() == ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_YEAR) {
            if (!(vh instanceof ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsMonthSectionHolder)
                    && !(vh instanceof ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsYearSectionHolder)) {
                googleStyleUIProcessor.switchUIForNextDepth(vh);
            }
            return;
        }

        super.onClickFragmentItem(vh);
    }

    @Override
    public void onDragItem(ImageSelectAdapterHolders.PhotoFragmentItemHolder holder ,int type) {
        if (holder == null || googleStyleUIProcessor == null) return;
        googleStyleUIProcessor.selectDragItem(holder,type);


    }

    @Override
    public void onDragItemEmpty(int type) {
        if ( googleStyleUIProcessor == null) return;
        googleStyleUIProcessor.selectDragItemEmpty();
    }

    @Override
    public boolean isExistAlbumList() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int iResId = R.layout.fragment_imagedetail_google_style;

        View inflaterView = inflater.inflate(iResId, container, false);

        if (Build.VERSION.SDK_INT > 22) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION); // 설명을 보면 한번 사용자가 거부하고, 다시 묻지 않기를 체크하지 않았을때 여기를 탄다고 한다. 이때 설명을 넣고 싶으면 이걸 지우고 넣자.
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION);
                }
                permissionGranted = false;
            }
        }

        //기본 컨트롤 초기화
        initControls(inflaterView);

        //리스너 등록
        registerListeners();

        return inflaterView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (permissionGranted) {
            doAfterCheckPermission();
        }
    }

    //폰 사진 데이터 로딩 완료 ()
    @Override
    public void onFinishPhonePhotoLoad() {
        dismissedFDialog();

        //로딩 된 사진 데이터를 공유하기 위해
        if (googleStyleUIProcessor != null) {
            try {
                googleStyleUIProcessor.initPhotoURIData(getPhonePhotoData());

                //타이틀에 달린 앨범 선택 리스트 셀력터뷰를 생성하도록 요청..
                initAlbumList();

                //로딩 된 사진 데이터로 Adapter grouping, section 등을 처리해야 하기 때문에 미리 가공한다. 가공이 완료되면 onFinishedCurrentUIDepthPhotoConvert, onFinishedAllPhotoConvert를 호출한다.
                googleStyleUIProcessor.convertPhotoListOnThread();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        //if (Config.useKorean()) {
            if (imageSelectActivityV2 != null)
                imageSelectActivityV2.showTutorial(ISnapsImageSelectConstants.eTUTORIAL_TYPE.PHONE_FRAGMENT_PINCH_MOTION);
        //}
    }

    @Override
    public void onUpdatedPhotoList(String imageKey) {
        if (googleStyleUIProcessor != null) {
            googleStyleUIProcessor.updatedPhotoList(imageKey);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const_VALUE.REQ_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doAfterCheckPermission();
                } else {
                    MessageUtil.alert(getActivity(), getString(R.string.need_to_permission_accept_for_get_phone_pictures), "", R.string.cancel, R.string.confirm_move_to_setting, false, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK) {
                                SystemIntentUtil.showSystemSetting(getActivity());
                            }
                            getFragmentManager().popBackStack();
                        }
                    });
                }
                break;
        }
    }


    private void registerListeners() {
        //갱신을 위해..
        imageSelectActivityV2.registerListUpdateListener(this);
    }

    private void initControls(View view) {
        if (view == null) return;
        lyErrorView = view.findViewById(R.id.ly_sticky_network_err_parent);
        ivErrorImg = (ImageView) view.findViewById(R.id.iv_wifi);
        tvErrorText = (FTextView) view.findViewById(R.id.tv_network_text_title);
        tvErrorTextSub = (FTextView) view.findViewById(R.id.tv_network_text_d);
        tvErrorRetryBtn = (FTextView) view.findViewById(R.id.btn_sticky_network_err_retry);

        SnapsSuperRecyclerView recyclerViewDepthYear = (SnapsSuperRecyclerView) view.findViewById(R.id.custom_snaps_native_super_recycler_view_depth_year);
        SnapsSuperRecyclerView recyclerViewDepthMonth = (SnapsSuperRecyclerView) view.findViewById(R.id.custom_snaps_native_super_recycler_view_depth_month);
        SnapsSuperRecyclerView recyclerViewDepthDay = (SnapsSuperRecyclerView) view.findViewById(R.id.custom_snaps_native_super_recycler_view_depth_day);
        SnapsSuperRecyclerView recyclerViewDepthStaggered = (SnapsSuperRecyclerView) view.findViewById(R.id.custom_snaps_native_super_recycler_view_depth_staggered);
        DateDisplayScrollBar dateDisplayScrollBar = (DateDisplayScrollBar) view.findViewById(R.id.date_display_scroll_bar);
        GooglePhotoStyleFrontView googlePhotoStyleFrontView = (GooglePhotoStyleFrontView) view.findViewById(R.id.custom_snaps_native_fake_layout);

        //스티키뷰라 뷰가 전체적으로 아래에 위치한 것 같은 느낌이 있으니, 네트워크 에러 뷰의 아이템들을 위로 올려준다.
        RelativeLayout marginView = (RelativeLayout) view.findViewById(R.id.ly_sticky_network_err_margin_view);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) marginView.getLayoutParams();
        layoutParams.bottomMargin = (int) imageSelectActivityV2.getResources().getDimension(R.dimen.image_select_fragment_top_are_height);
        marginView.setLayoutParams(layoutParams);

        googleStyleUIProcessor = new GooglePhotoStylePhoneFragmentProcessor.Builder()
                .setPhonePhotoRecyclerViewDepthYear(recyclerViewDepthYear)
                .setPhonePhotoRecyclerViewDepthMonth(recyclerViewDepthMonth)
                .setPhonePhotoRecyclerViewDepthDay(recyclerViewDepthDay)
                .setPhonePhotoRecyclerViewDepthStaggered(recyclerViewDepthStaggered)
                .setGooglePhotoStyleFrontView(googlePhotoStyleFrontView)
                .setDateDisplayScrollBar(dateDisplayScrollBar)
                .setFragmentItemClickListener(this)
                .setSelectDragItemListener(this)
                .create(imageSelectActivityV2);

        ImageSelectUIProcessor processor = imageSelectActivityV2.getUIProcessor();
        if (processor != null) {
            this.googleStyleUIProcessor.setPhotoFilterInfo(processor.getPhotoFilterInfo());
        }
    }

    private void doAfterCheckPermission() {
        //앨범이 존재한다면 앨범을 만들고, 이미지 로딩을 시작한다. (앨범이 없으면 그냥 바로 이미지 로딩..)
        loadImageIfExistCreateAlbumList(null);
    }

    private void initAlbumList() throws Exception {
        //타이틀에 달린 앨범 선택 리스트 셀력터뷰를 생성하도록 요청..
        if (googleStyleUIProcessor != null) {

            googleStyleUIProcessor.setDefaultAlbumCursor();

            ImageSelectPhonePhotoData phonePhotoData = googleStyleUIProcessor.getPhotoURIData();
            if (phonePhotoData != null) {
                ArrayList<IAlbumData> resultList = phonePhotoData.getArrCursor();
                if (resultList != null && !resultList.isEmpty()) {
                    if (itemStateChangedListener != null)
                        itemStateChangedListener.onRequestedMakeAlbumList(resultList);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        imageSelectActivityV2.unRegisterListUpdateListener(this);

        if( googleStyleUIProcessor != null ) googleStyleUIProcessor.releaseInstace();
    }
}