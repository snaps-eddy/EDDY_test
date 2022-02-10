package com.snaps.mobile.activity.themebook.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaps.common.imp.iSnapsPageCanvasInterface;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.edit.spc.SmartRecommendBookDetailEditCanvas;
import com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.holder.SnapsCanvasContainerLayout;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory.eSnapsCanvasType.SMART_SNAPS_ANALYSIS_PRODUCT_DETAIL_EDIT_CANVAS;

/** 상세 편집에서 SnapsPage 페이징 처리 **/
public class SmartRecommendBookDetailEditPagerAdapter extends PagerAdapter implements iSnapsPageCanvasInterface {
    private static final String TAG = SmartRecommendBookDetailEditPagerAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<SnapsPage> pageList;
    private Map<SnapsPageCanvas, SnapsCanvasContainerLayout> canvasSet;
    private InterceptTouchableViewPager viewPager = null;

    public SmartRecommendBookDetailEditPagerAdapter(Context context){
        this.context = context;
        this.canvasSet = new HashMap<>();
    }

    public void initCanvasMatrix() {
        if (canvasSet == null) return;
        try {
            for (Map.Entry<SnapsPageCanvas, SnapsCanvasContainerLayout> canvas : canvasSet.entrySet()) {
                if (canvas == null) continue;
                SnapsPageCanvas snapsPageCanvas = canvas.getKey();
                if (snapsPageCanvas == null) continue;

                snapsPageCanvas.initLocation();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void setDataWithEditRequestInfo(SnapsPageEditRequestInfo requestInfo) {
        if (requestInfo == null) return;

        clearCanvasSet();

        initPageList();

        if (requestInfo.isCover()) {
            SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
            SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
            if (snapsTemplate == null) return;

            ArrayList<SnapsPage> pageList = snapsTemplate.getPages();
            if (pageList != null && pageList.size() > 1) {
                this.pageList.add(pageList.get(0));
            }
        } else {
            SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
            SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
            if (snapsTemplate == null) return;

            ArrayList<SnapsPage> pageList = snapsTemplate.getPages();
            if (pageList != null && pageList.size() > 1) {
                for (int ii = 1; ii<pageList.size(); ii++) {
                    this.pageList.add(pageList.get(ii));
                }
            }
        }
    }

    private void initPageList() {
        if (pageList != null) pageList.clear();
        else pageList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return pageList != null ? pageList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public SnapsPage getSnapsPageOnPageList(int position) {
        return pageList != null && pageList.size() > position ? pageList.get(position) : null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if (pageList == null || pageList.size() <= position) return null;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.smart_snaps_analysis_product_edit_list_cover_pager_item, null);

        SnapsPage snapsPage = pageList.get(position);
        SnapsCanvasContainerLayout containerLayout = (SnapsCanvasContainerLayout) view.findViewById(R.id.smart_snaps_analysis_product_edit_list_cover_pager_item_ly);

        loadCanvas(snapsPage, containerLayout, position);

        container.addView(view, 0);
        return view;
    }

    private void loadCanvas(SnapsPage snapsPage, SnapsCanvasContainerLayout canvasParentLayout, final int position) {
        SmartRecommendBookDetailEditCanvas canvas =
                (SmartRecommendBookDetailEditCanvas) SnapsCanvasFactory.createPageCanvasWithType(SMART_SNAPS_ANALYSIS_PRODUCT_DETAIL_EDIT_CANVAS, context);

        if (canvas != null) {
            canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            canvas.setGravity(Gravity.CENTER);
            canvas.setId(R.id.fragment_root_view_id);

            canvasParentLayout.addView(canvas);

            canvas.setEnableButton(true);

            canvas.setIsPageSaving(false);

            canvas.setLandscapeMode(false);

            canvas.setCallBack(this);
            PhotobookCommonUtils.imageRange(snapsPage);

            canvas.setSnapsPage(snapsPage, snapsPage.getPageID(), true, null);

            if(getViewPager() != null) {
                canvas.setViewPager(getViewPager());
                getViewPager().addCanvas(canvas);
            }

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

    private void clearCanvasSet() {
        if (canvasSet != null) {
            canvasSet.clear();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
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

    private void refreshCanvas(int pageIndex) {
        if (getCanvasSet() == null) return;

        try {
            for(Map.Entry<SnapsPageCanvas, SnapsCanvasContainerLayout> entrySet : getCanvasSet().entrySet()) {
                SnapsCanvasContainerLayout containerLayout = entrySet.getValue();
                if (containerLayout != null) {
                    SnapsPageCanvas snapsPageCanvas = containerLayout.getSnapsPageCanvas();
                    if (snapsPageCanvas == null) continue;

                    if (pageIndex == snapsPageCanvas.getPageNumber()) {
                        containerLayout.refreshSnapsPageCanvas();
                    }
                }
            }
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    @Override
    public void notifyDataSetChanged() {
        refreshCanvas();

        super.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(int position) {
        refreshCanvas(position);

        super.notifyDataSetChanged();
    }

    @Override
    public void onImageLoadStart() {}

    @Override
    public void onImageLoadComplete(int page) {}

    public InterceptTouchableViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(InterceptTouchableViewPager viewPager) {
        this.viewPager = viewPager;
    }
}
