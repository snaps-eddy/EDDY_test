package com.snaps.mobile.activity.cartorder;

import com.snaps.common.utils.net.xml.bean.Xml_PostAddress;
import com.snaps.common.utils.net.xml.bean.Xml_RecentAddress;

public interface IdeliveryLayout {
	public void selectSenderFirstNumber(int selectIdx);
	public void selectReceiverFirstNumber(int selectIdx);
	public void selectRecentAddress(int selectIdx, Xml_RecentAddress recentAddress);
	public void selectPostNumber(Xml_PostAddress postAddress);
	public void selectDeliveryMsg(int selectIdx);
}
