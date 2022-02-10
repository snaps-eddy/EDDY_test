package com.snaps.common.structure.control;

import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.filters.ImageEffectBitmap;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.ColorUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.ui.RotateUtil;

import java.io.Serializable;

import errorhandle.logger.SnapsLogger;

public class SnapsLayoutControl extends SnapsControl implements iSnapsControlInterface, Parcelable, Serializable {
    private static final String TAG = SnapsLayoutControl.class.getSimpleName();
    private static final long serialVersionUID = 5182832533950878734L;
    final String FACEBOOK_BORDER_COLOR = "cbcccb";

    private int layoutControlIdx = -1;

    // imageData와 매칭시키기 위한 키
    private double imageDataKey = 0d;
    // source type
    public String type = "";
    // fill 정보
    public String fit = "";
    // 이미지 배경 색상
    public String bgColor = "";
    // 이미지 경로
    public String imagePath = "";
    // 이미지 리소스 타입
    public String srcTargetType = "";
    // 이미지 리소스
    public String srcTarget = "";
    // Year Seq
    public String imgYear = "";
    // Image Seq
    public String imgSeq = "";
    // Image Width
    public String imgWidth = "";
    // Image Height
    public String imgHeight = "";
    // org Image
    public String oriPath = "";
    // tiny Image
    public String tinyPath = "";
    // thumb Image
    public String thumPath = "";
    // 실제 파일명
    public String realFileName = "";
    // 이미지가 로컬이미지 인지 판단.
    public boolean local;
    // 이미지 마스크 ID
    public String mask = "";
    // 이미지 마스크 리소스 URL
    public String maskURL = "";
    // 이미지 마스크 타입 image, page
    public String maskType = "";
    // 이미지 마스크 Radius 값
    public String maskRadius = "";
    // Name
    public String name = "";

    // resourceURL
    public String resourceURL = "";

    // 0 = 카카오 스토리, 1 = 로컬 앨벌, 2 = 스냅스
    public int imageLoadType = 0;
    // Border값
    public String border = "";

    public boolean isVisible;

    public String uploadPath = "";
    public String sizeOrgImg = "0";
    public String mstPath = "";
    public String contentType = "";
    public String exchange = "false";
    public String helper = "false";
    public String noclip = "false";
    public String useAlpha = "false";
    public String alpha = "255";
    public String formItem = "false";
    public String checkFull = "false";
    public String stick = "";

    public String tempImageColor = "";

    /**
     * 선택된 이미지 정보
     **/
    public MyPhotoSelectImageData imgData;

    // 2014.08.04 bys 추가
    // 인화불가 여부
    public boolean isNoPrintImage = false;

    //업로드 실패된 이미지는 별도로 표시해 준다.
    public boolean isUploadFailedOrgImg = false;

    // 액자 틀...
    public String bordersinglecolortype = "", bordersinglealpha = "", bordersinglethick = "", bordersinglecolor = "";
    public String borderURL = "";

    public boolean isSnsBookCover = false; // 카카오북, 페이스북포토북, 인스타그램북 통합 사용을 위해

    public String facebookImageID = "";
    public String qrCodeUrl = "";
    //이미지 full
    public boolean isImageFull = false;

    public int getLayoutControlIdx() {
        return layoutControlIdx;
    }

    public void setLayoutControlIdx(int layoutControlIdx) {
        this.layoutControlIdx = layoutControlIdx;
    }

