package com.snaps.common.structure.control;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ColorUtil;

import java.io.Serializable;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;

public class SnapsFormControl extends SnapsControl implements iSnapsControlInterface, Parcelable, Serializable {
	private static final String TAG = SnapsFormControl.class.getSimpleName();
	/**
	 * 
	 */
	private static final long serialVersionUID = -1084399055126252460L;
	public String layerName = "";
	public String type = "";
	public String bgColor = "";
	public String name = "";
	public String value = "";
	
	/**  Year Seq **/
	public String imgYear = "";
	/**  Image Seq **/
	public String imgSeq = "";
	public String fit = "";
	
	public String uploadPath = "";
	public String angleClip = "0";
	public String sizeOrgImg = "0";
	public String mstPath = "";
	public String orgPath = "";
	public String srcTargetType = "";
	public String srcTarget = "";
	public String contentType = "";
	public String exchange = "false";
	public String helper = "false";
	public String noclip = "false";
	public String useAlpha = "false";
	public String alpha = "1";
	public String formItem = "false";
	public String checkFull = "false";
	public String stick = "";
	public String resourceURL = "";
	
	@Override
	public SnapsXML getControlSaveXML(SnapsXML xml) {
		
		try
		{
			xml.startTag( null, "image");
			
			xml.attribute(null, "rc", this.getRc() );
			xml.attribute(null, "priority", this.priority);
			xml.attribute(null, "angle", this.angle );
			xml.attribute(null, "readOnly", this.readOnly);
			xml.attribute(null, "move", this.move);
			xml.attribute(null, "resize", this.resize);
			xml.attribute(null, "rotate", this.rotate);
			xml.attribute(null, "delete", this.delete);
			xml.attribute(null, "copy", this.copy);
			xml.attribute(null, "exclude", this.exclude);
			xml.attribute(null, "regName", this.regName);
			xml.attribute(null, "regValue", this.regValue);
			xml.attribute(null, "layerName", this.layername);
			xml.attribute(null, "imgYear", this.imgYear);
			xml.attribute(null, "imgSeq", this.imgSeq);
			xml.attribute(null, "uploadPath", this.uploadPath);
			xml.attribute(null, "rcClip", this.getRcClip());
			xml.attribute(null, "angleClip", this.angleClip);
			xml.attribute(null, "sizeOrgImg", this.sizeOrgImg);
			xml.attribute(null, "mstPath", this.mstPath);
			xml.attribute(null, "orgPath", this.orgPath);
			xml.attribute(null, "srcType", this.type );
			xml.attribute(null, "srcTargetType", this.srcTargetType);
			xml.attribute(null, "srcTarget", this.srcTarget);
			xml.attribute(null, "fit", this.fit);
			xml.attribute(null, "bgColor", this.bgColor);
			xml.attribute(null, "exchange", this.exchange);
			xml.attribute(null, "helper", this.helper);
			xml.attribute(null, "noclip", this.noclip);
			xml.attribute(null, "useAlpha", this.useAlpha);
			xml.attribute(null, "alpha", this.alpha);
			xml.attribute(null, "formItem", this.formItem);
			xml.attribute(null, "checkFull", this.checkFull);
			xml.attribute(null, "x", this.x);
			xml.attribute(null, "y", this.y);
			xml.attribute(null, "width", this.width);
			xml.attribute(null, "height", this.height);
			
			xml.attribute(null, "mask", "" );
			xml.attribute(null, "borderOption", "inner" );
			xml.attribute(null, "bordersinglecolortype", "" );
			
			xml.startTag( null, "source");
			xml.attribute(null, "type", this.type);
			xml.attribute(null, "target", this.srcTarget);
			xml.attribute(null, "resourceURL", this.resourceURL);
			
			xml.endTag(null, "source");
						
			// TODO : 이미지 효과 저장 ( 이미지 효과가 있을 경우 )
			xml.endTag(null, "image");
		}
		catch( Exception e )
		{
			Dlog.e(TAG, e);
		}
		
		return xml;
	}

