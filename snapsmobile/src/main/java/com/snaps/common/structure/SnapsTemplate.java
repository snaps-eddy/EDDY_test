package com.snaps.common.structure;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplateInfo.COVER_TYPE;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsSceneMaskControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.control.TextFormat;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.snaps.common.utils.constant.Const_VALUES.QRCODE_TYPE;

/**
 * com.snaps.kakao.structure SnapsTemplate.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 27.
 * @Version :
 */
public class SnapsTemplate implements Parcelable, Serializable {
    private static final String TAG = SnapsTemplate.class.getSimpleName();
    /**
     *
     */
    private static final long serialVersionUID = -8458522409238388456L;

    public final static float a5PerA6 = 1.40f;
    public String version = "";
    public String type = "";

    private ArrayList<SnapsPage> pageList = new ArrayList<SnapsPage>();
    private ArrayList<SnapsPage> dynamicPageList = new ArrayList<SnapsPage>();

    public ArrayList<SnapsTemplatePrice> priceList = new ArrayList<SnapsTemplatePrice>();
    public ArrayList<SnapsPage> _backPageList = new ArrayList<SnapsPage>();
    public ArrayList<SnapsPage> _hiddenPageList = new ArrayList<SnapsPage>();
    public ArrayList<SnapsDelImage> delimgList = new ArrayList<SnapsDelImage>();
    public SnapsClientInfo clientInfo = new SnapsClientInfo();
    public SnapsTemplateInfo info = new SnapsTemplateInfo();
    public SnapsSaveInfo saveInfo = new SnapsSaveInfo();
    public ArrayList<MyPhotoSelectImageData> myphotoImageList = new ArrayList<MyPhotoSelectImageData>();
    public ArrayList<SnapsPage> clonePageList = new ArrayList<SnapsPage>();
    // 사용되는 폰트 정보를 저장하기 위한 set
    public HashSet<String> fonts = new HashSet<String>();
    public List<String> arrFonts = new ArrayList<String>();

    private SnapsProductOption snapsProductOption = new SnapsProductOption();

    // 제품 편집 완료 여부...
    private String F_PRO_YORN = "Y";
    private int F_ADD_PAGE = 0; // 추가페이지 설정..

    // 페이지 아이디
    private int pageID = 0;

    // 카카오스토리 북 책등 폰트 사이즈
    public String SNS_BOOK_STICK = "14";
    public String SNS_BOOK_STICK_COLOR = "FFFFFFFF";

    private SnapsTextControl snsBookStick = null;

    public int getPageID() {
        return pageID++;
    }

    public SnapsTemplate() {

    }

    public int getF_ADD_PAGE() {
        return F_ADD_PAGE;
    }

    public void setF_ADD_PAGE(int f_ADD_PAGE) {
        F_ADD_PAGE = f_ADD_PAGE;
    }

    public String getF_PRO_YORN() {
        return F_PRO_YORN;
    }

    public void setF_PRO_YORN(String f_PRO_YORN) {
        F_PRO_YORN = f_PRO_YORN;
    }

    public SnapsProductOption getProductOption() {
        return snapsProductOption;
    }

    /******************************
     * 테마북에서 사용하는 함수들...
     ******************************/

    public void setBgClickEnable(int pageIDX, boolean isEnable) {

        if (getPages() != null && getPages().size() > 0) {
            SnapsPage page = getPages().get(pageIDX);
            for (SnapsControl bg : page.getBgList()) {
                bg.isClick = isEnable ? "true" : "false";
            }
        }
    }

    private boolean isShouldAddBookThickness() {
        if (getPages().size() >= info.getSoftCoverAddSpineText() + 2) {
            return true;
        }
        return false;
    }

