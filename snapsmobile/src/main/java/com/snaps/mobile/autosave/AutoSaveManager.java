package com.snaps.mobile.autosave;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.snaps_image_proccesor.image_coordinate_processor.ImageCoordinateCalculator;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsCalendarRecoverPage;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;

import java.io.File;
import java.util.ArrayList;

import errorhandle.SnapsAssert;
import errorhandle.logger.Logg;

/**
 * 자동 저장 관련
 */
public class AutoSaveManager implements IAutoSaveActions, IAutoSaveExportProcessor, IAutoSaveConstants {
    private static final String TAG = AutoSaveManager.class.getSimpleName();
    private volatile static AutoSaveManager gInstance = null;

    private Context context = null;

    //파일 저장, 삭제 등 실질적인 작업을 수행
    private AutoSaveFileProcess fileProcessor = null;

    //제품 정보를 가지고 있음.
    private AutoSaveProjectInfo projectInfo = null;

    //핵심 오브젝트
    private SnapsTemplate snapsTemplate = null;

    //달력용..(달력은 template이 워낙에 커서, layoutControl만 저장 함.)
    private SnapsCalendarRecoverPage objOnlyLayoutControl = null;

    //무슨 상품을 편집 중인지..
    private int currentProductType = 0;

    private boolean isActiveAutoSaving = false;

    private boolean isRecoveryMode = false;

    private boolean isMissingImgFile = false;

    private boolean isExportCalendarTemplate = false;

    private long lastAutoSaveTime = 0l;

    public static void createInstance(Context context) {
        synchronized (AutoSaveManager.class) {
            gInstance = new AutoSaveManager(context);
            gInstance.context = context;
        }
    }

    public static AutoSaveManager getInstance() {
        return gInstance;
    }

    public static void finalizeInstance() {
        if (gInstance != null) {
            gInstance.fileProcessor = null;
            gInstance.projectInfo = null;
            gInstance.snapsTemplate = null;
            gInstance.objOnlyLayoutControl = null;
            gInstance = null;
        }
    }

    public static void checkAutoSavedFiles() {
        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan == null) {
            return;
        }

