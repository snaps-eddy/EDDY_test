package com.snaps.mobile.edit_activity_tools.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.customview.EditActivityThumbnailRecyclerView;
import com.snaps.mobile.edit_activity_tools.customview.EditActivityThumbnailSpacingDecoration;

import java.util.ArrayList;

import errorhandle.logger.Logg;

public class EditActivityThumbnailUtils {
    private static final String TAG = EditActivityThumbnailUtils.class.getSimpleName();
    public static final int PAGE_MOVE_TYPE_NONE = 0;
    public static final int PAGE_MOVE_TYPE_PREV = 1;
    public static final int PAGE_MOVE_TYPE_NEXT = 2;

    private Context conext = null;
    private EditActivityThumbnailRecyclerView mRecyclerView;
    private SnapsTemplate template = null;
    private ArrayList<SnapsPage> _pageList = null;
    private boolean isLandscapeMode = false;
    private BaseEditActivityThumbnailAdapter adpater = null;
    private EditActivityThumbnailSpacingDecoration spacingDecoration = null;

    private int maxPage = 75; // 최대페이지
    private int minPage = 15; // 최소페이지
    private int spinePage = 37; // 책등추가 페이지 -1이면 하드커버인경우..

    public static Rect getRectOffsetThumbnailDimens(Activity activity) {
        boolean isExistText = Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isCalendar() || Config.isSimpleMakingBook() || Const_PRODUCT.isCardProduct();
        final int MAX_HEIGHT = isExistText ? 50 : 60;
        int offsetImgWidth = UIUtil.convertDPtoPX(activity, 85);
        int offsetImgHeight = UIUtil.convertDPtoPX(activity, MAX_HEIGHT);

        return new Rect(0, 0, offsetImgWidth, offsetImgHeight);
    }

    public static Rect getRectNewPolaroidOffsetThumbnailDimens(Activity activity) {
        int offsetImgWidth = UIUtil.convertDPtoPX(activity, 85);
        int offsetImgHeight = UIUtil.convertDPtoPX(activity, 85);

        return new Rect(0, 0, offsetImgWidth, offsetImgHeight);
    }

    public static Rect getRectCardOffsetThumbnailDimens(Activity activity, boolean type) {
        final int MAX_WDITH = type ? 43 : 29;
        final int MAX_HEIGHT = type ? 31 : 41;
        int offsetImgWidth = UIUtil.convertDPtoPX(activity, MAX_WDITH);
        int offsetImgHeight = UIUtil.convertDPtoPX(activity, MAX_HEIGHT);

        return new Rect(0, 0, offsetImgWidth, offsetImgHeight);
    }

    public static Rect getNewYearsCardRectOffsetThumbnailDimens(Activity activity) {

        int offsetImgWidth = UIUtil.convertDPtoPX(activity, 78);
        int offsetImgHeight = UIUtil.convertDPtoPX(activity, 56);

        return new Rect(0, 0, offsetImgWidth, offsetImgHeight);
    }

    public static Rect getNameStickerRectOffsetThumbnailDimens(Activity activity) {

        int offsetImgWidth = UIUtil.convertDPtoPX(activity, 80);
        int offsetImgHeight = UIUtil.convertDPtoPX(activity, 33);

        return new Rect(0, 0, offsetImgWidth, offsetImgHeight);
    }

    public static Rect getCardShapeRectOffsetThumbnailDimens(Activity activity) { //FIXME
        int offsetImgWidth = UIUtil.convertDPtoPX(activity, 52);
        int offsetImgHeight = UIUtil.convertDPtoPX(activity, 81);

        return new Rect(0, 0, offsetImgWidth, offsetImgHeight);
    }

    public static Rect getAccordionCardRectOffsetThumbnailDimens(Activity activity) { //FIXME
        int offsetImgWidth = UIUtil.convertDPtoPX(activity, 84);
        int offsetImgHeight = UIUtil.convertDPtoPX(activity, 20);

        return new Rect(0, 0, offsetImgWidth, offsetImgHeight);
    }

