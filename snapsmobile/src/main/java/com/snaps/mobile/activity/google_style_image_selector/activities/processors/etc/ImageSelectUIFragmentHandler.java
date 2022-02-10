package com.snaps.mobile.activity.google_style_image_selector.activities.processors.etc;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectStateChangedListener;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectBaseFragment;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;

import java.util.Stack;

import static com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.UNKNOWN;

/**
 * Created by ysjeong on 2016. 12. 15..
 */

public class ImageSelectUIFragmentHandler {

    private Stack<ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT> fragmentTypeStack = null;    //폰 사진, SNS 사진 등 Fragment가 변경될 때마다 Type으로 관리 한다.
    private ImageSelectBaseFragment currentFragment = null;
    private ImageSelectActivityV2 activity = null;

    public ImageSelectUIFragmentHandler(ImageSelectActivityV2 activity) {
        this.activity = activity;

        this.fragmentTypeStack = new Stack<>();
    }

    //Fragment 변경
    public void loadFragment(IImageSelectStateChangedListener listener,
                               ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT fragmentType) {
        changeFragment(listener, fragmentType, true);

    }

    public void changeFragment(IImageSelectStateChangedListener listener,
                               ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT fragmentType) {
        changeFragment(listener, fragmentType, false);

    }

    private void changeFragment(IImageSelectStateChangedListener listener,
                               ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT fragmentType,
                               boolean isFirstLoad) {
        if (fragmentType == null || activity == null) return;

        int animA = isFirstLoad ? -1 : R.anim.anim_slide_in_right;
        int animB = isFirstLoad ? -1 : R.anim.anim_slide_out_left;
        int animOutA = isFirstLoad ? -1 : R.anim.anim_slide_in_left;
        int animOutB = isFirstLoad ? -1 : R.anim.anim_slide_out_right;

        currentFragment = ImageSelectFragmentFactory.createFragment(activity, fragmentType);
        if (currentFragment != null) {
            int frameLayoutId = R.id.google_photo_style_image_select_frame_main_ly;

            boolean replaceBackStack = !(Config.isSnapsPhotoPrint() && fragmentType == ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.SELECT_IMAGE_SRC); //기존 코드가..사진 인화는 replce

            if (replaceBackStack) {
                FragmentUtil.replceBackStack(frameLayoutId, activity, currentFragment, null, animA, animB, animOutA, animOutB);

            } else {
                FragmentUtil.replce(frameLayoutId, activity, currentFragment, null, animA, animB, animOutA, animOutB);
            }
            
            currentFragment.setItemStateChangedListener(listener);

            pushFragmentType(fragmentType);
        }
    }

    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT getCurrentFragmentType() {
        if (fragmentTypeStack == null || fragmentTypeStack.isEmpty()) return UNKNOWN;
        return fragmentTypeStack.lastElement();
    }

    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT getRootFragmentType() {
        if (fragmentTypeStack == null || fragmentTypeStack.isEmpty()) return UNKNOWN;
        return fragmentTypeStack.firstElement();
    }

    public void popFragmentType() {
        if (fragmentTypeStack == null) return;
        fragmentTypeStack.pop();
    }

    private void pushFragmentType(ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT type) {
        if (fragmentTypeStack == null) fragmentTypeStack = new Stack<>();
        fragmentTypeStack.push(type);
    }

    public int getFragmentTypeSize() {
        if (fragmentTypeStack == null) return 0;
        return fragmentTypeStack.size();
    }

    public ImageSelectBaseFragment getCurrentFragment() {
        return currentFragment;
    }
}
