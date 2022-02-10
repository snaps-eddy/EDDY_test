package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.storybook.StoryDataType;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.StoryBookDataManager;
import com.snaps.mobile.activity.book.StoryBookFragmentActivity;
import com.snaps.mobile.activity.book.StoryStyleFactory;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForNewKakaoBook extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForNewKakaoBook(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT performGetDefaultFragmentType() {
        applyReceivedIntentInfo();
        return ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.KAKAO_BOOK_ALBUM;
    }

    private void applyReceivedIntentInfo() {
        //FIXME 불필요한 코드로 보인다.. 확인 해 보자.
        Intent intent = imageSelectActivity.getIntent();
        if (intent != null) {
            String prjCode = intent.getStringExtra(Const_EKEY.MYART_PROJCODE);
            if (prjCode != null) {
                Config.setPROJ_CODE(prjCode);
                Config.setPROD_CODE(intent.getStringExtra(Const_EKEY.MYART_PRODCODE));
                return;
            }
        }

        ImageSelectIntentData intentData = imageSelectActivity.getIntentData();
        if (intentData == null)
            return;

        boolean isException = false;

        String startDate = "";
        String endDate = "";
        String projectTitle = "";
        String paperCode = "";
        String templateId = null;
        String productCode = "";

        int commentCount = 0;
        int photoCount = 0;
        StoryStyleFactory.eStoryDataStyle storybookStyle = null;

        // 댓글 표시 갯수 제한
        String szCommentCount = intentData.getWebCommentCount();
        if (szCommentCount == null || szCommentCount.length() < 1)
            isException = true;
        else
            commentCount = Integer.parseInt(szCommentCount);

        // 사진 표시 갯수 제한
        String szPhotoCnt = intentData.getWebPhotoCount();
        if (szCommentCount == null || szCommentCount.length() < 1)
            isException = true;
        else
            photoCount = Integer.parseInt(szPhotoCnt);

        // 커버 타이틀
        projectTitle = intentData.getWebTitleKey();
        if (projectTitle != null && projectTitle.length() > 0)
            projectTitle = StringUtil.getFilterString(projectTitle);

        // 용지 타입
        paperCode = intentData.getWebPaperCode();

        // 상품 코드
        productCode = intentData.getHomeSelectProductCode();
        startDate = intentData.getWebStartDate();

        endDate = intentData.getWebEndDate();

        // 템플릿 아이디
        templateId = intentData.getThemeSelectTemplate();

        storybookStyle = StoryStyleFactory.getStyleByTmpId(templateId);

        StoryBookDataManager dataManager = StoryBookDataManager.createInstance(storybookStyle, imageSelectActivity, StoryDataType.KAKAO_STORY);
        if (dataManager == null || isException) {
            MessageUtil.toast(imageSelectActivity, R.string.loading_fail);
            imageSelectActivity.finish();
            return;
        }

        dataManager.setCommentCount(commentCount);
        dataManager.setPhotoCount(photoCount);
        dataManager.setProjectTitle(projectTitle);
        dataManager.setPaperCode(paperCode);
        dataManager.setStartDate(startDate);
        dataManager.setEndDate(endDate);
        dataManager.setProductCode(productCode);
        dataManager.setTemplateId(templateId);
        dataManager.setStorybookStyle(storybookStyle);

        Config.setPROJ_NAME(projectTitle);
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
            ArrayList<String> keyList = holder.getSelectImgKeyList();
            if (keyList != null && !keyList.isEmpty()) {
                StoryBookDataManager dataManager = StoryBookDataManager.getInstance();
                if (dataManager != null)
                    dataManager.removeStories(keyList);
            }
            Intent saveIntent = new Intent(imageSelectActivity, StoryBookFragmentActivity.class);
            imageSelectActivity.startActivity(saveIntent);
            imageSelectActivity.finish();
        }
    }
}