    public static Rect getSloganRectOffsetThumbnailDimens(Activity activity) { //FIXME
        int offsetImgWidth = UIUtil.convertDPtoPX(activity, 80);
        int offsetImgHeight = UIUtil.convertDPtoPX(activity, 23);

        return new Rect(0, 0, offsetImgWidth, offsetImgHeight);
    }

    public static Rect getRectThumbanilViewSizeOffsetPage(Activity activity, boolean isLandscapeMode, SnapsPage page) {
        Rect offsetRect = EditActivityThumbnailUtils.getRectOffsetThumbnailDimens(activity);

        //Ben 땜방
        //폴라로이드 썸네일 위쪽에 붙어서 높이를 늘려서 대충 이뻐 보이게..
        if (Const_PRODUCT.isNewPolaroidPackProduct()) {
            offsetRect = getRectNewPolaroidOffsetThumbnailDimens(activity);
        }

        if (page == null || offsetRect == null) {
            return null;
        }

        int imgWidth = offsetRect.width();
        int imgHeight = offsetRect.height();
        int offsetWidth = imgWidth;
        int offsetHeight = imgHeight;
        int pageWidth = 84;
        int pageHeight = 20;

        float fRat = 0.f;

        try {
            pageWidth = Integer.parseInt(page.width);
            pageHeight = Integer.parseInt(page.height);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (pageWidth > pageHeight) {
            fRat = pageHeight / (float) pageWidth;
            imgHeight = (int) (imgWidth * fRat);
        } else {
            fRat = pageWidth / (float) pageHeight;
            imgWidth = (int) (imgHeight * fRat);
        }

        float scale = 1;

        if (isLandscapeMode) {
            scale = offsetWidth / (float) imgWidth;
            imgWidth = offsetWidth;
            imgHeight = (int) (imgHeight * scale);
        } else {
            scale = offsetHeight / (float) imgHeight;
            imgHeight = offsetHeight;
            imgWidth = (int) (imgWidth * scale);
        }

        return new Rect(0, 0, imgWidth, imgHeight);
    }

    public static Rect getRectCardThumbanilViewSizeOffsetPage(Activity activity, boolean isLandscapeMode, SnapsPage page) {
        Rect offsetRect = null;
        int pageWidth = 85;
        int pageHeight = 50;
        try {
            pageWidth = Integer.parseInt(page.width);
            pageHeight = Integer.parseInt(page.height);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        boolean type = pageWidth > pageHeight;
        offsetRect = EditActivityThumbnailUtils.getRectCardOffsetThumbnailDimens(activity, type);

        if (page == null || offsetRect == null) {
            return null;
        }

        int imgWidth = offsetRect.width();
        int imgHeight = offsetRect.height();
        int offsetWidth = imgWidth;
        int offsetHeight = imgHeight;

        float fRat = 0.f;

        if (pageWidth > pageHeight) {
            fRat = pageHeight / (float) pageWidth;
            imgHeight = (int) (imgWidth * fRat);
        } else {
            fRat = pageWidth / (float) pageHeight;
            imgWidth = (int) (imgHeight * fRat);
        }

        float scale = 1;

        scale = offsetHeight / (float) imgHeight;
        imgHeight = offsetHeight;
        imgWidth = (int) (imgWidth * scale);

        return new Rect(0, 0, imgWidth, imgHeight);
    }

    public static Rect getRectAccordionCardThumbanilViewSizeOffsetPage(Activity activity, boolean isLandscapeMode, SnapsPage page) {
        Rect offsetRect = EditActivityThumbnailUtils.getAccordionCardRectOffsetThumbnailDimens(activity);
        if (page == null || offsetRect == null) {
            return null;
        }

        int imgWidth = offsetRect.width();
        int imgHeight = offsetRect.height();
        int offsetWidth = imgWidth;
        int offsetHeight = imgHeight;
        int pageWidth = 78;
        int pageHeight = 78;

        float fRat = 0.f;

        try {
            pageWidth = Integer.parseInt(page.width);
            pageHeight = Integer.parseInt(page.height);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (pageWidth > pageHeight) {
            fRat = pageHeight / (float) pageWidth;
            imgHeight = (int) (imgWidth * fRat);
        } else {
            fRat = pageWidth / (float) pageHeight;
            imgWidth = (int) (imgHeight * fRat);
        }

        float scale = 1;

        if (isLandscapeMode) {
            scale = offsetWidth / (float) imgWidth;
            imgWidth = offsetWidth;
            imgHeight = (int) (imgHeight * scale);
        } else {
            scale = offsetHeight / (float) imgHeight;
            imgHeight = offsetHeight;
            imgWidth = (int) (imgWidth * scale);
        }

        return new Rect(0, 0, imgWidth, imgHeight);
    }

    public static Rect getRectNewYearsCardThumbanilViewSizeOffsetPage(Activity activity, boolean isLandscapeMode, SnapsPage page) {
        Rect offsetRect = null;
        if (Const_PRODUCT.isNameStickerProduct()) {
            offsetRect = EditActivityThumbnailUtils.getNameStickerRectOffsetThumbnailDimens(activity);
        } else {
            offsetRect = EditActivityThumbnailUtils.getNewYearsCardRectOffsetThumbnailDimens(activity);
        }

        if (page == null || offsetRect == null) {
            return null;
        }

        int imgWidth = offsetRect.width();
        int imgHeight = offsetRect.height();
        int offsetWidth = imgWidth;
        int offsetHeight = imgHeight;
        int pageWidth = 78;
        int pageHeight = 78;

        float fRat = 0.f;

        try {
            pageWidth = Integer.parseInt(page.width);
            pageHeight = Integer.parseInt(page.height);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (pageWidth > pageHeight) {
            fRat = pageHeight / (float) pageWidth;
            imgHeight = (int) (imgWidth * fRat);
        } else {
            fRat = pageWidth / (float) pageHeight;
            imgWidth = (int) (imgHeight * fRat);
        }

        float scale = 1;

        if (isLandscapeMode) {
            scale = offsetWidth / (float) imgWidth;
            imgWidth = offsetWidth;
            imgHeight = (int) (imgHeight * scale);
        } else {
            scale = offsetHeight / (float) imgHeight;
            imgHeight = offsetHeight;
            imgWidth = (int) (imgWidth * scale);
        }

        return new Rect(0, 0, imgWidth, imgHeight);
    }

    public static Rect getRectCardShapeThumbnailViewSizeOffsetPage(Activity activity, boolean isLandscapeMode, SnapsPage page) { //FIXME
        Rect offsetRect = EditActivityThumbnailUtils.getCardShapeRectOffsetThumbnailDimens(activity);
        if (page == null || offsetRect == null) {
            return null;
        }

        int imgWidth = offsetRect.width();
        int imgHeight = offsetRect.height();
        int offsetWidth = imgWidth;
        int offsetHeight = imgHeight;
        int pageWidth = 52;
        int pageHeight = 81;

        float fRat = 0.f;

        try {
            pageWidth = Integer.parseInt(page.width);
            pageHeight = Integer.parseInt(page.height);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (pageWidth > pageHeight) {
            fRat = pageHeight / (float) pageWidth;
            imgHeight = (int) (imgWidth * fRat);
        } else {
            fRat = pageWidth / (float) pageHeight;
            imgWidth = (int) (imgHeight * fRat);
        }

        float scale = 1;

        if (isLandscapeMode) {
            scale = offsetWidth / (float) imgWidth;
            imgWidth = offsetWidth;
            imgHeight = (int) (imgHeight * scale);
        } else {
            scale = offsetHeight / (float) imgHeight;
            imgHeight = offsetHeight;
            imgWidth = (int) (imgWidth * scale);
        }

        return new Rect(0, 0, imgWidth * 2, imgHeight);
    }

    public static Rect getRectSloganThumbnailViewSizeOffsetPage(Activity activity, boolean isLandscapeMode, SnapsPage page) { //FIXME
        Rect offsetRect = EditActivityThumbnailUtils.getSloganRectOffsetThumbnailDimens(activity);
        if (page == null || offsetRect == null) {
            return null;
        }

        int imgWidth = offsetRect.width();
        int imgHeight = offsetRect.height();
        int offsetWidth = imgWidth;
        int offsetHeight = imgHeight;
        int pageWidth = 52;
        int pageHeight = 81;

        float fRat = 0.f;

        try {
            pageWidth = (int)Float.parseFloat(page.width);
            pageHeight = (int)Float.parseFloat(page.height);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (pageWidth > pageHeight) {
            fRat = pageHeight / (float) pageWidth;
            imgHeight = (int) (imgWidth * fRat);
        } else {
            fRat = pageWidth / (float) pageHeight;
            imgWidth = (int) (imgHeight * fRat);
        }

        float scale = 1;

        if (isLandscapeMode) {
            scale = offsetWidth / (float) imgWidth;
            imgWidth = offsetWidth;
            imgHeight = (int) (imgHeight * scale);
        } else {
            scale = offsetHeight / (float) imgHeight;
            imgHeight = offsetHeight;
            imgWidth = (int) (imgWidth * scale);
        }

        return new Rect(0, 0, imgWidth, imgHeight * 2);
    }

    public EditActivityThumbnailUtils() {
    }

    public void init(Context context, EditActivityThumbnailRecyclerView recyclerView, BaseEditActivityThumbnailAdapter adapter, boolean isLandscapeMode) {
        this.conext = context;
        this.mRecyclerView = recyclerView;
        this.isLandscapeMode = isLandscapeMode;
        this.adpater = adapter;
    }

    public void setPageList(ArrayList<SnapsPage> _pageList) {
        this._pageList = _pageList;
    }

    public static boolean isCalendarVerticalProduct() {
        return Config.isCalendarMini(Config.getPROD_CODE()) || Config.isCalendarNormalVert(Config.getPROD_CODE()) || Config.isCalenderWall(Config.getPROD_CODE());
    }

    public void setItemDecoration() {
        if (isSkipItemDecorationProduct()) {
            return;
        }

        try {
            spacingDecoration
                    = new EditActivityThumbnailSpacingDecoration(conext);

            spacingDecoration.setLandscapeMode(isLandscapeMode);

            if (isCalendarVerticalProduct()) {
                Rect rect = getRectThumbanilViewSizeOffsetPage((Activity) conext, isLandscapeMode, _pageList != null ? _pageList.get(0) : null);
                spacingDecoration.setItemDimensRect(rect);

            }
            spacingDecoration.setSpaceOffset();

            if (mRecyclerView != null) {
                mRecyclerView.addItemDecoration(spacingDecoration);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private boolean isSkipItemDecorationProduct() {
        return Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct();
    }

    public void releaseInstance() {
        if (mRecyclerView != null && spacingDecoration != null) {
            mRecyclerView.removeItemDecoration(spacingDecoration);
        }
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public int getMinPage() {
        return minPage;
    }

    public void setMinPage(int minPage) {
        this.minPage = minPage;
    }

    public int getSpinePage() {
        return spinePage;
    }

    public void setSpinePage(int spinePage) {
        this.spinePage = spinePage;
    }

    public boolean isLimitPageCount() {
        if (_pageList == null) {
            return false;
        }
        return _pageList.size() <= (getMinPage() + 2); //TODO  차일드 갯수가 0으로 들어올 수 있음..
    }

    private static String getCalendarMonthText(Context context, int month) {
        //String monthText = String.valueOf(month) + (ContextUtil.getString(com.snaps.mobile.R.string.month, "월"));
        String monthText = String.valueOf(month) + (context.getString(com.snaps.mobile.R.string.month));
        if (Config.useEnglish()) {
            monthText = StringUtil.getMonthStr(month - 1, true);
        }
        return monthText;
    }

    public static void setDragViewsText(Context context, int totalSize, int curPosition, int position, TextView introindex, TextView leftIndex, TextView rightIndex) {
        if (Config.isCalendar()) {
            if (totalSize == 14) {
                totalSize = 12;
            }

            if (totalSize >= 26) {
                totalSize = 24;
            }

            int div = totalSize % 2;
            int nStartYear = GetTemplateXMLHandler.getStartYear();
            int nStartMonth = GetTemplateXMLHandler.getStartMonth();

            Dlog.d("setDragViewsText() totalSize:" + totalSize);
            int label;
            int cmp = 0;
            if (Config.isCalenderWall(Config.getPROD_CODE())) {
                cmp = position - 1;
            } else if (div != 0) {
                cmp = (int) (Math.ceil((double) position / 2.0)) - 1;
            } else {
                if (totalSize > 12 && !Config.isCalenderWall(Config.getPROD_CODE())) {
                    cmp = (int) (Math.floor(position / 2.0));
                } else {
                    cmp = position;
                }
            }

            if ((nStartMonth + cmp) > 12) {
                label = ((nStartMonth + cmp)) % 12;
            } else {
                label = nStartMonth + cmp;
            }

            String _label = "";
            String sideLabel = "";
            if (div == 0) {
                // 커버가 없는 경우
                if (totalSize == 12) {
                    _label = getCalendarMonthText(context, label) + " ";

                } else if (totalSize == 24) {
                    sideLabel = context.getString(position % 2 == 0 ? R.string.front : R.string.back);
                    _label = getCalendarMonthText(context, label) + " " + "(" + sideLabel + ")";
                }
            } else if (totalSize == 13) {
                _label = (position == 0 ? context.getString(R.string.cover) : getCalendarMonthText(context, label) + " ");

            } else if (totalSize == 25) {
                if (position == 0) {
                    _label = context.getString(R.string.cover);
                } else {
                    sideLabel = context.getString(position % 2 == 0 ? R.string.front : R.string.back);
                    _label = getCalendarMonthText(context, label) + " " + "(" + sideLabel + ")";
                }
            }

            introindex.setText(_label);
            introindex.setVisibility(View.VISIBLE);
            introindex.setTextColor(Color.argb(255, 153, 153, 153));
            leftIndex.setVisibility(View.INVISIBLE);
            rightIndex.setVisibility(View.INVISIBLE);

        } else if (Const_PRODUCT.isPackageProduct()) {
            introindex.setVisibility(View.GONE);
            leftIndex.setVisibility(View.GONE);
            rightIndex.setVisibility(View.GONE);

        } else if (Const_PRODUCT.isCardProduct()) {
            int textColor = (position == curPosition ?
                    Color.argb(255, 229, 71, 54) : Color.argb(153, 153, 153, 153));
            introindex.setText(position % 2 == 0 ? R.string.front : Const_PRODUCT.isCardShapeFolder() ? R.string.inner_side : R.string.back);
            introindex.setVisibility(View.VISIBLE);
            introindex.setTextColor(textColor);
            leftIndex.setVisibility(View.GONE);
            rightIndex.setVisibility(View.GONE);

        } else if (Const_PRODUCT.isAccordionCardProduct()) {
            int textColor = (position == curPosition ?
                    Color.argb(255, 229, 71, 54) : Color.argb(153, 153, 153, 153));
            introindex.setText(position % 2 == 0 ? R.string.front : R.string.back);
            introindex.setVisibility(View.VISIBLE);
            introindex.setTextColor(textColor);
            leftIndex.setVisibility(View.GONE);
            rightIndex.setVisibility(View.GONE);

        } else if (Const_PRODUCT.isBabyNameStikerGroupProduct()) {
            int textColor = (position == curPosition ?
                    Color.argb(255, 229, 71, 54) : Color.argb(153, 153, 153, 153));
            introindex.setText(position == 0 ? "1p" : "2p");
            introindex.setVisibility(View.VISIBLE);
            introindex.setTextColor(textColor);
            leftIndex.setVisibility(View.GONE);
            rightIndex.setVisibility(View.GONE);

        } else if (Const_PRODUCT.isMagicalReflectiveSloganProduct() ||
                Const_PRODUCT.isReflectiveSloganProduct() ||
                Const_PRODUCT.isHolographySloganProduct())
        {
            introindex.setText(position == 0 ? R.string.front : R.string.back);
            introindex.setTextColor(Color.parseColor(position == curPosition ? "#e36a63" : "#999999"));
            introindex.setVisibility(View.VISIBLE);
            leftIndex.setVisibility(View.INVISIBLE);
            rightIndex.setVisibility(View.INVISIBLE);

        } else {
            int textColor = (position == curPosition ? Color.parseColor("#e36a63") : Color.parseColor("#999999"));

            if (position <= 1) {
                introindex.setText(position == 0 ? R.string.cover : R.string.inner_page);
                introindex.setVisibility(View.VISIBLE);
                introindex.setTextColor(textColor);
                leftIndex.setVisibility(View.INVISIBLE);
                rightIndex.setVisibility(View.INVISIBLE);
            }
            else {
                int pp = (position - 2) * 2 + 2;
                introindex.setVisibility(View.INVISIBLE);
                leftIndex.setVisibility(View.VISIBLE);
                rightIndex.setVisibility(View.VISIBLE);
                leftIndex.setText("" + pp);
                rightIndex.setText("" + (++pp));
                leftIndex.setTextColor(textColor);
                rightIndex.setTextColor(textColor);
            }
        }
    }

    public void setSelectionDragView(final int moveType, final int selectedPosition) {
        if (mRecyclerView == null || _pageList == null) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshBottomViewOutLine(selectedPosition);
                mRecyclerView.scrollToPosition(selectedPosition);
            }
        }, 100);
    }

    public void setSelectionCardShapeDragView(final int moveType, final int selectedPosition) {
        if (mRecyclerView == null || _pageList == null) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final int SELECTION_POSITION = selectedPosition / 2;

                mRecyclerView.scrollToIdx(moveType, _pageList.size() / 2, SELECTION_POSITION); //??? FIXME
                refreshBottomViewOutLine(selectedPosition);
            }
        }, 100);
    }

    public void refreshBottomViewOutLine(int selectedPosition) {
        if (adpater == null || _pageList == null || _pageList.size() <= selectedPosition || _pageList.get(selectedPosition).isSelected) {
            return;
        }

        for (SnapsPage p : _pageList) {
            p.isSelected = false;
        }

        _pageList.get(selectedPosition).isSelected = true;

        adpater.refreshThumbnailsLineAndText(selectedPosition);
    }

    public void sortPagesIndex(Activity activity, int selectedPosition) {
        PhotobookCommonUtils.sortPagesIndex(_pageList, selectedPosition);
    }

    public void sortPagesIndex(Activity activity, ArrayList<SnapsPage> _frontPageList, ArrayList<SnapsPage> _backPageList) {
        if (activity == null || _frontPageList == null || _backPageList == null || _backPageList.isEmpty()) {
            return;
        }
        // 페이지 인덱스 및 이미지 인덱스 조정...
        for (int i = 0; i < _frontPageList.size(); i++) {
            SnapsPage p = _frontPageList.get(i);
            p.setPageID(i);
            p.isSelected = false;

            SnapsPage backP = _backPageList.get(i);
            backP.setPageID(i);

            for (SnapsControl control : p.getLayoutList()) {
                control.setPageIndex(i);

                // 이미지 정렬를 위해..
                if (control instanceof SnapsLayoutControl) {

                    if (((SnapsLayoutControl) control).imgData != null) {
                        // ((SnapsLayoutControl) control).imgData.IMG_IDX = i * 2 + Integer.parseInt(control.regValue);
                        ((SnapsLayoutControl) control).imgData.IMG_IDX = PhotobookCommonUtils.getImageIDX(i, control.regValue);
                        ((SnapsLayoutControl) control).imgData.pageIDX = i;
                    }
                }
            }

            for (SnapsControl control : backP.getLayoutList()) {
                control.setPageIndex(i);

                // 이미지 정렬를 위해..
                if (control instanceof SnapsLayoutControl) {

                    if (((SnapsLayoutControl) control).imgData != null) {
                        ((SnapsLayoutControl) control).imgData.IMG_IDX = PhotobookCommonUtils.getImageIDX(i, control.regValue);
                        ((SnapsLayoutControl) control).imgData.pageIDX = i;
                    }
                }
            }
        }
    }
}
