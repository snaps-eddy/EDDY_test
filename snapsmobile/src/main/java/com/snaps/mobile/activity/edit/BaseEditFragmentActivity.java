package com.snaps.mobile.activity.edit;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.card.SnapsTextOptions;
import com.snaps.mobile.activity.common.interfacies.SnapsEditActExternalConnectionBridge;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.activity.edit.pager.BaseSnapsPagerController;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.order.ISnapsCaptureListener;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.ArrayList;

import errorhandle.CatchFragmentActivity;

public class BaseEditFragmentActivity extends CatchFragmentActivity implements OnClickListener, SnapsEditActExternalConnectionBridge {
	private static final String TAG = BaseEditFragmentActivity.class.getSimpleName();
	public ArrayList<SnapsPage> _pageList = new ArrayList<>();

//	/** 상품 멀티 템플릿 */
	public SnapsTemplate _template = null;

//	/** Select Photo List */
	public ArrayList<MyPhotoSelectImageData> _galleryList = new ArrayList<MyPhotoSelectImageData>();

//	/** Page Load Count **/
	public int loadCompleteCount = 0;

	/** Select Photo Index */
	public int _imgIndex = 0;

	public DialogDefaultProgress pageProgress;

	public BaseSnapsPagerController _loadPager;
	public ArrayList<Fragment> _canvasList = new ArrayList<>();

	/** 명함 text List */
	public ArrayList<String> _textList;

	private ISnapsCaptureListener snapsPageCaptureListener = null;

	private Thread mProgressStateCheker = null;
	private long m_lLastProgressTime = 0;
	
	public void pageProgressUnload() {
		try {
			if (pageProgress != null)
				pageProgress.dismiss();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
	
	public void pageProgressLoad() {

        if(!Config.isRealServer()) return;

		if (SnapsOrderManager.isUploadingProject()) return;

		try {
			if (pageProgress != null && !pageProgress.isShowing()) {
				pageProgress.show();
				checkProgressState();
			}
			m_lLastProgressTime = System.currentTimeMillis();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public void showPageProgress() {
		if (pageProgress != null && !pageProgress.isShowing()) {
			pageProgress.show();
		}
	}
	
	private void checkProgressState() {
		if(mProgressStateCheker == null || mProgressStateCheker.getState() != Thread.State.RUNNABLE) {
			mProgressStateCheker = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						while (System.currentTimeMillis() - m_lLastProgressTime < 10000) { //안 멈추는 현상이 간헐적으로 발생하여, 10초로 제
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								Dlog.e(TAG, e);
							}
						}
						
						runOnUiThread(new Runnable() {
							public void run() {
								if(pageProgress != null && pageProgress.isShowing()) {
									pageProgress.dismiss();
								}
							}
						});
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				}
			});
			mProgressStateCheker.start();
		}
	}

	public void setPageFileOutput(final int index) {
		if (getSnapsPageCaptureListener() != null) {
			getSnapsPageCaptureListener().onFinishPageCapture(true);
		}
	}

	synchronized public void setPageThumbnailFail(final int pageIdx) {
		if (getSnapsPageCaptureListener() != null) {
			getSnapsPageCaptureListener().onFinishPageCapture(false);
		}
	}

	synchronized public void setPageThumbnail(final int pageIdx, String filePath) {
		//메인 페이지만 생성하니, 의미 없는 코드
	}
	
	synchronized public ArrayList<SnapsPage> getBackPageList() {
		if(_template == null) return null;
		return _template._backPageList;
	}

	synchronized public ArrayList<SnapsPage> getHiddenPageList() {
		if(_template == null) return null;
		return _template._hiddenPageList;
	}

	synchronized public SnapsTextOptions getTextOptions() {
		if(_template == null || _template.info == null) return null;
		return _template.info.snapsTextOption;
	}

	@Override
	public void onClick(View v) {}


	/***
	 * qrcode 위치를 구하는 함수
	 * @return
     */
	public Rect getQRCodeRect() {

		float mmQRWidth = 10;
		float mmQRHeight = 12.5f;

		// 커버를 구한다. 기준위치는 테마북으로 한다.
		SnapsPage coverPage = _template.getPages().get(0);

		float mm2px = 0.f;
		try {
			mm2px = coverPage.info.getPXMM();
		}catch (Exception e) {
			return null;
		}

		// 책등마진
		int thickMargin = (int)(mm2px * SnapsTemplateInfo.HARDCOVER_SPINE_WIDTH);
		//좌측마진 mm to px
		int leftMargin = (int)(mm2px*10);
		int bottomMargin = (int)(mm2px*7);

		int pxQRWidth = (int)(mm2px*mmQRWidth);
		int pxQRHeight = (int)(mm2px*mmQRHeight);

		int width = coverPage.getOriginWidth();
		int height = (int) Float.parseFloat(coverPage.height);

		Rect rect = new Rect();
		rect.left = width/2 - pxQRWidth - leftMargin - thickMargin/2;
		rect.top  = height - pxQRHeight - bottomMargin;
		rect.right = rect.left + pxQRWidth;
		rect.bottom = rect.top + pxQRHeight;
		return rect;
	}

	public ISnapsCaptureListener getSnapsPageCaptureListener() {
		return snapsPageCaptureListener;
	}

	public void setSnapsPageCaptureListener(ISnapsCaptureListener snapsPageCaptureListener) {
		this.snapsPageCaptureListener = snapsPageCaptureListener;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if( requestCode == Const_VALUE.REQ_CODE_PERMISSION ) {
			if( grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
				MessageUtil.toast(this, R.string.complete_granted_permission_plz_continue_order);
			} else {
				MessageUtil.toast(this, R.string.canceled_granted_permission_plz_continue_order);
			}
		}
	}

	@Override
	public SnapsTemplate getTemplate() {
		return _template;
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public int getCanvasLoadCompleteCount() {
		return loadCompleteCount;
	}

	@Override
	public void increaseCanvasLoadCompleteCount() {
		++loadCompleteCount;
	}

	@Override
	public void decreaseCanvasLoadCompleteCount() {
		--loadCompleteCount;
	}

	@Override
	public ArrayList<SnapsPage> getPageList() {
		return _pageList;
	}

	@Override
	public ArrayList<MyPhotoSelectImageData> getGalleryList() {
		return _galleryList;
	}

	@Override
	public SnapsProductEditorAPI getProductEditorAPI() {
		return null;
	}
}