        if (saveMan.isExistSavedFiles()) {
            try {
                saveMan.recovery();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    private AutoSaveManager() {
    }

    private AutoSaveManager(Context context) {
        init(context);
    }

    private void init(Context context) {
        fileProcessor = new AutoSaveFileProcess(context);
    }

//	public static void sendAutoSaveFilesToSnapsServer(final Activity activity, final String userId) throws Exception {
//		ATask.executeVoidWithThreadPool(new OnTask() {
//			@Override
//			public void onPre() {}
//
//			@Override
//			public void onBG() {
//				try {
//					GetMultiPartMethod.sendAutoSaveFileWithUserId(userId);
//				} catch (Exception e) {
//					Dlog.e(TAG, e);
//				}
//			}
//
//			@Override
//			public void onPost() {}
//		});
//	}

    public static void exportAutoSaveTemplateFile(SnapsTemplate template) throws Exception {
        if (!isSupportProductAutoSave()) {
            return;
        }
        AutoSaveManager saveManager = AutoSaveManager.getInstance();
        if (saveManager != null && !saveManager.isRecoveryMode()) {
            saveManager.exportTemplate(template);
        }
    }

    public static boolean isSupportProductAutoSave() {
        return !Config.isSnapsDiary()
                && !SnapsDiaryDataManager.isAliveSnapsDiaryService()
                && !Config.isSNSBook()
                && !Config.isSnapsSticker()
                && !Config.isIdentifyPhotoPrint()
                && !Config.isSmartSnapsRecommendLayoutPhotoBook()
                && !Config.isCalendar();
    }

    public boolean isExistSavedFiles() {
        if (fileProcessor == null) {
            return false;
        }
        return fileProcessor.checkAutoSavedFilesExists();
    }

    @Override
    public void startAutoSave(int productType) {
        isActiveAutoSaving = true;
        currentProductType = productType;
    }

    @Override
    public void finishAutoSaveMode() {
        isActiveAutoSaving = false;
        isRecoveryMode = false;
        currentProductType = 0;
        delete();
    }

    @Override
    public void continueAutoSave() {
        isActiveAutoSaving = true;
        if (projectInfo != null) {
            currentProductType = projectInfo.getProductType();
        }
    }

    @Override
    public void recovery() throws Exception {
        if (fileProcessor == null) {
            return;
        }

        ATask.executeVoidWithThreadPool(new OnTask() {

            boolean isCalendar = false;

            @Override
            public void onPre() {
            }

            @Override
            public void onPost() {
                //파일 손상..
                if (projectInfo == null
                        || !projectInfo.isValidSaveInfo()
                        || snapsTemplate == null
                        || snapsTemplate.getPages() == null
                        || snapsTemplate.getPages().isEmpty()
                        || (isCalendar && objOnlyLayoutControl == null)) {
                    Dlog.w(TAG, "recovery() failed load autosave file");
                    finishAutoSaveMode();
                    return;
                }

                if (isCalendar) {
                    try {
                        mergeCalendarTemplate();

                        recoveryCalendarSummary(projectInfo);
                    } catch (Exception e) {
                        finishAutoSaveMode();
                        return;
                    }
                }

                snapsTemplate.myphotoImageList = PhotobookCommonUtils.getImageListFromTemplate(snapsTemplate);

                //2020.04.03 ben : 포토북 편집 화면에서 미리보기 버튼(삼각형) 버튼을 클릭하면 포토북이 보이지 않는다.
                //이유는 SnapsTemplateManager에 템플릿이 설정되지 않아서..
                //일단 자동 저장 파일을 복구했을때 SnapsTemplateManager에 값을 설정되지 않아서 뭔가 문제가 생기는 것 같아서 추가 했는데, 추가하는게 더 문제가 되려나....
                SnapsTemplateManager.getInstance().setSnapsTemplate(snapsTemplate);

                recoveryActivity();
            }

            /**
             * 달력은 템플릿 용량이 너무 커서, layoutControl 만 저장하고 복구할때,
             * 템플릿과 합쳐서 복구한다.
             */
            private void mergeCalendarTemplate() throws Exception {

                ArrayList<SnapsControl> layoutControls = objOnlyLayoutControl.getLayouts();
                ArrayList<SnapsPage> pageList = snapsTemplate.getPages();
                ArrayList<String> tumbnailPaths = objOnlyLayoutControl.getThumbnailPaths();

                if (pageList == null || pageList.isEmpty()) {
                    return;
                }
                for (int ii = 0; ii < pageList.size(); ii++) {
                    SnapsPage page = pageList.get(ii);
                    if (page == null || tumbnailPaths.size() <= ii) {
                        continue;
                    }
                    page.thumbnailPath = tumbnailPaths.get(ii);
                }

                boolean isFind = false;
                for (SnapsControl control : layoutControls) {
                    isFind = false;
                    for (SnapsPage page : pageList) {
                        if (page == null) {
                            continue;
                        }
                        ArrayList<SnapsControl> controls = page.getLayoutList();
                        for (int ii = controls.size() - 1; ii >= 0; ii--) {
                            SnapsControl c = controls.get(ii);
                            if (c != null && c instanceof SnapsLayoutControl) {
                                SnapsLayoutControl layoutControl = (SnapsLayoutControl) c;
                                SnapsLayoutControl layoutControlOrg = (SnapsLayoutControl) control;

                                if (layoutControl.getLayoutControlIdx() > -1
                                        && layoutControlOrg.getLayoutControlIdx() == layoutControl.getLayoutControlIdx()) {
                                    controls.remove(layoutControl);
                                    controls.add(ii, layoutControlOrg);
                                    isFind = true;
                                    break;
                                }
                            }
                        }
                        if (isFind) {
                            break;
                        }
                    }
                }
            }

            @Override
            public void onBG() {
                File fileProjectInfo = new File(fileProcessor.getFilePath(FILE_TYPE_INFO));
                Object objProjectInfo = fileProcessor.getObjectFromFile(fileProjectInfo);
                if (objProjectInfo != null && objProjectInfo instanceof AutoSaveProjectInfo) {
                    projectInfo = (AutoSaveProjectInfo) objProjectInfo;
                }

                if (projectInfo == null || !projectInfo.isValidSaveInfo()) {
                    return;
                }

                try {
                    File fileTemplate = new File(fileProcessor.getFilePath(FILE_TYPE_TEMPLATE));
                    Object objTemplate = fileProcessor.getObjectFromFile(fileTemplate);
                    if (objTemplate != null && objTemplate instanceof SnapsTemplate) {
                        snapsTemplate = (SnapsTemplate) objTemplate;
                    }

                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }

                if (projectInfo != null
                        && (projectInfo.getProductType() == PRODUCT_TYPE_CALENDAR || projectInfo.getProductType() == PRODUCT_TYPE_WOOD_BLOCK_CALENDAR)) {
                    isCalendar = true;
                    File fileControls = new File(fileProcessor.getFilePath(FILE_TYPE_LAYOUT_CONTROLS));
                    Object objControls = fileProcessor.getObjectFromFile(fileControls);
                    if (objControls != null && objControls instanceof SnapsCalendarRecoverPage) {
                        objOnlyLayoutControl = (SnapsCalendarRecoverPage) objControls;
                    }
                }
            }
        });
    }

    @Override
    public void delete() {
        if (fileProcessor == null) {
            return;
        }
        fileProcessor.deleteAllFiles();
    }

    @Override
    public void exportTemplate(SnapsTemplate template) {
        if (fileProcessor == null || !isActiveAutoSaving() || !isAllowAutoSaveTerm()) {
            return;
        }
        lastAutoSaveTime = System.currentTimeMillis();

        try {
            if (Config.isCalendar()) {
                setLayoutControlsIdx(template);
            }

            fileProcessor.exportTemplate(template);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private boolean isAllowAutoSaveTerm() { //부하를 방지하기 위해 너무 자주 자동 저장을 실시 하지 않는다
        long allowTerm = Build.VERSION.SDK_INT >= 22 ? 30000 : 60000;
        return System.currentTimeMillis() - lastAutoSaveTime > allowTerm;
    }

    @Override
    public void exportProjectInfo(AutoSaveProjectInfo info) {
        if (fileProcessor == null || !isActiveAutoSaving()) {
            return;
        }
        fileProcessor.exportProjectInfo(info);
    }

    @Override
    public void exportLayoutControls(ArrayList<SnapsPage> pages,
                                     ArrayList<String> thumbnailPaths,
                                     int lastPageIdx) {
        if (fileProcessor == null || !isActiveAutoSaving() || pages == null || pages.isEmpty()) {
            return;
        }

        fileProcessor.exportLayoutControls(pages, thumbnailPaths, lastPageIdx);
    }

    private void setConfigInfo(AutoSaveProjectInfo info) {
        if (info == null) {
            return;
        }

        Config.setPROJ_CODE(info.getProjCode());
        Config.setPROD_CODE(info.getProdCode());
        Config.setPAPER_CODE(info.getPaperCode());
        Config.setFRAME_TYPE(info.getFrameType());
        Config.setFRAME_ID(info.getFrameId());
        Config.setNOTE_PAPER_CODE(info.getNotePaperCode());
        Config.setGLOSSY_TYPE(info.getGlossyType());
        Config.setPROJ_NAME(info.getProjName());
        Config.setTMPL_CODE(info.getTmplCode());
        Config.setTMPL_COVER(info.getTmplCover());
        Config.setTMPL_COVER_TITLE(info.getTmplCoverTitle());
        Config.setUSER_COVER_COLOR(info.getUserCoverColor());
        Config.setCARD_QUANTITY(info.getCardQuantity());
        Config.setDesignId(info.getDESIGN_ID());
        Config.setFromCart(info.isFromCart());
        Config.setBACK_TYPE(info.getBackType());
    }

    private boolean isEndlessTryRecovery() {
        int tryCount = Setting.getInt(context, SETTING_KEY_AUTO_SAVE_RECOVERY_TRY_COUNT);
        return tryCount > (Config.isDevelopVersion() ? 999 : 3);
    }

    private void recoveryActivity() {
        if (context == null || projectInfo == null) {
            return;
        }

        if (isEndlessTryRecovery()) {
            delete();
            Setting.set(context, SETTING_KEY_AUTO_SAVE_RECOVERY_TRY_COUNT, 0);
            return;
        }

        int tryCount = Setting.getInt(context, SETTING_KEY_AUTO_SAVE_RECOVERY_TRY_COUNT);
        Setting.set(context, SETTING_KEY_AUTO_SAVE_RECOVERY_TRY_COUNT, tryCount + 1);

        setConfigInfo(projectInfo);

        setRecoveryMode(true);

        continueAutoSave();

        Intent intent = new Intent(context, SnapsEditActivity.class);
        switch (projectInfo.getProductType()) {
            case PRODUCT_TYPE_SIMPLE_BOOKS:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SIMPLE_PHOTO_BOOK.ordinal());
                context.startActivity(intent);
                break;

            case PRODUCT_TYPE_KT_BOOK:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.KT_Book.ordinal());
                context.startActivity(intent);
                break;

            //달력류는 XML 구조상  write/read가 느려서 자동 저장을 막았다.
//		case PRODUCT_TYPE_CALENDAR:
//			intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.CALENDAR.ordinal());
//			context.startActivity(intent);
//			break;
//			case PRODUCT_TYPE_WOOD_BLOCK_CALENDAR:
//				intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.WOODBLOCK_CALENDAR.ordinal());
//				context.startActivity(intent);
//				break;
            case PRODUCT_TYPE_FRAME:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.FRAME.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_PACKAGE_KIT:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.PACKAGE_KIT.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_SIMPLE_MAKING_BOOK:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SIMPLE_MAKING_BOOK.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_CARD:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.CARD.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_PHOTO_CARD:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.PHOTO_CARD.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_WALLET_PHOTO:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.WALLET_PHOTO.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_IDENTIFY_PHOTO:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.IDENTIFY_PHOTO.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_NEWYEARS_CARD:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.NEW_YEARS_CARD.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_STICKER:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.STICKER.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_ACCORDION_CARD:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.ACCORDION_CARD.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_POSTER:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.POSTER.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_SLOGAN:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SLOGAN.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_BABY_NANE_STICKER:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.BABY_NAME_STICKER.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_MINI_BANNER:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.MINI_BANNER.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_TRANSPARENCY_PHOTO_CARD:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.TRANSPARENCY_PHOTO_CARD.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_DIY_STICKER:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.DIY_STICKER.ordinal());
                context.startActivity(intent);
                break;
            case PRODUCT_TYPE_SMART_TALK:
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SMART_TALK.ordinal());
                context.startActivity(intent);
                break;
            default:
                SnapsAssert.assertTrue(false);
                finishAutoSaveMode();
                break;
        }
    }

