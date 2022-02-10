package com.snaps.mobile.activity.diary.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.swipe.SparseItemRemoveAnimator;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.SnapsDiaryInterfaceUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryListProcessor;
import com.snaps.mobile.activity.diary.adapter.SnapsDiaryBaseAdapter;
import com.snaps.mobile.activity.diary.adapter.SnapsDiaryGridShapeAdapter;
import com.snaps.mobile.activity.diary.adapter.SnapsDiaryGridSpacingItemDecoration;
import com.snaps.mobile.activity.diary.adapter.SnapsDiaryListShapeAdapter;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryRecyclerView;
import com.snaps.mobile.activity.diary.interfaces.IOnSnapsDiaryItemSelectedListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderClickListener;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListItemJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListJson;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryPageInfo;
import com.snaps.mobile.component.CustomSwipeRefreshLayout;

import java.util.List;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

/**
 * Created by ysjeong on 16. 4. 1..
 */
public class SnapsDiaryListFragment extends Fragment implements CustomSwipeRefreshLayout.OnRefreshListener, OnMoreListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks {

    private final int ITEM_LEFT_TO_LOAD_MORE = 3;

    private int m_iCurrentShape = SnapsDiaryBaseAdapter.SHAPE_LIST;

    private SnapsDiaryRecyclerView mRecycler;
    private SnapsDiaryListShapeAdapter mListShapeAdapter;
    private SnapsDiaryGridShapeAdapter mGridShapeAdapter;
    private SparseItemRemoveAnimator mSparseAnimator;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private SnapsDiaryGridSpacingItemDecoration mGridSpacingDecoretor = null;

    private IOnSnapsDiaryItemSelectedListener onDiaryItemSelectedListener = null;
    private ISnapsDiaryHeaderClickListener onDiaryStripListener = null;

    public static SnapsDiaryListFragment newInstance(SnapsDiaryListProcessor listProcessor) {
        SnapsDiaryListFragment f = new SnapsDiaryListFragment();
        f.setOnDiaryItemSelectedListener(listProcessor);
        f.setOnDiaryStripListener(listProcessor);
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }

    public void setOnDiaryItemSelectedListener(IOnSnapsDiaryItemSelectedListener listener) {
        onDiaryItemSelectedListener = listener;
    }

