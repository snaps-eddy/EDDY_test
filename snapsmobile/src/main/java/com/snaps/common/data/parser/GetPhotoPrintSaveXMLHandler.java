package com.snaps.common.data.parser;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.photoprint.SnapsPhotoPrintItem;
import com.snaps.common.structure.photoprint.SnapsPhotoPrintProject;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class GetPhotoPrintSaveXMLHandler extends DefaultHandler {
	private static final String TAG = GetPhotoPrintSaveXMLHandler.class.getSimpleName();

	SnapsPhotoPrintProject mProject = null;
	SnapsPhotoPrintItem mItem = null;
	String mSceneID = "";
	String mSceneProductCode = "";
	String mSceneUnitPrice = "";
	String mSceneViewRatio = "";

	@Override
	public void startDocument() throws SAXException {
		mProject = new SnapsPhotoPrintProject();
	}

	@Override
	public void endDocument() throws SAXException {

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (localName.equals("basket")) {
			mProject.mSBasketVersion = getValue(attributes, "version");
			mProject.setProjectCode(getValue(attributes, "projectCode"));
		} else if (localName.equals("item")) {

			mProject.mItemID = getValue(attributes, "id");
			mProject.mMaker = getValue(attributes, "maker");
			mProject.mEditDate = getValue(attributes, "editdate");
			mProject.mType = getValue(attributes, "type");
			mProject.mCheck = getValue(attributes, "check");

		} else if (localName.equals("photoOption")) {
			mProject.mGlossy = getValue(attributes, "glossy");
		} else if (localName.equals("scene")) {
			mSceneID = getValue(attributes, "id");
			mSceneProductCode = getValue(attributes, "productCode");
			mSceneUnitPrice = getValue(attributes, "unitCost");
			mSceneViewRatio = getValue(attributes, "viewportRatio");
			// 사진인화
			mProject.mViewRatio = getValue(attributes, "viewportRatio");
		} else if (localName.equals("photo")) {
			mItem = new SnapsPhotoPrintItem();

			mItem.mProdCode = mSceneProductCode;
			mItem.mProdID = mSceneID;

			mItem.mLocalPath = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "localPath"));
			mItem.mOrgPath = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "orgPath"));
			mItem.mGlossy = getValue(attributes, "glossy");
			mItem.mPaperMatch = getValue(attributes, "paperMatch");
			mItem.mAutoBright = getValue(attributes, "autoBright");
			mItem.mRecomment = getValue(attributes, "recommend");
			mItem.mAngle = getIntValue(attributes, "angle");
			mItem.mThumbAngle = getIntValue(attributes, "thumbAngle");
			mItem.mOrderCount = getValue(attributes, "orderCount");
			mItem.mImgYear = getValue(attributes, "imgYear");
			mItem.mImgSeq = getValue(attributes, "imgSeq");
			mItem.mUploadPath = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "uploadPath"));
			mItem.mOrgSize = getValue(attributes, "orgSize");
			mItem.mTrimPos = getValue(attributes, "trimPos");
			mItem.mEndPos = getValue(attributes, "endPos");
			mItem.mWidth = getValue(attributes, "width");
			mItem.mHeight = getValue(attributes, "height");
			mItem.mThumImgPath = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "thumbImgPath"));
			mItem.mOffsetX = getValue(attributes, "offsetX");
			mItem.mOffsetY = getValue(attributes, "offsetY");

			String[] size = mItem.mOrgSize.split(" ");
			int orgW = Integer.parseInt(size[0]);
			int orgH = Integer.parseInt(size[1]);
			int cropW = Integer.parseInt(mItem.mWidth);
			int cropH = Integer.parseInt(mItem.mHeight);

			if (orgW > orgH) {
				mItem.mScale = String.format("%.4f", (550.f / (float) orgW));// String.valueOf(550.f / (float) orgW);
			} else
				mItem.mScale = String.format("%.4f", (550.f / (float) orgH));// String.valueOf(550.f / (float) orgH);

			// orientation
			if (orgW > orgH) {// 가로가 세로 보다 긴 직사각형
				mItem.mOrientation = 0;
			} else {
				if (orgW == orgH) {
					mItem.mOrientation = 0;

				} else {
					mItem.mOrientation = 1;
				}
			}

			float orgX = (orgW - cropW) / 2.f - Float.parseFloat(mItem.mOffsetX) * orgW;
			float orgY = (orgH - cropH) / 2.f - Float.parseFloat(mItem.mOffsetY) * orgH;

			mItem.mX = String.valueOf((int) orgX);
			mItem.mY = String.valueOf((int) orgY);

			// 가격설정...
			mItem.setUnitPrice(mSceneUnitPrice);
			mItem.setSellPrice(String.valueOf(Integer.parseInt(mItem.getUnitPrice()) * Integer.parseInt(mItem.mOrderCount)));

		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("item")) {

		} else if (localName.equals("photo")) {
			mProject.mData.add(mItem);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
	}

	private String getValue(Attributes target, String name) {
		String value = target.getValue(name);
		return (value == null) ? "" : value;
	}

	private int getIntValue(Attributes target, String name) {
		String value = getValue(target, name);
		return value.equals("") ? 0 : Integer.parseInt(value);
	}

	public SnapsPhotoPrintProject getPrject() {
		return mProject;
	}

	public ArrayList<MyPhotoSelectImageData> getMyPhotoSelectImageData() {
		ArrayList<MyPhotoSelectImageData> imgDatas = new ArrayList<MyPhotoSelectImageData>();

		for (SnapsPhotoPrintItem item : mProject.mData) {
			MyPhotoSelectImageData d = new MyPhotoSelectImageData();

			d.F_IMG_SQNC = item.mImgSeq;
			d.F_IMG_YEAR = item.mImgYear;
			d.ORIGINAL_PATH = item.mOrgPath;
			
			if(item.mLocalPath != null && item.mLocalPath.length() > 0)
				d.ORIGINAL_PATH = item.mLocalPath;
			
			if (!item.isFaceBookImage()) {
				// 로컬이미지 인경우만..
				d.KIND = Const_VALUES.SELECT_UPLOAD;
			} else {
				d.KIND = Const_VALUES.SELECT_FACEBOOK;
			}

			d.PATH = item.mOrgPath;
			d.THUMBNAIL_PATH = item.mThumImgPath;

			String size[] = item.mOrgSize.split(" ");

			d.F_IMG_HEIGHT = size[1];
			d.F_IMG_WIDTH = size[0];
			d.ROTATE_ANGLE = item.mAngle;
			d.ROTATE_ANGLE_THUMB = item.mThumbAngle;
			CropInfo cInfo = new CropInfo();
			if (item.mOffsetX.equals("0")) {
				cInfo.cropOrient = CropInfo.CORP_ORIENT.HEIGHT;
				cInfo.movePercent = Float.parseFloat(item.mOffsetY);
			} else {
				cInfo.cropOrient = CropInfo.CORP_ORIENT.WIDTH;
				cInfo.movePercent = Float.parseFloat(item.mOffsetX);
			}

			//
			cInfo.startPercent = (int) (Float.parseFloat(item.mTrimPos) * 100.f);
			cInfo.endPercent = (int) (Float.parseFloat(item.mEndPos) * 100.f);

			d.CROP_INFO = cInfo;
			d.photoPrintCount = Integer.parseInt(item.mOrderCount);

			imgDatas.add(d);
		}

		return imgDatas;
	}

	public void parsing(String xmlString) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(this);
			InputSource source = new InputSource();
			source.setCharacterStream(new StringReader(xmlString));
			reader.parse(source);
		} catch (ParserConfigurationException e) {
			Dlog.e(TAG, e);

		} catch (SAXException e) {
			Dlog.e(TAG, e);

		} catch (IOException e) {
			Dlog.e(TAG, e);

		}
	}

	public boolean getGlossyType() {
		return !mProject.mGlossy.equals("glossy");
	}

}
