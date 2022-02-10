package com.snaps.common.structure.page;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.SnapsTemplateInfo.COVER_TYPE;
import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsFormControl;
import com.snaps.common.structure.control.SnapsHelperControl;
import com.snaps.common.structure.control.SnapsHoleControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsSceneCutControl;
import com.snaps.common.structure.control.SnapsSceneMaskControl;
import com.snaps.common.structure.control.SnapsStickControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BRect;
import com.snaps.common.utils.ui.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;

/**
 * template의 scene에 해당함. cover,title,page 등
 *
 * @author crjung
 */
public class SnapsPage implements Parcelable, Serializable {
    private static final String TAG = SnapsPage.class.getSimpleName();

    private static final long serialVersionUID = -5025225691844167598L;

    public static final String PAGETYPE_COVER = "cover";
    public static final String PAGETYPE_TITLE = "title";
    public static final String PAGETYPE_PAGE = "page";

    public String type = "";
    public String width = "";
    public String height = "";
    public String orgMultiformId = "";
    public String multiformId = "";
    public String templateCode = "";
    public String orgBgId = "";
    public String border = "";
    public String layout = "";
    public String background = "";
    public String embedCount = "0";
    public String year = "";
    public String month = "";
    public String dynamicMode = "yes";
    public String side = "";
    public String subType = "";

    public boolean thumbImg = false;
    public Bitmap thumbImage = null;

    public boolean isReverse = false;

    protected ArrayList<SnapsControl> _layer_bg;
    protected ArrayList<SnapsControl> _layer_layout; // 테마북 레이아웃 추가..
    protected ArrayList<SnapsControl> _layer_control;
    protected ArrayList<SnapsControl> _layer_form;

    // 캡쳐이미지 썸네일 패스...
    public String thumbnailPath = "";
    public String previewPath = "";
    public boolean isSelected = false;// 테마북 bottomview에서 사용하는 변수

    /**********************
     * 맥스 페이지... 관련..
     **********************/
    // 책등을 넣을지 말지.
    /**** Max page ****/
    private int maxPageX = 0; // 맥스페이지가 적용이 될때.. 추가될 offset 값설정 태마북은 무조건. 우측으로 컨트롤들을 밀어버린다...
    boolean isBookThickness = false;
    boolean isBookThicknessText = false;

    // 바코드 추가 여부 설정...
    public boolean isInsertQRCode = false;

    //한번 만들어진 페이지 썸네일은 다시 따지 않게 하기 위해.
    public boolean isMakedPageThumbnailFile = false;

    float addmmWidth = 0.f;

    // 페이지 아이디 페이지 인덱스가 아님...
    int pageID = 0;
    // 레이아웃 인덱스...
    int pageLayoutIDX = 0;

    public SnapsTemplateInfo info = new SnapsTemplateInfo();

    String snsproperty = "";
    String textType = "";

    // 인덱스가 들어가는 영역
    int index_x = 0;
    int index_y = 0;
    int index_width = 0;
    int index_heigth = 0;
    public String vAlign = "";

    private BRect imageLayerRect = new BRect();

    private int quantity = 1;

    public SnapsPage() {

    }

    public SnapsPage(int id, SnapsTemplateInfo info) {
        // bg , background
        _layer_bg = new ArrayList<SnapsControl>();

        // image
        _layer_layout = new ArrayList<SnapsControl>();

        // text , sticker , balloon text
        _layer_control = new ArrayList<SnapsControl>();

        // layer_form
        _layer_form = new ArrayList<SnapsControl>();

        pageID = id;
        this.info = info;
    }


