package com.snaps.mobile.product_native_ui.ui;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.snaps.common.utils.develop.SnapsDevelopHelper;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.DetailProductNativeActivity;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.SubCategory;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsProductListParams;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler;
import com.snaps.mobile.component.FrameLayoutForScrollObserve;
import com.snaps.mobile.component.ObserveScrollingRecyclerView;
import com.snaps.mobile.component.SnapsNativeListViewProcess;
import com.snaps.mobile.interfaces.OnPageScrollListener;
import com.snaps.mobile.product_native_ui.interfaces.IOnSnapsProductListItemSelectedListener;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsProductListOpserver;
import com.snaps.mobile.product_native_ui.ui.adapter.SnapsProductListBaseAdapter;
import com.snaps.mobile.product_native_ui.ui.adapter.SnapsProductListGridShapeAdapter;
import com.snaps.mobile.product_native_ui.ui.adapter.SnapsProductListGridSpacingItemDecoration;
import com.snaps.mobile.product_native_ui.ui.adapter.SnapsProductListVerticalListShapeAdapter;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsBaseProductListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductGridShapeListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductPriceListItem;
import com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager;
import com.snaps.mobile.product_native_ui.util.SnapsProductNativeUIUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SnapsProductListView extends FrameLayoutForScrollObserve implements IOnSnapsProductListItemSelectedListener, ISnapsProductListOpserver {
	private static final String TAG = SnapsProductListView.class.getSimpleName();

	private SnapsProductListGridShapeAdapter gridShapeAdapter;

	private GridLayoutManager gridLayoutManager;

	private RecyclerView.ItemDecoration gridSpacingDecoretor;

	public SnapsProductListView(Context context) {
		super(context);
	}

	public SnapsProductListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SnapsProductListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onRequestedProductListSort(SnapsNativeUIManager.PRODUCT_LIST_SORT_TYPE sortType) {
		if (gridShapeAdapter != null)
			gridShapeAdapter.sortList(sortType);
	}

	@Override
	public void onRequestedGridViewModeChange(boolean isLargeView) {
		refreshGridState(isLargeView, gridShapeAdapter != null ? gridShapeAdapter.getData() : null);
	}

	@Override
	public void onProductListItemSelected(int position, final SnapsBaseProductListItem item) {
		if (item == null) return;
		if (SnapsDevelopHelper.isCMDCatchMode()) { //for develop
			MessageUtil.alertForCMDShow((Activity) context, getCMDUrl(item), new ICustomDialogListener() {
				@Override
				public void onClick(byte clickedOk) {
					onClickItem(item);
				}
			});
			return;
		}

		onClickItem(item);
	}

	private String getCMDUrl(SnapsBaseProductListItem item) {
		SnapsNativeUIManager menuDataManager = SnapsNativeUIManager.getInstance();
		SnapsProductListParams listParams = menuDataManager.getCurrentProductListParams();
		String tmplCode = "", tmplName = "", prodCode = "";
		int outerYN = 0;
		if (item instanceof SnapsProductGridShapeListItem) {
			tmplCode = ((SnapsProductGridShapeListItem)item).getTmplCode();
			try {
				tmplName = URLEncoder.encode(((SnapsProductGridShapeListItem)item).getContents(), "utf-8");
				tmplName = tmplName.replace("+", "%20");
			} catch (UnsupportedEncodingException e) {
				Dlog.e(TAG, e);
			}
			prodCode = ((SnapsProductGridShapeListItem)item).getProdCode();
			outerYN = ((SnapsProductGridShapeListItem)item).getIsOuterYN() != null && ((SnapsProductGridShapeListItem)item).getIsOuterYN().equalsIgnoreCase("Y") ? 1:0;
		}

		return String.format("snapsapp://detail?classCode=%s&tmplCode=%s&F_TMPL_NAME=%s&F_PROD_CODE=%s&F_OUTER_YORN=%d", listParams.getClssCode(), tmplCode, tmplName, prodCode, outerYN);
	}

	private void onClickItem(SnapsBaseProductListItem item) {
		if (item instanceof SnapsProductPriceListItem) {
			String cmd = ((SnapsProductPriceListItem) item).getCmd();
			cmd += SnapsWebEventBaseHandler.PRODUCT_SUB_LIST_PARAM;

			SnapsNativeUIManager menuDataManager = SnapsNativeUIManager.getInstance();
			if (menuDataManager != null) {
				SnapsNativeListViewProcess nativeListViewProcess = menuDataManager.getNativeListViewProcess((Activity)context);
				if (nativeListViewProcess != null) {
					nativeListViewProcess.shouldOverrideUrlLoading(null, cmd);
				}
			}
		} else if (item instanceof SnapsProductGridShapeListItem) {
			SnapsProductListParams params = new SnapsProductListParams();

			SnapsProductGridShapeListItem gridShapeListItem = (SnapsProductGridShapeListItem) item;

			params.setMclssCode(gridShapeListItem.getmClssCode());
			params.setTemplateCode(gridShapeListItem.getTmplCode());
			params.setOuter(gridShapeListItem.isPremium());
			params.setProdCode( gridShapeListItem.getProdCode() );

			SnapsNativeUIManager menuDataManager = SnapsNativeUIManager.getInstance();
			if (menuDataManager != null) {
				SnapsProductListParams listParams = menuDataManager.getCurrentProductListParams();
				if (listParams != null) {
					StringBuilder url = new StringBuilder();
					params.setClssCode(listParams.getClssCode());
					SubCategory subCategory = MenuDataManager.getInstance().getSubCategoryByF_CLSS_CODE( listParams.getClssCode() );
					if( subCategory == null )
						subCategory = SnapsMenuManager.getInstance().getSubCategory();
					if (subCategory != null) {
						params.setTitle( subCategory.getTitle() );
						params.setInfoUrl( subCategory.getInfoUrl() );
					}

					if( !StringUtil.isEmpty(gridShapeListItem.getTmplName()) )
						params.setTitle( gridShapeListItem.getTmplName() );

					params.setDetailInterfaceUrl( SnapsProductNativeUIUtil.getProductDetailPageUrl(params) );
					context.startActivity(DetailProductNativeActivity.getIntent(getContext(), params));
				}
			}
		}
	}

	private void refreshGridState(final boolean isGridLargeView, ArrayList<SnapsBaseProductListItem> data) {
		int spanCount = isGridLargeView ? 1 : SnapsProductListBaseAdapter.GRID_COLUMN_COUNT;

		if (gridLayoutManager == null)
			gridLayoutManager = new GridLayoutManager(context, spanCount);
		else
			gridLayoutManager.setSpanCount(spanCount);

		if (gridSpacingDecoretor != null)
			recyclerView.removeItemDecoration(gridSpacingDecoretor);

		if (!isGridLargeView) {
			gridSpacingDecoretor = new SnapsProductListGridSpacingItemDecoration(context); //recycler grid는 세로 spacing을 이렇게 주어야 한다..
			recyclerView.addItemDecoration(gridSpacingDecoretor);
		}

		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				if (position == 0) //sorting 영역
					return isGridLargeView ? 1 : SnapsProductListBaseAdapter.GRID_COLUMN_COUNT;
				return 1;
			}
		});

		recyclerView.setLayoutManager(gridLayoutManager);

		gridShapeAdapter = new SnapsProductListGridShapeAdapter(context, this, isGridLargeView);
		gridShapeAdapter.setData(data);
		recyclerView.setAdapter(gridShapeAdapter);
	}
	private void drawItems(boolean isSizeType, ArrayList<SnapsBaseProductListItem> productListItems) {
		drawItems(isSizeType,productListItems,"");
	}
	private void drawItems(boolean isSizeType, ArrayList<SnapsBaseProductListItem> productListItems, String viewType) {
		if (productListItems == null ||  recyclerView == null) return;

		if (isSizeType) {
			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
			SnapsProductListVerticalListShapeAdapter listShapeAdapter = new SnapsProductListVerticalListShapeAdapter(context, this);
			recyclerView.setLayoutManager(linearLayoutManager);
			listShapeAdapter.setData(productListItems);
			recyclerView.setAdapter(listShapeAdapter);
		} else {
			boolean isLargeView = false;
			if(TextUtils.isEmpty(viewType)) {
				isLargeView = SnapsNativeUIManager.DEFAULT_LIST_UI_LARGE_VIEW;
				SnapsNativeUIManager nativeUIManager = SnapsNativeUIManager.getInstance();
				if (nativeUIManager != null) {
					nativeUIManager.registeListSortOpserver(this);
					isLargeView = nativeUIManager.isGridListLargeView();
				}
			}else {
				if(viewType.equals("1")) {
					isLargeView = true;
				} else {
					isLargeView = false;
				}
				SnapsNativeUIManager nativeUIManager = SnapsNativeUIManager.getInstance();
				if (nativeUIManager != null) {
					nativeUIManager.registeListSortOpserver(this);
				}
			}

			refreshGridState(isLargeView, productListItems);
		}

		recyclerView.setRefreshing(false);
	}
	public void initUI( boolean isSizeSelecte, boolean isSingleTab, ArrayList<SnapsBaseProductListItem> productListItems ) {
		initUI(isSizeSelecte,isSingleTab,productListItems,"");
	}
	public void initUI( boolean isSizeSelecte, boolean isSingleTab, ArrayList<SnapsBaseProductListItem> productListItems,String viewType ) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View v = inflater.inflate(R.layout.custom_snaps_native_recycler_view, null);

		recyclerView = (ObserveScrollingRecyclerView) v.findViewById(R.id.custom_snaps_native_super_recycler_view);
		RecyclerView rv = recyclerView.getRecyclerView();
		if (rv != null) {
			rv.setVerticalScrollBarEnabled(false);
		}

        recyclerView.getRecyclerView().setPadding( recyclerView.getRecyclerView().getPaddingLeft(), getDefaultTopMargin(isSingleTab), recyclerView.getRecyclerView().getPaddingRight(), recyclerView.getRecyclerView().getPaddingBottom() );

        recyclerView.setOnScrollListener( new OnPageScrollListener() {
            @Override
            public boolean onScrollChanged(int l, int t, int oldl, int oldt) {
                return false;
            }

            @Override
            public boolean onScrollChanged(int dx, int dy) {
                if( pageScrollListener != null ) pageScrollListener.onScrollChanged( dx, dy );
                return false;
            }
        } );

        recyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (onStickyScrollTouchListener != null)
                    onStickyScrollTouchListener.onStickyScrollTouch(event, recyclerView);
                return false;
            }
        });


        if( getChildCount() > 0 )
            removeAllViews();

		drawItems(isSizeSelecte, productListItems,viewType);
				
		addView(v);

		setInitialized( true );
	}
}
