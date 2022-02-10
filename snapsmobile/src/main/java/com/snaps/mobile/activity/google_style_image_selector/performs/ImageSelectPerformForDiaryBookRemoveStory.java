package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.book.SNSBookFragmentActivity;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.publish.SnapsDiaryPublishFragmentActivity;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForDiaryBookRemoveStory extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForDiaryBookRemoveStory(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT performGetDefaultFragmentType() {
        applyReceivedIntentInfo();
        return ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.DIARY_ALBUM;
    }

    private void applyReceivedIntentInfo() {
        ImageSelectIntentData intentData = imageSelectActivity.getIntentData();
        if (intentData == null)
            return;

        // 인텐트에서 project코드 product코드를 가져온다.
        // 재편집여부 판단...
        Intent intent = imageSelectActivity.getIntent();
        if (intent != null) {
            String prjCode = intent.getStringExtra(Const_EKEY.MYART_PROJCODE);
            if (prjCode != null) {
                Config.setPROJ_CODE(prjCode);
                Config.setPROD_CODE(intent.getStringExtra(Const_EKEY.MYART_PRODCODE));
                // 재편집 모드인경우 하단 메뉴 필요없음..
                return;
            }
        }

        String startDate = "";
        String endDate = "";
        String projectTitle = "";
        String paperCode = "";
        String templateId = null;
        String productCode = "";

        // 커버 타이틀
        projectTitle = intentData.getWebTitleKey();
        if(projectTitle != null && projectTitle.length() > 0)
            projectTitle = StringUtil.getFilterString(projectTitle);

        // 용지 타입
        paperCode = intentData.getWebPaperCode();

        // 상품 코드
        productCode = intentData.getHomeSelectProductCode();

        // 기간
        startDate = intentData.getWebStartDate();
        endDate = intentData.getWebEndDate();

        // 템플릿 아이디
        templateId = intentData.getThemeSelectTemplate();

        Config.setPROJ_NAME(projectTitle);

        SnapsDiaryDataManager.getInstance().init(productCode, paperCode, templateId, projectTitle, startDate, endDate);
    }

    /**
     * 완료 버튼을 눌렀을 때의 처리
     */
    @Override
    public void onClickedNextBtn() {
        moveNextActivity();
    }

    /**
     * 액티비티 이동
     */
    @Override
    public void moveNextActivity() {
        if (imageSelectActivity == null) return;

        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {
            Intent saveIntent = new Intent(imageSelectActivity, SnapsDiaryPublishFragmentActivity.class);
            saveIntent.putIntegerArrayListExtra(SNSBookFragmentActivity.INTENT_KEY_REMOVE_DATA_INDEX_ARRAY, holder.getSelectImgIndexList());
            saveIntent.putExtra( SNSBookFragmentActivity.INTENT_KEY_TOTAL_DATA_COUNT, SnapsDiaryDataManager.getInstance().getPublishListInfo().getArrDiaryList().size() );
            imageSelectActivity.startActivity(saveIntent);
            imageSelectActivity.finish();
        }
    }
}
