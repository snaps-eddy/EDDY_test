package com.snaps.common.text;

import android.content.Context;
import android.text.TextUtils;

import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.control.TextFormat;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.snaps.common.utils.constant.SnapsAPI.URL_TEXT_TO_IMAGE_DOMAIN;

/**
 * Created by ysjeong on 2018. 3. 12..
 */

public class SnapsTextToImageUtil {
	private static final String TAG = SnapsTextToImageUtil.class.getSimpleName();

	//    private static final String SNAPS_TEXT_TO_IMG_CMD_TEXT_IMAGE = "textimage";
	private static final String SNAPS_TEXT_TO_IMG_CMD_TEXT_IMAGE = "textimageAny";
	private static final String SNAPS_TEXT_TO_IMG_CMD_SPINE_IMAGE = "spineimage";
	//spineimage -> 책등, textImage -> 일반

	/**
	 * 다른 포토북은 텍스트 수정이 불가능 하지만 KT 북은 가능하다.
	 * @return
	 */
	public static boolean isSupportEditTextProduct() {
		return !Config.isCalendar() && !Config.isSNSBook() && !SnapsDiaryDataManager.isAliveSnapsDiaryService() && (!Config.isPhotobooks() || Config.isKTBook());
	}

	public static String createTextToImageUrlWithAttribute(SnapsTextToImageAttribute attribute) {
		if (attribute == null) {
			return null;
		}

		SnapsTextControl textControl = attribute.getSnapsTextControl();

		if (textControl == null || StringUtil.isEmpty(textControl.text)) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		try {
			int controlWidth = attribute.isThumbnail() ? (int) (textControl.getIntWidth() / attribute.getThumbnailRatioX()) : textControl.getIntWidth();
			int controlHeight = attribute.isThumbnail() ? (int) (textControl.getIntHeight() / attribute.getThumbnailRatioY()) : textControl.getIntHeight();

//            if (attribute.isSpineText()) { //TODO  만약에 포토북쪽도 지원해야 한다면 책등 처리가 별도로 필요 하다
//                int temp = controlWidth;
//                controlWidth = controlHeight;
//                controlHeight = temp;
//            }

			builder.append(URL_TEXT_TO_IMAGE_DOMAIN);
			builder.append("/");
			builder.append(attribute.isSpineText() ? SNAPS_TEXT_TO_IMG_CMD_SPINE_IMAGE : SNAPS_TEXT_TO_IMG_CMD_TEXT_IMAGE);
			builder.append("/").append(controlWidth);
			builder.append("/").append(controlHeight);
			builder.append("/");
			builder.append(getEncodedDivStyleTextWithTextControl(textControl));
			builder.append("/end");
			builder.append("/snapsTextImageForAndroid.png");
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return builder.toString();
	}

	public static String createTextToImageUrlWithAttributeDp(Context context, SnapsTextToImageAttribute attribute) {
		if (attribute == null) {
			return null;
		}

		SnapsTextControl textControl = attribute.getSnapsTextControl();

		if (textControl == null || StringUtil.isEmpty(textControl.text)) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		try {
			int controlWidth = attribute.isThumbnail() ? (int) (textControl.getIntWidth() / attribute.getThumbnailRatioX()) : UIUtil.convertDPtoPXBabyNameSticker(context, textControl.getIntWidth());
			int controlHeight = attribute.isThumbnail() ? (int) (textControl.getIntHeight() / attribute.getThumbnailRatioY()) : UIUtil.convertDPtoPXBabyNameSticker(context, textControl.getIntHeight());

//            if (attribute.isSpineText()) { //TODO  만약에 포토북쪽도 지원해야 한다면 책등 처리가 별도로 필요 하다
//                int temp = controlWidth;
//                controlWidth = controlHeight;
//                controlHeight = temp;
//            }

			builder.append(URL_TEXT_TO_IMAGE_DOMAIN);
			builder.append("/");
			builder.append(attribute.isSpineText() ? SNAPS_TEXT_TO_IMG_CMD_SPINE_IMAGE : SNAPS_TEXT_TO_IMG_CMD_TEXT_IMAGE);
			builder.append("/").append(controlWidth);
			builder.append("/").append(controlHeight);
			builder.append("/");
			builder.append(getEncodedDivStyleTextWithTextControlDp(context, textControl));
			builder.append("/end");
			builder.append("/snapsTextImageForAndroid.png");
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return builder.toString();
	}

	public static String createTextToHtmlWithAttribute(SnapsTextToImageAttribute attribute) {
		if (attribute == null) {
			return null;
		}

		SnapsTextControl textControl = attribute.getSnapsTextControl();

		if (textControl == null || StringUtil.isEmpty(textControl.text)) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		try {
			builder.append("<html><head><style>p,div{white-space:pre-wrap;margin:0;text-indent:0;-qt-indent:0;}</style></head><body>");
			builder.append(getDivStyleTextWithTextControl(textControl, textControl.text));
			builder.append("</body></html>");
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return builder.toString();
	}

	private static String getEncodedDivStyleTextWithTextControl(SnapsTextControl textControl) throws UnsupportedEncodingException {
		return getEncodedTextContents(getDivStyleTextWithTextControl(textControl, textControl.text));
	}

	private static String getEncodedDivStyleTextWithTextControlDp(Context context, SnapsTextControl textControl) throws UnsupportedEncodingException {
		return getEncodedTextContents(getDivStyleTextWithTextControlDp(context, textControl, textControl.text));
	}

	private static String getEncodedTextContents(String textContents) throws UnsupportedEncodingException {
		String encodedStr = URLEncoder.encode(textContents, "utf-8");
		return encodedStr.replace("+", "%20");
	}

	private static String getFontStyleFromSnapsTextControl(SnapsTextControl textControl) {
		if (textControl != null) {
			TextFormat textFormat = textControl.format;
			return (textFormat != null && textFormat.italic != null
					&& (textFormat.italic.equalsIgnoreCase("1") || textFormat.italic.equalsIgnoreCase("true") || textFormat.italic.equalsIgnoreCase("italic")))
					? "italic" : "none";
		}
		return "none";
	}

	private static String getTextDecorationFromSnapsTextControl(SnapsTextControl textControl) {
		if (textControl != null) {
			TextFormat textFormat = textControl.format;
			return (textFormat != null && textFormat.underline != null
					&& (textFormat.underline.equalsIgnoreCase("1") || textFormat.underline.equalsIgnoreCase("true") || textFormat.underline.equalsIgnoreCase("underline")))
					? "underline" : "none";
		}
		return "none";
	}

	private static String getDivStyleTextWithTextControl(SnapsTextControl textControl, String textContents) {
		if (textControl == null || textControl.format == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("<div style=")
				.append("\"")
				.append("font-family:'").append(textControl.format.fontFace).append("';")
				.append("font-size:").append(textControl.format.fontSize).append("px;")
				.append("color:").append("#").append(textControl.format.fontColor).append(";")
				.append("text-align:").append(textControl.format.align).append(";")
				.append("font-style:").append(getFontStyleFromSnapsTextControl(textControl)).append(";")
				.append("text-decoration:").append(getTextDecorationFromSnapsTextControl(textControl)).append(";")
				.append("\"")
				.append(">")
				.append(convertTextContentsForTextServer(textContents))
				.append("</div>");

		return builder.toString();
	}

	private static String getDivStyleTextWithTextControlDp(Context context, SnapsTextControl textControl, String textContents) {
		if (textControl == null || textControl.format == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		int font = 0;
		if (TextUtils.isEmpty(textControl.format.fontSize)) {
			font = 12;
		} else {
			font = Integer.parseInt(textControl.format.fontSize);
		}
		String dpFont = UIUtil.convertDPtoPXBabyNameSticker(context, font) + "";
		builder.append("<div style=")
				.append("\"")
				.append("font-family:'").append(textControl.format.fontFace).append("';")
				.append("font-size:").append(dpFont).append("px;")
				.append("color:").append("#").append(textControl.format.fontColor).append(";")
				.append("text-align:").append(textControl.format.align).append(";")
				.append("font-style:").append(getFontStyleFromSnapsTextControl(textControl)).append(";")
				.append("text-decoration:").append(getTextDecorationFromSnapsTextControl(textControl)).append(";")
				.append("\"")
				.append(">")
				.append(convertTextContentsForTextServer(textContents))
				.append("</div>");

		return builder.toString();
	}

	private static String convertTextContentsForTextServer(String textContents) {
		String result = StringUtil.CleanInvalidXmlChars(textContents);
		result = convertXmlEscapeChar(result);
		result = convertLineSpaceCharToBrTag(result);
		return result;
	}

	private static String convertLineSpaceCharToBrTag(String str) {
		if (StringUtil.isEmpty(str) || !str.contains("\n")) {
			return str;
		}
		return str.replace("\n", "<br>");
	}

	private static String convertXmlEscapeChar(String str) {
		if (StringUtil.isEmpty(str) || (!str.contains("<") && !str.contains(">"))) {
			return str;
		}
		return str.replace("<", "&lt;").replace(">", "&gt;");
	}
}
