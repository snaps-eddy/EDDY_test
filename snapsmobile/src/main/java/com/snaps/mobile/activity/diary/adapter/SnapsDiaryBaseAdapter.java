package com.snaps.mobile.activity.diary.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.interfaces.IOnSnapsDiaryItemSelectedListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderClickListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderStrategy;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryRecyclerCustomAdapter;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ysjeong on 16. 3. 9..
 */
public abstract class SnapsDiaryBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ISnapsDiaryRecyclerCustomAdapter {
    private static final String TAG = SnapsDiaryBaseAdapter.class.getSimpleName();
    public static final int GRID_COLUMN_COUNT = 3;
    public static final int SHAPE_LIST = 0;
    public static final int SHAPE_GRID = 1;

    protected ArrayList<SnapsDiaryListItem> data;
    protected IOnSnapsDiaryItemSelectedListener onDiaryItemSelectedListener;
    protected Context context;

    private ISnapsDiaryHeaderStrategy headerStrategy = null;
    private ISnapsDiaryHeaderClickListener stripListener = null;

    private int shape = 0;

    protected abstract void clearImageResource(RecyclerView.ViewHolder holder) throws Exception;

    public SnapsDiaryBaseAdapter(Context context, IOnSnapsDiaryItemSelectedListener listener, ISnapsDiaryHeaderClickListener stripListener) {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryListInfo listInfo = dataManager.getListInfo();
        if(listInfo == null || listInfo.getArrDiaryList() == null) return;

        this.data = (ArrayList<SnapsDiaryListItem>)listInfo.getArrDiaryList().clone();
        this.context = context;
        this.onDiaryItemSelectedListener = listener;
        this.stripListener = stripListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return getHeaderViewHolder(parent);
        } else
            return getItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(position == 0) {
            if(headerStrategy != null)
                headerStrategy.setHeaderInfo(holder);
        }
    }

    protected SnapsDiaryListItem getItem(int pos) {
        if(data == null || data.size() <= pos) return null;
        return data.get(pos);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData() {
        clear();
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryListInfo listInfo = dataManager.getListInfo();
        if(listInfo == null || listInfo.getArrDiaryList() == null) return;

        this.data = (ArrayList<SnapsDiaryListItem>)listInfo.getArrDiaryList().clone();
        if(!isExistHeader())
            data.add(0, new SnapsDiaryListItem(SnapsDiaryListItem.ITEM_TYPE_HEADER));
        notifyDataSetChanged();
    }

    public void refreshHeader() {
        if(!isExistHeader()) return;

        createHeader();

        notifyItemChanged(0);
    }

    public void refreshThumbnail() {
        if(!isExistHeader() || headerStrategy == null) return;
        headerStrategy.refreshThumbnail();
    }

    public void destroyView() {
        if(headerStrategy != null)
            headerStrategy.destoryView();
    }

    public void setShape(int shape) {
        this.shape = shape;
    }
    public int getShape() {
        return this.shape;
    }

    public void add(SnapsDiaryListItem contents) {
        insert(contents, data.size());
    }

    protected boolean isExistHeader() {
        if(data == null || data.size() < 1) return false;

        SnapsDiaryListItem listItem = data.get(0);
        if (listItem != null && listItem.isHeader()) {
            return true;
        }
        return false;
    }

    public void insert(SnapsDiaryListItem contents, int position) {
        data.add(position, contents);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(SnapsDiaryListItem[] contentses) {
        int startIndex = data.size();
        data.addAll(startIndex, Arrays.asList(contentses));
        notifyItemRangeInserted(startIndex, contentses.length);
    }

    public void addAll(List<SnapsDiaryListItem> contentses) {
        int startIndex = data.size();
        data.addAll(startIndex, contentses);
        notifyItemRangeInserted(startIndex, contentses.size());
    }

    private void createHeader() {
        if(headerStrategy != null)
            headerStrategy.destoryView();
        headerStrategy = SnapsDiaryHeaderFactory.createHeader(context, shape, stripListener);
    }

    private RecyclerView.ViewHolder getHeaderViewHolder(ViewGroup parent) {
        createHeader();
        return headerStrategy.getViewHolder(parent);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        try {
            clearImageResource(holder);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