    @Override
    public SnapsXML getControlSaveXML(SnapsXML xml) {
        try {
            SnapsLogger.appendOrderLog("SnapsLayoutControl saveXML make start ");
            // 로컬 리소스는 xml을 만들지 않는다.
            if (type.equalsIgnoreCase("local_resource"))
                return xml;

            xml.startTag(null, "image");
            xml.attribute(null, "x", this.x);
            xml.attribute(null, "y", this.y);
            xml.attribute(null, "width", this.width);
            xml.attribute(null, "height", this.height);
            xml.attribute(null, "offsetX", getOffsetX() + "");
            xml.attribute(null, "offsetY", getOffsetY() + "");

            if (this.name != null && this.name.trim().length() > 0) {
                xml.attribute(null, "name", this.name);
            }

            // FIXME 자유 회전이 적용 된 경
            int angle = 0;

            try {
                angle = this.freeAngle + Integer.parseInt(getAngle());
            } catch (NumberFormatException e) {
                Dlog.e(TAG, e);
            }

            xml.attribute(null, "angle", String.valueOf(angle));

            xml.attribute(null, "angleClip", this.angleClip);
            xml.attribute(null, "border", this.border);
            xml.attribute(null, "isEditable", isClick);

            xml.attribute(null, "isNewKakaoCover", (this.isSnsBookCover ? "true" : "false"));

            // 사진틀...
            xml.attribute(null, "bordersinglecolortype", this.bordersinglecolortype);
            xml.attribute(null, "bordersinglethick", this.bordersinglethick);
            xml.attribute(null, "bordersinglealpha", this.bordersinglealpha);
            xml.attribute(null, "bordersinglecolor", this.bordersinglecolor);
            xml.attribute(null, "qrCodeUrl", (this.qrCodeUrl != null) ? this.qrCodeUrl : "");
            xml.attribute(null, "imagefull", (this.isImageFull ? "true" : "false"));

            if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                if (imgData != null) {
                    imgData.writeSmartSnapsImageInfo(xml);
                }
            }

            // regist
            xml.startTag(null, "regist");
            if (this.type.equals("webitem")) {
                xml.attribute(null, "name", this.regName);
            } else {
                xml.attribute(null, "name", this.regName);
            }
            xml.attribute(null, "value", this.regValue);
            xml.endTag(null, "regist");

            // source
            xml.startTag(null, "source");
            xml.attribute(null, "type", this.type);
            xml.attribute(null, "fit", this.fit);
            xml.attribute(null, "bgcolor", this.bgColor);
            xml.attribute(null, "target_type", this.srcTargetType);
            xml.attribute(null, "target", this.srcTarget);
            xml.attribute(null, "mask", this.mask);
            xml.attribute(null, "maskURL", this.maskURL);
            xml.attribute(null, "mask_type", this.maskType);
            xml.attribute(null, "mask_radius", this.maskRadius);

            xml.endTag(null, "source");

            // MyphotoSelectedData
            if (imgData != null) {
                SnapsLogger.appendOrderLog("SnapsLayoutControl saveXML make imgData : " + imgData.PATH);
                imgData.getSaveXML(xml, true);
            }

            xml.endTag(null, "image");

        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("getControlSaveXML(SnapsLayoutControl) exception : " + e.toString());
        }

