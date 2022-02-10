package com.snaps.mobile.activity.photoprint.manager;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsDelImage;
import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.photoprint.ImpUploadProject;
import com.snaps.common.structure.photoprint.SnapsXmlMakeResult;
import com.snaps.common.structure.photoprint.json.PhotoPrintJsonObjectTmplnfo;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.photoprint.exceptions.SnapsPhotoPrintSizeInfoException;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;
import com.snaps.mobile.order.order_v2.exceptions.SnapsIOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;

/**
 * Created by songhw on 2017. 3. 31..
 */

public class PhotoPrintProject implements ImpUploadProject, Parcelable, Serializable {
    private static final String TAG = PhotoPrintProject.class.getSimpleName();

    private static final long serialVersionUID = 1887210153696066414L;

    private PhotoPrintJsonObjectTmplnfo tmplInfo;

    private ArrayList<PhotoPrintData> datas, addedDatas;
    private PhotoPrintData baseData;

    private String projCode, prodCode, cartThumbnail, fontStyleHtmlString;
    private String basketVersion = "2.5.0.0";
    private int sellPrice, originPrice, totalCount;

    private boolean isEditMode;

    private float[] fontPosData;

    // 진행 사항 설정.. 디바이스에서만 사용한 데이터들...
    int step = 0; // 단계설정 프로젝트 코드,작품썸네일,작품페이지,원본이미지,xml올리기
    int subStep = 0; // 서브단계설정 -1이면 완료단계..
    int retryCount = 0; // 재시도 횟수
    int cancel = 0; // 취소여부.. 0:취소아님 1:취소
    int completeProgress = 0;


    public PhotoPrintProject( PhotoPrintJsonObjectTmplnfo tmplInfo, String versionCode, String prodCode, String projCode, String cartThumbnail, String fontStyleHtmlString, boolean isEditMode ) {
        this.tmplInfo = tmplInfo;
        this.prodCode = prodCode;
        this.projCode = projCode;
        this.cartThumbnail = cartThumbnail;
        this.fontStyleHtmlString = fontStyleHtmlString;
        this.isEditMode = isEditMode;
        this.basketVersion = versionCode;
    }

