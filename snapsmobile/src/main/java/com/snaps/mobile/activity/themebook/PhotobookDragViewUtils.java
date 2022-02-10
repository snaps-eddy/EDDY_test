package com.snaps.mobile.activity.themebook;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.customui.HDraggableGridParent;
import com.snaps.common.customui.dragdrop.DSRelativeLayout;
import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

import java.util.ArrayList;

import errorhandle.logger.Logg;

public class PhotobookDragViewUtils {
	private static final String TAG = PhotobookDragViewUtils.class.getSimpleName();
	private Context conext = null;
	private HDraggableGridParent gridOnelineSelectImgs;
	private ArrayList<DSRelativeLayout> m_arrDragViews = null;
	private ArrayList<SnapsPage> _pageList = null;
	private boolean isLandscapeMode = false;

	public PhotobookDragViewUtils() {
	};


	public void init(Context context, HDraggableGridParent gridOnelineSelectImgs, boolean isLandscapeMode) {
		this.conext = context;
		// this.imageLoader.setDiscCache(128, 128);
//		this.displayoptions = ImageLoaderOption.getOption_Empty4444();
		this.gridOnelineSelectImgs = gridOnelineSelectImgs;
		this.isLandscapeMode = isLandscapeMode;
	}

	public void setPageList(ArrayList<SnapsPage> _pageList) {
		this._pageList = _pageList;
	}

	public void setDragViewList(ArrayList<DSRelativeLayout> m_arrDragViews) {
		this.m_arrDragViews = m_arrDragViews;
	}

