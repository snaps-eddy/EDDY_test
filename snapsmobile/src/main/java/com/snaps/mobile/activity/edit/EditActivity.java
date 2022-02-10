package com.snaps.mobile.activity.edit;

/**
 *
 * com.snaps.kakao.activity.edit EditActivity.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 22.
 * @Version :
 *
 * 더 이상 사용하지 않아서 주석처리. 액티비티 이름은 나중에 정리.
 */
@SuppressWarnings("unchecked")
public class EditActivity {
//public class EditActivity extends BaseEditFragmentActivity implements ISnapsHandler, SnapsOrderActivityBridge, SnapsImageUploadStateListener, ISnapsOrderStateListener {
//	private static final String TAG = EditActivity.class.getSimpleName();
//
//	/** Top View */
//	private View _topArea;
//	/** Bottom View */
//	private View _bottomArea;
//	/** Color Change Button */
//	private TextView _btnTopColorChange;
//	private TextView _btnTopImageEdit;
//	/** layout Change Button */
//	private TextView _btnTopLayoutChange;
//	/** case Change Button */
//	private TextView _btnTopCaseChange;
//	/** Template Load URL */
//	private String _url = "";
//
//	/** 페북북 관련 데이터 */
//	public KakaoBookData _kakaobookData;
//
//	/** 페북북 관련 데이터 */
//	public FaceBookData _fbbookData;
//	/** 페북북 커버리스트 데이터 */
//	public ArrayList<Xml_CoverResource> _fbbookCovers = new ArrayList<Xml_CoverResource>();
//
//	/** 카카오 스토리 User ID */
//	private String userNo = "";
//	/** 주문 코드 **/
//	private String _order;
//	/** 데이터 연동 결과 **/
//	private boolean taskComplete = false;
//	/** 현재 업로드 Index **/
//	private int uploadIndex = 0;
//	/** 원본 이미지 소스 데이터 **/
//	private MyPhotoSelectImageData orgData;
//	/** 현재 보여지는 아이템 Int 값 **/
//	private int editPageView;
//
//	private Xml_BgCover xmlBgCover;
//	private int bgCount = 1;
//	private SnapsBgControl bgTemp;
//	private SnapsTemplate _tempList;
//
//	public ArrayList<Fragment> _canvasList;
//
//	private int progress_value = 0;
//	private Map<Integer, BgInfo> mapCoverColor = new HashMap<Integer, BgInfo>();
//
//	boolean isUploadNShare = false;
//
//	// Dialog들
//	DialogConfirmFragment diagConfirm;
//	DialogInputNameFragment diagInput;
//	DialogSharePopupFragment diagShare;
//
//	KakaoLink kakaoLink;
//
//	// 재편집인지 확인하는 함수..
//	protected boolean IS_EDIT_MODE = false;
//
//	private SnapsHandler mSnapsHandler = null;
//
//	private Toast stickerKitBgRandom = null;
//	private boolean isStickerKitBgRandomToastCanceled = false;
//	private int stickerKitBgRandomToastShowCount = 0;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
//
//		Config.setIS_MAKE_RUNNING(true);
//
//		// 인디케이터 없애기.
//		Window win = getWindow();
//		win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//		setContentView(R.layout.activity_editpage);
//
//		_topArea = (View) findViewById(R.id.topArea);
//		_bottomArea = (View) findViewById(R.id.pager_counter);
//
//		_btnTopColorChange = (TextView) findViewById(R.id.btnTopColorChange);
//		_btnTopImageEdit = (TextView) findViewById(R.id.btnTopImageEdit);
//		_btnTopCaseChange = (TextView) findViewById(R.id.btnTopCaseChange);
//		_btnTopLayoutChange = (TextView) findViewById(R.id.btnTopLayoutChange);
//
//		ImageView btnTopShare = (ImageView) findViewById(R.id.btnTopShare);
//		btnTopShare.setVisibility(View.GONE);
//
//		mSnapsHandler = new SnapsHandler(this);
//
//		findViewById(R.id.screen_area).setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				isStickerKitBgRandomToastCanceled = true;
//				findViewById(R.id.screen_area).setOnTouchListener(null);
//				return false;
//			}
//		});
//
//		this.init();
//		// 배경 변경 데이터를 가져온다.
//		// 초기 템플릿 데이터.
//		this.getTemplateHandler();
//
//		try {
//			kakaoLink = KakaoLink.getLink(this);
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//		}
//	}
//
//	private void init() {
//		_canvasList = new ArrayList<Fragment>();
//		// 프로젝트 코드 초기화.
//		Config.setPROJ_CODE("");
//		Config.setPROJ_NAME("");
//
//		// 인텐트에서 project코드 product코드를 가져온다.
//		String prjCode = getIntent().getStringExtra(Const_EKEY.MYART_PROJCODE);
//		if (prjCode != null) {
//			IS_EDIT_MODE = true;
//			Config.setPROJ_CODE(prjCode);
//			Config.setPROD_CODE(getIntent().getStringExtra(Const_EKEY.MYART_PRODCODE));
//		}
//
//		 if (Config.isSnapsSticker()) {
//			// 스티커킷 컬러 변경 버튼은 항상 보이도록.
//			_btnTopImageEdit.setVisibility(ImageView.VISIBLE);
//			_btnTopColorChange.setVisibility(ImageView.GONE);
//
//			this.getBackGroundList(Config.RESOURCE_TMPL_CODE_BACKGROUND, Config.RESOURCE_ITEM_TYPE_CODE);
//		}
//		pageProgress = new DialogDefaultProgress(this);
//
//		ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
//		if (holder != null)
//			_galleryList = holder.getNormalData();
//
//		_textList = (ArrayList<String>) getIntent().getSerializableExtra("textdata");
//		_fbbookData = (FaceBookData) getIntent().getParcelableExtra("fbbook");
//		_kakaobookData = (KakaoBookData) getIntent().getParcelableExtra("kakaobook");
//
//		_fbbookCovers = (ArrayList<Xml_CoverResource>) getIntent().getSerializableExtra("fbbook_cover");
//
//		// 폴더 생성.
//		FileUtil.initProjectFileSaveStorage();
//
//		_loadPager = new SnapsPagerController(this, pageProgress);
//
//		// 템플릿을 다운받는다.
//	}
//
//	private void setGalleryDefaultRatio() {
//		if(_galleryList == null || _galleryList.isEmpty()) return;
//
//		double defaultRatio = 1;
//		ArrayList<MyPhotoSelectImageData> _imageList = _galleryList;
//		for(MyPhotoSelectImageData imgData : _imageList) {
//			if(imgData.cropRatio != 0)
//				defaultRatio = imgData.cropRatio;
//			else
//				imgData.cropRatio = defaultRatio;
//		}
//
//	}
//
//	@Override
//	public void onDisconnectNetwork() {}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//
//		Config.setIS_MAKE_RUNNING(false);
//
//		SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_APPLICATION);
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//
//		Config.setIS_MAKE_RUNNING(true);
//
//		userNo = SnapsLoginManager.getUUserNo(this);
//
//		onResumeControl();
//	}
//
//	@Override
//	public void onDestroy() {
//
//		// UI 캡쳐 중 이면 정지 시킨다.
//		if (_loadPager != null) {
//			try {
//				_loadPager.close();
//			} catch (IllegalStateException e) {
//				Dlog.e(TAG, e);
//			}
//		}
//
//		Config.setIS_MAKE_RUNNING(false);
//		// Progress Instance 제거.
//		SnapsTimerProgressView.destroyProgressView();
//		pageProgressUnload();
//		try {
//			if (diagConfirm != null)
//				diagConfirm.dismiss();
//			if (diagInput != null)
//				diagInput.dismiss();
//			if (diagShare != null)
//				diagShare.dismiss();
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//		}
//		super.onDestroy();
//
//		kakaoLink = null;
//		mapCoverColor = null;
//		orgData = null;
//		pageProgress = null;
//		_galleryList = null;
//		_pageList = null;
//		_canvasList = null;
//		_template = null;
//		diagConfirm = null;
//		diagInput = null;
//		diagShare = null;
//
//		try {
//			ViewUnbindHelper.unbindReferences(getWindow().getDecorView());
//			// System.gc();
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//		}
//
//		SnapsOrderManager.finalizeInstance();
//
//		SnapsUploadFailedImageDataCollector.clearHistory(Config.getPROJ_CODE());
//
//		ImageSelectUtils.initPhotoLastSelectedHistory();
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//		SnapsOrderManager.unRegisterNetworkChangeReceiver();
//	}
//
//	@Override
//	public void requestMakePagesThumbnailFile(ISnapsCaptureListener captureListener) {}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (RESULT_OK == resultCode) {
//
//			ArrayList<MyPhotoSelectImageData> modifiedImgList = new ArrayList<MyPhotoSelectImageData>();
//			DataTransManager dtMan = DataTransManager.getInstance();
//			if(dtMan != null) {
//				modifiedImgList = dtMan.getPhotoImageDataList();
//			} else {
//				DataTransManager.notifyAppFinish(this);
//				return;
//			}
//
//			if(_galleryList != null) {
//				for (MyPhotoSelectImageData cropData : modifiedImgList) {
//					if (cropData.isModify == -1)
//						continue;
//
//					MyPhotoSelectImageData d = PhotobookCommonUtils.getMyPhotoSelectImageDataWithImgIdx(_galleryList, cropData.getImageDataKey());
//
//					if (d != null) {
//						d.CROP_INFO = cropData.CROP_INFO;
//						d.FREE_ANGLE = cropData.FREE_ANGLE;
//						d.ROTATE_ANGLE = cropData.ROTATE_ANGLE;
//						d.ROTATE_ANGLE_THUMB = cropData.ROTATE_ANGLE_THUMB;
//						d.isApplyEffect = cropData.isApplyEffect;
//						d.EFFECT_PATH = cropData.EFFECT_PATH;
//						d.EFFECT_THUMBNAIL_PATH = cropData.EFFECT_THUMBNAIL_PATH;
//						d.EFFECT_TYPE = cropData.EFFECT_TYPE;
//						d.isAdjustableCropMode = cropData.isAdjustableCropMode;
//						d.ADJ_CROP_INFO = cropData.ADJ_CROP_INFO;
//						d.ORIGINAL_ROTATE_ANGLE = cropData.ORIGINAL_ROTATE_ANGLE;
//						d.ORIGINAL_THUMB_ROTATE_ANGLE = cropData.ORIGINAL_THUMB_ROTATE_ANGLE;
//						d.screenWidth = cropData.screenWidth;
//						d.screenHeight = cropData.screenHeight;
//						d.editorOrientation = cropData.editorOrientation;
//					}
//				}
//			}
//
//			for (Fragment fragment : _canvasList) {// 모든 이미지 다시 그리기
//				StickerCanvasFragment canvas = (StickerCanvasFragment) fragment;
//				canvas.makeSnapsCanvas();
//				if (canvas != null)
//					canvas.reLoadImageView();
//			}
//		}
//	}
//
//	private void onResumeControl() {
//		if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_CAPTURE)) {// 캡쳐 도중 멈췄을 경우 다시 전송 팝업.
//			requestMakeMainPageThumbnailFile(getSnapsPageCaptureListener());
//		} else if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_UPLOAD_COMPLETE)) {
//			SnapsOrderManager.showCompleteUploadPopup();
//		}
//
//		SnapsOrderManager.setSnapsOrderStatePauseCode("");
//
//		SnapsOrderManager.registerNetworkChangeReceiverOnResume();
//	}
//
//	/**
//	 * UI 버튼 클릭
//	 *
//	 * @param v
//	 */
//	public void onClick(View v) {
//		UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);
//
//		try {
//			int id = v.getId();
//			if (id == R.id.btnTopColorChange) {// 스티커킷 색상변경
//
//				if (xmlBgCover == null) {
//				}
//				// BackGround Color Change Button
//				getResourceChnageData(xmlBgCover.bgList.get(bgCount).F_XML_PATH);
//			}
//
//			else if (id == R.id.btnTopImageEdit) {// 사진편집
//
//				setGalleryDefaultRatio();
//
//				Intent imageEditIntent = new Intent(getApplicationContext(), ImageEditActivity.class);
//				PhotobookCommonUtils.setImageDataKey(_galleryList);
//
//
//				ArrayList<MyPhotoSelectImageData> tempList = new ArrayList <MyPhotoSelectImageData>();
//				int[] indexAry = new int[_galleryList.size()];
//				for (int i = 0; i < _galleryList.size(); ++i ) {
//					if (_galleryList.get(i).KIND != Const_VALUES.SELECT_SNAPS) {
//						indexAry[i] = tempList.size();
//						tempList.add(_galleryList.get(i));
//					}
//					else indexAry[i] = -1;
//				}
//
//				if( tempList.size() < 1 ) {
//					MessageUtil.toast(getApplicationContext(), R.string.no_editable_photo);
//					return;
//				}
//
//				DataTransManager dtMan = DataTransManager.getInstance();
//				if (dtMan != null) {
//					dtMan.setPhotoImageDataList(tempList);
//				} else {
//					DataTransManager.notifyAppFinish(this);
//					return;
//				}
//
//				int itemIdx = _loadPager.getPagerSelected();
//				Dlog.d("onClick() itemIdx:" + itemIdx);
//
//				int stickerCountInPage = 0; // 페이지당 들어가는 스티커 갯수
//				if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_6)) {
//					stickerCountInPage = 6;
//				} else if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_2)) {
//					stickerCountInPage = 2;
//				} else if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_1)) {
//					stickerCountInPage = 1;
//				}
//
//				int imgCnt = _galleryList.size(); // 이미지 총갯수
//				itemIdx = (itemIdx == 0) ? 0 : (itemIdx - 1) * stickerCountInPage % imgCnt; // 이미지와 스티커 포함하여 현재 페이지 첫 이미지의 인덱스
//                int editImageIndex = -1;
//                for( int i = 0; i < indexAry.length; ++i ) {
//                    if( indexAry[i] > -1 ) {
//                        if( i > itemIdx - 1 ) {
//                            editImageIndex = indexAry[i];
//                            break;
//                        }
//                        else if( editImageIndex < 0 ) editImageIndex = indexAry[i];
//                    }
//                }
//
//                if( editImageIndex > -1 ) {
//                    imageEditIntent.putExtra("dataIndex", editImageIndex);// 현재 보여지는 스티커의 1번째 이미지 idx
//                    startActivityForResult(imageEditIntent, 0);
//                }
//			}
//
//			else if (id == R.id.btnTopCaseChange) {// 명함 케이스변경, 페북북 색상변경
//
//				if (xmlBgCover == null ) {
//					MessageUtil.toast(EditActivity.this, R.string.loading_fail);
//				}
//
//				getResourceChnageData(xmlBgCover.bgList.get(bgCount).F_XML_PATH);// 명함 케이스 변경.
//
//			} else if (id == R.id.btnTopBack) {
//
//				MessageUtil.alertnoTitle(EditActivity.this, getString(R.string.init_edit_move_selectpage), new ICustomDialogListener() {
//					@Override
//					public void onClick(byte clickedOk) {
//						switch (clickedOk) {
//						case ICustomDialogListener.OK:
//							finish();
//							break;
//						default:
//							break;
//						}
//
//					}
//				});
//			} else if (id == R.id.btnTopOrder || id == R.id.textTopOrder) {// 장바구니
//				SnapsOrderManager.performSaveToBasket(this);
//			} else if (id == R.id.btn_confim) {
//				String confimType = (String) v.getTag();
//				Intent intent = null;
//				if (confimType.equalsIgnoreCase(DialogConfirmFragment.DIALOG_TYPE_CAPTURE_AGAIN)) {
//					// 다시 캡쳐 진행.
//						setPageFileOutput(0);
//				} else if (confimType.equalsIgnoreCase(DialogConfirmFragment.DIALOG_TYPE_ORDER_COMPLETE)) {
//
//					intent = new Intent(EditActivity.this, RenewalHomeActivity.class);
//					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					intent.putExtra("goToCart", true);
//					startActivity(intent);
//					finish();
//				}
//			} else if (id == R.id.button_input_name) {
//			}
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//		}
//	}
//
//	/***
//	 * 장바구니화면으로 이동하기...
//	 */
//	public void goToCartList() {
//	}
//
//	/**
//	 *
//	 * 카카오톡 공유
//	 *
//	 * @throws NameNotFoundException
//	 */
//
//	/**
//	 * @param message
//	 */
//	private void alert(String message) {
//		MessageUtil.alert(this, getString(R.string.app_name), message);
//	}
//
//	/**
//	 *
//	 * 초기 스티커킷 배경 리스트 가져오기.
//	 */
//	private void getBackGroundList(final String type, final String code) {
//
//		ATask.executeVoid(new ATask.OnTask() {
//
//			@Override
//			public void onPre() {
//			}
//
//			@Override
//			public void onBG() {
//				xmlBgCover = GetParsedXml.getChangeList(Config.getPROD_CODE(), type, code, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//			}
//
//			@Override
//			public void onPost() {
//
//				if (Config.isSnapsSticker() && new Random().nextInt(2) == 1)// 랜덤에 걸리면 2번째 템플릿 커버로 변경
//					getResourceChnageData(xmlBgCover.bgList.get(bgCount).F_XML_PATH);
//			}
//		});
//	}
//
//	/**
//	 *
//	 * 리소스 xml url
//	 *
//	 * @param url
//	 */
//	private void getResourceChnageData(final String url) {
//
//		ATask.executeBooleanDefProgress(this, new ATask.OnTaskResult() {
//
//			@Override
//			public void onPre() {
//			}
//
//			@Override
//			public boolean onBG() {
//				SnapsTemplate temp = GetTemplateLoad.getTemplate(url, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//				bgTemp = (SnapsBgControl) temp.getPages().get(0).getBgList().get(0);
//
//				Config.setUSER_COVER_COLOR(bgTemp.coverColor);
//
//				try {
//					BgInfo bgInfo = mapCoverColor.get(bgCount);
//					// SnapsTemplate temp = null;
//					if (bgInfo == null) {
//						temp = GetTemplateLoad.getTemplate(url, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//						bgTemp = (SnapsBgControl) temp.getPages().get(0).getBgList().get(0);
//						mapCoverColor.put(bgCount, new BgInfo(bgTemp));
//					} else {
//						bgInfo.setInfo(bgTemp);
//					}
//					Config.setUSER_COVER_COLOR(bgTemp.coverColor);
//					Dlog.d("getResourceChnageData() bgCount:" + bgCount + ", bgInfo:" + bgInfo + ", bg.coverColor:" + bgTemp.coverColor);
//
//					if (temp != null || bgInfo != null)
//						return true;
//				} catch (Exception e) {
//					Dlog.e(TAG, e);
//				}
//				return false;
//			}
//
//			@Override
//			public void onPost(boolean result) {
//				if (!result ) {
//					MessageUtil.toast(EditActivity.this, R.string.loading_fail);
//				} else {
//					if (Config.isSnapsSticker())
//						changeCoverTemplate();
//				}
//			}
//		});
//	}
//
//	class BgInfo {
//		public String target;
//		public String bgColor;
//		public String coverColor;
//		public String resourceURL;
//
//		public BgInfo(SnapsBgControl bgcontrol) {
//			this.target = bgcontrol.srcTarget;
//			this.bgColor = bgcontrol.bgColor;
//			this.coverColor = bgcontrol.coverColor;
//			this.resourceURL = bgcontrol.resourceURL;
//		}
//
//		public void setInfo(SnapsBgControl bgcontrol) {
//			bgcontrol.srcTarget = this.target;
//			bgcontrol.bgColor = this.bgColor;
//			bgcontrol.coverColor = this.coverColor;
//			bgcontrol.resourceURL = this.resourceURL;
//		}
//	}
//
//	/**
//	 *
//	 * Cover 템플릿 변경하기.
//	 */
//	private void changeCoverTemplate() {
//
//		for (SnapsPage page : _template.getPages()) {
//			SnapsBgControl bg = (SnapsBgControl) page.getBgList().get(0);
//
//			if (page.type.equalsIgnoreCase("cover")) {
//				page.thumbImg = false;
//				page.changeBg(bgTemp);
//			}
//
//			bg.coverColor = bgTemp.coverColor;
//		}
//
//		// 배경 색상 바꾸자.
//		for (Fragment fragment : _canvasList) {
//			SnapsCanvasFragment canvas = (SnapsCanvasFragment) fragment;
//
//			if (canvas != null)
//				canvas.reLoadView();
//		}
//
//		if (++bgCount >= xmlBgCover.bgList.size()) {
//			bgCount = 0;
//		}
//	}
//
//	public void progressUnload() {
//		SnapsTimerProgressView.destroyProgressView();
//	}
//
//	int kind = 0;
//
//	/**
//	 *
//	 * 상품 템플릿 데이터 구성
//	 */
//	private void getTemplateHandler() {
//
//		if (Config.isSnapsSticker()) {
//			if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_6)) {
//				_url = Const_VALUE.PATH_PACKAGE(this, false) + Const_Template.FILEPATH_STICKET6_TEMPLATE;
//				kind = 6;
//			} else if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_2)) {
//				_url = Const_VALUE.PATH_PACKAGE(this, false) + Const_Template.FILEPATH_STICKET2_TEMPLATE;
//				kind = 2;
//			} else if (Config.getTMPL_CODE().equals(Config.TEMPLATE_STICKER_1)) {
//				_url = Const_VALUE.PATH_PACKAGE(this, false) + Const_Template.FILEPATH_STICKET1_TEMPLATE;
//				kind = 1;
//			}
//		}
//
//		if (!_url.equalsIgnoreCase("") || IS_EDIT_MODE) {
//			ATask.executeVoid(new ATask.OnTask() {
//				@Override
//				public void onPre() {
//					pageProgress.show();
//				}
//
//				@Override
//				public void onBG() {
//					// 웹 템플릿 가져오기.
//					// 템플릿 버젼 체크 및 다운로드..
//					TemplateUtil util = new TemplateUtil();
//					util.downloadTemplete(EditActivity.this, Config.getPROD_CODE(), kind);
//
//					// 로컬 템플릿 가져오기.
//					if (!IS_EDIT_MODE) {
//						_template = GetTemplateLoad.getFileTemplate(_url, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//						_template.info.F_PAPER_CODE = Config.getPAPER_CODE();
//						_template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
//					} else {
//						_url = SnapsAPI.GET_API_SAVE_XML() + "&prmProjCode=" + Config.getPROJ_CODE();
//						Dlog.d("getTemplateHandler() template url:" + _url);
//						_template = GetTemplateLoad.getThemeBookTemplate(_url, IS_EDIT_MODE, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//						_galleryList = PhotobookCommonUtils.getImageListFromTemplate(_template);
//
//						if(_template != null && _template.info != null) {
//							Config.setPAPER_CODE(_template.info.F_PAPER_CODE);
//							Config.setGLOSSY_TYPE(_template.info.F_GLOSSY_TYPE);
//						}
//					}
//				}
//
//				@Override
//				public void onPost() {
//					if (_template != null) {
//						_pageList = new ArrayList<SnapsPage>();
//
//						SnapsBgControl bg = (SnapsBgControl) _template.getPages().get(0).getBgList().get(0);
//						String coverColor = bg.coverColor;
//
//						Config.setUSER_COVER_COLOR(coverColor);
//						for (SnapsPage page : _template.getPages()) {
//							if (Config.isSnapsSticker()) {
//								((SnapsBgControl) page.getBgList().get(0)).coverColor = coverColor;
//							}
//
//							if (!page.type.equalsIgnoreCase("hidden"))
//								_pageList.add(page);
//						}
//
//						loadPager();
//
//						if (mSnapsHandler != null)
//							mSnapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_DISMISS_PROGRESS, 5000);
//
//						_template.clientInfo.screendpi = String.valueOf(getResources().getDisplayMetrics().densityDpi);
//						_template.clientInfo.screenresolution = SystemUtil.getScreenResolution(EditActivity.this);
//
//						initSnapsOrderManager();
//
//						startStickerKitBgRandomToast();
//					} else {
//						progressUnload();
//						finish();
//
//						Toast.makeText(EditActivity.this, R.string.loading_fail, Toast.LENGTH_SHORT).show();
//						SnapsOrderManager.setSnapsOrderStatePauseCode(getResources().getString(R.string.loading_fail));
//					}
//				}
//			});
//		} else {
//			finish();
//		}
//	}
//
//	private void startStickerKitBgRandomToast() {
//		if (!Config.isSnapsSticker()) return;
//		stickerKitBgRandom = MessageUtil.makeToast(EditActivity.this, R.string.sticker_kit_bg_random_toast_msg);
//
//		if (mSnapsHandler != null)
//			mSnapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_SHOW_STICKER_KIT_BG_RANDOM_TOAST, 2000); //뷰페이저가 로딩 될 때까지 시간을 벌어준다
//	}
//
//	private void showStickerKitBgRandomToast() {
//		if (isStickerKitBgRandomToastCanceled || ++stickerKitBgRandomToastShowCount > 4) return;
//
//		if (stickerKitBgRandom != null) {
//			stickerKitBgRandom.show();
//		}
//
//		if (mSnapsHandler != null)
//			mSnapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_SHOW_STICKER_KIT_BG_RANDOM_TOAST, 1000);
//	}
//
//	private void initSnapsOrderManager() {
//		try {
//			SnapsOrderManager.initialize(this);
//
//            SnapsOrderManager.setImageUploadStateListener(this);
//			SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//			SnapsAssert.assertException(this, e);
//		}
//	}
//
//	/**
//	 *
//	 * SnapsPage 재정렬.
//	 */
//	private final static Comparator<SnapsPage> myComparator = new Comparator<SnapsPage>() {
//		private final Collator _collator = Collator.getInstance();
//
//		@Override
//		public int compare(SnapsPage p, SnapsPage n) {
//
//			return _collator.compare(n.type, p.type);
//		}
//	};
//
//	/**
//	 *
//	 * 페이지 로드
//	 */
//	private void loadPager() {
//		_loadPager.loadPage(_pageList, _canvasList, _topArea.getMeasuredHeight(), _bottomArea.getMeasuredHeight(), 5);
//	}
//
//	@Override
//	public ArrayList<MyPhotoSelectImageData> getUploadImageList() {
//		return _galleryList;//PhotobookCommonUtils.getImageListFromTemplate(_template);
//	}
//
//	@Override
//	public SnapsOrderAttribute getSnapsOrderAttribute() {
//		return new SnapsOrderAttribute.Builder()
//				.setActivity(this)
//				.setEditMode(IS_EDIT_MODE)
//				.setHiddenPageList(getHiddenPageList())
//				.setImageList(PhotobookCommonUtils.getImageListFromTemplate(_template))
//				.setPageList(_pageList)
//				.setPagerController(_loadPager)
//				.setSnapsTemplate(_template)
//				.setBackPageList(getBackPageList())
//				.setCanvasList(_canvasList)
//				.setTextOptions(getTextOptions())
//				.create();
//	}
//
//	@Override
//	public SnapsTemplate getTemplate() {
//		return _template;
//	}
//
//	@Override
//	public void requestMakeMainPageThumbnailFile(ISnapsCaptureListener captureListener) {
//		if (!SnapsOrderManager.isUploadingProject()) {
//			return;
//		}
//
//		setSnapsPageCaptureListener(captureListener);
//
//		if (_canvasList == null || _canvasList.isEmpty()) return;
//
//		int currentPosition = _loadPager.getPagerSelected();
//		if (_pageList.size() <= currentPosition + 2)
//			currentPosition -= 2;
//		else
//			currentPosition += 2;
//
//		SnapsCanvasFragment captureFragment = (SnapsCanvasFragment) _canvasList.get(currentPosition);
//
//		if(captureFragment != null) {
//			captureFragment.getArguments().clear();
//			captureFragment.getArguments().putInt("index", 1);
//			captureFragment.getArguments().putBoolean("pageSave", true);
//			captureFragment.getArguments().putBoolean("preThumbnail", true);
//			captureFragment.getArguments().putBoolean("pageLoad", false);
//			if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) {
//				// 현재 Destory 상태이면 멈추고 index 값을 줄인다.
//				SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_CAPTURE);
//			} else {
//				captureFragment.makeSnapsCanvas();
//			}
//		}
//	}
//
//	@Override
//	public void onOrgImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData) {}
//
//	@Override
//	public void onThumbImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData) {}
//
//	@Override
//	public void onOrderStateChanged(int state) {}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (SnapsUploadFailedImageDataCollector.isShowingUploadFailPopup()) return false;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	public void onUploadFailedOrgImgWhenSaveToBasket() {
//		SnapsUploadFailedImagePopupAttribute popupAttribute = SnapsUploadFailedImagePopup.createUploadFailedImagePopupAttribute(this, Config.getPROJ_CODE(), true);
//
//		SnapsUploadFailedImageDataCollector.showUploadFailedOrgImageListPopup(popupAttribute , new SnapsUploadFailedImagePopup.SnapsUploadFailedImagePopupListener() {
//			@Override
//			public void onShowUploadFailedImagePopup() {}
//
//			@Override
//			public void onSelectedUploadFailedImage(List<MyPhotoSelectImageData> uploadFailedImageList) {
//				PhotobookCommonUtils.setUploadFailedIconVisibleStateToShow(_template);
//
//				try {
//					((SnapsPagerController2) _loadPager).pageAdapter.notifyDataSetChanged();
//				} catch (Exception e) { Dlog.e(TAG, e); }
//			}
//		});
//	}
//
//	@Override
//	public Activity getActivity() {
//		return this;
//	}
//
//	private static final int HANDLE_MSG_DISMISS_PROGRESS = 1;
//	private static final int HANDLE_MSG_SHOW_STICKER_KIT_BG_RANDOM_TOAST = 2;
//
//	@Override
//	public void handleMessage(Message msg) {
//		switch (msg.what) {
//			case HANDLE_MSG_DISMISS_PROGRESS :
//				if (pageProgress != null && pageProgress.isShowing()) //간혹, 프로그레스가 알 수 없는 이유로 중지 되지 않아서 그냥 중단 시켜 버림.
//					pageProgress.dismiss();
//				break;
//			case HANDLE_MSG_SHOW_STICKER_KIT_BG_RANDOM_TOAST:
//				if (isStickerKitBgRandomToastCanceled) {
//					if (stickerKitBgRandom != null) {
//						stickerKitBgRandom.cancel();
//					}
//				} else
//					showStickerKitBgRandomToast();
//				break;
//		}
//	}
}