	@Override
	public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX,
			float difY) {
		
		try
		{
			// 이미지일경우.
			xml.startTag( null , "object" );
			xml.attribute( null, "type" , "design" );

			xml.attribute( null, "rscType" , this.srcTargetType );
			xml.attribute( null , "id" , this.srcTarget );
			
			if ( ! this.bgColor.equalsIgnoreCase( "" ) ) {
				xml.attribute( null, "bgColor", String.valueOf( ColorUtil.getParseColor( "#" + this.bgColor ) ) );
			}

			xml.attribute(null, "alpha", "255" );
			xml.attribute(null, "angle" , this.angle );
			xml.attribute(null, "width", this.width );
			xml.attribute(null, "height", this.height );
			
			String [] imageRc = this.getRc().replace(" ", "|").split("\\|");
			String [] imageRcClip = this.getRcClip().replace(" ", "|").split("\\|");
			
			
			// TODO : 수정 필요..
			if ( this.angle != "0" && this.angle != "" )
			{
				if ( sp.type == SnapsPage.PAGETYPE_COVER )
				{
					xml.attribute(null, "x", imageRc[ 0 ] );
					xml.attribute(null, "y", imageRc[ 1 ] );
					xml.attribute(null, "clipX", imageRcClip[ 0 ] );
					xml.attribute(null, "clipY", imageRcClip[ 1 ] );
				}
				else
				{
					xml.attribute(null, "x", imageRc[ 0 ] );
					xml.attribute(null, "y", imageRc[ 1 ] );
					xml.attribute(null, "clipX", imageRcClip[ 0 ] );
					xml.attribute(null, "clipY", imageRcClip[ 1 ] );
				}
			}
			else
			{
				if ( sp.type == SnapsPage.PAGETYPE_COVER )
				{
					xml.attribute(null, "x", imageRc[ 0 ] );
					xml.attribute(null, "y", imageRc[ 1 ] );
					xml.attribute(null, "clipX", imageRcClip[ 0 ] );
					xml.attribute(null, "clipY", imageRcClip[ 1 ] );
				}
				else
				{
					xml.attribute(null, "x", imageRc[ 0 ] );
					xml.attribute(null, "y", imageRc[ 1 ] );
					xml.attribute(null, "clipX", imageRcClip[ 0 ] );
					xml.attribute(null, "clipY", imageRcClip[ 1 ] );
				}
			}
			

			xml.attribute( null , "clipWidth", imageRcClip[ 2 ] );
			xml.attribute( null , "clipHeight", imageRcClip[ 3 ] );
			
			// 사진틀이 있을 경우 사진틀..
			// 이미지 효과 저장. bright / sharpen / grayscale / sepia
			xml.endTag( null , "object" );
		}
		catch ( Exception e )
		{
			SnapsLogger.appendOrderLog("getControlAuraOrderXML formControl exception. " + e.toString());
			Dlog.e(TAG, e);
		}
		
		return xml;
	}
	
	public SnapsFormControl() {}
	
	public SnapsFormControl(Parcel in) {
		super(in);
		readFromParcel(in);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(layerName);
		dest.writeString(type);
		dest.writeString(bgColor);
		dest.writeString(name);
		dest.writeString(value);
		dest.writeString(imgYear);
		dest.writeString(imgSeq);
		dest.writeString(fit);
		dest.writeString(uploadPath);
		dest.writeString(angleClip);
		dest.writeString(sizeOrgImg);
		dest.writeString(mstPath);
		dest.writeString(orgPath);
		dest.writeString(srcTargetType);
		dest.writeString(srcTarget);
		dest.writeString(contentType);
		dest.writeString(exchange);
		dest.writeString(helper);
		dest.writeString(noclip);
		dest.writeString(useAlpha);
		dest.writeString(alpha);
		dest.writeString(formItem);
		dest.writeString(checkFull);
		dest.writeString(stick);
		dest.writeString(resourceURL);
	}
	
	private void readFromParcel(Parcel in) {
		layerName = in.readString();
		type = in.readString();
		bgColor = in.readString();
		name = in.readString();
		value = in.readString();
		imgYear = in.readString();
		imgSeq = in.readString();
		fit = in.readString();
		uploadPath = in.readString();
		angleClip = in.readString();
		sizeOrgImg = in.readString();
		mstPath = in.readString();
		orgPath = in.readString();
		srcTargetType = in.readString();
		srcTarget = in.readString();
		contentType = in.readString();
		exchange = in.readString();
		helper = in.readString();
		noclip = in.readString();
		useAlpha = in.readString();
		alpha = in.readString();
		formItem = in.readString();
		checkFull = in.readString();
		stick = in.readString();
		resourceURL = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public SnapsFormControl createFromParcel(Parcel in) {
			return new SnapsFormControl(in);
		}

		@Override
		public SnapsFormControl[] newArray(int size) {
			return new SnapsFormControl[size];
		}
	};

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		return sb.toString();
	}
}