	private void setImageViewSizeOffsetPage(RelativeLayout layout, SnapsPage page) {
		if (page == null || conext == null || layout == null)
			return;

		boolean isExistText = Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isCalendar() || Config.isSimpleMakingBook() || Const_PRODUCT.isCardProduct();
		final int MAX_HEIGHT = isExistText ? 50 : 60;

		int imgWidth = UIUtil.convertDPtoPX(conext, 85);
		int imgHeight = UIUtil.convertDPtoPX(conext, MAX_HEIGHT);
		int offsetWidth = isLandscapeMode ? UIUtil.convertDPtoPX(conext, Config.isCalendarVert(Config.getPROD_CODE()) ? 70 : 76) : imgWidth;
		int offsetHeight = imgHeight;

		if (isLandscapeMode && Const_PRODUCT.isCardProduct())
			offsetWidth = UIUtil.convertDPtoPX(conext, 50);

		int pageWidth = 85;
		int pageHeight = 50;

		float fRat = 0.f;

		try {
			pageWidth = Integer.parseInt(page.width);
			pageHeight = Integer.parseInt(page.height);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		if (pageWidth > pageHeight) {
			fRat = pageHeight / (float) pageWidth;
			imgHeight = (int) (imgWidth * fRat);
		} else {
			fRat = pageWidth / (float) pageHeight;
			imgWidth = (int) (imgHeight * fRat);
		}

		float scale = 1;

		if (isLandscapeMode) {
			scale = offsetWidth / (float) imgWidth;
			imgWidth = offsetWidth;
			imgHeight = (int) (imgHeight * scale);
		} else {
			scale = offsetHeight / (float) imgHeight;
			imgHeight = offsetHeight;
			imgWidth = (int) (imgWidth * scale);
		}

		if (gridOnelineSelectImgs != null) {
			gridOnelineSelectImgs.setSetchildSize(imgWidth);
		}

		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) layout.getLayoutParams();
		params.width = imgWidth;
		params.height = imgHeight;

		layout.setLayoutParams(params);
	}

//	public View makeDragView(boolean isLandscape, final SnapsPage snapsPage, final int currentPosition, final int position) {
//
//		int layoutId = isLandscape ? R.layout.bottomview_item_renewal_horizontal_dragable : R.layout.bottomview_item_renewal_dragable;
//
//		if (Config.isCalendarMini(Config.getPROD_CODE()) || Config.isCalendarNormalVert(Config.getPROD_CODE())) {
//			layoutId = R.layout.bottomview_item_forcal_renewal_dragable;
//		} else if (Const_PRODUCT.isPackageProduct()) {
//			layoutId = R.layout.bottomview_item_package_kit_renewal_dragable;
//		} else if (Const_PRODUCT.isCardProduct()) {
//			layoutId = isLandscape ? R.layout.bottomview_item_forcal_renewal_dragable : R.layout.bottomview_item_forcal_renewal_dragable;
//		}
//
//		com.snaps.common.customui.dragdrop.DSRelativeLayout mview = (com.snaps.common.customui.dragdrop.DSRelativeLayout) LayoutInflater.from(conext).inflate(layoutId, null);
//
//		mview.setId((99990 + position)); // 기존 뷰의 ID와 구분하기 위해 더미값 99990을 넣음(의미 없음.)
//		final ImageView image = (ImageView) mview.findViewById(R.id.item);
//
//		RelativeLayout imgLayout = (RelativeLayout) mview.findViewById(R.id.item_lay);
//		if (!Config.isCalendarMini(Config.getPROD_CODE()) && !Config.isCalendarVert(Config.getPROD_CODE()) && !Config.isCalendarNormalVert(Config.getPROD_CODE()) && !Config.isCalenderWall(Config.getPROD_CODE()))
//			setImageViewSizeOffsetPage(imgLayout, snapsPage);
//
//		ImageView warnining = (ImageView) mview.findViewById(R.id.iv_warning);
//		warnining.setVisibility(View.INVISIBLE);
//
//		if (!snapsPage.thumbnailPath.equals("")) {
//			// mDownloader.loadBitmap(pageList.get(position).thumbnailPath, image);
//			if (image != null)
//				image.setScaleType(ScaleType.CENTER_INSIDE);
//
////			imageLoader.displayImage("file://" + snapsPage.thumbnailPath, image, displayoptions, new ImageLoadingListener() {
////				@Override
////				public void onLoadingStarted(String imageUri, View view) {
////				}
////
////				@Override
////				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
////				}
////
////				@Override
////				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
////				}
////
////				@Override
////				public void onLoadingCancelled(String imageUri, View view) {
////					if (image != null)
////						image.setScaleType(ScaleType.FIT_XY);
////				}
////			});
//			final String URL = "file://" + snapsPage.thumbnailPath;
//			SnapsImageViewTarget bitmapImageViewTarget = new SnapsImageViewTarget(image, null) {
//				@Override
//				public void onLoadStarted(@Nullable Drawable placeholder) {
//					super.onLoadStarted(placeholder);
//				}
//
//				@Override
//				public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//					super.onResourceReady(resource, transition);
//				}
//
//				@Override
//				public void onLoadFailed(@Nullable Drawable errorDrawable) {
//					super.onLoadFailed(errorDrawable);
//					if (image != null)
//						image.setScaleType(ScaleType.FIT_XY);
//				}
//			};
//
//			ImageLoader.asyncDisplayImage(coverAct, URL, bitmapImageViewTarget);
//
//
//		} else
//			image.setImageResource(R.drawable.img_default_pic_bottom);
//
//		TextView introindex = (TextView) mview.findViewById(R.id.itemintroindex);
//		TextView leftIndex = (TextView) mview.findViewById(R.id.itemleft);
//		TextView rightIndex = (TextView) mview.findViewById(R.id.itemright);
//
//		setDragViewsText(currentPosition, position, introindex, leftIndex, rightIndex);
//
//		if (snapsPage.isSelected) {
//			image.setBackgroundResource(R.drawable.shape_image_border_select);
//		} else {
//			image.setBackgroundResource(R.drawable.shape_image_border);
//			// image.setBackgroundResource(0);
//		}
//
//		// 느낌표 추가
//		if (snapsPage.isExistResolutionImage())
//			warnining.setVisibility(View.VISIBLE);
//		else
//			warnining.setVisibility(View.INVISIBLE);
//
//		image.setFocusable(false);
//
//		mview.setSnapsPage(snapsPage);
//		// mview.setThumbnailPath(snapsPage.thumbnailPath);
//		m_arrDragViews.add(position, mview);
//
//		return mview;
//	}