    private boolean isExistBookThickness() {
        SnapsPage coverPage = getPages().get(0);

        ArrayList<SnapsControl> snapsControlList = coverPage.getControlList();
        int size = snapsControlList.size();
        for (int i = 0; i < size; ++i) {
            SnapsControl control = snapsControlList.get(i);
            if (control instanceof SnapsTextControl) {
                if (((SnapsTextControl) control).format.verticalView.equalsIgnoreCase("true")) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setApplyMaxPage() {
        // 커버 타입이 없으면 패스..
        if (info.getCoverType() == COVER_TYPE.NONE_COVER)
            return;

        // 하드 커버인 경우..
        if (info.getCoverType() == COVER_TYPE.HARD_COVER) {

            // 책등이미지를 추가한다.
            addSpineImage(0);

            // 경우에만 맥스 페이지 적용을 한다.

            float computeWidth = computeMaxPageAdditionWidth();
            applyMaxPage(0, computeWidth, computeWidth);
            return;
        }

        // 책등이 늘어나야 하는 넓이 값을 구한다. px
        float computeWidth = computeMaxPageAdditionWidth();

        // 소프트 커버 인경우.. 문제 발생...
        float[] computeWidthControl = computeMaxPageAdditionWidthSoftCover();

        if (isShouldAddBookThickness()) {
            if (!isExistBookThickness()) {
                Dlog.d("setApplyMaxPage() addBookThicknessText");
                addBookThicknessText(computeWidth);
            }
        } else {
            if (isExistBookThickness()) {
                //KT 북 - 이전 코드 문제로 책등이 사라지는 문제가 있어서 KT 북인 경우 책등 제거하는 동작을 하지 않는다.
                if (!Config.isKTBook()) {
                    Dlog.d("setApplyMaxPage() deleteBookThicknessText");
                    deleteBookThicknessText();
                }
            }
        }

        // 커버만 max page를 적용한다.
        applyMaxPage(computeWidthControl[0], computeWidthControl[1], computeWidth);
    }

    public float getHardCoverSpineWidth() {
        // 기본 책등 넓이를 빼준다.
        return info.maxPageInfo.getHardCoverSpineMMSize(info.F_PAPER_CODE, getPages().size() - 2) - SnapsTemplateInfo.HARDCOVER_SPINE_WIDTH;
    }

    /***
     * 추가될 책등의 넓이를 구한다.
     *
     * @return
     */
    float computeMaxPageAdditionWidth() {

        if (info.getCoverType() == COVER_TYPE.HARD_COVER) {
            info.mmMidWidth = info.maxPageInfo.getHardCoverSpineMMSize(info.F_PAPER_CODE, getPages().size() - 2);
            float spinewidth = (info.mmMidWidth - SnapsTemplateInfo.HARDCOVER_SPINE_WIDTH) * info.getPXMM();

            return Math.max(0.f, spinewidth);

        } else if (info.getCoverType() == COVER_TYPE.SOFT_COVER) {
            // 1mm = 2px 296px/148mm
            float pxPerMM = Float.parseFloat(info.F_COVER_XML_WIDTH) / Float.parseFloat(info.F_COVER_MM_WIDTH);
            // 맥스페잊 적용으로 수정..됨..
            int addPageCount = Config.isThemeBook() ? getPages().size() - 16 : getPages().size() - (Integer.parseInt(info.F_BASE_QUANTITY) + 2);

            info.mmMidWidth = info.getPaperThick() * (getPages().size() - 1); // 커버 두께

            float addtionWidth = 0.f;
            if (Config.isThemeBook()) {
                addtionWidth = (addPageCount * info.getPaperThick()) * pxPerMM;
            } else {
                if (getPages().size() - 2 > 75) { // 소트프 커버인경우 75장(151p)부터
                    // maxpage를 적용한다.
                    // 책등 전체 크기에서 - 151p일때 책등크기를 빼기를 해준다.
                    addtionWidth = (info.getPaperThick() * (addPageCount - 65)) * pxPerMM;// ((info.getPaperThick() * (pageList.size() - 1)) - info.getSoftSpineBasemmWidth()) * pxPerMM;

                }
            }

            Dlog.d("computeMaxPageAdditionWidth() addtionWidth:" + addtionWidth);
            // 음수는 적용하지 않는다...
            return Math.max(0.f, addtionWidth);
        } else
            return 0.f;

    }

    /***
     * 소프트 커버에서 컨트롤이 이동되어야할 값을 구하는 함수...
     *
     * @return
     */
    float[] computeMaxPageAdditionWidthSoftCover() {
        float[] addtionWidths = {0.f, 0.f};
        if (info.getCoverType() == COVER_TYPE.SOFT_COVER) {
            // 1mm = 2px 296px/148mm
            float pxPerMM = Float.parseFloat(info.F_COVER_XML_WIDTH) / Float.parseFloat(info.F_COVER_MM_WIDTH);
            // 맥스페잊 적용으로 수정..됨..
            int addPageCount = Config.isThemeBook() ? getPages().size() - 16 : getPages().size() - (Integer.parseInt(info.F_BASE_QUANTITY) + 2);
            float addtionWidth = 0.f;

            if (Config.isThemeBook()) {
                addtionWidth = (addPageCount * info.getPaperThick()) * pxPerMM;
            } else {
                addtionWidth = ((getPages().size() - 2) - 10) * info.getPaperThick() * pxPerMM;

            }

            addtionWidth = Math.max(0.f, addtionWidth);

            if (getPages().size() - 2 > 75) {
                // 151p이후 추가되는 장수는 절반이 아니 전부 옮긴다.
                float add151_401 = ((getPages().size() - 2) - 75) * info.getPaperThick() * pxPerMM;
                // 151일때 좌측 offset값을 넘겨야 한다.
                float add21_151 = (65 * info.getPaperThick() * pxPerMM) / 2.f;
                addtionWidths[0] = -add21_151;
                addtionWidths[1] = add151_401 + add21_151;

            } else {
                float halfAddtionWidth = addtionWidth / 2.f;
                addtionWidths[0] = -halfAddtionWidth;
                addtionWidths[1] = halfAddtionWidth;
            }

        }

        return addtionWidths;

    }

    /**
     * 커버의 크기는 151p부터 늘어나고 컨트롤들은 무조건 페이지가 늘어나면 우측으로 이동을 한다.
     */
    void applyMaxPage(float additionContorlWidth_L, float additionContorlWidth_R, float additionCoverWidth) {
        // 커버페이지를 구한다. 첫번째는 무조건 커버라고 생각하고 가정..
        SnapsPage coverPage = getPages().get(0);

        int coverWidth = coverPage.getOriginWidth();

        // 커버에 추가될 넓이를 설정한다.
        coverPage.setMaxPageX((int) additionCoverWidth);

        // bglayer 늘리기.
        for (SnapsControl control : coverPage.getBgList()) {
            if (control instanceof SnapsBgControl) {
                control.width = coverPage.getWidth() + "";
                control.height = coverPage.height;
            }
        }

        // 이미지들...
        for (SnapsControl control : coverPage.getLayoutList()) {
            if (control instanceof SnapsLayoutControl) {
                if (((SnapsLayoutControl) control).type.equals("local_resource"))
                    continue;

                boolean isEnableMove = true;
                if (coverWidth / 2 > control.getIntX() && coverWidth / 2 > control.getIntWidth()) {
                    isEnableMove = false;
                }

                int moveAmount = (int) (isEnableMove ? additionContorlWidth_R : additionContorlWidth_L);
                control.setMaxPageX(moveAmount);
            }
        }

        boolean isWriteBookThickness = !Const_PRODUCT.isDesignNoteProduct() && !Const_PRODUCT.isSNSBook();

        // 텍스트들... 책등은 제외... 책등기준으로 왼쪽만 이동을 시킨다.
        for (SnapsControl control : coverPage.getControlList()) {
            // 책등기준으로 왼쪽만 이동을 시킨다.
            boolean isEnableMove = true;
            if (coverWidth / 2 > control.getIntX())
                isEnableMove = false;

            if (control instanceof SnapsTextControl) {
                SnapsTextControl snapsTextControl = (SnapsTextControl) control;
                if (snapsTextControl.format.verticalView.equalsIgnoreCase("true")) {
                    // 책등인 경우
                    control.setMaxPageX((int) (additionContorlWidth_L / 2.f));

                    if (isWriteBookThickness) {
                        snapsTextControl.text = Config.getPROJ_NAME();
                    }
                } else {
                    // 책등이 아닌 경우
                    if (snapsTextControl.format.orientation == TextFormat.TEXT_ORIENTAION_HORIZONTAL) {
                        int moveAmount = (int) (isEnableMove ? additionContorlWidth_R : additionContorlWidth_L);
                        control.setMaxPageX(moveAmount);
                    }
                }
            } else if (control instanceof SnapsClipartControl) {
                isEnableMove = coverWidth / 2 <= control.getIntX() + control.getIntWidth() / 2; // 클립아트는 x좌표 기준이 아니라 중앙을 기준으로 정한다.

                int moveAmount = (int) (isEnableMove ? additionContorlWidth_R : additionContorlWidth_L);
                control.setMaxPageX(moveAmount);
            }
        }

        coverPage.setAddmmWidth(Math.max(0.f, additionCoverWidth * info.getMMPX()));

        // 책등이미지를 추가한다.
        addSpineImage(0);
    }

    void deleteBookThicknessText() {
        // 커버페이지를 구한다. 첫번째는 무조건 커버라고 생각하고 가정..
        SnapsPage coverPage = getPages().get(0);

        ArrayList<SnapsControl> snapsControlList = coverPage.getControlList();
        int size = snapsControlList.size();
        for (int i = 0; i < size; ++i) {
            SnapsControl control = snapsControlList.get(i);
            if (control instanceof SnapsTextControl) {
                // 책등인 경우 삭제하기...
                if (((SnapsTextControl) control).format.verticalView.equalsIgnoreCase("true")) {
                    coverPage.deleteControl(control);
                    return;
                }
            }
        }
    }

    /***
     * 책등에 텍스트를 추가하는 함수..
     *
     */
    void addBookThicknessText(float computeWidth) {
        if (Const_PRODUCT.isSNSBook()) {
            addNewKakaoBookThicknessText(computeWidth);
        } else {
            if (!Const_PRODUCT.isDesignNoteProduct())
                addPhotoBookThicknessText(computeWidth);
        }
    }

    public void addPhotoBookThicknessText(float computeWidth) {
        SnapsPage coverPage = getPages().get(0);

        String offsetY = "20";
        String width = "8";
        String height = coverPage.height + "";
        String fontSize = "8";
        String fontFace = "스냅스 윤고딕 330";
        String bold = "0";

        //KT 북
        if (Config.isKTBook()) {
            offsetY = "28";
            width = "5";
            height = "90";
            fontSize = "4";
            fontFace = "스냅스 윤고딕 700 Bold";
            bold = "1";
        }

        SnapsTextControl textControl = new SnapsTextControl();
        // textControl.setX(298 + "");
        textControl.setX(((int) ((coverPage.getOriginWidth()) / 2.f - 8 / 2.f)) + "");
        textControl.y = offsetY;
        textControl.width = width;
        textControl.height = height;
        textControl.format.fontFace = fontFace;

        textControl.format.fontSize = fontSize;
        textControl.format.fontColor = "ff000000";
        textControl.format.baseFontColor = textControl.format.fontColor;
        textControl.format.align = "left";
        textControl.format.bold = bold;
        textControl.format.italic = "0";
        textControl.format.underline = "false";
        textControl.format.verticalView = "true";
        textControl.albumMode = "true";
        textControl.text = Config.getPROJ_NAME();
        textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;

        //bug fix
        //배경이 검정색일때 검정색 글자면 안보인다.
        //현재 일단 검정색만 처리
        try {
            boolean isBgLayerTypeColor = coverPage.getBgControl().type.equals("color");
            boolean isBgLayerResourceURLEmpty = (coverPage.getBgControl().resourceURL.length() == 0); //이거 굳이 검사안해도 되지만...
            boolean isBgLayerColorBlack = coverPage.getBgControl().bgColor.equals("000000");  //일단 검정색만
            if (isBgLayerTypeColor && isBgLayerResourceURLEmpty && isBgLayerColorBlack) {
                Dlog.w(TAG, "addPhotoBookThicknessText() The background color is black so the text color is changed to white !!");
                textControl.format.fontColor = "ffffff";
            }
            //https://gammabeta.tistory.com/390
            //위의 싸이트를 참고하면 결국 배경색의 명도를 구해서 일정 값 기준으로 책등 텍스트를 흰색 또는 검정으로 설정하면 될듯 한데...
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        // cover에 책등을 추가한다.
        coverPage.addControl(textControl);
    }

    private void addNewKakaoBookThicknessText(float computeWidth) {
        SnapsPage coverPage = getPages().get(0);
        SnapsTextControl textControl = snsBookStick;
        int xPos = ((int) ((coverPage.getOriginWidth()) / 2.f - 8 / 2.f));
        if (textControl == null) {
            textControl = new SnapsTextControl();
            textControl.setX(xPos + "");
            textControl.y = "20";
            textControl.width = 8 + "";
            textControl.height = coverPage.height + "";
            textControl.format.fontFace = "스냅스 윤고딕 700bold";
            textControl.format.fontSize = "8";
            textControl.format.auraOrderFontSize = SNS_BOOK_STICK;
            textControl.format.fontColor = SNS_BOOK_STICK_COLOR;
            textControl.format.baseFontColor = textControl.format.fontColor;
            textControl.format.align = "left";
            textControl.format.bold = "0";
            textControl.format.italic = "0";
            textControl.format.underline = "false";
            textControl.format.verticalView = "true";
            textControl.albumMode = "true";
            textControl.text = (info.F_SNS_BOOK_INFO_PERIOD.equals("") ? "" : (info.F_SNS_BOOK_INFO_PERIOD + " ")) + Config.getPROJ_NAME();
            textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;

            // 책등을 넣어준다.
            LineText line = new LineText();
            line.x = textControl.x;
            line.y = textControl.y;
            line.width = textControl.width;
            line.height = textControl.height;
            line.text = textControl.text;
            textControl.textList.add(line);
        } else {
            // 강제로 맞춤... 10은 의미 없음..
            textControl.setX(xPos - 10 + "");
            textControl.text = (info.F_SNS_BOOK_INFO_PERIOD.equals("") ? "" : (info.F_SNS_BOOK_INFO_PERIOD + " ")) + Config.getPROJ_NAME();
            // 책등을 넣어준다.
            LineText line = new LineText();
            line.x = textControl.x;
            line.y = textControl.y;
            line.width = textControl.width;
            line.height = textControl.height;
            line.text = textControl.text;
            textControl.textList.add(line);
        }

        if (Config.isSnapsDiary()) {
            SnapsDiaryDataManager manager = SnapsDiaryDataManager.getInstance();
            if (!StringUtil.isEmpty(manager.getStartDate()) && !StringUtil.isEmpty(manager.getEndDate())) {
                textControl.text = StringUtil.getFormattedDateString(manager.getStartDate(), "yyyyMMdd", "yyyy. MM. dd")
                        + " - " + StringUtil.getFormattedDateString(manager.getEndDate(), "yyyyMMdd", "yyyy. MM. dd");
            }

            if (textControl.textList.size() > 0)
                textControl.textList.get(0).text = textControl.text;
            coverPage.addControl(textControl);
        } else // cover에 책등을 추가한다.
            coverPage.addControl(textControl);
    }

    /***
     * 지정된 위치에 qr 코드를 넣어주는 함수..
     *
     * @param rect
     */
    public void addQRcode(Rect rect) {
        if (rect == null || !Const_PRODUCT.isDesignNoteProduct()
                && !Config.isSimplePhotoBook()
                && !Config.isSimpleMakingBook()
                && !Config.isSNSBook()
                && !Config.isCalendarWide(Config.getPROD_CODE())
                && !Config.isCalendarVert(Config.getPROD_CODE())
                && !Const_PRODUCT.isCardShapeNormal()
                && !Const_PRODUCT.isAccordionCardProduct())
            return;

        SnapsPage coverPage = getCoverPage();
        if (coverPage != null) {
            if (isExistQRLayout(coverPage)) return;

            SnapsLayoutControl layout = new SnapsLayoutControl();
            layout.setX(String.valueOf(rect.left));
            layout.y = String.valueOf(rect.top);
            layout.width = String.valueOf(rect.width());
            layout.height = String.valueOf(rect.height());

            layout.type = "local_resource"; // 화면단에서만 보여주는 이미지들...
            if (Const_PRODUCT.isDesignNoteProduct()) {
                layout.resourceURL = "@drawable/note_logo_qr";
            } else if (Config.isCalendar()) {// 달QRCode
                layout.resourceURL = "@drawable/calendar_qrcode";
            } else if (Const_PRODUCT.isCardShapeNormal()) {
                layout.resourceURL = "@drawable/qrcode_card_flat";
            } else if (Const_PRODUCT.isAccordionCardProduct()) {
                layout.resourceURL = "@drawable/accordioncard_qrcode";
            } else
                layout.resourceURL = "@drawable/photobook_qrcode";
            layout.imageLoadType = QRCODE_TYPE;

            if (Config.isCalendarWide(Config.getPROD_CODE()) || Config.isCalendarVert(Config.getPROD_CODE())) {
                coverPage.addLayout(layout);
            } else if (Const_PRODUCT.isCardShapeNormal() || Const_PRODUCT.isAccordionCardProduct()) {
                coverPage.addLayout(layout);
            } else {
                //커버 페이지에만 qrcode 추가 레더커버인 경우 표시하지 않는다
                if (coverPage.type.equals("cover") && !coverPage.info.F_COVER_TYPE.equals("leather")) {
                    coverPage.addLayout(layout);
                }
            }
        }
    }

    private SnapsPage getCoverPage() {
        if (Config.isCalendarWide(Config.getPROD_CODE()) || Config.isCalendarVert(Config.getPROD_CODE())) {
            return getPages().get(getPages().size() - 1);
        } else if (Const_PRODUCT.isCardShapeNormal() || Const_PRODUCT.isAccordionCardProduct()) {
            if (getPages().size() > 1) {
                return getPages().get(1);
            }
        } else {
            return getPages().get(0);
        }
        return null;
    }

    private boolean isExistQRLayout(SnapsPage coverPage) {
        if (coverPage == null) return false;

        ArrayList<SnapsControl> layoutControlList = coverPage.getLayoutList();
        if (layoutControlList != null) {
            for (SnapsControl control : layoutControlList) {
                if (control == null || !(control instanceof SnapsLayoutControl)) continue;
                if (((SnapsLayoutControl) control).imageLoadType == QRCODE_TYPE) return true;
            }
        }
        return false;
    }

    /***
     * 페이지 아이디를 가지고 페이지를 가져오는 함수..
     *
     * @param id
     * @return
     */
    public SnapsPage getSnapsPageByID(int id) {
        for (SnapsPage p : getPages()) {
            if (p.getPageID() == id)
                return p;
        }

        return null;
    }

    /***
     * 페이지 아이디를 가지고 페이지 인덱스를 가져오는 함수..
     *
     * @param id
     * @return
     */
    public int getSnapsPageIndexByID(int id) {
        int idx = 0;
        for (SnapsPage p : getPages()) {
            if (p.getPageID() == id)
                return idx;
            idx++;
        }

        return -1;
    }

    /***
     * 하드 커버에 스파인 가이드를 추가하는 함수..
     */
    public void addSpine() {
        // 커버타입이 하드가 아니면 스파인 가이드를 만들지 않는다.
        if (!info.F_COVER_TYPE.equals("hard"))
            return;

        // 스파인 검색....
        boolean isExistSpine = false;
        SnapsLayoutControl layout = null;
        SnapsPage coverPage = getPages().get(0);
        for (SnapsControl c : coverPage.getLayoutList()) {
            if (c instanceof SnapsLayoutControl) {
                if (((SnapsLayoutControl) c).imageLoadType == Const_VALUES.SPINE_TYPE) {
                    isExistSpine = true;
                    layout = (SnapsLayoutControl) c;
                    break;
                }
            }
        }
        // 기존 스파인 가이드가 있는경우... 사이즈만 수정을 한다.

        /*
         * 8.f / 10.f 하드커버 내지무광 43 페이지(22장) 이상 스파인 2
         *
         * 하드커버 내지무광 41 페이지(21장) 이상 스파인 2
         */

        float mmSpineWidth = info.maxPageInfo.getHardCoverSpineMMSize(info.F_PAPER_CODE, getPages().size() - 2);// (pageList.size() - 1)
        // > gubunPage ?
        // 10.f :
        // 8.f;
        int pxSpineWidth = (int) ((Integer.parseInt(info.F_COVER_MID_WIDTH) / SnapsTemplateInfo.HARDCOVER_SPINE_WIDTH) * mmSpineWidth);

        // 커버를 가져온다.
        // 커버에 스파인이 들어갈 위치를 구한다.
        // 스파인을 추가한다.
        if (!isExistSpine) {
            layout = new SnapsLayoutControl();
            coverPage.addLayout(layout);
        }

        layout.setX((int) (coverPage.getOriginWidth() / 2.f - pxSpineWidth / 2.f) + "");
        layout.y = "-10";
        layout.width = String.valueOf(pxSpineWidth); //
        layout.height = String.valueOf((int) (Float.parseFloat(coverPage.height) + 20));
        layout.type = "local_resource"; // 화면단에서만 보여주는 이미지들...
        layout.resourceURL = "@drawable/hardcover_spine";
        layout.bgColor = "";

        layout.imageLoadType = Const_VALUES.SPINE_TYPE;// 스파인..
    }

    /***
     * 책등 이미지를 생성하는 함수..
     */
    void addSpineImage(int addWidth) {
        SnapsPage coverPage = getPages().get(0);
        boolean isExist = false;
        SnapsLayoutControl layout = null;
        for (SnapsControl c : coverPage.getLayoutList()) {
            if (c instanceof SnapsLayoutControl) {
                if (((SnapsLayoutControl) c).imageLoadType == Const_VALUES.SPINE_IMAGE_TYPE) {
                    layout = (SnapsLayoutControl) c;
                    isExist = true;
                    break;
                }

            }
        }

        if (layout == null)
            layout = new SnapsLayoutControl();

        // 이미 책등이미지가 있는경우 위치이동만한다.
        int width = 40 + addWidth;      //책등 폭이 40인듯.. 고정 값..
        layout.setX(coverPage.getWidth() / 2.f - (width / 2) + "");
        layout.y = "0";
        layout.width = width + "";
        layout.height = coverPage.height;

        if (isExist)
            return;

        layout.type = "local_resource"; // 화면단에서만 보여주는 이미지들...
        layout.imageLoadType = Const_VALUES.SPINE_IMAGE_TYPE;
        if (info.getCoverType() == COVER_TYPE.HARD_COVER) {
            layout.resourceURL = "@drawable/book_hard_stick";
            coverPage.addLayout(layout);
        } else if (info.getCoverType() == COVER_TYPE.SOFT_COVER) {
            layout.resourceURL = "@drawable/book_soft_stick";
            coverPage.addLayout(layout);
        }

    }

    /***
     * 페이지 타입을 가지고 페이지를 구하는 함수..
     *
     * @param type
     * @return
     */
    SnapsPage getPageWithType(String type) {
        for (SnapsPage page : getPages()) {
            if (page.type.equals(type))
                return page;
        }

        return null;
    }

    public SnapsPage getPageLayoutIDX(int idx) {

        return clonePageList.get(idx);
    }

    /***
     * 페이지를 복사하는 함수..
     */
    public void clonePage() {
        int i = 1; // 커버는 카피하지 않는다.
        for (SnapsPage page : pageList) {
            SnapsPage p = page.copyPage(i);
            clonePageList.add(p);
            i++;
        }
    }

    /***
     * width와 heigth를 변경하는 함수..(액자에서 세로형이나 가로형이 똑같은 사이즈로 들어오기 때문에 수정..)
     */
    public void changeWidthHeight() {
        SnapsPage page = getPages().get(0);
        boolean isFlag1 = false;
        boolean isFlag2 = false;

        if (page.getWidth() > Integer.parseInt(page.height)) {
            isFlag1 = true;
        }

        if (Integer.parseInt(info.F_PAGE_PIXEL_WIDTH) > Integer.parseInt(info.F_PAGE_PIXEL_HEIGHT)) {
            isFlag2 = true;
        }

        // 다르면 width와 heigt가 반대인 경우...
        if (isFlag1 != isFlag2) {
            String temp = info.F_PAGE_PIXEL_WIDTH;
            // page px
            info.F_PAGE_PIXEL_WIDTH = info.F_PAGE_PIXEL_HEIGHT;
            info.F_PAGE_PIXEL_HEIGHT = temp;

            // page mm
            temp = info.F_PAGE_MM_WIDTH;
            info.F_PAGE_MM_WIDTH = info.F_PAGE_MM_HEIGHT;
            info.F_PAGE_MM_HEIGHT = temp;

        }

    }

    /***
     * max page 정보를 로드 한다.
     *
     * @param context
     */
    public void initMaxPageInfo(Context context) {

        //401p 적용
        info.maxPageInfo = MenuDataManager.getInstance().getMenuData().maxPageInfo;


        //맥스페이지를 설정을 한다
        String paperCode = "";
        if (!StringUtil.isEmpty(Config.getPAPER_CODE()))//신규 편집인 경우
            paperCode = Config.getPAPER_CODE();
        else if (!StringUtil.isEmpty(info.F_PAPER_CODE)) {// 재편집인 경우
            paperCode = info.F_PAPER_CODE;
        }
        try {
            String F_MAX_QUANTITY = info.maxPageInfo.getMaxPageWithPaperCode(paperCode);
            if (F_MAX_QUANTITY != null)
                info.F_MAX_QUANTITY = F_MAX_QUANTITY;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public SnapsTemplate(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeString(type);

        dest.writeTypedList(priceList);
        dest.writeTypedList(pageList);
        dest.writeTypedList(dynamicPageList);
        dest.writeTypedList(_backPageList);
        dest.writeTypedList(_hiddenPageList);
        dest.writeTypedList(delimgList);

        dest.writeParcelable(clientInfo, 0);
        dest.writeParcelable(info, 0);
        dest.writeParcelable(saveInfo, 0);
        dest.writeParcelable(snapsProductOption, 0);

        dest.writeTypedList(myphotoImageList);
        dest.writeTypedList(clonePageList);

        dest.writeString(F_PRO_YORN);
        dest.writeInt(F_ADD_PAGE);
        dest.writeInt(pageID);

        dest.writeString(SNS_BOOK_STICK);
        dest.writeString(SNS_BOOK_STICK_COLOR);

        if (fonts != null) {
            arrFonts.clear();
            Object[] arFonts = (Object[]) fonts.toArray();
            if (arFonts instanceof String[]) {
                for (Object fontObj : arFonts) {
                    arrFonts.add((String) fontObj);
                }
//				Collections.addAll(arrFonts, arFonts);
            }
        }
        dest.writeList(arrFonts);
    }

    @SuppressWarnings("unchecked")
    private void readFromParcel(Parcel in) {

        version = in.readString();
        type = in.readString();

        in.readTypedList(priceList, SnapsTemplatePrice.CREATOR);
        in.readTypedList(pageList, SnapsPage.CREATOR);
        in.readTypedList(dynamicPageList, SnapsPage.CREATOR);
        in.readTypedList(_backPageList, SnapsPage.CREATOR);
        in.readTypedList(_hiddenPageList, SnapsPage.CREATOR);
        in.readTypedList(delimgList, SnapsDelImage.CREATOR);

        in.readParcelable(SnapsClientInfo.class.getClassLoader());
        in.readParcelable(SnapsTemplateInfo.class.getClassLoader());
        in.readParcelable(SnapsSaveInfo.class.getClassLoader());
        in.readParcelable(SnapsProductOption.class.getClassLoader());

        in.readTypedList(myphotoImageList, MyPhotoSelectImageData.CREATOR);
        in.readTypedList(clonePageList, SnapsPage.CREATOR);

        F_PRO_YORN = in.readString();
        F_ADD_PAGE = in.readInt();
        pageID = in.readInt();

        SNS_BOOK_STICK = in.readString();
        SNS_BOOK_STICK_COLOR = in.readString();

        in.readList(arrFonts, String.class.getClassLoader());
        if (arrFonts != null && !arrFonts.isEmpty()) {
            fonts.clear();
            for (String font : arrFonts) {
                fonts.add(font);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SnapsTemplate createFromParcel(Parcel in) {
            return new SnapsTemplate(in);
        }

        @Override
        public SnapsTemplate[] newArray(int size) {
            return new SnapsTemplate[size];
        }
    };

    /**
     * 책등을 복사를 해놓는다.
     *
     * @param control
     */
    public void setSNSBookStick(SnapsTextControl control) {
        if (control != null)
            this.snsBookStick = control.copyControl();
    }

    public void initLayoutControlsIdInTemplate() throws Exception {
        ArrayList<SnapsPage> snapsPageList = getPages();
        if (snapsPageList == null) return;

        int size = snapsPageList.size();
        for (int index = 0; index < size; index++) {
            SnapsPage page = snapsPageList.get(index);
            ArrayList<SnapsControl> layers = page.getLayoutList();
            for (SnapsControl control : layers) {
                control.setControlId(-1);
            }

            ArrayList<SnapsControl> bgControls = page.getBgList();
            for (SnapsControl control : bgControls) {
                control.setControlId(-1);
            }

            ArrayList<SnapsControl> layoutControls = page.getControlList();
            for (SnapsControl control : layoutControls) {
                control.setControlId(-1);
            }

            ArrayList<SnapsControl> formList = page.getFormList();
            for (SnapsControl control : formList) {
                control.setControlId(-1);
            }
        }
    }

    public int getImageCountOnAllPages() {
        ArrayList<SnapsPage> snapsPageList = getPages();
        if (snapsPageList == null || snapsPageList.isEmpty()) return 0;

        int result = 0;
        for (SnapsPage snapsPage : snapsPageList) {
            if (snapsPage == null) continue;
            result += snapsPage.getImageCountOnPage();
        }
        return result;
    }

    public boolean isContainHiddenPageOnPageList() {
        ArrayList<SnapsPage> snapsPageList = getPages();
        if (snapsPageList == null || snapsPageList.isEmpty()) return false;

        for (SnapsPage page : getPages()) {
            if (page == null || page.type == null) continue;
            if (page.type.equalsIgnoreCase("hidden")) {
                return true;
            }
        }
        return false;
    }


    /***
     * 템플릿 다운로드 url를 만드는 함수.
     */

    public static String getTemplateUrl() {
        StringBuilder sb = new StringBuilder();
        if (Const_PRODUCT.isNormalTemplateProduct()) {
            sb.append(SnapsAPI.GET_API_NORMALTEMPLATE())
                    .append("&prmProdCode=").append(Config.getPROD_CODE())
                    .append("&prmTmplCode=").append(Config.getTMPL_CODE())
                    .append("&prmChgCvrCode=")
                    .append("&frameid=").append(Config.getFRAME_ID());
        } else {
            sb.append(SnapsAPI.GET_API_MULTITEMPLATE())
                    .append("&prmProdCode=").append(Config.getPROD_CODE())
                    .append("&prmTmplCode=").append(Config.getTMPL_CODE())
                    .append("&prmBgCode=").append(Config.getTMPL_COVER() != null ? Config.getTMPL_COVER() : "");

            if (Const_PRODUCT.isDesignNoteProduct()) {
                sb.append("&prmPaperCode=").append(Config.getNOTE_PAPER_CODE());
            }

            if (Config.getFRAME_ID() != null && Config.getFRAME_ID().length() > 0) {
                sb.append("&frameid=").append(Config.getFRAME_ID());
            }
        }
        return sb.toString();
    }

    public static String getTemplateNewYearsCardUrl(ArrayList<String> templateCodes) {
        StringBuilder sb = new StringBuilder(SnapsAPI.GET_API_MULTIPLEDATATEMPLATE());
        sb.append("&prmProdCode=").append(Config.getPROD_CODE());

        for (String name : templateCodes) {
            sb.append("&prmTmplCode=").append(name);
        }
        sb.append("&prmBgCode=").append((Config.getTMPL_COVER() != null ? Config.getTMPL_COVER() : ""));
        return sb.toString();
    }

    public static String getDiaryTemplateUrl(String diaryTempateXMLPath) {
        if (SnapsDiaryDataManager.isAliveSnapsDiaryService() && diaryTempateXMLPath != null && diaryTempateXMLPath.length() > 0) {
            return SnapsAPI.DOMAIN(false) + diaryTempateXMLPath;
        } else {
            return getTemplateUrl();
        }
    }

    /**
     * Web 에서 받아올 때는 pageList에 저장시킨다.
     *
     * @return
     */
    public ArrayList<SnapsPage> getOriginalPages() {
        return pageList;
    }

    /**
     * 그 뒤에 dynamic을 쓰던 original을 쓰던 product에 따라 나뉜다. (현재 KT Book 만 사용한다.)
     *
     * @return
     */
    public ArrayList<SnapsPage> getPages() {
        return isResourceTemplate() && dynamicPageList != null && !dynamicPageList.isEmpty() ? dynamicPageList : pageList;
    }

    public void setNullPages() {
        this.pageList = null;
        this.dynamicPageList = null;
    }

    public void initDynamicTemplate() {
        if (!isResourceTemplate()) {
            return;
        }

        dynamicPageList = new ArrayList<>();

        // Cover
        dynamicPageList.add(pageList.get(1).copyPage(0));
        // Title
        dynamicPageList.add(pageList.get(2).copyPage(1));

        // Page
        dynamicPageList.add(pageList.get(4).copyPage(2));
        dynamicPageList.add(pageList.get(6).copyPage(3));
        dynamicPageList.add(pageList.get(5).copyPage(4));
        dynamicPageList.add(pageList.get(6).copyPage(5));
        dynamicPageList.add(pageList.get(7).copyPage(6));
        dynamicPageList.add(pageList.get(4).copyPage(7));
        dynamicPageList.add(pageList.get(7).copyPage(8));
        dynamicPageList.add(pageList.get(5).copyPage(9));
        dynamicPageList.add(pageList.get(6).copyPage(10));
        dynamicPageList.add(pageList.get(6).copyPage(11));
    }

    public void makeDynamicTemplate(List<MyPhotoSelectImageData> selectImages) {
        if (!isResourceTemplate()) {
            Dlog.e(TAG, "Template is not Resource template");
            return;
        }

        if (selectImages == null || selectImages.size() != 22) {
            Dlog.e(TAG, "Selected images are not 22");
            return;
        }

        dynamicPageList = new ArrayList<>();

        // 0 -> 세로
        // 1 -> 가로
        // 2 -> 세로
        // 3 -> 가로

        SnapsPage coverPage = isHorizontalPhoto(selectImages.get(0)) ? pageList.get(0) : pageList.get(1);
        dynamicPageList.add(coverPage.copyPage(0));
        Dlog.d("Cover Page picked " + (isHorizontalPhoto(selectImages.get(0)) ? "가로" : "세로"));

        SnapsPage titlePage = isHorizontalPhoto(selectImages.get(1)) ? pageList.get(2) : pageList.get(3);
        dynamicPageList.add(titlePage.copyPage(1));
        Dlog.d("Title Page picked " + (isHorizontalPhoto(selectImages.get(1)) ? "가로" : "세로"));


        // Resouce template
        // 4 -> 가로, 가로
        // 5 -> 세로, 세로
        // 6 -> 가로, 세로
        // 7 -> 세로, 가로

        MyPhotoSelectImageData bufferFirstPageData = null;

        for (int i = 2; i < selectImages.size(); i++) {

            MyPhotoSelectImageData imageData = selectImages.get(i);

            if (i % 2 == 0) {
                bufferFirstPageData = imageData;
                continue;
            }

            int isBufferdDataHorizontal = isHorizontalPhoto(bufferFirstPageData) ? 2 : 0;
            int isHorizontal = isHorizontalPhoto(imageData) ? 1 : 0;

            SnapsPage page;
            int sum = isHorizontal | isBufferdDataHorizontal;
            switch (sum) {
                case 0: // 00
                    page = pageList.get(5);
                    Dlog.d("(" + (i - 1) + ", " + i + ") Picked 세로, 세로");
                    break;

                case 1: // 01
                    page = pageList.get(7);
                    Dlog.d("(" + (i - 1) + ", " + i + ") Picked 세로, 가로");
                    break;

                case 2: // 10
                    page = pageList.get(6);
                    Dlog.d("(" + (i - 1) + ", " + i + ") Picked 가로, 세로");
                    break;

                case 3: // 11
                default:
                    //가로, 가로
                    page = pageList.get(4);
                    Dlog.d("(" + (i - 1) + ", " + i + ") Picked 가로, 가로");
                    break;
            }
            dynamicPageList.add(page.copyPage(i));
            bufferFirstPageData = null;
        }
    }

    private boolean isHorizontalPhoto(MyPhotoSelectImageData imageData) {
        if (imageData == null) {
            return false;
        }

        float mWidth = Float.parseFloat(imageData.F_IMG_WIDTH);
        float mHeight = Float.parseFloat(imageData.F_IMG_HEIGHT);

        if (imageData.ROTATE_ANGLE == 0 || imageData.ROTATE_ANGLE == 180) {
            return mWidth >= mHeight;

        } else {
            return mWidth <= mHeight;
        }
    }

    private boolean isResourceTemplate() {
        if (pageList == null || pageList.isEmpty()) {
            return false;
        }

        int coverCount = 0;
        int titleCount = 0;

        for (SnapsPage page : pageList) {
            if (page.isCover()) {
                coverCount++;
            }

            if (page.isTitle()) {
                titleCount++;
            }
        }

        return coverCount > 1 && titleCount > 1;
    }

    public int getPixelWidth() {
        return Integer.parseInt(info.F_PAGE_PIXEL_WIDTH);
    }

    public int getPixelHeight() {
        return Integer.parseInt(info.F_PAGE_PIXEL_HEIGHT);
    }

    public boolean hasAccessories() {

        SnapsProductOption productOption = getProductOption();

        if (productOption == null) {
            return false;
        }

        return productOption.hasAccessories();
    }

    public void removeAccessoriesInfo() {

        SnapsProductOption productOption = getProductOption();

        if (productOption == null) {
            return;
        }

        productOption.removeAccessoriesInfo();

    }

    @Nullable
    public SnapsSceneMaskControl findSceneMask() {
        if (getPages() == null || getPages().size() < 1) {
            return null;
        }

        for (SnapsPage snapsPage : getPages()) {
            SnapsControl sceneMaskControl = snapsPage.getControlByProperty("sceneMask");
            if (sceneMaskControl != null) {
                return (SnapsSceneMaskControl) sceneMaskControl;
            }
        }
        return null;
    }

    /**
     * 정확히 어떤 기준으로 스티커 백그라운드를 가져올 지는 모르겠다.
     *
     * @return
     */
    @Nullable
    public SnapsLayoutControl getStickerBackgroundLayoutControl() {
        for (SnapsControl control : getAllLayoutControls()) {
            if (control instanceof SnapsLayoutControl) {
                SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                Dlog.d("Layoutcontrol Name " + layoutControl.name);
                if (layoutControl.isForBackground()) {
                    return layoutControl;
                }
            }
        }
        return null;
    }

    public List<SnapsControl> getAllLayoutControls() {
        ArrayList<SnapsControl> layoutControls = new ArrayList<>();
        for (SnapsPage page : getPages()) {
            layoutControls.addAll(page.getLayoutList());
        }
        return layoutControls;
    }
}