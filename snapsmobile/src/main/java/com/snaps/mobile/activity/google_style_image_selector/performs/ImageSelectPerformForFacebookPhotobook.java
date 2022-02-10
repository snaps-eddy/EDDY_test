package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.facebook.utils.sns.FacebookUtil;
import com.snaps.mobile.activity.book.FacebookPhotobookFragmentActivity;
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

public class ImageSelectPerformForFacebookPhotobook extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForFacebookPhotobook(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT performGetDefaultFragmentType() {
        applyReceivedIntentInfo();
        return ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.FACE_BOOK_ALBUM;
    }

    private void applyReceivedIntentInfo() {
        ImageSelectIntentData intentData = imageSelectActivity.getIntentData();
        if (intentData == null)
            return;

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
        String postCnt = "";

        int commentCount = 0;
        int photoCount = 0;
        int answerCnt = 0;

        // 댓글 표시 갯수 제한
        commentCount = Integer.parseInt(intentData.getWebCommentCount());

        // 사진 표시 갯수 제한
        photoCount = Integer.parseInt(intentData.getWebPhotoCount());

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

        answerCnt = Integer.parseInt(intentData.getWebAnswerCount());

        postCnt = intentData.getWebPostCount();

        FacebookUtil.BookMaker maker = FacebookUtil.BookMaker.getInstance();
        maker.setTemplateId(templateId);
        maker.setProductCode(productCode);
        maker.setPaperCode(paperCode);
        maker.startTime = startDate;
        maker.endTime = endDate;
        maker.commentLimit = commentCount;
        maker.replyLimit = answerCnt;
        maker.showPhotoType = photoCount == 1 ? FacebookUtil.BookMaker.SHOW_PHOTO_LIMIT_1 : photoCount == 5 ? FacebookUtil.BookMaker.SHOW_PHOTO_LIMIT_5 : FacebookUtil.BookMaker.SHOW_PHOTO_UNLIMIT;
        maker.showMyPostOnly = "mine".equalsIgnoreCase(postCnt);
        maker.coverTitle = projectTitle;

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
                FacebookUtil.BookMaker maker = FacebookUtil.BookMaker.getInstance();
                if (maker != null)
                    maker.clearSelectedPosts(keyList);
            }
            Intent saveIntent = new Intent(imageSelectActivity, FacebookPhotobookFragmentActivity.class);
            imageSelectActivity.startActivity(saveIntent);
            imageSelectActivity.finish();
        }
    }
}
