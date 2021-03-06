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
    // ???????????? ?????? ????????? ???????????? ?????? set
    public HashSet<String> fonts = new HashSet<String>();
    public List<String> arrFonts = new ArrayList<String>();

    private SnapsProductOption snapsProductOption = new SnapsProductOption();

    // ?????? ?????? ?????? ??????...
    private String F_PRO_YORN = "Y";
    private int F_ADD_PAGE = 0; // ??????????????? ??????..

    // ????????? ?????????
    private int pageID = 0;

    // ?????????????????? ??? ?????? ?????? ?????????
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
     * ??????????????? ???????????? ?????????...
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
        // ?????? ????????? ????????? ??????..
        if (info.getCoverType() == COVER_TYPE.NONE_COVER)
            return;

        // ?????? ????????? ??????..
        if (info.getCoverType() == COVER_TYPE.HARD_COVER) {

            // ?????????????????? ????????????.
            addSpineImage(0);

            // ???????????? ?????? ????????? ????????? ??????.

            float computeWidth = computeMaxPageAdditionWidth();
            applyMaxPage(0, computeWidth, computeWidth);
            return;
        }

        // ????????? ???????????? ?????? ?????? ?????? ?????????. px
        float computeWidth = computeMaxPageAdditionWidth();

        // ????????? ?????? ?????????.. ?????? ??????...
        float[] computeWidthControl = computeMaxPageAdditionWidthSoftCover();

        if (isShouldAddBookThickness()) {
            if (!isExistBookThickness()) {
                Dlog.d("setApplyMaxPage() addBookThicknessText");
                addBookThicknessText(computeWidth);
            }
        } else {
            if (isExistBookThickness()) {
                //KT ??? - ?????? ?????? ????????? ????????? ???????????? ????????? ????????? KT ?????? ?????? ?????? ???????????? ????????? ?????? ?????????.
                if (!Config.isKTBook()) {
                    Dlog.d("setApplyMaxPage() deleteBookThicknessText");
                    deleteBookThicknessText();
                }
            }
        }

        // ????????? max page??? ????????????.
        applyMaxPage(computeWidthControl[0], computeWidthControl[1], computeWidth);
    }

    public float getHardCoverSpineWidth() {
        // ?????? ?????? ????????? ?????????.
        return info.maxPageInfo.getHardCoverSpineMMSize(info.F_PAPER_CODE, getPages().size() - 2) - SnapsTemplateInfo.HARDCOVER_SPINE_WIDTH;
    }

    /***
     * ????????? ????????? ????????? ?????????.
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
            // ???????????? ???????????? ??????..???..
            int addPageCount = Config.isThemeBook() ? getPages().size() - 16 : getPages().size() - (Integer.parseInt(info.F_BASE_QUANTITY) + 2);

            info.mmMidWidth = info.getPaperThick() * (getPages().size() - 1); // ?????? ??????

            float addtionWidth = 0.f;
            if (Config.isThemeBook()) {
                addtionWidth = (addPageCount * info.getPaperThick()) * pxPerMM;
            } else {
                if (getPages().size() - 2 > 75) { // ????????? ??????????????? 75???(151p)??????
                    // maxpage??? ????????????.
                    // ?????? ?????? ???????????? - 151p?????? ??????????????? ????????? ?????????.
                    addtionWidth = (info.getPaperThick() * (addPageCount - 65)) * pxPerMM;// ((info.getPaperThick() * (pageList.size() - 1)) - info.getSoftSpineBasemmWidth()) * pxPerMM;

                }
            }

            Dlog.d("computeMaxPageAdditionWidth() addtionWidth:" + addtionWidth);
            // ????????? ???????????? ?????????...
            return Math.max(0.f, addtionWidth);
        } else
            return 0.f;

    }

    /***
     * ????????? ???????????? ???????????? ?????????????????? ?????? ????????? ??????...
     *
     * @return
     */
    float[] computeMaxPageAdditionWidthSoftCover() {
        float[] addtionWidths = {0.f, 0.f};
        if (info.getCoverType() == COVER_TYPE.SOFT_COVER) {
            // 1mm = 2px 296px/148mm
            float pxPerMM = Float.parseFloat(info.F_COVER_XML_WIDTH) / Float.parseFloat(info.F_COVER_MM_WIDTH);
            // ???????????? ???????????? ??????..???..
            int addPageCount = Config.isThemeBook() ? getPages().size() - 16 : getPages().size() - (Integer.parseInt(info.F_BASE_QUANTITY) + 2);
            float addtionWidth = 0.f;

            if (Config.isThemeBook()) {
                addtionWidth = (addPageCount * info.getPaperThick()) * pxPerMM;
            } else {
                addtionWidth = ((getPages().size() - 2) - 10) * info.getPaperThick() * pxPerMM;

            }

            addtionWidth = Math.max(0.f, addtionWidth);

            if (getPages().size() - 2 > 75) {
                // 151p?????? ???????????? ????????? ????????? ?????? ?????? ?????????.
                float add151_401 = ((getPages().size() - 2) - 75) * info.getPaperThick() * pxPerMM;
                // 151?????? ?????? offset?????? ????????? ??????.
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
     * ????????? ????????? 151p?????? ???????????? ??????????????? ????????? ???????????? ???????????? ???????????? ????????? ??????.
     */
    void applyMaxPage(float additionContorlWidth_L, float additionContorlWidth_R, float additionCoverWidth) {
        // ?????????????????? ?????????. ???????????? ????????? ???????????? ???????????? ??????..
        SnapsPage coverPage = getPages().get(0);

        int coverWidth = coverPage.getOriginWidth();

        // ????????? ????????? ????????? ????????????.
        coverPage.setMaxPageX((int) additionCoverWidth);

        // bglayer ?????????.
        for (SnapsControl control : coverPage.getBgList()) {
            if (control instanceof SnapsBgControl) {
                control.width = coverPage.getWidth() + "";
                control.height = coverPage.height;
            }
        }

        // ????????????...
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

        // ????????????... ????????? ??????... ?????????????????? ????????? ????????? ?????????.
        for (SnapsControl control : coverPage.getControlList()) {
            // ?????????????????? ????????? ????????? ?????????.
            boolean isEnableMove = true;
            if (coverWidth / 2 > control.getIntX())
                isEnableMove = false;

            if (control instanceof SnapsTextControl) {
                SnapsTextControl snapsTextControl = (SnapsTextControl) control;
                if (snapsTextControl.format.verticalView.equalsIgnoreCase("true")) {
                    // ????????? ??????
                    control.setMaxPageX((int) (additionContorlWidth_L / 2.f));

                    if (isWriteBookThickness) {
                        snapsTextControl.text = Config.getPROJ_NAME();
                    }
                } else {
                    // ????????? ?????? ??????
                    if (snapsTextControl.format.orientation == TextFormat.TEXT_ORIENTAION_HORIZONTAL) {
                        int moveAmount = (int) (isEnableMove ? additionContorlWidth_R : additionContorlWidth_L);
                        control.setMaxPageX(moveAmount);
                    }
                }
            } else if (control instanceof SnapsClipartControl) {
                isEnableMove = coverWidth / 2 <= control.getIntX() + control.getIntWidth() / 2; // ??????????????? x?????? ????????? ????????? ????????? ???????????? ?????????.

                int moveAmount = (int) (isEnableMove ? additionContorlWidth_R : additionContorlWidth_L);
                control.setMaxPageX(moveAmount);
            }
        }

        coverPage.setAddmmWidth(Math.max(0.f, additionCoverWidth * info.getMMPX()));

        // ?????????????????? ????????????.
        addSpineImage(0);
    }

    void deleteBookThicknessText() {
        // ?????????????????? ?????????. ???????????? ????????? ???????????? ???????????? ??????..
        SnapsPage coverPage = getPages().get(0);

        ArrayList<SnapsControl> snapsControlList = coverPage.getControlList();
        int size = snapsControlList.size();
        for (int i = 0; i < size; ++i) {
            SnapsControl control = snapsControlList.get(i);
            if (control instanceof SnapsTextControl) {
                // ????????? ?????? ????????????...
                if (((SnapsTextControl) control).format.verticalView.equalsIgnoreCase("true")) {
                    coverPage.deleteControl(control);
                    return;
                }
            }
        }
    }

    /***
     * ????????? ???????????? ???????????? ??????..
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
        String fontFace = "????????? ????????? 330";
        String bold = "0";

        //KT ???
        if (Config.isKTBook()) {
            offsetY = "28";
            width = "5";
            height = "90";
            fontSize = "4";
            fontFace = "????????? ????????? 700 Bold";
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
        //????????? ??????????????? ????????? ????????? ????????????.
        //?????? ?????? ???????????? ??????
        try {
            boolean isBgLayerTypeColor = coverPage.getBgControl().type.equals("color");
            boolean isBgLayerResourceURLEmpty = (coverPage.getBgControl().resourceURL.length() == 0); //?????? ?????? ??????????????? ?????????...
            boolean isBgLayerColorBlack = coverPage.getBgControl().bgColor.equals("000000");  //?????? ????????????
            if (isBgLayerTypeColor && isBgLayerResourceURLEmpty && isBgLayerColorBlack) {
                Dlog.w(TAG, "addPhotoBookThicknessText() The background color is black so the text color is changed to white !!");
                textControl.format.fontColor = "ffffff";
            }
            //https://gammabeta.tistory.com/390
            //?????? ???????????? ???????????? ?????? ???????????? ????????? ????????? ?????? ??? ???????????? ?????? ???????????? ?????? ?????? ???????????? ???????????? ?????? ??????...
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        // cover??? ????????? ????????????.
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
            textControl.format.fontFace = "????????? ????????? 700bold";
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

            // ????????? ????????????.
            LineText line = new LineText();
            line.x = textControl.x;
            line.y = textControl.y;
            line.width = textControl.width;
            line.height = textControl.height;
            line.text = textControl.text;
            textControl.textList.add(line);
        } else {
            // ????????? ??????... 10??? ?????? ??????..
            textControl.setX(xPos - 10 + "");
            textControl.text = (info.F_SNS_BOOK_INFO_PERIOD.equals("") ? "" : (info.F_SNS_BOOK_INFO_PERIOD + " ")) + Config.getPROJ_NAME();
            // ????????? ????????????.
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
        } else // cover??? ????????? ????????????.
            coverPage.addControl(textControl);
    }

    /***
     * ????????? ????????? qr ????????? ???????????? ??????..
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

            layout.type = "local_resource"; // ?????????????????? ???????????? ????????????...
            if (Const_PRODUCT.isDesignNoteProduct()) {
                layout.resourceURL = "@drawable/note_logo_qr";
            } else if (Config.isCalendar()) {// ???QRCode
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
                //?????? ??????????????? qrcode ?????? ??????????????? ?????? ???????????? ?????????
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
     * ????????? ???????????? ????????? ???????????? ???????????? ??????..
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
     * ????????? ???????????? ????????? ????????? ???????????? ???????????? ??????..
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
     * ?????? ????????? ????????? ???????????? ???????????? ??????..
     */
    public void addSpine() {
        // ??????????????? ????????? ????????? ????????? ???????????? ????????? ?????????.
        if (!info.F_COVER_TYPE.equals("hard"))
            return;

        // ????????? ??????....
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
        // ?????? ????????? ???????????? ????????????... ???????????? ????????? ??????.

        /*
         * 8.f / 10.f ???????????? ???????????? 43 ?????????(22???) ?????? ????????? 2
         *
         * ???????????? ???????????? 41 ?????????(21???) ?????? ????????? 2
         */

        float mmSpineWidth = info.maxPageInfo.getHardCoverSpineMMSize(info.F_PAPER_CODE, getPages().size() - 2);// (pageList.size() - 1)
        // > gubunPage ?
        // 10.f :
        // 8.f;
        int pxSpineWidth = (int) ((Integer.parseInt(info.F_COVER_MID_WIDTH) / SnapsTemplateInfo.HARDCOVER_SPINE_WIDTH) * mmSpineWidth);

        // ????????? ????????????.
        // ????????? ???????????? ????????? ????????? ?????????.
        // ???????????? ????????????.
        if (!isExistSpine) {
            layout = new SnapsLayoutControl();
            coverPage.addLayout(layout);
        }

        layout.setX((int) (coverPage.getOriginWidth() / 2.f - pxSpineWidth / 2.f) + "");
        layout.y = "-10";
        layout.width = String.valueOf(pxSpineWidth); //
        layout.height = String.valueOf((int) (Float.parseFloat(coverPage.height) + 20));
        layout.type = "local_resource"; // ?????????????????? ???????????? ????????????...
        layout.resourceURL = "@drawable/hardcover_spine";
        layout.bgColor = "";

        layout.imageLoadType = Const_VALUES.SPINE_TYPE;// ?????????..
    }

    /***
     * ?????? ???????????? ???????????? ??????..
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

        // ?????? ?????????????????? ???????????? ?????????????????????.
        int width = 40 + addWidth;      //?????? ?????? 40??????.. ?????? ???..
        layout.setX(coverPage.getWidth() / 2.f - (width / 2) + "");
        layout.y = "0";
        layout.width = width + "";
        layout.height = coverPage.height;

        if (isExist)
            return;

        layout.type = "local_resource"; // ?????????????????? ???????????? ????????????...
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
     * ????????? ????????? ????????? ???????????? ????????? ??????..
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
     * ???????????? ???????????? ??????..
     */
    public void clonePage() {
        int i = 1; // ????????? ???????????? ?????????.
        for (SnapsPage page : pageList) {
            SnapsPage p = page.copyPage(i);
            clonePageList.add(p);
            i++;
        }
    }

    /***
     * width??? heigth??? ???????????? ??????..(???????????? ??????????????? ???????????? ????????? ???????????? ???????????? ????????? ??????..)
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

        // ????????? width??? heigt??? ????????? ??????...
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
     * max page ????????? ?????? ??????.
     *
     * @param context
     */
    public void initMaxPageInfo(Context context) {

        //401p ??????
        info.maxPageInfo = MenuDataManager.getInstance().getMenuData().maxPageInfo;


        //?????????????????? ????????? ??????
        String paperCode = "";
        if (!StringUtil.isEmpty(Config.getPAPER_CODE()))//?????? ????????? ??????
            paperCode = Config.getPAPER_CODE();
        else if (!StringUtil.isEmpty(info.F_PAPER_CODE)) {// ???????????? ??????
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
     * ????????? ????????? ????????????.
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
     * ????????? ???????????? url??? ????????? ??????.
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
     * Web ?????? ????????? ?????? pageList??? ???????????????.
     *
     * @return
     */
    public ArrayList<SnapsPage> getOriginalPages() {
        return pageList;
    }

    /**
     * ??? ?????? dynamic??? ?????? original??? ?????? product??? ?????? ?????????. (?????? KT Book ??? ????????????.)
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

        // 0 -> ??????
        // 1 -> ??????
        // 2 -> ??????
        // 3 -> ??????

        SnapsPage coverPage = isHorizontalPhoto(selectImages.get(0)) ? pageList.get(0) : pageList.get(1);
        dynamicPageList.add(coverPage.copyPage(0));
        Dlog.d("Cover Page picked " + (isHorizontalPhoto(selectImages.get(0)) ? "??????" : "??????"));

        SnapsPage titlePage = isHorizontalPhoto(selectImages.get(1)) ? pageList.get(2) : pageList.get(3);
        dynamicPageList.add(titlePage.copyPage(1));
        Dlog.d("Title Page picked " + (isHorizontalPhoto(selectImages.get(1)) ? "??????" : "??????"));


        // Resouce template
        // 4 -> ??????, ??????
        // 5 -> ??????, ??????
        // 6 -> ??????, ??????
        // 7 -> ??????, ??????

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
                    Dlog.d("(" + (i - 1) + ", " + i + ") Picked ??????, ??????");
                    break;

                case 1: // 01
                    page = pageList.get(7);
                    Dlog.d("(" + (i - 1) + ", " + i + ") Picked ??????, ??????");
                    break;

                case 2: // 10
                    page = pageList.get(6);
                    Dlog.d("(" + (i - 1) + ", " + i + ") Picked ??????, ??????");
                    break;

                case 3: // 11
                default:
                    //??????, ??????
                    page = pageList.get(4);
                    Dlog.d("(" + (i - 1) + ", " + i + ") Picked ??????, ??????");
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
     * ????????? ?????? ???????????? ????????? ?????????????????? ????????? ?????? ????????????.
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