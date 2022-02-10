package com.snaps.mobile.utils.ui;

import java.util.ArrayList;

import com.snaps.common.structure.page.SnapsPage;

public class ArrayListUtil {

	/**
	 * ArrayList에서 특정 인덱스까지만 남기고 버리는 함수 
	 * @param list
	 * @param index
	 * @return
	 */
	static public ArrayList<SnapsPage> removeArrayList(ArrayList<SnapsPage> list, int index) {
		ArrayList<SnapsPage> arr = new ArrayList<SnapsPage>();

		for (int i = 0; i < index; i++) {
			arr.add(list.get(i));
		}

		return arr;
	}

}
