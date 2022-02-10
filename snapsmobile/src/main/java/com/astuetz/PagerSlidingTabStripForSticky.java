/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.astuetz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.component.EndlessPagerBaseAdapter;

import java.util.Locale;

import font.FTextView;

public class PagerSlidingTabStripForSticky extends HorizontalScrollView {
	public interface IconTabProvider {
		public int getPageIconResId(int position);
	}

	public static final int TAB_TYPE_DEFAULT = 0; // 영역을 균등분배하여 그 가운데 텍스트를 배치하는 옵션.
	public static final int TAB_TYPE_EXPANDED = 1; // expanded를 사용하던 경우에 auto를 사용하지만 강제로 사용해야 될 경우를 위해 남겨둠
	public static final int TAB_TYPE_AUTO = 2; // 많을땐 스크롤 되게(expanded) 하고 적을땐 margin 같게 배치하는 옵션.
	private int tabType = TAB_TYPE_AUTO;

	// @formatter:off
	private static final int[] ATTRS = new int[] {
		android.R.attr.textSize,
		android.R.attr.textColor
    };
	// @formatter:on

	private LinearLayout.LayoutParams defaultTabLayoutParams;
	private LinearLayout.LayoutParams expandedTabLayoutParams;

	private final PageListener pageListener = new PageListener();
	public OnPageChangeListener delegatePageListener;

	private LinearLayout tabsContainer;
	private ViewPager pager;
	private View lastTab;

	private int tabCount;

	private int currentPosition = 0;
	private float currentPositionOffset = 0f;

	private Paint rectPaint;
	private Paint dividerPaint;

	private int indicatorColor = 0xEE000000;
	private int underlineColor = 0xFFEEEEEE;
	private int dividerColor = 0xAA000000;

	private boolean shouldExpand = false;
	private boolean textAllCaps = true;

	private int scrollOffset = 52;
	private int indicatorHeight = 3;
	private int underlineHeight = 1;
	private int dividerPadding = 12;
	private int tabPadding = 0;
	private int dividerWidth = 1;

	private int tabTextSize = 16;
	private int tabTextColor = 0xFF333333;
	private int tabTextSelectedColor = 0xFF333333;
	private int tabTextSpecialColor = 0xFFe5362c;
	private Typeface tabTypeface = null;
	private int tabTypefaceStyle = Typeface.BOLD;

	private int lastScrollX = -1;

	private int tabBackgroundResId = -1;
	private int backgroundColor = 0xFFFFFFFF;

	private int totalSpaceWidth = 0;
	private int screenWidth;

	private Locale locale;

	private Context context;

	private boolean isDrawUnderline = false;

	private boolean isStripClickable = true;

	public PagerSlidingTabStripForSticky(Context context) {
		this(context, null);
		this.context = context;
		this.isStripClickable = true;
	}

	public PagerSlidingTabStripForSticky(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.context = context;
		this.isStripClickable = true;
	}

