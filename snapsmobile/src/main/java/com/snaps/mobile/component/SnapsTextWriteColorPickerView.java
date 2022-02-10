package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.common.utils.ui.ViewIDGenerator;
import com.snaps.mobile.R;

import java.util.LinkedHashMap;
import java.util.Set;

public class SnapsTextWriteColorPickerView extends LinearLayout implements View.OnClickListener {
	private static final String TAG = SnapsTextWriteColorPickerView.class.getSimpleName();

	public interface ISnapsTextWriteColorPickerListener {
		void onSelectTextColor(String color);
	}

	private static final LinkedHashMap<String, String[]> mapColors;
	static {
		mapColors = new LinkedHashMap<>();
		mapColors.put("ffffff", new String[] { "ffffff", "f3f3f3", "cccccc", "999999", "666666", "000000" });
		mapColors.put("980000", new String[] { "980000", "dd7e6b", "cc4125", "a61c00", "85200c", "5b0f00" });
		mapColors.put("ff0000", new String[] { "ff0000", "ea9999", "e06666", "cc0000", "990000", "660000" });
		mapColors.put("ff9900", new String[] { "ff9900", "f9cb9c", "f6b26b", "e69138", "b45f06", "783f04" });
		mapColors.put("ffff00", new String[] { "ffff00", "ffe599", "ffd966", "f1c232", "bf9000", "7f6000" });
		mapColors.put("00ff00", new String[] { "00ff00", "b6d7a8", "93c47d", "6aa84f", "38761d", "274e13" });
		mapColors.put("00ffff", new String[] { "00ffff", "a2c4c9", "76a5af", "45818e", "134f5c", "0c343d" });
		mapColors.put("4a86e8", new String[] { "4a86e8", "a4c2f4", "6d9eeb", "3c78d8", "1155cc", "1c4587" });
		mapColors.put("0000ff", new String[] { "0000ff", "9fc5e8", "6fa8dc", "3d85c6", "0b5394", "073763" });
		mapColors.put("9900ff", new String[] { "9900ff", "b4a7d6", "8e7cc3", "674ea7", "351c75", "20124d" });
		mapColors.put("ff00ff", new String[] { "ff00ff", "d5a6bd", "c27ba0", "a64d79", "741b47", "4c1130" });
	}

	private SparseArray<SnapsTextWriteColorPickerItemHolder> mapParentColorViews = null;
	private SparseArray<SnapsTextWriteColorPickerItemHolder> mapChildrenColorViews = null;

	private SnapsTextWriteColorPickerItemHolder currentParentColorHolder = null;

	private View baseColorBgView = null, baseColorSelector = null;
	private String baseFontColor = "";

	private ISnapsTextWriteColorPickerListener colorPickerListener = null;

	public SnapsTextWriteColorPickerView(Context context) {
		super(context);
		init();
	}