	public void setDragViewsText(int curPosition, int position, TextView introindex, TextView leftIndex, TextView rightIndex) {

		if (Config.isCalendar()) {
			String _label = "";

			String sideLabel = "";

			int size = _pageList.size();
			if (size == 14)
				size = 12;

			if (size >= 26)
				size = 24;

			int div = size % 2;
			int nStartYear = GetTemplateXMLHandler.getStartYear();
			int nStartMonth = GetTemplateXMLHandler.getStartMonth();

			int label;
			int cmp = 0;
			if (Config.isCalenderWall(Config.getPROD_CODE())) {
				cmp = (int) (position) - 1;
			} else if (div != 0) {
				cmp = (int) (Math.ceil((double) position / 2.0)) - 1;
			} else {
				if (size > 12 && !Config.isCalenderWall(Config.getPROD_CODE()))
					cmp = (int) (Math.floor(position / 2.0));
				else
					cmp = (int) (position);

			}
			if ((nStartMonth + cmp) > 12)
				label = ((nStartMonth + cmp)) % 12;
			else
				label = nStartMonth + cmp;

			if (div == 0) // 커버가 없는 경우
			{
				if (size == 12) {
					_label = Integer.toString(label) + conext.getString(R.string.month) + " ";

				} else if (size == 24) {
					if (position % 2 == 0)
						sideLabel = conext.getString(R.string.front);
					else
						sideLabel = conext.getString(R.string.back);

					_label = Integer.toString(label) + conext.getString(R.string.month) + " " + " (" + sideLabel + ")";

				}

			} else if (size == 13) {

				if (position == 0) // 커버
					_label = conext.getString(R.string.cover);
				else {
					_label = Integer.toString(label) + conext.getString(R.string.month) + " ";

				}

			} else if (size == 25) {

				if (position == 0) // 커버
					_label = conext.getString(R.string.cover);
				else {
					if (position % 2 != 0)
						sideLabel = conext.getString(R.string.front);
					else
						sideLabel = conext.getString(R.string.back);

					_label = Integer.toString(label) + conext.getString(R.string.month) + " " + " (" + sideLabel + ")";

				}

			}

			introindex.setText(_label);
			introindex.setVisibility(View.VISIBLE);
			introindex.setTextColor(Color.BLACK);
			leftIndex.setVisibility(View.INVISIBLE);
			rightIndex.setVisibility(View.INVISIBLE);

		} else if (Const_PRODUCT.isPackageProduct()) {
			introindex.setVisibility(View.GONE);
			leftIndex.setVisibility(View.GONE);
			rightIndex.setVisibility(View.GONE);
		} else if (Const_PRODUCT.isCardProduct()) {
			int textColor = Color.argb(186, 186, 186, 186);
			if (position == curPosition)
				textColor = Color.argb(255, 229, 71, 54);

			// 하단 텍스트 설정
			if (position % 2 == 0) {
				introindex.setText(conext.getString(R.string.front));
				introindex.setVisibility(View.VISIBLE);
				introindex.setTextColor(textColor);
				leftIndex.setVisibility(View.INVISIBLE);
				rightIndex.setVisibility(View.INVISIBLE);
			} else {
				introindex.setText(Const_PRODUCT.isCardShapeFolder() ? conext.getString(R.string.inner_side) : conext.getString(R.string.back));
				introindex.setVisibility(View.VISIBLE);
				introindex.setTextColor(textColor);
				leftIndex.setVisibility(View.INVISIBLE);
				rightIndex.setVisibility(View.INVISIBLE);
			}
		} else {
			int textColor = Color.argb(186, 186, 186, 186);
			if (position == curPosition)
				textColor = Color.argb(255, 229, 71, 54);

			// 하단 텍스트 설정
			if (position == 0) {
				introindex.setText(conext.getString(R.string.cover));
				introindex.setVisibility(View.VISIBLE);
				introindex.setTextColor(textColor);
				leftIndex.setVisibility(View.INVISIBLE);
				rightIndex.setVisibility(View.INVISIBLE);
			} else if (position == 1) {
				introindex.setText(conext.getString(R.string.inner_page));
				introindex.setVisibility(View.VISIBLE);
				introindex.setTextColor(textColor);
				leftIndex.setVisibility(View.INVISIBLE);
				rightIndex.setVisibility(View.INVISIBLE);
			} else {
				int pp = (position - 2) * 2 + 2;
				leftIndex.setText(Integer.toString(pp));
				rightIndex.setText(Integer.toString(++pp));
				introindex.setVisibility(View.INVISIBLE);

				leftIndex.setTextColor(textColor);
				rightIndex.setTextColor(textColor);
			}
		}
	}

