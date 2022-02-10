package com.snaps.mobile.activity.diary.customview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

/**
 * Created by ysjeong on 16. 4. 1..
 */
public class SnapsSuperRecyclerView extends SuperRecyclerView {

    private Context context;

    private boolean isAddedScrollListener = false;

    private boolean isAddedViewTreeObserver = false;

    private RecyclerView.ItemDecoration itemDecoration = null;

    private RecyclerView.LayoutManager layoutManager = null;

    public SnapsSuperRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public SnapsSuperRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnapsSuperRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        hideProgress(); //FIXME 기본적으로 프로그래스 감추어 놓음.

        RecyclerView recylerView = getRecyclerView(); //스크롤바도 넣지 않는다.(우측에 마진이 생긴다.)
        recylerView.setVerticalScrollBarEnabled(false);

        setRefreshing(false);
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        return itemDecoration;
    }

    public void setItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        if (getItemDecoration() != null)
            this.removeItemDecoration(getItemDecoration() );

        this.itemDecoration = itemDecoration;

        this.addItemDecoration(itemDecoration);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        super.setLayoutManager(layoutManager);
        this.layoutManager = layoutManager;
    }

    public boolean isExistLayoutManager() {
        return layoutManager != null;
    }

    public boolean isAddedScrollListener() {
        return isAddedScrollListener;
    }

    public void setIsAddedScrollListener(boolean isAddedScrollListener) {
        this.isAddedScrollListener = isAddedScrollListener;
    }

    public boolean isAddedViewTreeObserver() {
        return isAddedViewTreeObserver;
    }

    public void setIsAddedViewTreeObserver(boolean isAddedViewTreeObserver) {
        this.isAddedViewTreeObserver = isAddedViewTreeObserver;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView != null) return recyclerView.getLayoutManager();
        return null;
    }

    public int getComputeVerticalScrollOffset() {
        RecyclerView recyclerView = getRecyclerView();
        return recyclerView != null ? recyclerView.computeVerticalScrollOffset() : 0;
    }
}
