package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectTrayPageCountInfo;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForFrame extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForFrame(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT performGetDefaultFragmentType() {
        return ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.SELECT_IMAGE_SRC;
    }

    /**
     * 완료 버튼을 눌렀을 때의 처리
     */
    @Override
    public void onClickedNextBtn() {
        if (imageSelectActivity == null) return;

        ImageSelectManager manager = ImageSelectManager.getInstance();
        if (manager == null) return;

        boolean isNotAllSelected = false; //템플릿이 있는데, 사진을 모두 선택하지 않은 형태

        ImageSelectTrayPageCountInfo pageCountInfo = manager.getPageCountInfo();
        if (pageCountInfo != null) {
//            if (pageCountInfo.getCurrentSelectedImageCount() < pageCountInfo.getTotalTemplateImageCount()) {
//                isNotAllSelected = true;
//            }
            isNotAllSelected = pageCountInfo.hasEmptyImageContainer();
        }

        if (isNotAllSelected) {
            MessageUtil.alertnoTitle(imageSelectActivity, imageSelectActivity.getString(R.string.not_packaged_full_msg), new ICustomDialogListener() {

                @Override
                public void onClick(byte clickedOk) {
                    if (clickedOk == ICustomDialogListener.OK)
                        moveNextActivity();
                    else
                        MessageUtil.toast(getApplicationContext(), imageSelectActivity.getString(R.string.cancel_msg));
                }
            });
        } else {
            moveNextActivity();
        }
    }

    /**
     * 액티비티 이동
     */
    @Override
    public void moveNextActivity() {
        if(isSuccessSetSimpleDatas()) {
            Intent saveIntent = new Intent(imageSelectActivity, SnapsEditActivity.class);
            saveIntent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.FRAME.ordinal());
            saveIntent.putExtra("templete", BaseImageSelectPerformer.TEMPLATE_PATH);
            imageSelectActivity.startActivity(saveIntent);
            imageSelectActivity.finish();
        }
    }
}