    public void setOnDiaryStripListener(ISnapsDiaryHeaderClickListener listener) {
        onDiaryStripListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View infalter = inflater.inflate(R.layout.snaps_diary_list, container, false);

        mRecycler = (SnapsDiaryRecyclerView) infalter.findViewById(R.id.snaps_diary_list_recycler_view);

        mGridSpacingDecoretor = new SnapsDiaryGridSpacingItemDecoration(UIUtil.convertDPtoPX(getActivity(), 1)); //recycler grid는 세로 spacing을 이렇게 주어야 한다..

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mGridLayoutManager = new GridLayoutManager(getActivity(), SnapsDiaryBaseAdapter.GRID_COLUMN_COUNT);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0)
                    return 3;
                return 1;
            }
        });

        mRecycler.setLayoutManager(getLayoutManager());

        boolean dismissEnabled = isSwipeToDismissEnabled();
        if (dismissEnabled) {
            mRecycler.setupSwipeToDismiss(this);
            mSparseAnimator = new SparseItemRemoveAnimator();
            mRecycler.getRecyclerView().setItemAnimator(mSparseAnimator);
        }

        mListShapeAdapter = new SnapsDiaryListShapeAdapter(getActivity(), onDiaryItemSelectedListener, onDiaryStripListener);
        mGridShapeAdapter = new SnapsDiaryGridShapeAdapter(getActivity(), onDiaryItemSelectedListener, onDiaryStripListener);

        mRecycler.setAdapter(mListShapeAdapter);
        mRecycler.setRefreshing(false);

        mListShapeAdapter.refreshData();
        mGridShapeAdapter.refreshData();

        return infalter;
    }

    public void destroyView() {
        if(mListShapeAdapter != null)
            mListShapeAdapter.destroyView();
        if(mGridShapeAdapter != null)
            mGridShapeAdapter.destroyView();
    }

    public void changeShape(int shape) {
        m_iCurrentShape = shape;
        final int PREV_OFFSET_Y = mRecycler.getComputeVerticalScrollOffset();
        if (PREV_OFFSET_Y > 50) {
            mRecycler.setVisibility(View.INVISIBLE);
        }

        refreshAdapter();

        mRecycler.post(new Runnable() {
            @Override
            public void run() {
                mRecycler.scrollByY(PREV_OFFSET_Y);
            }
        });
    }

    public void refreshAdapter() {
        if (mRecycler == null) return;
        if (m_iCurrentShape == SnapsDiaryBaseAdapter.SHAPE_GRID) {
            mGridShapeAdapter.checkMinGripCount();
            mRecycler.removeItemDecoration(mGridSpacingDecoretor);
            mRecycler.addItemDecoration(mGridSpacingDecoretor);
            mRecycler.setLayoutManager(getLayoutManager());
            mRecycler.setAdapter(mGridShapeAdapter);
        } else {
            mRecycler.removeItemDecoration(mGridSpacingDecoretor);
            mRecycler.setLayoutManager(getLayoutManager());
            mRecycler.setAdapter(mListShapeAdapter);
        }
    }

    public void scrollToPosition(int position) {
        getLayoutManager().scrollToPosition(position);
    }

    public void setRecyclerViewMoreListener() {
        if(mRecycler == null) return;
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryListInfo listInfo = dataManager.getListInfo();
        if(listInfo.isMoreNextPage()) {
            mRecycler.setupMoreListener(this, ITEM_LEFT_TO_LOAD_MORE);
        } else {
            mRecycler.removeMoreListener();
            mRecycler.hideMoreProgress();
        }
    }

    private boolean isSwipeToDismissEnabled() {
        return false;
    }

    private RecyclerView.LayoutManager getLayoutManager() {
        if(m_iCurrentShape == SnapsDiaryBaseAdapter.SHAPE_LIST)
            return mLinearLayoutManager;
        else
            return mGridLayoutManager;
    }

    @Override
    public void onRefresh() {}

    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {

        final SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();

        final SnapsDiaryListInfo listInfo = dataManager.getListInfo();
        if(listInfo.isMoreNextPage()) {
            SnapsDiaryPageInfo pageInfo = new SnapsDiaryPageInfo();
            pageInfo.setPagingNo(listInfo.getCurrentPageNo() + 1);
            pageInfo.setPagingSize(listInfo.getPageSize());
            SnapsDiaryInterfaceUtil.getDiaryList(getActivity(), pageInfo, new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
                @Override
                public void onPreperation() {
                }

                @Override
                public void onResult(boolean result, Object resultObj) {
                    if (result) {
                        if (resultObj == null) {
                            setRecyclerViewMoreListener();
                            return;
                        }
                        SnapsDiaryListJson listResult = (SnapsDiaryListJson) resultObj;

                        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                        SnapsDiaryListInfo listInfo = dataManager.getListInfo();

                        listInfo.setCurrentPageNo(listResult.getPageNo());
                        listInfo.setTotalCount(listResult.getTotalCount());
                        listInfo.setPageSize(listResult.getPageSize());
                        listInfo.setIosCount(listResult.getIosCount());
                        listInfo.setAndroidCount(listResult.getAndroidCount());

                        SnapsDiaryListInfo addedList = new SnapsDiaryListInfo();

                        List<SnapsDiaryListItemJson> list = listResult.getDiaryList();
                        if (list != null && !list.isEmpty()) {
                            listInfo.addDiaryList(list);
                            addedList.addDiaryList(list);
                        }

                        mListShapeAdapter.addAll(addedList.getArrDiaryList());
                        mGridShapeAdapter.addAll(addedList.getArrDiaryList());

                        setRecyclerViewMoreListener();
                    } else {
                        MessageUtil.toast(getActivity(), getString(R.string.diary_failed_get_more_list));
                        setRecyclerViewMoreListener();
                    }
                }
            });
        } else {
            setRecyclerViewMoreListener();
        }
    }

    @Override
    public boolean canDismiss(int position) {
        return true;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mSparseAnimator.setSkipNext(true);
            mListShapeAdapter.remove(position);
            mGridShapeAdapter.remove(position);
        }
    }

    public SnapsDiaryListShapeAdapter getListShapeAdapter() {
        return mListShapeAdapter;
    }

    public SnapsDiaryGridShapeAdapter getGridShapeAdapter() {
        return mGridShapeAdapter;
    }
}