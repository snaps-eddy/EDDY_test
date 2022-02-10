package com.snaps.mobile.activity.common.products.book_product;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.ThemeBookClipBoard;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class SimpleMakingBookEditor extends SnapsBookShapeEditor {

    public SimpleMakingBookEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null)
            coverModify.setVisibility(View.GONE);

        ImageView textModify = getEditControls().getThemeTextModify();
        if (textModify != null)
            textModify.setVisibility(View.GONE);
    }

    @Override
    public int getPopMenuPhotoTooltipLayoutResId(Intent intent) {
        String pageType = intent != null ? intent.getStringExtra("pageType") : null;
        return (pageType != null && pageType.length() > 0 && !"cover".equalsIgnoreCase(pageType)) ? R.layout.popmenu_photo_no_edit
                : R.layout.popmenu_photo;
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_SIMPLE_MAKING_BOOK;
    }

    @Override
    public Rect getQRCodeRect() {
        SnapsPage coverPage = getTemplate().getPages().get(0);
        int width = coverPage.getOriginWidth();
        int height = (int) Float.parseFloat(coverPage.height);

        Rect rect = new Rect();
        rect.left = 260 * width / 604;
        rect.top = 378 * height / 422;
        rect.right = rect.left + 25;
        rect.bottom = rect.top + 29;
        return rect;
    }

    @Override
    public void initImageRangeInfoOnLoadedTemplate(SnapsTemplate template) {
        // 이미지 삽입..차례대
        ArrayList<MyPhotoSelectImageData> _galleryList = getEditInfo().getGalleryList();
        if (_galleryList == null || _galleryList.size() == 0)
            _galleryList = template.myphotoImageList;

        // 커버를 만들기 위해, 커버의 이미지 갯수대로 랜덤으로 추출하여 리스트의 앞에 추가해준다.
        if (template.getPages().size() > 0) {
            SnapsPage cover = template.getPages().get(0);
            ArrayList<SnapsControl> layoutControls = cover.getLayerLayouts();
            if (layoutControls != null) {
                int coverImageCount = layoutControls.size();
                int totalImageCount = _galleryList.size();

                if (coverImageCount > 0 && totalImageCount > 0) {
                    ArrayList<MyPhotoSelectImageData> cloneList = new ArrayList<MyPhotoSelectImageData>();
                    for (MyPhotoSelectImageData imgData : _galleryList) {
                        MyPhotoSelectImageData tempImg = new MyPhotoSelectImageData();
                        tempImg.set(imgData);
                        cloneList.add(tempImg);
                    }

                    ArrayList<Integer> imgIndexAry = new ArrayList<Integer>();
                    ArrayList<Integer> emptyIndexAry = new ArrayList<Integer>();
                    for (int i = 0; i < totalImageCount; ++i) {
                        if (cloneList.get(i).IMAGE_ID > 0)
                            imgIndexAry.add(i);
                        else
                            emptyIndexAry.add(i);
                    }

                    for (int ii = 0; ii < coverImageCount; ii++) {
                        int temp, randomIdx;
                        if (imgIndexAry.size() > 0 && coverImageCount - ii - 1 < imgIndexAry.size()) { // 앞에서 부터 채워질 수 있게 빈 이미지 먼저 삽입.
                            temp = (int) (Math.random() * imgIndexAry.size());
                            randomIdx = imgIndexAry.get(temp);
                            imgIndexAry.remove(temp);
                        } else {
                            temp = (int) (Math.random() * emptyIndexAry.size());
                            randomIdx = emptyIndexAry.get(temp);
                            emptyIndexAry.remove(temp);
                        }

                        _galleryList.add(0, cloneList.get(randomIdx));
                    }
                    if (cloneList != null)
                        cloneList.clear();
                }
            }
        }

        super.handleBaseImageRangeInfoOnLoadedTemplate(template);
    }

    @Override
    public void handleCenterPagerSelected() {
        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null)
            coverModify.setVisibility(View.GONE);

        ImageView textModify = getEditControls().getThemeTextModify();
        if (textModify != null)
            textModify.setVisibility(View.GONE);
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        MessageUtil.toast(getActivity(), R.string.simple_making_book_only_cover_image_edit_msg);
        showEditActivityTutorial();
    }

    @Override
    public void showAddStickToastMsg() { /** 심플 포토북에서는 표시하지 않도록 한다 */ }

    @Override
    public void showCoverSpineDeletedToastMsg() { /** 심플 포토북에서는 표시하지 않도록 한다 */  }

    @Override
    public void showAddPageActivity() {
        dismissPopOvers();

        ThemeBookClipBoard pageClipBoard = getEditControls().getPageClipBoard();
        pageClipBoard.copy(getPageList().get(2), false); // 커버, 속지를 건너띄고 첫번째 페이지를 추가.
        if (isOverPageCount()) {
            MessageUtil.toast(getActivity(), R.string.disable_add_page);
            return;
        }

        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager != null) {
            int currentItemIdx = centerPager.getCurrentItem();
            addPage(currentItemIdx < 2 ? 2 : currentItemIdx + 1, pageClipBoard.getCopiedPage());
        }
    }

    @Override
    public Point getNoPrintToastOffsetForScreenLandscape() {
        return new Point(70, 12);
    }

    @Override
    public Point getNoPrintToastOffsetForScreenPortrait() {
        return new Point(0, 92);
    }

    @Override
    protected boolean isImageEditableOnlyCover() {
        return true;
    }
}
