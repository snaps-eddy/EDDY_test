package com.snaps.mobile.activity.selectimage.handler;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.CropUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class SelectImgHandler {
	ArrayList<String> selectImgKeyList = new ArrayList<String>();
	HashMap<String, MyPhotoSelectImageData> selectImgMap = new HashMap<String, MyPhotoSelectImageData>();
	SortedMap<Long, MyPhotoSelectImageData> selectImgOrderDataMap = new TreeMap<Long, MyPhotoSelectImageData>();

	public ArrayList<String> getSelectImgKeyList() {
		return selectImgKeyList;
	}

	public ArrayList<Integer> getSelectImgIndexList() {
		// 인스타그램북, 일기, 페이스북 등 array index를 키로 사용하는 경우만 사용. 다른데는 확인 안해봄.
		ArrayList<Integer> list = new ArrayList<Integer>();
		if( selectImgKeyList != null && selectImgKeyList.size() > 0 ) {
			for (int i = 0; i < selectImgKeyList.size(); ++i)
				list.add( Integer.parseInt(selectImgKeyList.get(i)) );
		}
		return list;
	}

	public void setSelectImgKeyList(ArrayList<String> selectImgKeyList) {
		this.selectImgKeyList = selectImgKeyList;
	}

	public HashMap<String, MyPhotoSelectImageData> getSelectImgMap() {
		return selectImgMap;
	}

	public void setSelectImgMap(HashMap<String, MyPhotoSelectImageData> selectImgMap) {
		this.selectImgMap = selectImgMap;
	}

	// size
	public int getListSize() {
		return selectImgKeyList.size();
	}

	public int getMapSize() {
		return selectImgMap.size();
	}

	public MyPhotoSelectImageData getData(String key) {
		return selectImgMap.get(key);
	}

	// data
	public ArrayList<MyPhotoSelectImageData> getSimpleData(ArrayList<String> arr) {
		ArrayList<MyPhotoSelectImageData> returnList = new ArrayList<MyPhotoSelectImageData>();

		for (String key : arr) {
		if (key.equals("")) {
				returnList.add(null);
			} else
				returnList.add(selectImgMap.get(key));

		}

		return returnList;
	}

	// data
	public ArrayList<MyPhotoSelectImageData> getNormalData() {
		ArrayList<MyPhotoSelectImageData> returnList = new ArrayList<MyPhotoSelectImageData>();
		for (int i = 0; i < getListSize(); i++)
			returnList.add(selectImgMap.get(selectImgKeyList.get(i)));

		// exif 정보를 다시 확인한다.
		// 로컬 사진만 확인한다.
		for (MyPhotoSelectImageData d : returnList) {
			if (d.KIND == Const_VALUES.SELECT_PHONE) {
				int exifAngle = CropUtil.getExifOrientation(d.PATH);
				d.ROTATE_ANGLE = (exifAngle + (d.ROTATE_ANGLE_THUMB == -1 ? 0 : d.ROTATE_ANGLE_THUMB)) % 360;

			}
		}

		return returnList;
	}

	/***
	 * 페이스북인경우 스토리를 시간순으로 정렬을 해야 한다.
	 * 
	 * @return
	 */
	public ArrayList<MyPhotoSelectImageData> getOrderData() {
		ArrayList<MyPhotoSelectImageData> returnList = new ArrayList<MyPhotoSelectImageData>();
		Iterator<String> it1 = selectImgMap.keySet().iterator();
		while (it1.hasNext()) {
			MyPhotoSelectImageData imgData = selectImgMap.get(it1.next());
			returnList.add(imgData);
		}

		selectImgOrderDataMap.clear();
		return returnList;
	}

	class FaceBookCompare implements Comparator<MyPhotoSelectImageData> {

		@Override
		public int compare(MyPhotoSelectImageData lhs, MyPhotoSelectImageData rhs) {
			// TODO Auto-generated method stub

			long first = Long.valueOf(lhs.FB_OBJECT_ID);
			long second = Long.valueOf(rhs.FB_OBJECT_ID);

			if (first > second) {
				return 1;
			} else if (first == second) {
				return 0;
			} else
				return -1;
		}
	}

	class KaKaoCompare implements Comparator<MyPhotoSelectImageData> {

		@Override
		public int compare(MyPhotoSelectImageData lhs, MyPhotoSelectImageData rhs) {
			// TODO Auto-generated method stub
			if (lhs.photoTakenDateTime > rhs.photoTakenDateTime) {
				return 1;
			} else if (lhs.photoTakenDateTime == rhs.photoTakenDateTime) {
				return 0;
			} else
				return -1;
		}
	}

	public void putData(String key, int idx, MyPhotoSelectImageData imgData) {
		selectImgMap.put(key, imgData);

		if (idx >= 0 && idx < selectImgKeyList.size()) {
			selectImgKeyList.add(idx, key);
		} else
			selectImgKeyList.add(key);
	}

	public void putData(String key, MyPhotoSelectImageData imgData) {
		selectImgMap.put(key, imgData);
		if (selectImgKeyList != null)
			selectImgKeyList.add(key);
	}

	public int removeData(String key) {
		if(selectImgMap == null) return -1;
		MyPhotoSelectImageData imgData = selectImgMap.remove(key);
		if(imgData != null) {
			selectImgKeyList.remove(key);
			reorderMap();
			return imgData.selectIdx;
		}
		return -1;
	}

	public List<Integer> removeLast(int count) {
		int startIdx = selectImgKeyList.size() - 1;
		int endIdx = selectImgKeyList.size() - count;
		List<Integer> listIdx = new ArrayList<Integer>();
		for (int i = startIdx; i >= endIdx; i--) {
			String key = selectImgKeyList.get(i);
			MyPhotoSelectImageData imgData = selectImgMap.remove(key);
			selectImgKeyList.remove(key);
			listIdx.add(imgData.selectIdx);
		}
		return listIdx;
	}

	// util
	public void reorderMap() {
		int idx = 0;
		for (int i = 0; i < selectImgKeyList.size(); i++) {
			if(selectImgMap != null && selectImgMap.containsKey(selectImgKeyList.get(i))) {
				selectImgMap.get(selectImgKeyList.get(i)).selectIdx = idx++;
			}
		}
	}

	public boolean isSelected(String key) {
		return selectImgMap.containsKey(key);
	}

	public String getSelectCount(String key) {
		MyPhotoSelectImageData imgData = selectImgMap.get(key);
		if (imgData == null)
			return null;
		return (imgData.selectIdx + 1) + "/" + selectImgMap.size();
	}
}
