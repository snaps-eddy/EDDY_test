package com.snaps.mobile.activity.google_style_image_selector.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;

import com.bumptech.glide.Glide;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryUploadOpserver;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies.ImageSelectUIProcessorStrategyFactory;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectSNSData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUIPhotoFilter;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectListUpdateListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectPublicMethods;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectStateChangedListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectTitleBarListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.GIFTutorialView;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintDataManager;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import errorhandle.SnapsAssert;
import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.REQCODE_GOOGLE_SCOPE;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.REQCODE_INPUT_TITLE;

/**
 * 사진 선택 통합 Activity
 */
public class ImageSelectActivityV2 extends SnapsBaseFragmentActivity implements ISnapsDiaryUploadOpserver,
		IImageSelectStateChangedListener, IImageSelectTitleBarListener,
		IImageSelectPublicMethods, GoHomeOpserver.OnGoHomeOpserver {
	private static final String TAG = ImageSelectActivityV2.class.getSimpleName();
	/**
	 *    - 선택 되는 이미지 정보는 ImageSelectManager(singleton)에서 ImageSelectImgDataHolder로 공통 관리 한다.
	 *
	 * 	  - 실제 UI 는 대 부분 ImageSelectUIProcessor 에서 처리 한다.
	 */
	private ImageSelectUIProcessor mUIProcessor = null;

	private ImageSelectIntentData mIntentData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//오류 로그 수집
		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		//UI 처리자(디테일한 UI는 대부분 해당 클래스에서 처리)
		mUIProcessor = new ImageSelectUIProcessor(this);

		//수신된 인텐트 기본 정보
		setDefaultIntentData();

		//화면 회전에 대한 설정
		if (mUIProcessor.isLandScapeMode())
			UIUtil.updateFullscreenStatus(this, true);
		else
			UIUtil.updateFullscreenStatus(this, false);

		setContentView(R.layout.activity_google_photo_style_image_select);

		if (!isEditActivityFinishing()) {
			initialize();
		}

		GoHomeOpserver.addGoHomeListener(this);

		//TODO::임시 구현
		//heif 파일이 존재하는 경우 UI에 어떻게 표시할지 기획된 내용이 아직없다.
		//TODO::일단 아래 소스 문제는 사진 선택 화면이 경우 표시되는데, 사진 변경 화면에서는 표시할 필요가 없을 것 같은데 소스 상으로 구별하는 방법은 찾아봐야 한다.
		//HEIF (.heic) 존재 유무
//		ImageSelectManager manager = ImageSelectManager.getInstance();
//		if (manager != null) {
//			int heifImageCount = manager.getHeifImageCount();
//			heifImageCount = 99;
//			if (heifImageCount > 0) {
//				MessageUtil.toast(this, "[개발 버전]\nHEIF(.heic) 파일이 존재합니다.\n" + heifImageCount + "개");
//
//			}
//		}
	}

	@Override
	public void onGoHome() {
		finish();
	}

	private boolean isEditActivityFinishing() {
		SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
		if (snapsTemplateManager.isActivityFinishing()) {
			ATask.executeVoidWithThreadPoolBooleanDefProgress(this, new ATask.OnTaskResult() {
				@Override
				public void onPre() {}
				@Override
				public boolean onBG() {
					SnapsTemplateManager.waitIfEditActivityFinishing();
					return true;
				}

				@Override
				public void onPost(boolean result) {
					initialize();
				}
			});
			return true;
		}
		return false;
	}

	private Map<String, String> mSelfAIImageMap= null;

	public boolean isSelfAIImage(String path) {

		if(mSelfAIImageMap.containsKey(path))
			return true;
		return false;

	}

	private void initialize() {

		mSelfAIImageMap = null;

		if(Config.getAI_IS_SELFAI() && !Config.getAI_SELFAI_EDITTING()) {

			ATask.executeVoidDefProgress(ImageSelectActivityV2.this, new ATask.OnTask() {
				@Override
				public void onPre() {
				}

				@Override
				public void onBG() {
					mSelfAIImageMap = GetParsedXml.getStoryImageList(SystemUtil.getDeviceId(ImageSelectActivityV2.this), SnapsLoginManager.getUUserNo(ImageSelectActivityV2.this), Config.getAI_SEARCHTYPE(), Config.getAI_SEARCHVALUE(), Config.getAI_SEARCHDATE(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());

				}

				@Override
				public void onPost() {
					// 마지막 앨범 cursor값을 기억하고 있어서 시간/장소 추천의 경우 "모든 사진" cursor index 0에서 찾아야 하므로 아래 기억값을 초기화 시켜준다
					ImageSelectUtils.initPhotoLastSelectedHistory();
					initializeFinally();
				}
			});

		} else {
			initializeFinally();
		}
	}

	private void initializeFinally() {
		//실질적인 UI 처리자 생성
		mUIProcessor.createUIProcessor();

		//기본 Fragment 로딩 (제품별로 처음 나오는 기본 프래그 먼트가 다를 수 있다.)
		mUIProcessor.loadBaseFragment();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_UP) {
			SnapsTutorialUtil.clearTooltip();
		}

		return super.dispatchTouchEvent(ev);
	}

	/**
	 * Fragment에서 사진을 선택했을 때..
	 */
	@Override
	public void onFragmentItemSelected(ImageSelectAdapterHolders.PhotoFragmentItemHolder fragmentViewHolder) {
		if (mUIProcessor == null) return;

		try {
			mUIProcessor.tryInsertImageDataToHolder(fragmentViewHolder);
		} catch (Exception e) {
			SnapsAssert.assertException(this, e);
			Dlog.e(TAG, e);
		}
	}

	/**
	 * 선택 해제 처리 (tray에서건, Fragment에서건 선택 해제는 여기서 공통적으로 처리 한다.)
     */
	@Override
	public void onItemUnSelectedListener(eCONTROL_TYPE controlType, String mapKey) {
		if (mUIProcessor == null) return;

		//선택 된 이미지 정보는 ImageSelectUtils의 imageSelectDataHolder에서 공통으로 관리한다.
		mUIProcessor.removeSelectedImageData(mapKey);

		ImageSelectTrayBaseAdapter trayBaseAdapter = mUIProcessor.getTrayAdapter();
		if (trayBaseAdapter != null)
			trayBaseAdapter.removeSelectedImage(mapKey);
	}

	/**
	 * 트레이 아이템을 클릭 했을 때
	 */
	@Override
	public void onTrayItemSelected(ImageSelectTrayCellItem item) {
		if (mUIProcessor == null) return;
		mUIProcessor.handleOnTrayItemSelected(item);
	}

	/**
	 *  스크롤이 동작할때
	 */
	@Override
	public void onChangedRecyclerViewScroll() {}

	/**
	 * 트레이 전체 보기 버튼 클릭
	 */
	@Override
	public void onClickedTrayAllView() {
		if (mUIProcessor == null) return;

		ImageSelectManager manager = ImageSelectManager.getInstance();
		if (manager == null) return;

		//현재 트레이 아이템 셋팅
		ImageSelectTrayBaseAdapter adapter = mUIProcessor.getTrayAdapter();
		if (adapter != null)
			manager.cloneTrayCellItemList(adapter.getTrayCellItemList());

		//TEMPLATE or SNS or EMPTY or SINGLE_CHOOSE
		ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE selectedUIType = mUIProcessor.getImageSelectType();

//		Intent intent = new Intent(this, ImageSelectActivityTrayAllView.class);
//		intent.putExtra(Const_VALUE.KEY_IMAGE_SELECT_UI_TYPE, selectedUIType != null ? selectedUIType.ordinal() : -1);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		Intent intent = ImageSelectActivityTrayAllView.getIntent(this, selectedUIType);
		startActivityForResult(intent, ISnapsImageSelectConstants.REQCODE_TRAY_ALL_VIEW);
		overridePendingTransition(R.anim.anim_for_tray_all_view_down_to_up, 0);

		SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_selectphoto_clickTotal));
	}

	@Override
	public void onClickedTrayAddBtn() {
		if (mUIProcessor == null) return;

		//현재 트레이 아이템 셋팅
		ImageSelectTrayBaseAdapter adapter = mUIProcessor.getTrayAdapter();
		if (adapter != null) adapter.performClickTrayAddBtn();
	}

	/**
	 * 백키
	 */
	@Override
	public void onClickedBackKey() {
		if (mUIProcessor == null) return;

		try {
			if(mUIProcessor.hideAlbumListSelector()) //앨범 리스트가 펼쳐져 있다면, 다시 넣음.
                return;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		if (mUIProcessor.removeTutorial()) //튜토리얼 종료
			return;

		if (mUIProcessor.getFragmentTypeSize() <= 1) { //루트 Fragment에서 종료 시도
			WebLogConstants.eWebLogName logName = isMultiChooseType() ? WebLogConstants.eWebLogName.photobook_annie_addphoto_clickBack : WebLogConstants.eWebLogName.photobook_annie_selectphoto_clickBack;
			SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName).appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

			if (checkImmediatelyFinishActivity()) {
				initImageSelectInfo();
				if(!isSingleChooseType()) {
					String msg = getString(R.string.moveto_detailpage_msg);
					//KT 북
					if (Config.isKTBook()) {
						msg = Const_VALUES.KT_BOOK_BACK_KEY_MSG;
					}
					MessageUtil.alertnoTitle(this, msg, new ICustomDialogListener() {

						@Override
						public void onClick(byte clickedOk) {
							switch (clickedOk) {
								case ICustomDialogListener.OK:
									ImageSelectActivityV2.this.finish();
									break;
								default:
									break;
							}

						}
					});
				} else {
					ImageSelectActivityV2.this.finish();
				}

			}
		} else {
			mUIProcessor.popFragmentType(); //enumType
			FragmentUtil.onBackPressed(this);

			mUIProcessor.updateTitle();
		}
	}

	private void initImageSelectInfo() {
		ImageSelectUtils.initLastSelectedPhotoSourceKind();

		if (Config.isSmartSnapsRecommendLayoutPhotoBook() && !isMultiChooseType()) {
//			SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
//			smartSnapsManager.clearLayoutDataOfAnalysisPhotoBook();

			SmartSnapsManager.finalizeInstance();
		}
	}

	/**
	 * 앨범 사진에 진입했을 때, 폰 앨범 리스트 셀렉터를 생성해 준다.
     */
	@Override
	public void onRequestedMakeAlbumList(ArrayList<IAlbumData> cursors) {
		if (mUIProcessor == null) return;
		mUIProcessor.makeAlbumListSelector(cursors);
	}

	@Override
	public void onRequestRemovePrevAlbumInfo() {
		if (mUIProcessor == null) return;
		mUIProcessor.removeAlbumListSelector();
	}

	/**
	 * 앨범을 선택 했을 때 호출 됨.
     */
	@Override
	public void onSelectedAlbumList(IAlbumData cursor) {
		if (cursor == null || mUIProcessor == null) {
			return;
		}

		try {
			mUIProcessor.hideAlbumListSelector(); //타이틀바 다시 올림.

			//똑같은 앨범을 선택했다면, 무시한다.
			IAlbumData currentAlbum = mUIProcessor.getCurrentFragmentAlbumData();
			if (currentAlbum == null || (cursor.getAlbumUrl() != null && !currentAlbum.getAlbumUrl().equalsIgnoreCase(cursor.getAlbumUrl()))) {

				mUIProcessor.notifyAlbumListUpdateListener(cursor); //리스트 갱신

				mUIProcessor.updateTitle(cursor.getAlbumName());
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	/**
	 * 다음(또는 완료) 키 클릭
     */
	@Override
	public void onClickedNextKey() {
		if (mUIProcessor == null) return;

		//제품 별로 구분되어 있다. (ImageSelectPerformerFactory)
		IImageSelectProductPerform performer = mUIProcessor.getPerformer();
		if (performer != null) {
			performer.onClickedNextBtn();
		}

		if (isMultiChooseType()) {
			SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_addphoto_clickEnter)
					.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(getPageIndex()))
					.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
		} else {
			SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_selectphoto_clickEnter)
					.appendPayload(WebLogConstants.eWebLogPayloadType.WHERE, isMultiChooseType() ? WebLogConstants.eWebLogPhotoUploadCompleteWhere.SECOND.getValue() : WebLogConstants.eWebLogPhotoUploadCompleteWhere.FIRST.getValue()));
		}
	}

	public int getPageIndex() {
		return mIntentData != null ? mIntentData.getPageIndex() : -1;
	}

	/**
	 * 타이틀 바 텍스트 선택
	 */
	@Override
	public void onClickedTitleText() {
		if (mUIProcessor == null) return;
		if(Config.getAI_IS_SELFAI() && !Config.getAI_SELFAI_EDITTING()) return;

		try {
			mUIProcessor.switchAlbumListState();

			SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_selectphoto_clickRange));
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	/**
	 * SelectImageSrcFragment 에서 선택했을 때, Fragment 전환 처리
	 */
	@Override
	public void onRequestedFragmentChange(ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT fragmentType, int selectedProduct) {
		if (fragmentType == ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.SELECT_IMAGE_SRC) {
			if (selectedProduct == Const_VALUES.SELECT_FACEBOOK && !Config.IS_SUPPORT_FACEBOOK) {
				MessageUtil.toast(this, R.string.facebook_not_support_msg);
				return;
			}

			if (selectedProduct == Const_VALUES.SELECT_INSTAGRAM && !Config.IS_SUPPORT_INSTAGRAM) {
				MessageUtil.toast(this, R.string.instagram_not_support_msg);
				return;
			}

			ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT fragment = ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.convertSelectProductToEnum(selectedProduct);
			if (fragment != null && fragment != ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.UNKNOWN)
				mUIProcessor.changeFragment(fragment);
		}
	}

	/**
	 * 사진 또는 트레이 로딩 시 에러가 발생했을 경우의 처리
     */
	@Override
	public void onTemplateDownloadErrorOccur(ePHOTO_LIST_ERR_TYPE errType) {
		if (mUIProcessor == null) return;
		mUIProcessor.setTemplateDownloadErrorUIState(errType);
	}

	@SuppressWarnings("unchecked")
	@SuppressLint("InflateParams")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mUIProcessor == null) return;

		switch (requestCode) {
			case ISnapsImageSelectConstants.REQCODE_DIARY_WRITE :
			{
				try {
					mUIProcessor.refreshPrevSelectedImageList();
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
				break;
			}
            case ISnapsImageSelectConstants.REQCODE_PHOTOPRINT :
                ImageSelectUtils.removeAllImageData();

                PhotoPrintDataManager photoPrintDataManager = PhotoPrintDataManager.getInstance();
                if( photoPrintDataManager != null && photoPrintDataManager.getDatas() != null ) {
                    for( PhotoPrintData photoPrintData : photoPrintDataManager.getDatas() ) {
                        String imgKey = PhotoPrintDataManager.getMapKey( photoPrintData );
						mUIProcessor.putSelectedImageData(imgKey, photoPrintData.getMyPhotoSelectImageData());
                    }
                }

				mUIProcessor.notifyListUpdateListener(null);

				mUIProcessor.refreshRemovedTrayImages();
                break;
			case ISnapsImageSelectConstants.REQCODE_TRAY_ALL_VIEW:
//				if (resultCode == ISnapsImageSelectConstants.RESULT_CODE_TRAY_ALL_VIEW_EDITED) { //전체보기에서 어떠한 동작을 했을 때만 갱신한다.
					mUIProcessor.refreshTrayCellListByAllViewList();
//				}
				break;

			case ISnapsImageSelectConstants.REQCODE_IMGDETAILEDIT : //사진인화에서 썸네일 클릭 후 회전 처리 되었을 때
				if (resultCode == RESULT_OK) {
					ArrayList<String> tempList = data.getStringArrayListExtra(Const_EKEY.IMG_DATA_KEYLIST);
					HashMap<String, MyPhotoSelectImageData> tempMap = (HashMap<String, MyPhotoSelectImageData>) data.getSerializableExtra(Const_EKEY.IMG_DATA_MAP);
					mUIProcessor.refreshThumbnailsByPhotoPrintRotatedInfo(tempList, tempMap);
				}
				break;
			case ISnapsImageSelectConstants.REQCODE_EDIT:
				if (resultCode == RESULT_OK) {
					finish();
				}
				break;
			case ISnapsImageSelectConstants.REQCODE_GOOGLE_SIGN_IN:
				if (resultCode == RESULT_OK) {
					Dlog.d("succeed for google sign in. [ISnapsImageSelectConstants.REQCODE_GOOGLE_SIGN_IN]");
				}
				break;
			case REQCODE_GOOGLE_SCOPE :
				//FIXME... 요청 후 완료 되었을때,
				if (resultCode == RESULT_OK) {
					Dlog.d("succeed for google sign in. [REQCODE_GOOGLE_SCOPE ]");
				}
				break;
			case REQCODE_INPUT_TITLE:
				if (resultCode == RESULT_OK) {
					Dlog.d("succeed for google sign in. [REQCODE_INPUT_TITLE]");
					IImageSelectProductPerform performer = mUIProcessor.getPerformer();
					if (performer != null) {
						performer.moveNextActivity();
					}
				}

				break;
			default:
				ImageSelectSNSData snsData = mUIProcessor.getSNSData();
				if (snsData != null) {
					IFacebook facebook = snsData.getFacebook();
					if (facebook != null) {
						facebook.onActivityResult(this, requestCode, resultCode, data);
					}
				}
				break;
		}

		super.onActivityResult( requestCode, resultCode, data );
	}

	@Override
	public void onDestroy() {
		try {
            super.onDestroy();
        } catch( Exception e ) {
			Dlog.e(TAG, e);
        }

		if (mUIProcessor != null) {
			mUIProcessor.releaseInstance();
		}

		if (mIntentData != null) {
			mIntentData = null;
		}

		Setting.set(this, ISnapsImageSelectConstants.SINGLE_CHOOSE_IMAGE_KEY, "");

		if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
			if (isSingleChooseType()) { 	//일기 쓰기에서 사진 변경할 때는 데이터를 초기화 하지 않는다(뒤로 가기 기능이 있기 때문...)
				ImageSelectManager.finalizeInstance();
			}

			SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
			dataManager.removeDiaryUploadObserver(this);
		} else {
			ImageSelectManager.finalizeInstance();
		}

        PhotoPrintDataManager.getInstance().destroy();

		try {
			ViewUnbindHelper.unbindReferences(getWindow().getDecorView(), null, false);

//			GooglePhotoUtil.finalizeInstance();
            if( !Config.isSnapsPhotoPrint() )
                Glide.get(this).clearMemory();
        } catch ( Exception e ) {
			Dlog.e(TAG, e);
        }
	}

	@Override
	public void onStart() {
		super.onStart();
		if (getSNSData() != null) {
			IFacebook facebook = getSNSData().getFacebook();
			if (facebook != null) {
				facebook.addCallback();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (getSNSData() != null) {
			IFacebook facebook = getSNSData().getFacebook();
			if (facebook != null) {
				facebook.removeCallback();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (getSNSData() != null) {
			IFacebook facebook = getSNSData().getFacebook();
			if (facebook != null) {
				facebook.saveInstance(outState);
			}
		}
	}

	@Override
	public void onBackPressed() {
		onClickedBackKey();
	}

	//일기 업로드가 완료 되어 있을 때, Background에 있는 Activity를 모두 종료 시킨다.
	@Override
	public void onFinishDiaryUpload(boolean isIssuedInk, boolean isNewWrite) {
		finish();
	}

	private void setDefaultIntentData() {
		//기본 Intent 설정
		Intent getItt = getIntent();
		if (getItt == null) return;

		getItt.setExtrasClassLoader(ImageSelectIntentData.class.getClassLoader());
		ImageSelectIntentData intentData = (ImageSelectIntentData) getItt.getSerializableExtra(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY);
		if (intentData == null) return;

		mIntentData = intentData;

		//화면 회전 관련...
		boolean isScreenModeChange = intentData.isOrientationChanged();
		if (mUIProcessor != null)
			mUIProcessor.setLandScapeMode(isScreenModeChange ? UIUtil.fixCurrentOrientationAndReturnBoolLandScape(this) : UIUtil.fixOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));

		if (intentData.getHomeSelectProduct() == Config.SELECT_SNAPS_DIARY) {
			SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
			dataManager.registDiaryUploadObserver(this);
		}
        else if( intentData.getHomeSelectProduct() == Config.SELECT_PHOTO_PRINT )
            PhotoPrintDataManager.getInstance().init();

		//기존에 남아 있는 정보 삭제
		ImageSelectUtils.removeAllImageData();
	}

	//사용자가 사진을 선택했다면, 바로 종료하지 않고 컨펌을 띄운다.
	private boolean checkImmediatelyFinishActivity() {
		boolean isActivityFinish = true;
		boolean isSelectedEvenOne = false;
		ImageSelectImgDataHolder imageSelectImgDataHolder = ImageSelectUtils.getSelectImageHolder();
		if (imageSelectImgDataHolder != null) {
			isSelectedEvenOne = imageSelectImgDataHolder.getMapSize() > 0;
		}

		if (isSelectedEvenOne && !isSingleChooseType()) {
			if ((SnapsDiaryDataManager.isAliveSnapsDiaryService())
					|| (Config.isSnapsPhotoPrint()) || (Config.isSimplePhotoBook() || Config.isSimpleMakingBook() || Config.isCalendar() || Const_PRODUCT.isPackageProduct() || Const_PRODUCT.isCardProduct()))  {
				MessageUtil.alertnoTitle(this, String.format(getString(R.string.size_cancel)), new ICustomDialogListener() {
					@Override
					public void onClick(byte clickedOk) {
						if (clickedOk == ICustomDialogListener.OK) {
							SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_selectphotobackpopup_clickOk));

							if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
								SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
								SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
								writeInfo.clearImageList();
							}

							DataTransManager.releaseCloneImageSelectDataHolder();
							DataTransManager.releaseInstance();

							initImageSelectInfo();

							finish();
						} else {
							SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_selectphotobackpopup_clickCancel));
						}
					}
				});

				isActivityFinish = false;
			}
		} else {
			if (!isSingleChooseType() && !isMultiChooseType()) {
				DataTransManager.releaseCloneImageSelectDataHolder();
			}
		}

		return isActivityFinish;
	}

	/**
	 * 외부에서 접근하는 메서드들..
	 */
	@Override
	public ImageSelectUIProcessor getUIProcessor() {
		return mUIProcessor;
	}

	@Override
	public ImageSelectIntentData getIntentData() {
		return mIntentData;
	}

	@Override
	public ImageSelectSNSData getSNSData() {
		if (getUIProcessor() == null) return null;
		return getUIProcessor().getSNSData();
	}

	@Override
	public ImageSelectUIPhotoFilter getPhotoFilterInfo() {
		if (getUIProcessor() == null) return null;
		return getUIProcessor().getPhotoFilterInfo();
	}

	@Override
	public void updateTitle(int id) {
		if (getUIProcessor() != null) getUIProcessor().updateTitle(id);
	}

	@Override
	public String getTitleText() {
		return getUIProcessor().getTitleText();
	}

	@Override
	public void showTutorial(final ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType) {
		if(Config.isSNSBook()){
			if(Config.isNewKakaoBook()){
				SnapsTutorialUtil.showToast(this,new SnapsTutorialAttribute.Builder().setText(getString(R.string.tutorial_except_story_touch))
						.setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOAST_REMOVE_POST)
						.create());
			}else{
				SnapsTutorialUtil.showToast(this,new SnapsTutorialAttribute.Builder().setText(getString(R.string.tutorial_except_post_touch))
						.setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOAST_REMOVE_POST)
						.create());
			}

		}else if(Config.isSnapsDiary()) {
			//일기는 일단 나누도 나중에 일기 튜토리얼 할때 푼다
//			SnapsTutorialUtil.showToast(this,new SnapsTutorialAttribute.Builder().setText(getString(R.string.tutorial_except_diary_touch))
//					.setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOAST_REMOVE_POST)
//					.create());
		}else {
			if (getUIProcessor() != null) {

					getUIProcessor().showTutorial(tutorialType, new GIFTutorialView.CloseListener() {
						@Override
						public void close() {
							if(mUIProcessor.getTrayControl() != null){
								SnapsTutorialUtil.showTooltip(ImageSelectActivityV2.this, new SnapsTutorialAttribute.Builder().setTargetView(mUIProcessor.getTrayControl()
										.getTrayThumbRecyclerView()).setText(getString(R.string.tutorial_image_select_add))
										.setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
										.setTopMargin(UIUtil.convertDPtoPX(ImageSelectActivityV2.this, -10))
										.create());
							}
						}
					});
					SnapsTutorialUtil.checkTimeThreeMinDelay(new SnapsTutorialUtil.OnThreeMinListener() {
						@Override
						public void threeMin() {

							if (mUIProcessor.getTvNextKey() != null) {
								SnapsTutorialUtil.showTooltip(ImageSelectActivityV2.this, new SnapsTutorialAttribute.Builder()
										.setTargetView(mUIProcessor.getTvNextKey())
										.setText(getString(R.string.tutorial_look_bigger_image_add))
										.setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
										.setTopMargin(UIUtil.convertDPtoPX(ImageSelectActivityV2.this, -30))
										.create());
							}
						}

					});
				}

			}
	}

	public void showTutorialSnsPhoto() {
		SnapsTutorialUtil.showTooltip(ImageSelectActivityV2.this, new SnapsTutorialAttribute.Builder().setTargetView(mUIProcessor.getTrayControl()
				.getTrayThumbRecyclerView()).setText(getString(R.string.tutorial_image_select_add))
				.setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
				.setTopMargin(UIUtil.convertDPtoPX(ImageSelectActivityV2.this, -10))
				.create());

		SnapsTutorialUtil.checkTimeThreeMinDelay(new SnapsTutorialUtil.OnThreeMinListener() {
			@Override
			public void threeMin() {

					if (mUIProcessor.getTvNextKey() != null) {
						SnapsTutorialUtil.showTooltip(ImageSelectActivityV2.this, new SnapsTutorialAttribute.Builder()
								.setTargetView(mUIProcessor.getTvNextKey())
								.setText(getString(R.string.tutorial_look_bigger_image_add))
								.setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
								.setTopMargin(UIUtil.convertDPtoPX(ImageSelectActivityV2.this, -30))
								.create());
					}
				}

		});
	}

	public void showTutorialThreeItemAdd() {
				if(mUIProcessor.getTvNextKey() != null) {
					SnapsTutorialUtil.showTooltip(ImageSelectActivityV2.this, new SnapsTutorialAttribute.Builder()
							.setTargetView(mUIProcessor.getTvNextKey())
							.setText(getString(R.string.tutorial_look_bigger_image_add))
							.setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
							.setTopMargin(UIUtil.convertDPtoPX(ImageSelectActivityV2.this, -30))
							.create());
				}
	}

	@Override
	public int getHomeSelectProdKind() {
		return  getUIProcessor() != null ? getUIProcessor().getHomeSelectProdKind() : -1;
	}

	@Override
	public boolean isLandScapeMode() {
		return getUIProcessor() != null && getUIProcessor().isLandScapeMode();
	}

	@Override
	public boolean isAddableImage() {
		return getUIProcessor() != null && getUIProcessor().isAddableImage();
	}

	@Override
	public void setMaxImageCount() {
		if (getUIProcessor() != null) getUIProcessor().setMaxImageCount();
	}

	@Override
	public void setMaxImageCount(int count) {
		if (getUIProcessor() != null) getUIProcessor().setMaxImageCount(count);
	}

	@Override
	public boolean isSingleChooseType() {
		return getUIProcessor() != null && getUIProcessor().isSingleChooseType();
	}

	@Override
	public boolean isMultiChooseType() {
		return getUIProcessor() != null && getUIProcessor().isMultiChooseType();
	}

	@Override
	public void putSelectedImageData(String key, MyPhotoSelectImageData imgData) {
		if (getUIProcessor() != null) getUIProcessor().putSelectedImageData(key, imgData);
	}

	@Override
	public void removeSelectedImageData(String key) {
		if (getUIProcessor() != null) getUIProcessor().removeSelectedImageData(key);
	}

	@Override
	public void registerListUpdateListener(IImageSelectListUpdateListener listUpdateListener) {
		if (getUIProcessor() != null) getUIProcessor().registerListUpdateListener(listUpdateListener);
	}

	@Override
	public void unRegisterListUpdateListener(IImageSelectListUpdateListener listUpdateListener) {
		if (getUIProcessor() != null) getUIProcessor().unRegisterListUpdateListener(listUpdateListener);
	}
}