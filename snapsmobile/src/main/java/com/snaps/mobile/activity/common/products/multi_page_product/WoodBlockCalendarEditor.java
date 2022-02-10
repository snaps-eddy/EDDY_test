package com.snaps.mobile.activity.common.products.multi_page_product;

import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.autosave.IAutoSaveConstants;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class WoodBlockCalendarEditor extends CalendarEditor {
    private static final String TAG = WoodBlockCalendarEditor.class.getSimpleName();

    public WoodBlockCalendarEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void setPreviewBtnVisibleState() {
        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        if (previewBtn != null) {
            previewBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_WOOD_BLOCK_CALENDAR;
    }

    @Override
    public void appendAddPageOnLoadedTemplate(SnapsTemplate template) {
        if(!AutoSaveManager.isAutoSaveRecoveryMode()) {
            //hidden은 나오면 안되는데, 나오고 있다..
            if(template != null && template.getPages() != null) {
                if (template.isContainHiddenPageOnPageList()) {
                    template._hiddenPageList = new ArrayList<SnapsPage>();

                    for(int ii = 1; ii >= 0; ii--) {
                        SnapsPage page = template.getPages().get(ii);
                        if(page != null && page.type != null && page.type.equalsIgnoreCase("hidden")) {
                            template._hiddenPageList.add(0, page);
                            template.getPages().remove(page);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void swapAllTextControl(SnapsTemplate snapsTemplate) throws Exception {
        removeHiddenPages(snapsTemplate);
        super.swapAllTextControl(snapsTemplate);
    }

    private void removeHiddenPages(SnapsTemplate template) {
        if(template != null && template.getPages() != null) {
            if (template.isContainHiddenPageOnPageList()) {
                for(int ii = 1; ii >= 0; ii--) {
                    SnapsPage page = template.getPages().get(ii);
                    if(page != null && page.type != null && page.type.equalsIgnoreCase("hidden")) {
                        template.getPages().remove(page);
                    }
                }
            }

            try {
                PhotobookCommonUtils.refreshPagesId(template.getPages());
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }
}
