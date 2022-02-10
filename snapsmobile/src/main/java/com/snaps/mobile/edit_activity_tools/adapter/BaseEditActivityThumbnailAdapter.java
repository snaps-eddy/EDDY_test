package com.snaps.mobile.edit_activity_tools.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.snaps.common.structure.page.SnapsPage;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.edit_activity_tools.interfaces.IEditThumbnailItemTouchHelperAdapter;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 7. 17..
 */

public abstract class BaseEditActivityThumbnailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IEditThumbnailItemTouchHelperAdapter {
    public abstract void setSnapsProductEditorAPI(SnapsProductEditorAPI productEditorAPI);

    public abstract void setIsLandscapeMode(boolean isLandscape);

    public abstract void setData(ArrayList<SnapsPage> pageList);

    public abstract void refreshThumbnailsLineAndText(int position);

    public abstract void releaseInstance();
}