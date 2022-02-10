package com.snaps.mobile.activity.photoprint.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.parser.GetNewPhotoPrintSaveXMLHandler;
import com.snaps.common.structure.SnapsXML;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintProject;

import org.xml.sax.Attributes;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by songhw on 2017. 2. 22..
 */

public class PhotoPrintData implements Parcelable, Serializable {
    private static final String TAG = PhotoPrintData.class.getSimpleName();

    private static final long serialVersionUID = 1501636277242207365L;

    public static final String TYPE_GLOSSY = "glossy";
    public static final String TYPE_MATT = "matt";

    private MyPhotoSelectImageData myPhotoSelectImageData;

    private String glossyType = TYPE_GLOSSY;
    private String align, fontFamily, fontStyleHtml, orgImageSize, tinyPath;

    private boolean makeBorder = false;
    private boolean isImageFull = false;
    private boolean adjustBrightness = true;
    private boolean showPhotoDate = false;
    private boolean isSelected = false;

    private int[] size;

    private int count = 1;
    private int angle = 0;
    private float x, y, width, height, fontSize;

    public PhotoPrintData() {}

    public PhotoPrintData( MyPhotoSelectImageData myPhotoSelectImageData ) { this.myPhotoSelectImageData = myPhotoSelectImageData; }

    public PhotoPrintData( PhotoPrintData baseData ) {
        size = baseData.size;
        glossyType = baseData.glossyType;
        makeBorder = baseData.makeBorder;
        isImageFull = baseData.isImageFull;
        isSelected = baseData.isSelected;
        adjustBrightness = baseData.adjustBrightness;
        showPhotoDate = baseData.showPhotoDate;
        count = baseData.count;
    }