    protected PhotoPrintProject(Parcel in) {
        tmplInfo = in.readParcelable(PhotoPrintJsonObjectTmplnfo.class.getClassLoader());
        datas = in.createTypedArrayList(PhotoPrintData.CREATOR);
        addedDatas = in.createTypedArrayList(PhotoPrintData.CREATOR);
        baseData = in.readParcelable(PhotoPrintData.class.getClassLoader());
        projCode = in.readString();
        prodCode = in.readString();
        cartThumbnail = in.readString();
        basketVersion = in.readString();
        sellPrice = in.readInt();
        originPrice = in.readInt();
        totalCount = in.readInt();
        fontPosData = in.createFloatArray();
        step = in.readInt();
        subStep = in.readInt();
        retryCount = in.readInt();
        cancel = in.readInt();
        completeProgress = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(tmplInfo, flags);
        dest.writeTypedList(datas);
        dest.writeTypedList(addedDatas);
        dest.writeParcelable(baseData, flags);
        dest.writeString(projCode);
        dest.writeString(prodCode);
        dest.writeString(cartThumbnail);
        dest.writeString(basketVersion);
        dest.writeInt(sellPrice);
        dest.writeInt(originPrice);
        dest.writeInt(totalCount);
        dest.writeFloatArray(fontPosData);
        dest.writeInt(step);
        dest.writeInt(subStep);
        dest.writeInt(retryCount);
        dest.writeInt(cancel);
        dest.writeInt(completeProgress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoPrintProject> CREATOR = new Creator<PhotoPrintProject>() {
        @Override
        public PhotoPrintProject createFromParcel(Parcel in) {
            return new PhotoPrintProject(in);
        }

        @Override
        public PhotoPrintProject[] newArray(int size) {
            return new PhotoPrintProject[size];
        }
    };

    public void setDatas(ArrayList<PhotoPrintData> datas, ArrayList<PhotoPrintData> addedDatas, PhotoPrintData baseData, float[] fontPos ) {
        this.datas = datas;
        this.addedDatas = addedDatas;
        this.baseData = baseData;
        this.fontPosData = fontPos;

        totalCount = 0;
        for( PhotoPrintData data : datas )
            totalCount += data.getCount();
    }

    public void setPrice( int sell, int origin ) {
        this.sellPrice = sell;
        this.originPrice = origin;
    }

    @Override
    public String getOrderCode() {
        return "146000";
    }

    @Override
    public void setProjectCode(String prjCode) {
        this.projCode = prjCode;
    }

    @Override
    public String getProjectCode() {
        return projCode;
    }

    @Override
    public void setItemImgSeqWithImageId(int imageId, SnapsDelImage data) {
        PhotoPrintData item = findPhotoPrintDataWithId(imageId);
        if (item == null) return;

        item.getMyPhotoSelectImageData().F_IMG_SQNC = data.imgSeq;
        item.getMyPhotoSelectImageData().F_IMG_YEAR = data.imgYear;
        item.getMyPhotoSelectImageData().F_UPLOAD_PATH = data.uploadPath;
        item.getMyPhotoSelectImageData().THUMBNAIL_PATH = data.thumbNailUrl;
        item.setOrgImageSize( data.sizeOrgImg );
        item.setTinyPath( data.tinyPath );
        item.getMyPhotoSelectImageData().ORIGINAL_PATH = data.oriPath;
}

    @Override
    public void setApplicationVersion(String version) {
        // ignore
    }

    @Override
    public String getCartThumbnail() { return cartThumbnail; }

    @Override
    public ArrayList<String> getWorkThumbnails() {
        ArrayList<String> array = new ArrayList<String>();
        for (PhotoPrintData item : datas)
            array.add( item.getMyPhotoSelectImageData().THUMBNAIL_PATH );

        return array;
    }

    @Override
    public String getOriginalPathWithIndex(int index) {
        return datas.get(index).getMyPhotoSelectImageData().PATH;
    }

    public String getThumbnailWithImageId(int imageId ) {
        PhotoPrintData photoPrintData = findPhotoPrintDataWithId(imageId);
        return photoPrintData != null ? photoPrintData.getMyPhotoSelectImageData().THUMBNAIL_PATH : "";
    }

    private String getBooleanString( boolean flag ) {
        return flag ? "yes" : "no";
    }

    @Override
    public SnapsXmlMakeResult makeSaveXML(String filePath) {
        File saveFile;

//        SnapsLogger.appendOrderLog("photo print project makeSaveXML point1");
        SnapsLogger.appendOrderLog("photo print project makeSaveXML point1");
        if (filePath == null) {
            try {
                saveFile = Config.getPROJECT_FILE(Config.SAVE_XML_FILE_NAME);
                if (saveFile == null) throw new SnapsIOException("failed make save Xml File");
            } catch (Exception e) {
                SnapsLogger.appendOrderLog("photo print project makeSaveXML exception : " + e.toString());
                Dlog.e(TAG, e);
                return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
            }
        }
        else
            saveFile = new File(filePath);

        SnapsLogger.appendOrderLog("photo print project makeSaveXML point2");

        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                Dlog.e(TAG, e);
                return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
            }
        }

        SnapsLogger.appendOrderLog("photo print project makeSaveXML point3");

        FileOutputStream fileStream = null;
        try {
            fileStream = new FileOutputStream(saveFile);
        } catch (FileNotFoundException e) {
            Dlog.e(TAG, e);
            return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
        }

        SnapsLogger.appendOrderLog("photo print project makeSaveXML point4");

        try {
            SnapsXML xml = new SnapsXML(fileStream);
            xml.startTag(null, "basket");
            xml.attribute(null, "version", basketVersion);
            xml.attribute(null, "appType", "Android");

            xml.startTag(null, "item");
            xml.attribute(null, "chnlCode", Config.getCHANNEL_CODE() );
            xml.attribute(null, "projName", "");
            xml.attribute(null, "cTmplCode", "");
            xml.attribute(null, "uaInfo", "android");
            xml.attribute(null, "userAgent", "");

            Date date = new Date();
            String regDate = new SimpleDateFormat( "yyyy.MM.dd hh:mm" ).format( date );
            String regDateXml = new SimpleDateFormat( "hh:mm:ss:SSS" ).format( date );
            xml.attribute(null, "regDateXml", regDateXml );
            xml.attribute(null, "regDate", regDate);


            xml.startTag(null, "info");
            xml.attribute(null, "printBrightType", getBooleanString(baseData.isAdjustBrightness()));
            xml.attribute(null, "prodName", tmplInfo.getF_PROD_NAME());
            xml.attribute(null, "prodCode", prodCode);
            xml.attribute(null, "prodNickName", tmplInfo.getF_PROD_NICK_NAME());
            xml.attribute(null, "dpcRCMM", tmplInfo.getF_RES_MIN());
            xml.attribute(null, "printRegDateType", getBooleanString(baseData.isShowPhotoDate()));
            xml.attribute(null, "pageMM", tmplInfo.getF_PAGE_MM_WIDTH() + " " + tmplInfo.getF_PAGE_MM_HEIGHT());
            xml.attribute(null, "prodType", tmplInfo.getF_PROD_TYPE());
            xml.attribute(null, "printBorderType", getBooleanString(baseData.isMakeBorder()));
            xml.attribute(null, "printGlossyType", baseData.getGlossyType());
            xml.attribute(null, "pagePX", tmplInfo.getF_PAGE_PIXEL_WIDTH() + " " + tmplInfo.getF_PAGE_PIXEL_HEIGHT());
            xml.attribute(null, "coverMM", tmplInfo.getF_PAGE_MM_WIDTH() + " " + tmplInfo.getF_PAGE_MM_HEIGHT());
            xml.attribute(null, "prodSize", tmplInfo.getF_PROD_SIZE());
            xml.attribute(null, "sceneNum", datas.size() + "");
            xml.attribute(null, "printCnt", baseData.getCount() + "");
            xml.attribute(null, "printImgFullType", getBooleanString(baseData.isImageFull()));
            xml.attribute(null, "coverPX", tmplInfo.getF_PAGE_PIXEL_WIDTH() + " " + tmplInfo.getF_PAGE_PIXEL_HEIGHT());
            xml.endTag(null, "info");

            xml.startTag(null, "price");
            xml.attribute(null, "sellPrice", sellPrice + "");
            xml.attribute(null, "totalSellPrice", (sellPrice * totalCount) + "");
            xml.attribute(null, "totalOrgPrice", (originPrice * totalCount) + "");
            xml.endTag(null, "price");

            String borderThickness = getBorderThickness() + "0";

            baseData.setMyPhotoSelectImageData( new MyPhotoSelectImageData() );
            baseData.getMyPhotoSelectImageData().F_IMG_WIDTH = "100"; //임의의 값이다 0 이하로 넣으면 삭제 된다
            baseData.getMyPhotoSelectImageData().F_IMG_HEIGHT = "100";
            baseData.makeSaveXml( xml, 0, fontPosData, borderThickness, fontStyleHtmlString );

            if (datas != null) {
                Dlog.d("makeSaveXML() datas.size:" + datas.size());
                for( int i = 1; i < datas.size() + 1; ++i )
                    datas.get(i - 1).makeSaveXml( xml, i, fontPosData, borderThickness, fontStyleHtmlString );
            }

            xml.addTag( null, "hiddenScene", "" );
            xml.endTag( null, "item" );
            xml.endTag( null, "basket" );
            xml.endDocument();

            fileStream.close();
        } catch (Exception e) {
            SnapsLogger.appendOrderLog("photo print project exception : "+ e.toString());
            Dlog.e(TAG, e);
            return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
        }

        SnapsLogger.appendOrderLog("photo print project makeSaveXML point5");

        return new SnapsXmlMakeResult.Builder().setSuccess(true).setXmlFile(saveFile).create();
    }

