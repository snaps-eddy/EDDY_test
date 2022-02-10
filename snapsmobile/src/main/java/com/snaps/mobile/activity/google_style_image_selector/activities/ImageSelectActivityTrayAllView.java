package com.snaps.mobile.activity.google_style_image_selector.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies.ImageSelectUIProcessorStrategyFactory;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUITrayControl;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectTrayAllViewListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayEmptyShapeAllViewAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayIdentifyPhotoAllViewAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTraySmartRecommendBookAllViewAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTraySmartSnapsSelectAllViewAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayTemplateShapeAllViewAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.CustomGridLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.ImageSelectTrayAllViewSpacingItemDecoration;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;

import java.util.ArrayList;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

/**
 * 선택한 사진 또는 템플릿 전체 보기
 */
public class ImageSelectActivityTrayAllView extends SnapsBaseFragmentActivity implements IImageSelectTrayAllViewListener {

    private static ImageSelectActivityV2 imageSelectActivityV2 = null;    //FIXME crash 때문에 급하게 이렇게 구현은 했는데 구조 개선이 필요하다...

    private ImageSelectTrayBaseAdapter mTrayAdapter;

    private ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE mUIType;

    private ImageSelectTrayCellItem dummyCellItem = null;

    private boolean isOccurredAnyMotion = false;