	public SnapsTextWriteColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SnapsTextWriteColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		initLayout();
		invalidate();
	}

	private void initLayout() {
		this.setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.CENTER_VERTICAL);

		LinearLayout parentColorLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams parentColorLayoutParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, getParentColorLayoutHeight());
		parentColorLayoutParam.leftMargin = UIUtil.convertDPtoPX(getContext(), 21);
		parentColorLayoutParam.rightMargin = UIUtil.convertDPtoPX(getContext(), 20);
		parentColorLayout.setLayoutParams(parentColorLayoutParam);
		parentColorLayout.setOrientation(LinearLayout.HORIZONTAL);
		this.addView(parentColorLayout);

		LinearLayout childrenColorLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams childrenLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, getChildColorLayoutHeight());
		childrenLayoutParams.topMargin = UIUtil.convertDPtoPX(getContext(), 11);
		childrenLayoutParams.leftMargin = UIUtil.convertDPtoPX(getContext(), 16);
		childrenLayoutParams.rightMargin = UIUtil.convertDPtoPX(getContext(), 16);
		childrenColorLayout.setLayoutParams(childrenLayoutParams);
		childrenColorLayout.setOrientation(LinearLayout.HORIZONTAL);
		this.addView(childrenColorLayout);

		createParentColorViews(parentColorLayout);

		createChildrenColorViews(childrenColorLayout);
	}

	public void selectPrevColor(String color) {
		try {
			int[] colorIndex = findColorIndex(color);
			if (colorIndex == null) {
				onClickParentColorWithItemHolder(mapParentColorViews.valueAt(0));
				onClickedBaseBgColor();
				return;
			}

			onClickParentColorWithItemHolder(mapParentColorViews.valueAt(colorIndex[0]));
			onClickChildColorWithItemHolder(mapChildrenColorViews.valueAt(colorIndex[1]));
		} catch (Exception e) { Dlog.e(TAG, e); }
	}

	//0 은 parent 1의 child 의 index를 반환 한다.
	private int[] findColorIndex(String color) {
		if (StringUtil.isEmpty(color) || mapColors == null) return null;
		Set<String> parentColorSet = mapColors.keySet();
		int parentColorIndex = 0;
		for (String parentColor : parentColorSet) {
			String[] childrenColors = mapColors.get(parentColor);
			int childColorIndex = 0;
			for (String childrenColor : childrenColors) {
				if (childrenColor.equalsIgnoreCase(color)) {
					return new int[] { parentColorIndex, childColorIndex};
				}
				childColorIndex++;
			}
			parentColorIndex++;
		}
		return null;
	}

	private int getParentColorLayoutHeight() {
		final int TOTAL_COLOR_ITEM_CNT = 11;
		int screenWidth = UIUtil.getScreenWidth(getContext()) - UIUtil.convertDPtoPX(getContext(), 41);
		return (screenWidth/TOTAL_COLOR_ITEM_CNT) + UIUtil.convertDPtoPX(getContext(), 13);
	}

	private int getChildColorLayoutHeight() {
		final int TOTAL_COLOR_ITEM_CNT = 6;
		int screenWidth = UIUtil.getScreenWidth(getContext()) - UIUtil.convertDPtoPX(getContext(), 32);
		return screenWidth/TOTAL_COLOR_ITEM_CNT;
	}

	public void removeChildColorItemSelector() {
		try {
			changeChildrenColorWithParentHolder(null);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void createChildrenColorViews(LinearLayout childrenColorLayout) {
		mapChildrenColorViews = new SparseArray<>();

		SnapsTextWriteColorPickerItemHolder parentColorHolder = mapParentColorViews.valueAt(0);
		String[] childrenColor = mapColors.get(parentColorHolder.getColor());

		for (String color : childrenColor) {
			int id = ViewIDGenerator.generateViewId(-1);
			SnapsTextWriteColorPickerItemHolder holder = createChildrenColorItemHolder(childrenColorLayout, color, id);
			mapChildrenColorViews.put(id, holder);
		}
	}

	private SnapsTextWriteColorPickerItemHolder createParentColorItemHolder(LinearLayout container, String color, int id) {
		View eachColorHolder = LayoutInflater.from(getContext()).inflate(R.layout.snaps_text_write_parent_color_picker_item_holder, null);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
		eachColorHolder.setLayoutParams(layoutParams);

		View bg = eachColorHolder.findViewById(R.id.snasp_text_write_color_picker_item_holder_bg_view);
		bg.setBackgroundColor(getParseColor(color));
		bg.setId(id);
		bg.setOnClickListener(this);

		View footer = eachColorHolder.findViewById(R.id.snasp_text_write_color_picker_item_holder_footer);
		View selector = eachColorHolder.findViewById(R.id.snasp_text_write_color_picker_item_holder_selector);

		container.addView(eachColorHolder);

		return new SnapsTextWriteColorPickerItemHolder.Builder().setColor(color).setParentView(eachColorHolder).setBg(bg).setFooter(footer).setSelector(selector).create();
	}

	private SnapsTextWriteColorPickerItemHolder createChildrenColorItemHolder(LinearLayout container, String color, int id) {
		View eachColorHolder = LayoutInflater.from(getContext()).inflate(R.layout.snaps_text_write_children_color_picker_item_holder, null);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
		if (container.getChildCount() > 0)
			layoutParams.leftMargin = UIUtil.convertDPtoPX(getContext(), 2);
		eachColorHolder.setLayoutParams(layoutParams);

		View bg = eachColorHolder.findViewById(R.id.snasp_text_write_color_picker_item_holder_bg_view);
		bg.setBackgroundColor(getParseColor(color));
		bg.setId(id);
		bg.setOnClickListener(this);

		View selector = eachColorHolder.findViewById(R.id.snasp_text_write_color_picker_item_holder_selector);

		container.addView(eachColorHolder);

		return new SnapsTextWriteColorPickerItemHolder.Builder().setColor(color).setParentView(eachColorHolder).setBg(bg).setSelector(selector).create();
	}

	private void createParentColorViews(LinearLayout parentColorLayout) {
		mapParentColorViews = new SparseArray<>();
		Set<String> parentColors = mapColors.keySet();
		for (String parentColor : parentColors) {
			int id = ViewIDGenerator.generateViewId(-1);
			SnapsTextWriteColorPickerItemHolder holder = createParentColorItemHolder(parentColorLayout, parentColor, id);
			mapParentColorViews.put(id, holder);
		}
	}

	private int getParseColor(String color) {
		return Color.parseColor("#" + color);
	}

	@Override
	public void onClick(View v) {
		try {
			if (mapParentColorViews.get(v.getId()) != null) {
				onClickParentColorWithItemHolder(mapParentColorViews.get(v.getId()));
			} else if (mapChildrenColorViews.get(v.getId()) != null) {
				onClickChildColorWithItemHolder(mapChildrenColorViews.get(v.getId()));
			}
		} catch (Exception e) { Dlog.e(TAG, e); }
	}

	private void onClickParentColorWithItemHolder(SnapsTextWriteColorPickerItemHolder holder) throws Exception {
		if (holder == null) return;

		notifyParentColorSelector(holder);
	}

	private void notifyParentColorSelector(SnapsTextWriteColorPickerItemHolder selectedHolder) throws Exception {
		if (currentParentColorHolder == selectedHolder && selectedHolder != null) return;

		for (int ii = 0; ii < mapParentColorViews.size(); ii++) {
			SnapsTextWriteColorPickerItemHolder itemHolder = mapParentColorViews.valueAt(ii);
			if (itemHolder == selectedHolder) {
				currentParentColorHolder = selectedHolder;
				itemHolder.getSelector().setVisibility(View.VISIBLE);
				itemHolder.getFooter().setVisibility(View.GONE);
			} else {
				itemHolder.getSelector().setVisibility(View.GONE);
				itemHolder.getFooter().setVisibility(View.VISIBLE);
			}
		}

		changeChildrenColorWithParentHolder(selectedHolder);
	}

	private void changeChildrenColorWithParentHolder(SnapsTextWriteColorPickerItemHolder selectedParentHolder) throws Exception {
		String[] childrenColor = selectedParentHolder != null ? mapColors.get(selectedParentHolder.getColor()) : null;

		for (int ii = 0; ii < mapChildrenColorViews.size(); ii++) {
			SnapsTextWriteColorPickerItemHolder itemHolder = mapChildrenColorViews.valueAt(ii);
			if (childrenColor != null) {
				itemHolder.setColor(childrenColor[ii]);

				View bgView = itemHolder.getBg();
				bgView.setBackgroundColor(getParseColor(itemHolder.getColor()));

				if (ii == 0) {
					itemHolder.getSelector().setVisibility(View.VISIBLE);

					hideBaseColorItemSelector();

					if (getColorPickerListener() != null)
						getColorPickerListener().onSelectTextColor(itemHolder.getColor());
				} else {
					itemHolder.getSelector().setVisibility(View.GONE);
				}
			} else {
				itemHolder.getSelector().setVisibility(View.GONE);
			}
		}
	}

	private void onClickChildColorWithItemHolder(SnapsTextWriteColorPickerItemHolder holder) throws Exception {
		if (holder == null || mapChildrenColorViews == null) return;

		performClickParentColorItemIfNotSelected(holder);

		for (int ii = 0; ii < mapChildrenColorViews.size(); ii++) {
			SnapsTextWriteColorPickerItemHolder itemHolder = mapChildrenColorViews.valueAt(ii);

			if (holder == itemHolder) {
				itemHolder.getSelector().setVisibility(View.VISIBLE);

				hideBaseColorItemSelector();

				if (getColorPickerListener() != null) {
					getColorPickerListener().onSelectTextColor(itemHolder.getColor());
				}
			} else {
				itemHolder.getSelector().setVisibility(View.GONE);
			}
		}
	}

	private void hideBaseColorItemSelector() {
		if (getBaseColorSelector() != null && getBaseColorSelector().isShown()) {
			getBaseColorSelector().setVisibility(View.GONE);
		}
	}

	private void performClickParentColorItemIfNotSelected(SnapsTextWriteColorPickerItemHolder holder) {
		if (holder == null) return;
		int[] colorIndex = findColorIndex(holder.getColor());
		if (colorIndex == null) return;

		try {
			onClickParentColorWithItemHolder(mapParentColorViews.valueAt(colorIndex[0]));
		} catch (Exception e) { Dlog.e(TAG, e); }
	}

	public ISnapsTextWriteColorPickerListener getColorPickerListener() {
		return colorPickerListener;
	}

	public void setColorPickerListener(ISnapsTextWriteColorPickerListener colorPickerListener) {
		this.colorPickerListener = colorPickerListener;
	}

	public void setBaseColorBgView(View baseColorBgView) {
		this.baseColorBgView = baseColorBgView;
	}

	public View getBaseColorSelector() {
		return baseColorSelector;
	}

	public void initBaseColorSelectorWithBaseColor(View baseColorSelector, final String baseFontColor) {
		this.baseColorSelector = baseColorSelector;
		this.baseFontColor = baseFontColor;

		this.baseColorBgView.setBackgroundColor(Color.parseColor("#" + baseFontColor));

		this.baseColorBgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickedBaseBgColor();
			}
		});
	}

	private void onClickedBaseBgColor() {
		if (StringUtil.isEmpty(baseFontColor)) return;

		removeChildColorItemSelector();

		if (getBaseColorSelector() != null)
			getBaseColorSelector().setVisibility(View.VISIBLE);

		if (getColorPickerListener() != null)
			getColorPickerListener().onSelectTextColor(baseFontColor);
	}

	private static class SnapsTextWriteColorPickerItemHolder {
		private View parentView;
		private View bg;
		private View selector;
		private String color;
		private View footer;

		private SnapsTextWriteColorPickerItemHolder(Builder builder) {
			this.parentView = builder.parentView;
			this.bg = builder.bg;
			this.selector = builder.selector;
			this.color = builder.color;
			this.footer = builder.footer;
		}

		public View getFooter() {
			return footer;
		}

		public View getParentView() {
			return parentView;
		}

		public View getBg() {
			return bg;
		}

		public View getSelector() {
			return selector;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public static class Builder {
			private View parentView;
			private View bg;
			private View footer;
			private View selector;
			private String color;

			public Builder setFooter(View footer) {
				this.footer = footer;
				return this;
			}

			public Builder setColor(String color) {
				this.color = color;
				return this;
			}

			public Builder setParentView(View parentView) {
				this.parentView = parentView;
				return this;
			}

			public Builder setBg(View bg) {
				this.bg = bg;
				return this;
			}

			public Builder setSelector(View selector) {
				this.selector = selector;
				return this;
			}

			public SnapsTextWriteColorPickerItemHolder create() {
				return new SnapsTextWriteColorPickerItemHolder(this);
			}
		}
	}
}
