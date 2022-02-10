package com.snaps.common.utils.ui;

public interface ICustomDialogListener {
	public static byte CANCEL = 0;
	public static byte OK = 1;
	void onClick(byte clickedOk);
}