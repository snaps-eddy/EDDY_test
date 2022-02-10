package com.snaps.mobile.activity.common.products.base;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.fragment.app.FragmentActivity;
import android.view.View;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 10. 19..
 */

/**
 * 기본적인 포토북의 공통 동작들이 정의 되어 있음
 * 제품별로 다르게 동작하도록 처리 해야 하는 부분이 있다면 아래 메서드를 오버라이딩 하면 됩니다
 */
public abstract class SnapsProductBaseEditorCommonImplement extends SnapsProductBaseEditor {
    protected SnapsProductBaseEditorCommonImplement(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * 템플릿이 로딩 된 시점에 사진 선택 화면에서 선택한 사진들을 템플릿의 레이아웃 컨트롤에 배치 한다
     */
    @Override
    public void initImageRangeInfoOnLoadedTemplate(SnapsTemplate template) {
        super.handleBaseImageRangeInfoOnLoadedTemplate(template);
    }

    /**
     * 템플릿이 로딩 된 시점에 추가 페이지에 대한 처리를 한다.(포토북의 경우 처음에 페이지를 추가할 수 있다)
     */
    @Override
    public void appendAddPageOnLoadedTemplate(SnapsTemplate template) {
        super.handleBaseAppendAddPageOnLoadedTemplate(template);
    }

    /**
     * 템플릿 로딩이 성공적으로 수행 되었는 지 체크 한다. (카드 등 히든 페이지를 분리한다던가하는 부가적인 작업이 필요하다)
     */
    @Override
    public boolean isSuccessInitializeTemplate(SnapsTemplate template) {
        return super.checkBaseSuccessInitializeTemplate(template);
    }


    @Override
    public void refreshSelectedNewImageData(MyPhotoSelectImageData newImageData, SnapsLayoutControl control) {
        super.handleRefreshSelectedNewImageData(newImageData, control);
    }

    /**
     * 템플릿을 로딩하기 전에 수행해야 할 작업이 있다면 오버라이딩 한다
     */
    @Override
    public SnapsTemplate getTemplate(String _url) {
        return handleGetBaseTemplate(_url);
    }

    /**
     * 썸네일 영역을 클릭 했을 때의 동작을 정의 한다
     */
    @Override
    public void onThumbnailViewClick(View view, int position) {
        super.handleOnThumbnailViewClick(view, position);
    }

    /**
     * Activity에 onResume 이 처음에 들어왔을 때 호출된다. 보통 템플릿 로딩을 수행한다
     */
    @Override
    public void handleOnFirstResume() {
        super.handleBaseOnFirstResume();
    }

    /**
     * 메인 ViewPager에 페이징이 일어났을 때 호출된다
     */
    @Override
    public void onCenterPagerSelected(int page) {
        super.handleOnCenterPagerSelected(page);
    }

    /**
     * 각 상품 별 미리 보기 버튼 Visible여부에 대한 설정
     */
    @Override
    public void setPreviewBtnVisibleState() {
        super.handleBasePreviewBtnVisibleState();
    }

    @Override
    public BaseEditActivityThumbnailAdapter createThumbnailAdapter() {
        return super.handleCreateThumbnailAdapter();
    }

    /**
     * 썸네일을 강제로 선택하는 기능에 대한 정의를 한다.
     */
    @Override
    public void setThumbnailSelectionDragView(int pageChangeType, int page) {
        super.handleBaseThumbnailSelectionDragView(pageChangeType, page);
    }

    /**
     * Activity의 layout자체가 다르다면 해당 메서드를 오버라이딩해서 설정하도록 한다
     */
    @Override
    public void setActivityContentView() {
        super.handleBaseActivityContentView();
    }

    /**
     * 하단 썸네일의 복사, 삭제 붙여 넣기 등의 Tooltip 처리
     */
    @Override
    public void showBottomThumbnailPopOverView(View offsetView, int position) {
        super.handleShowBottomThumbnailPopOverView(offsetView, position);
    }

    @Override
    public void showCannotDeletePageToast(int minCount) {
        super.handleShowCannotDeletePageToast(minCount);
    }

    @Override
    public void showPageOverCountToastMessage() {
        super.handleShowPageOverCountToastMessage();
    }

    /**
     * 하단 썸네일의 복사, 삭제 붙여 넣기 등의 Tooltip 처리
     */
    @Override
    public void showGalleryPopOverView(View view, int position) {
        super.handleShowGalleryPopOverView(view, position);
    }

    /**
     * 디자인 변경 버튼을 눌렀을 때의 동작을 정의한다
     */
    @Override
    public void onClickedChangeDesign() {
        super.handleBaseChangePageDesign();
    }

    @Override
    public void onClickedChangePeriod() {
        super.handleBaseChangePeriod();
    }

    @Override
    public void onClickedInfo() {
        super.handleBaseInfo();
    }

    /**
     * 마지막으로 편집 중이던 Item의 index를 반환한다
     */
    @Override
    public int getLastEditPageIndex() {
        return super.getBaseLastEditPageIndex();
    }

    /**
     * 최대 추가할 수 있는 페이지 수를 정의 한다
     */
    @Override
    public boolean isLackMinPageCount() {
        return super.checkBaseLackMinPageCount();
    }

    @Override
    public String getDeletePageMessage() {
        return super.getBaseDeletePageMessage();
    }

    @Override
    public void exportAutoSaveTemplate() {
        super.handleExportAutoSaveTemplate();
    }

    @Override
    public SnapsTemplate recoveryTemplateFromAutoSavedFile() {
        return super.handleRecoveryTemplateFromAutoSavedFile();
    }

    @Override
    public void selectCenterPager(int position, boolean isSmoothScroll) {
        super.handleBaseSelectCenterPager(position, isSmoothScroll);
    }

    @Override
    public ArrayList<String> getPageThumbnailPaths() {
        return super.getBasePageThumbnailPaths();
    }

    /**
     * 데이터의 변화가 일어 났을 때, 리스트 갱신을 하고 나서의 후처리를 진행한다
     */
    @Override
    public void handleAfterRefreshList(int startPageIDX, int endPageIdx) {
        super.handleBaseAfterRefreshList(startPageIDX, endPageIdx);
    }

    @Override
    public void deletePage() {
        super.handleDeletePage();
    }

    @Override
    public void deletePage(int index) {
        super.handleDeletePage(index);
    }

    @Override
    public boolean isOverPageCount() {
        return super.checkBaseOverPageCount();
    }

    @Override
    public void showAddStickToastMsg() {
        super.handleShowAddStickToastMsg();
    }

    @Override
    public void showCoverSpineDeletedToastMsg() {
        super.handleShowCoverSpineDeletedToastMsg();
    }

    @Override
    public void showAddPageActivity() {
        super.handleShowAddPageActivity();
    }

    @Override
    public Point getNoPrintToastOffsetForScreenLandscape() {
        return super.handleGetBaseNoPrintToastOffsetForScreenLandscape();
    }

    @Override
    public Point getNoPrintToastOffsetForScreenPortrait() {
        return super.handleGetBaseNoPrintToastOffsetForScreenPortrait();
    }

    @Override
    public Rect getQRCodeRect() {
        return super.getBaseQRCodeRect();
    }

    @Override
    public int getPopMenuPhotoTooltipLayoutResId(Intent intent) {
        return super.getBasePopMenuPhotoTooltipLayoutResId(intent);
    }

    @Override
    public void initPaperInfoOnLoadedTemplate(SnapsTemplate template) {
        super.handleInitPaperInfoOnLoadedTemplate(template);
    }

    /***
     * 템플릿이 호출이 완료 된 후에 필요한 정보들을 셋팅한다
     */
    @Override
    public void setTemplateBaseInfo() {
        super.handleBaseTemplateBaseInfo();
    }

    @Override
    public void addPage(int index) {
        super.handleAddPage(index);
    }

    @Override
    public void pageProgressUnload() {
        super.handlePageProgressUnload();
    }

    @Override
    public void showPageProgress() {}

    @Override
    public boolean addPage(int pageIDX, SnapsPage... pages) {
        return super.handleAddPage(pageIDX, pages);
    }

    @Override
    public void changePage(int pageIDX, SnapsPage pages) {
        super.handlechangePage(pageIDX,pages);
    }

    @Override
    public void refreshList(int startPageIDX, int endPageIdx) {
        super.handleRefreshList(startPageIDX, endPageIdx);
    }

    @Override
    public void refreshUI() {
        super.handleRefreshUI();
    }

    @Override
    public void refreshPageThumbnail() {
        super.handleBaseRefreshPageThumbnail();
    }

    @Override
    public void refreshPageThumbnail(int page, long delay) {
        super.handleBaseRefreshPageThumbnail(page, delay);
    }

    /**
     * 템플릿이 로딩 되기 전에 편집 정보를 초기화 한다
     */
    @Override
    public void initEditInfoBeforeLoadTemplate() {}

    /**
     * 액티비티 생성 시점에 컨트롤들의 Visible 상태를 초기화 한다
     */
    @Override
    public void initControlVisibleStateOnActivityCreate() {}

    @Override
    public void onClickedBgControl() {}

    /**
     * Card 등 hidden Page 가 존재 하는 경우, Hidden Page에 대한 처리를 진행 한다
     */
    @Override
    public void initHiddenPageOnLoadedTemplate(SnapsTemplate template) {}

    //FIXME  이거 겹치는 기능 같은데..확인 좀 해 보자
    @Override
    public void handleCenterPagerSelected() {}

    @Override
    public int getAutoSaveProductCode() {
        return 0;
    }

    //FIXME 이것도 겹치는 거 같은데...
    @Override
    public void preHandleLoadedTemplateInfo(SnapsTemplate template) {}

    /**
     * 포토카드나 지갑용 사진은 썸네일 영역에 Counter가 달려 있다
     */
    @Override
    public void onThumbnailCountViewClick(View view, int position) {}

    /**
     * 포토북 류는 하단 썸네일을 롱클릭하여 드래깅하거나 삭제 시킬 수 있다
     */
    @Override
    public void onThumbnailViewLongClick(View view, int position) {}

    /**
     * 템플릿 로딩이 완전히 완료 된 시점에 호출 된다
     */
    @Override
    public void onCompleteLoadTemplateHook() {}

    @Override
    public void showDesignChangeTutorial() {}

    @Override
    public void handleScreenRotatedHook() {}

    @Override
    public void refreshSelectedNewImageDataHook(MyPhotoSelectImageData imageData) {}

    @Override
    public void setCardShapeLayout() {}

    @Override
    public void notifyTextControlFromIntentDataHook(SnapsTextControl control) {}

    @Override
    public void suspendSmartSnapsFaceSearching() {
        super.handleBaseSuspendSmartSnapsFaceSearching();
    }

    @Override
    public boolean isAddedPage() {
        return super.isAddedPage();
    }

    @Override
    public void onFinishedFirstSmartSnapsAnimation() {}

    @Override
    public void startSmartSearchOnEditorFirstLoad() {
        super.handleSmartSearchOnEditorFirstLoad();
    }

    @Override
    public boolean shouldSmartSnapsAnimateOnActivityStart() {
        return false;
    }

    //하단 썸네일이 두개로 이루어진 상품은 true로 override 해 줘야 한다..
    @Override
    public boolean isTwinShapeBottomThumbnail() {
        return false;
    }
}