    protected SnapsPage(Parcel in) {
        type = in.readString();
        width = in.readString();
        height = in.readString();
        orgMultiformId = in.readString();
        multiformId = in.readString();
        templateCode = in.readString();
        orgBgId = in.readString();
        border = in.readString();
        layout = in.readString();
        background = in.readString();
        embedCount = in.readString();
        year = in.readString();
        month = in.readString();
        dynamicMode = in.readString();
        side = in.readString();
        subType = in.readString();
        thumbImg = in.readByte() != 0;
        thumbImage = in.readParcelable(Bitmap.class.getClassLoader());
        isReverse = in.readByte() != 0;
        _layer_bg = in.createTypedArrayList(SnapsControl.CREATOR);
        _layer_layout = in.createTypedArrayList(SnapsControl.CREATOR);
        _layer_control = in.createTypedArrayList(SnapsControl.CREATOR);
        _layer_form = in.createTypedArrayList(SnapsControl.CREATOR);
        thumbnailPath = in.readString();
        previewPath = in.readString();
        isSelected = in.readByte() != 0;
        maxPageX = in.readInt();
        isBookThickness = in.readByte() != 0;
        isBookThicknessText = in.readByte() != 0;
        isInsertQRCode = in.readByte() != 0;
        isMakedPageThumbnailFile = in.readByte() != 0;
        addmmWidth = in.readFloat();
        pageID = in.readInt();
        pageLayoutIDX = in.readInt();

        try {
            info = in.readParcelable(SnapsTemplateInfo.class.getClassLoader());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        snsproperty = in.readString();
        textType = in.readString();
        index_x = in.readInt();
        index_y = in.readInt();
        index_width = in.readInt();
        index_heigth = in.readInt();
        vAlign = in.readString();

        try {
            imageLayerRect = in.readParcelable(BRect.class.getClassLoader());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(width);
        dest.writeString(height);
        dest.writeString(orgMultiformId);
        dest.writeString(multiformId);
        dest.writeString(templateCode);
        dest.writeString(orgBgId);
        dest.writeString(border);
        dest.writeString(layout);
        dest.writeString(background);
        dest.writeString(embedCount);
        dest.writeString(year);
        dest.writeString(month);
        dest.writeString(dynamicMode);
        dest.writeString(side);
        dest.writeString(subType);
        dest.writeByte((byte) (thumbImg ? 1 : 0));
        dest.writeParcelable(thumbImage, flags);
        dest.writeByte((byte) (isReverse ? 1 : 0));
        dest.writeTypedList(_layer_bg);
        dest.writeTypedList(_layer_layout);
        dest.writeTypedList(_layer_control);
        dest.writeTypedList(_layer_form);
        dest.writeString(thumbnailPath);
        dest.writeString(previewPath);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeInt(maxPageX);
        dest.writeByte((byte) (isBookThickness ? 1 : 0));
        dest.writeByte((byte) (isBookThicknessText ? 1 : 0));
        dest.writeByte((byte) (isInsertQRCode ? 1 : 0));
        dest.writeByte((byte) (isMakedPageThumbnailFile ? 1 : 0));
        dest.writeFloat(addmmWidth);
        dest.writeInt(pageID);
        dest.writeInt(pageLayoutIDX);
        dest.writeParcelable(info, flags);
        dest.writeString(snsproperty);
        dest.writeString(textType);
        dest.writeInt(index_x);
        dest.writeInt(index_y);
        dest.writeInt(index_width);
        dest.writeInt(index_heigth);
        dest.writeString(vAlign);
        dest.writeParcelable(imageLayerRect, flags);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SnapsPage> CREATOR = new Creator<SnapsPage>() {
        @Override
        public SnapsPage createFromParcel(Parcel in) {
            return new SnapsPage(in);
        }

        @Override
        public SnapsPage[] newArray(int size) {
            return new SnapsPage[size];
        }
    };

    public int getPageLayoutIDX() {
        return pageLayoutIDX;
    }

    public void setPageLayoutIDX(int pageLayoutIDX) {
        this.pageLayoutIDX = pageLayoutIDX;
    }

    public void setPageID(int pageId) {
        this.pageID = pageId;
    }

    public int getPageID() {
        return pageID;
    }

    public void setAddmmWidth(float addmmWidth) {
        this.addmmWidth = addmmWidth;
        Dlog.d("setAddmmWidth() addmmWidth:" + addmmWidth);
    }

    public void close() {
        try {
            _layer_bg.clear();
            _layer_layout.clear();
            _layer_control.clear();
            _layer_form.clear();

            _layer_bg = null;
            _layer_layout = null;
            _layer_control = null;
            _layer_form = null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 폼 추가.
     */
    public void addForm(SnapsFormControl control) {
        control.setX("0");
        control.y = "0";
        control.width = this.width;
        control.height = this.height;

        _layer_form.add(control);
    }

    /**
     * 배경 추가.
     */
    public void addBg(SnapsBgControl control) {
        control.setX("0");
        control.y = "0";
        control.width = this.width;
        control.height = this.height;

        _layer_bg.add(control);
    }

    /**
     * 레이아웃 추가.
     *
     * @param control
     */
    public void addLayout(SnapsLayoutControl control) {
        _layer_layout.add(control);
    }

    /**
     * 아이템 추가.
     *
     * @param control
     */
    public void addControl(SnapsControl control) {
        _layer_control.add(control);
    }

    /***
     * 컨트롤 삭제..
     *
     * @param control
     */
    public void deleteControl(SnapsControl control) {
        if (_layer_control != null && control != null && _layer_control.contains(control))
            _layer_control.remove(control);
    }

    /***
     * 레이아웃 삭제..
     *
     * @param layout
     */
    public void deleteLayout(SnapsLayoutControl layout) {
        if (_layer_layout != null && layout != null && _layer_layout.contains(layout))
            _layer_layout.remove(layout);
    }

    /**
     * 배경 바꾸기.
     *
     * @param control
     */
    public void changeBg(SnapsBgControl control) {
        removeAllBg();
        this.addBg(control);
    }

    public void changeLayout(ArrayList<SnapsControl> list) {
        removeAllLayout();

        for (SnapsControl control : list) {
            this.addLayout((SnapsLayoutControl) control);
        }

    }

    private void removeAllLayout() {
        _layer_layout.clear();
    }

    /**
     * 배경 삭제.
     */
    private void removeAllBg() {
        _layer_bg.clear();
    }

    /**
     * 배경 리스트
     *
     * @return
     */
    public ArrayList<SnapsControl> getBgList() {
        return this._layer_bg;
    }

    /**
     * 레이아웃 리스트
     *
     * @return
     */
    public ArrayList<SnapsControl> getLayoutList() {
        return this._layer_layout;
    }

    public ArrayList<SnapsControl> getLayerLayouts() {
        return _layer_layout;
    }

    public ArrayList<SnapsControl> getLayoutListByProperty(String property) {
        ArrayList<SnapsControl> controls = new ArrayList<SnapsControl>();
        for (SnapsControl c : _layer_layout) {
            if (c.getSnsproperty().equals(property))
                controls.add(c);

        }
        return controls;
    }

    /**
     * 조건 무시하려면 null로
     *
     * @param regName  : regist name값
     * @param regValue : regist value값
     * @return
     */
    public ArrayList<SnapsLayoutControl> getLayoutListByRegData(String regName, String regValue) {
        ArrayList<SnapsLayoutControl> controls = new ArrayList<SnapsLayoutControl>();
        for (SnapsControl c : _layer_layout) {
            if (c instanceof SnapsLayoutControl && (regName == null || regName.equals(c.regName)) && (regValue == null || regValue.equals(c.regValue)))
                controls.add((SnapsLayoutControl) c);
        }
        return controls;
    }

    public SnapsControl getLayoutByProperty(String property) {
        for (SnapsControl c : _layer_layout) {
            if (c.getSnsproperty().equals(property))
                return c;

        }
        return null;
    }

    public SnapsControl getLayoutByRegData(String regName, String regValue) {
        for (SnapsControl c : _layer_layout) {
            if (regName.equals(c.regName) && regValue.equals(c.regValue))
                return c;

        }
        return null;
    }

    public SnapsControl getControlByProperty(String property) {
        for (SnapsControl c : _layer_control) {
            if (c.getSnsproperty().equals(property))
                return c;

        }
        return null;
    }

    public ArrayList<SnapsControl> getControlListByProperty(String property) {
        ArrayList<SnapsControl> controls = new ArrayList<SnapsControl>();
        for (SnapsControl c : _layer_control) {
            if (c.getSnsproperty().equals(property))
                controls.add(c);
        }
        return controls;
    }

    public SnapsControl getControlByPos(String x, String y) {
        for (SnapsControl c : _layer_control) {
            if (x.equals(c.x) && y.equals(c.y))
                return c;

        }
        return null;
    }

    /**
     * 오브젝트 리스트
     *
     * @return
     */
    public ArrayList<SnapsControl> getControlList() {
        return this._layer_control;
    }

    /***
     * 텍스트컨트롤만 반환하는 함수..
     *
     * @return
     */
    public ArrayList<SnapsControl> getTextControlList() {
        ArrayList<SnapsControl> textControls = new ArrayList<SnapsControl>();

        for (SnapsControl c : this._layer_control) {
            if (c instanceof SnapsTextControl) {
                textControls.add(c);
            }
        }

        return textControls;
    }

    /***
     * clipart 컨트롤만 반환하는 함수..
     *
     * @return
     */
    public ArrayList<SnapsControl> getClipartControlList() {
        ArrayList<SnapsControl> cliparts = new ArrayList<SnapsControl>();

        for (SnapsControl c : this._layer_control) {
            if (c instanceof SnapsClipartControl) {
                cliparts.add(c);
            }
        }

        //키링 상품 때문에 추가
        //키링에서 키홀이 있는데 키홀은 스티커 범주에 속한다.
        //참고로 Clipart는 스티커 범주이다.
        for (SnapsControl c : this._layer_control) {
            if (c instanceof SnapsHoleControl || c instanceof SnapsHelperControl || c instanceof SnapsStickControl || c instanceof SnapsSceneMaskControl ||
                c instanceof SnapsSceneCutControl) {
                cliparts.add(c);
            }
        }

        return cliparts;
    }

    /**
     * 폼 리스트
     *
     * @return
     */
    public ArrayList<SnapsControl> getFormList() {
        return this._layer_form;
    }

    public int getOriginWidth() {
        return Integer.parseInt(width);
    }

    public int getWidth() {
        return Integer.parseInt(width);
    }

    public int getHeight() {
        return Integer.parseInt(height);
    }

    int getWidthAddMaxPage() {
        return Integer.parseInt(width) + maxPageX;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int getMaxPageX() {
        return maxPageX;
    }

    public void setMaxPageX(int maxPageX) {
        this.maxPageX = maxPageX;
    }

    public boolean isEditedPage() {
        return isExistImageDataInPage() || isExistTextDataInPage();
    }

    private boolean isExistTextDataInPage() {
        ArrayList<SnapsControl> textControls = getTextControlList();
        for (SnapsControl c : textControls) {
            if (!StringUtil.isEmpty(((SnapsTextControl) c).text)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExistImageDataInPage() {
        for (SnapsControl control : getLayoutList()) {
            if (control instanceof SnapsLayoutControl) {
                SnapsLayoutControl snapsLayoutControl = (SnapsLayoutControl) control;
                if (snapsLayoutControl.type.equalsIgnoreCase("local_resource"))
                    continue;

                if (snapsLayoutControl.regName.equalsIgnoreCase(""))
                    continue;

                if (snapsLayoutControl.type.equalsIgnoreCase("browse_file") && snapsLayoutControl.imgData != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getImageLayoutControlCountOnPage() {
        ArrayList<SnapsControl> snapsControlList = getLayoutList();
        if (snapsControlList == null) return 0;
        int result = 0;
        for (SnapsControl control : snapsControlList) {
            if (control != null && control instanceof SnapsLayoutControl) {
                if (((SnapsLayoutControl) control).type.equalsIgnoreCase("browse_file"))
                    result++;
            }
        }
        return result;
    }

    public SnapsLayoutControl getFirstImageLayoutControlOnPage() {
        ArrayList<SnapsControl> snapsControlList = getLayoutList();
        if (snapsControlList == null) return null;

        SnapsLayoutControl layoutControl = null;
        for (SnapsControl control : snapsControlList) {
            if (control != null && control instanceof SnapsLayoutControl) {
                SnapsLayoutControl snapsLayoutControl = (SnapsLayoutControl) control;
                if (snapsLayoutControl.type.equalsIgnoreCase("browse_file")) {
                    layoutControl = snapsLayoutControl;
                    break;
                }
            }
        }
        return layoutControl;
    }

    public int getImageCountOnPage() {
        ArrayList<SnapsControl> snapsControlList = getLayoutList();
        if (snapsControlList == null) return 0;
        int result = 0;
        for (SnapsControl control : snapsControlList) {
            if (control != null && control instanceof SnapsLayoutControl) {
                SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                if (layoutControl.type.equalsIgnoreCase("browse_file") && layoutControl.imgData != null) {
                    result++;
                }
            }
        }
        return result;
    }

    public List<MyPhotoSelectImageData> getImageDataListOnPage() {
        ArrayList<SnapsControl> snapsControlList = getLayoutList();
        if (snapsControlList == null) return null;
        List<MyPhotoSelectImageData> result = new ArrayList<>();
        for (SnapsControl control : snapsControlList) {
            if (control != null && control instanceof SnapsLayoutControl) {
                SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                if (layoutControl.type.equalsIgnoreCase("browse_file")) {
                    result.add(layoutControl.imgData);
                }
            }
        }
        return result;
    }

    public boolean isExistLowResolutionImageData() {
        ArrayList<SnapsControl> snapsControlList = getLayoutList();
        if (snapsControlList == null) return false;
        for (SnapsControl control : snapsControlList) {
            if (control != null && control instanceof SnapsLayoutControl) {
                SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                if (layoutControl.type.equalsIgnoreCase("browse_file")) {
                    if (layoutControl.isNoPrintImage) return true;
                }
            }
        }
        return false;
    }

    /**
     * @param xml
     * @return
     */
    public SnapsXML getSavePageXML(SnapsXML xml) {
        try {

            // <scene type="title" width="594" height="422" layout="TBA5_BIL_1u" multiform="TBA5MF_T1_1u" sub_type="" side="">
            xml.startTag(null, "scene");
            xml.attribute(null, "rc", "0 0 " + this.width + " " + this.height);
            xml.attribute(null, "embedCount", this.embedCount);
            xml.attribute(null, "orgMultiformId", this.orgMultiformId);
            xml.attribute(null, "orgBgId", this.orgBgId);
            xml.attribute(null, "type", this.type);
            xml.attribute(null, "layout", this.layout);
            xml.attribute(null, "border", this.border);
            xml.attribute(null, "background", this.background);
            xml.attribute(null, "side", this.side);
            xml.attribute(null, "multiform", this.multiformId);
            if (StringUtil.isEmpty(multiformId)) {
                xml.attribute(null, "multiformId", this.multiformId);
            }
            xml.attribute(null, "templateCode", this.templateCode);

            xml.attribute(null, "isMakedPageThumbnailFile", this.isMakedPageThumbnailFile ? "true" : "false");

            if (Config.isCalendar()) {
                int nStartYear = 0;
                int nStartMonth = 0;
                nStartYear = GetTemplateXMLHandler.getStartYear();
                nStartMonth = GetTemplateXMLHandler.getStartMonth();
                xml.attribute(null, "year", String.format("%d", nStartYear));
                xml.attribute(null, "month", String.format("%d", nStartMonth));
                Dlog.d("getSavePageXML() startYear:" + nStartYear);

            } else {
                xml.attribute(null, "year", this.year);
                xml.attribute(null, "month", this.month);

            }

            xml.attribute(null, "dynamicMode", this.dynamicMode);
            xml.attribute(null, "width", this.width);
            xml.attribute(null, "height", this.height);
            xml.attribute(null, "layoutIndex", String.valueOf(this.pageLayoutIDX));
            xml.attribute(null, "prnt_cnt", String.valueOf(this.quantity));

            {
                xml.startTag(null, "layer");
                xml.attribute(null, "name", "background_layer");
                this.getSnapsControlSaveXML(xml, getBgList());
                xml.endTag(null, "layer");
            }

            {
                xml.startTag(null, "layer");
                xml.attribute(null, "name", "image_layer");

                if (imageLayerRect != null) {
                    xml.attribute(null, "x", String.valueOf(imageLayerRect.left));
                    xml.attribute(null, "y", String.valueOf(imageLayerRect.top));
                    xml.attribute(null, "width", String.valueOf(imageLayerRect.width()));
                    xml.attribute(null, "height", String.valueOf(imageLayerRect.height()));
                }

                this.getSnapsControlSaveXML(xml, getLayoutList());

                xml.endTag(null, "layer");
            }

            {
                xml.startTag(null, "layer");
                xml.attribute(null, "name", "form_layer");
                this.getSnapsControlSaveXML(xml, getFormList());
                xml.endTag(null, "layer");

            }
            // form_layer
            // 마블 액자로 인해 신설..
            ArrayList<SnapsControl> cliparts = getClipartControlList();
            {
                xml.startTag(null, "layer");
                xml.attribute(null, "name", "control_layer");

                this.getSnapsControlSaveXML(xml, cliparts);
                xml.endTag(null, "layer");
            }

            this.getSnapsControlSaveXML(xml, getTextControlList()); // 텍스트만 걸러낸다.

            xml.endTag(null, "scene");

            // 테마북때문에 가격정보 추가 (수정기능 추가로 )

        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("makeSaveXML getSavePageXML exception : " + e.toString());
        }

        Dlog.d("getSavePageXML() xml:" + xml.toString());
        return xml;
    }

    /**
     * @param xml
     * @param list
     * @return
     */
    private SnapsXML getSnapsControlSaveXML(SnapsXML xml, ArrayList<SnapsControl> list) {
        for (SnapsControl item : list) {
            xml = item.getControlSaveXML(xml);
        }

        return xml;
    }

    private String getPagePixelWidth(int pageIdx, SnapsTemplateInfo info) {
        return info != null && pageIdx == 0 ? info.F_PAGE_PIXEL_WIDTH : width;
    }

    /**
     * @param xml
     * @param info
     * @param pageIdx
     * @return
     */
    public SnapsXML getAuraOrderPageXML(SnapsXML xml, SnapsTemplateInfo info, int pageIdx) {
        SnapsLogger.appendOrderLog("write aura xml getAuraOrderPageXML point 1");
        if (Config.isCalendar() && this.type.equals("hidden")) {
            if (Config.isWoodBlockCalendar()) { //우브블럭 달력 앞에는 히든 페이지가 들어간다.
            } else {
                return xml;
            }
        }

        try {

            float coverDifX = 0;
            float coverDifY = 0;
            String editWidth = info.F_PAGE_PIXEL_WIDTH;
            String editHeight = info.F_PAGE_PIXEL_HEIGHT;

            int templatePagePixelWidth = !StringUtil.isEmpty(editWidth) ? Integer.parseInt(editWidth) : 0;
            int pageWidth = !StringUtil.isEmpty(width) ? Integer.parseInt(width) : 0;

            boolean isSameMmWidthAndWidth = templatePagePixelWidth == pageWidth;
            boolean isAddPageTag = false;

            if (Const_PRODUCT.isExistPageTag()) {
                xml.startTag(null, "page");
                isAddPageTag = true;

                if (this.type.equals("page")) {
                    xml.attribute(null, "pageIdx", Integer.toString(pageIdx));

                    if (Const_PRODUCT.isPackageProduct()) {
                        xml.attribute(null, "type", "page");
                        if (Const_PRODUCT.isTtabujiProduct())
                            xml.attribute(null, "sizeType", Const_PRODUCT.PRODUCT_SIZE_TYPE_PACKAGE_TTAEBUJI);
                    } else
                        xml.attribute(null, "type", this.type);

                    if (Config.isSnapsSticker()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else if (Const_PRODUCT.isPackageProduct()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else if (Const_PRODUCT.isCardProduct()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else if (Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isNewYearsCardProduct()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else if (Config.isWoodBlockCalendar()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else {
                        xml.attribute(null, "effectivePage", "split_both");
                    }

                    if (Const_PRODUCT.isNewYearsCardProduct() || Const_PRODUCT.isCardProduct()) {
                        if (isSameMmWidthAndWidth) {
                            xml.attribute(null, "mmWidth", info.F_PAGE_MM_WIDTH);
                            xml.attribute(null, "mmHeight", info.F_PAGE_MM_HEIGHT);
                        } else {
                            xml.attribute(null, "mmWidth", info.F_PAGE_MM_HEIGHT);
                            xml.attribute(null, "mmHeight", info.F_PAGE_MM_WIDTH);
                        }
                    } else {
                        xml.attribute(null, "mmWidth", info.F_PAGE_MM_WIDTH);
                        xml.attribute(null, "mmHeight", info.F_PAGE_MM_HEIGHT);
                    }

                    if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewYearsCardProduct()) {
                        if (pageIdx > 0 && pageIdx % 2 == 0) { //0은 커버니까
                            xml.attribute(null, "prnt_cnt", String.valueOf(getQuantity()));
                        }
                    } else if (Const_PRODUCT.isTransparencyPhotoCardProduct() || (Const_PRODUCT.isStikerGroupProduct() && !Const_PRODUCT.isDIYStickerProduct()) || Const_PRODUCT.isPosterGroupProduct() || (Const_PRODUCT.isSloganProduct() && pageIdx % 2 == 0)) {
                        xml.attribute(null, "prnt_cnt", String.valueOf(getQuantity()));
                    }

                    xml.startTag(null, "editinfo");

                    if (Const_PRODUCT.isNewYearsCardProduct() || Const_PRODUCT.isCardProduct()) {
                        if (isSameMmWidthAndWidth) {
                            xml.attribute(null, "editWidth", getPagePixelWidth(pageIdx, info));
                            xml.attribute(null, "editHeight", info.F_PAGE_PIXEL_HEIGHT);
                        } else {
                            xml.attribute(null, "editWidth", info.F_PAGE_PIXEL_HEIGHT);
                            xml.attribute(null, "editHeight", getPagePixelWidth(pageIdx, info));
                        }
                    } else {
                        xml.attribute(null, "editWidth", getPagePixelWidth(pageIdx, info));
                        xml.attribute(null, "editHeight", info.F_PAGE_PIXEL_HEIGHT);
                    }

                    xml.endTag(null, "editinfo");

                    editWidth = getPagePixelWidth(pageIdx, info);
                    editHeight = info.F_PAGE_PIXEL_HEIGHT;

                } else if (this.type.equals("title")) {
                    xml.attribute(null, "pageIdx", Integer.toString(pageIdx));
                    xml.attribute(null, "type", this.type);

                    if (Config.isSnapsSticker()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else if (Config.isThemeBook()) {
                        xml.attribute(null, "effectivePage", "split_right");
                    } else if (Config.isCalendar()) {
                        if (Config.isWoodBlockCalendar()) {
                            xml.attribute(null, "effectivePage", "single");
                        } else {
                            xml.attribute(null, "effectivePage", "split_both");
                        }
                    } else {
                        xml.attribute(null, "effectivePage", "single");
                    }

                    xml.attribute(null, "mmWidth", info.F_TITLE_MM_WIDTH);
                    xml.attribute(null, "mmHeight", info.F_TITLE_MM_HEIGHT);

                    xml.startTag(null, "editinfo");
                    xml.attribute(null, "editWidth", getPagePixelWidth(pageIdx, info));
                    xml.attribute(null, "editHeight", info.F_PAGE_PIXEL_HEIGHT);
                    xml.endTag(null, "editinfo");

                    editWidth = getPagePixelWidth(pageIdx, info);
                    editHeight = info.F_PAGE_PIXEL_HEIGHT;
                } else if (this.type.equals("cover")) {
                    xml.attribute(null, "pageIdx", Integer.toString(pageIdx));
                    xml.attribute(null, "type", this.type);

                    if (Config.isSnapsSticker()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else if (Const_PRODUCT.isPackageProduct()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else if (Const_PRODUCT.isCardProduct()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else if (Const_PRODUCT.isPhotoCardProduct()) {
                        xml.attribute(null, "effectivePage", "single");
                    } else {
                        xml.attribute(null, "effectivePage", "single");
                    }

                    if (Const_PRODUCT.isCardProduct() && pageIdx % 2 == 0) {
                        xml.attribute(null, "prnt_cnt", String.valueOf(getQuantity()));
                    }

                    xml.attribute(null, "coverType", info.F_COVER_TYPE);

                    float mmPageW = 0.0f;
                    // 테마북 소프트 북...
                    if (Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isSimpleMakingBook() || Const_PRODUCT.isSNSBook()) { // 테마북은 페이지장수에 따라
                        // mm가 바뀐다.

                        // 소프트북인 경우 전체 mmWidth는 줄어들지 않는다...
                        float mmWidth = Float.parseFloat(info.F_COVER_EDGE_WIDTH) + Math.max(0.f, addmmWidth);
                        Dlog.d("getAuraOrderPageXML() mmWidth:" + mmWidth);

                        // if (info.F_COVER_TYPE.equalsIgnoreCase("hard")) {
                        if (info.getCoverType() == COVER_TYPE.HARD_COVER) {
                            xml.attribute(null, "CoverEdgeType", info.F_COVEREDGE_TYPE);
                            // 기본 템플릿에 8mm가 잡힌 상태이기때문에..
                            mmPageW = Float.parseFloat(info.F_COVER_MM_WIDTH) + addmmWidth;// info.F_SPINE_WIDTH - 8.f;
                            xml.attribute(null, "mmWidth", String.valueOf((int) mmPageW));
                        } else {
                            xml.attribute(null, "CoverEdgeType", "0");
                            xml.attribute(null, "mmWidth", String.valueOf(mmWidth));
                        }

                        xml.attribute(null, "mmHeight", info.F_COVER_MM_HEIGHT);
                        xml.attribute(null, "mmRefWidth", String.valueOf(mmWidth));
                        xml.attribute(null, "mmRefHeight", info.F_COVER_EDGE_HEIGHT);

                    } else {// 테마북
                        if (!Config.isCalendar()) {
                            xml.attribute(null, "mmWidth", info.F_COVER_EDGE_WIDTH);
                            xml.attribute(null, "mmHeight", info.F_COVER_EDGE_HEIGHT);
                        } else {
                            xml.attribute(null, "mmWidth", info.F_COVER_MM_WIDTH);
                            xml.attribute(null, "mmHeight", info.F_COVER_MM_HEIGHT);

                        }
                        xml.attribute(null, "mmRefWidth", info.F_COVER_MM_WIDTH);
                        xml.attribute(null, "mmRefHeight", info.F_COVER_MM_HEIGHT);
                    }

                    xml.startTag(null, "editinfo");
                    xml.attribute(null, "midMM", "true");

                    if (info.getCoverType() == COVER_TYPE.HARD_COVER) {

                        coverDifX = (Float.parseFloat(info.F_COVER_VIRTUAL_WIDTH) - getWidthAddMaxPage()) / 2;
                        coverDifY = (Float.parseFloat(info.F_COVER_VIRTUAL_HEIGHT) - Integer.parseInt(this.height)) / 2;

                        editWidth = String.valueOf(getWidthAddMaxPage() + (coverDifX * 2));  //이거 결국  editWidth = Float.parseFloat(info.F_COVER_VIRTUAL_WIDTH) 이거랑 같잔아!!!
                        editHeight = String.valueOf(Integer.parseInt(this.height) + (coverDifY * 2));
                        if (Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isSimpleMakingBook() || Const_PRODUCT.isSNSBook()) {
                            editWidth = info.F_COVER_VIRTUAL_WIDTH;
                            xml.attribute(null, "editWidth", info.F_COVER_VIRTUAL_WIDTH);
                            xml.attribute(null, "editHeight", info.F_COVER_VIRTUAL_HEIGHT);
                            // 하드 커버일 경우..
                            xml.attribute(null, "midWidth", String.valueOf(info.mmMidWidth));
                        } else {

                            xml.attribute(null, "midWidth", info.mmMidWidth + "");
                            xml.attribute(null, "editWidth", editWidth);
                            xml.attribute(null, "editHeight", editHeight);
                        }
                    } else {

                        xml.attribute(null, "midWidth", String.valueOf(info.mmMidWidth));
                        xml.attribute(null, "editWidth", String.valueOf(getWidthAddMaxPage()));
                        xml.attribute(null, "editHeight", this.height);

                        editWidth = getWidthAddMaxPage() + "";
                        editHeight = this.height;
                    }

                    xml.endTag(null, "editinfo");
                } else if (this.type.equals("hidden")) {
                    xml.attribute(null, "pageIdx", Integer.toString(pageIdx));
                    if (Const_PRODUCT.isPackageProduct()) {
                        if (Const_PRODUCT.isTtabujiProduct()) { // 떼부지는 인덱스 페이지가 없다.
                            xml.attribute(null, "type", "page");
                        } else {
                            int indexPage = 1;

                            if (pageIdx == indexPage)
                                xml.attribute(null, "type", "index");
                            else {
                                if (Const_PRODUCT.isNewPolaroidPackProduct()) {
                                    //뉴 플라로이드는 hidden 그대로 넣는다.
                                    xml.attribute(null, "type", (pageIdx == 0 ? "page" : this.type));
                                } else
                                    xml.attribute(null, "type", "page");
                            }
                        }
                    } else if (Config.isWoodBlockCalendar()) { //FIXME...리펙토링 필요...
                        int indexPage = 1;
                        if (pageIdx == indexPage)
                            xml.attribute(null, "type", "index");
                        else
                            xml.attribute(null, "type", "page");
                    } else if (Const_PRODUCT.isPhotoCardProduct()) {
                        int indexPage = 1;
                        if (pageIdx == indexPage)
                            xml.attribute(null, "type", "index");
                        else
                            xml.attribute(null, "type", "page");
                    } else if (Const_PRODUCT.isTransparencyPhotoCardProduct()) {
                        int indexPage = 0;
                        if (pageIdx == indexPage)
                            xml.attribute(null, "type", "index");
                        else
                            xml.attribute(null, "type", "page");
                    } else if (Const_PRODUCT.isNewWalletProduct()) {
                        int indexPage = 1;
                        if (pageIdx == indexPage)
                            xml.attribute(null, "type", "index");
                        else
                            xml.attribute(null, "type", "page");
                    } else if (Const_PRODUCT.isNewYearsCardProduct()) {
                        int indexPage = 1;
                        if (pageIdx == indexPage)
                            xml.attribute(null, "type", "index");
                        else
                            xml.attribute(null, "type", "page");
                    } else
                        xml.attribute(null, "type", this.type);

                    if (Const_PRODUCT.isTtabujiProduct())
                        xml.attribute(null, "sizeType", Const_PRODUCT.PRODUCT_SIZE_TYPE_PACKAGE_TTAEBUJI);

                    xml.attribute(null, "effectivePage", "single");

                    xml.attribute(null, "mmWidth", info.F_PAGE_MM_WIDTH);
                    xml.attribute(null, "mmHeight", info.F_PAGE_MM_HEIGHT);

                    xml.startTag(null, "editinfo");
                    xml.attribute(null, "editWidth", getPagePixelWidth(pageIdx, info));
                    xml.attribute(null, "editHeight", info.F_PAGE_PIXEL_HEIGHT);
                    xml.endTag(null, "editinfo");

                    editWidth = getPagePixelWidth(pageIdx, info);
                    ;
                    editHeight = info.F_PAGE_PIXEL_HEIGHT;
                }
            } else {// 액자인 경우.. 폴라로이드
                xml.startTag(null, "editinfo");
                xml.attribute(null, "editWidth", getPagePixelWidth(pageIdx, info));
                xml.attribute(null, "editHeight", info.F_PAGE_PIXEL_HEIGHT);
                xml.endTag(null, "editinfo");
            }
            SnapsLogger.appendOrderLog("write aura xml getAuraOrderPageXML point 2");

            this.getSnapsControlAuraOrderXML(xml, getBgList(), Float.parseFloat(editWidth), Float.parseFloat(editHeight));
            SnapsXML snapsXml = this.getSnapsControlAuraOrderXML(xml, getLayoutList(), coverDifX, coverDifY);
            if (snapsXml == null) {
                SnapsLogger.appendOrderLog("write aura xml getAuraOrderPageXML exception");
                return null;
            }
            SnapsLogger.appendOrderLog("write aura xml getAuraOrderPageXML point 3");


//				// Edit에서만 폼을 사용한다.
            this.getSnapsControlAuraOrderXML(xml, getFormList(), 0, 0);


            SnapsLogger.appendOrderLog("write aura xml getAuraOrderPageXML point 4");

            this.getSnapsControlAuraOrderXML(xml, getControlList(), coverDifX, coverDifY);

            SnapsLogger.appendOrderLog("write aura xml getAuraOrderPageXML point 5");

            // 텀블러에 부채꼴 레이아웃을 추가한다.
            addThumblrLayout(snapsXml, info, pageIdx);

            if (isAddPageTag) xml.endTag(null, "page");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xml;
    }

    /**
     * @param xml
     * @param list
     * @param difX
     * @param difY
     * @return
     */
    private SnapsXML getSnapsControlAuraOrderXML(SnapsXML xml, ArrayList<SnapsControl> list, float difX, float difY) {
        boolean isDesignNoteProduct = Const_PRODUCT.isDesignNoteProduct();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            SnapsControl item = list.get(i);

            if (isDesignNoteProduct && item instanceof SnapsTextControl)
                ;// 디자인 노트인 경우 텍스는 제외한다....
            else {
                if (xml == null) return null;

                if (item != null) xml = item.getControlAuraOrderXML(xml, this, difX, difY);
            }

        }

        return xml;
    }

    /***
     * 페이지 안에 인쇄불가 사진이 있는지 확인하는 함수..
     *
     * @return
     */
    public boolean isExistResolutionImage() {
        for (SnapsControl c : getLayoutList()) {
            if (((SnapsLayoutControl) c).isNoPrintImage)
                return true;
        }

        return false;
    }

    public boolean isExistUploadFailedImage() {
        for (SnapsControl c : getLayoutList()) {
            if (((SnapsLayoutControl) c).isUploadFailedOrgImg)
                return true;
        }

        return false;
    }

    public SnapsPage copyPage(int pageIndex) {
        return copyPage(pageIndex, false);
    }

    public SnapsPage copyPage(int pageIndex, boolean isContentsCopy) {
        SnapsPage page = new SnapsPage(pageIndex, info);
        page.type = type;
        page.setWidth(getWidth() + "");
        page.height = height;
        page.orgMultiformId = orgMultiformId;
        page.multiformId = multiformId;
        page.templateCode = templateCode;
        page.orgBgId = orgBgId;
        page.border = border;
        page.layout = layout;
        page.background = background;
        page.side = side;
        page.isMakedPageThumbnailFile = false;

        for (SnapsControl c : getBgList()) {
            if (!(c instanceof SnapsBgControl))
                continue;

            SnapsBgControl snapsBgControl = (SnapsBgControl) c;

            SnapsBgControl control = new SnapsBgControl();
            control.layerName = c.layername;
            control.regName = c.regName;
            control.regValue = c.regValue;
            control.type = snapsBgControl.type;
            control.fit = snapsBgControl.fit;
            control.bgColor = snapsBgControl.bgColor;
            control.coverColor = snapsBgControl.coverColor;
            control.srcTarget = snapsBgControl.srcTarget;
            control.srcTargetType = snapsBgControl.srcTargetType;
            control.resourceURL = snapsBgControl.resourceURL;
            control.getVersion = snapsBgControl.getVersion;
            control.setX(String.valueOf(snapsBgControl.getX()));
            control.setSnsproperty(c.getSnsproperty());
            control.setTextType(c.getTextType());

            page.addBg(control);
        }

        for (SnapsControl c : getLayoutList()) {

            if (!(c instanceof SnapsLayoutControl))
                continue;

            // 로컬 리소스는 추가하지 않는다. 타입 확인..
            if (((SnapsLayoutControl) c).type.equals("local_resource"))
                continue;

            SnapsLayoutControl snapsLayoutControl = (SnapsLayoutControl) c;

            // SnapsTControl일때 처리하기..
            SnapsLayoutControl layout = new SnapsLayoutControl();

            layout.regName = c.regName;
            layout.regValue = c.regValue;
            layout.type = snapsLayoutControl.type;
            layout.fit = snapsLayoutControl.fit;
            layout.bgColor = snapsLayoutControl.bgColor;
            layout.srcTargetType = snapsLayoutControl.srcTargetType;
            layout.srcTarget = snapsLayoutControl.srcTarget;
            layout.resourceURL = snapsLayoutControl.resourceURL;
            layout.mask = snapsLayoutControl.mask;
            layout.maskURL = snapsLayoutControl.maskURL;
            layout.maskType = snapsLayoutControl.maskType;
            layout.maskRadius = snapsLayoutControl.maskRadius;

            layout.setX(snapsLayoutControl.getX() + "");
            layout.y = snapsLayoutControl.y;
            layout.width = snapsLayoutControl.width;
            layout.height = snapsLayoutControl.height;
            layout.angle = snapsLayoutControl.angle;
            layout.border = snapsLayoutControl.border;
            layout.isClick = snapsLayoutControl.isClick;
            if (isContentsCopy && snapsLayoutControl.imgData != null) {
                layout.imgData = new MyPhotoSelectImageData();
                layout.imgData.set(snapsLayoutControl.imgData);
                layout.img_width = snapsLayoutControl.img_width;
                layout.img_height = snapsLayoutControl.img_height;
                layout.img_x = snapsLayoutControl.img_x;
                layout.img_y = snapsLayoutControl.img_y;
                layout.freeAngle = snapsLayoutControl.freeAngle;
                layout.angle = snapsLayoutControl.angle;
            } else
                layout.imgData = null;
            layout.angle = "0";
            layout.imagePath = "";
            layout.imageLoadType = 0;
            layout.setSnsproperty(c.getSnsproperty());
            layout.setTextType(c.getTextType());
            layout.stick_dirction = snapsLayoutControl.stick_dirction;
            layout.stick_margin = snapsLayoutControl.stick_margin;
            layout.stick_target = snapsLayoutControl.stick_target;
            layout.id = snapsLayoutControl.id;
            layout.isImageFull = snapsLayoutControl.isImageFull;
            layout.isNoPrintImage = snapsLayoutControl.isNoPrintImage;

            page.addLayout(layout);
        }

        for (SnapsControl c : getClipartControlList()) {
            if (!(c instanceof SnapsClipartControl))
                continue;

            SnapsClipartControl snapsClipartControl = (SnapsClipartControl) c;

            SnapsClipartControl clipart = new SnapsClipartControl();

            clipart._controlType = SnapsControl.CONTROLTYPE_STICKER;
            clipart.angle = c.angle;
            clipart.alpha = snapsClipartControl.alpha;
            clipart.clipart_id = snapsClipartControl.clipart_id;
            clipart.setX(c.x);
            clipart.y = c.y;
            clipart.width = c.width;
            clipart.height = c.height;
            clipart.resourceURL = snapsClipartControl.resourceURL;
            clipart.srcTarget = snapsClipartControl.srcTarget;
            clipart.setSnsproperty(c.getSnsproperty());
            clipart.stick_dirction = snapsClipartControl.stick_dirction;
            clipart.stick_margin = snapsClipartControl.stick_margin;
            clipart.stick_target = snapsClipartControl.stick_target;
            clipart.id = snapsClipartControl.id;
            clipart.setOverPrint(snapsClipartControl.isOverPrint());

            page.addControl(clipart);
        }

        for (SnapsControl c : getTextControlList()) {
            if (!(c instanceof SnapsTextControl))
                continue;

            SnapsTextControl snapsTextControl = (SnapsTextControl) c;

            SnapsTextControl textControl = new SnapsTextControl();

            textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;
            textControl.regName = c.regName;
            textControl.regValue = c.regValue;
            textControl.setX(snapsTextControl.getX() + "");
            textControl.y = snapsTextControl.y;
            textControl.width = snapsTextControl.width;
            textControl.height = snapsTextControl.height;
            textControl.isClick = snapsTextControl.isClick;
            textControl.format.fontFace = snapsTextControl.format.fontFace;
            textControl.format.alterFontFace = snapsTextControl.format.alterFontFace;
            textControl.format.fontSize = snapsTextControl.format.fontSize;
            textControl.format.fontColor = snapsTextControl.format.fontColor;
            textControl.format.baseFontColor = snapsTextControl.format.baseFontColor;
            textControl.format.align = snapsTextControl.format.align;
            textControl.format.bold = snapsTextControl.format.bold;
            textControl.format.italic = snapsTextControl.format.italic;
            textControl.format.underline = snapsTextControl.format.underline;
            textControl.format.verticalView = snapsTextControl.format.verticalView;
            textControl.format.setOverPrint(snapsTextControl.format.isOverPrint());
            textControl.albumMode = snapsTextControl.albumMode;
            textControl.setSnsproperty(c.getSnsproperty());
            textControl.setTextType(c.getTextType());
            textControl.setFormat(c.getFormat());
            textControl.initialText = snapsTextControl.initialText;
            textControl.emptyText = snapsTextControl.emptyText;
            textControl.stick_dirction = snapsTextControl.stick_dirction;
            textControl.stick_margin = snapsTextControl.stick_margin;
            textControl.stick_target = snapsTextControl.stick_target;
            textControl.angle = snapsTextControl.angle;
            textControl.id = snapsTextControl.id;
            if (isContentsCopy) {
                textControl.text = snapsTextControl.text;
                textControl.htmlText = snapsTextControl.htmlText;
                if (snapsTextControl.textList != null) {
                    ArrayList<LineText> arrayList = snapsTextControl.textList;
                    for (LineText lineText : arrayList) {
                        textControl.textList.add(lineText);
                    }
                }
            }

            page.addControl(textControl);
        }

        for (SnapsControl c : getFormList()) {
            if (!(c instanceof SnapsFormControl))
                continue;

            SnapsFormControl snapsFormControl = (SnapsFormControl) c;

            SnapsFormControl form = new SnapsFormControl();
            form.layerName = c.layername;
            form.regName = c.regName;
            form.regValue = c.regValue;
            form.type = snapsFormControl.type;
            form.fit = snapsFormControl.fit;
            form.bgColor = snapsFormControl.bgColor;
            form.srcTargetType = snapsFormControl.srcTargetType;
            form.srcTarget = snapsFormControl.srcTarget;
            form.resourceURL = snapsFormControl.resourceURL;
            form.getVersion = snapsFormControl.getVersion;
            form.angle = snapsFormControl.angle;
            form.setSnsproperty(c.getSnsproperty());

            page.addForm(form);
        }

        return page;
    }

    /**
     * 하단 썸네일 만들 때 사용한다.
     * (썸네일 크기에 맞게 크기와 위치를 교정한다.)
     */
    public void setScaledDimensions(float scaleX, float scaleY) {
        width = String.valueOf((int) (getWidth() * scaleX));
        height = String.valueOf((int) (getHeight() * scaleY));
        if (info != null && info.F_PAGE_PIXEL_WIDTH != null && info.F_PAGE_PIXEL_WIDTH.length() > 0)
            info.F_THUMBNAIL_PAGE_PIXEL_WIDTH = String.valueOf((int) (Float.parseFloat(info.F_PAGE_PIXEL_WIDTH) * scaleX));

        if (info != null && info.F_PAGE_PIXEL_HEIGHT != null && info.F_PAGE_PIXEL_HEIGHT.length() > 0)
            info.F_THUMBNAIL_PAGE_PIXEL_HEIGHT = String.valueOf((int) (Float.parseFloat(info.F_PAGE_PIXEL_HEIGHT) * scaleY));

        setScaledDimensions(getBgList(), scaleX, scaleY);
        setScaledDimensions(getLayoutList(), scaleX, scaleY);
        setScaledDimensions(getTextControlList(), scaleX, scaleY);
        setScaledDimensions(getClipartControlList(), scaleX, scaleY);
        setScaledDimensions(getFormList(), scaleX, scaleY);
    }

    private void setScaledDimensions(ArrayList<SnapsControl> snapsControlList, float scaleX, float scaleY) {
        if (snapsControlList == null) return;
        for (SnapsControl c : snapsControlList) {
            c.width = String.valueOf((int) (c.getIntWidth() * scaleX));
            c.height = String.valueOf((int) (c.getIntHeight() * scaleY));
            c.x = String.valueOf((int) (c.getIntX() * scaleX));
            c.y = String.valueOf((int) (c.getIntY() * scaleY));
        }
    }

    void addThumblrLayout(SnapsXML xml, SnapsTemplateInfo info, int pageIdx) {

        if (!Const_PRODUCT.isTumblerProduct() || pageIdx != 0)
            return;

        xml.startTag(null, "object");
        xml.attribute(null, "type", "sticker");
        xml.attribute(null, "alpha", "255");
        xml.attribute(null, "angle", "0");
        xml.attribute(null, "x", "0");
        xml.attribute(null, "y", "0");
        xml.attribute(null, "width", getPagePixelWidth(pageIdx, info));
        xml.attribute(null, "height", info.F_PAGE_PIXEL_HEIGHT);
        // 고급형
        if (Config.getPROD_CODE().equals(Const_PRODUCT.PRODUCT_TUMBLR_GRADE)) {
            // <object type="sticker" id="0390013030" alpha="255" angle="0" x="0" y="0" width="699" height="468”/>
            xml.attribute(null, "id", "0390013030");

        } else {// 보급형..
            // <object type="sticker" id="0390013029" alpha="255" angle="0" x="0" y="0" width="669" height="381"/>
            xml.attribute(null, "id", "0390013029");
        }

        xml.endTag(null, "object");

    }

    public ArrayList<SnapsControl> getLayerBgs() {
        return _layer_bg;
    }

    public ArrayList<SnapsControl> getLayerControls() {
        return _layer_control;
    }

    public ArrayList<SnapsControl> getLayerForms() {
        return _layer_form;
    }

    public String getSnsproperty() {
        return snsproperty;
    }

    public void setSnsproperty(String snsproperty) {
        this.snsproperty = snsproperty;
    }

    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
    }

    /**
     * 기준시간 이후에 있는 컨트롤를 삭제하는 함수..
     *
     * @param baseDate
     */
    public void removeControls(String baseDate) {
        long base = StringUtil.convertCreateStringToLong(baseDate);

        ArrayList<SnapsControl> deleteControls = new ArrayList<SnapsControl>();

        for (SnapsControl c : _layer_bg) {
            if (c.identifier.equals("") || StringUtil.convertCreateStringToLong(c.identifier) <= base)
                continue;

            deleteControls.add(c);
        }

        _layer_bg.removeAll(deleteControls);
        deleteControls.clear();

        for (SnapsControl c : _layer_layout) {
            if (c.identifier.equals("") || StringUtil.convertCreateStringToLong(c.identifier) <= base)
                continue;

            deleteControls.add(c);
        }

        _layer_layout.removeAll(deleteControls);
        deleteControls.clear();

        for (SnapsControl c : _layer_control) {
            if (c.identifier.equals("") || StringUtil.convertCreateStringToLong(c.identifier) <= base)
                continue;

            deleteControls.add(c);
        }

        _layer_control.removeAll(deleteControls);
        deleteControls.clear();

        for (SnapsControl c : _layer_form) {
            if (c.identifier.equals("") || StringUtil.convertCreateStringToLong(c.identifier) <= base)
                continue;

            deleteControls.add(c);
        }

        _layer_form.removeAll(deleteControls);
        deleteControls.clear();

    }

    /**
     * 우측페이지에 컨트롤이 있는지 확인하는 함수 카카오스토리북에서 오른쪽페이지가 비웠져있는때 처리를 위해...
     *
     * @return
     */
    public boolean isExistControls() {
        int halfLine = Integer.parseInt(width) / 2;

        for (SnapsControl c : _layer_layout) {
            if (c.getIntX() > halfLine)
                return true;
        }

        for (SnapsControl c : _layer_control) {
            // 컨트롤에서 우측 상단 날짜는 무시를 한다.
            if (c.getSnsproperty().equals("inner_month"))
                continue;
            if (c.getIntX() > halfLine)
                return true;
        }
        for (SnapsControl c : _layer_form) {
            if (c.getIntX() > halfLine)
                return true;
        }
        return false;
    }

    public boolean removeSticker(String snsProperty) {
        SnapsControl control = null;
        for (SnapsControl c : _layer_control) {
            if (c.getSnsproperty().equals(snsProperty)) {
                control = c;
                break;
            }
        }

        if (control != null) {
            _layer_control.remove(control);
            return true;
        }

        return false;
    }

    public void removeSticker(SnapsClipartControl control) {
        for (SnapsControl c : _layer_control) {
            if (c instanceof SnapsClipartControl && c.equals(control)) {
                _layer_control.remove(c);
                return;
            }
        }
    }

    public boolean removeText(String snsProperty) {
        SnapsControl control = null;
        for (SnapsControl c : _layer_control) {
            if (c.getSnsproperty().equals(snsProperty)) {
                control = c;
                break;
            }
        }

        if (control != null) {
            _layer_control.remove(control);
            return true;
        }

        return false;
    }

    public boolean removeImage(String snsProperty) {
        SnapsControl control = null;
        for (SnapsControl c : _layer_layout) {
            if (c.getSnsproperty().equals(snsProperty)) {
                control = c;
                break;
            }
        }

        if (control != null) {
            _layer_layout.remove(control);
            return true;
        }

        return false;
    }

    public boolean removeImagesBySnsProperty(String snsProperty) {
        boolean removed = false;
        if (_layer_layout != null && _layer_layout.size() > 0 && snsProperty != null) {
            for (int i = _layer_layout.size() - 1; i > -1; --i) {
                if (snsProperty.equals(_layer_layout.get(i).getSnsproperty())) {
                    _layer_layout.remove(i);
                    removed = true;
                }
            }
        }
        return removed;
    }

    public SnapsTextControl getTextControlByValue(String snsProperty, String value) {
        for (SnapsControl c : _layer_control) {
            if (c instanceof SnapsTextControl)
                if (snsProperty.equals(c.getSnsproperty()) && value.equals(c.regValue)) {
                    return (SnapsTextControl) c;
                }
        }
        return null;
    }

    public SnapsTextControl getTextControl(String snsProperty) {
        for (SnapsControl c : _layer_control) {
            if (c instanceof SnapsTextControl)
                if (c.getSnsproperty().equals(snsProperty)) {
                    return (SnapsTextControl) c;
                }
        }
        return null;
    }

    public ArrayList<SnapsTextControl> getTextControls(String snsProperty) {
        ArrayList<SnapsTextControl> list = new ArrayList<SnapsTextControl>();
        for (SnapsControl c : _layer_control) {
            if (c instanceof SnapsTextControl)
                if (c.getSnsproperty().equals(snsProperty)) {
                    list.add((SnapsTextControl) c);
                }
        }
        return list;
    }

    public void setIndexRect(String rect) {
        if (rect != null) {
            String[] r = rect.split(" ");
            if (r.length == 4) {
                index_x = Integer.parseInt(r[0]);
                index_y = Integer.parseInt(r[1]);
                index_width = Integer.parseInt(r[2]);
                index_heigth = Integer.parseInt(r[3]);
            }
        }
    }

    public BRect getImageLayerRect() {
        return imageLayerRect;
    }

    public void setImageLayerRect(BRect imageLayerRect) {
        this.imageLayerRect = imageLayerRect;
    }

    public Rect getIndexRect() {
        return new Rect(index_x, index_y, index_x + index_width, index_y + index_heigth);
    }

    /***
     * 컨트롤 아이디를 가지고 컨트롤를 가져오는 함수.
     *
     * @param identifier
     * @return
     */
    public SnapsTextControl getTextControlByID(String identifier) {
        for (SnapsControl c : _layer_control) {
            if (c instanceof SnapsTextControl)
                if (c.id.equals(identifier)) {
                    return (SnapsTextControl) c;
                }
        }
        return null;
    }

    public boolean removeText(SnapsTextControl control) {
        if (control != null) {
            _layer_control.remove(control);
            return true;
        }

        return false;
    }

    public void removeAllTextControl() {
        ArrayList<SnapsControl> controls = getControlList();
        if (controls == null) return;
        for (int ii = controls.size() - 1; ii >= 0; ii--) {
            SnapsControl c = controls.get(ii);
            if (c instanceof SnapsTextControl) {
                controls.remove(c);
            }
        }
    }

    /***
     * 텍스트 컨트롤의 auraOrderTextSize를 설정하는 함수.
     *
     * @param ratio
     */
    public void setTextControlFont(float ratio) {
        for (SnapsControl c : _layer_control) {
            if (c instanceof SnapsTextControl) {
                SnapsTextControl snapsTextControl = (SnapsTextControl) c;
                snapsTextControl.format.auraOrderFontSize = Float.parseFloat(snapsTextControl.format.fontSize) * ratio + "";
            }

        }
    }

    /***
     * 텍스트 컨트롤의 auraOrderTextSize를 설정하는 함수.
     *
     * @param ratio
     */
    public static void setTextControlFont(SnapsTextControl c, float ratio) {
        c.format.auraOrderFontSize = Float.parseFloat(c.format.fontSize) * ratio + "";
    }

    /***
     * 우측 영역에 레이아웃이 있는지 체크하는 함수 있으면 true 리턴..
     *
     * @return
     */
    public boolean isExistRightArea() {
        return false;
    }

    /***
     * 페이지에서 책등을 구하는 함수 없으면 null를 리턴한다.
     *
     * @return
     */
    public SnapsTextControl getBookStick() {
        for (SnapsControl control : getControlList()) {
            if (control instanceof SnapsTextControl) {
                SnapsTextControl snapsTextControl = (SnapsTextControl) control;
                if (snapsTextControl.format.verticalView.equalsIgnoreCase("true")) {
                    return snapsTextControl;
                }
            }
        }

        return null;
    }

    /***
     * BgControl이 있으면 리턴하는 함수..
     *
     * @return
     */
    public SnapsBgControl getBgControl() {
        if (_layer_bg.size() > 0)
            return (SnapsBgControl) _layer_bg.get(0);
        return null;
    }

    public SnapsControl getControlByStickerTarget(String id) {
        for (SnapsControl c : _layer_layout) {
            if (c.stick_target.equals(id))
                return c;
        }
        for (SnapsControl c : _layer_control) {
            if (c.stick_target.equals(id))
                return c;
        }
        return null;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isCover() {
        return "cover".equalsIgnoreCase(type);
    }

    public boolean isTitle() {
        return "title".equalsIgnoreCase(type);
    }
}
