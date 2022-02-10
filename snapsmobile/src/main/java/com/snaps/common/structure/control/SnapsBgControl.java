package com.snaps.common.structure.control;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ColorUtil;
import com.snaps.common.utils.ui.StringUtil;

import java.io.Serializable;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;

public class SnapsBgControl extends SnapsControl implements iSnapsControlInterface, Parcelable, Serializable {
	private static final String TAG = SnapsBgControl.class.getSimpleName();
	private static final long serialVersionUID = 8018682963873389663L;
	
	public String layerName = "";
	public String type = "";
	public String bgColor = "";
	public String coverColor = "";
	public String name = "";
	public String value = "";

	/** Year Seq **/
	public String imgYear = "";
	/** Image Seq **/
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

	public boolean isPng() {
		return resourceURL.contains(".png");
	}

	@Override
	public SnapsXML getControlSaveXML(SnapsXML xml) {
		try {
			xml.startTag(null, "image");
			xml.attribute(null, "x", this.x);
			xml.attribute(null, "y", this.y);
			xml.attribute(null, "width", this.width);
			xml.attribute(null, "height", this.height);
			xml.attribute(null, "angle", this.angle);
			xml.attribute(null, "isEditable", isClick);

			// regist
			xml.startTag(null, "regist");
			if (this.type.equals("webitem")) {
				xml.attribute(null, "name", "background");
			} else {
				xml.attribute(null, "name", this.regName);
			}
			xml.attribute(null, "regvalue", this.regValue);
			xml.endTag(null, "regist");

			// source
			xml.startTag(null, "source");
			xml.attribute(null, "type", this.type);
			xml.attribute(null, "fit", this.fit);
			xml.attribute(null, "bgcolor", this.bgColor);
			xml.attribute(null, "target_type", this.srcTargetType);
			xml.attribute(null, "target", this.srcTarget);

			xml.attribute(null, "resourceURL", this.resourceURL);

			xml.endTag(null, "source");

			xml.endTag(null, "image");

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return xml;
	}

	@Override
	public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY) {
		try {
			xml.startTag(null, "object");

			if (!this.srcTarget.equalsIgnoreCase("")) {
				// resourceURL 이미지 배경.
				xml.attribute(null, "type", "design");
				xml.attribute(null, "rscType", this.srcTargetType);
				xml.attribute(null, "id", this.srcTarget);

				if (!this.bgColor.equalsIgnoreCase("")) {
					xml.attribute(null, "bgColor", String.valueOf(ColorUtil.getParseColor("#" + this.bgColor)));
				}
			} else if (!this.bgColor.equalsIgnoreCase("")) {
				// 배경이 색상일 경우..
				xml.attribute(null, "type", "rectangle");
				if (Const_PRODUCT.isFrameProduct() &&!Config.isCalendar()) {
					xml.attribute(null, "bgColor", String.valueOf(ColorUtil.getParseColor("#" + ColorUtil.covertRGBToBGR(this.bgColor))));
				} else {
					xml.attribute(null, "bgColor", String.valueOf(ColorUtil.getParseColor("#" + this.bgColor)));
				}
			} else {
				// 기본 흰색으로..
				xml.attribute(null, "type", "rectangle");

				xml.attribute(null, "bgColor", String.valueOf(ColorUtil.getParseColor("#ffffffff")));

			}

			xml.attribute(null, "alpha", "255");
			xml.attribute(null, "angle", this.angle);
			
//			boolean isRotatePage = sp.type.equals(SnapsPage.PAGETYPE_PAGE)
//					&& ( (Const_PRODUCT.isCardShapeWide() && !Const_PRODUCT.isCardShapeFolder()) || ( !Const_PRODUCT.isCardShapeWide() && Const_PRODUCT.isCardShapeFolder()) );
			int pageWidth = !StringUtil.isEmpty(sp.width) ? Integer.parseInt(sp.width) : 0;
			int diffXInt = (int) difX;
			boolean isSameMmWidthAndWidth = pageWidth == diffXInt;

//			if(isRotatePage) {
//				xml.attribute(null, "width", String.valueOf(difY));
//				xml.attribute(null, "height", String.valueOf(difX));
//			} else {
				if(Const_PRODUCT.isNewYearsCardProduct() || Const_PRODUCT.isCardProduct()) {
					if (isSameMmWidthAndWidth) {
						xml.attribute(null, "width", String.valueOf(difX));
						xml.attribute(null, "height", String.valueOf(difY));
					} else {
						xml.attribute(null, "width", String.valueOf(difY));
						xml.attribute(null, "height", String.valueOf(difX));
					}
				}else {
					xml.attribute(null, "width", String.valueOf(difX));
					xml.attribute(null, "height", String.valueOf(difY));
				}

//			}

			String[] imageRc = this.getRc().replace(" ", "|").split("\\|");
			String[] imageRcClip = this.getRcClip().replace(" ", "|").split("\\|");

			// TODO : 수정 필요..
			if (this.angle != "0" && this.angle != "") {
				if (sp.type == SnapsPage.PAGETYPE_COVER) {
					xml.attribute(null, "x", imageRc[0]);
					xml.attribute(null, "y", imageRc[1]);
					xml.attribute(null, "clipX", imageRcClip[0]);
					xml.attribute(null, "clipY", imageRcClip[1]);
				} else {
					xml.attribute(null, "x", imageRc[0]);
					xml.attribute(null, "y", imageRc[1]);
					xml.attribute(null, "clipX", imageRcClip[0]);
					xml.attribute(null, "clipY", imageRcClip[1]);
				}
			} else {
				if (sp.type == SnapsPage.PAGETYPE_COVER) {
					xml.attribute(null, "x", imageRc[0]);
					xml.attribute(null, "y", imageRc[1]);
					xml.attribute(null, "clipX", imageRcClip[0]);
					xml.attribute(null, "clipY", imageRcClip[1]);
				} else {
					xml.attribute(null, "x", imageRc[0]);
					xml.attribute(null, "y", imageRc[1]);
					xml.attribute(null, "clipX", imageRcClip[0]);
					xml.attribute(null, "clipY", imageRcClip[1]);
				}
			}

//			if(isRotatePage) {
//				xml.attribute(null, "clipWidth", String.valueOf(difY));
//				xml.attribute(null, "clipHeight", String.valueOf(difX));
//			} else {
				if(Const_PRODUCT.isNewYearsCardProduct() || Const_PRODUCT.isCardProduct()) {
					if (isSameMmWidthAndWidth) {
						xml.attribute(null, "clipWidth", String.valueOf(difX));
						xml.attribute(null, "clipHeight", String.valueOf(difY));
					} else {
						xml.attribute(null, "clipWidth", String.valueOf(difY));
						xml.attribute(null, "clipHeight", String.valueOf(difX));
					}
				}else {
					xml.attribute(null, "clipWidth", String.valueOf(difX));
					xml.attribute(null, "clipHeight", String.valueOf(difY));
				}
//			}

			// 사진틀이 있을 경우 사진틀..
			// 이미지 효과 저장. bright / sharpen / grayscale / sepia
			xml.endTag(null, "object");
		} catch (Exception e) {
			SnapsLogger.appendOrderLog("getControlAuraOrderXML bgControl exception. " + e.toString());
			Dlog.e(TAG, e);
		}

		return xml;
	}



	
	public SnapsBgControl() {}
	
	public SnapsBgControl(Parcel in) {
		super(in);
		readFromParcel(in);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(layerName);
		dest.writeString(type);
		dest.writeString(bgColor);
		dest.writeString(coverColor);
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
		coverColor = in.readString();
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
		public SnapsBgControl createFromParcel(Parcel in) {
			return new SnapsBgControl(in);
		}

		@Override
		public SnapsBgControl[] newArray(int size) {
			return new SnapsBgControl[size];
		}
	};

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		return sb.toString();
	}
}
