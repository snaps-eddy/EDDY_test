//package com.snaps.common.structure.control;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.snaps.common.structure.SnapsXML;
//import com.snaps.common.structure.page.SnapsPage;
//import com.snaps.common.utils.constant.Config;
//import com.snaps.common.utils.constant.Const_VALUE;
//import com.snaps.common.utils.constant.Const_VALUES;
//import com.snaps.common.utils.imageloader.filters.ImageEffectBitmap;
//import errorhandle.logger.Logg;
//import com.snaps.common.utils.ui.BitmapUtil;
//import com.snaps.common.utils.ui.ColorUtil;
//import com.snaps.common.utils.ui.StringUtil;
//import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//
//import errorhandle.logger.SnapsLogger;
//
//public class SnapsTControl extends SnapsLayoutControl implements iSnapsControlInterface, Parcelable, Serializable {
//
//	/**
//	 *
//	 */
//	private static final long serialVersionUID = -6878414381343877068L;
//
//	static public final int TEXT_MARGIN_TOP = 10;
//	static public final int TEXT_MARGIN_BOTTOM = 10;
//	static public final int TEXT_MARGIN_LEFT = 20;
//	static public final int TEXT_MARGIN_RIGHT = 20;
//
//	// 텍스트 속성을 저장하기 위한 변수들
//	public TextFormat format = new TextFormat();
//	public String albumMode = "";
//	public String initialText = "";
//	public String text = "";
//	public String auraOrderText = "";
//	public String htmlText = "";
//	public ArrayList<LineText> textList = new ArrayList<LineText>();
//
//	// TControl을 위한 변수들...
//	private int pageCoverW = 0;// 커버 크기
//	private int pageCoverH = 0;// 커버 크기
//
//	private boolean isLeftPageControl = false; // 페이지 왼쪽 컨트롤인지 오른쪽 컨트롤인지 판단 변수
//	int editState = -1; // -1:초기값... 1:로컬사진,2:콘텐츠,3:텍스트,4:영역편집 // 페이지 편집 상태...
//
//	private int textControlID = -1;
//	private int imageControlID = -1;
//
//	public int getTextControlID() {
//		return textControlID;
//	}
//
//	public void setTextControlID(int textControlID) {
//		this.textControlID = textControlID;
//	}
//
//	public int getImageControlID() {
//		return imageControlID;
//	}
//
//	public void setImageControlID(int imageControlID) {
//		this.imageControlID = imageControlID;
//	}
//
//	public int getEditState() {
//
//		if (srcTargetType.equalsIgnoreCase(Const_VALUE.TEXT_TYPE)) {
//			return 3;
//		} else if (srcTargetType.equalsIgnoreCase(Const_VALUE.USERIMAGE_TYPE)) {
//			if (imgData == null)
//				return -1;
//			else
//				return 1;
//		} else if (srcTargetType.equalsIgnoreCase(Const_VALUE.CONTENT_TYPE)) {
//			return 2;
//		}
//
//		return -1;
//	}
//
//	public void setEditState(int editState) {
//		this.editState = editState;
//	}
//
//	public boolean isLeftPageControl() {
//		return isLeftPageControl;
//	}
//
//	public void setLeftPageControl(boolean isLeftPageControl) {
//		this.isLeftPageControl = isLeftPageControl;
//	}
//
//	public int getPageCoverW() {
//		return pageCoverW;
//	}
//
//	public void setPageCoverW(int pageCoverW) {
//		this.pageCoverW = pageCoverW;
//	}
//
//	public int getPageCoverH() {
//		return pageCoverH;
//	}
//
//	public void setPageCoverH(int pageCoverH) {
//		this.pageCoverH = pageCoverH;
//	}
//
//	@Override
//	public SnapsXML getControlSaveXML(SnapsXML xml) {
//
//		if (srcTargetType.equalsIgnoreCase(Const_VALUE.TEXT_TYPE)) {
//			return getTextSaveXML(xml);
//		} else if (srcTargetType.equalsIgnoreCase(Const_VALUE.USERIMAGE_TYPE) || srcTargetType.equalsIgnoreCase(Const_VALUE.CONTENT_TYPE) || srcTargetType.equalsIgnoreCase(Const_VALUE.NONE_TYPE)) {
//			return getImageControlSaveXML(xml);
//		}
//
//		return xml;
//	}
//
//	@Override
//	public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, int difX, int difY) {
//		if (srcTargetType.equalsIgnoreCase(Const_VALUE.TEXT_TYPE)) {
//			return getTextControlAuraOrderXML(xml, sp, difX, difY);
//		} else if (srcTargetType.equalsIgnoreCase(Const_VALUE.USERIMAGE_TYPE) || srcTargetType.equalsIgnoreCase(Const_VALUE.CONTENT_TYPE)) {
//			return getImageControlAuraOrderXML(xml, sp, difX, difY);
//		}
//
//		return xml;
//	}
//
//	public SnapsXML getTextSaveXML(SnapsXML xml) {
//
//		// <tobject type="browse_file" target_type="text" x="0" y="0" width="371" height="525" angle="0" value="1" face="스냅스 윤고딕 330" size="11"
//		// color="0" align="left" bold="0" italic="0"
//		// initial_text="가나다라마바사"/>
//		try {
//
//			xml.startTag(null, "tobject");
//			xml.attribute(null, "type", type);
//			xml.attribute(null, "target_type", srcTargetType);
//			xml.attribute(null, "x", x);
//			xml.attribute(null, "y", y);
//			xml.attribute(null, "width", width);
//			xml.attribute(null, "height", height);
//			xml.attribute(null, "angle", angle);
//			xml.attribute(null, "value", regValue);
//			xml.attribute(null, "initial_text", initialText);
//			xml.attribute(null, "fontFace", format.fontFace);
//			xml.attribute(null, "fontSize", format.fontSize);
//			xml.attribute(null, "align", format.align);
//			xml.attribute(null, "bold", format.bold);
//			xml.attribute(null, "italic", format.italic);
//			xml.attribute(null, "color", format.fontColor);
//			xml.attribute(null, "text", StringUtil.convertEmojiUniCodeToAlias(text));
//
//			xml.endTag(null, "tobject");
//
//		} catch (Exception e) {
//			Logg.d("Exception Error", "SnapsTextControl getControlSaveXML");
//		}
//
//		return xml;
//
//	}
//
//	public SnapsXML getTextControlAuraOrderXML(SnapsXML xml, SnapsPage sp, int difX, int difY) {
//		try {
//			xml.startTag(null, "object");
//			xml.attribute(null, "type", "textlist");
//
//			if ("true".equals(this.format.verticalView)) {
//				xml.attribute(null, "direction", "vertical");
//			} else
//				xml.attribute(null, "direction", "horizontal");
//
//			xml.attribute(null, "scaleFont", "0");
//			xml.attribute(null, "face", this.format.fontFace);
//			xml.attribute(null, "size", this.format.fontSize);
//
//			if (!this.format.fontColor.equalsIgnoreCase("")) {
//				xml.attribute(null, "color", String.valueOf(ColorUtil.getParseColor("#" + this.format.fontColor)));
//			} else {
//				xml.attribute(null, "color", "0");
//			}
//
//			xml.attribute(null, "align", this.format.align);
//			xml.attribute(null, "bold", "0");
//			xml.attribute(null, "italic", "0");
//
//			if (sp.type.equalsIgnoreCase("cover")) {
//				xml.attribute(null, "x", String.valueOf((Integer.parseInt(this.x) + this.getMaxPageX() + difX)));
//				xml.attribute(null, "y", String.valueOf((Integer.parseInt(this.y) + difY)));
//			} else {
//				xml.attribute(null, "x", Integer.parseInt(this.x) + TEXT_MARGIN_LEFT + "");
//				xml.attribute(null, "y", this.y);
//			}
//
//			if ("true".equals(this.format.verticalView)) {// 세로 텍스트라면 반대로 그리고 회전했기 때문에 aura.xml 만들때는 다시 원래대로 바꿈.
//				xml.attribute(null, "width", this.height);
//				xml.attribute(null, "height", this.width);
//			} else {
//				xml.attribute(null, "width", (Integer.parseInt(this.width) - 40) + "");
//				xml.attribute(null, "height", this.height);
//			}
//
//			xml.attribute(null, "newTextType", "0");
//
//			for (LineText linetext : textList) {
//
//				xml.startTag(null, "line");
//				if (sp.type.equalsIgnoreCase("cover")) {
//					xml.attribute(null, "x", String.valueOf((Integer.parseInt(linetext.x) + this.getMaxPageX() + difX)));
//					xml.attribute(null, "y", String.valueOf((Integer.parseInt(linetext.y) + difY)));
//				} else {
//					xml.attribute(null, "x", linetext.x);
//					xml.attribute(null, "y", linetext.y);
//				}
//
//				if ("true".equals(this.format.verticalView)) {// 세로 텍스트라면 반대로 그리고 회전했기 때문에 aura.xml 만들때는 다시 원래대로 바꿈.
//					xml.attribute(null, "width", linetext.height);
//					xml.attribute(null, "height", linetext.width);
//				} else {
//					if (sp.type.equalsIgnoreCase("page")) {// 내지 텍스트
//						xml.attribute(null, "width", linetext.width);
//						xml.attribute(null, "height", linetext.height);
//					} else {
//						xml.attribute(null, "width", linetext.width);
//						xml.attribute(null, "height", linetext.height);
//					}
//				}
//
//				if (linetext.text == null) {
//					xml.text("");
//				} else {
//					xml.text(linetext.text);
//				}
//
//				xml.endTag(null, "line");
//			}
//
//			xml.endTag(null, "object");
//		} catch (Exception e) {
//			Logg.d("Exception Error", "SnapsTextControl getControlAuraOrderXML");
//			SnapsLogger.appendOrderLog("getControlAuraOrderXML tControl text exception. " + e.toString());
//		}
//
//		return xml;
//	}
//
//	public SnapsXML getImageControlSaveXML(SnapsXML xml) {
//		try {
//
//			// <tobject type="webitem" target_type="content" target="test_c1" resourceURL="/Upload/Data1/Resource/themabg/edit/ed_test_c1.jpg" x="0"
//			// y="0" width="297" height="422" angle="0" value="1"
//			xml.startTag(null, "tobject");
//			// 기본 정보 저장..
//			xml.attribute(null, "type", type);
//			xml.attribute(null, "target_type", srcTargetType);
//			xml.attribute(null, "target", srcTarget);
//			xml.attribute(null, "x", x);
//			xml.attribute(null, "y", y);
//			xml.attribute(null, "width", width);
//			xml.attribute(null, "height", height);
//
//			// FIXME 자유 회전이 적용 된 경우
//			int angle = 0;
//
//			try {
//				angle = this.freeAngle + Integer.parseInt(getAngle());
//			} catch (NumberFormatException e) {
//			}
//
//			xml.attribute(null, "angle", String.valueOf(angle));
//
//			xml.attribute(null, "thumbAngle", angleClip);
//			xml.attribute(null, "value", regValue);
//
//			if (srcTargetType.equalsIgnoreCase(Const_VALUE.CONTENT_TYPE)) {
//				xml.attribute(null, "resourceURL", resourceURL);
//			} else if (srcTargetType.equalsIgnoreCase(Const_VALUE.USERIMAGE_TYPE)) {
//				// crop정보 저장
//				if (imgData != null) {
//					xml = imgData.getSaveXML(xml, false);
//
//				}
//			}
//			xml.endTag(null, "tobject");
//
//		} catch (Exception e) {
//			Logg.d("Exception Error", "SnapsLayoutControl getControlSaveXML");
//		}
//
//		return xml;
//	}
//
//	public SnapsXML getImageControlAuraOrderXML(SnapsXML xml, SnapsPage sp, int difX, int difY) {
//		try {
//			// 이미지일경우.
//			xml.startTag(null, "object");
//
//			String[] imageRc = this.getRc().replace(" ", "|").split("\\|");
//			String[] imageRcClip = this.getRcClip().replace(" ", "|").split("\\|");
//
//			if (srcTargetType.equals(Const_VALUE.CONTENT_TYPE))
//				xml.attribute(null, "type", "design");
//			else
//				xml.attribute(null, "type", "image");
//
//			Logg.d("-------------" + this.bgColor + "-------------");
//			if (!this.bgColor.equalsIgnoreCase("")) {
//				xml.attribute(null, "bgColor", String.valueOf(ColorUtil.getParseColor("#" + this.bgColor)));
//			}
//
//			if (this.imgData != null) {
//
//				// FIXME 자유 회전이 적용 된 경우
//				int angle = 0;
//
//				try {
//					angle = this.freeAngle + Integer.parseInt(getAngle());
//				} catch (NumberFormatException e) {
//				}
//
//				xml.attribute(null, "angle", String.valueOf(angle));
//				xml.attribute(null, "angleClip", this.angleClip);
//
//				boolean urlTypeImage = false;
//				switch (this.imgData.KIND) {
//					case Const_VALUES.SELECT_SNAPS:
//					case Const_VALUES.SELECT_SDK_CUSTOMER:
//					case Const_VALUES.SELECT_BETWEEN:
//						urlTypeImage = true;
//						break;
//					case Const_VALUES.SELECT_PHONE:
//					case Const_VALUES.SELECT_UPLOAD:
//						urlTypeImage = false;
//						break;
//					case Const_VALUES.SELECT_INSTAGRAM:
//					case Const_VALUES.SELECT_KAKAO:
//					case Const_VALUES.SELECT_FACEBOOK:
//					case Const_VALUES.SELECT_GOOGLEPHOTO:
//						urlTypeImage = Config.isSNSBook(); // SNS북이면 이전 방식대로 url로 업로드. 그 외에는 서버에 업로드된 file 타입으로.
//						break;
//				}
//				// 업로드 실패처리
//				if (!urlTypeImage && ("".equals(this.imgData.F_UPLOAD_PATH) || "".equals(this.imgData.F_IMG_YEAR) || "".equals(this.imgData.F_IMG_SQNC))) {
//					SnapsUploadFailedImageDataCollector.addUploadFailedImageData(Config.getPROJ_CODE(), imgData); //FIXME 차라리 사용자에게 해당 사진을 지우고 다시 시도할 수 있도록 원본 이미지 오류라고 치부해 버린다.
//					return null;
//				}
//
//				if( urlTypeImage ) {
//					xml.attribute(null, "uploadType", "url");
//					xml.attribute(null, "uploadURL", StringUtil.convertEmojiUniCodeToAlias(this.imgData.PATH));
//					xml.attribute(null, "file", "");
//				}
//				else {
//					xml.attribute(null, "uploadType", "file");
//					xml.attribute(null, "file", StringUtil.convertEmojiUniCodeToAlias(this.imgData.F_UPLOAD_PATH));
//					xml.attribute(null, "imgYear", this.imgData.F_IMG_YEAR);
//					xml.attribute(null, "imgSeq", this.imgData.F_IMG_SQNC);
//				}
//			} else {
//
//				xml.attribute(null, "angle", "0");
//				xml.attribute(null, "angleClip", "0");
//
//				xml.attribute(null, "rscType", this.srcTargetType);
//				xml.attribute(null, "id", this.srcTarget);
//			}
//
//			xml.attribute(null, "x", String.valueOf((Integer.parseInt(imageRc[0]) + Integer.parseInt(imageRcClip[0]) + difX)));
//			xml.attribute(null, "y", String.valueOf((Integer.parseInt(imageRc[1]) + Integer.parseInt(imageRcClip[1]) + difY)));
//
//			if (this.regName.equalsIgnoreCase("line")) {
//				xml.attribute(null, "width", this.width);
//				xml.attribute(null, "height", this.height);
//			} else if (this.regName.equalsIgnoreCase("background")) {
//				xml.attribute(null, "width", this.width);
//				xml.attribute(null, "height", this.height);
//			} else {
//
//				//이미지의 비율이 맞지 않으면 보정해 준다.
//				imageRc = BitmapUtil.checkImageRatio(imgData, imageRc, imageRcClip);
//
//				//보정에도 실패한다면 에러처리
//				if(imageRc != null && imageRc.length >= 4) {
//					try {
//						float rectW = Float.parseFloat(imageRc[2]);
//						float rectH = Float.parseFloat(imageRc[3]);
//						if(imgData != null) {
//							float imgW = Float.parseFloat(imgData.F_IMG_WIDTH);
//							float imgH = Float.parseFloat(imgData.F_IMG_HEIGHT);
//							boolean isWrongRatio = (imgW > imgH && rectW < rectH) || (imgW < imgH && rectW > rectH);
//							if(isWrongRatio) {
//								Logg.y("tControl ratio err!");
//								return null;
//							}
//						}
//
//						if(imgData != null && ((imageRc[2] != null && imageRc[2].trim().equals("0"))
//								|| (imageRc[3] != null && imageRc[3].trim().equals("0")))) {
//							return null;
//						}
//					} catch (Exception e) {
//						Dlog.e(TAG, e);
//					}
//				}
//
//				xml.attribute(null, "width", imageRc[2]);
//				xml.attribute(null, "height", imageRc[3]);
//			}
//
//			xml.attribute(null, "clipX", String.valueOf((Integer.parseInt(imageRcClip[0]) + difX)));
//			xml.attribute(null, "clipY", String.valueOf((Integer.parseInt(imageRcClip[1]) + difY)));
//
//			xml.attribute(null, "clipWidth", imageRcClip[2]);
//			xml.attribute(null, "clipHeight", imageRcClip[3]);
//
//			if (!this.maskType.equalsIgnoreCase("")) {
//				// 마스크 타입이 있으면 정보를 넣어준다.
//				xml.attribute(null, "mask_type", this.maskType);
//				xml.attribute(null, "mask_radius", this.maskRadius);
//				xml.attribute(null, "mask", "");
//			} else {
//				xml.attribute(null, "mask", this.mask);
//				if (!this.maskRadius.equalsIgnoreCase(""))
//					xml.attribute(null, "mask_radius", this.maskRadius);
//			}
//
//			xml.attribute(null, "imgAlpha", this.alpha);
//			xml.attribute(null, "alpha", "1");
//			xml.attribute(null, "borderOption", "inner");
//			xml.attribute(null, "bordersinglecolortype", "");
//
//			// 이펙트 효과 적용
//			if (this.imgData != null && this.imgData.isApplyEffect) {
//
//				String value = ImageEffectBitmap.getAuraEffectValue(this.imgData.EFFECT_TYPE);
//				String name = ImageEffectBitmap.getAuraEffectName(this.imgData.EFFECT_TYPE);
//
//				if (value != null && value.length() > 0 && name != null && name.length() > 0) {
//					xml.startTag(null, "effect");
//					xml.attribute(null, "value", value);
//					xml.attribute(null, "name", name);
//					xml.endTag(null, "effect");
//				}
//			}
//
//			xml.endTag(null, "object");
//		} catch (Exception e) {
//			Logg.d("Exception Error", "SnapsLayoutControl getControlAuraOrderXML");
//			SnapsLogger.appendOrderLog("getControlAuraOrderXML tControl image exception. " + e.toString());
//		}
//
//		return xml;
//	}
//
//	public SnapsTControl() {}
//
//	public SnapsTControl(Parcel in) {
//		readFromParcel(in);
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//
//		dest.writeParcelable(format, 0);
//		dest.writeString(albumMode);
//		dest.writeString(initialText);
//		dest.writeString(text);
//		dest.writeString(auraOrderText);
//		dest.writeString(htmlText);
//		dest.writeTypedList(textList);
//		dest.writeInt(pageCoverW);
//		dest.writeInt(pageCoverH);
//
//		dest.writeBooleanArray(new boolean[] { isLeftPageControl });
//
//		dest.writeInt(editState);
//		dest.writeInt(textControlID);
//		dest.writeInt(imageControlID);
//	}
//
//	@SuppressWarnings("unchecked")
//	private void readFromParcel(Parcel in) {
//		format = in.readParcelable(TextFormat.class.getClassLoader());
//		albumMode = in.readString();
//		initialText = in.readString();
//		text = in.readString();
//		auraOrderText = in.readString();
//		htmlText = in.readString();
//		in.readTypedList(textList, LineText.CREATOR);
//		pageCoverW = in.readInt();
//		pageCoverH = in.readInt();
//
//		boolean[] arrBool = new boolean[1];
//		in.readBooleanArray(arrBool);
//		isLeftPageControl = arrBool[0];
//
//		editState = in.readInt();
//		textControlID = in.readInt();
//		imageControlID = in.readInt();
//	}
//
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	@SuppressWarnings("rawtypes")
//	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
//
//		@Override
//		public SnapsTControl createFromParcel(Parcel in) {
//			return new SnapsTControl(in);
//		}
//
//		@Override
//		public SnapsTControl[] newArray(int size) {
//			return new SnapsTControl[size];
//		}
//	};
//
//}