        return xml;
    }

    @Override
    public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY) {
        try {
            SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl getControlAuraOrderXML point1");
            // 로컬 리소스는 xml을 만들지 않는다.
            if (type.equalsIgnoreCase("local_resource"))
                return xml;

            if (Const_PRODUCT.isCardShapeFolder()) {
                difX = cardFolderFixValueX;
                difY = cardFolderFixValueY;
            }

            //임시.. 였는데 페이스북 챕터가 모두 글로만 구성될 경우를 대비하여 살린다.
            if (!Config.isRealServer() && this.imgData != null) {
                if (Config.isSNSPhoto(imgData.KIND)) {
                    if (this.imgData.PATH == null || this.imgData.PATH.equals(""))
                        return xml;
                }
            }

            // 이미지일경우.
            xml.startTag(null, "object");

            String[] imageRc = this.getRc().replace(" ", "|").split("\\|");
            String[] imageRcClip = this.getRcClip().replace(" ", "|").split("\\|");

            if (this.type.equalsIgnoreCase("webitem")) {
                if (this.regName.equalsIgnoreCase("line"))// (layout.regName.equals("background") ||
                    // layout.regName.equals("line"))
                    xml.attribute(null, "type", "line");
                else if (this.regName.equalsIgnoreCase("background"))// (layout.regName.equals("background")
                    // ||
                    // layout.regName.equals("line"))
                    xml.attribute(null, "type", "rectangle");
                else
                    xml.attribute(null, "type", "design");
            } else if (this.type.equalsIgnoreCase("border")) {
                xml = getControlAuraOrderXMLByBorder(xml, sp, difX, difY);
                xml.endTag(null, "object");
                return xml;
            } else
                // browser image
                xml.attribute(null, "type", "image");

            SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl getControlAuraOrderXML point2");

            Dlog.d("getControlAuraOrderXML() bgColor:" + bgColor);

            if (!this.bgColor.equalsIgnoreCase("")) {
                xml.attribute(null, "bgColor", String.valueOf(ColorUtil.getParseColor("#" + this.bgColor)));
            }

            // FIXME 자유 회전이 적용 된 경우
            int angle = 0;

            try {
                angle = this.freeAngle + Integer.parseInt(getAngle());
            } catch (NumberFormatException e) {
                Dlog.e(TAG, e);
            }

            xml.attribute(null, "angle", String.valueOf(angle));
            xml.attribute(null, "angleClip", this.angleClip);

            SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl getControlAuraOrderXML point3");

            if (this.imgData != null) {
                boolean urlTypeImage = false;
                switch (this.imgData.KIND) {
                    case Const_VALUES.SELECT_SNAPS:
                    case Const_VALUES.SELECT_SDK_CUSTOMER:
                    case Const_VALUES.SELECT_BETWEEN:
                        urlTypeImage = true;
                        break;
                    case Const_VALUES.SELECT_PHONE:
                    case Const_VALUES.SELECT_UPLOAD:
                        urlTypeImage = false;
                        break;
                    case Const_VALUES.SELECT_INSTAGRAM:
                    case Const_VALUES.SELECT_KAKAO:
                    case Const_VALUES.SELECT_FACEBOOK:
                        urlTypeImage = Config.isSNSBook(); // SNS북이면 이전 방식대로 url로 업로드. 그 외에는 서버에 업로드된 file 타입으로.
                        break;
                }

                // 업로드 실패처리
                if (!urlTypeImage && ("".equals(this.imgData.F_UPLOAD_PATH) || "".equals(this.imgData.F_IMG_YEAR) || "".equals(this.imgData.F_IMG_SQNC))) {
                    Dlog.e(TAG, "getControlAuraOrderXML() upload image error");
                    SnapsLogger.appendOrderLog("SnapsLayoutControl imgData Err : F_UPLOAD_PATH > " + this.imgData.F_UPLOAD_PATH + ", F_IMG_YEAR > " + this.imgData.F_IMG_YEAR + ", F_IMG_SQNC > " + this.imgData.F_IMG_SQNC + ", SnapsOrderManager.getProjectCode() : " + SnapsOrderManager.getProjectCode());
                    SnapsUploadFailedImageDataCollector.addUploadFailedImageData(SnapsOrderManager.getProjectCode(), imgData); //FIXME 차라리 사용자에게 해당 사진을 지우고 다시 시도할 수 있도록 원본 이미지 오류라고 치부해 버린다.
                    return null;
                }

                if (urlTypeImage) {
                    SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl url Path : " + this.imgData.PATH);

                    xml.attribute(null, "uploadType", "url");
                    xml.attribute(null, "uploadURL", StringUtil.convertEmojiUniCodeToAlias(this.imgData.PATH));
                    xml.attribute(null, "file", "");
                } else {
                    SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl file Path : " + this.imgData.F_UPLOAD_PATH);

                    xml.attribute(null, "uploadType", "file");
                    xml.attribute(null, "file", StringUtil.removeQuestionChar(StringUtil.convertEmojiUniCodeToAlias(this.imgData.F_UPLOAD_PATH)));
                    xml.attribute(null, "imgYear", this.imgData.F_IMG_YEAR);
                    xml.attribute(null, "imgSeq", this.imgData.F_IMG_SQNC);
                }
            } else {
                xml.attribute(null, "rscType", this.srcTargetType);
                xml.attribute(null, "id", this.srcTarget);
            }

            float imgX = Float.parseFloat(imageRc[0]) + Float.parseFloat(imageRcClip[0]) + difX;
            float imgY = Float.parseFloat(imageRc[1]) + Float.parseFloat(imageRcClip[1]) + difY;

            // clipRect 회전축..
            PointF pivot = new PointF(Float.parseFloat(imageRcClip[0]) + difX + Float.parseFloat(imageRcClip[2]) / 2.f, Float.parseFloat(imageRcClip[1]) + difY + Float.parseFloat(imageRcClip[3]) / 2.f);

            PointF cP = convertClipRect(this, this.angleClip, imgX, imgY, pivot);

            xml.attribute(null, "x", String.valueOf(cP.x));
            xml.attribute(null, "y", String.valueOf(cP.y));

            if (this.regName.equalsIgnoreCase("line")) {
                xml.attribute(null, "width", this.width);
                xml.attribute(null, "height", this.height);
            } else if (this.regName.equalsIgnoreCase("background")) {
                xml.attribute(null, "width", this.width);
                xml.attribute(null, "height", this.height);
            } else {
                // 이미지의 비율이 맞지 않으면 보정해 준다.
                if (Config.isSimpleMakingBook()) {
                    if (getPageIndex() == 0) // cover
                        imageRc = BitmapUtil.checkImageRatio(imgData, imageRc, imageRcClip);
                } else
                    imageRc = BitmapUtil.checkImageRatio(imgData, imageRc, imageRcClip);

                // 보정에도 실패한다면 에러처리
                if (imageRc != null && imageRc.length >= 4) {
                    try {
                        //가끔 카카오북에서 ""으로 기록되는 증상이 확인 됨.
                        if (Config.isSNSBook()) {
                            if (imageRc[2] == null || imageRc[2].length() < 1 || imageRc[3] == null || imageRc[3].length() < 1) {
                                imageRc[2] = imageRcClip[2];
                                imageRc[3] = imageRcClip[3];
                            }
                        }

                        float rectW = Float.parseFloat(imageRc[2]);
                        float rectH = Float.parseFloat(imageRc[3]);
                        if (imgData != null) {
                            float imgW = Float.parseFloat(imgData.F_IMG_WIDTH);
                            float imgH = Float.parseFloat(imgData.F_IMG_HEIGHT);
                            boolean isWrongRatio = (imgW > imgH && rectW < rectH) || (imgW < imgH && rectW > rectH);
                            if (isWrongRatio) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("imgW:").append(imgW).append(", ");
                                sb.append("imgH:").append(imgH).append(", ");
                                sb.append("rectW:").append(rectW).append(", ");
                                sb.append("rectH:").append(rectH);
                                Dlog.e(TAG, "getControlAuraOrderXML() ratio Error : " + sb.toString());
                                SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl ratio error1");
                                SnapsUploadFailedImageDataCollector.addUploadFailedImageData(SnapsOrderManager.getProjectCode(), imgData); //FIXME 차라리 사용자에게 해당 사진을 지우고 다시 시도할 수 있도록 원본 이미지 오류라고 치부해 버린다.
                                return null;
                            }
                        }

                        if (imgData != null && ((imageRc[2] != null && imageRc[2].trim().equals("0")) || (imageRc[3] != null && imageRc[3].trim().equals("0")))) {
                            Dlog.e(TAG, "getControlAuraOrderXML() imageRc Error");
                            SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl ratio error2");
                            SnapsUploadFailedImageDataCollector.addUploadFailedImageData(SnapsOrderManager.getProjectCode(), imgData); //FIXME 차라리 사용자에게 해당 사진을 지우고 다시 시도할 수 있도록 원본 이미지 오류라고 치부해 버린다.
                            return null;
                        }
                    } catch (Exception e) {
                        SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl ratio error3(exception) : " + e.toString());
                        Dlog.e(TAG, e);
                        SnapsUploadFailedImageDataCollector.addUploadFailedImageData(SnapsOrderManager.getProjectCode(), imgData); //FIXME 차라리 사용자에게 해당 사진을 지우고 다시 시도할 수 있도록 원본 이미지 오류라고 치부해 버린다.
                    }
                }

                xml.attribute(null, "width", imageRc[2]);
                xml.attribute(null, "height", imageRc[3]);
            }

            SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl getControlAuraOrderXML point4");

            xml.attribute(null, "clipX", String.valueOf((Integer.parseInt(imageRcClip[0]) + difX)));
            xml.attribute(null, "clipY", String.valueOf((Integer.parseInt(imageRcClip[1]) + difY)));

            xml.attribute(null, "clipWidth", imageRcClip[2]);
            xml.attribute(null, "clipHeight", imageRcClip[3]);

            if (!this.maskType.equalsIgnoreCase("")) {
                // 마스크 타입이 있으면 정보를 넣어준다.
                xml.attribute(null, "mask_type", this.maskType);
                xml.attribute(null, "mask_radius", this.maskRadius);
                xml.attribute(null, "mask", "");
            } else {
                xml.attribute(null, "mask", this.mask);
                if (!this.maskRadius.equalsIgnoreCase(""))
                    xml.attribute(null, "mask_radius", this.maskRadius);
            }

            xml.attribute(null, "alpha", this.alpha);
            xml.attribute(null, "borderOption", "inner");

            // 사진틀이 있을 경우 사진틀..
            // 이미지 효과 저장. bright / sharpen / grayscale / sepia
            // 사진틀...

            if (this.bordersinglecolortype.equals("1")) {// 컬러타입 보더..
                xml.attribute(null, "bordersinglecolortype", this.bordersinglecolortype);
                xml.attribute(null, "bordersinglethick", this.bordersinglethick);
                xml.attribute(null, "bordersinglealpha", String.valueOf((int) (Float.parseFloat(this.bordersinglealpha) * 255)));
                xml.attribute(null, "bordersinglecolor", String.valueOf(ColorUtil.getParseColor(this.bordersinglecolor)));
            } else {
                xml.attribute(null, "bordersinglecolortype", "");
            }

            // 이펙트 효과 적용
            if (this.imgData != null && this.imgData.isApplyEffect) {

                String value = ImageEffectBitmap.getAuraEffectValue(this.imgData.EFFECT_TYPE);
                String name = ImageEffectBitmap.getAuraEffectName(this.imgData.EFFECT_TYPE);

                if (value != null && value.length() > 0 && name != null && name.length() > 0) {
                    xml.startTag(null, "effect");
                    xml.attribute(null, "value", value);
                    xml.attribute(null, "name", name);
                    xml.endTag(null, "effect");
                }
            }

            if (qrCodeUrl != null && qrCodeUrl.length() > 0) {
                xml.attribute(null, "qrCodeUrl", qrCodeUrl);
            }

            SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl getControlAuraOrderXML point5");

            xml.endTag(null, "object");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("snapsAuraXml SnapsLayoutControl exception : " + e.toString());
        }

        return xml;
    }

    SnapsXML getControlAuraOrderXMLByBorder(SnapsXML xml, SnapsPage sp, float difX, float difY) {
        xml.attribute(null, "type", "rectangle");
        xml.attribute(null, "borderColor", String.valueOf(ColorUtil.getParseColor("#" + FACEBOOK_BORDER_COLOR)));
        xml.attribute(null, "angle", "0");
        xml.attribute(null, "angleClip", "0");
        String[] imageRcClip = this.getRcClip().replace(" ", "|").split("\\|");
        xml.attribute(null, "clipX", String.valueOf((Float.parseFloat(imageRcClip[0]) + difX)));
        xml.attribute(null, "clipY", String.valueOf((Float.parseFloat(imageRcClip[1]) + difY)));

        xml.attribute(null, "clipWidth", imageRcClip[2]);
        xml.attribute(null, "clipHeight", imageRcClip[3]);

        xml.attribute(null, "x", String.valueOf((Integer.parseInt(imageRcClip[0]) + difX)));
        xml.attribute(null, "y", String.valueOf((Integer.parseInt(imageRcClip[1]) + difY)));

        xml.attribute(null, "width", imageRcClip[2]);
        xml.attribute(null, "height", imageRcClip[3]);

        return xml;
    }

    /***
     * 회전시 중심에서 회전이 된것처럼 좌표를 변환해주는 함수..
     *
     * @param control
     * @param angle
     */
    PointF convertClipRect(SnapsLayoutControl control, String angle, float xx, float yy, PointF pivot) {
        try {
            float left = xx;
            float top = yy;
            float right = left + Float.parseFloat(control.img_width);
            float bottom = top + Float.parseFloat(control.img_height);

            RectF r = new RectF(left, top, right, bottom);
            float fAngle = Float.parseFloat(angle);
            RectF rRect = RotateUtil.convertCenterRotateRect2(r, fAngle, pivot);

            return new PointF(rRect.left, rRect.top);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return null;

    }

    public SnapsXML getControlSummaryXML(SnapsXML xml, int cal_idx) {

        try {

            xml.startTag(null, "image");
            xml.attribute(null, "x", this.x);
            xml.attribute(null, "y", this.y);
            xml.attribute(null, "w", this.width);
            xml.attribute(null, "h", this.height);
            xml.attribute(null, "cal_idx", String.valueOf(cal_idx));
            xml.startTag(null, "source");
            xml.attribute(null, "type", "webitem");
            xml.attribute(null, "target_type", "design");
            xml.attribute(null, "fit", "fill_in");
            xml.endTag(null, "source");
            xml.endTag(null, "image");

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xml;
    }

    public SnapsLayoutControl copyControl() {
        SnapsLayoutControl layout = new SnapsLayoutControl();
        layout.setX(x);
        layout.y = y;
        layout.type = type;
        layout.width = width;
        layout.height = height;
        layout.angle = angle;
        layout.border = border;
        layout.isClick = isClick;

        layout.stick_dirction = stick_dirction;
        layout.stick_margin = stick_margin;
        layout.stick_target = stick_target;

        layout.regName = regName;
        layout.regValue = regValue;
        layout.isSnsBookCover = isSnsBookCover;
        layout.isImageFull = isImageFull;

        return layout;
    }

    public SnapsLayoutControl copyImageControl() {
        SnapsLayoutControl layout = new SnapsLayoutControl();
        layout.setX(x);
        layout.y = y;
        layout.type = type;
        layout.width = width;
        layout.height = height;
        layout.angle = angle;
        layout.border = border;
        layout.isClick = isClick;

        layout.stick_dirction = stick_dirction;
        layout.stick_margin = stick_margin;
        layout.stick_target = stick_target;

        layout.regName = regName;
        layout.regValue = regValue;
        layout.isSnsBookCover = isSnsBookCover;

        if (imgData != null) {
            layout.imgData = new MyPhotoSelectImageData();
            layout.imgData.set(imgData);
        }

        layout.img_width = img_width;
        layout.img_height = img_height;
        layout.angle = angle;
        layout.angleClip = angleClip;
        layout.freeAngle = freeAngle;
        layout.srcTargetType = srcTargetType;
        layout.srcTarget = srcTarget;
        layout.isImageFull = isImageFull;

        return layout;
    }


    public double getImageDataKey() {
        return imageDataKey;
    }

    public void setImageDataKey(double imageDataKey) {
        this.imageDataKey = imageDataKey;
    }

    public SnapsLayoutControl() {
    }

    public SnapsLayoutControl(Parcel in) {
        super(in);
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(layoutControlIdx);
        dest.writeDouble(imageDataKey);
        dest.writeString(type);
        dest.writeString(fit);

        dest.writeString(bgColor);
        dest.writeString(imagePath);
        dest.writeString(srcTargetType);
        dest.writeString(srcTarget);
        dest.writeString(imgYear);
        dest.writeString(imgSeq);
        dest.writeString(imgWidth);
        dest.writeString(imgHeight);

        dest.writeString(oriPath);
        dest.writeString(tinyPath);
        dest.writeString(thumPath);
        dest.writeString(realFileName);

        boolean[] arrBool = {local, isVisible, isNoPrintImage, isUploadFailedOrgImg, isSnsBookCover, isImageFull};
        dest.writeBooleanArray(arrBool);

        dest.writeString(mask);
        dest.writeString(maskURL);
        dest.writeString(maskType);
        dest.writeString(maskRadius);
        dest.writeString(name);
        dest.writeString(resourceURL);

        dest.writeInt(imageLoadType);

        dest.writeString(border);

        dest.writeString(uploadPath);
        dest.writeString(sizeOrgImg);
        dest.writeString(mstPath);
        dest.writeString(contentType);
        dest.writeString(exchange);
        dest.writeString(helper);
        dest.writeString(noclip);
        dest.writeString(useAlpha);
        dest.writeString(alpha);
        dest.writeString(formItem);
        dest.writeString(checkFull);
        dest.writeString(stick);
        dest.writeString(tempImageColor);

        dest.writeParcelable(imgData, 0);

        dest.writeString(bordersinglecolortype);
        dest.writeString(bordersinglealpha);
        dest.writeString(bordersinglethick);
        dest.writeString(bordersinglecolor);
        dest.writeString(borderURL);
        dest.writeString(qrCodeUrl);
    }

    private void readFromParcel(Parcel in) {
        layoutControlIdx = in.readInt();
        imageDataKey = in.readDouble();
        type = in.readString();
        fit = in.readString();

        bgColor = in.readString();
        imagePath = in.readString();
        srcTargetType = in.readString();
        srcTarget = in.readString();
        imgYear = in.readString();
        imgSeq = in.readString();
        imgWidth = in.readString();
        imgHeight = in.readString();

        oriPath = in.readString();
        tinyPath = in.readString();
        thumPath = in.readString();
        realFileName = in.readString();

        boolean[] arrBool = new boolean[6];
        in.readBooleanArray(arrBool);
        local = arrBool[0];
        isVisible = arrBool[1];
        isNoPrintImage = arrBool[2];
        isUploadFailedOrgImg = arrBool[3];
        isSnsBookCover = arrBool[4];
        isImageFull = arrBool[5];

        mask = in.readString();
        maskURL = in.readString();
        maskType = in.readString();
        maskRadius = in.readString();
        name = in.readString();
        resourceURL = in.readString();

        imageLoadType = in.readInt();

        border = in.readString();
        uploadPath = in.readString();
        sizeOrgImg = in.readString();
        mstPath = in.readString();
        contentType = in.readString();
        exchange = in.readString();
        helper = in.readString();

        noclip = in.readString();
        useAlpha = in.readString();
        alpha = in.readString();
        formItem = in.readString();
        checkFull = in.readString();
        stick = in.readString();
        tempImageColor = in.readString();

        imgData = in.readParcelable(CropInfo.class.getClassLoader());

        bordersinglecolortype = in.readString();
        bordersinglealpha = in.readString();
        bordersinglethick = in.readString();
        bordersinglecolor = in.readString();
        borderURL = in.readString();
        qrCodeUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SnapsLayoutControl createFromParcel(Parcel in) {
            return new SnapsLayoutControl(in);
        }

        @Override
        public SnapsLayoutControl[] newArray(int size) {
            return new SnapsLayoutControl[size];
        }
    };

    public static SnapsLayoutControl makeImageLayoutControl() {
        SnapsLayoutControl layout = new SnapsLayoutControl();
        layout.x = "0";
        layout.y = "0";
        layout.type = "browse_file";
        layout.width = "137";
        layout.height = "0";
        layout.border = "false";

        return layout;
    }

    public static SnapsLayoutControl getProfileImageLayoutControl() {
        SnapsLayoutControl layout = new SnapsLayoutControl();
        layout.x = "0";
        layout.y = "0";
        layout.type = "browse_file";
        layout.width = "11";
        layout.height = "11";
        layout.border = "false";

        return layout;
    }

    public String getFacebookBordColor(boolean isIncludeShap) {
        return isIncludeShap ? "#" + FACEBOOK_BORDER_COLOR : FACEBOOK_BORDER_COLOR;
    }

    public void initImageRc() {
        img_x = "0";
        img_y = "0";
        img_width = "0";
        img_height = "0";
    }

    /**
     * Seal Sticker 에서 배경으로 사용될 컨트롤인지 확인하는 방법.
     * 일단은 layout control 에서만 사용하도록 설정.
     */
    public boolean isForBackground() {
        return "deepBack".equals(name);
    }

    public boolean isEmptyImage() {
        return imgData == null;
    }

    public boolean isNotEmptyImage() {
        return !isEmptyImage();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(", ");
        String urlPreFix = "";
        if (resourceURL != null && resourceURL.length() > 0) {
            urlPreFix = SnapsAPI.DOMAIN(false);
        }
        sb.append("resourceURL:").append(urlPreFix).append(resourceURL).append(", ");
        sb.append("alpha:").append(alpha);
        return sb.toString();
    }
}
