package com.snaps.mobile.activity.common.products.multi_page_product;

import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.mobile.R;
import com.snaps.mobile.autosave.IAutoSaveConstants;

import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A2_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A2_VERTICAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A3_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A3_VERTICAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A4_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A4_VERTICAL;

/**
 * Created by kimduckwon on 2018. 1. 15..
 */

public class PosterEditor extends SnapsCounterPageEditor{
    private static final String TAG = PosterEditor.class.getSimpleName();

    public final static int MAX_A4_QUANTITY = 5;
    public final static int MAX_A3_QUANTITY = 3;
    public final static int MAX_A2_QUANTITY = 1;
    public PosterEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @Override
    public void initControlVisibleStateOnActivityCreate() {
        super.initControlVisibleStateOnActivityCreate();
        if(Const_PRODUCT.POSTER_A2_VERTICAL.equals(Config.getPROD_CODE())|| Const_PRODUCT.POSTER_A2_HORIZONTAL.equals(Config.getPROD_CODE())) {
            RelativeLayout addPageLy = getEditControls().getAddPageLy();
            if (addPageLy != null)
                addPageLy.setVisibility(View.GONE);
        }
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_POSTER;
    }

    @Override
    public void setActivityContentView() {
        getActivity().setContentView(R.layout.activity_edit_new_years_card);
    }

    @Override
    public int setMaxQuantity() {
        return getMaxQuantity();
    }

    private int getMaxQuantity() {
        switch (Config.getPROD_CODE()) {
            case POSTER_A4_VERTICAL:
            case POSTER_A4_HORIZONTAL:
                return MAX_A4_QUANTITY;
            case POSTER_A3_VERTICAL:
            case POSTER_A3_HORIZONTAL:
                return MAX_A3_QUANTITY;
            case POSTER_A2_VERTICAL:
            case POSTER_A2_HORIZONTAL:
                return MAX_A2_QUANTITY;

            default:
                return 0;

        }
    }

    @Override
    public void appendAddPageOnLoadedTemplate(SnapsTemplate template) {
        DataTransManager dataTransManager = DataTransManager.getInstance();
        int count =dataTransManager.getPhotoImageDataList().size();
        try {
            int currentPageCount = template.getPages().size();
            int maxImageCount = (template.getPages().get(0).getLayerLayouts().size());
            int addCount = (int) Math.ceil((double)count / maxImageCount);


            // 페이지 추가
            for(int i=0; i<addCount -1  ; i++) {
                int lastPageIndex = currentPageCount-1;
                int addPageIndex = currentPageCount;


                SnapsPage copiedLastPage = template.getPages().get(lastPageIndex).copyPage(addPageIndex);
                template.getPages().add(copiedLastPage);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public String setTitle() {
        return getString(R.string.snaps_poster);
    }

    @Override
    public boolean addTemplatePage(SnapsTemplate snapsTemplate) {
        return false;
    }

    @Override
    public String getDeletePageMessage() {
        return getString(R.string.snaps_poster_delete);
    }

    @Override
    public void changeTemplatePage(SnapsTemplate snapsTemplate) {

    }
}