	public PagerSlidingTabStripForSticky(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		this.isStripClickable = true;

		setFillViewport(true);
		setWillNotDraw(false);

		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(tabsContainer);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;

		scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
		indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
		underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
		dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
		tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
		dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
		tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

		// get system attrs (android:textSize and android:textColor)

		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

		tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
		tabTextColor = a.getColor(1, tabTextColor);

		a.recycle();

		// get custom attrs

		a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);
		indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
		underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
		dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);
		indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
		underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
		dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding);
		tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding);
		tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackgroundResId);
		backgroundColor = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsBackgroundColor, backgroundColor);
		shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand);
		scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
		textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

		a.recycle();

		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setStyle(Style.FILL);

		dividerPaint = new Paint();
		dividerPaint.setAntiAlias(true);
		dividerPaint.setStrokeWidth(dividerWidth);

		defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

		if (locale == null) {
			locale = getResources().getConfiguration().locale;
		}

		setBackgroundColor(backgroundColor);
	}

	public void setIsDrawUnderline(boolean isDrawUnderline) {
		this.isDrawUnderline = isDrawUnderline;
	}

	public void setStripClickable(boolean flag) {
		this.isStripClickable = flag;
	}

	public void setViewPager(ViewPager pager) {
		this.pager = pager;

		if (pager.getAdapter() == null) {
			tabCount = 0;
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		} else {
			PagerAdapter adapter = pager.getAdapter();

			if (adapter instanceof EndlessPagerBaseAdapter) {
				tabCount = ((EndlessPagerBaseAdapter) pager.getAdapter()).getDataCount();
			}
		}

		if (tabCount < 6) {
			tabsContainer.setWeightSum(tabCount);
		}

		pager.setOnPageChangeListener(pageListener);

		notifyDataSetChanged();
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.delegatePageListener = listener;
	}

	public void notifyDataSetChanged() {
		if (tabCount < 1) {
			return;
		}
		tabsContainer.removeAllViews();

		PagerAdapter adapter = pager.getAdapter();

		if (adapter instanceof EndlessPagerBaseAdapter) {
			EndlessPagerBaseAdapter baseAdapter = (EndlessPagerBaseAdapter) pager.getAdapter();

			for (int i = 0; i < tabCount; i++) {
				if (pager.getAdapter() instanceof IconTabProvider) {
					addIconTab(i, ((IconTabProvider) baseAdapter).getPageIconResId(i));
				} else {
					addTextTab(i, baseAdapter.getPageTitle(i).toString(), baseAdapter.isBadgeExist(i));
				}

			}
		}

		int current = pager.getCurrentItem();
		int firstPosition = current - (current % tabCount);
		for (int i = 0; i < tabsContainer.getChildCount(); ++i) {
			tabsContainer.getChildAt(i).setTag(new RealPosition(firstPosition + i));
			if (i == tabsContainer.getChildCount() - 1) {
				lastTab = tabsContainer.getChildAt(i);
			}
		}

		setTextSelect(current % tabCount);
		updateTabStyles();

		if (lastTab != null) {
			lastTab.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				@SuppressLint("NewApi")
				@Override
				public void onGlobalLayout() {
					lastTab.getViewTreeObserver().removeGlobalOnLayoutListener(this);

					currentPosition = pager.getCurrentItem() % tabCount;
					scrollToChild(currentPosition, 0);
				}
			});
		}
	}

	private void addTextTab(final int position, String title, boolean isBadgeExist) {
		RelativeLayout tabLayout = null;
		LinearLayout.LayoutParams lParam = null;
		tabLayout = (RelativeLayout) inflate(getContext(), R.layout.tab_item_for_sticky, null);
		lParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		tabLayout.setLayoutParams(lParam);

		FTextView tabText = (FTextView) tabLayout.findViewById(R.id.tab_text);
		tabText.setText(title);
		tabText.setTextColor(tabTextColor);

		FontUtil.applyTextViewTypeface(tabText, FontUtil.eSnapsFonts.YOON_GOTHIC_760);

		tabLayout.findViewById(R.id.tab_badge).setVisibility(isBadgeExist ? View.VISIBLE : View.GONE);

		addTab(position, tabLayout);
	}

	private void addIconTab(final int position, int resId) {

		ImageButton tab = new ImageButton(getContext());
		tab.setImageResource(resId);

		addTab(position, tab);

	}

	private void addTab(final int position, View tab) {
		tab.setFocusable(true);
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isStripClickable) {
					if (pager != null) {
						pager.setCurrentItem(((RealPosition) v.getTag()).value);
					}
				}
			}
		});

		tab.setPadding(tabPadding, 0, tabPadding, 0);
		tab.setTag(new RealPosition(position));

		tabsContainer.addView(tab, position % tabCount);
	}

	private void updateTabStyles() {
		if (!MenuDataManager.viewPageHorizontallyScrollable()) {
			return;
		}

		RelativeLayout.LayoutParams params;
		totalSpaceWidth = screenWidth;
		View v, lSpace, rSpace;

		int spaceBase = getResources().getDimensionPixelSize(R.dimen.tab_space_base);
		int spaceSide = getResources().getDimensionPixelSize(R.dimen.tab_space_side);

		if (tabType == TAB_TYPE_EXPANDED) { // 그냥 기본 margin값 적용하여 뿌린다.
			for (int i = 0; i < tabCount; i++) {
				v = tabsContainer.getChildAt(i);
				if (tabBackgroundResId > -1) {
					v.setBackgroundResource(tabBackgroundResId);
				}

				lSpace = v.findViewById(R.id.left_space);
				rSpace = v.findViewById(R.id.right_space);

				params = (RelativeLayout.LayoutParams) lSpace.getLayoutParams();
				params.width = i == 0 ? spaceSide : spaceBase;
				lSpace.setLayoutParams(params);

				params = (RelativeLayout.LayoutParams) rSpace.getLayoutParams();
				params.width = i == tabCount - 1 ? spaceSide : spaceBase;
				rSpace.setLayoutParams(params);
			}
		} else { // default나 auto인 경우.
			for (int i = 0; i < tabCount; i++) { // 실제 텍스트뷰의 넓이가 전부 구해질때까지 반복.
				v = tabsContainer.getChildAt(i).findViewById(R.id.text_layout);
				if (v.getWidth() < 1) {
					tabsContainer.getChildAt(tabCount - 1).getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@SuppressLint("NewApi")
						@Override
						public void onGlobalLayout() {
							updateTabStyles();
							tabsContainer.getChildAt(tabCount - 1).getViewTreeObserver().removeGlobalOnLayoutListener(this);
						}
					});
					return;
				}
				totalSpaceWidth -= v.getWidth();
			}

			if (tabType == TAB_TYPE_DEFAULT) { // defalut면 전체영역을 n분해서 그림.
				int viewW;
				int total = 0;
				for (int i = 0; i < tabCount; i++) {
					totalSpaceWidth = (screenWidth - 0) / tabCount;
					if (i < (screenWidth - 0) % tabCount) {
						totalSpaceWidth++;
					}

					v = tabsContainer.getChildAt(i);
					if (tabBackgroundResId > -1) {
						v.setBackgroundResource(tabBackgroundResId);
					}

					viewW = v.findViewById(R.id.text_layout).getWidth();
					total += viewW;

					lSpace = v.findViewById(R.id.left_space);
					rSpace = v.findViewById(R.id.right_space);

					params = (RelativeLayout.LayoutParams) lSpace.getLayoutParams();
					params.width = (totalSpaceWidth - viewW) / 2 + (((totalSpaceWidth - viewW) % 2 == 0 ? 0 : 1));
					total += params.width;
					lSpace.setLayoutParams(params);

					params = (RelativeLayout.LayoutParams) rSpace.getLayoutParams();
					params.width = (totalSpaceWidth - viewW) / 2;
					total += params.width;
					rSpace.setLayoutParams(params);
				}
				return;
			} else if (totalSpaceWidth < 1 || (totalSpaceWidth - spaceSide * 2 - spaceBase * (tabCount - 1) * 2 < 0 && tabType == TAB_TYPE_AUTO)) { // audo인데 화면이 안넘어가는 경우엔 expanded로 바꾸고 다시 draw.
				tabType = TAB_TYPE_EXPANDED;
				updateTabStyles();
				return;
			}

			int[][] fixDataAry = getSpaceData(spaceSide); // 각 아이템마다 간격을 구해서 draw.

			for (int i = 0; i < tabCount; i++) {
				v = tabsContainer.getChildAt(i);
				if (tabBackgroundResId > -1) {
					v.setBackgroundResource(tabBackgroundResId);
				}

				lSpace = v.findViewById(R.id.left_space);
				rSpace = v.findViewById(R.id.right_space);

				params = (RelativeLayout.LayoutParams) lSpace.getLayoutParams();
				params.width = fixDataAry[i][0];
				lSpace.setLayoutParams(params);

				params = (RelativeLayout.LayoutParams) rSpace.getLayoutParams();
				params.width = fixDataAry[i][1];
				rSpace.setLayoutParams(params);
			}
		}
	}

	/**
	 * text 간 배열을 위한 공간을 구
	 *
	 * @return
	 */
	private int[][] getSpaceData(int spaceSide) {
		int[][] fixData = new int[tabCount][2];
		totalSpaceWidth = totalSpaceWidth - spaceSide * 2;

		fixData[0][0] = spaceSide;
		fixData[fixData.length - 1][1] = spaceSide;
		int space = totalSpaceWidth / (tabCount * 2 - 2);
		for (int i = 0; i < fixData.length; ++i) {
			if (i > 0) {
				fixData[i][0] = space;
			}
			if (i < fixData.length - 1) {
				fixData[i][1] = space;
			}
		}

		int remainSpace = totalSpaceWidth % (tabCount * 2 - 2);
		int spaceIndex[] = {0, 1};
		for (int i = 0; i < remainSpace; ++i) {
			if (spaceIndex[0] > fixData.length - 2) {
				spaceIndex = new int[]{1, 0};
			}
			fixData[spaceIndex[0]][spaceIndex[1]]++;
		}
		return fixData;
	}

	private void scrollToChild(int position, int offset) {
		if (tabCount == 0) {
			return;
		}

		position = position % tabCount;
		int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0) {
			newScrollX -= scrollOffset;
		}

		if ((newScrollX != lastScrollX) && !(position == tabCount - 1 && offset != 0)) {
			lastScrollX = newScrollX;
			smoothScrollTo(newScrollX, 0);
		}
	}

	private void setTextSelect(int position) {
		for (int i = 0; i < tabsContainer.getChildCount(); ++i) {
//			( (FTextView) tabsContainer.getChildAt(i).findViewById(R.id.tab_text) ).setTextColor( i == position ? tabTextSelectedColor : tabTextColor );
			FTextView tabTextView = (FTextView) tabsContainer.getChildAt(i).findViewById(R.id.tab_text);
			CharSequence tabText = tabTextView.getText();
			if (tabText != null) {
				String tabTitle = tabText.toString();
				int textColor = getTabTextColorWithTabTitle(i == position, tabTitle);
				tabTextView.setTextColor(textColor);
			}
		}
	}

	private int getTabTextColorWithTabTitle(boolean isSelected, String title) {
		if (isSpecialTitle(title)) {
			return tabTextSpecialColor;
		}
		return isSelected ? tabTextSelectedColor : tabTextColor;
	}

	private boolean isSpecialTitle(String title) {
		if (StringUtil.isEmpty(title)) {
			return false;
		}
		for (String specialTxt : SPECIAL_TAB_TITLES) {
			if (title.toLowerCase().equalsIgnoreCase(specialTxt)) {
				return true;
			}
		}
		return false;
	}

	private final String[] SPECIAL_TAB_TITLES = {
			"hot"
	};

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (isInEditMode() || tabCount == 0) {
			return;
		}

		final int height = getHeight();
		float tabSizeModifier = getResources().getDimensionPixelSize(R.dimen.tab_more_line_size);

		// draw indicator line

		rectPaint.setColor(Color.parseColor("#60BBBBBB"));

		// default: line below current tab
		View currentTab = tabsContainer.getChildAt(currentPosition % tabCount).findViewById(R.id.text_layout);
		float lineLeft = currentTab.getLeft() + getPrevTabRight(currentPosition % tabCount) - tabSizeModifier;
		float lineRight = currentTab.getRight() + getPrevTabRight(currentPosition % tabCount) + tabSizeModifier;

		View currentTextView = tabsContainer.getChildAt(currentPosition % tabCount).findViewById(R.id.tab_text);
		float top = currentTextView.getTop();
		float bottom = currentTextView.getBottom();
		top += ((bottom - top) / 2);

		// if there is an offset, start interpolating left and right coordinates between current and next tab
		if (currentPositionOffset > 0f && currentPosition % tabCount < tabCount - 1) {
			int nextPosition = currentPosition % tabCount + 1;
			View nextTab = tabsContainer.getChildAt(nextPosition).findViewById(R.id.text_layout);
			final float nextTabLeft = nextTab.getLeft() + getPrevTabRight(nextPosition) - tabSizeModifier;
			final float nextTabRight = nextTab.getRight() + getPrevTabRight(nextPosition) + tabSizeModifier;

			lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
			lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
		} else if (currentPosition == tabCount - 1 && lastScrollX < 1) {
			int prevPosition = (currentPosition + 1) % tabCount;
			View prevTab = tabsContainer.getChildAt(prevPosition).findViewById(R.id.text_layout);
			final float prevTabLeft = prevTab.getLeft() + getPrevTabRight(prevPosition) - tabSizeModifier;
			final float prevTabRight = prevTab.getRight() + getPrevTabRight(prevPosition) + tabSizeModifier;

			lineLeft = prevTabLeft;
			lineRight = prevTabRight;
		}

		canvas.drawRect(lineLeft, top, lineRight, bottom, rectPaint);

		// draw underline

		if (isDrawUnderline) {
			rectPaint.setColor(underlineColor);
			canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);
		}
	}

	private float getPrevTabRight(int currentPosition) {
		if (currentPosition < 1) {
			return 0;
		} else {
			return tabsContainer.getChildAt(currentPosition - 1).getRight();
		}
	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			currentPosition = position % tabCount;
			currentPositionOffset = positionOffset;

			scrollToChild(position % tabCount, (int) (positionOffset * tabsContainer.getChildAt(currentPosition).getWidth()));

			invalidate();

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				scrollToChild(pager.getCurrentItem(), 0);
			}

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageSelected(int position) {
			if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}

			int firstPos = position - (position % tabCount);
			for (int i = 0; i < tabsContainer.getChildCount(); ++i) {
				tabsContainer.getChildAt(i).setTag(new RealPosition(firstPos + i));
			}

			setTextSelect(position % tabCount);

			invalidate();
		}

	}

	public void setTabType(int type) {
		tabType = type;
	}

	public void setIndicatorColor(int indicatorColor) {
		this.indicatorColor = indicatorColor;
		invalidate();
	}

	public void setIndicatorColorResource(int resId) {
		this.indicatorColor = getResources().getColor(resId);
		invalidate();
	}

	public int getIndicatorColor() {
		return this.indicatorColor;
	}

	public void setIndicatorHeight(int indicatorLineHeightPx) {
		this.indicatorHeight = indicatorLineHeightPx;
		invalidate();
	}

	public int getIndicatorHeight() {
		return indicatorHeight;
	}

	public void setUnderlineColor(int underlineColor) {
		this.underlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineColorResource(int resId) {
		this.underlineColor = getResources().getColor(resId);
		invalidate();
	}

	public int getUnderlineColor() {
		return underlineColor;
	}

	public void setDividerColor(int dividerColor) {
		this.dividerColor = dividerColor;
		invalidate();
	}

	public void setDividerColorResource(int resId) {
		this.dividerColor = getResources().getColor(resId);
		invalidate();
	}

	public int getDividerColor() {
		return dividerColor;
	}

	public void setUnderlineHeight(int underlineHeightPx) {
		this.underlineHeight = underlineHeightPx;
		invalidate();
	}

	public int getUnderlineHeight() {
		return underlineHeight;
	}

	public void setDividerPadding(int dividerPaddingPx) {
		this.dividerPadding = dividerPaddingPx;
		invalidate();
	}

	public int getDividerPadding() {
		return dividerPadding;
	}

	public void setScrollOffset(int scrollOffsetPx) {
		this.scrollOffset = scrollOffsetPx;
		invalidate();
	}

	public int getScrollOffset() {
		return scrollOffset;
	}

	public void setShouldExpand(boolean shouldExpand) {
		this.shouldExpand = shouldExpand;
		requestLayout();
	}

	public boolean getShouldExpand() {
		return shouldExpand;
	}

	public boolean isTextAllCaps() {
		return textAllCaps;
	}

	public void setAllCaps(boolean textAllCaps) {
		this.textAllCaps = textAllCaps;
	}

	public void setTextSize(int textSizePx) {
		this.tabTextSize = textSizePx;
		updateTabStyles();
	}

	public int getTextSize() {
		return tabTextSize;
	}

	public void setTextColor(int textColor) {
		this.tabTextColor = textColor;
		updateTabStyles();
	}

//	public void setTextColorResource(int resId) {
//		this.tabTextColor = getResources().getColor(resId);
//		updateTabStyles();
//	}

	public int getTextColor() {
		return tabTextColor;
	}

	public void setTypeface(Typeface typeface, int style) {
		this.tabTypeface = typeface;
		this.tabTypefaceStyle = style;
		updateTabStyles();
	}

	public void setTabBackground(int resId) {
		this.tabBackgroundResId = resId;
	}

	public int getTabBackground() {
		return tabBackgroundResId;
	}

	public void setTabPaddingLeftRight(int paddingPx) {
		this.tabPadding = paddingPx;
		updateTabStyles();
	}

	public int getTabPaddingLeftRight() {
		return tabPadding;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		currentPosition = savedState.currentPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPosition = currentPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currentPosition;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPosition);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	private class RealPosition {
		public int value;

		public RealPosition(int value) {
			this.value = value;
		}

	}
}
