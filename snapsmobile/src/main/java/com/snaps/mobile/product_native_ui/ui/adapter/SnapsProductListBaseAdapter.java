package com.snaps.mobile.product_native_ui.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryRecyclerCustomAdapter;
import com.snaps.mobile.product_native_ui.interfaces.IOnSnapsProductListItemSelectedListener;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsBaseProductListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ysjeong on 16. 3. 9..
 */
public abstract class SnapsProductListBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ISnapsDiaryRecyclerCustomAdapter {
    private static final String TAG = SnapsProductListBaseAdapter.class.getSimpleName();
    public static final int GRID_COLUMN_COUNT = 2;
    public static final int SHAPE_LIST = 0;
    public static final int SHAPE_GRID = 1;

    protected ArrayList<SnapsBaseProductListItem> data;
    protected Context context;

    protected IOnSnapsProductListItemSelectedListener clickListener = null;

    private int shape = 0;

    protected abstract void clearImageResourceOnViewRecycled(RecyclerView.ViewHolder holder) throws Exception;

    public SnapsProductListBaseAdapter(Context context, IOnSnapsProductListItemSelectedListener listener) {
        this.context = context;
        this.clickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {}

    protected SnapsBaseProductListItem getItem(int pos) {
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

    public void setShape(int shape) {
        this.shape = shape;
    }
    public int getShape() {
        return this.shape;
    }

    public void add(SnapsBaseProductListItem contents) {
        insert(contents, data.size());
    }

    public void insert(SnapsBaseProductListItem contents, int position) {
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

    public void addAll(SnapsBaseProductListItem[] contentses) {
        int startIndex = data.size();
        data.addAll(startIndex, Arrays.asList(contentses));
        notifyItemRangeInserted(startIndex, contentses.length);
    }

    public void addAll(List<SnapsBaseProductListItem> contentses) {
        int startIndex = data.size();
        data.addAll(startIndex, contentses);
        notifyItemRangeInserted(startIndex, contentses.size());
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        try {
            clearImageResourceOnViewRecycled(holder);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