    protected PhotoPrintData(Parcel in) {
        myPhotoSelectImageData = in.readParcelable(MyPhotoSelectImageData.class.getClassLoader());
        glossyType = in.readString();
        align = in.readString();
        fontFamily = in.readString();
        fontStyleHtml = in.readString();
        orgImageSize = in.readString();
        tinyPath = in.readString();
        makeBorder = in.readByte() != 0;
        isImageFull = in.readByte() != 0;
        adjustBrightness = in.readByte() != 0;
        showPhotoDate = in.readByte() != 0;
        isSelected = in.readByte() != 0;
        size = in.createIntArray();
        count = in.readInt();
        angle = in.readInt();
        x = in.readFloat();
        y = in.readFloat();
        width = in.readFloat();
        height = in.readFloat();
        fontSize = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(myPhotoSelectImageData, flags);
        dest.writeString(glossyType);
        dest.writeString(align);
        dest.writeString(fontFamily);
        dest.writeString(fontStyleHtml);
        dest.writeString(orgImageSize);
        dest.writeString(tinyPath);
        dest.writeByte((byte) (makeBorder ? 1 : 0));
        dest.writeByte((byte) (isImageFull ? 1 : 0));
        dest.writeByte((byte) (adjustBrightness ? 1 : 0));
        dest.writeByte((byte) (showPhotoDate ? 1 : 0));
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeIntArray(size);
        dest.writeInt(count);
        dest.writeInt(angle);
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(width);
        dest.writeFloat(height);
        dest.writeFloat(fontSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoPrintData> CREATOR = new Creator<PhotoPrintData>() {
        @Override
        public PhotoPrintData createFromParcel(Parcel in) {
            return new PhotoPrintData(in);
        }

        @Override
        public PhotoPrintData[] newArray(int size) {
            return new PhotoPrintData[size];
        }
    };

    /**
     * create new clone object except myPhotoSelectImageData instance
     * @return
     */
    public PhotoPrintData clone() {
        PhotoPrintData data = new PhotoPrintData( this );
        data.myPhotoSelectImageData = myPhotoSelectImageData;
        data.angle = angle;
        data.x = x;
        data.y = y;
        data.width = width;
        data.height = height;
        data.align = align;
        data.fontFamily = fontFamily;
        data.fontStyleHtml = fontStyleHtml;
        data.orgImageSize = orgImageSize;
        data.tinyPath = tinyPath;
        data.fontSize = fontSize;
        return data;
    }

    public void syncOptions( PhotoPrintData tempData ) {
        size = tempData.size;
        glossyType = tempData.glossyType;
        makeBorder = tempData.makeBorder;
        if( !isImageFull() && tempData.isImageFull() )
            initPosition();
        isImageFull = tempData.isImageFull;
        adjustBrightness = tempData.isAdjustBrightness();
        showPhotoDate = tempData.showPhotoDate;
        count = tempData.count;
    }

    public static boolean isChanged( PhotoPrintData origin, PhotoPrintData newData ) {
        return !origin.glossyType.equalsIgnoreCase( newData.glossyType )
                || origin.makeBorder != newData.makeBorder
                || origin.isImageFull != newData.isImageFull
                || origin.adjustBrightness != newData.adjustBrightness
                || origin.showPhotoDate != newData.showPhotoDate
                || origin.myPhotoSelectImageData != newData.myPhotoSelectImageData
                || origin.count != newData.count
                || origin.x != newData.x
                || origin.y != newData.y
                || origin.angle != newData.angle;
    }



    public void toggleSelected() { isSelected = !isSelected; }
    public void cancelSelect() { isSelected = false; }

    /**
     * getters / setters
     */
    public boolean isShowPhotoDate() { return showPhotoDate; }
    public void setShowPhotoDate( boolean flag ) { showPhotoDate = flag; }

    public boolean isSelected() { return isSelected; }

    public String getGlossyType() { return glossyType; }
    public void setGlossyType(String glossyType) { this.glossyType = glossyType; }

    public boolean isMakeBorder() { return makeBorder; }
    public void setMakeBorder(boolean makeBorder) { this.makeBorder = makeBorder; }

    public boolean isImageFull() { return isImageFull; }
    public void setImageFull(boolean imageFull) { isImageFull = imageFull; }

    public boolean isRotated() { return angle == 90 || angle == 270; }

    public boolean isAdjustBrightness() { return adjustBrightness; }
    public void setAdjustBrightness(boolean adjustBrightness) { this.adjustBrightness = adjustBrightness; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getAngle() { return angle; }
    public void setAngle(int angle) { this.angle = angle; }

    public MyPhotoSelectImageData getMyPhotoSelectImageData() { return myPhotoSelectImageData; }
    public void setMyPhotoSelectImageData( MyPhotoSelectImageData data ) { myPhotoSelectImageData = data; }

    public int[] getSize() { return size; }
    public void setSize(int[] size) { this.size = size; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public void setPosition( float x, float y ) {
        this.x = x;
        this.y = y;
    }

    public float getFontSize() { return fontSize; }
    public void setFontSize(float fontSize) { this.fontSize = fontSize; }

    public String getAlign() { return align; }
    public void setAlign(String align) { this.align = align;}

    public String getFontFamily() { return fontFamily;}
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

    public String getFontStyleHtml() { return fontStyleHtml; }
    public void setFontStyleHtml(String fontStyleHtml) { this.fontStyleHtml = fontStyleHtml; }

    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public String getSaveXmlDateString() {
        long time = getMyPhotoSelectImageData().photoTakenDateTime;
        if( time < 1000000000000L )
            time *= 1000;
        if( time < 1 ) return "";

        return new SimpleDateFormat( "yyyy:MM:dd hh:mm:ss" ).format( new Date(time) );
    }
    public void setTimeFromDateString( String dateString ) {
        getMyPhotoSelectImageData().photoTakenDateTime = 0;
        if( StringUtil.isEmpty(dateString) ) return;

        try {
            getMyPhotoSelectImageData().photoTakenDateTime = new SimpleDateFormat( "yyyy:MM:dd hh:mm:ss" ).parse( dateString ).getTime();
        } catch (ParseException e) {
            Dlog.e(TAG, e);
        }
    }

    public void makeSaveXml( SnapsXML xml, int index, float[] fontPosData, String borderThickness, String fontStyleHtmlString ) {
        int frameW, frameH;
        int editW = Math.min( getSize()[0], getSize()[1] );
        int editH = Math.max( getSize()[0], getSize()[1] );
        int temp;

        float imageW, imageH;
        if( !StringUtil.isEmpty(getOrgImageSize()) && getOrgImageSize().contains(" ") ) {
            String[] sizeString = getOrgImageSize().split(" ");
            imageW = Float.parseFloat(sizeString[0]);
            imageH = Float.parseFloat(sizeString[1]);
            if (imageW <= 0 || imageH <= 0) {
                imageW = Float.parseFloat( getMyPhotoSelectImageData().F_IMG_WIDTH );
                imageH = Float.parseFloat( getMyPhotoSelectImageData().F_IMG_HEIGHT );
            }
        }
        else if( !StringUtil.isEmpty(getMyPhotoSelectImageData().F_IMG_WIDTH) && !StringUtil.isEmpty(getMyPhotoSelectImageData().F_IMG_HEIGHT) ) {
            imageW = Float.parseFloat( getMyPhotoSelectImageData().F_IMG_WIDTH );
            imageH = Float.parseFloat( getMyPhotoSelectImageData().F_IMG_HEIGHT );
        }
        else // size 정보 없음.
            return;

        if (imageW <= 0 || imageH <= 0) return; //Size error

        boolean rotatedImage = ( getMyPhotoSelectImageData().ROTATE_ANGLE + angle ) % 180 != 0;
        boolean isHorizontalImage = ( imageW < imageH && rotatedImage ) || ( imageW > imageH && !rotatedImage );

        if( isHorizontalImage ) {
            temp = editW;
            editW = editH;
            editH = temp;
        }

        frameW = editW;
        frameH = editH;

        if( rotatedImage ) {
            temp = frameW;
            frameW = frameH;
            frameH = temp;
        }

        int[] scaledSize = UIUtil.getPosByImageType( isImageFull(), new int[]{frameW, frameH}, new int[]{(int)imageW, (int)imageH} );
        xml.startTag(null, "scene");
        xml.attribute(null, "sceneResolutionWarring", "false");
        xml.attribute(null, "printBrightType", getBooleanString(isAdjustBrightness()));
        xml.attribute(null, "width", editW + "");
        xml.attribute(null, "height", editH + "");
        xml.attribute(null, "objectNum", "3");
        xml.attribute(null, "printRegDateType", getBooleanString(isShowPhotoDate()));
        xml.attribute(null, "printBorderType", getBooleanString(isMakeBorder()));
        xml.attribute(null, "printGlossyType", getGlossyType());
        xml.attribute(null, "reactId", index + "");
        xml.attribute(null, "type", "page");
        xml.attribute(null, "printCnt", getCount() + "");
        xml.attribute(null, "printImgFullType", getBooleanString(isImageFull()));

        xml.startTag(null, "objects");
        xml.attribute(null, "width", editW + "");
        xml.attribute(null, "height", editH + "");
        xml.attribute(null, "value", "0");
        xml.attribute(null, "status", "ready");
        xml.attribute(null, "angle", "0");
        xml.attribute(null, "x", "0");
        xml.attribute(null, "y", "0");
        xml.attribute(null, "reactId", index + "_0");
        xml.attribute(null, "type", "rectangle");
        xml.attribute(null, "alpha", "100");
        xml.attribute(null, "bgColor", "#FFFFFF");
        xml.endTag(null, "objects");

        xml.startTag(null, "objects");
        xml.attribute(null, "imgSeq", getMyPhotoSelectImageData().F_IMG_SQNC);
        xml.attribute(null, "imgYear", getMyPhotoSelectImageData().F_IMG_YEAR);
        xml.attribute(null, "file", StringUtil.convertEmojiUniCodeToAlias(getMyPhotoSelectImageData().F_IMG_NAME));
        xml.attribute(null, "imgKind", getMyPhotoSelectImageData().KIND + "" );
        xml.attribute(null, "borderOption", "inner_border");

        xml.attribute(null, "scaledWidth", scaledSize[2] + "");
        xml.attribute(null, "scaledHeight", scaledSize[3] + "");
        xml.attribute(null, "zoomSlideVal", "0");
        xml.attribute(null, "borderURL", "");
        xml.attribute(null, "thumUrl", StringUtil.convertEmojiUniCodeToAlias(getMyPhotoSelectImageData().THUMBNAIL_PATH));
        xml.attribute(null, "uploadUrl", StringUtil.convertEmojiUniCodeToAlias(getMyPhotoSelectImageData().F_UPLOAD_PATH));
        xml.attribute(null, "originUrl", StringUtil.convertEmojiUniCodeToAlias(getMyPhotoSelectImageData().ORIGINAL_PATH));
        xml.attribute(null, "tinyUrl", StringUtil.convertEmojiUniCodeToAlias(getTinyPath()));
        xml.attribute(null, "angleClip", "0");
        xml.attribute(null, "maskURL", "");
        xml.attribute(null, "sizeWidth", imageW + "");
        xml.attribute(null, "sizeHeight", imageH + "");
        xml.attribute(null, "imageRotate", getMyPhotoSelectImageData().ROTATE_ANGLE + "" );
        xml.attribute(null, "width", scaledSize[2] + "");
        xml.attribute(null, "height", scaledSize[3] + "");
        xml.attribute(null, "fileName", StringUtil.convertEmojiUniCodeToAlias(getUploadedFileName()));

        int imageClipW = editW;
        int imageClipH = editH;
        float clipX = 0;
        float clipY = 0;
        if( isImageFull() ) {
            imageClipW = scaledSize[ isRotated() ? 3 : 2 ];
            imageClipH = scaledSize[ isRotated() ? 2 : 3 ];
            clipX = ( (float)editW - (float)imageClipW ) / 2f;
            clipY = ( (float)editH - (float)imageClipH ) / 2f;
        }
        xml.attribute(null, "clipX", clipX + "");
        xml.attribute(null, "clipY", clipY + "");
        xml.attribute(null, "bordersinglecolortype", "1");
        xml.attribute(null, "bordersinglealpha", "100");
        xml.attribute(null, "border", "");
        xml.attribute(null, "resolutionWarring", "false");
        xml.attribute(null, "mask", "");
        xml.attribute(null, "bordersinglecolor", "#FFFFFF");
        xml.attribute( null, "bordersinglethick", isMakeBorder() ? borderThickness : "0" );
        xml.attribute( null, "clipFrameType", isMakeBorder() ? "borderColor" : "" );

        xml.attribute(null, "value", "0");
        xml.attribute(null, "effect", getMyPhotoSelectImageData().EFFECT_PATH);
        xml.attribute(null, "status", index == 0 ? "ready" : "blob");
        xml.attribute(null, "zoom", "1");
        xml.attribute(null, "angle", getAngle() + getMyPhotoSelectImageData().ROTATE_ANGLE + "");
        xml.attribute(null, "tmpImgSeq", "");
        xml.attribute(null, "fileOrgName", StringUtil.convertEmojiUniCodeToAlias(getMyPhotoSelectImageData().F_IMG_NAME));
        xml.attribute(null, "fileOrgPath", StringUtil.convertEmojiUniCodeToAlias(getMyPhotoSelectImageData().PATH));

        if( (imageClipW - imageClipH) * (imageW - imageH) < 0 ) {
            temp = imageClipW;
            imageClipW = imageClipH;
            imageClipH = temp;
        }

        if( (getAngle() + getMyPhotoSelectImageData().ROTATE_ANGLE) % 180 != 0 ) {
            temp = imageClipW;
            imageClipW = imageClipH;
            imageClipH = temp;
        }

        xml.attribute(null, "clipWidth", imageClipW + "");
        xml.attribute(null, "clipHeight", imageClipH + "");
        float[] position = getConvertedPositionToHtml5( getX(), getY(), new float[]{imageClipW, imageClipH}, new float[]{scaledSize[2], scaledSize[3]} );
        xml.attribute(null, "x", position[0] + "");
        xml.attribute(null, "y", position[1] + "");
        xml.attribute(null, "reactId", index + "_1");
        xml.attribute(null, "type", "image");
        xml.attribute(null, "effectName", getMyPhotoSelectImageData().EFFECT_TYPE);
        xml.attribute(null, "exifdate", getSaveXmlDateString());
        xml.attribute(null, "alpha", "100");
        xml.attribute(null, "bgColor", "#FFFFFF");
        xml.endTag(null, "objects");

        int startIndex = fontStyleHtmlString.indexOf( "<TextFlow " );
        int endIndex = fontStyleHtmlString.indexOf( ">" );
        String htmlText = fontStyleHtmlString.substring( startIndex + "<TextFlow ".length(), endIndex );
        StringBuilder sb = new StringBuilder();
        sb.append( "<p style=\"text-align:" ).append( getHtmlStringByName(htmlText, "textAlign") );
        sb.append( "\"><font color=\"" ).append( getHtmlStringByName(htmlText, "color") );
        sb.append( "\" face=\"" ).append( getHtmlStringByName(htmlText, "fontFamily") );
        sb.append( "\" style=\"font-size:" ).append( getHtmlStringByName(htmlText, "fontSize") );
        sb.append( "px;\"></font></p>" );

        xml.startTag(null, "objects");
        float[] fontPos = PhotoPrintProject.getFontPos( this, new float[]{frameW, frameH}, new float[]{scaledSize[2], scaledSize[3]}, fontPosData );
        xml.attribute(null, "width", fontPos[2] + "");
        xml.attribute(null, "height", fontPos[3] + "");
        xml.attribute(null, "status", "ready");
        xml.attribute(null, "angle", "0");
        xml.attribute(null, "placeholder", "");
        xml.attribute(null, "x", fontPos[0] + "");
        xml.attribute(null, "y", fontPos[1] + "");
        xml.attribute(null, "reactId", index + "_2");
        xml.attribute(null, "type", "html");
        xml.attribute(null, "direction", "horizontal");
        xml.text( sb.toString() );
        xml.endTag(null, "objects");

        xml.endTag(null, "scene");
    }

    private String getHtmlStringByName( String htmlString, String name ) {
        if( StringUtil.isEmpty(htmlString) || StringUtil.isEmpty(name) ) return "";

        String[] strAry = htmlString.split( "' " );
        String value = "";

        String[] tempAry;
        for( int i = 0; i < strAry.length; ++i ) {
            tempAry = strAry[i].split( "=" );
            if( tempAry.length > 1 && name.equals(tempAry[0]) ) {
                value = tempAry[1];
                break;
            }
        }

        if( value.contains( "'") )
            value = value.replaceAll( "'", "" );

        return value;
    }

    public void setDataFromSaveXml( GetNewPhotoPrintSaveXMLHandler handler, String localName, Attributes attributes, boolean fromHtml5StyleSave ) {
        if( "scene".equalsIgnoreCase(localName) ) {
            setAdjustBrightness( handler.getBooleanValue(attributes, "printBrightType") );
            setShowPhotoDate( handler.getBooleanValue(attributes, "printRegDateType") );
            setMakeBorder( handler.getBooleanValue(attributes, "printBorderType") );
            setGlossyType( handler.getValue(attributes, "printGlossyType") );
            setImageFull( handler.getBooleanValue(attributes, "printImgFullType") );
            setCount( handler.getIntValue(attributes, "printCnt") );
        }
        else if( "objects".equalsIgnoreCase(localName) ) {
            myPhotoSelectImageData.F_IMG_SQNC = handler.getValue( attributes, "imgSeq" );
            myPhotoSelectImageData.F_IMG_YEAR = handler.getValue( attributes, "imgYear" );
            myPhotoSelectImageData.F_IMG_NAME = StringUtil.convertEmojiAliasToUniCode(handler.getValue( attributes, "file" ));

            myPhotoSelectImageData.ORIGINAL_PATH = StringUtil.convertEmojiAliasToUniCode(handler.getValue( attributes, "originUrl" ));
            myPhotoSelectImageData.THUMBNAIL_PATH = StringUtil.convertEmojiAliasToUniCode(handler.getValue( attributes, "thumUrl" ));
            myPhotoSelectImageData.F_UPLOAD_PATH = StringUtil.convertEmojiAliasToUniCode(handler.getValue( attributes, "uploadUrl" ));
            setTinyPath( StringUtil.convertEmojiAliasToUniCode(handler.getValue(attributes, "tinyUrl")) );
            String kindStr = handler.getValue( attributes, "imgKind" );
            myPhotoSelectImageData.KIND = StringUtil.isEmpty( kindStr ) ? StringUtil.isEmpty( myPhotoSelectImageData.F_IMG_SQNC ) ? Const_VALUES.SELECT_FACEBOOK : Const_VALUES.SELECT_UPLOAD : Integer.parseInt( kindStr );

            myPhotoSelectImageData.F_IMG_WIDTH = handler.getValue( attributes, "sizeWidth" );
            myPhotoSelectImageData.F_IMG_HEIGHT = handler.getValue( attributes, "sizeHeight" );
            myPhotoSelectImageData.ROTATE_ANGLE = 0;
            myPhotoSelectImageData.EFFECT_PATH = handler.getValue( attributes, "effect" );
            myPhotoSelectImageData.EFFECT_TYPE = handler.getValue( attributes, "effectName" );
            setAngle( handler.getIntValue(attributes, "angle") );
            myPhotoSelectImageData.ROTATE_ANGLE = handler.getIntValue( attributes, "rotateAngle" );
            myPhotoSelectImageData.PATH = StringUtil.convertEmojiAliasToUniCode(handler.getValue( attributes, "fileOrgPath" ));
            float[] position = new float[]{ handler.getFloatValue(attributes, "x"), handler.getFloatValue(attributes, "y") };
            if( fromHtml5StyleSave )
                position = getConvertedPositionFromHtml5( position[0], position[1],
                        new float[]{ handler.getFloatValue(attributes, "clipWidth"), handler.getFloatValue(attributes, "clipHeight") },
                        new float[]{ handler.getFloatValue(attributes, "width"), handler.getFloatValue(attributes, "height") } );
            setX( position[0] );
            setY( position[1] );
            setTimeFromDateString( handler.getValue(attributes, "exifdate") );
        }
    }

    private float[] getConvertedPositionFromHtml5( float originX, float originY, float[] clipSize, float[] imageSize ) {
        float[] newPos = new float[2];
        newPos[0] = originX - ( clipSize[0] - imageSize[0] ) / 2;
        newPos[1] = originY - ( clipSize[1] - imageSize[1] ) / 2;
        return newPos;
    }

    private float[] getConvertedPositionToHtml5( float originX, float originY, float[] clipSize, float[] imageSize ) {
        float[] newPos = new float[2];
        newPos[0] = originX + ( clipSize[0] - imageSize[0] ) / 2;
        newPos[1] = originY + ( clipSize[1] - imageSize[1] ) / 2;
        return newPos;
    }

    public String getAuraXmlDateString() {
        long time = getMyPhotoSelectImageData().photoTakenDateTime;
        if( time < 1000000000000L )
            time *= 1000;
        if( time < 1 ) return "";

        return new SimpleDateFormat( "yyyy.MM.dd" ).format( new Date(time) );
    }

    public String getOrgImageSize() { return orgImageSize; }
    public void setOrgImageSize(String orgImageSize) { this.orgImageSize = orgImageSize; }

    public String getTinyPath() { return tinyPath; }
    public void setTinyPath(String tinyPath) { this.tinyPath = tinyPath; }

    public String getUploadedFileName() {
        if( myPhotoSelectImageData == null ) return "";
        String path = myPhotoSelectImageData.F_UPLOAD_PATH;
        int index = 0;
        while( path.contains("/") ) {
            index = path.indexOf( "/" );
            path = path.substring( index + 1, path.length() );
        }
        return path;
    }

    public void initPosition() {
        x = 0;
        y = 0;
    }

    public void resetPosition( PhotoPrintData baseData ) {
        x = isImageFull ? 0 : baseData.getX();
        y = isImageFull ? 0 : baseData.getY();
    }

    private String getBooleanString( boolean flag ) {
        return flag ? "yes" : "no";
    }
}