    public void exportProjectInfo() {
        if (!isActiveAutoSaving() || isRecoveryMode()) {
            return;
        }
        projectInfo = new AutoSaveProjectInfo();
        projectInfo.setProductType(currentProductType);
        projectInfo.setConfigInfo();

        if (Config.isCalendar()) {
            setCalendarSummary(projectInfo);
        }

        exportProjectInfo(projectInfo);
    }

    private void setCalendarSummary(AutoSaveProjectInfo info) {
        if (info == null) {
            return;
        }

        info.setnOldYear(GetTemplateXMLHandler.getStartYear());
        info.setnOldMonth(GetTemplateXMLHandler.getStartMonth());

        info.setSummaryTaget(GetTemplateXMLHandler.getSummaryTarget());
        info.setSummaryWidth(GetTemplateXMLHandler.getSummaryWidth());
        info.setSummaryHeight(GetTemplateXMLHandler.getSummaryHeight());

        info.setSummaryLayer(GetTemplateXMLHandler.getSummaryLayer());
    }

    private void recoveryCalendarSummary(AutoSaveProjectInfo info) {
        if (info == null) {
            return;
        }

        GetTemplateXMLHandler.setStartYear(info.getnOldYear());
        GetTemplateXMLHandler.setStartMonth(info.getnOldMonth());

        GetTemplateXMLHandler.summaryTaget = info.getSummaryTaget();
        GetTemplateXMLHandler.summaryWidth = info.getSummaryWidth();
        GetTemplateXMLHandler.summaryHeight = info.getSummaryHeight();

        GetTemplateXMLHandler.summaryLayer = info.getSummaryLayer();
    }

