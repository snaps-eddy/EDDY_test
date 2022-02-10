package com.snaps.mobile.activity.common.products.single_page_product;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.PassPortRuleDialog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.pager.SnapsPagerController2;
import com.snaps.mobile.activity.themebook.ImageEditActivity;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class IdentifyPhotoEditor extends SnapsSinglePageEditor {
    private static final String TAG = IdentifyPhotoEditor.class.getSimpleName();

    public IdentifyPhotoEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_IDENTIFY_PHOTO;
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistThumbnailLayout();

    }

    @Override
    public void onClickedInfo() {
            PassPortRuleDialog dialog = new PassPortRuleDialog(getActivity(), false);
            dialog.show();
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startImageEditActivity();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();
        //한국어 일때만 사진 규정 다이얼로그를 볼수 있는 인포버튼을 표시한다.
        if (Config.useKorean()) {
            if (Config.isPassportPhoto()) {
                ImageView infoBtn = getEditControls().getThemeInfo();
                if (infoBtn != null) {
                    infoBtn.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void startImageEditActivity() {
        if (getOrientationChecker() != null) {
            getOrientationChecker().setCurrentOrientationPrevInEditor();
            getOrientationChecker().setChangedPhoto(false);
        }

        if (getTemplate() != null) {
            ArrayList<MyPhotoSelectImageData> images = getMyPhotoSelectImageData(false);
            PhotobookCommonUtils.imageResolutionCheckForIdentifyPhotoPrint(getTemplate());
            PhotobookCommonUtils.setImageDataScaleable(getTemplate());
            DataTransManager dtMan = DataTransManager.getInstance();
            if(dtMan != null) {
                dtMan.setPhotoImageDataList(images);
            }

        }

        Intent intent = new Intent(getActivity(), ImageEditActivity.class);
        intent.putExtra("dataIndex", 0);
        getActivity().startActivityForResult(intent, REQ_MODIFY);
    }

    @Override
    public void refreshChangedPhoto() {
        ArrayList<MyPhotoSelectImageData> imgList = DataTransManager.getImageDataFromDataTransManager(getActivity());
        if (imgList == null || imgList.isEmpty()) return;

        MyPhotoSelectImageData changedImageData = findEditedFistImageDataWithImageList(imgList);
        if (changedImageData == null) return;

        refreshImageDataWithChangedData(changedImageData);
    }

    @Override
    public void initPaperInfoOnLoadedTemplate(SnapsTemplate template) {
        if(getEditInfo().IS_EDIT_MODE()) {
            Config.setPAPER_CODE(template.info.F_PAPER_CODE);
            Config.setGLOSSY_TYPE(template.info.F_GLOSSY_TYPE);
        } else {
            template.info.F_PAPER_CODE = Config.getPAPER_CODE();
            template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
        }
    }

    @Override
    public void initImageRangeInfoOnLoadedTemplate(SnapsTemplate template) {
        PhotobookCommonUtils.imageRangeForIdentifyPhoto(template, getEditInfo().getGalleryList());
    }

    private MyPhotoSelectImageData findEditedFistImageDataWithImageList(ArrayList<MyPhotoSelectImageData> imgList) {
        for (MyPhotoSelectImageData cropData : imgList) {
            if (cropData.isModify != -1) {
                return cropData;
            }
        }
        return null;
    }

    private void refreshImageDataWithChangedData(MyPhotoSelectImageData changedImageData) {
        if (changedImageData == null) return;
        ArrayList<Integer> changeList = new ArrayList<Integer>();
        ArrayList<MyPhotoSelectImageData> templateImageList = PhotobookCommonUtils.getImageListFromTemplate(getTemplate());
        if (templateImageList != null) {
            for (MyPhotoSelectImageData cropData : templateImageList) {
                if (cropData != null) {
                    cropData.set(changedImageData);
                    cropData.mmPageWidth = StringUtil.isEmpty(getTemplate().info.F_PAGE_MM_WIDTH) ? 0 : Float.parseFloat(getTemplate().info.F_PAGE_MM_WIDTH);
                    cropData.pxPageWidth = StringUtil.isEmpty(getTemplate().info.F_PAGE_PIXEL_WIDTH) ? 0 : Integer.parseInt(getTemplate().info.F_PAGE_PIXEL_WIDTH);

                    changeList.add(cropData.pageIDX);
                }
            }
        }

        refreshImageWithPageIndexList(changeList);
    }

    @Override
    public void refreshSelectedNewImageData(MyPhotoSelectImageData newImageData, SnapsLayoutControl offsetLayoutControl) {
        if (getTemplate() == null || getTemplate().getPages() == null || getTemplate().getPages().isEmpty()) return;

        SnapsPage snapsPage = getTemplate().getPages().get(0);
        ArrayList<SnapsControl> controls = snapsPage.getLayerLayouts();
        if (controls == null || controls.isEmpty()) return;

        for (SnapsControl snapsControl : controls) {
            if(snapsControl == null || !(snapsControl instanceof SnapsLayoutControl)) return;
            SnapsLayoutControl control = (SnapsLayoutControl) snapsControl;
            if (control.type == null || !control.type.equalsIgnoreCase("browse_file"))
                continue;

            newImageData.cropRatio = control.getRatio();
            newImageData.IMG_IDX = PhotobookCommonUtils.getImageIDX(control.getPageIndex(), control.regValue);// Integer.parseInt(control.getPageIndex() + "" + control.regValue); //

            newImageData.pageIDX = control.getPageIndex();
            newImageData.mmPageWidth = StringUtil.isEmpty( getTemplate().info.F_PAGE_MM_WIDTH ) ? 0 : Float.parseFloat( getTemplate().info.F_PAGE_MM_WIDTH );
            newImageData.pxPageWidth = StringUtil.isEmpty( getTemplate().info.F_PAGE_PIXEL_WIDTH ) ? 0 : Integer.parseInt( getTemplate().info.F_PAGE_PIXEL_WIDTH );
            newImageData.controlWidth = control.width;
            control.imgData = new MyPhotoSelectImageData();
            control.imgData.set(newImageData);
            control.angle = String.valueOf(newImageData.ROTATE_ANGLE);
            control.imagePath = newImageData.PATH;
            control.imageLoadType = newImageData.KIND;
            // 인쇄가능 여부..
            try {
                setPhotoResolutionEnableWithLayoutControl(control);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            control.isUploadFailedOrgImg = false;
        }

        SnapsPagerController2 loadPager = getEditControls().getLoadPager();
        if (loadPager != null && loadPager.pageAdapter != null)
            loadPager.pageAdapter.notifyDataSetChanged();

        offerQueue(0, 0);
        OrientationManager.fixCurrentOrientation(getActivity());
        refreshPageThumbnailsAfterDelay();

        SnapsOrderManager.uploadOrgImgOnBackground();

        startImageEditActivity();
    }

    @Override
    public void setPhotoResolutionEnableWithLayoutControl(SnapsLayoutControl newControl) {
        try {
            ResolutionUtil.setIdentifyPhotoEnableResolution(getTemplate(), newControl);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public int getPopMenuPhotoTooltipLayoutResId(Intent intent) {
        return R.layout.popmenu_photo_no_delete;
    }
}
