package com.snaps.common.customui.sticky;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStripForSticky;
import com.snaps.mobile.component.CustomSensitivityViewPager;

/**
 * Created by ysjeong on 16. 8. 3..
 */
@Deprecated
public class StickyControls {

    private CustomSensitivityViewPager viewPager;
    private PagerSlidingTabStripForSticky pagerSlidingTabStripForSticky;
    private LinearLayout imageLayout;
    private RelativeLayout titleLayout;
    private TextView titleText;
    private TextView stickyTitle;
    private TextView stickyDesc;
    private TextView stickyInfo;
    private TextView stickyInfoFake;
    private ImageView backKey;
    private ImageView menuKey;
    private ImageView infoIcon;
    private ImageView mainImage;

    private StickyControls(StickyControlBuilder builder) {
        viewPager = builder.viewPager;
        pagerSlidingTabStripForSticky = builder.pagerSlidingTabStripForSticky;
        imageLayout = builder.imageLayout;
        titleLayout = builder.titleLayout;
        titleText = builder.titleText;
        stickyTitle = builder.stickyTitle;
        stickyDesc = builder.stickyDesc;
        stickyInfo = builder.stickyInfo;
        stickyInfoFake = builder.stickyInfoFake;
        backKey = builder.backKey;
        menuKey = builder.menuKey;
        infoIcon = builder.infoIcon;
        mainImage = builder.mainImage;
    }

    public CustomSensitivityViewPager getViewPager() {
        return viewPager;
    }

    public PagerSlidingTabStripForSticky getPagerSlidingTabStripForSticky() {
        return pagerSlidingTabStripForSticky;
    }

    public LinearLayout getImageLayout() {
        return imageLayout;
    }

    public RelativeLayout getTitleLayout() {
        return titleLayout;
    }

    public TextView getTitleText() {
        return titleText;
    }

    public TextView getStickyTitle() {
        return stickyTitle;
    }

    public TextView getStickyDesc() {
        return stickyDesc;
    }

    public TextView getStickyInfo() {
        return stickyInfo;
    }

    public TextView getStickyInfoFake() {
        return stickyInfoFake;
    }

    public ImageView getBackKey() {
        return backKey;
    }

    public ImageView getMenuKey() {
        return menuKey;
    }

    public ImageView getInfoIcon() {
        return infoIcon;
    }

    public ImageView getMainImage() {
        return mainImage;
    }

    public static class StickyControlBuilder {
        private CustomSensitivityViewPager viewPager;
        private PagerSlidingTabStripForSticky pagerSlidingTabStripForSticky;
        private LinearLayout imageLayout;
        private RelativeLayout titleLayout;
        private TextView titleText;
        private TextView stickyTitle;
        private TextView stickyDesc;
        private TextView stickyInfo;
        private TextView stickyInfoFake;
        private ImageView backKey;
        private ImageView menuKey;
        private ImageView infoIcon;
        private ImageView mainImage;

        public StickyControlBuilder setViewPager(CustomSensitivityViewPager viewPager) {
            this.viewPager = viewPager;
            return this;
        }

        public StickyControlBuilder setPagerSlidingTabStripForSticky(PagerSlidingTabStripForSticky pagerSlidingTabStripForSticky) {
            this.pagerSlidingTabStripForSticky = pagerSlidingTabStripForSticky;
            return this;
        }

        public StickyControlBuilder setImageLayout(LinearLayout imageLayout) {
            this.imageLayout = imageLayout;
            return this;
        }

        public StickyControlBuilder setTitleLayout(RelativeLayout titleLayout) {
            this.titleLayout = titleLayout;
            return this;
        }

        public StickyControlBuilder setTitleText(TextView titleText) {
            this.titleText = titleText;
            return this;
        }

        public StickyControlBuilder setStickyTitle(TextView stickyTitle) {
            this.stickyTitle = stickyTitle;
            return this;
        }

        public StickyControlBuilder setStickyDesc(TextView stickyDesc) {
            this.stickyDesc = stickyDesc;
            return this;
        }

        public StickyControlBuilder setStickyInfo(TextView stickyInfo) {
            this.stickyInfo = stickyInfo;
            return this;
        }

        public StickyControlBuilder setStickyInfoFake(TextView stickyInfoFake) {
            this.stickyInfoFake = stickyInfoFake;
            return this;
        }

        public StickyControlBuilder setBackKey(ImageView backKey) {
            this.backKey = backKey;
            return this;
        }

        public StickyControlBuilder setMenuKey(ImageView menuKey) {
            this.menuKey = menuKey;
            return this;
        }

        public StickyControlBuilder setInfoIcon(ImageView infoIcon) {
            this.infoIcon = infoIcon;
            return this;
        }

        public StickyControlBuilder setMainImage(ImageView mainImage) {
            this.mainImage = mainImage;
            return this;
        }

        public StickyControls createControls() {
            return new StickyControls(this);
        }
    }
}
