package com.snaps.mobile.activity.themebook.adapter;

import android.graphics.Color;
import android.graphics.Rect;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.data.img.BRect;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.edit.spc.SmartRecommendBookEditListItemCanvas;
import com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.holder.SmartRecommendBookEditListCoverHolder;
import com.snaps.mobile.activity.themebook.holder.SmartRecommendBookEditListPageHolder;
import com.snaps.mobile.activity.themebook.holder.SmartRecommendBookListDummyHolder;
import com.snaps.mobile.activity.themebook.holder.SnapsCanvasContainerLayout;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SmartRecommendBookCoverInfo;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.interfacies.ISmartRecommendBookCoverChangeListener;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;
import font.FTextView;

import static com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory.eSnapsCanvasType.SMART_SNAPS_ANALYSIS_PRODUCT_EDIT_LIST_ITEM_CANVAS;

/** 메인화면 전체 리스트(커버 부분+속지+내지)의 페이저 처리를 위한 Adapter **/
public class SmartRecommendBookMainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final String TAG = SmartRecommendBookMainListAdapter.class.getSimpleName();

	private ArrayList<SnapsPage> snapsPageList;

	private SnapsCommonResultListener<SnapsPageEditRequestInfo> itemClickListener = null;
	private SnapsCommonResultListener<SnapsPageEditRequestInfo> itemLongClickListener = null;
	private FragmentActivity activity = null;

	private Map<SnapsPageCanvas, SnapsCanvasContainerLayout> canvasSet = new HashMap();

	private SmartRecommendBookEditCoverPagerAdapter coverPagerAdapter = null;
	private List<ImageView> mIndicators = null;
	private int lastSelectedCoverItemPosition = 0;

	private ISmartRecommendBookCoverChangeListener coverChangeListener = null;

	private SmartRecommendBookCoverInfo coverInfo;

	public SmartRecommendBookMainListAdapter(FragmentActivity activity) {
		this.activity = activity;
	}

	public void setItemClickListener(SnapsCommonResultListener<SnapsPageEditRequestInfo> itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public void setItemLongClickListener(SnapsCommonResultListener<SnapsPageEditRequestInfo> itemLongClickListener) {
		this.itemLongClickListener = itemLongClickListener;
	}

	public void setCoverChangeListener(ISmartRecommendBookCoverChangeListener coverChangeListener) {
		this.coverChangeListener = coverChangeListener;
	}

	public void setListWithTemplate(SnapsTemplate template) {
		if (template == null) return;

		initPageListWithTemplate(template);

		notifyDataSetChanged();
	}

	public void forceListItemClick(final int position) {
		if (position == 0) {
			if (itemClickListener != null) {
				itemClickListener.onResult(createPageEditRequestInfo());
			}
		} else {
			if (itemClickListener != null) {
				itemClickListener.onResult(new SnapsPageEditRequestInfo.Builder().setPageIndex(position).create());
			}
		}
	}

	public int getLastSelectedCoverItemPosition() {
		return lastSelectedCoverItemPosition;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == SmartSnapsConstants.eSmartSnapsAnalysisProductEditListHolderType.HOLDER_TYPE_COVER.ordinal()) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.smart_snaps_analysis_product_edit_list_cover_item, parent, false);
			return new SmartRecommendBookEditListCoverHolder(view);
		} else if (viewType == SmartSnapsConstants.eSmartSnapsAnalysisProductEditListHolderType.HOLDER_TYPE_DUMMY.ordinal()) {
			return createDummyViewHolder();
		}

		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.smart_snaps_analysis_product_edit_list_page_item, parent, false);
		return new SmartRecommendBookEditListPageHolder(view);
	}

	private RecyclerView.ViewHolder createDummyViewHolder() {
		View view = new View(activity);
		view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtil.convertDPtoPX(activity, 100)));
		view.setBackgroundColor(Color.parseColor("#f4f4f4"));
		return new SmartRecommendBookListDummyHolder(view);
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) return SmartSnapsConstants.eSmartSnapsAnalysisProductEditListHolderType.HOLDER_TYPE_COVER.ordinal();
		if (position == snapsPageList.size()) return SmartSnapsConstants.eSmartSnapsAnalysisProductEditListHolderType.HOLDER_TYPE_DUMMY.ordinal();
		return -1;
	}

	public SnapsPageCanvas findSnapsPageCanvasWithTouchOffsetRect(BRect touchRect) {
		if (touchRect == null) return null;
		SnapsPageCanvas foundCanvas = null;
		try {
			foundCanvas = findSnapsPageCanvasOnInnerPageWithTouchOffsetRect(touchRect);
			if (foundCanvas == null && coverPagerAdapter != null)
				foundCanvas = coverPagerAdapter.findSnapsPageCanvasOnCoverWithTouchOffsetRect(touchRect);
		} catch (Exception e) { Dlog.e(TAG, e); }
		return foundCanvas;
	}

	private SnapsPageCanvas findSnapsPageCanvasOnInnerPageWithTouchOffsetRect(BRect touchRect) {
		if (canvasSet == null) return null;
		Set<SnapsPageCanvas> innerPageCanvasSet = canvasSet.keySet();
		for (SnapsPageCanvas snapsPageCanvas : innerPageCanvasSet) {
			if (snapsPageCanvas == null || snapsPageCanvas.isSuspendedLayerLoad()) continue;

			Rect rect = new Rect();
			snapsPageCanvas.getGlobalVisibleRect(rect);

			if (rect.top > getMinTouchRectTopOffsetY() && rect.contains(touchRect.centerX(), touchRect.centerY())) {
				Dlog.d("findSnapsPageCanvasOnInnerPageWithTouchOffsetRect() find pinch target:" + snapsPageCanvas);
				return snapsPageCanvas;
			}
		}
		return null;
	}

	private int getMinTouchRectTopOffsetY() {
		return UIUtil.convertDPtoPX(activity, 48);
	}

	public ArrayList<SnapsPage> getPhotoItemList() {
		return snapsPageList;
	}

	public SnapsPage getItem(int position) {
		return getPhotoItemList().get(position);
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (holder == null || getItemCount() <= position) return;

		try {
			if (holder instanceof SmartRecommendBookEditListCoverHolder) {
				onBindCoverViewHolder((SmartRecommendBookEditListCoverHolder)holder);
			} else if (holder instanceof SmartRecommendBookEditListPageHolder) {
				onBindPageViewHolder((SmartRecommendBookEditListPageHolder)holder, position);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsAssert.assertException(activity, e);
		}
	}

	private void onBindCoverViewHolder(SmartRecommendBookEditListCoverHolder coverHolder) throws Exception {
		loadCoverViewPager(coverHolder);
	}

	private void onBindPageViewHolder(SmartRecommendBookEditListPageHolder pageHolder, int position) throws Exception {
		loadCanvas(position, pageHolder.getCanvasParentLayout());

		FTextView labelView = pageHolder.getLabel();
		if (labelView != null) labelView.setText(getLabelWithPosition(position));
	}

	private String getLabelWithPosition(int position) {
		if (activity == null) return "";
		switch (position) {
			case 0: return "";
			case 1: return activity.getString(R.string.inner_page);
			default:{
				int page = (position - 2) * 2 + 2;
				return String.format(activity.getString(R.string.format_with_page), page, page+1);
			}
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

	public void refreshCoverTitle() {
		if (coverInfo == null) return;
		try {
			TextView pageCountView = coverInfo.getPageCountView();
			pageCountView.setText(getTotalPageCountText());

			TextView photoCountView = coverInfo.getPhotoCountView();
			photoCountView.setText(getTotalPhotoCountText());
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsAssert.assertException(activity, e);
		}
	}

	private String getTotalPageCountText() {
		ArrayList<SnapsPage> pages = getPageList();
		int pageCount = pages != null ? pages.size() : 0; //더미가 하나 있으니...-1
		if (pageCount > 0)
			pageCount = (pageCount*2) - 2 - 1;
		return String.valueOf(pageCount);
	}

	private String getTotalPhotoCountText() {
		ArrayList<SnapsPage> pages = getPageList();
		int result = 0;
		if (pages != null) {
			for (SnapsPage snapsPage : pages) {
				if (snapsPage != null)  {
					result += snapsPage.getImageCountOnPage();
				}
			}
		}
		return String.valueOf(result);
	}

	private void loadCoverViewPager(SmartRecommendBookEditListCoverHolder coverHolder) throws Exception {
		if (coverHolder == null) return;

		ViewPager viewPager = coverHolder.getCoverViewPager();

		LinearLayout indicateLayout = coverHolder.getIndicatorLayout();

		coverInfo = new SmartRecommendBookCoverInfo.Builder()
				.setPageCountView(coverHolder.getPageCountView()).setPhotoCountView(coverHolder.getPhotoCountView()).create();

		refreshCoverTitle();

		coverPagerAdapter = new SmartRecommendBookEditCoverPagerAdapter(activity);
		coverPagerAdapter.setItemClickListener(itemClickListener);

		viewPager.setAdapter(coverPagerAdapter);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageScrollStateChanged(int state) {}

			@Override
			public void onPageSelected(int position) {
				lastSelectedCoverItemPosition = position;
				setIndicator(position);
				if (coverChangeListener != null && coverPagerAdapter != null) {
					coverChangeListener.onCoverChanged(coverPagerAdapter.getSnapsPage(position));
				}
			}
		});

		createIndicator(indicateLayout);
		viewPager.setCurrentItem(lastSelectedCoverItemPosition);
		setIndicator(lastSelectedCoverItemPosition);

		FTextView editBtn = coverHolder.getEditBtn();
		editBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_clickCover)
						.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, "0")
						.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

				if (itemClickListener != null) {
					itemClickListener.onResult(createPageEditRequestInfo());
				}
			}
		});
	}

	private SnapsPageEditRequestInfo createPageEditRequestInfo() {
		return new SnapsPageEditRequestInfo.Builder().setCover(true).setCoverTemplateIndex(lastSelectedCoverItemPosition).setPageIndex(0).create();
	}

	private void setIndicator(int position) {
		if (mIndicators == null || mIndicators.size() <= position) return;

		for (int ii = 0; ii < mIndicators.size(); ii++) {
			ImageView imageView = mIndicators.get(ii);
			if (imageView == null) return;

			if (imageView.getDrawable() != null) {
				imageView.getDrawable().setCallback(null);
				imageView.setImageBitmap(null);
			}

			imageView.setImageResource(position == ii ? R.drawable.img_diary_tutorial_dot_focus : R.drawable.img_diary_tutorial_dot);
		}
	}

	private void createIndicator(LinearLayout layout) {
		if (layout == null) return;

		layout.removeAllViews();

		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		ArrayList<SnapsPage> coverList = smartSnapsManager.getCoverPageListOfAnalysisPhotoBook();
		if (coverList == null) return;

		mIndicators = new ArrayList<>();
		final int indicatorDimens = UIUtil.convertDPtoPX(activity, 6);
		for (int ii = 0; ii < coverList.size(); ii++) {
			ImageView indicator = new ImageView(activity);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(indicatorDimens, indicatorDimens);
			indicator.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			layoutParams.leftMargin = ii > 0 ? UIUtil.convertDPtoPX(activity, 4) : 0;
			indicator.setLayoutParams(layoutParams);

			layout.addView(indicator);
			mIndicators.add(indicator);
		}
	}

	private void loadCanvas(final int position, SnapsCanvasContainerLayout canvasParentLayout) {
		SmartRecommendBookEditListItemCanvas canvas =
				(SmartRecommendBookEditListItemCanvas) SnapsCanvasFactory.createPageCanvasWithType(SMART_SNAPS_ANALYSIS_PRODUCT_EDIT_LIST_ITEM_CANVAS, activity);

		if (canvas != null) {
			canvas.setSnapsPageClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebLogConstants.eWebLogName logName = position == 1 ? WebLogConstants.eWebLogName.photobook_annie_complete_clickIndeximg : WebLogConstants.eWebLogName.photobook_annie_complete_clickPage;
					SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
							.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(position))
							.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

					if (itemClickListener != null) {
						itemClickListener.onResult(new SnapsPageEditRequestInfo.Builder().setPageIndex(position).create());
					}
				}
			});

			canvas.setSnapsPageLongClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_pressPage)
							.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(position))
							.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

					if (itemLongClickListener != null) {
						itemLongClickListener.onResult(new SnapsPageEditRequestInfo.Builder().setPageIndex(position).create());
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

			if( getPageList() != null &&  getPageList().size() > position ) {
				SnapsPage spcPage = getPageList().get(position);

				PhotobookCommonUtils.imageRange(spcPage);

				canvas.setSnapsPage(spcPage, position, true, null);
			}

			addCanvasOnContainer(canvasParentLayout, canvas);
		}
	}

	@Override
	public void onViewRecycled(RecyclerView.ViewHolder holder) {
		super.onViewRecycled(holder);

		if (holder == null) return;

		try {
			if (holder instanceof SmartRecommendBookEditListCoverHolder) {
				recycleCoverViewHolder((SmartRecommendBookEditListCoverHolder)holder);
			} else if (holder instanceof SmartRecommendBookEditListPageHolder) {
				recyclePagerViewHolder((SmartRecommendBookEditListPageHolder)holder);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsAssert.assertException(activity, e);
		}

//		LayoutDesignItemHolder photoHolder = (LayoutDesignItemHolder) holder;
//
//		if (photoHolder.getIvCoverThumbnail() != null) {
//			ImageLoader.clear(activity, photoHolder.getIvCoverThumbnail());
//		}
	}

	private void recycleCoverViewHolder(SmartRecommendBookEditListCoverHolder coverHolder) throws Exception {
		ViewPager viewPager = coverHolder.getCoverViewPager();
		if (viewPager != null) {
			final SmartRecommendBookEditCoverPagerAdapter pagerAdapter = (SmartRecommendBookEditCoverPagerAdapter) viewPager.getAdapter();
			if (pagerAdapter != null) {
				Map<SnapsPageCanvas, SnapsCanvasContainerLayout> containerLayoutMap = pagerAdapter.getCanvasSet();
				if (containerLayoutMap != null) {
					for (Map.Entry<SnapsPageCanvas, SnapsCanvasContainerLayout> entry : containerLayoutMap.entrySet()) {
						if (entry != null) {
							removeCanvas(entry.getValue());
						}
					}
				}
			}
		}
	}

	private void recyclePagerViewHolder(SmartRecommendBookEditListPageHolder pageHolder) throws Exception {
		SnapsCanvasContainerLayout containerLayout = pageHolder.getCanvasParentLayout();
		removeCanvas(containerLayout);
	}

	private void removeCanvas(SnapsCanvasContainerLayout containerLayout) {
		if (containerLayout != null && containerLayout.getChildCount() > 0) {
			SnapsPageCanvas snapsPageCanvas = containerLayout.getSnapsPageCanvas();
			if (snapsPageCanvas != null) {
				snapsPageCanvas.releaseReferences();
			}
			containerLayout.removeAllViews();
		}
	}

	private ArrayList<SnapsPage> getPageList() {
		return snapsPageList;
	}

	@Override
	public int getItemCount() {
		return snapsPageList != null ? snapsPageList.size()+1 : 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void clear() {
		int size = snapsPageList.size();
		snapsPageList.clear();
		notifyItemRangeRemoved(0, size);
	}

	public void addAll(SnapsPage[] contentses) {
		int startIndex = snapsPageList.size();
		snapsPageList.addAll(startIndex, Arrays.asList(contentses));
		notifyItemRangeInserted(startIndex, contentses.length);
	}

	public void addAll(List<SnapsPage> contentses) {
		int startIndex = snapsPageList.size();
		snapsPageList.addAll(startIndex, contentses);
		notifyItemRangeInserted(startIndex, contentses.size());
	}

	private void initPageListWithTemplate(SnapsTemplate template) {
		if (template == null) return;

		if (snapsPageList != null)
			snapsPageList.clear();
		else
			snapsPageList = new ArrayList<>();

		snapsPageList = template.getPages();
	}
}