    public void checkMissingImageFile(boolean isCartProduct) {
        try {
            SnapsTemplate template = getSnapsTemplate();
            ArrayList<SnapsPage> pageList = template.getPages();
            PhotobookCommonUtils.getImageListFromTemplate(template);

            if (pageList == null || pageList.isEmpty()) {
                return;
            }

            for (SnapsPage page : pageList) {
                if (page == null) {
                    continue;
                }
                ArrayList<SnapsControl> controls = page.getLayoutList();

                if (controls == null) {
                    continue;
                }

                for (SnapsControl c : controls) {
                    if (c != null && c instanceof SnapsLayoutControl) {
                        SnapsLayoutControl layoutControl = (SnapsLayoutControl) c;
                        MyPhotoSelectImageData data = layoutControl.imgData;

                        if (data == null || data.PATH == null || data.PATH.length() < 1) {
                            continue;
                        }

                        if (Config.isSNSPhoto(data.KIND)) {
                            Rect rect = HttpUtil.getNetworkImageRect(data.PATH);
                            if (rect == null || rect.width() < 1 || rect.height() < 1) {
                                setMissingImgFile(true);
                            }
                        } else if (data.KIND == Const_VALUES.SELECT_PHONE) {
                            File localFile = new File(data.PATH);
                            if (localFile.exists()) {
                                //이미지의 비율이 맞지 않는 이미지 데이터를 삭제 시켜 버린다.
                                String[] imageRc = layoutControl.getRc().replace(" ", "|").split("\\|");
                                String[] imageRcClip = layoutControl.getRcClip().replace(" ", "|").split("\\|");

                                try {
                                    //화면에 보여지지 않은 부분은 계산이 되어 있지 않기 때문에 계산을 한 번 해 준다.
                                    float rectW = Float.parseFloat(imageRc[2]);
                                    float rectH = Float.parseFloat(imageRc[3]);
                                    if (rectW == 0 || rectH == 0) {
                                        ImageCoordinateCalculator.setLayoutControlCoordinateInfo(context, layoutControl);
                                        imageRc = layoutControl.getRc().replace(" ", "|").split("\\|");
                                    }
                                } catch (Exception e) {
                                    Dlog.e(TAG, e);
                                }

                                boolean isWrongRatio = false;

                                imageRc = BitmapUtil.checkImageRatio(data, imageRc, imageRcClip);

                                if (imageRc != null && imageRc.length >= 4) {
                                    try {
                                        float rectW = Float.parseFloat(imageRc[2]);
                                        float rectH = Float.parseFloat(imageRc[3]);
                                        float imgW = Float.parseFloat(data.F_IMG_WIDTH);
                                        float imgH = Float.parseFloat(data.F_IMG_HEIGHT);
                                        isWrongRatio = (imgW > imgH && rectW < rectH) || (imgW < imgH && rectW > rectH);
                                        if (!isWrongRatio) {
                                            if ((imageRc[2] != null && imageRc[2].trim().equals("0"))
                                                    || (imageRc[3] != null && imageRc[3].trim().equals("0"))) {
                                                isWrongRatio = true;
                                            }
                                        } else {
                                        }
                                    } catch (Exception e) {
                                        Dlog.e(TAG, e);
                                        isWrongRatio = true;
                                    }
                                } else {
                                    isWrongRatio = true;
                                }

                                if (isWrongRatio) {
                                    Dlog.w(TAG, "checkMissingImageFile() isWrongRatio:" + isWrongRatio);
                                    layoutControl.imgData = null;
                                    layoutControl.srcTargetType = Const_VALUE.USERIMAGE_TYPE;

                                    layoutControl.srcTarget = "";
                                    layoutControl.resourceURL = "";
                                    setMissingImgFile(true);
                                }
                            } else {
                                if (isCartProduct) {
                                    Rect rect = HttpUtil.getNetworkImageRect(SnapsAPI.DOMAIN(false) + data.ORIGINAL_PATH);
                                    if (rect != null && rect.width() > 0 && rect.height() > 0) {
                                        return;
                                    }
                                }

                                layoutControl.imgData = null;
                                layoutControl.srcTargetType = Const_VALUE.USERIMAGE_TYPE;

                                layoutControl.srcTarget = "";
                                layoutControl.resourceURL = "";

                                setMissingImgFile(true);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void setLayoutControlsIdx(SnapsTemplate snapsTemplate) {
        if (snapsTemplate == null || snapsTemplate.getPages() == null) {
            return;
        }
        try {
            ArrayList<SnapsPage> pageList = snapsTemplate.getPages();
            int idx = 0;
            for (SnapsPage page : pageList) {
                if (page == null) {
                    continue;
                }
                ArrayList<SnapsControl> controls = page.getLayoutList();

                if (controls == null) {
                    continue;
                }

                for (SnapsControl c : controls) {
                    if (c != null && c instanceof SnapsLayoutControl) {
                        SnapsLayoutControl layoutControl = (SnapsLayoutControl) c;
                        layoutControl.setLayoutControlIdx(idx++);
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public AutoSaveProjectInfo getProjectInfo() {
        return projectInfo;
    }

    public SnapsTemplate getSnapsTemplate() {
        return snapsTemplate;
    }

    public boolean isActiveAutoSaving() {
        return isActiveAutoSaving;
    }

    public void setActiveAutoSaving(boolean isActiveAutoSaving) {
        this.isActiveAutoSaving = isActiveAutoSaving;
    }

    public static boolean isAutoSaveRecoveryMode() {
        return getInstance() != null && getInstance().isRecoveryMode;
    }

    public boolean isRecoveryMode() {
        return isRecoveryMode;
    }

    public void setRecoveryMode(boolean isRecoveryMode) {
        this.isRecoveryMode = isRecoveryMode;
    }

    public String getFilePath(byte TYPE) {
        if (fileProcessor == null) {
            return null;
        }
        return fileProcessor.getFilePath(TYPE, true);
    }

    public String getFilePath(byte TYPE, boolean realFile) {
        if (fileProcessor == null) {
            return null;
        }
        return fileProcessor.getFilePath(TYPE, realFile);
    }

    public int getCalendarLastIdx() {
        if (objOnlyLayoutControl == null) {
            return 0;
        }
        return objOnlyLayoutControl.getLastPageIdx();
    }

    public boolean isMissingImgFile() {
        return isMissingImgFile;
    }

    public void setMissingImgFile(boolean isMissingImgFile) {
        this.isMissingImgFile = isMissingImgFile;
    }

    public boolean isExportCalendarTemplate() {
        return isExportCalendarTemplate;
    }

    public void setExportCalendarTemplate(boolean isExportCalendarTemplate) {
        this.isExportCalendarTemplate = isExportCalendarTemplate;
    }
}
