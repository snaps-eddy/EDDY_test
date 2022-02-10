package com.snaps.mobile.activity.themebook.adapter;

import android.content.Context;
import android.graphics.Rect;
import androidx.viewpager.widget.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaps.common.data.img.BRect;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.edit.spc.SmartRecommendBookCoverEditCanvas;
import com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.holder.SnapsCanvasContainerLayout;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import static com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory.eSnapsCanvasType.SMART_SNAPS_ANALYSIS_PHOTO_BOOK_COVER_EDIT_CANVAS;

/** 상세 편집에서 메인 화면 커버 페이징 처리 **/
public class SmartRecommendBookEditCoverPagerAdapter extends PagerAdapter {
    private static final String TAG = SmartRecommendBookEditCoverPagerAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<SnapsPage> coverList;
    private Map<SnapsPageCanvas, SnapsCanvasContainerLayout> canvasSet;
    private SnapsCommonResultListener<SnapsPageEditRequestInfo> itemClickListener = null;

    SmartRecommendBookEditCoverPagerAdapter(Context context){
        this.context = context;
        this.canvasSet = new HashMap<>();

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        this.coverList = smartSnapsManager.getCoverPageListOfAnalysisPhotoBook();
    }

    public void setItemClickListener(SnapsCommonResultListener<SnapsPageEditRequestInfo> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getCount() {
        return coverList != null ? coverList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if (coverList == null || coverList.size() <= position) return null;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.smart_snaps_analysis_product_edit_list_cover_pager_item, null);

        SnapsPage snapsPage = coverList.get(position);
        SnapsCanvasContainerLayout containerLayout = (SnapsCanvasContainerLayout) view.findViewById(R.id.smart_snaps_analysis_product_edit_list_cover_pager_item_ly);

        loadCanvas(snapsPage, containerLayout, position);

        container.addView(view, 0);
        return view;
    }

    public SnapsPageCanvas findSnapsPageCanvasOnCoverWithTouchOffsetRect(BRect touchRect) {
        if (canvasSet == null) return null;
        Set<SnapsPageCanvas> innerPageCanvasSet = canvasSet.keySet();
        for (SnapsPageCanvas snapsPageCanvas : innerPageCanvasSet) {
            if (snapsPageCanvas == null) continue;

            Rect rect = new Rect();
            snapsPageCanvas.getGlobalVisibleRect(rect);

            if (rect.top > getMinTouchRectTopOffsetY() && rect.contains(touchRect.centerX(), touchRect.centerY())) {
                Dlog.d("findSnapsPageCanvasOnCoverWithTouchOffsetRect() target:" + snapsPageCanvas);
                return snapsPageCanvas;
            }
        }
        return null;
    }

    private int getMinTouchRectTopOffsetY() {
        return UIUtil.convertDPtoPX(context, 48);
    }

    public SnapsPage getSnapsPage(int position) {
        return coverList != null && coverList.size() > position ? coverList.get(position) : null;
    }

    private void loadCanvas(SnapsPage snapsPage, SnapsCanvasContainerLayout canvasParentLayout, final int position) {
        SmartRecommendBookCoverEditCanvas canvas =
                (SmartRecommendBookCoverEditCanvas) SnapsCanvasFactory.createPageCanvasWithType(SMART_SNAPS_ANALYSIS_PHOTO_BOOK_COVER_EDIT_CANVAS, context);

        if (canvas != null) {
            canvas.setSnapsPageClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_clickCoverimg)
                            .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, "0")
                            .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

                    if (itemClickListener != null) {
                        itemClickListener.onResult(new SnapsPageEditRequestInfo.Builder().setCover(true).setCoverTemplateIndex(position).setPageIndex(0).create());
                    }
                }
            });

            canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            canvas.setGravity(Gravity.CENTER);
            canvas.setId(R.id.fragment_root_view_id);

            canvasParentLayout.addView(canvas);

            canvas.setEnableButton(true);

            canvas.setIsPageSaving(false);

            canvas.setLandscapeMode(false);

            PhotobookCommonUtils.imageRange(snapsPage);

            canvas.setSnapsPage(snapsPage, 0, true, null);

            addCanvasOnContainer(canvasParentLayout, canvas);
        }
    }

    private void addCanvasOnContainer(SnapsCanvasContainerLayout canvasParentLayout, SnapsPageCanvas canvas) {
        if (canvasParentLayout == null) return;

        if (canvasSet.containsKey(canvas)) {
            SnapsCanvasContainerLayout parent = canvasSet.get(canvas);
            if (parent != null) {
                parent.removeAllViews();
            }
        }

        canvasParentLayout.setSnapsPageCanvas(canvas);

        canvasSet.put(canvas, canvasParentLayout);
    }

    public Map<SnapsPageCanvas, SnapsCanvasContainerLayout> getCanvasSet() {
        return canvasSet;
    }

    @Override
    public void notifyDataSetChanged() {
        refreshCanvas();
        super.notifyDataSetChanged();
    }

    private void refreshCanvas() {
        if (getCanvasSet() == null) return;

        try {
            for(Map.Entry<SnapsPageCanvas, SnapsCanvasContainerLayout> entrySet : getCanvasSet().entrySet()) {
                SnapsCanvasContainerLayout containerLayout = entrySet.getValue();
                if (containerLayout != null) {
                    containerLayout.refreshSnapsPageCanvas();
                }
            }
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