    public static Intent getIntent(ImageSelectActivityV2 activityV2, ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE selectedUIType) {
        Intent intent = new Intent(activityV2, ImageSelectActivityTrayAllView.class);
        intent.putExtra(Const_VALUE.KEY_IMAGE_SELECT_UI_TYPE, selectedUIType != null ? selectedUIType.ordinal() : -1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        imageSelectActivityV2 = activityV2;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //오류 로그 수집
        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        setContentView(R.layout.activity_google_photo_style_image_select_tray_all_view);

        initControls();

        loadTrayData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        imageSelectManager.setTrayAllViewMode(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        imageSelectManager.setTrayAllViewMode(false);
    }

    @Override
    public void onOccurredAnyMotion(ISnapsImageSelectConstants.eTRAY_CELL_STATE cellState) {
        isOccurredAnyMotion = true;

        updateConfirmBtnStateByCellState(cellState);
    }

    private void updateConfirmBtnStateByCellState(ISnapsImageSelectConstants.eTRAY_CELL_STATE cellState) {
        if (cellState == null) return;

        TextView confirmBtn = findViewById(R.id.google_photo_style_image_select_title_bar_close_tv);
        if (confirmBtn == null) return;

        confirmBtn.setText(cellState == ISnapsImageSelectConstants.eTRAY_CELL_STATE.TEMPLATE ? R.string.move : R.string.done);
    }

    private void loadTrayData() {
        if (mTrayAdapter == null) return;
        ImageSelectManager manager = ImageSelectManager.getInstance();
        if (manager == null) return;

        ArrayList<ImageSelectTrayCellItem> trayCellItems = manager.getTempTrayCellItemList();

        ArrayList<ImageSelectTrayCellItem> trayItemList = null;
        if (mUIType != null && mUIType == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.TEMPLATE) {
            trayItemList = convertTrayItemListToAllViewList(trayCellItems);
        } else {
            if (mUIType == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SMART_ANALYSIS)
                trayItemList = removeDummyItem(trayCellItems);
            else
                trayItemList = trayCellItems;
        }

        int findSelectedCellItemId = findSelectedCellItemId(trayCellItems);

        mTrayAdapter.setTrayAllViewList(trayItemList, findSelectedCellItemId);
    }

    private ArrayList<ImageSelectTrayCellItem> removeDummyItem(ArrayList<ImageSelectTrayCellItem> list) {
        if (list == null || list.isEmpty()) return list;
        ImageSelectTrayCellItem item = list.get(0);
        if (item.getCellState() == ISnapsImageSelectConstants.eTRAY_CELL_STATE.EMPTY_DUMMY) {
            dummyCellItem = list.remove(0);
            refreshCellItemId(list);
            return list;
        }

        return list;
    }

    private void refreshCellItemId(ArrayList<ImageSelectTrayCellItem> trayCellItemList) {
        if (trayCellItemList == null) return;

        for (int ii = 0; ii < trayCellItemList.size(); ii++) {
            ImageSelectTrayCellItem cellItem = trayCellItemList.get(ii);

            cellItem.setCellId(ii);
        }
    }


    //선택 되어 있는 아이템을 찾는다.
    private int findSelectedCellItemId(ArrayList<ImageSelectTrayCellItem> itemList) {
        if (itemList == null) return -1;

        for (ImageSelectTrayCellItem item : itemList) {
            if (item == null) continue;
            if (item.isSelected()) {
                item.setSelected(false); //다시 선택할거니까, 선택 해제 해 준다..(두 번 클릭되어 이미지가 지워져버리는 것을 방지하기 위해..)
                return item.getCellId();
            }
        }

        return -1;
    }

    private ImageSelectTrayCellItem insertSectionTitle(String label) {
        ImageSelectTrayCellItem cellItem = new ImageSelectTrayCellItem(this, ISnapsImageSelectConstants.INVALID_VALUE, ISnapsImageSelectConstants.eTRAY_CELL_STATE.SECTION_TITLE);
        cellItem.setLabel(label);
        return cellItem;
    }

    private ImageSelectTrayCellItem insertSectionLine() {
        return new ImageSelectTrayCellItem(this, ISnapsImageSelectConstants.INVALID_VALUE, ISnapsImageSelectConstants.eTRAY_CELL_STATE.SECTION_LINE);
    }

    //전체 보기용 리스트 생성
    private ArrayList<ImageSelectTrayCellItem> convertTrayItemListToAllViewList(ArrayList<ImageSelectTrayCellItem> trayCellItems) {
        if (trayCellItems == null) return null;

        ArrayList<ImageSelectTrayCellItem> allViewList = new ArrayList<>();

        String prevLabel = "";
        boolean isSectionInserted = false;

        for (int ii = 0; ii < trayCellItems.size(); ii++) {
            ImageSelectTrayCellItem trayCellItem = trayCellItems.get(ii);
            if (trayCellItem == null || trayCellItem.isPlusBtn()) continue;

            String label = trayCellItem.getLabel();
            if (label == null) continue;

            if (!prevLabel.equalsIgnoreCase(label)) {
                prevLabel = label;

                //라인 삽입
                if (isSectionInserted)
                    allViewList.add(insertSectionLine());

                //섹션 삽입
                allViewList.add(insertSectionTitle(label));
                isSectionInserted = true;
            }

            //아이템 삽입
            allViewList.add(trayCellItem);
        }

        if (Config.isCheckPlusButton() && !mTrayAdapter.isSmartChoiceType()) {
            allViewList.add(insertSectionLine());
            allViewList.add(new ImageSelectTrayCellItem(this, ISnapsImageSelectConstants.INVALID_VALUE, ISnapsImageSelectConstants.eTRAY_CELL_STATE.PLUS_BUTTON));
        }

        return allViewList;
    }

    private void finishActivity() {
        if (mTrayAdapter != null) {
            ImageSelectManager manager = ImageSelectManager.getInstance();
            if (manager != null) {
                ArrayList<ImageSelectTrayCellItem> trayList
                        = manager.getConvertedTrayList(mTrayAdapter.getTrayCellItemList(), dummyCellItem);

                manager.cloneTrayCellItemList(trayList);
            }
        }

        if (isOccurredAnyMotion)
            setResult(ISnapsImageSelectConstants.RESULT_CODE_TRAY_ALL_VIEW_EDITED);

        finish();
        overridePendingTransition(0, R.anim.anim_for_tray_all_view_up_to_down);
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }


    private void createTrayAdapter() {
        int uiType = getIntent().getIntExtra(Const_VALUE.KEY_IMAGE_SELECT_UI_TYPE, -1);
        if (uiType >= 0) {
            mUIType = ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.values()[uiType];
            if (mUIType == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.TEMPLATE) {
                mTrayAdapter = new ImageSelectTrayTemplateShapeAllViewAdapter(this, imageSelectActivityV2);
            } else if (mUIType == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SMART_SNAPS) {
                mTrayAdapter = new ImageSelectTraySmartSnapsSelectAllViewAdapter(this, imageSelectActivityV2);
            } else if (mUIType == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SMART_ANALYSIS) {
                mTrayAdapter = new ImageSelectTraySmartRecommendBookAllViewAdapter(this, imageSelectActivityV2);
            }
        }

        if (mTrayAdapter == null) {
            if (Config.isIdentifyPhotoPrint())
                mTrayAdapter = new ImageSelectTrayIdentifyPhotoAllViewAdapter(this, imageSelectActivityV2);
            else
                mTrayAdapter = new ImageSelectTrayEmptyShapeAllViewAdapter(this, imageSelectActivityV2);
        }

        mTrayAdapter.setTrayAllViewListener(this);

        isOccurredAnyMotion = false;
    }

    //컨트롤 초기화
    private void initControls() {
        findViewById(R.id.google_photo_style_image_select_title_bar_close_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        createTrayAdapter();

        SnapsRecyclerView recyclerView = (SnapsRecyclerView) findViewById(R.id.google_photo_style_image_select_tray_all_view_recyclerview);

        ImageSelectUITrayControl trayControl = new ImageSelectUITrayControl();

        TextView leftCountView = (TextView) findViewById(R.id.google_photo_style_image_select_tray_count_left_tv);
        TextView rightCountView = (TextView) findViewById(R.id.google_photo_style_image_select_tray_count_right_tv);
        trayControl.setLeftCountView(leftCountView);
        trayControl.setRightCountView(rightCountView);
        trayControl.setTrayThumbRecyclerView(recyclerView);
        trayControl.setTrayAdapter(mTrayAdapter);
        mTrayAdapter.setTrayControl(trayControl);

//		TextView trayAllViewTitle = (TextView) findViewById(R.id.google_photo_style_image_select_tray_all_view_title_tv);
//		if (trayAllViewTitle != null) {
//			//if (mTrayAdapter.isSmartChoiceType()) {
//				trayAllViewTitle.setText(R.string.image_select_tray_all_view_title);
//			//}
//		}

        if (recyclerView == null) return;

        CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(this, 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mTrayAdapter == null) return 1;

                int viewType = mTrayAdapter.getItemViewType(position);
                if (viewType == ISnapsImageSelectConstants.eTRAY_CELL_STATE.SECTION_TITLE.ordinal()
                        || viewType == ISnapsImageSelectConstants.eTRAY_CELL_STATE.SECTION_LINE.ordinal()) {
                    return 4;
                }

                return 1;
            }
        });

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ImageSelectTrayAllViewSpacingItemDecoration(this));
        recyclerView.setAdapter(mTrayAdapter);

        View bottomDescView = findViewById(R.id.google_photo_style_image_select_tray_all_view_bottom_desc);
        bottomDescView.setVisibility(mUIType == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.TEMPLATE ? View.VISIBLE : View.GONE);
    }
}