    public boolean isEditMode() { return isEditMode; }

    private String getBorderThickness() {
        float mmW = Float.parseFloat( tmplInfo.getF_PAGE_MM_WIDTH() );
        float pxW = Float.parseFloat( tmplInfo.getF_PAGE_PIXEL_WIDTH() );
        int borderThickness = (int)( 5 / mmW * pxW );
        return borderThickness + "";
    }

    @Override
    public SnapsXmlMakeResult makeAuraOrderXML(String filePath) {
        SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point1");
        File saveFile = null;

        if (filePath == null) {
            try {
                saveFile = Config.getPROJECT_FILE(Config.AURA_ORDER_XML_FILE_NAME);
                if (saveFile == null) throw new SnapsIOException("failed make aura file");
            } catch (Exception e) {
                SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML exception 1: " + e.toString());
                Dlog.e(TAG, e);
                return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
            }
        } else {
            saveFile = new File(filePath);
        }

        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML exception 2:" + e.toString());
                Dlog.e(TAG, e);
                return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
            }
        }

        FileOutputStream fileStream = null;

        try {
            fileStream = new FileOutputStream(saveFile);
        } catch (FileNotFoundException e) {
            SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML exception3 : " + e.toString());
            Dlog.e(TAG, e);
            return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
        }

        try {
            SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point2");

            SnapsXML xml = new SnapsXML(fileStream);
            xml.startTag(null, "Order");
            xml.attribute(null, "code", "");
            xml.startTag(null, "Application");
            xml.attribute(null, "name", "android_mobile");
            xml.attribute(null, "version", Config.getAPP_VERSION());
            String regDateXml = new SimpleDateFormat( "hh:mm:ss:SSS" ).format( new Date() );
            xml.attribute(null, "regDateXml", regDateXml);
            xml.endTag(null, "Application");

            SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point3");

            int frameW, frameH, mmWidth, mmHeight, editW, editH;
            for (PhotoPrintData data : datas) {
                xml.startTag(null, "item");
                xml.attribute(null, "rcmm_yorn", "yes");
                xml.attribute(null, "prod_real_name", tmplInfo.getF_PROD_NAME());
                xml.attribute(null, "prod_nick_name", tmplInfo.getF_PROD_NICK_NAME());
                xml.attribute(null, "prod_name", tmplInfo.getF_PROD_NICK_NAME());
                xml.attribute(null, "prnt_cnt", data.getCount() + "");
                xml.attribute(null, "glss_type", data.getGlossyType());
                xml.attribute(null, "prod_code", prodCode);
                xml.attribute(null, "type", "photo_print");
                xml.attribute(null, "edge_type", "noedge");
                xml.attribute(null, "sell_price", sellPrice + "");
                xml.attribute(null, "brht_type", getBooleanString(data.isAdjustBrightness()));

                SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML :  " + data.getMyPhotoSelectImageData().PATH);

                if( data.getMyPhotoSelectImageData().ROTATE_ANGLE % 180 != 0 ) {
                    float temp = data.getX();
                    data.setX( data.getY() );
                    data.setY( temp );
                }
                data.setAngle( (data.getAngle() + data.getMyPhotoSelectImageData().ROTATE_ANGLE) % 360);

                SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point3-1");

                float imageW, imageH;
                if( !StringUtil.isEmpty(data.getOrgImageSize()) && data.getOrgImageSize().contains(" ") ) {
                    Dlog.d("makeAuraOrderXML() data.getOrgImageSize:" + data.getOrgImageSize());
                    String[] sizeString = data.getOrgImageSize().split(" ");
                    imageW = Float.parseFloat(sizeString[0]);
                    imageH = Float.parseFloat(sizeString[1]);
                    if (imageW <= 0 || imageH <= 0) {
                        imageW = Float.parseFloat( data.getMyPhotoSelectImageData().F_IMG_WIDTH );
                        imageH = Float.parseFloat( data.getMyPhotoSelectImageData().F_IMG_HEIGHT );
                    }

                    SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point3-1 size check 1 : " + (imageW + ", " + imageH));
                }
                else if( !StringUtil.isEmpty(data.getMyPhotoSelectImageData().F_IMG_WIDTH) && !StringUtil.isEmpty(data.getMyPhotoSelectImageData().F_IMG_HEIGHT) ) {
                    imageW = Float.parseFloat( data.getMyPhotoSelectImageData().F_IMG_WIDTH );
                    imageH = Float.parseFloat( data.getMyPhotoSelectImageData().F_IMG_HEIGHT );

                    SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point3-1 size check 2 : " + (imageW + ", " + imageH));
                }
                else {
                    // size 정보 없음.
                    SnapsLogger.appendOrderLog("is not exist size info");
                    Dlog.w(TAG, "PhotoPrint Upload Error [is not exist size info]");
                    return new SnapsXmlMakeResult.Builder().setSuccess(false).setPhotoPrintData(data).setException(new SnapsPhotoPrintSizeInfoException()).create();
                }

                SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point3-2");

                if (imageW <= 0 || imageH <= 0) {
                    SnapsLogger.appendOrderLog("is not exist size info : " + (imageW + ", " + imageH));
                    Dlog.w(TAG, "PhotoPrint Upload Error [is not exist size info] " + (imageW + ", " + imageH));
                    return new SnapsXmlMakeResult.Builder().setSuccess(false).setPhotoPrintData(data).setException(new SnapsPhotoPrintSizeInfoException()).create();
                }

                // 세로 기본
                mmWidth = Math.min( Integer.parseInt(tmplInfo.getF_PAGE_MM_WIDTH()), Integer.parseInt(tmplInfo.getF_PAGE_MM_HEIGHT()) );
                mmHeight = Math.max( Integer.parseInt(tmplInfo.getF_PAGE_MM_WIDTH()), Integer.parseInt(tmplInfo.getF_PAGE_MM_HEIGHT()) );
                editW = Math.min(data.getSize()[0], data.getSize()[1]);
                editH = Math.max(data.getSize()[0], data.getSize()[1]);
                frameW = Math.min(data.getSize()[0], data.getSize()[1]);
                frameH = Math.max(data.getSize()[0], data.getSize()[1]);
                int temp;
                boolean isRotated = data.getAngle() == 90 || data.getAngle() == 270;
                if( (isRotated && imageW <= imageH) || (!isRotated && imageW > imageH) ) {
                    temp = mmWidth;
                    mmWidth = mmHeight;
                    mmHeight = temp;
                    temp = editW;
                    editW = editH;
                    editH = temp;
                }

                if( imageW > imageH ) {
                    temp = frameW;
                    frameW = frameH;
                    frameH = temp;
                }

                SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point3-3");

                int[] scaledSize = UIUtil.getPosByImageType( data.isImageFull(), new int[]{frameW, frameH}, new int[]{(int)imageW, (int)imageH} );
                xml.startTag(null, "page");
                xml.attribute(null, "type", "page");
                xml.attribute(null, "effectivePage", "single");
                xml.attribute(null, "mmWidth", mmWidth + "");
                xml.attribute(null, "mmHeight", mmHeight + "");

                xml.startTag(null, "editinfo");
                xml.attribute( null, "orientation", "0" );
                xml.attribute( null, "editWidth", editW + "" );
                xml.attribute( null, "editHeight", editH + "" );
                xml.endTag(null, "editinfo");

                xml.startTag(null, "object");
                xml.attribute( null, "width", frameW + "" );
                xml.attribute( null, "height", frameH + "" );
                xml.attribute( null, "clipX", "0" );
                xml.attribute( null, "clipY", "0" );
                xml.attribute( null, "angle", "0" );
                xml.attribute( null, "clipWidth", editW + "" );
                xml.attribute( null, "clipHeight", editH + "" );
                xml.attribute( null, "x", "0" );
                xml.attribute( null, "y", "0" );
                xml.attribute( null, "type", "rectangle" );
                xml.attribute( null, "alpha", "255" );
                xml.attribute( null, "bgColor", "16777215" );
                xml.endTag(null, "object");

                xml.startTag(null, "object");
                xml.attribute( null, "imgYear", data.getMyPhotoSelectImageData().F_IMG_YEAR);
                xml.attribute( null, "angleClip", "0" );
                xml.attribute( null, "width", scaledSize[2] + "" );
                xml.attribute( null, "height", scaledSize[3] + "" );
                if( data.isMakeBorder() ) {
                    xml.attribute( null, "bordersinglethick", getBorderThickness() );
                    xml.attribute( null, "bordersinglecolortype", "1" );
                    xml.attribute( null, "bordersinglealpha", "1" );
                    xml.attribute( null, "bordersinglecolor", "16777215" );
                }

                SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point3-4");

                int imageClipW = editW;
                int imageClipH = editH;
                float clipX = 0;
                float clipY = 0;
                if( data.isImageFull() ) {
                    imageClipW = scaledSize[ data.isRotated() ? 3 : 2 ];
                    imageClipH = scaledSize[ data.isRotated() ? 2 : 3 ];
                    clipX = ( (float)editW - (float)imageClipW ) / 2f;
                    clipY = ( (float)editH - (float)imageClipH ) / 2f;
                }
                float[] auraPosition = getAuraPos( data, new float[]{editW, editH}, new float[]{scaledSize[2], scaledSize[3]} );
                xml.attribute( null, "angle", data.getAngle() + "" );
                xml.attribute( null, "clipX", clipX + "" );
                xml.attribute( null, "clipY", clipY + "" );
                xml.attribute( null, "clipWidth", imageClipW + "" );
                xml.attribute( null, "clipHeight", imageClipH + "" );
                xml.attribute( null, "x", auraPosition[0] + "" );
                xml.attribute( null, "y", auraPosition[1] + "" );
                xml.attribute( null, "type", "image" );
                xml.attribute( null, "file", data.getUploadedFileName() );
                xml.attribute( null, "imgSeq", data.getMyPhotoSelectImageData().F_IMG_SQNC);
                xml.attribute( null, "alpha", "1" );
                xml.attribute( null, "bgColor", "16777215" );
                xml.endTag(null, "object");

                SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point3-5");

                float[] fontPos = getFontPos( data, new float[]{frameW, frameH}, new float[]{scaledSize[2], scaledSize[3]}, fontPosData );
                xml.startTag(null, "object");
                xml.attribute( null, "color", "#FFFFFF" );
                xml.attribute( null, "width", fontPos[2] + "" );
                xml.attribute( null, "height", fontPos[3] + "" );
                xml.attribute( null, "angle", "0" );
                xml.attribute( null, "x", fontPos[0] + "" );
                xml.attribute( null, "y", fontPos[1] + "" );
                xml.attribute( null, "type", "html" );
                xml.attribute( null, "direction", "horizontal" );
                if( data.isShowPhotoDate() && !StringUtil.isEmpty(data.getAuraXmlDateString()) ) {
                    xml.attribute( null, "noScaleFontSize", "9" );
                    xml.cData( "<div align=\"right\" style=\"white-space:pre-wrap;font-family:'스냅스 윤고딕 330'; color:#ffffff; font-weight:normal; text-decoration:initial; font-style:normal; font-size:9px;\">" + data.getAuraXmlDateString() + "</div>" );
                }
                xml.endTag(null, "object");

                xml.endTag(null, "page");
                xml.endTag(null, "item");
            }

            SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML point4");

            xml.endTag(null, "Order");
            xml.endDocument();

            fileStream.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("photo print project makeAuraOrderXML exception 4 :" + e.toString());
            return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
        }
        return new SnapsXmlMakeResult.Builder().setSuccess(true).setXmlFile(saveFile).create();
    }

    private float[] getAuraPos( PhotoPrintData data, float[] frameSize, float[] imageSize ) {
        float dataX = data.getX();
        float dataY = data.getY();
        if( data.getMyPhotoSelectImageData().ROTATE_ANGLE % 180 != 0 ) {
            float temp = dataX;
            dataX = dataY;
            dataY = temp;
        }
        float x = dataX + ( frameSize[0] - imageSize[0] ) / 2f;
        float y = dataY + ( frameSize[1] - imageSize[1] ) / 2f;

        if( !data.isImageFull() ) {
            if( frameSize[0] == imageSize[0] ) {
                if( y + imageSize[1] < frameSize[1] )
                    y = frameSize[1] - imageSize[1];
                else if( y > 0 )
                    y = 0;

                x = 0;
            }
            else if( frameSize[1] == imageSize[1] ) {
                if( x + imageSize[0] < frameSize[0] )
                    x = frameSize[0] - imageSize[0];
                else if( x > 0 )
                    x = 0;

                y = 0;
            }
        }

        return new float[]{ x, y };
    }

    public static float[] getFontPos( PhotoPrintData data, float[] frameSize, float[] imageSize, float[] fontPosData ) {
        boolean isRotated = data.getAngle() % 180 != 0;
        float marginR = fontPosData[0];
        float height = fontPosData[3];
        float x, y, w, h;
        if( data.isImageFull() ) {
            x = ( frameSize[ isRotated ? 1 : 0 ] - imageSize[ isRotated ? 1 : 0 ] ) / 2 + marginR;
            y = ( frameSize[ isRotated ? 0 : 1 ] - imageSize[ isRotated ? 0 : 1 ] ) / 2 + imageSize[ isRotated ? 0 : 1 ] - height;
            w = imageSize[ isRotated ? 1 : 0
                    ] - 2 * marginR;
            h = height;
        }
        else {
            x = marginR;
            y = frameSize[ isRotated ? 0 : 1 ] - height;
            w = frameSize[ isRotated ? 1 : 0 ] - 2 * marginR;
            h = height;
        }

        return new float[]{ x, y, w, h };
    }

    @Override
    public SnapsXmlMakeResult makeOptionXML(String filePath) {
        File saveFile = null;

        if (filePath == null) {
            try {
                saveFile = Config.getPROJECT_FILE("imgOption.xml");
                if (saveFile == null) throw new SnapsIOException("failed make imgOption");
            } catch (Exception e) {
                Dlog.e(TAG, e);
                return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
            }
        } else {
            saveFile = new File(filePath);
        }

        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                Dlog.e(TAG, e);
                return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
            }
        }

        FileOutputStream fileStream = null;

        try {
            fileStream = new FileOutputStream(saveFile);
        } catch (FileNotFoundException e) {
            Dlog.e(TAG, e);
            return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
        }

        try {
            SnapsXML xml = new SnapsXML(fileStream);
            xml.startTag(null, "string");
            xml.startTag(null, "DTL");
            xml.addTag(null, "F_PROJ_CODE", projCode);
            xml.addTag(null, "F_IMGX_YEAR", datas.get(0).getMyPhotoSelectImageData().F_IMG_YEAR);
            xml.addTag(null, "F_IMGX_SQNC", datas.get(0).getMyPhotoSelectImageData().F_IMG_SQNC);
            xml.endTag(null, "DTL");

            Date date;
            for (PhotoPrintData item : datas) {
                xml.startTag(null, "ImageOrderInfo");
                xml.addTag(null, "F_ORDR_CODE", "");
                xml.addTag(null, "F_ALBM_ID", projCode);
                xml.addTag(null, "F_PROD_CODE", prodCode);
                xml.addTag(null, "F_IMGX_YEAR", item.getMyPhotoSelectImageData().F_IMG_YEAR);
                xml.addTag(null, "F_IMGX_SQNC", item.getMyPhotoSelectImageData().F_IMG_SQNC);
                xml.addTag(null, "F_PRNT_CNT", item.getCount() + "");
                xml.addTag(null, "F_GLSS_TYPE", PhotoPrintData.TYPE_GLOSSY.equalsIgnoreCase(item.getGlossyType()) ? "G" : "M");
                xml.addTag(null, "F_EDGE_TYPE", item.isMakeBorder() ? "Y" : "N");
                xml.addTag(null, "F_POOL_TYPE", item.isImageFull() ? "i" : "p");
                xml.addTag(null, "F_BRHT_TYPE", item.isAdjustBrightness() ? "Y" : "N");
                xml.addTag(null, "F_SHOW_DATE", item.isShowPhotoDate() ? "Y" : "N");
                xml.addTag(null, "F_RCMM_YORN", "");
                xml.addTag(null, "F_UNIT_COST", sellPrice + "");
                xml.addTag(null, "F_SELL_PRICE", (sellPrice * totalCount) + "");
                xml.addTag(null, "F_TRIM_CORD", "");
                xml.addTag(null, "F_PCKG_CODE", "");
                xml.addTag(null, "F_VIEW_NO", "");
                xml.addTag(null, "F_APP_VER", Config.getAPP_VERSION());
                date = new Date();
                String regDateXml = new SimpleDateFormat( "hh:mm:ss:SSS" ).format( date );
                xml.addTag(null, "REG_DATE_XML", regDateXml);
                xml.endTag(null, "ImageOrderInfo");
            }

            xml.endTag(null, "string");
            xml.endDocument();

            fileStream.close();

        } catch (Exception e) {
            Dlog.e(TAG, e);
            return new SnapsXmlMakeResult.Builder().setSuccess(false).setException(e).create();
        }
        return new SnapsXmlMakeResult.Builder().setSuccess(true).setXmlFile(saveFile).create();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void setProcessStep(int step, int subStep) {
        this.step = step;
        this.subStep = subStep;
        // 원본사진을 올리는 경우 완료 카운트 증가.
        if (step == 3 && subStep > 0) {
            this.completeProgress = subStep;
        }
    }

    @Override
    public int getProcessStep() {
        return step;
    }

    @Override
    public int getProcessSubStep() {
        return subStep;
    }

    @Override
    public int getCancel() {
        return cancel;
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public void setRetryCount(int count) {
        if (count == -1) {
            retryCount = 0;
            return;
        }

        retryCount += count;
    }

    @Override
    public boolean isFacebookImage(int index) {
        return datas.get( index ).getMyPhotoSelectImageData().ORIGINAL_PATH.startsWith( "http" );
    }

    @Override
    public int getUploadComleteCount() {
        return completeProgress;
    }

    @Override
    public int getImageKindWithIndex(int index) {
        return datas.get( index ).getMyPhotoSelectImageData().KIND;
    }

    @Override
    public boolean chagneImageSize(int index, int width, int height) {
        return false;
    }

    @Override
    public boolean removeImageDataWithImageId(int imageId) throws Exception {
        if (datas == null) return false;
        Dlog.d("removeImageDataWithImageId() data index:" + imageId + ", size:" + datas.size());

        for (int ii = datas.size() - 1; ii>=0; ii--) {
            PhotoPrintData data = datas.get(ii);
            if (data == null) continue;

            MyPhotoSelectImageData imageData = data.getMyPhotoSelectImageData();
            if (imageData == null) continue;

            if (imageId == (int)imageData.IMAGE_ID) {
                datas.remove(ii);
                break;
            }
        }

        Dlog.d("removeImageDataWithImageId() datas.size:" + datas.size());
        return true;
    }

    @Override
    public PhotoPrintData getPhotoPrintDataWithImageId(int imageId) {
        return findPhotoPrintDataWithId(imageId);
    }

    @Override
    public PhotoPrintData getPhotoPrintDataWithIndex(int index) {
        if (datas == null || datas.size() <= index) return null;
        return datas.get(index);
    }

    private PhotoPrintData findPhotoPrintDataWithId(int imageId) {
        if (datas == null) return null;

        for (PhotoPrintData data : datas) {
            if (data == null) continue;

            MyPhotoSelectImageData imageData = data.getMyPhotoSelectImageData();
            if (imageData == null) continue;

            if (imageId == imageData.IMAGE_ID) {
                return data;
            }
        }
        return null;
    }

}
