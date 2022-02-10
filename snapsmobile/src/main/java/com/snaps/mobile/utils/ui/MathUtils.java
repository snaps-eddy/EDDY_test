package com.snaps.mobile.utils.ui;

public class MathUtils {

	/***
	 * 특정 숫자를 1/2로 나누어서 기준 숫자보다 작거나 같은 숫자를 반환하는 함수
	 *
	 * @param num
	 * @param baseNum
	 * @return
	 */
	public static int getHalfNumber(int num, int baseNum) {
		int n = num;
		while (true) {
			n = n / 2;
			if (n <= baseNum) {
				break;
			}
		}

		return n;
	}

}