	public void setSelectionDragView(final int moveType, final int position) {
		if (gridOnelineSelectImgs == null)
			return;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				gridOnelineSelectImgs.scrollToIdx(moveType, position);

				refreshBottomViewOutLine(position);
			}
		}, 100);
	}

	public void refreshBottomViewOutLine(int position) {
		try {
			if (!_pageList.get(position).isSelected) {
				for (SnapsPage p : _pageList) {
					p.isSelected = false;
				}

				if (m_arrDragViews != null) {
					for (int i = 0; i < m_arrDragViews.size(); i++) {
						if (i == position)
							continue;
						View view = (View) m_arrDragViews.get(i);
						bottomViewItemClick(view, false);
					}
				}

				_pageList.get(position).isSelected = true;
				bottomViewItemClick(m_arrDragViews.get(position), true);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public void bottomViewItemClick(View view, boolean isClick) {
		if (view == null || view.findViewById(R.id.item) == null)
			return;

		int drawable = isClick ? R.drawable.shape_image_border_select : R.drawable.shape_image_border;
		view.findViewById(R.id.item).setBackgroundResource(drawable);

		TextView leftIndex = (TextView) view.findViewById(R.id.itemleft);
		TextView rightIndex = (TextView) view.findViewById(R.id.itemright);
		TextView introIndex = (TextView) view.findViewById(R.id.itemintroindex);

		if (isClick) {
			leftIndex.setTextColor(Color.argb(255, 229, 71, 54));
			rightIndex.setTextColor(Color.argb(255, 229, 71, 54));
			introIndex.setTextColor(Color.argb(255, 229, 71, 54));

		} else {
			leftIndex.setTextColor(Color.argb(186, 186, 186, 186));
			rightIndex.setTextColor(Color.argb(186, 186, 186, 186));
			introIndex.setTextColor(Color.argb(186, 186, 186, 186));
		}
	}

//	public void refreshDragView(int idx) {
//		if (m_arrDragViews == null || idx >= m_arrDragViews.size())
//			return;
//
//		DSRelativeLayout child = (DSRelativeLayout) m_arrDragViews.get(idx);
//		SnapsPage snapsPage = child.getSnapsPage();
//		if (child != null) {
//			final ImageView image = (ImageView) child.findViewById(R.id.item);
//			if (!snapsPage.thumbnailPath.equals("")) {
//				if (image != null)
//					image.setScaleType(ScaleType.CENTER_INSIDE);
//
////				imageLoader.displayImage("file://" + snapsPage.thumbnailPath, image, displayoptions, new ImageLoadingListener() {
////					@Override
////					public void onLoadingStarted(String imageUri, View view) {
////					}
////
////					@Override
////					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
////					}
////
////					@Override
////					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
////					}
////
////					@Override
////					public void onLoadingCancelled(String imageUri, View view) {
////						if (image != null)
////							image.setScaleType(ScaleType.FIT_XY);
////					}
////				});
//			} else
//				image.setImageResource(R.drawable.img_default_pic_bottom);
//
//			ImageView warnining = (ImageView) child.findViewById(R.id.iv_warning);
//			if (snapsPage.isExistResolutionImage()) {
//				warnining.setVisibility(View.VISIBLE);
//			} else {
//				warnining.setVisibility(View.INVISIBLE);
//			}
//		}
//	}
//
//	public void refreshAllDragView() {
//		if (gridOnelineSelectImgs == null)
//			return;
//
//		for (int ii = 0; ii < gridOnelineSelectImgs.getChildCount(); ii++) {
//			DSRelativeLayout child = (DSRelativeLayout) gridOnelineSelectImgs.getChildAt(ii);
//			SnapsPage snapsPage = child.getSnapsPage();
//			if (child != null) {
//				final ImageView image = (ImageView) child.findViewById(R.id.item);
//				if (!snapsPage.thumbnailPath.equals("")) {
//					if (image != null)
//						image.setScaleType(ScaleType.CENTER_INSIDE);
//
////					imageLoader.displayImage("file://" + snapsPage.thumbnailPath, image, displayoptions, new ImageLoadingListener() {
////						@Override
////						public void onLoadingStarted(String imageUri, View view) {
////						}
////
////						@Override
////						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
////						}
////
////						@Override
////						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
////						}
////
////						@Override
////						public void onLoadingCancelled(String imageUri, View view) {
////							if (image != null)
////								image.setScaleType(ScaleType.FIT_XY);
////						}
////					});
//
//					final String URL = SnapsAPI.DOMAIN(false) + imgUrl;
//					SnapsImageViewTarget bitmapImageViewTarget = new SnapsImageViewTarget(vh.imgCoverAlbum, null) {
//						@Override
//						public void onLoadStarted(@Nullable Drawable placeholder) {
//							super.onLoadStarted(placeholder);
//							setProgressVisible(true);
//						}
//
//						@Override
//						public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//							super.onResourceReady(resource, transition);
//							int maxCount = Math.min(8, getCount());
//							if(++loadComplateCount >= maxCount)
//								setProgressVisible(false);
//							if (coverAlbum != null) {
//								coverAlbum.setScaleType(ImageView.ScaleType.FIT_XY);
//							}
//						}
//
//						@Override
//						public void onLoadFailed(@Nullable Drawable errorDrawable) {
//							super.onLoadFailed(errorDrawable);
//							setProgressVisible(false);
//						}
//					};
//
//					ImageLoader.asyncDisplayImage(coverAct, URL, bitmapImageViewTarget);
//
//				} else
//					image.setImageResource(R.drawable.img_default_pic_bottom);
//
//				ImageView warnining = (ImageView) child.findViewById(R.id.iv_warning);
//				if (snapsPage.isExistResolutionImage())
//					warnining.setVisibility(View.VISIBLE);
//				else
//					warnining.setVisibility(View.INVISIBLE);
//			}
//		}
//	}

//	public void refreshAllDragViewsText(int currentPosition) {
//		if (m_arrDragViews == null || m_arrDragViews.isEmpty())
//			return;
//
//		int idx = 0;
//		for (DSRelativeLayout dView : m_arrDragViews) {
//			TextView introindex = (TextView) dView.findViewById(R.id.itemintroindex);
//			TextView leftIndex = (TextView) dView.findViewById(R.id.itemleft);
//			TextView rightIndex = (TextView) dView.findViewById(R.id.itemright);
//			setDragViewsText(currentPosition, idx++, introindex, leftIndex, rightIndex);
//		}
//	}
//
//	public void sortPagesIndex(Activity activity) {
//		// 페이지 인덱스 및 이미지 인덱스 조정...
//		for (int i = 0; i < _pageList.size(); i++) {
//			SnapsPage p = _pageList.get(i);
//			p.setPageID(i);
//			p.isSelected = false;
//
//			for (SnapsControl control : p.getLayoutList()) {
//				control.setPageIndex(i);
//
//				// 이미지 정렬를 위해..
//				if (control instanceof SnapsLayoutControl) {
//
//					if (((SnapsLayoutControl) control).imgData != null) {
//						((SnapsLayoutControl) control).imgData.IMG_IDX = PhotobookCommonUtils.getImageIDX(i, control.regValue);
//						((SnapsLayoutControl) control).imgData.pageIDX = i;
//					}
//				}
//
//				if (control instanceof SnapsTControl) {
//					if (((SnapsTControl) control).imgData != null) {
//						// ((SnapsLayoutControl) control).imgData.IMG_IDX = i * 2 + Integer.parseInt(control.regValue);
//						((SnapsTControl) control).imgData.IMG_IDX = PhotobookCommonUtils.getImageIDX(i, control.regValue);
//						((SnapsTControl) control).imgData.pageIDX = i;
//					}
//				}
//			}
//		}
//	}
//
//	public void sortPagesIndex(Activity activity, ArrayList<SnapsPage> _frontPageList, ArrayList<SnapsPage> _backPageList) {
//		if (activity == null || _frontPageList == null || _backPageList == null)
//			return;
//		// 페이지 인덱스 및 이미지 인덱스 조정...
//		for (int i = 0; i < _frontPageList.size(); i++) {
//			SnapsPage p = _frontPageList.get(i);
//			p.setPageID(i);
//			p.isSelected = false;
//
//			SnapsPage backP = _backPageList.get(i);
//			backP.setPageID(i);
//
//			for (SnapsControl control : p.getLayoutList()) {
//				control.setPageIndex(i);
//
//				// 이미지 정렬를 위해..
//				if (control instanceof SnapsLayoutControl) {
//					if (((SnapsLayoutControl) control).imgData != null) {
//						((SnapsLayoutControl) control).imgData.IMG_IDX = PhotobookCommonUtils.getImageIDX(i, control.regValue);
//						((SnapsLayoutControl) control).imgData.pageIDX = i;
//					}
//				}
//
//				if (control instanceof SnapsTControl) {
//					if (((SnapsTControl) control).imgData != null) {
//						((SnapsTControl) control).imgData.IMG_IDX = PhotobookCommonUtils.getImageIDX(i, control.regValue);
//						((SnapsTControl) control).imgData.pageIDX = i;
//					}
//				}
//			}
//
//			for (SnapsControl control : backP.getLayoutList()) {
//				control.setPageIndex(i);
//
//				// 이미지 정렬를 위해..
//				if (control instanceof SnapsLayoutControl) {
//					if (((SnapsLayoutControl) control).imgData != null) {
//						((SnapsLayoutControl) control).imgData.IMG_IDX = PhotobookCommonUtils.getImageIDX(i, control.regValue);
//						((SnapsLayoutControl) control).imgData.pageIDX = i;
//					}
//				}
//
//				if (control instanceof SnapsTControl) {
//					if (((SnapsTControl) control).imgData != null) {
//						((SnapsTControl) control).imgData.IMG_IDX = PhotobookCommonUtils.getImageIDX(i, control.regValue);
//						((SnapsTControl) control).imgData.pageIDX = i;
//					}
//				}
//			}
//		}
//	}
}
